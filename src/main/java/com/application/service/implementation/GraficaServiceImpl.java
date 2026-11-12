package com.application.service.implementation;

import com.application.persistence.entity.compra.Compra;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.repository.CompraRepository;
import com.application.persistence.repository.DetalleVentaRepository;
import com.application.persistence.repository.FacturaProveedorRepository;
import com.application.persistence.repository.ProductoRepository;
import com.application.presentation.dto.grafica.EstadisticasMensualesResponse;
import com.application.presentation.dto.grafica.GraficaIngresosGastosResponse;
import com.application.presentation.dto.grafica.columnasApiladas.StockCategoriaDTO;
import com.application.presentation.dto.grafica.columnasApiladas.StockComprasResponse;
import com.application.presentation.dto.grafica.columnasApiladas.VentasCategoriaDTO;
import com.application.presentation.dto.grafica.comprasRecientes.CompraResumenResponse;
import com.application.presentation.dto.grafica.historicoVentas.HistoricoVentasResponse;
import com.application.presentation.dto.grafica.historicoVentas.VentasAnualesDTO;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import com.application.presentation.dto.grafica.progresoActual.EstadisticasGeneralesDTO;
import com.application.presentation.dto.grafica.ventasCiudades.CoordenadasCiudadDTO;
import com.application.presentation.dto.grafica.ventasCiudades.MapaVentasResponse;
import com.application.presentation.dto.grafica.ventasCiudades.VentaCiudadMapaDTO;
import com.application.presentation.dto.grafica.ventasCiudades.VentasCiudadDTO;
import com.application.presentation.dto.grafica.ventasTotales.VentasPorEstadoDTO;
import com.application.presentation.dto.grafica.ventasTotales.VentasTotalesResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.GraficaService;
import com.application.configuration.fastapi.FastApiAdmin;
import com.application.configuration.fastapi.FastApiException;
import com.application.persistence.repository.UsuarioRepository;
import com.application.presentation.dto.grafica.DatosPanelResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class GraficaServiceImpl implements GraficaService {

    /** Meses que muestran las gráficas de ingresos/gastos y ventas totales. */
    private static final int MESES_GRAFICAS = 12;

    private final DashboardApiClient dashboardApi;
    private final FastApiAdmin fastApiAdmin;
    private final UsuarioRepository usuarioRepository;
    private final CompraRepository compraRepository;
    private final CloudinaryService cloudinaryService;
    private final ProductoRepository productoRepository;
    private final DetalleVentaRepository detalleVentaRepository;
    private final FacturaProveedorRepository facturaProveedorRepository;

    private final Map<String, CoordenadasCiudadDTO> coordenadasCiudades = Map.ofEntries(
            Map.entry("Bogotá", new CoordenadasCiudadDTO("Bogotá", 4.7110, -74.0721)),
            Map.entry("Medellín", new CoordenadasCiudadDTO("Medellín", 6.2442, -75.5812)),
            Map.entry("Cali", new CoordenadasCiudadDTO("Cali", 3.4516, -76.5320)),
            Map.entry("Barranquilla", new CoordenadasCiudadDTO("Barranquilla", 10.9639, -74.7964)),
            Map.entry("Cartagena", new CoordenadasCiudadDTO("Cartagena", 10.3910, -75.4794)),
            Map.entry("Bucaramanga", new CoordenadasCiudadDTO("Bucaramanga", 7.1193, -73.1227)),
            Map.entry("Cúcuta", new CoordenadasCiudadDTO("Cúcuta", 7.8939, -72.5078)),
            Map.entry("Pereira", new CoordenadasCiudadDTO("Pereira", 4.8087, -75.6906)),
            Map.entry("Santa Marta", new CoordenadasCiudadDTO("Santa Marta", 11.2408, -74.1990)),
            Map.entry("Manizales", new CoordenadasCiudadDTO("Manizales", 5.0703, -75.5138)),
            Map.entry("Armenia", new CoordenadasCiudadDTO("Armenia", 4.5370, -75.6751)),
            Map.entry("Villavicencio", new CoordenadasCiudadDTO("Villavicencio", 4.1420, -73.6266)),
            Map.entry("Ibagué", new CoordenadasCiudadDTO("Ibagué", 4.4447, -75.2422)),
            Map.entry("Neiva", new CoordenadasCiudadDTO("Neiva", 2.9345, -75.2809)),
            Map.entry("Pasto", new CoordenadasCiudadDTO("Pasto", 1.2136, -77.2811)),
            Map.entry("Montería", new CoordenadasCiudadDTO("Montería", 8.7500, -75.8833)),
            Map.entry("Valledupar", new CoordenadasCiudadDTO("Valledupar", 10.4631, -73.2532)),
            Map.entry("Sincelejo", new CoordenadasCiudadDTO("Sincelejo", 9.3047, -75.3978)),
            Map.entry("Popayán", new CoordenadasCiudadDTO("Popayán", 2.4448, -76.6147)),
            Map.entry("Tunja", new CoordenadasCiudadDTO("Tunja", 5.5353, -73.3678))
    );

    // Sin transacción: no retener una conexión del pool mientras se espera a FastAPI.
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public GraficaIngresosGastosResponse obtenerDatosGraficaIngresosGastos() {
        try {
            return dashboardApi.ingresosGastos(MESES_GRAFICAS);
        } catch (FastApiException e) {
            log.warn("Ingresos-gastos desde FastAPI no disponible; se calcula localmente: {}", e.getMessage());
            return calcularIngresosGastosLocal();
        }
    }

    /** Respaldo si FastAPI no responde: mismas consultas que antes de la integración. */
    private GraficaIngresosGastosResponse calcularIngresosGastosLocal() {
        // Obtener datos de ingresos y gastos
        List<EstadisticasMensualesResponse> ingresos = compraRepository.findIngresosMensuales();
        List<EstadisticasMensualesResponse> gastos = facturaProveedorRepository.findGastosMensuales();

        // Combinar y procesar los datos
        return procesarDatosGrafica(ingresos, gastos);
    }

    private GraficaIngresosGastosResponse procesarDatosGrafica(
            List<EstadisticasMensualesResponse> ingresos,
            List<EstadisticasMensualesResponse> gastos) {

        GraficaIngresosGastosResponse resultado = new GraficaIngresosGastosResponse(new ArrayList<>(), new ArrayList<>(), new ArrayList<>());

        // Crear un mapa para combinar los datos usando clave compuesta (año, mes)
        Map<ClaveMes, Double> ingresosMap = new HashMap<>();
        Map<ClaveMes, Double> gastosMap = new HashMap<>();
        Set<ClaveMes> periodos = new TreeSet<>();

        // Procesar ingresos
        for (EstadisticasMensualesResponse ingreso : ingresos) {
            ClaveMes clave = new ClaveMes(ingreso.año(), ingreso.mes());
            ingresosMap.put(clave, ingreso.ingresos());
            periodos.add(clave);
        }

        // Procesar gastos
        for (EstadisticasMensualesResponse gasto : gastos) {
            ClaveMes clave = new ClaveMes(gasto.año(), gasto.mes());
            gastosMap.put(clave, gasto.gastos());
            periodos.add(clave);
        }

        // Convertir a listas ordenadas
        for (ClaveMes periodo : periodos) {
            String nombreMesCompleto = obtenerNombreMes(periodo.mes()) + " " + periodo.año();
            resultado.categorias().add(nombreMesCompleto);
            resultado.ingresos().add(ingresosMap.getOrDefault(periodo, 0.0));
            resultado.gastos().add(gastosMap.getOrDefault(periodo, 0.0));
        }

        return resultado;
    }

    private String obtenerNombreMes(int numeroMes) {
        String[] meses = {"Ene", "Feb", "Mar", "Abr", "May", "Jun",
                "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
        return meses[numeroMes - 1];
    }

    private record ClaveMes(int año, int mes) implements Comparable<ClaveMes> {
        @Override
        public int compareTo(ClaveMes otro) {
            if (this.año != otro.año) {
                return Integer.compare(this.año, otro.año);
            }
            return Integer.compare(this.mes, otro.mes);
        }
    }

    // Sin transacción: no retener una conexión del pool mientras se espera a FastAPI.
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public VentasTotalesResponse obtenerVentasTotalesUltimos12Meses() {
        try {
            return dashboardApi.ventasTotales(MESES_GRAFICAS);
        } catch (FastApiException e) {
            log.warn("Ventas totales desde FastAPI no disponible; se calcula localmente: {}", e.getMessage());
            return calcularVentasTotalesLocal();
        }
    }

    private VentasTotalesResponse calcularVentasTotalesLocal() {
        LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(12);

        List<VentasPorEstadoDTO> ventasPorEstado = compraRepository.findVentasPorEstadoUltimos12Meses(fechaInicio);

        return procesarDatosVentasTotales(ventasPorEstado);
    }

    private VentasTotalesResponse procesarDatosVentasTotales(List<VentasPorEstadoDTO> ventasPorEstado) {
        // Inicializar valores
        double totalPagadas = 0.0;
        double totalCanceladas = 0.0;
        double totalRechazadas = 0.0;
        double totalPendientes = 0.0;

        // Sumar por estado
        for (VentasPorEstadoDTO venta : ventasPorEstado) {
            switch (venta.estado()) {
                case "PAGADO":
                    totalPagadas += venta.montoTotal();
                    break;
                case "CANCELADO":
                    totalCanceladas += venta.montoTotal();
                    break;
                case "RECHAZADO":
                    totalRechazadas += venta.montoTotal();
                    break;
                case "PENDIENTE":
                    totalPendientes += venta.montoTotal();
                    break;
            }
        }

        // Calcular total de reembolsos (canceladas + rechazadas)
        double totalReembolsos = totalCanceladas + totalRechazadas;

        // Crear series y labels
        List<Double> series = Arrays.asList(totalPagadas, totalReembolsos, totalPendientes);
        double totalVentas = series.stream().mapToDouble(Double::doubleValue).sum();

        // Formatear labels con porcentajes
        List<String> labels = Arrays.asList(
                formatLabel("Envíos", totalPagadas, totalVentas),
                formatLabel("Reembolsos", totalReembolsos, totalVentas),
                formatLabel("Pedidos", totalPendientes, totalVentas)
        );

        return new VentasTotalesResponse(series, labels, totalVentas);
    }

    private String formatLabel(String tipo, double valor, double total) {
        double porcentaje = total > 0 ? (valor / total) * 100 : 0;
        DecimalFormat df = new DecimalFormat("#,##0.00");
        DecimalFormat pf = new DecimalFormat("0");

        return String.format("%s $%s (%s%%)",
                tipo,
                df.format(valor),
                pf.format(porcentaje));
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public StockComprasResponse obtenerDatosStockCompras() {
        return fastApiAdmin.leer("graficas.stock-compras", "/api/graficas/stock-compras", StockComprasResponse.class, this::calcularStockComprasLocal);
    }

    private StockComprasResponse calcularStockComprasLocal() {
        LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(6);

        List<VentasCategoriaDTO> ventasPorCategoria = detalleVentaRepository.findVentasPorCategoriaUltimosByMeses(fechaInicio);
        List<StockCategoriaDTO> stockPorCategoria = productoRepository.findStockPorCategoria();

        return procesarDatosStockCompras(ventasPorCategoria, stockPorCategoria);
    }

    private StockComprasResponse procesarDatosStockCompras(
            List<VentasCategoriaDTO> ventasPorCategoria,
            List<StockCategoriaDTO> stockPorCategoria) {

        // Crear mapas para fácil acceso
        Map<String, Long> ventasMap = ventasPorCategoria.stream()
                .collect(Collectors.toMap(VentasCategoriaDTO::categoria, VentasCategoriaDTO::unidadesVendidas));

        Map<String, Long> stockMap = stockPorCategoria.stream()
                .collect(Collectors.toMap(StockCategoriaDTO::categoria, StockCategoriaDTO::stockDisponible));

        // Combinar todas las categorías únicas
        Set<String> categoriasUnicas = new LinkedHashSet<>();
        categoriasUnicas.addAll(ventasMap.keySet());
        categoriasUnicas.addAll(stockMap.keySet());

        // Limitar a un máximo de 10 categorías para mejor visualización
        List<String> categorias = categoriasUnicas.stream()
                .limit(10)
                .collect(Collectors.toList());

        List<Long> ventas = new ArrayList<>();
        List<Long> stock = new ArrayList<>();

        // Llenar los arrays en el mismo orden
        for (String categoria : categorias) {
            ventas.add(ventasMap.getOrDefault(categoria, 0L));
            stock.add(stockMap.getOrDefault(categoria, 0L));
        }

        return new StockComprasResponse(categorias, ventas, stock);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public MapaVentasResponse obtenerVentasParaMapa() {
        return fastApiAdmin.leer("graficas.mapa-ventas", "/api/graficas/mapa-ventas", MapaVentasResponse.class, this::calcularMapaVentasLocal);
    }

    private MapaVentasResponse calcularMapaVentasLocal() {
        LocalDateTime fechaInicio = LocalDateTime.now().minusMonths(12);

        List<VentasCiudadDTO> ventasPorCiudad = compraRepository.findVentasPorCiudad(fechaInicio);

        return procesarDatosParaMapa(ventasPorCiudad);
    }

    private MapaVentasResponse procesarDatosParaMapa(List<VentasCiudadDTO> ventasPorCiudad) {
        List<VentaCiudadMapaDTO> ventasMapeadas = new ArrayList<>();

        for (VentasCiudadDTO venta : ventasPorCiudad) {
            // Buscar coordenadas para la ciudad
            CoordenadasCiudadDTO coordenadas = encontrarCoordenadas(venta.ciudad());

            if (coordenadas != null) {
                ventasMapeadas.add(new VentaCiudadMapaDTO(
                        venta.ciudad(),
                        venta.unidadesVendidas(),
                        coordenadas.latitud(),
                        coordenadas.longitud(),
                        venta.empresasCompradoras()
                ));
            }
        }

        // Ordenar por cantidad de ventas (descendente)
        ventasMapeadas.sort((a, b) -> Long.compare(b.ventas(), a.ventas()));

        // Limitar a las 15 ciudades con más ventas para mejor visualización
        if (ventasMapeadas.size() > 15) {
            ventasMapeadas = ventasMapeadas.subList(0, 15);
        }

        return new MapaVentasResponse(ventasMapeadas);
    }

    private CoordenadasCiudadDTO encontrarCoordenadas(String ciudad) {
        // Buscar coincidencia exacta primero
        if (coordenadasCiudades.containsKey(ciudad)) {
            return coordenadasCiudades.get(ciudad);
        }

        // Buscar coincidencias parciales (por si la ciudad tiene nombre diferente en la BD)
        for (Map.Entry<String, CoordenadasCiudadDTO> entry : coordenadasCiudades.entrySet()) {
            if (ciudad.toLowerCase().contains(entry.getKey().toLowerCase()) ||
                    entry.getKey().toLowerCase().contains(ciudad.toLowerCase())) {
                return entry.getValue();
            }
        }

        return null; // No se encontraron coordenadas para esta ciudad
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public HistoricoVentasResponse obtenerHistoricoVentas() {
        return fastApiAdmin.leer("graficas.historico-ventas", "/api/graficas/historico-ventas", HistoricoVentasResponse.class, this::calcularHistoricoVentasLocal);
    }

    private HistoricoVentasResponse calcularHistoricoVentasLocal() {
        List<VentasAnualesDTO> ventasAnuales = compraRepository.findVentasAnuales();

        return procesarDatosParaGraficoLinea(ventasAnuales);
    }

    private HistoricoVentasResponse procesarDatosParaGraficoLinea(List<VentasAnualesDTO> ventasAnuales) {
        List<Double> series = new ArrayList<>();
        List<Integer> categorias = new ArrayList<>();

        double maxVenta = 0;
        double minVenta = Double.MAX_VALUE;
        double sumaVentas = 0;

        // Ordenar por año
        ventasAnuales.sort(Comparator.comparing(VentasAnualesDTO::año));

        for (VentasAnualesDTO venta : ventasAnuales) {
            double ventaTotal = venta.ventasTotales() != null ? venta.ventasTotales() : 0;

            series.add(ventaTotal);
            categorias.add(venta.año());

            // Calcular estadísticas
            if (ventaTotal > maxVenta) maxVenta = ventaTotal;
            if (ventaTotal < minVenta) minVenta = ventaTotal;
            sumaVentas += ventaTotal;
        }

        double promedioVentas = !series.isEmpty() ? sumaVentas / series.size() : 0;

        if (minVenta == Double.MAX_VALUE) {
            minVenta = 0;
        }

        // Generar datos suavizados con subpicos (como en el ejemplo original)
        if (series.size() > 1) {
            return generarDatosConSubpicos(series, categorias, maxVenta, minVenta, promedioVentas);
        }

        return new HistoricoVentasResponse(series, categorias, maxVenta, minVenta, promedioVentas);
    }

    private HistoricoVentasResponse generarDatosConSubpicos(List<Double> seriesBase, List<Integer> categoriasBase,
                                                            double maxVenta, double minVenta, double promedioVentas) {
        List<Double> seriesConSubpicos = new ArrayList<>();
        List<Integer> categoriasConSubpicos = new ArrayList<>();

        Random random = new Random(12345); // Semilla fija para consistencia

        for (int i = 0; i < seriesBase.size(); i++) {
            double valorBase = seriesBase.get(i);
            int añoBase = categoriasBase.get(i);

            // Agregar punto principal
            seriesConSubpicos.add(valorBase);
            categoriasConSubpicos.add(añoBase * 1000); // Multiplicar para preservar decimales

            // Si no es el último año, generar subpicos
            if (i < seriesBase.size() - 1) {
                double valorSiguiente = seriesBase.get(i + 1);
                int añoSiguiente = categoriasBase.get(i + 1);

                // Generar 8-12 subpicos entre años
                int numSubpicos = 10;
                for (int j = 1; j <= numSubpicos; j++) {
                    double progresion = j / (double)(numSubpicos + 1);
                    double añoIntermedio = añoBase + progresion;
                    double valorInterpolado = valorBase + (valorSiguiente - valorBase) * progresion;

                    // Agregar variación aleatoria para los subpicos
                    double variacion = (random.nextDouble() * 0.15 - 0.075) * valorBase;
                    double valorConSubpico = valorInterpolado + variacion;

                    // Limitar valores dentro de rangos razonables
                    valorConSubpico = Math.max(valorConSubpico * 0.8, Math.min(valorConSubpico * 1.2, valorConSubpico));

                    seriesConSubpicos.add(valorConSubpico);
                    categoriasConSubpicos.add((int)(añoIntermedio * 1000));
                }
            }
        }

        return new HistoricoVentasResponse(seriesConSubpicos, categoriasConSubpicos, maxVenta, minVenta, promedioVentas);
    }

    /** Panel principal: una sola llamada a FastAPI ({@code /api/graficas/dashboard}); local si no responde. */
    @Override
    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    public DatosPanelResponse obtenerDatosPanel() {
        return fastApiAdmin.leer("graficas.dashboard", "/api/graficas/dashboard", DatosPanelResponse.class,
                () -> {
                    Double ingresos = compraRepository.getIngresosAnuales();
                    Long compras = compraRepository.getTotalComprasAnuales();
                    Long clientes = usuarioRepository.totalClientes();
                    return new DatosPanelResponse(
                            ingresos != null ? ingresos : 0.0,
                            compras != null ? compras : 0L,
                            clientes != null ? clientes : 0L,
                            obtenerEstadisticasGenerales(),
                            getTopProductosMasVendidos(),
                            getComprasRecientes());
                });
    }

    @Override
    public EstadisticasGeneralesDTO obtenerEstadisticasGenerales() {
        // Obtener datos actuales
        Double ingresosActuales = compraRepository.findIngresosSemestreActual();
        Double gastosActuales = facturaProveedorRepository.findGastosSemestreActual();
        Long stockActual = productoRepository.findStockTotal();
        Double beneficioActual = ingresosActuales - gastosActuales;

        // Obtener datos del semestre anterior para proyecciones
        Double ingresosAnterior = compraRepository.findIngresosSemestreAnterior();
        Double gastosAnterior = facturaProveedorRepository.findGastosSemestreAnterior();
        Double beneficioAnterior = ingresosAnterior - gastosAnterior;

        // Calcular proyecciones para el semestre actual (duplicar el semestre anterior)
        Double proyeccionIngresosAnual = ingresosAnterior * 1.05;
        Double proyeccionGastosAnual = gastosAnterior * 1.05;
        Double proyeccionBeneficioAnual = beneficioAnterior * 1.05;

        // Objetivo de stock basado en crecimiento del 5% respecto al semestre anterior
        // Para simular un objetivo, usaremos un 5% más que el stock actual
        Long objetivoStock = (long) (stockActual * 1.05);

        // Calcular porcentajes de progreso (cuánto llevamos vs proyección anual)
        Integer progresoIngresos = calcularProgreso(ingresosActuales, proyeccionIngresosAnual);
        Integer progresoGastos = calcularProgreso(gastosActuales, proyeccionGastosAnual);
        Integer progresoBeneficio = calcularProgreso(beneficioActual, proyeccionBeneficioAnual);
        Integer progresoStock = calcularProgreso(stockActual.doubleValue(), objetivoStock.doubleValue());

        // Calcular variaciones respecto al semestre anterior
        Double variacionIngresos = calcularVariacion(ingresosActuales, ingresosAnterior);
        Double variacionGastos = calcularVariacion(gastosActuales, gastosAnterior);
        Double variacionBeneficio = calcularVariacion(beneficioActual, beneficioAnterior);

        // Para stock, comparar con objetivo
        Double variacionStock = calcularVariacion(stockActual.doubleValue(), objetivoStock.doubleValue());

        return new EstadisticasGeneralesDTO(
                ingresosActuales,
                gastosActuales,
                beneficioActual,
                stockActual,
                proyeccionIngresosAnual,
                proyeccionGastosAnual,
                proyeccionBeneficioAnual,
                objetivoStock,
                progresoIngresos,
                progresoGastos,
                progresoBeneficio,
                progresoStock,
                variacionIngresos,
                variacionGastos,
                variacionBeneficio,
                variacionStock
        );
    }

    private Integer calcularProgreso(Double valorActual, Double proyeccionAnual) {
        if (proyeccionAnual == 0) return 0;
        double progreso = (valorActual / proyeccionAnual) * 100;
        return (int) Math.min(Math.max(progreso, 0), 100); // Limitar entre 0-100
    }

    private Double calcularVariacion(Double valorActual, Double valorAnterior) {
        if (valorAnterior == 0) return 0.0;
        return ((valorActual - valorAnterior) / valorAnterior) * 100;
    }

    @Override
    public List<ProductoMasVendidoResponse> getTopProductosMasVendidos() {
        List<ProductoMasVendidoResponse> productos = productoRepository.findTopProductosMasVendidos();
        return productos.stream()
                .map(producto -> new ProductoMasVendidoResponse(
                        producto.productoId(),
                        producto.nombre(),
                        cloudinaryService.getImagenUrl(producto.imagen()),
                        producto.precio(),
                        producto.totalVentas()
                ))
                .toList();
    }

    @Override
    public List<CompraResumenResponse> getComprasRecientes() {
        List<Compra> compras = compraRepository.findAllComprasWithUsuarioOrderByFechaDesc();
        return compras.stream()
                .map(compra -> {
                    Usuario usuario = compra.getUsuario();
                    String nombreCompleto = usuario.getNombres() + " " + usuario.getApellidos();

                    return new CompraResumenResponse(
                            compra.getCompraId(),
                            "ORD-" + String.format("%04d", compra.getCompraId()),
                            nombreCompleto,
                            compra.getFecha(),
                            compra.getTotal(),
                            compra.getEstado().getDescripcion()
                    );
                })
                .limit(4)
                .toList();
    }
}
