package com.endemonat.application.service;package com.endemonat.application.service;package com.endemonat.application.service;



import com.endemonat.application.entity.Category;

import com.endemonat.application.entity.Transaction;

import com.endemonat.application.repository.CategoryRepository;import com.endemonat.application.entity.Category;import com.endemonat.application.entity.Category;

import com.endemonat.application.repository.TransactionRepository;

import org.springframework.stereotype.Service;import com.endemonat.application.entity.Transaction;import com.endemonat.application.entity.Transaction;



import java.math.BigDecimal;import com.endemonat.application.repository.CategoryRepository;import com.endemonat.application.repository.CategoryRepository;

import java.time.LocalDateTime;

import java.util.*;import com.endemonat.application.repository.TransactionRepository;import com.endemonat.application.repository.TransactionRepository;

import java.util.stream.Collectors;

import org.springframework.stereotype.Service;import org.springframework.stereotype.Service;

/**

 * Smart Category Intelligence Service - TDD Implementation

 * @author Emilio und Leander

 */import java.math.BigDecimal;import java.math.BigDecimal;

@Service

public class SmartCategoryService {import java.time.LocalDateTime;import java.math.RoundingMode;



    private final CategoryRepository categoryRepository;import java.util.*;import java.time.LocalDate;

    private final TransactionRepository transactionRepository;

import java.util.stream.Collectors;import java.time.LocalDateTime;

    public SmartCategoryService(CategoryRepository categoryRepository,

                              TransactionRepository transactionRepository) {import java.util.*;

        this.categoryRepository = categoryRepository;

        this.transactionRepository = transactionRepository;/**import java.util.stream.Collectors;

    }

 * Smart Category Intelligence Service

    public String predictCategory(Transaction transaction) {

        if (transaction == null || transaction.getDescription() == null) { * Provides TDD-developed AI-like category prediction and analysis/**

            return null;

        } *  * Smart Category Intelligence Service



        List<Category> availableCategories = categoryRepository.findByIsActiveTrue(); * @author Emilio und Leander * Provides complex analysis and recommendations for spending categories

        if (availableCategories.isEmpty()) {

            return null; */ * 

        }

@Service * @author Emilio und Leander

        String[] keywords = extractKeywords(transaction.getDescription());

        Map<String, Integer> categoryMatches = new HashMap<>();public class SmartCategoryService { */



        for (String keyword : keywords) {@Service

            List<Transaction> historicalTransactions = 

                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);    private final CategoryService categoryService;public class SmartCategoryService {

            

            for (Transaction historical : historicalTransactions) {    private final TransactionService transactionService;

                if (historical.getCategoryId() != null) {

                    categoryMatches.merge(historical.getCategoryId(), 1, Integer::sum);    private final CategoryRepository categoryRepository;    private final CategoryService categoryService;

                }

            }    private final TransactionRepository transactionRepository;    private final TransactionService transactionService;

        }

    private final CategoryRepository categoryRepository;

        return categoryMatches.entrySet().stream()

            .max(Map.Entry.comparingByValue())    public SmartCategoryService(CategoryService categoryService,     private final TransactionRepository transactionRepository;

            .map(Map.Entry::getKey)

            .orElse(null);                              TransactionService transactionService,

    }

                              CategoryRepository categoryRepository,    public SmartCategoryService(CategoryService categoryService, 

    public CategoryPrediction predictCategoryWithConfidence(Transaction transaction) {

        String categoryId = predictCategory(transaction);                              TransactionRepository transactionRepository) {                              TransactionService transactionService,

        if (categoryId == null) {

            return null;        this.categoryService = categoryService;                              CategoryRepository categoryRepository,

        }

        this.transactionService = transactionService;                              TransactionRepository transactionRepository) {

        String[] keywords = extractKeywords(transaction.getDescription());

        int totalMatches = 0;        this.categoryRepository = categoryRepository;        this.categoryService = categoryService;

        

        for (String keyword : keywords) {        this.transactionRepository = transactionRepository;        this.transactionService = transactionService;

            List<Transaction> historicalTransactions = 

                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);    }        this.categoryRepository = categoryRepository;

            totalMatches += historicalTransactions.size();

        }        this.transactionRepository = transactionRepository;



        double confidence = Math.min(1.0, totalMatches / 10.0);    // TDD IMPLEMENTATION: AI-basierte Kategorie-Vorhersage    }

        return new CategoryPrediction(categoryId, confidence);

    }



    public List<CategoryPrediction> predictTopCategories(Transaction transaction, int limit) {    /**    // TDD IMPLEMENTATION: AI-basierte Kategorie-Vorhersage

        if (transaction == null || transaction.getDescription() == null) {

            return new ArrayList<>();     * Predicts category for a transaction based on historical data

        }

     */    /**

        String[] keywords = extractKeywords(transaction.getDescription());

        Map<String, Integer> categoryMatches = new HashMap<>();    public String predictCategory(Transaction transaction) {     * Predicts category for a transaction based on historical data



        for (String keyword : keywords) {        if (transaction == null || transaction.getDescription() == null) {     */

            List<Transaction> historicalTransactions = 

                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);            return null;    public String predictCategory(Transaction transaction) {

            

            for (Transaction historical : historicalTransactions) {        }        if (transaction == null || transaction.getDescription() == null) {

                if (historical.getCategoryId() != null) {

                    categoryMatches.merge(historical.getCategoryId(), 1, Integer::sum);            return null;

                }

            }        List<Category> availableCategories = categoryRepository.findByIsActiveTrue();        }

        }

        if (availableCategories.isEmpty()) {

        return categoryMatches.entrySet().stream()

            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())            return null;        List<Category> availableCategories = categoryRepository.findByIsActiveTrue();

            .limit(limit)

            .map(entry -> {        }        if (availableCategories.isEmpty()) {

                double confidence = Math.min(1.0, entry.getValue() / 10.0);

                return new CategoryPrediction(entry.getKey(), confidence);            return null;

            })

            .collect(Collectors.toList());        // Extract keywords from description        }

    }

        String[] keywords = extractKeywords(transaction.getDescription());

    public Map<String, SpendingPattern> analyzeSpendingPatterns(String categoryId) {

        LocalDateTime endDate = LocalDateTime.now();        Map<String, Integer> categoryMatches = new HashMap<>();        // Extract keywords from description

        LocalDateTime startDate = endDate.minusMonths(3);

                String[] keywords = extractKeywords(transaction.getDescription());

        List<Transaction> transactions = transactionRepository

            .findByCategoryIdAndDateBetween(categoryId, startDate, endDate);        // Search for historical transactions with similar keywords        Map<String, Integer> categoryMatches = new HashMap<>();

        

        Map<String, SpendingPattern> patterns = new HashMap<>();        for (String keyword : keywords) {

        

        SpendingPattern weeklyPattern = analyzeWeeklyPattern(transactions);            List<Transaction> historicalTransactions =         // Search for historical transactions with similar keywords

        if (weeklyPattern != null) {

            patterns.put("WEEKLY_RECURRING", weeklyPattern);                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);        for (String keyword : keywords) {

        }

                                List<Transaction> historicalTransactions = 

        return patterns;

    }            for (Transaction historical : historicalTransactions) {                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);



    public List<CategoryRecommendation> recommendCategoryOptimization(String categoryId) {                if (historical.getCategoryId() != null) {            

        List<CategoryRecommendation> recommendations = new ArrayList<>();

                            categoryMatches.merge(historical.getCategoryId(), 1, Integer::sum);            for (Transaction historical : historicalTransactions) {

        List<Transaction> transactions = transactionRepository.findByCategoryId(categoryId);

        if (transactions.isEmpty()) {                }                if (historical.getCategoryId() != null) {

            return recommendations;

        }            }                    categoryMatches.merge(historical.getCategoryId(), 1, Integer::sum);

        

        BigDecimal totalSpent = transactions.stream()        }                }

            .map(Transaction::getAmount)

            .reduce(BigDecimal.ZERO, BigDecimal::add);            }

        

        BigDecimal averageTransaction = totalSpent        // Return category with most matches        }

            .divide(BigDecimal.valueOf(transactions.size()), 2, java.math.RoundingMode.HALF_UP);

                return categoryMatches.entrySet().stream()

        if (averageTransaction.compareTo(new BigDecimal("100")) > 0) {

            recommendations.add(new CategoryRecommendation(            .max(Map.Entry.comparingByValue())        // Return category with most matches

                RecommendationType.INCREASE_BUDGET,

                "Hohe durchschnittliche Ausgaben erkannt. Budget erhöhen erwägen.",            .map(Map.Entry::getKey)        return categoryMatches.entrySet().stream()

                averageTransaction

            ));            .orElse(null);            .max(Map.Entry.comparingByValue())

        }

            }            .map(Map.Entry::getKey)

        return recommendations;

    }            .orElse(null);



    private String[] extractKeywords(String description) {    /**    }

        if (description == null || description.isEmpty()) {

            return new String[0];     * Enhanced prediction with confidence score

        }

        return description.toLowerCase().split("[\\s\\-_.,;:]+");     */    /**

    }

    public CategoryPrediction predictCategoryWithConfidence(Transaction transaction) {     * Enhanced prediction with confidence score

    private SpendingPattern analyzeWeeklyPattern(List<Transaction> transactions) {

        Map<Integer, Integer> weeklyFrequency = new HashMap<>();        String categoryId = predictCategory(transaction);     */

        

        for (Transaction transaction : transactions) {        if (categoryId == null) {    public CategoryPrediction predictCategoryWithConfidence(Transaction transaction) {

            int dayOfWeek = transaction.getDate().getDayOfWeek().getValue();

            weeklyFrequency.merge(dayOfWeek, 1, Integer::sum);            return null;        String categoryId = predictCategory(transaction);

        }

                }        if (categoryId == null) {

        int maxFrequency = weeklyFrequency.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        if (maxFrequency > transactions.size() * 0.7) {            return null;

            return new SpendingPattern(PatternType.WEEKLY, maxFrequency, "Wöchentliches Muster erkannt");

        }        // Calculate confidence based on historical data volume        }

        

        return null;        String[] keywords = extractKeywords(transaction.getDescription());

    }

        int totalMatches = 0;        // Calculate confidence based on historical data volume

    // Inner Classes

    public static class CategoryPrediction {                String[] keywords = extractKeywords(transaction.getDescription());

        private final String categoryId;

        private final double confidenceScore;        for (String keyword : keywords) {        int totalMatches = 0;

        

        public CategoryPrediction(String categoryId, double confidenceScore) {            List<Transaction> historicalTransactions =         

            this.categoryId = categoryId;

            this.confidenceScore = confidenceScore;                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);        for (String keyword : keywords) {

        }

                    totalMatches += historicalTransactions.size();            List<Transaction> historicalTransactions = 

        public String getCategoryId() { return categoryId; }

        public double getConfidenceScore() { return confidenceScore; }        }                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);

    }

            totalMatches += historicalTransactions.size();

    public static class SpendingPattern {

        private final PatternType type;        double confidence = Math.min(1.0, totalMatches / 10.0); // Max confidence at 10+ matches        }

        private final int frequency;

        private final String description;        

        

        public SpendingPattern(PatternType type, int frequency, String description) {        return new CategoryPrediction(categoryId, confidence);        double confidence = Math.min(1.0, totalMatches / 10.0); // Max confidence at 10+ matches

            this.type = type;

            this.frequency = frequency;    }        

            this.description = description;

        }        return new CategoryPrediction(categoryId, confidence);

        

        public PatternType getType() { return type; }    /**    }

        public int getFrequency() { return frequency; }

        public String getDescription() { return description; }     * Get top category predictions

    }

     */    /**

    public enum PatternType {

        WEEKLY, MONTHLY, DAILY, IRREGULAR    public List<CategoryPrediction> predictTopCategories(Transaction transaction, int limit) {     * Get top category predictions

    }

        if (transaction == null || transaction.getDescription() == null) {     */

    public static class CategoryRecommendation {

        private final RecommendationType type;            return new ArrayList<>();    public List<CategoryPrediction> predictTopCategories(Transaction transaction, int limit) {

        private final String message;

        private final BigDecimal relevantAmount;        }        if (transaction == null || transaction.getDescription() == null) {

        

        public CategoryRecommendation(RecommendationType type, String message, BigDecimal relevantAmount) {            return new ArrayList<>();

            this.type = type;

            this.message = message;        String[] keywords = extractKeywords(transaction.getDescription());        }

            this.relevantAmount = relevantAmount;

        }        Map<String, Integer> categoryMatches = new HashMap<>();

        

        public RecommendationType getType() { return type; }        String[] keywords = extractKeywords(transaction.getDescription());

        public String getMessage() { return message; }

        public BigDecimal getRelevantAmount() { return relevantAmount; }        for (String keyword : keywords) {        Map<String, Integer> categoryMatches = new HashMap<>();

    }

            List<Transaction> historicalTransactions = 

    public enum RecommendationType {

        INCREASE_BUDGET, DECREASE_BUDGET, CONSOLIDATE_PURCHASES, SPLIT_CATEGORY                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);        for (String keyword : keywords) {

    }

}                        List<Transaction> historicalTransactions = 

            for (Transaction historical : historicalTransactions) {                transactionRepository.findByDescriptionContainingIgnoreCase(keyword);

                if (historical.getCategoryId() != null) {            

                    categoryMatches.merge(historical.getCategoryId(), 1, Integer::sum);            for (Transaction historical : historicalTransactions) {

                }                if (historical.getCategoryId() != null) {

            }                    categoryMatches.merge(historical.getCategoryId(), 1, Integer::sum);

        }                }

            }

        return categoryMatches.entrySet().stream()        }

            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())

            .limit(limit)        return categoryMatches.entrySet().stream()

            .map(entry -> {            .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())

                double confidence = Math.min(1.0, entry.getValue() / 10.0);            .limit(limit)

                return new CategoryPrediction(entry.getKey(), confidence);            .map(entry -> {

            })                double confidence = Math.min(1.0, entry.getValue() / 10.0);

            .collect(Collectors.toList());                return new CategoryPrediction(entry.getKey(), confidence);

    }            })

            .collect(Collectors.toList());

    // TDD IMPLEMENTATION: Ausgaben-Pattern Erkennung    }



    /**    // TDD IMPLEMENTATION: Ausgaben-Pattern Erkennung

     * Analyze spending patterns for a category

     */    /**

    public Map<String, SpendingPattern> analyzeSpendingPatterns(String categoryId) {     * Analyze spending patterns for a category

        LocalDateTime endDate = LocalDateTime.now();     */

        LocalDateTime startDate = endDate.minusMonths(3); // 3 Monate Analyse    public Map<String, SpendingPattern> analyzeSpendingPatterns(String categoryId) {

                LocalDateTime endDate = LocalDateTime.now();

        List<Transaction> transactions = transactionRepository        LocalDateTime startDate = endDate.minusMonths(3); // 3 Monate Analyse

            .findByCategoryIdAndDateBetween(categoryId, startDate, endDate);        

                List<Transaction> transactions = transactionRepository

        Map<String, SpendingPattern> patterns = new HashMap<>();            .findByCategoryIdAndDateBetween(categoryId, startDate, endDate);

                

        // Weekly pattern analysis        Map<String, SpendingPattern> patterns = new HashMap<>();

        SpendingPattern weeklyPattern = analyzeWeeklyPattern(transactions);        

        if (weeklyPattern != null) {        // Weekly pattern analysis

            patterns.put("WEEKLY_RECURRING", weeklyPattern);        SpendingPattern weeklyPattern = analyzeWeeklyPattern(transactions);

        }        if (weeklyPattern != null) {

                    patterns.put("WEEKLY_RECURRING", weeklyPattern);

        // Monthly pattern analysis          }

        SpendingPattern monthlyPattern = analyzeMonthlyPattern(transactions);        

        if (monthlyPattern != null) {        // Monthly pattern analysis  

            patterns.put("MONTHLY_RECURRING", monthlyPattern);        SpendingPattern monthlyPattern = analyzeMonthlyPattern(transactions);

        }        if (monthlyPattern != null) {

                    patterns.put("MONTHLY_RECURRING", monthlyPattern);

        return patterns;        }

    }        

        return patterns;

    // TDD IMPLEMENTATION: Optimierungs-Empfehlungen    }



    /**    // TDD IMPLEMENTATION: Optimierungs-Empfehlungen

     * Recommend category optimizations

     */    /**

    public List<CategoryRecommendation> recommendCategoryOptimization(String categoryId) {     * Recommend category optimizations

        List<CategoryRecommendation> recommendations = new ArrayList<>();     */

            public List<CategoryRecommendation> recommendCategoryOptimization(String categoryId) {

        List<Transaction> transactions = transactionRepository.findByCategoryId(categoryId);        List<CategoryRecommendation> recommendations = new ArrayList<>();

        if (transactions.isEmpty()) {        

            return recommendations;        List<Transaction> transactions = transactionRepository.findByCategoryId(categoryId);

        }        if (transactions.isEmpty()) {

                    return recommendations;

        BigDecimal totalSpent = transactions.stream()        }

            .map(Transaction::getAmount)        

            .reduce(BigDecimal.ZERO, BigDecimal::add);        BigDecimal totalSpent = transactions.stream()

                    .map(Transaction::getAmount)

        BigDecimal averageTransaction = totalSpent            .reduce(BigDecimal.ZERO, BigDecimal::add);

            .divide(BigDecimal.valueOf(transactions.size()), 2, java.math.RoundingMode.HALF_UP);        

                BigDecimal averageTransaction = totalSpent

        // High spending recommendation            .divide(BigDecimal.valueOf(transactions.size()), 2, RoundingMode.HALF_UP);

        if (averageTransaction.compareTo(new BigDecimal("100")) > 0) {        

            recommendations.add(new CategoryRecommendation(        // High spending recommendation

                RecommendationType.INCREASE_BUDGET,        if (averageTransaction.compareTo(new BigDecimal("100")) > 0) {

                "Hohe durchschnittliche Ausgaben erkannt. Budget erhöhen erwägen.",            recommendations.add(new CategoryRecommendation(

                averageTransaction                RecommendationType.INCREASE_BUDGET,

            ));                "Hohe durchschnittliche Ausgaben erkannt. Budget erhöhen erwägen.",

        }                averageTransaction

                    ));

        // Frequent small transactions        }

        if (transactions.size() > 20 && averageTransaction.compareTo(new BigDecimal("10")) < 0) {        

            recommendations.add(new CategoryRecommendation(        // Frequent small transactions

                RecommendationType.CONSOLIDATE_PURCHASES,        if (transactions.size() > 20 && averageTransaction.compareTo(new BigDecimal("10")) < 0) {

                "Viele kleine Transaktionen. Einkäufe bündeln könnte sparen.",            recommendations.add(new CategoryRecommendation(

                averageTransaction                RecommendationType.CONSOLIDATE_PURCHASES,

            ));                "Viele kleine Transaktionen. Einkäufe bündeln könnte sparen.",

        }                averageTransaction

                    ));

        return recommendations;        }

    }        

        return recommendations;

    // HELPER METHODS    }



    private String[] extractKeywords(String description) {    // HELPER METHODS

        if (description == null || description.isEmpty()) {

            return new String[0];    private String[] extractKeywords(String description) {

        }        return description.toLowerCase()

        return description.toLowerCase()            .split("[\\s\\-_.,;:]+")

            .split("[\\s\\-_.,;:]+");            .length > 0 ? description.toLowerCase().split("[\\s\\-_.,;:]+") : new String[]{description};

    }    }



    private SpendingPattern analyzeWeeklyPattern(List<Transaction> transactions) {    private SpendingPattern analyzeWeeklyPattern(List<Transaction> transactions) {

        // Simplistic weekly pattern detection        // Simplistic weekly pattern detection

        Map<Integer, Integer> weeklyFrequency = new HashMap<>();        Map<Integer, Integer> weeklyFrequency = new HashMap<>();

                

        for (Transaction transaction : transactions) {        for (Transaction transaction : transactions) {

            int dayOfWeek = transaction.getDate().getDayOfWeek().getValue();            int dayOfWeek = transaction.getDate().getDayOfWeek().getValue();

            weeklyFrequency.merge(dayOfWeek, 1, Integer::sum);            weeklyFrequency.merge(dayOfWeek, 1, Integer::sum);

        }        }

                

        // If 70%+ transactions happen on same day of week        // If 70%+ transactions happen on same day of week

        int maxFrequency = weeklyFrequency.values().stream().mapToInt(Integer::intValue).max().orElse(0);        int maxFrequency = weeklyFrequency.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        if (maxFrequency > transactions.size() * 0.7) {        if (maxFrequency > transactions.size() * 0.7) {

            return new SpendingPattern(PatternType.WEEKLY, maxFrequency, "Wöchentliches Muster erkannt");            return new SpendingPattern(PatternType.WEEKLY, maxFrequency, "Wöchentliches Muster erkannt");

        }        }

                

        return null;        return null;

    }    }



    private SpendingPattern analyzeMonthlyPattern(List<Transaction> transactions) {    private SpendingPattern analyzeMonthlyPattern(List<Transaction> transactions) {

        // Simplistic monthly pattern detection        // Simplistic monthly pattern detection

        Map<Integer, Integer> monthlyFrequency = new HashMap<>();        Map<Integer, Integer> monthlyFrequency = new HashMap<>();

                

        for (Transaction transaction : transactions) {        for (Transaction transaction : transactions) {

            int dayOfMonth = transaction.getDate().getDayOfMonth();            int dayOfMonth = transaction.getDate().getDayOfMonth();

            int weekOfMonth = (dayOfMonth - 1) / 7 + 1; // Group by week of month            int weekOfMonth = (dayOfMonth - 1) / 7 + 1; // Group by week of month

            monthlyFrequency.merge(weekOfMonth, 1, Integer::sum);            monthlyFrequency.merge(weekOfMonth, 1, Integer::sum);

        }        }

                

        int maxFrequency = monthlyFrequency.values().stream().mapToInt(Integer::intValue).max().orElse(0);        int maxFrequency = monthlyFrequency.values().stream().mapToInt(Integer::intValue).max().orElse(0);

        if (maxFrequency > transactions.size() * 0.6) {        if (maxFrequency > transactions.size() * 0.6) {

            return new SpendingPattern(PatternType.MONTHLY, maxFrequency, "Monatliches Muster erkannt");            return new SpendingPattern(PatternType.MONTHLY, maxFrequency, "Monatliches Muster erkannt");

        }        }

                

        return null;        return null;

    }    }



    // INNER CLASSES für TDD    // INNER CLASSES für TDD



    public static class CategoryPrediction {    public static class CategoryPrediction {

        private final String categoryId;        private final String categoryId;

        private final double confidenceScore;        private final double confidenceScore;

                

        public CategoryPrediction(String categoryId, double confidenceScore) {        public CategoryPrediction(String categoryId, double confidenceScore) {

            this.categoryId = categoryId;            this.categoryId = categoryId;

            this.confidenceScore = confidenceScore;            this.confidenceScore = confidenceScore;

        }        }

                

        public String getCategoryId() { return categoryId; }        public String getCategoryId() { return categoryId; }

        public double getConfidenceScore() { return confidenceScore; }        public double getConfidenceScore() { return confidenceScore; }

    }    }



    public static class SpendingPattern {    public static class SpendingPattern {

        private final PatternType type;        private final PatternType type;

        private final int frequency;        private final int frequency;

        private final String description;        private final String description;

                

        public SpendingPattern(PatternType type, int frequency, String description) {        public SpendingPattern(PatternType type, int frequency, String description) {

            this.type = type;            this.type = type;

            this.frequency = frequency;            this.frequency = frequency;

            this.description = description;            this.description = description;

        }        }

                

        public PatternType getType() { return type; }        public PatternType getType() { return type; }

        public int getFrequency() { return frequency; }        public int getFrequency() { return frequency; }

        public String getDescription() { return description; }        public String getDescription() { return description; }

    }    }



    public enum PatternType {    public enum PatternType {

        WEEKLY, MONTHLY, DAILY, IRREGULAR        WEEKLY, MONTHLY, DAILY, IRREGULAR

    }    }



    public static class CategoryRecommendation {    public static class CategoryRecommendation {

        private final RecommendationType type;        private final RecommendationType type;

        private final String message;        private final String message;

        private final BigDecimal relevantAmount;        private final BigDecimal relevantAmount;

                

        public CategoryRecommendation(RecommendationType type, String message, BigDecimal relevantAmount) {        public CategoryRecommendation(RecommendationType type, String message, BigDecimal relevantAmount) {

            this.type = type;            this.type = type;

            this.message = message;            this.message = message;

            this.relevantAmount = relevantAmount;            this.relevantAmount = relevantAmount;

        }        }

                

        public RecommendationType getType() { return type; }        public RecommendationType getType() { return type; }

        public String getMessage() { return message; }        public String getMessage() { return message; }

        public BigDecimal getRelevantAmount() { return relevantAmount; }        public BigDecimal getRelevantAmount() { return relevantAmount; }

    }    }



    public enum RecommendationType {    public enum RecommendationType {

        INCREASE_BUDGET, DECREASE_BUDGET, CONSOLIDATE_PURCHASES, SPLIT_CATEGORY        INCREASE_BUDGET, DECREASE_BUDGET, CONSOLIDATE_PURCHASES, SPLIT_CATEGORY,

    }        URGENT_REDUCE, MODERATE_REDUCE, CAN_INCREASE, MAINTAIN

}    }

    public enum CategoryRiskLevel {
        SAFE, WARNING, CRITICAL, EMERGENCY
    }

    public static class SpendingHealthScore {
        private final double score;
        private final String description;
        private final CategoryRiskLevel riskLevel;
        
        public SpendingHealthScore(double score, String description, CategoryRiskLevel riskLevel) {
            this.score = score;
            this.description = description;
            this.riskLevel = riskLevel;
        }
        
        public double getScore() { return score; }
        public String getDescription() { return description; }
        public CategoryRiskLevel getRiskLevel() { return riskLevel; }
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
    private CategoryRiskLevel determineRiskLevel(BigDecimal spendingVelocity, BigDecimal remainingSafeBudget, long daysRemaining) {
        // Risk based on spending velocity
        if (spendingVelocity.compareTo(new BigDecimal("2.0")) > 0) {
            return CategoryRiskLevel.EMERGENCY; // Spending 2x+ the safe rate
        } else if (spendingVelocity.compareTo(new BigDecimal("1.5")) > 0) {
            return CategoryRiskLevel.CRITICAL; // Spending 1.5x+ the safe rate
        } else if (spendingVelocity.compareTo(new BigDecimal("1.2")) > 0) {
            return CategoryRiskLevel.WARNING; // Spending 1.2x+ the safe rate
        } else {
            return CategoryRiskLevel.SAFE; // Spending within safe limits
        }
    }
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


}