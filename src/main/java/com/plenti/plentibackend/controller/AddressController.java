package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.AddressDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for address operations
 */
@RestController
@RequestMapping("/api/addresses")
@Tag(name = "Addresses", description = "Address management endpoints")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @GetMapping("/{userId}")
    @Operation(summary = "Get user addresses", description = "Get all addresses for a user")
    public ResponseEntity<ResponseDTO<List<AddressDTO>>> getUserAddresses(@PathVariable Long userId) {
        List<AddressDTO> addresses = addressService.getUserAddresses(userId);
        return ResponseEntity.ok(ResponseDTO.success(addresses));
    }

    @PostMapping
    @Operation(summary = "Create address", description = "Add a new delivery address")
    public ResponseEntity<ResponseDTO<AddressDTO>> createAddress(@Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO created = addressService.createAddress(addressDTO);
        return ResponseEntity.ok(ResponseDTO.success("Address created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update address", description = "Update address details")
    public ResponseEntity<ResponseDTO<AddressDTO>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressDTO addressDTO) {
        AddressDTO updated = addressService.updateAddress(id, addressDTO);
        return ResponseEntity.ok(ResponseDTO.success("Address updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete address", description = "Delete an address")
    public ResponseEntity<ResponseDTO<String>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(ResponseDTO.success("Address deleted successfully", "deleted"));
    }

    @PutMapping("/{id}/default")
    @Operation(summary = "Set default address", description = "Set address as default")
    public ResponseEntity<ResponseDTO<AddressDTO>> setDefaultAddress(@PathVariable Long id) {
        AddressDTO address = addressService.setDefaultAddress(id);
        return ResponseEntity.ok(ResponseDTO.success("Default address set", address));
    }
}
