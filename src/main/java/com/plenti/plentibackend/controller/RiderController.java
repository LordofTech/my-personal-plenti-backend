package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.entity.Rider;
import com.plenti.plentibackend.entity.RiderLocation;
import com.plenti.plentibackend.service.RiderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for rider operations
 */
@RestController
@RequestMapping("/api/rider")
@RequiredArgsConstructor
@Tag(name = "Rider", description = "Delivery rider operations")
public class RiderController {

    private final RiderService riderService;

    @PostMapping("/login")
    @Operation(summary = "Rider login")
    public ResponseEntity<?> riderLogin(@RequestBody Map<String, String> credentials) {
        String phoneNumber = credentials.get("phoneNumber");
        String password = credentials.get("password");
        
        // TODO: Implement proper authentication with password validation and JWT token generation
        // For now, returning error message
        return ResponseEntity.status(501)
                .body(Map.of("message", "Rider authentication not fully implemented. Use admin endpoints to manage riders."));
    }

    @GetMapping("/available")
    @Operation(summary = "Get available riders")
    public ResponseEntity<List<Rider>> getAvailableRiders() {
        return ResponseEntity.ok(riderService.getAvailableRiders());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get rider by ID")
    public ResponseEntity<Rider> getRiderById(@PathVariable Long id) {
        return ResponseEntity.ok(riderService.getRiderById(id));
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Update rider status")
    public ResponseEntity<Rider> updateRiderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> status) {
        return ResponseEntity.ok(riderService.updateRiderStatus(id, status.get("status")));
    }

    @PutMapping("/location")
    @Operation(summary = "Update rider location")
    public ResponseEntity<RiderLocation> updateRiderLocation(@RequestBody Map<String, Object> location) {
        Long riderId = Long.valueOf(location.get("riderId").toString());
        Double latitude = Double.valueOf(location.get("latitude").toString());
        Double longitude = Double.valueOf(location.get("longitude").toString());
        return ResponseEntity.ok(riderService.updateRiderLocation(riderId, latitude, longitude));
    }

    @GetMapping("/{id}/location")
    @Operation(summary = "Get rider's latest location")
    public ResponseEntity<RiderLocation> getRiderLatestLocation(@PathVariable Long id) {
        return ResponseEntity.ok(riderService.getRiderLatestLocation(id));
    }

    @GetMapping("/{id}/location/history")
    @Operation(summary = "Get rider's location history")
    public ResponseEntity<List<RiderLocation>> getRiderLocationHistory(@PathVariable Long id) {
        return ResponseEntity.ok(riderService.getRiderLocationHistory(id));
    }

    @PostMapping
    @Operation(summary = "Create new rider (admin)")
    public ResponseEntity<Rider> createRider(@RequestBody Rider rider) {
        return ResponseEntity.ok(riderService.createRider(rider));
    }

    @PutMapping("/{id}/toggle")
    @Operation(summary = "Toggle rider active status (admin)")
    public ResponseEntity<Rider> toggleRiderActiveStatus(@PathVariable Long id) {
        return ResponseEntity.ok(riderService.toggleRiderActiveStatus(id));
    }
}
