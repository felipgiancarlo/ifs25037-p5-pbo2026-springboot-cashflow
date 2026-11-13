package org.delcom.ifs25037springbootcashflow.framework.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.progressbar.ProgressBar;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import org.delcom.ifs25037springbootcashflow.domain.entity.User;
import org.delcom.ifs25037springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25037springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25037springbootcashflow.framework.auth.AuthSession;
import org.delcom.ifs25037springbootcashflow.usecase.AuthUseCase;

/**
 * Halaman login.
 *
 * Elemen form memakai &lt;input&gt; HTML biasa (bukan TextField/PasswordField)
 * supaya id langsung ada di DOM saat halaman pertama kali dirender:
 *   #login-username, #login-password, #login-submit
 */
@Route("login")
@PageTitle("Login")
public class LoginView extends VerticalLayout {

    public LoginView(AuthUseCase authUseCase, IUserRepository userRepository) {
        if (AuthSession.currentUser(userRepository).isPresent()) {
            UI.getCurrent().navigate(FinanceView.class);
            return;
        }

        setSizeFull();
        // Landmark utama (Axe: landmark-one-main / region)
        getElement().setAttribute("role", "main");
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        getStyle().set("background", "#F3F4F6");

        // ---------- Input username ----------
        Input username = new Input();
        username.setId("login-username");
        username.setType("text");
        username.setPlaceholder("Username");
        username.getElement().setAttribute("name", "username");
        username.getElement().setAttribute("autocomplete", "username");
        username.setValueChangeMode(ValueChangeMode.EAGER);
        styleInput(username);

        NativeLabel usernameLabel = new NativeLabel("Username");
        usernameLabel.setFor(username);
        styleLabel(usernameLabel);

        // ---------- Input password ----------
        Input password = new Input();
        password.setId("login-password");
        password.setType("password");
        password.setPlaceholder("Password");
        password.getElement().setAttribute("name", "password");
        password.getElement().setAttribute("autocomplete", "current-password");
        password.setValueChangeMode(ValueChangeMode.EAGER);
        styleInput(password);

        NativeLabel passwordLabel = new NativeLabel("Password");
        passwordLabel.setFor(password);
        styleLabel(passwordLabel);

        ProgressBar loadingBar = new ProgressBar();
        loadingBar.setIndeterminate(true);
        loadingBar.setVisible(false);
        loadingBar.setWidthFull();

        // ---------- Tombol submit ----------
        Button loginButton = new Button("Masuk");
        loginButton.setId("login-submit");

        Runnable submit = () -> {
            loadingBar.setVisible(true);
            loginButton.setEnabled(false);
            try {
                User user = authUseCase.login(username.getValue(), password.getValue());
                AuthSession.login(user);
                UI.getCurrent().navigate(FinanceView.class);
            } catch (ValidationException ex) {
                loadingBar.setVisible(false);
                loginButton.setEnabled(true);
                Notification n = Notification.show(ex.getMessage(), 4000, Notification.Position.BOTTOM_START);
                n.addThemeVariants(NotificationVariant.LUMO_ERROR);
            }
        };

        loginButton.addClickListener(event -> submit.run());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        loginButton.setWidthFull();
        loginButton.getStyle().set("margin-top", "0.85rem");
        loginButton.getStyle().set("cursor", "pointer");

        // Enter pada kolom input memicu submit.
        // Event "input" (EAGER) sudah mengirim nilai ke server sebelum keyup Enter.
        username.getElement().addEventListener("keyup", event -> submit.run())
                .setFilter("event.key === 'Enter'");
        password.getElement().addEventListener("keyup", event -> submit.run())
                .setFilter("event.key === 'Enter'");

        H1 title = new H1("Masuk");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Gunakan kredensial akun kamu untuk masuk");
        subtitle.getStyle().set("margin", "0 0 0.15rem 0").set("color", "#4B5563");

        RouterLink registerLink = new RouterLink("Belum punya akun? Daftar", RegisterView.class);
        registerLink.getStyle()
                .set("align-self", "flex-end")
                .set("text-align", "right")
                .set("cursor", "pointer");

        VerticalLayout form = new VerticalLayout(
                title,
                subtitle,
                usernameLabel,
                username,
                passwordLabel,
                password,
                loadingBar,
                loginButton,
                registerLink);
        form.setPadding(false);
        form.setSpacing(false);
        form.setWidthFull();
        form.getStyle().set("gap", "0.45rem");

        Div card = new Div(form);
        card.getStyle()
                .set("width", "100%")
                .set("max-width", "420px")
                .set("background", "#FFFFFF")
                .set("border", "1px solid #E5E7EB")
                .set("border-radius", "12px")
                .set("padding", "1.35rem 1.35rem")
                .set("box-shadow", "0 8px 24px rgba(15, 23, 42, 0.08)")
                .set("box-sizing", "border-box");

        add(card);
    }

    private static void styleInput(Input input) {
        input.setWidthFull();
        input.getStyle()
                .set("padding", "0.6rem 0.75rem")
                .set("border", "1px solid #D1D5DB")
                .set("border-radius", "8px")
                .set("font-size", "1rem")
                .set("box-sizing", "border-box")
                .set("margin", "0");
    }

    private static void styleLabel(NativeLabel label) {
        label.getStyle()
                .set("font-size", "0.875rem")
                .set("font-weight", "500")
                .set("color", "#374151")
                .set("margin-top", "0.25rem");
    }
}
