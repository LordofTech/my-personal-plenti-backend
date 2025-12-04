package com.plenti.plenti.backend.service;

import com.plenti.plenti.backend.dto.PaymentDTO;
import com.plenti.plenti.backend.entity.Payment;
import com.plenti.plenti.backend.repository.PaymentRepository;
import com.plenti.plenti.backend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentService {

    @Autowired
    private RestTemplate monnifyRestTemplate;

    @Autowired
    private PaymentRepository paymentRepository;

    @Value("${monnify.base.url:https://api.monnify.com}")
    private String monnifyBaseUrl;

    @Value("${monnify.contract.code:YOUR_CONTRACT_CODE}")
    private String contractCode;  // Configured in application.properties; replace placeholder

    public String initiatePayment(PaymentDTO paymentDTO) {
        // Prepare request body for Monnify API, aligning with Plenti's transparent pricing and secure trade
        Map<String, Object> request = new HashMap<>();
        request.put("amount", paymentDTO.getAmount());
        // Use defaults if DTO fields missing; add customerName/email to PaymentDTO for real use
        request.put("customerName", "Customer Name");  // paymentDTO.getCustomerName() if added to DTO
        request.put("customerEmail", "customer@example.com");  // paymentDTO.getCustomerEmail() if added
        request.put("paymentReference", paymentDTO.getReference());
        request.put("paymentDescription", "Plenti Order Payment for Order ID: " + paymentDTO.getOrderId());
        request.put("currencyCode", "NGN");
        request.put("contractCode", contractCode);

        // Add headers if needed (e.g., API key from config)
        HttpHeaders headers = new HttpHeaders();
        // headers.add("Authorization", "Bearer " + monnifyApiKey);  // Uncomment and configure if required

        ResponseEntity<Map> response = monnifyRestTemplate.exchange(
                monnifyBaseUrl + "/api/v1/merchant/transactions/init-transaction",
                HttpMethod.POST,
                new HttpEntity<>(request, headers),
                Map.class
        );

        Map body = response.getBody();
        if (body == null || !body.containsKey("checkoutUrl")) {
            throw new RuntimeException("Failed to initiate payment");
        }
        String checkoutUrl = (String) body.get("checkoutUrl");

        // Save payment entity for tracking in Plenti's system
        Payment payment = Mapper.toPayment(paymentDTO);
        payment.setStatus("Initiated");
        paymentRepository.save(payment);

        return checkoutUrl;
    }

    public PaymentDTO verifyPayment(String transactionId) {
        // Query Monnify for verification, supporting Plenti's reliable and fast fulfillment
        ResponseEntity<Map> response = monnifyRestTemplate.getForEntity(
                monnifyBaseUrl + "/api/v1/merchant/transactions/query?paymentReference=" + transactionId,
                Map.class
        );

        Map body = response.getBody();
        if (body == null || !body.containsKey("paymentStatus")) {
            throw new RuntimeException("Failed to verify payment");
        }
        String status = (String) body.get("paymentStatus");

        // Update local payment record
        Payment payment = paymentRepository.findByReference(transactionId);
        if (payment == null) {
            throw new RuntimeException("Payment not found");
        }
        payment.setStatus(status);
        return Mapper.toPaymentDTO(paymentRepository.save(payment));
    }
}