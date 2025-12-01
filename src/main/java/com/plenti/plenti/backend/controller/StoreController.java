package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.StoreDTO;
import com.plenti.plenti.backend.service.StoreService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
public class StoreController {

    @Autowired
    private StoreService storeService;

    @GetMapping
    @Operation(summary = "Get all stores", description = "Retrieves a list of all dark stores and partner stores in the Plenti ecommerce app for hybrid fulfillment, targeting 20 in Lagos initially.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Stores retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<StoreDTO>> getAll() {
        return ResponseEntity.ok(storeService.findAll());
    }

    @PostMapping
    @Operation(summary = "Add a new store", description = "Adds a new dark store or partner store in the Plenti app for seamless drop-shipping and scale plan expansion.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store added successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid store data")
    })
    public ResponseEntity<StoreDTO> addStore(
            @Parameter(description = "Store details including name, location, type (dark/partner)") @RequestBody StoreDTO storeDTO) {
        return ResponseEntity.ok(storeService.save(storeDTO));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get store by ID", description = "Retrieves details of a specific store in the Plenti app for fulfillment logic.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Store not found")
    })
    public ResponseEntity<StoreDTO> getById(
            @Parameter(description = "Store ID") @PathVariable String id) {
        return ResponseEntity.ok(storeService.findById(id));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a store", description = "Updates details of an existing store in the Plenti app, supporting the hybrid model of owned dark stores and partners.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Store updated successfully"),
            @ApiResponse(responseCode = "404", description = "Store not found"),
            @ApiResponse(responseCode = "400", description = "Invalid update data")
    })
    public ResponseEntity<StoreDTO> updateStore(
            @Parameter(description = "Store ID") @PathVariable String id,
            @Parameter(description = "Updated store details") @RequestBody StoreDTO storeDTO) {
        return ResponseEntity.ok(storeService.update(id, storeDTO));
    }
}