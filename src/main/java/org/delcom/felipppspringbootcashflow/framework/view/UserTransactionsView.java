package org.delcom.felipppspringbootcashflow.framework.view;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.felipppspringbootcashflow.adapter.presenter.FinancePresenter;
import org.delcom.felipppspringbootcashflow.domain.entity.Balance;
import org.delcom.felipppspringbootcashflow.domain.entity.Transaction;
import org.delcom.felipppspringbootcashflow.domain.entity.User;
import org.delcom.felipppspringbootcashflow.domain.exception.EntityNotFoundException;
import org.delcom.felipppspringbootcashflow.framework.auth.AuthSession;
import org.delcom.felipppspringbootcashflow.usecase.FinanceUseCase;
import org.delcom.felipppspringbootcashflow.usecase.UserUseCase;

import java.util.List;

/**
 * Halaman admin: meninjau transaksi pengguna lain (read-only).
 */
@Route(value = "user-transactions", layout = MainLayout.class)
@PageTitle("Transaksi Pengguna")
public class UserTransactionsView extends VerticalLayout implements HasUrlParameter<Integer>, BeforeEnterObserver {

    private final UserUseCase userUseCase;
    private final FinanceUseCase financeUseCase;
    private final FinancePresenter presenter;
    private final Grid<Transaction> grid = new Grid<>(Transaction.class, false);
    private final H1 title = new H1("Transaksi Pengguna");
    private final Paragraph summary = new Paragraph();
    private final Paragraph empty = new Paragraph("Belum ada transaksi.");

    public UserTransactionsView(UserUseCase userUseCase, FinanceUseCase financeUseCase, FinancePresenter presenter) {
        this.userUseCase = userUseCase;
        this.financeUseCase = financeUseCase;
        this.presenter = presenter;

        setSizeFull();
        setPadding(true);
        setSpacing(false);
        getStyle().set("background", "#F3F4F6");

        title.getStyle().set("margin", "0");
        summary.getStyle().set("margin", "0.25rem 0 0.75rem").set("font-weight", "600").set("color", "#1F2937");
        empty.getStyle().set("margin", "0").set("color", "#4B5563");

        grid.addColumn(Transaction::getId).setHeader("ID").setAutoWidth(true).setFlexGrow(0);
        grid.addColumn(Transaction::getDescription).setHeader("Keterangan").setFlexGrow(1);
        grid.addColumn(transaction -> presenter.formatMoney(transaction.getAmount()))
                .setHeader("Jumlah").setAutoWidth(true);
        grid.addColumn(transaction -> presenter.formatType(transaction.getType()))
                .setHeader("Tipe").setAutoWidth(true);
        grid.addComponentColumn(transaction -> {
            if (transaction.getProofPath() == null || transaction.getProofPath().isBlank()) {
                return new Span("-");
            }
            return ImagePreviewDialog.clickableThumbnail(
                    "/uploads/" + transaction.getProofPath(),
                    "Bukti transaksi",
                    "48px"
            );
        }).setHeader("Bukti").setAutoWidth(true);
        grid.addColumn(presenter::formatCreatedAt).setHeader("Waktu").setAutoWidth(true);
        grid.setSizeFull();
        GridStyles.apply(grid);

        Div card = PageCard.wrap(title, summary, empty, grid);
        add(card);
        expand(card);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        User current = AuthSession.currentUser().orElse(null);
        if (current == null || !current.isAdmin()) {
            event.forwardTo(FinanceView.class);
        }
    }

    @Override
    public void setParameter(BeforeEvent event, Integer userId) {
        User current = AuthSession.currentUser().orElse(null);
        if (current == null || !current.isAdmin()) {
            return;
        }
        try {
            User owner = userUseCase.getUser(userId);
            title.setText("Transaksi milik " + owner.getFullName() + " (@" + owner.getUsername() + ")");
            List<Transaction> transactions = financeUseCase.getTransactionsByUser(userId);
            Balance balance = financeUseCase.summarize(transactions);
            summary.setText("Pemasukan: " + presenter.formatMoney(balance.totalIncome())
                    + "  |  Pengeluaran: " + presenter.formatMoney(balance.totalExpense())
                    + "  |  " + presenter.formatBalance(balance));
            grid.setItems(transactions);
            empty.setVisible(transactions.isEmpty());
            grid.setVisible(!transactions.isEmpty());
        } catch (EntityNotFoundException ex) {
            event.forwardTo(UsersView.class);
        }
    }
}
