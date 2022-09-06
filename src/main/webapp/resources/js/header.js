$(document).ready(function() {
	$("#logout").click(function(e) {
		e.preventDefault();
		$("#logout-form").submit();
	});
});
import { wsHead } from './global.js';
wsHead.onmessage = (e) => onMessage(JSON.parse(e.data));
var role = document.querySelector('input[id=role]').value;

let systemMessage = new Set();

fetch(`/speedlogist/api/mainchat/messagesList`).then(function(response) {
	response.json().then(function(text) {
		text.forEach(function(item) {
			switchHasMessageToArray(item, systemMessage);
		});
		switchHasRole(role, systemMessage);

	});

});

function onMessage(mes) {
	switchHasMessageToArray(mes, systemMessage)
	switchHasRole(role, systemMessage);
	
}

function switchHasRole(role, systemMessage) {
	switch (role) {
		case '[ROLE_ADMIN]':
			if (systemMessage.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
			}
			break;
		case '[ROLE_MANAGER]':
			if (systemMessage.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
			}
			break;
		case '[ROLE_TOPMANAGER]':
			if (systemMessage.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
			}
			break;
	}
}
function switchHasMessageToArray(mes, systemMessage) {
	switch (mes.toUser) {
		case 'system':
			systemMessage.add(mes);
			break;
	}
}

