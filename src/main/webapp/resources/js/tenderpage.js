var idRoute = document.querySelector('input[name=id]').value;
let ws = new WebSocket("ws://localhost:8080/speedlogist/chat");
console.log(ws);
ws.onopen = () => this.onOpenSock();
ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
ws.onclose = (e) => this.onClose();

function send() {
	sendMessage({
		text: document.querySelector('input[name=cost]').value,
		idRoute: idRoute,
		status: "1"
	})
};
console.log(idRoute);
document.querySelector('.agree').addEventListener("mousedown", ()=> {
	send();
})




function onOpenSock() {
};

function onMessage(msg) {
	console.log(msg);
};

function onClose() {

};

function sendMessage(message) {
	console.log(message);
	ws.send(JSON.stringify(message));
}