package org.delcom.ifs25037springbootcashflow;

import com.vaadin.flow.component.dependency.StyleSheet;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Meta;
import com.vaadin.flow.server.LoadDependenciesOnStartup;
import com.vaadin.flow.server.PWA;
import com.vaadin.flow.theme.lumo.Lumo;
import org.delcom.ifs25037springbootcashflow.framework.view.LoginView;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
// Hanya halaman login yang dimuat di awal; view lain (Grid, Upload, Dialog, dst.)
// dimuat saat pertama kali dibuka -> bundle JS awal lebih kecil (Lighthouse).
@LoadDependenciesOnStartup(LoginView.class)
@StyleSheet(Lumo.STYLESHEET)
@StyleSheet("styles.css")
@Meta(name = "description", content = "Cashflow: aplikasi web pencatatan keuangan pribadi untuk mencatat pemasukan dan pengeluaran serta memantau saldo.")
@PWA(name = "Cashflow", shortName = "Cashflow", iconPath = "icons/icon.png")
public class App implements AppShellConfigurator {

    public static void main(String[] args) throws Exception {
        DotEnv.load();
        SpringApplication.run(App.class, args);
    }
}