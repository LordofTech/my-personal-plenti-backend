package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.PaymentDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Controller for payment operations
 */
@RestController
@RequestMapping("/api/payments")
@Tag(name = "Payments", description = "Payment processing endpoints")
public class PaymentController {

    @Autowired
    private PaymentService paymentService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Start payment process and get checkout URL")
    public ResponseEntity<ResponseDTO<PaymentDTO>> initiatePayment(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());
        String method = request.getOrDefault("method", "card").toString();
        
        PaymentDTO payment = paymentService.initiatePayment(orderId, amount, method);
        return ResponseEntity.ok(ResponseDTO.success("Payment initiated", payment));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verify payment status by reference")
    public ResponseEntity<ResponseDTO<PaymentDTO>> verifyPayment(@RequestParam String reference) {
        PaymentDTO payment = paymentService.verifyPayment(reference);
        return ResponseEntity.ok(ResponseDTO.success("Payment verified", payment));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Get payment details by order ID")
    public ResponseEntity<ResponseDTO<PaymentDTO>> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ResponseDTO.success(payment));
    }
}
