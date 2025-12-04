package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.dto.StoreDTO;
import com.plenti.plentibackend.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for store operations
 */
@RestController
@RequestMapping("/api/stores")
@Tag(name = "Stores", description = "Store management endpoints")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    @Operation(summary = "Get all stores", description = "Get list of all stores")
    public ResponseEntity<ResponseDTO<List<StoreDTO>>> getAllStores() {
        List<StoreDTO> stores = storeService.getAllStores();
        return ResponseEntity.ok(ResponseDTO.success(stores));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store by ID", description = "Get store details by ID")
    public ResponseEntity<ResponseDTO<StoreDTO>> getStoreById(@PathVariable Long id) {
        StoreDTO store = storeService.getStoreById(id);
        return ResponseEntity.ok(ResponseDTO.success(store));
    }

    @GetMapping("/type/{type}")
    @Operation(summary = "Get stores by type", description = "Get stores filtered by type (dark/partner)")
    public ResponseEntity<ResponseDTO<List<StoreDTO>>> getStoresByType(@PathVariable String type) {
        List<StoreDTO> stores = storeService.getStoresByType(type);
        return ResponseEntity.ok(ResponseDTO.success(stores));
    }

    @PostMapping
    @Operation(summary = "Create store", description = "Add a new store (admin only)")
    public ResponseEntity<ResponseDTO<StoreDTO>> createStore(@Valid @RequestBody StoreDTO storeDTO) {
        StoreDTO created = storeService.createStore(storeDTO);
        return ResponseEntity.ok(ResponseDTO.success("Store created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update store", description = "Update store details (admin only)")
    public ResponseEntity<ResponseDTO<StoreDTO>> updateStore(
            @PathVariable Long id,
            @Valid @RequestBody StoreDTO storeDTO) {
        StoreDTO updated = storeService.updateStore(id, storeDTO);
        return ResponseEntity.ok(ResponseDTO.success("Store updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete store", description = "Delete a store (admin only)")
    public ResponseEntity<ResponseDTO<String>> deleteStore(@PathVariable Long id) {
        storeService.deleteStore(id);
        return ResponseEntity.ok(ResponseDTO.success("Store deleted successfully", "deleted"));
    }

    @GetMapping("/nearest")
    @Operation(summary = "Find nearest store", description = "Find nearest store based on coordinates")
    public ResponseEntity<ResponseDTO<StoreDTO>> findNearestStore(
            @RequestParam Double latitude,
            @RequestParam Double longitude) {
        StoreDTO store = storeService.findNearestStore(latitude, longitude);
        return ResponseEntity.ok(ResponseDTO.success("Nearest store found", store));
    }
}
