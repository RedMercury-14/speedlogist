import { snackbar } from "./snackbar/snackbar.js"
import { ajaxUtils } from "./ajaxUtils.js"
import {
	addDataToRouteForm,
	addListnersToPoint,
	changeCargoInfoInputsRequired,
	changeForm,
	dangerousInputOnChangeHandler,
	getStockAddress,
	inputEditBan,
	isInvalidPointForms,
	orderCargoInputOnChangeHandler,
	orderPallInputOnChangeHandler,
	orderWeightInputOnChangeHandler,
	setOrderDataToOrderForm,
	showIncotermsInsuranseInfo,
	transformAddressInputToSelect,
	typeTruckOnChangeHandler,
	validatePointDates,
} from "./procurementFormUtils.js"
import { dateHelper, disableButton, enableButton, getData } from "./utils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import {
	getAddressHTML,
	getAddressInfoHTML,
	getCargoInfoHTML,
	getCustomsAddressHTML,
	getDateHTML,
	getTimeHTML,
	getTnvdHTML,
} from "./procurementFormHtmlUtils.js"
import { getOrderData, getOrderForForm, getOrderStatusByStockDelivery } from "./procurementFormDataUtils.js"

const redirectUrl = (orderStatus) => orderStatus === 20 || disableSlotRedirect ? "../orders" : "../../slots"
const getInternalMovementShopsUrl = "../../../api/manager/getInternalMovementShops"
// const getOrderHasMarketNumberBaseUrl = "../../../api/procurement/getOrderHasMarketNumber/"
const getMarketOrderBaseUrl = `../../../api/manager/getMarketOrder/`

const token = $("meta[name='_csrf']").attr("content")

const FORM_TYPE = 'copy'

let error = false
// отключения переадресации в слоты
let disableSlotRedirect = false
let orderData = null
let orderStatus = 20

let domesticStocks


window.onload = async () => {
	// получаем внутренние склады
	const domesticStocksData = await getData(getInternalMovementShopsUrl)
	domesticStocks = domesticStocksData.map(stock => `${stock.numshop}-${stock.address}`)
	// здесь необходимо получить заказа по id и добавить в форму его данные
	const oldOrder = await getOrderForForm(FORM_TYPE)

	const creareOrderForm = document.querySelector('#orderForm')
	const setMarketNumberForm = document.querySelector('#setMarketNumberForm')
	const cancelBtn = document.querySelector('#cancelBtn')
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	const incotermsInput = document.querySelector('#incoterms')
	const disableSlotRedirectCheckbox = document.querySelector('#disableSlotRedirect')
	const dangerousInput = document.querySelector('#dangerous')

	// измеяем форму заявки и добавляем данные
	changeForm(oldOrder, FORM_TYPE)
	addDataToRouteForm(oldOrder, creareOrderForm, createPoint)

	const isInternalMovement = oldOrder.isInternalMovement === 'true'
	const orderWay = oldOrder.way
	// просим указать номер из маркета для заказов по РБ (контрагент) и импорта
	if (orderWay === 'Импорт' || (orderWay === 'РБ' && !isInternalMovement)) {
		showSetMarketNumberModal()
	}

	// получаем точки маршрута
	const points = creareOrderForm.querySelectorAll('.point')
	// изменяем редактирование формы
	changeEditingRules(oldOrder, creareOrderForm, points)
	// обработка формы получения данных заказа по номеру из Маркета
	setMarketNumberForm.addEventListener('submit', (e) => setMarketNumberFormSubmitHandler(e, creareOrderForm))
	// листнер на изменение способа загрузки
	methodLoadInput.addEventListener('change', (e) => {
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})
	// листнер на изменение типа кузова
	typeTruckInput.addEventListener('change', (e) => {
		typeTruckOnChangeHandler(e)
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})
	// листнер на изменение условий поставки
	incotermsInput.addEventListener('change', showIncotermsInsuranseInfo)
	// обработка отправки формы
	creareOrderForm.addEventListener('submit', (e) => orderFormSubmitHandler(e))
	// листнер на отмену создания заявки
	cancelBtn.addEventListener('click', () => window.location.href = '../orders')
	// обработчик отключения редиректа на слоты
	disableSlotRedirectCheckbox.addEventListener('change', (e) => disableSlotRedirect = e.target.checked)
	// обработчик на поле Опасный груз
	// dangerousInput && dangerousInput.addEventListener('change', dangerousInputOnChangeHandler)

	// обработчик на поле Кол-во паллет ДЛЯ АХО
	const orderPallInput = document.querySelector('#orderPall')
	orderPallInput && orderPallInput.addEventListener('change', orderPallInputOnChangeHandler)

	// обработчик на поле Масса груза ДЛЯ АХО
	const orderWeightInput = document.querySelector('#orderWeight')
	orderWeightInput && orderWeightInput.addEventListener('change', orderWeightInputOnChangeHandler)

	// обработчик на поле Груз
	const orderCargoInput = document.querySelector('#cargo')
	orderCargoInput && orderCargoInput.addEventListener('change', orderCargoInputOnChangeHandler)
}

// метод получения ссылки для отправки формы
function getAddNewProcurementUrl(orderStatus, orderWay) {
	// АХО
	if (orderWay === 'АХО') return "../../../api/manager/addNewProcurementByMaintenance"
	return orderStatus === 20
		? "../../../api/manager/addNewProcurement"
		: "../../../api/manager/addNewProcurementHasMarket"
}

// обработчик отправки формы заказа
function orderFormSubmitHandler(e) {
	e.preventDefault()

	// проверяем, заполнена ли предыдущая точка
	if (isInvalidPointForms(e.target)) return

	const formData = new FormData(e.target)
	const data = getOrderData(formData, orderData, orderStatus)
	const way = data.way

	if (!validateForm(data)) {
		return
	}

	disableButton(e.submitter)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: getAddNewProcurementUrl(orderStatus, way),
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show('Заявка создана!')
				setTimeout(() => {
					window.location.href = redirectUrl(data.status)
				}, 500);
			} else {
				snackbar.show('Возникла ошибка - обновите страницу')
				enableButton(e.submitter)
			}
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => {
			enableButton(e.submitter)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// валидация формы
function validateForm(data) {
	if (!validatePointDates(data)) {
		snackbar.show('Некорректная дата загрузки либо выгрузки')
		return false
	}

	if (error) {
		snackbar.show('Проверьте данные!')
		return false
	}

	return true
}


// отображение модального окна ввода номера из Маркета
function showSetMarketNumberModal() {
	$('#setMarketNumberModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideSetMarketNumberModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#setMarketNumberModal').modal('hide')
}

// обработчик отправки формы ввода номера из Маркета
async function setMarketNumberFormSubmitHandler(e, orderForm) {
	e.preventDefault()
	e.stopImmediatePropagation()
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	const formData = new FormData(e.target)
	const markerNumber = formData.get('setMarketNumber')
	if (!markerNumber) return

	ajaxUtils.get({
		url: getMarketOrderBaseUrl + markerNumber,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (!data) {
				alert('Ошибка! Обновите страницу')
				return
			}

			const order = data.order
			if (data.status !== '200') alert(data.message)
			if (!order) return
			if (order.status !== 5) return

			orderData = order
			hideSetMarketNumberModal()
			// добавляем данные в форму заявкиы
			setOrderDataToOrderForm(orderForm, order)
			// добавляем данные в форму точкек загрузки и выгрузки
			setOrderDataToPointForm(orderForm, order)
			// // меняем статус заявки
			orderStatus = getOrderStatusByStockDelivery(order.numStockDelivery)
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// заполняет данные заказа в форме точки
function setOrderDataToPointForm(form, orderData) {
	const points = form.querySelectorAll('.point')
	points.forEach((point, i) => {
		const pointIndex = i + 1
		const form = point.querySelector(`#pointform_${pointIndex}`)
		const pointType = point.dataset.type
		const cargoInput = form.pointCargo
		const pallInput = form.pall
		const weightInput = form.weight
		const volumeInput = form.volume
		const pointAddressInput = form.address

		const oldPall = pallInput.value
		const newPall = orderData.pall

		cargoInput.value = orderData.cargo
		pallInput.value = newPall
		// pallInput.setAttribute('readonly', 'true')

		if (oldPall !== newPall) {
			weightInput.value = orderData.weight ? orderData.weight : ''
			volumeInput.value = orderData.volume ? orderData.volume : ''
		}

		if (points.length === 2 && pointType === 'Выгрузка') {
			const oldPointAddress = pointAddressInput.value
			const newPointAddress = getStockAddress(orderData.numStockDelivery)
			pointAddressInput.value = newPointAddress ? newPointAddress : oldPointAddress
		}
	})
}

// метод создания точки маршрута для заявки
function createPoint(routeData, pointData, index) {
	const point = document.createElement('div')
	const pointIndex = index + 1
	const date = dateHelper.getDateForInput(pointData.date)
	const type = pointData.type ? pointData.type : ''
	const idOrder = pointData.idOrder ? pointData.idOrder : ''
	const time = pointData.time ? pointData.time.slice(0,5) : ''
	const tnvd = pointData.tnvd ? pointData.tnvd : ''
	const bodyAddress = pointData.bodyAddress ? pointData.bodyAddress : ''
	const customsAddress = pointData.customsAddress ? pointData.customsAddress : ''

	const isInternalMovement = routeData.isInternalMovement === 'true'
	const way = routeData.way
	const EAEUImport = false

	const dateHTML = getDateHTML(isInternalMovement, type, way, pointIndex, date)
	const timeHTML = getTimeHTML(type, way, pointIndex, time)
	const tnvdHTML = getTnvdHTML(type, way, pointIndex, tnvd)
	const cargoInfoHTML = getCargoInfoHTML(routeData, isInternalMovement, way, pointIndex, pointData)
	const addressHTML = getAddressHTML(routeData, type, way, pointIndex, bodyAddress)
	const addressInfoHTML = getAddressInfoHTML(type, way, pointIndex, pointData)
	const customsAddressHTML = getCustomsAddressHTML(EAEUImport, type, way, pointIndex, customsAddress)

	point.className = 'card point'
	point.innerHTML = `
		<form class='pointForm' id='pointform_${pointIndex}' name='pointform_${pointIndex}' action=''>
			<div class='card-header d-flex justify-content-between'>
				<h5 class='d-flex align-items-center mb-0'>
					Точка ${pointIndex}: ${type}
				</h5>
				<input type='hidden' class='form-control' name='type' id='type' value='${type}'>
			</div>
			<div class='card-body'>
				<div class='row-container info-container form-group'>
					${dateHTML}
					${timeHTML}
					${cargoInfoHTML}
				</div>
				${tnvdHTML}
				${addressHTML}
				<div class='row-container form-group'>
					${addressInfoHTML}
				</div>
				${customsAddressHTML}
			</div>
		</form>
	`

	addListnersToPoint(point, way, pointIndex)

	// изменение полей адреса для заявок внутреннего перемещения
	if (isInternalMovement) {
		setTimeout(() => {
			transformAddressInputToSelect(point, domesticStocks)
			$('.selectpicker').selectpicker()
		}, 0)
	}

	return point
}

// изменение правил редактирования формы
function changeEditingRules(oldOrder, editOrderForm, points) {
	const way = oldOrder.way
	const isInternalMovement = oldOrder.isInternalMovement === 'true'
}