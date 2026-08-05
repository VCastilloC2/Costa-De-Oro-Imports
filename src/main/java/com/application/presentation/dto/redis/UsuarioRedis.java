package com.application.presentation.dto.redis;

import com.application.persistence.entity.usuario.enums.EIdentificacion;

import java.io.Serializable;

public record UsuarioRedis(Long usuarioId, EIdentificacion tipoIdentificacion, String numeroIdentificacion,
                           String imagen, String nombres, String apellidos, String telefono, String correo,

        /*
         * Contiene el hash generado por BCrypt.
         * Nunca debe contener la contraseña en texto plano.
         */
                           String passwordHash, String direccion, boolean enabled, boolean accountNonExpired,
                           boolean accountNonLocked, boolean credentialsNonExpired, Long rolId,

        /*
         * Ejemplos:
         * ROLE_ADMIN
         * ROLE_CLIENTE
         * ROLE_PROVEEDOR
         */
                           String autoridad, Long empresaId) implements Serializable {

    public String nombreCompleto() {
        String nombre = nombres == null ? "" : nombres.trim();
        String apellido = apellidos == null ? "" : apellidos.trim();

        return (nombre + " " + apellido).trim();
    }

    /**
     * Convierte el objeto interno de Redis en una respuesta segura.
     * No incluye passwordHash.
     */
    public UsuarioPerfilRedis toPerfilDTO() {
        return new UsuarioPerfilRedis(usuarioId, tipoIdentificacion, numeroIdentificacion, imagen, nombres, apellidos, telefono, correo, direccion, enabled, rolId, autoridad, empresaId);
    }

}