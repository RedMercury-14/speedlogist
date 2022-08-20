var token = $("meta[name='_csrf']").attr("content");
let currentElem = null;
let idRoute; // даёт id по наведению!
let cost;
// слушает все строки таблицы
sort.onmouseover = function test(event) {
	if (currentElem) return;
	let target = event.target.closest('tr');
	if (!target) return;
	if (!table.contains(target)) return;
	currentElem = target;
	idRoute = target.querySelector('#idRoute').innerHTML;
};

sort.onmouseout = function(event) {
	let relatedTarget = event.relatedTarget;
	while (relatedTarget) {
		if (relatedTarget == currentElem) return;
		relatedTarget = relatedTarget.parentNode;
	}
	currentElem.style.background = '';
	currentElem = null;
};
//контекстное меню
(function() {
	var routeItem = document.querySelectorAll('#idRoute');
	for (i = 0; i < routeItem.length; i++) {
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
			route = el.innerHTML;
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
		var url = `../logistics/rouadUpdate?id=${route}&statStock=1`
		window.location.href = url;
	}, false);
	document.querySelector("#l2").addEventListener("click", () => {		
		var url = `../logistics/rouadUpdate?id=${route}&statRoute=1`
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
//
//test
//(function() {
//	var routeItem = document.querySelectorAll('#idRoute');
//	var numStock = document.querySelectorAll('#numStock');
//	var temperature = document.querySelectorAll('#temperature');
//	var timeLoadPreviously = document.querySelectorAll('#timeLoadPreviously');
//	$.getJSON('../../route', function(data) {
//		console.log(data.users[3].idRoute);
////		setInterval(() => {
////		for (i = 0; i < routeItem.length; i++) {
////			var routeItemI = routeItem[i];
////			var numStockInem = numStock[i];
////
////			//console.log(routeItemI);
////		}
////	}, 2000);
//	})
//	
//})();


//запись в номер склада
var numStock = document.querySelectorAll('#numStock');
for (var i = 0; i < numStock.length; i++) {
	numStock[i].addEventListener('click', function func() {
		var targetIdRoute = idRoute;
		var input = document.createElement('input');
		input.size = '2';
		input.value = this.innerHTML;
		this.innerHTML = '';
		this.appendChild(input);
		var td = this;
		input.addEventListener('blur', function() {
			td.innerHTML = this.value;
			var jsonData = { idRoute: targetIdRoute, numStock: this.value };
			$.ajax({
				type: "POST",
				url: "../../api/route/numStock",
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
				url: "../../api/route/temperature",
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
				url: "../../api/route/timeLoadPreviously",
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
				url: "../../api/route/time",
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

