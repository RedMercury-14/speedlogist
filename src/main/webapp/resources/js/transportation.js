import { ws } from './global.js';
import { wsHead } from './global.js';
//try{}catch(e){}
//ws.onmessage = (e) => onMessage(JSON.parse(e.data));
function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function sendMessageHead(message) {
	wsHead.send(JSON.stringify(message));
}

let rows = document.querySelectorAll('tr');
for (let i = 1; i < rows.length; i++) {
	let row = rows[i];
	let idRoute = row.querySelector('td[id=idTarget]').innerHTML;
	row.querySelector('#status').addEventListener('mousedown', (event) => {
		var index = row.querySelector('#option').options.selectedIndex
		var text = row.querySelector('#option').options[index].value
		sendStatus(text, idRoute)
	})
	if (row.querySelector('input[name=update]') != null) {
		row.querySelector('input[name=update]').addEventListener('mousedown', () => {
			var routeDirection = row.querySelector('td[id=routeDirection]').innerHTML;
			sendProofDriverAndCar(idRoute, routeDirection);
		})
	} else {
		row.querySelector('input[name=revers]').addEventListener('mousedown', () => {
			var routeDirection = row.querySelector('td[id=routeDirection]').innerHTML;
			sendReversDriverAndCar(idRoute, routeDirection);
		})

	}
}

function sendStatus(text, idRoute) {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'disposition',
		text: text,
		idRoute: idRoute,
		status: "1"
	})
};
function sendProofDriverAndCar(idRoute, routeDirection) {
	var companyName = document.querySelector('input[id=companyName]').value;
	sendMessageHead({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'system',
		text: `Перевозчиком ${companyName} заявлен новый водитель и авто на маршрут ${routeDirection}`,
		idRoute: idRoute,
		status: "1"
	})
};

function sendReversDriverAndCar(idRoute, routeDirection) {
	var companyName = document.querySelector('input[id=companyName]').value;
	sendMessageHead({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'system',
		text: `Перевозчиком ${companyName} отменен водитель и авто в маршруе ${routeDirection}`,
		idRoute: idRoute,
		status: "1"
	})
};