var token = $("meta[name='_csrf']").attr("content");
import { ws } from './global.js';


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