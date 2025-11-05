package com.endemonat.application.controller;

import com.endemonat.application.entity.Transaction;
import com.endemonat.application.service.TransactionService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Transaction management.
 * Provides endpoints for CRUD operations and transaction queries.
 * 
 * @author Emilio und Leander
 */
@RestController
@RequestMapping("/api/transactions")
@CrossOrigin(origins = "*") // Configure according to your frontend needs
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    /**
     * Create a new transaction with intelligent validation
     */
    @PostMapping("/smart")
    public ResponseEntity<TransactionService.TransactionCreationResult> createSmartTransaction(
            @Valid @RequestBody Transaction transaction) {
        try {
            TransactionService.TransactionCreationResult result = 
                transactionService.createTransactionWithValidation(transaction);
            return new ResponseEntity<>(result, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Create a new transaction
     */
    @PostMapping
    public ResponseEntity<Transaction> createTransaction(@Valid @RequestBody Transaction transaction) {
        try {
            Transaction createdTransaction = transactionService.createTransaction(transaction);
            return new ResponseEntity<>(createdTransaction, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all transactions
     */
    @GetMapping
    public ResponseEntity<List<Transaction>> getAllTransactions() {
        List<Transaction> transactions = transactionService.getAllTransactions();
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transaction by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionById(@PathVariable String id) {
        return transactionService.getTransactionById(id)
                .map(transaction -> ResponseEntity.ok(transaction))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update transaction by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Transaction> updateTransaction(@PathVariable String id, 
                                                       @Valid @RequestBody Transaction transaction) {
        return transactionService.updateTransaction(id, transaction)
                .map(updatedTransaction -> ResponseEntity.ok(updatedTransaction))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete transaction by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable String id) {
        if (transactionService.deleteTransaction(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get transactions by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Transaction>> getTransactionsByCategory(@PathVariable String categoryId) {
        List<Transaction> transactions = transactionService.getTransactionsByCategory(categoryId);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transactions by type
     */
    @GetMapping("/type/{type}")
    public ResponseEntity<List<Transaction>> getTransactionsByType(@PathVariable Transaction.TransactionType type) {
        List<Transaction> transactions = transactionService.getTransactionsByType(type);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transactions within date range
     */
    @GetMapping("/date-range")
    public ResponseEntity<List<Transaction>> getTransactionsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByDateRange(startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transactions by category and date range
     */
    @GetMapping("/category/{categoryId}/date-range")
    public ResponseEntity<List<Transaction>> getTransactionsByCategoryAndDateRange(
            @PathVariable String categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        List<Transaction> transactions = transactionService.getTransactionsByCategoryAndDateRange(categoryId, startDate, endDate);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Search transactions by description
     */
    @GetMapping("/search")
    public ResponseEntity<List<Transaction>> searchTransactions(@RequestParam String keyword) {
        List<Transaction> transactions = transactionService.searchTransactionsByDescription(keyword);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Get transactions by amount range
     */
    @GetMapping("/amount-range")
    public ResponseEntity<List<Transaction>> getTransactionsByAmountRange(
            @RequestParam BigDecimal minAmount,
            @RequestParam BigDecimal maxAmount) {
        List<Transaction> transactions = transactionService.getTransactionsByAmountRange(minAmount, maxAmount);
        return ResponseEntity.ok(transactions);
    }

    /**
     * Calculate total expenses by category
     */
    @GetMapping("/category/{categoryId}/total-expenses")
    public ResponseEntity<BigDecimal> getTotalExpensesByCategory(
            @PathVariable String categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal total = transactionService.calculateTotalExpensesByCategory(categoryId, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    /**
     * Calculate total income by category
     */
    @GetMapping("/category/{categoryId}/total-income")
    public ResponseEntity<BigDecimal> getTotalIncomeByCategory(
            @PathVariable String categoryId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal total = transactionService.calculateTotalIncomeByCategory(categoryId, startDate, endDate);
        return ResponseEntity.ok(total);
    }

    /**
     * Calculate total expenses
     */
    @GetMapping("/total-expenses")
    public ResponseEntity<BigDecimal> getTotalExpenses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal total = transactionService.calculateTotalExpenses(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    /**
     * Calculate total income
     */
    @GetMapping("/total-income")
    public ResponseEntity<BigDecimal> getTotalIncome(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        BigDecimal total = transactionService.calculateTotalIncome(startDate, endDate);
        return ResponseEntity.ok(total);
    }

    /**
     * Get transaction statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<TransactionService.TransactionStatistics> getTransactionStatistics() {
        TransactionService.TransactionStatistics statistics = transactionService.getTransactionStatistics();
        return ResponseEntity.ok(statistics);
    }
}