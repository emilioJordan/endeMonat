package com.endemonat.application.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

/**
 * Home Controller for basic application information.
 * Provides a welcome page and API information.
 * 
 * @author Emilio und Leander
 */
@RestController
public class HomeController {

    @GetMapping("/")
    public Map<String, Object> home() {
        Map<String, Object> response = new HashMap<>();
        response.put("application", "EndeMonat - Expense and Budget Management System");
        response.put("version", "1.0.0");
        response.put("team", "Emilio und Leander");
        response.put("status", "Running");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("transactions", "/api/transactions");
        endpoints.put("smart-transactions", "/api/transactions/smart");
        endpoints.put("categories", "/api/categories");
        endpoints.put("budgets", "/api/budgets");
        endpoints.put("end-of-month-analysis", "/api/analysis/end-of-month");
        endpoints.put("survival-prediction", "/api/analysis/survival-prediction");
        endpoints.put("category-intelligence", "/api/analysis/category-intelligence");
        endpoints.put("health", "/actuator/health");
        
        response.put("endpoints", endpoints);
        
        return response;
    }

    @GetMapping("/api")
    public Map<String, Object> apiInfo() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "EndeMonat API v1.0.0");
        response.put("documentation", "See README.md for API documentation");
        
        Map<String, String> endpoints = new HashMap<>();
        endpoints.put("GET /api/transactions", "Get all transactions");
        endpoints.put("POST /api/transactions/smart", "Create transaction with AI validation");
        endpoints.put("POST /api/transactions", "Create new transaction");
        endpoints.put("GET /api/categories", "Get all categories");
        endpoints.put("POST /api/categories", "Create new category");
        endpoints.put("GET /api/budgets", "Get all budgets");
        endpoints.put("POST /api/budgets", "Create new budget");
        endpoints.put("GET /api/analysis/end-of-month", "🎯 Ende Monat Analysis");
        endpoints.put("GET /api/analysis/survival-prediction", "🔮 Month Survival Prediction");
        endpoints.put("GET /api/analysis/category-intelligence", "🧠 AI Category Intelligence");
        endpoints.put("GET /api/analysis/spending-recommendations", "💡 Smart Spending Tips");
        
        response.put("availableEndpoints", endpoints);
        
        return response;
    }
}