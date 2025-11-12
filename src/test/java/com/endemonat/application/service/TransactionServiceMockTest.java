package com.endemonat.application.service;

import com.endemonat.application.entity.Transaction;
import com.endemonat.application.repository.TransactionRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Unit-Test 1: Mock anstatt DB
 * Testet TransactionService ohne DB-Anbindung mit Mocking
 * 
 * Bewertungskriterien:
 * - Code-Unit ohne DB-Anbindung komplett testen
 * - Mocking korrekt einsetzen
 * - Alle relevanten Pfade und Verhaltensweisen testen
 * - Positive, Negative und Boundary-Value Tests
 * 
 * @author Emilio und Leander
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceMockTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private BudgetService budgetService;

    @InjectMocks
    private TransactionService transactionService;

    private Transaction testTransaction;
    private List<Transaction> testTransactions;

    @BeforeEach
    void setUp() {
        // Test-Daten vorbereiten
        testTransaction = new Transaction();
        testTransaction.setId("test-id-123");
        testTransaction.setDescription("Test Ausgabe");
        testTransaction.setAmount(new BigDecimal("50.00"));
        testTransaction.setType(Transaction.TransactionType.EXPENSE);
        testTransaction.setCategoryId("category-1");
        testTransaction.setDate(LocalDateTime.now());
        testTransaction.setCreatedAt(LocalDateTime.now());
        testTransaction.setUpdatedAt(LocalDateTime.now());

        Transaction transaction2 = new Transaction();
        transaction2.setId("test-id-456");
        transaction2.setDescription("Test Einnahme");
        transaction2.setAmount(new BigDecimal("100.00"));
        transaction2.setType(Transaction.TransactionType.INCOME);
        transaction2.setCategoryId("category-2");

        testTransactions = Arrays.asList(testTransaction, transaction2);
    }

    // POSITIVE TESTS

    @Test
    void createTransaction_ValidTransaction_ShouldReturnSavedTransaction() {
        // Arrange
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        assertEquals(testTransaction.getDescription(), result.getDescription());
        assertEquals(testTransaction.getAmount(), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void getAllTransactions_ShouldReturnAllTransactions() {
        // Arrange
        when(transactionRepository.findAllByOrderByDateDesc()).thenReturn(testTransactions);

        // Act
        List<Transaction> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertEquals(2, result.size());
        verify(transactionRepository, times(1)).findAllByOrderByDateDesc();
    }

    @Test
    void getTransactionById_ExistingId_ShouldReturnTransaction() {
        // Arrange
        when(transactionRepository.findById("test-id-123")).thenReturn(Optional.of(testTransaction));

        // Act
        Optional<Transaction> result = transactionService.getTransactionById("test-id-123");

        // Assert
        assertTrue(result.isPresent());
        assertEquals(testTransaction.getId(), result.get().getId());
        verify(transactionRepository, times(1)).findById("test-id-123");
    }

    @Test
    void updateTransaction_ExistingTransaction_ShouldUpdateAndReturn() {
        // Arrange
        Transaction updatedTransaction = new Transaction();
        updatedTransaction.setDescription("Updated Description");
        updatedTransaction.setAmount(new BigDecimal("75.00"));
        updatedTransaction.setType(Transaction.TransactionType.EXPENSE);
        updatedTransaction.setCategoryId("category-1");

        when(transactionRepository.findById("test-id-123")).thenReturn(Optional.of(testTransaction));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Optional<Transaction> result = transactionService.updateTransaction("test-id-123", updatedTransaction);

        // Assert
        assertTrue(result.isPresent());
        verify(transactionRepository, times(1)).findById("test-id-123");
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_ExistingId_ShouldReturnTrue() {
        // Arrange
        when(transactionRepository.existsById("test-id-123")).thenReturn(true);
        doNothing().when(transactionRepository).deleteById("test-id-123");

        // Act
        boolean result = transactionService.deleteTransaction("test-id-123");

        // Assert
        assertTrue(result);
        verify(transactionRepository, times(1)).existsById("test-id-123");
        verify(transactionRepository, times(1)).deleteById("test-id-123");
    }

    // NEGATIVE TESTS

    @Test
    void getTransactionById_NonExistingId_ShouldReturnEmpty() {
        // Arrange
        when(transactionRepository.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act
        Optional<Transaction> result = transactionService.getTransactionById("non-existing-id");

        // Assert
        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById("non-existing-id");
    }

    @Test
    void updateTransaction_NonExistingId_ShouldReturnEmpty() {
        // Arrange
        Transaction updatedTransaction = new Transaction();
        when(transactionRepository.findById("non-existing-id")).thenReturn(Optional.empty());

        // Act
        Optional<Transaction> result = transactionService.updateTransaction("non-existing-id", updatedTransaction);

        // Assert
        assertFalse(result.isPresent());
        verify(transactionRepository, times(1)).findById("non-existing-id");
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void deleteTransaction_NonExistingId_ShouldReturnFalse() {
        // Arrange
        when(transactionRepository.existsById("non-existing-id")).thenReturn(false);

        // Act
        boolean result = transactionService.deleteTransaction("non-existing-id");

        // Assert
        assertFalse(result);
        verify(transactionRepository, times(1)).existsById("non-existing-id");
        verify(transactionRepository, never()).deleteById(anyString());
    }

    // BOUNDARY VALUE TESTS

    @Test
    void createTransaction_ZeroAmount_ShouldHandleCorrectly() {
        // Arrange
        testTransaction.setAmount(BigDecimal.ZERO);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        assertEquals(BigDecimal.ZERO, result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_VeryLargeAmount_ShouldHandleCorrectly() {
        // Arrange
        testTransaction.setAmount(new BigDecimal("999999999.99"));
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        assertEquals(new BigDecimal("999999999.99"), result.getAmount());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    @Test
    void createTransaction_NullDate_ShouldSetCurrentDate() {
        // Arrange
        testTransaction.setDate(null);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(testTransaction);

        // Act
        Transaction result = transactionService.createTransaction(testTransaction);

        // Assert
        assertNotNull(result);
        assertNotNull(result.getDate());
        verify(transactionRepository, times(1)).save(any(Transaction.class));
    }

    // COMPLEX BUSINESS LOGIC TESTS

    @Test
    void getTransactionsByCategory_ShouldFilterCorrectly() {
        // Arrange
        List<Transaction> categoryTransactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByCategoryIdOrderByDateDesc("category-1"))
            .thenReturn(categoryTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByCategory("category-1");

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals("category-1", result.get(0).getCategoryId());
        verify(transactionRepository, times(1)).findByCategoryIdOrderByDateDesc("category-1");
    }

    @Test
    void getTransactionsByType_ExpenseType_ShouldReturnOnlyExpenses() {
        // Arrange
        List<Transaction> expenseTransactions = Arrays.asList(testTransaction);
        when(transactionRepository.findByType(Transaction.TransactionType.EXPENSE))
            .thenReturn(expenseTransactions);

        // Act
        List<Transaction> result = transactionService.getTransactionsByType(Transaction.TransactionType.EXPENSE);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(Transaction.TransactionType.EXPENSE, result.get(0).getType());
        verify(transactionRepository, times(1)).findByType(Transaction.TransactionType.EXPENSE);
    }

    @Test
    void calculateTotalExpensesByCategory_ShouldCalculateCorrectly() {
        // Arrange
        LocalDateTime startDate = LocalDateTime.now().minusDays(30);
        LocalDateTime endDate = LocalDateTime.now();
        
        List<Transaction> expenses = Arrays.asList(
            createTransactionWithAmount(new BigDecimal("50.00")),
            createTransactionWithAmount(new BigDecimal("25.00")),
            createTransactionWithAmount(new BigDecimal("75.00"))
        );
        
        when(transactionRepository.findByCategoryDateRangeAndType(
            "category-1", startDate, endDate, Transaction.TransactionType.EXPENSE))
            .thenReturn(expenses);

        // Act
        BigDecimal result = transactionService.calculateTotalExpensesByCategory("category-1", startDate, endDate);

        // Assert
        assertEquals(new BigDecimal("150.00"), result);
        verify(transactionRepository, times(1)).findByCategoryDateRangeAndType(
            "category-1", startDate, endDate, Transaction.TransactionType.EXPENSE);
    }

    // EDGE CASES

    @Test
    void getAllTransactions_EmptyRepository_ShouldReturnEmptyList() {
        // Arrange
        when(transactionRepository.findAllByOrderByDateDesc()).thenReturn(Arrays.asList());

        // Act
        List<Transaction> result = transactionService.getAllTransactions();

        // Assert
        assertNotNull(result);
        assertTrue(result.isEmpty());
        verify(transactionRepository, times(1)).findAllByOrderByDateDesc();
    }

    @Test
    void createTransaction_NullTransaction_ShouldHandleGracefully() {
        // Arrange - No mocking needed, service should handle null directly

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> {
            transactionService.createTransaction(null);
        });
    }

    // Helper Methods
    private Transaction createTransactionWithAmount(BigDecimal amount) {
        Transaction transaction = new Transaction();
        transaction.setAmount(amount);
        transaction.setType(Transaction.TransactionType.EXPENSE);
        return transaction;
    }
}