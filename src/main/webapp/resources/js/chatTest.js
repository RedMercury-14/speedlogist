//window.addEventListener("load", e=>chatUnit.init);

let startbox;
let chatbox;
let nameInput;
let startBtn;
let textArea;
let chatMessageContainer;
var ws;
var name1;
init()
function init() {
	startbox = document.querySelector(".start");
	chatbox = document.querySelector(".chat");
	nameInput = startbox.querySelector("input");
	startBtn = document.querySelector(".btn");
	textArea = chatbox.querySelector("textarea");
	//chatMessageContainer = chatbox.querySelector(".messeges");
	
};


startBtn.addEventListener("mousedown", () => {
	var name = nameInput.value;
	ws = new WebSocket("ws://localhost:8080/speedlogist/chat");
	ws.onopen = () => this.onOpenSock();
	ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
	ws.onclose = (e) => this.onClose();		
	startbox.style.display = "none";
	chatbox.style.display = "block";
	
});
textArea.addEventListener("keyup", (e)=>{
	if(e.ctrlKey && e.keyCode === 13){
		e.preventDefault();
		send();
	}
}) // метод обработки нажатия CTRL+ENTER!
document.querySelector('#send').addEventListener("mousedown", (e) =>{
	e.preventDefault();
		send();
})
function send(){
	sendMessage({
		fromUser : this.name.value,
		text : textArea.value,
		status : "1"
	})
};


function onOpenSock(){	
};

function onMessage(msg){
	let msgBlock = document.createElement("div");
	msgBlock.className = "message";
	let fromBlock = document.createElement("div");
	fromBlock.className = "fromUser";
	fromBlock.innerText = msg.fromUser;
	let textBlock = document.createElement("div");
	textBlock.className = "text";
	textBlock.innerText = msg.text;	
	msgBlock.appendChild(fromBlock);
	msgBlock.appendChild(textBlock);	
	chatbox.appendChild(msgBlock);
};

function onClose(){
	
};

function sendMessage (message){
	console.log(message);
	ws.send(JSON.stringify(message));
}