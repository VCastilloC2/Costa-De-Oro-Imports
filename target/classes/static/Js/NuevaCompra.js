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

    // Abrir notificaciones del admin
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

    // Inputs de fecha
    if (document.getElementById("fechaemision")) {
        flatpickr("#fechaemision", {
            dateFormat: "Y-m-d",
            minDate: "today",
        });
    }

    // Validaciones del formulario
    const fieldsProducto = {
        numerofactura: { regex: /^#[A-Za-z0-9]+$/, errorMessage: "El numero de la factura debe iniciar con el símbolo # y contener solo letras o números después." },
        fechaemision: { regex: /^\d{4}-(0[1-9]|1[0-2])-(0[1-9]|[12]\d|3[01])$/, errorMessage: "Por favor eliga una fecha de emision" },
        cantidadproductos: { regex: /^[1-9][0-9]*$/, errorMessage: "ingresa una cantidad de productos" },
        preciocompra: { regex: /^(?!0+(?:[.,]0+)?$)\d+(?:[.,]\d{1,2})?$/, errorMessage: "ingresa un precio de compra " },
        subtotal: { regex: /^(?!0+(?:[.,]0+)?$)\d+(?:[.,]\d{1,2})?$/, errorMessage: "ingresa un subtotal de compra " }
    };

    // Formateo de moneda COP - CORREGIDO
    function formatCOP(value) {
        // Asegurarnos de que value sea string
        const stringValue = String(value || '');
        const number = Number(stringValue.replace(/\D/g, ''));
        if (isNaN(number)) return '';
        return number.toLocaleString('es-CO', {
            style: 'currency',
            currency: 'COP',
            maximumFractionDigits: 0
        });
    }

    // Función para limpiar formato de moneda (obtener solo números) - CORREGIDO
    function limpiarFormatoMoneda(valor) {
        return String(valor || '').replace(/\D/g, '');
    }

    function setupMoneyInput(inputOrId) {
        const input = typeof inputOrId === "string"
            ? document.getElementById(inputOrId)
            : inputOrId;

        if (!input) return;

        input.addEventListener('input', e => {
            const value = e.target.value.replace(/\D/g, '');
            e.target.value = value ? formatCOP(value) : '';

            // Calcular subtotal automáticamente si es un campo de precio
            if (input.name.includes("precioCompra")) {
                calcularSubtotalDesdePrecio(input);
            }
        });

        input.addEventListener('keydown', e => {
            const currentValue = Number(input.value.replace(/\D/g, '')) || 0;

            if (e.key === 'ArrowUp') {
                e.preventDefault();
                input.value = formatCOP(currentValue + 1000);
                if (input.name.includes("precioCompra")) {
                    calcularSubtotalDesdePrecio(input);
                }
            }

            if (e.key === 'ArrowDown') {
                e.preventDefault();
                input.value = formatCOP(Math.max(0, currentValue - 1000));
                if (input.name.includes("precioCompra")) {
                    calcularSubtotalDesdePrecio(input);
                }
            }
        });
    }

    // ========== MÉTODOS DE CÁLCULO MEJORADOS ==========

    // Calcular subtotal cuando cambia el precio
    function calcularSubtotalDesdePrecio(precioInput) {
        const fila = precioInput.closest('.fila-producto');
        calcularSubtotalFila(fila);
    }

    // Calcular subtotal cuando cambia la cantidad
    function calcularSubtotalDesdeCantidad(cantidadInput) {
        const fila = cantidadInput.closest('.fila-producto');
        calcularSubtotalFila(fila);
    }

    // Calcular subtotal para una fila específica - CORREGIDO
    function calcularSubtotalFila(fila) {
        const cantidadInput = fila.querySelector('input[name*="cantidad"]');
        const precioInput = fila.querySelector('input[name*="precioCompra"]');
        const subtotalInput = fila.querySelector('input[name*="subtotal"]');

        // Usar valores numéricos directamente
        const cantidad = parseInt(cantidadInput.value) || 0;
        const precio = parseInt(limpiarFormatoMoneda(precioInput.value)) || 0;
        const subtotal = cantidad * precio;

        if (subtotalInput) {
            subtotalInput.value = formatCOP(subtotal);
            // Validar visualmente el subtotal
            validarCampoVisual(subtotalInput, 'subtotal');
        }

        // Actualizar total general
        calcularTotalGeneral();
    }

    // Calcular total general de todas las filas - CORREGIDO
    function calcularTotalGeneral() {
        let total = 0;
        const filas = document.querySelectorAll('.fila-producto');

        filas.forEach(fila => {
            const subtotalInput = fila.querySelector('input[name*="subtotal"]');
            const subtotalValor = parseInt(limpiarFormatoMoneda(subtotalInput.value)) || 0;
            total += subtotalValor;
        });

        const totalInput = document.getElementById('totalcompra');
        if (totalInput) {
            totalInput.value = formatCOP(total);
        }
    }

    // Función auxiliar para validación visual de campos
    function validarCampoVisual(input, campoId) {
        const reglas = fieldsProducto[campoId];
        if (!reglas) return;

        const value = limpiarFormatoMoneda(input.value);
        const box = input.closest(".formulario__box");
        const checkIcon = box?.querySelector(".ri-check-line");
        const closeIcon = box?.querySelector(".ri-close-line");
        const errorMessage = box?.nextElementSibling;

        if (value === "") {
            input.style.border = "";
            if (checkIcon) checkIcon.style.display = "none";
            if (closeIcon) closeIcon.style.display = "none";
            if (errorMessage) errorMessage.style.display = "none";
            return;
        }

        if (reglas.regex.test(value)) {
            input.style.border = "2px solid #0034de";
            if (checkIcon) checkIcon.style.display = "inline-block";
            if (closeIcon) closeIcon.style.display = "none";
            if (errorMessage) errorMessage.style.display = "none";
        } else {
            input.style.border = "2px solid #fd1f1f";
            if (checkIcon) checkIcon.style.display = "none";
            if (closeIcon) closeIcon.style.display = "inline-block";
            if (errorMessage) errorMessage.style.display = "block";
        }
    }

    // ========== NUEVA FUNCIÓN PARA LIMPIAR VALORES ANTES DE ENVIAR ==========
    function limpiarValoresMonedaAntesDeEnviar() {
        // Limpiar precioCompra
        const preciosCompra = document.querySelectorAll('input[name*="precioCompra"]');
        preciosCompra.forEach(input => {
            const valorLimpio = limpiarFormatoMoneda(input.value);
            input.value = valorLimpio;
        });

        // Limpiar subtotal
        const subtotales = document.querySelectorAll('input[name*="subtotal"]');
        subtotales.forEach(input => {
            const valorLimpio = limpiarFormatoMoneda(input.value);
            input.value = valorLimpio;
        });

        // Limpiar totalCompra
        const totalCompra = document.getElementById('totalcompra');
        if (totalCompra) {
            const valorLimpio = limpiarFormatoMoneda(totalCompra.value);
            totalCompra.value = valorLimpio;
        }
    }
    // ========== FIN NUEVA FUNCIÓN ==========

    // ========== FIN MÉTODOS DE CÁLCULO ==========

    // Aplicar formatos de dinero
    setupMoneyInput('preciocompra');
    setupMoneyInput('subtotal');
    setupMoneyInput('totalcompra');

    // Cargar productos del proveedor
    function cargarProductosProveedor() {
        const formulario = document.getElementById("formularioProducto");
        const proveedorId = formulario.getAttribute("data-proveedor-id");
        const selectsProductos = document.querySelectorAll('select[name*="productoId"]');

        if (!proveedorId) return;

        fetch(`/admin/provedores/productos/${proveedorId}`)
            .then(response => response.json())
            .then(productos => {
                selectsProductos.forEach(select => {
                    // Limpiar opciones excepto la primera
                    while (select.options.length > 1) {
                        select.remove(1);
                    }

                    // Agregar productos
                    productos.forEach(producto => {
                        const option = document.createElement('option');
                        option.value = producto.id;
                        option.textContent = producto.nombre;
                        select.appendChild(option);
                    });
                });
            })
            .catch(error => {
                console.error('Error cargando productos:', error);
            });
    }

    // Inicializar validaciones
    function inicializarValidaciones() {
        Object.keys(fieldsProducto).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            if (!input) return;

            const inputBox = input.closest(".formulario__box");
            const checkIcon = inputBox?.querySelector(".ri-check-line");
            const errorIcon = inputBox?.querySelector(".ri-close-line");
            const errorMessage = inputBox?.nextElementSibling;

            input.addEventListener("input", () => {
                let value = input.value.trim();

                if (fieldId === "preciocompra" || fieldId === "subtotal") {
                    value = limpiarFormatoMoneda(value);
                }

                const label = inputBox?.querySelector(".box__label");

                if (value === "") {
                    inputBox?.classList.remove("input-error");
                    if (checkIcon) checkIcon.style.display = "none";
                    if (errorIcon) errorIcon.style.display = "none";
                    if (errorMessage) errorMessage.style.display = "none";
                    input.style.border = "";
                    if (label) label.classList.remove("error");
                } else if (fieldsProducto[fieldId].regex.test(value)) {
                    if (checkIcon) checkIcon.style.display = "inline-block";
                    if (errorIcon) errorIcon.style.display = "none";
                    if (errorMessage) errorMessage.style.display = "none";
                    input.style.border = "2px solid #0034de";
                    inputBox?.classList.remove("input-error");
                    if (label) label.classList.remove("error");
                } else {
                    if (checkIcon) checkIcon.style.display = "none";
                    if (errorIcon) errorIcon.style.display = "inline-block";
                    if (errorMessage) errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    inputBox?.classList.add("input-error");
                    if (label) label.classList.add("error");
                }
            });
        });

        // Validación de selects
        const selectsProductos = document.querySelectorAll('select[name*="productoId"]');
        selectsProductos.forEach(select => {
            const errorProductos = select.closest('.input__formulario').querySelector('.formulario__error');

            select.addEventListener("change", () => {
                if (select.selectedIndex > 0) {
                    select.style.border = "2px solid #0034de";
                    if (errorProductos) errorProductos.style.display = "none";
                } else {
                    select.style.border = "";
                }
            });
        });
    }

    // Agregar nuevas filas de productos - ACTUALIZADO
    const contenedorFilas = document.getElementById("contenedor-filas");
    const btnAgregarFila = document.querySelector(".btn-agregar-fila");

    if (btnAgregarFila && contenedorFilas) {
        btnAgregarFila.addEventListener("click", () => {
            const filaBase = document.querySelector(".fila-producto");
            const nuevaFila = filaBase.cloneNode(true);

            // Obtener el índice de la nueva fila
            const totalFilas = document.querySelectorAll('.fila-producto').length;

            // Actualizar los nombres de los campos con el nuevo índice
            nuevaFila.querySelectorAll('select[name*="productoId"]').forEach(select => {
                select.name = `detalles[${totalFilas}].productoId`;
            });

            nuevaFila.querySelectorAll('input[name*="cantidad"]').forEach(input => {
                input.name = `detalles[${totalFilas}].cantidad`;
            });

            nuevaFila.querySelectorAll('input[name*="precioCompra"]').forEach(input => {
                input.name = `detalles[${totalFilas}].precioCompra`;
            });

            nuevaFila.querySelectorAll('input[name*="subtotal"]').forEach(input => {
                input.name = `detalles[${totalFilas}].subtotal`;
            });

            // Limpiar inputs
            nuevaFila.querySelectorAll("input").forEach(input => {
                input.value = "";
                input.style.border = "";
            });

            // Limpiar selects
            nuevaFila.querySelectorAll("select").forEach(select => {
                select.selectedIndex = 0;
                select.style.border = "";
            });

            // Ocultar errores e iconos
            nuevaFila.querySelectorAll(".formulario__error").forEach(e => e.style.display = "none");
            nuevaFila.querySelectorAll(".ri-check-line").forEach(i => i.style.display = "none");
            nuevaFila.querySelectorAll(".ri-close-line").forEach(i => i.style.display = "none");

            // Insertar fila
            contenedorFilas.appendChild(nuevaFila);

            // Reactivar funcionalidades
            nuevaFila.querySelectorAll(".validar-numerico").forEach(input => {
                setupMoneyInput(input);
            });

            // Configurar eventos de cálculo para la nueva fila
            configurarEventosCalculoFila(nuevaFila);

            activarValidacionesFila(nuevaFila);

            // Recargar productos en el nuevo select
            cargarProductosProveedor();
        });
    }

    // Configurar eventos de cálculo para una fila - CORREGIDO
    function configurarEventosCalculoFila(fila) {
        const cantidadInput = fila.querySelector('input[name*="cantidad"]');
        const precioInput = fila.querySelector('input[name*="precioCompra"]');

        if (cantidadInput) {
            cantidadInput.addEventListener('input', () => {
                calcularSubtotalDesdeCantidad(cantidadInput);
            });
        }

        if (precioInput) {
            // Ya está configurado en setupMoneyInput, pero reforzamos
            precioInput.addEventListener('input', () => {
                calcularSubtotalDesdePrecio(precioInput);
            });
        }
    }

    function activarValidacionesFila(fila) {
        const inputs = fila.querySelectorAll(".validar-input");
        const selects = fila.querySelectorAll(".validar-select");

        inputs.forEach(input => {
            const idCampo = input.id ? input.id.replace(/[0-9]/g, "") : input.name;
            input.addEventListener("input", () => validarCampoDinamico(input, idCampo));
        });

        selects.forEach(select => {
            select.addEventListener("change", () => {
                const errorElement = select.closest('.input__formulario').querySelector('.formulario__error');
                if (select.selectedIndex > 0) {
                    select.style.border = "2px solid #0034de";
                    if (errorElement) errorElement.style.display = "none";
                } else {
                    select.style.border = "2px solid #fd1f1f";
                    if (errorElement) errorElement.style.display = "block";
                }
            });
        });
    }

    function validarCampoDinamico(input, idCampo) {
        const reglas = fieldsProducto[idCampo];
        if (!reglas) return;

        let value = input.value.trim();

        if (idCampo === "preciocompra" || idCampo === "subtotal" || input.classList.contains('validar-numerico')) {
            value = limpiarFormatoMoneda(value);
        }

        const box = input.closest(".formulario__box");
        const checkIcon = box?.querySelector(".ri-check-line");
        const closeIcon = box?.querySelector(".ri-close-line");
        const errorMessage = box?.nextElementSibling;

        if (value === "") {
            input.style.border = "";
            if (checkIcon) checkIcon.style.display = "none";
            if (closeIcon) closeIcon.style.display = "none";
            if (errorMessage) errorMessage.style.display = "none";
            return;
        }

        if (reglas.regex.test(value)) {
            input.style.border = "2px solid #0034de";
            if (checkIcon) checkIcon.style.display = "inline-block";
            if (closeIcon) closeIcon.style.display = "none";
            if (errorMessage) errorMessage.style.display = "none";
        } else {
            input.style.border = "2px solid #fd1f1f";
            if (checkIcon) checkIcon.style.display = "none";
            if (closeIcon) closeIcon.style.display = "inline-block";
            if (errorMessage) errorMessage.style.display = "block";
        }
    }

    // Validación del formulario al enviar - ACTUALIZADA
    const addform = document.getElementById("formularioProducto");
    if (addform) {
        addform.addEventListener("submit", function (e) {
            let formularioValido = true;
            let selectsValidos = true;

            // Validar campos principales
            Object.keys(fieldsProducto).forEach(fieldId => {
                const input = document.getElementById(fieldId);
                if (!input) return;

                const regex = fieldsProducto[fieldId].regex;
                const inputBox = input.closest(".formulario__box");
                const checkIcon = inputBox?.querySelector(".ri-check-line");
                const errorIcon = inputBox?.querySelector(".ri-close-line");
                const errorMessage = inputBox?.nextElementSibling;

                let value = input.value.trim();

                if (fieldId === "preciocompra" || fieldId === "subtotal") {
                    value = limpiarFormatoMoneda(value);
                }

                if (!regex.test(value)) {
                    formularioValido = false;
                    if (checkIcon) checkIcon.style.display = "none";
                    if (errorIcon) errorIcon.style.display = "inline-block";
                    if (errorMessage) errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    const label = inputBox?.querySelector(".box__label");
                    if (label) label.classList.add("error");
                    if (inputBox) inputBox.classList.add("input-error");
                }
            });

            // Validar selects de productos
            const selectsProductos = document.querySelectorAll('select[name*="productoId"]');
            selectsProductos.forEach(select => {
                const errorProductos = select.closest('.input__formulario').querySelector('.formulario__error');

                if (select.selectedIndex <= 0) {
                    selectsValidos = false;
                    if (errorProductos) errorProductos.style.display = "block";
                    select.style.border = "2px solid #fd1f1f";
                }
            });

            if (!formularioValido || !selectsValidos) {
                Swal.fire({
                    icon: "error",
                    title: "Oops...",
                    text: "Por favor rellene el formulario correctamente",
                    customClass: {
                        title: 'swal-title',
                        popup: 'swal-popup'
                    }
                });
                e.preventDefault();
                return;
            }

            // ========== LIMPIAR VALORES ANTES DE ENVIAR ==========
            limpiarValoresMonedaAntesDeEnviar();
            // ========== FIN LIMPIAR VALORES ==========

            sessionStorage.setItem("loginSuccess", "true");
        });
    }

    // Inicializar
    cargarProductosProveedor();
    inicializarValidaciones();

    // Activar validaciones en la fila base
    const filaBase = document.querySelector(".fila-producto");
    if (filaBase) {
        activarValidacionesFila(filaBase);
        configurarEventosCalculoFila(filaBase);
    }
});