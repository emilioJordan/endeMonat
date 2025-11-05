package com.endemonat.application.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Budget entity for managing user budgets.
 * Helps users track spending limits for different categories and periods.
 * 
 * @author Emilio und Leander
 */
@Document(collection = "budgets")
public class Budget {

    @Id
    private String id;

    @NotBlank(message = "Budget name cannot be blank")
    @Size(min = 2, max = 100, message = "Budget name must be between 2 and 100 characters")
    @Field("name")
    private String name;

    @Size(max = 300, message = "Description cannot exceed 300 characters")
    @Field("description")
    private String description;

    @NotNull(message = "Budget amount cannot be null")
    @Positive(message = "Budget amount must be positive")
    @Field("amount")
    private BigDecimal amount;

    @Field("spentAmount")
    private BigDecimal spentAmount;

    @NotNull(message = "Category ID cannot be null")
    @Field("categoryId")
    private String categoryId;

    @NotNull(message = "Budget period cannot be null")
    @Field("period")
    private BudgetPeriod period;

    @NotNull(message = "Start date cannot be null")
    @Field("startDate")
    private LocalDate startDate;

    @NotNull(message = "End date cannot be null")
    @Field("endDate")
    private LocalDate endDate;

    @Field("isActive")
    private boolean isActive;

    @Field("alertThreshold")
    private BigDecimal alertThreshold; // Percentage threshold for alerts (e.g., 0.8 for 80%)

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    // Constructors
    public Budget() {
        this.spentAmount = BigDecimal.ZERO;
        this.isActive = true;
        this.alertThreshold = new BigDecimal("0.8"); // Default 80% threshold
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Budget(String name, BigDecimal amount, String categoryId, BudgetPeriod period, LocalDate startDate, LocalDate endDate) {
        this();
        this.name = name;
        this.amount = amount;
        this.categoryId = categoryId;
        this.period = period;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getSpentAmount() {
        return spentAmount;
    }

    public void setSpentAmount(BigDecimal spentAmount) {
        this.spentAmount = spentAmount;
        this.updatedAt = LocalDateTime.now();
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
        this.updatedAt = LocalDateTime.now();
    }

    public BudgetPeriod getPeriod() {
        return period;
    }

    public void setPeriod(BudgetPeriod period) {
        this.period = period;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
        this.updatedAt = LocalDateTime.now();
    }

    public BigDecimal getAlertThreshold() {
        return alertThreshold;
    }

    public void setAlertThreshold(BigDecimal alertThreshold) {
        this.alertThreshold = alertThreshold;
        this.updatedAt = LocalDateTime.now();
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    // Helper methods
    public BigDecimal getRemainingAmount() {
        return amount.subtract(spentAmount);
    }

    public BigDecimal getSpentPercentage() {
        if (amount.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return spentAmount.divide(amount, 4, RoundingMode.HALF_UP);
    }

    public boolean isOverBudget() {
        return spentAmount.compareTo(amount) > 0;
    }

    public boolean isAlertThresholdReached() {
        return getSpentPercentage().compareTo(alertThreshold) >= 0;
    }

    @Override
    public String toString() {
        return "Budget{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", amount=" + amount +
                ", spentAmount=" + spentAmount +
                ", categoryId='" + categoryId + '\'' +
                ", period=" + period +
                ", startDate=" + startDate +
                ", endDate=" + endDate +
                ", isActive=" + isActive +
                '}';
    }

    /**
     * Enum for budget periods
     */
    public enum BudgetPeriod {
        WEEKLY,
        MONTHLY,
        QUARTERLY,
        YEARLY,
        CUSTOM
    }
}