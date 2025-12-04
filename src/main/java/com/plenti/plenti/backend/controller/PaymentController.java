package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.PaymentDTO;
import com.plenti.plenti.backend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate a payment", description = "Starts a payment process for an order in the Plenti ecommerce app using Monnify integration, returning a checkout URL.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment initiated successfully with checkout URL"),
            @ApiResponse(responseCode = "400", description = "Invalid payment data")
    })
    public ResponseEntity<String> initiate(
            @Parameter(description = "Payment details including orderId, amount, method") @RequestBody PaymentDTO paymentDTO) {
        String checkoutUrl = paymentService.initiatePayment(paymentDTO);
        return ResponseEntity.ok(checkoutUrl);
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify payment status", description = "Verifies the status of a payment in the Plenti app after completion, matching payment success screens with receipt confirmation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Payment verified successfully"),
            @ApiResponse(responseCode = "404", description = "Transaction not found"),
            @ApiResponse(responseCode = "400", description = "Invalid transaction ID")
    })
    public ResponseEntity<PaymentDTO> verify(
            @Parameter(description = "Transaction ID or reference") @RequestParam String transactionId) {
        return ResponseEntity.ok(paymentService.verifyPayment(transactionId));
    }
}