// DashboardPredicciones.js — gráfica mensual de ventas con ApexCharts.
// ApexCharts se carga como script global en el HTML (antes de este módulo).

const URL_DATOS = '/admin/prediccion/datos-grafica';
const MESES_PERMITIDOS = [6, 12, 24]; // MAX_MESES_PROYECCION del controller = 24

const formatoCOP = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

// "$ 12 M" en pantallas pequeñas: los números completos no caben en el eje Y.
const formatoCOPCorto = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  notation: 'compact',
  maximumFractionDigits: 1,
});

const formatoMes = new Intl.DateTimeFormat('es-CO', { month: 'short', year: 'numeric' });

const boton = document.getElementById('btnPrediccion');
const selectHorizonte = document.getElementById('selectHorizonte');
const overlay = document.getElementById('loadingPrediccion');
const textoCarga = document.getElementById('loadingTexto');
const barra = document.getElementById('progressBar');
const contenedor = document.getElementById('chartVentas');
const resumen = {
  caja: document.getElementById('resumenPrediccion'),
  hasta: document.getElementById('resumenHasta'),
  total: document.getElementById('resumenTotal'),
  error: document.getElementById('resumenError'),
};

let grafica = null;
let temporizador = null;
let ultimosDatos = null;
let ultimoAnchoCompacto = null;

/** Pide la serie mensual al controller Spring (que a su vez consulta FastAPI). */
async function obtenerDatos(proyeccionMeses) {
  const res = await fetch(`${URL_DATOS}?proyeccion_meses=${proyeccionMeses}`, {
    headers: { Accept: 'application/json' },
    credentials: 'same-origin',
  });
  if (!res.ok) {
    throw new Error(`el servidor respondió ${res.status}`);
  }
  return res.json();
}

/** "2021-01" -> timestamp del primer día del mes (hora local, evita desfases de zona). */
const aFecha = (mes) => new Date(`${mes}-01T00:00:00`).getTime();

const esCompacto = () => contenedor.clientWidth < 640;

/** Altura proporcional al ancho: ni aplastada en móvil ni gigante en monitores anchos. */
const alturaGrafica = () => Math.round(Math.min(440, Math.max(280, contenedor.clientWidth * 0.5)));

/**
 * Divide la serie en:
 *  - reales:   ventas registradas (puntos)
 *  - ajuste:   lo que el modelo estima para meses ya ocurridos (línea fina, sirve para ver el error)
 *  - proyeccion: meses futuros (línea discontinua, lo que realmente se está prediciendo)
 */
function separarSeries(datos) {
  const reales = [];
  const ajuste = [];
  const proyeccion = [];
  datos.serie.forEach((p, i) => {
    const x = aFecha(p.fecha);
    const futuro = p.tipo === 'prediccion';
    reales.push({ x, y: p.real });
    ajuste.push({ x, y: futuro ? null : p.prediccion });
    // El primer punto futuro se une con el último ajuste para que la línea no quede cortada.
    const siguienteEsFuturo = datos.serie[i + 1]?.tipo === 'prediccion';
    proyeccion.push({ x, y: futuro || siguienteEsFuturo ? p.prediccion : null });
  });
  return { reales, ajuste, proyeccion };
}

function construirOpciones(datos) {
  const compacto = esCompacto();
  const { reales, ajuste, proyeccion } = separarSeries(datos);
  const primerFuturo = datos.serie.find((p) => p.tipo === 'prediccion');

  return {
    chart: {
      type: 'line',
      height: alturaGrafica(),
      width: '100%',
      toolbar: { show: false },
      zoom: { enabled: false },
      fontFamily: 'Geist, sans-serif',
      animations: { enabled: true, speed: 400 },
      parentHeightOffset: 0,
    },
    series: [
      { name: 'Ventas reales', data: reales },
      { name: 'Ajuste del modelo', data: ajuste },
      { name: 'Proyección', data: proyeccion },
    ],
    colors: ['#0a6cff', '#9cc7ff', '#f59e0b'],
    stroke: { width: [0, 2, 3], curve: 'straight', dashArray: [0, 0, 6] },
    markers: { size: [compacto ? 3 : 5, 0, 0], strokeWidth: 0, hover: { sizeOffset: 2 } },
    xaxis: {
      type: 'datetime',
      labels: {
        datetimeUTC: false,
        format: 'yyyy',
        hideOverlappingLabels: true,
        style: { fontSize: compacto ? '10px' : '12px' },
      },
      title: { text: compacto ? undefined : 'Años' },
      tooltip: { enabled: false },
    },
    yaxis: {
      min: 0,
      forceNiceScale: true,
      tickAmount: compacto ? 4 : 5,
      title: { text: compacto ? undefined : 'Ventas totales (COP)' },
      labels: {
        formatter: (v) => (compacto ? formatoCOPCorto : formatoCOP).format(v),
        style: { fontSize: compacto ? '10px' : '12px' },
        minWidth: compacto ? 40 : 90,
        maxWidth: compacto ? 56 : 120,
      },
    },
    annotations: primerFuturo
      ? {
          xaxis: [{
            x: aFecha(primerFuturo.fecha),
            borderColor: '#f59e0b',
            strokeDashArray: 4,
            label: {
              text: compacto ? 'Proyección' : 'Inicio proyección',
              orientation: 'horizontal',
              textAnchor: 'end', // a la izquierda de la línea: no se sale por el borde derecho
              offsetX: -4,
              style: { background: '#f59e0b', color: '#fff', fontSize: '11px' },
            },
          }],
        }
      : {},
    grid: { padding: { left: compacto ? 0 : 8, right: compacto ? 8 : 16 } },
    legend: {
      position: 'top',
      horizontalAlign: 'left',
      fontSize: compacto ? '11px' : '13px',
      itemMargin: { horizontal: 8, vertical: 2 },
    },
    tooltip: {
      shared: true,
      x: { formatter: (v) => formatoMes.format(new Date(v)) },
      y: { formatter: (v) => (v == null ? '—' : formatoCOP.format(v)) },
    },
    noData: { text: 'Sin datos de ventas' },
  };
}

/**
 * Error ponderado (WAPE) entre el ajuste del modelo y las ventas reales de los meses cerrados:
 * suma de |predicción − real| / suma de ventas reales. A diferencia del MAPE, un mes con ventas
 * casi en 0 no dispara el porcentaje. Es una referencia de calidad: si el modelo se equivoca mucho
 * en el pasado, la proyección no es fiable.
 */
function errorMedio(datos) {
  const cerrados = datos.serie.filter((p) => p.tipo === 'real' && p.real != null);
  const totalReal = cerrados.reduce((acc, p) => acc + p.real, 0);
  if (totalReal <= 0) return null;
  const errorAbs = cerrados.reduce((acc, p) => acc + Math.abs(p.prediccion - p.real), 0);
  return (errorAbs / totalReal) * 100;
}

function pintarResumen(datos) {
  const futuros = datos.serie.filter((p) => p.tipo === 'prediccion');
  if (futuros.length === 0) {
    resumen.caja.hidden = true;
    return;
  }
  const ultimo = futuros[futuros.length - 1];
  const total = futuros.reduce((acc, p) => acc + (p.prediccion || 0), 0);
  // Preferir el backtest de FastAPI (entrena sin los últimos 12 meses y los predice: error real
  // fuera de muestra). Si no hay historia suficiente, se usa el error a un paso calculado aquí.
  const validacion = datos.metricas?.validacion;
  const error = validacion?.wape ?? errorMedio(datos);
  resumen.error.title = validacion?.wape != null
    ? `Backtest ${validacion.desde} a ${validacion.hasta}: el modelo se entrenó sin esos meses y se comparó con lo real`
    : 'Diferencia media entre el ajuste del modelo y las ventas reales de los meses cerrados';

  resumen.hasta.textContent = formatoMes.format(new Date(aFecha(ultimo.fecha)));
  resumen.total.textContent = formatoCOP.format(total);
  resumen.error.textContent = error == null ? '—' : `±${error.toFixed(0)} %`;
  resumen.error.style.color = error == null ? '' : error <= 20 ? '#15803d' : error <= 40 ? '#b45309' : '#b91c1c';
  resumen.caja.hidden = false;
}

function dibujar(datos) {
  ultimosDatos = datos;
  ultimoAnchoCompacto = esCompacto();
  if (grafica) grafica.destroy();
  grafica = new ApexCharts(contenedor, construirOpciones(datos));
  grafica.render();
  pintarResumen(datos);
}

function mostrarCarga(activa, meses) {
  if (activa) {
    textoCarga.textContent = meses > 0
      ? `Calculando la proyección de ${meses} meses…`
      : 'Cargando las ventas históricas…';
  }
  overlay.hidden = !activa;
  boton.disabled = activa;
  selectHorizonte.disabled = activa;
}

/** Barra que avanza hasta 90 % mientras llega la respuesta. */
function iniciarBarra() {
  let progreso = 0;
  barra.style.width = '0%';
  clearInterval(temporizador);
  temporizador = setInterval(() => {
    progreso += (90 - progreso) * 0.08;
    barra.style.width = `${progreso}%`;
  }, 200);
}

async function cargar(proyeccionMeses) {
  mostrarCarga(true, proyeccionMeses);
  iniciarBarra();
  try {
    const datos = await obtenerDatos(proyeccionMeses);
    dibujar(datos);
    barra.style.width = '100%';
  } catch (err) {
    console.error(err);
    alert(`No se pudo cargar la predicción: ${err.message}`);
  } finally {
    clearInterval(temporizador);
    setTimeout(() => mostrarCarga(false), 300);
  }
}

/** Al cambiar de tamaño: ajustar altura y, si se cruza el umbral móvil/escritorio, redibujar. */
let esperaResize = null;
window.addEventListener('resize', () => {
  clearTimeout(esperaResize);
  esperaResize = setTimeout(() => {
    if (!grafica || !ultimosDatos) return;
    if (esCompacto() !== ultimoAnchoCompacto) {
      dibujar(ultimosDatos);
    } else {
      grafica.updateOptions({ chart: { height: alturaGrafica() } }, false, false);
    }
  }, 150);
});

// Menú lateral desplegable en tablet/móvil (el botón solo se ve por debajo de 1024 px).
const sidebar = document.getElementById('sidebarPrincipal');
const toggleSidebar = document.getElementById('sidebarToggle');
const backdrop = document.getElementById('sidebarBackdrop');

function abrirMenu(abrir) {
  if (!sidebar || !toggleSidebar) return;
  sidebar.classList.toggle('is-open', abrir);
  toggleSidebar.setAttribute('aria-expanded', String(abrir));
  toggleSidebar.setAttribute('aria-label', abrir ? 'Cerrar menú' : 'Abrir menú');
  if (backdrop) backdrop.hidden = !abrir;
}

toggleSidebar?.addEventListener('click', () => abrirMenu(!sidebar.classList.contains('is-open')));
backdrop?.addEventListener('click', () => abrirMenu(false));
document.addEventListener('keydown', (e) => {
  if (e.key === 'Escape') abrirMenu(false);
});

const mesesSeleccionados = () => {
  const valor = Number(selectHorizonte?.value);
  return MESES_PERMITIDOS.includes(valor) ? valor : 12;
};

// Carga inicial: solo historia (6 años). El botón agrega la proyección elegida en el selector.
boton?.addEventListener('click', () => cargar(mesesSeleccionados()));
cargar(0);
