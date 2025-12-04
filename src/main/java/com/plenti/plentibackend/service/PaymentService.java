package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.PaymentDTO;
import com.plenti.plentibackend.entity.Payment;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.PaymentRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.UUID;

/**
 * Service for payment processing with Monnify
 */
@Service
public class PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private Mapper mapper;

    @Value("${monnify.base.url}")
    private String monnifyBaseUrl;

    @Value("${monnify.api.key}")
    private String apiKey;

    @Value("${monnify.contract.code}")
    private String contractCode;

    private final WebClient webClient;

    public PaymentService() {
        this.webClient = WebClient.builder().build();
    }

    @Transactional
    public PaymentDTO initiatePayment(Long orderId, Double amount, String method) {
        Payment payment = new Payment();
        payment.setOrderId(orderId);
        payment.setAmount(amount);
        payment.setStatus("PENDING");
        payment.setReference(generateReference());
        payment.setMethod(method);

        Payment savedPayment = paymentRepository.save(payment);

        // In a real application, call Monnify API here
        String checkoutUrl = generateCheckoutUrl(savedPayment.getReference(), amount);

        PaymentDTO dto = mapper.toPaymentDTO(savedPayment);
        dto.setCheckoutUrl(checkoutUrl);
        return dto;
    }

    @Transactional
    public PaymentDTO verifyPayment(String reference) {
        Payment payment = paymentRepository.findByReference(reference)
                .orElseThrow(() -> new PlentiException("Payment not found"));

        // In a real application, verify with Monnify API
        // For now, we'll mark as successful
        payment.setStatus("SUCCESS");
        Payment updatedPayment = paymentRepository.save(payment);

        return mapper.toPaymentDTO(updatedPayment);
    }

    public PaymentDTO getPaymentByOrderId(Long orderId) {
        Payment payment = paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new PlentiException("Payment not found"));
        return mapper.toPaymentDTO(payment);
    }

    private String generateReference() {
        return "PLT-" + UUID.randomUUID().toString();
    }

    private String generateCheckoutUrl(String reference, Double amount) {
        // In a real application, this would call Monnify API
        return monnifyBaseUrl + "/checkout?ref=" + reference + "&amount=" + amount;
    }
}
