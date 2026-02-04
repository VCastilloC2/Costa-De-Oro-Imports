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

    // === Actualizar el estado de la Categoría
    const btnGuardar = document.getElementById("guardar-estado");
    const selectEstado = document.getElementById("filtro-estado");
    const compraId = document.getElementById("compraId").value;

    btnGuardar.addEventListener("click", async () => {
        const estadoSeleccionado = selectEstado.value;

        if (!estadoSeleccionado) {
            Swal.fire({
                icon: "warning",
                title: "Selecciona un estado",
                text: "Debes elegir un estado antes de guardar",
                timer: 2500,
                showConfirmButton: false,
                customClass: {
                    title: 'swal-title',
                    popup: 'swal-popup'
                }
            });
            return;
        }

        // Confirmar con SweetAlert antes de enviar
        const confirm = await Swal.fire({
            icon: "question",
            title: "¿Cambiar estado?",
            text: `El estado se cambiará a "${estadoSeleccionado}"`,
            showCancelButton: true,
            confirmButtonText: "Sí, cambiar",
            cancelButtonText: "Cancelar",
            customClass: {
                title: 'swal-title',
                popup: 'swal-popup'
            }
        });

        if (confirm.isConfirmed) {
            try {
                // Redirige al endpoint del @Controller
                window.location.href = `/admin/compra/detalle-compra/cambiar-estado/${compraId}?estado=${encodeURIComponent(estadoSeleccionado)}`;
            } catch (error) {
                Swal.fire({
                    icon: "error",
                    title: "Error",
                    text: "No se pudo cambiar el estado",
                    customClass: {
                        title: 'swal-title',
                        popup: 'swal-popup'
                    }
                });
                console.error("Error al cambiar estado:", error);
            }
        }
    });

    // === MOSTRAR RESPUESTA DEL BACK-END ===
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

});