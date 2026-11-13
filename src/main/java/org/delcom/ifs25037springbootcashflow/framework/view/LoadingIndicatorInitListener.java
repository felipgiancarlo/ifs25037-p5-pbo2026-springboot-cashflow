package org.delcom.ifs25037springbootcashflow.framework.view;

import com.vaadin.flow.component.page.LoadingIndicatorConfiguration;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.stereotype.Component;

/**
 * Konfigurasi indikator loading global untuk tindakan yang memerlukan waktu tunggu.
 */
@Component
public class LoadingIndicatorInitListener implements VaadinServiceInitListener {

    private static final String MARK_INDICATOR_JS =
            "const mark = () => {"
            + "  document.querySelectorAll('vaadin-connection-indicator').forEach(el => {"
            + "    if (el.getAttribute('role') !== 'status') {"
            + "      el.setAttribute('role', 'status');"
            + "      el.setAttribute('aria-live', 'polite');"
            + "    }"
            + "  });"
            + "};"
            + "mark();"
            + "if (!window.__cashflowIndicatorObserver) {"
            + "  window.__cashflowIndicatorObserver = new MutationObserver(mark);"
            + "  window.__cashflowIndicatorObserver.observe(document.body, { childList: true });"
            + "}";

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            LoadingIndicatorConfiguration conf = uiEvent.getUI().getLoadingIndicatorConfiguration();
            conf.setApplyDefaultTheme(true);
            conf.setFirstDelay(150);
            conf.setSecondDelay(600);
            conf.setThirdDelay(1500);
            // Indikator koneksi bawaan Vaadin dijadikan live region agar tidak
            // dianggap konten di luar landmark (Axe: region).
            uiEvent.getUI().getPage().executeJs(MARK_INDICATOR_JS);
        });
    }
}

