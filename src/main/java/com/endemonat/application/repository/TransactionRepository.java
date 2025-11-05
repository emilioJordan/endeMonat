package com.endemonat.application.repository;

import com.endemonat.application.entity.Transaction;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Transaction entity.
 * Provides data access methods for financial transactions.
 * 
 * @author Emilio und Leander
 */
@Repository
public interface TransactionRepository extends MongoRepository<Transaction, String> {

    /**
     * Find transactions by category ID
     */
    List<Transaction> findByCategoryId(String categoryId);

    /**
     * Find transactions by type
     */
    List<Transaction> findByType(Transaction.TransactionType type);

    /**
     * Find transactions by category and type
     */
    List<Transaction> findByCategoryIdAndType(String categoryId, Transaction.TransactionType type);

    /**
     * Find transactions within a date range
     */
    List<Transaction> findByDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find transactions by category within a date range
     */
    List<Transaction> findByCategoryIdAndDateBetween(String categoryId, LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find transactions with amount greater than specified value
     */
    List<Transaction> findByAmountGreaterThan(BigDecimal amount);

    /**
     * Find transactions with amount less than specified value
     */
    List<Transaction> findByAmountLessThan(BigDecimal amount);

    /**
     * Find transactions with amount between specified range
     */
    List<Transaction> findByAmountBetween(BigDecimal minAmount, BigDecimal maxAmount);

    /**
     * Find transactions by description containing keyword (case-insensitive)
     */
    List<Transaction> findByDescriptionContainingIgnoreCase(String keyword);

    /**
     * Calculate total amount for a specific category and date range
     */
    @Query("{ 'categoryId': ?0, 'date': { $gte: ?1, $lte: ?2 }, 'type': ?3 }")
    List<Transaction> findByCategoryDateRangeAndType(String categoryId, LocalDateTime startDate, LocalDateTime endDate, Transaction.TransactionType type);

    /**
     * Get transactions ordered by date descending
     */
    List<Transaction> findAllByOrderByDateDesc();

    /**
     * Get transactions by category ordered by date descending
     */
    List<Transaction> findByCategoryIdOrderByDateDesc(String categoryId);

    /**
     * Count transactions by type
     */
    long countByType(Transaction.TransactionType type);

    /**
     * Count transactions by category
     */
    long countByCategoryId(String categoryId);
}