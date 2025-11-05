package com.endemonat.application.repository;

import com.endemonat.application.entity.Budget;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Budget entity.
 * Provides data access methods for budget management.
 * 
 * @author Emilio und Leander
 */
@Repository
public interface BudgetRepository extends MongoRepository<Budget, String> {

    /**
     * Find budgets by category ID
     */
    List<Budget> findByCategoryId(String categoryId);

    /**
     * Find all active budgets
     */
    List<Budget> findByIsActiveTrue();

    /**
     * Find all inactive budgets
     */
    List<Budget> findByIsActiveFalse();

    /**
     * Find budgets by period
     */
    List<Budget> findByPeriod(Budget.BudgetPeriod period);

    /**
     * Find active budgets by period
     */
    List<Budget> findByIsActiveTrueAndPeriod(Budget.BudgetPeriod period);

    /**
     * Find budgets that are currently active (within date range)
     */
    @Query("{ 'isActive': true, 'startDate': { $lte: ?0 }, 'endDate': { $gte: ?0 } }")
    List<Budget> findActiveBudgetsForDate(LocalDate date);

    /**
     * Find budgets for a specific category that are currently active
     */
    @Query("{ 'categoryId': ?0, 'isActive': true, 'startDate': { $lte: ?1 }, 'endDate': { $gte: ?1 } }")
    List<Budget> findActiveBudgetsForCategoryAndDate(String categoryId, LocalDate date);

    /**
     * Find budgets that have exceeded their budget amount
     */
    @Query("{ 'isActive': true, $expr: { $gt: ['$spentAmount', '$amount'] } }")
    List<Budget> findOverBudgets();

    /**
     * Find budgets that have reached their alert threshold
     */
    @Query("{ 'isActive': true, $expr: { $gte: [{ $divide: ['$spentAmount', '$amount'] }, '$alertThreshold'] } }")
    List<Budget> findBudgetsReachingAlertThreshold();

    /**
     * Find budgets with amount greater than specified value
     */
    List<Budget> findByAmountGreaterThan(BigDecimal amount);

    /**
     * Find budgets with amount less than specified value
     */
    List<Budget> findByAmountLessThan(BigDecimal amount);

    /**
     * Find budgets within a date range
     */
    List<Budget> findByStartDateLessThanEqualAndEndDateGreaterThanEqual(LocalDate endDate, LocalDate startDate);

    /**
     * Find budgets starting after a specific date
     */
    List<Budget> findByStartDateAfter(LocalDate date);

    /**
     * Find budgets ending before a specific date
     */
    List<Budget> findByEndDateBefore(LocalDate date);

    /**
     * Find budgets by name containing keyword (case-insensitive)
     */
    List<Budget> findByNameContainingIgnoreCase(String keyword);

    /**
     * Get budgets ordered by start date descending
     */
    List<Budget> findAllByOrderByStartDateDesc();

    /**
     * Get active budgets ordered by start date descending
     */
    List<Budget> findByIsActiveTrueOrderByStartDateDesc();

    /**
     * Count active budgets
     */
    long countByIsActiveTrue();

    /**
     * Count budgets by category
     */
    long countByCategoryId(String categoryId);

    /**
     * Count budgets by period
     */
    long countByPeriod(Budget.BudgetPeriod period);
}