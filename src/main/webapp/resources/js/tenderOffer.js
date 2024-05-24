import { ajaxUtils } from './ajaxUtils.js';
import { ws } from './global.js';
import { wsHead } from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
let idRoute = document.querySelector('#idRoute').value;
let routeDirection = document.querySelector('#routeDirection').value;
setTimeout(() => onButton(null), 500);
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
		button.className = val.text;
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
	setTimeout(() => doOptimalCost(), 600);
	setTimeout(() => doBadCost(), 600);
});



window.onload = () => {
	// проверка статуса заказа по маршруту
	checkOrderForStatus(idRoute)

}


function sleep(milliseconds) {
  const date = Date.now();
  let currentDate = null;
  do {
    currentDate = Date.now();
  } while (currentDate - date < milliseconds);
}

function onMessage(msg) {
	sleep(500);
	if (msg.comment != null && msg.comment == 'delete') {
		console.log("1111111111111111111111111")
		location.reload();
	} else if (idRoute == msg.idRoute && msg.fromUser != 'system') {
		let row = document.createElement("tr");
		let td0 = document.createElement("td");
		td0.classList.add("none");
		td0.innerText = msg.fromUser
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
		button.className = msg.text;
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
		setTimeout(() => onButton(row), 500);
	}

};

// управление подтверждением минимальной ценой
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
			if (document.querySelectorAll("tr").length == 1) {
				if (!confirm("Вы уверены, что хотите принять данное предложение?")) return
				var pass = prompt('Подтверждено единственное предложение. Требуется дополнительное подтверждение от администратора');
				if (pass == "goodboy") {
					alert('полетел обычный запрос')
				} else {
					alert('потребует подтверждения')
				}
			} else if (parseInt(event.target.className) > parseInt(mincost)) {
				if (document.querySelector('#role').value == '[ROLE_ADMIN]' || document.querySelector('#role').value == '[ROLE_TOPMANAGER]') {
					if (confirm("Выбрана не самая оптимальная цена, Вы уверены?")) {
						alert("Цена принята");
						confrom(event.target.id, event.target.className, idRoute, row.querySelector("#cost").innerText.split(' ')[1])
					} else {

					}
				} else {
//					alert("Выбрана не самая оптимальная цена. Недостаточно прав для подтверждения");
					if (!confirm("Вы уверены, что хотите принять данное предложение?")) return
					confrom(event.target.id, event.target.className, idRoute, row.querySelector("#cost").innerText.split(' ')[1])
				}
			} else {
				if (!confirm("Вы уверены, что хотите принять данное предложение?")) return
				confrom(event.target.id, event.target.className, idRoute, row.querySelector("#cost").innerText.split(' ')[1])
			}
		})
	} else {		
		var routeItem = document.querySelectorAll('tr');
		for (let i = 0; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			if (routeItemI.querySelector("#finalCost") == null) {
				continue;
			}
			if (mincost == null) {
				mincost = routeItemI.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim();
			} else if (parseInt(mincost) > parseInt(routeItemI.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim())) {
				mincost = routeItemI.querySelector("#finalCost").innerHTML.replace(/[^+\d]/g, '').trim();
			}
			console.log(mincost);
			if (routeItemI.querySelector("input[type=button]") != null) {
				routeItemI.querySelector("input[type=button]").addEventListener("mousedown", event => {
					console.log(event);
					if (parseInt(event.target.className) > parseInt(mincost)) {
						if (document.querySelector('#role').value == '[ROLE_ADMIN]' || document.querySelector('#role').value == '[ROLE_TOPMANAGER]') {
							if (confirm("Выбрана не самая оптимальная цена, Вы уверены?")) {

								if (document.querySelectorAll("tr").length == 1) {
									var pass = prompt('Подтверждено единственное предложение. Требуется дополнительное подтверждение от администратора');
									if (pass == "goodboy") {
										alert("Цена принята");
										confrom(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1])
									} else {
										alert("Уведомление перевозчику не отправлено! Требуется дополнительное подтверждение");
										confromWhisStatus(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1],'8')
									}
								}else{
									alert("Цена принята");
									confrom(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1])
								}
								
							}
						} else {
//							alert("Выбрана не самая оптимальная цена. Недостаточно прав для подтверждения");
							confrom(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1])							
						}
					} else {
						if (!confirm("Вы уверены, что хотите принять данное предложение?")) return
						if(document.querySelectorAll("tr").length == 1){
							var pass = prompt('Подтверждено единственное предложение. Требуется дополнительное подтверждение от администратора');
							if (pass == "goodboy") {
										alert("Цена принята");
										confrom(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1])
									} else {
										alert("Уведомление перевозчику не отправлено! Требуется дополнительное подтверждение");
										confromWhisStatus(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1],'8')
									}
						}else{
							confrom(event.target.id, event.target.className, idRoute, routeItemI.querySelector("#cost").innerHTML.split(' ')[1])
						}
												
					}
				})
			}

		}	
		if (mincost == null && document.querySelector("#sort").querySelectorAll("tr").length != 1) {	
//			location.reload();   <---- тут косяк, нужно разобраться
		}
		console.log('после списка ' + mincost);
		console.log('всего строк ' + document.querySelectorAll("tr").length)
	}
}
function confrom(login, cost, idRoute, currency) {
	send(idRoute, login, cost, currency);
	var url = `./confrom?login=${login}&cost=${cost}&idRoute=${idRoute}&currency=${currency}`
	window.location.href = url;
}
function confromWhisStatus(login, cost, idRoute, currency, status) {
	//send(idRoute, login, cost, currency);
	var url = `./confrom?login=${login}&cost=${cost}&idRoute=${idRoute}&currency=${currency}&status=${status}`
	window.location.href = url;
}
function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function sendMessageToUser(message) {
	wsHead.send(JSON.stringify(message));
}
function send(idRoute, login, cost, currency) {
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
		text: 'Ваше предложение к маршруту ' + routeDirection + ' с ценой ' + cost + ' ' + currency + ' одобрено! Необходимо назначить машину и водителя.',
		idRoute: idRoute,
		url: '/speedlogist/main/carrier/transportation',
		status: "1"
	});

	var routeItem = document.querySelectorAll('tr');
	for (let i = 0; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		if (routeItemI.querySelector("#login").innerHTML != targetLogin) {
			sendMessageToUser({
				fromUser: "logist",
				toUser: routeItemI.querySelector("#login").innerHTML,
				text: 'К сожалению, предложенная Вами цена для маршрута ' + routeDirection + ' нам не подходит.',
				idRoute: idRoute,
				status: "1"
			});
		}
	}

};

function sendOneUser(idRoute, login, cost, currency) {
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
		text: 'Ваше предложение к маршруту ' + routeDirection + ' с ценой ' + cost + ' ' + currency + ' одобрено! Необходимо назначить машину и водителя.',
		idRoute: idRoute,
		url: '/speedlogist/main/carrier/transportation',
		status: "1"
	});
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
			td1.innerText = "Предложение от: " + val.companyName + ", цена указана " + val.nds;
			let td2 = document.createElement("td");
			td2.id = "cost";
			td2.innerText = val.text + " " + val.currency;
			let td3 = document.createElement("td");
			let td4 = document.createElement("td");
			td4.id = "finalCost";
			if (val.currency == 'BYN') {
				td4.innerText = "по курсу НБРБ, на сегодняшний день: " + val.text + " BYN";
				td4.className = val.text;
			}
			if (val.currency == 'KZT') {
				fetch(`https://www.nbrb.by/api/exrates/rates/${KZT}`).then((response) => {
					response.json().then((text) => {
						td4.innerText = "по курсу НБРБ, на сегодняшний день: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
						td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
					});
				});
			}
			if (val.currency == 'RUB') {
				fetch(`https://www.nbrb.by/api/exrates/rates/${RUB}`).then((response) => {
					response.json().then((text) => {
						td4.innerText = "по курсу НБРБ, на сегодняшний день: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
						td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
					});
				});
			}
			if (val.currency == 'EUR') {
				fetch(`https://www.nbrb.by/api/exrates/rates/${EUR}`).then((response) => {
					response.json().then((text) => {
						td4.innerText = "по курсу НБРБ, на сегодняшний день: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
						td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
					});
				});
			}
			if (val.currency == 'USD') {
				fetch(`https://www.nbrb.by/api/exrates/rates/${USD}`).then((response) => {
					response.json().then((text) => {
						td4.innerText = "по курсу НБРБ, на сегодняшний день: " + Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale) + " BYN";
						td4.className = Math.round(text.Cur_OfficialRate * val.text / text.Cur_Scale);
					});
				});
			}
			let input = document.createElement("input");
			input.type = 'hidden';
			input.id = 'id'
			input.innerHTML = val.idRoute;
			td3.appendChild(input);
			row.appendChild(td1);
			row.appendChild(td2);
			row.appendChild(td3);
			row.appendChild(td4);
			document.querySelector("#sort").appendChild(row);
		});
	})
	setTimeout(() => doOptimalCostHistory(), 600);
	setTimeout(() => doBadCostHistory(), 600);
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
function doOptimalCost() {
	let rows = document.querySelectorAll('tr');
	for (let i = 0; i < rows.length; i++) {
		let rowI = rows[i];
		if (rowI.querySelector('#finalCost') == null) {
			continue;
		}
		if (rowI.querySelector('#finalCost').innerHTML.split(" ")[3] == mincost) {
			rowI.classList.add('activRow');
			break;
		}
	}
}
let maxCost = 0;
function doBadCost() {
	let rows = document.querySelectorAll('tr');
	for (let i = 0; i < rows.length; i++) {
		let rowI = rows[i];
		if (rowI.querySelector('#finalCost') == null) {
			continue;
		}
		if (parseInt(rowI.querySelector('#finalCost').innerHTML.split(" ")[3]) > parseInt(maxCost)) {
			maxCost = rowI.querySelector('#finalCost').innerHTML.split(" ")[3];
		}
	}
	for (let i = 0; i < rows.length; i++) {
		let rowI = rows[i];
		if (rowI.querySelector('#finalCost') == null) {
			continue;
		}
		if (rowI.querySelector('#finalCost').innerHTML.split(" ")[3] == maxCost) {
			rowI.classList.add('badRow');
		}
	}
}
//методы поиска оптимальной цены для вывода истории предложений
function doOptimalCostHistory() {
	let rows = document.querySelectorAll('tr');
	for (let i = 0; i < rows.length; i++) {
		let rowI = rows[i];
		if (rowI.querySelector('#finalCost') == null) {
			continue;
		}
		if (rowI.querySelector('#finalCost').innerHTML.split(" ")[6] == mincost) {
			rowI.classList.add('activRow');
			break;
		}
	}
}
let maxCostHistory = 0;
function doBadCostHistory() {
	let rows = document.querySelectorAll('tr');
	for (let i = 0; i < rows.length; i++) {
		let rowI = rows[i];
		if (rowI.querySelector('#finalCost') == null) {
			continue;
		}
		if (parseInt(rowI.querySelector('#finalCost').innerHTML.split(" ")[6]) > parseInt(maxCostHistory)) {
			maxCostHistory = rowI.querySelector('#finalCost').innerHTML.split(" ")[6];
		}
	}
	for (let i = 0; i < rows.length; i++) {
		let rowI = rows[i];
		if (rowI.querySelector('#finalCost') == null) {
			continue;
		}
		if (rowI.querySelector('#finalCost').innerHTML.split(" ")[6] == maxCostHistory) {
			rowI.classList.add('badRow');
		}
	}
}

//кнопка подтверждления для админа

function proof(){
	var button = document.querySelector('#proof');
	button && button.addEventListener("mousedown", event =>{
		var loginThis = document.querySelector('#loginUser').value;
		var costThis = document.querySelector('#cost').innerHTML.split(' ')[0];
		var currencyThis = document.querySelector('#cost').innerHTML.split(' ')[1];
		sendOneUser(idRoute, loginThis, costThis, currencyThis);
		var url = `./confrom?idRoute=${idRoute}`
		window.location.href = url;
	})
	
}
proof();

function checkOrderForStatus(idRoute) {
	ajaxUtils.get({
		url: `../../../api/logistics/checkOrderForStatus/${idRoute}`,
		successCallback: (data) => {
			if (data.status === '200') {
				orderStatusHandler(data)
				return
			}
			
		}
	})
}

function orderStatusHandler(data) {
	const messageContainer = document.querySelector('#messageContainer')
	const status = Number(data.message)
	if (!messageContainer) return
	if (!status) return
	
	if (status === 10) {
		const text = 'Заявка на транспорт по данному маршруту была отменена!'
		messageContainer.innerHTML = text
		$('#displayMessageModal').modal('show')
		$('#displayMessageModal').addClass('show')
		$('.modal-backdrop').addClass("whiteOverlay")
	}
}