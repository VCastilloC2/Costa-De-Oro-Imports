package com.application.service.implementation.redis;

import com.application.presentation.dto.redis.UsuarioRedis;
import com.application.service.interfaces.redis.UsuarioCacheService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

/**
 * Servicio de caché para Usuario usando Redis
 * Diseñado para ser resiliente: si Redis falla, la app continúa funcionando
 */
@Service
public class UsuarioCacheServiceImpl implements UsuarioCacheService {

    private static final Logger logger =
            LoggerFactory.getLogger(UsuarioCacheServiceImpl.class);

    private static final String CACHE_KEY_ID =
            "usuario:id:";

    private static final String CACHE_KEY_EMAIL =
            "usuario:email:";

    private static final long CACHE_TTL_HOURS = 24;

    private final RedisTemplate<String, UsuarioRedis> redisTemplate;

    public UsuarioCacheServiceImpl(
            @Qualifier("usuarioRedisTemplate")
            ObjectProvider<RedisTemplate<String, UsuarioRedis>> provider
    ) {
        this.redisTemplate = provider.getIfAvailable();
    }

    /**
     * Obtener usuario del caché por ID
     *
     * @param usuarioId ID del usuario
     * @return Usuario del caché o null si no existe
     */
    @Override
    public Optional<UsuarioRedis> obtener(Long usuarioId) {
        if (redisTemplate == null || usuarioId == null) {
            return Optional.empty();
        }

        try {
            String key = construirKeyId(usuarioId);

            UsuarioRedis usuario =
                    redisTemplate.opsForValue().get(key);

            if (usuario != null) {
                logger.debug(
                        "Usuario {} obtenido desde Redis",
                        usuarioId
                );
            }

            return Optional.ofNullable(usuario);

        } catch (Exception e) {
            logger.warn(
                    "Error al obtener usuario {} desde Redis: {}",
                    usuarioId,
                    e.getMessage()
            );

            return Optional.empty();
        }
    }

    /**
     * Obtener usuario del caché por correo
     *
     * @param correo Correo del usuario
     * @return Usuario del caché o null si no existe
     */
    @Override
    public Optional<UsuarioRedis> obtenerPorCorreo(String correo) {
        if (redisTemplate == null || !tieneTexto(correo)) {
            return Optional.empty();
        }

        try {
            String correoNormalizado =
                    normalizarCorreo(correo);

            String key =
                    construirKeyCorreo(correoNormalizado);

            UsuarioRedis usuario =
                    redisTemplate.opsForValue().get(key);

            if (usuario != null) {
                logger.debug(
                        "Usuario {} obtenido desde Redis por correo",
                        usuario.usuarioId()
                );
            }

            return Optional.ofNullable(usuario);

        } catch (Exception e) {
            logger.warn(
                    "Error al obtener usuario por correo {} desde Redis: {}",
                    correo,
                    e.getMessage()
            );

            return Optional.empty();
        }
    }

    /**
     * Guardar usuario en caché
     *
     * @param usuario Usuario a guardar
     */
    @Override
    public void guardar(UsuarioRedis usuario) {
        if (!puedeGuardar(usuario)) {
            return;
        }

        try {
            guardarClaves(usuario);

            logger.debug(
                    "Usuario {} guardado en Redis",
                    usuario.usuarioId()
            );

        } catch (Exception e) {
            logger.warn(
                    "Error al guardar usuario {} en Redis: {}",
                    usuario.usuarioId(),
                    e.getMessage()
            );
        }
    }

    /**
     * Actualizar usuario en caché
     *
     * @param usuario Usuario actualizado
     */
    @Override
    public void actualizar(
            UsuarioRedis usuario,
            String correoAnterior
    ) {
        if (!puedeGuardar(usuario)) {
            return;
        }

        try {
            String nuevoCorreo =
                    normalizarCorreo(usuario.correo());

            /*
             * Si el usuario cambió su correo, eliminamos la clave
             * correspondiente al correo anterior.
             */
            if (tieneTexto(correoAnterior)) {
                String correoAnteriorNormalizado =
                        normalizarCorreo(correoAnterior);

                if (!correoAnteriorNormalizado.equals(nuevoCorreo)) {
                    redisTemplate.delete(
                            construirKeyCorreo(
                                    correoAnteriorNormalizado
                            )
                    );
                }
            }

            guardarClaves(usuario);

            logger.debug(
                    "Usuario {} actualizado en Redis",
                    usuario.usuarioId()
            );

        } catch (Exception e) {
            logger.warn(
                    "Error al actualizar usuario {} en Redis: {}",
                    usuario.usuarioId(),
                    e.getMessage()
            );
        }
    }

    /**
     * Eliminar usuario del caché
     *
     * @param usuarioId ID del usuario
     */
    @Override
    public void eliminar(
            Long usuarioId
    ) {
        if (redisTemplate == null || usuarioId == null) {
            return;
        }

        try {
            List<String> keys = new ArrayList<>();

            keys.add(construirKeyId(usuarioId));

            redisTemplate.delete(keys);

            logger.debug(
                    "Usuario {} eliminado de Redis",
                    usuarioId
            );

        } catch (Exception e) {
            logger.warn(
                    "Error al eliminar usuario {} de Redis: {}",
                    usuarioId,
                    e.getMessage()
            );
        }
    }

    /**
     * Eliminar usuario del caché por correo
     *
     * @param correo Correo del usuario
     */
    @Override
    public void eliminarPorCorreo(String correo) {
        if (redisTemplate == null || !tieneTexto(correo)) {
            return;
        }

        try {
            Optional<UsuarioRedis> usuarioCache =
                    obtenerPorCorreo(correo);

            List<String> keys = new ArrayList<>();

            keys.add(
                    construirKeyCorreo(
                            normalizarCorreo(correo)
                    )
            );

            usuarioCache
                    .map(UsuarioRedis::usuarioId)
                    .ifPresent(usuarioId ->
                            keys.add(construirKeyId(usuarioId))
                    );

            redisTemplate.delete(keys);

        } catch (Exception e) {
            logger.warn(
                    "Error al eliminar usuario por correo {}: {}",
                    correo,
                    e.getMessage()
            );
        }
    }

    /**
     * Verificar si Redis está disponible
     *
     * @return true si Redis está disponible
     */
    @Override
    public boolean isRedisAvailable() {
        if (redisTemplate == null) {
            return false;
        }

        try {
            String respuesta = redisTemplate.execute(
                    (RedisCallback<String>) connection -> connection.ping()
            );

            return "PONG".equalsIgnoreCase(respuesta);

        } catch (Exception e) {
            logger.warn(
                    "Redis no disponible: {}",
                    e.getMessage()
            );

            return false;
        }
    }

    private void guardarClaves(UsuarioRedis usuario) {
        redisTemplate.opsForValue().set(
                construirKeyId(usuario.usuarioId()),
                usuario,
                CACHE_TTL_HOURS,
                TimeUnit.HOURS
        );

        redisTemplate.opsForValue().set(
                construirKeyCorreo(usuario.correo()),
                usuario,
                CACHE_TTL_HOURS,
                TimeUnit.HOURS
        );
    }

    private boolean puedeGuardar(UsuarioRedis usuario) {
        return redisTemplate != null
                && usuario != null
                && usuario.usuarioId() != null
                && tieneTexto(usuario.correo());
    }

    private String construirKeyId(Long usuarioId) {
        return CACHE_KEY_ID + usuarioId;
    }

    private String construirKeyCorreo(String correo) {
        return CACHE_KEY_EMAIL + normalizarCorreo(correo);
    }

    private String normalizarCorreo(String correo) {
        return correo
                .trim()
                .toLowerCase(Locale.ROOT);
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

}