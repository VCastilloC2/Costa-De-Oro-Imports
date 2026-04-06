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

    /* Cargar la imagen del usuario con js */
    const imageForms = document.querySelectorAll('.formulario__imagen');

    imageForms.forEach((form) => {
        // Seleccionar elementos dentro de cada formulario
        const fileInput = form.querySelector('.fileInput');
        const previewContainer = form.querySelector('.preview-container');
        const uploadButton = form.querySelector('.upload-btn');
        const resetButton = form.querySelector('.reset-btn');
        const errorFormato = form.querySelector('.error--formato');
        const errorVacio = form.querySelector('.error--vacio');
        const boxImagen = form.querySelector('.formulario__boximagen');

        const formatosPermitidos = ["image/jpeg", "image/png", "image/webp"];
        const tamañoMaximo = 5 * 1024 * 1024; // 5 MB
        let selectedFile = null;

        // Verificar si ya hay una imagen precargada y mostrarla correctamente
        const existingImage = previewContainer.querySelector('img');
        if (existingImage && existingImage.src) {
            // Asegurarse de que la imagen se muestre correctamente
            existingImage.style.display = 'block';
            existingImage.style.maxWidth = '100%';
            existingImage.style.maxHeight = '200px';
            existingImage.style.borderRadius = '8px';
            existingImage.style.objectFit = 'cover';
            previewContainer.style.display = 'block';
            boxImagen.style.border = "1px solid #0034de";

            // Asegurar que el contenedor sea visible
            previewContainer.style.visibility = 'visible';
            previewContainer.style.opacity = '1';
        }

        // Abrir explorador al hacer clic en "Subir"
        uploadButton.addEventListener("click", (e) => {
            e.preventDefault();
            fileInput.click();
        });

        // Evento cuando se selecciona archivo
        fileInput.addEventListener("change", (e) => {
            const file = e.target.files[0];
            selectedFile = file || null;

            // Reiniciar mensajes visuales
            errorFormato.style.display = "none";
            errorVacio.style.display = "none";
            boxImagen.style.border = "1px solid #ddd";

            // Si no seleccionó archivo
            if (!file) {
                // Si ya había una imagen, mantenerla
                if (existingImage && existingImage.src) {
                    return; // Mantener la imagen existente
                } else {
                    errorVacio.style.display = "block";
                    boxImagen.style.border = "2px solid #e53935";
                }
                return;
            }

            // Validar formato
            if (!formatosPermitidos.includes(file.type)) {
                errorFormato.textContent = "Solo se permiten archivos JPG, PNG o WEBP.";
                errorFormato.style.display = "block";
                boxImagen.style.border = "2px solid #e53935";
                return;
            }

            // Validar tamaño
            if (file.size > tamañoMaximo) {
                errorFormato.textContent = "La imagen no debe superar 5 MB.";
                errorFormato.style.display = "block";
                boxImagen.style.border = "2px solid #e53935";
                return;
            }

            // Si pasa las validaciones → mostrar barra de carga
            previewContainer.innerHTML = `<div class="progress-bar"></div>`;
            previewContainer.style.display = "flex";
            boxImagen.style.border = "1px solid #0034de";

            simulateProgress(file, previewContainer);
        });

        // Función de simulación de progreso
        function simulateProgress(file, container) {
            let width = 0;
            const progressBar = container.querySelector('.progress-bar');
            const interval = setInterval(() => {
                if (width >= 100) {
                    clearInterval(interval);
                    setTimeout(() => {
                        const reader = new FileReader();
                        reader.onload = function (event) {
                            container.innerHTML = `<img src="${event.target.result}" alt="Imagen subida">`;
                            container.style.display = 'block';
                        };
                        reader.readAsDataURL(file);
                    }, 300);
                } else {
                    width += 2;
                    if (progressBar) {
                        progressBar.style.width = width + "%";
                    }
                }
            }, 30);
        }

        // Función de validación para este formulario específico
        function validarImagenes() {
            const file = fileInput.files[0];
            const existingImage = previewContainer.querySelector('img');

            errorFormato.style.display = "none";
            errorVacio.style.display = "none";
            boxImagen.style.border = "1px solid #ddd";

            // Si no hay archivo nuevo pero hay imagen existente, es válido
            if (!file && existingImage && existingImage.src) {
                return true;
            }

            if (!file) {
                errorVacio.style.display = "block";
                boxImagen.style.border = "2px solid #e53935";
                return false;
            }

            if (!formatosPermitidos.includes(file.type)) {
                errorFormato.textContent = "Solo se permiten archivos JPG, PNG o WEBP.";
                errorFormato.style.display = "block";
                boxImagen.style.border = "2px solid #e53935";
                return false;
            }

            if (file.size > tamañoMaximo) {
                errorFormato.textContent = "La imagen no debe superar 5 MB.";
                errorFormato.style.display = "block";
                boxImagen.style.border = "2px solid #e53935";
                return false;
            }

            boxImagen.style.border = "1px solid #0034de";
            return true;
        }

        // Botón "Limpiar"
        resetButton.addEventListener("click", (e) => {
            e.preventDefault();
            fileInput.value = "";
            // No eliminar la imagen existente, solo limpiar archivos nuevos
            if (!existingImage || !existingImage.src) {
                previewContainer.innerHTML = "";
                previewContainer.style.display = "none";
            }
            selectedFile = null;

            // Quitar errores visuales
            errorFormato.style.display = "none";
            errorVacio.style.display = "none";
            boxImagen.style.border = "1px solid #ddd";
        });

        // Guardar referencia a la función de validación para usarla en el submit
        form._validarImagenes = validarImagenes;
    });

    //Validaciones del formulario de agregar clientes
    const fieldsClientes = {
        nombre: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El nombre debe tener al menos 3 letras." },
        apellido: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El apellido debe tener al menos 3 letras." },
        telefono: { regex: /^\d{1,10}$/, errorMessage: "El teléfono solo puede contener numeros y el maximo son 10 digitos." },
        correo: { regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/, errorMessage: " El correo solo puede contener letras,numeros,puntos,guiones y guion bajo." },
        identificacion: { regex: /^\d{6,10}$/, errorMessage: "La cédula debe contener entre 6 y 10 dígitos numéricos." },
        direccion: { regex: /^(?=.*\d)(?=.*[A-Za-z])[A-Za-z0-9\s#.,-]{5,}$/, errorMessage: "Ingresa una dirección válida (ej. Calle 45 #10-23, 130002 o San Fernando, Calle 45 #10-23, 130002)." },
        password: { regex: /^.{4,15}$/, errorMessage: "La contraseña tiene que ser de 4 a 15 digitos" },
        nit: { regex: /^\d{1,12}$/, errorMessage: "El NIT no puede estar vacío y debe contener solo números." },
        razonsocial: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,50}$/, errorMessage: "La razón social solo puede contener letras, espacios y debe tener entre 3 y 50 caracteres." },
        direction: { regex: /^(?=.*\d)(?=.*[A-Za-z])[A-Za-z0-9\s#.,-]{5,}$/, errorMessage: "Ingresa una dirección válida (ej. Calle 45 #10-23, 130002 o San Fernando, Calle 45 #10-23, 130002)." },
        phone: { regex: /^\d{1,10}$/, errorMessage: "El teléfono solo puede contener numeros y el maximo son 10 digitos." },
        email: { regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/, errorMessage: " El correo solo puede contener letras,numeros,puntos,guiones y guion bajo." }
    };

    // obtenemos los select
    const selectTipoIdentificacion = document.getElementById("tipoIdentificacion");
    const errorTipoIdentificacion = document.querySelector(".error--TipoIdentificacion");
    const selectCiudad = document.getElementById("Ciudad");
    const errorCiudad = document.querySelector(".error--Ciudad");

    // Validar campos al cargar la página
    Object.keys(fieldsClientes).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        if (input) {
            const inputBox = input.closest(".formulario__box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;
            const value = input.value.trim();

            // Validar campo al cargar
            if (value !== "") {
                if (fieldsClientes[fieldId].regex.test(value)) {
                    checkIcon.style.display = "inline-block";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "2px solid #0034de";
                    inputBox.classList.remove("input-error");
                } else {
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "inline-block";
                    errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    inputBox.classList.add("input-error");
                }
            }

            // Event listener para cambios en tiempo real
            input.addEventListener("input", () => {
                const value = input.value.trim();
                const label = inputBox.querySelector(".box__label");

                if (value === "") {
                    inputBox.classList.remove("input-error");
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "";
                    if (label) label.classList.remove("error");
                } else if (fieldsClientes[fieldId].regex.test(value)) {
                    checkIcon.style.display = "inline-block";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "2px solid #0034de";
                    inputBox.classList.remove("input-error");
                    if (label) label.classList.remove("error");
                } else {
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "inline-block";
                    errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    inputBox.classList.add("input-error");
                    if (label) label.classList.add("error");
                }
            });
        }
    });

    // Validar selects al cargar la página
    if (selectTipoIdentificacion && selectTipoIdentificacion.selectedIndex > 0) {
        selectTipoIdentificacion.style.border = "2px solid #0034de";
        errorTipoIdentificacion.style.display = "none";
    }

    // Ocultar advertencias y errores de select al interactuar
    [selectTipoIdentificacion, selectCiudad].forEach(select => {
        if (select) {
            select.addEventListener("change", () => {
                if (select.selectedIndex > 0) {
                    select.style.border = "2px solid #0034de";
                } else {
                    select.style.border = "";
                }

                if (select === selectTipoIdentificacion && select.selectedIndex > 0) {
                    errorTipoIdentificacion.style.display = "none";
                }

                if (select === selectCiudad && select.selectedIndex > 0) {
                    errorCiudad.style.display = "none";
                }
            });
        }
    });

    const addform = document.getElementById("formularioClientes");

    addform.addEventListener("submit", function (e) {
        let formularioValido = true;
        let selectsValidos = true;

        // 1. Validar inputs
        Object.keys(fieldsClientes).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            if (input) {
                const regex = fieldsClientes[fieldId].regex;
                const inputBox = input.closest(".formulario__box");
                const checkIcon = inputBox.querySelector(".ri-check-line");
                const errorIcon = inputBox.querySelector(".ri-close-line");
                const errorMessage = inputBox.nextElementSibling;
                const value = input.value.trim();

                // Para el campo de contraseña, solo validar si no está vacío
                if (fieldId === 'password' && value === '') {
                    // Contraseña vacía es válida (mantener la actual)
                    checkIcon.style.display = "inline-block";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "2px solid #0034de";
                    inputBox.classList.remove("input-error");
                    return;
                }

                if (!regex.test(value)) {
                    formularioValido = false;
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "inline-block";
                    errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    const label = inputBox.querySelector(".box__label");
                    if (label) label.classList.add("error");
                    inputBox.classList.add("input-error");
                } else {
                    const label = inputBox.querySelector(".box__label");
                    if (label) label.classList.remove("error");
                }
            }
        });

        const TipoIdentificacionSeleccionada = selectTipoIdentificacion && selectTipoIdentificacion.selectedIndex > 0;
        const CiudadSeleccionada = selectCiudad && selectCiudad.selectedIndex > 0;

        if (!TipoIdentificacionSeleccionada) {
            selectsValidos = false;
            errorTipoIdentificacion.style.display = "block";
            if (selectTipoIdentificacion) selectTipoIdentificacion.style.border = "2px solid #fd1f1f";
        }

        if (!CiudadSeleccionada) {
            selectsValidos = false;
            errorCiudad.style.display = "block";
            if (selectCiudad) selectCiudad.style.border = "2px solid #fd1f1f";
        }

        // Validar TODOS los formularios de imagen
        const imageForms = document.querySelectorAll('.formulario__imagen');
        imageForms.forEach((form) => {
            if (!form._validarImagenes()) {
                formularioValido = false;
            }
        });

        // Mostrar error si algo está mal
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

        // Caso éxito
        sessionStorage.setItem("loginSuccess", "true");
    });

    // Lista de ciudades principales de Colombia
    const ciudadesColombia = [
        "Bogotá", "Medellín", "Cali", "Barranquilla", "Cartagena", "Cúcuta", "Bucaramanga", "Pereira",
        "Santa Marta", "Ibagué", "Manizales", "Neiva", "Villavicencio", "Armenia", "Soacha", "Valledupar",
        "Palmira", "Montería", "Popayán", "Pasto"
    ];

    // Función para llenar el select y seleccionar la ciudad actual
    function poblarSelectCiudades() {
        // Obtener la ciudad actual del proveedor desde Thymeleaf
        // Necesitamos pasar la ciudad actual desde el controller
        const ciudadActual = '[[${proveedorResponse.ciudad()}]]'; // Esto se renderizará con Thymeleaf

        ciudadesColombia.forEach(ciudad => {
            const option = document.createElement("option");
            option.value = ciudad;
            option.textContent = ciudad;

            // Seleccionar la ciudad actual si coincide
            if (ciudad === ciudadActual) {
                option.selected = true;
                selectCiudad.style.border = "2px solid #0034de";
                errorCiudad.style.display = "none";
            }

            selectCiudad.appendChild(option);
        });
    }

    // Ejecutar al cargar
    poblarSelectCiudades();

    // Forzar la visualización de imágenes existentes después de un pequeño delay
    setTimeout(() => {
        const existingImages = document.querySelectorAll('.preview-container img');
        existingImages.forEach(img => {
            if (img.src && img.src !== window.location.href) {
                img.style.display = 'block';
                img.style.maxWidth = '100%';
                img.style.maxHeight = '200px';
                img.style.borderRadius = '8px';
                img.style.objectFit = 'cover';
                const container = img.closest('.preview-container');
                if (container) {
                    container.style.display = 'block';
                    container.style.visibility = 'visible';
                    container.style.opacity = '1';
                }
            }
        });
    }, 100);
});