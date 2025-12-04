package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.*;
import com.plenti.plentibackend.service.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * Controller for admin operations
 */
@RestController
@RequestMapping("/api/admin")
@Tag(name = "Admin", description = "Admin dashboard and management endpoints")
public class AdminController {

    @Autowired
    private UserService userService;

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private AnalyticsService analyticsService;

    @GetMapping("/users")
    @Operation(summary = "List all users", description = "Get list of all users (admin only)")
    public ResponseEntity<ResponseDTO<List<UserDTO>>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(ResponseDTO.success(users));
    }

    @PostMapping("/products")
    @Operation(summary = "Create product", description = "Add a new product (admin only)")
    public ResponseEntity<ResponseDTO<ProductDTO>> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO created = productService.createProduct(productDTO);
        return ResponseEntity.ok(ResponseDTO.success("Product created successfully", created));
    }

    @PutMapping("/products/{id}")
    @Operation(summary = "Update product", description = "Update product details (admin only)")
    public ResponseEntity<ResponseDTO<ProductDTO>> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductDTO productDTO) {
        ProductDTO updated = productService.updateProduct(id, productDTO);
        return ResponseEntity.ok(ResponseDTO.success("Product updated successfully", updated));
    }

    @DeleteMapping("/products/{id}")
    @Operation(summary = "Delete product", description = "Delete a product (admin only)")
    public ResponseEntity<ResponseDTO<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ResponseDTO.success("Product deleted successfully", "deleted"));
    }

    @PutMapping("/products/{id}/restock")
    @Operation(summary = "Restock product", description = "Add stock to a product (admin only)")
    public ResponseEntity<ResponseDTO<String>> restockProduct(
            @PathVariable Long id,
            @RequestBody Map<String, Integer> request) {
        Integer quantity = request.get("quantity");
        productService.restockProduct(id, quantity);
        return ResponseEntity.ok(ResponseDTO.success("Product restocked successfully", quantity + " units added"));
    }

    @GetMapping("/products/low-stock")
    @Operation(summary = "Get low stock products", description = "Get products with low stock (admin only)")
    public ResponseEntity<ResponseDTO<List<ProductDTO>>> getLowStockProducts(@RequestParam Integer threshold) {
        List<ProductDTO> products = productService.getLowStockProducts(threshold);
        return ResponseEntity.ok(ResponseDTO.success(products));
    }

    @GetMapping("/categories")
    @Operation(summary = "List categories", description = "Get all categories (admin only)")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseDTO.success(categories));
    }

    @GetMapping("/orders")
    @Operation(summary = "List all orders", description = "Get all orders (admin only)")
    public ResponseEntity<ResponseDTO<List<OrderDTO>>> getAllOrders() {
        List<OrderDTO> orders = orderService.getAllOrders();
        return ResponseEntity.ok(ResponseDTO.success(orders));
    }

    @GetMapping("/analytics/summary")
    @Operation(summary = "Get analytics summary", description = "Get dashboard analytics (admin only)")
    public ResponseEntity<ResponseDTO<AnalyticsDTO>> getAnalyticsSummary() {
        AnalyticsDTO analytics = analyticsService.getAnalyticsSummary();
        return ResponseEntity.ok(ResponseDTO.success(analytics));
    }
}
