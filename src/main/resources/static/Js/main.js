export function activarGlassmorphism() {
    // Efecto glassmorphism solo al hacer scroll
    const header = document.querySelector('.header');

    window.addEventListener('scroll', () => {
        if (window.scrollY > 10) {
            header.classList.add('scrolled');
        } else {
            header.classList.remove('scrolled');
        }
    });
}

export function addProductToCart({name, price, img, qty = 1, openDrawer = true, stock = null, id = null, code = null, brand = null,
                                  country = null, type = null, oldPrice = null, description = null, active = null, category = null,
                                  subcategory = null }) {
    let cart = JSON.parse(localStorage.getItem("cart")) || [];

    const existing = cart.find(p => p.name === name);

    // Calcular cantidad total que tendría el producto
    const newQty = existing ? existing.qty + qty : qty;

    // VALIDAR STOCK - si hay stock definido y la nueva cantidad excede el stock
    if (stock !== null && newQty > stock) {
        Toastify({
            text: `No hay suficiente stock. Máximo disponible: ${stock} unidades`,
            duration: 3000,
            close: true,
            gravity: "top",
            position: "right",
            stopOnFocus: true,
            style: {
                background: "linear-gradient(to right, #ff416c, #ff4b2b)"
            }
        }).showToast();
        return; // No agregar al carrito
    }

    if (existing) {
        existing.qty += qty;  // 🔥 ahora suma la cantidad correcta
    } else {
        cart.push({
          id,
          code,
          name,
          brand,
          country,
          type,
          price,
          oldPrice,
          description,
          category,
          subcategory,
          img,
          qty,
          stock,
          active
        });
    }

    localStorage.setItem("cart", JSON.stringify(cart));

    // Toast de confirmación
    Toastify({
        text: `${qty} x ${name} agregado al carrito`,
        duration: 2000,
        close: true,
        gravity: "top",
        position: "right",
        stopOnFocus: true,
        style: {
            background: "linear-gradient(to right, #00b09b, #96c93d)"
        }
    }).showToast();

    // Disparamos evento para que initCart refresque la UI
    document.dispatchEvent(new CustomEvent("cartUpdated", { detail: { openDrawer } }));
}

// flag global para evitar duplicación
let cartInitialized = false;
export function initCart() {

    if (cartInitialized) {
        return;
    }

    cartInitialized = true;

    const drawer = document.getElementById("cart-drawer");
    const overlay = document.getElementById("cart-overlay");
    const closeBtn = document.getElementById("cart-close");
    const cartList = document.getElementById("cart-list");
    const subtotalEl = document.getElementById("cart-subtotal");
    const cartCount = document.getElementById("cart-count");
    const cartIcon = document.getElementById("cart-icon");
    const seguirComprandoBtn = document.getElementById("seguir-comprando");

    // Footer
    const cartFooter = document.querySelector(".cart-footer");
    const subtotalContainer = cartFooter.querySelector(".cart-subtotal");
    const btnSeguir = cartFooter.querySelector("button:nth-child(2)");
    const btnFinalizar = cartFooter.querySelector("button:nth-child(3)");

    // Botón cerrar dinámico
    let btnCerrar = document.createElement("button");
    btnCerrar.className = "btn-primary";
    btnCerrar.textContent = "Cerrar";
    btnCerrar.addEventListener("click", closeCart);

    let cart = JSON.parse(localStorage.getItem("cart")) || [];

    // Refrescar UI al cargar
    updateCart();

    // ESCUCHAR cuando otro módulo agregue algo al carrito
    document.addEventListener("cartUpdated", (e) => {
        cart = JSON.parse(localStorage.getItem("cart")) || [];
        updateCart();
        // Si quien disparó quiere abrir el drawer, lo hacemos
        if (e?.detail?.openDrawer) openCart();
    });

    // EVENT DELEGATION - Un solo listener para toda la página
    document.addEventListener('click', (e) => {
        // Verificar si se hizo click en el ícono del carrito o en un elemento dentro de él
        if (e.target.classList.contains('ri-shopping-cart-line') ||
            e.target.closest('.ri-shopping-cart-line')) {

            const icon = e.target.classList.contains('ri-shopping-cart-line')
                ? e.target
                : e.target.closest('.ri-shopping-cart-line');

            const card = icon.closest(".card");
            if (card) {
                const name = card.querySelector(".description").textContent.trim();
                const price = parseInt(card.getAttribute("data-price"));
                const img = card.querySelector("img").src;
                const stock = parseInt(card.getAttribute("data-stock")) || null;
                const id = card.getAttribute("data-id");
                const code = card.getAttribute("data-code");
                const brand = card.getAttribute("data-brand");
                const country = card.getAttribute("data-country");
                const type = card.getAttribute("data-type");
                const oldPrice = card.getAttribute("data-oldprice");
                const description = card.getAttribute("data-description");
                const active = card.getAttribute("data-active");
                const category = card.getAttribute("data-category");
                const subcategory = card.getAttribute("data-subcategory");

                // Llamamos la función pública que ya maneja localStorage, toast y evento
                addProductToCart({ id, code, name, brand, country, type, price, oldPrice, stock, description, active,
                                category, subcategory, img, openDrawer: true });

                // Prevenir el comportamiento por defecto y stop propagation
                e.preventDefault();
                e.stopPropagation();
            }
        }
    });

    // Abrir y cerrar drawer
    function openCart() {
        drawer.classList.add("is-open");
        overlay.classList.add("is-open");
    }

    function closeCart() {
        drawer.classList.remove("is-open");
        overlay.classList.remove("is-open");
    }

    closeBtn.addEventListener("click", closeCart);
    overlay.addEventListener("click", closeCart);
    cartIcon.addEventListener("click", openCart);

    // Cerrar el carrito cuando quiera seguir comprando
    if (seguirComprandoBtn) {
        seguirComprandoBtn.addEventListener("click", closeCart);
    }

    // Guardar en localStorage
    function saveCart() {
        localStorage.setItem("cart", JSON.stringify(cart));
    }

    // Actualizar contador del header
    function updateCartCount() {
        const totalItems = cart.reduce((acc, p) => acc + p.qty, 0);
        cartCount.textContent = totalItems > 0 ? totalItems : "0";
    }

    // Actualizar UI del carrito
    function updateCart() {
        cartList.innerHTML = "";
        let subtotal = 0;

        if (cart.length === 0) {
            cartList.innerHTML = `<p class="empty-cart">No hay productos en el carrito.</p>`;

            // Ocultar subtotal cuando no haya productos
            subtotalContainer.style.display = "none";

            // Ocultar botones de compra
            btnSeguir.style.display = "none";
            btnFinalizar.style.display = "none";

            // Agregar botón cerrar si no está
            if (!cartFooter.contains(btnCerrar)) {
                cartFooter.appendChild(btnCerrar);
            }
        } else {
            cart.forEach((item, index) => {
                subtotal += parseInt(item.price) * item.qty;

                const li = document.createElement("li");
                li.className = "cart-item";
                li.innerHTML = `
                    <img src="${item.img}" alt="">
                    <div class="info">
                        <p class="name">${item.name}</p>
                        <div class="quantity-control" data-index="${index}">
                            <button class="minus">−</button>
                            <span class="qty">${item.qty}</span>
                            <button class="plus">+</button>
                        </div>
                        <span class="price">$${parseInt(item.price).toLocaleString('es-CO')}</span>
                    </div>
                    <button class="remove" data-index="${index}">
                        <i class="ri-delete-bin-6-line"></i>
                    </button>
                `;
                cartList.appendChild(li);
            });

            subtotalEl.textContent = "$" + parseInt(subtotal).toLocaleString('es-CO');

            // Mostrar subtotal solo si hay productos
            subtotalContainer.style.display = "flex";

            // Mostrar botones de compra
            btnSeguir.style.display = "block";
            btnFinalizar.style.display = "block";

            // Eliminar botón cerrar si existe
            if (cartFooter.contains(btnCerrar)) {
                cartFooter.removeChild(btnCerrar);
            }

            // Eventos de + y - (también podrías usar event delegation aquí)
            document.querySelectorAll(".quantity-control").forEach(control => {
                const i = control.dataset.index;

                // Botón menos
                control.querySelector(".minus").addEventListener("click", () => {
                    if (cart[i].qty > 0) {
                        cart[i].qty--;
                    }

                    if (cart[i].qty === 0) {
                        cart.splice(i, 1);
                    }

                    saveCart();
                    updateCart();
                });

                // Botón más
                control.querySelector(".plus").addEventListener("click", () => {
                    if (cart[i].stock !== null && cart[i].qty >= cart[i].stock) {
                        Toastify({
                            text: `No hay más stock disponible. Máximo: ${cart[i].stock} unidades`,
                            duration: 3000,
                            close: true,
                            gravity: "top",
                            position: "right",
                            stopOnFocus: true,
                            style: {
                                background: "linear-gradient(to right, #ff416c, #ff4b2b)"
                            }
                        }).showToast();
                        return;
                    }
                    cart[i].qty++;
                    saveCart();
                    updateCart();
                });
            });

            // Eliminar producto manualmente
            document.querySelectorAll(".cart-item .remove").forEach(btn => {
                btn.addEventListener("click", e => {
                    const i = e.currentTarget.dataset.index;
                    cart.splice(i, 1);
                    saveCart();
                    updateCart();
                });
            });
        }

        // Siempre actualizar contador del header
        updateCartCount();
    }
}

// flag global para evitar duplicación
let heartInitialized = false;
export function inicialHeart() {

    if (heartInitialized) {
        return;
    }
    heartInitialized = true;

    const favCount = document.getElementById("fav-count");

    // Recuperar productos guardados
    let favoritos = JSON.parse(localStorage.getItem("favoritos")) || [];
    let count = favoritos.length;
    favCount.textContent = count;

    // Seleccionar todos los íconos de corazón
    const addToFavCount = document.querySelectorAll(".card-icons .ri-heart-line");

    addToFavCount.forEach(icon => {
        icon.addEventListener("click", () => {
            const card = icon.closest(".card");

            // Extraer datos del producto
            const producto = {
                imagen: card.querySelector("img").src,
                nombre: card.querySelector(".description").textContent.trim(),
                precio: card.querySelector(".price").textContent.trim(),
                estado: "En Stock"
            };

            // Evitar duplicados
            if (!favoritos.some(p => p.nombre === producto.nombre)) {
                favoritos.push(producto);

                // Actualizar contador
                count = favoritos.length;
                favCount.textContent = count;

                // Guardar en localStorage
                localStorage.setItem("favoritos", JSON.stringify(favoritos));

                Toastify({
                    text: "Agregado a favoritos",
                    duration: 2000,
                    style: {
                        background: "linear-gradient(to right, #ff416c, #ff4b2b)",
                    }
                }).showToast();
            } else {
                // Opcional: Mensaje si ya está en favoritos
                Toastify({
                    text: "Ya está en favoritos",
                    duration: 2000,
                    style: {
                        background: "linear-gradient(to right, #ff9900, #ff6600)",
                    }
                }).showToast();
            }
        });
    });
}

export function rederigirFav() {
    document.getElementById("go-fav").addEventListener("click", () => {
        window.location.href = "/favorito";
    });
}

export function finalizarCompra() {
    const btnFinalizarcompra = document.getElementById("finalizar-compra");

    if (btnFinalizarcompra) {
        btnFinalizarcompra.addEventListener("click", () => {
            // Verificar si hay productos en el carrito
            const cart = JSON.parse(localStorage.getItem("cart")) || [];

            if (cart.length === 0) {
                Swal.fire({
                    title: "Carrito vacío",
                    text: "Agrega productos al carrito antes de finalizar la compra",
                    icon: "warning",
                    confirmButtonText: "Entendido",
                    customClass: {
                        title: 'swal-title',
                        popup: 'swal-popup'
                    }
                });
                return;
            }

            // Redirigir a la página del carrito
            window.location.href = "/carrito";
        });
    }
}

export function desplegablePerfil() {
    /* Menú desplegable del perfil - Versión corregida */

    // Buscar ambos menús (autenticado y no autenticado)
    const subMenuAutenticado = document.getElementById("SubMenu");
    const subMenuAnonimo = document.getElementById("SubMenu1");

    // Seleccionar correctamente el ícono de usuario (está dentro del span con clase icon__item)
    const profileIcon = document.querySelector(".icon__item .ri-user-line");

    if (profileIcon) {
        profileIcon.addEventListener("click", function (e) {
            e.stopPropagation();

            // Cerrar el otro menú si está abierto
            if (subMenuAutenticado && subMenuAnonimo) {
                if (subMenuAutenticado.classList.contains("open__menu")) {
                    subMenuAutenticado.classList.remove("open__menu");
                }
                if (subMenuAnonimo.classList.contains("open__menu")) {
                    subMenuAnonimo.classList.remove("open__menu");
                }
            }

            // Abrir el menú correspondiente
            if (subMenuAutenticado) {
                subMenuAutenticado.classList.toggle("open__menu");
            }
            if (subMenuAnonimo) {
                subMenuAnonimo.classList.toggle("open__menu");
            }
        });

        // Cerrar menú al hacer clic fuera
        document.addEventListener("click", function (e) {
            const isClickInsideMenu =
                (subMenuAutenticado && subMenuAutenticado.contains(e.target)) ||
                (subMenuAnonimo && subMenuAnonimo.contains(e.target)) ||
                (profileIcon && profileIcon.contains(e.target));

            if (!isClickInsideMenu) {
                if (subMenuAutenticado) subMenuAutenticado.classList.remove("open__menu");
                if (subMenuAnonimo) subMenuAnonimo.classList.remove("open__menu");
            }
        });
    }
}

export function carruselProductos() {
    // mostrar los productos con carrusel
    document.querySelectorAll(".flex").forEach(carrusel => {
        const track = carrusel.querySelector(".flex__productos-track");
        const prevBtn = carrusel.querySelector(".arrow--left");
        const nextBtn = carrusel.querySelector(".arrow--right");

        if (!track || !prevBtn || !nextBtn) return;

        const cardWidth = 300; // ancho de cada card
        const gap = 40;        // espacio entre cards
        const visibles = 4;    // cuántos se muestran a la vez

        let posicion = 0;
        const totalProductos = track.querySelectorAll(".card").length;
        const maxPosicion = (totalProductos - visibles) * (cardWidth + gap);

        const paso = visibles * (cardWidth + gap); // mueve 4 productos

        nextBtn.addEventListener("click", () => {
            if (posicion < maxPosicion) {
                posicion += paso;
                if (posicion > maxPosicion) posicion = maxPosicion; // no pasar límite
                track.style.transform = `translateX(-${posicion}px)`;
            }
        });

        prevBtn.addEventListener("click", () => {
            if (posicion > 0) {
                posicion -= paso;
                if (posicion < 0) posicion = 0; // no pasar inicio
                track.style.transform = `translateX(-${posicion}px)`;
            }
        });
    });
}

// flag global para evitar duplicación
let viewInitialized = false;
export function verProductos() {
    if (viewInitialized) {
        return;
    }
    viewInitialized = true;

    // event delegation - Un solo listener para toda la página
    document.addEventListener('click', (e) => {
        // Verificar si se hizo click en el ícono "Ver" o en un elemento dentro de él
        if (e.target.classList.contains('ri-eye-line') || e.target.closest('.ri-eye-line')) {

            const icon = e.target.classList.contains('ri-eye-line')
                ? e.target
                : e.target.closest('.ri-eye-line');
            const card = icon.closest(".card");

            if (card) {
                const product = {
                    id: card.getAttribute("data-id"),
                    code: card.getAttribute("data-code"),
                    name: card.querySelector(".description").textContent.trim(),
                    brand: card.getAttribute("data-brand"),
                    country: card.getAttribute("data-country"),
                    type: card.getAttribute("data-type"),
                    price: parseInt(card.getAttribute("data-price")),
                    oldPrice: card.getAttribute("data-oldprice") ? parseInt(card.getAttribute("data-oldprice")) : null,
                    stock: card.getAttribute("data-stock") ? parseInt(card.getAttribute("data-stock")) : null,
                    description: card.getAttribute("data-description"),
                    active: card.getAttribute("data-active") === "true",
                    category: card.getAttribute("data-category"),
                    subcategory: card.getAttribute("data-subcategory"),
                    image: card.querySelector("img").src
                };

                // almacenar en localStore
                localStorage.setItem("selectedProduct", JSON.stringify(product));

                // redirigir a la pagina de ver
                window.location.href = "/ver";

                e.preventDefault();
                e.stopPropagation();
            }
        }
    });
}

export function toggleMenu() {
    const menu = document.querySelector(".header__navbar");
    const openBtn = document.getElementById("menu-toggle");
    const closeBtn = document.getElementById("close-menu");

    // Usar optional chaining para evitar errores
    openBtn?.addEventListener("click", () => menu?.classList.add("active"));
    closeBtn?.addEventListener("click", () => menu?.classList.remove("active"));
}

document.addEventListener('DOMContentLoaded', () => {

    if (!localStorage.getItem("mayorDeEdadAceptado")) {
        Swal.fire({
            html: `
        <div class="contenedor-imagen-modal">
            <img src="/Assets/Img/Logos/costaoroimport.png"
            alt="Mayor de edad"
            class="mi-imagen-modal">
        </div>
        <h2 class="swal2-title">¿ Eres Mayor De Edad ?</h2>
        <p class="texto-advertencia">
            "Prohíbese El Expendio De Bebidas Embriagantes A Menores De Edad"<br>
            "El Exceso De Alcohol Es Perjudicial Para La Salud"
        </p>
        `,
            showCancelButton: true,
            confirmButtonText: 'Sí, Soy Mayor De Edad',
            cancelButtonText: 'No, Soy Menor De Edad',
            allowOutsideClick: false,
            allowEscapeKey: false,
            backdrop: `rgba(0,0,0,0.8)`,
            width: '1000px',
            customClass: {
                popup: 'swal-popup'
            }
        }).then((result) => {
            if (result.isConfirmed) {
                // Guardamos en localStorage que ya aceptó
                localStorage.setItem("mayorDeEdadAceptado", "true");

            } else {
                // Si no acepta, lo manda a Google
                window.location.href = "https://www.google.com";
            }
        });
    }

    //desplegar menu del usuario
    desplegablePerfil();

    //rederigir a Favorito
    rederigirFav();

    //Llamamos a la funcion
    activarGlassmorphism();

    // Menú hamburguesa
    toggleMenu();

    //Carrusel del inicio - ACTUALIZADO con la versión del primer archivo
    const slides = document.querySelectorAll(".carousel__slide");
    const prevBtn = document.querySelector("[data-direction='prev']");
    const nextBtn = document.querySelector("[data-direction='next']");
    const dotsContainer = document.querySelector(".carousel__dots");
    const heroCarousel = document.querySelector(".hero__carousel");
    let currentSlide = 0;
    let autoPlayInterval;

    // Crear los puntos dinámicamente según el número de slides
    if (slides.length > 0 && dotsContainer) {
        slides.forEach((_, index) => {
            const dot = document.createElement("span");
            dot.classList.add("carousel__dot");
            dot.dataset.index = index;
            dotsContainer.appendChild(dot);
        });

        const dots = document.querySelectorAll(".carousel__dot");

        // Mostrar slide por índice
        function showSlide(index) {
            slides.forEach((slide, i) => {
                slide.classList.toggle("active", i === index);
                dots[i]?.classList.toggle("active", i === index);
            });

            const activeSlide = slides[index];
            const theme = activeSlide.getAttribute("data-theme");

            // Cambiar fondo o estilos de tema según el data-theme
            if (heroCarousel) {
                heroCarousel.classList.remove("hero--paulaner", "hero--tequila", "hero--budweiser", "hero--whiskys", "hero--packmixtos");
                heroCarousel.classList.add(`hero--${theme}`);
            }

            currentSlide = index;
        }

        // Funciones para avanzar y retroceder
        function nextSlide() {
            const nextIndex = (currentSlide + 1) % slides.length;
            showSlide(nextIndex);
        }

        function prevSlide() {
            const prevIndex = (currentSlide - 1 + slides.length) % slides.length;
            showSlide(prevIndex);
        }

        // Autoplay
        function startAutoplay() {
            autoPlayInterval = setInterval(nextSlide, 6000);
        }

        function stopAutoplay() {
            clearInterval(autoPlayInterval);
        }

        // Eventos de botones
        if (nextBtn) {
            nextBtn.addEventListener("click", () => {
                nextSlide();
                stopAutoplay();
                startAutoplay();
            });
        }

        if (prevBtn) {
            prevBtn.addEventListener("click", () => {
                prevSlide();
                stopAutoplay();
                startAutoplay();
            });
        }

        // Eventos de los puntos
        dots.forEach(dot => {
            dot.addEventListener("click", () => {
                const index = parseInt(dot.dataset.index);
                showSlide(index);
                stopAutoplay();
                startAutoplay();
            });
        });

        // Inicialización
        showSlide(0);
        startAutoplay();
    }

    // Duplicación de logos (solo si existe en la vista) - ACTUALIZADO
    const slider = document.querySelector('.logos__slide');
    if (slider && (!slider.nextElementSibling || !slider.nextElementSibling.classList.contains('logos__slide'))) {
        const clone = slider.cloneNode(true);
        slider.parentElement.appendChild(clone);
    }

    //invocar el iniciar carrito y corazon
    inicialHeart();

    initCart();

    //finalizar compra
    finalizarCompra();

    // mandar los productos a la pagina ver
    verProductos();

    // mostrar los productos con carrusel
    carruselProductos();

    //cambiar anio del footer automaticamente
    document.getElementById("anio__pagina").textContent = new Date().getFullYear();

});