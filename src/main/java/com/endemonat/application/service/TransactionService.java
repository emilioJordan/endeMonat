package com.endemonat.application.service;

import com.endemonat.application.entity.Transaction;
import com.endemonat.application.entity.Budget;
import com.endemonat.application.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Transaction entity.
 * Contains business logic for transaction management.
 * 
 * @author Emilio und Leander
 */
@Service
public class TransactionService {

    private final TransactionRepository transactionRepository;
    private final BudgetService budgetService;

    public TransactionService(TransactionRepository transactionRepository, BudgetService budgetService) {
        this.transactionRepository = transactionRepository;
        this.budgetService = budgetService;
    }

    /**
     * Create a new transaction with intelligent budget validation
     */
    public TransactionCreationResult createTransactionWithValidation(Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        
        // Complex validation logic for expenses
        if (transaction.getType() == Transaction.TransactionType.EXPENSE) {
            return validateAndCreateExpense(transaction);
        } else {
            // Income transactions are always welcome!
            Transaction savedTransaction = transactionRepository.save(transaction);
            return new TransactionCreationResult(
                savedTransaction, 
                TransactionStatus.SUCCESS,
                "💰 Einnahme erfolgreich hinzugefügt! Das hilft dem Budget!"
            );
        }
    }

    /**
     * Complex expense validation with multiple conditions
     */
    private TransactionCreationResult validateAndCreateExpense(Transaction transaction) {
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        long daysRemaining = java.time.temporal.ChronoUnit.DAYS.between(today, endOfMonth);
        
        // Calculate current month spending for category
        BigDecimal monthlySpending = calculateTotalExpensesByCategory(
            transaction.getCategoryId(),
            today.withDayOfMonth(1).atStartOfDay(),
            LocalDateTime.now()
        );
        
        // Get active budgets for this category
        List<Budget> activeBudgets = budgetService.getCurrentActiveBudgetsForCategory(transaction.getCategoryId());
        
        String warningMessage = "";
        TransactionStatus status = TransactionStatus.SUCCESS;
        
        // Complex budget checking logic
        if (!activeBudgets.isEmpty()) {
            for (Budget budget : activeBudgets) {
                BigDecimal newTotal = monthlySpending.add(transaction.getAmount());
                BigDecimal remaining = budget.getAmount().subtract(newTotal);
                BigDecimal dailyAverage = monthlySpending.divide(
                    new BigDecimal(today.getDayOfMonth()), 2, RoundingMode.HALF_UP);
                BigDecimal projectedMonthEnd = dailyAverage.multiply(new BigDecimal(endOfMonth.getDayOfMonth()));
                
                // Multiple warning conditions
                if (newTotal.compareTo(budget.getAmount()) > 0) {
                    // Already over budget
                    status = TransactionStatus.OVER_BUDGET;
                    warningMessage = "🚨 ACHTUNG: Budget bereits um " + 
                        newTotal.subtract(budget.getAmount()) + " CHF überschritten!";
                } else if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                    // Will exceed budget with this transaction
                    status = TransactionStatus.BUDGET_EXCEEDED;
                    warningMessage = "⛔ STOPP: Diese Ausgabe würde das Budget um " + 
                        remaining.abs() + " CHF überschreiten!";
                } else if (daysRemaining > 0) {
                    BigDecimal dailyBudgetLeft = remaining.divide(new BigDecimal(daysRemaining), 2, RoundingMode.HALF_UP);
                    
                    if (transaction.getAmount().compareTo(dailyBudgetLeft.multiply(new BigDecimal("3"))) > 0) {
                        // Transaction is more than 3 days worth of remaining budget
                        status = TransactionStatus.MAJOR_EXPENSE;
                        warningMessage = "💸 WARNUNG: Das ist eine große Ausgabe! " +
                            "Danach nur noch " + dailyBudgetLeft + " CHF pro Tag übrig.";
                    } else if (remaining.divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
                               .compareTo(new BigDecimal("0.2")) < 0) {
                        // Less than 20% budget remaining
                        status = TransactionStatus.LOW_BUDGET;
                        warningMessage = "⚠️ Vorsicht: Nur noch " + remaining + " CHF im Budget übrig!";
                    } else if (projectedMonthEnd.add(transaction.getAmount()).compareTo(budget.getAmount()) > 0) {
                        // Projected to exceed budget by month end
                        status = TransactionStatus.PROJECTED_OVERSPEND;
                        warningMessage = "📊 Info: Basierend auf bisherigen Ausgaben könnte das Budget knapp werden.";
                    }
                }
                
                // Update budget spent amount
                budget.setSpentAmount(newTotal);
                budgetService.updateBudget(budget.getId(), budget);
            }
        } else {
            // No budget set - warn about uncontrolled spending
            status = TransactionStatus.NO_BUDGET;
            warningMessage = "❓ Keine Budget-Kontrolle für diese Kategorie. Vorsicht vor unkontrollierten Ausgaben!";
        }
        
        // Additional complex conditions based on amount
        if (transaction.getAmount().compareTo(new BigDecimal("100")) > 0) {
            if (status == TransactionStatus.SUCCESS) {
                status = TransactionStatus.LARGE_EXPENSE;
                warningMessage = "💳 Große Ausgabe registriert. Überprüfen Sie andere Kategorien!";
            }
        }
        
        // Weekend spending warning
        if (today.getDayOfWeek().getValue() >= 6) { // Saturday or Sunday
            if (transaction.getAmount().compareTo(new BigDecimal("50")) > 0) {
                warningMessage += " 🎪 Wochenend-Ausgabe: Besonders gut auf spontane Käufe aufpassen!";
            }
        }
        
        // End of month critical period
        if (daysRemaining <= 5 && daysRemaining > 0) {
            if (transaction.getAmount().compareTo(new BigDecimal("20")) > 0) {
                warningMessage += " 📅 Ende Monat Modus: Jeder Franken zählt jetzt!";
            }
        }
        
        Transaction savedTransaction = transactionRepository.save(transaction);
        
        if (warningMessage.isEmpty()) {
            warningMessage = "✅ Ausgabe im grünen Bereich. Weiter so!";
        }
        
        return new TransactionCreationResult(savedTransaction, status, warningMessage);
    }

    /**
     * Create a new transaction
     */
    public Transaction createTransaction(Transaction transaction) {
        if (transaction.getDate() == null) {
            transaction.setDate(LocalDateTime.now());
        }
        transaction.setCreatedAt(LocalDateTime.now());
        transaction.setUpdatedAt(LocalDateTime.now());
        return transactionRepository.save(transaction);
    }

    /**
     * Get all transactions
     */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAllByOrderByDateDesc();
    }

    /**
     * Get transaction by ID
     */
    public Optional<Transaction> getTransactionById(String id) {
        return transactionRepository.findById(id);
    }

    /**
     * Update existing transaction
     */
    public Optional<Transaction> updateTransaction(String id, Transaction updatedTransaction) {
        return transactionRepository.findById(id)
                .map(transaction -> {
                    transaction.setDescription(updatedTransaction.getDescription());
                    transaction.setAmount(updatedTransaction.getAmount());
                    transaction.setType(updatedTransaction.getType());
                    transaction.setCategoryId(updatedTransaction.getCategoryId());
                    if (updatedTransaction.getDate() != null) {
                        transaction.setDate(updatedTransaction.getDate());
                    }
                    transaction.setUpdatedAt(LocalDateTime.now());
                    return transactionRepository.save(transaction);
                });
    }

    /**
     * Delete transaction by ID
     */
    public boolean deleteTransaction(String id) {
        if (transactionRepository.existsById(id)) {
            transactionRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get transactions by category
     */
    public List<Transaction> getTransactionsByCategory(String categoryId) {
        return transactionRepository.findByCategoryIdOrderByDateDesc(categoryId);
    }

    /**
     * Get transactions by type
     */
    public List<Transaction> getTransactionsByType(Transaction.TransactionType type) {
        return transactionRepository.findByType(type);
    }

    /**
     * Get transactions within date range
     */
    public List<Transaction> getTransactionsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByDateBetween(startDate, endDate);
    }

    /**
     * Get transactions by category and date range
     */
    public List<Transaction> getTransactionsByCategoryAndDateRange(String categoryId, LocalDateTime startDate, LocalDateTime endDate) {
        return transactionRepository.findByCategoryIdAndDateBetween(categoryId, startDate, endDate);
    }

    /**
     * Search transactions by description
     */
    public List<Transaction> searchTransactionsByDescription(String keyword) {
        return transactionRepository.findByDescriptionContainingIgnoreCase(keyword);
    }

    /**
     * Get transactions by amount range
     */
    public List<Transaction> getTransactionsByAmountRange(BigDecimal minAmount, BigDecimal maxAmount) {
        return transactionRepository.findByAmountBetween(minAmount, maxAmount);
    }

    /**
     * Calculate total expenses for a category in a date range
     */
    public BigDecimal calculateTotalExpensesByCategory(String categoryId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> expenses = transactionRepository.findByCategoryDateRangeAndType(
                categoryId, startDate, endDate, Transaction.TransactionType.EXPENSE);
        return expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total income for a category in a date range
     */
    public BigDecimal calculateTotalIncomeByCategory(String categoryId, LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> income = transactionRepository.findByCategoryDateRangeAndType(
                categoryId, startDate, endDate, Transaction.TransactionType.INCOME);
        return income.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total expenses in a date range
     */
    public BigDecimal calculateTotalExpenses(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> expenses = transactionRepository.findByDateBetween(startDate, endDate)
                .stream()
                .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
                .toList();
        return expenses.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate total income in a date range
     */
    public BigDecimal calculateTotalIncome(LocalDateTime startDate, LocalDateTime endDate) {
        List<Transaction> income = transactionRepository.findByDateBetween(startDate, endDate)
                .stream()
                .filter(t -> t.getType() == Transaction.TransactionType.INCOME)
                .toList();
        return income.stream()
                .map(Transaction::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get transaction statistics
     */
    public TransactionStatistics getTransactionStatistics() {
        long totalTransactions = transactionRepository.count();
        long totalExpenses = transactionRepository.countByType(Transaction.TransactionType.EXPENSE);
        long totalIncome = transactionRepository.countByType(Transaction.TransactionType.INCOME);
        
        return new TransactionStatistics(totalTransactions, totalExpenses, totalIncome);
    }

    /**
     * Inner class for transaction statistics
     */
    public static class TransactionStatistics {
        private final long totalTransactions;
        private final long totalExpenses;
        private final long totalIncome;

        public TransactionStatistics(long totalTransactions, long totalExpenses, long totalIncome) {
            this.totalTransactions = totalTransactions;
            this.totalExpenses = totalExpenses;
            this.totalIncome = totalIncome;
        }

        public long getTotalTransactions() { return totalTransactions; }
        public long getTotalExpenses() { return totalExpenses; }
        public long getTotalIncome() { return totalIncome; }
    }

    /**
     * Result class for transaction creation with validation
     */
    public static class TransactionCreationResult {
        private final Transaction transaction;
        private final TransactionStatus status;
        private final String message;

        public TransactionCreationResult(Transaction transaction, TransactionStatus status, String message) {
            this.transaction = transaction;
            this.status = status;
            this.message = message;
        }

        public Transaction getTransaction() { return transaction; }
        public TransactionStatus getStatus() { return status; }
        public String getMessage() { return message; }
    }

    /**
     * Enum for transaction creation status
     */
    public enum TransactionStatus {
        SUCCESS,
        OVER_BUDGET,
        BUDGET_EXCEEDED,
        MAJOR_EXPENSE,
        LARGE_EXPENSE,
        LOW_BUDGET,
        PROJECTED_OVERSPEND,
        NO_BUDGET
    }
}