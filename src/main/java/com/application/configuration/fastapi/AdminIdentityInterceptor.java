package com.application.configuration.fastapi;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;

/**
 * Añade {@code X-Admin-Email} con el correo del ADMIN que tiene la sesión en Spring.
 *
 * <p>FastAPI solo acepta la API key de servicio en rutas de administración si esta cabecera
 * identifica a un ADMIN activo (y lo registra en su auditoría). Para usuarios sin rol ADMIN o
 * anónimos no se envía: FastAPI responde entonces solo con datos públicos.</p>
 */
class AdminIdentityInterceptor implements ClientHttpRequestInterceptor {

    static final String ADMIN_EMAIL_HEADER = "X-Admin-Email";

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution)
            throws IOException {
        String correo = correoAdminActual();
        if (correo != null) {
            request.getHeaders().set(ADMIN_EMAIL_HEADER, correo);
        }
        return execution.execute(request, body);
    }

    static String correoAdminActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken) {
            return null;
        }
        boolean esAdmin = auth.getAuthorities().stream().anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
        if (!esAdmin) {
            return null;
        }
        String correo = auth.getPrincipal() instanceof UserDetails ud ? ud.getUsername() : auth.getName();
        return correo == null || correo.isBlank() ? null : correo;
    }
}
