import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, verProductos, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();
    inicialHeart();
    initCart();
    rederigirFav();
    finalizarCompra();
    verProductos();
    toggleMenu();

    // ==================== VARIABLES GLOBALES ====================
    let productosOriginales = [];
    let productosFiltrados = [];
    let filtrosActivos = {
        paises: [],
        marcas: [],
        categorias: [],
        subcategorias: [],
        precioMin: 0,
        precioMax: 10000000,
        ordenarPor: ''
    };

    // ==================== ELEMENTOS DEL DOM ====================
    const productsPerPage = 16;
    const cardsContainer = document.querySelector('.flex__productos-track');
    const cards = document.querySelectorAll('.card');
    const pageNumbersContainer = document.querySelector('.pagination__Numbers');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');
    const minRange = document.getElementById('minRange');
    const maxRange = document.getElementById('maxRange');
    const rangeMinDisplay = document.getElementById('rangeMin');
    const rangeMaxDisplay = document.getElementById('rangeMax');
    const sliderContainer = document.querySelector('.slider-container');

    let currentPage = 1;
    let totalPages = Math.ceil(cards.length / productsPerPage);

    // ==================== INICIALIZACIÓN PRINCIPAL ====================
    function inicializar() {
        console.log('Inicializando sistema de filtros...');
        console.log('Productos encontrados en DOM:', cards.length);

        // Inicializar productos desde el DOM
        inicializarProductos();

        // Inicializar paginación
        if (cards.length > 0) {
            initializePagination();
        } else {
            document.querySelector('.pagination').style.display = 'none';
            mostrarMensajeNoResultados();
        }

        // Inicializar todos los sistemas
        inicializarEventosFiltros();
        inicializarSidebar();
        inicializarOrdenamiento();
        inicializarRangoPrecio();
        inicializarBusquedaFiltros();
        inicializarFiltrosAplicados();
        inicializarVistaGridLista();
        inicializarToggleMenus();

        // Mostrar todos los productos al inicio
        mostrarTodosLosProductos();

        console.log('Sistema de filtros inicializado correctamente');
    }

    // ==================== INICIALIZACIÓN DE PRODUCTOS ====================
    function inicializarProductos() {
        productosOriginales = Array.from(cards).map(card => {
            const precio = parseFloat(card.getAttribute('data-price')) || 0;
            const precioRegular = parseFloat(card.getAttribute('data-oldprice')) || 0;

            return {
                elemento: card,
                id: card.getAttribute('data-id'),
                nombre: card.getAttribute('data-name') || '',
                marca: card.getAttribute('data-brand') || '',
                pais: card.getAttribute('data-country') || '',
                categoria: card.getAttribute('data-category') || '',
                subcategoria: card.getAttribute('data-subcategory') || '',
                precio: precio,
                precioRegular: precioRegular,
                stock: parseInt(card.getAttribute('data-stock') || '0'),
                imagen: card.getAttribute('data-image') || ''
            };
        });

        productosFiltrados = [...productosOriginales];
        console.log('Productos inicializados:', productosOriginales.length);
    }

    // ==================== MOSTRAR TODOS LOS PRODUCTOS ====================
    function mostrarTodosLosProductos() {
        console.log('Mostrando todos los productos');

        // Mostrar todos los productos
        cards.forEach(card => {
            card.style.display = 'block';
            card.classList.remove('hidden');
        });

        // Resetear productos filtrados
        productosFiltrados = [...productosOriginales];

        // Actualizar paginación
        recargarPaginacion();

        // Limpiar mensaje de no resultados si existe
        const existingMessage = cardsContainer.querySelector('.no-results');
        if (existingMessage) {
            existingMessage.remove();
        }
    }

    // ==================== EVENTOS DE FILTROS ====================
    function inicializarEventosFiltros() {
        // Eventos para checkboxes de países
        document.querySelectorAll('.menu__item:nth-child(3) input[type="checkbox"]').forEach(checkbox => {
            checkbox.addEventListener('change', aplicarFiltros);
        });

        // Eventos para checkboxes de marcas
        document.querySelectorAll('.menu__item:nth-child(4) input[type="checkbox"]').forEach(checkbox => {
            checkbox.addEventListener('change', aplicarFiltros);
        });

        // Eventos para categorías padre
        document.querySelectorAll('.parent-checkbox').forEach(checkbox => {
            checkbox.addEventListener('change', function() {
                const subcategoryList = this.closest('.category-group').querySelector('.subcategory-list');
                if (subcategoryList) {
                    subcategoryList.querySelectorAll('input[type="checkbox"]').forEach(subCheckbox => {
                        subCheckbox.checked = this.checked;
                    });
                }
                aplicarFiltros();
            });
        });

        // Eventos para subcategorías
        document.querySelectorAll('.subcategory-list input[type="checkbox"]').forEach(checkbox => {
            checkbox.addEventListener('change', aplicarFiltros);
        });
    }

    // ==================== APLICAR FILTROS ====================
    function aplicarFiltros() {
        console.log('Aplicando filtros...');

        // Recoger filtros activos
        const nuevosFiltros = {
            paises: obtenerValoresCheckbox('.menu__item:nth-child(3) input[type="checkbox"]:checked'),
            marcas: obtenerValoresCheckbox('.menu__item:nth-child(4) input[type="checkbox"]:checked'),
            categorias: obtenerValoresCategoriasPadre('.parent-checkbox:checked'),
            subcategorias: obtenerValoresCheckbox('.subcategory-list input[type="checkbox"]:checked'),
            precioMin: parseFloat(minRange.value) || 0,
            precioMax: parseFloat(maxRange.value) || 1000000
        };

        filtrosActivos = { ...filtrosActivos, ...nuevosFiltros };

        console.log('Filtros activos:', filtrosActivos);

        // Verificar si hay filtros activos
        const hayFiltrosActivos = filtrosActivos.paises.length > 0 ||
                                 filtrosActivos.marcas.length > 0 ||
                                 filtrosActivos.categorias.length > 0 ||
                                 filtrosActivos.subcategorias.length > 0 ||
                                 filtrosActivos.precioMin > 0 ||
                                 filtrosActivos.precioMax < 1000000;

        console.log('Hay filtros activos:', hayFiltrosActivos);

        if (!hayFiltrosActivos) {
            mostrarTodosLosProductos();
            actualizarFiltrosAplicados();
            return;
        }

        // Aplicar filtros localmente primero para respuesta inmediata
        aplicarFiltrosLocales();

        // Luego aplicar filtros via API para consistencia con la base de datos
        aplicarFiltrosAPI();
    }

    // ==================== FILTRADO LOCAL (Respuesta inmediata) ====================
    function aplicarFiltrosLocales() {
        productosFiltrados = productosOriginales.filter(producto => {
            const cumplePais = filtrosActivos.paises.length === 0 || filtrosActivos.paises.includes(producto.pais);
            const cumpleMarca = filtrosActivos.marcas.length === 0 || filtrosActivos.marcas.includes(producto.marca);
            const cumpleCategoria = filtrosActivos.categorias.length === 0 || filtrosActivos.categorias.includes(producto.categoria);
            const cumpleSubcategoria = filtrosActivos.subcategorias.length === 0 || filtrosActivos.subcategorias.includes(producto.subcategoria);
            const cumplePrecio = producto.precio >= filtrosActivos.precioMin && producto.precio <= filtrosActivos.precioMax;

            console.log(`Producto ${producto.nombre}:`, {
                pais: cumplePais,
                marca: cumpleMarca,
                categoria: cumpleCategoria,
                subcategoria: cumpleSubcategoria,
                precio: cumplePrecio
            });

            return cumplePais && cumpleMarca && cumpleCategoria && cumpleSubcategoria && cumplePrecio;
        });

        console.log('Productos después de filtro local:', productosFiltrados.length);
        actualizarVistaProductos();
        actualizarFiltrosAplicados();
    }

    // ==================== FILTRADO API (Para consistencia) ====================
    async function aplicarFiltrosAPI() {
        try {
            const response = await fetch('/api/productos/filtrar', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify({
                    paises: filtrosActivos.paises,
                    marcas: filtrosActivos.marcas,
                    categorias: filtrosActivos.categorias,
                    subcategorias: filtrosActivos.subcategorias,
                    precioMin: filtrosActivos.precioMin,
                    precioMax: filtrosActivos.precioMax,
                    ordenarPor: filtrosActivos.ordenarPor
                })
            });

            if (!response.ok) {
                throw new Error('Error en la respuesta del servidor');
            }

            const productosFiltradosAPI = await response.json();
            console.log('Productos filtrados desde API:', productosFiltradosAPI.length);

            // Si hay diferencia, actualizar con los datos de la API
            if (productosFiltradosAPI.length !== productosFiltrados.length) {
                actualizarVistaConProductosAPI(productosFiltradosAPI);
            }

        } catch (error) {
            console.error('Error al filtrar productos via API:', error);
            // Mantenemos los resultados del filtro local
        }
    }

    function actualizarVistaConProductosAPI(productosAPI) {
        // Ocultar todos los productos primero
        cards.forEach(card => {
            card.style.display = 'none';
            card.classList.add('hidden');
        });

        // Mostrar solo los productos que vienen de la API
        productosAPI.forEach(productoAPI => {
            const card = Array.from(cards).find(card =>
                card.getAttribute('data-id') == productoAPI.id
            );
            if (card) {
                card.style.display = 'block';
                card.classList.remove('hidden');
            }
        });

        recargarPaginacion();
        actualizarFiltrosAplicados();
    }

    // ==================== FUNCIONES DE FILTRADO MEJORADAS ====================
    function obtenerValoresCheckbox(selector) {
        const checkboxes = document.querySelectorAll(selector);
        const valores = [];

        checkboxes.forEach(cb => {
            // Primero intentar obtener el valor del atributo value
            if (cb.value && cb.value.trim() !== '') {
                valores.push(cb.value);
            } else {
                // Si no tiene value, obtener el texto del span
                const span = cb.nextElementSibling;
                if (span && span.textContent && span.textContent.trim() !== '') {
                    valores.push(span.textContent.trim());
                } else {
                    // Último recurso: buscar cualquier elemento hermano de texto
                    const label = cb.closest('label');
                    if (label) {
                        const textContent = label.textContent.trim();
                        const checkboxText = textContent.replace('✓', '').trim();
                        if (checkboxText) {
                            valores.push(checkboxText);
                        }
                    }
                }
            }
        });

        console.log('Valores obtenidos de checkboxes:', valores);
        return valores;
    }

    // FUNCIÓN ESPECÍFICA PARA CATEGORÍAS PADRE
    function obtenerValoresCategoriasPadre(selector) {
        const checkboxes = document.querySelectorAll(selector);
        const valores = [];

        checkboxes.forEach(cb => {
            // Para categorías padre, necesitamos obtener el texto del span que está después del checkbox
            const categoryGroup = cb.closest('.category-group');
            if (categoryGroup) {
                const span = categoryGroup.querySelector('.category-main span');
                if (span && span.textContent && span.textContent.trim() !== '') {
                    valores.push(span.textContent.trim());
                }
            }
        });

        console.log('Valores obtenidos de categorías padre:', valores);
        return valores;
    }

    // ==================== ACTUALIZAR VISTA ====================
    function actualizarVistaProductos() {
        console.log('Actualizando vista. Productos filtrados:', productosFiltrados.length);

        // Ocultar todos los productos primero
        cards.forEach(card => {
            card.style.display = 'none';
            card.classList.add('hidden');
        });

        // Mostrar solo los productos filtrados
        productosFiltrados.forEach(productoFiltrado => {
            if (productoFiltrado.elemento) {
                productoFiltrado.elemento.style.display = 'block';
                productoFiltrado.elemento.classList.remove('hidden');
            }
        });

        recargarPaginacion();
    }

    // ==================== PAGINACIÓN ====================
    function recargarPaginacion() {
        const cardsVisibles = document.querySelectorAll('.card:not(.hidden)');
        totalPages = Math.ceil(cardsVisibles.length / productsPerPage);
        currentPage = 1;

        console.log('Recargando paginación. Cards visibles:', cardsVisibles.length, 'Total páginas:', totalPages);

        if (cardsVisibles.length === 0) {
            document.querySelector('.pagination').style.display = 'none';
            mostrarMensajeNoResultados();
        } else {
            document.querySelector('.pagination').style.display = 'flex';
            createPageNumbers();
            showPage(1);
        }
    }

    function mostrarMensajeNoResultados() {
        const existingMessage = cardsContainer.querySelector('.no-results');
        if (!existingMessage) {
            const noResults = document.createElement('div');
            noResults.className = 'no-results';
            noResults.style.cssText = 'grid-column: 1 / -1; text-align: center; padding: 3rem; width: 100%;';
            noResults.innerHTML = `
                <h3>No se encontraron productos</h3>
                <p>Intenta ajustar los filtros para ver más resultados.</p>
            `;
            cardsContainer.appendChild(noResults);
        }
    }

    function initializePagination() {
        createPageNumbers();
        showPage(1);
        updateButtons();
    }

    function createPageNumbers() {
        pageNumbersContainer.innerHTML = '';

        if (totalPages <= 1) return;

        let startPage = Math.max(1, currentPage - 2);
        let endPage = Math.min(totalPages, startPage + 4);

        if (endPage - startPage < 4) {
            startPage = Math.max(1, endPage - 4);
        }

        if (startPage > 1) {
            addPageButton(1);
            if (startPage > 2) {
                addDots();
            }
        }

        for (let i = startPage; i <= endPage; i++) {
            addPageButton(i);
        }

        if (endPage < totalPages) {
            if (endPage < totalPages - 1) {
                addDots();
            }
            addPageButton(totalPages);
        }

        updateActivePage();
    }

    function addPageButton(pageNumber) {
        const pageButton = document.createElement('div');
        pageButton.className = 'button__pagination';
        pageButton.textContent = pageNumber;
        pageButton.addEventListener('click', () => showPage(pageNumber));
        pageNumbersContainer.appendChild(pageButton);
    }

    function addDots() {
        const dots = document.createElement('div');
        dots.className = 'button__pagination dots';
        dots.textContent = '...';
        dots.style.cssText = 'cursor: default; pointer-events: none;';
        pageNumbersContainer.appendChild(dots);
    }

    function showPage(page) {
        currentPage = page;
        const cardsVisibles = Array.from(document.querySelectorAll('.card:not(.hidden)'));

        // Ocultar todos
        cardsVisibles.forEach(card => {
            card.style.display = 'none';
        });

        // Mostrar solo los de la página actual
        const startIndex = (page - 1) * productsPerPage;
        const endIndex = startIndex + productsPerPage;

        for (let i = startIndex; i < endIndex && i < cardsVisibles.length; i++) {
            if (cardsVisibles[i]) {
                // En lugar de 'block', usar el estilo por defecto (vacío)
                // para que respete los estilos CSS
                cardsVisibles[i].style.display = '';
            }
        }

        createPageNumbers();
        updateButtons();
    }

    function updateActivePage() {
        const pageButtons = document.querySelectorAll('.button__pagination:not(.dots)');
        pageButtons.forEach((button) => {
            if (parseInt(button.textContent) === currentPage) {
                button.classList.add('active');
            } else {
                button.classList.remove('active');
            }
        });
    }

    function updateButtons() {
        if (!prevBtn || !nextBtn) return;

        prevBtn.disabled = currentPage === 1;
        nextBtn.disabled = currentPage === totalPages;

        prevBtn.style.opacity = currentPage === 1 ? '0.5' : '1';
        prevBtn.style.cursor = currentPage === 1 ? 'not-allowed' : 'pointer';
        nextBtn.style.opacity = currentPage === totalPages ? '0.5' : '1';
        nextBtn.style.cursor = currentPage === totalPages ? 'not-allowed' : 'pointer';
    }

    // ==================== SIDEBAR ====================
    function inicializarSidebar() {
        const sidebar = document.getElementById('sidebar-filtros');
        const closeBtn = document.getElementById('close-sidebar');
        const openBtn = document.getElementById('btn-filtros');

        if (!sidebar || !closeBtn || !openBtn) {
            console.error('No se encontraron elementos del sidebar');
            return;
        }

        let overlay = document.querySelector('.sidebar-overlay');
        if (!overlay) {
            overlay = document.createElement('div');
            overlay.className = 'sidebar-overlay';
            document.body.appendChild(overlay);
        }

        openBtn.addEventListener('click', () => {
            sidebar.classList.add('active');
            overlay.classList.add('active');
            document.body.style.overflow = 'hidden';
        });

        closeBtn.addEventListener('click', () => {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        });

        overlay.addEventListener('click', () => {
            sidebar.classList.remove('active');
            overlay.classList.remove('active');
            document.body.style.overflow = '';
        });
    }

    // ==================== ORDENAMIENTO ====================
    function inicializarOrdenamiento() {
        const ordenarCheckboxes = document.querySelectorAll('.menu__item:first-child input[type="checkbox"]');
        const ordenarBtn = document.querySelector('.menu__item:first-child .menu__toggle');

        if (!ordenarBtn) {
            console.error('No se encontró el botón de ordenar');
            return;
        }

        const textoOriginal = ordenarBtn.textContent;

        ordenarCheckboxes.forEach((checkbox) => {
            checkbox.addEventListener('change', (e) => {
                ordenarCheckboxes.forEach((cb) => {
                    if (cb !== checkbox) cb.checked = false;
                });

                if (checkbox.checked) {
                    const textoSeleccionado = checkbox.parentElement.textContent.trim();
                    ordenarBtn.textContent = `${textoOriginal} ${textoSeleccionado}`;
                    filtrosActivos.ordenarPor = textoSeleccionado;
                    aplicarOrdenamiento(textoSeleccionado);
                } else {
                    ordenarBtn.textContent = textoOriginal;
                    filtrosActivos.ordenarPor = '';
                    // Volver al orden original
                    productosFiltrados = [...productosOriginales];
                    actualizarVistaProductos();
                }
            });
        });

        console.log('Ordenamiento inicializado correctamente');
    }

    async function aplicarOrdenamiento(tipoOrden) {
        try {
            let endpoint = '';

            switch(tipoOrden) {
                case 'Más vendido':
                    endpoint = '/api/productos/mas-vendidos';
                    break;
                case 'Precio bajo a alto':
                    endpoint = '/api/productos/precio-asc';
                    break;
                case 'Precio alto a bajo':
                    endpoint = '/api/productos/precio-desc';
                    break;
                case 'Nombre ascendente':
                    endpoint = '/api/productos/nombre-asc';
                    break;
                case 'Nombre descendente':
                    endpoint = '/api/productos/nombre-desc';
                    break;
                default:
                    console.log('Tipo de ordenamiento no reconocido:', tipoOrden);
                    ordenamientoLocal(tipoOrden);
                    return;
            }

            console.log('Solicitando ordenamiento desde:', endpoint);
            const response = await fetch(endpoint);

            if (!response.ok) {
                throw new Error(`Error ${response.status}: ${response.statusText}`);
            }

            const productosOrdenados = await response.json();
            console.log('Productos ordenados recibidos:', productosOrdenados.length);

            actualizarProductosOrdenados(productosOrdenados, tipoOrden);

        } catch (error) {
            console.error('Error al ordenar productos via API:', error);
            // Fallback a ordenamiento local
            ordenamientoLocal(tipoOrden);
        }
    }

    function ordenamientoLocal(tipoOrden) {
        console.log('Aplicando ordenamiento local:', tipoOrden);

        productosFiltrados.sort((a, b) => {
            switch(tipoOrden) {
                case 'Precio bajo a alto':
                    return a.precio - b.precio;
                case 'Precio alto a bajo':
                    return b.precio - a.precio;
                case 'Nombre ascendente':
                    return a.nombre.localeCompare(b.nombre);
                case 'Nombre descendente':
                    return b.nombre.localeCompare(a.nombre);
                case 'Más vendido':
                    // Para más vendido, necesitarías tener un campo de ventas en tus datos
                    // Por ahora, ordenamos por ID como fallback
                    return a.id - b.id;
                default:
                    return 0;
            }
        });

        actualizarVistaProductos();
    }

    function actualizarProductosOrdenados(productosOrdenados, tipoOrden) {
        console.log('Actualizando vista con productos ordenados desde API');

        // Ocultar todos los productos primero
        cards.forEach(card => {
            card.style.display = 'none';
            card.classList.add('hidden');
        });

        // Mostrar productos en el orden recibido
        productosOrdenados.forEach(producto => {
            const card = Array.from(cards).find(card =>
                card.getAttribute('data-id') == producto.id
            );
            if (card) {
                card.style.display = 'block';
                card.classList.remove('hidden');
                // Mover el card al final para mantener el orden visual
                cardsContainer.appendChild(card);
            }
        });

        recargarPaginacion();
    }

    // ==================== RANGO DE PRECIO ====================
    function inicializarRangoPrecio() {
        if (!minRange || !maxRange) return;

        function formatCurrency(value) {
            return '$' + parseInt(value).toLocaleString('es-CO');
        }

        function updateValues() {
            let minVal = parseInt(minRange.value);
            let maxVal = parseInt(maxRange.value);

            // Evitar que los sliders se crucen
            if (maxVal - minVal < 100000) {
                if (event && event.target === minRange) {
                    minVal = maxVal - 100000;
                    minRange.value = minVal;
                } else {
                    maxVal = minVal + 100000;
                    maxRange.value = maxVal;
                }
            }

            if (rangeMinDisplay) rangeMinDisplay.textContent = formatCurrency(minVal);
            if (rangeMaxDisplay) rangeMaxDisplay.textContent = formatCurrency(maxVal);

            // Actualizar la barra de progreso visual
            if (sliderContainer) {
                const minPercent = (minVal / minRange.max) * 100;
                const maxPercent = 100 - (maxVal / maxRange.max) * 100;

                sliderContainer.style.setProperty('--range-min', minPercent + '%');
                sliderContainer.style.setProperty('--range-max', maxPercent + '%');
            }

            // Aplicar filtros
            aplicarFiltros();
        }

        minRange.addEventListener('input', updateValues);
        maxRange.addEventListener('input', updateValues);

        // Inicializar valores
        updateValues();
    }

    // ==================== BÚSQUEDA EN FILTROS ====================
    function inicializarBusquedaFiltros() {
        document.querySelectorAll('.filter-input').forEach(input => {
            input.addEventListener('input', e => {
                const filter = e.target.value.toLowerCase();
                const submenu = e.target.closest('.submenu');
                if (!submenu) return;

                const items = submenu.querySelectorAll('li:not(:first-child)'); // Excluir el input de búsqueda

                items.forEach(item => {
                    const label = item.querySelector('label');
                    if (label) {
                        const text = label.textContent.toLowerCase();
                        item.style.display = text.includes(filter) ? '' : 'none';
                    }
                });
            });
        });
    }

    // ==================== FILTROS APLICADOS ====================
    function inicializarFiltrosAplicados() {
        const limpiarTodoBtn = document.getElementById('limpiar-todo');
        if (limpiarTodoBtn) {
            limpiarTodoBtn.addEventListener('click', limpiarFiltros);
        }

        console.log('Filtros aplicados inicializados');
    }

    function actualizarFiltrosAplicados() {
        const filtros = [];

        if (filtrosActivos.paises.length > 0) {
            filtros.push(...filtrosActivos.paises.map(p => `País: ${p}`));
        }
        if (filtrosActivos.marcas.length > 0) {
            filtros.push(...filtrosActivos.marcas.map(m => `Marca: ${m}`));
        }
        if (filtrosActivos.categorias.length > 0) {
            filtros.push(...filtrosActivos.categorias.map(c => `Categoría: ${c}`));
        }
        if (filtrosActivos.subcategorias.length > 0) {
            filtros.push(...filtrosActivos.subcategorias.map(s => `Subcategoría: ${s}`));
        }
        if (filtrosActivos.precioMin > 0 || filtrosActivos.precioMax < 10000000) {
            filtros.push(`Precio: $${filtrosActivos.precioMin.toLocaleString('es-CO')} - $${filtrosActivos.precioMax.toLocaleString('es-CO')}`);
        }

        const filtrosLista = document.querySelector('#filtros-aplicados .submenu');
        const limpiarTodoDiv = document.querySelector('.limpiar__todo');
        const filtrosAplicadosElement = document.getElementById('filtros-aplicados');

        if (filtros.length > 0 && filtrosLista && limpiarTodoDiv && filtrosAplicadosElement) {
            filtrosLista.innerHTML = filtros.map(filtro => `
                <li><label>${filtro}</label></li>
            `).join('');
            limpiarTodoDiv.style.display = 'block';
            filtrosAplicadosElement.style.display = 'block';
        } else if (limpiarTodoDiv && filtrosAplicadosElement) {
            limpiarTodoDiv.style.display = 'none';
            filtrosAplicadosElement.style.display = 'none';
        }

        console.log('Filtros aplicados actualizados:', filtros);
    }

    function limpiarFiltros() {
        console.log('Limpiando todos los filtros');

        // Desmarcar todos los checkboxes
        document.querySelectorAll('#sidebar-filtros input[type="checkbox"]').forEach(checkbox => {
            checkbox.checked = false;
        });

        // Resetear filtros activos
        filtrosActivos = {
            paises: [],
            marcas: [],
            categorias: [],
            subcategorias: [],
            precioMin: 0,
            precioMax: 10000000,
            ordenarPor: ''
        };

        // Resetear rango de precio a valores por defecto
        if (minRange && maxRange) {
            minRange.value = 0;
            maxRange.value = 1000000;

            // Actualizar visualización
            const event = new Event('input');
            minRange.dispatchEvent(event);
        }

        // Resetear texto de ordenar
        const ordenarBtn = document.querySelector('.menu__item:first-child .menu__toggle');
        if (ordenarBtn) {
            ordenarBtn.textContent = 'Ordenar por:';
        }

        mostrarTodosLosProductos();
        actualizarFiltrosAplicados();
    }

    // ==================== VISTA GRID/LISTA ====================
    function inicializarVistaGridLista() {
        const btnGrid = document.getElementById('btn-grid');
        const btnList = document.getElementById('btn-list');
        const flexProductos = document.querySelector('.flex__productos');

        if (!btnGrid || !btnList || !flexProductos) return;

        const savedView = localStorage.getItem('productView') || 'grid';
        applyView(savedView);

        btnGrid.addEventListener('click', () => {
            applyView('grid');
            localStorage.setItem('productView', 'grid');
        });

        btnList.addEventListener('click', () => {
            applyView('list');
            localStorage.setItem('productView', 'list');
        });

        function applyView(viewType) {
            flexProductos.classList.remove('grid-view', 'list-view');
            flexProductos.classList.add(viewType + '-view');
            btnGrid.classList.toggle('active', viewType === 'grid');
            btnList.classList.toggle('active', viewType === 'list');
        }
    }

    // ==================== TOGGLE MENÚS ====================
    function inicializarToggleMenus() {
        const menuToggles = document.querySelectorAll('.menu__toggle');

        console.log('Encontrados', menuToggles.length, 'menús toggle');

        menuToggles.forEach(toggle => {
            toggle.addEventListener('click', e => {
                e.preventDefault();
                e.stopPropagation();

                const menuItem = toggle.closest('.menu__item');
                if (!menuItem) return;

                console.log('Toggle clickeado:', toggle.textContent);

                const isFiltros = menuItem.id === 'filtros-aplicados';
                const wasActive = menuItem.classList.contains('active');

                if (isFiltros) {
                    menuItem.classList.toggle('active');
                    return;
                }

                // Cerrar todos los demás menús excepto filtros aplicados
                document.querySelectorAll('.menu__item').forEach(item => {
                    if (item.id === 'filtros-aplicados') return;
                    item.classList.remove('active');
                });

                if (!wasActive) {
                    menuItem.classList.add('active');
                }
            });
        });

        // Cerrar menús al hacer clic fuera
        document.addEventListener('click', (e) => {
            if (!e.target.closest('.menu__item')) {
                document.querySelectorAll('.menu__item').forEach(item => {
                    item.classList.remove('active');
                });
            }
        });

        console.log('Toggle menus inicializados correctamente');
    }

    // ==================== EVENT LISTENERS PAGINACIÓN ====================
    if (prevBtn) {
        prevBtn.addEventListener('click', () => {
            if (currentPage > 1) {
                showPage(currentPage - 1);
            }
        });
    }

    if (nextBtn) {
        nextBtn.addEventListener('click', () => {
            if (currentPage < totalPages) {
                showPage(currentPage + 1);
            }
        });
    }

    // ==================== INICIAR TODO ====================
    inicializar();
});