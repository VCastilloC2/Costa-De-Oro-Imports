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
    email: {
        regex: /^[a-zA-Z0-9._-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,4}$/,
        errorMessage: "El correo solo puede contener letras, números, puntos, guiones y guion bajo."
    },
    telefono: {
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

const formEmpresa = document.querySelector(".form");

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

// Al cargar la página, revisamos si hay bandera de login
window.addEventListener("DOMContentLoaded", () => {

    const body = document.body;
    const mensaje = body.dataset.mensaje;

    if (mensaje) {
        Swal.fire({
            icon: "success",
            title: "Registro exitoso",
            text: mensaje,
            timer: 3000,
            showConfirmButton: false,
            timerProgressBar: true,
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        }).then(() => {
            // Redirigir al index después del Swal
            window.location.href = "/";
        });
    }
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