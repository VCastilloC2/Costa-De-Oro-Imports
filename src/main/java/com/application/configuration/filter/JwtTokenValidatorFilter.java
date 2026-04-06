package com.application.configuration.filter;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.repository.UsuarioRepository;
import com.application.utils.JwtUtils;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtTokenValidatorFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final UsuarioRepository usuarioRepository;

    @Override
    protected void doFilterInternal(@NotNull HttpServletRequest request,
                                    @NotNull HttpServletResponse response,
                                    @NotNull FilterChain filterChain) throws ServletException, IOException {

        Cookie[] cookies = request.getCookies();
        String jwtToken = null;

        // recorremos las cookies para obtener la que tiene el token
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                // "access_token" este es el nombre de la cookie que tiene el token
                if (cookie.getName().equals("access_token")) {
                    jwtToken = cookie.getValue();
                    break;
                }
            }
        }

        if (jwtToken != null) {

            DecodedJWT decodedJWT = jwtUtils.validateToken(jwtToken);

            String username =  decodedJWT.getSubject(); // se extrae el email del usuario
            String permisos =  decodedJWT.getClaim("authorities").asString();

            Collection<? extends GrantedAuthority> authorities = AuthorityUtils.commaSeparatedStringToAuthorityList(permisos);

            Usuario usuario = usuarioRepository.findByCorreo(username)
                    .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado en el filtro 'JwtTokenValidatorFilter'"));

            CustomUserPrincipal customUserPrincipal = new CustomUserPrincipal(usuario);

            // seteamos al usuario al contexto de Spring Security
            Authentication authentication = new UsernamePasswordAuthenticationToken(customUserPrincipal, null, authorities);
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        filterChain.doFilter(request, response);
    }
}
