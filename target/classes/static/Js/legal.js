// legal.js - Funcionalidades comunes para páginas legales
// Costa De Oro Imports

(function () {
    'use strict';

    /* -----------------------------------------------------------
       1) Header glassmorphism al hacer scroll
    ----------------------------------------------------------- */
    function activarGlassmorphism() {
        const header = document.querySelector('.header');
        if (!header) return;

        const onScroll = () => {
            if (window.scrollY > 10) {
                header.classList.add('scrolled');
            } else {
                header.classList.remove('scrolled');
            }
        };

        window.addEventListener('scroll', onScroll, { passive: true });
        onScroll();
    }

    /* -----------------------------------------------------------
       2) Menú móvil (open / close)
    ----------------------------------------------------------- */
    function configurarMenuMovil() {
        const openBtn = document.getElementById('open-menu');
        const closeBtn = document.getElementById('close-menu');
        const navbar = document.querySelector('.header__navbar');

        if (openBtn && navbar) {
            openBtn.addEventListener('click', () => {
                navbar.classList.add('active');
            });
        }
        if (closeBtn && navbar) {
            closeBtn.addEventListener('click', () => {
                navbar.classList.remove('active');
            });
        }

        // Cerrar al hacer click en un enlace
        document.querySelectorAll('.navbar__link').forEach(link => {
            link.addEventListener('click', () => {
                if (navbar) navbar.classList.remove('active');
            });
        });
    }

    /* -----------------------------------------------------------
       3) Scroll suave a secciones desde la sidebar
    ----------------------------------------------------------- */
    function configurarScrollSuave() {
        document.querySelectorAll('a[href^="#"]').forEach(link => {
            link.addEventListener('click', (e) => {
                const href = link.getAttribute('href');
                if (!href || href === '#') return;
                const target = document.querySelector(href);
                if (!target) return;
                e.preventDefault();
                const offset = 120;
                const top = target.getBoundingClientRect().top + window.scrollY - offset;
                window.scrollTo({ top, behavior: 'smooth' });
            });
        });
    }

    /* -----------------------------------------------------------
       4) Resaltar sección activa en la sidebar mientras se hace scroll
    ----------------------------------------------------------- */
    function resaltarSeccionActiva() {
        const sidebarLinks = document.querySelectorAll('.legal-sidebar__list a[href^="#"]');
        if (sidebarLinks.length === 0) return;

        const sections = Array.from(sidebarLinks)
            .map(link => {
                const id = link.getAttribute('href').substring(1);
                return document.getElementById(id);
            })
            .filter(Boolean);

        if (sections.length === 0) return;

        const observer = new IntersectionObserver((entries) => {
            entries.forEach(entry => {
                if (entry.isIntersecting) {
                    sidebarLinks.forEach(l => l.classList.remove('legal-sidebar__link--active'));
                    const id = entry.target.id;
                    const active = document.querySelector(`.legal-sidebar__list a[href="#${id}"]`);
                    if (active) active.classList.add('legal-sidebar__link--active');
                }
            });
        }, {
            rootMargin: '-30% 0px -60% 0px',
            threshold: 0
        });

        sections.forEach(s => observer.observe(s));
    }

    /* -----------------------------------------------------------
       5) Año dinámico en el copyright
    ----------------------------------------------------------- */
    function actualizarAnio() {
        const el = document.getElementById('anio__pagina');
        if (el) el.textContent = new Date().getFullYear();
    }

    /* -----------------------------------------------------------
       6) Validación básica del formulario PQRS
    ----------------------------------------------------------- */
    function configurarFormularioPQRS() {
        const form = document.getElementById('legalForm');
        if (!form) return;

        form.addEventListener('submit', (e) => {
            e.preventDefault();

            const nombre = form.querySelector('[name="nombre"]')?.value.trim();
            const email = form.querySelector('[name="email"]')?.value.trim();
            const tipo = form.querySelector('[name="tipo"]')?.value;
            const mensaje = form.querySelector('[name="mensaje"]')?.value.trim();

            if (!nombre || !email || !tipo || !mensaje) {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: 'warning',
                        title: 'Campos incompletos',
                        text: 'Por favor completa todos los campos obligatorios.',
                        confirmButtonColor: '#f59e0b'
                    });
                } else {
                    alert('Por favor completa todos los campos obligatorios.');
                }
                return;
            }

            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailRegex.test(email)) {
                if (typeof Swal !== 'undefined') {
                    Swal.fire({
                        icon: 'error',
                        title: 'Email inválido',
                        text: 'Por favor ingresa un correo electrónico válido.',
                        confirmButtonColor: '#f59e0b'
                    });
                } else {
                    alert('Email inválido.');
                }
                return;
            }

            // Feedback de éxito
            if (typeof Swal !== 'undefined') {
                Swal.fire({
                    icon: 'success',
                    title: '¡Solicitud enviada!',
                    html: 'Hemos recibido tu <strong>' + tipo + '</strong>.<br>Te responderemos en máximo 15 días hábiles.',
                    confirmButtonColor: '#f59e0b'
                });
            } else {
                alert('Solicitud enviada correctamente.');
            }
            form.reset();
        });
    }

    /* -----------------------------------------------------------
       7) Botón "Volver arriba"
    ----------------------------------------------------------- */
    function configurarBotonVolverArriba() {
        const btn = document.createElement('button');
        btn.className = 'legal-back-to-top';
        btn.setAttribute('aria-label', 'Volver arriba');
        btn.innerHTML = '<i class="ri-arrow-up-line"></i>';
        document.body.appendChild(btn);

        const onScroll = () => {
            if (window.scrollY > 400) {
                btn.classList.add('legal-back-to-top--visible');
            } else {
                btn.classList.remove('legal-back-to-top--visible');
            }
        };
        window.addEventListener('scroll', onScroll, { passive: true });

        btn.addEventListener('click', () => {
            window.scrollTo({ top: 0, behavior: 'smooth' });
        });
    }

    /* -----------------------------------------------------------
       Inicialización
    ----------------------------------------------------------- */
    document.addEventListener('DOMContentLoaded', () => {
        activarGlassmorphism();
        configurarMenuMovil();
        configurarScrollSuave();
        resaltarSeccionActiva();
        actualizarAnio();
        configurarFormularioPQRS();
        configurarBotonVolverArriba();
    });
})();
