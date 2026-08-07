package com.application.configuration.custom;

import com.application.persistence.entity.empresa.Empresa;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.redis.UsuarioRedis;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;
import java.util.*;

@Getter
public class CustomUserPrincipal implements UserDetails, OAuth2User {

    // Atributos para Spring Security
    private final String correo;
    private final String password;
    private final List<GrantedAuthority> authorities = new ArrayList<>();
    private final boolean isEnabled;
    private final boolean accountNonExpired;
    private final boolean accountNonLocked;
    private final boolean credentialsNonExpired;

    // Atributos OAuth2
    private final Map<String, Object> attributes;

    // Información adicional
    private final String nombres;
    private final String apellidos;
    private final Empresa empresa;

    /**
     * Login tradicional (Usuario)
     */
    public CustomUserPrincipal(Usuario usuario) {
        this(usuario, Collections.emptyMap());
    }

    /**
     * Login tradicional (Redis)
     */
    public CustomUserPrincipal(UsuarioRedis usuario) {
        this(usuario, Collections.emptyMap());
    }

    /**
     * Login OAuth2 (Entidad Usuario)
     */
    public CustomUserPrincipal(
            Usuario usuario,
            Map<String, Object> attributes
    ) {
        this.correo = usuario.getCorreo();
        this.password = usuario.getPassword();

        this.authorities.add(
                new SimpleGrantedAuthority(
                        "ROLE_" + usuario.getRol().getName().name()
                )
        );

        this.isEnabled = usuario.isEnabled();
        this.accountNonExpired = usuario.isAccountNonExpired();
        this.accountNonLocked = usuario.isAccountNonLocked();
        this.credentialsNonExpired = usuario.isCredentialsNonExpired();

        this.nombres = usuario.getNombres();
        this.apellidos = usuario.getApellidos();
        this.empresa = usuario.getEmpresa();

        this.attributes = attributes;
    }

    /**
     * Login OAuth2 (Redis)
     */
    public CustomUserPrincipal(
            UsuarioRedis usuario,
            Map<String, Object> attributes
    ) {
        this.correo = usuario.correo();
        this.password = usuario.passwordHash();

        this.authorities.add(
                new SimpleGrantedAuthority(
                        usuario.autoridad()
                )
        );

        this.isEnabled = usuario.enabled();
        this.accountNonExpired = usuario.accountNonExpired();
        this.accountNonLocked = usuario.accountNonLocked();
        this.credentialsNonExpired = usuario.credentialsNonExpired();

        this.nombres = usuario.nombres();
        this.apellidos = usuario.apellidos();

        // Redis no almacena la entidad Empresa completa
        this.empresa = null;

        this.attributes = attributes;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return correo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return isEnabled;
    }

    @Override
    public Map<String, Object> getAttributes() {
        return attributes;
    }

    @Override
    public String getName() {
        return correo;
    }
}