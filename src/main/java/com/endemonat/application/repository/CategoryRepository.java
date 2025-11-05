package com.endemonat.application.repository;

import com.endemonat.application.entity.Category;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Category entity.
 * Provides data access methods for expense categories.
 * 
 * @author Emilio und Leander
 */
@Repository
public interface CategoryRepository extends MongoRepository<Category, String> {

    /**
     * Find category by name (case-insensitive)
     */
    Optional<Category> findByNameIgnoreCase(String name);

    /**
     * Find all active categories
     */
    List<Category> findByIsActiveTrue();

    /**
     * Find all inactive categories
     */
    List<Category> findByIsActiveFalse();

    /**
     * Find categories by name containing keyword (case-insensitive)
     */
    List<Category> findByNameContainingIgnoreCase(String keyword);

    /**
     * Find categories by description containing keyword (case-insensitive)
     */
    List<Category> findByDescriptionContainingIgnoreCase(String keyword);

    /**
     * Check if category name exists (case-insensitive)
     */
    boolean existsByNameIgnoreCase(String name);

    /**
     * Find all categories ordered by name
     */
    List<Category> findAllByOrderByNameAsc();

    /**
     * Find active categories ordered by name
     */
    List<Category> findByIsActiveTrueOrderByNameAsc();

    /**
     * Count active categories
     */
    long countByIsActiveTrue();

    /**
     * Count inactive categories
     */
    long countByIsActiveFalse();
}