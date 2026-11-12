package org.delcom.ifs25037springbootcashflow.domain.entity;

/**
 * Ringkasan keuangan: jumlah transaksi, total pemasukan, dan total pengeluaran.
 *
 * @param count        jumlah transaksi
 * @param totalIncome  total seluruh pemasukan
 * @param totalExpense total seluruh pengeluaran
 */
public record Balance(int count, long totalIncome, long totalExpense) {

    /** Saldo = total pemasukan dikurangi total pengeluaran (bisa negatif). */
    public long balance() {
        return totalIncome - totalExpense;
    }
}
