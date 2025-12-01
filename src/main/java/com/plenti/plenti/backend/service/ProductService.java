package com.plenti.plenti.backend.service;

import com.plenti.plenti.backend.dto.ProductDTO;
import com.plenti.plenti.backend.entity.Product;
import com.plenti.plenti.backend.repository.ProductRepository;
import com.plenti.plenti.backend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
public class ProductService {

    @Autowired
    private ProductRepository productRepository;

    public List<ProductDTO> findAll() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .map(Mapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public ProductDTO findById(String id) {
        Product product = productRepository.findById(id).orElseThrow(() -> new RuntimeException("Product not found"));
        return Mapper.toProductDTO(product);
    }

    public List<ProductDTO> search(String query) {
        return StreamSupport.stream(productRepository.findByNameContaining(query).spliterator(), false)
                .map(Mapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public List<String> findAllCategories() {
        return StreamSupport.stream(productRepository.findAll().spliterator(), false)
                .map(Product::getCategory)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<ProductDTO> findByCategory(String category) {
        return StreamSupport.stream(productRepository.findByCategory(category).spliterator(), false)
                .map(Mapper::toProductDTO)
                .collect(Collectors.toList());
    }

    public void checkStock(String productId, int quantity) {
        Product product = productRepository.findById(productId).orElseThrow();
        if (product.getStock() < quantity) {
            throw new RuntimeException("Stockout for " + product.getName());
        }
    }
}