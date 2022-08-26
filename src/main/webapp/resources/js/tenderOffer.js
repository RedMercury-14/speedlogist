let ws = new WebSocket("ws://localhost:8080/speedlogist/chat");
ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
let idRoute = document.querySelector('#idRoute').value;
setTimeout(() => onButton(null), 500);
$.getJSON(`../../../api/info/message/routes/${idRoute}`, function(data) {
	if (data.length == 0) {
		history();
	}
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
		input.id = 'id';
		input.innerHTML = val.idRoute;
		td3.appendChild(input);
		td3.appendChild(button);
		row.appendChild(td1);
		row.appendChild(td2);
		row.appendChild(td3);
		document.querySelector("#sort").appendChild(row);
	});
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
	//	row.querySelector("input[type=button]").addEventListener("mousedown", event => {
	//		confrom(event.target.id, event.target.className, idRoute)
	//	})
	onButton(row);
};
var mincost = null;
function onButton(row) {	
	console.log("start " + mincost)
	if (row != null) {
		if (mincost == null) {
			mincost = row.querySelector("#cost").innerHTML
		} else if (mincost > row.querySelector("#cost").innerHTML) {
			mincost = row.querySelector("#cost").innerHTML
		}
		row.querySelector("input[type=button]").addEventListener("mousedown", event => {
			if(event.target.className + " EUR">mincost){
					if(document.querySelector('#role').value == '[ROLE_ADMIN]' || document.querySelector('#role').value == '[ROLE_TOPMANAGER]' ){
						alert("Выбрана не самая оптимальная цена");
						confrom(event.target.id, event.target.className, idRoute)
					}else{
						alert("Выбрана не самая оптимальная цена. Недостаточно прав для подтверждения");
					}
				}else{
					confrom(event.target.id, event.target.className, idRoute)
				}
		})
	} else {
		var routeItem = document.querySelectorAll('tr');
		for (i = 0; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			if (mincost == null) {
				mincost = routeItemI.querySelector("#cost").innerHTML
			} else if (mincost > routeItemI.querySelector("#cost").innerHTML) {
				mincost = routeItemI.querySelector("#cost").innerHTML
			}
			routeItemI.querySelector("input[type=button]").addEventListener("mousedown", event => {
				if(event.target.className + " EUR">mincost){
					if(document.querySelector('#role').value == '[ROLE_ADMIN]' || document.querySelector('#role').value == '[ROLE_TOPMANAGER]' ){
						alert("Выбрана не самая оптимальная цена");
						confrom(event.target.id, event.target.className, idRoute)
					}else{
						alert("Выбрана не самая оптимальная цена. Недостаточно прав для подтверждения");
					}
				}else{
					confrom(event.target.id, event.target.className, idRoute)
				}
			})
		}
	}
}
function confrom(login, cost, idRoute) {
	send(idRoute);
	var url = `./confrom?login=${login}&cost=${cost}&idRoute=${idRoute}`
	window.location.href = url;
}
function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function send(idRoute) {
	sendMessage({
		fromUser: "system",
		text: idRoute,
		idRoute: idRoute,
		status: "1"
	})
};
function history() {
	$.getJSON(`../../../api/memory/message/routes/${idRoute}`, function(data) {
		let Trow = document.createElement("tr");
		Trow.innerHTML = 'История предложений';
		document.querySelector("#sort").appendChild(Trow);
		$.each(data, function(key, val) {
			let row = document.createElement("tr");
			let td1 = document.createElement("td");
			td1.innerText = "Предложение от: " + val.companyName;
			let td2 = document.createElement("td");
			td2.id = "cost";
			td2.innerText = val.text + " EUR";
			let td3 = document.createElement("td");
			let input = document.createElement("input");
			input.type = 'hidden';
			input.id = 'id'
			input.innerHTML = val.idRoute;
			td3.appendChild(input);
			row.appendChild(td1);
			row.appendChild(td2);
			row.appendChild(td3);
			document.querySelector("#sort").appendChild(row);
		})
	});
}
