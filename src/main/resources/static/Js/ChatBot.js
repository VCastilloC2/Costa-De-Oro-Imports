// ── Configuración ──────────────────────────────────────────────
const API_URL = "/api/chat"; // ← endpoint de Spring AI

// ── Referencias DOM ────────────────────────────────────────────
const chatBody = document.getElementById("chatBody");
const userInput = document.getElementById("userInput");
const sendBtn = document.getElementById("sendBtn");
const typingRow = document.getElementById("typingRow");
const emptyState = document.getElementById("emptyState");
const statusDot = document.getElementById("statusDot");
const statusText = document.getElementById("statusText");

// ── Estado ─────────────────────────────────────────────────────
let isLoading = false;

// ── ChatId persistente (mantiene contexto del chat) ──────────────────────────
let chatId = localStorage.getItem("chatId");
if (!chatId) {
    chatId = "user-" + Math.random().toString(36).substring(2);
    localStorage.setItem("chatId", chatId);
}

// ── Auto-resize del textarea ───────────────────────────────────
userInput.addEventListener("input", () => {
    userInput.style.height = "0px"; // 🔥 clave anti-glitch
    userInput.style.height = userInput.scrollHeight + "px";
});

// ── Enter / Shift+Enter ────────────────────────────────────────
userInput.addEventListener("keydown", (e) => {
    if (e.key === "Enter" && !e.shiftKey) {
        e.preventDefault();
        sendMessage();
    }
});

// ── Enviar mensaje ─────────────────────────────────────────────
async function sendMessage() {
    const text = userInput.value.trim();
    if (!text || isLoading) return;

    hideEmptyState();
    appendMessage("user", text);
    clearInput();
    setLoading(true);

    setStatus("connecting"); // 🔥 AQUÍ

    try {
        const reply = await fetchAI(text);
        appendMessage("bot", reply);

        setStatus("online"); // 🔥 SI TODO SALE BIEN
    } catch (err) {
        appendMessage("bot", `⚠️ ${err.message || "Error"}`, true);

        setStatus("offline"); // 🔥 SI FALLA
    } finally {
        setLoading(false);
    }
}

// ── Chequiando la conección ────────────────────────────────────────
async function checkConnection() {
    try {
        setStatus("connecting");

        const res = await fetch(API_URL, {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify({
                message: "ping",
                chatId: "health-check"
            })
        });

        if (!res.ok) throw new Error();

        setStatus("online");
    } catch (e) {
        setStatus("offline");
    }
}

// Ejecutar al iniciar
checkConnection();

// ── Sugerencias rápidas ────────────────────────────────────────
function sendSuggestion(text) {
    userInput.value = text;
    sendMessage();
}

function setStatus(status) {
    statusDot.classList.remove("online", "offline", "connecting");

    if (status === "online") {
        statusDot.classList.add("online");
        statusText.textContent = "En línea";
    }

    if (status === "offline") {
        statusDot.classList.add("offline");
        statusText.textContent = "Sin conexión";
    }

    if (status === "connecting") {
        statusDot.classList.add("connecting");
        statusText.textContent = "Conectando...";
    }
}

// ── Llamada al backend Spring AI ──────────────────────────────
async function fetchAI(message) {
    const response = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
            "Accept": "application/json",
        },
        body: JSON.stringify({
            message,
            chatId // 🔥 aquí va
        }),
    });

    if (!response.ok) {
        const errorText = await response.text().catch(() => "");
        throw new Error(
            errorText
                ? `Error ${response.status}: ${errorText}`
                : `Error del servidor (${response.status})`
        );
    }

    const data = await response.json();

    return (
        data.response ??
        data.content ??
        data.message ??
        data.result ??
        JSON.stringify(data)
    );
}

// ── Agregar burbuja de mensaje ─────────────────────────────────
function appendMessage(role, text, isError = false) {
    const isBot = role === "bot";

    const row = document.createElement("div");
    row.className = `msg ${role}`;

    const avatar = document.createElement("div");
    avatar.className = isBot ? "msg-avatar" : "msg-avatar user-av";
    avatar.innerHTML = isBot
        ? '<i class="ri-robot-2-line"></i>'
        : '<i class="ri-user-3-line"></i>';

    const bubble = document.createElement("div");
    bubble.className = `bubble${isError ? " error" : ""}`;
    bubble.innerHTML = marked.parse(text);

    row.appendChild(avatar);
    row.appendChild(bubble);

    chatBody.insertBefore(row, typingRow);
    scrollToBottom();
}

// ── Loading / typing indicator ─────────────────────────────────
function setLoading(state) {
    isLoading = state;
    sendBtn.disabled = state;
    typingRow.style.display = state ? "flex" : "none";

    if (state) scrollToBottom();
}

// ── Limpiar chat ───────────────────────────────────────────────
function clearChat() {
    Array.from(chatBody.children).forEach((el) => {
        if (el.id !== "typingRow" && el.id !== "emptyState") {
            el.remove();
        }
    });

    showEmptyState();
}

// ── Empty state ────────────────────────────────────────────────
function hideEmptyState() {
    if (emptyState) emptyState.style.display = "none";
}

function showEmptyState() {
    if (emptyState) emptyState.style.display = "";
}

// ── Scroll ────────────────────────────────────────────────────
function scrollToBottom() {
    requestAnimationFrame(() => {
        chatBody.scrollTop = chatBody.scrollHeight;
    });
}

// ── Focus automático ───────────────────────────────────────────
userInput.focus();

// ── Limpiar input ──────────────────────────────────────────────
function clearInput() {
    userInput.value = "";
    userInput.style.height = "auto";
}

function toggleChat() {
    const chat = document.getElementById("chatContainer");
    chat.classList.toggle("active");
}
