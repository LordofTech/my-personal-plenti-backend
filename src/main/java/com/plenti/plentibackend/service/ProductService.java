package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.ProductDTO;
import com.plenti.plentibackend.entity.Product;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.ProductRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for product management operations
 */
@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private Mapper mapper;

    public List<ProductDTO> getAllProducts() {
        return productRepository.findAll().stream()
                .map(mapper::toProductDTO)
                .toList();
    }

    public ProductDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Product not found"));
        return mapper.toProductDTO(product);
    }

    public List<ProductDTO> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query).stream()
                .map(mapper::toProductDTO)
                .toList();
    }

    public List<ProductDTO> getProductsByCategory(String category) {
        return productRepository.findByCategory(category).stream()
                .map(mapper::toProductDTO)
                .toList();
    }

    public List<ProductDTO> getProductsByCategoryId(Long categoryId) {
        return productRepository.findByCategoryId(categoryId).stream()
                .map(mapper::toProductDTO)
                .toList();
    }

    @Transactional
    public ProductDTO createProduct(ProductDTO productDTO) {
        Product product = mapper.toProductEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return mapper.toProductDTO(savedProduct);
    }

    @Transactional
    public ProductDTO updateProduct(Long id, ProductDTO productDTO) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Product not found"));

        if (productDTO.getName() != null) product.setName(productDTO.getName());
        if (productDTO.getDescription() != null) product.setDescription(productDTO.getDescription());
        if (productDTO.getPrice() != null) product.setPrice(productDTO.getPrice());
        if (productDTO.getCategory() != null) product.setCategory(productDTO.getCategory());
        if (productDTO.getStock() != null) product.setStock(productDTO.getStock());
        if (productDTO.getImageUrl() != null) product.setImageUrl(productDTO.getImageUrl());
        if (productDTO.getCategoryId() != null) product.setCategoryId(productDTO.getCategoryId());

        Product updatedProduct = productRepository.save(product);
        return mapper.toProductDTO(updatedProduct);
    }

    @Transactional
    public void deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new PlentiException("Product not found");
        }
        productRepository.deleteById(id);
    }

    @Transactional
    public void restockProduct(Long id, Integer quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Product not found"));
        product.setStock(product.getStock() + quantity);
        productRepository.save(product);
    }

    public List<ProductDTO> getLowStockProducts(Integer threshold) {
        return productRepository.findByStockLessThan(threshold).stream()
                .map(mapper::toProductDTO)
                .toList();
    }

    @Transactional
    public void updateProductRating(Long productId, Double averageRating, Integer reviewCount) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new PlentiException("Product not found"));
        product.setAverageRating(averageRating);
        product.setReviewCount(reviewCount);
        productRepository.save(product);
    }
}
