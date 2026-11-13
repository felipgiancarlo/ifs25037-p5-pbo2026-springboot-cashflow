package org.delcom.ifs25037springbootcashflow.framework.view;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpServletResponseWrapper;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Memberi cache panjang untuk aset statis ber-hash/ber-versi (bundle Vaadin,
 * lumo.css, styles.css) agar Lighthouse "efficient cache policy" lolos.
 * Halaman HTML dan data dinamis tidak disentuh.
 */
@Component
public class StaticCacheFilter extends OncePerRequestFilter {

    private static final String LONG_CACHE = "public, max-age=31536000, immutable";

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain chain
    ) throws ServletException, IOException {
        if (isCacheable(request)) {
            chain.doFilter(request, new LongCacheResponse(response));
        } else {
            chain.doFilter(request, response);
        }
    }

    private static boolean isCacheable(HttpServletRequest request) {
        String path = request.getRequestURI();
        if (path.startsWith("/VAADIN/build/")) {
            return true;
        }
        // Aset ber-versi (?v-c=hash) seperti /lumo/lumo.css dan /styles.css
        return request.getParameter("v-c") != null
                && (path.endsWith(".css") || path.endsWith(".js"));
    }

    /** Memaksa header Cache-Control tetap panjang walau di-set ulang oleh Vaadin. */
    private static final class LongCacheResponse extends HttpServletResponseWrapper {

        LongCacheResponse(HttpServletResponse response) {
            super(response);
            super.setHeader("Cache-Control", LONG_CACHE);
        }

        @Override
        public void setHeader(String name, String value) {
            if (!"Cache-Control".equalsIgnoreCase(name)) {
                super.setHeader(name, value);
            }
        }

        @Override
        public void addHeader(String name, String value) {
            if (!"Cache-Control".equalsIgnoreCase(name)) {
                super.addHeader(name, value);
            }
        }
    }
}
