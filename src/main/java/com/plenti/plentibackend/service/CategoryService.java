package com.plenti.plentibackend.service;

import com.plenti.plentibackend.dto.CategoryDTO;
import com.plenti.plentibackend.entity.Category;
import com.plenti.plentibackend.exception.PlentiException;
import com.plenti.plentibackend.repository.CategoryRepository;
import com.plenti.plentibackend.util.Mapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service for category management operations
 */
@Service
public class CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private Mapper mapper;

    public List<CategoryDTO> getAllCategories() {
        return categoryRepository.findAll().stream()
                .map(mapper::toCategoryDTO)
                .toList();
    }

    public List<CategoryDTO> getRootCategories() {
        return categoryRepository.findByParentIdIsNull().stream()
                .map(mapper::toCategoryDTO)
                .toList();
    }

    public CategoryDTO getCategoryById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Category not found"));
        
        // Load subcategories manually
        List<Category> subcategories = categoryRepository.findByParentId(id);
        category.setSubcategories(subcategories);
        
        return mapper.toCategoryDTO(category);
    }

    public List<CategoryDTO> getSubcategories(Long parentId) {
        return categoryRepository.findByParentId(parentId).stream()
                .map(mapper::toCategoryDTO)
                .toList();
    }

    @Transactional
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        if (categoryRepository.findByName(categoryDTO.getName()).isPresent()) {
            throw new PlentiException("Category with this name already exists");
        }
        Category category = mapper.toCategoryEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return mapper.toCategoryDTO(savedCategory);
    }

    @Transactional
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new PlentiException("Category not found"));

        if (categoryDTO.getName() != null) category.setName(categoryDTO.getName());
        if (categoryDTO.getDescription() != null) category.setDescription(categoryDTO.getDescription());
        if (categoryDTO.getImageUrl() != null) category.setImageUrl(categoryDTO.getImageUrl());

        Category updatedCategory = categoryRepository.save(category);
        return mapper.toCategoryDTO(updatedCategory);
    }

    @Transactional
    public void deleteCategory(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new PlentiException("Category not found");
        }
        categoryRepository.deleteById(id);
    }
}
