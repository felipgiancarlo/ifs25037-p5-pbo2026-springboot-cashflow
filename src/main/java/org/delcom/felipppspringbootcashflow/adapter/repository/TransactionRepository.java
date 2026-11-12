package org.delcom.felipppspringbootcashflow.adapter.repository;

import org.delcom.felipppspringbootcashflow.domain.entity.Transaction;
import org.delcom.felipppspringbootcashflow.domain.entity.TransactionType;
import org.delcom.felipppspringbootcashflow.domain.repository.ITransactionRepository;

import java.util.List;

/**
 * Implementasi repository transaksi menggunakan basis generic {@link InMemoryRepository}.
 * Dipakai pada unit test; produksi memakai {@link JdbcTransactionRepository}.
 */
public class TransactionRepository extends InMemoryRepository<Transaction, Integer> implements ITransactionRepository {
    /** Penghitung ID otomatis, bertambah setiap kali transaksi baru disimpan. */
    private int idCounter = 0;

    public TransactionRepository() {
        super(Transaction::getId);
    }

    @Override
    public Transaction save(int userId, String description, long amount, TransactionType type) {
        return save(new Transaction(nextId(), userId, description, amount, type));
    }

    @Override
    public List<Transaction> findByUserId(int userId) {
        return findBy(transaction -> transaction.getUserId() == userId);
    }

    /** Menghasilkan ID unik berikutnya. */
    private int nextId() {
        return ++idCounter;
    }
}
