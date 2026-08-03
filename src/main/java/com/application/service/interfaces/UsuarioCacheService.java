package com.application.service.interfaces;

import com.application.persistence.entity.usuario.Usuario;

public interface UsuarioCacheService {
    Usuario obtener(Long usuarioId);
    Usuario obtenerPorCorreo(String correo);
    void guardar(Usuario usuario);
    void actualizar(Usuario usuario);
    void eliminar(Long usuarioId);
    void eliminarPorCorreo(String correo);
    void limpiarTodo();
    boolean isRedisAvailable();
}