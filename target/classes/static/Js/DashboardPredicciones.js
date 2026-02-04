document.addEventListener("DOMContentLoaded", () => {

    // Efecto glassmorphism solo al hacer scroll
    const header = document.querySelector('.content__header');

    window.addEventListener('scroll', () => {
        if (window.scrollY > 10) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });

    //Menú desplegable del perfil
    const subMenu = document.getElementById("SubMenu");
    const profileImage = document.getElementById("user__admin");

    if (subMenu && profileImage) {
        profileImage.addEventListener("click", function (e) {
            e.stopPropagation(); // Evita que el click cierre el menú inmediatamente
            subMenu.classList.toggle("open__menu");
        });

        // Cerrar menú al hacer clic fuera
        document.addEventListener("click", function (e) {
            if (!subMenu.contains(e.target) && !profileImage.contains(e.target)) {
                subMenu.classList.remove("open__menu");
            }
        });
    }

    //abrir la notificaciones del admin
    const notifIcon = document.getElementById("notifIcon");
    const notifMenu = document.getElementById("notifMenu");

    notifIcon.addEventListener("click", (e) => {
        e.stopPropagation();
        notifMenu.classList.toggle("open");
    });

    // cerrar al hacer clic fuera
    document.addEventListener("click", (e) => {
        if (!notifMenu.contains(e.target) && !notifIcon.contains(e.target)) {
            notifMenu.classList.remove("open");
        }
    });

    // Variables globales
    let ventas = {};
    let chart; // Variable global para el gráfico
    let añoActual = new Date().getFullYear();
    let datosOriginales = []; // Guardar los datos originales
    let ultimoAñoCargado = 2025; // Último año con datos completos
    let prediccionGenerada = false; // Controlar si ya se generó predicción

    // Inicializar la aplicación
    inicializarAplicacion();

    async function inicializarAplicacion() {
        await cargarDatosIniciales();
        inicializarGrafica();
    }

    async function cargarDatosIniciales() {
        try {
            const response = await fetch('/admin/prediccion/datos-grafica');
            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }
            const data = await response.json();

            ventas = data.ventasPorAño;
            console.log('Datos cargados desde el servidor:', ventas);
        } catch (error) {
            console.error('Error cargando datos:', error);
            // Usar datos por defecto si hay error - CORREGIDOS: Valores en escala real (sin dividir entre 1000)
            ventas = {
                2020: [12000000, 9500000, 14000000, 11000000, 18000000, 13000000, 20000000, 15000000, 22000000, 17000000, 26000000, 21000000],
                2021: [20000000, 18000000, 23000000, 19000000, 26000000, 24000000, 30000000, 25000000, 31000000, 28000000, 33000000, 29000000],
                2022: [15000000, 13000000, 17000000, 16000000, 21000000, 18000000, 25000000, 23000000, 27000000, 24000000, 26000000, 23000000],
                2023: [17000000, 16000000, 19000000, 18000000, 23000000, 20000000, 26000000, 24000000, 30000000, 27000000, 31000000, 29000000],
                2024: [20000000, 22000000, 21000000, 23000000, 26000000, 24000000, 30000000, 28000000, 33000000, 30000000, 35000000, 32000000],
                2025: [22000000, 21000000, 24000000, 20000000, 26000000, 23000000, 29000000, 25000000, 31000000, 28000000, null, null] // Nov y Dic 2025 vacíos
            };
            console.log('Usando datos simulados por fallo en la conexión');
        }
    }

    function inicializarGrafica() {
        const meses = [
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        ];

        // ========== CREAR PUNTOS MENSUALES ==========
        datosOriginales = [];
        for (const anio in ventas) {
            ventas[anio].forEach((valor, i) => {
                if (valor !== null) { // Solo agregar meses con datos
                    datosOriginales.push({
                        x: `${anio}-${String(i + 1).padStart(2, "0")}`,
                        y: valor,
                        mes: meses[i],
                        anio: anio
                    });
                }
            });
        }

        // ========== SUMAR TOTAL ANUAL ==========
        function totalAnual(año) {
            const datosAño = datosOriginales.filter(item => item.anio === año.toString());
            return datosAño.reduce((acc, item) => acc + item.y, 0);
        }

        var options = {
            chart: {
                type: 'line',
                height: 450,
                zoom: { enabled: true, type: 'x' },
                toolbar: { show: false },
                foreColor: "#333",
                fontFamily: "Geist, Urbanist, sans-serif"
            },

            dataLabels: {
                enabled: false
            },

            series: [{
                name: "Ventas Totales",
                data: datosOriginales
            }],

            stroke: {
                width: 4,
                curve: "smooth",
                colors: ["#0080ff"]
            },
            markers: {
                size: 6,
                colors: ["#0080ff"],
                strokeColors: "#ffffff",
                strokeWidth: 2
            },

            fill: {
                type: "gradient",
                gradient: {
                    shade: "light",
                    type: "vertical",
                    shadeIntensity: 0.5,
                    gradientToColors: ["#0080ff"],
                    inverseColors: false,
                    opacityFrom: 0.8,
                    opacityTo: 0.2,
                    stops: [0, 90, 100]
                }
            },

            xaxis: {
                type: "category",
                title: {
                    text: "Años",
                    offsetY: 5,
                    style: {
                        fontSize: "14px",
                        fontWeight: 600,
                        color: "#333",
                        fontFamily: "Geist, Urbanist, sans-serif"
                    }
                },
                labels: {
                    formatter: function (value, timestamp, index) {
                        if (!value) return "";

                        const mes = value.substring(5, 7);
                        const año = value.substring(0, 4);

                        if (mes === "01") return año;
                        return "";
                    }
                },
                tickAmount: Object.keys(ventas).length * 2
            },

            yaxis: {
                title: {
                    text: "Ventas Totales (COP)",
                    style: {
                        fontSize: "14px",
                        fontWeight: 600,
                        color: "#333",
                        fontFamily: "Geist, Urbanist, sans-serif"
                    }
                },
                labels: {
                    formatter: function (value) {
                        // CORREGIDO: Mostrar en pesos colombianos sin la "K"
                        return `$${Math.round(value).toLocaleString("es-CO")}`;
                    }
                }
            },

            tooltip: {
                custom: function ({ series, seriesIndex, dataPointIndex, w }) {
                    const point = w.config.series[seriesIndex].data[dataPointIndex];
                    const mes = point.mes;
                    const año = point.anio;
                    const valor = point.y;

                    const total = totalAnual(año).toLocaleString("es-CO");

                    return `
                    <div style="
                        background: #ffffff;
                        border-radius: 12px;
                        border: 1px solid #e5e5e5;
                        font-family: Geist, Urbanist, sans-serif;
                        overflow: hidden;
                        min-width: 180px;
                        box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                    ">
                        <div style="
                            background: #f2f4f7;
                            padding: 8px 12px;
                            font-size: 14px;
                            font-weight: 600;
                            color: #333;
                            border-bottom: 1px solid #e1e1e1;
                        ">
                            ${mes} ${año}
                        </div>
                        <div style="
                            padding: 8px 12px 10px 12px;
                            display: flex;
                            flex-direction: column;
                            gap: 4px;
                        ">
                            <div style="
                                display: flex;
                                align-items: center;
                                gap: 6px;
                                font-size: 14px;
                                color: #222;
                            ">
                                <div style="
                                    width: 10px;
                                    height: 10px;
                                    background: #0080ff;
                                    border-radius: 50%;
                                "></div>
                                <span style="font-weight: 500;">Ventas del mes:</span>
                                <span style="font-weight: 700;">$${Math.round(valor).toLocaleString("es-CO")}</span>
                            </div>
                            <div style="
                                display: flex;
                                align-items: center;
                                gap: 6px;
                                font-size: 14px;
                                color: #444;
                            ">
                                <div style="
                                    width: 10px;
                                    height: 10px;
                                    background: #0080ff;
                                    border-radius: 50%;
                                "></div>
                                <span style="font-weight: 500;">Total del año:</span>
                                <span style="font-weight: 700;">$${total}</span>
                            </div>
                        </div>
                    </div>
                `;
                }
            },

            responsive: [{
                breakpoint: 768,
                options: {
                    chart: {
                        height: 350
                    },
                    stroke: {
                        width: 3
                    },
                    markers: {
                        size: 4
                    }
                }
            }]
        };

        chart = new ApexCharts(document.querySelector("#chartVentas"), options);
        chart.render();

        // ======================== GENERAR PREDICCIÓN ========================
        document.getElementById("btnPrediccion").addEventListener("click", () => {
            if (!prediccionGenerada) {
                generarPrediccionAutomatica();
            } else {
                mostrarMensajeError('La predicción ya fue generada anteriormente');
            }
        });
    }

    function generarPrediccionAutomatica() {
        // Mostrar loading
        const overlay = document.getElementById("loadingPrediccion");
        const progress = document.getElementById("progressBar");
        const btn = document.getElementById("btnPrediccion");

        overlay.style.display = "flex";
        btn.disabled = true;

        let width = 0;
        const interval = setInterval(() => {
            if (width >= 90) {
                clearInterval(interval);
                generarPrediccionCompleta();
            } else {
                width += 2;
                progress.style.width = width + "%";
            }
        }, 40);
    }

    async function generarPrediccionCompleta() {
        try {
            console.log('Generando predicción completa automáticamente...');

            // CORREGIDO: Valores en pesos colombianos reales para Noviembre y Diciembre 2025
            const noviembre2025 = 13500000;
            const diciembre2025 = 20500000;

            console.log(`Completando 2025: Noviembre=$${noviembre2025.toLocaleString("es-CO")}, Diciembre=$${diciembre2025.toLocaleString("es-CO")}`);

            // 1. Actualizar datos de Noviembre y Diciembre del último año (2025)
            const nuevosDatos = [...datosOriginales];

            // Remover meses existentes de Noviembre y Diciembre 2025 si los hay
            const datosFiltrados = nuevosDatos.filter(item =>
                !(item.anio === ultimoAñoCargado.toString() && (item.mes === "Noviembre" || item.mes === "Diciembre"))
            );

            // Agregar nuevos datos de Noviembre y Diciembre 2025 automáticamente
            datosFiltrados.push({
                x: `${ultimoAñoCargado}-11`,
                y: noviembre2025,
                mes: "Noviembre",
                anio: ultimoAñoCargado.toString()
            });

            datosFiltrados.push({
                x: `${ultimoAñoCargado}-12`,
                y: diciembre2025,
                mes: "Diciembre",
                anio: ultimoAñoCargado.toString()
            });

            // 2. Generar predicción para el siguiente año (2026)
            const añoPrediccion = ultimoAñoCargado + 1;
            const prediccionData = await generarPrediccionSiguienteAño(añoPrediccion, noviembre2025, diciembre2025);

            // CORRECCIÓN: Validar que la predicción tenga datos
            if (!prediccionData || prediccionData.length === 0) {
                throw new Error('No se recibieron datos de predicción');
            }

            console.log('Datos de predicción 2026 recibidos:', prediccionData);

            // 3. Combinar todos los datos (históricos + 2025 completo + 2026 predicción)
            const datosCombinados = [...datosFiltrados, ...prediccionData];

            console.log('Datos combinados para gráfica:', datosCombinados);

            // 4. Actualizar la gráfica
            chart.updateSeries([{
                name: "Ventas Totales + Predicción",
                data: datosCombinados
            }]);

            // 5. Actualizar variable global y marcar como generada
            ultimoAñoCargado = añoPrediccion;
            prediccionGenerada = true;

            // Deshabilitar el botón permanentemente
            const btn = document.getElementById("btnPrediccion");
            btn.disabled = true;
            btn.style.opacity = "0.6";
            btn.style.cursor = "not-allowed";
            btn.textContent = "Predicción Generada";

            // Mostrar mensaje de éxito
            mostrarMensajeExito(`¡Predicción generada exitosamente para ${añoPrediccion}! Se completaron Noviembre y Diciembre 2025 y se proyectó todo 2026.`);

        } catch (error) {
            console.error('Error generando predicción:', error);
            mostrarMensajeError('Error generando predicción: ' + error.message);

            // Rehabilitar el botón si hay error
            const btn = document.getElementById("btnPrediccion");
            btn.disabled = false;
        } finally {
            // Ocultar loading
            const overlay = document.getElementById("loadingPrediccion");
            const progress = document.getElementById("progressBar");

            overlay.style.display = "none";
            progress.style.width = "0%";
        }
    }

    async function generarPrediccionSiguienteAño(añoPrediccion, noviembre, diciembre) {
        try {
            console.log('Solicitando predicción del año', añoPrediccion, 'al servidor...');

            const response = await fetch('/admin/prediccion/prediccion-2026', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                }
            });

            if (!response.ok) {
                throw new Error(`Error HTTP: ${response.status}`);
            }

            const result = await response.json();
            console.log('Respuesta completa del servidor:', result);

            if (result.success && result.proyeccion) {
                const meses = [
                    "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
                    "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
                ];

                // CORRECCIÓN: Verificar la estructura real de los datos
                console.log('Estructura de proyección:', result.proyeccion);

                return result.proyeccion.map(item => {
                    // Diferentes posibles nombres de campo según el backend
                    const valor = item.ventaPredicha || item.valor || item.total || item.prediccion || item.ventaTotal || 0;

                    console.log(`Procesando mes ${item.mes} (${item.nombreMes}): $${valor}`);

                    return {
                        x: `${añoPrediccion}-${String(item.mes).padStart(2, "0")}`,
                        y: valor,
                        mes: item.nombreMes || meses[item.mes - 1],
                        anio: añoPrediccion.toString()
                    };
                });
            } else {
                throw new Error(result.mensaje || 'Estructura de datos incorrecta');
            }

        } catch (error) {
            console.warn('Error obteniendo predicción del servidor, usando cálculo local:', error);
            return generarPrediccionLocalSiguienteAño(añoPrediccion, noviembre, diciembre);
        }
    }

    function generarPrediccionLocalSiguienteAño(añoPrediccion, noviembre, diciembre) {
        const meses = [
            "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio",
            "Julio", "Agosto", "Septiembre", "Octubre", "Noviembre", "Diciembre"
        ];

        let prediccionData = [];

        // Calcular tendencia basada en los últimos meses
        const promedioUltimosMeses = (noviembre + diciembre) / 2;

        // Factores de estacionalidad mejorados para distribución decreciente
        const factoresEstacionales = [
            0.95,  // Enero
            0.92,  // Febrero
            0.98,  // Marzo
            1.00,  // Abril
            1.05,  // Mayo
            1.02,  // Junio
            1.08,  // Julio
            1.10,  // Agosto
            1.03,  // Septiembre
            1.00,  // Octubre
            1.15,  // Noviembre
            1.25   // Diciembre
        ];

        for (let mes = 1; mes <= 12; mes++) {
            const factor = factoresEstacionales[mes - 1];
            // Aplicar crecimiento moderado del 5% anual + estacionalidad
            const proyeccion = Math.round(promedioUltimosMeses * 1.05 * factor);

            prediccionData.push({
                x: `${añoPrediccion}-${String(mes).padStart(2, "0")}`,
                y: proyeccion,
                mes: meses[mes - 1],
                anio: añoPrediccion.toString()
            });
        }

        console.log('Predicción local generada para año', añoPrediccion, ':', prediccionData);
        return prediccionData;
    }

    function mostrarMensajeExito(mensaje) {
        mostrarMensaje(mensaje, '#4CAF50');
    }

    function mostrarMensajeError(mensaje) {
        mostrarMensaje(mensaje, '#f44336');
    }

    function mostrarMensaje(mensaje, color) {
        // Crear un toast de éxito
        const toast = document.createElement('div');
        toast.style.cssText = `
            position: fixed;
            top: 20px;
            right: 20px;
            background: ${color};
            color: white;
            padding: 12px 20px;
            border-radius: 8px;
            font-family: 'Geist', sans-serif;
            font-weight: 500;
            box-shadow: 0 4px 12px rgba(0,0,0,0.15);
            z-index: 10000;
            animation: slideIn 0.3s ease;
        `;
        toast.textContent = mensaje;

        document.body.appendChild(toast);

        setTimeout(() => {
            toast.style.animation = 'slideOut 0.3s ease';
            setTimeout(() => {
                if (document.body.contains(toast)) {
                    document.body.removeChild(toast);
                }
            }, 300);
        }, 5000);
    }

    // Agregar estilos CSS para las animaciones
    const style = document.createElement('style');
    style.textContent = `
        @keyframes slideIn {
            from { transform: translateX(100%); opacity: 0; }
            to { transform: translateX(0); opacity: 1; }
        }
        @keyframes slideOut {
            from { transform: translateX(0); opacity: 1; }
            to { transform: translateX(100%); opacity: 0; }
        }
    `;
    document.head.appendChild(style);

});