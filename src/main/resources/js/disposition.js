import {ws} from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
getTable();
function onMessage(message) {
	console.log(message)
	if (message.idRoute != null && message.toUser == 'disposition') {
		addPoint(message);
	}

}
function getTable() {
	$.getJSON(`../../../api/route/disposition`, function(data) {
		$.each(data, function(key, val) {
			let row = document.createElement("tr");
			row.id = val.idRoute;
			let td1 = document.createElement('td');
			td1.id = 'route'
			td1.classList.add('active');
			td1.innerHTML = val.routeDirection;
			row.appendChild(td1);
			tdConstructor('Подача_машины', row);
			tdConstructor('На_месте_зазгрузки', row)
			tdConstructor('Начали_загружать', row)
			tdConstructor('Загружена', row)
			tdConstructor('На_таможне_отправления', row)
			tdConstructor('Затаможена', row)
			tdConstructor('В_пути', row)
			tdConstructor('Проходит_границу', row)
			tdConstructor('На_таможне_назначения', row)
			tdConstructor('Растаможена', row)
			tdConstructor('На_выгрузке', row)
			document.querySelector('#table').appendChild(row);
			points(val.idRoute);
		});
	});
}

function tdConstructor(target, row) {
	let td = document.createElement('td');
	td.className = target;	
	row.appendChild(td);
}

function points(idRoute) {
	$.getJSON(`../../../api/message/disposition/${idRoute}`, function(data) {
		$.each(data, function(key, val) {
			let row = document.getElementById(idRoute);
			let td = row.querySelector(`.${val.text}`);
			td.innerHTML = val.datetime;			
			td.classList.add('active');
		});
	});
}
function addPoint(message) {
	let row = document.getElementById(message.idRoute);
	let td = row.querySelector(`.${message.text}`);
	td.innerHTML = message.datetime;
	td.classList.add('active');
}