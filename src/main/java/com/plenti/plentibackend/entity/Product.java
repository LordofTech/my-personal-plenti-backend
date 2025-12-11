package com.plenti.plentibackend.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Entity representing a product in the Plenti catalog
 */
@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1000)
    private String description;

    @Column(nullable = false)
    private Double price;

    private String category;

    @Column(nullable = false)
    private Integer stock = 0;

    private String imageUrl;

    private LocalDateTime lastUpdated = LocalDateTime.now();

    private Long categoryId;

    private Double averageRating = 0.0;

    private Integer reviewCount = 0;

    private Double bulkPrice;

    @Column(nullable = false)
    private Boolean isClearance = false;

    @Column(nullable = false)
    private Boolean isFreebie = false;

    @Column(nullable = false)
    private Boolean isFeatured = false;

    private Double flashSalePrice;

    private LocalDateTime flashSaleEnd;

    @PrePersist
    @PreUpdate
    protected void onUpdate() {
        lastUpdated = LocalDateTime.now();
        if (stock == null) {
            stock = 0;
        }
        if (averageRating == null) {
            averageRating = 0.0;
        }
        if (reviewCount == null) {
            reviewCount = 0;
        }
        if (isClearance == null) {
            isClearance = false;
        }
        if (isFreebie == null) {
            isFreebie = false;
        }
        if (isFeatured == null) {
            isFeatured = false;
        }
    }

    // Explicit getters and setters for build compatibility
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }
    
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    
    public Integer getStock() { return stock; }
    public void setStock(Integer stock) { this.stock = stock; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public LocalDateTime getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDateTime lastUpdated) { this.lastUpdated = lastUpdated; }
    
    public Long getCategoryId() { return categoryId; }
    public void setCategoryId(Long categoryId) { this.categoryId = categoryId; }
    
    public Double getAverageRating() { return averageRating; }
    public void setAverageRating(Double averageRating) { this.averageRating = averageRating; }
    
    public Integer getReviewCount() { return reviewCount; }
    public void setReviewCount(Integer reviewCount) { this.reviewCount = reviewCount; }
    
    public Double getBulkPrice() { return bulkPrice; }
    public void setBulkPrice(Double bulkPrice) { this.bulkPrice = bulkPrice; }
    
    public Boolean getIsClearance() { return isClearance; }
    public void setIsClearance(Boolean isClearance) { this.isClearance = isClearance; }
    
    public Boolean getIsFreebie() { return isFreebie; }
    public void setIsFreebie(Boolean isFreebie) { this.isFreebie = isFreebie; }
    
    public Boolean getIsFeatured() { return isFeatured; }
    public void setIsFeatured(Boolean isFeatured) { this.isFeatured = isFeatured; }
    
    public Double getFlashSalePrice() { return flashSalePrice; }
    public void setFlashSalePrice(Double flashSalePrice) { this.flashSalePrice = flashSalePrice; }
    
    public LocalDateTime getFlashSaleEnd() { return flashSaleEnd; }
    public void setFlashSaleEnd(LocalDateTime flashSaleEnd) { this.flashSaleEnd = flashSaleEnd; }
}
