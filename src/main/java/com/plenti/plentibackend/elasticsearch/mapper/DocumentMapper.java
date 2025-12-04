package com.plenti.plentibackend.elasticsearch.mapper;

import com.plenti.plentibackend.elasticsearch.document.CategoryDocument;
import com.plenti.plentibackend.elasticsearch.document.ProductDocument;
import com.plenti.plentibackend.elasticsearch.document.StoreDocument;
import com.plenti.plentibackend.entity.Category;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.entity.Store;
import org.springframework.stereotype.Component;

/**
 * Mapper to convert JPA entities to Elasticsearch documents
 */
@Component
public class DocumentMapper {

    /**
     * Convert Product entity to ProductDocument
     */
    public ProductDocument toProductDocument(Product product) {
        if (product == null) {
            return null;
        }
        
        ProductDocument document = new ProductDocument();
        document.setId(product.getId());
        document.setName(product.getName());
        document.setDescription(product.getDescription());
        document.setPrice(product.getPrice());
        document.setCategory(product.getCategory());
        document.setCategoryId(product.getCategoryId());
        document.setStock(product.getStock());
        document.setImageUrl(product.getImageUrl());
        document.setAverageRating(product.getAverageRating());
        document.setReviewCount(product.getReviewCount());
        document.setIsClearance(product.getIsClearance());
        document.setIsFreebie(product.getIsFreebie());
        document.setIsFeatured(product.getIsFeatured());
        document.setLastUpdated(product.getLastUpdated());
        
        return document;
    }

    /**
     * Convert Category entity to CategoryDocument
     */
    public CategoryDocument toCategoryDocument(Category category) {
        if (category == null) {
            return null;
        }
        
        CategoryDocument document = new CategoryDocument();
        document.setId(category.getId());
        document.setName(category.getName());
        document.setDescription(category.getDescription());
        document.setImageUrl(category.getImageUrl());
        document.setParentId(category.getParentId());
        
        return document;
    }

    /**
     * Convert Store entity to StoreDocument
     */
    public StoreDocument toStoreDocument(Store store) {
        if (store == null) {
            return null;
        }
        
        StoreDocument document = new StoreDocument();
        document.setId(store.getId());
        document.setName(store.getName());
        document.setLocation(store.getLocation());
        document.setType(store.getType());
        document.setLatitude(store.getLatitude());
        document.setLongitude(store.getLongitude());
        
        return document;
    }
}
