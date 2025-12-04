package com.plenti.plentibackend.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

/**
 * Test to verify Elasticsearch configuration behavior
 */
@SpringBootTest
@TestPropertySource(properties = {
    "elasticsearch.enabled=false",
    "spring.data.elasticsearch.repositories.enabled=false"
})
class ElasticsearchConfigTest {

    @Test
    void contextLoadsWithoutElasticsearch() {
        // This test verifies that the application context loads successfully
        // when Elasticsearch is disabled, ensuring graceful fallback to MySQL
    }
}
