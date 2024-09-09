import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { getOrderData, getOrderStatusByStockDelivery } from './procurementFormDataUtils.js'
import {
	getAddressHTML,
	getAddressInfoHTML,
	getCargoInfoHTML,
	getCustomsAddressHTML,
	getDateHTML,
	getTimeHTML,
	getTnvdHTML,
} from "./procurementFormHtmlUtils.js"
import {
	addBelarusValueToCountryInputs,
	addListnersToPoint,
	changeEditingOptions,
	changeSubmitButtonText,
	dangerousInputOnChangeHandler,
	hideAddUnloadPointButton,
	hideFormField,
	hideMarketInfoTextarea,
	hideMarketNumberInput,
	inputEditBan,
	isInvalidPointForms,
	orderCargoInputOnChangeHandler,
	orderPallInputOnChangeHandler,
	orderWeightInputOnChangeHandler,
	setCounterparty,
	setFormName,
	setOrderDataToOrderForm,
	setWayType,
	showFormField,
	showIncotermsInsuranseInfo,
	transformAddressInputToSelect,
	transformToAhoComment,
	typeTruckOnChangeHandler,
	validatePointDates,
} from "./procurementFormUtils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { disableButton, enableButton, getData, isStockProcurement, } from './utils.js'

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

	const role = document.querySelector('#role').value
	if (isStockProcurement(role)) {
		showStockProcFormTypeModal()
	} else {
		// отображение модального окна для выбора типа маршрута
		showWayTypeModal()
	}

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
	const wayTypeInput = document.querySelector('#way')
	wayButtonsContainer.addEventListener('click', (e) => wayButtonsContainerOnClickHandler(e, wayTypeInput))
	
	// обработчик для модального окна оклейка, УКЗ, СИ, акциз
	const fullImportButtonsContainer = document.querySelector('#fullImportButtons')
	// fullImportButtonsContainer.addEventListener('click', (e) => fullImportButtonsContainerOnClickHandler(e, controlUKZSelect, addUnloadPointForm))

	const EAEUImportButtonsContainer = document.querySelector('#EAEUImportButtons')
	EAEUImportButtonsContainer.addEventListener('click', EAEUImportButtonsContainerOnClickHandler)

	// обработчик для модального окна оклейка, УКЗ, СИ, акциз
	const RBButtonsContainer = document.querySelector('#RBButtons')
	RBButtonsContainer.addEventListener('click', (e) => RBButtonsContainerOnClickHandler(e))

	// обработчик для модального окна типа заявки для роли закупок внутренних перемещений
	const stockProcFormTypeButtonsContainer = document.querySelector('#stockProcFormTypeButtons')
	stockProcFormTypeButtonsContainer.addEventListener('click', (e) => stockProcFormTypeButtonsContainerOnClickHandler(e))

	// обработчик отключения редиректа на слоты
	const disableSlotRedirectCheckbox = document.querySelector('#disableSlotRedirect')
	disableSlotRedirectCheckbox.addEventListener('change', (e) => disableSlotRedirect = e.target.checked)

	// обработчик на поле Опасный груз
	const dangerousInput = document.querySelector('#dangerous')
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
	if (orderWay === 'АХО') return "../../api/manager/addNewProcurementByMaintenance"
	return orderStatus === 20
		? "../../api/manager/addNewProcurement"
		: "../../api/manager/addNewProcurementHasMarket"
}

// превращение формы в форму внутренних перевозок
function transformToInternalMovementForm() {
	isInternalMovement = true
	const isInternalMovementInput = document.querySelector('#isInternalMovement')
	isInternalMovementInput.value = 'true'
	orderWay = 'РБ'
	// изменяем название формы
	setFormName('Форма создания заявки (внутреннее перемещение)')
	// установка контрагента для внутренних перемещений
	setCounterparty('ЗАО "Доброном"')
	// установка типа маршрута
	setWayType(orderWay)
	// добавляем тип маршрута в текст кнопки создания заявки
	changeSubmitButtonText('внутреннее перемещение')
	// скрываем поля с информацией из Маркета
	hideMarketNumberInput()
	hideMarketInfoTextarea()
}

// превращение формы в форму перевозок АХО
function transformToAhoForm() {
	orderWay = 'АХО'
	// изменяем название формы
	setFormName('Форма создания заявки (перевозка АХО)')
	// установка контрагента для АХО
	setCounterparty('ЗАО "Доброном"')
	// установка типа маршрута
	setWayType(orderWay)
	// скрываем поля с контактом контрагента
	const fio = document.querySelector('#fio')
	const contactContainer = fio.parentElement.parentElement
	contactContainer.classList.add('none')
	// изменяем поле Комментарий
	transformToAhoComment()

	showFormField('orderPall', '', true)
	showFormField('orderWeight', '', true)
	hideFormField('loadNumber')
	hideFormField('marketNumber')
	hideFormField('marketInfo')
	hideFormField('stacking')

	changeSubmitButtonText('перевозка АХО')
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
			// просим указать номер из маркета
			showSetMarketNumberModal()
			// спрашиваем про импорт из стран ТС
			// showEAEUImportModal()
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText(wayType)
			// наименование контрагента не редактируется
			inputEditBan(document, 'counterparty', true)
			// показываем дополнительные поля для Импорта
			// showFormField('recipient', 'ЗАО "Доброном"', true)
			showFormField('control', '', true)
			// showFormField('routeComments', '', false)
			// showFormField('truckLoadCapacity', '', true)
			// showFormField('truckVolume', '', true)
			// showFormField('phytosanitary', '', true)
			// showFormField('veterinary', '', true)
			// showFormField('dangerous', '', true)
		}

		if (wayType === 'РБ') {
			// показываем модалку с выриантом заявки РБ
			showRBModal()
		}

		if (wayType === 'Экспорт') {
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText(wayType)
			// скрываем поля с информацией из Маркета
			hideMarketNumberInput()
			hideMarketInfoTextarea()
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
		// // просим указать номер из маркета
		// showSetMarketNumberModal()
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
			setCounterparty('ЗАО "Доброном"')
			// добавляем тип маршрута в текст кнопки создания заявки
			changeSubmitButtonText('внутреннее перемещение')
			// скрываем поля с информацией из Маркета
			hideMarketNumberInput()
			hideMarketInfoTextarea()
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

// обработчик нажатия на кнопки модального окна типа заявки для роли закупок внутренних перемещений
function stockProcFormTypeButtonsContainerOnClickHandler(e) {
	if (e.target.classList.contains('btn')) {
		const value = e.target.dataset.value
		
		if (value === 'internalMovement') {
			// превращение в форму для внутренних перемещений
			transformToInternalMovementForm()
		}
		if (value === 'aho') {
			// превращение в форму для перевозок АХО
			transformToAhoForm()
		}

		hideStockProcFormTypeModal()
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
	console.log("🚀 ~ orderFormSubmitHandler ~ data:", data)
	if (isInvalidOrderForm(data)) {
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: getAddNewProcurementUrl(orderStatus, orderWay),
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show('Заявка создана!')
				setTimeout(() => {
					window.location.href = redirectUrl(data.status)
				}, 500)
			} else {
				snackbar.show('Возникла ошибка - попробуйте очистить форму и заполнить заново')
				enableButton(e.submitter)
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
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
	pointEditableRules(point, way, pointIndex)
	return point
}

function pointEditableRules(point, way, pointIndex) {
	if (way === 'РБ') {
		inputEditBan(point, '.country', true)
	}

	if (way === 'АХО') {
		inputEditBan(point, '.country', true)
	}

	if (way === 'РБ' && !isInternalMovement) {
		inputEditBan(point, '.country', true)
		inputEditBan(point, `#pall_${pointIndex}`, true)
	}

	if (way === 'Импорт') {
		inputEditBan(point, `#pall_${pointIndex}`, true)
	}
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
function showStockProcFormTypeModal() {
	$('#stockProcFormTypeModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideStockProcFormTypeModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#stockProcFormTypeModal').modal('hide')
}

// отображение модального окна типа заявки для роли закупок внутренних перемещений
function showSetMarketNumberModal() {
	$('#setMarketNumberModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideSetMarketNumberModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#setMarketNumberModal').modal('hide')
}
