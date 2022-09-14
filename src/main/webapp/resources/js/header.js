$(document).ready(function() {
	$("#logout").click(function(e) {
		e.preventDefault();
		$("#logout-form").submit();
	});
});
import { wsHead } from './global.js';
wsHead.onmessage = (e) => onMessage(JSON.parse(e.data));
var role = document.querySelector('input[id=role]').value;
var login = document.querySelector('#login').value;

let systemMessage = new Set();
let allMessage = new Set();

fetch(`/speedlogist/api/mainchat/messagesList&${login}`).then(function(response) {
	response.json().then(function(text) {
		text.forEach(function(item) {
			switchHasMessageToArray(item);
		});
		if (role == '[ROLE_ADMIN]' || role == '[ROLE_MANAGER]' || role == '[ROLE_TOPMANAGER]') {
			switchHasRole(role, systemMessage);
		} else if (role == '[ROLE_CARRIER]') {
			switchHasRole(role, allMessage);
		}

	});

});

function onMessage(mes) {
	console.log(mes);
	switchHasMessageToArray(mes)
	if (mes.toUser == 'system') {
		switchHasRole(role, systemMessage);
	} else if (mes.toUser == 'international') {
		switchHasRole(role, allMessage)
	} else if(mes.toUser == login){
		switchHasRole(role, allMessage)
	}


}

function switchHasRole(role, arr) {// показывает колличество сообщений по ролям(общие)
	switch (role) {
		case '[ROLE_ADMIN]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
			}
			break;
		case '[ROLE_MANAGER]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
			}
			break;
		case '[ROLE_TOPMANAGER]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
			}
			break;
		case '[ROLE_CARRIER]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = allMessage.size;
			}
			break;
	}
}
function switchHasMessageToArray(mes) {
	switch (mes.toUser) {
		case 'system':
			systemMessage.add(mes);
			break;
		case 'international':
			allMessage.add(mes);
			break;
		case login:
			allMessage.add(mes);
			break;
	}
}

