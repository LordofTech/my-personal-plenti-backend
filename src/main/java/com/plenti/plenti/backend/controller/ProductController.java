package com.plenti.plenti.backend.controller;

import com.plenti.plenti.backend.dto.ProductDTO;
import com.plenti.plenti.backend.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @GetMapping
    @Operation(summary = "Get all products", description = "Retrieves a list of all products in the Plenti ecommerce app for browsing popular items, freebies, and discounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<ProductDTO>> getAll() {
        return ResponseEntity.ok(productService.findAll());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID", description = "Retrieves details of a specific product in the Plenti app, including description and price for product pages.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Product not found")
    })
    public ResponseEntity<ProductDTO> getById(
            @Parameter(description = "Product ID") @PathVariable String id) {
        return ResponseEntity.ok(productService.findById(id));
    }

    @GetMapping("/search")
    @Operation(summary = "Search products", description = "Searches for products by query in the Plenti app, supporting keyword searches like 'wine' for relevant results.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Search results retrieved"),
            @ApiResponse(responseCode = "400", description = "Invalid query")
    })
    public ResponseEntity<List<ProductDTO>> search(
            @Parameter(description = "Search query") @RequestParam String query) {
        return ResponseEntity.ok(productService.search(query));
    }

    @GetMapping("/categories")
    @Operation(summary = "Get list of categories", description = "Retrieves a list of product categories in the Plenti app, such as Food, Fashion, for category browsing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Categories retrieved successfully"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(productService.findAllCategories());
    }

    @GetMapping("/category")
    @Operation(summary = "Get products by category", description = "Retrieves products in a specific category in the Plenti app for detailed browsing.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Products in category retrieved"),
            @ApiResponse(responseCode = "404", description = "Category not found")
    })
    public ResponseEntity<List<ProductDTO>> byCategory(
            @Parameter(description = "Category name") @RequestParam String category) {
        return ResponseEntity.ok(productService.findByCategory(category));
    }
}