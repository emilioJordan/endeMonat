package com.endemonat.application.service;

import com.endemonat.application.entity.Budget;
import com.endemonat.application.entity.Category;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Advanced Budget Analysis Service
 * Provides complex calculations and predictions for end-of-month scenarios
 * 
 * @author Emilio und Leander
 */
@Service
public class BudgetAnalysisService {

    private final BudgetService budgetService;
    private final TransactionService transactionService;
    private final CategoryService categoryService;

    public BudgetAnalysisService(BudgetService budgetService, 
                               TransactionService transactionService,
                               CategoryService categoryService) {
        this.budgetService = budgetService;
        this.transactionService = transactionService;
        this.categoryService = categoryService;
    }

    /**
     * Complex analysis for "Ende Monat" - End of Month Budget Status
     */
    public EndOfMonthAnalysis analyzeEndOfMonthSituation() {
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        long daysRemaining = ChronoUnit.DAYS.between(today, endOfMonth);
        
        List<Budget> activeBudgets = budgetService.getCurrentActiveBudgets();
        Map<String, BigDecimal> categorySpending = calculateCategorySpending(today);
        
        // Complex conditions for budget analysis
        BigDecimal totalRemainingBudget = BigDecimal.ZERO;
        BigDecimal projectedOverspend = BigDecimal.ZERO;
        List<String> criticalCategories = new ArrayList<>();
        List<String> warningCategories = new ArrayList<>();
        List<String> safeCategories = new ArrayList<>();
        
        for (Budget budget : activeBudgets) {
            BigDecimal spent = categorySpending.getOrDefault(budget.getCategoryId(), BigDecimal.ZERO);
            BigDecimal remaining = budget.getAmount().subtract(spent);
            BigDecimal dailyAverage = calculateDailyAverage(budget.getCategoryId(), today);
            BigDecimal projectedSpending = dailyAverage.multiply(new BigDecimal(daysRemaining));
            
            // Complex if-else logic for budget status
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                // Already over budget
                criticalCategories.add(budget.getCategoryId());
                projectedOverspend = projectedOverspend.add(remaining.abs());
            } else if (projectedSpending.compareTo(remaining) > 0) {
                // Will likely exceed budget
                BigDecimal overspend = projectedSpending.subtract(remaining);
                if (overspend.divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
                    .compareTo(new BigDecimal("0.3")) > 0) {
                    // More than 30% overspend projected
                    criticalCategories.add(budget.getCategoryId());
                    projectedOverspend = projectedOverspend.add(overspend);
                } else {
                    // Minor overspend
                    warningCategories.add(budget.getCategoryId());
                }
            } else {
                // Safe spending
                BigDecimal buffer = remaining.subtract(projectedSpending);
                if (buffer.divide(budget.getAmount(), 2, RoundingMode.HALF_UP)
                    .compareTo(new BigDecimal("0.2")) > 0) {
                    // More than 20% buffer
                    safeCategories.add(budget.getCategoryId());
                    totalRemainingBudget = totalRemainingBudget.add(buffer);
                } else {
                    // Tight but manageable
                    warningCategories.add(budget.getCategoryId());
                }
            }
        }
        
        // Determine overall financial health
        FinancialHealthStatus healthStatus = determineFinancialHealth(
            criticalCategories.size(), warningCategories.size(), safeCategories.size(),
            projectedOverspend, totalRemainingBudget);
        
        return new EndOfMonthAnalysis(
            daysRemaining,
            totalRemainingBudget,
            projectedOverspend,
            criticalCategories,
            warningCategories,
            safeCategories,
            healthStatus,
            generateRecommendations(healthStatus, criticalCategories, warningCategories)
        );
    }

    /**
     * Calculate spending recommendations based on remaining budget
     */
    public Map<String, SpendingRecommendation> calculateSpendingRecommendations() {
        Map<String, SpendingRecommendation> recommendations = new HashMap<>();
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        long daysRemaining = ChronoUnit.DAYS.between(today, endOfMonth);
        
        if (daysRemaining <= 0) {
            daysRemaining = 1; // Avoid division by zero
        }
        
        List<Budget> activeBudgets = budgetService.getCurrentActiveBudgets();
        
        for (Budget budget : activeBudgets) {
            BigDecimal remaining = budget.getRemainingAmount();
            BigDecimal dailyAverage = calculateDailyAverage(budget.getCategoryId(), today);
            BigDecimal recommendedDaily = remaining.divide(new BigDecimal(daysRemaining), 2, RoundingMode.HALF_UP);
            
            SpendingLevel level;
            String message;
            BigDecimal maxDailySpend;
            
            // Complex conditions for spending recommendations
            if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
                level = SpendingLevel.EMERGENCY_STOP;
                message = "⛔ STOPP! Budget bereits überschritten. Keine weiteren Ausgaben in dieser Kategorie!";
                maxDailySpend = BigDecimal.ZERO;
            } else if (recommendedDaily.compareTo(dailyAverage.multiply(new BigDecimal("0.5"))) < 0) {
                level = SpendingLevel.CRITICAL;
                message = "🚨 Nur noch " + recommendedDaily + " CHF pro Tag! Drastisch reduzieren!";
                maxDailySpend = recommendedDaily;
            } else if (recommendedDaily.compareTo(dailyAverage.multiply(new BigDecimal("0.8"))) < 0) {
                level = SpendingLevel.WARNING;
                message = "⚠️ Vorsichtig ausgeben: Max " + recommendedDaily + " CHF täglich";
                maxDailySpend = recommendedDaily;
            } else if (recommendedDaily.compareTo(dailyAverage.multiply(new BigDecimal("1.2"))) > 0) {
                level = SpendingLevel.COMFORTABLE;
                message = "✅ Entspannt: Bis zu " + recommendedDaily + " CHF täglich möglich";
                maxDailySpend = recommendedDaily.multiply(new BigDecimal("1.1"));
            } else {
                level = SpendingLevel.NORMAL;
                message = "👍 Normal ausgeben: Etwa " + recommendedDaily + " CHF täglich";
                maxDailySpend = recommendedDaily;
            }
            
            recommendations.put(budget.getCategoryId(), 
                new SpendingRecommendation(level, message, recommendedDaily, maxDailySpend, dailyAverage));
        }
        
        return recommendations;
    }

    /**
     * Predict if user will survive until end of month
     */
    public MonthSurvivalPrediction predictMonthSurvival() {
        LocalDate today = LocalDate.now();
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        long daysRemaining = ChronoUnit.DAYS.between(today, endOfMonth);
        
        List<Budget> activeBudgets = budgetService.getCurrentActiveBudgets();
        BigDecimal totalRemainingBudget = BigDecimal.ZERO;
        BigDecimal totalProjectedSpending = BigDecimal.ZERO;
        int criticalCategories = 0;
        
        for (Budget budget : activeBudgets) {
            BigDecimal remaining = budget.getRemainingAmount();
            BigDecimal dailyAverage = calculateDailyAverage(budget.getCategoryId(), today);
            BigDecimal projected = dailyAverage.multiply(new BigDecimal(daysRemaining));
            
            totalRemainingBudget = totalRemainingBudget.add(remaining);
            totalProjectedSpending = totalProjectedSpending.add(projected);
            
            if (remaining.compareTo(projected) < 0) {
                criticalCategories++;
            }
        }
        
        // Complex survival prediction logic
        SurvivalStatus status;
        String message;
        List<String> recommendations = new ArrayList<>();
        
        if (totalRemainingBudget.compareTo(BigDecimal.ZERO) <= 0) {
            status = SurvivalStatus.GAME_OVER;
            message = "💸 Game Over! Alle Budgets aufgebraucht. Zeit für Notfallplan!";
            recommendations.add("Sofort alle nicht-essentiellen Ausgaben stoppen");
            recommendations.add("Nach zusätzlichen Einnahmequellen suchen");
            recommendations.add("Freunde/Familie um finanzielle Hilfe bitten");
        } else if (criticalCategories >= activeBudgets.size() * 0.7) {
            status = SurvivalStatus.CRITICAL;
            message = "🆘 Kritisch! " + criticalCategories + " von " + activeBudgets.size() + " Kategorien in der Krise!";
            recommendations.add("Nur noch absolute Notwendigkeiten kaufen");
            recommendations.add("Alle Abonnements und Extras kündigen");
            recommendations.add("Günstige Alternativen für alles suchen");
        } else if (totalProjectedSpending.compareTo(totalRemainingBudget.multiply(new BigDecimal("1.2"))) > 0) {
            status = SurvivalStatus.RISKY;
            message = "⚠️ Riskant! Ausgaben müssen um " + 
                     totalProjectedSpending.subtract(totalRemainingBudget).divide(totalRemainingBudget, 2, RoundingMode.HALF_UP)
                     .multiply(new BigDecimal("100")) + "% reduziert werden!";
            recommendations.add("Ausgaben in allen Kategorien reduzieren");
            recommendations.add("Günstigere Alternativen wählen");
            recommendations.add("Spontankäufe vermeiden");
        } else if (totalProjectedSpending.compareTo(totalRemainingBudget.multiply(new BigDecimal("0.8"))) < 0) {
            status = SurvivalStatus.COMFORTABLE;
            message = "😎 Entspannt! Du hast sogar noch Puffer für Extras!";
            recommendations.add("Du kannst dir auch mal was gönnen");
            recommendations.add("Vielleicht für nächsten Monat sparen?");
        } else {
            status = SurvivalStatus.TIGHT_BUT_MANAGEABLE;
            message = "🤏 Knapp aber machbar! Vorsichtig bis Monatsende!";
            recommendations.add("Bei jedem Kauf zweimal überlegen");
            recommendations.add("Angebote und Rabatte nutzen");
            recommendations.add("Ungeplante Ausgaben vermeiden");
        }
        
        return new MonthSurvivalPrediction(
            status, message, daysRemaining, totalRemainingBudget, 
            totalProjectedSpending, recommendations
        );
    }

    /**
     * Calculate daily spending average for a category
     */
    private BigDecimal calculateDailyAverage(String categoryId, LocalDate currentDate) {
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = currentDate.atTime(23, 59, 59);
        
        BigDecimal totalSpent = transactionService.calculateTotalExpensesByCategory(
            categoryId, startDateTime, endDateTime);
        
        long daysElapsed = ChronoUnit.DAYS.between(startOfMonth, currentDate) + 1;
        
        if (daysElapsed <= 0) {
            return BigDecimal.ZERO;
        }
        
        return totalSpent.divide(new BigDecimal(daysElapsed), 2, RoundingMode.HALF_UP);
    }

    /**
     * Calculate category spending for current month
     */
    private Map<String, BigDecimal> calculateCategorySpending(LocalDate currentDate) {
        Map<String, BigDecimal> spending = new HashMap<>();
        LocalDate startOfMonth = currentDate.withDayOfMonth(1);
        LocalDateTime startDateTime = startOfMonth.atStartOfDay();
        LocalDateTime endDateTime = currentDate.atTime(23, 59, 59);
        
        List<Category> categories = categoryService.getActiveCategories();
        
        for (Category category : categories) {
            BigDecimal totalSpent = transactionService.calculateTotalExpensesByCategory(
                category.getId(), startDateTime, endDateTime);
            spending.put(category.getId(), totalSpent);
        }
        
        return spending;
    }

    /**
     * Determine overall financial health based on multiple factors
     */
    private FinancialHealthStatus determineFinancialHealth(int critical, int warning, int safe,
                                                         BigDecimal overspend, BigDecimal remaining) {
        double criticalRatio = (double) critical / (critical + warning + safe);
        double remainingRatio = remaining.compareTo(BigDecimal.ZERO) == 0 ? 0 : 
                               remaining.subtract(overspend).divide(remaining, 2, RoundingMode.HALF_UP).doubleValue();
        
        if (criticalRatio >= 0.5 || remainingRatio < -0.3) {
            return FinancialHealthStatus.CRITICAL;
        } else if (criticalRatio >= 0.3 || remainingRatio < 0.1) {
            return FinancialHealthStatus.POOR;
        } else if (criticalRatio >= 0.1 || remainingRatio < 0.3) {
            return FinancialHealthStatus.FAIR;
        } else if (remainingRatio >= 0.5) {
            return FinancialHealthStatus.EXCELLENT;
        } else {
            return FinancialHealthStatus.GOOD;
        }
    }

    /**
     * Generate contextual recommendations
     */
    private List<String> generateRecommendations(FinancialHealthStatus status, 
                                               List<String> critical, List<String> warning) {
        List<String> recommendations = new ArrayList<>();
        
        switch (status) {
            case CRITICAL:
                recommendations.add("🚨 NOTFALL: Alle nicht-essentiellen Ausgaben sofort stoppen!");
                recommendations.add("💳 Kreditkarten zu Hause lassen");
                recommendations.add("🍜 Nur noch selbst kochen, kein Auswärts essen");
                recommendations.add("🚌 Öffentliche Verkehrsmittel statt Taxi/Uber");
                break;
            case POOR:
                recommendations.add("⚠️ Vorsicht: Ausgaben stark reduzieren");
                recommendations.add("🛒 Einkaufsliste schreiben und strikt befolgen");
                recommendations.add("☕ Coffee-to-go und Snacks vermeiden");
                break;
            case FAIR:
                recommendations.add("👍 Aufpassen: Spontankäufe vermeiden");
                recommendations.add("💰 Bei größeren Ausgaben zweimal überlegen");
                recommendations.add("🏷️ Angebote und Rabatte nutzen");
                break;
            case GOOD:
                recommendations.add("✅ Weiter so! Budget im grünen Bereich");
                recommendations.add("💾 Überlege, ob du etwas sparen möchtest");
                break;
            case EXCELLENT:
                recommendations.add("🎉 Perfekt! Du hast deine Finanzen im Griff");
                recommendations.add("🎁 Du kannst dir auch mal etwas Besonderes gönnen");
                recommendations.add("💰 Spare den Überschuss für nächsten Monat");
                break;
        }
        
        return recommendations;
    }

    // Inner classes for complex data structures
    public static class EndOfMonthAnalysis {
        private final long daysRemaining;
        private final BigDecimal totalRemainingBudget;
        private final BigDecimal projectedOverspend;
        private final List<String> criticalCategories;
        private final List<String> warningCategories;
        private final List<String> safeCategories;
        private final FinancialHealthStatus healthStatus;
        private final List<String> recommendations;

        public EndOfMonthAnalysis(long daysRemaining, BigDecimal totalRemainingBudget, 
                                BigDecimal projectedOverspend, List<String> criticalCategories,
                                List<String> warningCategories, List<String> safeCategories,
                                FinancialHealthStatus healthStatus, List<String> recommendations) {
            this.daysRemaining = daysRemaining;
            this.totalRemainingBudget = totalRemainingBudget;
            this.projectedOverspend = projectedOverspend;
            this.criticalCategories = criticalCategories;
            this.warningCategories = warningCategories;
            this.safeCategories = safeCategories;
            this.healthStatus = healthStatus;
            this.recommendations = recommendations;
        }

        // Getters
        public long getDaysRemaining() { return daysRemaining; }
        public BigDecimal getTotalRemainingBudget() { return totalRemainingBudget; }
        public BigDecimal getProjectedOverspend() { return projectedOverspend; }
        public List<String> getCriticalCategories() { return criticalCategories; }
        public List<String> getWarningCategories() { return warningCategories; }
        public List<String> getSafeCategories() { return safeCategories; }
        public FinancialHealthStatus getHealthStatus() { return healthStatus; }
        public List<String> getRecommendations() { return recommendations; }
    }

    public static class SpendingRecommendation {
        private final SpendingLevel level;
        private final String message;
        private final BigDecimal recommendedDailyAmount;
        private final BigDecimal maxDailySpend;
        private final BigDecimal currentDailyAverage;

        public SpendingRecommendation(SpendingLevel level, String message, 
                                    BigDecimal recommendedDailyAmount, BigDecimal maxDailySpend,
                                    BigDecimal currentDailyAverage) {
            this.level = level;
            this.message = message;
            this.recommendedDailyAmount = recommendedDailyAmount;
            this.maxDailySpend = maxDailySpend;
            this.currentDailyAverage = currentDailyAverage;
        }

        // Getters
        public SpendingLevel getLevel() { return level; }
        public String getMessage() { return message; }
        public BigDecimal getRecommendedDailyAmount() { return recommendedDailyAmount; }
        public BigDecimal getMaxDailySpend() { return maxDailySpend; }
        public BigDecimal getCurrentDailyAverage() { return currentDailyAverage; }
    }

    public static class MonthSurvivalPrediction {
        private final SurvivalStatus status;
        private final String message;
        private final long daysRemaining;
        private final BigDecimal totalRemainingBudget;
        private final BigDecimal totalProjectedSpending;
        private final List<String> recommendations;

        public MonthSurvivalPrediction(SurvivalStatus status, String message, long daysRemaining,
                                     BigDecimal totalRemainingBudget, BigDecimal totalProjectedSpending,
                                     List<String> recommendations) {
            this.status = status;
            this.message = message;
            this.daysRemaining = daysRemaining;
            this.totalRemainingBudget = totalRemainingBudget;
            this.totalProjectedSpending = totalProjectedSpending;
            this.recommendations = recommendations;
        }

        // Getters
        public SurvivalStatus getStatus() { return status; }
        public String getMessage() { return message; }
        public long getDaysRemaining() { return daysRemaining; }
        public BigDecimal getTotalRemainingBudget() { return totalRemainingBudget; }
        public BigDecimal getTotalProjectedSpending() { return totalProjectedSpending; }
        public List<String> getRecommendations() { return recommendations; }
    }

    public enum FinancialHealthStatus {
        CRITICAL, POOR, FAIR, GOOD, EXCELLENT
    }

    public enum SpendingLevel {
        EMERGENCY_STOP, CRITICAL, WARNING, NORMAL, COMFORTABLE
    }

    public enum SurvivalStatus {
        GAME_OVER, CRITICAL, RISKY, TIGHT_BUT_MANAGEABLE, COMFORTABLE
    }
}