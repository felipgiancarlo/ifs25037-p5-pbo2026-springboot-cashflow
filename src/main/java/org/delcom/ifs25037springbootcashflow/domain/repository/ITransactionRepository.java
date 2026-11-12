package org.delcom.ifs25037springbootcashflow.domain.repository;

import org.delcom.ifs25037springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25037springbootcashflow.domain.entity.TransactionType;

import java.util.List;

/**
 * Port spesifik transaksi yang memperluas generic {@link IRepository}.
 * Menambahkan method pembuatan transaksi baru dengan penomoran ID otomatis.
 */
public interface ITransactionRepository extends IRepository<Transaction, Integer> {
    /**
     * Menyimpan transaksi baru untuk pengguna tertentu (tanpa bukti).
     */
    Transaction save(int userId, String description, long amount, TransactionType type);

    /** Mengambil semua transaksi milik satu pengguna. */
    List<Transaction> findByUserId(int userId);
}
