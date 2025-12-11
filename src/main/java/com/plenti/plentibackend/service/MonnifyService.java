package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.PaymentTransactionDTO;
import com.plenti.plentibackend.dto.SavedCardDTO;
import com.plenti.plentibackend.entity.PaymentTransaction;
import com.plenti.plentibackend.entity.SavedCard;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.PaymentTransactionRepository;
import com.plenti.plentibackend.repository.SavedCardRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service for Monnify payment processing
 */
@Service
@Slf4j
public class MonnifyService {

    @Value("${monnify.api.key:}")
    private String monnifyApiKey;

    @Value("${monnify.secret.key:}")
    private String monnifySecretKey;

    @Value("${monnify.contract.code:}")
    private String monnifyContractCode;

    @Value("${monnify.base.url:https://sandbox.monnify.com}")
    private String monnifyBaseUrl;

    @Autowired
    private PaymentTransactionRepository paymentTransactionRepository;

    @Autowired
    private SavedCardRepository savedCardRepository;

    private final WebClient webClient;

    public MonnifyService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Authenticate with Monnify API
     */
    public String authenticate() {
        if (monnifyApiKey == null || monnifyApiKey.isEmpty()) {
            log.warn("Monnify API key not configured");
            return "mock-token";
        }

        try {
            // In production, call Monnify auth endpoint
            log.info("Authenticating with Monnify...");
            return "mock-token";
        } catch (Exception e) {
            log.error("Monnify authentication failed: {}", e.getMessage());
            throw new PlentiException("Payment authentication failed");
        }
    }

    /**
     * Initialize payment transaction
     */
    @Transactional
    public Map<String, Object> initializeTransaction(Map<String, Object> paymentRequest) {
        Long userId = Long.valueOf(paymentRequest.get("userId").toString());
        Long orderId = paymentRequest.get("orderId") != null ? 
                Long.valueOf(paymentRequest.get("orderId").toString()) : null;
        Double amount = Double.valueOf(paymentRequest.get("amount").toString());
        String paymentMethod = paymentRequest.getOrDefault("paymentMethod", "card").toString();

        // Generate unique transaction reference
        String transactionRef = "TXN_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8);

        // Create payment transaction record
        PaymentTransaction transaction = new PaymentTransaction();
        transaction.setUserId(userId);
        transaction.setOrderId(orderId);
        transaction.setTransactionReference(transactionRef);
        transaction.setAmount(amount);
        transaction.setStatus("PENDING");
        transaction.setPaymentMethod(paymentMethod);
        transaction.setPaymentGateway("monnify");
        paymentTransactionRepository.save(transaction);

        // In production, call Monnify API to initialize transaction
        Map<String, Object> response = new HashMap<>();
        response.put("transactionReference", transactionRef);
        response.put("checkoutUrl", monnifyBaseUrl + "/checkout/" + transactionRef);
        response.put("amount", amount);
        response.put("status", "PENDING");

        log.info("Payment transaction initialized: {}", transactionRef);
        return response;
    }

    /**
     * Verify transaction status
     */
    @Transactional
    public PaymentTransactionDTO verifyTransaction(String transactionReference) {
        PaymentTransaction transaction = paymentTransactionRepository
                .findByTransactionReference(transactionReference)
                .orElseThrow(() -> new PlentiException("Transaction not found"));

        // In production, call Monnify API to verify transaction
        // For now, simulate verification
        log.info("Verifying transaction: {}", transactionReference);

        return toPaymentTransactionDTO(transaction);
    }

    /**
     * Charge a card
     */
    public Map<String, Object> chargeCard(Map<String, Object> cardPaymentRequest) {
        Long userId = Long.valueOf(cardPaymentRequest.get("userId").toString());
        Double amount = Double.valueOf(cardPaymentRequest.get("amount").toString());
        String cardToken = cardPaymentRequest.get("cardToken").toString();

        // In production, call Monnify API to charge card
        log.info("Charging card for user {}: amount {}", userId, amount);

        Map<String, Object> response = new HashMap<>();
        response.put("status", "SUCCESS");
        response.put("transactionReference", "TXN_" + System.currentTimeMillis());
        response.put("amount", amount);
        return response;
    }

    /**
     * Save card for user
     */
    @Transactional
    public SavedCardDTO saveCard(Long userId, Map<String, String> cardDetails) {
        SavedCard savedCard = new SavedCard();
        savedCard.setUserId(userId);
        savedCard.setCardToken(cardDetails.get("cardToken"));
        savedCard.setLast4Digits(cardDetails.get("last4Digits"));
        savedCard.setCardType(cardDetails.get("cardType"));
        savedCard.setExpiryMonth(cardDetails.get("expiryMonth"));
        savedCard.setExpiryYear(cardDetails.get("expiryYear"));
        savedCard.setIsDefault(Boolean.parseBoolean(cardDetails.getOrDefault("isDefault", "false")));

        // If this card is set as default, unset other defaults
        if (savedCard.getIsDefault()) {
            savedCardRepository.findByUserIdAndIsDefaultTrue(userId)
                    .ifPresent(card -> {
                        card.setIsDefault(false);
                        savedCardRepository.save(card);
                    });
        }

        savedCard = savedCardRepository.save(savedCard);
        log.info("Card saved for user {}: ending in {}", userId, savedCard.getLast4Digits());

        return toSavedCardDTO(savedCard);
    }

    /**
     * List saved cards for user
     */
    public List<SavedCardDTO> listSavedCards(Long userId) {
        return savedCardRepository.findByUserId(userId)
                .stream()
                .map(this::toSavedCardDTO)
                .collect(Collectors.toList());
    }

    // Helper methods
    private PaymentTransactionDTO toPaymentTransactionDTO(PaymentTransaction transaction) {
        PaymentTransactionDTO dto = new PaymentTransactionDTO();
        dto.setId(transaction.getId());
        dto.setUserId(transaction.getUserId());
        dto.setOrderId(transaction.getOrderId());
        dto.setTransactionReference(transaction.getTransactionReference());
        dto.setAmount(transaction.getAmount());
        dto.setStatus(transaction.getStatus());
        dto.setPaymentMethod(transaction.getPaymentMethod());
        dto.setPaymentGateway(transaction.getPaymentGateway());
        dto.setCardType(transaction.getCardType());
        dto.setLast4Digits(transaction.getLast4Digits());
        dto.setCreatedAt(transaction.getCreatedAt());
        dto.setCompletedAt(transaction.getCompletedAt());
        return dto;
    }

    private SavedCardDTO toSavedCardDTO(SavedCard card) {
        SavedCardDTO dto = new SavedCardDTO();
        dto.setId(card.getId());
        dto.setUserId(card.getUserId());
        dto.setCardToken(card.getCardToken());
        dto.setLast4Digits(card.getLast4Digits());
        dto.setCardType(card.getCardType());
        dto.setExpiryMonth(card.getExpiryMonth());
        dto.setExpiryYear(card.getExpiryYear());
        dto.setIsDefault(card.getIsDefault());
        dto.setCreatedAt(card.getCreatedAt());
        return dto;
    }
}
