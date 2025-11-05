package com.endemonat.application.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Transaction entity representing a financial transaction.
 * Used for managing user expenses and income.
 * 
 * @author Emilio und Leander
 */
@Document(collection = "transactions")
public class Transaction {

    @Id
    private String id;

    @NotBlank(message = "Description cannot be blank")
    @Field("description")
    private String description;

    @NotNull(message = "Amount cannot be null")
    @Positive(message = "Amount must be positive")
    @Field("amount")
    private BigDecimal amount;

    @NotNull(message = "Transaction type cannot be null")
    @Field("type")
    private TransactionType type;

    @NotNull(message = "Category ID cannot be null")
    @Field("categoryId")
    private String categoryId;

    @Field("date")
    private LocalDateTime date;

    @Field("createdAt")
    private LocalDateTime createdAt;

    @Field("updatedAt")
    private LocalDateTime updatedAt;

    // Constructors
    public Transaction() {
        this.date = LocalDateTime.now();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public Transaction(String description, BigDecimal amount, TransactionType type, String categoryId) {
        this();
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.categoryId = categoryId;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
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

    @Override
    public String toString() {
        return "Transaction{" +
                "id='" + id + '\'' +
                ", description='" + description + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", categoryId='" + categoryId + '\'' +
                ", date=" + date +
                '}';
    }

    /**
     * Enum for transaction types
     */
    public enum TransactionType {
        EXPENSE,
        INCOME
    }
}