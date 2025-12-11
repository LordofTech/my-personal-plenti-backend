package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.StoreDTO;
import com.plenti.plentibackend.entity.Store;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.StoreRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for store assignment and delivery calculations
 */
@Service
@Slf4j
public class StoreAssignmentService {

    @Autowired
    private StoreRepository storeRepository;

    private static final double EARTH_RADIUS_KM = 6371.0;
    
    @Value("${store.delivery.avg-speed-kmh:40.0}")
    private double avgSpeedKmh; // Average delivery speed in km/h
    
    @Value("${store.delivery.base-fee:500.0}")
    private double baseDeliveryFee; // Base fee in currency units
    
    @Value("${store.delivery.per-km-fee:50.0}")
    private double perKmFee; // Fee per kilometer

    /**
     * Find nearest store to given coordinates
     */
    public StoreDTO findNearestStore(double lat, double lng) {
        List<Store> stores = storeRepository.findAll();
        
        if (stores.isEmpty()) {
            throw new PlentiException("No stores available");
        }

        Store nearestStore = null;
        double minDistance = Double.MAX_VALUE;

        for (Store store : stores) {
            if (store.getLatitude() != null && store.getLongitude() != null) {
                double distance = calculateDistance(lat, lng, store.getLatitude(), store.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearestStore = store;
                }
            }
        }

        if (nearestStore == null) {
            throw new PlentiException("No stores with valid coordinates found");
        }

        log.info("Found nearest store: {} at distance {} km", nearestStore.getName(), minDistance);
        return toStoreDTO(nearestStore);
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
