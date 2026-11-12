package org.delcom.ifs25037springbootcashflow.adapter.presenter;

import org.delcom.ifs25037springbootcashflow.domain.entity.Balance;
import org.delcom.ifs25037springbootcashflow.domain.entity.Transaction;
import org.delcom.ifs25037springbootcashflow.domain.entity.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class FinancePresenterTest {

    private final FinancePresenter presenter = new FinancePresenter();
    private final Transaction income = new Transaction(
            1, 1, "Gaji", 1_000_000, TransactionType.INCOME, null, LocalDateTime.of(2026, 3, 4, 5, 6));
    private final Transaction expense = new Transaction(
            2, 1, "Makan", 25_000, TransactionType.EXPENSE, null, LocalDateTime.of(2026, 3, 4, 5, 6));

    @Test
    void formatsMoneyWithThousandsSeparatorAndTwoDecimals() {
        assertEquals("Rp 1,000,000.00", presenter.formatMoney(1_000_000));
        assertEquals("Rp 0.00", presenter.formatMoney(0));
        assertEquals("Rp 4,975,000.00", presenter.formatMoney(4_975_000));
        assertEquals("-Rp 1,500.00", presenter.formatMoney(-1_500));
    }

    @Test
    void formatsRowAndBalanceLikeModuleReference() {
        assertEquals("[1] Gaji | Rp 1,000,000.00 | [Pemasukan]", presenter.format(income));
        assertEquals("[2] Makan | Rp 25,000.00 | [Pengeluaran]", presenter.format(expense));
        assertEquals("Saldo: Rp 4,975,000.00", presenter.formatBalance(new Balance(2, 5_000_000, 25_000)));
        assertEquals("Pemasukan", presenter.formatType(TransactionType.INCOME));
        assertEquals("04/03/2026 05:06", presenter.formatCreatedAt(income));
    }

    @Test
    void formatsLists() {
        String all = presenter.showTransactions(List.of(income, expense));
        assertTrue(all.startsWith("Daftar Transaksi:"));
        assertTrue(all.contains("[1] Gaji"));
        assertTrue(all.contains("[2] Makan"));

        assertEquals("Daftar Transaksi:\n- Data transaksi belum tersedia!", presenter.showTransactions(List.of()));
        assertEquals("Hasil Pencarian: \"x\"\n- Transaksi tidak ditemukan!", presenter.showSearchResults(List.of(), "x"));
        assertTrue(presenter.showSearchResults(List.of(income), "gaji").contains("[1] Gaji"));
        assertTrue(presenter.showSortedTransactions(List.of(income)).startsWith("Daftar Transaksi (Terurut):"));
        assertTrue(presenter.showSortedTransactions(List.of()).contains("belum tersedia"));
    }

    @Test
    void formatsMessages() {
        assertTrue(presenter.showAddSuccess(income).startsWith("Berhasil mencatat transaksi: [1] Gaji"));
        assertEquals("Berhasil menghapus transaksi.", presenter.showRemoveSuccess());
        assertEquals("[!] Gagal menghapus transaksi dengan ID: 5.", presenter.showRemoveFailed(5));
        assertEquals("Berhasil mengubah transaksi.", presenter.showUpdateSuccess());
        assertEquals("[!] Gagal mengubah transaksi dengan ID: 5.", presenter.showUpdateFailed(5));
        assertEquals("[!] ID tidak valid!", presenter.showInvalidId());
        assertEquals("[!] Pilihan tidak valid!", presenter.showInvalidSortOption());
        assertEquals("[!] oops", presenter.showValidationError("oops"));
    }
}
