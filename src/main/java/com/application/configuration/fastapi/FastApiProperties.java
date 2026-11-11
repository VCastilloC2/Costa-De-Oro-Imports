package com.application.configuration.fastapi;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;
import org.springframework.validation.annotation.Validated;

import java.net.URI;
import java.time.Duration;

/**
 * Configuración tipada del servicio FastAPI (prefijo {@code fastapi.*}).
 *
 * <p>Los valores llegan desde variables de entorno / {@code .env}:
 * {@code FASTAPI_BASE_URL}, {@code FASTAPI_API_KEY}, {@code FASTAPI_CONNECT_TIMEOUT}, etc.
 * En el perfil cloud, si falta la URL, la API key o la URL no es https, la aplicación NO arranca
 * (fail-fast), en lugar de fallar en la primera petición en producción.</p>
 *
 * @param baseUrl        URL base del servicio (local: http://localhost:8000, cloud: https://...)
 * @param apiKey         credencial de servicio enviada en la cabecera {@code X-API-Key}
 * @param requireHttps   si es {@code true} se rechaza una {@code baseUrl} sin TLS (perfil cloud)
 * @param connectTimeout tiempo máximo para abrir la conexión TCP/TLS
 * @param readTimeout    tiempo máximo esperando la respuesta en llamadas normales (dashboard)
 * @param predictionReadTimeout tiempo máximo para predicciones: FastAPI entrena el modelo y puede tardar
 * @param retry          política de reintentos ante fallos transitorios
 */
@Validated
@ConfigurationProperties(prefix = "fastapi")
public record FastApiProperties(
        @NotNull URI baseUrl,
        String apiKey,
        @DefaultValue("false") boolean requireHttps,
        @DefaultValue("3s") @NotNull Duration connectTimeout,
        @DefaultValue("15s") @NotNull Duration readTimeout,
        @DefaultValue("120s") @NotNull Duration predictionReadTimeout,
        @DefaultValue @Valid @NotNull Retry retry) {

    /**
     * @param maxRetries reintentos adicionales tras el primer intento (0 = sin reintentos)
     * @param delay      espera inicial entre intentos
     * @param multiplier factor de backoff exponencial
     * @param maxDelay   tope de la espera entre intentos
     */
    public record Retry(
            @DefaultValue("2") @Min(0) @Max(10) int maxRetries,
            @DefaultValue("300ms") @NotNull Duration delay,
            @DefaultValue("2.0") @DecimalMin("1.0") double multiplier,
            @DefaultValue("3s") @NotNull Duration maxDelay) {

        public Retry {
            if (delay != null && delay.isNegative()) {
                throw new IllegalArgumentException("fastapi.retry.delay no puede ser negativo");
            }
            // RetryPolicy de Spring exige maxDelay > 0
            if (maxDelay != null && (maxDelay.isZero() || maxDelay.isNegative())) {
                throw new IllegalArgumentException("fastapi.retry.max-delay debe ser mayor que 0 (p. ej. 3s)");
            }
        }
    }

    public FastApiProperties {
        if (baseUrl != null) {
            String scheme = baseUrl.getScheme();
            if (scheme == null || baseUrl.getHost() == null) {
                throw new IllegalArgumentException("fastapi.base-url debe ser una URL absoluta (http/https): " + baseUrl);
            }
            if (requireHttps && !"https".equalsIgnoreCase(scheme)) {
                throw new IllegalArgumentException(
                        "fastapi.base-url debe usar https cuando fastapi.require-https=true (perfil cloud)");
            }
        }
        // En cloud la credencial es obligatoria; en local se permite vacía para poder arrancar
        // la tienda sin FastAPI (las llamadas devolverán 401 y se registrará el error).
        if (requireHttps && (apiKey == null || apiKey.isBlank())) {
            throw new IllegalArgumentException("FASTAPI_API_KEY es obligatoria en el perfil cloud");
        }
        apiKey = apiKey == null ? "" : apiKey.strip();
    }

    /** Nunca exponer la API key en logs ni en actuator/configprops. */
    @Override
    public String toString() {
        return "FastApiProperties[baseUrl=" + baseUrl + ", apiKey=****, requireHttps=" + requireHttps
                + ", connectTimeout=" + connectTimeout + ", readTimeout=" + readTimeout
                + ", predictionReadTimeout=" + predictionReadTimeout + ", retry=" + retry + "]";
    }
}
