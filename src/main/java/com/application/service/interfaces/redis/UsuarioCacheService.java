package com.application.service.interfaces.redis;

import com.application.presentation.dto.redis.UsuarioRedis;

import java.util.Optional;

public interface UsuarioCacheService {
    Optional<UsuarioRedis> obtener(Long id);

    Optional<UsuarioRedis> obtenerPorCorreo(String correo);

    void guardar(UsuarioRedis usuario);

    void actualizar(UsuarioRedis usuario, String correoAnterior);

    void eliminar(Long id);

    void eliminarPorCorreo(String correo);

    boolean isRedisAvailable();
}