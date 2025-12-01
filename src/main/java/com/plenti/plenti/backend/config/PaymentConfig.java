package com.plenti.plenti.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.web.client.RestTemplate;
import java.util.Base64;

@Configuration
public class PaymentConfig {

    private static final String MONNIFY_API_KEY = "YOUR_MONNIFY_API_KEY"; // Placeholder
    private static final String MONNIFY_SECRET_KEY = "YOUR_MONNIFY_SECRET_KEY"; // Placeholder

    @Bean
    public RestTemplate monnifyRestTemplate() {
        RestTemplate restTemplate = new RestTemplate();
        String auth = MONNIFY_API_KEY + ":" + MONNIFY_SECRET_KEY;
        String base64Auth = Base64.getEncoder().encodeToString(auth.getBytes());
        ClientHttpRequestInterceptor interceptor = (request, body, execution) -> {
            request.getHeaders().add("Authorization", "Basic " + base64Auth);
            return execution.execute(request, body);
        };
        restTemplate.getInterceptors().add(interceptor);
        return restTemplate;
    }
}
