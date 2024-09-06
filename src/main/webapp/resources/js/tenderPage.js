var idRoute = document.querySelector('input[name=id]').value;
changeCost();
import { ajaxUtils } from './ajaxUtils.js';
import { ws } from './global.js';
const token = $("meta[name='_csrf']").attr("content")
ws.onopen = () => onOpenSock();
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
ws.onclose = (e) => onClose();
function sendCost() {
	if (document.querySelector('input[name=cost]').value != '' && document.querySelector('input[name=cost]').value % 1 == 0) {
		sendMessage({
			fromUser: document.querySelector('input[id=login]').value,
			text: document.querySelector('input[name=cost]').value,
			idRoute: idRoute,
			currency: document.querySelector('select[id=currency]').value,
			//nds: document.querySelector('select[id=nds]').value,
			fullName: document.querySelector('#fullName').value,
			status: "1"
		});
	}
};
function sendCostPOST() {
	if (document.querySelector('input[name=cost]').value != '' && document.querySelector('input[name=cost]').value % 1 == 0) {
		const data = {
			fromUser: document.querySelector('input[id=login]').value,
			text: document.querySelector('input[name=cost]').value,
			idRoute: idRoute,
			currency: document.querySelector('select[id=currency]').value,
			//nds: document.querySelector('select[id=nds]').value,
			fullName: document.querySelector('#fullName').value,
			status: "1"
		}
		ajaxUtils.postJSONdata({
			url: `../../../api/carrier/cost`,
			token: token,
			data: data,
			successCallback: (res) => {
				console.log(res)
				if (res.status === '200') {
					backToTender()
				} else {
					alert(res.message)
				}
			},
		})
	}
}
function cancelCost() {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		text: document.querySelector('input[name=userCost]').value,
		idRoute: idRoute,
		comment: 'delete',
		status: "1"
	});
};
function cancelCostPOST() {
	const data = {
		fromUser: document.querySelector('input[id=login]').value,
		text: document.querySelector('input[name=userCost]').value,
		idRoute: idRoute,
		comment: 'delete',
		status: "1"
	}
	ajaxUtils.postJSONdata({
		url: `../../../api/carrier/cost`,
		token: token,
		data: data,
		successCallback: (res) => {
			console.log(res)
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

	// кнопка Поддержать цену
	if (submitBtnName === 'agree') {
		if (parseInt(document.querySelector('input[name=cost]').value) > 0) {
			if (document.querySelector('#startPriceChoice') != null) {
				if (parseInt(document.querySelector('.lastCost').innerHTML) > parseInt(document.querySelector('input[name=cost]').value)) {
					sendCost();
					backToTender()
					// sendCostPOST()
				} else {
					alert('Недопустимое цена! Ваша цена должна быть меньше последней предложенной');
				}
			} else {
				sendCost();
				backToTender()
				// sendCostPOST()
			}
		}
	}

	// кнопка Отменить
	if (submitBtnName === 'notagree') {
		cancelCost()
		backToTender()
		// cancelCostPOST()
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
