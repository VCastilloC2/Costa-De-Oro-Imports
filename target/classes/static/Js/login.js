// === 🚨 Variables dinámicas de Thymeleaf ===
const body = document.body;
const mensajeError   = body.dataset.mensajeError || null;
const mensajeExitoso = body.dataset.mensajeExitoso || null;
const loginSuccess   = body.dataset.loginSuccess === "true";
const rol = body.dataset.rol;
const next = body.dataset.next;
const termsLabel = document.getElementById("terms-label");

const fields = {
    email: {
        regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
        errorMessage: "El correo solo puede contener letras, números, puntos, guiones y guion bajo."
    },
    password: {
        regex: /^.{4,15}$/,
        errorMessage: "La contraseña tiene que ser de 4 a 15 dígitos."
    }
};

// Validación dinámica de inputs
Object.keys(fields).forEach(fieldId => {
    const input = document.getElementById(fieldId);
    const inputBox = input.closest(".input-box");
    const validIcon = inputBox.querySelector(".ri-check-line");
    const invalidIcon = inputBox.querySelector(".ri-close-line");
    const errorMessage = inputBox.nextElementSibling;

    input.addEventListener("input", () => {
        const value = input.value.trim();

        if (value === "") {
            // Resetear estilos si está vacío
            inputBox.classList.remove("input-error", "input-valid");
            validIcon.style.display = "none";
            invalidIcon.style.display = "none";
            errorMessage.style.display = "none";
            input.style.border = "1px solid #ccc";
        } else if (fields[fieldId].regex.test(value)) {
            // Válido
            inputBox.classList.add("input-valid");
            inputBox.classList.remove("input-error");
            validIcon.style.display = "block";
            invalidIcon.style.display = "none";
            errorMessage.style.display = "none";
            input.style.border = "2px solid #0034de";
        } else {
            // Inválido
            inputBox.classList.add("input-error");
            inputBox.classList.remove("input-valid");
            validIcon.style.display = "none";
            invalidIcon.style.display = "block";
            errorMessage.style.display = "block";
            input.style.border = "2px solid #fd1f1f";
        }
    });
});

const form = document.querySelector("form");
const inputs = document.querySelectorAll("input:not([type='checkbox'])");
const checkbox = document.querySelector(".remember-forgot input");
const loginBtn = document.querySelector("#loginBtn")
        || document.querySelector(".formulario__btn.g-recaptcha");

// Validación antes de enviar
function validateForm() {
    let formValid = true;
    inputs.forEach(input => {
        const value = input.value.trim();
        if (value === "" || !fields[input.id].regex.test(value)) {
            formValid = false;
        }
    });
    if (!checkbox.checked) {
        formValid = false;
    }
    return formValid;
}

// === 🚀 Callback de Google reCAPTCHA ===
window.onSubmit = function (token) {
    form.submit();
};

// === 🚀 Evento en el botón de login ===
if (loginBtn) {
    loginBtn.addEventListener("click", () => {
        if (!validateForm()) {
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
        grecaptcha.execute();
    });
}

// === 🚀 Flujo de login exitoso antes de ir ===
window.addEventListener("DOMContentLoaded", () => {
    if (typeof loginSuccess !== "undefined" && loginSuccess === true) {
        Swal.fire({
            title: "Inicio de sesión exitoso",
            icon: "success",
            timer: 3000,
            timerProgressBar: true,
            allowOutsideClick: false,
            allowEscapeKey: false,
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        }).then(() => {
            if (rol == "ROLE_ADMIN") {
                window.location.href = "/admin/principal/";
            } else {
                // ir a la última página solicitada antes del login
                window.location.href = next || "/";
            }
        });
    }
});

// === 🚨 Mensajes dinámicos desde el backend ===
if (typeof mensajeError !== "undefined" && mensajeError !== null) {
    Swal.fire({
        icon: "error",
        title: "Credenciales inválidas",
        text: mensajeError,
        customClass: {
            title: 'swal-title',
            popup: 'swal-popup'
        }
    });
}

if (typeof mensajeExitoso !== "undefined" && mensajeExitoso !== null) {
    Swal.fire({
        icon: "success",
        title: "Sesión cerrada",
        text: mensajeExitoso,
        timer: 3000,
        timerProgressBar: true,
        customClass: {
            title: 'swal-title',
            popup: 'swal-popup'
        }
    });
}