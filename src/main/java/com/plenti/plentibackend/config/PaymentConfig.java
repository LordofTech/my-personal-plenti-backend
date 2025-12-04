package com.plenti.plentibackend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Monnify payment integration
 */
@Configuration
public class PaymentConfig {

    @Value("${monnify.api.key}")
    private String apiKey;

    @Value("${monnify.secret.key}")
    private String secretKey;

    @Value("${monnify.contract.code}")
    private String contractCode;

    @Value("${monnify.base.url}")
    private String baseUrl;

    public String getApiKey() {
        return apiKey;
    }

    public String getSecretKey() {
        return secretKey;
    }

    public String getContractCode() {
        return contractCode;
    }

    public String getBaseUrl() {
        return baseUrl;
    }
}
