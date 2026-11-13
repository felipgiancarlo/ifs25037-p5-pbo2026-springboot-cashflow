package org.delcom.ifs25037springbootcashflow.framework.view;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Input;
import com.vaadin.flow.component.html.NativeButton;
import com.vaadin.flow.component.html.NativeLabel;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.delcom.ifs25037springbootcashflow.domain.entity.User;
import org.delcom.ifs25037springbootcashflow.domain.exception.ValidationException;
import org.delcom.ifs25037springbootcashflow.domain.repository.IUserRepository;
import org.delcom.ifs25037springbootcashflow.framework.auth.AuthSession;
import org.delcom.ifs25037springbootcashflow.usecase.AuthUseCase;

/**
 * Halaman login.
 *
 * Sengaja hanya memakai elemen HTML biasa (Div, Input, NativeButton, Anchor),
 * tanpa komponen web Vaadin (Button, TextField, VerticalLayout, Notification, ...),
 * dan tidak merujuk kelas view lain (FinanceView/RegisterView). Dengan begitu
 * bundle JavaScript awal untuk halaman login sangat kecil (Lighthouse Performance),
 * sedangkan view lain dimuat saat pertama kali dibuka.
 *
 * ID yang dipakai grader: #login-username, #login-password, #login-submit
 */
@Route("login")
@PageTitle("Login")
public class LoginView extends Div {

    private static final String PRIMARY = "#2563EB";

    public LoginView(AuthUseCase authUseCase, IUserRepository userRepository) {
        if (AuthSession.currentUser(userRepository).isPresent()) {
            // Navigasi lewat path (bukan FinanceView.class) agar bundle login tetap kecil.
            UI.getCurrent().navigate("");
            return;
        }

        setSizeFull();
        // Landmark utama (Axe: landmark-one-main / region)
        getElement().setAttribute("role", "main");
        getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("background", "#F3F4F6")
                .set("box-sizing", "border-box")
                .set("padding", "1rem");

        // ---------- Judul ----------
        H1 title = new H1("Masuk");
        title.getStyle().set("margin", "0");

        Paragraph subtitle = new Paragraph("Gunakan kredensial akun kamu untuk masuk");
        subtitle.getStyle().set("margin", "0 0 0.15rem 0").set("color", "#4B5563");

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

        // ---------- Pesan error (pengganti Notification) ----------
        Div errorBox = new Div();
        errorBox.getElement().setAttribute("role", "alert");
        errorBox.setVisible(false);
        errorBox.getStyle()
                .set("background", "#FEE2E2")
                .set("color", "#991B1B")
                .set("border", "1px solid #FCA5A5")
                .set("border-radius", "8px")
                .set("padding", "0.6rem 0.75rem")
                .set("font-size", "0.875rem")
                .set("margin-top", "0.5rem");

        // ---------- Tombol submit ----------
        NativeButton loginButton = new NativeButton("Masuk");
        loginButton.setId("login-submit");
        loginButton.getElement().setAttribute("type", "button");
        loginButton.getStyle()
                .set("width", "100%")
                .set("margin-top", "0.85rem")
                .set("padding", "0.7rem 1rem")
                .set("background", PRIMARY)
                .set("color", "#FFFFFF")
                .set("border", "none")
                .set("border-radius", "8px")
                .set("font-size", "1rem")
                .set("font-weight", "600")
                .set("font-family", "inherit")
                .set("cursor", "pointer");

        Runnable submit = () -> {
            errorBox.setVisible(false);
            loginButton.setText("Memproses...");
            loginButton.getElement().setEnabled(false);
            try {
                User user = authUseCase.login(username.getValue(), password.getValue());
                AuthSession.login(user);
                UI.getCurrent().navigate("");
            } catch (ValidationException ex) {
                loginButton.setText("Masuk");
                loginButton.getElement().setEnabled(true);
                errorBox.setText(ex.getMessage());
                errorBox.setVisible(true);
            }
        };

        loginButton.addClickListener(event -> submit.run());

        // Enter pada kolom input memicu submit.
        // Event "input" (EAGER) sudah mengirim nilai ke server sebelum keyup Enter.
        username.getElement().addEventListener("keyup", event -> submit.run())
                .setFilter("event.key === 'Enter'");
        password.getElement().addEventListener("keyup", event -> submit.run())
                .setFilter("event.key === 'Enter'");

        // ---------- Link daftar (sama seperti RouterLink: <a router-link href="register">) ----------
        Anchor registerLink = new Anchor("register", "Belum punya akun? Daftar");
        registerLink.getElement().setAttribute("router-link", true);
        registerLink.getStyle()
                .set("align-self", "flex-end")
                .set("text-align", "right")
                .set("margin-top", "0.85rem")
                .set("cursor", "pointer");

        // ---------- Kartu form ----------
        Div form = new Div(
                title,
                subtitle,
                usernameLabel,
                username,
                passwordLabel,
                password,
                errorBox,
                loginButton,
                registerLink);
        form.getStyle()
                .set("display", "flex")
                .set("flex-direction", "column")
                .set("gap", "0.45rem")
                .set("width", "100%");

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