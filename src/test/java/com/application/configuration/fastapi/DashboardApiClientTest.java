package com.application.configuration.fastapi;

import com.application.presentation.dto.grafica.GraficaIngresosGastosResponse;
import com.application.presentation.dto.grafica.ventasTotales.VentasTotalesResponse;
import com.application.service.implementation.DashboardApiClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.net.URI;
import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class DashboardApiClientTest {

    private static final String BASE = "http://fastapi.test";

    private MockRestServiceServer server;
    private DashboardApiClient client;

    @BeforeEach
    void setUp() {
        FastApiProperties props = new FastApiProperties(URI.create(BASE), "cdo_pk_test", false,
                Duration.ofSeconds(1), Duration.ofSeconds(1),
                new FastApiProperties.Retry(0, Duration.ZERO, 1.0, Duration.ofMillis(1)));
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        RestClient restClient = FastApiClientConfig.configure(builder, props).build();
        client = new DashboardApiClient(
                new FastApiGateway(restClient, new FastApiClientConfig().fastApiRetryTemplate(props)));
    }

    @Test
    void ingresosGastosMapeaAlDtoDeLaGrafica() {
        server.expect(requestTo(BASE + "/api/v1/dashboard/ingresos-gastos?meses=12"))
                .andExpect(header("X-API-Key", "cdo_pk_test"))
                .andRespond(withSuccess("""
                        {"categorias":["Oct 2026","Nov 2026"],"ingresos":[150.0,200.0],
                         "gastos":[30.0,80.0],"totalIngresos":350.0,"totalGastos":110.0}""",
                        MediaType.APPLICATION_JSON));

        GraficaIngresosGastosResponse r = client.ingresosGastos(12);

        assertThat(r.categorias()).containsExactly("Oct 2026", "Nov 2026");
        assertThat(r.ingresos()).containsExactly(150.0, 200.0);
        assertThat(r.gastos()).containsExactly(30.0, 80.0);
        server.verify();
    }

    @Test
    void ventasTotalesMapeaAlDtoDeLaDona() {
        server.expect(requestTo(BASE + "/api/v1/dashboard/ventas-totales?meses=12"))
                .andRespond(withSuccess("""
                        {"series":[350.0,50.0,25.0],
                         "labels":["Envíos $350.00 (82%)","Reembolsos $50.00 (12%)","Pedidos $25.00 (6%)"],
                         "totalVentas":425.0}""", MediaType.APPLICATION_JSON));

        VentasTotalesResponse r = client.ventasTotales(12);

        assertThat(r.series()).containsExactly(350.0, 50.0, 25.0);
        assertThat(r.labels()).hasSize(3);
        assertThat(r.totalVentas()).isEqualTo(425.0);
        server.verify();
    }
}
