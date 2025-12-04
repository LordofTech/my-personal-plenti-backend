package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.elasticsearch.document.CategoryDocument;
import com.plenti.plentibackend.elasticsearch.document.ProductDocument;
import com.plenti.plentibackend.elasticsearch.document.StoreDocument;
import com.plenti.plentibackend.elasticsearch.service.ElasticsearchService;
import com.plenti.plentibackend.elasticsearch.service.ElasticsearchSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST controller for Elasticsearch search operations
 */
@RestController
@RequestMapping("/api/es")
@Tag(name = "Elasticsearch", description = "Fast search endpoints powered by Elasticsearch")
public class ElasticsearchController {

    @Autowired
    private ElasticsearchService elasticsearchService;

    @Autowired
    private ElasticsearchSyncService elasticsearchSyncService;

    /**
     * Fast product search
     */
    @GetMapping("/products/search")
    @Operation(summary = "Search products", description = "Fast full-text search for products with fuzzy matching")
    public ResponseEntity<List<ProductDocument>> searchProducts(@RequestParam String q) {
        List<ProductDocument> results = elasticsearchService.searchProducts(q);
        return ResponseEntity.ok(results);
    }

    /**
     * Advanced product search with filters
     */
    @GetMapping("/products/advanced-search")
    @Operation(summary = "Advanced product search", description = "Search products with filters (category, price range, stock)")
    public ResponseEntity<List<ProductDocument>> advancedSearch(
            @RequestParam(required = false) String q,
            @RequestParam(required = false) Long category,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean inStock) {
        
        List<ProductDocument> results = elasticsearchService.advancedSearch(
                q, category, minPrice, maxPrice, inStock);
        return ResponseEntity.ok(results);
    }

    /**
     * Autocomplete suggestions
     */
    @GetMapping("/autocomplete")
    @Operation(summary = "Autocomplete", description = "Get autocomplete suggestions for product names")
    public ResponseEntity<List<String>> autocomplete(
            @RequestParam String q,
            @RequestParam(defaultValue = "10") int limit) {
        
        List<String> suggestions = elasticsearchService.autocomplete(q, limit);
        return ResponseEntity.ok(suggestions);
    }

    /**
     * Search categories
     */
    @GetMapping("/categories/search")
    @Operation(summary = "Search categories", description = "Search categories by name or description")
    public ResponseEntity<List<CategoryDocument>> searchCategories(@RequestParam String q) {
        List<CategoryDocument> results = elasticsearchService.searchCategories(q);
        return ResponseEntity.ok(results);
    }

    /**
     * Search stores
     */
    @GetMapping("/stores/search")
    @Operation(summary = "Search stores", description = "Search stores by name or location")
    public ResponseEntity<List<StoreDocument>> searchStores(@RequestParam String q) {
        List<StoreDocument> results = elasticsearchService.searchStores(q);
        return ResponseEntity.ok(results);
    }

    /**
     * Manual reindex (admin only)
     */
    @PostMapping("/reindex")
    @Operation(summary = "Reindex all data", description = "Manually trigger full reindex from MySQL to Elasticsearch")
    public ResponseEntity<Map<String, String>> reindex() {
        elasticsearchSyncService.reindexAll();
        
        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "Reindex started successfully");
        
        return ResponseEntity.ok(response);
    }

    /**
     * Elasticsearch health check
     */
    @GetMapping("/health")
    @Operation(summary = "Health check", description = "Check Elasticsearch availability")
    public ResponseEntity<Map<String, Object>> health() {
        boolean available = elasticsearchService.isElasticsearchAvailable();
        
        Map<String, Object> response = new HashMap<>();
        response.put("elasticsearch", available ? "UP" : "DOWN");
        response.put("status", available ? "healthy" : "unavailable");
        
        return ResponseEntity.ok(response);
    }
}
