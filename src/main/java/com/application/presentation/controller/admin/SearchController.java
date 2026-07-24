package com.application.presentation.controller.admin;

import com.application.persistence.entity.rol.enums.ERol;
import com.application.presentation.dto.SearchResponse;
import com.application.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/search")
@RequiredArgsConstructor
public class SearchController {

    private final SearchService searchService;

    /**
     * Búsqueda universal.
     * - Sin sesión: solo productos, categorías y blog.
     * - Con sesión admin: incluye clientes y proveedores.
     *
     * @param q     término de búsqueda (mínimo 2 caracteres)
     * @param tipo  filtro opcional: productos | categorias | blog | clientes | proveedores
     * @param limit máximo de resultados por tipo (default 10, máx 50)
     */
    @GetMapping("/universal")
    public ResponseEntity<SearchResponse> universal(
            @RequestParam(value = "q", required = false) String q,
            @RequestParam(value = "tipo", required = false) String tipo,
            @RequestParam(value = "limit", required = false, defaultValue = "10") int limit) {

        if (q == null || q.trim().length() < 2) {
            return ResponseEntity.ok(SearchResponse.builder()
                    .success(false)
                    .message("El término de búsqueda debe tener al menos 2 caracteres")
                    .query(q)
                    .totalResults(0)
                    .build());
        }

        String safeTipo = (tipo == null) ? null : tipo.toLowerCase();

        // Bloquear búsquedas de admin si el usuario no tiene rol
        if (safeTipo != null) {
            boolean requiereAdmin = safeTipo.equals("clientes") || safeTipo.equals("proveedores");
            if (requiereAdmin && !esAdmin()) {
                log.warn("Búsqueda '{}' bloqueada: usuario sin rol ADMIN", safeTipo);
                return ResponseEntity.ok(SearchResponse.builder()
                        .success(false)
                        .query(q)
                        .message("No autorizado")
                        .totalResults(0)
                        .build());
            }
        }

        int safeLimit = Math.max(1, Math.min(limit, 50));
        log.debug("Búsqueda universal -> q='{}' tipo='{}' limit={}", q, safeTipo, safeLimit);

        SearchResponse response = searchService.buscarUniversal(q, safeTipo, safeLimit);
        return ResponseEntity.ok(response);
    }

    private boolean esAdmin() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) {
            return false;
        }
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_" + ERol.ADMIN.name()));
    }
}
