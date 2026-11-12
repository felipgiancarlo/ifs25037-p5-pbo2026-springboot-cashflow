package org.delcom.felipppspringbootcashflow.usecase;

import org.delcom.felipppspringbootcashflow.adapter.repository.TransactionRepository;
import org.delcom.felipppspringbootcashflow.domain.entity.Balance;
import org.delcom.felipppspringbootcashflow.domain.entity.SortOption;
import org.delcom.felipppspringbootcashflow.domain.entity.Transaction;
import org.delcom.felipppspringbootcashflow.domain.entity.TransactionType;
import org.delcom.felipppspringbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.felipppspringbootcashflow.domain.exception.ValidationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinanceUseCaseTest {

    private static final int USER = 1;
    private static final int OTHER = 2;

    private FinanceUseCase useCase;

    @BeforeEach
    void setUp() {
        useCase = new FinanceUseCase(new TransactionRepository());
    }

    @Test
    void addIncomeAndExpenseTrimDescription() throws Exception {
        Transaction income = useCase.addIncome(USER, "  Gaji  ", 5_000_000);
        Transaction expense = useCase.addExpense(USER, "Makan", 25_000);

        assertEquals("Gaji", income.getDescription());
        assertEquals(TransactionType.INCOME, income.getType());
        assertEquals(TransactionType.EXPENSE, expense.getType());
        assertEquals(2, useCase.getTransactionsByUser(USER).size());
        assertEquals(2, useCase.getAllTransactions().size());
    }

    @Test
    void addValidation() {
        assertThrows(ValidationException.class, () -> useCase.addIncome(USER, "  ", 100));
        assertThrows(ValidationException.class, () -> useCase.addIncome(USER, null, 100));
        assertThrows(ValidationException.class, () -> useCase.addIncome(USER, "x", 0));
        assertThrows(ValidationException.class, () -> useCase.addExpense(USER, "x", -5));
        assertThrows(ValidationException.class, () -> useCase.addTransaction(USER, "x", 5, null));
    }

    @Test
    void parseAmountValidatesInput() throws Exception {
        assertEquals(1500, useCase.parseAmount(" 1500 "));
        assertThrows(ValidationException.class, () -> useCase.parseAmount(null));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("   "));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("abc"));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("12.5"));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("0"));
        assertThrows(ValidationException.class, () -> useCase.parseAmount("-10"));
    }

    @Test
    void removeTransaction() throws Exception {
        Transaction transaction = useCase.addIncome(USER, "Hapus", 100);

        useCase.removeTransaction(transaction.getId());
        assertTrue(useCase.getTransactionsByUser(USER).isEmpty());
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransaction(transaction.getId()));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(0));
        assertThrows(ValidationException.class, () -> useCase.removeTransaction(-1));
    }

    @Test
    void removeForUserChecksOwnership() throws Exception {
        Transaction mine = useCase.addIncome(USER, "Milik saya", 100);

        assertThrows(ValidationException.class, () -> useCase.removeTransactionForUser(OTHER, mine.getId()));
        assertThrows(EntityNotFoundException.class, () -> useCase.removeTransactionForUser(USER, 999));
        assertThrows(ValidationException.class, () -> useCase.removeTransactionForUser(USER, 0));

        useCase.removeTransactionForUser(USER, mine.getId());
        assertTrue(useCase.getTransactionsByUser(USER).isEmpty());
    }

    @Test
    void updateTransactionChangesOnlyProvidedFields() throws Exception {
        Transaction transaction = useCase.addIncome(USER, "Lama", 100);

        useCase.updateTransaction(transaction.getId(), "  Baru ", 200L, TransactionType.EXPENSE, "proofs/a.png", false);
        assertEquals("Baru", transaction.getDescription());
        assertEquals(200, transaction.getAmount());
        assertEquals(TransactionType.EXPENSE, transaction.getType());
        assertEquals("proofs/a.png", transaction.getProofPath());

        useCase.updateTransaction(transaction.getId(), null, null, null, null, false);
        assertEquals("Baru", transaction.getDescription());
        assertEquals("proofs/a.png", transaction.getProofPath());

        useCase.updateTransaction(transaction.getId(), null, null, null, "ignored.png", true);
        assertNull(transaction.getProofPath());
    }

    @Test
    void updateTransactionFailures() throws Exception {
        Transaction transaction = useCase.addIncome(USER, "Satu", 100);

        assertThrows(EntityNotFoundException.class, () -> useCase.updateTransaction(999, "x", null, null, null, false));
        assertThrows(ValidationException.class, () -> useCase.updateTransaction(0, "x", null, null, null, false));
        assertThrows(ValidationException.class,
                () -> useCase.updateTransaction(transaction.getId(), " ", null, null, null, false));
        assertThrows(ValidationException.class,
                () -> useCase.updateTransaction(transaction.getId(), null, 0L, null, null, false));
        assertEquals("Satu", transaction.getDescription());
        assertEquals(100, transaction.getAmount());
    }

    @Test
    void updateForUserChecksOwnership() throws Exception {
        Transaction mine = useCase.addIncome(USER, "Saya", 100);

        useCase.updateTransactionForUser(USER, mine.getId(), "Saya2", 300L, null, null, false);
        assertEquals("Saya2", mine.getDescription());
        assertEquals(300, mine.getAmount());

        assertThrows(ValidationException.class,
                () -> useCase.updateTransactionForUser(OTHER, mine.getId(), "x", null, null, null, false));
        assertThrows(EntityNotFoundException.class,
                () -> useCase.updateTransactionForUser(USER, 999, "x", null, null, null, false));
    }

    @Test
    void searchIsCaseInsensitiveAndScopedPerUser() throws Exception {
        useCase.addIncome(USER, "Gaji Bulanan", 100);
        useCase.addExpense(USER, "Beli kopi", 10);
        useCase.addExpense(OTHER, "Gaji orang lain", 10);

        assertEquals(1, useCase.searchTransactions(USER, "GAJI").size());
        assertEquals(2, useCase.searchTransactions(USER, "").size());
        assertEquals(2, useCase.searchTransactions(USER, null).size());
        assertTrue(useCase.searchTransactions(USER, "tidakada").isEmpty());
    }

    @Test
    void sortUsesSortOptionComparator() throws Exception {
        useCase.addIncome(USER, "A", 500);
        useCase.addExpense(USER, "B", 100);
        useCase.addIncome(USER, "C", 900);

        List<Transaction> asc = useCase.sortTransactions(USER, SortOption.AMOUNT_ASC);
        assertEquals(100, asc.get(0).getAmount());
        List<Transaction> desc = useCase.sortTransactions(USER, SortOption.AMOUNT_DESC);
        assertEquals(900, desc.get(0).getAmount());
        assertEquals(TransactionType.INCOME, useCase.sortTransactions(USER, SortOption.INCOME_FIRST).get(0).getType());
        assertEquals(TransactionType.EXPENSE, useCase.sortTransactions(USER, SortOption.EXPENSE_FIRST).get(0).getType());
    }

    @Test
    void balanceAddsIncomeAndSubtractsExpense() throws Exception {
        useCase.addIncome(USER, "Gaji", 5_000_000);
        useCase.addExpense(USER, "Sewa", 25_000);
        useCase.addExpense(OTHER, "Bukan saya", 999);

        Balance balance = useCase.getBalance(USER);
        assertEquals(2, balance.count());
        assertEquals(5_000_000, balance.totalIncome());
        assertEquals(25_000, balance.totalExpense());
        assertEquals(4_975_000, balance.balance());

        Transaction extra = useCase.addExpense(USER, "Lagi", 1_000);
        assertEquals(4_974_000, useCase.getBalance(USER).balance());
        useCase.removeTransaction(extra.getId());
        assertEquals(4_975_000, useCase.getBalance(USER).balance());
        assertEquals(0, useCase.getBalance(99).balance());
    }
}
