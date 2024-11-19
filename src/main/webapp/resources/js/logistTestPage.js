import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { getStockAddress } from './globalRules/ordersRules.js'
import {
	addBelarusValueToCountryInputs,
	addListnersToPoint,
	changeEditingOptions,
	changeSubmitButtonText,
	changeTemperatureInputRequired,
	changeTruckLoadCapacityValue,
	dangerousInputOnChangeHandler,
	hideAddUnloadPointButton,
	hideFormField,
	inputEditBan,
	isInvalidPointForms,
	setFormName,
	setOrderDataToOrderForm,
	setWayType,
	showFormField,
	showIncotermsInsuranseInfo,
	transformAddressInputToSelect,
	validatePointDates,
} from "./procurementFormUtils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { dateHelper, disableButton, enableButton, getData, isStockProcurement, removeSingleQuotes, } from './utils.js'

const testOrder = {
    "idOrder": 17684,
    "counterparty": "ТОРГОВАЯ КОМПАНИЯ МИНСК КРИСТАЛЛ ТРЕЙД",
    "contact": null,
    "cargo": "Водка\"СТОЛЬГРАДНАЯ\" сув.бут.0.2л, ",
    "typeLoad": null,
    "methodLoad": null,
    "typeTruck": null,
    "temperature": null,
    "control": null,
    "comment": null,
    "status": 5,
    "dateCreate": null,
    "dateDelivery": 1723755600000,
    "manager": null,
    "telephoneManager": null,
    "stacking": null,
    "logist": null,
    "logistTelephone": null,
    "marketNumber": "19480244",
    "onloadWindowDate": null,
    "onloadWindowTime": null,
    "loadNumber": null,
    "numStockDelivery": "1700",
    "pall": "30",
    "addresses": null,
    "way": null,
    "onloadTime": null,
    "incoterms": null,
    "changeStatus": "Заказ создан в маркете: 2024-08-14",
    "needUnloadPoint": null,
    "idRamp": null,
    "timeDelivery": null,
    "timeUnload": "01:10:00",
    "loginManager": null,
    "sku": 1,
    "monoPall": 30,
    "mixPall": 0,
    "isInternalMovement": null,
    "mailInfo": null,
    "slotInfo": null,
    "dateCreateMarket": 1723582800000,
    "marketInfo": null,
    "marketContractType": null,
    "marketContractGroupId": null,
    "marketContractNumber": null,
    "marketContractorId": "118188",
    "numProduct": "2371^",
    "statusYard": null,
    "unloadStartYard": null,
    "unloadFinishYard": null,
    "pallFactYard": null,
    "weightFactYard": null,
    "marketOrderSumFirst": null,
    "marketOrderSumFinal": null,
    "arrivalFactYard": null,
    "registrationFactYard": null,
    "addressLoading": null
}

const addNewProcurementUrl = (orderStatus) => orderStatus === 20
	? "../../api/manager/addNewProcurement"
	: "../../api/manager/addNewProcurementHasMarket"
const redirectUrl = (orderStatus) => orderStatus === 20 || disableSlotRedirect ? "orders" : "../slots"
const getInternalMovementShopsUrl = "../../api/manager/getInternalMovementShops"
// const getOrderHasMarketNumberBaseUrl = "../../api/procurement/getOrderHasMarketNumber/"
const getMarketOrderBaseUrl = `../../api/manager/getMarketOrder/`

const token = $("meta[name='_csrf']").attr("content")

let error = false
// отключения переадресации в слоты
let disableSlotRedirect = false
// количество точек в форме
let pointsCounter = 0
// необходимость точки выгрузки
let needUnloadPoint = false
// внутреннее перемещение
let isInternalMovement = false
// адреса складов для внутренних перемещений
let domesticStocks
// тип маршрута
let orderWay
// данные заказа
let orderData = null
// статус заказа
let orderStatus = 20
// импорт из стран ТС
let EAEUImport = false

window.onload = async () => {
	// получаем внутренние склады
	const domesticStocksData = await getData(getInternalMovementShopsUrl)
	domesticStocks = domesticStocksData.map(stock => `${stock.numshop}-${stock.address}`)

	const wayTypeInput = document.querySelector('#way')
	const wayType = 'Импорт'
	wayTypeInput.value = wayType
	orderWay = wayType
	// изменяем название формы
	setFormName(`Форма создания заявки (${wayType})`)
	// спрашиваем про импорт из стран ТС
	showEAEUImportModal()
	// добавляем тип маршрута в текст кнопки создания заявки
	changeSubmitButtonText(wayType)
	// наименование контрагента не редактируется
	inputEditBan(document, 'counterparty', true)
	// показываем дополнительные поля для Импорта
	showFormField('recipient', 'ЗАО "Доброном"', true)
	showFormField('control', '', true)
	showFormField('routeComments', '', false)
	showFormField('truckLoadCapacity', '', true)
	showFormField('truckVolume', '90', true)
	showFormField('phytosanitary', '', true)
	showFormField('veterinary', '', true)
	showFormField('dangerous', '', true)

	const orderForm = document.querySelector('#orderForm')
	const clearOrderFormBtn = document.querySelector('#clearOrderForm')
	const pointList = document.querySelector('#pointList')

	const deleteLastPoint = document.querySelector('#deleteLastPoint')
	deleteLastPoint.addEventListener('click', (e) => deleteLastPointOnClickHandler(e, pointList))

	// форма получения данных заказа по номеру из Маркета
	const setMarketNumberForm = document.querySelector('#setMarketNumberForm')
	setMarketNumberForm.addEventListener('submit', (e) => setMarketNumberFormSubmitHandler(e, orderForm))

	// листнер на кнопки модального окна промежуточной точки загрузки
	const middleUnloadPointButtons = document.querySelector('#middleUnloadPointButtons')
	middleUnloadPointButtons.addEventListener('click', middleUnloadPointButtonsOnClichHandler)

	// листнер на изменение типа кузова
	const typeTruckInput = document.querySelector('#typeTruck')
	typeTruckInput.addEventListener('change', typeTruckOnChangeHandler)

	// листнер на изменение условий поставки
	const incotermsInput = document.querySelector('#incoterms')
	incotermsInput.addEventListener('change', showIncotermsInsuranseInfo)

	clearOrderFormBtn.addEventListener( 'click', (e) => window.location.reload())
	orderForm.addEventListener('submit', (e) => orderFormSubmitHandler(e))

	// обработчик для модального окна типа маршрута
	const wayButtonsContainer = document.querySelector('#wayButtons')
	wayButtonsContainer.addEventListener('click', (e) => wayButtonsContainerOnClickHandler(e, wayTypeInput))
	
	// обработчик для модального окна оклейка, УКЗ, СИ, акциз
	const fullImportButtonsContainer = document.querySelector('#fullImportButtons')
	// fullImportButtonsContainer.addEventListener('click', (e) => fullImportButtonsContainerOnClickHandler(e, controlUKZSelect, addUnloadPointForm))

	const EAEUImportButtonsContainer = document.querySelector('#EAEUImportButtons')
	EAEUImportButtonsContainer.addEventListener('click', EAEUImportButtonsContainerOnClickHandler)

	// обработчик для модального окна оклейка, УКЗ, СИ, акциз
	const RBButtonsContainer = document.querySelector('#RBButtons')
	RBButtonsContainer.addEventListener('click', (e) => RBButtonsContainerOnClickHandler(e))

	// обработчик отключения редиректа на слоты
	const disableSlotRedirectCheckbox = document.querySelector('#disableSlotRedirect')
	disableSlotRedirectCheckbox.addEventListener('change', (e) => disableSlotRedirect = e.target.checked)

	// обработчик на поле Опасный груз
	const dangerousInput = document.querySelector('#dangerous')
	dangerousInput && dangerousInput.addEventListener('change', dangerousInputOnChangeHandler)
}

// превращение формы в форму внутренних перевозок
function transformToInternalMovementForm() {
	isInternalMovement = true
	orderWay = 'РБ'
	// изменяем название формы
	setFormName('Форма создания заявки (внутреннее перемещение)')
	// установка контрагента для внутренних перемещений
	setInputValue(document, '#counterparty', 'ЗАО "Доброном"')
	// установка типа маршрута
	setWayType(orderWay)
	// добавляем тип маршрута в текст кнопки создания заявки
	changeSubmitButtonText('внутреннее перемещение')
	// скрываем поля с информацией из Маркета
	hideFormField('marketNumber')
	hideFormField('marketInfo')
}

// обработчик нажатия на кнопки модального окна выбора типа маршрута
function wayButtonsContainerOnClickHandler(e, wayTypeInput) {
	if (e.target.classList.contains('btn')) {
		const wayType = e.target.dataset.value
		wayTypeInput.value = wayType
		orderWay = wayType
		hideWayTypeModal()

		// изменяем название формы
		setFormName(`Форма создания заявки (${wayType})`)

		if (wayType === 'Импорт') {
			// спрашиваем про импорт из стран ТС
			showEAEUImportModal()
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText(wayType)
			// наименование контрагента не редактируется
			inputEditBan(document, 'counterparty', true)
			// показываем дополнительные поля для Импорта
			showFormField('recipient', 'ЗАО "Доброном"', true)
			showFormField('control', '', true)
			showFormField('routeComments', '', false)
			showFormField('truckLoadCapacity', '', true)
			showFormField('truckVolume', '', true)
			showFormField('phytosanitary', '', true)
			showFormField('veterinary', '', true)
			showFormField('dangerous', '', true)
		}

		if (wayType === 'РБ') {
			// показываем модалку с выриантом заявки РБ
			showRBModal()
		}

		if (wayType === 'Экспорт') {
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText(wayType)
			// скрываем поля с информацией из Маркета
			hideFormField('marketNumber')
			hideFormField('marketInfo')
		}
	}
}

// обработчик нажатия на кнопки модального окна требований к импорту
function fullImportButtonsContainerOnClickHandler(e, controlUKZSelect, addUnloadPointForm) {
	if (e.target.classList.contains('btn')) {
		const fullImportType = e.target.dataset.value
		hidefullImportModal()

		if (fullImportType === 'Да') {
			// отключаем и скрываем кнопку добавления точки выгрузки
			hideAddUnloadPointButton()
			
			// флаг отсутствия точки выгрузки
			needUnloadPoint = true
			const needUnloadPointInput = document.querySelector('#needUnloadPoint')
			needUnloadPointInput.value = 'true'
		} else {
			// показываем поле "Сверка УКЗ"
			const controlContainer = document.querySelector('#control-container')
			controlContainer.classList.remove('none')
			controlUKZSelect.options[0].selected = true

			// автозаполнение значения страны в точке выгрузки
			addBelarusValueToCountryInputs(addUnloadPointForm)
		}
	}
}

function EAEUImportButtonsContainerOnClickHandler(e) {
	if (e.target.classList.contains('btn')) {
		const EAEUImportType = e.target.dataset.value
		hideEAEUImportModal()
		// устанавливаем флаг импорта из ТС
		EAEUImport = EAEUImportType === 'Да'
		if (!EAEUImport) {
			showFormField('tir', '', true)
		}

		const order = testOrder
		orderData = order
		hideSetMarketNumberModal()
		// добавляем данные в форму заявки
		const orderForm = document.querySelector('#orderForm')
		setOrderDataToOrderForm(orderForm, order)
		// меняем статус заявки
		orderStatus = getOrderStatusByStockDelivery(order.numStockDelivery)
		return
	}
}

// обработчик нажатия на кнопки модального окна вариантов перевозок по РБ
function RBButtonsContainerOnClickHandler(e) {
	if (e.target.classList.contains('btn')) {
		const RBType = e.target.dataset.value
		hideRBModal()
		if (RBType === 'domestic') {
			isInternalMovement = true
			const isInternalMovementInput = document.querySelector('#isInternalMovement')
			isInternalMovementInput.value = 'true'
			// изменяем название формы
			setFormName('Форма создания заявки (внутреннее перемещение)')
			// установка контрагента для внутренних перемещений
			setInputValue(document, '#counterparty', 'ЗАО "Доброном"')
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText('внутреннее перемещение')
			// скрываем поля с информацией из Маркета
			hideFormField('marketNumber')
			hideFormField('marketInfo')
		} else if (RBType === 'counterparty') {
			// просим указать номер из маркета
			showSetMarketNumberModal()
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText('РБ')
			// изменяем название формы
			setFormName('Форма создания заявки (заказ от контрагента)')
		}
	}
}

// обработчик нажатия на кнопки модального окна промежуточной точки загрузки
function middleUnloadPointButtonsOnClichHandler(e) {
	if (e.target.classList.contains('btn')) {
		const value = e.target.dataset.value
		const pointList = document.querySelector('#pointList')
		const pointType = value === 'Да' ? 'Загрузка' : 'Выгрузка'
		createPoint(e, pointList, pointType)
	}
}

// обработчик отправки формы указания номера из маркета
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
			// меняем статус заявки
			orderStatus = getOrderStatusByStockDelivery(order.numStockDelivery)
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// обработчики отправки форм
function orderFormSubmitHandler(e) {
	e.preventDefault()
	e.stopImmediatePropagation()

	// проверяем, заполнена ли предыдущая точка
	if (isInvalidPointForms(e.target)) return

	// контейнер для точек маршрута
	const pointList = e.target.querySelector('#pointList')

	if (e.submitter.id === 'addLoadPoint') {
		createPoint(e, pointList, 'Загрузка')
		changeEditingTruckFields(pointsCounter)
		return
	}

	if (e.submitter.id === 'addUnloadPoint') {
		// уточнение о необходимости промежуточной точки загрузки
		if (pointsCounter > 1 && hasUnloadPoint(e.target)) {
			$('#middleUnloadPointModal').modal('show')
			return
		}

		createPoint(e, pointList, 'Выгрузка')
		changeEditingTruckFields(pointsCounter)
		return
	}

	const formData = new FormData(e.target)
	const data = getOrderData(formData, orderData, orderStatus)

	if (isInvalidOrderForm(data)) {
		return
	}

	snackbar.show('Тестовая форма заполнена!')

	// disableButton(e.submitter)

	// ajaxUtils.postJSONdata({
	// 	url: addNewProcurementUrl(orderStatus),
	// 	token: token,
	// 	data: data,
	// 	successCallback: (res) => {
	// 		if (res.status === '200') {
	// 			snackbar.show('Заявка создана!')
	// 			setTimeout(() => {
	// 				window.location.href = redirectUrl(data.status)
	// 			}, 500)
	// 		} else {
	// 			snackbar.show('Возникла ошибка - попробуйте очистить форму и заполнить заново')
	// 			enableButton(e.submitter)
	// 		}
	// 	},
	// 	errorCallback: () => {
	// 		enableButton(e.submitter)
	// 	}
	// })
}

function createPoint(e, pointList, pointType) {
	const pointElement = getPointElement(orderData, orderWay, pointType, pointsCounter)
	pointList.append(pointElement)
	$('.selectpicker').selectpicker()
	e.submitter && e.submitter.blur()
	pointsCounter++
}

// валидация формы заявки
function isInvalidOrderForm(data) {
	if (data.points.length < 1) {
		snackbar.show('Необходимо добавить точки загрузки и выгрузки!')
		return true
	}

	if (!data.points.find(point => point.type === 'Загрузка')) {
		snackbar.show('Необходимо добавить точку загрузки!')
		return true
	}

	if (!data.points.find(point => point.type === 'Выгрузка') && data.needUnloadPoint === 'false') {
		snackbar.show('Необходимо добавить точку выгрузки!')
		return true
	}

	if (!validatePointDates(data)) {
		snackbar.show('Некорректная дата загрузки либо выгрузки!')
		return true
	}

	if (error) {
		snackbar.show('Проверьте данные!')
		return true
	}

	return false
}

// создание формы точки маршрута
function getPointElement(order, way, pointType, index) {
	const point = document.createElement('div')
	const pointIndex = index + 1
	const dateHTML = getDateHTML(isInternalMovement, pointType, way, pointIndex)
	const timeHTML = getTimeHTML(pointType, way, pointIndex)
	const tnvdHTML = getTnvdHTML(pointType, way, pointIndex)
	const cargoInfoHTML = getCargoInfoHTML(order, isInternalMovement, way, pointIndex)
	const addressHTML = getAddressHTML(order, pointType, way, pointIndex)
	const addressInfoHTML = getAddressInfoHTML(pointType, way, pointIndex)
	const customsAddressHTML = getCustomsAddressHTML(EAEUImport, pointType, way, pointIndex)

	point.className = 'card point'
	point.innerHTML = `
		<form class='pointForm' id='pointform_${pointIndex}' name='pointform_${pointIndex}' action=''>
			<div class='card-header d-flex justify-content-between'>
				<h5 class='d-flex align-items-center mb-0'>
					Точка ${pointIndex}: ${pointType}
				</h5>
				<input type='hidden' class='form-control' name='type' id='type' value='${pointType}'>
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

	// для внутренних перевозок делаем поле адреса выпадающим списком
	if (isInternalMovement) {
		transformAddressInputToSelect(point, domesticStocks)
		$('.selectpicker').selectpicker()
	}
	// навешиваем листнеры
	addListnersToPoint(point, way, pointIndex)
	return point
}

// проверка формы на наличие точки выгрузки
function hasUnloadPoint(form) {
	const formData = new FormData(form)
	const data = getOrderData(formData)
	const hasUnloadPoint = data.points.filter(point => point.type === 'Выгрузка').length > 0
	return hasUnloadPoint
}

// удаление последней точки маршрута
function deleteLastPointOnClickHandler(e, pointList) {
	const points = pointList.querySelectorAll('.point')
	if (points.length > 0) {
		points[points.length - 1].remove()
		pointsCounter--
	} else return
	changeEditingTruckFields(pointsCounter)
}

// блокируем редактирование полей транспорта
function changeEditingTruckFields(pointsCounter) {
	const canEdit = pointsCounter === 0
	const typeLoad = document.querySelector('#typeLoad')
	const methodLoad = document.querySelector('#methodLoad')
	const typeTruck = document.querySelector('#typeTruck')
	const incoterms = document.querySelector('#incoterms')
	const stacking = document.querySelector('#stacking')

	changeEditingOptions(typeLoad, canEdit)
	changeEditingOptions(methodLoad, canEdit)
	changeEditingOptions(typeTruck, canEdit)
	changeEditingOptions(incoterms, canEdit)
	changeEditingOptions(stacking, canEdit)
}

// отображение модального окна типа маршрута
function showWayTypeModal() {
	$('#wayTypeModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideWayTypeModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#wayTypeModal').modal('hide')
}

// отображение модального окна действий при импорте
function showfullImportModal() {
	$('#fullImportModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hidefullImportModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#fullImportModal').modal('hide')
}

// отображение модального окна импорта из ТС
function showEAEUImportModal() {
	$('#EAEUImportModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideEAEUImportModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#EAEUImportModal').modal('hide')
}

// отображение модального окна действий при импорте
function showRBModal() {
	$('#RBModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideRBModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#RBModal').modal('hide')
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








function typeTruckOnChangeHandler(e) {
	const typeTruck = e.target.value
	changeTemperatureInputRequired(typeTruck)
	showIncotermsInput(typeTruck)
	changeTruckLoadCapacityValue(typeTruck)
	truckVolumeVisibleToggler(typeTruck)
}

function showIncotermsInput(typeTruck) {
	const incotermsContainer = document.querySelector("#incoterms-container")
	const incotermsInput = document.querySelector("#incoterms")

	if (typeTruck.includes("Контейнер")) {
		incotermsContainer.classList.remove('none')
		incotermsInput.removeAttribute("disabled")
		showFormField('deliveryLocation', '', true)
	} else {
		incotermsContainer.classList.add('none')
		incotermsInput.setAttribute("disabled", true)
		hideFormField('deliveryLocation')
	}
}

function truckVolumeVisibleToggler(typeTruck, way) {
	const wayInput = document.querySelector('#way')
	const wayValue = way ? way :  wayInput && wayInput.value
	if (wayValue !== 'Импорт') return
	if (typeTruck.includes('Контейнер') || typeTruck === 'Открытый') {
		hideFormField('truckVolume')
	} else {
		showFormField('truckVolume', '90', true)
	}
}

function getDateHTML(isInternalMovement, type, way, index, value) {
	const inputValue = value ? value : ''
	const minValidDate = getMinValidDate(isInternalMovement, way)
	const typeClassName = type === 'Загрузка' ? 'loadDate' : 'unloadDate'
	const requiredMarker = way === "РБ" || way === 'Экспорт' || type === 'Загрузка' ? '<span class="text-red">*</span>' : ''
	const required = way === "РБ" || way === 'Экспорт' || type === 'Загрузка' ? 'required' : ''
	const infoMarker = type === 'Загрузка'
		? `<span id="statusInfoLabel_${index}" class="status-info-label">!</span>
			<div id="statusInfo_${index}" class="status-info">
				<p class="mb-1">При создании заявки до 11:00 текущего дня минимальная дата загрузки - завтра, после 11:00 - через 2 дня</p>
				<p class="mb-0">Для внутренних перемещений до 12:00 - завтра, после 12:00 - через 2 дня</p>
			</div>
		`
		: ''
	return `<div class='pointDate'>
				<div class="d-flex align-items-center position-relative">
					<label for='date_${index}' class='col-form-label text-muted font-weight-bold mr-2'>Дата ${requiredMarker}</label>
					${infoMarker}
				</div>
				<input value='${inputValue}' type='date' class='form-control ${typeClassName}' name='date' id='date_${index}' min='${minValidDate}' ${required}>
			</div>
		`
}
function getMinValidDate(isInternalMovement, way) {
	const minValidDate = dateHelper.getMinValidDate({
		isInternalMovement: isInternalMovement ? 'true' : 'false',
		way: way
	})
	const loadDateInputs = document.querySelectorAll('.loadDate')
	if (loadDateInputs.length === 0) return minValidDate
	const lastLoadDateInput = loadDateInputs[loadDateInputs.length - 1]
	return lastLoadDateInput ? lastLoadDateInput.value : minValidDate
}


function getTimeHTML(type, way, index, value) {
	const inputValue = value ? value : ''
	const timeOptions = getTimeOptions(inputValue)
	const typeClassName = type === 'Загрузка' ? 'loadTime' : 'unloadTime'
	const noneClassName = type === 'Выгрузка' && way !== 'Импорт' ? 'none' : ''
	const required = type === 'Загрузка' ? 'required' : ''
	const requiredMarker = type === 'Загрузка' ? '<span class="text-red">*</span>' : ''
	const timeRemark = way !== 'РБ' ? '<span class="time-mark text-muted">По местному времени</span>' : ''
	return `<div class='pointTime ${noneClassName}'>
				<label for='time_${index}' class='col-form-label text-muted font-weight-bold '>Время ${requiredMarker}</label>
				<select id='time_${index}' ${required} name="time" class="form-control ${typeClassName}">
					<option value="" hidden disabled selected> --:-- </option>
					${timeOptions}
				</select>
				${timeRemark}
			</div>
		`
}
function getTimeOptions(value) {
	const timeOptions = []
	for (let i = 0; i < 24; i++) {
		const hour = i < 10 ? `0${i}` : `${i}`
		const selectedAttrForHour = `${hour}:00` === value ? 'selected' : ''
		const selectedAttrForHalfLour = `${hour}:30` === value ? 'selected' : ''
		timeOptions.push(`<option value="${hour}:00" ${selectedAttrForHour} class="font-weight-bold">${hour}:00</option>`)
		timeOptions.push(`<option value="${hour}:30" ${selectedAttrForHalfLour}>${hour}:30</option>`)
	}
	return timeOptions.join('')
}


function getTnvdHTML(type, way, index, value) {
	if (type !== 'Загрузка') return ''
	const inputValue = value ? value : ''
	const tnvdRequired = way === "РБ" ? '' : 'required'
	const tnvdRequiredMarker = way === "РБ" ? '' : '<span class="text-red">*</span>'
	return `<div class='form-group'>
				<label for="tnvd_${index}" class='col-form-label text-muted font-weight-bold'>Коды ТН ВЭД ${tnvdRequiredMarker}</label>
				<textarea class='form-control' name='tnvd' id='tnvd_${index}' placeholder='Коды ТН ВЭД' ${tnvdRequired}>${inputValue}</textarea>
			</div>`
}


function getCargoInfoHTML(order, isInternalMovement, way, index, pointData) {
	const { pallRequiredAttr, weightRequiredAttr, volumeRequiredAttr } = getRequiredAttrs()
	const { pointCargo, pall, weight, volume } = getCargoInfo(order, index, pointData)
	const pallReadonlyAttr = !isInternalMovement && way === 'РБ'
	return `<div class='cargoName'>
				<label for='pointCargo_${index}' class='col-form-label text-muted font-weight-bold'>Наименование груза <span class='text-red'>*</span></label>
				<input type='text' class='form-control' name='pointCargo' id='pointCargo_${index}' placeholder='Наименование' value='${pointCargo}' required>
			</div>
			<div class='cargoPall'>
				<label for='pall_${index}' class='col-form-label text-muted font-weight-bold'>Паллеты, шт</label>
				<input type='number' class='form-control' name='pall' id='pall_${index}' placeholder='Паллеты, шт' min='0' value='${pall}' ${pallRequiredAttr}  ${pallReadonlyAttr}>
			</div>
			<div class='cargoWeight'>
				<label for='weight_${index}' class='col-form-label text-muted font-weight-bold'>Масса, кг</label>
				<input type='number' class='form-control' name='weight' id='weight_${index}' placeholder='Масса, кг' min='0' value='${weight}' ${weightRequiredAttr}>
			</div>
			<div class='cargoVolume'>
				<label for='volume_${index}' class='col-form-label text-muted font-weight-bold'>Объем, м.куб.</label>
				<input type='number' class='form-control' name='volume' id='volume_${index}' placeholder='Объем, м.куб.' min='0' value='${volume}' ${volumeRequiredAttr}>
			</div>`

	
}
function getCargoInfo(order, pointIndex, pointData) {
	const cargoInfo = {
		pointCargo: '',
		pall: '',
		weight: '',
		volume: '',
	}

	// если есть данные о точке
	if (pointData) {
		cargoInfo.pointCargo = pointData.cargo ? pointData.cargo : ''
		cargoInfo.pall = pointData.pall ? pointData.pall : ''
		cargoInfo.weight = pointData.weight ? pointData.weight : ''
		cargoInfo.volume = pointData.volume ? pointData.volume : ''
		return cargoInfo
	}

	// если точка не первая, то берем данные из предыдущей точки
	if (pointIndex > 1) {
		return getPrevPointCargoInfo(pointIndex)
	}

	// если данных от заказа нет, то поля пустые
	if (!order) return cargoInfo

	cargoInfo.pointCargo = order.cargo ? order.cargo : ''
	cargoInfo.pall = order.pall ? order.pall : ''
	cargoInfo.weight = order.weight ? order.weight : ''
	cargoInfo.volume = order.volume ? order.volume : ''
	return cargoInfo
}
function getPrevPointCargoInfo(pointIndex) {
	const cargoInfo = {
		pointCargo: '',
		pall: '',
		weight: '',
		volume: '',
	}
	const prevIndex = pointIndex - 1
	const pointCargoInput = document.querySelector(`#pointCargo_${prevIndex}`)
	const pallInput = document.querySelector(`#pall_${prevIndex}`)
	const weightInput = document.querySelector(`#weight_${prevIndex}`)
	const volumeInput = document.querySelector(`#volume_${prevIndex}`)
	cargoInfo.pointCargo = pointCargoInput ? pointCargoInput.value : ''
	cargoInfo.pall = pallInput ? pallInput.value : ''
	cargoInfo.weight = weightInput ? weightInput.value : ''
	cargoInfo.volume = volumeInput ? volumeInput.value : ''
	return cargoInfo
}

function getRequiredAttrs() {
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	if (typeTruckInput.value.includes('Контейнер')) {
		return {
			pallRequiredAttr: '',
			weightRequiredAttr: '',
			volumeRequiredAttr: '',
		}
	} else {
		if (methodLoadInput.value === 'Навалом') {
			return {
				pallRequiredAttr: '',
				weightRequiredAttr: '',
				volumeRequiredAttr: 'required',
			}
		} else {
			return {
				pallRequiredAttr: 'required',
				weightRequiredAttr: 'required',
				volumeRequiredAttr: '',
			}
		}
	}
}


function getAddressHTML(order, type, way, index, value) {
	const addressName = type === 'Загрузка' ? 'загрузки' : 'выгрузки'
	const country = getCountry(type, way, value)
	if (way === 'Импорт' && type === 'Загрузка') {
		const { postIndex, region, city, street, building, buildingBody } = getImportAddressObj(value)
		return `
			<div class="form-group">
				<label for="country_${index}" class='col-form-label text-muted font-weight-bold'>Адрес ${addressName} <span class='text-red'>*</span></label>
				<div class="address-container--import">
					<div class="row-container">
						<div class="autocomplete">
							<input value='${country}' type="text" class="form-control country withoutСommas" name="country" id="country_${index}" autocomplete="off" placeholder="Страна *" required>
						</div>
						<input value='${postIndex}' type="number" class="form-control postIndex withoutСommas" name="postIndex" id="postIndex_${index}" placeholder="Индекс *" required>
						<input value='${region}' type="text" class="form-control region withoutСommas" name="region" id="region_${index}" autocomplete="off" placeholder="Регион/область *" required>
					</div>
					<div class="row-container">
						<input value='${city}' type="text" class="form-control city withoutСommas" name="city" id="city_${index}" placeholder="Город *" required>
						<input value='${street}' type="text" class="form-control street withoutСommas" name="street" id="street_${index}" placeholder="Улица *" required>
						<input value='${building}' type="text" class="form-control building withoutСommas" name="building" id="building_${index}" placeholder="Здание *" required>
						<input value='${buildingBody}' type="text" class="form-control buildingBody withoutСommas" name="buildingBody" id="buildingBody_${index}" placeholder="Корпус">
					</div>
				</div>
			</div>
		`
	}
	const address = getAddress(order, type, way, value)
	return `<div class="form-group">
				<label for="country_${index}" class="col-form-label text-muted font-weight-bold">Адрес ${addressName} <span class="text-red">*</span></label>
				<div class="form-group address-container">
					<div class="autocomplete">
						<input type="text" class="form-control country" name="country" id="country_${index}" placeholder="Страна" autocomplete="off" value='${country}' required>
					</div>
					<input type="text" class="form-control address-input" name="address" id="address_${index}" autocomplete="off" placeholder="Город, улица и т.д." value='${address}' required>
				</div>
			</div>`
}
function getImportAddressObj(addressValue) {
	const obj = {
		postIndex: '',     // Почтовый индекс
		region: '',        // Регион
		city: '',          // Город
		street: '',        // Улица
		building: '',      // Номер здания
		buildingBody: ''   // Корпус здания
	}
	// Если addressValue отсутствует, возвращаем пустой объект obj
	if (!addressValue) return obj

	const [ country, address ] = addressValue.split('; ')
	const addressParts = address.split(', ')
	obj.postIndex = addressParts[0] ? addressParts[0] : ''
	obj.region = addressParts[1] ? addressParts[1] : ''
	obj.city = addressParts[2] ? addressParts[2] : ''
	obj.street = addressParts[3] ? addressParts[3] : ''
	obj.building = addressParts[4] ? addressParts[4] : ''
	obj.buildingBody = addressParts[5] ? addressParts[5] : ''
	return obj
	
}
function getAddress(order, type, way, addressValue) {
	let address = ''
	if (addressValue) {
		return addressValue.split('; ')[1]
	}
	if (!order || !order.numStockDelivery) return ''
	if ((type === 'Выгрузка' && way === 'Импорт')
		|| (type === 'Выгрузка' && way === 'РБ')
		|| (type === 'Загрузка' && way === 'Экспорт')
	) address = getStockAddress(order.numStockDelivery)
	return address
}
function getCountry(type, way, addressValue) {
	let country = ''
	if (type === 'Выгрузка' && way === 'Импорт') country = 'BY Беларусь'
	if (way === 'РБ') country = 'BY Беларусь'
	if (type === 'Загрузка' && way === 'Экспорт') country = 'BY Беларусь'
	if (addressValue) country = addressValue.split('; ')[0]
	return country
}


function getAddressInfoHTML(type, way, index, pointData) {
	const {
		weekdaysTF_from,
		weekdaysTF_to,
		saturdayTF_from,
		saturdayTF_to,
		saturdayTF_NotWork,
		sundayTF_from,
		sundayTF_to,
		sundayTF_NotWork,
	} = getTimeFrameInfo(pointData)

	const satutdayTFDisabledAttr = saturdayTF_NotWork ? 'disabled' : ''
	const sundayTFDisabledAttr = sundayTF_NotWork ? 'disabled' : ''

	const saturdayTF_NotWorkCheckedAttr = saturdayTF_NotWork ? 'checked' : ''
	const sundayTF_NotWorkCheckedAttr = sundayTF_NotWork ? 'checked' : ''

	const textareaRows = way === 'Импорт' ? 3 : 1

	// если есть данные (для маршрутов, копирования и редактирования), то общее поле контакта
	const contactInputs = pointData
		? `<textarea class="form-control contact" rows="${textareaRows}" name="pointContact" id="pointContact_${index}" placeholder="ФИО, телефон">${pointData.contact}</textarea>`
		: `
			<input type="text" class="form-control" name="pointContact_fio" id="pointContact_fio_${index}" placeholder="ФИО" required>
			<input type="text" class="form-control" name="pointContact_tel" id="pointContact_tel_${index}" placeholder="Телефон" required>
		`
	const addressName = type === 'Загрузка' ? 'загрузки' : 'выгрузки'
	return way === 'Импорт'
		? `<div class="timeFrame-container--import">
				<span class="col-form-label text-muted font-weight-bold d-inline-block">Время работы точки ${addressName} <span class="text-red">*</span></span>
				<div class="timeFrame-inputs--import">
					<label for='weekdaysTimeFrame_from_${index}' class="grid-item1 col-form-label text-muted text-nowrap">Будние дни:</label>
					<span class="grid-item2 ">С</span>
					<input value='${weekdaysTF_from}' list="times" type="time" class="grid-item3 form-control" name="weekdaysTimeFrame_from" id="weekdaysTimeFrame_from_${index}" required>
					<span class="grid-item4 ">по</span>
					<input value='${weekdaysTF_to}' list="times" type="time" class="grid-item5 form-control" name="weekdaysTimeFrame_to" id="weekdaysTimeFrame_to_${index}" required>
					<label for='saturdayTimeFrame_from_${index}' class="grid-item6 col-form-label text-muted text-nowrap">Суббота:</label>
					<span class="grid-item7 ">С</span>
					<input value='${saturdayTF_from}' ${satutdayTFDisabledAttr} list="times" type="time" class="grid-item8 form-control" name="saturdayTimeFrame_from" id="saturdayTimeFrame_from_${index}" required>
					<span class="grid-item9 ">по</span>
					<input value='${saturdayTF_to}' ${satutdayTFDisabledAttr} list="times" type="time" class="grid-item10 form-control" name="saturdayTimeFrame_to" id="saturdayTimeFrame_to_${index}" required>
					<input class="grid-item11 " type="checkbox" ${saturdayTF_NotWorkCheckedAttr} name="saturdayTimeFrame_NotWork" id="saturdayTimeFrame_NotWork_${index}">
					<label class="grid-item12 form-check-label text-nowrap" for="saturdayTimeFrame_NotWork_${index}">Не работают</label>
					<label for='sundayTimeFrame_from_${index}' class="grid-item13 col-form-label text-muted text-nowrap">Воскресенье:</label>
					<span class="grid-item14 ">С</span>
					<input value='${sundayTF_from}' ${sundayTFDisabledAttr} list="times" type="time" class="grid-item15 form-control" name="sundayTimeFrame_from" id="sundayTimeFrame_from_${index}" required>
					<span class="grid-item16 ">по</span>
					<input value='${sundayTF_to}' ${sundayTFDisabledAttr} list="times" type="time" class="grid-item17 form-control" name="sundayTimeFrame_to" id="sundayTimeFrame_to_${index}" required>
					<input class="grid-item18 " type="checkbox" ${sundayTF_NotWorkCheckedAttr} name="sundayTimeFrame_NotWork" id="sundayTimeFrame_NotWork_${index}">
					<label class="grid-item19 form-check-label text-nowrap" for="sundayTimeFrame_NotWork_${index}">Не работают</label>
				</div>
			</div>
			<div class="contact-container--import">
				<label for="pointContact_fio_${index}" class="col-form-label text-muted font-weight-bold">Контактное лицо на точке ${addressName} <span class="text-red">*</span></label>
				<div class="contact-inputs--import">
					${contactInputs}
				</div>
			</div>
		`	
		: `<div class="timeFrame-container">
				<label for='timeFrame_from_${index}' class="col-form-label text-muted font-weight-bold">Время работы точки ${addressName} <span class="text-red">*</span></label>
				<div class="input-row-container">
					С
					<input value='${weekdaysTF_from}' list="times" type="time" class="form-control" name="timeFrame_from" id="timeFrame_from_${index}" required>
					по
					<input value='${weekdaysTF_to}' list="times" type="time" class="form-control" name="timeFrame_to" id="timeFrame_to_${index}" required>
				</div>
			</div>
			<div class="contact-container">
				<label for="pointContact_fio_${index}" class="col-form-label text-muted font-weight-bold">Контактное лицо на точке ${addressName} <span class="text-red">*</span></label>
				<div class="contact-inputs">
					${contactInputs}
				</div>
			</div>
		`
}
function getTimeFrameInfo(pointData) {
	const timeFrameInfo = {
		weekdaysTF_from: '', // Время начала работы в будние дни
		weekdaysTF_to: '',   // Время окончания работы в будние дни
		saturdayTF_from: '', // Время начала работы в субботу
		saturdayTF_to: '',   // Время окончания работы в субботу
		saturdayTF_NotWork: false, // Флаг, указывающий, что в субботу не работает
		sundayTF_from: '',   // Время начала работы в воскресенье
		sundayTF_to: '',     // Время окончания работы в воскресенье
		sundayTF_NotWork: false, // Флаг, указывающий, что в воскресенье не работает
	}

	// Если нет данных, возвращаем пустой объект timeFrameInfo
	if (!pointData || !pointData.timeFrame) return timeFrameInfo

	const { timeFrame } = pointData
	const timeFrameParts = timeFrame.split('; ')

	// Если временная рамка указана только для одного интервала (будние дни)
	if (timeFrameParts.length === 1) {
		const timeFrame = getTimeFrameOrStatus(timeFrameParts[0])
		timeFrameInfo.weekdaysTF_from = timeFrame[0] ? timeFrame[0] : '' // Установка времени начала для будних дней
		timeFrameInfo.weekdaysTF_to = timeFrame[1] ? timeFrame[1] : ''   // Установка времени окончания для будних дней
	}

	// Если временная рамка указана для трех интервалов (будние дни, суббота и воскресенье)
	if (timeFrameParts.length === 3) {
		// Получение временных рамок для каждого из дней
		const weekdaysTimeFrame = getTimeFrameOrStatus(timeFrameParts[0])
		const saturdayTimeFrame = getTimeFrameOrStatus(timeFrameParts[1])
		const sundayTimeFrame = getTimeFrameOrStatus(timeFrameParts[2])

		// Обработка временных рамок для будних дней
		if (weekdaysTimeFrame.length === 2) {
			timeFrameInfo.weekdaysTF_from = weekdaysTimeFrame[0] // Время начала работы в будние дни
			timeFrameInfo.weekdaysTF_to = weekdaysTimeFrame[1]   // Время окончания работы в будние дни
		}

		// Обработка временных рамок для субботы
		if (saturdayTimeFrame.length === 2) {
			timeFrameInfo.saturdayTF_from = saturdayTimeFrame[0] // Время начала работы в субботу
			timeFrameInfo.saturdayTF_to = saturdayTimeFrame[1]   // Время окончания работы в субботу
		} else if (saturdayTimeFrame.length === 1) {
			timeFrameInfo.saturdayTF_NotWork = true // Установка флага, что суббота - выходной
		}

		// Обработка временных рамок для воскресенья
		if (sundayTimeFrame.length === 2) {
			timeFrameInfo.sundayTF_from = sundayTimeFrame[0] // Время начала работы в воскресенье
			timeFrameInfo.sundayTF_to = sundayTimeFrame[1]   // Время окончания работы в воскресенье
		} else if (sundayTimeFrame.length === 1) {
			timeFrameInfo.sundayTF_NotWork = true // Установка флага, что воскресенье - выходной
		}
	}
	return timeFrameInfo
}
function getTimeFrameOrStatus(text) {
	// Регулярное выражение для поиска времени в формате "с hh:mm по hh:mm" или фразы "не работают" (без учета регистра)
	const pattern = /с\s(\d{2}:\d{2})\sпо\s(\d{2}:\d{2})|не работают/i
	const match = text.match(pattern)

	if (match) {
		// Если найдены оба времени начала и окончания (группы 1 и 2), возвращаем их в виде массива
		if (match[1] && match[2]) {
			return [match[1], match[2]]
		} 
		// Если найдено совпадение с текстом "не работают", возвращаем его в массиве
		else if (match[0] === "не работают") {
			return ["не работают"]
		}
	}
	
	// Если нет совпадений, возвращаем пустой массив
	return []
}


function getCustomsAddressHTML(EAEUImport, type, way, index, value) {
	const addressName = type === 'Загрузка' ? '(таможня отправления)' : '(таможня назначения)'
	if (way === 'РБ' || EAEUImport) return ''
	if (way === 'Экспорт') {
		const customsCountry = getCountry(null, null, value)
		const customsAddress = getAddress(null, type, way, value)
		return `<div class="form-group">
					<label for="customsCountry_${index}" class="col-form-label text-muted font-weight-bold">Место таможенного оформления ${addressName}</label>
					<div class="form-group address-container">
						<div class="autocomplete">
							<input value='${customsCountry}' type="text" class="form-control country" name="customsCountry" id="customsCountry_${index}" placeholder="Страна">
						</div>
						<input value='${customsAddress}' type="text" class="form-control address-input" name="customsAddress" id="customsAddress_${index}" placeholder="Адрес">
					</div>
				</div>
			`
	}
	// для Импорта

	const customsInPointAddress = getCustomsInPointAddress(value)
	const { country, postIndex, region, city, street, building, buildingBody } = getImportCustomsAddressObj(value, customsInPointAddress)
	const customsInPointAddressSelected = customsInPointAddress ? 'selected' : ''
	const header = type === 'Загрузка'
		? `<label for="customsInPointAddress_${index}" class='col-form-label text-muted font-weight-bold'>Место таможенного оформления ${addressName} <span class='text-red'>*</span></label>
			<div class="customsInPointAddress-container pb-3">
				<label class="sr-only" for="customsInPointAddress_${index}">Затаможка на месте?</label>
				<div class="input-group">
					<div class="input-group-prepend">
						<div class="input-group-text">Затаможка на месте загрузки?</div>
					</div>
					<select id="customsInPointAddress_${index}" name="customsInPointAddress" class="form-control" required>
						<option value="" selected hidden disabled></option>
						<option ${customsInPointAddressSelected} value="Да">Да</option>
						<option value="Нет">Нет</option>
					</select>
				</div>
			</div>
		`
		: `<label for="customsCountry_${index}" class='col-form-label text-muted font-weight-bold'>Место таможенного оформления ${addressName} <span class='text-red'>*</span></label>`
	let noneClassName = type === 'Загрузка' ? 'none' : ''
	if (value && !customsInPointAddress) noneClassName = ''
	const required = type === 'Загрузка' ? '' : 'required'
	return `<div>
				${header}
				<div class="address-container--import customsContainer_${index} ${noneClassName}">
					<div class="row-container">
						<div class="autocomplete">
							<input value='${country}' type="text" class="form-control country withoutСommas" name="customsCountry" id="customsCountry_${index}" placeholder="Страна *" ${required}>
						</div>
						<input value='${postIndex}' type="number" class="form-control postIndex withoutСommas" name="customsPostIndex" id="customsPostIndex_${index}" placeholder="Индекс *" ${required}>
						<input value='${region}' type="text" class="form-control region withoutСommas" name="customsRegion" id="customsRegion_${index}" placeholder="Регион/область *" ${required}>
					</div>
					<div class="row-container">
						<input value='${city}' type="text" class="form-control city withoutСommas" name="customsCity" id="customsCity_${index}" placeholder="Город *" ${required}>
						<input value='${street}' type="text" class="form-control street withoutСommas" name="customsStreet" id="customsStreet_${index}" placeholder="Улица *" ${required}>
						<input value='${building}' type="text" class="form-control building withoutСommas" name="customsBuilding" id="customsBuilding_${index}" placeholder="Здание *" ${required}>
						<input value='${buildingBody}' type="text" class="form-control buildingBody withoutСommas" name="customsBuildingBody" id="customsBuildingBody_${index}" placeholder="Корпус">
					</div>
				</div>
			</div>
		`
}
function getCustomsInPointAddress(addressValue) {
	if (!addressValue) return false
	return addressValue.includes('Затаможка на месте загрузки')
}
function getImportCustomsAddressObj(addressValue, customsInPointAddress) {
	const obj = {
		country: '',       // Страна
		postIndex: '',     // Почтовый индекс
		region: '',        // Регион
		city: '',          // Город
		street: '',        // Улица
		building: '',      // Номер здания
		buildingBody: ''   // Корпус здания
	}

	// Если addressValue отсутствует или указано customsInPointAddress, возвращаем пустой объект obj
	if (!addressValue || customsInPointAddress) return obj

	const [ country, address ] = addressValue.split('; ')
	const addressParts = address.split(', ')
	obj.country = country ? country : ''
	obj.postIndex = addressParts[0] ? addressParts[0] : ''
	obj.region = addressParts[1] ? addressParts[1] : ''
	obj.city = addressParts[2] ? addressParts[2] : ''
	obj.street = addressParts[3] ? addressParts[3] : ''
	obj.building = addressParts[4] ? addressParts[4] : ''
	obj.buildingBody = addressParts[5] ? addressParts[5] : ''
	return obj
}


// форматирование данных формы заявки
function getOrderData(formData, orderDataFromMarket, orderStatus) {
	const data = Object.fromEntries(formData)
	const way = data.way
	const points = getPointsData(way)
	const contact = getCounterpartyContact(data)
	const control = data.control === 'Да'
	const stacking = data.stacking === 'Да'
	const isInternalMovement = data.isInternalMovement === 'true'
	const dateDelivery = getDateDelivery(points)
	const numStockDelivery = getNumStockDelivery(isInternalMovement, points, orderDataFromMarket)
	const status = isInternalMovement ? getOrderStatusByStockDelivery(numStockDelivery) : orderStatus
	const marketNumber = getMarketNumber(isInternalMovement, status, data)

	const recipient = data.recipient
	const tir = data.tir === 'Да'
	const routeComments = data.routeComments
	const deliveryLocation = data.deliveryLocation ? data.deliveryLocation : ''
	const truckLoadCapacity = data.truckLoadCapacity
	const truckVolume = data.truckVolume
	const phytosanitary = data.phytosanitary === 'Да'
	const veterinary = data.veterinary === 'Да'
	const dangerous = data.dangerous === 'Да'
	const dangerousUN = data.dangerousUN
	const dangerousClass = data.dangerousClass ? data.dangerousClass : ''
	const dangerousPackingGroup = data.dangerousPackingGroup ? data.dangerousPackingGroup : ''
	const dangerousRestrictionCodes = data.dangerousRestrictionCodes ? data.dangerousRestrictionCodes : ''

	return {
		contertparty: data.counterparty,
		contact,
		control,
		way: data.way,
		marketNumber,
		orderCount: data.orderCount,
		comment: data.comment,
		temperature: data.temperature,
		typeLoad: data.typeLoad ? data.typeLoad : '',
		methodLoad: data.methodLoad ? data.methodLoad : '',
		typeTruck: data.typeTruck ? data.typeTruck : '',
		incoterms: data.incoterms ? data.incoterms : '',
		stacking,
		cargo: data.cargo,
		dateDelivery: orderDataFromMarket ? orderDataFromMarket.dateDelivery : dateDelivery,
		points,
		needUnloadPoint: data.needUnloadPoint === 'true' ? 'true' : 'false',
		loadNumber: data.loadNumber,
		isInternalMovement: isInternalMovement ? 'true' : 'false',
		status,
		idOrder: orderDataFromMarket ? orderDataFromMarket.idOrder : null,
		numStockDelivery,
		recipient,
		tir,
		routeComments,
		truckLoadCapacity,
		truckVolume,
		phytosanitary,
		veterinary,
		dangerous,
		dangerousUN,
		dangerousClass,
		dangerousPackingGroup,
		deliveryLocation,
		dangerousRestrictionCodes,
	}
}

function getMarketNumber(isInternalMovement, status, data) {
	return isInternalMovement && status === 6
		? `${new Date().getTime()}`
		: data.marketNumber
}
function getNumStockDelivery(isInternalMovement, points, orderDataFromMarket) {
	return isInternalMovement && points.length && points[points.length - 1].date
		? points[points.length - 1].bodyAdress
			.split('; ')[1]
			.split('-')[0]
		: orderDataFromMarket
			? orderDataFromMarket.numStockDelivery : null
}
function getDateDelivery(points) {
	if (points.length === 0) return ''
	const lastPoint = points[points.length - 1]
	return lastPoint.date || ''
}
function getCounterpartyContact(data) {
	const { contact, fio, tel } = data
	if (contact) return contact
	if (fio && tel) return `${fio}, тел. ${tel}`
	if (fio) return `${fio}`
	if (tel) return `Тел. ${tel}`
	return ""
}


// получение данных из форм точек маршрута
function getPointsData(way) {
	const points = []
	const pointForms = document.querySelectorAll('.pointForm')
	pointForms.forEach((form, i) => {
		const bodyAdress = getPointAddress(way, form)
		const timeFrame = getTimeFrame(way, form)
		const contact = getPointContact(way, form)
		const customsAddress = getCustomsAddress(way, form)
		points.push({
			pointNumber: i + 1,
			type: form.type.value,
			date: form.date.value,
			time: form.time.value,
			cargo: form.pointCargo ? removeSingleQuotes(form.pointCargo.value) : '',
			pall: form.pall.value,
			weight: form.weight.value,
			volume: form.volume.value,
			tnvd: form.tnvd ? form.tnvd.value : null,
			bodyAdress: removeSingleQuotes(bodyAdress),
			timeFrame,
			contact: removeSingleQuotes(contact),
			customsAddress: removeSingleQuotes(customsAddress),
		})
	})

	return points
}
function getPointAddress(orderWay, form) {
	const pointType = form.type.value
	if (orderWay === 'Импорт' && pointType === 'Загрузка') {
		let addressInfo = []
		const separator = ', '
		const country = form.country.value
		const postIndex = form.postIndex.value
		const region = form.region.value
		const city = form.city.value
		const street = form.street.value
		const building = form.building.value
		const buildingBody = form.buildingBody.value
		addressInfo.push(postIndex, region, city, street, building, buildingBody)
		const address = addressInfo.filter(item => item).join(separator).replace(/;/g, '.')
		return `${country}; ${address}`
	}
	const country = form.country.value
	const address = form.address.value.replace(/;/g, ',')
	return `${country}; ${address}`
}
function getTimeFrame(orderWay, form) {
	if (orderWay !== 'Импорт') {
		const timeFrame_from = form.timeFrame_from.value
		const timeFrame_to = form.timeFrame_to.value
		return timeFrame_from && timeFrame_to
			? `С ${timeFrame_from} по ${timeFrame_to}`
			: timeFrame_from
				? `С ${timeFrame_from}`
				: timeFrame_to
					? `По ${timeFrame_to}` : ''
	}
	// для Импорта
	const WDTF_from = form.weekdaysTimeFrame_from.value
	const WDTF_to = form.weekdaysTimeFrame_to.value
	const satTF_from = form.saturdayTimeFrame_from.value
	const satTF_to = form.saturdayTimeFrame_to.value 
	const satTF_NotWork = form.saturdayTimeFrame_NotWork.checked
	const sunTF_from = form.sundayTimeFrame_from.value
	const sunTF_to = form.sundayTimeFrame_to.value 
	const sunTF_NotWork = form.sundayTimeFrame_NotWork.checked
	const WDTF = `Будние дни: с ${WDTF_from} по ${WDTF_to}`
	const satTF = satTF_NotWork
		? 'Суббота: не работают'
		: `Суббота: с ${satTF_from} по ${satTF_to}`
	const sunTF = sunTF_NotWork
		? 'Воскресенье: не работают'
		: `Воскресенье: с ${sunTF_from} по ${sunTF_to}`
	return `${WDTF}; ${satTF}; ${sunTF}`
}
function getPointContact(orderWay, form) { 
	const pointContactInput = form.pointContact
	const pointContact_fioInput = form.pointContact_fio
	const pointContact_telInput = form.pointContact_tel

	if (!pointContactInput &&
		!pointContact_fioInput &&
		pointContact_telInput) return ''

	if (pointContactInput) {
		return pointContactInput.value ? pointContactInput.value : ''
	}

	const pointContact_fio = form.pointContact_fio.value
	const pointContact_tel = form.pointContact_tel.value
	return pointContact_fio && pointContact_tel
		? `${pointContact_fio}, тел. ${pointContact_tel}`
		: pointContact_fio
			? `${pointContact_fio}`
			: pointContact_tel
				? `Тел. ${pointContact_tel}` : ''
}
function getCustomsAddress(orderWay, form) {
	if (orderWay === 'РБ') return ''
	if (orderWay === 'Экспорт') {
		const customsCountry = form.customsCountry.value
		const customsAddress = form.customsAddress.value
			? form.customsAddress.value.replace(/;/g, ',')
			: ''
		return customsCountry && customsAddress
			? `${customsCountry}; ${customsAddress}`
			: customsCountry
				? customsCountry
				: customsAddress
					? customsAddress : ''
	}
	// для Импорта
	const customsInPointAddress = form.customsInPointAddress ? form.customsInPointAddress.value : ''
	if (customsInPointAddress === 'Да') return 'Затаможка на месте загрузки'
	if (!form.customsCountry) return ''
	let addressInfo = []
	const separator = ', '
	const country = form.customsCountry.value
	const postIndex = form.customsPostIndex.value
	const region = form.customsRegion.value
	const city = form.customsCity.value
	const street = form.customsStreet.value
	const building = form.customsBuilding.value
	const buildingBody = form.customsBuildingBody.value
	addressInfo.push(postIndex, region, city, street, building, buildingBody)
	const address = addressInfo
		.filter(item => item)
		.join(separator)
		.replace(/;/g, '.')
	return `${country}; ${address}`
}

// получение статута заявки в зависимости от склада доставки
function getOrderStatusByStockDelivery(numStockDelivery) {
	switch (numStockDelivery) {
		case '1700':
		case '1200':
		case '1230':
		case '1214':
		case '1250':
		case '1100':
		case 1700:
		case 1200:
		case 1230:
		case 1214:
		case 1250:
		case 1100:
			return 6
		default:
			return 20
	}
}

