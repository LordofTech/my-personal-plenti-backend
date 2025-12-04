package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.PromoCodeDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.PromoCodeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for promo code operations
 */
@RestController
@RequestMapping("/api/promo")
@Tag(name = "Promo Codes", description = "Promo code management endpoints")
public class PromoCodeController {

    @Autowired
    private PromoCodeService promoCodeService;

    @PostMapping("/validate")
    @Operation(summary = "Validate promo code", description = "Check if promo code is valid")
    public ResponseEntity<ResponseDTO<PromoCodeDTO>> validatePromoCode(@RequestBody Map<String, Object> request) {
        String code = request.get("code").toString();
        Double orderAmount = Double.valueOf(request.get("orderAmount").toString());
        PromoCodeDTO promoCode = promoCodeService.validatePromoCode(code, orderAmount);
        return ResponseEntity.ok(ResponseDTO.success("Promo code is valid", promoCode));
    }

    @PostMapping("/apply")
    @Operation(summary = "Apply promo code", description = "Apply promo code to order and get discount")
    public ResponseEntity<ResponseDTO<Double>> applyPromoCode(@RequestBody Map<String, Object> request) {
        String code = request.get("code").toString();
        Double orderAmount = Double.valueOf(request.get("orderAmount").toString());
        Double discount = promoCodeService.applyPromoCode(code, orderAmount);
        return ResponseEntity.ok(ResponseDTO.success("Promo code applied", discount));
    }

    @GetMapping
    @Operation(summary = "Get all promo codes", description = "Get list of all promo codes (admin only)")
    public ResponseEntity<ResponseDTO<List<PromoCodeDTO>>> getAllPromoCodes() {
        List<PromoCodeDTO> promoCodes = promoCodeService.getAllPromoCodes();
        return ResponseEntity.ok(ResponseDTO.success(promoCodes));
    }

    @PostMapping
    @Operation(summary = "Create promo code", description = "Create a new promo code (admin only)")
    public ResponseEntity<ResponseDTO<PromoCodeDTO>> createPromoCode(@Valid @RequestBody PromoCodeDTO promoCodeDTO) {
        PromoCodeDTO created = promoCodeService.createPromoCode(promoCodeDTO);
        return ResponseEntity.ok(ResponseDTO.success("Promo code created successfully", created));
    }
}
