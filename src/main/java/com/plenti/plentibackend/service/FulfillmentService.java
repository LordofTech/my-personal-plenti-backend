package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.*;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for order fulfillment workflow and delivery management
 */
@Service
@Slf4j
public class FulfillmentService {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private RiderRepository riderRepository;

    @Autowired
    private RiderLocationRepository riderLocationRepository;

    @Autowired
    private OrderTrackingRepository orderTrackingRepository;

    @Autowired
    private StoreAssignmentService storeAssignmentService;

    @Autowired
    private SmsService smsService;

    @Autowired
    private NotificationService notificationService;

    @Value("${fulfillment.auto-assign.enabled:true}")
    private boolean autoAssignEnabled;

    @Value("${fulfillment.max-rider-distance-km:10.0}")
    private double maxRiderDistanceKm;

    @Value("${fulfillment.default.latitude:6.5244}")
    private double defaultLatitude; // Default Lagos coordinates
    
    @Value("${fulfillment.default.longitude:3.3792}")
    private double defaultLongitude;

    private static final double EARTH_RADIUS_KM = 6371.0;

    /**
     * Process new order for fulfillment
     */
    @Transactional
    public Order processFulfillment(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));

        if (order.getStatus() != OrderStatus.PENDING) {
            throw new PlentiException("Order is not in pending status");
        }

        // Extract delivery coordinates from address
        // In production, this would use geocoding service
        double[] coords = extractCoordinatesFromAddress(order.getDeliveryAddress());

        // Find nearest store
        Store nearestStore = findNearestStoreWithInventory(coords[0], coords[1]);
        
        // Assign store to order
        order.setStoreId(nearestStore.getId());
        order.setStatus(OrderStatus.CONFIRMED);
        order = orderRepository.save(order);

        // Create tracking entry
        createTrackingEntry(orderId, OrderStatus.CONFIRMED, 
            "Order confirmed and assigned to " + nearestStore.getName(), null);

        log.info("Order {} assigned to store {}", orderId, nearestStore.getName());

        // Auto-assign rider if enabled
        if (autoAssignEnabled) {
            try {
                autoAssignRider(order, coords[0], coords[1]);
            } catch (Exception e) {
                log.warn("Failed to auto-assign rider to order {}: {}", orderId, e.getMessage());
            }
        }

        return order;
    }

    /**
     * Find nearest store with inventory availability
     */
    private Store findNearestStoreWithInventory(double lat, double lng) {
        List<Store> stores = storeRepository.findAll();

        if (stores.isEmpty()) {
            throw new PlentiException("No stores available");
        }

        Store nearestStore = null;
        double minDistance = Double.MAX_VALUE;

        for (Store store : stores) {
            if (store.getLatitude() != null && store.getLongitude() != null && Boolean.TRUE.equals(store.getActive())) {
                double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestStore = store;
                }
            }
        }

        if (nearestStore == null) {
            throw new PlentiException("No active stores with valid coordinates found");
        }

        return nearestStore;
    }

    /**
     * Auto-assign rider based on proximity and availability
     */
    @Transactional
    public Rider autoAssignRider(Order order, double destLat, double destLng) {
        // Find available riders near the store
        Store store = storeRepository.findById(order.getStoreId())
                .orElseThrow(() -> new PlentiException("Store not found"));

        List<Rider> availableRiders = riderRepository.findByStatusAndIsActiveTrue("AVAILABLE");

        if (availableRiders.isEmpty()) {
            log.warn("No available riders found for order {}", order.getId());
            return null;
        }

        // Find nearest available rider
        Rider nearestRider = null;
        double minDistance = Double.MAX_VALUE;

        for (Rider rider : availableRiders) {
            // Get rider's last known location
            Optional<RiderLocation> locationOpt = riderLocationRepository
                    .findFirstByRiderIdOrderByTimestampDesc(rider.getId());

            if (locationOpt.isPresent()) {
                RiderLocation location = locationOpt.get();
                double distance = calculateDistance(
                    store.getLatitude(), store.getLongitude(),
                    location.getLatitude(), location.getLongitude()
                );

                if (distance < minDistance && distance <= maxRiderDistanceKm) {
                    minDistance = distance;
                    nearestRider = rider;
                }
            }
        }

        if (nearestRider != null) {
            assignRiderToOrder(order.getId(), nearestRider.getId());
            log.info("Auto-assigned rider {} to order {} (distance: {} km)", 
                nearestRider.getName(), order.getId(), minDistance);
            return nearestRider;
        }

        log.warn("No riders within {} km found for order {}", maxRiderDistanceKm, order.getId());
        return null;
    }

    /**
     * Manually assign rider to order
     */
    @Transactional
    public Order assignRiderToOrder(Long orderId, Long riderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));

        Rider rider = riderRepository.findById(riderId)
                .orElseThrow(() -> new PlentiException("Rider not found"));

        if (!rider.getIsActive()) {
            throw new PlentiException("Rider is not active");
        }

        // Update order
        order.setRiderId(String.valueOf(riderId));
        order.setRiderName(rider.getName());
        order.setStatus(OrderStatus.PROCESSING);
        order = orderRepository.save(order);

        // Update rider availability
        rider.setStatus("BUSY");
        riderRepository.save(rider);

        // Create tracking entry
        createTrackingEntry(orderId, OrderStatus.PROCESSING, 
            "Rider " + rider.getName() + " assigned to order", riderId);

        // Send notifications
        try {
            // Send notification to rider
            notificationService.sendNotification("/topic/riders/" + riderId, 
                Map.of("orderId", orderId, "message", "New order assigned"));
        } catch (Exception e) {
            log.error("Failed to send rider assignment notification: {}", e.getMessage());
        }

        log.info("Rider {} assigned to order {}", rider.getName(), orderId);
        return order;
    }

    /**
     * Update delivery status
     */
    @Transactional
    public Order updateDeliveryStatus(Long orderId, OrderStatus newStatus, String message) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));

        OrderStatus oldStatus = order.getStatus();
        order.setStatus(newStatus);
        order = orderRepository.save(order);

        // Create tracking entry
        createTrackingEntry(orderId, newStatus, message, 
            order.getRiderId() != null ? Long.parseLong(order.getRiderId()) : null);

        // Send status update notifications
        try {
            sendStatusUpdateNotifications(order, newStatus);
        } catch (Exception e) {
            log.error("Failed to send status update notifications: {}", e.getMessage());
        }

        // If order is delivered or cancelled, mark rider as available
        if (newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.CANCELLED) {
            releaseRider(order.getRiderId());
        }

        log.info("Order {} status updated: {} -> {}", orderId, oldStatus, newStatus);
        return order;
    }

    /**
     * Mark order as out for delivery
     */
    @Transactional
    public Order markOutForDelivery(Long orderId) {
        return updateDeliveryStatus(orderId, OrderStatus.OUT_FOR_DELIVERY, 
            "Order is out for delivery");
    }

    /**
     * Mark order as delivered
     */
    @Transactional
    public Order markDelivered(Long orderId) {
        return updateDeliveryStatus(orderId, OrderStatus.DELIVERED, 
            "Order has been delivered successfully");
    }

    /**
     * Get order fulfillment status with tracking history
     */
    public Map<String, Object> getFulfillmentStatus(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new PlentiException("Order not found"));

        List<OrderTracking> trackingHistory = orderTrackingRepository
                .findByOrderIdOrderByTimestampAsc(orderId);

        Map<String, Object> status = new HashMap<>();
        status.put("orderId", orderId);
        status.put("currentStatus", order.getStatus());
        status.put("storeId", order.getStoreId());
        status.put("riderId", order.getRiderId());
        status.put("riderName", order.getRiderName());
        status.put("trackingHistory", trackingHistory);
        status.put("estimatedDeliveryTime", calculateEstimatedDeliveryTime(order));

        // Get rider current location if available
        if (order.getRiderId() != null) {
            try {
                Long riderId = Long.parseLong(order.getRiderId());
                Optional<RiderLocation> locationOpt = riderLocationRepository
                        .findFirstByRiderIdOrderByTimestampDesc(riderId);
                locationOpt.ifPresent(location -> {
                    status.put("riderLocation", Map.of(
                        "latitude", location.getLatitude(),
                        "longitude", location.getLongitude(),
                        "timestamp", location.getTimestamp()
                    ));
                });
            } catch (Exception e) {
                log.warn("Failed to get rider location: {}", e.getMessage());
            }
        }

        return status;
    }

    /**
     * Create order tracking entry
     */
    private void createTrackingEntry(Long orderId, OrderStatus status, String message, Long riderId) {
        OrderTracking tracking = new OrderTracking();
        tracking.setOrderId(orderId);
        tracking.setStatus(status);
        tracking.setStatusMessage(message);
        tracking.setDescription(generateStatusDescription(status));
        
        if (riderId != null) {
            tracking.setRiderId(String.valueOf(riderId));
            // Get rider location if available
            Optional<RiderLocation> locationOpt = riderLocationRepository
                    .findFirstByRiderIdOrderByTimestampDesc(riderId);
            locationOpt.ifPresent(location -> {
                tracking.setRiderLatitude(location.getLatitude());
                tracking.setRiderLongitude(location.getLongitude());
            });
        }
        
        orderTrackingRepository.save(tracking);
    }

    /**
     * Generate human-readable status description
     */
    private String generateStatusDescription(OrderStatus status) {
        switch (status) {
            case PENDING:
                return "Your order has been received and is being processed";
            case PROCESSING:
                return "Your order is being processed";
            case CONFIRMED:
                return "Your order has been confirmed and assigned to a store";
            case PACKED:
                return "Your order has been packed and is ready for delivery";
            case OUT_FOR_DELIVERY:
                return "Your order is on its way";
            case DELIVERED:
                return "Your order has been delivered";
            case CANCELLED:
                return "Your order has been cancelled";
            case REFUNDED:
                return "Your order has been refunded";
            default:
                return "Order status updated";
        }
    }

    /**
     * Send status update notifications
     */
    private void sendStatusUpdateNotifications(Order order, OrderStatus status) {
        // Send push notification with orderId
        notificationService.sendOrderStatusUpdate(order.getId(), status);

        // Send SMS for important status changes
        if (status == OrderStatus.OUT_FOR_DELIVERY || status == OrderStatus.DELIVERED) {
            // Get user phone from order or user service
            // smsService.sendOrderStatusSms(phoneNumber, order.getId(), status);
        }
    }

    /**
     * Release rider after order completion
     */
    private void releaseRider(String riderIdStr) {
        if (riderIdStr != null) {
            try {
                Long riderId = Long.parseLong(riderIdStr);
                Optional<Rider> riderOpt = riderRepository.findById(riderId);
                riderOpt.ifPresent(rider -> {
                    rider.setStatus("AVAILABLE");
                    riderRepository.save(rider);
                    log.info("Rider {} marked as available", rider.getName());
                });
            } catch (Exception e) {
                log.error("Failed to release rider {}: {}", riderIdStr, e.getMessage());
            }
        }
    }

    /**
     * Calculate estimated delivery time
     */
    private LocalDateTime calculateEstimatedDeliveryTime(Order order) {
        // Base delivery time: 60 minutes
        int baseMinutes = 60;

        // Adjust based on current status
        int adjustedMinutes;
        switch (order.getStatus()) {
            case PENDING:
                adjustedMinutes = 60;
                break;
            case PROCESSING:
                adjustedMinutes = 55;
                break;
            case CONFIRMED:
                adjustedMinutes = 50;
                break;
            case PACKED:
                adjustedMinutes = 40;
                break;
            case OUT_FOR_DELIVERY:
                adjustedMinutes = 20;
                break;
            case DELIVERED:
                adjustedMinutes = 0;
                break;
            default:
                adjustedMinutes = 60;
        }

        return LocalDateTime.now().plusMinutes(adjustedMinutes);
    }

    /**
     * Extract coordinates from address (simplified version)
     * In production, use a geocoding service
     */
    private double[] extractCoordinatesFromAddress(String address) {
        // Use configured default coordinates
        // In production, implement proper geocoding
        return new double[]{defaultLatitude, defaultLongitude};
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

        return EARTH_RADIUS_KM * c;
    }
}
