import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, verProductos, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    finalizarCompra();

    verProductos();

    toggleMenu();

    //cambiar foto de perfil 
    const fileInput = document.getElementById('fileInput');
    const preview = document.getElementById('preview');
    const btnSubir = document.getElementById('btnSubir');
    const btnRestablecer = document.getElementById('btnRestablecer');
    const form = document.getElementById('formFoto');
    const imagenOriginal = preview.src;

    // Cuando haces clic en "Subir"
    btnSubir.addEventListener('click', () => {
        // Si el botón está en modo guardar, envía el formulario
        if (btnSubir.classList.contains('button--guardar')) {
            form.submit();
        } else {
            fileInput.click();
        }
    });

    // Mostrar vista previa y cambiar botón a "Guardar"
    fileInput.addEventListener('change', (e) => {
        const file = e.target.files[0];
        if (file) {
            const reader = new FileReader();
            reader.onload = (event) => {
                preview.src = event.target.result;
            };
            reader.readAsDataURL(file);

            // Cambiar botón de "Subir" → "Guardar"
            btnSubir.textContent = "Guardar";
            btnSubir.classList.remove("button--subir");
            btnSubir.classList.add("button--guardar");
        }
    });

    // Restablecer imagen y volver a estado original
    btnRestablecer.addEventListener('click', () => {
        preview.src = imagenOriginal;
        fileInput.value = "";

        // Volver el botón a su estado original
        btnSubir.textContent = "Subir";
        btnSubir.classList.remove("button--guardar");
        btnSubir.classList.add("button--subir");
    });

    //validaciones datos del usuario
    const fields = {
        nombre: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El nombre debe tener al menos 3 letras." },
        apellido: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El apellido debe tener al menos 3 letras." },
        identificacion: { regex: /^\d{6,10}$/, errorMessage: "La cédula debe contener entre 6 y 10 dígitos." },
        telefono: { regex: /^\d{1,10}$/, errorMessage: "El teléfono solo puede contener números (máx. 10)." },
        email: { regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/, errorMessage: "El correo solo puede contener letras,numeros,puntos,guiones y guion bajo." },
    };

    const selectTipoIdentificacion = document.getElementById("tipoIdentificacion");
    const errorTipoIdentificacion = document.querySelector(".error--tipoIdentificacion");

    Object.keys(fields).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        if (!input) return;

        const inputBox = input.closest(".input-box");
        if (!inputBox) return;

        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.nextElementSibling;

        input.addEventListener("input", () => {
            const value = input.value.trim();
            const label = inputBox.querySelector("label");

            if (value === "") {
                inputBox.classList.remove("input-error");
                checkIcon && (checkIcon.style.display = "none");
                errorIcon && (errorIcon.style.display = "none");
                errorMessage && (errorMessage.style.display = "none");
                input.style.border = "";
                label && label.classList.remove("error");
            }
            else if (fields[fieldId].regex.test(value)) {
                checkIcon && (checkIcon.style.display = "inline-block");
                errorIcon && (errorIcon.style.display = "none");
                errorMessage && (errorMessage.style.display = "none");
                input.style.border = "2px solid #0034de";
                inputBox.classList.remove("input-error");
                label && label.classList.remove("error");
            }
            else {
                checkIcon && (checkIcon.style.display = "none");
                errorIcon && (errorIcon.style.display = "inline-block");
                errorMessage && (errorMessage.style.display = "block");
                input.style.border = "2px solid #fd1f1f";
                inputBox.classList.add("input-error");
                label && label.classList.add("error");
            }
        });
    });

    // Ocultar advertencias y errores de select al interactuar
    [selectTipoIdentificacion].forEach(select => {
        select.addEventListener("change", () => {

            if (select.selectedIndex > 0) {
                select.style.border = "2px solid #0034de";
            } else {
                select.style.border = "";
            }

            if (select === selectPregunta1 && select.selectedIndex > 0) {
                errorTipoIdentificacion.style.display = "none";
            }

        });
    });

    const addform = document.querySelector(".personales__formulario");

    addform.addEventListener("submit", function (e) {
        let formularioValido = true;
        let selectsValidos = true;


        // 2. Validar inputs (solo si el checkbox está marcado)
        Object.keys(fields).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            const regex = fields[fieldId].regex;


            const inputBox = input.closest(".input-box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;

            if (!regex.test(input.value.trim())) {
                formularioValido = false;
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                const label = inputBox.querySelector("label");
                if (label) label.classList.add("error"); // marcar error
                inputBox.classList.add("input-error");
            } else {
                const label = inputBox.querySelector("label");
                if (label) label.classList.remove("error"); // quitar error si es válido
            }
        });

        const tipoIdentificacionSeleccionada = selectTipoIdentificacion.selectedIndex > 0;

        if (!tipoIdentificacionSeleccionada) {
            selectsValidos = false;
            errorTipoIdentificacion.style.display = "block";
            selectTipoIdentificacion.style.border = "2px solid #fd1f1f";
        }


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

    // Mensajes del Back-end
    window.addEventListener("DOMContentLoaded", () => {

        const body = document.body;
        const mensaje = body.dataset.mensaje;

        if (mensaje) {
            Swal.fire({
                title: "Proceso exitoso",
                text: mensaje,
                icon: "success",
                timer: 3000,
                showConfirmButton: false,
                timerProgressBar: true,
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
        }
    });

    const formEmpresa = document.querySelector(".empresa__formulario");
    if (formEmpresa) {
        //cambiar foto de empresa
        const fileInputEmpresa = document.getElementById('fileInputEmpresa');
        const previewEmpresa = document.getElementById('previewEmpresa');
        const btnSubirEmpresa = document.getElementById('btnSubirEmpresa');
        const btnRestablecerEmpresa = document.getElementById('btnRestablecerEmpresa');
        const formFotoEmpresa = document.getElementById('formFotoEmpresa');
        const imagenOriginalEmpresa = previewEmpresa.src;

        // Al hacer clic en "Subir" o "Guardar"
        btnSubirEmpresa.addEventListener('click', () => {
            if (btnSubirEmpresa.classList.contains('button--guardar')) {
                formFotoEmpresa.submit();
            } else {
                fileInputEmpresa.click();
            }
        });

        // Vista previa y cambio de botón a "Guardar"
        fileInputEmpresa.addEventListener('change', (e) => {
            const file = e.target.files[0];
            if (file) {
                const reader = new FileReader();
                reader.onload = (event) => {
                    previewEmpresa.src = event.target.result;
                };
                reader.readAsDataURL(file);

                btnSubirEmpresa.textContent = "Guardar";
                btnSubirEmpresa.classList.remove("button--subir");
                btnSubirEmpresa.classList.add("button--guardar");
            }
        });

        // Restablecer imagen original
        btnRestablecerEmpresa.addEventListener('click', () => {
            previewEmpresa.src = imagenOriginalEmpresa;
            fileInputEmpresa.value = "";

            btnSubirEmpresa.textContent = "Subir";
            btnSubirEmpresa.classList.remove("button--guardar");
            btnSubirEmpresa.classList.add("button--subir");
        });

        //validaciones de datos de la empresa
        const fieldsEmpresa = {
            nit: {
                regex: /^\d+$/,
                errorMessage: "El NIT no puede estar vacío y debe contener solo números."
            },
            razonSocial: {
                regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,50}$/,
                errorMessage: "La razón social solo puede contener letras, espacios y debe tener entre 3 y 50 caracteres."
            },
            direccion: {
                regex: /^(?=.*\d)(?=.*[A-Za-z])[A-Za-z0-9\s#.,-]{5,}$/,
                errorMessage: "Ingresa una dirección válida (ej. Calle 45 #10-23, 130002 o San Fernando, Calle 45 #10-23, 130002)."
            },
            correo: {
                regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
                errorMessage: "El correo solo puede contener letras, números, puntos, guiones y guion bajo."
            },
            phone: {
                regex: /^\d{1,10}$/,
                errorMessage: "El teléfono solo puede contener números y el máximo son 10 dígitos."
            }
        };

        const selectTiposector = document.getElementById("tipoSector");
        const errorTiposector = document.querySelector(".error--Tiposector");
        const selectCiudad = document.getElementById("Ciudad");
        const errorCiudad = document.querySelector(".error--ciudad");

        Object.keys(fieldsEmpresa).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            const inputBox = input.closest(".input-box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;

            input.addEventListener("input", () => {
                const value = input.value.trim();
                const label = inputBox.querySelector("label");

                if (value === "") {
                    inputBox.classList.remove("input-error");
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "";
                    if (label) label.classList.remove("error"); //lo reseteas
                } else if (fieldsEmpresa[fieldId].regex.test(value)) {
                    checkIcon.style.display = "inline-block";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "2px solid #0034de";
                    inputBox.classList.remove("input-error");
                    if (label) label.classList.remove("error"); //quitar rojo cuando es válido
                } else {
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "inline-block";
                    errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    inputBox.classList.add("input-error");
                    if (label) label.classList.add("error"); // marcar rojo cuando es inváli
                }
            });
        });

        // Ocultar advertencias y errores de select al interactuar
        [selectTiposector, selectCiudad].forEach(select => {
            select.addEventListener("change", () => {

                if (select.selectedIndex > 0) {
                    select.style.border = "2px solid #0034de";
                } else {
                    select.style.border = "";
                }

                if (select === selectTiposector && select.selectedIndex > 0) {
                    errorTiposector.style.display = "none";
                }

                if (select === selectCiudad && select.selectedIndex > 0) {
                    errorCiudad.style.display = "none";
                }

            });
        });

        formEmpresa.addEventListener("submit", function (e) {
            let formularioValido = true;
            let selectsValidos = true;


            // 2. Validar inputs (solo si el checkbox está marcado)
            Object.keys(fieldsEmpresa).forEach(fieldId => {
                const input = document.getElementById(fieldId);
                const regex = fieldsEmpresa[fieldId].regex;


                const inputBox = input.closest(".input-box");
                const checkIcon = inputBox.querySelector(".ri-check-line");
                const errorIcon = inputBox.querySelector(".ri-close-line");
                const errorMessage = inputBox.nextElementSibling;

                if (!regex.test(input.value.trim())) {
                    formularioValido = false;
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "inline-block";
                    errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    const label = inputBox.querySelector("label");
                    if (label) label.classList.add("error"); // marcar error
                    inputBox.classList.add("input-error");
                } else {
                    const label = inputBox.querySelector("label");
                    if (label) label.classList.remove("error"); // quitar error si es válido
                }
            });

            const TiposectorSeleccionada = selectTiposector.selectedIndex > 0;
            const ciudadSeleccionada = selectCiudad.selectedIndex > 0;

            if (!TiposectorSeleccionada) {
                selectsValidos = false;
                errorTiposector.style.display = "block";
                selectTiposector.style.border = "2px solid #fd1f1f";
            }

            if (!ciudadSeleccionada) {
                selectsValidos = false;
                errorCiudad.style.display = "block";
                selectCiudad.style.border = "2px solid #fd1f1f";
            }


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

        // Lista de ciudades principales de Colombia (ejemplo, puedes extenderla)
        const ciudadesColombia = [
            "Bogotá",
            "Medellín",
            "Cali",
            "Barranquilla",
            "Cartagena",
            "Cúcuta",
            "Bucaramanga",
            "Pereira",
            "Santa Marta",
            "Ibagué",
            "Manizales",
            "Neiva",
            "Villavicencio",
            "Armenia",
            "Soacha",
            "Valledupar",
            "Palmira",
            "Montería",
            "Popayán",
            "Pasto"
            // Aquí puedes agregar todas las demás ciudades o municipios
        ];

        // Función para llenar el select
        function poblarSelectCiudades() {
            ciudadesColombia.forEach(ciudad => {
                const option = document.createElement("option");
                option.value = ciudad;
                option.textContent = ciudad;
                selectCiudad.appendChild(option);
            });
        }

        // Ejecutar al cargar
        poblarSelectCiudades();
    }

});