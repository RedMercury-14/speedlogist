contextMenu();
onNumberMessage();
var route;
var routeDirection;
import { ws } from './global.js';
import { wsHead } from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
var role = document.querySelector('input[id=role]').value;



function onNumberMessage() {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		let coll = routeItemI.querySelector('.coll');
		if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Тендерзавершен.Перевозчикпринят.') {
			routeItemI.classList.add("finishRow");
		} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Ожиданиеподтверждения') {
			routeItemI.classList.add("attentionRow");
		} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Маршрутзавершен.') {
			routeItemI.classList.add("endRow");
		}
		getNumMessege(routeItemI.querySelector('#idRoute').innerHTML, coll, routeItemI)
	}
}

function onMessage(msg) {
	if (msg.idRoute != null) {
		var routeItem = document.querySelectorAll('tr');
		for (let i = 1; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			if (routeItemI.querySelector("#idRoute").innerHTML == msg.idRoute) {
				let coll = routeItemI.querySelector('.coll');
				setTimeout(() => getNumMessege(msg.idRoute, coll), 500);
				routeItemI.classList.add("activRow");
			}
		}
	}
};

function getNumMessege(idRoute, coll, routeItemI) {
	fetch(`../../api/info/message/numroute/${idRoute}`).then(function(response) {
		response.text().then(function(text) {
			coll.innerText = "(" + text + ")";
			if (text >= '1') {
				routeItemI.classList.add("activRow");
			}
		});
	});
}

function sendMessage(message) {
	wsHead.send(JSON.stringify(message));
}

function contextMenu() {
	(function() {
		var routeItem = document.querySelectorAll('tr');
		for (let i = 0; i < routeItem.length; i++) {
			var routeItemI = routeItem[i];
			contextMenuListner(routeItemI);
		}
		const menu = document.querySelector(".right-click-menu");
		function contextMenuListner(el) {
			el.addEventListener("contextmenu", event => {
				event.preventDefault();
				menu.style.top = `${event.clientY}px`;
				menu.style.left = `${event.clientX}px`;
				menu.classList.add("active");
				route = el.querySelector('#idRoute').innerHTML;
				routeDirection = el.querySelector('#routeDirection').innerHTML;
			}, false);
		}
		document.addEventListener("click", event => {
			if (event.button !== 2) {
				menu.classList.remove("active");
			}
		}, false);

		menu.addEventListener("click", event => {
			event.stopPropagation();
		}, false);

		document.querySelector("#l1").addEventListener("click", () => {
			var url = `./international/tenderOffer?idRoute=${route}`
			window.location.href = url;
		}, false);
		document.querySelector("#l2").addEventListener("click", () => {
			var url = `../logistics/rouadUpdate?id=${route}&statRoute=1&comment=international`
			window.location.href = url;
			sendMessage({
				fromUser: "logist",
				toUser: "international",
				text: 'Маршрут ' + routeDirection + ' доступен для торгов',
				idRoute: route,
				status: "1"
			})
		}, false);
		document.querySelector("#l3").addEventListener("click", () => {
			var url = `../logistics/international/routeShow?idRoute=${route}`
			window.location.href = url;
		}, false);
		document.querySelector("#l4").addEventListener("click", () => {
			if (role == '[ROLE_ADMIN]' || role == '[ROLE_TOPMANAGER]') {
				var url = `/speedlogist/main/logistics/international/editRoute?idRoute=${route}`
				window.location.href = url;
			} else {
				alert("В доступе отказано");
			}
		}, false);
		document.querySelector("#l5").addEventListener("click", () => {
			fetch(`/speedlogist/api/memory/message/routes/${route}`).then(function(response) {
				response.json().then(function(text) {
					var flag = false;
					text.forEach(function(element) {
						if (element.text == 'На_выгрузке') {
							flag = true;
						}
					})
					if (flag) {
						var url = `/speedlogist/main/logistics/international/routeEnd?idRoute=${route}`
						window.location.href = url;
					} else {
						alert('Маршрут не может быть завершен, т.к. авто не прибыло на место разгрузки.')
					}
				});
			});
		}, false);
		document.querySelector("#l6").addEventListener("click", () => {
			var url = `/speedlogist/main/logistics/international/addRoute?idRoute=${route}`
			window.location.href = url;
		}, false);
	})();
}



var modal = document.querySelectorAll('#myModal');
var btn = document.querySelectorAll("#myBtn");
for (let i = 0; i < btn.length; i++) {
	var modalI = modal[i];
	var btnI = btn[i];
	var span = modalI.querySelector(".closer");
	modalTarget(modalI, btnI, span)
}
function modalTarget(modalI, btnI, span) {
	btnI.addEventListener("click", () => {
		modalI.style.display = "block";
	})
	span.onclick = function() {
		modalI.style.display = "none";
	}
	window.onclick = function(event) {
		if (event.target == modalI) {
			modalI.style.display = "none";
		}
	}
}





