if (/Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i.test(navigator.userAgent)) {
	console.log("mobile") // код для мобильных устройств	
	document.querySelector("#pc").classList.add('none');
} else {
	console.log("comp");
	document.querySelector("#mySidenav").classList.add('none');
	document.querySelector("#openMenu").classList.add('none');
}

$(document).ready(function() {
	$("#logout").click(function(e) {
		e.preventDefault();
		$("#logout-form").submit();
	});
});
$(document).ready(function() {
	$("#logoutMob").click(function(e) {
		e.preventDefault();
		$("#logout-form").submit();
	});
});
import { wsHead } from './global.js';
wsHead.onmessage = (e) => onMessage(JSON.parse(e.data));
var role = document.querySelector('input[id=role]').value;
var sessionCheck = document.querySelector('#sessionCheck').value;
if (document.querySelector('#login') != null) {
	var login = document.querySelector('#login').value;
}

let systemMessage = new Set();
let allMessage = new Set();
let regionalAllMessage = new Set();

// fetch(`/speedlogist/api/mainchat/messagesList&${login}`).then(function(response) {
// 	response.json().then(function(text) {
// 		text.forEach(function(item) {
// 			switchHasMessageToArray(item);
// 		});
// 		if (role == '[ROLE_ADMIN]' || role == '[ROLE_MANAGER]' || role == '[ROLE_TOPMANAGER]') {
// 			switchHasRole(role, systemMessage);
// 		} else if (role == '[ROLE_CARRIER]') {
// 			//для внутренних перевозок используется проверка на check
// 			if (sessionCheck == '') {
// 				switchHasRole(role, regionalAllMessage);
// 			} else {
// 				switchHasRole(role, allMessage);
// 			}
// 		}
// 	});

// });

function onMessage(mes) {
	switchHasMessageToArray(mes)
	if (mes.toUser == 'system') {
		switchHasRole(role, systemMessage);
	} else if (mes.toUser == 'international') {
		switchHasRole(role, allMessage)
	} else if (mes.toUser == login) {
		if (sessionCheck == '') {
			switchHasRole(role, regionalAllMessage)
		} else {
			switchHasRole(role, allMessage)
		}
	} else if (mes.toUser == 'regional') {
		switchHasRole(role, regionalAllMessage)
	}


}

function switchHasRole(role, arr) {// показывает колличество сообщений по ролям(международники)
	switch (role) {
		case '[ROLE_ADMIN]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
				document.querySelector('.badgeMobile').innerHTML = '(' + systemMessage.size + ")";
				document.querySelector('.badgeMobileTitle').innerHTML = '(новые сообщения)';
				document.querySelector('#symbolMessage').style.color = 'red';
			}
			break;
		case '[ROLE_MANAGER]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
				document.querySelector('.badgeMobile').innerHTML = '(' + systemMessage.size + ")";
				document.querySelector('.badgeMobileTitle').innerHTML = '(новые сообщения)';
				document.querySelector('#symbolMessage').style.color = 'red';
			}
			break;
		case '[ROLE_TOPMANAGER]':
			if (arr.size != 0) {
				document.querySelector('.badge').innerHTML = systemMessage.size;
				document.querySelector('.badgeMobile').innerHTML = '(' + systemMessage.size + ")";
				document.querySelector('.badgeMobileTitle').innerHTML = '(новые сообщения)';
				document.querySelector('#symbolMessage').style.color = 'red';
			}
			break;
		case '[ROLE_CARRIER]':
			if (arr.size != 0) {
				if (sessionCheck == '') {
					if (regionalAllMessage.size != 0) {
						document.querySelector('.badge').innerHTML = regionalAllMessage.size;
						document.querySelector('.badgeMobile').innerHTML = '(' + regionalAllMessage.size + ")";
						document.querySelector('.badgeMobileTitle').innerHTML = '(новые сообщения)';
						document.querySelector('#symbolMessage').style.color = 'red';
					}
				} else {
					if (allMessage.size != 0) {
						document.querySelector('.badge').innerHTML = allMessage.size;
						document.querySelector('.badgeMobile').innerHTML = '(' + allMessage.size + ")";
						document.querySelector('.badgeMobileTitle').innerHTML = '(новые сообщения)';
						document.querySelector('#symbolMessage').style.color = 'red';
					}
				}
			}
			break;
	}


}
function switchHasMessageToArray(mes) { //внесение в список сообщений в зависимости от тега сообщений	
	switch (mes.toUser) {
		case 'system':
			systemMessage.add(mes);
			break;
		case 'international':
			allMessage.add(mes);
			break;
		case login:
			if (sessionCheck == '') {
				regionalAllMessage.add(mes);
			} else {
				allMessage.add(mes);
			}
			break;
		case 'regional':
			regionalAllMessage.add(mes);
			break;
	}
}


document.querySelector("body").classList.remove('none');
