package org.delcom.felipppspringbootcashflow.usecase;

import org.delcom.felipppspringbootcashflow.domain.entity.Balance;
import org.delcom.felipppspringbootcashflow.domain.entity.SortOption;
import org.delcom.felipppspringbootcashflow.domain.entity.Transaction;
import org.delcom.felipppspringbootcashflow.domain.entity.TransactionType;
import org.delcom.felipppspringbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.felipppspringbootcashflow.domain.exception.ValidationException;
import org.delcom.felipppspringbootcashflow.domain.repository.ITransactionRepository;

import java.util.List;
import java.util.Optional;

/**
 * Use case yang menangani logika bisnis pencatatan keuangan.
 * Tidak melakukan I/O — hanya memproses data dan melempar checked exception
 * domain jika operasi tidak valid atau data tidak ditemukan.
 */
public class FinanceUseCase {
    /** Port repository yang di-inject dari luar (Dependency Inversion). */
    private final ITransactionRepository transactionRepository;

    public FinanceUseCase(ITransactionRepository transactionRepository) {
        this.transactionRepository = transactionRepository;
    }

    /** Mengambil semua transaksi milik satu pengguna. */
    public List<Transaction> getTransactionsByUser(int userId) {
        return transactionRepository.findByUserId(userId);
    }

    /** Mengambil semua transaksi yang tersedia. */
    public List<Transaction> getAllTransactions() {
        return transactionRepository.findAll();
    }

    /** Mengubah teks jumlah dari input menjadi angka; menolak non-numerik dan non-positif. */
    public long parseAmount(String rawAmount) throws ValidationException {
        if (rawAmount == null || rawAmount.trim().isEmpty()) {
            throw new ValidationException("Jumlah tidak boleh kosong");
        }
        long amount;
        try {
            amount = Long.parseLong(rawAmount.trim());
        } catch (NumberFormatException ex) {
            throw new ValidationException("Jumlah harus berupa angka bulat");
        }
        validateAmount(amount);
        return amount;
    }

    /** Menambahkan pemasukan baru setelah validasi. */
    public Transaction addIncome(int userId, String description, long amount) throws ValidationException {
        return addTransaction(userId, description, amount, TransactionType.INCOME);
    }

    /** Menambahkan pengeluaran baru setelah validasi. */
    public Transaction addExpense(int userId, String description, long amount) throws ValidationException {
        return addTransaction(userId, description, amount, TransactionType.EXPENSE);
    }

    /** Menambahkan transaksi dengan jenis tertentu setelah validasi. */
    public Transaction addTransaction(int userId, String description, long amount, TransactionType type)
            throws ValidationException {
        validateDescription(description);
        validateAmount(amount);
        if (type == null) {
            throw new ValidationException("Jenis transaksi tidak boleh kosong");
        }
        return transactionRepository.save(userId, description.trim(), amount, type);
    }

    /** Menghapus transaksi berdasarkan ID; melempar exception jika tidak ditemukan. */
    public void removeTransaction(int id) throws EntityNotFoundException, ValidationException {
        validateId(id);
        boolean deleted = transactionRepository.deleteById(id);
        if (!deleted) {
            throw new EntityNotFoundException(id);
        }
    }

    /** Menghapus transaksi milik pengguna tertentu. */
    public void removeTransactionForUser(int userId, int id) throws EntityNotFoundException, ValidationException {
        Transaction transaction = requireOwnedTransaction(userId, id);
        removeTransaction(transaction.getId());
    }

    /**
     * Mengubah keterangan, jumlah, jenis, dan/atau bukti transaksi.
     * Parameter {@code null} berarti field tersebut tidak diubah
     * (kecuali {@code clearProof=true} untuk menghapus bukti).
     */
    public void updateTransaction(
            int id,
            String description,
            Long amount,
            TransactionType type,
            String proofPath,
            boolean clearProof
    ) throws EntityNotFoundException, ValidationException {
        validateId(id);
        Optional<Transaction> found = transactionRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        Transaction transaction = found.get();
        applyUpdates(transaction, description, amount, type, proofPath, clearProof);
        transactionRepository.update(transaction);
    }

    /** Mengubah transaksi milik pengguna tertentu. */
    public void updateTransactionForUser(
            int userId,
            int id,
            String description,
            Long amount,
            TransactionType type,
            String proofPath,
            boolean clearProof
    ) throws EntityNotFoundException, ValidationException {
        Transaction transaction = requireOwnedTransaction(userId, id);
        applyUpdates(transaction, description, amount, type, proofPath, clearProof);
        transactionRepository.update(transaction);
    }

    /** Mencari transaksi pengguna yang keterangannya mengandung kata kunci (case-insensitive). */
    public List<Transaction> searchTransactions(int userId, String keyword) {
        String lowerKeyword = keyword == null ? "" : keyword.toLowerCase();
        return transactionRepository.findByUserId(userId).stream()
                .filter(transaction -> transaction.getDescription().toLowerCase().contains(lowerKeyword))
                .toList();
    }

    /** Mengurutkan transaksi pengguna sesuai kriteria {@link SortOption} yang dipilih. */
    public List<Transaction> sortTransactions(int userId, SortOption option) {
        return transactionRepository.findByUserId(userId).stream()
                .sorted(option.comparator())
                .toList();
    }

    /** Menghitung ringkasan keuangan (jumlah, pemasukan, pengeluaran, saldo) milik satu pengguna. */
    public Balance getBalance(int userId) {
        return summarize(transactionRepository.findByUserId(userId));
    }

    /** Menghitung ringkasan keuangan dari daftar transaksi yang diberikan. */
    public Balance summarize(List<Transaction> transactions) {
        long income = 0;
        long expense = 0;
        for (Transaction transaction : transactions) {
            if (transaction.isIncome()) {
                income += transaction.getAmount();
            } else {
                expense += transaction.getAmount();
            }
        }
        return new Balance(transactions.size(), income, expense);
    }

    private Transaction requireOwnedTransaction(int userId, int id)
            throws EntityNotFoundException, ValidationException {
        validateId(id);
        Optional<Transaction> found = transactionRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException(id);
        }
        Transaction transaction = found.get();
        if (transaction.getUserId() != userId) {
            throw new ValidationException("Transaksi tidak milik pengguna ini");
        }
        return transaction;
    }

    private void applyUpdates(
            Transaction transaction,
            String description,
            Long amount,
            TransactionType type,
            String proofPath,
            boolean clearProof
    ) throws ValidationException {
        if (description != null) {
            validateDescription(description);
        }
        if (amount != null) {
            validateAmount(amount);
        }
        if (description != null) {
            transaction.changeDescription(description.trim());
        }
        if (amount != null) {
            transaction.changeAmount(amount);
        }
        if (type != null) {
            transaction.changeType(type);
        }
        if (clearProof) {
            transaction.changeProofPath(null);
        } else if (proofPath != null) {
            transaction.changeProofPath(proofPath);
        }
    }

    private static void validateDescription(String description) throws ValidationException {
        if (description == null || description.trim().isEmpty()) {
            throw new ValidationException("Keterangan tidak boleh kosong");
        }
    }

    private static void validateAmount(long amount) throws ValidationException {
        if (amount <= 0) {
            throw new ValidationException("Jumlah harus lebih dari 0");
        }
    }

    private static void validateId(int id) throws ValidationException {
        if (id <= 0) {
            throw new ValidationException("ID tidak valid");
        }
    }
}
