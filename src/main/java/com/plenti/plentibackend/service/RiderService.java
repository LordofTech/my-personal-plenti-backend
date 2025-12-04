package com.plenti.plentibackend.service;

import com.plenti.plentibackend.entity.Rider;
import com.plenti.plentibackend.entity.RiderLocation;
import com.plenti.plentibackend.repository.RiderLocationRepository;
import com.plenti.plentibackend.repository.RiderRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for managing delivery riders
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class RiderService {

    private final RiderRepository riderRepository;
    private final RiderLocationRepository riderLocationRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Create new rider
     */
    public Rider createRider(Rider rider) {
        log.info("Creating new rider: {}", rider.getName());
        rider.setPassword(passwordEncoder.encode(rider.getPassword()));
        rider.setStatus("AVAILABLE");
        rider.setIsActive(true);
        return riderRepository.save(rider);
    }

    /**
     * Get rider by ID
     */
    public Rider getRiderById(Long id) {
        log.info("Fetching rider with ID: {}", id);
        return riderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rider not found with ID: " + id));
    }

    /**
     * Get rider by phone number
     */
    public Rider getRiderByPhoneNumber(String phoneNumber) {
        log.info("Fetching rider with phone: {}", phoneNumber);
        return riderRepository.findByPhoneNumber(phoneNumber)
                .orElseThrow(() -> new RuntimeException("Rider not found with phone: " + phoneNumber));
    }

    /**
     * Get all active riders
     */
    public List<Rider> getActiveRiders() {
        log.info("Fetching all active riders");
        return riderRepository.findByIsActiveTrue();
    }

    /**
     * Get available riders
     */
    public List<Rider> getAvailableRiders() {
        log.info("Fetching available riders");
        return riderRepository.findByStatusAndIsActiveTrue("AVAILABLE");
    }

    /**
     * Update rider status
     */
    public Rider updateRiderStatus(Long riderId, String status) {
        log.info("Updating rider {} status to: {}", riderId, status);
        Rider rider = getRiderById(riderId);
        rider.setStatus(status);
        return riderRepository.save(rider);
    }

    /**
     * Update rider location
     */
    public RiderLocation updateRiderLocation(Long riderId, Double latitude, Double longitude) {
        log.info("Updating location for rider: {}", riderId);
        
        Rider rider = getRiderById(riderId);
        rider.setCurrentLatitude(latitude);
        rider.setCurrentLongitude(longitude);
        rider.setLastLocationUpdate(LocalDateTime.now());
        riderRepository.save(rider);
        
        RiderLocation location = new RiderLocation();
        location.setRiderId(riderId);
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setTimestamp(LocalDateTime.now());
        
        return riderLocationRepository.save(location);
    }

    /**
     * Get rider's latest location
     */
    public RiderLocation getRiderLatestLocation(Long riderId) {
        log.info("Fetching latest location for rider: {}", riderId);
        return riderLocationRepository.findFirstByRiderIdOrderByTimestampDesc(riderId)
                .orElse(null);
    }

    /**
     * Get rider's location history
     */
    public List<RiderLocation> getRiderLocationHistory(Long riderId) {
        log.info("Fetching location history for rider: {}", riderId);
        return riderLocationRepository.findByRiderIdOrderByTimestampDesc(riderId);
    }

    /**
     * Update rider rating
     */
    public Rider updateRiderRating(Long riderId, Double newRating) {
        log.info("Updating rating for rider {}: {}", riderId, newRating);
        Rider rider = getRiderById(riderId);
        
        // Calculate weighted average
        int totalDeliveries = rider.getTotalDeliveries();
        double currentRating = rider.getRating();
        double updatedRating = ((currentRating * totalDeliveries) + newRating) / (totalDeliveries + 1);
        
        rider.setRating(updatedRating);
        rider.setTotalDeliveries(totalDeliveries + 1);
        
        return riderRepository.save(rider);
    }

    /**
     * Activate/Deactivate rider
     */
    public Rider toggleRiderActiveStatus(Long riderId) {
        log.info("Toggling active status for rider: {}", riderId);
        Rider rider = getRiderById(riderId);
        rider.setIsActive(!rider.getIsActive());
        return riderRepository.save(rider);
    }
}
