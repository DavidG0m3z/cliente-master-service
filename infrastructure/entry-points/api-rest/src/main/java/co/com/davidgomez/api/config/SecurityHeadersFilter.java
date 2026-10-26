package co.com.davidgomez.api.config;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@WebFilter("/**")
public class SecurityHeadersFilter implements Filter {

    private static final String H2_CONSOLE_PATH = "/h2";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // Pattern matching de Java 16+: si request ES un HttpServletRequest,
        // lo convierte y lo guarda en httpRequest en la misma línea.
        // La consola H2 necesita scripts e iframes que la política de abajo bloquea.
        // Solo aplica en desarrollo local: en producción la consola H2 está desactivada.
        if (request instanceof HttpServletRequest httpRequest
                && httpRequest.getRequestURI() != null
                && httpRequest.getRequestURI().startsWith(H2_CONSOLE_PATH)) {
            chain.doFilter(request, response);
            return;
        }

        HttpServletResponse header = (HttpServletResponse) response;
        header.setHeader("Content-Security-Policy", "default-src 'self'; frame-ancestors 'self'; form-action 'self'");
        header.setHeader("Strict-Transport-Security", "max-age=31536000;");
        header.setHeader("X-Content-Type-Options", "nosniff");
        header.setHeader("Server", "");
        header.setHeader("Cache-Control", "no-store");
        header.setHeader("Pragma", "no-cache");
        header.setHeader("Referrer-Policy", "strict-origin-when-cross-origin");
        chain.doFilter(request, response);
    }
}