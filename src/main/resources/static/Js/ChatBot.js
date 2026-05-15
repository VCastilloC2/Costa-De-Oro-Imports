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
// MARKDOWN
// ─────────────────────────────────────────
function renderMarkdown(text) {

    return DOMPurify.sanitize(
        marked.parse(text)
    );
}

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

        await fetchAI(text);

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
async function fetchAI(message) {

    const bubble = appendBotMessage();

    const response = await fetch(API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            message
        })
    });

    if (!response.ok) {
        throw new Error("Error HTTP");
    }

    const text = await response.text();

    bubble.innerHTML = renderMarkdown(text);

    scrollToBottom();
}

// ─────────────────────────────────────────
// BURBUJA
// ─────────────────────────────────────────
function appendBotMessage() {

    const row = document.createElement("div");

    row.className = "msg bot";

    const avatar = document.createElement("div");

    avatar.className = "msg-avatar";

    avatar.innerHTML =
        '<i class="ri-robot-2-line"></i>';

    const bubble = document.createElement("div");

    bubble.className = "bubble";

    row.appendChild(avatar);

    row.appendChild(bubble);

    chatBody.appendChild(row);

    scrollToBottom();

    return bubble;
}

// ─────────────────────────────────────────
// MENSAJE NORMAL - VERSIÓN CORREGIDA
// ─────────────────────────────────────────
function appendMessage(role, text, isError = false) {

    hideEmptyState();

    const row = document.createElement("div");

    row.className = `msg ${role}`;

    const avatar = document.createElement("div");

    avatar.className =
        role === "bot"
            ? "msg-avatar"
            : "msg-avatar user-av";

    avatar.innerHTML =
        role === "bot"
            ? '<i class="ri-robot-2-line"></i>'
            : '<i class="ri-user-3-line"></i>';

    const bubble = document.createElement("div");

    bubble.className =
        `bubble${isError ? " error" : ""}`;

    bubble.innerHTML = renderMarkdown(text);

    row.appendChild(avatar);

    row.appendChild(bubble);

    chatBody.appendChild(row);

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
    chatBody.scrollTop =
        chatBody.scrollHeight;
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