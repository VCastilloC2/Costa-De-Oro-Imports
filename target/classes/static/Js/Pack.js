import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, verProductos, toggleMenu } from "./main.js";

document.addEventListener('DOMContentLoaded', () => {

    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    finalizarCompra();

    verProductos();

    toggleMenu();

    //filtrar los productos
    // Configuración
    const productsPerPage = 16;
    const cards = document.querySelectorAll('.card');
    const pageNumbersContainer = document.querySelector('.pagination__Numbers');
    const prevBtn = document.getElementById('prevBtn');
    const nextBtn = document.getElementById('nextBtn');

    let currentPage = 1;
    let totalPages = Math.ceil(cards.length / productsPerPage);

    // Inicializar paginación
    function initializePagination() {
        createPageNumbers();
        showPage(1);
        updateButtons();
    }

    // Crear números de página
    function createPageNumbers() {
        pageNumbersContainer.innerHTML = '';

        for (let i = 1; i <= totalPages; i++) {
            const pageNumber = document.createElement('div');
            pageNumber.className = 'button__pagination';
            pageNumber.textContent = i;
            pageNumber.addEventListener('click', () => {
                showPage(i);
            });
            pageNumbersContainer.appendChild(pageNumber);
        }
    }

    // Mostrar página específica - CORREGIDO
    function showPage(page) {
        currentPage = page;

        // Ocultar todos los productos
        cards.forEach(card => {
            card.classList.remove('active-page');
        });

        // Calcular rango de productos a mostrar
        const startIndex = (page - 1) * productsPerPage;
        const endIndex = startIndex + productsPerPage;

        // Mostrar productos de la página actual
        for (let i = startIndex; i < endIndex && i < cards.length; i++) {
            cards[i].classList.add('active-page');
        }

        // Actualizar botones activos
        updateActivePage();
        updateButtons();
    }

    // Actualizar página activa
    function updateActivePage() {
        const pageButtons = document.querySelectorAll('.button__pagination');
        pageButtons.forEach((button, index) => {
            if (index + 1 === currentPage) {
                button.classList.add('active');
            } else {
                button.classList.remove('active');
            }
        });
    }

    // Actualizar estado de los botones
    function updateButtons() {
        prevBtn.style.display = currentPage === 1 ? 'none' : 'block';
        nextBtn.style.display = currentPage === totalPages ? 'none' : 'block';
    }

    // Event listeners
    prevBtn.addEventListener('click', () => {
        if (currentPage > 1) {
            showPage(currentPage - 1);
        }
    });

    nextBtn.addEventListener('click', () => {
        if (currentPage < totalPages) {
            showPage(currentPage + 1);
        }
    });

    // Inicializar
    if (cards.length > 0) {
        initializePagination();
    }

});