package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.StoreDTO;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.entity.Store;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for store assignment, delivery calculations, and inventory management
 */
@Service
@Slf4j
public class StoreAssignmentService {

    @Autowired
    private StoreRepository storeRepository;
    
    @Autowired
    private ProductRepository productRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;
    
    @Value("${store.delivery.avg-speed-kmh:40.0}")
    private double avgSpeedKmh; // Average delivery speed in km/h
    
    @Value("${store.delivery.base-fee:500.0}")
    private double baseDeliveryFee; // Base fee in currency units
    
    @Value("${store.delivery.per-km-fee:50.0}")
    private double perKmFee; // Fee per kilometer
    
    @Value("${store.delivery.max-distance-km:15.0}")
    private double maxDeliveryDistanceKm; // Maximum delivery distance

    /**
     * Find nearest store to given coordinates (active stores only)
     */
    public StoreDTO findNearestStore(double lat, double lng) {
        List<Store> stores = storeRepository.findAll().stream()
                .filter(store -> Boolean.TRUE.equals(store.getActive()))
                .collect(Collectors.toList());
        
        if (stores.isEmpty()) {
            throw new PlentiException("No active stores available");
        }

        Store nearestStore = null;
        double minDistance = Double.MAX_VALUE;

        for (Store store : stores) {
            if (store.getLatitude() != null && store.getLongitude() != null) {
                double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
                if (distance < minDistance && distance <= maxDeliveryDistanceKm) {
                    minDistance = distance;
                    nearestStore = store;
                }
            }
        }

        if (nearestStore == null) {
            throw new PlentiException("No stores available within delivery range");
        }

        log.info("Found nearest store: {} at distance {} km", nearestStore.getName(), minDistance);
        return toStoreDTO(nearestStore);
    }
    
    /**
     * Find all stores within delivery range sorted by distance
     */
    public List<Map<String, Object>> findStoresInRange(double lat, double lng) {
        List<Store> stores = storeRepository.findAll().stream()
                .filter(store -> Boolean.TRUE.equals(store.getActive()))
                .collect(Collectors.toList());
        
        List<Map<String, Object>> storesInRange = new ArrayList<>();
        
        for (Store store : stores) {
            if (store.getLatitude() != null && store.getLongitude() != null) {
                double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
                if (distance <= maxDeliveryDistanceKm) {
                    Map<String, Object> storeInfo = new HashMap<>();
                    storeInfo.put("store", toStoreDTO(store));
                    storeInfo.put("distance", Math.round(distance * 100.0) / 100.0);
                    storeInfo.put("eta", calculateSimpleETA(distance));
                    storeInfo.put("deliveryFee", calculateSimpleDeliveryFee(distance));
                    storesInRange.add(storeInfo);
                }
            }
        }
        
        // Sort by distance
        storesInRange.sort((a, b) -> Double.compare(
            (Double) a.get("distance"), 
            (Double) b.get("distance")
        ));
        
        log.info("Found {} stores within {} km range", storesInRange.size(), maxDeliveryDistanceKm);
        return storesInRange;
    }
    
    /**
     * Auto-assign store based on proximity and inventory
     */
    public Store autoAssignStore(double lat, double lng, List<Long> productIds) {
        List<Store> stores = storeRepository.findAll().stream()
                .filter(store -> Boolean.TRUE.equals(store.getActive()))
                .collect(Collectors.toList());
        
        if (stores.isEmpty()) {
            throw new PlentiException("No active stores available");
        }
        
        // For now, return nearest store
        // In production, this would check inventory availability
        Store nearestStore = null;
        double minDistance = Double.MAX_VALUE;
        
        for (Store store : stores) {
            if (store.getLatitude() != null && store.getLongitude() != null) {
                double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
                if (distance < minDistance && distance <= maxDeliveryDistanceKm) {
                    // In production, verify inventory here
                    if (checkStoreHasInventory(store.getId(), productIds)) {
                        minDistance = distance;
                        nearestStore = store;
                    }
                }
            }
        }
        
        if (nearestStore == null) {
            throw new PlentiException("No stores with required products available within delivery range");
        }
        
        log.info("Auto-assigned store: {} at distance {} km", nearestStore.getName(), minDistance);
        return nearestStore;
    }
    
    /**
     * Check if store has inventory for products
     * Simplified version - in production this would check actual inventory
     */
    public boolean checkStoreHasInventory(Long storeId, List<Long> productIds) {
        // Simplified logic - in production, check against inventory table
        // For now, assume all stores have all products
        return true;
    }
    
    /**
     * Check if location is within delivery coverage
     */
    public Map<String, Object> checkDeliveryCoverage(double lat, double lng) {
        List<Store> nearbyStores = storeRepository.findAll().stream()
                .filter(store -> Boolean.TRUE.equals(store.getActive()))
                .filter(store -> {
                    if (store.getLatitude() != null && store.getLongitude() != null) {
                        double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
                        return distance <= maxDeliveryDistanceKm;
                    }
                    return false;
                })
                .collect(Collectors.toList());
        
        Map<String, Object> coverage = new HashMap<>();
        coverage.put("covered", !nearbyStores.isEmpty());
        coverage.put("storeCount", nearbyStores.size());
        
        if (!nearbyStores.isEmpty()) {
            Store nearest = nearbyStores.stream()
                    .min(Comparator.comparingDouble(store -> 
                        calculateDistance(lat, lng, store.getLatitude(), store.getLongitude())
                    ))
                    .orElse(null);
            
            if (nearest != null) {
                double distance = calculateDistance(lat, lng, nearest.getLatitude(), nearest.getLongitude());
                coverage.put("nearestStore", nearest.getName());
                coverage.put("distance", Math.round(distance * 100.0) / 100.0);
                coverage.put("estimatedDeliveryTime", calculateSimpleETA(distance));
            }
        }
        
        return coverage;
    }

    /**
     * Calculate estimated time of arrival (ETA) in minutes
     */
    public Map<String, Object> calculateETA(Long storeId, double destLat, double destLng) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new PlentiException("Store not found"));

        if (store.getLatitude() == null || store.getLongitude() == null) {
            throw new PlentiException("Store coordinates not available");
        }

        double distanceKm = calculateDistance(
                store.getLatitude(), store.getLongitude(), destLat, destLng);
        double etaMinutes = (distanceKm / avgSpeedKmh) * 60;

        Map<String, Object> result = new HashMap<>();
        result.put("storeId", storeId);
        result.put("storeName", store.getName());
        result.put("distanceKm", Math.round(distanceKm * 100.0) / 100.0);
        result.put("etaMinutes", Math.round(etaMinutes));
        result.put("etaFormatted", formatETA((int) Math.round(etaMinutes)));

        return result;
    }

    /**
     * Calculate delivery fee based on distance
     */
    public Map<String, Object> calculateDeliveryFee(Long storeId, double destLat, double destLng) {
        Store store = storeRepository.findById(storeId)
                .orElseThrow(() -> new PlentiException("Store not found"));

        if (store.getLatitude() == null || store.getLongitude() == null) {
            throw new PlentiException("Store coordinates not available");
        }

        double distanceKm = calculateDistance(
                store.getLatitude(), store.getLongitude(), destLat, destLng);
        double deliveryFee = baseDeliveryFee + (distanceKm * perKmFee);

        Map<String, Object> result = new HashMap<>();
        result.put("storeId", storeId);
        result.put("distanceKm", Math.round(distanceKm * 100.0) / 100.0);
        result.put("deliveryFee", Math.round(deliveryFee * 100.0) / 100.0);
        result.put("baseFee", baseDeliveryFee);
        result.put("distanceFee", Math.round((distanceKm * perKmFee) * 100.0) / 100.0);

        return result;
    }

    /**
     * Calculate distance between two coordinates using Haversine formula
     */
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return EARTH_RADIUS_KM * c;
    }
    
    /**
     * Calculate simple ETA in minutes
     */
    private int calculateSimpleETA(double distanceKm) {
        return (int) Math.round((distanceKm / avgSpeedKmh) * 60) + 10; // Add 10 min prep time
    }
    
    /**
     * Calculate simple delivery fee
     */
    private double calculateSimpleDeliveryFee(double distanceKm) {
        return Math.round((baseDeliveryFee + (distanceKm * perKmFee)) * 100.0) / 100.0;
    }

    private String formatETA(int minutes) {
        if (minutes < 60) {
            return minutes + " minutes";
        } else {
            int hours = minutes / 60;
            int remainingMinutes = minutes % 60;
            return hours + "h " + remainingMinutes + "m";
        }
    }

    private StoreDTO toStoreDTO(Store store) {
        StoreDTO dto = new StoreDTO();
        dto.setId(store.getId());
        dto.setName(store.getName());
        dto.setLocation(store.getLocation());
        dto.setType(store.getType());
        dto.setInventoryCapacity(store.getInventoryCapacity());
        dto.setLatitude(store.getLatitude());
        dto.setLongitude(store.getLongitude());
        dto.setCity(store.getCity());
        dto.setState(store.getState());
        dto.setPhone(store.getPhone());
        dto.setEmail(store.getEmail());
        dto.setOperatingHours(store.getOperatingHours());
        dto.setActive(store.getActive());
        return dto;
    }
}
