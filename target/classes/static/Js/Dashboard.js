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

    //incrementos en los datos
    function formatNumberCustom(num, hasDollar = false) {
        let str = num.toString();

        if (str.length > 6) {
            // Si es millón o más: coma en millón, punto en miles
            let millones = str.slice(0, str.length - 6);
            let miles = str.slice(str.length - 6, str.length - 3);
            let cientos = str.slice(str.length - 3);
            return (hasDollar ? "$" : "") + millones + "." + miles + "." + cientos;
        } else {
            // Menor al millón: coma normal
            return (hasDollar ? "$" : "") + parseInt(num).toLocaleString("es-CO");
        }
    }

    function animateCounter(element, start, end, duration, hasDollar = false, suffix = "") {
        let startTimestamp = null;
        const step = (timestamp) => {
            if (!startTimestamp) startTimestamp = timestamp;
            const progress = Math.min((timestamp - startTimestamp) / duration, 1);

            // Asegurar que siempre es un entero creciente
            const value = Math.floor(progress * (end - start) + start);

            element.textContent = formatNumberCustom(value, hasDollar) + suffix;

            if (progress < 1) {
                requestAnimationFrame(step);
            }
        };
        requestAnimationFrame(step);
    }

    // Animar todos los .ingresos__total
    document.querySelectorAll(".ingresos__total").forEach((el) => {
        const finalValue = parseInt(el.textContent.replace(/[^0-9]/g, ""));
        const hasDollar = el.textContent.includes("$");
        animateCounter(el, 0, finalValue, 2000, hasDollar);
    });

    // Animar los <strong> (30+ , 35+ etc.)
    document.querySelectorAll(".ingresos__text strong").forEach((el) => {
        const finalValue = parseInt(el.textContent.replace(/\D/g, ""));
        animateCounter(el, 0, finalValue, 1500, false, "+");
    });

    // ===== GRAFICA DE INGRESOS
    cargarGraficaIngresos();

    // ===== GRAFICA DE VENTAS TOTALES
    cargarVentasTotales();

    // ===== GRAFICA DE COLUMNAS APILADAS
    cargarStockCompras();

    // ===== MAPA DE COLOMBIA CON VENTAS POR CIUDAD
    cargarMapaVentas();






    // grafica ventas por horas y dias de la semana
    // Días de la semana
    const days = ["Lun", "Mar", "Mié", "Jue", "Vie", "Sáb", "Dom"];

    // Rangos horarios
    const timeRanges = ["12AM-2AM", "6AM-8AM", "12PM-2PM", "6PM-8PM"];

    // Licores posibles
    const licores = ["Whisky", "Ron", "Cerveza", "Vino", "Tequila", "Vodka"];

    // Colores asignados a cada licor (para tooltip)
    const colorLicores = {
        Whisky: "#d4a017",
        Ron: "#8b0000",
        Cerveza: "#f4c542",
        Vino: "#800080",
        Tequila: "#e68a00",
        Vodka: "#4db8ff"
    };

    // Generar datos simulados de ventas
    const ventasPorHoraYDia = {};
    days.forEach(day => {
        ventasPorHoraYDia[day] = {};
        timeRanges.forEach(range => {
            // Generar ventas por cada licor (número aleatorio)
            const detalleLicores = {};
            licores.forEach(licor => {
                detalleLicores[licor] = Math.floor(Math.random() * 50); // 0–50 ventas
            });

            // Calcular total general
            const total = Object.values(detalleLicores).reduce((a, b) => a + b, 0);

            ventasPorHoraYDia[day][range] = {
                detalle: detalleLicores,
                total: total
            };
        });
    });

    // Crear estructura para ApexCharts
    const seriesData = timeRanges.map(range => ({
        name: range,
        data: days.map(day => ventasPorHoraYDia[day][range].total)
    }));

    // Opciones del gráfico
    const optionsGroupedBar = {
        chart: {
            type: 'bar',
            height: 500,
            width: '100%',
            fontFamily: 'Geist',
            toolbar: { show: false }
        },
        plotOptions: {
            bar: {
                horizontal: false,
                columnWidth: '55%',
                endingShape: 'rounded'
            }
        },
        colors: ["#28a745", "#ffc107", "#dc3545", "#007bff"], // colores pastel
        series: seriesData,
        xaxis: {
            categories: days,
            labels: {
                rotate: -45,
                style: {
                    colors: '#444',
                    fontSize: '13px',
                    fontFamily: 'Geist'
                }
            }
        },
        legend: {
            position: 'top',
            horizontalAlign: 'center',
            labels: {
                colors: '#000',
                useSeriesColors: true,
                fontFamily: 'Geist, sans-serif'
            }
        },
        tooltip: {
            custom: function ({ series, seriesIndex, dataPointIndex, w }) {
                const range = w.config.series[seriesIndex].name;
                const day = w.config.xaxis.categories[dataPointIndex];
                const info = ventasPorHoraYDia[day][range];
                const detalle = info.detalle;

                // Crear tabla HTML estilizada
                let detalleHTML = "";
                Object.entries(detalle).forEach(([licor, cantidad]) => {
                    detalleHTML += `
                    <div style="
                        display: flex;
                        justify-content: space-between;
                        align-items: center;
                        padding: 2px 0;
                        border-bottom: 1px solid #eee;
                    ">
                        <div style="display: flex; align-items: center; gap: 6px;">
                            <span style="
                                display: inline-block;
                                width: 10px;
                                height: 10px;
                                border-radius: 50%;
                                background-color: ${colorLicores[licor]};
                            "></span>
                            <span style="font-weight: 500;">${licor}</span>
                        </div>
                        <span style="color: #555;">${cantidad}</span>
                    </div>
                `;
                });

                return `
                <div style="
                    font-family: Geist, sans-serif;
                    background: #fff;
                    border-radius: 8px;
                    padding: 10px 12px;
                    box-shadow: 0 2px 8px rgba(0,0,0,0.15);
                    min-width: 200px;
                ">
                    <div style="font-weight: 600; color: #333; margin-bottom: 6px;">
                        ${day} (${range})
                    </div>
                    <div style="font-size: 13px; color: #666; margin-bottom: 8px;">
                        Total: <b>${info.total}</b> licores vendidos
                    </div>
                    ${detalleHTML}
                </div>
            `;
            }
        },
        dataLabels: {
            enabled: false
        },
        grid: {
            borderColor: '#eee',
            row: {
                colors: ['#f9f9f9', 'transparent'],
                opacity: 0.5
            }
        }
    };

    // Renderizar gráfico
    const chartGroupedBar = new ApexCharts(document.querySelector("#heatmap"), optionsGroupedBar);
    chartGroupedBar.render();


    // ===== GRAFICA DE HISTÓRICO DE VENTAS
    // ====== Generador pseudoaleatorio determinista ======
//    function seededRandom(seed) {
//        var x = Math.sin(seed) * 10000;
//        return x - Math.floor(x);
//    }
//
//    // ====== GENERADOR DE DATOS CON SUBPICOS EN TODAS LAS LÍNEAS (DET.) ======
//    function generateYearWiseSalesWithContinuousSubPeaks(baseYear, count, yrange, seed = 12345) {
//        var series = [];
//        var lastValue = 1200; // Valor inicial en $
//
//        for (var i = 0; i < count; i++) {
//            var year = baseYear + i;
//
//            // Número de puntos principales (picos grandes) por año
//            var mainPoints = 2;
//            var mainStart = lastValue;
//
//            for (var mp = 0; mp < mainPoints; mp++) {
//                // Pico grande determinista
//                var mainChange = Math.floor(seededRandom(seed + year * 10 + mp) * 350 - 200);
//                var mainValue = mainStart + mainChange;
//
//                if (mainValue > yrange.max) mainValue = yrange.max - seededRandom(seed + year * 20 + mp) * 50;
//                if (mainValue < yrange.min) mainValue = yrange.min + seededRandom(seed + year * 30 + mp) * 50;
//
//                // Subpicos deterministas
//                var subPeaks = 10; // cantidad de micro-picos por tramo
//                for (var sp = 0; sp < subPeaks; sp++) {
//                    var microChange = Math.floor(seededRandom(seed + year * 40 + mp * 10 + sp) * 20 - 10);
//                    var y = mainStart + ((mainValue - mainStart) * (sp + 1) / subPeaks) + microChange;
//
//                    series.push([year + (mp + sp / subPeaks) / mainPoints, y]);
//                }
//
//                mainStart = mainValue;
//            }
//
//            lastValue = mainStart;
//        }
//
//        return series;
//    }
//
//    // ====== DATOS ======
//    var salesData = generateYearWiseSalesWithContinuousSubPeaks(2017, 9, { min: 1000, max: 2500 });
//
//    // ====== OPCIONES DEL CHART ======
//    var optionsProye = {
//        series: [{
//            name: 'Ventas Totales',
//            data: salesData
//        }],
//        chart: {
//            type: 'area',
//            stacked: false,
//            height: 400,
//            zoom: { type: 'x', enabled: true, autoScaleYaxis: true },
//            toolbar: { show: false },
//            fontFamily: 'Geist, sans-serif'
//        },
//        dataLabels: { enabled: false },
//        stroke: { curve: 'straight', width: 3 },
//        markers: { size: 0 },
//        fill: {
//            type: 'gradient',
//            gradient: {
//                shade: 'light',
//                type: 'vertical',
//                shadeIntensity: 1,
//                gradientToColors: ['#00BFFF'],
//                inverseColors: false,
//                opacityFrom: 0.6,
//                opacityTo: 0,
//                stops: [0, 100]
//            }
//        },
//        yaxis: {
//            labels: { formatter: val => `$${val.toFixed(0)}`, style: { fontFamily: 'Geist, sans-serif', fontSize: '14px', fontWeight: 400 } },
//            title: { text: 'Ventas Totales', style: { fontFamily: 'Geist, sans-serif', fontSize: '13px', fontWeight: 700 } }
//        },
//        xaxis: {
//            type: 'numeric',
//            title: { text: 'Años', style: { fontFamily: 'Geist, sans-serif', fontSize: '14px', fontWeight: 700 } },
//            labels: { formatter: val => Math.floor(val), style: { fontFamily: 'Geist, sans-serif', fontSize: '13px' } }
//        },
//        tooltip: { y: { formatter: val => `$${val.toFixed(0)}`, style: { fontFamily: 'Geist, sans-serif' } } }
//    };
//
//    // ====== RENDERIZAR ======
//    var chartProye = new ApexCharts(document.querySelector("#chartVentas"), optionsProye);
//    chartProye.render();
//
//    //Predecir compras

    cargarHistoricoVentas()

});


// ===== GRAFICA DE INGRESOS
async function cargarGraficaIngresos() {
    try {
        const response = await fetch('/api/graficas/ingresos-gastos');
        const datos = await response.json();

        const totalMeses = datos.categorias.length;
        const inicioZoom = Math.max(0, totalMeses - 6);

        // Aplicar estilos CSS dinámicamente
        aplicarEstilosExportacion();

        const options = {
            chart: {
                type: 'area',
                height: 400,
                toolbar: {
                    show: true,
                    tools: {
                        download: true,
                        zoom: false,
                        zoomin: false,
                        zoomout: false,
                        pan: false,
                        reset: false
                    },
                    export: {
                        csv: {
                            filename: `ingresos-gastos-${new Date().toLocaleDateString()}`,
                            columnDelimiter: ',',
                            headerCategory: 'Mes',
                            headerValue: 'Valor',
                        },
                        png: {
                            filename: `ingresos-gastos-${new Date().toLocaleDateString()}`,
                        },
                        svg: {
                            filename: `ingresos-gastos-${new Date().toLocaleDateString()}`,
                        }
                    }
                },
                zoom: {
                    enabled: true,
                    type: 'x',
                    autoScaleYaxis: true
                },
                events: {
                    mounted: function(chartContext, config) {
                        // Asegurar que los estilos se apliquen después de que el chart se monte
                        setTimeout(aplicarEstilosExportacion, 100);
                    }
                }
            },
            series: [
                {
                    name: "Ingresos total",
                    data: datos.ingresos
                },
                {
                    name: "Gastos total",
                    data: datos.gastos
                }
            ],
            xaxis: {
                categories: datos.categorias,
                min: inicioZoom,
                max: totalMeses
            },
            yaxis: {
                min: 0,
                tickAmount: 5,
                labels: {
                    formatter: function (val) {
                        if (val >= 1000000) {
                            return "$" + (val / 1000000).toFixed(1) + "M";
                        } else if (val >= 1000) {
                            return "$" + (val / 1000).toFixed(0) + "k";
                        }
                        return "$" + val.toLocaleString();
                    }
                }
            },
            tooltip: {
                y: {
                    formatter: function (val) {
                        return "$" + val.toLocaleString();
                    }
                }
            },
            stroke: {
                curve: 'smooth',
                width: 2
            },
            colors: ["#28a745", "#ffc107"],
            markers: {
                size: 5,
                hover: {
                    size: 6.5,
                    sizeOffset: 0
                }
            },
            dataLabels: {
                enabled: false
            },
            legend: {
                position: 'top',
                fontFamily: 'Geist, sans-serif'
            }
        };

        const chart = new ApexCharts(document.querySelector("#chart"), options);
        chart.render();

    } catch (error) {
        console.error('Error cargando datos de la gráfica:', error);
        renderizarGraficaConDatosEjemplo();
    }
}

// Estilos para la exportación
function aplicarEstilosExportacion() {
    const style = document.createElement('style');
    style.textContent = `
        .apexcharts-menu {
            font-family: 'Geist', sans-serif !important;
            font-size: 14px !important;
            background: white !important;
            border: 1px solid #e0e0e0 !important;
            border-radius: 8px !important;
            box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1) !important;
            padding: 8px 0 !important;
            min-width: 150px !important;
        }

        .apexcharts-menu-item {
            font-family: 'Geist', sans-serif !important;
            font-size: 14px !important;
            font-weight: 400 !important;
            padding: 8px 16px !important;
            color: #333 !important;
            transition: all 0.2s ease !important;
            cursor: pointer !important;
        }

        .apexcharts-menu-item:hover {
            background-color: #f8f9fa !important;
            color: #000 !important;
        }

        /* También aplicar Geist a otros elementos del chart */
        .apexcharts-tooltip {
            font-family: 'Geist', sans-serif !important;
        }

        .apexcharts-legend-text {
            font-family: 'Geist', sans-serif !important;
        }
    `;

    // Evitar duplicar estilos
    if (!document.querySelector('#apexcharts-custom-styles')) {
        style.id = 'apexcharts-custom-styles';
        document.head.appendChild(style);
    }
}

// Función de respaldo con datos de ejemplo
function renderizarGraficaConDatosEjemplo() {
    const options = {
        chart: {
            type: 'area',
            height: 400,
            toolbar: {
                show: false
            }
        },
        series: [
            {
                name: "Ingresos total",
                data: [310000, 400000, 280000, 510000, 420000, 1000000]
            },
            {
                name: "Gastos total",
                data: [800000, 320000, 450000, 790000, 340000, 440000]
            }
        ],
        xaxis: {
            categories: ["Ene", "Feb", "Mar", "Abr", "May", "Jun"],
        },
        yaxis: {
            min: 200000,
            max: 1200000,
            tickAmount: 5,
            labels: {
                formatter: function (val) {
                    // Personalizamos SOLO los valores que nos interesan
                    switch (val) {
                        case 200000: return "20k";
                        case 300000: return "30k";
                        case 400000: return "40k";
                        case 600000: return "60k";
                        case 800000: return "80k";
                        case 1000000: return "100k";
                        case 1200000: return "120k";
                        default: return "";
                    }
                }
            }
        },
        tooltip: {
            y: {
                formatter: function (val) {
                    return "$" + val.toLocaleString();
                }
            }
        },
        stroke: {
            curve: 'smooth',
            width: 2
        },
        colors: ["#28a745", "#ffc107"],
        markers: {
            size: 5,
            hover: {
                size: 6.5,
                sizeOffset: 0
            }
        },
        dataLabels: {
            enabled: false
        },
        legend: {
            position: 'top'
        }
    };
    const chart = new ApexCharts(document.querySelector("#chart"), options);
    chart.render();
}

// ===== GRAFICA DE VENTAS TOTALES
async function cargarVentasTotales() {
    try {
        const response = await fetch('/api/graficas/ventas-totales');
        const datos = await response.json();

        const optionsVentasTotales = {
            chart: {
                type: 'donut',
                height: 400
            },
            series: datos.series,
            labels: datos.labels,
            colors: ["#28a745", "#ffc107", "#dc3545"], // verde, amarillo, rojo
            plotOptions: {
                pie: {
                    donut: {
                        size: '85%',
                        labels: {
                            show: true,
                            name: {
                                show: true,
                                fontSize: '16px',
                                offsetY: -10,
                                fontFamily: 'Geist, sans-serif'
                            },
                            value: {
                                show: true,
                                fontSize: '24px',
                                fontWeight: 700,
                                fontFamily: 'Geist, sans-serif',
                                formatter: function (val) {
                                    // Formatear el valor total
                                    if (val >= 1000000) {
                                        return "$" + (val / 1000000).toFixed(1) + "M";
                                    } else if (val >= 1000) {
                                        return "$" + (val / 1000).toFixed(0) + "k";
                                    }
                                    return "$" + Math.round(val).toLocaleString();
                                }
                            },
                            total: {
                                show: true,
                                label: 'Ventas totales',
                                fontSize: '18px',
                                color: '#000',
                                fontFamily: 'Geist, sans-serif',
                                formatter: function (w) {
                                    const total = w.globals.seriesTotals.reduce((a, b) => a + b, 0);
                                    if (total >= 1000000) {
                                        return "$" + (total / 1000000).toFixed(1) + "M";
                                    } else if (total >= 1000) {
                                        return "$" + (total / 1000).toFixed(0) + "k";
                                    }
                                    return "$" + Math.round(total).toLocaleString();
                                }
                            }
                        }
                    }
                }
            },
            stroke: {
                show: false,
                width: 0
            },
            dataLabels: {
                enabled: false
            },
            tooltip: {
                theme: "light",
                custom: function ({ series, seriesIndex, w }) {
                    const labelCompleto = w.globals.labels[seriesIndex];
                    const label = labelCompleto.split(" ")[0];
                    const value = series[seriesIndex].toLocaleString("es-CO");
                    return `
                    <div style="padding:6px 10px; font-family:Geist, sans-serif; font-size:14px; font-weight:600; color:#000; background:#fff; border-radius:6px; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
                        ${label}: $${value}
                    </div>
                `;
                }
            },
            legend: {
                position: 'bottom',
                horizontalAlign: 'left',
                fontSize: '14px',
                fontWeight: 600,
                fontFamily: 'Geist, sans-serif',
                itemMargin: {
                    vertical: 5
                },
                markers: {
                    radius: 12
                },
                formatter: function (seriesName, opts) {
                    return seriesName;
                }
            },
            responsive: [{
                breakpoint: 600,
                options: {
                    legend: {
                        position: 'bottom',
                        horizontalAlign: 'center'
                    }
                }
            }]
        };

        const chartVentasTotales = new ApexCharts(document.querySelector("#ventasTotales"), optionsVentasTotales);
        chartVentasTotales.render();

    } catch (error) {
        console.error('Error cargando datos de ventas totales:', error);
        renderizarVentasTotalesConDatosEjemplo();
    }
}

// Función de respaldo con datos de ejemplo
function renderizarVentasTotalesConDatosEjemplo() {
    const optionsVentasTotales = {
        chart: {
            type: 'donut',
            height: 400
        },
        series: [6000, 2000, 1000, 600],
        labels: [
            "Envíos $32,98 (2%)",
            "Reembolsos $11 (11%)",
            "Pedidos $14,87 (1%)",
            "Ingresos $3.271 (86%)"
        ],
        colors: ["#28a745", "#ffc107", "#dc3545", "#007bff"], // verde, amarillo, rojo, azul
        plotOptions: {
            pie: {
                donut: {
                    size: '85%',
                    labels: {
                        show: true,
                        name: {
                            show: true,
                            fontSize: '16px',
                            offsetY: -10,
                            fontFamily: 'Geist, sans-serif'
                        },
                        value: {
                            show: true,
                            fontSize: '24px',
                            fontWeight: 700,
                            fontFamily: 'Geist, sans-serif',
                            formatter: function (val) {
                                return val;
                            }
                        },
                        total: {
                            show: true,
                            label: 'Ventas totales',
                            fontSize: '18px',
                            color: '#000',
                            formatter: function (w) {
                                return w.globals.seriesTotals.reduce((a, b) => a + b, 0);
                            }
                        }
                    }
                }
            }
        },
        stroke: {
            show: false,
            width: 0
        },
        dataLabels: {
            enabled: false
        },
        tooltip: {
            theme: "light", // fondo blanco
            custom: function ({ series, seriesIndex, w }) {
                // Agarramos solo la primera palabra del label
                const labelCompleto = w.globals.labels[seriesIndex];
                const label = labelCompleto.split(" ")[0];
                const value = series[seriesIndex].toLocaleString("es-CO");
                return `
                <div style="padding:6px 10px; font-family:Geist, sans-serif; font-size:14px; font-weight:600; color:#000; background:#fff; border-radius:6px; box-shadow:0 2px 6px rgba(0,0,0,0.15);">
                    ${label}: ${value}
                </div>
            `;
            }
        },
        legend: {
            position: 'bottom',
            horizontalAlign: 'left',
            fontSize: '14px',
            fontWeight: 600,
            fontFamily: 'Geist, sans-serif',
            itemMargin: {
                vertical: 5
            },
            markers: {
                radius: 12
            },
            formatter: function (seriesName, opts) {
                return seriesName; // mantiene los textos como están
            }
        },
        responsive: [{
            breakpoint: 600,
            options: {
                legend: {
                    position: 'bottom',
                    horizontalAlign: 'center'
                }
            }
        }]
    };
    const chartVentasTotales = new ApexCharts(document.querySelector("#ventasTotales"), optionsVentasTotales);
    chartVentasTotales.render();
}

// ===== GRAFICA DE COLUMNAS APILADAS
async function cargarStockCompras() {
    try {
        const response = await fetch('/api/graficas/stock-compras');
        const datos = await response.json();

        const optionsColum = {
            chart: {
                type: 'bar',
                height: 400,
                stacked: true,
                toolbar: {
                    show: false
                },
                fontFamily: 'Geist, sans-serif'
            },
            series: [
                {
                    name: 'Ventas',
                    data: datos.ventas
                },
                {
                    name: 'Stock Disponible',
                    data: datos.stock
                }
            ],
            xaxis: {
                categories: datos.categorias,
                labels: {
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '12px'
                    },
                    rotate: -45
                }
            },
            yaxis: {
                labels: {
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '12px'
                    },
                    formatter: function(val) {
                        return val.toLocaleString('es-CO');
                    }
                },
                title: {
                    text: 'Unidades',
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '14px'
                    }
                }
            },
            legend: {
                position: 'top',
                labels: {
                    colors: '#000',
                    useSeriesColors: false,
                    fontFamily: 'Geist, sans-serif'
                }
            },
            plotOptions: {
                bar: {
                    borderRadius: 4,
                    columnWidth: '55%',
                    dataLabels: {
                        position: 'top'
                    }
                }
            },
            colors: ['#1E90FF', '#32CD32'],
            dataLabels: {
                enabled: false
            },
            tooltip: {
                y: {
                    formatter: function (val) {
                        return val.toLocaleString('es-CO') + " unidades";
                    }
                },
                style: {
                    fontFamily: 'Geist, sans-serif'
                }
            },
            grid: {
                borderColor: '#f1f1f1'
            },
            responsive: [{
                breakpoint: 768,
                options: {
                    plotOptions: {
                        bar: {
                            columnWidth: '70%'
                        }
                    },
                    xaxis: {
                        labels: {
                            rotate: -45,
                            style: {
                                fontSize: '10px'
                            }
                        }
                    }
                }
            }]
        };

        const chartColum = new ApexCharts(document.querySelector("#chart__Colum"), optionsColum);
        chartColum.render();

    } catch (error) {
        console.error('Error cargando datos de stock y compras:', error);
        renderizarStockComprasConDatosEjemplo();
    }
}

// Función de respaldo con datos de ejemplo
function renderizarStockComprasConDatosEjemplo() {
    const optionsColum = {
        chart: {
            type: 'bar',
            height: 400,
            stacked: true,
            toolbar: {
                show: false
            },
            fontFamily: 'Geist, sans-serif'
        },
        series: [
            {
                name: 'Ventas',
                data: [300, 305, 195, 250, 400, 200, 55, 450, 155]
            },
            {
                name: 'Stock Disponible',
                data: [450, 250, 450, 350, 280, 70, 100, 500, 200]
            }
        ],
        xaxis: {
            categories: ['Vino', 'Whisky', 'Tequila', 'Aguardiente', 'Ron', 'Ginebra', 'Vodka', 'Cerveza', 'Mezcal'],
            labels: {
                style: {
                    fontFamily: 'Geist, sans-serif',
                }
            }
        },
        yaxis: {
            labels: {
                style: {
                    fontFamily: 'Geist, sans-serif',
                    fontSize: '14px'
                }
            }
        },
        legend: {
            position: 'top',
            labels: {
                colors: '#000',
                useSeriesColors: false,
                fontFamily: 'Geist, sans-serif'
            }
        },
        plotOptions: {
            bar: {
                borderRadius: 0,
                columnWidth: '45%'
            }
        },
        colors: ['#1E90FF', '#32CD32'],
        dataLabels: {
            enabled: false
        },
        tooltip: {
            y: {
                formatter: function (val) {
                    return val + " unidades";
                }
            },
            style: {
                fontFamily: 'Geist, sans-serif'
            }
        }
    };

    const chartColum = new ApexCharts(document.querySelector("#chart__Colum"), optionsColum);
    chartColum.render();
}

// ===== MAPA DE COLOMBIA CON VENTAS POR CIUDAD
async function cargarMapaVentas() {
    try {
        const response = await fetch('/api/graficas/mapa-ventas');
        const datos = await response.json();

        // Crear mapa sin controles de zoom ni desplazamiento
        const map = L.map('map', {
            zoomControl: false,
            attributionControl: false,
            dragging: false,
            scrollWheelZoom: false,
            doubleClickZoom: false,
            boxZoom: false,
            keyboard: false,
            tap: false,
            zoomSnap: 0.1
        });

        // No usamos ningún fondo (sin gris)
        map.eachLayer(layer => map.removeLayer(layer));

        const URL_DEPTOS = 'https://raw.githubusercontent.com/santiblanko/colombia.geojson/master/depto.json';

        // Estilo de los departamentos
        function styleDept(feature) {
            return {
                color: '#c27a00',
                weight: 1,
                fillColor: '#ffe9b5',
                fillOpacity: 0.7
            };
        }

        // Cargar departamentos
        fetch(URL_DEPTOS)
            .then(r => r.json())
            .then(deptosGeoJSON => {
                const deptosLayer = L.geoJSON(deptosGeoJSON, {
                    style: styleDept,
                    onEachFeature: (feature, layer) => {
                        const name = feature.properties.NOMBRE || 'Departamento';
                        layer.bindTooltip(name, { sticky: true });
                    }
                }).addTo(map);

                // Centrar el mapa en Colombia
                const bounds = deptosLayer.getBounds();
                map.fitBounds(bounds, { padding: [0, 0] });

                // Hacer que se vea más grande (zoom manual)
                const center = bounds.getCenter();
                map.setView(center, 5.1);

                // Fijar límites para que no se pueda mover
                map.setMaxBounds(bounds);

                // Agregar puntos de ventas dinámicamente
                datos.ventasPorCiudad.forEach(ciudadData => {
                    const { ciudad, ventas, latitud, longitud, empresasCompradoras } = ciudadData;

                    // Calcular radio del círculo basado en las ventas
                    const radioBase = Math.max(5, Math.min(20, Math.sqrt(ventas) / 10));

                    const circle = L.circleMarker([latitud, longitud], {
                        radius: radioBase,
                        fillColor: '#1b6fb8',
                        color: '#0b3a66',
                        weight: 2,
                        opacity: 1,
                        fillOpacity: 0.8
                    }).addTo(map);

                    // Tooltip con información detallada
                    circle.bindTooltip(
                        `<div style="font-family: 'Geist', sans-serif; font-size: 12px; text-align: center;">
                            <strong>${ciudad}</strong><br>
                            Ventas: ${ventas.toLocaleString('es-CO')} unidades<br>
                            Empresas: ${empresasCompradoras}
                        </div>`,
                        {
                            direction: 'top',
                            offset: [0, -radioBase - 5],
                            className: 'map-tooltip'
                        }
                    );

                    // Agregar popup con más información
                    circle.bindPopup(
                        `<div style="font-family: 'Geist', sans-serif; min-width: 200px;">
                            <h3 style="margin: 0 0 10px 0; color: #1b6fb8;">${ciudad}</h3>
                            <p style="margin: 5px 0;"><strong>Unidades vendidas:</strong> ${ventas.toLocaleString('es-CO')}</p>
                            <p style="margin: 5px 0;"><strong>Empresas compradoras:</strong> ${empresasCompradoras}</p>
                            <p style="margin: 5px 0; font-size: 11px; color: #666;">Últimos 12 meses</p>
                        </div>`
                    );
                });

            })
            .catch(error => {
                console.error('Error cargando el mapa de Colombia:', error);
                // Mostrar mensaje de error en el mapa
                document.getElementById('map').innerHTML = `
                    <div style="display: flex; align-items: center; justify-content: center; height: 400px; font-family: 'Geist', sans-serif; color: #666;">
                        <div style="text-align: center;">
                            <h3>Error cargando el mapa</h3>
                            <p>No se pudieron cargar los datos geográficos</p>
                        </div>
                    </div>
                `;
            });

    } catch (error) {
        console.error('Error cargando datos del mapa de ventas:', error);
        renderizarMapaConDatosEjemplo();
    }
}

// Función de respaldo con datos de ejemplo
function renderizarMapaConDatosEjemplo() {
    // Tu código original con datos de ejemplo
    const map = L.map('map', {
        zoomControl: false,
        attributionControl: false,
        dragging: false,
        scrollWheelZoom: false,
        doubleClickZoom: false,
        boxZoom: false,
        keyboard: false,
        tap: false,
        zoomSnap: 0.1
    });

    // No usamos ningún fondo (sin gris)
    map.eachLayer(layer => map.removeLayer(layer));

    const URL_DEPTOS = 'https://raw.githubusercontent.com/santiblanko/colombia.geojson/master/depto.json';
    const URL_MUNIS = 'https://raw.githubusercontent.com/santiblanko/colombia.geojson/master/mpio.json';

    // Ciudades y ventas
    const ventasPorCiudad = {
        'Bogotá D.C.': { ventas: 1245, coords: [4.7110, -74.0721] },
        'Medellín': { ventas: 890, coords: [6.2442, -75.5812] },
        'Cali': { ventas: 730, coords: [3.4516, -76.5320] },
        'Barranquilla': { ventas: 420, coords: [10.9639, -74.7964] },
        'Cartagena': { ventas: 315, coords: [10.3910, -75.4794] }
    };

    // Estilo de los departamentos
    function styleDept(feature) {
        return {
            color: '#c27a00',
            weight: 1,
            fillColor: '#ffe9b5',
            fillOpacity: 0.7
        };
    }

    // Cargar departamentos
    fetch(URL_DEPTOS)
        .then(r => r.json())
        .then(deptosGeoJSON => {
            const deptosLayer = L.geoJSON(deptosGeoJSON, {
                style: styleDept,
                onEachFeature: (feature, layer) => {
                    const name = feature.properties.NOMBRE || 'Departamento';
                    layer.bindTooltip(name, { sticky: true });
                }
            }).addTo(map);

            // Centrar el mapa en Colombia
            const bounds = deptosLayer.getBounds();
            map.fitBounds(bounds, { padding: [0, 0] });

            // Hacer que se vea más grande (zoom manual)
            const center = bounds.getCenter();
            map.setView(center, 5.1); // puedes subirlo un poco más si quieres acercarlo

            // Fijar límites para que no se pueda mover
            map.setMaxBounds(bounds);

            // Agregar puntos de ventas (ciudades)
            Object.entries(ventasPorCiudad).forEach(([nombre, data]) => {
                const { ventas, coords } = data;
                const circle = L.circleMarker(coords, {
                    radius: 5, // tamaño pequeño/mediano
                    fillColor: '#1b6fb8',
                    color: '#0b3a66',
                    weight: 1,
                    opacity: 1,
                    fillOpacity: 0.9
                }).addTo(map);

                // Tooltip con el número de ventas
                circle.bindTooltip(
                    `<strong>${nombre}</strong><br>Ventas: ${ventas}`,
                    { direction: 'top', offset: [0, -6] }
                );
            });
        });
}

// ===== GRAFICA DE HISTÓRICO DE VENTAS
async function cargarHistoricoVentas() {
    try {
        const response = await fetch('/api/graficas/historico-ventas');
        const datos = await response.json();

        // Preparar datos para el gráfico manteniendo la estructura original
        const salesData = datos.series.map((valor, index) => {
            const año = datos.categorias[index] / 1000; // Convertir de vuelta de milésimas
            return [año, Math.round(valor)]; // Redondear valores para coincidir con el estilo
        });

        // Calcular rango Y basado en los datos
        const valoresY = datos.series.map(valor => Math.round(valor));
        const maxY = Math.max(...valoresY);
        const minY = Math.min(...valoresY);
        const rangoY = maxY - minY;

        // Ajustar márgenes para que se vea como la imagen
        const yMin = minY - (rangoY * 0.1);
        const yMax = maxY + (rangoY * 0.1);

        // Calcular zoom inicial para últimos 5 años
        const añosUnicos = [...new Set(datos.categorias.map(cat => Math.floor(cat / 1000)))].sort();
        const añoActual = new Date().getFullYear();
        const añoInicioZoom = añoActual - 5;

        // Encontrar índices para el zoom
        let minIndex = 0;
        let maxIndex = salesData.length - 1;

        if (añosUnicos.length > 0) {
            // Buscar el índice del año de inicio del zoom
            for (let i = 0; i < salesData.length; i++) {
                if (salesData[i][0] >= añoInicioZoom) {
                    minIndex = Math.max(0, i - 1); // Un poco antes para contexto
                    break;
                }
            }

            // Buscar el índice del año actual
            for (let i = salesData.length - 1; i >= 0; i--) {
                if (salesData[i][0] <= añoActual) {
                    maxIndex = i;
                    break;
                }
            }
        }

        const optionsProye = {
            series: [{
                name: 'Ventas Totales',
                data: salesData
            }],
            chart: {
                type: 'area',
                stacked: false,
                height: 400,
                zoom: {
                    type: 'x',
                    enabled: true,
                    autoScaleYaxis: false // Deshabilitar autoScale para mantener estilo fijo
                },
                toolbar: {
                    show: true,
                    tools: {
                        download: true, // Solo el menú de exportación
                        zoom: false,
                        zoomin: false,
                        zoomout: false,
                        pan: false,
                        reset: false // Habilitar reset para volver a vista completa
                    }
                },
                fontFamily: 'Geist, sans-serif',
                animations: {
                    enabled: true,
                    easing: 'easeinout',
                    speed: 800
                }
            },
            dataLabels: {
                enabled: false
            },
            stroke: {
                curve: 'straight', // Cambiado a 'straight' como en el ejemplo original
                width: 3,
                lineCap: 'round'
            },
            markers: {
                size: 0, // Cero como en el ejemplo original - los puntos se muestran en hover
                hover: {
                    size: 5,
                    sizeOffset: 2
                }
            },
            fill: {
                type: 'gradient',
                gradient: {
                    shade: 'light',
                    type: 'vertical',
                    shadeIntensity: 0.5,
                    gradientToColors: ['#00BFFF'], // Azul más oscuro para el gradiente
                    inverseColors: false,
                    opacityFrom: 0.7,
                    opacityTo: 0.2,
                    stops: [0, 90, 100]
                }
            },
            yaxis: {
                labels: {
                    formatter: function(val) {
                        // Formato con k y M
                        if (val >= 1000000) {
                            return "$" + (val / 1000000).toFixed(1) + "M";
                        } else if (val >= 1000) {
                            return "$" + (val / 1000).toFixed(0) + "k";
                        }
                        return "$" + Math.round(val).toLocaleString();
                    },
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '12px',
                        fontWeight: 400,
                        colors: '#666'
                    }
                },
                title: {
                    text: 'Ventas Totales',
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '13px',
                        fontWeight: 700,
                        color: '#333'
                    }
                },
                min: yMin,
                max: yMax,
                tickAmount: 6 // Número fijo de ticks como en la imagen
            },
            xaxis: {
                type: 'numeric',
                title: {
                    text: 'Años',
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '14px',
                        fontWeight: 700,
                        color: '#333'
                    }
                },
                labels: {
                    formatter: function(val) {
                        // Mostrar solo el año entero
                        return Math.floor(val).toString();
                    },
                    style: {
                        fontFamily: 'Geist, sans-serif',
                        fontSize: '12px',
                        colors: '#666'
                    }
                },
                tickAmount: datos.categorias.length > 0 ?
                    Math.min(8, new Set(datos.categorias.map(c => Math.floor(c/1000))).size) : 6,
                tooltip: {
                    enabled: false
                },
                // Zoom inicial para últimos 5 años
                min: salesData[minIndex] ? salesData[minIndex][0] : undefined,
                max: salesData[maxIndex] ? salesData[maxIndex][0] : undefined
            },
            tooltip: {
                shared: true,
                intersect: false,
                x: {
                    formatter: function(val) {
                        return 'Año: ' + val.toFixed(1);
                    }
                },
                y: {
                    formatter: function(val) {
                        // Formato del tooltip con k y M
                        if (val >= 1000000) {
                            return "$" + (val / 1000000).toFixed(2) + " Millones";
                        } else if (val >= 1000) {
                            return "$" + (val / 1000).toFixed(1) + " Mil";
                        }
                        return "$" + Math.round(val).toLocaleString();
                    }
                },
                style: {
                    fontFamily: 'Geist, sans-serif',
                    fontSize: '13px'
                },
                marker: {
                    show: true
                }
            },
            grid: {
                borderColor: '#f1f1f1',
                strokeDashArray: 0, // Sin líneas punteadas
                padding: {
                    top: 0,
                    right: 0,
                    bottom: 0,
                    left: 0
                },
                yaxis: {
                    lines: {
                        show: true
                    }
                },
                xaxis: {
                    lines: {
                        show: false
                    }
                }
            },
            states: {
                hover: {
                    filter: {
                        type: 'lighten',
                        value: 0.15
                    }
                },
                active: {
                    filter: {
                        type: 'darken',
                        value: 0.35
                    }
                }
            }
        };

        const chartProye = new ApexCharts(document.querySelector("#chartVentas"), optionsProye);
        chartProye.render();

    } catch (error) {
        console.error('Error cargando histórico de ventas:', error);
        renderizarHistoricoConDatosEjemplo();
    }
}

// Función de respaldo con datos de ejemplo (actualizada con zoom y color oscuro)
function renderizarHistoricoConDatosEjemplo() {
    function seededRandom(seed) {
        var x = Math.sin(seed) * 10000;
        return x - Math.floor(x);
    }

    function generateYearWiseSalesWithContinuousSubPeaks(baseYear, count, yrange, seed = 12345) {
        var series = [];
        var lastValue = 1200000; // Aumentado para que use formatos k y M

        for (var i = 0; i < count; i++) {
            var year = baseYear + i;
            var mainPoints = 2;
            var mainStart = lastValue;

            for (var mp = 0; mp < mainPoints; mp++) {
                var mainChange = Math.floor(seededRandom(seed + year * 10 + mp) * 350000 - 200000);
                var mainValue = mainStart + mainChange;

                if (mainValue > yrange.max) mainValue = yrange.max - seededRandom(seed + year * 20 + mp) * 50000;
                if (mainValue < yrange.min) mainValue = yrange.min + seededRandom(seed + year * 30 + mp) * 50000;

                var subPeaks = 10;
                for (var sp = 0; sp < subPeaks; sp++) {
                    var microChange = Math.floor(seededRandom(seed + year * 40 + mp * 10 + sp) * 20000 - 10000);
                    var y = mainStart + ((mainValue - mainStart) * (sp + 1) / subPeaks) + microChange;

                    series.push([year + (mp + sp / subPeaks) / mainPoints, y]);
                }

                mainStart = mainValue;
            }

            lastValue = mainStart;
        }

        return series;
    }

    // Aumentar el rango para que use formatos k y M
    var salesData = generateYearWiseSalesWithContinuousSubPeaks(2017, 9, { min: 800000, max: 2500000 });

    // Calcular zoom para últimos 5 años en datos de ejemplo
    const añoActual = new Date().getFullYear();
    const añoInicioZoom = añoActual - 5;

    let minIndexEjemplo = 0;
    let maxIndexEjemplo = salesData.length - 1;

    // Buscar índices para el zoom en datos de ejemplo
    for (let i = 0; i < salesData.length; i++) {
        if (salesData[i][0] >= añoInicioZoom) {
            minIndexEjemplo = Math.max(0, i - 1);
            break;
        }
    }

    for (let i = salesData.length - 1; i >= 0; i--) {
        if (salesData[i][0] <= añoActual) {
            maxIndexEjemplo = i;
            break;
        }
    }

    var optionsProye = {
        series: [{
            name: 'Ventas Totales',
            data: salesData
        }],
        chart: {
            type: 'area',
            stacked: false,
            height: 400,
            zoom: { type: 'x', enabled: true, autoScaleYaxis: true },
            toolbar: {
                show: true,
                tools: {
                    download: true,
                    zoom: false,
                    zoomin: false,
                    zoomout: false,
                    pan: false,
                    reset: true // Habilitar reset
                }
            },
            fontFamily: 'Geist, sans-serif'
        },
        dataLabels: { enabled: false },
        stroke: {
            curve: 'straight',
            width: 3,
            colors: ['#1E40AF'] // Azul más oscuro
        },
        markers: { size: 0 },
        fill: {
            type: 'gradient',
            gradient: {
                shade: 'light',
                type: 'vertical',
                shadeIntensity: 1,
                gradientToColors: ['#1E3A8A'], // Azul más oscuro para el gradiente
                inverseColors: false,
                opacityFrom: 0.6,
                opacityTo: 0,
                stops: [0, 100]
            }
        },
        yaxis: {
            labels: {
                formatter: function(val) {
                    // Formato con k y M
                    if (val >= 1000000) {
                        return "$" + (val / 1000000).toFixed(1) + "M";
                    } else if (val >= 1000) {
                        return "$" + (val / 1000).toFixed(0) + "k";
                    }
                    return "$" + Math.round(val).toLocaleString();
                },
                style: {
                    fontFamily: 'Geist, sans-serif',
                    fontSize: '12px',
                    fontWeight: 400
                }
            },
            title: {
                text: 'Ventas Totales',
                style: {
                    fontFamily: 'Geist, sans-serif',
                    fontSize: '13px',
                    fontWeight: 700
                }
            }
        },
        xaxis: {
            type: 'numeric',
            title: {
                text: 'Años',
                style: {
                    fontFamily: 'Geist, sans-serif',
                    fontSize: '14px',
                    fontWeight: 700
                }
            },
            labels: {
                formatter: val => Math.floor(val),
                style: {
                    fontFamily: 'Geist, sans-serif',
                    fontSize: '12px'
                }
            },
            // Zoom inicial para últimos 5 años en datos de ejemplo
            min: salesData[minIndexEjemplo] ? salesData[minIndexEjemplo][0] : undefined,
            max: salesData[maxIndexEjemplo] ? salesData[maxIndexEjemplo][0] : undefined
        },
        tooltip: {
            y: {
                formatter: function(val) {
                    // Formato del tooltip con k y M
                    if (val >= 1000000) {
                        return "$" + (val / 1000000).toFixed(2) + " Millones";
                    } else if (val >= 1000) {
                        return "$" + (val / 1000).toFixed(1) + " Mil";
                    }
                    return "$" + Math.round(val).toLocaleString();
                },
                style: {
                    fontFamily: 'Geist, sans-serif'
                }
            }
        },
        colors: ['#1E40AF'] // Azul más oscuro
    };

    var chartProye = new ApexCharts(document.querySelector("#chartVentas"), optionsProye);
    chartProye.render();
}
