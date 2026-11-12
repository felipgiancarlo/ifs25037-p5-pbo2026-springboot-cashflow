package org.delcom.ifs25037springbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionTest {

    @Test
    void shortConstructorSetsDefaults() {
        Transaction transaction = new Transaction(1, 7, "Gaji", 5_000_000, TransactionType.INCOME);

        assertEquals(1, transaction.getId());
        assertEquals(7, transaction.getUserId());
        assertEquals("Gaji", transaction.getDescription());
        assertEquals(5_000_000, transaction.getAmount());
        assertEquals(TransactionType.INCOME, transaction.getType());
        assertNull(transaction.getProofPath());
        assertNotNull(transaction.getCreatedAt());
        assertTrue(transaction.isIncome());
    }

    @Test
    void fullConstructorKeepsGivenValues() {
        LocalDateTime time = LocalDateTime.of(2026, 1, 2, 3, 4);
        Transaction transaction = new Transaction(2, 1, "Makan", 25_000, TransactionType.EXPENSE, "proofs/a.png", time);

        assertEquals("proofs/a.png", transaction.getProofPath());
        assertEquals(time, transaction.getCreatedAt());
        assertFalse(transaction.isIncome());
    }

    @Test
    void signedAmountDependsOnType() {
        assertEquals(1000, new Transaction(1, 1, "a", 1000, TransactionType.INCOME).signedAmount());
        assertEquals(-1000, new Transaction(2, 1, "b", 1000, TransactionType.EXPENSE).signedAmount());
    }

    @Test
    void changeMethodsMutateFields() {
        Transaction transaction = new Transaction(1, 1, "a", 1000, TransactionType.INCOME);

        transaction.changeDescription("b");
        transaction.changeAmount(2000);
        transaction.changeType(TransactionType.EXPENSE);
        transaction.changeProofPath("proofs/x.png");

        assertEquals("b", transaction.getDescription());
        assertEquals(2000, transaction.getAmount());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals("proofs/x.png", transaction.getProofPath());
    }

    @Test
    void transactionTypeHasIndonesianLabel() {
        assertEquals("Pemasukan", TransactionType.INCOME.label());
        assertEquals("Pengeluaran", TransactionType.EXPENSE.label());
    }

    @Test
    void balanceIsIncomeMinusExpense() {
        Balance balance = new Balance(3, 10_000, 4_000);
        assertEquals(6_000, balance.balance());
        assertEquals(-1_000, new Balance(1, 0, 1_000).balance());
    }
}
