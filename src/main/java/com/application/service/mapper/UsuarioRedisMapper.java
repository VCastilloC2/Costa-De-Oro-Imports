package com.application.service.mapper;

import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.redis.UsuarioRedis;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRedisMapper {

    public UsuarioRedis toRedisDTO(Usuario usuario) {
        return new UsuarioRedis(
                usuario.getUsuarioId(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                usuario.getImagen(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getDireccion(),

                usuario.isEnabled(),
                usuario.isAccountNonExpired(),
                usuario.isAccountNonLocked(),
                usuario.isCredentialsNonExpired(),

                usuario.getRol().getRolId(),

                "ROLE_" + usuario.getRol().getName().name(),

                usuario.getEmpresa() != null
                        ? usuario.getEmpresa().getEmpresaId()
                        : null
        );
    }
}