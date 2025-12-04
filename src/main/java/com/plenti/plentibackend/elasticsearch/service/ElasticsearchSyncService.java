package com.plenti.plentibackend.elasticsearch.service;

import com.plenti.plentibackend.elasticsearch.document.CategoryDocument;
import com.plenti.plentibackend.elasticsearch.document.ProductDocument;
import com.plenti.plentibackend.elasticsearch.document.StoreDocument;
import com.plenti.plentibackend.elasticsearch.mapper.DocumentMapper;
import com.plenti.plentibackend.elasticsearch.repository.CategorySearchRepository;
import com.plenti.plentibackend.elasticsearch.repository.ProductSearchRepository;
import com.plenti.plentibackend.elasticsearch.repository.StoreSearchRepository;
import com.plenti.plentibackend.entity.Category;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.entity.Store;
import com.plenti.plentibackend.repository.CategoryRepository;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.repository.StoreRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Service to synchronize data from MySQL to Elasticsearch
 */
@Service
public class ElasticsearchSyncService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchSyncService.class);

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private CategorySearchRepository categorySearchRepository;

    @Autowired
    private StoreSearchRepository storeSearchRepository;

    @Autowired
    private DocumentMapper documentMapper;

    @Autowired
    private ElasticsearchService elasticsearchService;

    /**
     * Sync all products from MySQL to Elasticsearch
     */
    @Transactional(readOnly = true)
    public void syncAllProducts() {
        if (!elasticsearchService.isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, skipping product sync");
            return;
        }

        try {
            logger.info("Starting product sync to Elasticsearch");
            List<Product> products = productRepository.findAll();
            List<ProductDocument> documents = products.stream()
                    .map(documentMapper::toProductDocument)
                    .collect(Collectors.toList());
            
            productSearchRepository.saveAll(documents);
            logger.info("Successfully synced {} products to Elasticsearch", documents.size());
        } catch (Exception e) {
            logger.error("Error syncing products to Elasticsearch: {}", e.getMessage(), e);
        }
    }

    /**
     * Sync a single product to Elasticsearch
     */
    public void syncProduct(Product product) {
        if (!elasticsearchService.isElasticsearchAvailable()) {
            logger.debug("Elasticsearch not available, skipping product sync for ID: {}", product.getId());
            return;
        }

        try {
            ProductDocument document = documentMapper.toProductDocument(product);
            productSearchRepository.save(document);
            logger.debug("Synced product {} to Elasticsearch", product.getId());
        } catch (Exception e) {
            logger.error("Error syncing product {} to Elasticsearch: {}", product.getId(), e.getMessage(), e);
        }
    }

    /**
     * Delete a product from Elasticsearch
     */
    public void deleteProduct(Long productId) {
        if (!elasticsearchService.isElasticsearchAvailable()) {
            logger.debug("Elasticsearch not available, skipping product deletion for ID: {}", productId);
            return;
        }

        try {
            productSearchRepository.deleteById(productId);
            logger.debug("Deleted product {} from Elasticsearch", productId);
        } catch (Exception e) {
            logger.error("Error deleting product {} from Elasticsearch: {}", productId, e.getMessage(), e);
        }
    }

    /**
     * Sync all categories from MySQL to Elasticsearch
     */
    @Transactional(readOnly = true)
    public void syncAllCategories() {
        if (!elasticsearchService.isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, skipping category sync");
            return;
        }

        try {
            logger.info("Starting category sync to Elasticsearch");
            List<Category> categories = categoryRepository.findAll();
            List<CategoryDocument> documents = categories.stream()
                    .map(documentMapper::toCategoryDocument)
                    .collect(Collectors.toList());
            
            categorySearchRepository.saveAll(documents);
            logger.info("Successfully synced {} categories to Elasticsearch", documents.size());
        } catch (Exception e) {
            logger.error("Error syncing categories to Elasticsearch: {}", e.getMessage(), e);
        }
    }

    /**
     * Sync all stores from MySQL to Elasticsearch
     */
    @Transactional(readOnly = true)
    public void syncAllStores() {
        if (!elasticsearchService.isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, skipping store sync");
            return;
        }

        try {
            logger.info("Starting store sync to Elasticsearch");
            List<Store> stores = storeRepository.findAll();
            List<StoreDocument> documents = stores.stream()
                    .map(documentMapper::toStoreDocument)
                    .collect(Collectors.toList());
            
            storeSearchRepository.saveAll(documents);
            logger.info("Successfully synced {} stores to Elasticsearch", documents.size());
        } catch (Exception e) {
            logger.error("Error syncing stores to Elasticsearch: {}", e.getMessage(), e);
        }
    }

    /**
     * Reindex all data from MySQL to Elasticsearch
     */
    public void reindexAll() {
        logger.info("Starting full reindex of all data");
        syncAllProducts();
        syncAllCategories();
        syncAllStores();
        logger.info("Completed full reindex");
    }
}
