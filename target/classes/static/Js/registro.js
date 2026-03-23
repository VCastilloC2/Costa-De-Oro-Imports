//Validaciones con javascript
const fields = {
    nombre: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El nombre debe tener al menos 3 letras." },
    apellido: { regex: /^[A-Za-zÁÉÍÓÚáéíóúÑñ\s]{3,}$/, errorMessage: "El apellido debe tener al menos 3 letras." },
    identificacion: { regex: /^\d{6,10}$/, errorMessage: "La cédula debe contener entre 6 y 10 dígitos." },
    telefono: { regex: /^\d{1,10}$/, errorMessage: "El teléfono solo puede contener números (máx. 10)." },
    email: { regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/, errorMessage: "El correo solo puede contener letras,numeros,puntos,guiones y guion bajo." },
    password: { regex: /^.{4,12}$/, errorMessage: "La contraseña debe tener entre 4 y 12 caracteres." },
    repeatPassword: { regex: null, errorMessage: "Las contraseñas no coinciden." }
};

const tipoIdentificacionSelect = document.getElementById("tipoIdentificacion");

const termsLabel = document.getElementById("terms-label");

termsLabel.addEventListener("click", (e) => {
    // Evita que el checkbox se marque/desmarque automáticamente
    e.preventDefault();

    Swal.fire({
        title: "Términos y Condiciones",
        html: `
        <div style="text-align: left; max-height: 300px; overflow-y: auto; padding: 10px;">
            <h3>1. INFORMACIÓN GENERAL</h3>
            <p>Este sitio web es operado por Costa De Oro Imports. En todo el sitio, los términos "nosotros", "nos" y "nuestro" se refieren a Costa De Oro Imports.<br>
            Sitio web: www.costadeoroimports.co<br>
            Email de contacto: costadeoroimports@gmail.com</p>
            <br>
            <h3>2. PRODUCTOS Y SERVICIOS</h3>
            <p>Nos esforzamos por mostrar con la mayor precisión los colores y las imágenes de nuestros productos que aparecen en la tienda. No podemos garantizar que la visualización de cualquier color en su monitor sea exacta.</p>
            <br>
            <h3>3. PRECIOS Y PAGOS</h3>
            <p>Todos los precios están sujetos a cambios sin previo aviso. Los precios mostrados incluyen IVA cuando corresponda.</p>
            <br>
            <h3>4. ENVÍOS</h3>
            <p>El tiempo estimado de envío es de 5 - 10 días hábiles. Los tiempos de entrega son estimados y no podemos garantizar entregas en fechas específicas.</p>
            <br>
            <h3>5. POLÍTICA DE DEVOLUCIONES</h3>
            <p>Aceptamos devoluciones dentro de los 14 días posteriores a la recepción del producto.</p>
            <br>
            <h4>5.1 PROCEDIMIENTO DE DEVOLUCIÓN</h4>
            <ul>
                <li><b>Devolución del dinero</b> (solo aplica para casos de garantía y ley de retracto):
                    <ul>
                        <li>A través de transferencia: dentro de los cinco días hábiles siguientes de recibir el producto nuevamente en nuestra bodega.</li>
                        <li>A través de reversión del pago: quince días hábiles después de recibir el producto. Esta reversión corre por cuenta de tu entidad bancaria.</li>
                    </ul>
                </li>
                <li><b>Cambio del producto</b> (sujeto a disponibilidad de inventario).</li>
                <li><b>Cupón</b> para realizar una nueva compra (válido por seis meses a partir de la fecha de creación).</li>
            </ul>
            <br>
            <h4>5.2 CONDICIONES DEL PRODUCTO PARA DEVOLUCIÓN</h4>
            <p>El producto deberá devolverse en óptimas condiciones, sin rastros de uso, con etiquetas originales o introducidas en el empaque. Una vez recibido en nuestra bodega, verificaremos las condiciones y según el resultado, se enviará un producto nuevo o se entregará un cupón para otra compra.</p>
            <br>
            <h3>6. PRIVACIDAD Y PROTECCIÓN DE DATOS</h3>
            <p>Nos comprometemos a proteger su privacidad. La información personal que nos proporcione se utilizará únicamente para procesar su pedido y mejorar su experiencia de compra.</p>
            <br>
            <h3>7. MODIFICACIONES DE LOS TÉRMINOS</h3>
            <p>Nos reservamos el derecho de modificar estos términos en cualquier momento. Los cambios entrarán en vigor inmediatamente después de su publicación en el sitio web.</p>
        </div>
    `,
        icon: "info",
        showCancelButton: true,
        confirmButtonText: "Acepto",
        cancelButtonText: "Cancelar",
        customClass: {
            title: 'swal-title',
            popup: 'swal-popup'
        }
    }).then((result) => {
        if (result.isConfirmed) {
            checkbox.checked = true; // Marca el checkbox si acepta
        } else {
            checkbox.checked = false; // Lo deja desmarcado si cancela
        }
    });
});

// Verifica si el usuario ya existe
if (typeof mensajeExiste !== "undefined" && mensajeExiste !== null) {
    Swal.fire({
        icon: "error",
        title: "El usuario ya existe en la base de datos",
        text: mensajeExiste, // Usamos la variable mensajeExiste
        customClass: {
            title: 'swal-title',
            popup: 'swal-popup'
        }
    });
}

// Verifica si hubo error en credenciales
if (typeof mensajeError !== "undefined" && mensajeError !== null) {
    Swal.fire({
        icon: "error",
        title: "Credenciales inválidas",
        text: mensajeError, // Usamos la variable mensajeError
        customClass: {
            title: 'swal-title',
            popup: 'swal-popup'
        }
    });
}

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
            checkIcon.style.display = "none";
            errorIcon.style.display = "none";
            errorMessage.style.display = "none";
            input.style.border = "";
            label.style.color = "";
            inputBox.classList.remove("input-error");
        } else if (fieldId === "repeatPassword") {
            const passwordValue = document.getElementById("password").value.trim();
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
            errorMessage.style.display = "block";
            input.style.border = "2px solid #fd1f1f";
            label.style.color = "red";
            inputBox.classList.add("input-error");
        }
    });
});


const form = document.querySelector("form");
const inputs = document.querySelectorAll("input:not([type='checkbox'])");
const checkbox = document.querySelector(".remember-forgot input");
const tipoIdentificacionError = tipoIdentificacionSelect.closest(".formulario__input").querySelector(".input__error");

// Validación del select: ocultar error al seleccionar opción válida
tipoIdentificacionSelect.addEventListener("change", () => {
    if (tipoIdentificacionSelect.selectedIndex !== 0) {
        tipoIdentificacionError.style.display = "none";
        tipoIdentificacionSelect.style.border = "2px solid #0034de";
    }
});



form.addEventListener("submit", function (event) {
    let valid = true;
    let emptyCount = 0;
    let tipoIdentificacionInvalido = tipoIdentificacionSelect.selectedIndex === 0;

    Object.keys(fields).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        const value = input.value.trim();

        if (value === "") {
            emptyCount++;
            valid = false;
        } else if (fieldId === "repeatPassword") {
            const passwordValue = document.getElementById("password").value.trim();
            if (value !== passwordValue) {
                valid = false;
            }
        } else if (!fields[fieldId].regex.test(value)) {
            valid = false;
        }
    });

    // Caso: todos los campos vacíos y checkbox no marcado
    const allFieldsEmpty = emptyCount === Object.keys(fields).length && tipoIdentificacionInvalido && !checkbox.checked;

    if (allFieldsEmpty) {
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Por favor rellene el formulario correctamente",
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        });
        tipoIdentificacionError.style.display = "none";
        tipoIdentificacionSelect.style.border = "";
        event.preventDefault();
        return;
    }

    // Caso: solo el select está vacío
    if (tipoIdentificacionInvalido && emptyCount === 0 && checkbox.checked) {
        tipoIdentificacionError.style.display = "block";
        tipoIdentificacionSelect.style.border = "2px solid #fd1f1f";
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Por favor rellene el formulario correctamente",
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        });
        event.preventDefault();
        return;
    }

    // Validar tipoIdentificacion si es inválido
    if (tipoIdentificacionInvalido) {
        tipoIdentificacionError.style.display = "none";
        tipoIdentificacionSelect.style.border = "";
        valid = false;
    } else {
        tipoIdentificacionError.style.display = "none";
        tipoIdentificacionSelect.style.border = "2px solid #0034de";
    }

    // Validar checkbox
    if (!checkbox.checked) {
        valid = false;
    }

    // Si hay otros errores
    if (!valid) {
        Swal.fire({
            icon: "error",
            title: "Oops...",
            text: "Por favor rellene el formulario correctamente",
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        });
        event.preventDefault();
    } else {
        // Guardamos bandera en sessionStorage
        sessionStorage.setItem("loginSuccess", "true");
        // El formulario se envía normalmente
    }
});

// Al cargar la página, revisamos si hay bandera de login
window.addEventListener("DOMContentLoaded", () => {
    const body = document.body;
    const mensaje = body.getAttribute("data-mensaje");
    const success = body.getAttribute("data-success");

    if (mensaje) {
        Swal.fire({
            icon: success === "true" ? "success" : "error",
            title: success === "true" ? "Registro exitoso" : "Error",
            text: mensaje,
            timer: 3000,
            timerProgressBar: true,
            allowOutsideClick: false,
            allowEscapeKey: false,
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        }).then(() =>  {
            if (success === "true") {
                window.location.href = "/auth/login";
            }
        });
    }
});

const passwordInput = document.getElementById("password");
const repeatPasswordInput = document.getElementById("repeatPassword");
passwordInput.addEventListener("input", () => {
    repeatPasswordInput.dispatchEvent(new Event("input"));
});