package com.plenti.plentibackend.repository;

import com.plenti.plentibackend.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repository for Product entity
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    
    List<Product> findByNameContainingIgnoreCase(String name);
    
    List<Product> findByCategoryId(Long categoryId);
    
    List<Product> findByCategory(String category);
    
    List<Product> findByStockLessThan(Integer threshold);
    
    List<Product> findByIsClearanceTrueAndStockGreaterThan(Integer stock);
    
    List<Product> findByIsFreebieTrue();
    
    List<Product> findByIsFeaturedTrue();
    
    List<Product> findByFlashSaleEndAfter(java.time.LocalDateTime now);
}
