package com.plenti.plentibackend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.Map;

/**
 * Service for sending SMS via Termii
 */
@Service
@Slf4j
public class SmsService {

    @Value("${sms.api.key:}")
    private String smsApiKey;

    @Value("${sms.api.url:https://api.ng.termii.com/api/sms/send}")
    private String smsApiUrl;

    @Value("${sms.sender.id:Plenti}")
    private String senderId;

    private final WebClient webClient;

    public SmsService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    /**
     * Send SMS to a phone number
     */
    public boolean sendSms(String phoneNumber, String message) {
        if (smsApiKey == null || smsApiKey.isEmpty()) {
            log.warn("SMS API key not configured. Skipping SMS send to {}", phoneNumber);
            log.info("SMS Message: {}", message);
            return true; // Return true in development mode
        }

        try {
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("to", phoneNumber);
            requestBody.put("from", senderId);
            requestBody.put("sms", message);
            requestBody.put("type", "plain");
            requestBody.put("channel", "generic");
            requestBody.put("api_key", smsApiKey);

            Mono<String> response = webClient.post()
                    .uri(smsApiUrl)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class);

            String result = response.block();
            log.info("SMS sent successfully to {}: {}", phoneNumber, result);
            return true;
        } catch (Exception e) {
            log.error("Failed to send SMS to {}: {}", phoneNumber, e.getMessage());
            return false;
        }
    }

    /**
     * Send OTP SMS
     */
    public boolean sendOtpSms(String phoneNumber, String otp) {
        String message = String.format("Your Plenti verification code is: %s. Valid for 10 minutes.", otp);
        return sendSms(phoneNumber, message);
    }

    /**
     * Send order confirmation SMS
     */
    public boolean sendOrderConfirmationSms(String phoneNumber, String orderId) {
        String message = String.format("Your Plenti order #%s has been confirmed! Track your order in the app.", orderId);
        return sendSms(phoneNumber, message);
    }

    /**
     * Send delivery notification SMS
     */
    public boolean sendDeliveryNotificationSms(String phoneNumber, String orderId, String riderName) {
        String message = String.format("Your Plenti order #%s is out for delivery with %s!", orderId, riderName);
        return sendSms(phoneNumber, message);
    }
}
