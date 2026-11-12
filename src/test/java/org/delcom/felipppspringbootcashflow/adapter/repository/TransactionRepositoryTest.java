package org.delcom.felipppspringbootcashflow.adapter.repository;

import org.delcom.felipppspringbootcashflow.domain.entity.Transaction;
import org.delcom.felipppspringbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class TransactionRepositoryTest {

    private TransactionRepository repository;

    @BeforeEach
    void setUp() {
        repository = new TransactionRepository();
    }

    @Test
    void saveAssignsIncrementalIdsAndScopesByUser() {
        Transaction first = repository.save(1, "Satu", 100, TransactionType.INCOME);
        Transaction second = repository.save(1, "Dua", 200, TransactionType.EXPENSE);

        assertEquals(1, first.getId());
        assertEquals(2, second.getId());
        assertEquals(2, repository.findAll().size());
        assertEquals(2, repository.findByUserId(1).size());
        assertTrue(repository.findByUserId(2).isEmpty());
    }

    @Test
    void findByIdDeleteUpdateAndFindBy() {
        Transaction transaction = repository.save(1, "Hapus saya", 100, TransactionType.EXPENSE);

        assertTrue(repository.findById(transaction.getId()).isPresent());
        assertTrue(repository.findById(99).isEmpty());
        assertEquals(1, repository.findBy(t -> t.getAmount() == 100).size());

        transaction.changeAmount(500);
        repository.update(transaction);
        assertEquals(500, repository.findById(transaction.getId()).orElseThrow().getAmount());

        assertTrue(repository.deleteById(transaction.getId()));
        assertFalse(repository.deleteById(transaction.getId()));
        assertTrue(repository.findAll().isEmpty());
    }
}
