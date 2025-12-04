package com.plenti.plentibackend.elasticsearch.repository;

import com.plenti.plentibackend.elasticsearch.document.CategoryDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for Category search operations
 */
@Repository
public interface CategorySearchRepository extends ElasticsearchRepository<CategoryDocument, Long> {
    
    List<CategoryDocument> findByNameContainingIgnoreCase(String name);
}
