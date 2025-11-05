package com.endemonat.application.controller;

import com.endemonat.application.entity.Category;
import com.endemonat.application.service.CategoryService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Category management.
 * Provides endpoints for CRUD operations and category queries.
 * 
 * @author Emilio und Leander
 */
@RestController
@RequestMapping("/api/categories")
@CrossOrigin(origins = "*") // Configure according to your frontend needs
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    /**
     * Create a new category
     */
    @PostMapping
    public ResponseEntity<Category> createCategory(@Valid @RequestBody Category category) {
        try {
            Category createdCategory = categoryService.createCategory(category);
            return new ResponseEntity<>(createdCategory, HttpStatus.CREATED);
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all categories
     */
    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get all active categories
     */
    @GetMapping("/active")
    public ResponseEntity<List<Category>> getActiveCategories() {
        List<Category> categories = categoryService.getActiveCategories();
        return ResponseEntity.ok(categories);
    }

    /**
     * Get category by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable String id) {
        return categoryService.getCategoryById(id)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get category by name
     */
    @GetMapping("/name/{name}")
    public ResponseEntity<Category> getCategoryByName(@PathVariable String name) {
        return categoryService.getCategoryByName(name)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update category by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable String id, 
                                                 @Valid @RequestBody Category category) {
        try {
            return categoryService.updateCategory(id, category)
                    .map(updatedCategory -> ResponseEntity.ok(updatedCategory))
                    .orElse(ResponseEntity.notFound().build());
        } catch (IllegalArgumentException e) {
            return new ResponseEntity<>(HttpStatus.CONFLICT);
        }
    }

    /**
     * Delete category by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
        if (categoryService.deleteCategory(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Deactivate category (soft delete)
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Category> deactivateCategory(@PathVariable String id) {
        return categoryService.deactivateCategory(id)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Activate category
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Category> activateCategory(@PathVariable String id) {
        return categoryService.activateCategory(id)
                .map(category -> ResponseEntity.ok(category))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Search categories by name
     */
    @GetMapping("/search/name")
    public ResponseEntity<List<Category>> searchCategoriesByName(@RequestParam String keyword) {
        List<Category> categories = categoryService.searchCategoriesByName(keyword);
        return ResponseEntity.ok(categories);
    }

    /**
     * Search categories by description
     */
    @GetMapping("/search/description")
    public ResponseEntity<List<Category>> searchCategoriesByDescription(@RequestParam String keyword) {
        List<Category> categories = categoryService.searchCategoriesByDescription(keyword);
        return ResponseEntity.ok(categories);
    }

    /**
     * Check if category name exists
     */
    @GetMapping("/exists/{name}")
    public ResponseEntity<Boolean> categoryNameExists(@PathVariable String name) {
        boolean exists = categoryService.categoryNameExists(name);
        return ResponseEntity.ok(exists);
    }

    /**
     * Get category statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<CategoryService.CategoryStatistics> getCategoryStatistics() {
        CategoryService.CategoryStatistics statistics = categoryService.getCategoryStatistics();
        return ResponseEntity.ok(statistics);
    }
}