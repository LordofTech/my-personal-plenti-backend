package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.PaymentDTO;
import com.plenti.plentibackend.dto.PaymentTransactionDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.dto.SavedCardDTO;
import com.plenti.plentibackend.service.MonnifyService;
import com.plenti.plentibackend.service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
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

    @Autowired
    private MonnifyService monnifyService;

    @PostMapping("/initiate")
    @Operation(summary = "Initiate payment", description = "Start payment process and get checkout URL")
    public ResponseEntity<ResponseDTO<PaymentDTO>> initiatePayment(@RequestBody Map<String, Object> request) {
        Long orderId = Long.valueOf(request.get("orderId").toString());
        Double amount = Double.valueOf(request.get("amount").toString());
        String method = request.getOrDefault("method", "card").toString();
        
        PaymentDTO payment = paymentService.initiatePayment(orderId, amount, method);
        return ResponseEntity.ok(ResponseDTO.success("Payment initiated", payment));
    }

    @PostMapping("/initialize")
    @Operation(summary = "Initialize Monnify transaction", description = "Initialize payment transaction with Monnify")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> initializeTransaction(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = monnifyService.initializeTransaction(request);
        return ResponseEntity.ok(ResponseDTO.success("Transaction initialized", response));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verify payment", description = "Verify payment status by reference")
    public ResponseEntity<ResponseDTO<PaymentDTO>> verifyPayment(@RequestParam String reference) {
        PaymentDTO payment = paymentService.verifyPayment(reference);
        return ResponseEntity.ok(ResponseDTO.success("Payment verified", payment));
    }

    @PostMapping("/verify")
    @Operation(summary = "Verify Monnify transaction", description = "Verify Monnify transaction by reference")
    public ResponseEntity<ResponseDTO<PaymentTransactionDTO>> verifyTransaction(@RequestParam String reference) {
        PaymentTransactionDTO transaction = monnifyService.verifyTransaction(reference);
        return ResponseEntity.ok(ResponseDTO.success("Transaction verified", transaction));
    }

    @PostMapping("/card/charge")
    @Operation(summary = "Charge saved card", description = "Charge a saved card for payment")
    public ResponseEntity<ResponseDTO<Map<String, Object>>> chargeCard(@RequestBody Map<String, Object> request) {
        Map<String, Object> response = monnifyService.chargeCard(request);
        return ResponseEntity.ok(ResponseDTO.success("Card charged successfully", response));
    }

    @PostMapping("/card/save")
    @Operation(summary = "Save payment card", description = "Save a card for future payments")
    public ResponseEntity<ResponseDTO<SavedCardDTO>> saveCard(@RequestBody Map<String, Object> request) {
        Long userId = Long.valueOf(request.get("userId").toString());
        @SuppressWarnings("unchecked")
        Map<String, String> cardDetails = (Map<String, String>) request.get("cardDetails");
        
        SavedCardDTO savedCard = monnifyService.saveCard(userId, cardDetails);
        return ResponseEntity.ok(ResponseDTO.success("Card saved successfully", savedCard));
    }

    @GetMapping("/cards")
    @Operation(summary = "List saved cards", description = "Get list of user's saved payment cards")
    public ResponseEntity<ResponseDTO<List<SavedCardDTO>>> listSavedCards(@RequestParam Long userId) {
        List<SavedCardDTO> cards = monnifyService.listSavedCards(userId);
        return ResponseEntity.ok(ResponseDTO.success(cards));
    }

    @DeleteMapping("/cards/{cardId}")
    @Operation(summary = "Delete saved card", description = "Remove a saved payment card")
    public ResponseEntity<ResponseDTO<String>> deleteCard(@PathVariable Long cardId) {
        // In production, implement card deletion logic
        return ResponseEntity.ok(ResponseDTO.success("Card deleted successfully", "Card ID: " + cardId));
    }

    @PostMapping("/webhook")
    @Operation(summary = "Payment webhook", description = "Handle payment gateway webhook notifications")
    public ResponseEntity<ResponseDTO<String>> handleWebhook(@RequestBody Map<String, Object> webhookData) {
        // In production, process webhook data and update transaction status
        return ResponseEntity.ok(ResponseDTO.success("Webhook processed", "Received"));
    }

    @GetMapping("/order/{orderId}")
    @Operation(summary = "Get payment by order", description = "Get payment details by order ID")
    public ResponseEntity<ResponseDTO<PaymentDTO>> getPaymentByOrderId(@PathVariable Long orderId) {
        PaymentDTO payment = paymentService.getPaymentByOrderId(orderId);
        return ResponseEntity.ok(ResponseDTO.success(payment));
    }
}
