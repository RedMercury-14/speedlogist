import { ws } from './global.js';
import { wsHead } from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
let idRoute = document.querySelector('#idRoute').value;
let routeDirection = document.querySelector('#routeDirection').value;
setTimeout(() => onButton(null), 100);
import { EUR } from './global.js';
import { USD } from './global.js';
import { RUB } from './global.js';
import { KZT } from './global.js';
$.getJSON(`../../../api/info/message/routes/${idRoute}`, function(data) {
	if (data.length == 0) {
		history();
	}
	$.each(data, function(key, val) {
		let row = document.createElement("tr");
		let td0 = document.createElement("td");
		td0.classList.add("none");
		td0.innerText = val.fromUser
		td0.id = "login";
		let td1 = document.createElement("td");
		td1.innerText = "Предложение от: " + val.companyName;
		let td2 = document.createElement("td");
		td2.id = "cost";
		td2.innerText = val.text + " " + val.currency;
		let td3 = document.createElement("td");
		let button = document.createElement("input");
		button.type = 'button';
		button.id = val.fromUser;
		if (val.currency == 'BYN') {
			button.className = val.text;
		}
		if (val.currency == 'KZT') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${KZT}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		if (val.currency == 'RUB') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${RUB}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		if (val.currency == 'EUR') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${EUR}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		if (val.currency == 'USD') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${USD}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		button.value = "Принять";
		let td4 = document.createElement("td");
		td4.id = "finalCost";
		if (val.currency == 'BYN') {
			td4.innerText = "по курсу НБРБ: " + val.text + " BYN";
			td4.className = val.text;
		}
		if (val.currency == 'KZT') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${KZT}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		if (val.currency == 'RUB') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${RUB}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		if (val.currency == 'EUR') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${EUR}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}
		if (val.currency == 'USD') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${USD}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
				});
			});
		}

		let input = document.createElement("input");
		input.type = 'hidden';
		input.id = 'id';
		input.innerHTML = val.idRoute;
		td3.appendChild(input);
		td3.appendChild(button);
		row.appendChild(td0);
		row.appendChild(td1);
		row.appendChild(td2);
		row.appendChild(td3);
		row.appendChild(td4);
		document.querySelector("#sort").appendChild(row);
	});
});
function onMessage(msg) {
	if (idRoute == msg.idRoute && msg.fromUser != 'system') {
		let row = document.createElement("tr");
		let td0 = document.createElement("td");
		td0.classList.add("none");
		td0.innerText = val.fromUser
		td0.id = "login";
		let td1 = document.createElement("td");
		td1.innerText = "Предложение от: " + msg.companyName;
		let td2 = document.createElement("td");
		td2.innerText = msg.text + " " + msg.currency;
		td2.id = "cost";
		let td3 = document.createElement("td");
		let button = document.createElement("input");
		button.type = 'button';
		button.id = msg.fromUser;
		if (msg.currency == 'BYN') {
			button.className = msg.text;
		}
		if (msg.currency == 'KZT') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${KZT}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		if (msg.currency == 'RUB') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${RUB}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		if (msg.currency == 'EUR') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${EUR}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		if (msg.currency == 'USD') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${USD}`).then((response) => {
				response.json().then((text) => {
					button.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		button.value = "Принять";
		let td4 = document.createElement("td");
		td4.id = "finalCost";
		if (msg.currency == 'BYN') {
			td4.innerText = "по курсу НБРБ: " + msg.text + " BYN";
			td4.className = msg.text;
		}
		if (msg.currency == 'KZT') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${KZT}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		if (msg.currency == 'RUB') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${RUB}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		if (msg.currency == 'EUR') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${EUR}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		if (msg.currency == 'USD') {
			fetch(`https://www.nbrb.by/api/exrates/rates/${USD}`).then((response) => {
				response.json().then((text) => {
					td4.innerText = "по курсу НБРБ: " + Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale) + " BYN";
					td4.className = Math.round(text.Cur_OfficialRate * msg.text / text.Cur_Scale);
				});
			});
		}
		let input = document.createElement("input");
		input.type = 'hidden';
		input.id = 'id'
		input.innerHTML = msg.idRoute;
		td3.appendChild(input);
		td3.appendChild(button);
		row.appendChild(td0);
		row.appendChild(td1);
		row.appendChild(td2);
		row.appendChild(td3);
		row.appendChild(td4);
		document.querySelector("#sort").appendChild(row);
		//	row.querySelector("input[type=button]").addEventListener("mousedown", event => {
		//		confrom(event.target.id, event.target.className, idRoute)
		//	})
		setTimeout(() => onButton(row), 100);
	}

};

var mincost = null;
function onButton(row) {
	if (row != null) {
		if (mincost == null) {
			mincost = row.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim()
		} else if (parseInt(mincost) > parseInt(row.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim())) {
			mincost = row.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim();
		}
		console.log('после message ' + mincost);
		row.querySelector("input[type=button]").addEventListener("mousedown", event => {

			if (parseInt(event.target.className) > parseInt(mincost)) {
				if (document.querySelector('#role').value == '[ROLE_ADMIN]' || document.querySelector('#role').value == '[ROLE_TOPMANAGER]') {
					if (confirm("Выбрана не самая оптимальная цена, Вы уверены?")) {
						alert("Цена принята");
						confrom(event.target.id, event.target.className, idRoute, routeItemI)
					} else {

					}
				} else {
					alert("Выбрана не самая оптимальная цена. Недостаточно прав для подтверждения");
				}
			} else {
				confrom(event.target.id, event.target.className, idRoute, routeItemI)
			}
		})
	} else {
		var routeItem = document.querySelectorAll('tr');
		for (let i = 0; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			if (mincost == null) {
				mincost = routeItemI.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim();
			} else if (parseInt(mincost) > parseInt(routeItemI.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim())) {
				mincost = routeItemI.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim();
			}
			console.log(mincost);
			routeItemI.querySelector("input[type=button]").addEventListener("mousedown", event => {
				console.log(event);
				if (parseInt(event.target.className) > parseInt(mincost)) {
					if (document.querySelector('#role').value == '[ROLE_ADMIN]' || document.querySelector('#role').value == '[ROLE_TOPMANAGER]') {
						if (confirm("Выбрана не самая оптимальная цена, Вы уверены?")) {
							alert("Цена принята");
							confrom(event.target.id, event.target.className, idRoute, routeItemI)
						} else {

						}
					} else {
						alert("Выбрана не самая оптимальная цена. Недостаточно прав для подтверждения");
					}
				} else {
					confrom(event.target.id, event.target.className, idRoute, routeItemI)
				}
			})
		}

		console.log('после списка ' + mincost);
	}
}
function confrom(login, cost, idRoute, routeItemI) {
	send(idRoute, login, cost);
	var url = `./confrom?login=${login}&cost=${cost}&idRoute=${idRoute}`
	window.location.href = url;
}
function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function sendMessageToUser(message) {
	wsHead.send(JSON.stringify(message));
}
function send(idRoute, login, cost) {
	sendMessage({
		fromUser: "system",
		text: idRoute,
		idRoute: idRoute,
		status: "1"
	});
	var targetLogin = login
	sendMessageToUser({
		fromUser: "logist",
		toUser: login,
		text: 'Ваше предложение к маршруту ' + routeDirection + ' с ценой ' + cost + ' BYN одобрено! Необходимо назначить машину и водителя.',
		idRoute: idRoute,
		status: "1"
	});

	var routeItem = document.querySelectorAll('tr');
	for (let i = 0; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		if (routeItemI.querySelector("#login").innerHTML != targetLogin) {
			sendMessageToUser({
				fromUser: "logist",
				toUser: routeItemI.querySelector("#login").innerHTML,
				text: 'К сожалению, предложенная Вами цена для маршрута '+ routeDirection + ' нам не подходит.',
				idRoute: idRoute,
				status: "1"
			});
		}
	}

};
function history() {
	$.getJSON(`../../../api/memory/message/routes/${idRoute}`, function(data) {
		let Trow = document.createElement("tr");
		Trow.innerHTML = 'История предложений';
		document.querySelector("#sort").appendChild(Trow);
		let target = getMessage(data);
		target.forEach(function(val) {
			let row = document.createElement("tr");
			let td1 = document.createElement("td");
			td1.innerText = "Предложение от: " + val.companyName;
			let td2 = document.createElement("td");
			td2.id = "cost";
			td2.innerText = val.text + " " + val.currency;
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
		});
	})
}
function getMessage(data) {
	let target = new Set();
	data.forEach(function(item) {
		if (item.toUser == null) {
			target.add(item);
		}
	})
	return target;
}
