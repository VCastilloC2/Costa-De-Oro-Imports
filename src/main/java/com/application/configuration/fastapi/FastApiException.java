package com.application.configuration.fastapi;

import org.jspecify.annotations.Nullable;

/**
 * Error al comunicarse con el servicio FastAPI.
 *
 * <ul>
 *   <li>{@link Unavailable}: el servicio no respondió (caído, timeout, 5xx, 429) tras agotar los reintentos.</li>
 *   <li>{@link Rejected}: el servicio respondió con un 4xx (credencial inválida, datos inválidos, duplicado...).
 *       No se reintenta porque repetir la misma petición daría el mismo resultado.</li>
 * </ul>
 */
public sealed class FastApiException extends RuntimeException
        permits FastApiException.Unavailable, FastApiException.Rejected {

    /** Código HTTP devuelto por FastAPI, o {@code null} si no hubo respuesta (fallo de red / timeout). */
    private final @Nullable Integer status;

    /** Mensaje para el usuario final que envió FastAPI ({@code detail.mensaje} / {@code detail}), si lo hay. */
    private final @Nullable String detalle;

    protected FastApiException(String message, @Nullable Integer status, @Nullable String detalle,
                               @Nullable Throwable cause) {
        super(message, cause);
        this.status = status;
        this.detalle = detalle;
    }

    public @Nullable Integer getStatus() {
        return status;
    }

    public @Nullable String getDetalle() {
        return detalle;
    }

    public static final class Unavailable extends FastApiException {

        /** {@code false} cuando FastAPI ya recibió una petición no idempotente: repetirla podría duplicarla. */
        private final boolean reintentable;

        public Unavailable(String message, @Nullable Integer status, @Nullable Throwable cause) {
            this(message, status, cause, true);
        }

        private Unavailable(String message, @Nullable Integer status, @Nullable Throwable cause, boolean reintentable) {
            super(message, status, null, cause);
            this.reintentable = reintentable;
        }

        public boolean isReintentable() {
            return reintentable;
        }

        Unavailable sinReintentos() {
            return new Unavailable(getMessage(), getStatus(), getCause(), false);
        }
    }

    public static final class Rejected extends FastApiException {
        public Rejected(String message, int status) {
            this(message, status, null);
        }

        public Rejected(String message, int status, @Nullable String detalle) {
            super(message, status, detalle, null);
        }
    }
}
