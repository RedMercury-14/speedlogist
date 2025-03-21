
document.addEventListener('DOMContentLoaded', () => {

	const messageInput = document.getElementById("message-input")
	const sendBtn = document.getElementById("send-btn")
	sendBtn.addEventListener("click", (e) => sendMessage(messageInput));
    messageInput.addEventListener("keypress", (e) => {
        if (e.key === "Enter" && !e.shiftKey) {
            e.preventDefault();
            sendMessage(messageInput);
        }
    });
})

function sendMessage(inputField) {
	let messageText = inputField.value.trim();

	if (messageText === "") return;

	addMessage(messageText, "user-message");

	// Имитация ответа сервера
	setTimeout(() => {
		addMessage("Это ответ сервера: " + messageText, "server-message");
	}, 1000);

	inputField.value = "";
}


function addMessage(text, className) {
	const chatBox = document.getElementById("chat-box");
	const messageDiv = document.createElement("div");
	messageDiv.classList.add("message", className, "d-inline-block");
	messageDiv.innerText = text;
	
	const wrapperDiv = document.createElement("div");
	wrapperDiv.classList.add("d-flex", className === "user-message" ? "justify-content-end" : "justify-content-start");
	wrapperDiv.appendChild(messageDiv);

	chatBox.appendChild(wrapperDiv);
	chatBox.scrollTop = chatBox.scrollHeight;
}