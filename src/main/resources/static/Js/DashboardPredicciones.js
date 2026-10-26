// DashboardPredicciones.js — gráfica mensual de ventas con ApexCharts.
// ApexCharts se carga como script global en el HTML (antes de este módulo).

const URL_DATOS = '/admin/prediccion/datos-grafica';
const MESES_PROYECCION = 12; // igual que MESES_PROYECCION del controller

const formatoCOP = new Intl.NumberFormat('es-CO', {
  style: 'currency',
  currency: 'COP',
  maximumFractionDigits: 0,
});

const boton = document.getElementById('btnPrediccion');
const overlay = document.getElementById('loadingPrediccion');
const barra = document.getElementById('progressBar');
const contenedor = document.getElementById('chartVentas');

let grafica = null;
let temporizador = null;

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

function construirOpciones(datos) {
  // Serie 1: ventas reales (solo puntos, sin línea). Meses futuros quedan vacíos.
  const reales = datos.serie.map((p) => ({ x: aFecha(p.fecha), y: p.real }));
  // Serie 2: predicción del modelo para todos los meses (línea).
  const prediccion = datos.serie.map((p) => ({ x: aFecha(p.fecha), y: p.prediccion }));

  return {
    chart: {
      type: 'line',
      height: 360,
      toolbar: { show: false },
      fontFamily: 'Geist, sans-serif',
      animations: { enabled: true, speed: 500 },
    },
    series: [
      { name: 'Ventas reales', data: reales },
      { name: 'Predicción', data: prediccion },
    ],
    colors: ['#0a6cff', '#7cb7ff'],
    stroke: { width: [0, 2], curve: 'straight' },
    markers: { size: [6, 0], strokeWidth: 0 },
    xaxis: {
      type: 'datetime',
      tickAmount: Math.max(1, Math.round(datos.serie.length / 12)),
      labels: { format: 'yyyy' },
      title: { text: 'Años' },
    },
    yaxis: {
      min: 0,
      title: { text: 'Ventas Totales (COP)' },
      labels: { formatter: (v) => formatoCOP.format(v) },
    },
    legend: { position: 'top', horizontalAlign: 'left' },
    tooltip: {
      x: { format: 'MMM yyyy' },
      y: { formatter: (v) => (v == null ? '—' : formatoCOP.format(v)) },
    },
    noData: { text: 'Sin datos de ventas' },
  };
}

function dibujar(datos) {
  if (grafica) grafica.destroy();
  grafica = new ApexCharts(contenedor, construirOpciones(datos));
  grafica.render();
}

function mostrarCarga(activa) {
  overlay.style.display = activa ? 'flex' : 'none';
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
  mostrarCarga(true);
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

// Carga inicial: historia exacta (6 años). Al pulsar el botón se agrega el año siguiente.
boton?.addEventListener('click', () => cargar(MESES_PROYECCION));
cargar(0);
