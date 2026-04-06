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

    // Menú desplegable del perfil
    const subMenu = document.getElementById("SubMenu");
    const profileImage = document.getElementById("user__admin");

    if (subMenu && profileImage) {
        profileImage.addEventListener("click", function (e) {
            e.stopPropagation();
            subMenu.classList.toggle("open__menu");
        });

        document.addEventListener("click", function (e) {
            if (!subMenu.contains(e.target) && !profileImage.contains(e.target)) {
                subMenu.classList.remove("open__menu");
            }
        });
    }

    // Abrir la notificaciones del admin
    const notifIcon = document.getElementById("notifIcon");
    const notifMenu = document.getElementById("notifMenu");

    if (notifIcon && notifMenu) {
        notifIcon.addEventListener("click", (e) => {
            e.stopPropagation();
            notifMenu.classList.toggle("open");
        });

        document.addEventListener("click", (e) => {
            if (!notifMenu.contains(e.target) && !notifIcon.contains(e.target)) {
                notifMenu.classList.remove("open");
            }
        });
    }

    // FILTRO DE PRODUCTOS (por texto y estado)
    (() => {
        const searchInput = document.getElementById("inputSearch");
        const stateSelect = document.getElementById("filtro-estado");
        const tableBody = document.getElementById("favoritos-body");

        if (!searchInput || !stateSelect || !tableBody) return;

        const tableRows = Array.from(tableBody.rows);

        const filtrarTabla = () => {
            const texto = searchInput.value.trim().toLowerCase();
            const estadoSeleccionado = stateSelect.value.trim().toLowerCase();
            const palabras = texto.split(/\s+/).filter(Boolean);

            tableRows.forEach(row => {
                const celdas = Array.from(row.querySelectorAll("td"))
                    .filter(td => !td.classList.contains("menu__actions"))
                    .map(td => td.textContent.toLowerCase())
                    .join(" ");

                const estado = row.querySelector('.td__estados')?.textContent.toLowerCase() || '';

                const coincideTexto = palabras.length === 0 || palabras.some(palabra => celdas.includes(palabra));
                const coincideEstado = estadoSeleccionado === "todos" || estado === estadoSeleccionado;

                row.hidden = !(coincideTexto && coincideEstado);
            });

            // Recalcular paginación después de filtrar
            setTimeout(() => actualizarPaginacion(), 0);
        };

        searchInput.addEventListener('input', filtrarTabla);
        stateSelect.addEventListener('change', filtrarTabla);
    })();

    // PAGINACIÓN PROFESIONAL
    const filasPorPagina = 12;
    let paginaActual = 1;
    let totalFilas = 0;
    let totalPaginas = 0;

    const tablaBody = document.getElementById("favoritos-body");
    const contenedorBotones = document.querySelector(".pagination__button");
    const textoPaginacion = document.querySelector(".span__description");

    // === Función para obtener filas visibles ===
    function obtenerFilasVisibles() {
        return Array.from(tablaBody.querySelectorAll("tr"))
            .filter(tr => !tr.hidden);
    }

    // === Función para marcar los últimos dos menús visibles ===
    function marcarUltimosDosMenus() {
        const filasVisibles = obtenerFilasVisibles();

        const todosMenus = tablaBody.querySelectorAll("td.menu__actions .dropdown__menu");
        todosMenus.forEach(menu => menu.classList.remove("up"));

        const ultimosDos = filasVisibles.slice(-2);
        ultimosDos.forEach(tr => {
            const menu = tr.querySelector("td.menu__actions .dropdown__menu");
            if (menu) menu.classList.add("up");
        });
    }

    // === Mostrar filas según página seleccionada ===
    function mostrarPagina(pagina) {
        const filasVisibles = obtenerFilasVisibles();
        totalFilas = filasVisibles.length;
        totalPaginas = Math.ceil(totalFilas / filasPorPagina);

        const inicio = (pagina - 1) * filasPorPagina;
        const fin = inicio + filasPorPagina;

        // Ocultar todas las filas primero
        Array.from(tablaBody.querySelectorAll("tr")).forEach(tr => {
            tr.style.display = "none";
        });

        // Mostrar solo las filas visibles de la página actual
        filasVisibles.forEach((fila, index) => {
            if (index >= inicio && index < fin) {
                fila.style.display = "";
            }
        });

        // Actualizar texto de paginación
        const inicioTexto = totalFilas > 0 ? inicio + 1 : 0;
        const finTexto = Math.min(fin, totalFilas);
        textoPaginacion.textContent = `Mostrando del ${inicioTexto} al ${finTexto} de ${totalFilas} entradas`;

        // Control botones anterior / siguiente
        document.querySelector(".pasar--anterior").disabled = pagina === 1;
        document.querySelector(".pasar--siguiente").disabled = pagina === totalPaginas || totalPaginas === 0;

        // Actualizar botones de paginación
        crearBotonesPaginacion(pagina);

        // Marcar últimos dos menús
        marcarUltimosDosMenus();
    }

    // === Crear botones de paginación inteligente ===
    function crearBotonesPaginacion(paginaActual) {
        const anterior = contenedorBotones.querySelector(".pasar--anterior");
        const siguiente = contenedorBotones.querySelector(".pasar--siguiente");

        // Limpiar botones existentes
        contenedorBotones.querySelectorAll(".button__item, .pagination__dots").forEach(btn => btn.remove());

        if (totalPaginas <= 1) return;

        const botones = [];

        // Siempre mostrar primera página
        botones.push(1);

        // Rango de páginas a mostrar alrededor de la actual
        let inicioRango = Math.max(2, paginaActual - 2);
        let finRango = Math.min(totalPaginas - 1, paginaActual + 2);

        // Ajustar el rango si estamos cerca de los extremos
        if (paginaActual <= 3) {
            finRango = Math.min(5, totalPaginas - 1);
        } else if (paginaActual >= totalPaginas - 2) {
            inicioRango = Math.max(totalPaginas - 4, 2);
        }

        // Agregar puntos suspensivos después de la primera página si es necesario
        if (inicioRango > 2) {
            botones.push('...');
        }

        // Agregar páginas del rango
        for (let i = inicioRango; i <= finRango; i++) {
            botones.push(i);
        }

        // Agregar puntos suspensivos antes de la última página si es necesario
        if (finRango < totalPaginas - 1) {
            botones.push('...');
        }

        // Siempre mostrar última página si hay más de 1 página
        if (totalPaginas > 1) {
            botones.push(totalPaginas);
        }

        // Crear botones en el DOM
        botones.forEach(numero => {
            const btn = document.createElement("button");

            if (numero === '...') {
                btn.classList.add("pagination__dots");
                btn.textContent = "...";
                btn.disabled = true;
            } else {
                btn.classList.add("button__item");
                btn.textContent = numero;
                btn.classList.toggle("active", numero === paginaActual);

                btn.addEventListener("click", () => {
                    paginaActual = numero;
                    mostrarPagina(paginaActual);
                });
            }

            siguiente.before(btn);
        });
    }

    // === Eventos de navegación ===
    document.querySelector(".pasar--anterior").addEventListener("click", () => {
        if (paginaActual > 1) {
            paginaActual--;
            mostrarPagina(paginaActual);
        }
    });

    document.querySelector(".pasar--siguiente").addEventListener("click", () => {
        if (paginaActual < totalPaginas) {
            paginaActual++;
            mostrarPagina(paginaActual);
        }
    });

    // === Actualizar paginación cuando cambia el filtro ===
    function actualizarPaginacion() {
        paginaActual = 1;
        mostrarPagina(paginaActual);
    }

    // === Inicializar paginación ===
    function inicializarPaginacion() {
        mostrarPagina(paginaActual);
    }

    // Verificar de forma dinámica cuantos registros hay en la tabla
    const textoProductos = document.querySelector(".producto__texto");
    if (textoProductos) {
        const actualizarContadorProductos = () => {
            const filasVisibles = obtenerFilasVisibles();
            const totalFilas = filasVisibles.length;
            textoProductos.textContent = `Hay ${totalFilas} compra${totalFilas !== 1 ? 's' : ''} registrada${totalFilas !== 1 ? 's' : ''}`;
        };

        // Actualizar contador cuando cambien los filtros
        document.getElementById("inputSearch")?.addEventListener('input', actualizarContadorProductos);
        document.getElementById("filtro-estado")?.addEventListener('change', actualizarContadorProductos);
        actualizarContadorProductos();
    }

    // === Evento para redirigir a DetalleVenta ===
    document.querySelectorAll(".clickable-row").forEach(row => {
        row.addEventListener("click", () => {
            const compraId = row.getAttribute("data-id");
            if (compraId) {
                window.location.href = `/admin/compra/detalle-compra/${compraId}`;
            }
        });
    });

    // Inicializar
    inicializarPaginacion();

    // Recalcular paginación cuando la ventana cambie de tamaño (por si acaso)
    window.addEventListener('resize', () => {
        setTimeout(() => mostrarPagina(paginaActual), 100);
    });
});