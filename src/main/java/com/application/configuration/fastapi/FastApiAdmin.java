package com.application.configuration.fastapi;

import com.application.presentation.dto.general.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.Nullable;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.function.Supplier;

/**
 * Acceso de las vistas de administración de Spring a las rutas {@code /admin/*} y {@code /api/*} de FastAPI.
 *
 * <ul>
 *   <li><b>Lecturas</b> ({@link #leer}): si FastAPI falla o está desactivado, se usa la lógica local de Spring.
 *       Es seguro porque ambos backends trabajan sobre la misma base de datos.</li>
 *   <li><b>Escrituras</b> ({@link #escribir}): NO se repiten en local si FastAPI falla — si FastAPI ya la
 *       aplicó y solo se perdió la respuesta, repetirla duplicaría el cambio (alternar estado, facturas...).
 *       Se devuelve un {@link BaseResponse} con el mensaje de FastAPI o uno genérico.</li>
 *   <li>{@code fastapi.admin.enabled=false} (env {@code FASTAPI_ADMIN_ENABLED}) vuelve a la lógica local
 *       en todo, sin redesplegar código.</li>
 * </ul>
 *
 * <p>Autenticación: API key de servicio con permisos {@code admin:read}/{@code admin:write} más la cabecera
 * {@code X-Admin-Email} (la añade {@link AdminIdentityInterceptor}).</p>
 */
@Slf4j
@Component
public class FastApiAdmin {

    static final String NO_DISPONIBLE =
            "El servicio de administración no está disponible en este momento. Intenta de nuevo en unos minutos.";

    private final FastApiGateway fastApi;
    private final boolean enabled;

    public FastApiAdmin(FastApiGateway fastApi, @Value("${fastapi.admin.enabled:true}") boolean enabled) {
        this.fastApi = fastApi;
        this.enabled = enabled;
    }

    public boolean isEnabled() {
        return enabled;
    }

    /** GET a FastAPI deserializando en {@code tipo}; ante cualquier fallo, {@code local}. */
    public <T> T leer(String operacion, String uri, Class<T> tipo, Supplier<T> local, Object... uriVars) {
        return leer(operacion, local, c -> c.get().uri(uri, uriVars).retrieve().body(tipo));
    }

    /** Variante para tipos genéricos, p. ej. {@code new ParameterizedTypeReference<List<X>>() {}}. */
    public <T> T leer(String operacion, String uri, ParameterizedTypeReference<T> tipo, Supplier<T> local,
                      Object... uriVars) {
        return leer(operacion, local, c -> c.get().uri(uri, uriVars).retrieve().body(tipo));
    }

    private <T> T leer(String operacion, Supplier<T> local, java.util.function.Function<RestClient, T> llamada) {
        if (!enabled) {
            return local.get();
        }
        try {
            T resultado = fastApi.execute("admin." + operacion, llamada);
            if (resultado != null) {
                return resultado;
            }
            log.warn("FastAPI '{}' devolvió un cuerpo vacío; se usa la lógica local", operacion);
        } catch (FastApiException e) {
            log.warn("FastAPI '{}' no disponible ({}); se usa la lógica local", operacion, e.getMessage());
        }
        return local.get();
    }

    /**
     * Escritura (POST/PUT/PATCH/DELETE). {@code local} solo se usa si la integración está desactivada.
     * {@code cuerpo} se evalúa solo al llamar a FastAPI (p. ej. para subir la imagen a Cloudinary una vez).
     *
     * @return el {@code {mensaje, success}} de FastAPI, o {@code success=false} con un mensaje para el usuario
     */
    public BaseResponse escribir(String operacion, HttpMethod metodo, String uri, @Nullable Supplier<?> cuerpo,
                                 Supplier<BaseResponse> local, Object... uriVars) {
        if (!enabled) {
            return local.get();
        }
        boolean idempotente = metodo == HttpMethod.PUT || metodo == HttpMethod.DELETE;
        Object json = cuerpo != null ? cuerpo.get() : null;
        try {
            BaseResponse respuesta = fastApi.execute("admin." + operacion, c -> {
                RestClient.RequestBodySpec spec = c.method(metodo).uri(uri, uriVars);
                if (json != null) {
                    spec.contentType(MediaType.APPLICATION_JSON).body(json);
                }
                return spec.retrieve().body(BaseResponse.class);
            }, idempotente);
            return respuesta != null ? respuesta : new BaseResponse("Operación realizada", true);
        } catch (FastApiException.Rejected e) {
            String detalle = e.getDetalle();
            return new BaseResponse(detalle != null ? detalle : "No se pudo completar la operación", false);
        } catch (FastApiException.Unavailable e) {
            return new BaseResponse(NO_DISPONIBLE, false);
        }
    }
}
