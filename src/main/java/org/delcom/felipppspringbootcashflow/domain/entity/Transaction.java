package org.delcom.felipppspringbootcashflow.domain.entity;

import java.time.LocalDateTime;

/**
 * Entity inti yang merepresentasikan satu transaksi keuangan milik satu pengguna.
 * Berada di layer domain — tidak bergantung pada layer lain dan bebas dari
 * urusan tampilan (format uang) maupun penyimpanan.
 */
public class Transaction {
    /** ID unik transaksi, tidak boleh diubah setelah dibuat. */
    private final int id;

    /** Pemilik transaksi. */
    private final int userId;

    /** Keterangan transaksi. */
    private String description;

    /** Jumlah uang (selalu positif; arah ditentukan oleh {@link #type}). */
    private long amount;

    /** Jenis transaksi: pemasukan atau pengeluaran. */
    private TransactionType type;

    /** Path relatif bukti transaksi (opsional). */
    private String proofPath;

    /** Waktu transaksi dicatat. */
    private final LocalDateTime createdAt;

    /** Membuat transaksi baru tanpa bukti, dicatat pada waktu sekarang. */
    public Transaction(int id, int userId, String description, long amount, TransactionType type) {
        this(id, userId, description, amount, type, null, LocalDateTime.now());
    }

    /** Membuat transaksi dengan bukti dan waktu pencatatan yang sudah ditentukan. */
    public Transaction(
            int id,
            int userId,
            String description,
            long amount,
            TransactionType type,
            String proofPath,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.userId = userId;
        this.description = description;
        this.amount = amount;
        this.type = type;
        this.proofPath = proofPath;
        this.createdAt = createdAt;
    }

    public int getId() {
        return id;
    }

    public int getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public long getAmount() {
        return amount;
    }

    public TransactionType getType() {
        return type;
    }

    public String getProofPath() {
        return proofPath;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public boolean isIncome() {
        return type == TransactionType.INCOME;
    }

    /**
     * Dampak transaksi terhadap saldo: positif untuk pemasukan, negatif untuk pengeluaran.
     */
    public long signedAmount() {
        return isIncome() ? amount : -amount;
    }

    /** Mengubah keterangan transaksi. */
    public void changeDescription(String description) {
        this.description = description;
    }

    /** Mengubah jumlah transaksi. */
    public void changeAmount(long amount) {
        this.amount = amount;
    }

    /** Mengubah jenis transaksi. */
    public void changeType(TransactionType type) {
        this.type = type;
    }

    /** Mengubah path bukti transaksi. */
    public void changeProofPath(String proofPath) {
        this.proofPath = proofPath;
    }
}
