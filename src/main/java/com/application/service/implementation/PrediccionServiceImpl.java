package com.application.service.implementation;

import com.application.persistence.repository.CompraRepository;
import com.application.presentation.dto.DatosGraficaResponse;
import com.application.presentation.dto.ProyeccionMensualRequest;
import com.application.presentation.dto.venta.request.VentaRequest;
import com.application.service.interfaces.PrediccionService;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import weka.classifiers.Classifier;
import weka.core.Instance;
import weka.core.DenseInstance;
import weka.core.Instances;
import weka.core.SerializationHelper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.Year;
import java.util.*;

@Service
@Slf4j
public class PrediccionServiceImpl implements PrediccionService {

    private CompraRepository compraRepository;
    private Classifier modelo;
    private Instances estructura;

    @Autowired
    public PrediccionServiceImpl(CompraRepository compraRepository) {
        this.compraRepository = compraRepository;
    }

    @PostConstruct
    public void init() {
        try {
            // Cargar modelo WEKA
            modelo = (Classifier) SerializationHelper.read(
                    getClass().getResourceAsStream("/modelo/costa_de_oro_model.model")
            );

            // Cargar estructura del dataset ARFF
            estructura = new Instances(
                    new BufferedReader(new InputStreamReader(
                            getClass().getResourceAsStream("/modelo/costa_de_oro_weka.arff")
                    ))
            );
            estructura.setClassIndex(0); // venta_total

            log.info("Modelo WEKA cargado exitosamente");
        } catch (Exception e) {
            log.warn("No se pudo cargar el modelo WEKA, se usarán datos simulados: {}", e.getMessage());
        }
    }

    @Override
    public DatosGraficaResponse obtenerDatosParaGrafica() {
        Map<String, double[]> ventasPorAño = new HashMap<>();
        Map<String, Object> estadisticas = new HashMap<>();
        List<ProyeccionMensualRequest> proyeccion2026 = new ArrayList<>();

        try {
            // Obtener datos reales de los últimos 6 años
            int añoActual = Year.now().getValue();
            List<Object[]> ventasReales = compraRepository.findVentasPorRangoDeAnios(añoActual - 5, añoActual);

            // Procesar datos reales
            Map<Integer, Map<Integer, Double>> datosPorAño = new HashMap<>();

            for (Object[] venta : ventasReales) {
                Integer año = ((Number) venta[0]).intValue();
                Integer mes = ((Number) venta[1]).intValue();
                Double total = ((Number) venta[2]).doubleValue();

                datosPorAño.putIfAbsent(año, new HashMap<>());
                datosPorAño.get(año).put(mes, total);
            }

            // Convertir a formato esperado por el frontend
            for (int año = añoActual - 5; año <= añoActual; año++) {
                double[] ventasAnuales = new double[12];
                Map<Integer, Double> datosAño = datosPorAño.get(año);

                for (int mes = 1; mes <= 12; mes++) {
                    if (datosAño != null && datosAño.containsKey(mes)) {
                        ventasAnuales[mes-1] = datosAño.get(mes);
                    } else {
                        ventasAnuales[mes-1] = 0;
                    }
                }
                ventasPorAño.put(String.valueOf(año), ventasAnuales);
            }

            log.info("Datos de ventas cargados para {} años", ventasPorAño.size());

            // Calcular estadísticas
            calcularEstadisticas(ventasPorAño, estadisticas);

            // Generar proyección vacía (se generará bajo demanda)
            proyeccion2026 = new ArrayList<>();

        } catch (Exception e) {
            log.error("Error obteniendo datos reales, usando datos simulados: {}", e.getMessage());
            // Fallback a datos simulados
            return obtenerDatosSimulados();
        }

        return new DatosGraficaResponse(ventasPorAño, estadisticas, proyeccion2026);
    }

    @Override
    public double predecir(VentaRequest request) throws Exception {
        // Validar datos de entrada
        if (request.anno() < 2019 || request.anno() > 2030) {
            throw new IllegalArgumentException("Año fuera del rango válido (2019-2030)");
        }

        if (request.mes() < 1 || request.mes() > 12) {
            throw new IllegalArgumentException("Mes fuera del rango válido (1-12)");
        }

        // Si el modelo WEKA está disponible, usarlo
        if (modelo != null && estructura != null) {
            try {
                // Crear una instancia siguiendo la estructura ARFF
                Instance instancia = new DenseInstance(estructura.numAttributes());
                instancia.setDataset(estructura);

                // El atributo 0 (venta_total) es la clase, se marca como missing para predicción
                instancia.setMissing(0);
                instancia.setValue(1, request.anno());
                instancia.setValue(2, request.mes());
                instancia.setValue(3, request.cantidadProductos());
                instancia.setValue(4, request.totalUnidades());
                instancia.setValue(5, request.precioPromedio());

                // Realizar predicción
                double prediccion = modelo.classifyInstance(instancia);

                log.info("Predicción WEKA para {}/{}: productos={}, unidades={}, precio={} => prediccion={}",
                        request.mes(), request.anno(), request.cantidadProductos(),
                        request.totalUnidades(), request.precioPromedio(), prediccion);

                // Validar que la predicción sea positiva
                if (prediccion < 0) {
                    log.warn("Predicción negativa del modelo: {}. Ajustando a valor mínimo.", prediccion);
                    prediccion = 10000;
                }

                // Aplicar límite basado en datos históricos
                try {
                    DatosGraficaResponse datosHistoricos = obtenerDatosParaGrafica();
                    double picoMasAlto = 0;

                    for (double[] ventasAnuales : datosHistoricos.ventasPorAño().values()) {
                        for (double venta : ventasAnuales) {
                            if (venta > picoMasAlto) {
                                picoMasAlto = venta;
                            }
                        }
                    }

                    // Aplicar límite razonable: hasta 50% más que el pico histórico
                    double limiteMaximo = picoMasAlto * 1.5;
                    if (prediccion > limiteMaximo && picoMasAlto > 0) {
                        log.info("Predicción ajustada: {} → {} (límite del 150% del pico histórico {})",
                                prediccion, limiteMaximo, picoMasAlto);
                        prediccion = limiteMaximo;
                    }
                } catch (Exception e) {
                    log.warn("No se pudieron obtener datos históricos para validar: {}", e.getMessage());
                }

                return Math.round(prediccion * 100.0) / 100.0;

            } catch (Exception e) {
                log.error("Error en predicción WEKA: {}. Usando cálculo alternativo.", e.getMessage());
            }
        }

        // Fallback: usar cálculo basado en datos históricos
        return calcularPrediccionFallback(request);
    }

    /**
     * Calcula una predicción basada en datos históricos cuando el modelo WEKA no está disponible
     */
    private double calcularPrediccionFallback(VentaRequest request) {
        try {
            DatosGraficaResponse datos = obtenerDatosParaGrafica();
            Map<String, double[]> ventas = datos.ventasPorAño();

            // Obtener ventas históricas para el mes solicitado
            double[] ventas2023 = ventas.getOrDefault("2023", new double[12]);
            double[] ventas2024 = ventas.getOrDefault("2024", new double[12]);
            double[] ventas2025 = ventas.getOrDefault("2025", new double[12]);

            int mesIndex = request.mes() - 1;

            // Calcular promedio histórico del mes
            double suma = 0;
            int count = 0;

            if (ventas2023[mesIndex] > 0) { suma += ventas2023[mesIndex]; count++; }
            if (ventas2024[mesIndex] > 0) { suma += ventas2024[mesIndex]; count++; }
            if (ventas2025[mesIndex] > 0) { suma += ventas2025[mesIndex]; count++; }

            double promedioMes = count > 0 ? suma / count : 100000;

            // Aplicar crecimiento del 8% para 2026
            double prediccion = promedioMes * 1.08;

            return Math.round(prediccion * 100.0) / 100.0;

        } catch (Exception e) {
            log.error("Error en cálculo fallback: {}", e.getMessage());
            return 100000.0; // Valor por defecto razonable
        }
    }

    @Override
    public Map<String, Object> generarPrediccion2026() {
        Map<String, Object> resultado = new HashMap<>();
        List<ProyeccionMensualRequest> proyeccion = new ArrayList<>();

        try {
            DatosGraficaResponse datos = obtenerDatosParaGrafica();
            Map<String, double[]> ventas = datos.ventasPorAño();

            String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

            // Obtener datos históricos para calcular tendencias
            Map<Integer, Double> tendenciasPorMes = calcularTendenciasPorMes(ventas);
            Map<Integer, Double> factoresEstacionales = calcularFactoresEstacionales(ventas);

            for (int mes = 1; mes <= 12; mes++) {
                double prediccion;

                if (modelo != null) {
                    // Usar el modelo WEKA para predicción real con parámetros específicos por mes
                    try {
                        // Calcular parámetros basados en el mes y tendencias históricas
                        VentaRequest request = crearRequestParaMes(2026, mes, ventas, tendenciasPorMes);
                        prediccion = predecir(request); // CORRECCIÓN: No dividir entre 1000

                        // Aplicar factor estacional para diferenciar meses
                        double factorEstacional = factoresEstacionales.getOrDefault(mes, 1.0);
                        prediccion = prediccion * factorEstacional;

                    } catch (Exception e) {
                        log.warn("Error usando WEKA para mes {}, usando cálculo avanzado: {}", mes, e.getMessage());
                        prediccion = calcularPrediccionAvanzada(ventas, mes, tendenciasPorMes, factoresEstacionales);
                    }
                } else {
                    // Fallback a cálculo avanzado basado en tendencias y estacionalidad
                    prediccion = calcularPrediccionAvanzada(ventas, mes, tendenciasPorMes, factoresEstacionales);
                }

                // CORRECCIÓN: Asegurar que la predicción sea realista basada en datos históricos
                prediccion = ajustarPrediccionRealista(prediccion, ventas, mes);

                proyeccion.add(new ProyeccionMensualRequest(
                        mes, nombresMeses[mes-1], Math.round(prediccion * 100.0) / 100.0
                ));
            }

            resultado.put("proyeccion", proyeccion);
            resultado.put("success", true);
            resultado.put("mensaje", "Predicción 2026 generada exitosamente con variaciones mensuales");

        } catch (Exception e) {
            log.error("Error generando predicción 2026: {}", e.getMessage());
            resultado.put("success", false);
            resultado.put("mensaje", "Error generando predicción: " + e.getMessage());
        }

        return resultado;
    }

    // MÉTODO MODIFICADO: Ajustar predicción para que sea realista y no supere máximos históricos
    private double ajustarPrediccionRealista(double prediccion, Map<String, double[]> ventas, int mes) {
        // 1. Encontrar el pico MÁS ALTO de toda la historia (máximo global)
        double picoMasAlto = 0;
        for (double[] ventasAnuales : ventas.values()) {
            for (double venta : ventasAnuales) {
                if (venta > picoMasAlto) {
                    picoMasAlto = venta;
                }
            }
        }

        // Si no hay datos históricos, usar valores por defecto razonables
        if (picoMasAlto == 0) {
            picoMasAlto = 50000000; // Máximo razonable por defecto
        }

        // 2. Obtener ventas históricas recientes para este mes específico
        double[] ventas2023 = ventas.getOrDefault("2023", new double[12]);
        double[] ventas2024 = ventas.getOrDefault("2024", new double[12]);
        double[] ventas2025 = ventas.getOrDefault("2025", new double[12]);

        double maxHistoricoMes = Math.max(
                Math.max(ventas2023[mes-1], ventas2024[mes-1]),
                ventas2025[mes-1]
        );

        double minHistoricoMes = Math.min(
                Math.min(ventas2023[mes-1] > 0 ? ventas2023[mes-1] : Double.MAX_VALUE,
                        ventas2024[mes-1] > 0 ? ventas2024[mes-1] : Double.MAX_VALUE),
                ventas2025[mes-1] > 0 ? ventas2025[mes-1] : Double.MAX_VALUE
        );

        // Si no hay datos históricos para este mes, usar promedios
        if (minHistoricoMes == Double.MAX_VALUE) {
            minHistoricoMes = 10000; // Mínimo razonable
            maxHistoricoMes = picoMasAlto * 0.3; // 30% del pico máximo como tope para mes sin datos
        }

        // 3. REGLA PRINCIPAL: La predicción NUNCA puede superar el pico más alto histórico
        double prediccionMaximaAbsoluta = picoMasAlto;

        // 4. Pero puede bajar naturalmente (usar mínimo histórico del mes como base inferior)
        double prediccionMinima = minHistoricoMes * 0.7; // Permite bajar hasta 70% del mínimo histórico del mes

        // 5. Aplicar restricciones
        prediccion = Math.max(prediccionMinima, Math.min(prediccionMaximaAbsoluta, prediccion));

        // 6. Asegurar variación mensual realista (la gráfica puede subir y bajar)
        // pero manteniéndose dentro de rangos históricos para cada mes
        if (prediccion > maxHistoricoMes * 1.2) {
            // Si se aleja mucho del máximo histórico del mes, acercarlo
            prediccion = maxHistoricoMes * 1.2;
        }

        log.info("Mes {}: Predicción ajustada = {}, Máximo histórico mes = {}, Pico más alto = {}",
                mes, prediccion, maxHistoricoMes, picoMasAlto);

        return Math.round(prediccion * 100.0) / 100.0;
    }

    // Método para crear request específico por mes
    private VentaRequest crearRequestParaMes(int año, int mes, Map<String, double[]> ventas,
                                             Map<Integer, Double> tendencias) {
        // Calcular parámetros basados en datos históricos y el mes específico
        double[] ventas2023 = ventas.getOrDefault("2023", new double[12]);
        double[] ventas2024 = ventas.getOrDefault("2024", new double[12]);
        double[] ventas2025 = ventas.getOrDefault("2025", new double[12]);

        // Promedio histórico para este mes
        double promedioMes = (ventas2023[mes-1] + ventas2024[mes-1] + ventas2025[mes-1]) / 3.0;

        // Calcular cantidad de productos basada en ventas históricas
        int cantidadProductos = calcularCantidadProductos(promedioMes, mes);
        int totalUnidades = calcularTotalUnidades(cantidadProductos, mes);
        double precioPromedio = calcularPrecioPromedio(mes);

        return new VentaRequest(año, mes, cantidadProductos, totalUnidades, precioPromedio);
    }

    // MÉTODO MODIFICADO: Cálculo más conservador que respete máximos históricos
    private double calcularPrediccionAvanzada(Map<String, double[]> ventas, int mes,
                                              Map<Integer, Double> tendencias,
                                              Map<Integer, Double> factoresEstacionales) {
        double[] ventas2023 = ventas.getOrDefault("2023", new double[12]);
        double[] ventas2024 = ventas.getOrDefault("2024", new double[12]);
        double[] ventas2025 = ventas.getOrDefault("2025", new double[12]);

        // Usar el MÁXIMO histórico de este mes como base en lugar del promedio
        double maxHistoricoMes = Math.max(
                Math.max(ventas2023[mes-1], ventas2024[mes-1]),
                ventas2025[mes-1]
        );

        // Si no hay datos, usar cálculo alternativo
        if (maxHistoricoMes == 0) {
            double promedioHistorico = (ventas2023[mes-1] + ventas2024[mes-1] + ventas2025[mes-1]) / 3.0;
            maxHistoricoMes = promedioHistorico > 0 ? promedioHistorico : 10000;
        }

        // Obtener tendencia para este mes (más conservadora)
        double tendencia = tendencias.getOrDefault(mes, 1.05); // 5% por defecto en lugar de 8%

        // Obtener factor estacional
        double factorEstacional = factoresEstacionales.getOrDefault(mes, 1.0);

        // Calcular predicción basada en máximo histórico con crecimiento moderado
        double prediccion = maxHistoricoMes * tendencia * factorEstacional;

        return Math.round(prediccion * 100.0) / 100.0;
    }

    // Calcular tendencias de crecimiento por mes
    private Map<Integer, Double> calcularTendenciasPorMes(Map<String, double[]> ventas) {
        Map<Integer, Double> tendencias = new HashMap<>();
        double[] ventas2023 = ventas.getOrDefault("2023", new double[12]);
        double[] ventas2024 = ventas.getOrDefault("2024", new double[12]);
        double[] ventas2025 = ventas.getOrDefault("2025", new double[12]);

        for (int mes = 1; mes <= 12; mes++) {
            // Calcular crecimiento año a año para este mes
            double crecimiento2024 = ventas2023[mes-1] > 0 ? ventas2024[mes-1] / ventas2023[mes-1] : 1.0;
            double crecimiento2025 = ventas2024[mes-1] > 0 ? ventas2025[mes-1] / ventas2024[mes-1] : 1.0;

            // Promedio de crecimiento con mínimo del 3% y máximo del 12%
            double crecimientoPromedio = (crecimiento2024 + crecimiento2025) / 2.0;
            double tendencia = Math.max(1.03, Math.min(1.12, crecimientoPromedio));

            tendencias.put(mes, tendencia);
        }

        return tendencias;
    }

    // Calcular factores estacionales basados en patrones históricos
    private Map<Integer, Double> calcularFactoresEstacionales(Map<String, double[]> ventas) {
        Map<Integer, Double> factores = new HashMap<>();

        // Factores estacionales ajustados para ventas en escala real
        double[] factoresBase = {
                0.85,  // Enero: Post-navideño (bajo)
                0.80,  // Febrero: Temporada baja
                0.95,  // Marzo: Inicio escolar
                1.00,  // Abril: Normal
                1.10,  // Mayo: Día de la madre
                1.05,  // Junio: Mitad de año
                1.15,  // Julio: Vacaciones
                1.20,  // Agosto: Temporada alta
                1.10,  // Septiembre: Mes patrio
                1.05,  // Octubre: Halloween
                1.25,  // Noviembre: Black Friday
                1.50   // Diciembre: Navidad (muy alto)
        };

        // Ajustar factores basados en datos históricos reales
        if (!ventas.isEmpty()) {
            double[] ventas2025 = ventas.getOrDefault("2025", new double[12]);
            double promedio2025 = Arrays.stream(ventas2025).average().orElse(1.0);

            for (int mes = 0; mes < 12; mes++) {
                if (promedio2025 > 0 && ventas2025[mes] > 0) {
                    double factorHistorico = ventas2025[mes] / promedio2025;
                    // Combinar factor base con histórico (60% base, 40% histórico)
                    factores.put(mes + 1, (factoresBase[mes] * 0.6) + (factorHistorico * 0.4));
                } else {
                    factores.put(mes + 1, factoresBase[mes]);
                }
            }
        } else {
            for (int mes = 0; mes < 12; mes++) {
                factores.put(mes + 1, factoresBase[mes]);
            }
        }

        return factores;
    }

    // Métodos auxiliares para calcular parámetros del request - AJUSTADOS
    private int calcularCantidadProductos(double promedioVentas, int mes) {
        // Base: 1 producto por cada 50K en ventas (ajustado para escala real)
        double base = promedioVentas / 50000.0;
        double[] ajustesMensuales = {0.9, 0.8, 1.0, 1.0, 1.1, 1.0, 1.2, 1.3, 1.1, 1.0, 1.4, 1.5};
        return Math.max(10, (int) Math.round(base * ajustesMensuales[mes-1]));
    }

    private int calcularTotalUnidades(int cantidadProductos, int mes) {
        // Base: 10 unidades por producto (ajustado para escala real)
        double[] ajustesMensuales = {0.8, 0.7, 1.0, 1.0, 1.2, 1.1, 1.3, 1.4, 1.2, 1.1, 1.5, 1.6};
        return (int) Math.round(cantidadProductos * 10 * ajustesMensuales[mes-1]);
    }

    private double calcularPrecioPromedio(int mes) {
        // Precio promedio base ajustado para escala real
        double precioBase = 500.0;
        double[] ajustesMensuales = {0.95, 0.95, 1.0, 1.0, 1.05, 1.0, 1.08, 1.10, 1.05, 1.0, 1.15, 1.20};
        return precioBase * ajustesMensuales[mes-1];
    }

    // Métodos auxiliares existentes (sin cambios)
    private List<ProyeccionMensualRequest> generarProyeccion2026(Map<String, double[]> ventas) {
        List<ProyeccionMensualRequest> proyeccion = new ArrayList<>();
        String[] nombresMeses = {"Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"};

        for (int mes = 0; mes < 12; mes++) {
            proyeccion.add(new ProyeccionMensualRequest(
                    mes + 1, nombresMeses[mes], 0.0
            ));
        }
        return proyeccion;
    }

    private void calcularEstadisticas(Map<String, double[]> ventasPorAño, Map<String, Object> estadisticas) {
        double totalVentas = 0;
        int cantidadMeses = 0;
        double ventaMaxima = 0;
        double ventaMinima = Double.MAX_VALUE;

        for (double[] ventasAnuales : ventasPorAño.values()) {
            for (double venta : ventasAnuales) {
                if (venta > 0) {
                    totalVentas += venta;
                    cantidadMeses++;
                    ventaMaxima = Math.max(ventaMaxima, venta);
                    ventaMinima = Math.min(ventaMinima, venta);
                }
            }
        }

        estadisticas.put("totalVentas", totalVentas);
        estadisticas.put("promedioMensual", cantidadMeses > 0 ? totalVentas / cantidadMeses : 0);
        estadisticas.put("ventaMaxima", ventaMaxima);
        estadisticas.put("ventaMinima", ventaMinima > 0 && ventaMinima != Double.MAX_VALUE ? ventaMinima : 0);
        estadisticas.put("cantidadMeses", cantidadMeses);
    }

    private DatosGraficaResponse obtenerDatosSimulados() {
        Map<String, double[]> ventasPorAño = new HashMap<>();
        Map<String, Object> estadisticas = new HashMap<>();

        // CORRECCIÓN: Datos simulados en escala real (miles)
        ventasPorAño.put("2020", new double[]{12000, 9500, 14000, 11000, 18000, 13000, 20000, 15000, 22000, 17000, 26000, 21000});
        ventasPorAño.put("2021", new double[]{20000, 18000, 23000, 19000, 26000, 24000, 30000, 25000, 31000, 28000, 33000, 29000});
        ventasPorAño.put("2022", new double[]{15000, 13000, 17000, 16000, 21000, 18000, 25000, 23000, 27000, 24000, 26000, 23000});
        ventasPorAño.put("2023", new double[]{17000, 16000, 19000, 18000, 23000, 20000, 26000, 24000, 30000, 27000, 31000, 29000});
        ventasPorAño.put("2024", new double[]{20000, 22000, 21000, 23000, 26000, 24000, 30000, 28000, 33000, 30000, 35000, 32000});
        ventasPorAño.put("2025", new double[]{22000, 21000, 24000, 20000, 26000, 23000, 29000, 25000, 31000, 28000, 34000, 30000});

        calcularEstadisticas(ventasPorAño, estadisticas);
        return new DatosGraficaResponse(ventasPorAño, estadisticas, new ArrayList<>());
    }

}