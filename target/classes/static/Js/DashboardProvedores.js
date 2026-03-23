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

    // llevar a la pagina de nueva compra
//    document.querySelectorAll(".dropdown__item.compra").forEach(boton => {
//        boton.addEventListener("click", () => {
//            window.location.href = "/admin/provedores/nueva-compra";
//        });
//    });

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
                text: `Esta acción eliminará al proveedor "${nombre}" permanentemente.`,
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
                    window.location.href = `/admin/provedores/delete/${id}`;
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
                text: `¿Deseas editar la información del proveedor "${nombre}"?`,
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
                    window.location.href = `/admin/provedores/update-proveedor/${id}`;
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
            const titulo = activo ? "¿Deseas deshabilitar este proveedor?" : "¿Deseas habilitar este proveedor?";
            const texto = activo
                ? `Podrás volver a habilitar al proveedor "${nombre}" más adelante.`
                : `El proveedor "${nombre}" volverá a estar habilitado en el sistema.`;

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
                    window.location.href = `/admin/provedores/disable/${id}`;
                }
            });
        }
    });


    // Paginación de las cards de proveedores
    const cards = document.querySelectorAll(".principal__container .container__card");
    const paginationContainer = document.querySelector(".pagination__button");
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
        // Limpiar botones previos
        paginationContainer.querySelectorAll(".button__item").forEach(btn => btn.remove());

        // Crear mínimo un botón
        for (let i = 1; i <= (totalPages === 0 ? 1 : totalPages); i++) {
            const btn = document.createElement("button");
            btn.classList.add("button__item");
            btn.textContent = i;

            if (i === 1) btn.classList.add("active");

            btn.addEventListener("click", () => {
                currentPage = i;
                showPage(currentPage);
            });

            // Insertar antes del botón siguiente
            paginationContainer.insertBefore(btn, nextButton);
        }
    }

    // Botón anterior
    prevButton.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            showPage(currentPage);
        }
    });

    // Botón siguiente
    nextButton.addEventListener("click", () => {
        if (currentPage < totalPages) {
            currentPage++;
            showPage(currentPage);
        }
    });

    // Inicializar
    createPaginationButtons();
    showPage(currentPage);

    // Selecciona el <p> donde irá el número dinámico
    const productoTexto = document.querySelector(".producto__texto");

    // Actualiza el texto automáticamente
    if (productoTexto) {
        const total = cards.length;
        productoTexto.textContent = `Hay ${total} proveedores registrados`;
    }
});