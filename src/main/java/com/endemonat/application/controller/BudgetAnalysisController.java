package com.endemonat.application.controller;

import com.endemonat.application.service.BudgetAnalysisService;
import com.endemonat.application.service.SmartCategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * REST Controller for advanced budget analysis and "Ende Monat" predictions.
 * Provides complex financial analysis with multiple conditional logic.
 * 
 * @author Emilio und Leander
 */
@RestController
@RequestMapping("/api/analysis")
@CrossOrigin(origins = "*")
public class BudgetAnalysisController {

    private final BudgetAnalysisService budgetAnalysisService;
    private final SmartCategoryService smartCategoryService;

    public BudgetAnalysisController(BudgetAnalysisService budgetAnalysisService, SmartCategoryService smartCategoryService) {
        this.budgetAnalysisService = budgetAnalysisService;
        this.smartCategoryService = smartCategoryService;
    }

    /**
     * Get comprehensive end-of-month analysis
     * Complex analysis with multiple conditions and recommendations
     */
    @GetMapping("/end-of-month")
    public ResponseEntity<BudgetAnalysisService.EndOfMonthAnalysis> getEndOfMonthAnalysis() {
        try {
            BudgetAnalysisService.EndOfMonthAnalysis analysis = 
                budgetAnalysisService.analyzeEndOfMonthSituation();
            return ResponseEntity.ok(analysis);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get spending recommendations for all categories
     * Complex calculations based on remaining budget and spending patterns
     */
    @GetMapping("/spending-recommendations")
    public ResponseEntity<Map<String, BudgetAnalysisService.SpendingRecommendation>> getSpendingRecommendations() {
        try {
            Map<String, BudgetAnalysisService.SpendingRecommendation> recommendations = 
                budgetAnalysisService.calculateSpendingRecommendations();
            return ResponseEntity.ok(recommendations);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Predict if user will survive until end of month
     * Complex survival prediction with detailed recommendations
     */
    @GetMapping("/survival-prediction")
    public ResponseEntity<BudgetAnalysisService.MonthSurvivalPrediction> getMonthSurvivalPrediction() {
        try {
            BudgetAnalysisService.MonthSurvivalPrediction prediction = 
                budgetAnalysisService.predictMonthSurvival();
            return ResponseEntity.ok(prediction);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * Get smart category intelligence analysis
     * Advanced AI-like analysis of spending patterns
     */
    @GetMapping("/category-intelligence")
    public ResponseEntity<SmartCategoryService.CategoryIntelligenceReport> getCategoryIntelligence() {
        try {
            SmartCategoryService.CategoryIntelligenceReport report = 
                smartCategoryService.analyzeCategoryIntelligence();
            return ResponseEntity.ok(report);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}