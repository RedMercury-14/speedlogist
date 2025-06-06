import { getArchiveRoutesBaseUrl } from "./globalConstants/urls.js";

// остановился тут дописать и проверить рест контроллер доделать таблички
var dateStart = document.querySelector('#dateStart');
var dateFinish = document.querySelector('#dateFinish');
var dateFinishObj = new Date(dateFinish.value);
var div = document.querySelector('#div');
//костыль, чтобы список подтягивался после возврата
if (localStorage.getItem("dateStart") != "" && localStorage.getItem("dateFinish") != "") {
	if (new Date(localStorage.getItem("dateStart")) < new Date(localStorage.getItem("dateFinish"))) {
		dateStart.value = localStorage.getItem("dateStart");
		dateFinish.value = localStorage.getItem("dateFinish");
		getRoute();
	} else {
		div.innerHTML = "Дата начала не может быть позже даты окончания запроса"
	}
}
//конец костыля
dateStart.addEventListener('change', function() {
	if (dateStart.value != "" && dateFinish.value != "") {
		if (new Date(dateStart.value) < new Date(dateFinish.value)) {
			localStorage.setItem("dateStart", dateStart.value);
			getRoute();
		} else {
			div.innerHTML = "Дата начала не может быть позже даты окончания запроса"
		}

	}

})
dateFinish.addEventListener('change', function() {
	if (dateStart.value != "" && dateFinish.value != "") {
		if (new Date(dateStart.value) < new Date(dateFinish.value)) {
			localStorage.setItem("dateFinish", dateFinish.value);
			getRoute();
		} else {
			div.innerHTML = "Дата начала не может быть позже даты окончания запроса"
		}
	}

})

function getRoute() {
	var xhr = new XMLHttpRequest();
	xhr.open(
		'GET',
		`${getArchiveRoutesBaseUrl}&${dateStart.value}&${dateFinish.value}`,
		true
	)
	xhr.send();
	xhr.onreadystatechange = function() {
		if (xhr.readyState != 4) {
			return
		}

		if (xhr.status === 200) {
			div.innerHTML = "";
			var obj = JSON.parse(xhr.responseText); // получаем обратно ответ со стрингом рабочего дня, преобразуем в объект
			if (xhr.responseText.length <= 2) {
				div.innerHTML = "Маршруты отсутствуют"
			}
			var tableDiv = document.createElement('table');
			var tbody = document.createElement('tbody');
			var thead = document.createElement('thead');
			var trth = document.createElement('tr');
						trth.innerHTML =`
						<th>Название маршрута</th>
						<th>Дата загрузки</th>
						<th>Груз</th>
						<th>Количество паллет</th>
						<th>Вес</th>
						<th>Цена перевозки</th>`;

//			var th1 = document.createElement('th');
//			th1.innerHTML = "Название маршрута";
//			trth.appendChild(th1);
//			var th2 = document.createElement('th');
//			th2.innerHTML = "Дата загрузки";
//			trth.append(th2);
//			var th3 = document.createElement('th');
//			th3.innerHTML = "Груз";
//			trth.append(th3);
//			var th4 = document.createElement('th');
//			th4.innerHTML = "Количество паллет";
//			trth.append(th4);
//			var th5 = document.createElement('th');
//			th5.innerHTML = "Вес";
//			trth.append(th5);
//			var th6 = document.createElement('th');
//			th6.innerHTML = "Цена перевозки";
//			trth.append(th6);
			
			thead.append(trth);
			tableDiv.append(thead);

			obj.forEach(route => {
				let tr = document.createElement('tr');
				tr.id = route.idRoute;
				tr.innerHTML = `
				<td width="300"><a href="../tender/tenderpage?routeId=${route.idRoute}">${route.routeDirection}</a></td>
				<td>${route.simpleDateStart}</td>
				<td>${route.roteHasShop[0].cargo} </td>
				<td>${route.totalLoadPall} </td>
				<td>${route.totalCargoWeight} </td>
				<td>${route.finishPrice} ${route.startCurrency}</td>
				`;
				tbody.append(tr);
				tableDiv.append(tbody);
			})
			tableDiv.classList.add('table');
			tableDiv.classList.add('table-bordered');
			// tableDiv.classList.add('border-primary');
			tableDiv.classList.add('table-condensed');
			tableDiv.classList.add('table-hover');
			tableDiv.classList.add('table-responsive');
			div.append(tableDiv);


		} else {
			console.log('err', xhr.responseText)
		}
	}
}




