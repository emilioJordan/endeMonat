package com.endemonat.application.service;

import com.endemonat.application.entity.Budget;
import com.endemonat.application.repository.BudgetRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service class for Budget entity.
 * Contains business logic for budget management.
 * 
 * @author Emilio und Leander
 */
@Service
public class BudgetService {

    private final BudgetRepository budgetRepository;
    private final TransactionService transactionService;

    public BudgetService(BudgetRepository budgetRepository, TransactionService transactionService) {
        this.budgetRepository = budgetRepository;
        this.transactionService = transactionService;
    }

    /**
     * Create a new budget
     */
    public Budget createBudget(Budget budget) {
        budget.setCreatedAt(LocalDateTime.now());
        budget.setUpdatedAt(LocalDateTime.now());
        
        // Initialize spent amount if not set
        if (budget.getSpentAmount() == null) {
            budget.setSpentAmount(BigDecimal.ZERO);
        }
        
        return budgetRepository.save(budget);
    }

    /**
     * Get all budgets
     */
    public List<Budget> getAllBudgets() {
        return budgetRepository.findAllByOrderByStartDateDesc();
    }

    /**
     * Get all active budgets
     */
    public List<Budget> getActiveBudgets() {
        return budgetRepository.findByIsActiveTrueOrderByStartDateDesc();
    }

    /**
     * Get budget by ID
     */
    public Optional<Budget> getBudgetById(String id) {
        return budgetRepository.findById(id);
    }

    /**
     * Update existing budget
     */
    public Optional<Budget> updateBudget(String id, Budget updatedBudget) {
        return budgetRepository.findById(id)
                .map(budget -> {
                    budget.setName(updatedBudget.getName());
                    budget.setDescription(updatedBudget.getDescription());
                    budget.setAmount(updatedBudget.getAmount());
                    budget.setCategoryId(updatedBudget.getCategoryId());
                    budget.setPeriod(updatedBudget.getPeriod());
                    budget.setStartDate(updatedBudget.getStartDate());
                    budget.setEndDate(updatedBudget.getEndDate());
                    budget.setActive(updatedBudget.isActive());
                    budget.setAlertThreshold(updatedBudget.getAlertThreshold());
                    budget.setUpdatedAt(LocalDateTime.now());
                    return budgetRepository.save(budget);
                });
    }

    /**
     * Delete budget by ID
     */
    public boolean deleteBudget(String id) {
        if (budgetRepository.existsById(id)) {
            budgetRepository.deleteById(id);
            return true;
        }
        return false;
    }

    /**
     * Get budgets by category
     */
    public List<Budget> getBudgetsByCategory(String categoryId) {
        return budgetRepository.findByCategoryId(categoryId);
    }

    /**
     * Get budgets by period
     */
    public List<Budget> getBudgetsByPeriod(Budget.BudgetPeriod period) {
        return budgetRepository.findByIsActiveTrueAndPeriod(period);
    }

    /**
     * Get current active budgets for today
     */
    public List<Budget> getCurrentActiveBudgets() {
        return budgetRepository.findActiveBudgetsForDate(LocalDate.now());
    }

    /**
     * Get current active budgets for a specific category
     */
    public List<Budget> getCurrentActiveBudgetsForCategory(String categoryId) {
        return budgetRepository.findActiveBudgetsForCategoryAndDate(categoryId, LocalDate.now());
    }

    /**
     * Update spent amount for a budget based on transactions
     */
    public Optional<Budget> updateBudgetSpentAmount(String budgetId) {
        return budgetRepository.findById(budgetId)
                .map(budget -> {
                    BigDecimal totalSpent = transactionService.calculateTotalExpensesByCategory(
                            budget.getCategoryId(),
                            budget.getStartDate().atStartOfDay(),
                            budget.getEndDate().atTime(23, 59, 59)
                    );
                    budget.setSpentAmount(totalSpent);
                    budget.setUpdatedAt(LocalDateTime.now());
                    return budgetRepository.save(budget);
                });
    }

    /**
     * Update spent amounts for all active budgets
     */
    public void updateAllActiveBudgetSpentAmounts() {
        List<Budget> activeBudgets = getActiveBudgets();
        for (Budget budget : activeBudgets) {
            updateBudgetSpentAmount(budget.getId());
        }
    }

    /**
     * Get budgets that are over budget
     */
    public List<Budget> getOverBudgets() {
        return budgetRepository.findOverBudgets();
    }

    /**
     * Get budgets that have reached alert threshold
     */
    public List<Budget> getBudgetsReachingAlertThreshold() {
        return budgetRepository.findBudgetsReachingAlertThreshold();
    }

    /**
     * Search budgets by name
     */
    public List<Budget> searchBudgetsByName(String keyword) {
        return budgetRepository.findByNameContainingIgnoreCase(keyword);
    }

    /**
     * Deactivate budget
     */
    public Optional<Budget> deactivateBudget(String id) {
        return budgetRepository.findById(id)
                .map(budget -> {
                    budget.setActive(false);
                    budget.setUpdatedAt(LocalDateTime.now());
                    return budgetRepository.save(budget);
                });
    }

    /**
     * Activate budget
     */
    public Optional<Budget> activateBudget(String id) {
        return budgetRepository.findById(id)
                .map(budget -> {
                    budget.setActive(true);
                    budget.setUpdatedAt(LocalDateTime.now());
                    return budgetRepository.save(budget);
                });
    }

    /**
     * Get budget statistics
     */
    public BudgetStatistics getBudgetStatistics() {
        long totalBudgets = budgetRepository.count();
        long activeBudgets = budgetRepository.countByIsActiveTrue();
        List<Budget> overBudgets = getOverBudgets();
        List<Budget> alertBudgets = getBudgetsReachingAlertThreshold();
        
        return new BudgetStatistics(totalBudgets, activeBudgets, overBudgets.size(), alertBudgets.size());
    }

    /**
     * Get budget progress for a specific budget
     */
    public BudgetProgress getBudgetProgress(String budgetId) {
        return budgetRepository.findById(budgetId)
                .map(budget -> {
                    BigDecimal remainingAmount = budget.getRemainingAmount();
                    BigDecimal spentPercentage = budget.getSpentPercentage();
                    boolean isOverBudget = budget.isOverBudget();
                    boolean isAlertThresholdReached = budget.isAlertThresholdReached();
                    
                    return new BudgetProgress(budget.getId(), budget.getName(), budget.getAmount(), 
                            budget.getSpentAmount(), remainingAmount, spentPercentage, 
                            isOverBudget, isAlertThresholdReached);
                })
                .orElse(null);
    }

    /**
     * Inner class for budget statistics
     */
    public static class BudgetStatistics {
        private final long totalBudgets;
        private final long activeBudgets;
        private final long overBudgets;
        private final long alertBudgets;

        public BudgetStatistics(long totalBudgets, long activeBudgets, long overBudgets, long alertBudgets) {
            this.totalBudgets = totalBudgets;
            this.activeBudgets = activeBudgets;
            this.overBudgets = overBudgets;
            this.alertBudgets = alertBudgets;
        }

        public long getTotalBudgets() { return totalBudgets; }
        public long getActiveBudgets() { return activeBudgets; }
        public long getOverBudgets() { return overBudgets; }
        public long getAlertBudgets() { return alertBudgets; }
    }

    /**
     * Inner class for budget progress
     */
    public static class BudgetProgress {
        private final String budgetId;
        private final String budgetName;
        private final BigDecimal budgetAmount;
        private final BigDecimal spentAmount;
        private final BigDecimal remainingAmount;
        private final BigDecimal spentPercentage;
        private final boolean isOverBudget;
        private final boolean isAlertThresholdReached;

        public BudgetProgress(String budgetId, String budgetName, BigDecimal budgetAmount, 
                            BigDecimal spentAmount, BigDecimal remainingAmount, BigDecimal spentPercentage,
                            boolean isOverBudget, boolean isAlertThresholdReached) {
            this.budgetId = budgetId;
            this.budgetName = budgetName;
            this.budgetAmount = budgetAmount;
            this.spentAmount = spentAmount;
            this.remainingAmount = remainingAmount;
            this.spentPercentage = spentPercentage;
            this.isOverBudget = isOverBudget;
            this.isAlertThresholdReached = isAlertThresholdReached;
        }

        // Getters
        public String getBudgetId() { return budgetId; }
        public String getBudgetName() { return budgetName; }
        public BigDecimal getBudgetAmount() { return budgetAmount; }
        public BigDecimal getSpentAmount() { return spentAmount; }
        public BigDecimal getRemainingAmount() { return remainingAmount; }
        public BigDecimal getSpentPercentage() { return spentPercentage; }
        public boolean isOverBudget() { return isOverBudget; }
        public boolean isAlertThresholdReached() { return isAlertThresholdReached; }
    }
}