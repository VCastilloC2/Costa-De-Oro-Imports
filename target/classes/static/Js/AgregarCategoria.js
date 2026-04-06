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

    // funcionamiento de las dos categorias en el formulario
    const inputSub = document.getElementById("Subcategoria");
    const listaSub = document.getElementById("subcategoriaLista");

    // Creamos contenedor visual para tags dentro del input
    const contenedorTags = document.createElement("div");
    contenedorTags.id = "subcategoriaTags";
    inputSub.parentNode.insertBefore(contenedorTags, inputSub);

    let subcategorias = [];
    let listaVisible = false;

    inputSub.addEventListener("keydown", (e) => {
        if (e.key === "Enter") {
            e.preventDefault();
            const valor = inputSub.value.trim();
            if (valor === "") return;
            if (subcategorias.includes(valor.toLowerCase())) return;

            subcategorias.push(valor.toLowerCase());
            inputSub.value = "";
            actualizarInterfaz();
        }
    });

    // Mostrar la lista al hacer clic o enfocar el input
    inputSub.addEventListener("focus", () => {
        listaVisible = true;
        listaSub.classList.add("visible");
        listaSub.classList.remove("oculto");
    });

    // Ocultar la lista cuando haces clic fuera del input o de la lista
    document.addEventListener("click", (e) => {
        if (!inputSub.contains(e.target) && !listaSub.contains(e.target)) {
            listaVisible = false;
            listaSub.classList.remove("visible");
            listaSub.classList.add("oculto");
        }
    });

    function actualizarInterfaz() {
        // Actualiza lista desplegable
        listaSub.innerHTML = "";
        subcategorias.forEach((sub) => {
            const p = document.createElement("p");
            p.innerHTML = `${sub} <i class="ri-close-line"></i>`;
            p.querySelector("i").addEventListener("click", () => eliminarSubcategoria(sub));
            listaSub.appendChild(p);
        });

        // Actualiza tags dentro del input
        contenedorTags.innerHTML = "";
        const visibles = subcategorias.slice(0, 2);
        visibles.forEach((sub) => {
            const tag = document.createElement("div");
            tag.className = "sub-tag";
            tag.innerHTML = `${sub} <i class="ri-close-line"></i>`;
            tag.querySelector("i").addEventListener("click", () => eliminarSubcategoria(sub));
            contenedorTags.appendChild(tag);
        });

        if (subcategorias.length > 2) {
            const extra = document.createElement("div");
            extra.className = "sub-tag";
            extra.textContent = `+${subcategorias.length - 2}`;
            contenedorTags.appendChild(extra);
        }

        // Mostrar/ocultar lista
        if (subcategorias.length === 0) {
            listaSub.classList.add("oculto");
        } else if (listaVisible) {
            listaSub.classList.add("visible");
        }

        // Actualizar el input oculto
        document.getElementById("subcategoriasHidden").value = subcategorias.join(",");

        requestAnimationFrame(() => {
            // Medimos el ancho real ocupado por los tags
            let offset = 0;
            if (contenedorTags.children.length > 0) {
                const lastTag = contenedorTags.lastElementChild;
                offset = lastTag.offsetLeft + lastTag.offsetWidth + 5;
            } else {
                offset = 15; // margen por defecto si no hay tags
            }

            // Mueve el contenedor de los tags dentro del input
            contenedorTags.style.position = "absolute";
            contenedorTags.style.left = "30px";
            contenedorTags.style.top = "22px";

            // Mueve visualmente el input al mismo nivel, sin cambiar su ancho ni padding
            inputSub.style.textIndent = offset + "px";
            inputSub.focus();

            // coloca el cursor al final
            const len = inputSub.value.length;
            inputSub.setSelectionRange(len, len);
        });

        // Mantiene el label arriba si existen subcategorías
        if (subcategorias.length > 0) {
            inputSub.classList.add("has-value");
        } else {
            inputSub.classList.remove("has-value");
        }
    }

    function eliminarSubcategoria(valor) {
        subcategorias = subcategorias.filter((s) => s !== valor);
        actualizarInterfaz();
    }

    // Imagen del producto - CORREGIDO: Usar el input file que ya existe en el HTML
    const boxImagen = document.querySelector('.formulario__boximagen');
    const inputFile = document.getElementById('fileInput'); // Usar el input existente
    const previewContainer = document.getElementById('previewContainer');
    const errorFormato = document.querySelector('.error--formato');
    const errorVacio = document.querySelector('.error--vacio');

    const formatosPermitidos = ["image/jpeg", "image/png", "image/webp"];

    // Al hacer clic en el contenedor, abrir explorador
    boxImagen.addEventListener('click', () => inputFile.click());

    // Escuchar cambio del input
    inputFile.addEventListener('change', (event) => {
        const file = event.target.files[0];
        if (!file) return;

        // Validar formato
        if (!formatosPermitidos.includes(file.type)) {
            errorFormato.style.display = 'block';
            errorVacio.style.display = 'none';
            previewContainer.innerHTML = '';
            previewContainer.style.display = 'none';
            boxImagen.classList.remove('imagen-activa');
            return;
        }

        // Si el formato es válido → ocultar errores
        errorFormato.style.display = 'none';
        errorVacio.style.display = 'none';
        boxImagen.style.border = '1px solid #ddd';

        // Crear barra de carga
        previewContainer.innerHTML = `<div class="progress-bar"></div>`;
        previewContainer.style.display = 'flex';
        boxImagen.classList.add('imagen-activa');

        // Simular carga
        setTimeout(() => {
            const img = document.createElement('img');
            img.src = URL.createObjectURL(file);

            previewContainer.innerHTML = '';
            previewContainer.appendChild(img);

            // Crear botón eliminar (solo si no existe)
            let removeBtn = boxImagen.querySelector('.remove-image');
            if (!removeBtn) {
                removeBtn = document.createElement('span');
                removeBtn.classList.add('remove-image');
                removeBtn.textContent = 'Eliminar imagen';
                boxImagen.appendChild(removeBtn);
            }

            // Acción eliminar
            removeBtn.addEventListener('click', (e) => {
                e.stopPropagation(); // evita que abra el explorador

                // Eliminar imagen y botón
                previewContainer.style.display = 'none';
                previewContainer.innerHTML = '';
                boxImagen.classList.remove('imagen-activa');
                removeBtn.remove();
                inputFile.value = ''; // limpiar el input
            });
        }, 2000);
    });

    //Descripcion de un producto
    const toolbarOptions = [
        ['bold', 'italic', 'underline'],
        [{ 'list': 'ordered'}, { 'list': 'bullet' }],
        ['link', 'clean']
    ];

    const quill = new Quill('#editor-container', {
        modules: {
            toolbar: toolbarOptions
        },
        theme: 'snow',
        placeholder: 'Escribe la descripción de la categoría...'
    });

    //Validaciones del formulario de agregar producto
    const fieldsProducto = {
        categoriaprincipal: {
            regex: /^([A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)(\s[A-ZÁÉÍÓÚÑ][a-záéíóúñ]+)*$/,
            errorMessage: "La categoria principal debe tener al menos 3 letras donde la primera letra tiene que ser mayuscula y no contener números."
        }
    };

    // Asignamos validaciones de escritura en tiempo real
    Object.keys(fieldsProducto).forEach(fieldId => {
        const input = document.getElementById(fieldId);
        const inputBox = input.closest(".formulario__box");
        const checkIcon = inputBox.querySelector(".ri-check-line");
        const errorIcon = inputBox.querySelector(".ri-close-line");
        const errorMessage = inputBox.parentNode.querySelector(".formulario__error");

        input.addEventListener("input", () => {
            const value = input.value.trim();
            const label = inputBox.querySelector("label.box__label");

            if (value === "") {
                inputBox.classList.remove("input-error");
                checkIcon.style.display = "none";
                errorIcon.style.display = "none";
                errorMessage.style.display = "none";
                input.style.border = "";
                if (label) label.classList.remove("error");
            } else if (fieldsProducto[fieldId].regex.test(value)) {
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
    });

    // Obtener radios y mensaje de error
    const radiosEstado = document.querySelectorAll('input[name="activo"]');
    const errorEstado = document.querySelector('.error--estado');
    const radiosCustom = document.querySelectorAll('.radio__custom');

    // Quitar el error cuando el usuario selecciona un estado
    radiosEstado.forEach(radio => {
        radio.addEventListener('change', () => {
            errorEstado.style.display = 'none';
            radiosCustom.forEach(r => r.classList.remove('error'));
        });
    });

    // Validación de imagen
    function validarImagenes() {
        const file = inputFile.files[0];
        errorFormato.style.display = "none";
        errorVacio.style.display = "none";
        boxImagen.style.border = "1px solid #ddd";

        if (!file) {
            errorVacio.style.display = "block";
            boxImagen.style.border = "2px solid #e53935";
            return false;
        }

        if (!formatosPermitidos.includes(file.type)) {
            errorFormato.style.display = "block";
            boxImagen.style.border = "2px solid #e53935";
            return false;
        }

        boxImagen.style.border = "1px solid #ddd";
        return true;
    }

    // Validación de descripción del producto
    const boxDescripcion = document.querySelector('.formulario__boxdescripcion');
    const errorDescripcion = document.querySelector('.error--descripcion');

    function validarDescripcion() {
        const contenido = quill.getText().trim();
        errorDescripcion.style.display = 'none';
        boxDescripcion.classList.remove('error-border');

        if (contenido === '' || contenido.length === 0) {
            errorDescripcion.style.display = 'block';
            boxDescripcion.classList.add('error-border');
            return false;
        }

        return true;
    }

    quill.on('text-change', () => {
        const contenido = quill.getText().trim();
        if (contenido.length > 0) {
            errorDescripcion.style.display = 'none';
            boxDescripcion.classList.remove('error-border');
        }
    });

    // --- VALIDACIÓN GENERAL DEL FORMULARIO ---
    const addform = document.getElementById("formularioProducto");

    addform.addEventListener("submit", function (e) {
        let formularioValido = true;

        // Validar categoría principal
        const inputCategoria = document.getElementById("categoriaprincipal");
        const categoriaBox = inputCategoria.closest(".formulario__box");
        const categoriaCheck = categoriaBox.querySelector(".ri-check-line");
        const categoriaErrorIcon = categoriaBox.querySelector(".ri-close-line");
        const categoriaErrorMsg = categoriaBox.parentNode.querySelector(".formulario__error");
        const categoriaLabel = categoriaBox.querySelector("label.box__label");

        if (inputCategoria.value.trim() === "" || !fieldsProducto.categoriaprincipal.regex.test(inputCategoria.value.trim())) {
            formularioValido = false;
            categoriaCheck.style.display = "none";
            categoriaErrorIcon.style.display = "inline-block";
            categoriaErrorMsg.style.display = "block";
            inputCategoria.style.border = "2px solid #fd1f1f";
            if (categoriaLabel) categoriaLabel.classList.add("error");
            categoriaBox.classList.add("input-error");
        } else {
            categoriaCheck.style.display = "inline-block";
            categoriaErrorIcon.style.display = "none";
            categoriaErrorMsg.style.display = "none";
            inputCategoria.style.border = "2px solid #0034de";
            if (categoriaLabel) categoriaLabel.classList.remove("error");
            categoriaBox.classList.remove("input-error");
        }

        // --- VALIDAR SUBCATEGORÍAS ---
        const hiddenSub = document.getElementById("subcategoriasHidden");
        const subBox = document.getElementById("subcategoriaBox");
        const subCheck = subBox.querySelector(".ri-check-line");
        const subErrorIcon = subBox.querySelector(".ri-close-line");
        const subErrorMsg = subBox.parentNode.querySelector(".formulario__error");

        // Validar que haya al menos una subcategoría
        if (subcategorias.length === 0) {
            formularioValido = false;
            subCheck.style.display = "none";
            subErrorIcon.style.display = "inline-block";
            subErrorMsg.style.display = "block";
            subBox.classList.add("input-error");
        } else {
            subErrorMsg.style.display = "none";
            subBox.classList.remove("input-error");
            subErrorIcon.style.display = "none";
            subCheck.style.display = "inline-block";
        }

        // Validar estado (radio buttons)
        const estadoSeleccionado = Array.from(radiosEstado).some(radio => radio.checked);

        if (!estadoSeleccionado) {
            formularioValido = false;
            errorEstado.style.display = 'block';
            radiosCustom.forEach(r => r.classList.add('error'));
        } else {
            errorEstado.style.display = 'none';
            radiosCustom.forEach(r => r.classList.remove('error'));
        }

        // Validar imagen
        if (!validarImagenes()) {
            formularioValido = false;
        }

        // Validar descripción
        if (!validarDescripcion()) {
            formularioValido = false;
        }

        // CORRECCIÓN: Obtener el texto plano sin etiquetas HTML para la descripción
        const contenidoDescripcion = quill.getText().trim();
        document.getElementById("descripcionHidden").value = contenidoDescripcion;

        // Mostrar error general
        if (!formularioValido) {
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
});