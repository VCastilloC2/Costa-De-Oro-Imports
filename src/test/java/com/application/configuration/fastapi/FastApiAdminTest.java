package com.application.configuration.fastapi;

import com.application.presentation.dto.general.response.BaseResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.client.ExpectedCount;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.time.Duration;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.content;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.headerDoesNotExist;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withException;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class FastApiAdminTest {

    private static final String BASE = "http://fastapi.test";

    private MockRestServiceServer server;
    private FastApiGateway gateway;

    record Item(Long id, String nombre) {
    }

    @BeforeEach
    void setUp() {
        FastApiProperties props = new FastApiProperties(URI.create(BASE), "cdo_pk_test", false,
                Duration.ofSeconds(1), Duration.ofSeconds(1), Duration.ofSeconds(1),
                new FastApiProperties.Retry(2, Duration.ofMillis(1), 1.0, Duration.ofMillis(5)));
        RestClient.Builder builder = RestClient.builder();
        server = MockRestServiceServer.bindTo(builder).build();
        gateway = new FastApiGateway(FastApiClientConfig.configure(builder, props).build(),
                new FastApiClientConfig().fastApiRetryTemplate(props));
    }

    @AfterEach
    void limpiarSeguridad() {
        SecurityContextHolder.clearContext();
    }

    private static void sesion(String correo, String rol) {
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                correo, null, List.of(new SimpleGrantedAuthority(rol))));
    }

    @Test
    void lecturaUsaFastApiYEnviaElAdminDeLaSesion() {
        sesion("admin@x.co", "ROLE_ADMIN");
        server.expect(requestTo(BASE + "/admin/blog/7"))
                .andExpect(header("X-API-Key", "cdo_pk_test"))
                .andExpect(header("X-Admin-Email", "admin@x.co"))
                .andRespond(withSuccess("{\"id\":7,\"nombre\":\"remoto\",\"extra\":true}", MediaType.APPLICATION_JSON));

        Item item = new FastApiAdmin(gateway, true).leer("blog.obtener", "/admin/blog/{id}", Item.class,
                () -> new Item(7L, "local"), 7);

        assertThat(item.nombre()).isEqualTo("remoto");
        server.verify();
    }

    @Test
    void sinRolAdminNoSeEnviaLaCabecera() {
        sesion("cliente@x.co", "ROLE_PERSONA_NATURAL");
        server.expect(requestTo(BASE + "/api/search/universal"))
                .andExpect(headerDoesNotExist("X-Admin-Email"))
                .andRespond(withSuccess("{\"id\":1,\"nombre\":\"x\"}", MediaType.APPLICATION_JSON));
        new FastApiAdmin(gateway, true).leer("s", "/api/search/universal", Item.class, () -> null);
        server.verify();
    }

    @Test
    void lecturaVuelveALaLogicaLocalSiFastApiFalla() {
        server.expect(ExpectedCount.times(3), requestTo(BASE + "/admin/blog"))
                .andRespond(withStatus(HttpStatus.SERVICE_UNAVAILABLE));
        Item item = new FastApiAdmin(gateway, true).leer("blog", "/admin/blog", Item.class, () -> new Item(1L, "local"));
        assertThat(item.nombre()).isEqualTo("local");

        server.reset();
        server.expect(requestTo(BASE + "/admin/blog")).andRespond(withStatus(HttpStatus.FORBIDDEN));
        assertThat(new FastApiAdmin(gateway, true).leer("blog", "/admin/blog", Item.class,
                () -> new Item(1L, "local")).nombre()).isEqualTo("local");
    }

    @Test
    void desactivadoUsaSoloLaLogicaLocal() {
        FastApiAdmin admin = new FastApiAdmin(gateway, false);
        assertThat(admin.leer("x", "/admin/x", Item.class, () -> new Item(1L, "local")).nombre()).isEqualTo("local");
        BaseResponse r = admin.escribir("x", HttpMethod.POST, "/admin/x", () -> {
            throw new AssertionError("no debe construir el cuerpo (ni subir imágenes)");
        }, () -> new BaseResponse("local", true));
        assertThat(r.mensaje()).isEqualTo("local");
        server.verify(); // ninguna petición
    }

    @Test
    void escrituraDevuelveElMensajeDeFastApi() {
        server.expect(requestTo(BASE + "/admin/clientes"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().json("{\"correo\":\"a@x.co\"}"))
                .andRespond(withSuccess("{\"mensaje\":\"Usuario creado exitosamente\",\"success\":true,\"id\":5}",
                        MediaType.APPLICATION_JSON));
        BaseResponse ok = new FastApiAdmin(gateway, true).escribir("clientes.crear", HttpMethod.POST,
                "/admin/clientes", () -> java.util.Map.of("correo", "a@x.co"), () -> {
                    throw new AssertionError("no debe usar la lógica local");
                });
        assertThat(ok).isEqualTo(new BaseResponse("Usuario creado exitosamente", true));

        server.reset();
        server.expect(requestTo(BASE + "/admin/clientes")).andRespond(withStatus(HttpStatus.CONFLICT)
                .contentType(MediaType.APPLICATION_JSON)
                .body("{\"detail\":{\"mensaje\":\"El correo a@x.co ya est\\u00e1 registrado\",\"success\":false}}"));
        BaseResponse conflicto = new FastApiAdmin(gateway, true).escribir("clientes.crear", HttpMethod.POST,
                "/admin/clientes", () -> java.util.Map.of(), () -> null);
        assertThat(conflicto).isEqualTo(new BaseResponse("El correo a@x.co ya está registrado", false));
    }

    @Test
    void escrituraNoIdempotenteNoSeRepiteSiFastApiYaLaRecibio() {
        server.expect(ExpectedCount.once(), requestTo(BASE + "/admin/blog/1/disable"))
                .andRespond(withStatus(HttpStatus.BAD_GATEWAY));
        BaseResponse r = new FastApiAdmin(gateway, true).escribir("blog.estado", HttpMethod.PATCH,
                "/admin/blog/{id}/disable", null, () -> null, 1);
        assertThat(r.success()).isFalse();
        assertThat(r.mensaje()).isEqualTo(FastApiAdmin.NO_DISPONIBLE);
        server.verify(); // una sola llamada: alternar dos veces dejaría el estado igual que al principio
    }

    @Test
    void escrituraSeReintentaSiNoLlegoAConectar() {
        AtomicInteger llamadas = new AtomicInteger();
        server.expect(ExpectedCount.twice(), requestTo(BASE + "/admin/blog/1/disable"))
                .andRespond(request -> {
                    if (llamadas.incrementAndGet() == 1) {
                        throw new ConnectException("connection refused");
                    }
                    return withSuccess("{\"mensaje\":\"Historia habilitada exitosamente\",\"success\":true}",
                            MediaType.APPLICATION_JSON).createResponse(request);
                });
        BaseResponse r = new FastApiAdmin(gateway, true).escribir("blog.estado", HttpMethod.PATCH,
                "/admin/blog/{id}/disable", null, () -> null, 1);
        assertThat(r.success()).isTrue();
        server.verify();
    }

    @Test
    void parserDeErroresDeFastApi() {
        assertThat(FastApiErrorParser.mensaje("{\"detail\":{\"mensaje\":\"Ya existe una factura\",\"success\":false}}"))
                .isEqualTo("Ya existe una factura");
        assertThat(FastApiErrorParser.mensaje("{\"detail\":\"API Key inv\\u00e1lida o revocada\"}"))
                .isEqualTo("API Key inválida o revocada");
        assertThat(FastApiErrorParser.mensaje(
                "{\"detail\":[{\"type\":\"missing\",\"loc\":[\"body\",\"titulo\"],\"msg\":\"Field required\"}]}"))
                .isEqualTo("Dato inválido en 'titulo': Field required");
        assertThat(FastApiErrorParser.mensaje("<html>502</html>")).isNull();
    }

    @Test
    void lecturaSinConexionUsaLocal() {
        server.expect(ExpectedCount.times(3), requestTo(BASE + "/admin/x"))
                .andRespond(withException(new IOException("down")));
        assertThat(new FastApiAdmin(gateway, true).leer("x", "/admin/x", Item.class, () -> new Item(9L, "local"))
                .id()).isEqualTo(9L);
    }
}
