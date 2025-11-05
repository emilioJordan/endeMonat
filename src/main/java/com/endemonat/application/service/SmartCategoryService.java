package com.endemonat.application.service;

import com.endemonat.application.entity.Category;
import com.endemonat.application.entity.Transaction;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Smart Category Intelligence Service
 * Provides complex analysis and recommendations for spending categories
 * 
 * @author Emilio und Leander
 */
@Service
public class SmartCategoryService {

    private final CategoryService categoryService;
    private final TransactionService transactionService;

    public SmartCategoryService(CategoryService categoryService, TransactionService transactionService) {
        this.categoryService = categoryService;
        this.transactionService = transactionService;
    }

    /**
     * Analyze spending patterns and provide category recommendations
     */
    public CategoryIntelligenceReport analyzeCategoryIntelligence() {
        List<Category> categories = categoryService.getActiveCategories();
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        
        Map<String, CategoryAnalysis> analyses = new HashMap<>();
        BigDecimal totalMonthlySpending = BigDecimal.ZERO;
        
        // Analyze each category
        for (Category category : categories) {
            CategoryAnalysis analysis = analyzeCategory(category, startOfMonth, today);
            analyses.put(category.getId(), analysis);
            totalMonthlySpending = totalMonthlySpending.add(analysis.getMonthlySpent());
        }
        
        // Find problematic categories
        List<String> overspendingCategories = new ArrayList<>();
        List<String> healthyCategories = new ArrayList<>();
        List<String> underusedCategories = new ArrayList<>();
        List<CategoryRecommendation> recommendations = new ArrayList<>();
        
        for (Map.Entry<String, CategoryAnalysis> entry : analyses.entrySet()) {
            String categoryId = entry.getKey();
            CategoryAnalysis analysis = entry.getValue();
            
            // Complex category classification
            if (analysis.getSpendingVelocity().compareTo(new BigDecimal("2.0")) > 0) {
                // Spending more than twice the safe rate
                overspendingCategories.add(categoryId);
                recommendations.add(new CategoryRecommendation(
                    categoryId,
                    RecommendationType.URGENT_REDUCE,
                    "🚨 DRINGEND: Ausgaben in dieser Kategorie drastisch reduzieren!",
                    analysis.getRecommendedDailyLimit()
                ));
            } else if (analysis.getSpendingVelocity().compareTo(new BigDecimal("1.3")) > 0) {
                // Spending 30% above safe rate
                overspendingCategories.add(categoryId);
                recommendations.add(new CategoryRecommendation(
                    categoryId,
                    RecommendationType.MODERATE_REDUCE,
                    "⚠️ Ausgaben reduzieren. Auf tägliches Limit achten.",
                    analysis.getRecommendedDailyLimit()
                ));
            } else if (analysis.getSpendingVelocity().compareTo(new BigDecimal("0.3")) < 0) {
                // Very low spending
                underusedCategories.add(categoryId);
                recommendations.add(new CategoryRecommendation(
                    categoryId,
                    RecommendationType.CAN_INCREASE,
                    "✅ Kategorie wird gut kontrolliert. Kleine Erhöhung möglich.",
                    analysis.getRecommendedDailyLimit().multiply(new BigDecimal("1.2"))
                ));
            } else {
                // Healthy spending
                healthyCategories.add(categoryId);
                recommendations.add(new CategoryRecommendation(
                    categoryId,
                    RecommendationType.MAINTAIN,
                    "👍 Perfekt! Weiter so.",
                    analysis.getRecommendedDailyLimit()
                ));
            }
        }
        
        // Generate overall spending health score
        SpendingHealthScore healthScore = calculateSpendingHealthScore(
            overspendingCategories.size(), 
            healthyCategories.size(), 
            underusedCategories.size(),
            totalMonthlySpending
        );
        
        return new CategoryIntelligenceReport(
            analyses,
            overspendingCategories,
            healthyCategories,
            underusedCategories,
            recommendations,
            healthScore,
            generateSmartInsights(analyses, healthScore)
        );
    }

    /**
     * Analyze individual category with complex logic
     */
    private CategoryAnalysis analyzeCategory(Category category, LocalDate startOfMonth, LocalDate today) {
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = today.atTime(23, 59, 59);
        
        // Get all transactions for this category
        List<Transaction> transactions = transactionService.getTransactionsByCategoryAndDateRange(
            category.getId(), startDateTime, endDateTime);
        
        // Filter expenses only
        List<Transaction> expenses = transactions.stream()
            .filter(t -> t.getType() == Transaction.TransactionType.EXPENSE)
            .collect(Collectors.toList());
        
        BigDecimal monthlySpent = expenses.stream()
            .map(Transaction::getAmount)
            .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        long daysElapsed = today.getDayOfMonth();
        long daysInMonth = today.lengthOfMonth();
        long daysRemaining = daysInMonth - daysElapsed;
        
        // Calculate spending patterns
        BigDecimal dailyAverage = daysElapsed > 0 ? 
            monthlySpent.divide(new BigDecimal(daysElapsed), 2, RoundingMode.HALF_UP) : 
            BigDecimal.ZERO;
        
        BigDecimal projectedMonthlySpending = dailyAverage.multiply(new BigDecimal(daysInMonth));
        
        // Determine safe daily spending for rest of month
        BigDecimal safeMonthlyBudget = estimateSafeMonthleBudget(monthlySpent, expenses.size(), daysElapsed);
        BigDecimal remainingSafeBudget = safeMonthlyBudget.subtract(monthlySpent);
        BigDecimal recommendedDailyLimit = daysRemaining > 0 ? 
            remainingSafeBudget.divide(new BigDecimal(daysRemaining), 2, RoundingMode.HALF_UP) :
            BigDecimal.ZERO;
        
        // Calculate spending velocity (how fast money is being spent)
        BigDecimal spendingVelocity = safeMonthlyBudget.compareTo(BigDecimal.ZERO) > 0 ?
            projectedMonthlySpending.divide(safeMonthlyBudget, 2, RoundingMode.HALF_UP) :
            BigDecimal.ZERO;
        
        // Determine category risk level
        CategoryRiskLevel riskLevel = determineRiskLevel(spendingVelocity, remainingSafeBudget, daysRemaining);
        
        return new CategoryAnalysis(
            category.getId(),
            monthlySpent,
            dailyAverage,
            projectedMonthlySpending,
            safeMonthlyBudget,
            recommendedDailyLimit,
            spendingVelocity,
            riskLevel,
            expenses.size()
        );
    }

    /**
     * Estimate safe monthly budget based on spending patterns
     */
    private BigDecimal estimateSafeMonthleBudget(BigDecimal currentSpent, int transactionCount, long daysElapsed) {
        // Complex estimation based on multiple factors
        BigDecimal baseEstimate = currentSpent.multiply(new BigDecimal("1.3")); // 30% buffer
        
        // Adjust based on transaction frequency
        if (transactionCount > daysElapsed * 2) {
            // Very frequent transactions - likely higher spending
            baseEstimate = baseEstimate.multiply(new BigDecimal("1.2"));
        } else if (transactionCount < daysElapsed * 0.5) {
            // Infrequent transactions - might be more controlled
            baseEstimate = baseEstimate.multiply(new BigDecimal("0.9"));
        }
        
        // Adjust based on time of month
        if (daysElapsed < 10) {
            // Early in month - be more generous in estimation
            baseEstimate = baseEstimate.multiply(new BigDecimal("1.1"));
        } else if (daysElapsed > 20) {
            // Late in month - be more conservative
            baseEstimate = baseEstimate.multiply(new BigDecimal("0.95"));
        }
        
        return baseEstimate;
    }

    /**
     * Determine risk level based on multiple factors
     */
    private CategoryRiskLevel determineRiskLevel(BigDecimal spendingVelocity, BigDecimal remainingBudget, long daysRemaining) {
        if (spendingVelocity.compareTo(new BigDecimal("2.0")) > 0 || remainingBudget.compareTo(BigDecimal.ZERO) <= 0) {
            return CategoryRiskLevel.CRITICAL;
        } else if (spendingVelocity.compareTo(new BigDecimal("1.5")) > 0 || 
                  (daysRemaining > 7 && remainingBudget.divide(new BigDecimal(daysRemaining), 2, RoundingMode.HALF_UP)
                   .compareTo(new BigDecimal("10")) < 0)) {
            return CategoryRiskLevel.HIGH;
        } else if (spendingVelocity.compareTo(new BigDecimal("1.2")) > 0) {
            return CategoryRiskLevel.MODERATE;
        } else if (spendingVelocity.compareTo(new BigDecimal("0.5")) < 0) {
            return CategoryRiskLevel.VERY_LOW;
        } else {
            return CategoryRiskLevel.LOW;
        }
    }

    /**
     * Calculate overall spending health score
     */
    private SpendingHealthScore calculateSpendingHealthScore(int problematicCategories, int healthyCategories, 
                                                           int underusedCategories, BigDecimal totalSpending) {
        int totalCategories = problematicCategories + healthyCategories + underusedCategories;
        
        if (totalCategories == 0) {
            return new SpendingHealthScore(0, "Keine Kategorien gefunden", HealthLevel.UNKNOWN);
        }
        
        // Calculate score based on multiple factors
        double categoryRatio = (double) healthyCategories / totalCategories;
        double problematicRatio = (double) problematicCategories / totalCategories;
        
        int score = (int) (categoryRatio * 100);
        score -= (int) (problematicRatio * 50); // Penalty for problematic categories
        
        // Adjust based on spending level
        if (totalSpending.compareTo(new BigDecimal("3000")) > 0) {
            score -= 10; // High spending penalty
        } else if (totalSpending.compareTo(new BigDecimal("1000")) < 0) {
            score += 5; // Conservative spending bonus
        }
        
        score = Math.max(0, Math.min(100, score)); // Clamp between 0-100
        
        String message;
        HealthLevel level;
        
        if (score >= 90) {
            level = HealthLevel.EXCELLENT;
            message = "🏆 Ausgezeichnet! Ihre Ausgaben sind perfekt kontrolliert!";
        } else if (score >= 75) {
            level = HealthLevel.GOOD;
            message = "✅ Gut! Kleine Verbesserungen möglich.";
        } else if (score >= 60) {
            level = HealthLevel.FAIR;
            message = "⚠️ Mittelmäßig. Einige Kategorien brauchen Aufmerksamkeit.";
        } else if (score >= 40) {
            level = HealthLevel.POOR;
            message = "🚨 Schlecht! Ausgaben dringend überdenken!";
        } else {
            level = HealthLevel.CRITICAL;
            message = "💸 Kritisch! Sofortiges Handeln erforderlich!";
        }
        
        return new SpendingHealthScore(score, message, level);
    }

    /**
     * Generate smart insights based on analysis
     */
    private List<String> generateSmartInsights(Map<String, CategoryAnalysis> analyses, SpendingHealthScore healthScore) {
        List<String> insights = new ArrayList<>();
        
        // General insights based on health score
        if (healthScore.getLevel() == HealthLevel.CRITICAL) {
            insights.add("🆘 NOTFALL: Budget-Kontrolle ist völlig verloren gegangen!");
            insights.add("💳 Sofort alle Kreditkarten zu Hause lassen");
            insights.add("🛒 Nur noch absolute Notwendigkeiten kaufen");
        } else if (healthScore.getLevel() == HealthLevel.POOR) {
            insights.add("🚨 Gefahr: Ausgaben sind nicht nachhaltig");
            insights.add("📊 Tägliche Budget-Limits strikt einhalten");
        }
        
        // Category-specific insights
        long highRiskCategories = analyses.values().stream()
            .mapToLong(a -> a.getRiskLevel() == CategoryRiskLevel.CRITICAL || a.getRiskLevel() == CategoryRiskLevel.HIGH ? 1 : 0)
            .sum();
        
        if (highRiskCategories > 2) {
            insights.add("⚡ " + highRiskCategories + " Kategorien sind in der Risikozone!");
            insights.add("🎯 Fokus auf die 2-3 größten Problemkategorien legen");
        }
        
        // Behavioral insights
        BigDecimal avgSpendingVelocity = analyses.values().stream()
            .map(CategoryAnalysis::getSpendingVelocity)
            .reduce(BigDecimal.ZERO, BigDecimal::add)
            .divide(new BigDecimal(analyses.size()), 2, RoundingMode.HALF_UP);
        
        if (avgSpendingVelocity.compareTo(new BigDecimal("1.5")) > 0) {
            insights.add("🚀 Generell zu hohe Ausgabengeschwindigkeit!");
            insights.add("⏳ Vor jedem Kauf 5 Minuten warten und überdenken");
        }
        
        return insights;
    }

    // Inner classes for complex data structures
    public static class CategoryIntelligenceReport {
        private final Map<String, CategoryAnalysis> categoryAnalyses;
        private final List<String> overspendingCategories;
        private final List<String> healthyCategories;
        private final List<String> underusedCategories;
        private final List<CategoryRecommendation> recommendations;
        private final SpendingHealthScore healthScore;
        private final List<String> smartInsights;

        public CategoryIntelligenceReport(Map<String, CategoryAnalysis> categoryAnalyses,
                                        List<String> overspendingCategories, List<String> healthyCategories,
                                        List<String> underusedCategories, List<CategoryRecommendation> recommendations,
                                        SpendingHealthScore healthScore, List<String> smartInsights) {
            this.categoryAnalyses = categoryAnalyses;
            this.overspendingCategories = overspendingCategories;
            this.healthyCategories = healthyCategories;
            this.underusedCategories = underusedCategories;
            this.recommendations = recommendations;
            this.healthScore = healthScore;
            this.smartInsights = smartInsights;
        }

        // Getters
        public Map<String, CategoryAnalysis> getCategoryAnalyses() { return categoryAnalyses; }
        public List<String> getOverspendingCategories() { return overspendingCategories; }
        public List<String> getHealthyCategories() { return healthyCategories; }
        public List<String> getUnderusedCategories() { return underusedCategories; }
        public List<CategoryRecommendation> getRecommendations() { return recommendations; }
        public SpendingHealthScore getHealthScore() { return healthScore; }
        public List<String> getSmartInsights() { return smartInsights; }
    }

    public static class CategoryAnalysis {
        private final String categoryId;
        private final BigDecimal monthlySpent;
        private final BigDecimal dailyAverage;
        private final BigDecimal projectedMonthlySpending;
        private final BigDecimal safeMonthlyBudget;
        private final BigDecimal recommendedDailyLimit;
        private final BigDecimal spendingVelocity;
        private final CategoryRiskLevel riskLevel;
        private final int transactionCount;

        public CategoryAnalysis(String categoryId, BigDecimal monthlySpent, BigDecimal dailyAverage,
                              BigDecimal projectedMonthlySpending, BigDecimal safeMonthlyBudget,
                              BigDecimal recommendedDailyLimit, BigDecimal spendingVelocity,
                              CategoryRiskLevel riskLevel, int transactionCount) {
            this.categoryId = categoryId;
            this.monthlySpent = monthlySpent;
            this.dailyAverage = dailyAverage;
            this.projectedMonthlySpending = projectedMonthlySpending;
            this.safeMonthlyBudget = safeMonthlyBudget;
            this.recommendedDailyLimit = recommendedDailyLimit;
            this.spendingVelocity = spendingVelocity;
            this.riskLevel = riskLevel;
            this.transactionCount = transactionCount;
        }

        // Getters
        public String getCategoryId() { return categoryId; }
        public BigDecimal getMonthlySpent() { return monthlySpent; }
        public BigDecimal getDailyAverage() { return dailyAverage; }
        public BigDecimal getProjectedMonthlySpending() { return projectedMonthlySpending; }
        public BigDecimal getSafeMonthlyBudget() { return safeMonthlyBudget; }
        public BigDecimal getRecommendedDailyLimit() { return recommendedDailyLimit; }
        public BigDecimal getSpendingVelocity() { return spendingVelocity; }
        public CategoryRiskLevel getRiskLevel() { return riskLevel; }
        public int getTransactionCount() { return transactionCount; }
    }

    public static class CategoryRecommendation {
        private final String categoryId;
        private final RecommendationType type;
        private final String message;
        private final BigDecimal recommendedDailyLimit;

        public CategoryRecommendation(String categoryId, RecommendationType type, String message, BigDecimal recommendedDailyLimit) {
            this.categoryId = categoryId;
            this.type = type;
            this.message = message;
            this.recommendedDailyLimit = recommendedDailyLimit;
        }

        // Getters
        public String getCategoryId() { return categoryId; }
        public RecommendationType getType() { return type; }
        public String getMessage() { return message; }
        public BigDecimal getRecommendedDailyLimit() { return recommendedDailyLimit; }
    }

    public static class SpendingHealthScore {
        private final int score;
        private final String message;
        private final HealthLevel level;

        public SpendingHealthScore(int score, String message, HealthLevel level) {
            this.score = score;
            this.message = message;
            this.level = level;
        }

        // Getters
        public int getScore() { return score; }
        public String getMessage() { return message; }
        public HealthLevel getLevel() { return level; }
    }

    public enum CategoryRiskLevel {
        VERY_LOW, LOW, MODERATE, HIGH, CRITICAL
    }

    public enum RecommendationType {
        URGENT_REDUCE, MODERATE_REDUCE, MAINTAIN, CAN_INCREASE
    }

    public enum HealthLevel {
        CRITICAL, POOR, FAIR, GOOD, EXCELLENT, UNKNOWN
    }
}