package com.application.service.mapper;

import com.application.persistence.entity.empresa.Empresa;
import com.application.persistence.entity.rol.Rol;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.redis.UsuarioRedis;
import org.springframework.stereotype.Component;

@Component
public class UsuarioRedisMapper {

    public UsuarioRedis toRedisDTO(Usuario usuario) {
        if (usuario == null) {
            return null;
        }

        Rol rol = usuario.getRol();
        Empresa empresa = usuario.getEmpresa();

        Long rolId = null;
        String autoridad = null;

        if (rol != null) {
            rolId = rol.getRolId();

            /*
             * Asumo que en Rol tienes:
             *
             * private ERol nombre;
             *
             * Si tu atributo se llama "rol", cambia:
             * rol.getNombre()
             *
             * por:
             * rol.getRol()
             */
            if (rol.getName() != null) {
                autoridad = rol.getName().name();
            }
        }

        Long empresaId = empresa != null
                ? empresa.getEmpresaId()
                : null;

        return new UsuarioRedis(
                usuario.getUsuarioId(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                usuario.getImagen(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getTelefono(),
                normalizarCorreo(usuario.getCorreo()),
                usuario.getPassword(),
                usuario.getDireccion(),
                usuario.isEnabled(),
                usuario.isAccountNonExpired(),
                usuario.isAccountNonLocked(),
                usuario.isCredentialsNonExpired(),
                rolId,
                autoridad,
                empresaId
        );
    }

    private String normalizarCorreo(String correo) {
        if (correo == null) {
            return null;
        }
        return correo.trim().toLowerCase();
    }
}