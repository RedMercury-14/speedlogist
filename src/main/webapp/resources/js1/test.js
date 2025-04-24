var token = $("meta[name='_csrf']").attr("content");
document.querySelector("#dateFinish").onchange = function() {

	var inp_dateStart = document.querySelector('input[name=dateStart]').value;
	var inp_dateFinish = document.querySelector('input[name=dateFinish]').value;


	document.querySelector('.content').innerHTML = `
	<div class="container-fluid">
	<div class="table-responsive">
	<table id="route" class="table table-bordered border-primary table-hover table-condensed" id = "sort">	
	<thead class="text-center">
				<tr>
					<th>Номер маршрута</th>
					<th>Название маршрута</th>
					<th>Перевозчик</th>
					<th>Номер машины</th>					
					<th>Дата загрузки</th>
					<th>Стоимость тендера</th>
					<th>Процент скидки</th>
					<th>Выставляемая стоимость</th>					
					<th>Общее колличество паллет</th>
					<th>Общий вес</th>
					<th>Нарушения</th>
					<th>Инфо</th>					
				</tr>
			</thead>`;
	$.getJSON(`../api/route/admin/${inp_dateStart},${inp_dateFinish}`, function(data) {
		$.each(data, function(key, val) {
			let row = document.createElement('tr');
			if (val.truck === null) {
				var numTruck = "";
			} else {
				var numTruck = val.truck.numTruck;
			}
			if (val.user === null) {
				var tenderCostStart = "";
				var companyName = "";
			} else {
				var companyName = val.user.companyName;
				var rate = val.user.rate;
				var tenderCostStart
				$.getJSON(`../api/route/${val.idRoute}`, function(data2) {
					tenderCostStart = data2.cost[rate];
					row.innerHTML = `<td id = "idRoute">${val.idRoute}</td>
				<td>${val.routeDirection}</td>
				<td>${companyName}</td>
				<td>${numTruck}</td>
				<td>${val.dateLoadPreviously}</td>
				<td>${tenderCostStart}</td>
				<td>${finishPrice} %</td>
				<td>${startPrice} руб</td>
				<td>${val.totalLoadPall}</td>
				<td>${val.totalCargoWeight}</td>`
					document.querySelector('#route').appendChild(row);

				})
			}
			if (val.finishPrice === null) {
				var finishPrice = "";
				var startPrice = "";
			} else {
				var finishPrice = val.finishPrice;
				var startPrice = val.startPrice
			}
		})		
	});
	setTimeout(() => contextMenu(), 1000);
};

document.querySelector("#dateStart").onchange = function() {
	var inp_dateStart = document.querySelector('input[name=dateStart]').value;
	var inp_dateFinish = document.querySelector('input[name=dateFinish]').value;
	if (inp_dateFinish != "") {
		$.getJSON(`../api/route/admin/${inp_dateStart},${inp_dateFinish}`, function(data) {
			//console.log(data)
		});
	}
};
function contextMenu() {
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
				console.log(event);
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
			console.log(route);
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
}







