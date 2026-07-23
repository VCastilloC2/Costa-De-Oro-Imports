import { activarGlassmorphism, inicialHeart, initCart, rederigirFav, finalizarCompra, toggleMenu, configurarLogout} from "./main.js";

document.addEventListener('DOMContentLoaded', () => {
    activarGlassmorphism();

    inicialHeart();

    initCart();

    rederigirFav();

    finalizarCompra();

    toggleMenu();

    // Cerrar sesión
    configurarLogout();

});