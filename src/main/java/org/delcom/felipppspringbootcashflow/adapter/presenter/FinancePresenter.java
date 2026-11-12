package org.delcom.felipppspringbootcashflow.adapter.presenter;

import org.delcom.felipppspringbootcashflow.domain.entity.Balance;
import org.delcom.felipppspringbootcashflow.domain.entity.Transaction;
import org.delcom.felipppspringbootcashflow.domain.entity.TransactionType;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

/**
 * Presenter yang memformat data dari use case menjadi teks siap tampil.
 * Memisahkan logika tampilan (format uang, tanggal, pesan) dari entity,
 * use case, dan view sehingga format output bisa diubah tanpa menyentuh domain.
 */
public class FinancePresenter {

    private static final DateTimeFormatter DATE_TIME_FORMAT =
            DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    /** Memformat uang menjadi {@code Rp 1,000,000.00} (pemisah ribuan koma, dua desimal). */
    public String formatMoney(long amount) {
        String formatted = String.format(Locale.US, "Rp %,.2f", (double) Math.abs(amount));
        return amount < 0 ? "-" + formatted : formatted;
    }

    /** Label jenis transaksi: Pemasukan / Pengeluaran. */
    public String formatType(TransactionType type) {
        return type.label();
    }

    /** Memformat tanggal pencatatan. */
    public String formatCreatedAt(Transaction transaction) {
        return transaction.getCreatedAt().format(DATE_TIME_FORMAT);
    }

    /** Memformat satu transaksi: {@code [id] keterangan | Rp x | [Jenis]}. */
    public String format(Transaction transaction) {
        return String.format(
                "[%d] %s | %s | [%s]",
                transaction.getId(),
                transaction.getDescription(),
                formatMoney(transaction.getAmount()),
                formatType(transaction.getType())
        );
    }

    /** Memformat saldo: {@code Saldo: Rp x}. */
    public String formatBalance(Balance balance) {
        return "Saldo: " + formatMoney(balance.balance());
    }

    /**
     * Memformat daftar transaksi menjadi teks multi-baris.
     * Mengembalikan pesan kosong jika list tidak berisi data.
     */
    public String formatList(List<Transaction> transactions, String header, String emptyMessage) {
        StringBuilder builder = new StringBuilder(header);
        if (transactions.isEmpty()) {
            builder.append('\n').append(emptyMessage);
            return builder.toString();
        }
        String body = transactions.stream()
                .map(this::format)
                .collect(Collectors.joining("\n"));
        builder.append('\n').append(body);
        return builder.toString();
    }

    /** Memformat daftar semua transaksi. */
    public String showTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi:", "- Data transaksi belum tersedia!");
    }

    /** Memformat hasil pencarian berdasarkan kata kunci. */
    public String showSearchResults(List<Transaction> transactions, String keyword) {
        return formatList(transactions, "Hasil Pencarian: \"" + keyword + "\"", "- Transaksi tidak ditemukan!");
    }

    /** Memformat daftar transaksi yang sudah diurutkan. */
    public String showSortedTransactions(List<Transaction> transactions) {
        return formatList(transactions, "Daftar Transaksi (Terurut):", "- Data transaksi belum tersedia!");
    }

    /** Pesan sukses setelah menambah transaksi. */
    public String showAddSuccess(Transaction transaction) {
        return "Berhasil mencatat transaksi: " + format(transaction);
    }

    /** Pesan sukses setelah menghapus transaksi. */
    public String showRemoveSuccess() {
        return "Berhasil menghapus transaksi.";
    }

    /** Pesan gagal saat menghapus transaksi. */
    public String showRemoveFailed(int id) {
        return String.format("[!] Gagal menghapus transaksi dengan ID: %d.", id);
    }

    /** Pesan sukses setelah mengubah transaksi. */
    public String showUpdateSuccess() {
        return "Berhasil mengubah transaksi.";
    }

    /** Pesan gagal saat mengubah transaksi. */
    public String showUpdateFailed(int id) {
        return String.format("[!] Gagal mengubah transaksi dengan ID: %d.", id);
    }

    /** Pesan saat ID yang dimasukkan tidak valid. */
    public String showInvalidId() {
        return "[!] ID tidak valid!";
    }

    /** Pesan saat opsi pengurutan tidak valid. */
    public String showInvalidSortOption() {
        return "[!] Pilihan tidak valid!";
    }

    /** Pesan validasi umum dari domain. */
    public String showValidationError(String message) {
        return "[!] " + message;
    }
}
