var idRoute = document.querySelector('input[name=id]').value;
changeCost();
import {ws} from './global.js';
ws.onopen = () => onOpenSock();
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
ws.onclose = (e) => onClose();
function sendCost() {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		text: document.querySelector('input[name=cost]').value,
		idRoute: idRoute,
		currency: document.querySelector('select[id=currency]').value,
		status: "1"
	})
};
try {
	document.querySelector('.agreeinternational').addEventListener("mousedown", (event) => {
		if (document.querySelector('.none') != null) {
			if (parseInt(document.querySelector('.lastCost').innerHTML) > parseInt(document.querySelector('input[name=cost]').value)) {
				sendCost();
			} else {
				alert('Недопустимое цена! Ваша цена должна быть меньше последней предложенной');
			}
		} else {
			sendCost();
		}
	})
} catch (e) { };

function onOpenSock() {
};

function onMessage(msg) {
	changeCost();
};

function onClose() {
	console.log('stop!')
};

function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function changeCost() {
	$.getJSON(`../../../api/info/message/routes/${idRoute}`, function(data) {
		if (data.length == 0) {
			$.getJSON(`../../../api/route/${idRoute}`, function(data) {
				try {
					document.querySelector('.lastCost').innerHTML = data.startPrice;
				} catch (e) { }

			});
		} else {

			var cost = '0';
			$.each(data, function(key, val) {
				if (cost == '0') {
					cost = val.text;
				} else if (parseInt(cost) > parseInt(val.text)) {
					cost = val.text;
				}
			});
			document.querySelector('.lastCost').innerHTML = cost;
		}

	});
	fetch(`../../../api/info/message/participants/${idRoute}`).then(function(response) {
		response.text().then(function(text) {
			try {
				document.querySelector('.numUsers').innerText = text;
			} catch (e) { };
		});
	});
}
function sendStatus(text) {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'disposition',
		text: text,
		idRoute: idRoute,
		status: "1"
	})
};

var buttons = document.querySelectorAll('input[type=button]')
for (let j = 0; j < buttons.length; j++) {
	let button = buttons[j];
	button.addEventListener("mousedown", (event) => {
		if (event.target.value != 'Назад') {
			sendStatus(event.target.name);
		}
	})
}
