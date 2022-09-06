var token = $("meta[name='_csrf']").attr("content");
import { ws } from './global.js';

//	$('#login').change(function() {
//		var str = document.querySelector('input[name=login]').value;
//		var jsonData = { Login: str };
//		$.ajax({
//			type: "POST",
//			url: "../api/user/isexists",
//			headers: { "X-CSRF-TOKEN": token },
//			data: JSON.stringify(jsonData),
//			contentType: 'application/json',
//			dataType: 'json',
//			success: function(html) {
//					document.querySelector('#messageLogin').innerHTML = html.message;				
//			},
//			error: function(err){
//				$('#messageLogin').html("");
//			}
//		})
//	});



$('#routeDirection').change(function() {
	document.querySelector('#message').innerHTML = '';
	var target = document.querySelector('#routeDirection').value;
	$.getJSON('../../../api/simpleroute', function(data) {
		$.each(data, function(key, val) {
			if (val.routeDirection == target) {
				document.querySelector('#message').innerHTML = 'Маршрут с данным названием уже соществует!';
			};

		})
	})
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

document.querySelector('input[name=На_выгрузке]').addEventListener('mousedown', (event) => {
	sendStatus(event.target.name);

})