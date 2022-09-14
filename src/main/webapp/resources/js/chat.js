var token = $("meta[name='_csrf']").attr("content");
import { wsHead } from './global.js';
wsHead.onmessage = (e) => onMessage(JSON.parse(e.data));
var role = document.querySelector('input[id=role]').value;
var login = document.querySelector('#login').value;
function accordion() {
	var acc = document.querySelectorAll(".accordion");
	var i;
	for (i = 0; i < acc.length; i++) {
		acc[i].addEventListener("mousedown", function() {
			this.classList.toggle("active");
			var panel = this.nextElementSibling;
			if (panel.style.display === "block") {
				panel.style.display = "none";
			} else {
				panel.style.display = "block";
			}
		});
	}
}


setTimeout(() => getMessageFromBoot(), 100); // спрашиваем в кеше сообщения

let systemMessage = new Set();
let allMessage = new Set();
let systemMessageDB = new Set();
let allMessageDB = new Set();
//тут получаем данные из кеша
function getMessageFromBoot() {
	fetch(`/speedlogist/api/mainchat/messagesList&${login}`).then(function(response) {
		response.json().then(function(text) {
			text.forEach(function(item) {
				switchHasMessageToArrayBoot(item);
			});
			if (role == '[ROLE_ADMIN]' || role == '[ROLE_MANAGER]' || role == '[ROLE_TOPMANAGER]') {
				switchHasRole(role, systemMessage);
			} else if (role == '[ROLE_CARRIER]') {
				switchHasRole(role, allMessage);
			}
		});

	});
}



//тут получаем данные из БД
fetch(`/speedlogist/api/mainchat/massages/getfromdb&${login}`).then(function(response) {
	response.json().then(function(text) {
		text.forEach(function(item) {
			switchHasMessageToArrayDB(item);
		});
		if (role == '[ROLE_ADMIN]' || role == '[ROLE_MANAGER]' || role == '[ROLE_TOPMANAGER]') {
			switchHasRole(role, systemMessageDB);
		} else if (role == '[ROLE_CARRIER]') {
			switchHasRole(role, allMessageDB);
		}

	});

});

function switchHasMessageToArrayBoot(mes) {
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
function switchHasMessageToArrayDB(mes) {
	switch (mes.toUser) {
		case 'system':
			systemMessageDB.add(mes);
			break;
		case 'international':
			allMessageDB.add(mes);
			break;
		case login:
			allMessageDB.add(mes);
			break;
	}
}
function switchHasRole(role, arr) {
	arr.forEach(function(item) {
		switch (role) {
			case '[ROLE_ADMIN]':
				if (arr.size != 0) {
					messageFromSystemDiv(item);
				}
				break;
			case '[ROLE_MANAGER]':
				if (arr.size != 0) {
					messageFromSystemDiv(item);
				}
				break;
			case '[ROLE_TOPMANAGER]':
				if (arr.size != 0) {
					messageFromSystemDiv(item);
				}
				break;
			case '[ROLE_CARRIER]':
				if (arr.size != 0) {
					messageConstructorDiv(item, 'Сообщение от Добронома');
				}
				break;
		}
	});
}

function onMessage(mes) {
	if (mes.fromUser != 'system') {
		if (role == '[ROLE_ADMIN]' || role == '[ROLE_MANAGER]' || role == '[ROLE_TOPMANAGER]') {
			messageFromSystemDiv(mes);
		}else if (role == '[ROLE_CARRIER]' && mes.toUser == 'international') {
			messageConstructorDiv(mes, 'Сообщение от Добронома');
		}else if(mes.toUser == login){
			messageConstructorDiv(mes, 'Сообщение от Добронома');
		}
	}
}
function messageFromSystemDiv(message) {
	let messageBlock = document.createElement('div');
	messageBlock.classList.add('container');

	let button = document.createElement('button');
	if (message.status != '1') {
		button.classList.add('accordion_old');
	} else {
		button.classList.add('accordion');
	}
	button.innerHTML = 'Cooбщение системы';
	button.addEventListener("mousedown", function() {
		message.comment = document.querySelector('#login').value;
		$.ajax({
			type: "POST",
			url: "/speedlogist/api/mainchat/massage/add",
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(message),
			contentType: 'application/json',
			dataType: 'json',
			success: function(html) {

			},
			error: function(err) { }
		})
		this.classList.toggle("active");
		var panel = this.nextElementSibling;
		if (panel.style.display === "block") {
			panel.style.display = "none";
		} else {
			panel.style.display = "block";
		}
	});

	let panel = document.createElement('div');
	panel.classList.add('panel')

	let containerMessage = document.createElement('div');
	containerMessage.classList.add('container-message');
	containerMessage.innerText = message.text;

	let time = messageBlock = document.createElement('span');
	time.classList.add('time-right');
	time.innerHTML = message.datetime;

	containerMessage.appendChild(time);
	panel.appendChild(containerMessage);

	document.querySelector('#list').prepend(panel);
	document.querySelector('#list').prepend(button);
}
function messageConstructorDiv(message, buttonName) {
	let messageBlock = document.createElement('div');
	messageBlock.classList.add('container');

	let button = document.createElement('button');
	if (message.status != '1') {
		button.classList.add('accordion_old');
	} else {
		button.classList.add('accordion');
	}
	button.innerHTML = buttonName;
	button.addEventListener("mousedown", function() {
		message.comment = document.querySelector('#login').value;
		$.ajax({
			type: "POST",
			url: "/speedlogist/api/mainchat/massage/add",
			headers: { "X-CSRF-TOKEN": token },
			data: JSON.stringify(message),
			contentType: 'application/json',
			dataType: 'json',
			success: function(html) {

			},
			error: function(err) { }
		})
		this.classList.toggle("active");
		var panel = this.nextElementSibling;
		if (panel.style.display === "block") {
			panel.style.display = "none";
		} else {
			panel.style.display = "block";
		}
	});

	let panel = document.createElement('div');
	panel.classList.add('panel')

	let containerMessage = document.createElement('div');
	containerMessage.classList.add('container-message');
	containerMessage.innerText = message.text;

	let time = messageBlock = document.createElement('span');
	time.classList.add('time-right');
	time.innerHTML = message.datetime;

	containerMessage.appendChild(time);
	panel.appendChild(containerMessage);

	document.querySelector('#list').prepend(panel);
	document.querySelector('#list').prepend(button);
}