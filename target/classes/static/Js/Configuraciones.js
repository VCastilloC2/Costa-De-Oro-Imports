import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, verProductos, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    finalizarCompra();

    verProductos();

    toggleMenu();

    // Validaciones De configuracion
    const fieldConfig = {
        respuesta1: { regex: /^.{1,}$/, errorMessage: " La respuesta de la pregunta es obligatoria" },
        respuesta2: { regex: /^.{1,}$/, errorMessage: " La respuesta de la pregunta es obligatoria" }
    };

    // variables 
    const formulario = document.querySelector(".formulario");
    const selectPregunta1 = document.getElementById("pregunta1");
    const errorPregunta1 = document.querySelector(".error--pregunta1");
    const selectPregunta2 = document.getElementById("pregunta2");
    const errorPregunta2 = document.querySelector(".error--pregunta2");

    // Validar en tiempo real los inputs
    Object.keys(fieldConfig).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        if (!input) return;

        const inputBox = input.closest(".input-box");
        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.nextElementSibling;
        const label = inputBox.querySelector("label");

        input.addEventListener("input", () => {

            const value = input.value.trim();
            if (value === "") {
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "";
                label.style.color = "";
                inputBox.classList.remove("input-error");
            } else if (fieldConfig[fieldId].regex.test(value)) {
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "2px solid #0034de";
                label.style.color = "";
                inputBox.classList.remove("input-error");
            } else {
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                label.style.color = "red";
                inputBox.classList.add("input-error");
            }
        });
    });

    // Ocultar advertencias y errores de select al interactuar
    [selectPregunta1, selectPregunta2].forEach(select => {
        select.addEventListener("change", () => {

            if (select.selectedIndex > 0) {
                select.style.border = "2px solid #0034de";
            } else {
                select.style.border = "";
            }

            if (select === selectPregunta1 && select.selectedIndex > 0) {
                errorPregunta1.style.display = "none";
            }
            if (select === selectPregunta2 && select.selectedIndex > 0) {
                errorPregunta2.style.display = "none";
            }

        });
    });

    // seleccionar el chexbox
    const checkAutenticacion = document.getElementById("autenticacion");

    formulario.addEventListener("submit", function (e) {
        let formularioValido = true;
        let selectsValidos = true;

        // Revisar si el checkbox está marcado
        if (!checkAutenticacion.checked) {
            Swal.fire({
                icon: "warning",
                title: "Atención",
                text: "Debe activar la autenticación en dos pasos para continuar",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
            e.preventDefault();
            return; // No sigue validando ni enviando
        }

        // 2. Validar inputs (solo si el checkbox está marcado)
        Object.keys(fieldConfig).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            const regex = fieldConfig[fieldId].regex;

            const inputBox = input.closest(".input-box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;
            const label = inputBox.querySelector("label");

            if (!regex.test(input.value.trim())) {
                formularioValido = false;

                // Marcar en rojo
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                label.style.color = "red";
                inputBox.classList.add("input-error");
            }
        });

        // Validar selects (solo si el checkbox está marcado)
        const pregunta1Seleccionada = selectPregunta1.selectedIndex > 0;
        const pregunta2Seleccionada = selectPregunta2.selectedIndex > 0;

        if (!pregunta1Seleccionada) {
            selectsValidos = false;
            errorPregunta1.style.display = "block";
            selectPregunta1.style.border = "2px solid #fd1f1f";
        }
        if (!pregunta2Seleccionada) {
            selectsValidos = false;
            errorPregunta2.style.display = "block";
            selectPregunta2.style.border = "2px solid #fd1f1f";
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


    //Validaciones cambiar contraseña
    const fields = {
        contraseñaAntigua: {
            regex: /^.{4,12}$/,
            errorMessage: "La contraseña debe tener entre 4 y 12 caracteres."
        },
        nuevaContraseña: {
            regex: /^.{4,12}$/,
            errorMessage: "La contraseña debe tener entre 4 y 12 caracteres."
        },
        CNContraseña: {
            regex: null,
            errorMessage: "Ambas contraseñas tienen que ser iguales."
        }
    };


    // Validación en tiempo real
    Object.keys(fields).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        if (!input) return;

        const inputBox = input.closest(".input-box");
        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.nextElementSibling;
        const label = inputBox.querySelector("label");

        input.addEventListener("input", () => {

            const value = input.value.trim();

            if (value === "") {
                inputBox.classList.remove("input-error");
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "";
                label.style.color = "";
            } else if (fieldId === "CNContraseña") {
                const passwordValue = document.getElementById("nuevaContraseña").value.trim();
                if (value === passwordValue) {
                    checkIcon.style.display = "inline-block";
                    errorIcon.style.display = "none";
                    errorMessage.style.display = "none";
                    input.style.border = "2px solid #0034de";
                    label.style.color = "";
                    inputBox.classList.remove("input-error");
                } else {
                    checkIcon.style.display = "none";
                    errorIcon.style.display = "inline-block";
                    errorMessage.textContent = fields[fieldId].errorMessage;
                    errorMessage.style.display = "block";
                    input.style.border = "2px solid #fd1f1f";
                    label.style.color = "red";
                    inputBox.classList.add("input-error");
                }
            } else if (fields[fieldId].regex.test(value)) {
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "2px solid #0034de";
                label.style.color = "";
                inputBox.classList.remove("input-error");
            } else {
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.textContent = fields[fieldId].errorMessage;
                errorMessage.style.display = "block";
                input.style.border = "2px solid #fd1f1f";
                label.style.color = "red";
                inputBox.classList.add("input-error");
            }
        });
    });

    // Validación cruzada para confirmación
    const nuevaPasswordInput = document.getElementById("nuevaContraseña");
    const confirmarInput = document.getElementById("CNContraseña");

    nuevaPasswordInput.addEventListener("input", () => {
        confirmarInput.dispatchEvent(new Event("input"));
    });

    // Validación al enviar el formulario
    const form = document.querySelector(".formulario__contra");

    form.addEventListener("submit", function (event) {
        let valid = true;

        Object.keys(fields).forEach(fieldId => {
            const input = document.getElementById(fieldId);
            const value = input.value.trim();

            if (
                value === "" ||
                (fieldId === "CNContraseña" && value !== document.getElementById("nuevaContraseña").value.trim()) ||
                (fieldId !== "CNContraseña" && !fields[fieldId].regex.test(value))
            ) {
                valid = false;
            }
        });

        if (!valid) {
            Swal.fire({
                icon: "error",
                title: "Oops...",
                text: "Por favor rellene el formulario correctamente",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                },
                footer: '<a href="#">Why do I have this issue?</a>'
            });

            event.preventDefault();
        } else {
            sessionStorage.setItem("loginSuccess", "true");
            // El formulario se envía normalmente
        }
    });

    //Validaciones de eliminar la cuenta
    const checkbox = document.getElementById('confirm-delete');
    const deleteBtn = document.getElementById('delete-btn');

    checkbox.addEventListener('change', () => {
        if (checkbox.checked) {
            deleteBtn.disabled = false;
            deleteBtn.classList.add('active');
        } else {
            deleteBtn.disabled = true;
            deleteBtn.classList.remove('active');
        }
    });


});