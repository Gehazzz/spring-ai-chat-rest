document.addEventListener("DOMContentLoaded", function() {
    const sendButton = document.getElementById("send-button");
    const chatInput = document.getElementById("chat-input");
    const messagesContainer = document.getElementById("messages");

    sendButton.addEventListener("click", function() {
        const prompt = chatInput.value;
        if (!prompt) return;
        chatInput.value = "";

        // Добавляем сообщение пользователя в чат
        const userDiv = document.createElement("div");
        userDiv.className = "message user";
        userDiv.innerHTML = `<img src="/images/user.png" alt="User"><div class="bubble">${prompt}</div>`;
        messagesContainer.appendChild(userDiv);

        const pathParts = window.location.pathname.split("/");
        const chatId = pathParts[pathParts.length - 1];
        const model = document.getElementById("model").value;
        const url = `/chat-stream/${chatId}?prompt=${encodeURIComponent(prompt)}&model=${encodeURIComponent(model)}`;

        const eventSource = new EventSource(url);
        let fullText = "";

        // Создаем блок для ответа AI
        const aiDiv = document.createElement("div");
        aiDiv.className = "message mentor";
        // Добавляем изображение ассистента
        aiDiv.innerHTML = `<img src="/images/mentor.png" alt="Mentor">`;
        // Создаем элемент для содержимого, куда будем вставлять ответ
        const aiBubble = document.createElement("div");
        aiBubble.className = "bubble";
        aiDiv.appendChild(aiBubble);
        messagesContainer.appendChild(aiDiv);

        eventSource.onmessage = function(event) {
            const data = JSON.parse(event.data);
            fullText += data.text;
            // Преобразуем Markdown в HTML (при условии, что marked.js подключен)
            aiBubble.innerHTML = marked.parse(fullText);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        };

        eventSource.onerror = function(e) {
            console.error("Ошибка SSE:", e);
            eventSource.close();
        };
    });
});

document.addEventListener("DOMContentLoaded", function() {
    const sendButton = document.getElementById("send-button");
    const chatInput = document.getElementById("chat-input");
    const messagesContainer = document.getElementById("messages");

    // Преобразуем все загруженные сообщения из Markdown в HTML
    const messageBubbles = messagesContainer.querySelectorAll(".bubble");
    messageBubbles.forEach(bubble => {
        // Берём исходный текст (без HTML-разметки)
        const md = bubble.textContent;
        // Преобразуем Markdown в HTML через marked (убедитесь, что marked подключён)
        bubble.innerHTML = marked.parse(md);
    });

    sendButton.addEventListener("click", function() {
        const prompt = chatInput.value;
        if (!prompt) return;
        chatInput.value = "";

        // Добавляем сообщение пользователя в чат
        const userDiv = document.createElement("div");
        userDiv.className = "message user";
        userDiv.innerHTML = `<img src="/images/user.png" alt="User"><div class="bubble">${prompt}</div>`;
        messagesContainer.appendChild(userDiv);

        // Можно также преобразовать Markdown для отправленного сообщения, если необходимо:
        userDiv.querySelector(".bubble").innerHTML = marked.parse(prompt);

        const pathParts = window.location.pathname.split("/");
        const chatId = pathParts[pathParts.length - 1];
        const model = document.getElementById("model").value;
        const url = `/chat-stream/${chatId}?prompt=${encodeURIComponent(prompt)}&model=${encodeURIComponent(model)}`;

        const eventSource = new EventSource(url);
        let fullText = "";

        // Создаем блок для ответа AI
        const aiDiv = document.createElement("div");
        aiDiv.className = "message mentor";
        aiDiv.innerHTML = `<img src="/images/mentor.png" alt="Mentor">`;
        const aiBubble = document.createElement("div");
        aiBubble.className = "bubble";
        aiDiv.appendChild(aiBubble);
        messagesContainer.appendChild(aiDiv);

        eventSource.onmessage = function(event) {
            const data = JSON.parse(event.data);
            fullText += data.text;
            // Преобразуем полный ответ с Markdown
            aiBubble.innerHTML = marked.parse(fullText);
            messagesContainer.scrollTop = messagesContainer.scrollHeight;
        };

        eventSource.onerror = function(e) {
            console.error("Ошибка SSE:", e);
            eventSource.close();
        };
    });
});
