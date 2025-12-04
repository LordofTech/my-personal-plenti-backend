package com.plenti.plentibackend.controller;

import com.plenti.plentibackend.dto.CategoryDTO;
import com.plenti.plentibackend.dto.ResponseDTO;
import com.plenti.plentibackend.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller for category operations
 */
@RestController
@RequestMapping("/api/categories")
@Tag(name = "Categories", description = "Category management endpoints")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping
    @Operation(summary = "Get all categories", description = "Get list of all categories")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> getAllCategories() {
        List<CategoryDTO> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ResponseDTO.success(categories));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get category by ID", description = "Get category with subcategories")
    public ResponseEntity<ResponseDTO<CategoryDTO>> getCategoryById(@PathVariable Long id) {
        CategoryDTO category = categoryService.getCategoryById(id);
        return ResponseEntity.ok(ResponseDTO.success(category));
    }

    @GetMapping("/root")
    @Operation(summary = "Get root categories", description = "Get top-level categories")
    public ResponseEntity<ResponseDTO<List<CategoryDTO>>> getRootCategories() {
        List<CategoryDTO> categories = categoryService.getRootCategories();
        return ResponseEntity.ok(ResponseDTO.success(categories));
    }

    @PostMapping
    @Operation(summary = "Create category", description = "Create a new category (admin only)")
    public ResponseEntity<ResponseDTO<CategoryDTO>> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO created = categoryService.createCategory(categoryDTO);
        return ResponseEntity.ok(ResponseDTO.success("Category created successfully", created));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update category", description = "Update category details (admin only)")
    public ResponseEntity<ResponseDTO<CategoryDTO>> updateCategory(
            @PathVariable Long id,
            @Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO updated = categoryService.updateCategory(id, categoryDTO);
        return ResponseEntity.ok(ResponseDTO.success("Category updated successfully", updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete category", description = "Delete a category (admin only)")
    public ResponseEntity<ResponseDTO<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ResponseDTO.success("Category deleted successfully", "deleted"));
    }
}
