package com.plenti.plentibackend.elasticsearch.repository;

import com.plenti.plentibackend.elasticsearch.document.ProductDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for Product search operations
 */
@Repository
public interface ProductSearchRepository extends ElasticsearchRepository<ProductDocument, Long> {
    
    List<ProductDocument> findByNameContainingIgnoreCase(String name);
    
    List<ProductDocument> findByCategoryId(Long categoryId);
}
