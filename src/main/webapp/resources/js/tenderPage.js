var idRoute = document.querySelector('input[name=idRoute]').value;
changeCost();
import { ajaxUtils } from './ajaxUtils.js';
import { ws, wsTenderMessagesUrl } from './global.js';
import { deleteTenderOfferUrl, getInfoParticipantsMessageBaseUrl, getInfoRouteMessageBaseUrl, getRouteBaseUrl, setTenderCostFromCarrierUrl, setTenderOfferUrl } from './globalConstants/urls.js';
import { createToast, playNewToastSound } from './Toast.js';
import { disableButton, enableButton, SmartWebSocket } from './utils.js';

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector('#login')?.value
// ws.onopen = () => onOpenSock();
// ws.onmessage = (e) => onMessage(JSON.parse(e.data));

document.addEventListener('DOMContentLoaded', () => {
	const startPriceForReduction = document.getElementById('startPriceForReduction')?.value
	if (startPriceForReduction) initTenderForReduction(startPriceForReduction)

	// отмена предложения по тендеру
	const deleteOfferBtn = document.getElementById('deleteOffer')
	deleteOfferBtn && deleteOfferBtn.addEventListener('click', deleteOfferBtnClickHandler)

	// форма установки предложения по тендеру
	const tenderOfferForm = document.querySelector('#tenderOfferForm')
	tenderOfferForm && tenderOfferForm.addEventListener('submit', tenderOfferFormSubmitHandler)

	new SmartWebSocket(`${wsTenderMessagesUrl}?user=${login}`, {
		reconnectInterval: 5000,
		maxReconnectAttempts: 5,
		onMessage: tenderSocketOnMessage,
		onClose: () => alert('Соединение с сервером потеряно. Перезагрузите страницу')
	})
})

function initTenderForReduction(startPriceForReduction) {
	const discountInput = document.getElementById('discount')
	const increaseBtn = document.getElementById('increase')
	const decreaseBtn = document.getElementById('decrease')
	const finalPriceInput = document.getElementById('final-price')

	const basePrice = parseInt(startPriceForReduction)
	const min = parseInt(discountInput.min)
	const max = parseInt(discountInput.max)

	function updateButtons() {
		const value = parseInt(discountInput.value)
		decreaseBtn.disabled = value <= min
		increaseBtn.disabled = value >= max
	}
	function updateUI() {
		updateButtons()
		updateFinalPrice()
	}
	function updateFinalPrice() {
		const discount = parseInt(discountInput.value)
		const finalPrice = basePrice * (1 - discount / 100)
		finalPriceInput.value = Math.round(finalPrice)
	}

	increaseBtn.addEventListener('click', () => {
		let value = parseInt(discountInput.value)
		if (value < max) {
			discountInput.value = value + 1
			updateUI()
		}
	})
	decreaseBtn.addEventListener('click', () => {
		let value = parseInt(discountInput.value)
		if (value > min) {
			discountInput.value = value - 1
			updateUI()
		}
	})

	updateUI()
}

// действия на сообщения от сокета тендеров
async function tenderSocketOnMessage(e) {
	const data = JSON.parse(e.data)

	if (data.status === '120') {
		return
	}

	if (data.status === '200') {
		if (data.wspath !== 'carrier-tenders') return

		const { action, idRoute: targetIdRoute, } = data
		if (!action) return
		if (targetIdRoute !== idRoute) return

		// превращение закрытого тендера в тендер на понижение
		if (action === 'change-tender-type') {
			alert('Тип тендера изменен - страница будет обновлена')
			document.location.reload()
		}

		// отмена тендера
		else if (action === 'cancel-tender') {
			alert('Тендер отменен - страница будет обновлена')
			document.location.reload()
		}

		// тендер завершен
		else if (action === 'finish-tender') {
			alert('Тендер завершен - страница будет обновлена')
			document.location.reload()
		}

		// уведомления перевозчикам
		else if (action === 'notification') {

			const toastOption = {
				date: new Date().getTime(),
				toUser: data.toUser,
				text: data.text,
				url: data.url,
				autoCloseTime: 10000
			}

			createToast(toastOption)
			playNewToastSound()
		}
	}
}


// удаление предложения
function deleteOfferBtnClickHandler() {
	const idCarrierBid = tenderOfferForm.idCarrierBid?.value
	const idRoute = tenderOfferForm.idRoute.value

	if (!idCarrierBid || !idRoute) return

	const payload = { idCarrierBid, idRoute }
	deleteOffer(payload)
}

// форма установки предложения
function tenderOfferFormSubmitHandler(e) {
	e.preventDefault()

	const submitter = e.submitter
	disableButton(submitter)

	// отмена предложения
	const submitBtnName = submitter.name
	if (submitBtnName === 'notagree') {
		cancelCostPOST_Old(submitter)
		return
	}
	
	const formdata = new FormData(e.target)
	const data = Object.fromEntries(formdata)
	const payload = {
		idRoute: data.idRoute ? Number(data.idRoute) : null,
		percent: data.discount ? Number(data.discount) : null,
		price: data.price ? Number(data.price) : null,
		comment: data.comment ? data.comment : null,
		currency: data.currency ? data.currency : null,
		idCarrierBid: data.idCarrierBid ? Number(data.idCarrierBid) : null,
	}

	sendOffer(payload, submitter)
}

// отмена предложения по старой системе
function cancelCostPOST_Old(submitter) {
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
				enableButton(submitter)
				alert(res.message)
			}
		},
	})
}
// установка предложения тендера
function sendOffer(data, submitter) {
	ajaxUtils.postJSONdata({
		url: setTenderOfferUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				backToTender()
			} else {
				enableButton(submitter)
				alert(res.message)
			}
		},
	})
}
// отмена предложения тендера
function deleteOffer(data) {
	ajaxUtils.postJSONdata({
		url: deleteTenderOfferUrl,
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
	localStorage.setItem('regionalTenderButtonAgree?'+document.querySelector('input[name=idRoute]').value, document.querySelector('#regionalTenderPriceAgree').value);
		sendMessage({
		fromUser:'system',
		toUser: 'routeManager',
		text: document.querySelector('#regionalTenderPriceAgree').value,
		idRoute: document.querySelector('input[name=idRoute]').value,
		comment: 'percent',
		status: "1"
	})
})

function backToTender() {
	setTimeout(() => {
		window.location.href = '../tender'
	}, 300);
}
