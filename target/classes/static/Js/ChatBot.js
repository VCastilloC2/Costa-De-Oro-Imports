// ─────────────────────────────────────────
// CONFIG
// ─────────────────────────────────────────
const API_URL = "/api/chat";

// ─────────────────────────────────────────
// DOM
// ─────────────────────────────────────────
const chatBody = document.getElementById("chatBody");
const userInput = document.getElementById("userInput");
const sendBtn = document.getElementById("sendBtn");
const typingRow = document.getElementById("typingRow");

const statusDot = document.getElementById("statusDot");
const statusText = document.getElementById("statusText");

const emptyState = document.getElementById("emptyState");

// ─────────────────────────────────────────
// STATE
// ─────────────────────────────────────────
let isLoading = false;

// ─────────────────────────────────────────
// STATUS INICIAL
// ─────────────────────────────────────────
setStatus("online");

// ─────────────────────────────────────────
// ENTER
// ─────────────────────────────────────────
userInput.addEventListener("keydown", (e) => {

    if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

// ─────────────────────────────────────────
// AUTO RESIZE
// ─────────────────────────────────────────
userInput.addEventListener("input", () => {

    userInput.style.height = "auto";
    userInput.style.height =
        userInput.scrollHeight + "px";
});

// ─────────────────────────────────────────
// ENVIAR MENSAJE
// ─────────────────────────────────────────
async function sendMessage() {

    const text = userInput.value.trim();

    if (!text || isLoading) return;

    hideEmptyState();

    appendMessage("user", text);

    clearInput();

    setLoading(true);

    setStatus("connecting");

    try {

        await fetchAIStream(text);

        setStatus("online");

    } catch (error) {

        appendMessage(
            "bot",
            "⚠️ Error al conectar con la IA.",
            true
        );

        setStatus("offline");

    } finally {

        setLoading(false);
    }
}

// ─────────────────────────────────────────
// STREAM IA - VERSIÓN CORREGIDA (con signos de puntuación)
// ─────────────────────────────────────────
async function fetchAIStream(message) {

    const bubble = appendStreamingMessage();

    let fullResponse = "";

    const response = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            message: message
        })
    });

    if (!response.ok) {
        throw new Error("Error HTTP");
    }

    const reader = response.body.getReader();
    const decoder = new TextDecoder("utf-8");

    while (true) {

        const { value, done } = await reader.read();

        if (done) break;

        const chunk = decoder.decode(value, { stream: true });

        // dividir líneas SSE
        const lines = chunk.split("\n");

        for (const line of lines) {

            if (!line.startsWith("data:")) continue;

            const token = line.replace("data:", "").trim();

            if (!token) continue;

            if (token === "[DONE]") {
                bubble.innerHTML = marked.parse(fullResponse);
                return;
            }

            fullResponse += token + " ";

            requestAnimationFrame(() => {

                bubble.innerHTML =
                    marked.parse(fullResponse);

                scrollToBottom();
            });
        }
    }

    bubble.innerHTML = marked.parse(fullResponse);
}

// ─────────────────────────────────────────
// BURBUJA STREAM - VERSIÓN CORREGIDA
// ─────────────────────────────────────────
function appendStreamingMessage() {
    // ocultar typing mientras aparece mensaje real
    typingRow.style.display = "none";

    const row = document.createElement("div");
    row.className = "msg bot";

    const avatar = document.createElement("div");
    avatar.className = "msg-avatar";
    avatar.innerHTML = '<i class="ri-robot-2-line"></i>';

    const bubble = document.createElement("div");
    bubble.className = "bubble";

    row.appendChild(avatar);
    row.appendChild(bubble);

    // Insertar antes del typingRow también
    if (typingRow && typingRow.parentNode === chatBody) {
        chatBody.insertBefore(row, typingRow);
    } else {
        chatBody.appendChild(row);
    }

    scrollToBottom();

    // Mostrar typing row después por si acaso
    typingRow.style.display = "flex";

    return bubble;
}

// ─────────────────────────────────────────
// MENSAJE NORMAL - VERSIÓN CORREGIDA
// ─────────────────────────────────────────
function appendMessage(role, text, isError = false) {
    // Ocultar empty state si existe
    hideEmptyState();

    const row = document.createElement("div");
    row.className = `msg ${role}`;

    const avatar = document.createElement("div");
    avatar.className = role === "bot" ? "msg-avatar" : "msg-avatar user-av";
    avatar.innerHTML = role === "bot"
        ? '<i class="ri-robot-2-line"></i>'
        : '<i class="ri-user-3-line"></i>';

    const bubble = document.createElement("div");
    bubble.className = `bubble${isError ? " error" : ""}`;
    bubble.innerHTML = marked.parse(text);

    row.appendChild(avatar);
    row.appendChild(bubble);

    // 🔥 CAMBIO IMPORTANTE: Insertar ANTES del typingRow
    // Esto asegura que los mensajes mantengan el orden correcto
    if (typingRow && typingRow.nextSibling) {
        chatBody.insertBefore(row, typingRow);
    } else {
        chatBody.insertBefore(row, typingRow);
    }

    // Asegurar que typingRow siempre esté al final (pero antes del empty state invisible)
    if (typingRow && typingRow.parentNode === chatBody) {
        // typingRow ya está donde debe estar
    }

    scrollToBottom();
}

// ─────────────────────────────────────────
// STATUS
// ─────────────────────────────────────────
function setStatus(status) {

    statusDot.classList.remove(
        "online",
        "offline",
        "connecting"
    );

    switch (status) {

        case "online":

            statusDot.classList.add("online");

            statusText.textContent =
                "En línea";

            break;

        case "offline":

            statusDot.classList.add("offline");

            statusText.textContent =
                "Sin conexión";

            break;

        case "connecting":

            statusDot.classList.add("connecting");

            statusText.textContent =
                "Escribiendo...";

            break;
    }
}

// ─────────────────────────────────────────
// LOADING - VERSIÓN CORREGIDA
// ─────────────────────────────────────────
function setLoading(state) {
    isLoading = state;
    sendBtn.disabled = state;

    if (state) {
        typingRow.style.display = "flex";
    } else {
        typingRow.style.display = "none";
    }
}
// ─────────────────────────────────────────
// CLEAR CHAT
// ─────────────────────────────────────────
function clearChat() {

    Array.from(chatBody.children)
        .forEach((el) => {

            if (
                el.id !== "typingRow" &&
                el.id !== "emptyState"
            ) {
                el.remove();
            }
        });

    showEmptyState();
}

// ─────────────────────────────────────────
// EMPTY STATE
// ─────────────────────────────────────────
function hideEmptyState() {

    if (emptyState) {
        emptyState.style.display = "none";
    }
}

function showEmptyState() {

    if (emptyState) {
        emptyState.style.display = "block";
    }
}

// ─────────────────────────────────────────
// SCROLL
// ─────────────────────────────────────────
function scrollToBottom() {

    requestAnimationFrame(() => {

        chatBody.scrollTop =
            chatBody.scrollHeight;
    });
}

// ─────────────────────────────────────────
// CLEAR INPUT
// ─────────────────────────────────────────
function clearInput() {

    userInput.value = "";

    userInput.style.height = "auto";
}

// ─────────────────────────────────────────
// TOGGLE CHAT
// ─────────────────────────────────────────
function toggleChat() {

    document
        .getElementById("chatContainer")
        .classList.toggle("active");
}

// ─────────────────────────────────────────
// FOCUS
// ─────────────────────────────────────────
userInput.focus();