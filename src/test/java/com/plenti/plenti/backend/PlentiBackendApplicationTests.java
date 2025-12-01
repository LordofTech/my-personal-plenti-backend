package com.plenti.plenti.backend;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootTest
class PlentiBackendApplicationTests {

    // Since Elasticsearch is no longer used, we remove ElasticsearchContainer setup

    // If you still need MySQL in your tests, you can set up a container for it
    // or update this section to use MySQL-related setup if required.
    // Example MySQL container setup (if needed):
    /*
    @Container
    static MySQLContainer mysqlContainer = new MySQLContainer("mysql:8.0.26")
            .withUsername("test")
            .withPassword("test")
            .withDatabaseName("testdb");
    */

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        // If you are using MySQL now, provide MySQL configuration here
        // registry.add("spring.datasource.url", mysqlContainer::getJdbcUrl);
        // registry.add("spring.datasource.username", mysqlContainer::getUsername);
        // registry.add("spring.datasource.password", mysqlContainer::getPassword);
    }

    @Test
    void contextLoads() {
    }
}
