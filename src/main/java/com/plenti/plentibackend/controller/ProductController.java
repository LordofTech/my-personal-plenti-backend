package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.ProductDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for product operations
 */
@RestController
@RequestMapping("/api/products")
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Get list of all products")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getAllProducts() {
        List<ProductDTO> products = productService.getAllProducts();
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Get product details by ID")
    public ResponseEntity<ResponseDTO<ProductDTO>> getProductById(@PathVariable Long id) {
        ProductDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ResponseDTO.success(product));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Search products by name")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> searchProducts(@RequestParam String query) {
        List<ProductDTO> products = productService.searchProducts(query);
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/category")
    @Operation(summary = "Get products by category", description = "Get products filtered by category")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getProductsByCategory(@RequestParam String category) {
        List<ProductDTO> products = productService.getProductsByCategory(category);
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(summary = "Get products by category ID", description = "Get products filtered by category ID")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getProductsByCategoryId(@PathVariable Long categoryId) {
        List<ProductDTO> products = productService.getProductsByCategoryId(categoryId);
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/clearance")
    @Operation(summary = "Get clearance products", description = "Get products on clearance sale")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getClearanceProducts() {
        List<ProductDTO> products = productService.getClearanceProducts();
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/freebies")
    @Operation(summary = "Get freebie products", description = "Get products marked as freebies")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getFreebieProducts() {
        List<ProductDTO> products = productService.getFreebieProducts();
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/featured")
    @Operation(summary = "Get featured products", description = "Get featured products")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getFeaturedProducts() {
        List<ProductDTO> products = productService.getFeaturedProducts();
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/flash-sales")
    @Operation(summary = "Get flash sale products", description = "Get products with active flash sales")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getFlashSaleProducts() {
        List<ProductDTO> products = productService.getFlashSaleProducts();
        return ResponseEntity.ok(ResponseDTO.success(products));
    }
}
