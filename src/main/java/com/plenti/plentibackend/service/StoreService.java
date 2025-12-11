package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.StoreDTO;
import com.plenti.plentibackend.entity.Store;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.StoreRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

/**
 * Service for store management operations
 */
@Service
public class StoreService {

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private Mapper mapper;
    
    @Autowired
    private StoreAssignmentService storeAssignmentService;

    public List<StoreDTO> getAllStores() {
        return storeRepository.findAll().stream()
                .map(mapper::toStoreDTO)
                .toList();
    }

    public StoreDTO getStoreById(Long id) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Store not found"));
        return mapper.toStoreDTO(store);
    }

    public List<StoreDTO> getStoresByType(String type) {
        return storeRepository.findByType(type).stream()
                .map(mapper::toStoreDTO)
                .toList();
    }

    @Transactional
    public StoreDTO createStore(StoreDTO storeDTO) {
        Store store = mapper.toStoreEntity(storeDTO);
        Store savedStore = storeRepository.save(store);
        return mapper.toStoreDTO(savedStore);
    }

    @Transactional
    public StoreDTO updateStore(Long id, StoreDTO storeDTO) {
        Store store = storeRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Store not found"));

        if (storeDTO.getName() != null) store.setName(storeDTO.getName());
        if (storeDTO.getLocation() != null) store.setLocation(storeDTO.getLocation());
        if (storeDTO.getType() != null) store.setType(storeDTO.getType());
        if (storeDTO.getInventoryCapacity() != null) store.setInventoryCapacity(storeDTO.getInventoryCapacity());
        if (storeDTO.getLatitude() != null) store.setLatitude(storeDTO.getLatitude());
        if (storeDTO.getLongitude() != null) store.setLongitude(storeDTO.getLongitude());

        Store updatedStore = storeRepository.save(store);
        return mapper.toStoreDTO(updatedStore);
    }

    @Transactional
    public void deleteStore(Long id) {
        if (!storeRepository.existsById(id)) {
            throw new PlentiException("Store not found");
        }
        storeRepository.deleteById(id);
    }

    public StoreDTO findNearestStore(Double latitude, Double longitude) {
        // Simple nearest store finder - in production, use proper geospatial queries
        List<Store> stores = storeRepository.findAll();
        Store nearest = null;
        double minDistance = Double.MAX_VALUE;

        for (Store store : stores) {
            if (store.getLatitude() != null && store.getLongitude() != null) {
                double distance = calculateDistance(latitude, longitude, 
                        store.getLatitude(), store.getLongitude());
                if (distance < minDistance) {
                    minDistance = distance;
                    nearest = store;
                }
            }
        }

        if (nearest == null) {
            throw new PlentiException("No stores available");
        }

        return mapper.toStoreDTO(nearest);
    }

    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        // Haversine formula for distance calculation
        double R = 6371; // Radius of the earth in km
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                Math.sin(dLon / 2) * Math.sin(dLon / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
    
    public Map<String, Object> calculateETA(Long storeId, double latitude, double longitude) {
        return storeAssignmentService.calculateETA(storeId, latitude, longitude);
    }
    
    public Map<String, Object> calculateDeliveryFee(Long storeId, double latitude, double longitude) {
        return storeAssignmentService.calculateDeliveryFee(storeId, latitude, longitude);
    }
    
    public Map<String, Object> checkDeliveryCoverage(double latitude, double longitude) {
        return storeAssignmentService.checkDeliveryCoverage(latitude, longitude);
    }
}
