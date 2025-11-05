package com.endemonat.application.controller;

import com.endemonat.application.entity.Budget;
import com.endemonat.application.service.BudgetService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

/**
 * REST Controller for Budget management.
 * Provides endpoints for CRUD operations and budget queries.
 * 
 * @author Emilio und Leander
 */
@RestController
@RequestMapping("/api/budgets")
@CrossOrigin(origins = "*") // Configure according to your frontend needs
public class BudgetController {

    private final BudgetService budgetService;

    public BudgetController(BudgetService budgetService) {
        this.budgetService = budgetService;
    }

    /**
     * Create a new budget
     */
    @PostMapping
    public ResponseEntity<Budget> createBudget(@Valid @RequestBody Budget budget) {
        try {
            Budget createdBudget = budgetService.createBudget(budget);
            return new ResponseEntity<>(createdBudget, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Get all budgets
     */
    @GetMapping
    public ResponseEntity<List<Budget>> getAllBudgets() {
        List<Budget> budgets = budgetService.getAllBudgets();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get all active budgets
     */
    @GetMapping("/active")
    public ResponseEntity<List<Budget>> getActiveBudgets() {
        List<Budget> budgets = budgetService.getActiveBudgets();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get budget by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Budget> getBudgetById(@PathVariable String id) {
        return budgetService.getBudgetById(id)
                .map(budget -> ResponseEntity.ok(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update budget by ID
     */
    @PutMapping("/{id}")
    public ResponseEntity<Budget> updateBudget(@PathVariable String id, 
                                             @Valid @RequestBody Budget budget) {
        return budgetService.updateBudget(id, budget)
                .map(updatedBudget -> ResponseEntity.ok(updatedBudget))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Delete budget by ID
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBudget(@PathVariable String id) {
        if (budgetService.deleteBudget(id)) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();
    }

    /**
     * Get budgets by category
     */
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<Budget>> getBudgetsByCategory(@PathVariable String categoryId) {
        List<Budget> budgets = budgetService.getBudgetsByCategory(categoryId);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get budgets by period
     */
    @GetMapping("/period/{period}")
    public ResponseEntity<List<Budget>> getBudgetsByPeriod(@PathVariable Budget.BudgetPeriod period) {
        List<Budget> budgets = budgetService.getBudgetsByPeriod(period);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get current active budgets
     */
    @GetMapping("/current")
    public ResponseEntity<List<Budget>> getCurrentActiveBudgets() {
        List<Budget> budgets = budgetService.getCurrentActiveBudgets();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get current active budgets for category
     */
    @GetMapping("/current/category/{categoryId}")
    public ResponseEntity<List<Budget>> getCurrentActiveBudgetsForCategory(@PathVariable String categoryId) {
        List<Budget> budgets = budgetService.getCurrentActiveBudgetsForCategory(categoryId);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Update spent amount for a budget
     */
    @PutMapping("/{id}/update-spent")
    public ResponseEntity<Budget> updateBudgetSpentAmount(@PathVariable String id) {
        return budgetService.updateBudgetSpentAmount(id)
                .map(budget -> ResponseEntity.ok(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Update spent amounts for all active budgets
     */
    @PutMapping("/update-all-spent")
    public ResponseEntity<Void> updateAllActiveBudgetSpentAmounts() {
        budgetService.updateAllActiveBudgetSpentAmounts();
        return ResponseEntity.ok().build();
    }

    /**
     * Get budgets that are over budget
     */
    @GetMapping("/over-budget")
    public ResponseEntity<List<Budget>> getOverBudgets() {
        List<Budget> budgets = budgetService.getOverBudgets();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Get budgets reaching alert threshold
     */
    @GetMapping("/alert-threshold")
    public ResponseEntity<List<Budget>> getBudgetsReachingAlertThreshold() {
        List<Budget> budgets = budgetService.getBudgetsReachingAlertThreshold();
        return ResponseEntity.ok(budgets);
    }

    /**
     * Search budgets by name
     */
    @GetMapping("/search")
    public ResponseEntity<List<Budget>> searchBudgets(@RequestParam String keyword) {
        List<Budget> budgets = budgetService.searchBudgetsByName(keyword);
        return ResponseEntity.ok(budgets);
    }

    /**
     * Deactivate budget
     */
    @PutMapping("/{id}/deactivate")
    public ResponseEntity<Budget> deactivateBudget(@PathVariable String id) {
        return budgetService.deactivateBudget(id)
                .map(budget -> ResponseEntity.ok(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Activate budget
     */
    @PutMapping("/{id}/activate")
    public ResponseEntity<Budget> activateBudget(@PathVariable String id) {
        return budgetService.activateBudget(id)
                .map(budget -> ResponseEntity.ok(budget))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * Get budget statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<BudgetService.BudgetStatistics> getBudgetStatistics() {
        BudgetService.BudgetStatistics statistics = budgetService.getBudgetStatistics();
        return ResponseEntity.ok(statistics);
    }

    /**
     * Get budget progress
     */
    @GetMapping("/{id}/progress")
    public ResponseEntity<BudgetService.BudgetProgress> getBudgetProgress(@PathVariable String id) {
        BudgetService.BudgetProgress progress = budgetService.getBudgetProgress(id);
        if (progress != null) {
            return ResponseEntity.ok(progress);
        }
        return ResponseEntity.notFound().build();
    }
}