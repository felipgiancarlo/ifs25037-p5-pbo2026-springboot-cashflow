package org.delcom.felipppspringbootcashflow.domain.entity;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SortOptionTest {

    private final Transaction small = new Transaction(1, 1, "kecil", 1_000, TransactionType.EXPENSE);
    private final Transaction big = new Transaction(2, 1, "besar", 9_000, TransactionType.INCOME);
    private final Transaction mid = new Transaction(3, 1, "sedang", 5_000, TransactionType.EXPENSE);

    private List<Integer> sortedIds(SortOption option) {
        List<Transaction> list = new ArrayList<>(List.of(small, big, mid));
        list.sort(option.comparator());
        return list.stream().map(Transaction::getId).toList();
    }

    @Test
    void amountAscending() {
        assertEquals(List.of(1, 3, 2), sortedIds(SortOption.AMOUNT_ASC));
    }

    @Test
    void amountDescending() {
        assertEquals(List.of(2, 3, 1), sortedIds(SortOption.AMOUNT_DESC));
    }

    @Test
    void incomeFirst() {
        assertEquals(2, sortedIds(SortOption.INCOME_FIRST).get(0));
    }

    @Test
    void expenseFirst() {
        assertEquals(2, sortedIds(SortOption.EXPENSE_FIRST).get(2));
    }

    @Test
    void hasAtLeastFourOptions() {
        assertEquals(4, SortOption.values().length);
    }
}
