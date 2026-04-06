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
            e.stopPropagation();
            subMenu.classList.toggle("open__menu");
        });

        document.addEventListener("click", function (e) {
            if (!subMenu.contains(e.target) && !profileImage.contains(e.target)) {
                subMenu.classList.remove("open__menu");
            }
        });
    }

    //abrir la notificaciones del admin
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

    // Variables globales
    let todasLasPeticiones = [];
    let peticionesFiltradas = [];
    let currentPage = 1;
    const cardsPerPage = 6;

    // Inicializar la aplicación
    function inicializarAplicacion() {
        cargarPeticiones();
        inicializarEventListeners();
    }

    // Cargar peticiones desde Thymeleaf
    function cargarPeticiones() {
        if (window.peticionesData && Array.isArray(window.peticionesData)) {
            todasLasPeticiones = window.peticionesData;
        } else {
            todasLasPeticiones = [];
        }

        peticionesFiltradas = [...todasLasPeticiones];
        actualizarContadores();
        renderizarCards();
        inicializarPaginacion();
    }

    // Actualizar contadores del dashboard
    function actualizarContadores() {
        const pendientes = todasLasPeticiones.filter(p => p.estadoPeticion === 'Pendiente').length;
        const enProceso = todasLasPeticiones.filter(p => p.estadoPeticion === 'En Proceso').length;
        const resueltos = todasLasPeticiones.filter(p => p.estadoPeticion === 'Resuelto').length;
        const total = todasLasPeticiones.length;

        document.getElementById('contador-pendientes').textContent = pendientes;
        document.getElementById('contador-proceso').textContent = enProceso;
        document.getElementById('contador-resueltos').textContent = resueltos;
        document.getElementById('contador-total').textContent = total;
    }

    // Renderizar cards dinámicamente
    function renderizarCards() {
        const container = document.getElementById('cards-container');
        const tituloSolicitudes = document.getElementById('titulo-solicitudes');

        container.innerHTML = '';

        if (peticionesFiltradas.length === 0) {
            container.innerHTML = `
                <div class="content__card card__vacia">
                    <div class="card__information">
                        <h3 class="information__title">No se encontraron solicitudes</h3>
                        <p class="information__text">Prueba ajustando los filtros o busca con otras palabras clave.</p>
                    </div>
                </div>
            `;
            tituloSolicitudes.textContent = `Solicitudes (0)`;
            return;
        }

        tituloSolicitudes.textContent = `Solicitudes (${peticionesFiltradas.length})`;

        peticionesFiltradas.forEach((peticion) => {
            const card = document.createElement('div');
            card.className = 'content__card';
            card.setAttribute('data-peticion-id', peticion.id);

            const tipoClase = peticion.tipoPeticion ? peticion.tipoPeticion.replace(/\s+/g, '') : '';
            const estadoClase = peticion.estadoPeticion ? peticion.estadoPeticion.replace(/\s+/g, '') : '';

            // Manejo seguro de datos del usuario
            const usuario = peticion.usuario || peticion.usurarioResponse || {};
            const nombres = usuario.nombres || '';
            const apellidos = usuario.apellidos || '';
            const correo = usuario.correo || '';
            const telefono = usuario.telefono || '';

            card.innerHTML = `
                <div class="card__estados">
                    <div class="estados__button">
                        <button class="button__item ${tipoClase}">${peticion.tipoPeticion || 'Sin tipo'}</button>
                        <button class="button__item ${estadoClase}">
                            <i class="ri-time-line"></i>
                            <span>${peticion.estadoPeticion || 'Sin estado'}</span>
                        </button>
                    </div>
                    <div class="dropdown">
                        <button class="dropdown__toggle">
                            <i class="ri-more-2-fill"></i>
                        </button>
                        <div class="dropdown__menu">
                            <button class="dropdown__item eliminar" data-id="${peticion.id}">
                                <i class="ri-delete-bin-5-line"></i> Eliminar
                            </button>
                            <button class="dropdown__item ver" data-id="${peticion.id}">
                                <i class="ri-eye-line"></i> Ver
                            </button>
                        </div>
                    </div>
                </div>
                <div class="card__information">
                    <h4 class="information__title">${peticion.asunto || 'Sin asunto'}</h4>
                    <p class="information__text">
                        <i class="ri-user-line"></i>
                        <span>${nombres} ${apellidos}</span>
                    </p>
                    <p class="information__date">
                        <i class="ri-calendar-line"></i>
                        <span>${formatearFecha(peticion.fecha)}</span>
                    </p>
                </div>
            `;

            container.appendChild(card);
        });

        aplicarPaginacion();
    }

    // Formatear fecha
    function formatearFecha(fecha) {
        if (!fecha) return 'Sin fecha';
        try {
            const date = new Date(fecha);
            if (isNaN(date.getTime())) {
                return 'Fecha inválida';
            }
            return date.toLocaleDateString('es-ES', {
                day: '2-digit',
                month: 'long',
                year: 'numeric'
            });
        } catch (error) {
            return 'Fecha inválida';
        }
    }

    // Inicializar event listeners
    function inicializarEventListeners() {
        const searchInput = document.getElementById("inputSearch");
        const selectSolicitud = document.getElementById("filtro-solicitud");
        const selectEstado = document.getElementById("filtro-estado");

        if (searchInput && selectSolicitud && selectEstado) {
            searchInput.addEventListener("input", aplicarFiltros);
            selectSolicitud.addEventListener("change", aplicarFiltros);
            selectEstado.addEventListener("change", aplicarFiltros);
        }

        inicializarModal();
    }

    // Aplicar filtros
    function aplicarFiltros() {
        const searchInput = document.getElementById("inputSearch");
        const selectSolicitud = document.getElementById("filtro-solicitud");
        const selectEstado = document.getElementById("filtro-estado");

        const texto = normalizarTexto(searchInput.value.trim());
        const tipoSolicitud = selectSolicitud.value;
        const estado = selectEstado.value;

        peticionesFiltradas = todasLasPeticiones.filter(peticion => {
            // Manejo seguro de datos del usuario para filtros
            const usuario = peticion.usuario || peticion.usurarioResponse || {};
            const nombres = usuario.nombres || '';
            const apellidos = usuario.apellidos || '';

            const coincideTexto = texto === '' ||
                normalizarTexto(peticion.asunto || '').includes(texto) ||
                normalizarTexto(nombres + ' ' + apellidos).includes(texto);

            const coincideTipo = tipoSolicitud === 'Todos' || peticion.tipoPeticion === tipoSolicitud;
            const coincideEstado = estado === 'Todos' || peticion.estadoPeticion === estado;

            return coincideTexto && coincideTipo && coincideEstado;
        });

        currentPage = 1;
        renderizarCards();
        actualizarPaginacion();
    }

    // Normalizar texto para búsqueda
    function normalizarTexto(texto) {
        return texto
            .toLowerCase()
            .normalize("NFD")
            .replace(/[\u0300-\u036f]/g, "");
    }

    // Paginación
    function inicializarPaginacion() {
        const prevButton = document.querySelector(".pasar--anterior");
        const nextButton = document.querySelector(".pasar--siguiente");

        if (prevButton && nextButton) {
            prevButton.addEventListener("click", () => {
                if (currentPage > 1) {
                    currentPage--;
                    aplicarPaginacion();
                }
            });

            nextButton.addEventListener("click", () => {
                const totalPages = Math.ceil(peticionesFiltradas.length / cardsPerPage);
                if (currentPage < totalPages) {
                    currentPage++;
                    aplicarPaginacion();
                }
            });
        }
    }

    function aplicarPaginacion() {
        const cards = document.querySelectorAll("#cards-container .content__card:not(.card__vacia)");
        const startIndex = (currentPage - 1) * cardsPerPage;
        const endIndex = startIndex + cardsPerPage;

        cards.forEach((card, index) => {
            card.style.display = (index >= startIndex && index < endIndex) ? 'block' : 'none';
        });

        actualizarPaginacion();
    }

    function actualizarPaginacion() {
        const totalPages = Math.ceil(peticionesFiltradas.length / cardsPerPage);
        const prevButton = document.querySelector(".pasar--anterior");
        const nextButton = document.querySelector(".pasar--siguiente");
        const paginationNumbers = document.getElementById("pagination-numbers");

        if (prevButton) prevButton.disabled = currentPage === 1;
        if (nextButton) nextButton.disabled = currentPage === totalPages || totalPages === 0;

        // Generar números de página
        if (paginationNumbers) {
            paginationNumbers.innerHTML = '';

            // Mostrar máximo 5 páginas
            let startPage = Math.max(1, currentPage - 2);
            let endPage = Math.min(totalPages, startPage + 4);

            // Ajustar si estamos cerca del final
            if (endPage - startPage < 4) {
                startPage = Math.max(1, endPage - 4);
            }

            for (let i = startPage; i <= endPage; i++) {
                const pageNumber = document.createElement('button');
                pageNumber.className = 'page-number';
                pageNumber.textContent = i;
                pageNumber.dataset.page = i;

                if (i === currentPage) {
                    pageNumber.classList.add('active');
                }

                pageNumber.addEventListener('click', () => {
                    currentPage = i;
                    aplicarPaginacion();
                });

                paginationNumbers.appendChild(pageNumber);
            }
        }
    }

    // Modal
    function inicializarModal() {
        const modal = document.getElementById("modalDetalle");
        const cerrarModal = document.getElementById("cerrarModal");
        const formEstado = document.getElementById("formEstado");

        if (cerrarModal) {
            cerrarModal.addEventListener("click", cerrarModalVentana);
        }

        if (modal) {
            modal.addEventListener("click", (e) => {
                if (e.target === modal) {
                    cerrarModalVentana();
                }
            });
        }

        if (formEstado) {
            formEstado.addEventListener("submit", actualizarEstadoPeticion);
        }

        document.addEventListener("click", (e) => {
            // Abrir modal al hacer click en cualquier parte de la card (excepto dropdown y eliminar)
            const card = e.target.closest(".content__card");
            if (card && !e.target.closest(".dropdown") && !e.target.closest(".eliminar")) {
                const peticionId = card.getAttribute('data-peticion-id');
                abrirModal(peticionId);
                return;
            }

            // Dropdown
            if (e.target.closest(".dropdown__toggle")) {
                const dropdown = e.target.closest(".dropdown");
                document.querySelectorAll(".dropdown").forEach(d => {
                    if (d !== dropdown) d.classList.remove("show");
                });
                dropdown.classList.toggle("show");
                e.stopPropagation();
            }

            // Eliminar
            if (e.target.closest(".eliminar")) {
                e.stopPropagation();
                const id = e.target.closest(".eliminar").getAttribute("data-id");
                eliminarPeticion(id);
            }

            // Ver (modal) desde el dropdown
            if (e.target.closest(".ver")) {
                e.stopPropagation();
                const id = e.target.closest(".ver").getAttribute("data-id");
                abrirModal(id);
                const dropdown = e.target.closest(".dropdown");
                if (dropdown) dropdown.classList.remove("show");
            }

            // Cerrar dropdowns al hacer clic fuera
            if (!e.target.closest(".dropdown")) {
                document.querySelectorAll(".dropdown").forEach(d => {
                    d.classList.remove("show");
                });
            }
        });
    }

    function abrirModal(peticionId) {
        const modal = document.getElementById("modalDetalle");
        const peticion = todasLasPeticiones.find(p => p.id == peticionId);

        if (!peticion) return;

        // Manejo seguro de datos del usuario para el modal
        const usuario = peticion.usuario || peticion.usurarioResponse || {};
        const nombres = usuario.nombres || '';
        const apellidos = usuario.apellidos || '';
        const correo = usuario.correo || '';
        const telefono = usuario.telefono || '';

        document.getElementById('modal-tipo').textContent = peticion.tipoPeticion || 'Sin tipo';
        document.getElementById('modal-tipo').className = `button__item ${(peticion.tipoPeticion || '').replace(/\s+/g, '')}`;

        document.getElementById('modal-estado').innerHTML = `<i class="ri-time-line"></i> <span>${peticion.estadoPeticion || 'Sin estado'}</span>`;
        document.getElementById('modal-estado').className = `button__item ${(peticion.estadoPeticion || '').replace(/\s+/g, '')}`;

        document.getElementById('modal-asunto').textContent = peticion.asunto || 'Sin asunto';
        document.getElementById('modal-fecha').textContent = formatearFecha(peticion.fecha);
        document.getElementById('modal-nombre').textContent = `${nombres} ${apellidos}`;
        document.getElementById('modal-email').textContent = correo;
        document.getElementById('modal-telefono').textContent = telefono;
        document.getElementById('modal-mensaje').textContent = peticion.mensaje || 'Sin mensaje';
        document.getElementById('modal-peticion-id').value = peticion.id;

        document.getElementById('select-estado-modal').value = peticion.estadoPeticion || 'Pendiente';

        modal.style.display = "flex";
    }

    function cerrarModalVentana() {
        const modal = document.getElementById("modalDetalle");
        modal.style.display = "none";
    }

    function actualizarEstadoPeticion(e) {
        e.preventDefault();
        const peticionId = document.getElementById('modal-peticion-id').value;
        const nuevoEstado = document.getElementById('select-estado-modal').value;

        // Redirigir al endpoint del controlador para cambiar el estado
        window.location.href = `/admin/pqrs/change-estado/${peticionId}?estadoPeticion=${encodeURIComponent(nuevoEstado)}`;
    }

    function eliminarPeticion(peticionId) {
        Swal.fire({
            title: "¿Estás seguro?",
            text: "Esta acción eliminará el registro permanentemente.",
            icon: "warning",
            showCancelButton: true,
            confirmButtonColor: "#d33",
            cancelButtonColor: "#3085d6",
            confirmButtonText: "Sí, eliminar",
            cancelButtonText: "Cancelar"
        }).then((result) => {
            if (result.isConfirmed) {
                // Redirigir al endpoint del controlador para eliminar
                window.location.href = `/admin/pqrs/delete/${peticionId}`;
            }
        });
    }

    // === MOSTRAR RESPUESTA DEL BACK-END ===
    function mostrarMensajeBackend() {
        // Obtener parámetros de la URL
        const urlParams = new URLSearchParams(window.location.search);
        const mensaje = urlParams.get('mensaje');
        const success = urlParams.get('success');

        if (mensaje) {
            // Decodificar el mensaje (ya que fue codificado en el controlador)
            const mensajeDecodificado = decodeURIComponent(mensaje);

            Swal.fire({
                icon: success === "true" ? "success" : "error",
                title: success === "true" ? "Proceso exitoso" : "Error",
                text: mensajeDecodificado,
                timer: 3000,
                timerProgressBar: true,
                allowOutsideClick: false,
                allowEscapeKey: false,
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            }).then(() => {
                // Limpiar la URL después de mostrar el mensaje
                const urlSinParametros = window.location.pathname;
                window.history.replaceState({}, document.title, urlSinParametros);
            });
        }
    }

    // Inicializar la aplicación
    inicializarAplicacion();

    // Mostrar mensajes del backend después de la inicialización
    mostrarMensajeBackend();
});