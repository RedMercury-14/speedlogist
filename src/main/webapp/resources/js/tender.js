import {ws} from './global.js';
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
$.getJSON('../../api/info/message/routes/from_me', function(data) {
	$.each(data, function(key, val) {
		var rowItem = document.querySelectorAll('tr');
		for (let i = 1; i < rowItem.length; i++) {
			var rowItemI = rowItem[i];
			var target = rowItemI.querySelector('.none').innerHTML;
			if (target == val.idRoute) {
				rowItemI.classList.add("activRow");
				rowItemI.querySelector('#offer').innerHTML = val.text + " BYN";
			}
		}
	})
});
function onMessage(msg) {
	setTimeout(() => changeCost(), 500);
};
changeCost()
function changeCost() {
	var routeItem = document.querySelectorAll('tr');
	for (let i = 1; i < routeItem.length; i++) {
		var routeItemI = routeItem[i];
		var idRoute = routeItemI.querySelector('.none').innerHTML;
		process(routeItemI, idRoute);
	}
}

function process(routeItemI, idRoute) {
	fetch(`../../api/info/message/routes/${idRoute}`).then(function(response) {
		response.json().then(function(text) {
			if (text.length != 0) {
				routeItemI.querySelector('.targetCost').innerHTML = text[text.length - 1].text;
			} else {
				$.getJSON(`../../api/route/${idRoute}`, function(data) {
					routeItemI.querySelector('.targetCost').innerHTML = data.startPrice;
				});
			}
			var target = routeItemI;
			if (routeItemI.querySelector('#offer').innerHTML == '') {				
				target.classList.add("");
			} else if (routeItemI.querySelector('#offer').innerHTML != routeItemI.querySelector('.targetCost').innerHTML) {
				target.classList.add("attentionRow");
			}
		});
	});
}