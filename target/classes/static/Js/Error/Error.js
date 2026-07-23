// Crear burbujas dinámicamente - mejorado para más realismo
function createBubbles() {
    const bubblesContainer = document.getElementById("bubbles");
    const bubbleCount = 30; // Aumentado para más densidad y realismo

    for (let i = 0; i < bubbleCount; i++) {
        const bubble = document.createElement("div");
        bubble.classList.add("bubble");

        // Determinar si es burbuja grande o pequeña
        const isLarge = Math.random() > 0.7; // 30% de probabilidad de ser grande
        if (isLarge) {
            bubble.classList.add("large");
        } else {
            bubble.classList.add("small");
        }

        // Posición y tamaño aleatorios mejorados (adaptado al ancho de la jarra)
        const size = Math.random() * 10 + 3; // Tamaños más variados (3-13px)
        const left = Math.random() * 85 + 5; // De 5% a 90% para cubrir mejor el ancho
        const delay = Math.random() * 4; // Delay hasta 4s para más dispersión
        const duration = Math.random() * 3 + 2; // Duración entre 2-5s para variedad en velocidad
        const initialBottom = Math.random() * 30; // Algunas burbujas empiezan un poco más arriba para efecto natural
        const sway = (Math.random() - 0.5) * 20; // Desviación horizontal ligera para trayectorias curvas

        bubble.style.width = `${size}px`;
        bubble.style.height = `${size}px`;
        bubble.style.left = `${left}%`;
        bubble.style.bottom = `${initialBottom}%`; // Posición inicial variable
        bubble.style.animationDelay = `${delay}s`;
        bubble.style.animationDuration = `${duration}s`;
        // Agregar sway personalizado vía variable CSS para translateX en la animación
        bubble.style.setProperty("--sway", `${sway}px`);

        bubblesContainer.appendChild(bubble);
    }
}

// Sincronizar la altura de la espuma con el líquido
function syncFoamWithLiquid() {
    const beerLiquid = document.getElementById("beer-liquid");
    const beerFoam = document.getElementById("beer-foam");

    // Verificar que los elementos existan
    if (!beerLiquid || !beerFoam) return;

    // Observar cambios en la altura del líquido
    const observer = new MutationObserver(function (mutations) {
        mutations.forEach(function (mutation) {
            if (
                mutation.type === "attributes" &&
                mutation.attributeName === "style"
            ) {
                // Obtener la altura actual del líquido
                const liquidHeight = beerLiquid.offsetHeight;
                const pitcherHeight = beerLiquid.parentElement.offsetHeight;

                // Calcular la altura de la espuma en función del líquido
                // Cuando el líquido sube, la espuma se comprime
                const foamHeight = Math.max(
                    10,
                    40 - (liquidHeight / pitcherHeight) * 15
                );
                beerFoam.style.height = `${foamHeight}px`;
            }
        });
    });

    observer.observe(beerLiquid, {
        attributes: true,
        attributeFilter: ["style"],
    });

    // También actualizar en cada frame de animación para mayor precisión
    function updateFoamHeight() {
        const liquidHeight = beerLiquid.offsetHeight;
        const pitcherHeight = beerLiquid.parentElement.offsetHeight;
        const foamHeight = Math.max(10, 40 - (liquidHeight / pitcherHeight) * 15);
        beerFoam.style.height = `${foamHeight}px`;
        requestAnimationFrame(updateFoamHeight);
    }

    updateFoamHeight();
}

// Animación interactiva de la jarra de cerveza
function setupBeerPitcherInteractions() {
    const beerPitcher = document.querySelector(".beer-pitcher");
    const beerLiquid = document.getElementById("beer-liquid");
    const foamLayers = document.querySelectorAll(".foam-layer"); // Pausar también la espuma para consistencia

    // Verificar que los elementos existan antes de agregar event listeners
    if (!beerPitcher) return;

    beerPitcher.addEventListener("mouseenter", function () {
        // Pausar animación del líquido
        if (beerLiquid) {
            beerLiquid.style.animationPlayState = "paused";
        }
        // Pausar todas las burbujas
        document.querySelectorAll(".bubble").forEach((bubble) => {
            bubble.style.animationPlayState = "paused";
        });
        // Pausar capas de espuma para efecto congelado
        foamLayers.forEach((layer) => {
            layer.style.animationPlayState = "paused";
        });
        // Efecto adicional: escala ligera para "agitar" al hover
        beerPitcher.style.transform = "scale(1.05)";
    });

    beerPitcher.addEventListener("mouseleave", function () {
        // Reanudar animación del líquido
        if (beerLiquid) {
            beerLiquid.style.animationPlayState = "running";
        }
        // Reanudar todas las burbujas
        document.querySelectorAll(".bubble").forEach((bubble) => {
            bubble.style.animationPlayState = "running";
        });
        // Reanudar capas de espuma
        foamLayers.forEach((layer) => {
            layer.style.animationPlayState = "running";
        });
        // Restaurar escala
        beerPitcher.style.transform = "scale(1)";
    });
}

// Crear burbujas dinámicamente - versión optimizada
function createBubblesOptimized() {
    const bubblesContainers = document.querySelectorAll(".bubbles");

    bubblesContainers.forEach((container) => {
        const bubbleCount = Math.floor(Math.random() * 10) + 20; // 20-30 burbujas

        for (let i = 0; i < bubbleCount; i++) {
            const bubble = document.createElement("div");
            bubble.classList.add("bubble");

            // Determinar tamaño
            const isLarge = Math.random() > 0.7;
            if (isLarge) bubble.classList.add("large");
            else bubble.classList.add("small");

            // Configuración de la burbuja
            const left = Math.random() * 85 + 5;
            const delay = Math.random() * 4;
            const duration = Math.random() * 3 + 2;
            const initialBottom = Math.random() * 30;
            const sway = (Math.random() - 0.5) * 20;

            Object.assign(bubble.style, {
                left: `${left}%`,
                bottom: `${initialBottom}%`,
                animationDelay: `${delay}s`,
                animationDuration: `${duration}s`,
            });

            bubble.style.setProperty("--sway", `${sway}px`);
            container.appendChild(bubble);
        }
    });
}

// Animación interactiva de la jarra de cerveza
function setupPitcherInteractions() {
    const beerPitchers = document.querySelectorAll(".beer-pitcher");

    beerPitchers.forEach((pitcher) => {
        const beerLiquid = pitcher.querySelector(".beer-liquid");
        const bubbles = pitcher.querySelectorAll(".bubble");
        const foamLayers = pitcher.querySelectorAll(".foam-layer");

        pitcher.addEventListener("mouseenter", () => {
            // Pausar todas las animaciones
            [beerLiquid, ...bubbles, ...foamLayers].forEach((element) => {
                if (element) element.style.animationPlayState = "paused";
            });

            // Efecto visual al hacer hover
            pitcher.style.transform = "scale(1.05)";
        });

        pitcher.addEventListener("mouseleave", () => {
            // Reanudar todas las animaciones
            [beerLiquid, ...bubbles, ...foamLayers].forEach((element) => {
                if (element) element.style.animationPlayState = "running";
            });

            // Restaurar transformación
            pitcher.style.transform = "scale(1)";
        });

        // Click para mostrar información adicional
        pitcher.addEventListener("click", () => {
            const errorType = document.querySelector("title").textContent;
            showBeerInfo(errorType);
        });
    });
}

// Mostrar información con alertas nativas
function showBeerInfo(errorType) {
    const messages = {
        "Error 404 - Página no encontrada": {
            title: "¡Página no encontrada!",
            text: "Esta página se ha evaporado como la espuma de una buena cerveza...",
        },
        "Error 401 - No autorizado": {
            title: "Acceso no autorizado",
            text: "Parece que esta jarra está cerrada con candado. ¿Tienes la llave?",
        },
        "Error 400 - Solicitud incorrecta": {
            title: "Problema de comunicación",
            text: "Tal vez se nos derramó un poco la cerveza. Vuelve a intentarlo.",
        },
        Error: {
            title: "En mantenimiento",
            text: "Estamos trabajando para mejorar tu experiencia. Vuelve pronto.",
        },
    };

    const config = messages[errorType] || messages["Error"];
    alert(`${config.title}\n\n${config.text}`);
}

// Función para mostrar confirmación al intentar volver
function setupBackButton() {
    const backButton = document.querySelector(".button");

    if (backButton) {
        backButton.addEventListener("click", (e) => {
            e.preventDefault();

            if (
                confirm(
                    "¿Volver atrás?\n\nEstás a punto de regresar a la página anterior"
                )
            ) {
                window.history.back();
            }
        });
    }
}

// Inicialización mejorada
document.addEventListener("DOMContentLoaded", function () {
    // Verificar que los elementos existan antes de inicializar
    const bubblesContainer = document.getElementById("bubbles");
    if (bubblesContainer) {
        createBubbles();
    }

    createBubblesOptimized();
    setupBeerPitcherInteractions();
    setupPitcherInteractions();
    setupBackButton();

    // Sincronizar espuma con líquido si los elementos existen
    const beerLiquid = document.getElementById("beer-liquid");
    const beerFoam = document.getElementById("beer-foam");
    if (beerLiquid && beerFoam) {
        syncFoamWithLiquid();
    }
});

// Manejo de errores global
window.addEventListener("error", (e) => {
    console.error("Error capturado:", e.error);
});
