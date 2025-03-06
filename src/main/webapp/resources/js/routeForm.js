var token = $("meta[name='_csrf']").attr("content");
import { ws } from './global.js';
import { addRoutePatternConformBaseUrl, getRouteBaseUrl, getRouteShowBaseUrl } from './globalConstants/urls.js';


//$.ajax({
//	type: "GET",
//    url : "https://www.avtodispetcher.ru/distance/?from=Орел&to=Минск",
//    success : function(result){
//        console.log(result);
//    }
//});

//var xmlhttp = new XMLHttpRequest();
//
//xmlhttp.onreadystatechange = function() {
//    if (xmlhttp.readyState == 4 && xmlhttp.status == 200) {
//        console.log(xmlhttp.responseText);
//    }
//}
//xmlhttp.open("GET", "https://www.avtodispetcher.ru/distance/?from=Орел&to=Минск", true);
//xmlhttp.send();

//fetch('https://www.avtodispetcher.ru/distance/?from=Орел&to=Минск"'), {
//  method: 'GET',
//	mode: 'no-cors'
//}
//  .then((response) => {
//   console.log(response);
//  })
//запрещаем радактировать оптимальную цену
var optimalCost = document.querySelectorAll('#optimalCost');
optimalCost.forEach(el=>{
	if(el.value != ""){
	el.readOnly = true;
}
})


function sendMessage(message) {
	ws.send(JSON.stringify(message));
}

function sendStatus(text) {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'disposition',
		text: text,
		idRoute: document.querySelector('input[id=idRoute]').value,
		status: "1"
	})
};
if (document.querySelector('input[name=На_выгрузке]') != null) {
	document.querySelector('input[name=На_выгрузке]').addEventListener('mousedown', (event) => {
		sendStatus(event.target.name);
	});
}
//if (document.querySelector('select[id=pattern]') != null) {
//	let idRoute = document.querySelector('select[id=pattern]').value;
//	document.querySelector('select[id=pattern]').onchange = function(event) {
//		idRoute = event.target.value;
//	}
//	console.log(idRoute);
//}

if (document.querySelector('select[id=pattern]') != null) {

	document.querySelector('select[id=pattern]').addEventListener('change', (event) => {
		let idRoute = event.target.value;
		$.getJSON(`${getRouteBaseUrl}${idRoute}`, function(route) {
			console.log(route.roteHasShop[0]);
			let num = route.roteHasShop.length;
			document.querySelector('#contentRoute').innerHTML = `
			<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">
			<thead class="text-center">
				<tr>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Тип транспорта</th>
					<th>Комментарии</th>	
					<th>Начальные стоимости перевозки</th>				
					<th>Оптимальная стоимость перевозки</th>
				</tr>
			</thead>
				
					<tbody>
					<tr>				
						<td><input type = "date" name="date" value="" required="true" /></td>
						<td><input type = "time" name="timeOfLoad" value="" required="true"/></td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>
						<td>${route.routeDirection}</td>
						<td>${route.typeTrailer}</td>
						<td>${route.userComments}</td>	
						<td>${route.startPrice} BYN</td>
						<td>${route.optimalCost} BYN</td>										
					</tr>
					</tbody>
							<input type="button" value="Создать маршрут">								
              			
			</table><br>
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
					<tr>
					<td>${route.roteHasShop[0].position}</td> 
					<td>${route.roteHasShop[0].weight}</td> 
					<td>${route.roteHasShop[0].pall}</td>
					<td>${route.roteHasShop[0].address}</td>
					</tr>
					<tr>
					<td>${route.roteHasShop[num - 1].position}</td> 
					<td>${route.roteHasShop[num - 1].weight}</td> 
					<td>${route.roteHasShop[num - 1].pall}</td>
					<td>${route.roteHasShop[num - 1].address}</td>
					</tr>  			
			</table>				
			</div>`;
		});
		setTimeout(() => button(idRoute), 300);

	});

}


function button(idRoute) {
	document.querySelector('input[type=button]').addEventListener('mousedown', () => {
		if (document.querySelector('input[name=date]').value == '' || document.querySelector('input[name=timeOfLoad]').value == '') {
			document.querySelector('#message').innerHTML = "Не заполнены обязательные поля";
		} else {
			let date = document.querySelector('input[name=date]').value;
			let timeOfLoad = document.querySelector('input[name=timeOfLoad]').value;
			var url = `${addRoutePatternConformBaseUrl}?idRoute=${idRoute}&date=${date}&timeOfLoad=${timeOfLoad}`
			window.location.href = url;
		}

	})
}
var idRoute = document.querySelector('#idRoute').value;
var xhr = new XMLHttpRequest()
xhr.open(
  'GET',
  `${getRouteShowBaseUrl}&${idRoute}`,
  true
)
xhr.send()
xhr.onreadystatechange = function() {
  if (xhr.readyState != 4) {
    return
  }

  if (xhr.status === 200) {
    console.log('result', xhr)
var obj = JSON.parse(xhr.responseText);
google.charts.load('current', {packages: ['corechart', 'line']});
google.charts.setOnLoadCallback(drawBasic);






//var date = obj.map(function (e) {
//  return e.dateLoadPreviously+','+e.finishPrice;
//});
//console.log(date);

//var cost = obj.map(function (e) {
//  return e.finishPrice;
//});
//console.log(cost);


function drawBasic() {
      var data = new google.visualization.DataTable();
      data.addColumn('string', 'Дата');
      data.addColumn('number', 'Цена, BYN');

	for (let i = 0; i <= obj.length-1; i++) {
		let arr = [obj[i].dateLoadPreviously, obj[i].finishPrice];
  		data.addRow(arr);
	}


      var options = {
		title: 'График изминения цены',
        hAxis: {
          title: 'Дата'
        },
        vAxis: {
          title: 'Цена'
        }
      };

      var chart = new google.visualization.LineChart(document.getElementById('curve_chart'));

      chart.draw(data, options);
    }

  } else {
    console.log('err', xhr.responseText)
  }
}





