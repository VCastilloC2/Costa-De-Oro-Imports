package com.application.service.implementation.redis;

import com.application.persistence.entity.usuario.Usuario;
import com.application.service.interfaces.UsuarioCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de caché para Usuario usando Redis
 * Diseñado para ser resiliente: si Redis falla, la app continúa funcionando
 */
@Service
public class UsuarioCacheServiceImpl implements UsuarioCacheService {

    private static final Logger logger = LoggerFactory.getLogger(UsuarioCacheService.class);
    private static final String CACHE_KEY_PREFIX = "usuario:";
    private static final String CACHE_KEY_EMAIL_PREFIX = "usuario:email:";
    private static final long CACHE_TTL = 24; // 24 horas

    @Autowired(required = false)
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * Obtener usuario del caché por ID
     * @param usuarioId ID del usuario
     * @return Usuario del caché o null si no existe
     */
    @Override
    public Usuario obtener(Long usuarioId) {
        if (redisTemplate == null) {
            return null; // Redis no disponible
        }

        try {
            String key = CACHE_KEY_PREFIX + usuarioId;
            Object obj = redisTemplate.opsForValue().get(key);

            if (obj instanceof Usuario) {
                logger.debug("Usuario {} obtenido del caché", usuarioId);
                return (Usuario) obj;
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error al obtener usuario {} del caché: {}", usuarioId, e.getMessage());
            return null; // Retorna null, el servicio buscará en BD
        }
    }

    /**
     * Obtener usuario del caché por correo
     * @param correo Correo del usuario
     * @return Usuario del caché o null si no existe
     */
    @Override
    public Usuario obtenerPorCorreo(String correo) {
        if (redisTemplate == null) {
            return null;
        }

        try {
            String key = CACHE_KEY_EMAIL_PREFIX + correo;
            Object obj = redisTemplate.opsForValue().get(key);

            if (obj instanceof Usuario) {
                logger.debug("Usuario con correo {} obtenido del caché", correo);
                return (Usuario) obj;
            }
            return null;
        } catch (Exception e) {
            logger.warn("Error al obtener usuario por correo {} del caché: {}", correo, e.getMessage());
            return null;
        }
    }

    /**
     * Guardar usuario en caché
     * @param usuario Usuario a guardar
     */
    @Override
    public void guardar(Usuario usuario) {
        if (redisTemplate == null || usuario == null || usuario.getUsuarioId() == null) {
            return; // Redis no disponible o datos inválidos
        }

        try {
            String keyId = CACHE_KEY_PREFIX + usuario.getUsuarioId();
            String keyEmail = CACHE_KEY_EMAIL_PREFIX + usuario.getCorreo();

            // Guardar por ID
            redisTemplate.opsForValue().set(
                    keyId,
                    usuario,
                    CACHE_TTL,
                    TimeUnit.HOURS
            );

            // Guardar por correo (para login rápido)
            if (usuario.getCorreo() != null && !usuario.getCorreo().isEmpty()) {
                redisTemplate.opsForValue().set(
                        keyEmail,
                        usuario,
                        CACHE_TTL,
                        TimeUnit.HOURS
                );
            }

            logger.debug("Usuario {} guardado en caché", usuario.getUsuarioId());
        } catch (Exception e) {
            logger.warn("Error al guardar usuario {} en caché: {}",
                    usuario.getUsuarioId(), e.getMessage());
            // No lanzar excepción, continuar con operación normal
        }
    }

    /**
     * Actualizar usuario en caché
     * @param usuario Usuario actualizado
     */
    @Override
    public void actualizar(Usuario usuario) {
        if (redisTemplate == null || usuario == null || usuario.getUsuarioId() == null) {
            return;
        }

        try {
            // Eliminar caché anterior
            eliminar(usuario.getUsuarioId());
            // Guardar nuevo caché
            guardar(usuario);
            logger.debug("Usuario {} actualizado en caché", usuario.getUsuarioId());
        } catch (Exception e) {
            logger.warn("Error al actualizar usuario {} en caché: {}",
                    usuario.getUsuarioId(), e.getMessage());
        }
    }

    /**
     * Eliminar usuario del caché
     * @param usuarioId ID del usuario
     */
    @Override
    public void eliminar(Long usuarioId) {
        if (redisTemplate == null || usuarioId == null) {
            return;
        }

        try {
            String keyId = CACHE_KEY_PREFIX + usuarioId;
            redisTemplate.delete(keyId);
            logger.debug("Usuario {} eliminado del caché", usuarioId);
        } catch (Exception e) {
            logger.warn("Error al eliminar usuario {} del caché: {}", usuarioId, e.getMessage());
        }
    }

    /**
     * Eliminar usuario del caché por correo
     * @param correo Correo del usuario
     */
    @Override
    public void eliminarPorCorreo(String correo) {
        if (redisTemplate == null || correo == null) {
            return;
        }

        try {
            String keyEmail = CACHE_KEY_EMAIL_PREFIX + correo;
            redisTemplate.delete(keyEmail);
            logger.debug("Usuario con correo {} eliminado del caché", correo);
        } catch (Exception e) {
            logger.warn("Error al eliminar usuario por correo {} del caché: {}", correo, e.getMessage());
        }
    }

    /**
     * Limpiar todo el caché de usuarios
     */
    @Override
    public void limpiarTodo() {
        if (redisTemplate == null) {
            return;
        }

        try {
            redisTemplate.delete(redisTemplate.keys(CACHE_KEY_PREFIX + "*"));
            redisTemplate.delete(redisTemplate.keys(CACHE_KEY_EMAIL_PREFIX + "*"));
            logger.info("Caché de usuarios limpiado completamente");
        } catch (Exception e) {
            logger.warn("Error al limpiar caché de usuarios: {}", e.getMessage());
        }
    }

    /**
     * Verificar si Redis está disponible
     * @return true si Redis está disponible
     */
    @Override
    public boolean isRedisAvailable() {
        if (redisTemplate == null) {
            return false;
        }

        try {
            redisTemplate.getConnectionFactory().getConnection().ping();
            return true;
        } catch (Exception e) {
            logger.warn("Redis no disponible: {}", e.getMessage());
            return false;
        }
    }
}
