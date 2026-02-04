import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    finalizarCompra();

    toggleMenu();



    // --- VALIDACIÓN DE FORMULARIO DE BLOG ---
    const fieldsContactos = {
        asunto: {
            regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,100}$/,
            errorMessage: "El campo Asunto es obligatorio"
        },
        email: {
            regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
            errorMessage: "El correo solo puede contener letras,numeros,puntos,guiones y guion bajo."
        },
        mensaje: {
            regex: /^.{10,500}$/,
            errorMessage: "Por favor, ingrese un mensaje."
        }
    };

    // --- ELEMENTOS DE RATING ---
    const ratingInput = document.getElementById('rating');
    const ratingContainer = document.getElementById('ratingContainer');
    const stars = document.querySelectorAll('.star');
    const ratingText = document.getElementById('ratingText');

    const ratingMessages = {
        1: 'Malo - Necesita mejorar',
        2: 'Regular - Puede mejorar',
        3: 'Bueno - Contenido aceptable',
        4: 'Muy Bueno - Gran contenido',
        5: 'Excelente - ¡Contenido excepcional!'
    };

    let selectedRating = 0;

    // --- MANEJO DE ESTRELLAS ---
    stars.forEach((star, index) => {
        star.addEventListener('click', () => {
            selectedRating = index + 1;
            ratingInput.value = selectedRating;

            updateStars(selectedRating);
            ratingText.textContent = ratingMessages[selectedRating];

            // Remover error si existe
            const parent = ratingContainer.closest('.formulario__input');
            const error = parent.querySelector('.input__error');
            ratingContainer.classList.remove('input-error');
            error.style.display = 'none';

            // Border azul al seleccionar
            ratingContainer.style.border = "2px solid #0034de";
        });

        star.addEventListener('mouseenter', () => {
            updateStars(index + 1);
            ratingText.textContent = ratingMessages[index + 1];
        });
    });

    document.getElementById('ratingStars').addEventListener('mouseleave', () => {
        updateStars(selectedRating);

        if (selectedRating > 0) {
            ratingText.textContent = ratingMessages[selectedRating];
        } else {
            ratingText.textContent = 'Selecciona tu calificación';
            ratingContainer.style.border = ""; // limpia borde si no ha seleccionado
        }
    });

    // --- ACTUALIZAR ESTRELLAS ---
    function updateStars(rating) {
        stars.forEach((star, index) => {
            star.classList.toggle("active", index < rating);
        });
    }


    // --- VALIDACIÓN EN TIEMPO REAL ---
    Object.keys(fieldsContactos).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        const inputBox = input.closest(".input-box");
        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.nextElementSibling;

        input.addEventListener("input", () => {
            const value = input.value.trim();
            const field = fieldsContactos[fieldId];

            if (value === "") {
                input.style.border = "";
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                inputBox.classList.remove("input-error");
            }
            else if (field.regex.test(value)) {
                input.style.border = "2px solid #0034de";
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                inputBox.classList.remove("input-error");
            }
            else {
                input.style.border = "2px solid #fd1f1f";
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                inputBox.classList.add("input-error");
            }
        });
    });


    // --- VALIDACIÓN GENERAL DEL FORMULARIO ---
    const addform = document.querySelector(".formulario");
    const inputs2 = addform.querySelectorAll("input, textarea");

    addform.addEventListener("submit", function (event) {
        event.preventDefault();

        let formValid = true;

        // Validar inputs
        inputs2.forEach(input => {
            const value = input.value.trim();
            const field = fieldsContactos[input.id];

            if (!field) return;

            const inputBox = input.closest(".input-box");
            const checkIcon = inputBox.querySelector(".ri-check-line");
            const errorIcon = inputBox.querySelector(".ri-close-line");
            const errorMessage = inputBox.nextElementSibling;

            if (value === "" || !field.regex.test(value)) {
                formValid = false;
                input.style.border = "2px solid #fd1f1f";
                checkIcon.style.display = "none";
                errorIcon.style.display = "inline-block";
                errorMessage.style.display = "block";
                inputBox.classList.add("input-error");
            } else {
                input.style.border = "2px solid #0034de";
                checkIcon.style.display = "inline-block";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                inputBox.classList.remove("input-error");
            }
        });

        // --- VALIDACIÓN DEL RATING ---
        if (selectedRating === 0) {
            const parent = ratingContainer.closest('.formulario__input');
            const error = parent.querySelector('.input__error');

            ratingContainer.classList.add('input-error');
            error.style.display = 'block';

            // borde rojo si falta calificación
            ratingContainer.style.border = "2px solid #fd1f1f";

            formValid = false;
        }

        // Si hay errores
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
            return;
        }

        // Si todo está correcto
        sessionStorage.setItem("loginSuccess", "true");
        addform.submit();
    });


    // --- MENSAJE ÉXITO ---
    window.addEventListener("DOMContentLoaded", () => {
        if (sessionStorage.getItem("loginSuccess") === "true") {

            Swal.fire({
                title: "Registro exitoso",
                icon: "success",
                timer: 3000,
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