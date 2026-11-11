package com.application.configuration.fastapi;

import org.jspecify.annotations.Nullable;

/**
 * Error al comunicarse con el servicio FastAPI.
 *
 * <ul>
 *   <li>{@link Unavailable}: el servicio no respondió (caído, timeout, 5xx, 429) tras agotar los reintentos.</li>
 *   <li>{@link Rejected}: el servicio respondió con un 4xx (credencial inválida, petición mal formada...).
 *       No se reintenta porque repetir la misma petición daría el mismo resultado.</li>
 * </ul>
 */
public sealed class FastApiException extends RuntimeException
        permits FastApiException.Unavailable, FastApiException.Rejected {

    /** Código HTTP devuelto por FastAPI, o {@code null} si no hubo respuesta (fallo de red / timeout). */
    private final @Nullable Integer status;

    protected FastApiException(String message, @Nullable Integer status, @Nullable Throwable cause) {
        super(message, cause);
        this.status = status;
    }

    public @Nullable Integer getStatus() {
        return status;
    }

    public static final class Unavailable extends FastApiException {
        public Unavailable(String message, @Nullable Integer status, @Nullable Throwable cause) {
            super(message, status, cause);
        }
    }

    public static final class Rejected extends FastApiException {
        public Rejected(String message, int status) {
            super(message, status, null);
        }
    }
}
