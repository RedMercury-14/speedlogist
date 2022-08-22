contextMenu();
onNumberMessage();
var route;
var num;
let ws = new WebSocket("ws://localhost:8080/speedlogist/chat");
ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
function onNumberMessage() {
	var routeItem = document.querySelectorAll('tr');
	for (i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		let coll = routeItemI.querySelector('.coll');
		getNumMessege(routeItemI.querySelector('#idRoute').innerHTML, coll)
	}
}

function onMessage(msg) {
	if (msg.idRoute != null) {
		var routeItem = document.querySelectorAll('tr');
		for (i = 1; i < routeItem.length; i++) {			
			var routeItemI = routeItem[i];
			if (routeItemI.querySelector("#idRoute").innerHTML == msg.idRoute) {				
				let coll = routeItemI.querySelector('.coll');
				setTimeout(()=>getNumMessege(msg.idRoute, coll), 500);
				
			}
		}
	}
};
function getNumMessege(idRoute, coll) {
	fetch(`../../api/info/message/numroute/${idRoute}`).then(function(response) {
		response.text().then(function(text) {
			coll.innerText = "(" + text + ")";
		});
	});
}

function contextMenu() {
	(function() {
		var routeItem = document.querySelectorAll('tr');
		for (i = 0; i < routeItem.length; i++) {
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
			console.log(route);
		}, false);
		document.querySelector("#l2").addEventListener("click", () => {
			var url = `../logistics/rouadUpdate?id=${route}&statRoute=1&comment=international`
			window.location.href = url;
		}, false);
		document.querySelector("#l3").addEventListener("click", () => {
			var url = `../carrier/tender/tenderpage?routeId=${route}`
			window.open(url);
			//window.location.href = url;
		}, false);
		document.querySelector("#l4").addEventListener("click", () => {
			alert("В доступе отказано");
		}, false);
	})();
}