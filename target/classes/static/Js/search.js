/**
 * 🔍 MÓDULO DE BÚSQUEDA UNIVERSAL
 *
 * Uso:
 * import { inicializarBusqueda } from './search.js';
 *
 * En tu HTML:
 * <div class="search-container">
 *   <input class="search-input" placeholder="Buscar...">
 *   <div class="search-results"></div>
 * </div>
 *
 * En tu script:
 * inicializarBusqueda();
 */

let searchTimeout;
let currentQuery = '';
let isAdminContext = false; // Detectado automáticamente
let initialized = false;

export function inicializarBusqueda() {
    if (initialized) return;
    initialized = true;

    // Detectar input: .search-input, #icon-search, o input dentro de .header__search
    const searchInput =
        document.querySelector('.search-input') ||
        document.querySelector('.header__search input') ||
        document.querySelector('#icon-search');

    if (!searchInput) {
        console.warn('No se encontró input de búsqueda');
        return;
    }

    // Detectar si estamos en contexto admin
    isAdminContext = window.location.pathname.includes('/admin');

    // Crear modal de resultados si no existe
    if (!document.querySelector('.search-modal')) {
        crearModalBusqueda();
    }

    // Event: Input con debounce
    if (searchInput.tagName === 'INPUT') {
        searchInput.addEventListener('input', (e) => {
            clearTimeout(searchTimeout);
            currentQuery = e.target.value.trim();

            if (currentQuery.length < 2) {
                cerrarResultados();
                return;
            }

            searchTimeout = setTimeout(() => {
                ejecutarBusqueda(currentQuery);
            }, 300); // Esperar 300ms después de dejar de escribir
        });
    } else {
        // Si es un ícono, abrir modal al click
        searchInput.addEventListener('click', () => {
            abrirModalBusqueda();
        });
    }

    // Cerrar modal con ESC
    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape') {
            cerrarResultados();
            document.querySelector('.search-modal')?.classList.remove('active');
        }
    });

    // Cerrar al hacer click fuera del input y de los resultados
    document.addEventListener('click', (e) => {
        const modal = document.querySelector('.search-modal');
        const results = document.querySelector('.search-results');

        const clickedInsideInput = searchInput.contains(e.target);
        const clickedInsideResults = results && results.contains(e.target);
        const clickedInsideModal = modal && modal.contains(e.target);

        if (!clickedInsideInput && !clickedInsideResults && !clickedInsideModal) {
            cerrarResultados();
            modal?.classList.remove('active');
        }
    });
}

// Auto-inicialización al cargar el DOM
if (document.readyState === 'loading') {
    document.addEventListener('DOMContentLoaded', inicializarBusqueda);
} else {
    inicializarBusqueda();
}

/**
 * Ejecutar búsqueda en el servidor
 * Endpoint: /api/search/universal?q=...&limit=...&tipo=...
 */
async function ejecutarBusqueda(query) {
    try {
        mostrarLoading();

        const endpoint = '/api/search/universal';
        const url = new URL(`${window.location.origin}${endpoint}`);
        url.searchParams.append('q', query);
        url.searchParams.append('limit', isAdminContext ? 10 : 5);

        const response = await fetch(url.toString(), {
            method: 'GET',
            headers: { 'Accept': 'application/json' },
            credentials: 'same-origin'
        });

        if (!response.ok) {
            throw new Error(`HTTP ${response.status}`);
        }

        const data = await response.json();

        if (data && data.success && (data.totalResults || 0) > 0) {
            renderizarResultados(data);
        } else {
            mostrarNoResultados(query);
        }

    } catch (error) {
        console.error('Error en búsqueda:', error);
        mostrarError();
    }
}

/**
 * Renderizar resultados agrupados por tipo
 */
function renderizarResultados(data) {
    const resultsContainer = document.querySelector('.search-results');
    if (!resultsContainer) return;

    let html = `<div class="search-results-list">`;

    // Tipos en orden de prioridad
    const tipos = ['productos', 'categorias', 'blog', 'clientes', 'proveedores'];

    tipos.forEach(tipo => {
        const items = data[tipo];
        if (items && items.length > 0) {
            html += `
                <div class="results-group" data-type="${escapeHtml(tipo)}">
                    <div class="group-header">
                        <h3 class="group-title">
                            ${getTituloGrupo(tipo)}
                            <span class="group-count">${items.length}</span>
                        </h3>
                        <div class="group-divider"></div>
                    </div>
                    <div class="group-items">
            `;

            items.forEach(item => {
                html += renderizarItem(item);
            });

            html += `
                    </div>
                </div>
            `;
        }
    });

    html += `</div>`;

    resultsContainer.innerHTML = html;
    resultsContainer.style.display = 'block';

    // Event delegation: un solo listener en el contenedor
    resultsContainer.querySelectorAll('.search-result-item').forEach(item => {
        item.addEventListener('click', () => {
            const url = item.getAttribute('data-url');
            if (url && url !== '#') {
                window.location.href = url;
            }
        });
    });
}

/**
 * Renderizar un item individual
 */
function renderizarItem(item) {
    const badgeClass = escapeHtml(item.badgeClass || 'badge-secondary');
    const icono = item.icono ? `<i class="${escapeHtml(item.icono)}"></i>` : '';
    const imagen = item.imagen
        ? `<img src="${escapeHtml(item.imagen)}" alt="${escapeHtml(item.titulo || '')}" class="item-imagen">`
        : '';

    const precio = (item.precio !== undefined && item.precio !== null && item.precio !== '')
        ? `$${formatPrice(item.precio)}`
        : '';

    return `
        <div class="search-result-item"
             data-url="${escapeHtml(item.url || '#')}"
             data-type="${escapeHtml(item.type || '')}">
            <div class="item-visual">
                ${imagen}
                <div class="item-icono">${icono}</div>
            </div>

            <div class="item-content">
                <div class="item-header">
                    <p class="item-titulo">${highlightQuery(item.titulo || '')}</p>
                    <span class="item-badge ${badgeClass}">${escapeHtml(item.badge || '')}</span>
                </div>

                ${item.subtitulo ? `<p class="item-subtitulo">${highlightQuery(item.subtitulo)}</p>` : ''}
                ${item.descripcion ? `<p class="item-descripcion">${highlightQuery(item.descripcion)}</p>` : ''}

                ${precio ? `<p class="item-precio">${escapeHtml(precio)}</p>` : ''}
            </div>

            <div class="item-arrow">
                <i class="ri-arrow-right-s-line"></i>
            </div>
        </div>
    `;
}

/**
 * Obtener título localizado del grupo
 */
function getTituloGrupo(tipo) {
    const titulos = {
        'productos': '🛍️ Productos',
        'categorias': '📂 Categorías',
        'blog': '📰 Blog',
        'clientes': '👥 Clientes',
        'proveedores': '🏪 Proveedores'
    };
    return titulos[tipo] || tipo;
}

/**
 * Resaltar query en el texto (con escape de HTML para evitar XSS)
 */
function highlightQuery(text) {
    if (!text) return '';
    if (!currentQuery) return escapeHtml(text);

    // Escapar primero, luego envolver coincidencias en <mark>
    const escaped = escapeHtml(text);
    const escapedQuery = escapeHtml(currentQuery).replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
    const regex = new RegExp(`(${escapedQuery})`, 'gi');
    return escaped.replace(regex, '<mark>$1</mark>');
}

/**
 * Formatear precio
 */
function formatPrice(price) {
    const num = Number(price);
    if (isNaN(num)) return escapeHtml(String(price));
    return num.toLocaleString('es-CO');
}

/**
 * Escape de HTML para evitar XSS al insertar contenido del backend
 */
function escapeHtml(str) {
    if (str === null || str === undefined) return '';
    return String(str)
        .replace(/&/g, '&amp;')
        .replace(/</g, '&lt;')
        .replace(/>/g, '&gt;')
        .replace(/"/g, '&quot;')
        .replace(/'/g, '&#39;');
}

/**
 * Estados visuales
 */
function mostrarLoading() {
    const resultsContainer = document.querySelector('.search-results');
    if (resultsContainer) {
        resultsContainer.innerHTML = `
            <div class="search-loading">
                <div class="loader"></div>
                <p>Buscando...</p>
            </div>
        `;
        resultsContainer.style.display = 'block';
    }
}

function mostrarNoResultados(query) {
    const resultsContainer = document.querySelector('.search-results');
    if (resultsContainer) {
        resultsContainer.innerHTML = `
            <div class="search-empty">
                <i class="ri-search-2-line"></i>
                <h3>No se encontraron resultados</h3>
                <p>para "<strong>${escapeHtml(query)}</strong>"</p>
                <small>Intenta con otros términos</small>
            </div>
        `;
        resultsContainer.style.display = 'block';
    }
}

function mostrarError() {
    const resultsContainer = document.querySelector('.search-results');
    if (resultsContainer) {
        resultsContainer.innerHTML = `
            <div class="search-error">
                <i class="ri-error-warning-line"></i>
                <h3>Error en la búsqueda</h3>
                <p>Algo salió mal. Intenta de nuevo.</p>
            </div>
        `;
        resultsContainer.style.display = 'block';
    }
}

function cerrarResultados() {
    const resultsContainer = document.querySelector('.search-results');
    if (resultsContainer) {
        resultsContainer.style.display = 'none';
    }
}

/**
 * Modal de búsqueda (para ícono de lupa)
 */
function crearModalBusqueda() {
    const modal = document.createElement('div');
    modal.className = 'search-modal';
    modal.innerHTML = `
        <div class="search-modal-content">
            <div class="search-modal-header">
                <input
                    type="text"
                    class="search-modal-input"
                    placeholder="🔍 Buscar productos, clientes, blog..."
                    autocomplete="off">
                <button class="search-modal-close" type="button">
                    <i class="ri-close-line"></i>
                </button>
            </div>
            <div class="search-results search-modal-results"></div>
        </div>
    `;

    document.body.appendChild(modal);

    // Eventos del modal
    const input = modal.querySelector('.search-modal-input');
    const closeBtn = modal.querySelector('.search-modal-close');

    input.addEventListener('input', (e) => {
        clearTimeout(searchTimeout);
        currentQuery = e.target.value.trim();

        if (currentQuery.length < 2) {
            cerrarResultados();
            return;
        }

        searchTimeout = setTimeout(() => {
            ejecutarBusqueda(currentQuery);
        }, 300);
    });

    closeBtn.addEventListener('click', () => {
        modal.classList.remove('active');
        input.value = '';
        currentQuery = '';
        cerrarResultados();
    });

    // Abrir modal con Ctrl+K o Cmd+K
    document.addEventListener('keydown', (e) => {
        if ((e.ctrlKey || e.metaKey) && e.key === 'k') {
            e.preventDefault();
            abrirModalBusqueda();
        }
    });
}

function abrirModalBusqueda() {
    const modal = document.querySelector('.search-modal');
    if (modal) {
        modal.classList.add('active');
        const input = modal.querySelector('.search-modal-input');
        if (input) input.focus();
    }
}
