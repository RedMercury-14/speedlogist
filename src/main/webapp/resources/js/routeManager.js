import { setRouteTemperatureUrl, setRouteTimeLoadPreviouslyUrl, setRouteTimeUrl, setRouteTypeTrailerUrl, setRouteUserCommentsUrl, toRouadUpdateUrl, toTenderpageUrl } from './globalConstants/urls.js'

var token = $("meta[name='_csrf']").attr("content");
let currentElem = null;
let idRoute; // даёт id по наведению!
let cost;
import { ws } from './global.js';
import { wsHead } from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
onNumberMessage();

function sendMessage(message) {
	wsHead.send(JSON.stringify(message));
}
function onMessage(msg) {
	console.log(msg);
	if (msg.fromUser == 'server' && msg.text.split(' ')[2] == 'завершились') {
		document.getElementById(msg.idRoute).classList.add("endRow");
	} if (msg.toUser == 'routeManager' && msg.comment == 'percent') {
		document.getElementById(msg.idRoute).classList.add("activRow");
		document.getElementById(msg.idRoute).querySelector('#finishPrice').innerHTML = msg.text + '%';
	}


	//let coll = routeItemI.querySelector('.coll');
	//		if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Тендерзавершен.Машинаиводительприняты.') {
	//			routeItemI.classList.add("finishRow");
	//		} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Ожиданиеподтверждения') {
	//			routeItemI.classList.add("attentionRow");
	//		} 


};
var routeItem = document.querySelectorAll('tr');
for (let i = 1; i < routeItem.length; i++) {
	var routeItemI = routeItem[i];
	if (routeItemI.querySelector('#finishPrice').innerHTML != '%') {
		routeItemI.classList.add("activRow");
	} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Ожиданиеподтверждения') {
		routeItemI.classList.add("attentionRow");
	} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Тендерзавершен.Отсутствуетмашина') {
		routeItemI.classList.add("endRow");
	}
}

// слушает все строки таблицы
sort.onmouseover = function test(event) {
	if (currentElem) return;
	let target = event.target.closest('tr');
	if (!target) return;
	//if (!table.contains(target)) return;
	currentElem = target;
	console.log(target.querySelector('#idRoute').innerHTML);
	idRoute = target.querySelector('#idRoute').innerHTML;
};

sort.onmouseout = function(event) {
	let relatedTarget = event.relatedTarget;
	while (relatedTarget) {
		if (relatedTarget == currentElem) return;
		relatedTarget = relatedTarget.parentNode;
	}
	//currentElem.style.background = '';
	currentElem = null;
};
//контекстное меню
(function() {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 0; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		contextMenuListner(routeItemI);
	}
	const menu = document.querySelector(".right-click-menu");
	var route;
	function contextMenuListner(el) {
		el.addEventListener("contextmenu", event => {
			event.preventDefault();
			menu.style.top = `${event.clientY}px`;
			menu.style.left = `${event.clientX}px`;
			menu.classList.add("active");
			route = el.querySelector('#idRoute').innerHTML;
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
		var url = `${toRouadUpdateUrl}?id=${route}&statStock=1`
		alert("Временно заблокировано");
		//window.location.href = url;
	}, false);
	document.querySelector("#l2").addEventListener("click", () => {
		var url = `${toRouadUpdateUrl}?id=${route}&statRoute=1`
		window.location.href = url;
		var routeDirection = document.getElementById(route).querySelector('#routeDirection').innerHTML;
		sendMessage({
			fromUser: "logist",
			toUser: "regional",
			text: 'Маршрут ' + routeDirection + ' доступен для торгов.',
			idRoute: route,
			status: "1"
		})
	}, false);
	document.querySelector("#l3").addEventListener("click", () => {
		var url = `${toTenderpageUrl}?routeId=${route}`
		window.location.href = url;
	}, false);
	document.querySelector("#l4").addEventListener("click", () => {
		alert("В доступе отказано");
	}, false);

})();

//запись в температуру
var tds = document.querySelectorAll('#temperature');
for (var i = 0; i < tds.length; i++) {
	tds[i].addEventListener('click', function func() {
		var targetIdRoute = idRoute;
		var input = document.createElement('input');
		input.size = '2';
		input.value = this.innerHTML;
		this.innerHTML = '';
		this.appendChild(input);
		var td = this;
		input.addEventListener('blur', function() {
			td.innerHTML = this.value;
			var jsonData = { idRoute: targetIdRoute, temperature: this.value };
			$.ajax({
				type: "POST",
				url: setRouteTemperatureUrl,
				headers: { "X-CSRF-TOKEN": token },
				data: JSON.stringify(jsonData),
				contentType: 'application/json',
				dataType: 'json',
				success: function(html) {
					console.log(html.message);

				},
				error: function(err) { }
			})
			td.addEventListener('click', func)
		})
		this.removeEventListener('click', func);
	})
}

//запись во время загрузки
var timeLoadPreviously = document.querySelectorAll('#timeLoadPreviously');
for (var i = 0; i < timeLoadPreviously.length; i++) {
	timeLoadPreviously[i].addEventListener('click', function func() {
		var targetIdRoute = idRoute;
		var input = document.createElement('input');
		input.type = 'time';
		input.value = this.innerHTML;
		this.innerHTML = '';
		this.appendChild(input);
		var td = this;
		input.addEventListener('blur', function() {
			td.innerHTML = this.value;
			var jsonData = { idRoute: targetIdRoute, timeLoadPreviously: this.value };
			$.ajax({
				type: "POST",
				url: setRouteTimeLoadPreviouslyUrl,
				headers: { "X-CSRF-TOKEN": token },
				data: JSON.stringify(jsonData),
				contentType: 'application/json',
				dataType: 'json',
				success: function(html) {
					console.log(html.message);

				},
				error: function(err) { }
			})
			td.addEventListener('click', func)
		})
		this.removeEventListener('click', func);
	})
}

//запись комментария
var userComments = document.querySelectorAll('#userComments');
for (var i = 0; i < userComments.length; i++) {
	userComments[i].addEventListener('click', function func() {
		var targetIdRoute = idRoute;
		var input = document.createElement('input');
		input.type = 'text';
		input.value = this.innerHTML;
		this.innerHTML = '';
		this.appendChild(input);
		var td = this;
		input.addEventListener('blur', function() {
			td.innerHTML = this.value;
			var jsonData = { idRoute: targetIdRoute, userComments: this.value };
			$.ajax({
				type: "POST",
				url: setRouteUserCommentsUrl,
				headers: { "X-CSRF-TOKEN": token },
				data: JSON.stringify(jsonData),
				contentType: 'application/json',
				dataType: 'json',
				success: function(html) {
					console.log(html.message);

				},
				error: function(err) { }
			})
			td.addEventListener('click', func)
		})
		this.removeEventListener('click', func);
	})
}

//запись типа прицепа
var typeTrailer = document.querySelectorAll('#typeTrailer');
for (var i = 0; i < typeTrailer.length; i++) {
	typeTrailer[i].addEventListener('click', function func() {
		var targetIdRoute = idRoute;
		
		this.addEventListener('change', (event)=> {			
			var jsonData = { idRoute: targetIdRoute, typeTrailer: event.target.value };
			$.ajax({
				type: "POST",
				url: setRouteTypeTrailerUrl,
				headers: { "X-CSRF-TOKEN": token },
				data: JSON.stringify(jsonData),
				contentType: 'application/json',
				dataType: 'json',
				success: function(html) {
					console.log(html.message);

				},
				error: function(err) { }
			})
			this.addEventListener('click', func)
		})
		this.removeEventListener('click', func);
	})
}

//запись во время таймера
var time = document.querySelectorAll('#time');
for (var i = 0; i < time.length; i++) {
	time[i].addEventListener('click', function func() {
		var targetIdRoute = idRoute;
		var input = document.createElement('input');
		input.type = 'time';
		input.value = this.innerHTML;
		this.innerHTML = '';
		this.appendChild(input);
		var td = this;
		input.addEventListener('blur', function() {
			td.innerHTML = this.value;
			var jsonData = { idRoute: targetIdRoute, time: this.value };
			$.ajax({
				type: "POST",
				url: setRouteTimeUrl,
				headers: { "X-CSRF-TOKEN": token },
				data: JSON.stringify(jsonData),
				contentType: 'application/json',
				dataType: 'json',
				success: function(html) {
					console.log(html.message);

				},
				error: function(err) { }
			})
			td.addEventListener('click', func)
		})
		this.removeEventListener('click', func);
	})
}

//подсветка строк
function onNumberMessage() {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		console.log(routeItemI.querySelector('#status').innerHTML.replace(/\s/g, ''))
		//let coll = routeItemI.querySelector('.coll');
		if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Тендерзавершен.Машинаиводительприняты.') {
			routeItemI.classList.add("finishRow");
		} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Ожиданиеподтверждения') {
			routeItemI.classList.add("attentionRow");
		} else if (routeItemI.querySelector('#status').innerHTML.replace(/\s/g, '') == 'Тендерзавершен.Отсутствуетмашина') {
			routeItemI.classList.add("endRow");
		}
		//getNumMessege(routeItemI.querySelector('#idRoute').innerHTML, coll, routeItemI)
	}
}

