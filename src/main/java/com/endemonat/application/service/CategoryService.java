package com.endemonat.application.service;

import com.endemonat.application.entity.Category;
import com.endemonat.application.repository.CategoryRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Category entity.
 * Contains business logic for category management.
 * 
 * @author Emilio und Leander
 */
@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryService(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    /**
     * Create a new category
     */
    public Category createCategory(Category category) {
        // Check if category name already exists
        if (categoryRepository.existsByNameIgnoreCase(category.getName())) {
            throw new IllegalArgumentException("Category with name '" + category.getName() + "' already exists");
        }
        
        category.setCreatedAt(LocalDateTime.now());
        category.setUpdatedAt(LocalDateTime.now());
        return categoryRepository.save(category);
    }

    /**
     * Get all categories
     */
    public List<Category> getAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    /**
     * Get all active categories
     */
    public List<Category> getActiveCategories() {
        return categoryRepository.findByIsActiveTrueOrderByNameAsc();
    }

    /**
     * Get category by ID
     */
    public Optional<Category> getCategoryById(String id) {
        return categoryRepository.findById(id);
    }

    /**
     * Get category by name (case-insensitive)
     */
    public Optional<Category> getCategoryByName(String name) {
        return categoryRepository.findByNameIgnoreCase(name);
    }

    /**
     * Update existing category
     */
    public Optional<Category> updateCategory(String id, Category updatedCategory) {
        return categoryRepository.findById(id)
                .map(category -> {
                    // Check if new name already exists (excluding current category)
                    Optional<Category> existingCategory = categoryRepository.findByNameIgnoreCase(updatedCategory.getName());
                    if (existingCategory.isPresent() && !existingCategory.get().getId().equals(id)) {
                        throw new IllegalArgumentException("Category with name '" + updatedCategory.getName() + "' already exists");
                    }
                    
                    category.setName(updatedCategory.getName());
                    category.setDescription(updatedCategory.getDescription());
                    category.setColor(updatedCategory.getColor());
                    category.setIcon(updatedCategory.getIcon());
                    category.setActive(updatedCategory.isActive());
                    category.setUpdatedAt(LocalDateTime.now());
                    return categoryRepository.save(category);
                });
    }

    /**
     * Delete category by ID
     */
    public boolean deleteCategory(String id) {
        if (categoryRepository.existsById(id)) {
            categoryRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Soft delete category (set as inactive)
     */
    public Optional<Category> deactivateCategory(String id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setActive(false);
                    category.setUpdatedAt(LocalDateTime.now());
                    return categoryRepository.save(category);
                });
    }

    /**
     * Reactivate category
     */
    public Optional<Category> activateCategory(String id) {
        return categoryRepository.findById(id)
                .map(category -> {
                    category.setActive(true);
                    category.setUpdatedAt(LocalDateTime.now());
                    return categoryRepository.save(category);
                });
    }

    /**
     * Search categories by name
     */
    public List<Category> searchCategoriesByName(String keyword) {
        return categoryRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Search categories by description
     */
    public List<Category> searchCategoriesByDescription(String keyword) {
        return categoryRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    /**
     * Check if category name exists
     */
    public boolean categoryNameExists(String name) {
        return categoryRepository.existsByNameIgnoreCase(name);
    }

    /**
     * Get category statistics
     */
    public CategoryStatistics getCategoryStatistics() {
        long totalCategories = categoryRepository.count();
        long activeCategories = categoryRepository.countByIsActiveTrue();
        long inactiveCategories = categoryRepository.countByIsActiveFalse();
        
        return new CategoryStatistics(totalCategories, activeCategories, inactiveCategories);
    }

    /**
     * Inner class for category statistics
     */
    public static class CategoryStatistics {
        private final long totalCategories;
        private final long activeCategories;
        private final long inactiveCategories;

        public CategoryStatistics(long totalCategories, long activeCategories, long inactiveCategories) {
            this.totalCategories = totalCategories;
            this.activeCategories = activeCategories;
            this.inactiveCategories = inactiveCategories;
        }

        public long getTotalCategories() { return totalCategories; }
        public long getActiveCategories() { return activeCategories; }
        public long getInactiveCategories() { return inactiveCategories; }
    }
}