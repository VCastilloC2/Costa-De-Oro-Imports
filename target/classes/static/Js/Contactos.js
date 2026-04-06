import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    toggleMenu();


    // --- VALIDACIONES DE CONTACTOS ---
    const fieldsContactos = {
        nombre: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El nombre debe tener al menos 3 letras." },
        sujeto: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,100}$/, errorMessage: "El campo Sujeto es obligatorio" },
        email: { regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/, errorMessage: "El correo solo puede contener letras,numeros,puntos,guiones y guion bajo." },
        comentario: { regex: /^.{10,500}$/, errorMessage: "Por favor, ingrese un comentario." }
    };

    const selectTipoSolicitud = document.getElementById("TipoSolicitud");
    const errorTipoSolicitud = document.querySelector(".error--TipoSolicitud");

    // --- VALIDACIÓN EN TIEMPO REAL DE INPUTS ---
    Object.keys(fieldsContactos).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        const inputBox = input.closest(".input-box");
        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.nextElementSibling;

        input.addEventListener("input", () => {
            const value = input.value.trim();

            if (value === "") {
                inputBox.classList.remove("input-error");
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "";
            } else if (fieldsContactos[fieldId].regex.test(value)) {
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
        });
    });

    // --- VALIDACIÓN DEL SELECT ---
    selectTipoSolicitud.addEventListener("change", () => {
        const value = selectTipoSolicitud.value;

        if (value && value !== "Tipo de solicitud") {
            selectTipoSolicitud.style.border = "2px solid #0034de";
            errorTipoSolicitud.style.display = "none";
            selectTipoSolicitud.classList.remove("input-error");
        } else {
            selectTipoSolicitud.style.border = "2px solid #fd1f1f";
            errorTipoSolicitud.style.display = "block";
            selectTipoSolicitud.classList.add("input-error");
        }
    });

    // --- VALIDACIÓN GENERAL AL ENVIAR EL FORMULARIO ---
    const addform = document.querySelector(".form");
    const inputs2 = addform.querySelectorAll("input, textarea");

    addform.addEventListener("submit", function (event) {
        event.preventDefault();
        let formValid = true;

        // Validar inputs y textarea
        inputs2.forEach(input => {
            const value = input.value.trim();
            const field = fieldsContactos[input.id];

            // Evitar error si el input no está en el objeto
            if (!field) return;

            const inputBox = input.closest(".input-box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;

            if (value === "" || !field.regex.test(value)) {
                formValid = false;
                input.style.border = "2px solid #fd1f1f";
                inputBox.classList.add("input-error");
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
            } else {
                input.style.border = "2px solid #0034de";
                inputBox.classList.remove("input-error");
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
            }
        });

        // Validar select
        const valueSelect = selectTipoSolicitud.value;
        if (!valueSelect || valueSelect === "Tipo de solicitud") {
            formValid = false;
            selectTipoSolicitud.style.border = "2px solid #fd1f1f";
            errorTipoSolicitud.style.display = "block";
            selectTipoSolicitud.classList.add("input-error");
        } else {
            selectTipoSolicitud.style.border = "2px solid #0034de";
            errorTipoSolicitud.style.display = "none";
            selectTipoSolicitud.classList.remove("input-error");
        }

        // Mostrar alerta si hay errores
        if (!formValid) {
            Swal.fire({
                icon: "error",
                title: "Oops...",
                text: "Por favor rellene el formulario correctamente",
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
        } else {
            sessionStorage.setItem("loginSuccess", "true");
            addform.submit(); // Enviar si todo está bien
        }
    });

    // --- MENSAJE DE ÉXITO ---
    window.addEventListener("DOMContentLoaded", () => {
        if (sessionStorage.getItem("loginSuccess") === "true") {
            Swal.fire({
                title: "Proceso exitoso",
                text: "Petición enviada exitosamente",
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
    });

});