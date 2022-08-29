var idRoute = document.querySelector('input[name=id]').value;
changeCost();
let ws = new WebSocket("ws://localhost:8080/speedlogist/chat");
ws.onopen = () => this.onOpenSock();
ws.onmessage = (e) => this.onMessage(JSON.parse(e.data));
ws.onclose = (e) => this.onClose();
function send() {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value, 
		text: document.querySelector('input[name=cost]').value,
		idRoute: idRoute,
		status: "1"
	})
};
document.querySelector('.agreeinternational').addEventListener("mousedown", (event) => {
	console.log(document.querySelector('input[name=cost]').value);
	if (document.querySelector('.none') != null) {
		if(parseInt(document.querySelector('.lastCost').innerHTML)>parseInt(document.querySelector('input[name=cost]').value)){
			send();
		}else{
			alert('Недопустимое цена! Ваша цена должна быть меньше последней предложенной');
		}
	} else {
		send();
	}
})

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
				document.querySelector('.lastCost').innerHTML = data.startPrice;
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
			document.querySelector('.numUsers').innerText = text;			
		});
	});
	
}
