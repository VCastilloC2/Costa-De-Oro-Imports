package com.application.service.implementation;

import com.application.configuration.fastapi.FastApiException;
import com.application.configuration.fastapi.FastApiGateway;
import com.application.presentation.dto.GraficaMensualResponse;
import com.fasterxml.jackson.annotation.JsonAlias;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;

/**
 * Adaptador hacia la API de predicciones en FastAPI.
 *
 * <p>URL, credenciales, timeouts y reintentos se configuran en {@code FastApiClientConfig}
 * (variables {@code FASTAPI_*} del {@code .env}); aquí solo se describen las peticiones.
 * Autenticación: cabecera {@code X-API-Key} con una clave de servicio emitida por FastAPI
 * ({@code POST /api/v1/predictions/clients}) con permisos {@code predictions:read} y
 * {@code predictions:generate}.</p>
 */
@Component
public class PrediccionApiClient {

    private final FastApiGateway fastApi;

    /** Usa el gateway con timeout largo (FASTAPI_PREDICTION_READ_TIMEOUT): entrenar el modelo tarda. */
    public PrediccionApiClient(@Qualifier("fastApiPredictionGateway") FastApiGateway fastApi) {
        this.fastApi = fastApi;
    }

    /** Serie mensual para la gráfica: anios * 12 meses (+ proyeccionMeses). */
    public GraficaMensualResponse obtenerGrafica(int anios, int proyeccionMeses) {
        GraficaMensualResponse respuesta = fastApi.execute("prediccion.grafica", client -> client.get()
                .uri(uri -> uri.path("/api/prediccion/datos-grafica")
                        .queryParam("anios", anios)
                        .queryParam("proyeccion_meses", proyeccionMeses)
                        .build())
                .retrieve()
                .body(GraficaMensualResponse.class));

        if (respuesta == null) {
            throw new FastApiException.Unavailable("FastAPI devolvió una gráfica vacía", 200, null);
        }
        return respuesta;
    }

    /** Compara los últimos meses mediante evaluación walk-forward. */
    public EvaluacionReciente evaluarUltimosMeses(int meses) {
        EvaluacionReciente respuesta = fastApi.execute("prediccion.evaluar-ultimos-meses", client -> client.get()
                .uri(uri -> uri.path("/api/prediccion/evaluar-ultimos-meses")
                        .queryParam("meses", meses)
                        .build())
                .retrieve()
                .body(EvaluacionReciente.class));
        if (respuesta == null) {
            throw new FastApiException.Unavailable("FastAPI no devolvió la evaluación reciente", 200, null);
        }
        return respuesta;
    }

    /** Venta total prevista para un mes concreto. */
    public double predecirMes(int anio, int mes) {
        RespuestaPrediccion respuesta = fastApi.execute("prediccion.mes", client -> client.post()
                .uri("/api/prediccion/generar")
                .contentType(MediaType.APPLICATION_JSON)
                .body(new SolicitudPrediccion(anio, mes))
                .retrieve()
                .body(RespuestaPrediccion.class));

        if (respuesta == null || !respuesta.success() || respuesta.resultado() == null) {
            throw new FastApiException.Unavailable("La API de predicciones no devolvió un resultado", 200, null);
        }
        return respuesta.resultado();
    }

    /** Cuerpo de POST /api/prediccion/generar. */
    public record SolicitudPrediccion(int anno, int mes) {
    }

    /** Respuesta de POST /api/prediccion/generar (mismos nombres que el JSON de Python). */
    public record RespuestaPrediccion(Double resultado, String mensaje, boolean success) {
    }

    public record EvaluacionReciente(@JsonAlias("meses_solicitados") int mesesSolicitados,
                                     @JsonAlias("meses_evaluados") int mesesEvaluados, String desde, String hasta,
                                     java.util.List<ComparacionMes> comparacion, MetricasEvaluacion metricas,
                                     String modelo, @JsonAlias("tipo_evaluacion") String tipoEvaluacion,
                                     @JsonAlias("fuente_datos") String fuenteDatos) {
    }

    public record ComparacionMes(String fecha, @JsonAlias("valor_real") double valorReal,
                                 @JsonAlias("valor_predicho") double valorPredicho,
                                 @JsonAlias("error_absoluto") double errorAbsoluto,
                                 @JsonAlias("error_porcentual") Double errorPorcentual, String modelo,
                                 @JsonAlias("meses_entrenamiento") int mesesEntrenamiento) {
    }

    public record MetricasEvaluacion(double mae, double mse, Double wmape) {
    }
}
