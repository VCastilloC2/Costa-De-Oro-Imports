package com.application.configuration.fastapi;

import com.application.presentation.dto.SearchResponse;
import com.application.presentation.dto.categoria.response.CategoriaProductoResponse;
import com.application.presentation.dto.categoria.response.CategoriaResponse;
import com.application.presentation.dto.categoria.response.SubCategoriaProductoResponse;
import com.application.presentation.dto.comentario.response.ComentarioResponse;
import com.application.presentation.dto.compra.response.CompraDashboardResponse;
import com.application.presentation.dto.compra.response.DetalleCompraResponse;
import com.application.presentation.dto.factura.response.FacturaProductoProveedorResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.grafica.DatosPanelResponse;
import com.application.presentation.dto.grafica.columnasApiladas.StockComprasResponse;
import com.application.presentation.dto.grafica.historicoVentas.HistoricoVentasResponse;
import com.application.presentation.dto.grafica.ventasCiudades.MapaVentasResponse;
import com.application.presentation.dto.historia.response.HistoriaResponse;
import com.application.presentation.dto.peticion.response.PeticionResponse;
import com.application.presentation.dto.producto.response.ProductoResponse;
import com.application.presentation.dto.usuario.response.ClienteResponse;
import com.application.presentation.dto.usuario.response.ProveedorEstadisticasResponse;
import com.application.presentation.dto.usuario.response.ProveedorProductoResponse;
import com.application.presentation.dto.usuario.response.ProveedorResponse;
import com.application.presentation.dto.usuario.response.UsuarioGastoResponse;
import com.application.service.implementation.DashboardApiClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfEnvironmentVariable;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Contrato real Spring ↔ FastAPI: deserializa las respuestas de un FastAPI en ejecución en los DTOs de Spring
 * que usan las vistas. La lógica local lanza error, así que cualquier fallo de contrato hace fallar el test
 * (en producción haría caer silenciosamente a la lógica local).
 *
 * <p>Solo se ejecuta con {@code FASTAPI_LIVE_URL} y {@code FASTAPI_LIVE_KEY} (clave con admin:read/admin:write);
 * los datos esperados son los del script de semilla usado en la verificación.</p>
 */
@EnabledIfEnvironmentVariable(named = "FASTAPI_LIVE_URL", matches = "https?://.+")
class FastApiAdminContratoLiveTest {

    private FastApiAdmin admin;
    private DashboardApiClient dashboard;

    record OpcionesProducto(List<CategoriaProductoResponse> categoriaList, List<ProveedorProductoResponse> proveedorList) {
    }

    @BeforeEach
    void setUp() {
        FastApiProperties props = new FastApiProperties(URI.create(System.getenv("FASTAPI_LIVE_URL")),
                System.getenv("FASTAPI_LIVE_KEY"), false, Duration.ofSeconds(3), Duration.ofSeconds(15),
                Duration.ofSeconds(30), new FastApiProperties.Retry(0, Duration.ZERO, 1.0, Duration.ofMillis(1)));
        FastApiClientConfig config = new FastApiClientConfig();
        FastApiGateway gateway = new FastApiGateway(config.fastApiRestClient(props), config.fastApiRetryTemplate(props));
        admin = new FastApiAdmin(gateway, true);
        dashboard = new DashboardApiClient(gateway);
        SecurityContextHolder.getContext().setAuthentication(UsernamePasswordAuthenticationToken.authenticated(
                "admin@example.com", null, List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))));
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    private static <T> Supplier<T> sinLocal(String que) {
        return () -> {
            throw new AssertionError("Cayó a la lógica local: el contrato de FastAPI no encaja con " + que);
        };
    }

    private <T> List<T> lista(String uri, ParameterizedTypeReference<List<T>> tipo, Object... vars) {
        return admin.leer(uri, uri, tipo, sinLocal(uri), vars);
    }

    @Test
    void catalogoYCompras() {
        List<ProductoResponse> productos = lista("/admin/producto", new ParameterizedTypeReference<>() {
        });
        assertThat(productos).singleElement().satisfies(p -> {
            assertThat(p.nombre()).isEqualTo("Club Colombia");
            assertThat(p.tipo()).hasToString("UNIDAD");
            assertThat(p.precio()).isEqualTo(5000.0);
            assertThat(p.nombreCategoria()).isEqualTo("Cerveza");
            assertThat(p.empresaProveedor()).isEqualTo("Licores SAS");
            assertThat(p.imagen()).startsWith("https://res.cloudinary.com/democloud/");
        });
        assertThat(admin.leer("p", "/admin/producto/{id}", ProductoResponse.class, sinLocal("ProductoResponse"), 1)
                .codigoProducto()).isEqualTo("CC-1");
        OpcionesProducto opciones = admin.leer("o", "/admin/producto/opciones", OpcionesProducto.class,
                sinLocal("opciones"));
        assertThat(opciones.categoriaList()).isNotEmpty();
        assertThat(opciones.proveedorList()).singleElement().satisfies(p ->
                assertThat(p.nombreEmpresa()).isEqualTo("Licores SAS"));
        List<SubCategoriaProductoResponse> subs = lista("/admin/producto/subcategorias/{id}",
                new ParameterizedTypeReference<>() {
                }, 1);
        assertThat(subs).extracting(SubCategoriaProductoResponse::nombre).containsExactly("Lager");

        List<CategoriaResponse> categorias = lista("/admin/categoria", new ParameterizedTypeReference<>() {
        });
        assertThat(categorias).singleElement().satisfies(c -> {
            assertThat(c.totalProductos()).isEqualTo(1);
            assertThat(c.subcategorias()).isNotEmpty();
        });

        List<CompraDashboardResponse> compras = lista("/admin/compra", new ParameterizedTypeReference<>() {
        });
        assertThat(compras).singleElement().satisfies(c -> {
            assertThat(c.numeroOrden()).isEqualTo("ORD-0001");
            assertThat(c.fecha()).isNotNull();
            assertThat(c.estado()).isEqualTo("Pagado");
        });
        DetalleCompraResponse detalle = admin.leer("d", "/admin/compra/{id}", DetalleCompraResponse.class,
                sinLocal("DetalleCompraResponse"), 1);
        assertThat(detalle.productos()).singleElement().satisfies(p -> assertThat(p.cantidad()).isEqualTo(1));
        assertThat(detalle.usuarioCorreo()).isEqualTo("ana@example.com");
    }

    @Test
    void usuariosYProveedores() {
        List<UsuarioGastoResponse> clientes = lista("/admin/clientes", new ParameterizedTypeReference<>() {
        });
        assertThat(clientes).singleElement().satisfies(c -> {
            assertThat(c.totalGastado()).isEqualTo(5950.0);
            assertThat(c.isEnabled()).isTrue();
        });
        ClienteResponse cliente = admin.leer("c", "/admin/clientes/{id}", ClienteResponse.class,
                sinLocal("ClienteResponse"), 2);
        assertThat(cliente.tipoIdentificacion()).hasToString("CC");
        assertThat(cliente.password()).isNull();

        List<ProveedorEstadisticasResponse> proveedores = lista("/admin/provedores",
                new ParameterizedTypeReference<>() {
                });
        assertThat(proveedores).singleElement().satisfies(p -> {
            assertThat(p.nombreEmpresa()).isEqualTo("Licores SAS");
            assertThat(p.totalGastado()).isEqualTo(200000.0);
            assertThat(p.totalGanado()).isEqualTo(5000.0);
        });
        ProveedorResponse proveedor = admin.leer("p", "/admin/provedores/{id}", ProveedorResponse.class,
                sinLocal("ProveedorResponse"), 3);
        assertThat(proveedor.razonSocial()).isEqualTo("Licores SAS");
        List<FacturaProductoProveedorResponse> productos = lista("/admin/provedores/productos/{id}",
                new ParameterizedTypeReference<>() {
                }, 3);
        assertThat(productos).singleElement().satisfies(p -> assertThat(p.stock()).isEqualTo(40));
    }

    @Test
    void contenido() {
        List<HistoriaResponse> blog = lista("/admin/blog", new ParameterizedTypeReference<>() {
        });
        assertThat(blog).isNotEmpty().allSatisfy(h -> assertThat(h.fecha()).isNotNull());
        List<PeticionResponse> pqrs = lista("/admin/pqrs", new ParameterizedTypeReference<>() {
        });
        assertThat(pqrs).singleElement().satisfies(p -> {
            assertThat(p.estadoPeticion()).isEqualTo("Pendiente");
            assertThat(p.usurarioResponse().correo()).isEqualTo("ana@example.com");
        });
        List<ComentarioResponse> resennas = lista("/admin/resenna", new ParameterizedTypeReference<>() {
        });
        assertThat(resennas).singleElement().satisfies(c -> {
            assertThat(c.calificacion()).isEqualTo(5);
            assertThat(c.blogResponse().titulo()).isEqualTo("Historia");
        });
    }

    @Test
    void dashboardReportesYBusqueda() {
        DatosPanelResponse panel = admin.leer("panel", "/api/graficas/dashboard", DatosPanelResponse.class,
                sinLocal("DatosPanelResponse"));
        assertThat(panel.ingresoAnual()).isEqualTo(5950.0);
        assertThat(panel.estadisticas()).isNotNull();
        assertThat(panel.comprasRecientes()).singleElement().satisfies(c -> assertThat(c.fechaPedido()).isNotNull());
        assertThat(panel.productosMasVendidos()).isNotEmpty();

        assertThat(admin.leer("s", "/api/graficas/stock-compras", StockComprasResponse.class, sinLocal("stock"))
                .categorias()).contains("Cerveza");
        assertThat(admin.leer("m", "/api/graficas/mapa-ventas", MapaVentasResponse.class, sinLocal("mapa"))
                .ventasPorCiudad()).isNotNull();
        assertThat(admin.leer("h", "/api/graficas/historico-ventas", HistoricoVentasResponse.class, sinLocal("hist"))
                .series()).isNotEmpty();

        for (String reporte : List.of("ventas-clientes", "ventas-producto", "ventas-cliente-producto",
                "rentabilidad-producto", "cuentas-pagar", "reporte-compras")) {
            Map<String, Object> r = admin.leer(reporte, "/api/reportes/" + reporte,
                    new ParameterizedTypeReference<Map<String, Object>>() {
                    }, sinLocal(reporte));
            assertThat(r).containsKeys("totalGeneral", "anio");
        }

        SearchResponse busqueda = admin.leer("b", "/api/search/universal?q={q}&limit={l}", SearchResponse.class,
                sinLocal("SearchResponse"), "ana", 10);
        assertThat(busqueda.getClientes()).isNotEmpty(); // admin vía X-Admin-Email ve clientes

        assertThat(dashboard.ingresosGastos(12).categorias()).hasSize(12);
        assertThat(dashboard.ventasTotales(12).series()).hasSize(3);
    }

    @Test
    void escrituras() {
        BaseResponse creado = admin.escribir("blog.crear", HttpMethod.POST, "/admin/blog",
                () -> AdminVistasParaTest.cuerpo("imagen", "blog/nuevo", "titulo", "Nueva", "descripcion", "D",
                        "historiaCompleta", "H", "activo", true), sinLocal("blog.crear"));
        assertThat(creado).isEqualTo(new BaseResponse("Historia creada exitosamente", true));

        BaseResponse estado = admin.escribir("pqrs.estado", HttpMethod.PATCH, "/admin/pqrs/{id}/estado",
                () -> Map.of("estadoPeticion", "En Proceso"), sinLocal("pqrs"), 1);
        assertThat(estado.success()).isTrue();

        BaseResponse compra = admin.escribir("compra.estado", HttpMethod.PATCH, "/admin/compra/{id}/estado",
                () -> Map.of("estado", "Pagado"), sinLocal("compra"), 1);
        assertThat(compra.success()).isTrue();

        BaseResponse duplicado = admin.escribir("clientes.crear", HttpMethod.POST, "/admin/clientes",
                () -> AdminVistasParaTest.cuerpo("tipoIdentificacion", "CC", "numeroIdentificacion", "9",
                        "nombres", "Ana", "apellidos", "Ruiz", "telefono", "999", "correo", "ana@example.com",
                        "direccion", "x", "password", "Secreta123"), sinLocal("clientes"));
        assertThat(duplicado).isEqualTo(new BaseResponse("El correo ana@example.com ya está registrado", false));

        BaseResponse factura = admin.escribir("provedores.factura", HttpMethod.POST, "/admin/provedores/compras",
                () -> AdminVistasParaTest.cuerpo("numeroFactura", "F-LIVE-" + System.nanoTime(),
                        "fechaEmision", LocalDate.now().toString(), "totalCompra", 40000.0, "proveedorId", 3,
                        "detalles", List.of(Map.of("productoId", 1, "cantidad", 10, "precioCompra", 4000))),
                sinLocal("factura"));
        assertThat(factura).isEqualTo(new BaseResponse("Factura de compra registrada exitosamente", true));
        assertThat(lista("/admin/provedores/productos/{id}",
                new ParameterizedTypeReference<List<FacturaProductoProveedorResponse>>() {
                }, 3).getFirst().stock()).isGreaterThanOrEqualTo(50);

        BaseResponse invalido = admin.escribir("blog.crear", HttpMethod.POST, "/admin/blog",
                () -> Map.of("titulo", "sin imagen"), sinLocal("blog.invalido"));
        assertThat(invalido.success()).isFalse();
        assertThat(invalido.mensaje()).startsWith("Dato inválido en '");
    }

    /** Copia mínima de AdminVistas.cuerpo (que es package-private en el paquete de controladores). */
    static final class AdminVistasParaTest {
        static Map<String, Object> cuerpo(Object... kv) {
            Map<String, Object> m = new java.util.LinkedHashMap<>();
            for (int i = 0; i < kv.length; i += 2) {
                m.put((String) kv[i], kv[i + 1]);
            }
            return m;
        }
    }
}
