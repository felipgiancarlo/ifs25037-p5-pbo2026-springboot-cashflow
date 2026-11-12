package org.delcom.ifs25037springbootcashflow.domain.entity;

/**
 * Jenis transaksi keuangan: pemasukan menambah saldo, pengeluaran mengurangi saldo.
 */
public enum TransactionType {
    INCOME("Pemasukan"),
    EXPENSE("Pengeluaran");

    private final String label;

    TransactionType(String label) {
        this.label = label;
    }

    /** Label tampilan dalam Bahasa Indonesia. */
    public String label() {
        return label;
    }
}
