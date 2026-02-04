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

    // FILTRO DE CARTAS (por texto y estado)
    (() => {
        const searchInput = document.getElementById("inputSearch");
        const stateSelect = document.getElementById("filtro-estado");
        const cards = document.querySelectorAll(".content__card");

        if (!searchInput || !stateSelect || !cards.length) return;

        const normalizar = texto =>
            texto
                .toLowerCase()
                .normalize("NFD")
                .replace(/[\u0300-\u036f]/g, "")
                .trim();

        const filtrarCartas = () => {
            const texto = normalizar(searchInput.value);
            const estadoSeleccionado = normalizar(stateSelect.value);
            const palabras = texto.split(/\s+/).filter(Boolean);

            cards.forEach(card => {
                const contenidoCarta = normalizar(card.textContent);
                const btnEstado = card.querySelector(".buttons__estado");

                const textoEstado = normalizar(btnEstado?.textContent || "");
                const claseEstado = [...(btnEstado?.classList || [])]
                    .map(normalizar)
                    .find(c => c === "activo" || c === "inactivo") || "";

                // Coincidencia de texto flexible, pero exacta para "activo" e "inactivo"
                const coincideTexto =
                    palabras.length === 0 ||
                    palabras.some(palabra => {
                        if (palabra === "activo" || palabra === "inactivo") {
                            // Coincidencia exacta solo para estos dos
                            return (
                                textoEstado === palabra ||
                                claseEstado === palabra
                            );
                        } else {
                            // Coincidencia parcial normal para el resto
                            return (
                                contenidoCarta.includes(palabra) ||
                                textoEstado.includes(palabra) ||
                                claseEstado.includes(palabra)
                            );
                        }
                    });

                // Coincidencia del select
                const coincideEstado =
                    estadoSeleccionado === "todos" ||
                    textoEstado === estadoSeleccionado ||
                    claseEstado === estadoSeleccionado;

                // Mostrar u ocultar carta
                card.style.display = coincideTexto && coincideEstado ? "flex" : "none";
            });
        };

        searchInput.addEventListener("input", filtrarCartas);
        stateSelect.addEventListener("change", filtrarCartas);
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
            const nombre = btnEliminar.getAttribute("data-titulo");

            Swal.fire({
                title: "¿Estás seguro?",
                text: `Esta acción eliminará el blog "${nombre}" permanentemente.`,
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
                    window.location.href = `/admin/blog/delete/${id}`;
                }
            });
        }

        // === EDITAR ===
        else if (e.target.closest(".editar")) {
            const btnEditar = e.target.closest(".editar");

            const id = btnEditar.getAttribute("data-id");
            const nombre = btnEditar.getAttribute("data-titulo");

            Swal.fire({
                title: "¿Estás seguro?",
                text: `¿Deseas editar el blog "${nombre}"?`,
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
                    window.location.href = `/admin/blog/update-blog/${id}`;
                }
            });
        }

        // === OCULTAR / MOSTRAR ===
        else if (e.target.closest(".ocultar")) {
            const btnOcultar = e.target.closest(".ocultar");
            const id = btnOcultar.getAttribute("data-id");
            const nombre = btnOcultar.getAttribute("data-titulo");
            const activo = btnOcultar.getAttribute("data-activo") === "true";

            const accion = activo ? "ocultar" : "mostrar";
            const titulo = activo ? "¿Deseas ocultar este blog?" : "¿Deseas mostrar este blog?";
            const texto = activo
                ? `Podrás volver a mostrar el blog "${nombre}" más adelante.`
                : `El blog "${nombre}" volverá a estar visible en el sistema.`;

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
                    window.location.href = `/admin/blog/disable/${id}`;
                }
            });
        }
    });
    

    //pagination de las cartas del blog
    const cards = document.querySelectorAll(".blog__content .content__card");
    const paginationContainer = document.querySelector(".table__pagination .pagination__button");
    const prevButton = document.querySelector(".pasar--anterior");
    const nextButton = document.querySelector(".pasar--siguiente");

    const cardsPerPage = 9;
    let currentPage = 1;
    const totalPages = Math.ceil(cards.length / cardsPerPage);

    // Mostrar cartas por página
    function showPage(page) {
        const start = (page - 1) * cardsPerPage;
        const end = start + cardsPerPage;

        cards.forEach((card, index) => {
            card.style.display = index >= start && index < end ? "flex" : "none";
        });

        // Actualizar botón activo
        document.querySelectorAll(".button__item").forEach(btn => {
            btn.classList.toggle("active", parseInt(btn.textContent) === page);
        });
    }

    // Crear botones numéricos del paginador
    function createPaginationButtons() {
        // Limpiar botones anteriores si existen
        paginationContainer.querySelectorAll(".button__item").forEach(btn => btn.remove());

        // Crear siempre al menos un botón (aunque haya 9 o menos cartas)
        for (let i = 1; i <= (totalPages === 0 ? 1 : totalPages); i++) {
            const btn = document.createElement("button");
            btn.classList.add("button__item");
            btn.textContent = i;
            if (i === 1) btn.classList.add("active");

            btn.addEventListener("click", () => {
                currentPage = i;
                showPage(currentPage);
            });

            // Insertar antes del botón "Siguiente"
            paginationContainer.insertBefore(btn, nextButton);
        }
    }

    // Eventos de navegación
    prevButton.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    });

    nextButton.addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            showPage(currentPage);
        }
    });

    // Inicializar
    createPaginationButtons();
    showPage(currentPage);


    // verificar de forma dinamica cuantos registros hay en los blog
    const contenedorBlogs = document.querySelector(".blog__content");
    const textoProductos = document.querySelector(".producto__texto");

    if (!contenedorBlogs || !textoProductos) return;

    function actualizarContador() {
        // Contar las cartas visibles (por si ocultas alguna)
        const cartas = Array.from(contenedorBlogs.querySelectorAll(".content__card"))
            .filter(card => getComputedStyle(card).display !== "none");

        const total = cartas.length;
        textoProductos.textContent = `Hay ${total} blog${total === 1 ? "" : "s"} registrado${total === 1 ? "" : "s"}`;
    }

    // Ejecutar al inicio
    actualizarContador();

    // Observar cambios en el contenedor (si se agregan o eliminan cartas)
    const observer = new MutationObserver(() => actualizarContador());
    observer.observe(contenedorBlogs, { childList: true, subtree: false });

});