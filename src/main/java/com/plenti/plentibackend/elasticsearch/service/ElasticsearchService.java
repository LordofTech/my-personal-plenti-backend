package com.plenti.plentibackend.elasticsearch.service;

import com.plenti.plentibackend.elasticsearch.document.CategoryDocument;
import com.plenti.plentibackend.elasticsearch.document.ProductDocument;
import com.plenti.plentibackend.elasticsearch.document.StoreDocument;
import com.plenti.plentibackend.elasticsearch.repository.CategorySearchRepository;
import com.plenti.plentibackend.elasticsearch.repository.ProductSearchRepository;
import com.plenti.plentibackend.elasticsearch.repository.StoreSearchRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.Query;
import org.springframework.stereotype.Service;

import co.elastic.clients.json.JsonData;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Service for Elasticsearch search operations with advanced features
 */
@Service
public class ElasticsearchService {

    private static final Logger logger = LoggerFactory.getLogger(ElasticsearchService.class);

    @Autowired
    private ProductSearchRepository productSearchRepository;

    @Autowired
    private CategorySearchRepository categorySearchRepository;

    @Autowired
    private StoreSearchRepository storeSearchRepository;

    @Autowired
    private ElasticsearchOperations elasticsearchOperations;

    @Value("${elasticsearch.enabled:true}")
    private boolean elasticsearchEnabled;

    /**
     * Check if Elasticsearch is available
     */
    public boolean isElasticsearchAvailable() {
        if (!elasticsearchEnabled) {
            return false;
        }
        try {
            elasticsearchOperations.indexOps(ProductDocument.class).exists();
            return true;
        } catch (Exception e) {
            logger.warn("Elasticsearch is not available: {}", e.getMessage());
            return false;
        }
    }

    /**
     * Full-text search for products with fuzzy matching
     */
    public List<ProductDocument> searchProducts(String query) {
        if (!isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, returning empty results");
            return new ArrayList<>();
        }

        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }

            // Use MultiMatchQuery with fuzzy matching for typo tolerance
            Query searchQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .multiMatch(m -> m
                                    .query(query)
                                    .fields("name^3", "description^1", "category^2")
                                    .fuzziness("AUTO")
                            )
                    )
                    .build();

            SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching products: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Advanced search with filters
     */
    public List<ProductDocument> advancedSearch(String query, Long categoryId, Double minPrice, Double maxPrice, Boolean inStockOnly) {
        if (!isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, returning empty results");
            return new ArrayList<>();
        }

        try {
            Query searchQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .bool(b -> {
                                // Text search
                                if (query != null && !query.trim().isEmpty()) {
                                    b.must(must -> must
                                            .multiMatch(m -> m
                                                    .query(query)
                                                    .fields("name^3", "description^1", "category^2")
                                                    .fuzziness("AUTO")
                                            )
                                    );
                                }

                                // Category filter
                                if (categoryId != null) {
                                    b.filter(filter -> filter
                                            .term(t -> t
                                                    .field("categoryId")
                                                    .value(categoryId)
                                            )
                                    );
                                }

                                // Price range filter
                                if (minPrice != null || maxPrice != null) {
                                    b.filter(filter -> filter
                                            .range(r -> {
                                                r.field("price");
                                                if (minPrice != null) {
                                                    r.gte(JsonData.of(minPrice));
                                                }
                                                if (maxPrice != null) {
                                                    r.lte(JsonData.of(maxPrice));
                                                }
                                                return r;
                                            })
                                    );
                                }

                                // Stock filter
                                if (inStockOnly != null && inStockOnly) {
                                    b.filter(filter -> filter
                                            .range(r -> r
                                                    .field("stock")
                                                    .gt(JsonData.of(0))
                                            )
                                    );
                                }

                                return b;
                            })
                    )
                    .build();

            SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in advanced search: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Autocomplete suggestions based on product names
     */
    public List<String> autocomplete(String prefix, int limit) {
        if (!isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, returning empty results");
            return new ArrayList<>();
        }

        try {
            if (prefix == null || prefix.trim().isEmpty()) {
                return new ArrayList<>();
            }

            Query searchQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .match(m -> m
                                    .field("name")
                                    .query(prefix)
                            )
                    )
                    .withMaxResults(limit)
                    .build();

            SearchHits<ProductDocument> searchHits = elasticsearchOperations.search(searchQuery, ProductDocument.class);
            return searchHits.stream()
                    .map(hit -> hit.getContent().getName())
                    .distinct()
                    .limit(limit)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error in autocomplete: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Search categories by name
     */
    public List<CategoryDocument> searchCategories(String query) {
        if (!isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, returning empty results");
            return new ArrayList<>();
        }

        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }

            Query searchQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .multiMatch(m -> m
                                    .query(query)
                                    .fields("name^2", "description^1")
                                    .fuzziness("AUTO")
                            )
                    )
                    .build();

            SearchHits<CategoryDocument> searchHits = elasticsearchOperations.search(searchQuery, CategoryDocument.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching categories: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }

    /**
     * Search stores by name or location
     */
    public List<StoreDocument> searchStores(String query) {
        if (!isElasticsearchAvailable()) {
            logger.warn("Elasticsearch not available, returning empty results");
            return new ArrayList<>();
        }

        try {
            if (query == null || query.trim().isEmpty()) {
                return new ArrayList<>();
            }

            Query searchQuery = NativeQuery.builder()
                    .withQuery(q -> q
                            .multiMatch(m -> m
                                    .query(query)
                                    .fields("name^2", "location^1")
                                    .fuzziness("AUTO")
                            )
                    )
                    .build();

            SearchHits<StoreDocument> searchHits = elasticsearchOperations.search(searchQuery, StoreDocument.class);
            return searchHits.stream()
                    .map(SearchHit::getContent)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            logger.error("Error searching stores: {}", e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
