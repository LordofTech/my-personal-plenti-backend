package com.plenti.plentibackend.elasticsearch;

import com.plenti.plentibackend.elasticsearch.service.ElasticsearchService;
import com.plenti.plentibackend.elasticsearch.service.ElasticsearchSyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * Initializes Elasticsearch indices on application startup
 * Runs after DataLoader to sync initial data
 */
@Component
@Order(3)
@ConditionalOnProperty(name = "elasticsearch.enabled", havingValue = "true", matchIfMissing = false)
public class ElasticsearchInitializer implements CommandLineRunner {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchInitializer.class);

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private ElasticsearchSyncService elasticsearchSyncService;

    @Override
    public void run(String... args) throws Exception {
        logger.info("Initializing Elasticsearch...");

        if (!elasticsearchService.isElasticsearchAvailable()) {
            logger.warn("Elasticsearch is not available. Skipping initialization. Application will continue with MySQL-based search.");
            return;
        }

        try {
            logger.info("Elasticsearch is available. Starting data synchronization...");
            
            // Sync all data from MySQL to Elasticsearch
            elasticsearchSyncService.reindexAll();
            
            logger.info("Elasticsearch initialization completed successfully");
        } catch (Exception e) {
            logger.error("Error during Elasticsearch initialization: {}", e.getMessage(), e);
            logger.warn("Application will continue with MySQL-based search");
        }
    }
}
