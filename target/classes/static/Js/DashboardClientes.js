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

    // FILTRO DE CLIENTES (por texto)
    (() => {
        const searchInput = document.getElementById("inputSearch");
        const tableBody = document.getElementById("favoritos-body");

        if (!searchInput || !tableBody) return;

        const tableRows = Array.from(tableBody.rows);

        const filtrarTabla = () => {
            const texto = searchInput.value.trim().toLowerCase();
            const palabras = texto.split(/\s+/).filter(Boolean);

            tableRows.forEach(row => {
                const celdas = Array.from(row.querySelectorAll("td"))
                    .filter(td => !td.classList.contains("menu__actions"))
                    .map(td => td.textContent.toLowerCase())
                    .join(" ");

                const coincideTexto = palabras.length === 0 || palabras.some(palabra => celdas.includes(palabra));
                row.hidden = !coincideTexto;
            });

            // Recalcular paginación después de filtrar
            setTimeout(() => actualizarPaginacion(), 0);
        };

        searchInput.addEventListener('input', filtrarTabla);
    })();

    // Manejar abrir/cerrar los menús
    document.addEventListener("click", function (e) {
        const dropdowns = document.querySelectorAll(".dropdown");
        dropdowns.forEach((dropdown) => {
            const toggle = dropdown.querySelector(".dropdown__toggle");
            const menu = dropdown.querySelector(".dropdown__menu");

            if (toggle.contains(e.target)) {
                dropdown.classList.toggle("show");
            } else {
                dropdown.classList.remove("show");
            }
        });
    });

    // === MOSTRAR RESPUESTA DEL BACK-END ===
    window.addEventListener("DOMContentLoaded", () => {
        const body = document.body;
        const mensaje = body.getAttribute("data-mensaje");
        const success = body.getAttribute("data-success");

        if (mensaje) {
            Swal.fire({
                icon: success === "true" ? "success" : "error",
                title: success === "true" ? "Proceso exitoso" : "Error",
                text: mensaje,
                timer: 3000,
                timerProgressBar: true,
                allowOutsideClick: false,
                allowEscapeKey: false,
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
        }
    });

    // ---- Modales con SweetAlert2 ----
    document.addEventListener("click", (e) => {
        // === ELIMINAR ===
        if (e.target.closest(".eliminar")) {
            const btnEliminar = e.target.closest(".eliminar");
            const id = btnEliminar.getAttribute("data-id");
            const nombre = btnEliminar.getAttribute("data-nombre");

            Swal.fire({
                title: "¿Estás seguro?",
                text: `Esta acción eliminará al cliente "${nombre}" permanentemente.`,
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#d33",
                cancelButtonColor: "#3085d6",
                confirmButtonText: "Sí, eliminar",
                cancelButtonText: "Cancelar",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    // Redirigimos al endpoint del @Controller
                    window.location.href = `/admin/clientes/delete/${id}`;
                }
            });
        }

        // === EDITAR ===
        else if (e.target.closest(".editar")) {
            const btnEditar = e.target.closest(".editar");

            const id = btnEditar.getAttribute("data-id");
            const nombre = btnEditar.getAttribute("data-nombre");

            Swal.fire({
                title: "¿Estás seguro?",
                text: `¿Deseas editar la información del cliente "${nombre}"?`,
                icon: "question",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#6c757d",
                confirmButtonText: "Sí, editar",
                cancelButtonText: "Cancelar",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    // Redirigimos al endpoint del @Controller
                    window.location.href = `/admin/clientes/update-cliente/${id}`;
                }
            });
        }

        // === OCULTAR / MOSTRAR ===
        else if (e.target.closest(".ocultar")) {
            const btnOcultar = e.target.closest(".ocultar");
            const id = btnOcultar.getAttribute("data-id");
            const nombre = btnOcultar.getAttribute("data-nombre");
            const activo = btnOcultar.getAttribute("data-activo") === "true";

            const accion = activo ? "deshabilitar" : "habilitar";
            const titulo = activo ? "¿Deseas deshabilitar este cliente?" : "¿Deseas habilitar este cliente?";
            const texto = activo
                ? `Podrás volver a habilitar al cliente "${nombre}" más adelante.`
                : `El cliente "${nombre}" volverá a estar habilitado en el sistema.`;

            Swal.fire({
                title: titulo,
                text: texto,
                icon: "question",
                showCancelButton: true,
                confirmButtonText: `Sí, ${accion}`,
                cancelButtonText: "Cancelar",
                confirmButtonColor: "#6c757d",
                cancelButtonColor: "#3085d6",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    // Redirigimos al endpoint del @Controller
                    window.location.href = `/admin/clientes/disable/${id}`;
                }
            });
        }
    });

    // PAGINACIÓN PROFESIONAL PARA CLIENTES
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

        // Asegurarse de que la página actual sea válida
        paginaActual = Math.max(1, Math.min(pagina, totalPaginas));

        const inicio = (paginaActual - 1) * filasPorPagina;
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
        const btnAnterior = document.querySelector(".pasar--anterior");
        const btnSiguiente = document.querySelector(".pasar--siguiente");

        if (btnAnterior) {
            btnAnterior.disabled = paginaActual === 1;
        }
        if (btnSiguiente) {
            btnSiguiente.disabled = paginaActual === totalPaginas || totalPaginas === 0;
        }

        // Actualizar botones de paginación
        crearBotonesPaginacion(paginaActual);

        // Marcar últimos dos menús
        marcarUltimosDosMenus();
    }

    // === Crear botones de paginación inteligente ===
    function crearBotonesPaginacion(paginaActual) {
        const btnSiguiente = contenedorBotones.querySelector(".pasar--siguiente");

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
                    mostrarPagina(numero);
                });
            }

            if (btnSiguiente) {
                btnSiguiente.before(btn);
            }
        });
    }

    // === Eventos de navegación ===
    document.querySelector(".pasar--anterior").addEventListener("click", () => {
        if (paginaActual > 1) {
            mostrarPagina(paginaActual - 1);
        }
    });

    document.querySelector(".pasar--siguiente").addEventListener("click", () => {
        if (paginaActual < totalPaginas) {
            mostrarPagina(paginaActual + 1);
        }
    });

    // === Actualizar paginación cuando cambia el filtro ===
    function actualizarPaginacion() {
        mostrarPagina(1);
    }

    // === Inicializar paginación ===
    function inicializarPaginacion() {
        mostrarPagina(1);
    }

    // Verificar de forma dinámica cuantos registros hay en la tabla de clientes
    const textoClientes = document.querySelector(".producto__texto");
    if (textoClientes) {
        const actualizarContadorClientes = () => {
            const filasVisibles = obtenerFilasVisibles();
            const totalFilas = filasVisibles.length;
            textoClientes.textContent = `Hay ${totalFilas} cliente${totalFilas !== 1 ? 's' : ''} registrado${totalFilas !== 1 ? 's' : ''}`;
        };

        // Actualizar contador cuando cambien los filtros
        document.getElementById("inputSearch")?.addEventListener('input', actualizarContadorClientes);
        actualizarContadorClientes();
    }

    // Inicializar
    inicializarPaginacion();

    // Recalcular paginación cuando la ventana cambie de tamaño
    window.addEventListener('resize', () => {
        setTimeout(() => mostrarPagina(paginaActual), 100);
    });
});