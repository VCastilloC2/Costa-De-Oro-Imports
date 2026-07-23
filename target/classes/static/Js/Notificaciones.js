import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, verProductos, toggleMenu, configurarLogout} from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    finalizarCompra();

    verProductos();

    toggleMenu();

    // Cerrar sesión
    configurarLogout();

});