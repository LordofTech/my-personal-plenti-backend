package com.plenti.plentibackend.elasticsearch.repository;

import com.plenti.plentibackend.elasticsearch.document.StoreDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Elasticsearch repository for Store search operations
 */
@Repository
public interface StoreSearchRepository extends ElasticsearchRepository<StoreDocument, Long> {
    
    List<StoreDocument> findByNameContainingIgnoreCase(String name);
}
