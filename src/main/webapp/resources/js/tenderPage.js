var idRoute = document.querySelector('input[name=idRoute]').value;
changeCost();
import { ajaxUtils } from './ajaxUtils.js';
import { ws } from './global.js';
import { deleteTenderForReductionOfferUrl, getInfoParticipantsMessageBaseUrl, getInfoRouteMessageBaseUrl, getRouteBaseUrl, setTenderCostFromCarrierUrl, setTenderForReductionOfferUrl } from './globalConstants/urls.js';
import { disableButton, enableButton } from './utils.js';
const token = $("meta[name='_csrf']").attr("content")
ws.onopen = () => onOpenSock();
ws.onmessage = (e) => onMessage(JSON.parse(e.data));
ws.onclose = (e) => onClose();

document.addEventListener('DOMContentLoaded', () => {
	const startPriceForReduction = document.getElementById('startPriceForReduction')?.value
	if (startPriceForReduction) initTenderForReduction(startPriceForReduction)
})

function isTenderForReduction() {
	const startPriceForReduction = document.getElementById('startPriceForReduction')?.value
	return !!startPriceForReduction
}

function initTenderForReduction(startPriceForReduction) {
	const cancelOfferForReductionBtn = document.getElementById('cancelOfferForReduction')
	cancelOfferForReductionBtn
	&& cancelOfferForReductionBtn.addEventListener('click', cancelOfferForReductionBtnClickHandler)

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

function cancelOfferForReductionBtnClickHandler() {
	const idCarrierBid = tenderOfferForm.idCarrierBid?.value
	const idRoute = tenderOfferForm.idRoute.value

	if (!idCarrierBid || !idRoute) return

	const payload = { idCarrierBid, idRoute }
	deleteCostForReduction(payload)
}

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
function sendCostPOST(submitter) {
	const cost = getCost()
	if (!cost) {
		enableButton(submitter)
		return
	}
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
				enableButton(submitter)
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
function cancelCostPOST(submitter) {
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
// установка предложения тендера на понижения
function sendCostForReduction(data, submitter) {
	ajaxUtils.postJSONdata({
		url: setTenderForReductionOfferUrl,
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
// отмена предложения тендера на понижения
function deleteCostForReduction(data) {
	ajaxUtils.postJSONdata({
		url: deleteTenderForReductionOfferUrl,
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

const tenderOfferForm = document.querySelector('#tenderOfferForm')
tenderOfferForm.addEventListener('submit', (e)=>{
	e.preventDefault()

	const submitter = e.submitter

	disableButton(submitter)

	if (isTenderForReduction()) {
		const formdata = new FormData(e.target)
		const data = Object.fromEntries(formdata)

		const payload = {
			idRoute: data.idRoute ? Number(data.idRoute) : null,
			percent: data.discount ? Number(data.discount) : null,
			price: data.userPriceForReduction ? Number(data.userPriceForReduction) : null,
			comment: data.comment ? data.comment : null,
			currency: data.currency ? data.currency : null,
			idCarrierBid: data.idCarrierBid ? Number(data.idCarrierBid) : null,
		}

		sendCostForReduction(payload, submitter)
		return
	}


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
					sendCostPOST(submitter)
				} else {
					alert('Недопустимое цена! Ваша цена должна быть меньше последней предложенной')
					enableButton(submitter)
				}
			} else {
				// sendCost();
				// backToTender()
				sendCostPOST(submitter)
			}
		}
	}

	// кнопка Отменить
	if (submitBtnName === 'notagree') {
		// cancelCost()
		// backToTender()
		cancelCostPOST(submitter)
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
