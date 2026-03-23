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

    //FILTRO DE PRODUCTOS (por texto y estado)
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
                // Texto de todas las celdas excepto menú
                const celdas = Array.from(row.querySelectorAll("td"))
                    .filter(td => !td.classList.contains("menu__actions"))
                    .map(td => td.textContent.toLowerCase())
                    .join(" ");

                // Contamos las estrellas llenas
                const estrellasLlenas = row.querySelectorAll(".td__calificacion .ri-star-fill").length;

                // Convertimos número a palabra para comparar con el select
                const numeroAPalabra = ["uno", "dos", "tres", "cuatro", "cinco"];
                const calificacionTexto = numeroAPalabra[estrellasLlenas - 1] || "";

                const coincideTexto = palabras.length === 0 || palabras.some(palabra => celdas.includes(palabra));
                const coincideEstado =
                    estadoSeleccionado === "seleccionar calificacion" || calificacionTexto === estadoSeleccionado;

                // Mostrar u ocultar la fila
                row.hidden = !(coincideTexto && coincideEstado);
            });
        };

        searchInput.addEventListener('input', filtrarTabla);
        stateSelect.addEventListener('change', filtrarTabla);
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

            Swal.fire({
                title: "¿Estás seguro?",
                text: `Esta acción eliminará esta reseña permanentemente.`,
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
                    window.location.href = `/admin/resenna/delete/${id}`;
                }
            });
        }

        // === OCULTAR / MOSTRAR ===
        else if (e.target.closest(".ocultar")) {
            const btnOcultar = e.target.closest(".ocultar");
            const id = btnOcultar.getAttribute("data-id");
            const activo = btnOcultar.getAttribute("data-activo") === "true";

            const accion = activo ? "ocultar" : "mostrar";
            const titulo = activo ? "¿Deseas ocultar esta reseña?" : "¿Deseas mostrar esta reseña?";
            const texto = activo
                ? `Podrás volver a mostrar esta reseña más adelante.`
                : `La reseña volverá a estar visible en el sistema.`;

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
                    window.location.href = `/admin/resenna/disable/${id}`;
                }
            });
        }
    });

    //pagination de la tabla 
    const filasPorPagina = 12;
    const tablaBody = document.getElementById("favoritos-body");
    const filas = tablaBody.querySelectorAll("tr");
    const totalFilas = filas.length;
    const totalPaginas = Math.ceil(totalFilas / filasPorPagina);

    const contenedorBotones = document.querySelector(".pagination__button");
    const textoPaginacion = document.querySelector(".span__description");

    let paginaActual = 1;

    // === Función para marcar los últimos dos menús visibles ===
    function marcarUltimosDosMenus() {
        // Seleccionar filas visibles
        const filasVisibles = Array.from(tablaBody.querySelectorAll("tr"))
            .filter(tr => tr.style.display !== "none");

        // Limpiar clases previas
        const todosMenus = tablaBody.querySelectorAll("td.menu__actions .dropdown__menu");
        todosMenus.forEach(menu => menu.classList.remove("up"));

        // Tomar las últimas dos filas visibles
        const ultimosDos = filasVisibles.slice(-2);
        ultimosDos.forEach(tr => {
            const menu = tr.querySelector("td.menu__actions .dropdown__menu");
            if (menu) menu.classList.add("up");
        });
    }

    // === Mostrar filas según página seleccionada ===
    function mostrarPagina(pagina) {
        const inicio = (pagina - 1) * filasPorPagina;
        const fin = inicio + filasPorPagina;

        filas.forEach((fila, index) => {
            fila.style.display = (index >= inicio && index < fin) ? "" : "none";
        });

        // Actualizar texto de paginación
        const inicioTexto = inicio + 1;
        const finTexto = Math.min(fin, totalFilas);
        textoPaginacion.textContent = `Mostrando del ${inicioTexto} al ${finTexto} de ${totalFilas} entradas`;

        // Actualizar botones activos
        const botonesPagina = contenedorBotones.querySelectorAll(".button__item");
        botonesPagina.forEach((btn, i) => {
            btn.classList.toggle("active", i + 1 === pagina);
        });

        // Control botones anterior / siguiente
        document.querySelector(".pasar--anterior").disabled = pagina === 1;
        document.querySelector(".pasar--siguiente").disabled = pagina === totalPaginas;

        // Después de actualizar las filas, marcamos los últimos dos menús visibles
        marcarUltimosDosMenus();
    }

    // === Crear botones dinámicamente ===
    function crearBotones() {
        const anterior = contenedorBotones.querySelector(".pasar--anterior");
        const siguiente = contenedorBotones.querySelector(".pasar--siguiente");

        contenedorBotones.querySelectorAll(".button__item").forEach(btn => btn.remove());

        for (let i = 1; i <= totalPaginas; i++) {
            const btn = document.createElement("button");
            btn.classList.add("button__item");
            btn.textContent = i;

            btn.addEventListener("click", () => {
                paginaActual = i;
                mostrarPagina(paginaActual);
            });

            siguiente.before(btn);
        }
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

    // === Inicializar paginación ===
    crearBotones();
    mostrarPagina(paginaActual);

    // verificar de forma dinamica cuantos registros hay en la tabla de productos
    const tablaBodyTotal = document.getElementById("favoritos-body");
    const textoProductos = document.querySelector(".producto__texto");

    if (tablaBodyTotal && textoProductos) {
        const totalFilas = tablaBodyTotal.querySelectorAll("tr").length;
        textoProductos.textContent = `Hay ${totalFilas} reseña${totalFilas !== 1 ? 's' : ''} registrada${totalFilas !== 1 ? 's' : ''}`;
    }


    // Selección de elementos principales
    const filasResenas = document.querySelectorAll(".notification__table tbody tr");
    const modal = document.getElementById("modalDetalle");
    const cerrarModal = document.getElementById("cerrarModal");

    // Elementos del modal
    const modalNombreblog = document.getElementById("modalNombreblog");
    const modalUsuario = document.getElementById("modalUsuario");
    const modalReseña = document.getElementById("modalReseña");
    const modalCalificacion = document.getElementById("modalCalificacion");
    const modalFecha = document.getElementById("modalFecha");

    // Abrir modal al hacer clic en una fila
    filasResenas.forEach(fila => {
        fila.addEventListener("click", (e) => {
            // Evitar abrir modal si se hace clic en los botones del menú
            if (e.target.closest(".menu__actions") || e.target.closest(".dropdown__item")) return;

            const celdas = fila.querySelectorAll("td");

            modalNombreblog.textContent = celdas[0]?.textContent.trim() || "";
            modalUsuario.textContent = celdas[1]?.textContent.trim() || "";
            modalReseña.textContent = celdas[2]?.textContent.trim() || "";
            modalFecha.textContent = celdas[4]?.textContent.trim() || "";

            // Copiar las estrellas
            const estrellasHTML = celdas[3]?.querySelector(".stars")?.innerHTML || "";
            modalCalificacion.innerHTML = estrellasHTML;

            // Mostrar modal
            modal.style.display = "flex";
        });
    });

    // Cerrar modal con la X
    cerrarModal.addEventListener("click", () => {
        modal.style.display = "none";
    });

    // Cerrar modal al hacer clic fuera del contenido
    window.addEventListener("click", (e) => {
        if (e.target === modal) {
            modal.style.display = "none";
        }
    });


});