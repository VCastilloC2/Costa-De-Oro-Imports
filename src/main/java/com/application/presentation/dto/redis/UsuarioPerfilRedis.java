package com.application.presentation.dto.redis;

import com.application.persistence.entity.usuario.enums.EIdentificacion;

public record UsuarioPerfilRedis(
        Long usuarioId,
        EIdentificacion tipoIdentificacion,
        String numeroIdentificacion,
        String imagen,
        String nombres,
        String apellidos,
        String telefono,
        String correo,
        String direccion,
        boolean enabled,
        Long rolId,
        String autoridad,
        Long empresaId
) {
}
