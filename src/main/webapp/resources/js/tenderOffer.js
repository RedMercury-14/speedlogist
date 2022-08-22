let ws = new WebSocket("ws://localhost:8080/speedlogist/chat");
ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
let idRoute = document.querySelector('#idRoute').value;
setTimeout(()=>onButton(),500);
$.getJSON(`../../../api/info/message/routes/${idRoute}`, function(data) {
	$.each(data, function(key, val) {
		let row = document.createElement("tr");
	let td1 = document.createElement("td");
	td1.innerText = "Предложение от: " + val.companyName;
	let td2 = document.createElement("td");
	td2.id = "cost";
	td2.innerText = val.text + " EUR";
	let td3 = document.createElement("td");
	let button = document.createElement("input");
	button.type = 'button';
	button.id = val.fromUser;
	button.className = val.text;
	button.value = "Принять";
	let input = document.createElement("input");
	input.type = 'hidden';
	input.id = 'id'
	input.innerHTML = val.idRoute;
	td3.appendChild(input);	
	td3.appendChild(button);
	row.appendChild(td1);
	row.appendChild(td2);
	row.appendChild(td3);
	document.querySelector("#sort").appendChild(row);
	
	})
});

function onMessage(msg) {
	let row = document.createElement("tr");
	let td1 = document.createElement("td");
	td1.innerText = "Предложение от: " + msg.companyName;
	let td2 = document.createElement("td");	
	td2.innerText = msg.text + " EUR";
	td2.id = "cost";
	let td3 = document.createElement("td");
	let button = document.createElement("input");
	button.type = 'button';
	button.id = msg.fromUser;
	button.className = msg.text;
	button.value = "Принять";
	let input = document.createElement("input");
	input.type = 'hidden';
	input.id = 'id'
	input.innerHTML = msg.idRoute;
	td3.appendChild(input);	
	td3.appendChild(button);
	row.appendChild(td1);
	row.appendChild(td2);
	row.appendChild(td3);
	document.querySelector("#sort").appendChild(row);
	row.querySelector("input[type=button]").addEventListener("mousedown", event =>{
			console.log(event.target.id);	
			console.log(event.target.className);
			confrom(event.target.id, event.target.className, idRoute)	
		})
	
};

function onButton(){
	var routeItem = document.querySelectorAll('tr');
	for (i = 0; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];		
		routeItemI.querySelector("input[type=button]").addEventListener("mousedown", event =>{
			console.log(event.target.id);	
			console.log(event.target.className);	
			confrom(event.target.id, event.target.className, idRoute)			
		})
		
	}
}

function confrom(login, cost, idRoute){	
	var url = `./confrom?login=${login}&cost=${cost}&idRoute=${idRoute}`
		window.location.href = url;
}