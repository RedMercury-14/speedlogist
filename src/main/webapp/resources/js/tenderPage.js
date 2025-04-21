var idRoute = document.querySelector('input[name=id]').value;
changeCost();
import { ajaxUtils } from './ajaxUtils.js';
import { ws } from './global.js';
import { getInfoParticipantsMessageBaseUrl, getInfoRouteMessageBaseUrl, getRouteBaseUrl, setTenderCostFromCarrierUrl } from './globalConstants/urls.js';
const token = $("meta[name='_csrf']").attr("content")
ws.onopen = () => onOpenSock();
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
ws.onclose = (e) => onClose();

function getCost() {
	const costInput = document.querySelector('input[name=cost]')
	if (!costInput) return null
	if (!costInput.value) return null
	return parseInt(costInput.value, 10)
}

function sendCost() {
	const cost = getCost()
	if (!cost) return
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		text: cost,
		idRoute: idRoute,
		currency: document.querySelector('select[id=currency]').value,
		//nds: document.querySelector('select[id=nds]').value,
		fullName: document.querySelector('#fullName').value,
		status: "1",
		comment: document.querySelector('input[name=comment]')?.value
	})
}
function sendCostPOST() {
	const cost = getCost()
	if (!cost) return
	const data = {
		fromUser: document.querySelector('input[id=login]').value,
		text: cost,
		idRoute: idRoute,
		currency: document.querySelector('select[id=currency]').value,
		//nds: document.querySelector('select[id=nds]').value,
		fullName: document.querySelector('#fullName').value,
		status: "1",
		comment: document.querySelector('input[name=comment]')?.value
	}
	ajaxUtils.postJSONdata({
		url: setTenderCostFromCarrierUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				backToTender()
			} else {
				alert(res.message)
			}
		},
	})
}
function cancelCost() {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		text: document.querySelector('input[name=userCost]').value,
		idRoute: idRoute,
		comment: 'delete',
		status: "1"
	})
}
function cancelCostPOST() {
	const data = {
		fromUser: document.querySelector('input[id=login]').value,
		text: document.querySelector('input[name=userCost]').value,
		idRoute: idRoute,
		comment: 'delete',
		status: "1"
	}
	ajaxUtils.postJSONdata({
		url: setTenderCostFromCarrierUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				backToTender()
			} else {
				alert(res.message)
			}
		},
	})
}
// try {
// 	document.querySelector('.agreeinternational').addEventListener("mousedown", (event) => {
// 		if (parseInt(document.querySelector('input[name=cost]').value) > 0) {
// 			if (document.querySelector('#startPriceChoice') != null) {
// 				if (parseInt(document.querySelector('.lastCost').innerHTML) > parseInt(document.querySelector('input[name=cost]').value)) {
// 					sendCost();
// 				} else {
// 					alert('Недопустимое цена! Ваша цена должна быть меньше последней предложенной');
// 				}
// 			} else {
// 				sendCost();
// 			}
// 		}
// 	})
// } catch (e) { };
// try {
// 	document.querySelector('.notagreeinternational').addEventListener("mousedown", (event) => {
// 		cancelCost();
// 	})
// } catch (e) { };

function onOpenSock() {
};

function onMessage(msg) {
	// отключено для закрытых тендеров
	// changeCost();
};

function onClose() {
	console.log('stop!')
};

function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function changeCost() {
	$.getJSON(`${getInfoRouteMessageBaseUrl}${idRoute}`, function(data) {
		if (data.length == 0) {
			$.getJSON(`${getRouteBaseUrl}${idRoute}`, function(data) {
				try {
					const lastCost = document.querySelector('.lastCost');
					const raz2 = document.querySelector('.raz2');
					if (lastCost) lastCost.innerHTML = data.startPrice;
					if (raz2) raz2.value = data.startPrice;
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
			const lastCost = document.querySelector('.lastCost');
			const raz2 = document.querySelector('.raz2');
			if (lastCost) lastCost.innerHTML = cost;
			if (raz2) raz2.value = cost;
		}

	});
	fetch(`${getInfoParticipantsMessageBaseUrl}${idRoute}`).then(function(response) {
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

var buttonRegionalAgree = document.querySelector('#regionalTenderButtonAgree');
buttonRegionalAgree && buttonRegionalAgree.addEventListener('mousedown', ()=>{
	//временно
	localStorage.setItem('regionalTenderButtonAgree?'+document.querySelector('input[name=id]').value, document.querySelector('#regionalTenderPriceAgree').value);
		sendMessage({
		fromUser:'system',
		toUser: 'routeManager',
		text: document.querySelector('#regionalTenderPriceAgree').value,
		idRoute: document.querySelector('input[name=id]').value,
		comment: 'percent',
		status: "1"
	})
})

const tenderOfferForm = document.querySelector('#tenderOfferForm')
tenderOfferForm.addEventListener('submit', (e)=>{
	e.preventDefault()

	const submitBtn = e.target.querySelector('input[type=submit]')
	const submitBtnName = submitBtn.name
	const cost = getCost()

	// кнопка Поддержать цену
	if (submitBtnName === 'agree') {
		if (cost > 0) {
			if (document.querySelector('#startPriceChoice') != null) {
				if (parseInt(document.querySelector('.lastCost').innerHTML) > cost) {
					// sendCost();
					// backToTender()
					sendCostPOST()
				} else {
					alert('Недопустимое цена! Ваша цена должна быть меньше последней предложенной')
				}
			} else {
				// sendCost();
				// backToTender()
				sendCostPOST()
			}
		}
	}

	// кнопка Отменить
	if (submitBtnName === 'notagree') {
		// cancelCost()
		// backToTender()
		cancelCostPOST()
	}
})

function backToTender() {
	setTimeout(() => {
		window.location.href = '../tender'
	}, 300);
}

function isInteger(num) {
	return (num ^ 0) === num;
}
