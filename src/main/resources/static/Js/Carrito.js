import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {

    // Usar la funcion activarGlassmorphism
    activarGlassmorphism();

    inicialHeart();

    initCart();

    toggleMenu();

    //rellenar la tabla mediante localStogare
    const cartTableBody = document.getElementById("favoritos-body");
    const paginationText = document.getElementById("pagination-text");
    const prevPageBtn = document.getElementById("prev-page");
    const nextPageBtn = document.getElementById("next-page");
    const paginationButtonsContainer = document.getElementById("pagination-buttons");
    const thereSpan = document.getElementById("there?");
    const totalEl = document.getElementById("content__precio");

    let cart = JSON.parse(localStorage.getItem("cart")) || [];
    let currentPage = 1;
    const rowsPerPage = 4;

    // === CUPONES ESTÁTICOS ===
    const cuponesEstaticos = {
        "DESCUENTO-2025": 5,    // 5% de descuento
        "PROMO-2025": 10,       // 10% de descuento
        "VERANO-2025": 15,      // 15% de descuento
        "BLACKFRIDAY": 25       // 25% de descuento
    };

    let cuponAplicado = null;
    let descuentoAplicado = 0;

    // === FUNCIÓN PARA APLICAR CUPÓN ===
    function aplicarCupon(codigoCupon) {
        const cupon = codigoCupon.trim().toUpperCase();

        if (cuponesEstaticos.hasOwnProperty(cupon)) {
            cuponAplicado = cupon;
            descuentoAplicado = cuponesEstaticos[cupon];

            // Guardar en localStorage para persistencia
            localStorage.setItem('cuponAplicado', JSON.stringify({
                codigo: cuponAplicado,
                descuento: descuentoAplicado
            }));

            Swal.fire({
                title: "¡Cupón aplicado!",
                text: `Se ha aplicado un ${descuentoAplicado}% de descuento`,
                icon: "success",
                timer: 3000,
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });

            return true;
        } else {
            cuponAplicado = null;
            descuentoAplicado = 0;
            localStorage.removeItem('cuponAplicado');

            Swal.fire({
                title: "Cupón inválido",
                text: "El código de cupón no es válido",
                icon: "error",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });

            return false;
        }
    }

    // === FUNCIÓN PARA CALCULAR DESCUENTO ===
    function calcularDescuento(subtotal) {
        if (cuponAplicado && descuentoAplicado > 0) {
            return (subtotal * descuentoAplicado) / 100;
        }
        return 0;
    }

    // === CARGAR CUPÓN AL INICIAR ===
    function cargarCuponGuardado() {
        const cuponGuardado = localStorage.getItem('cuponAplicado');
        if (cuponGuardado) {
            try {
                const cuponData = JSON.parse(cuponGuardado);
                cuponAplicado = cuponData.codigo;
                descuentoAplicado = cuponData.descuento;

                // Actualizar el input del cupón
                const cuponInput = document.getElementById("cupon");
                if (cuponInput) {
                    cuponInput.value = cuponAplicado;
                }
            } catch (error) {
                console.error("Error al cargar cupón guardado:", error);
                cuponAplicado = null;
                descuentoAplicado = 0;
            }
        }
    }

    // === MANEJO DE RESPUESTAS DE MERCADO PAGO ===
    function handleMercadoPagoResponse() {
        const urlParams = new URLSearchParams(window.location.search);
        const status = urlParams.get('status');
        const compraId = urlParams.get('compra');

        if (status && compraId) {
            // Limpiar los parámetros de la URL sin recargar la página
            window.history.replaceState({}, document.title, window.location.pathname);

            const alertStyles = {
                success: {
                    title: '¡Pago Completado Exitosamente!',
                    icon: 'success',
                    titleColor: '#10B981',
                    borderColor: '#10B981',
                    iconHtml: `
                        <div class="alert-icon success">
                            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                                <circle cx="24" cy="24" r="24" fill="#10B981" fill-opacity="0.1"/>
                                <path d="M20 24L23 27L28 22" stroke="#10B981" stroke-width="3" stroke-linecap="round" stroke-linejoin="round"/>
                                <circle cx="24" cy="24" r="23" stroke="#10B981" stroke-width="2"/>
                            </svg>
                        </div>
                    `,
                    buttonText: 'Continuar',
                    buttonClass: 'btn-success'
                },
                error: {
                    title: 'Transacción No Completada',
                    icon: 'error',
                    titleColor: '#EF4444',
                    borderColor: '#EF4444',
                    iconHtml: `
                        <div class="alert-icon error">
                            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                                <circle cx="24" cy="24" r="24" fill="#EF4444" fill-opacity="0.1"/>
                                <path d="M18 18L30 30M30 18L18 30" stroke="#EF4444" stroke-width="3" stroke-linecap="round"/>
                                <circle cx="24" cy="24" r="23" stroke="#EF4444" stroke-width="2"/>
                            </svg>
                        </div>
                    `,
                    buttonText: 'Reintentar',
                    buttonClass: 'btn-error'
                },
                pending: {
                    title: 'Pago en Proceso',
                    icon: 'warning',
                    titleColor: '#F59E0B',
                    borderColor: '#F59E0B',
                    iconHtml: `
                        <div class="alert-icon pending">
                            <svg width="48" height="48" viewBox="0 0 48 48" fill="none">
                                <circle cx="24" cy="24" r="24" fill="#F59E0B" fill-opacity="0.1"/>
                                <path d="M24 16V24L28 28" stroke="#F59E0B" stroke-width="3" stroke-linecap="round"/>
                                <circle cx="24" cy="24" r="23" stroke="#F59E0B" stroke-width="2"/>
                            </svg>
                        </div>
                    `,
                    buttonText: 'Entendido',
                    buttonClass: 'btn-warning'
                }
            };

            const config = alertStyles[status];
            if (!config) return;

            const getSuccessContent = () => `
                <div class="alert-content">
                    ${config.iconHtml}
                    <div class="alert-body">
                        <h3 class="alert-title" style="color: ${config.titleColor}">${config.title}</h3>
                        <div class="alert-details">
                            <div class="detail-item">
                                <span class="detail-label">Número de Orden:</span>
                                <span class="detail-value">#${compraId}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Estado:</span>
                                <span class="status-badge success">Completado</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Fecha:</span>
                                <span class="detail-value">${new Date().toLocaleDateString('es-CO')}</span>
                            </div>
                        </div>
                        <div class="alert-message">
                            <p>Hemos recibido tu pago correctamente. Recibirás un comprobante en tu correo electrónico en los próximos minutos.</p>
                        </div>
                    </div>
                </div>
            `;

            const getErrorContent = () => `
                <div class="alert-content">
                    ${config.iconHtml}
                    <div class="alert-body">
                        <h3 class="alert-title" style="color: ${config.titleColor}">${config.title}</h3>
                        <div class="alert-details">
                            <div class="detail-item">
                                <span class="detail-label">Referencia:</span>
                                <span class="detail-value">#${compraId}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Estado:</span>
                                <span class="status-badge error">Rechazado</span>
                            </div>
                        </div>
                        <div class="alert-message">
                            <p class="message-title">Posibles causas:</p>
                            <ul class="reason-list">
                                <li>Fondos insuficientes en la cuenta</li>
                                <li>Límite de la tarjeta excedido</li>
                                <li>Datos de la tarjeta incorrectos</li>
                                <li>Problemas temporales del sistema</li>
                            </ul>
                            <p class="suggestion">Te recomendamos verificar los datos e intentar nuevamente.</p>
                        </div>
                    </div>
                </div>
            `;

            const getPendingContent = () => `
                <div class="alert-content">
                    ${config.iconHtml}
                    <div class="alert-body">
                        <h3 class="alert-title" style="color: ${config.titleColor}">${config.title}</h3>
                        <div class="alert-details">
                            <div class="detail-item">
                                <span class="detail-label">Número de Orden:</span>
                                <span class="detail-value">#${compraId}</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Estado:</span>
                                <span class="status-badge pending">En Revisión</span>
                            </div>
                            <div class="detail-item">
                                <span class="detail-label">Tiempo Estimado:</span>
                                <span class="detail-value">24-48 horas</span>
                            </div>
                        </div>
                        <div class="alert-message">
                            <p>Tu transacción está siendo procesada por nuestro sistema. Este proceso puede tomar hasta 48 horas.</p>
                            <p class="notification-info">Recibirás una notificación por correo electrónico una vez completado el proceso.</p>
                        </div>
                    </div>
                </div>
            `;

            const content = {
                'success': getSuccessContent(),
                'error': getErrorContent(),
                'pending': getPendingContent()
            }[status];

            Swal.fire({
                html: content,
                showConfirmButton: true,
                confirmButtonText: config.buttonText,
                customClass: {
                    popup: 'professional-alert',
                    confirmButton: config.buttonClass,
                    actions: 'alert-actions'
                },
                buttonsStyling: false,
                backdrop: 'rgba(0, 0, 0, 0.6)',
                willClose: status === 'success' ? () => {
                    localStorage.removeItem("cart");
                    localStorage.removeItem("cuponAplicado"); // Limpiar cupón después de compra exitosa
                    window.location.reload();
                } : undefined
            });
        }
    }

    // --- Calcular total del carrito ---
    function updateCartTotal() {
        const subtotal = cart.reduce((acc, item) => acc + (parseInt(item.price) * item.qty), 0);
        totalEl.textContent = `$${parseInt(subtotal).toLocaleString('es-CO')}`;

        // Calcular descuento del cupón
        const descuento = calcularDescuento(subtotal);

        // Actualizar display del cupón
        const cuponEl = document.getElementById("cupon__envio");
        cuponEl.textContent = `-$${parseInt(descuento).toLocaleString('es-CO')}`;

        // Envío (siempre gratis en tu HTML actual → 0)
        const envio = 0;

        // Calcular total final
        const totalFinal = subtotal - descuento + envio;

        // Actualizar el total en pantalla
        const totalAllEl = document.getElementById("total__all");
        totalAllEl.textContent = `$${parseInt(totalFinal).toLocaleString('es-CO')}`;

        // Mostrar info del cupón aplicado si existe
        if (cuponAplicado && descuentoAplicado > 0) {
            // Limpiar info anterior y agregar nueva
            const existingInfo = document.querySelector('.cupon-info');
            if (existingInfo) {
                existingInfo.remove();
            }
            cuponEl.parentNode.appendChild(cuponInfo);
        }
    }

    // --- Render tabla ---
    function renderTable() {
        cartTableBody.innerHTML = "";

        if (cart.length === 0) {
            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td colspan="6" style="text-align:center; padding:20px; font-weight:600;">
                    No hay productos en el carrito
                </td>
            `;
            cartTableBody.appendChild(tr);

            paginationText.textContent = `Mostrando 0-0 de 0`;
            paginationButtonsContainer.innerHTML = "";
            prevPageBtn.disabled = true;
            nextPageBtn.disabled = true;
            thereSpan.textContent = `Hay 0 productos en el carrito.`;

            // Si no hay productos, el total debe ser 0
            updateCartTotal();
            return;
        }

        const start = (currentPage - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        const paginatedItems = cart.slice(start, end);

        paginatedItems.forEach((item, index) => {
            const globalIndex = start + index; // posición real en el array
            const subtotal = parseInt(item.price) * item.qty;

            const tr = document.createElement("tr");
            tr.innerHTML = `
                <td><img src="${item.img}" width="50"/></td>
                <td>${item.name}</td>
                <td>
                    <div class="quantity-control" data-index="${globalIndex}">
                        <button class="minus">−</button>
                        <span class="qty">${item.qty}</span>
                        <button class="plus" ${item.stock !== null && item.qty >= item.stock ? 'disabled style="opacity:0.5; cursor:not-allowed;"' : ''}>+</button> <!-- VALIDAR STOCK -->
                    </div>
                </td>
                <td>$${parseInt(item.price).toLocaleString('es-CO')}</td>
                <td>$${parseInt(subtotal).toLocaleString('es-CO')}</td>
                <td>
                    <button class="content__icon btn-delete" data-index="${globalIndex}">
                        <i class="ri-delete-bin-6-line" title="Eliminar"></i>
                    </button>
                </td>
            `;
            cartTableBody.appendChild(tr);
        });

        paginationText.textContent = `Mostrando ${start + 1}-${Math.min(end, cart.length)} de ${cart.length}`;
        thereSpan.textContent = `Hay ${cart.length} productos en el carrito.`;

        renderPaginationButtons();

        // Actualizar el total del carrito
        updateCartTotal();
    }

    // --- Render dinámico de los botones de página ---
    function renderPaginationButtons() {
        // Elimina botones de página previos (manteniendo Anterior/Siguiente)
        paginationButtonsContainer.querySelectorAll(".button__item").forEach(btn => btn.remove());

        const totalPages = Math.ceil(cart.length / rowsPerPage);

        for (let i = 1; i <= totalPages; i++) {
            const btn = document.createElement("button");
            btn.textContent = i;
            btn.classList.add("button__item");

            if (i === currentPage) btn.classList.add("active");

            btn.addEventListener("click", () => {
                currentPage = i;
                renderTable();
            });

            nextPageBtn.before(btn); // inserta antes del botón "Siguiente"
        }

        prevPageBtn.disabled = currentPage === 1;
        nextPageBtn.disabled = currentPage === totalPages;
    }

    // === MANEJO DEL FORMULARIO DE CUPÓN ===
    function setupCuponForm() {
        const formularioCupon = document.querySelector('.formulario__cupon');
        const cuponInput = document.getElementById('cupon');

        if (formularioCupon) {
            formularioCupon.addEventListener('submit', (e) => {
                e.preventDefault();

                const codigoCupon = cuponInput.value.trim();

                if (!codigoCupon) {
                    Swal.fire({
                        title: "Campo vacío",
                        text: "Por favor ingresa un código de cupón",
                        icon: "warning",
                        customClass: {
                            title: 'swal-title',
                            popup: 'swal-popup'
                        }
                    });
                    return;
                }

                // Aplicar cupón
                if (aplicarCupon(codigoCupon)) {
                    // Actualizar los totales
                    updateCartTotal();
                }
            });
        }
    }

    // Configuración de Mercado Pago (usa tu PUBLIC KEY)
    const mp = new MercadoPago('APP_USR-e36b15d6-08bc-4b88-b64f-6b255117534f', {
        locale: 'es-CO'
    });

    const MP = async () => {
        try {
            const cart = JSON.parse(localStorage.getItem("cart")) || [];

            // Validar carrito vacío
            if (cart.length === 0) {
                Swal.fire({
                    title: "Carrito vacío",
                    text: "Agrega productos al carrito antes de finalizar la compra",
                    icon: "warning",
                    confirmButtonText: "Entendido",
                    customClass: {
                        title: 'swal-title',
                        popup: 'swal-popup'
                    }
                });
                return;
            }

            // Validar stock antes de proceder
            for (const item of cart) {
                if (item.stock !== null && item.qty > item.stock) {
                    Swal.fire({
                        title: "Stock insuficiente",
                        text: `No hay suficiente stock de ${item.name}. Máximo disponible: ${item.stock} unidades`,
                        icon: "error",
                        confirmButtonText: "Entendido",
                        customClass: {
                            title: 'swal-title',
                            popup: 'swal-popup'
                        }
                    });
                    return;
                }
            }

            // Mostrar loading
            Swal.fire({
                title: "Procesando compra",
                html: `
                    <div class="loading-container">
                        <div class="loading-text">Validando información y stock</div>
                        <div class="progress-bar">
                            <div class="progress-fill"></div>
                        </div>
                    </div>
                `,
                allowOutsideClick: false,
                showConfirmButton: false,
                showCancelButton: false,
                backdrop: true,
                width: '500px',
                customClass: {
                    popup: 'professional-loading-popup',
                    container: 'professional-loading-container'
                }
            });

            // Preparar request para el backend
            const compraRequest = {
                detalleVentaRequests: cart.map(item => ({
                    productoId: item.id,
                    cantidad: item.qty
                })),
                metodoPago: "MERCADO_PAGO",
                cuponDescuento: cuponAplicado || "" // Incluir cupón aplicado
            };

            console.log("Enviando compra al backend:", compraRequest);

            // Llamar al endpoint integrado
            const response = await fetch("/api/mercado-pago/iniciar-compra", {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json',
                },
                body: JSON.stringify(compraRequest)
            });

            const data = await response.json();
            console.log("Respuesta del backend:", data);

            // Cerrar loading
            Swal.close();

            if (data.status === "success" && data.initPoint) {
                // Redirigir a Mercado Pago
                window.location.href = data.initPoint;
            } else {
                throw new Error(data.error || "Error al crear la compra");
            }

        } catch (error) {
            console.error("Error en MP:", error);
            Swal.fire({
                title: "Error",
                text: "No se pudo procesar el pago: " + error.message,
                icon: "error",
                confirmButtonText: "Entendido",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
        }
    }

    // Asignar evento al botón (si usas onclick en HTML, esta línea no es necesaria)
    const btnFinalizarCompra = document.getElementById("btnFinalizarCompra");
    if (btnFinalizarCompra) {
        btnFinalizarCompra.addEventListener("click", MP);
    }

    // --- Eventos de paginación ---
    prevPageBtn.addEventListener("click", () => {
        if (currentPage > 1) {
            currentPage--;
            renderTable();
        }
    });

    nextPageBtn.addEventListener("click", () => {
        if (currentPage < Math.ceil(cart.length / rowsPerPage)) {
            currentPage++;
            renderTable();
        }
    });

    // --- Delegación: eliminar + actualizar cantidad ---
    cartTableBody.addEventListener("click", (e) => {
        // Eliminar producto
        const btn = e.target.closest(".btn-delete");
        if (btn) {
            const index = parseInt(btn.getAttribute("data-index"));
            Swal.fire({
                title: "¿Estás seguro?",
                text: "¡No podrás revertir esto!",
                icon: "warning",
                showCancelButton: true,
                confirmButtonColor: "#3085d6",
                cancelButtonColor: "#d33",
                cancelButtonText: "Cancelar",
                confirmButtonText: "Sí, eliminar",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            }).then((result) => {
                if (result.isConfirmed) {
                    cart.splice(index, 1);
                    localStorage.setItem("cart", JSON.stringify(cart));

                    if ((currentPage - 1) * rowsPerPage >= cart.length && currentPage > 1) {
                        currentPage--;
                    }

                    renderTable();

                    Swal.fire({
                        title: "¡Eliminado!",
                        text: "El producto ha sido eliminado.",
                        icon: "success",
                        customClass: {
                            title: 'swal-title',
                            popup: 'swal-popup'
                        }
                    });

                }
            });
            return;
        }

        // Aumentar / disminuir cantidad
        if (e.target.classList.contains("plus") || e.target.classList.contains("minus")) {
            const control = e.target.closest(".quantity-control");
            const index = parseInt(control.getAttribute("data-index"));

            if (e.target.classList.contains("plus")) {
                if (cart[index].stock !== null && cart[index].qty >= cart[index].stock) {
                    Swal.fire({
                        title: "Stock insuficiente",
                        text: `No hay más stock disponible. Máximo: ${cart[index].stock} unidades`,
                        icon: "warning",
                        confirmButtonText: "Entendido",
                        customClass: {
                            title: 'swal-title',
                            popup: 'swal-popup'
                        }
                    });
                    return;
                }
                cart[index].qty++;
            } else if (e.target.classList.contains("minus") && cart[index].qty > 1) {
                cart[index].qty--;
            }

            localStorage.setItem("cart", JSON.stringify(cart));
            renderTable();
        }
    });

    // Manejar mensajes al cargar la página
    function handlePageLoadMessages() {
        // Mensaje de login exitoso
        if (sessionStorage.getItem("loginSuccess") === "true") {
            Swal.fire({
                title: "Registro de fecha y hora exito",
                icon: "success",
                timer: 3000,
                draggable: true,
                timerProgressBar: true,
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
            sessionStorage.removeItem("loginSuccess");
        }

        // Manejar respuesta de Mercado Pago
        handleMercadoPagoResponse();
    }

    // === INICIALIZACIÓN ===

    // Cargar cupón guardado al iniciar
    cargarCuponGuardado();

    // Configurar formulario de cupón
    setupCuponForm();

    // --- Render inicial ---
    renderTable();

    // Manejar mensajes de carga
    handlePageLoadMessages();

    //rederigir a Favorito
    rederigirFav();

});