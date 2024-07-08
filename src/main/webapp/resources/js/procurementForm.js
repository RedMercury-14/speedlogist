import { ajaxUtils } from './ajaxUtils.js';
import { autocomplete } from './autocomplete/autocomplete.js';
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js';
import { countries } from './global.js';
import {
	addBelarusValueToCountryInputs,
	addCargoInfoInUnloadForm,
	addMarketNumberInputRequired,
	changeCargoInfoInputsRequired,
	changeSubmitButtonText,
	changeTemperatureInputRequired,
	controlUKZSelectOnChangeHandler,
	getOrderStatusByStockDelivery,
	getStockAddress,
	hideAddUnloadPointButton,
	hideMarketInfoTextarea,
	hideMarketNumberInput,
	removeTnvdInputRequired,
	removeUnloadDateInputRequired,
	setCounterparty,
	setFormName,
	setMinValidDate,
	setOrderDataToLoadPointForm,
	setOrderDataToOrderForm,
	setOrderDataToUnloadPointForm,
	setUnloadDateMinValue,
	setUnloadTimeMinValue,
	setWayType,
	showIncotermsInput,
	showIncotermsInsuranseInfo,
	showUnloadTime,
	transformAddressInputToSelect,
	validatePointDates,
} from "./procurementFormUtils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js';
import { disableButton, enableButton, getData, isStockProcurement, removeSingleQuotes } from './utils.js';

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
let orderWay
let orderData = null
let orderStatus = 20

window.onload = async () => {
	// установка минимальной даты загрузки/выгрузки
	setMinValidDate()

	// получаем внутренние склады
	const domesticStocksData = await getData(getInternalMovementShopsUrl)
	const domesticStocks = domesticStocksData.map(stock => `${stock.numshop}-${stock.address}`)

	const addLoadPointForm = document.querySelector('#addLoadPointForm')
	const addUnloadPointForm = document.querySelector('#addUnloadPointForm')

	const role = document.querySelector('#role').value
	if (isStockProcurement(role)) {
		// превращение в форму для внутренних перемещений
		transformToInternalMovementForm(addLoadPointForm, domesticStocks, addUnloadPointForm)
	} else {
		// отображение модального окна для выбора типа маршрута
		showWayTypeModal()
	}

	const orderForm = document.querySelector('#orderForm')
	const clearOrderFormBtn = document.querySelector('#clearOrderForm')
	const counrtyInputs = document.querySelectorAll('.country')
	const pointContainer = document.querySelector('.point-container')

	// форма получения данных заказа по номеру из Маркета
	const setMarketNumberForm = document.querySelector('#setMarketNumberForm')
	setMarketNumberForm.addEventListener('submit', (e) => setMarketNumberFormSubmitHandler(e, orderForm, addLoadPointForm, addUnloadPointForm))

	// листнер на кнопки модалного окна промежуточной точки загрузки
	const middleUnloadPointButtons = document.querySelector('#middleUnloadPointButtons')
	middleUnloadPointButtons.addEventListener('click', middleUnloadPointButtonsOnClichHandler)

	// установка значения адреса таможни
	const controlUKZSelect = document.querySelector('#control')
	// controlUKZSelect.addEventListener('change', (e) => controlUKZSelectOnChangeHandler(e, addUnloadPointForm))

	// проверка наличия заявки по номеру из маркета
	const marketNumberMessageElem = document.querySelector('#marketNumberMessage')
	const marketNumberInput = document.querySelector('#marketNumber')
	marketNumberInput.addEventListener('change', async (e) => {
		marketNumberInputOnChangeHandler(e, marketNumberMessageElem)
	})

	// установка минимальных значений даты и времени выгрузки при изменении даты
	const loadDateInput = document.querySelector('#loadDate')
	const loadTimeSelect = document.querySelector('#loadTime')
	const unloadDateInput = document.querySelector('#unloadDate')
	const unloadTimeSelect = document.querySelector('#unloadTime')
	loadDateInput.addEventListener('change', (e) => setUnloadDateMinValue(e, unloadDateInput))
	unloadDateInput.addEventListener('change', (e) => {
		setUnloadTimeMinValue(loadDateInput, loadTimeSelect, e.target, unloadTimeSelect, orderWay)
	})

	// листнер на изменение типа кузова
	const typeTruckInput = document.querySelector('#typeTruck')
	typeTruckInput.addEventListener('change', (e) => {
		const typeTruck = e.target.value
		changeTemperatureInputRequired(typeTruck)
		showIncotermsInput(typeTruck)
	})
	
	// автозаполнение выпадающего списка стран для адресов
	for (let i = 0; i < counrtyInputs.length; i++) {
		const counrtyInput = counrtyInputs[i]
		autocomplete(counrtyInput, countries)
	}

	// листнер на изменение условий поставки
	const incotermsInput = document.querySelector('#incoterms')
	incotermsInput.addEventListener('change', showIncotermsInsuranseInfo)

	// информационное окно
	const statusInfoLabels = document.querySelectorAll('#statusInfoLabel')
	const statusInfos = document.querySelectorAll('#statusInfo')
	for (let i = 0; i < statusInfoLabels.length; i++) {
		const statusInfoLabel = statusInfoLabels[i]
		const statusInfo = statusInfos[i]
		statusInfoLabel.addEventListener('mouseover', (e) => statusInfo.classList.add('show'))
		statusInfoLabel.addEventListener('mouseout', (e) => statusInfo.classList.remove('show'))
	}

	clearOrderFormBtn.addEventListener( 'click', (e) => window.location.reload())
	orderForm.addEventListener('submit', (e) => orderFormSubmitHandler(e))
	addLoadPointForm.addEventListener('submit', (e) => pointsFormSubmitHandler(e, pointContainer))
	addUnloadPointForm.addEventListener('submit', (e) => pointsFormSubmitHandler(e, pointContainer))

	// обработчик для модального окна типа маршрута
	const wayButtonsContainer = document.querySelector('#wayButtons')
	const wayTypeInput = document.querySelector('#way')
	wayButtonsContainer.addEventListener('click', (e) => wayButtonsContainerOnClickHandler(e, wayTypeInput, controlUKZSelect, addLoadPointForm, addUnloadPointForm))
	
	// обработчик для модального окна оклейка, УКЗ, СИ, акциз
	const fullImportButtonsContainer = document.querySelector('#fullImportButtons')
	// fullImportButtonsContainer.addEventListener('click', (e) => fullImportButtonsContainerOnClickHandler(e, controlUKZSelect, addUnloadPointForm))

	// обработчик для модального окна оклейка, УКЗ, СИ, акциз
	const RBButtonsContainer = document.querySelector('#RBButtons')
	RBButtonsContainer.addEventListener('click', (e) => RBButtonsContainerOnClickHandler(e, addLoadPointForm, addUnloadPointForm, domesticStocks))

	// обработчик отключения редиректа на слоты
	const disableSlotRedirectCheckbox = document.querySelector('#disableSlotRedirect')
	disableSlotRedirectCheckbox.addEventListener('change', (e) => disableSlotRedirect = e.target.checked)
}

async function setMarketNumberFormSubmitHandler(e, orderForm, addLoadPointForm, addUnloadPointForm) {
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
			setOrderDataToLoadPointForm(addLoadPointForm, order)
			setOrderDataToUnloadPointForm(addUnloadPointForm, order)
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

	if (e.submitter.id === 'addLoadPoint') {
		const modal = $('#addLoadPointModal')
		modal.modal('show')
		changeCargoInfoInputsRequired(modal[0])
		return
	}

	if (e.submitter.id === 'addUnloadPoint') {
		if (pointsCounter > 1 && hasUnloadPoint(e.target)) {
			$('#middleUnloadPointModal').modal('show')
			return
		}

		const modal = $('#addUnloadPointModal')
		modal.modal('show')
		changeCargoInfoInputsRequired(modal[0])
		return
	}

	const formData = new FormData(e.target)
	const data = orderFormDataFormatter(formData)

	if (!validateOrderForm(data)) {
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: addNewProcurementUrl(orderStatus),
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
function pointsFormSubmitHandler(e, container) {
	e.preventDefault()
	e.stopImmediatePropagation()

	const formId = e.target.id
	const modalId = formId.slice(0, -4) + 'Modal'

	pointsCounter++

	const formData = new FormData(e.target)
	const data = pointFormDataFormatter(formData)

	if (pointsCounter === 1) {
		addCargoInfoInUnloadForm(data)
	}

	addPointToView(data, container)
 
	$(`#${modalId}`).modal('hide')
}

// форматирование данных форм
function orderFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const contact =
		data.fio && data.tel
			? `${data.fio}, тел. ${data.tel}`
			: data.fio
				? `${data.fio}`
				: data.tel
					? `Тел. ${data.tel}` : ''

	const control = data.control === 'Да'
	const stacking = data.stacking === 'Да'

	const points = []
	for (const key in data) {
		if (key.includes('address')) {
			const address = JSON.parse(data[key])
			points.push(address)
		}
	}

	const dateDelivery = points.length && points[points.length - 1].date
		? points[points.length - 1].date
		: ''

	const numStockDelivery = isInternalMovement && points.length && points[points.length - 1].date
		? points[points.length - 1].bodyAdress
			.split('; ')[1]
			.split('-')[0]
		: orderData
			? orderData.numStockDelivery : null

	const status = isInternalMovement ? getOrderStatusByStockDelivery(numStockDelivery) : orderStatus

	const marketNumber = isInternalMovement && status === 6
		? `${new Date().getTime()}`
		: data.marketNumber
	

	return {
		contertparty: data.contertparty,
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
		dateDelivery: orderData ? orderData.dateDelivery : dateDelivery,
		points,
		needUnloadPoint: needUnloadPoint ? 'true' : 'false',
		loadNumber: data.loadNumber,
		isInternalMovement: isInternalMovement ? 'true' : 'false',
		status,
		idOrder: orderData ? orderData.idOrder : null,
		numStockDelivery,
	}
}
function pointFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const timeFrame =
		data.timeFrame_from && data.timeFrame_to
			? `С ${data.timeFrame_from} по ${data.timeFrame_to}`
			: data.timeFrame_from
				? `С ${data.timeFrame_from}`
				: data.timeFrame_to
					? `По ${data.timeFrame_to}` : ''

	const contact =
		data.pointContact_fio && data.pointContact_tel
			? `${data.pointContact_fio}, тел. ${data.pointContact_tel}`
			: data.pointContact_fio
				? `${data.pointContact_fio}`
				: data.pointContact_tel
					? `Тел. ${data.pointContact_tel}` : ''

	const bodyAdress = `${data.country}; ${data.address}`

	const customsAddress = 
		data.customsCountry && data.customsAddress
			? `${data.customsCountry}; ${data.customsAddress}`
			: data.customsCountry
				? data.customsCountry
				: data.customsAddress
					? data.customsAddress : ''

	return {
		pointNumber: pointsCounter,
		type: data.type,
		date: data.date ? data.date : '',
		time: data.time ? data.time : '',
		cargo: data.pointCargo ? removeSingleQuotes(data.pointCargo) : '',
		pall: data.pall ? data.pall : '',
		weight: data.weight ? data.weight : '',
		volume: data.volume ? data.volume : '',
		tnvd:  data.tnvd ? data.tnvd : '',
		bodyAdress,
		customsAddress,
		timeFrame,
		contact,
	}
}

// валидация формы заявки
function validateOrderForm(data) {
	if (data.points.length < 1) {
		snackbar.show('Необходимо добавить точки загрузки и выгрузки!')
		return false
	}

	if (!data.points.find(point => point.type === 'Загрузка')) {
		snackbar.show('Необходимо добавить точку загрузки!')
		return false
	}

	if (!data.points.find(point => point.type === 'Выгрузка') && data.needUnloadPoint === 'false') {
		snackbar.show('Необходимо добавить точку выгрузки!')
		return false
	}

	if (!validatePointDates(data)) {
		snackbar.show('Некорректная дата загрузки либо выгрузки!')
		return false
	}

	if (error) {
		snackbar.show('Проверьте данные!')
		return false
	}

	return true
}

// добавление точки в форму
function addPointToView(data, container) {
	const type = data.type.slice(0, -1) + 'и'
	const dateToView = data.date.split('-').reverse().join('.')
	const bodyAdressToView = data.bodyAdress.replace(/&/, '; ')

	const point = document.createElement('div')
	point.className = 'point'

	const bodyAddressHtml = createPointInfoDiv('Адрес', bodyAdressToView)
	const timeFrameHtml = createPointInfoDiv('Время работы склада', data.timeFrame)
	const contactHtml = createPointInfoDiv('Контакт', data.contact)
	const cargoHtml = createPointInfoParagraf('Груз', data.cargo)
	const pallHtml = createPointInfoParagraf('Паллеты', data.pall, 'шт')
	const weightHtml = createPointInfoParagraf('Масса', data.weight, 'кг')
	const volumeHtml = createPointInfoParagraf('Объем', data.volume, 'м.куб.')
	const customsAddressHtml = createPointInfoDiv('Адрес таможни', data.customsAddress)

	const html = `
		<div class="point">
			<div class="card">
				<div class="card-header header-container">
					<h5 class="d-flex align-items-center mb-0">Точка ${type} №${data.pointNumber}, ${dateToView}, ${data.time}</h5>
					<input type="hidden" name="address_${data.pointNumber}" id="address_${data.pointNumber}" value='${JSON.stringify(data)}'>
					<div class="control-btns">
						<button type="button" class="none btn btn-outline-primary" title="Редактировать">
							${uiIcons.pencil}
						</button>
						<button type="button" class="none btn btn-outline-danger" title="Удалить">
							${uiIcons.trash}
						</button>
					</div>
				</div>
				<div class="card-body py-2">
					${bodyAddressHtml}
					${timeFrameHtml}
					${contactHtml}
					<div>
						${cargoHtml}
						${pallHtml}
					</div>
					<div>
						${weightHtml}
						${volumeHtml}
					</div>
					${customsAddressHtml}
				<div/>
			</div>
		</div>
	`

	if (pointsCounter === 1) {
		container.innerHTML = ''
	}

	point.innerHTML = html
	container.append(point)
}

// создание блоков с информацией о точке
function createPointInfoDiv(title, info) {
	return info
		? `<div class="d-flex flex-column">
				<p class="card-text mb-0 font-weight-bold">${title}:</p>
				<p class="card-text text-muted">${info}</p>
			</div>`
		: ''
}
function createPointInfoParagraf(title, info, units) {
	return info
		? `<p class="card-text d-flex flex-column">
				<span class="font-weight-bold">${title}:</span>
				<span class="text-muted">${info} ${units ? units : ''}</span>
			</p>`
		: ''
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

// обработчик нажатия на кнопки модального окна выбора типа маршрута
function wayButtonsContainerOnClickHandler(e, wayTypeInput, controlUKZSelect, addLoadPointForm, addUnloadPointForm) {
	if (e.target.classList.contains('btn')) {
		const wayType = e.target.dataset.value
		wayTypeInput.value = wayType
		orderWay = wayType

		// добавляем тип маршрута в текст кнопки создания заявки
		changeSubmitButtonText(wayType)

		hideWayTypeModal()

		if (wayType === 'Импорт') {
			// // показываем модалку с требованиями к импроту
			// showfullImportModal()

			// просим указать номер из маркета
			showSetMarketNumberModal()
			// показываем поле "Сверка УКЗ"
			const controlContainer = document.querySelector('#control-container')
			controlContainer.classList.remove('none')
			controlUKZSelect.options[0].selected = true
			// автозаполнение значения страны в точке выгрузки
			addBelarusValueToCountryInputs(addUnloadPointForm)
			// дата выгрузки необязательна
			removeUnloadDateInputRequired(addUnloadPointForm)
			// показать поле времени выгрузки
			const unloadTimeContainer = addUnloadPointForm.querySelector('.unloadTime-container')
			unloadTimeContainer.classList.remove('none')
		}

		if (wayType === 'РБ') {
			// показываем модалку с выриантом заявки РБ
			showRBModal()
		}

		if (wayType === 'Экспорт') {
			// скрываем поле номер из Маркета
			hideMarketNumberInput()
			// автозаполнение значения страны в точке загрузки
			addBelarusValueToCountryInputs(addLoadPointForm)
			// дата выгрузки необязательна
			removeUnloadDateInputRequired(addUnloadPointForm)
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

// обработчик нажатия на кнопки модального окна вариантов перевозок по РБ
function RBButtonsContainerOnClickHandler(e, addLoadPointForm, addUnloadPointForm, domesticStocks) {
	if (e.target.classList.contains('btn')) {
		const RBType = e.target.dataset.value
		hideRBModal()
		// делаем поле ТН ВЭД необязательным
		removeTnvdInputRequired()
		// автозаполнение значения страны в точках загрузки и выгрузки
		const countryInputs = document.querySelectorAll('#country')
		countryInputs.forEach(input => {
			input.value = 'BY Беларусь'
			input.setAttribute('readonly', 'true')
		})
		// скрываем инпуты с таможней
		const customsContainers = document.querySelectorAll('.customs-container')
		customsContainers.forEach(container => container.classList.add('none'))

		if (RBType === 'domestic') {
			isInternalMovement = true
			// скрываем поле номер из Маркета
			hideMarketNumberInput()
			// заменяем поле адреса на выпадающий список с адресами складов
			// для внутренних перевозок
			transformAddressInputToSelect(addLoadPointForm, domesticStocks)
			transformAddressInputToSelect(addUnloadPointForm, domesticStocks)
			$('.selectpicker').selectpicker()
			// время выгрузки отображается и обязательно к заполнению
			// showUnloadTime(addUnloadPointForm)
			// изменяем название формы
			setFormName('Форма создания заявки (внутреннее перемещение)')
			// установка контрагента для внутренних перемещений
			setCounterparty('ЗАО "Доброном"')
		} else if (RBType === 'counterparty') {
			// просим указать номер из маркета
			showSetMarketNumberModal()
			// изменяем название формы
			setFormName('Форма создания заявки (заказ от контрагента)')
		}

		// установка минимальной даты загрузки/выгрузки
		setMinValidDate({ isInternalMovement: isInternalMovement ? 'true' : 'false' })
	}
}

// проверка формы на наличие точки выгрузки
function hasUnloadPoint(form) {
	const formData = new FormData(form)
	const data = orderFormDataFormatter(formData)
	const hasUnloadPoint = data.points.filter(point => point.type === 'Выгрузка').length > 0
	return hasUnloadPoint
}


// обработчик нажатия на кнопки модального окна промежуточной точки загрузки
function middleUnloadPointButtonsOnClichHandler(e) {
	if (e.target.classList.contains('btn')) {
		const value = e.target.dataset.value

		if (value === 'Да') {
			// открываетм модальное окно точки загрузки
			const modal = $('#addLoadPointModal')
			modal.modal('show')
			changeCargoInfoInputsRequired(modal[0])
		} else {
			// открываетм модальное окно точки выгрузки
			const modal = $('#addUnloadPointModal')
			modal.modal('show')
			changeCargoInfoInputsRequired(modal[0])
		}
	}
}

function marketNumberInputOnChangeHandler(e, messageElem) {
	const marketNumber = e.target.value
	if (marketNumber) {
		checkMarketCode(marketNumber, messageElem)
	} else {
		error = false
		messageElem.innerHTML = ''
	}
}

async function checkMarketCode(marketNumber, messageElem) {
	const res = await getData(`../../api/procurement/checkMarketCode/${marketNumber}`)
	if (res.status === '200') {
		if (res.message === 'true') {
			error = true
			messageElem.innerHTML = 'Заявка с таким номером уже существует'
		} else {
			error = false
			messageElem.innerHTML = ''
		}
	}
}

// превращение формы в форму внутренних перевозок
function transformToInternalMovementForm(addLoadPointForm, domesticStocks, addUnloadPointForm) {
	isInternalMovement = true
	// изменяем название формы
	setFormName('Форма создания заявки (внутреннее перемещение)')
	// установка контрагента для внутренних перемещений
	setCounterparty('ЗАО "Доброном"')
	// установка типа маршрута
	setWayType('РБ')
	// делаем поле ТН ВЭД необязательным
	removeTnvdInputRequired()
	// автозаполнение значения страны в точках загрузки и выгрузки
	const countryInputs = document.querySelectorAll('#country')
	countryInputs.forEach(input => {
		input.value = 'BY Беларусь'
		input.setAttribute('readonly', 'true')
	})
	// скрываем инпуты с таможней
	const customsContainers = document.querySelectorAll('.customs-container')
	customsContainers.forEach(container => container.classList.add('none'))
	// скрываем поле номер из Маркета
	hideMarketNumberInput()
	hideMarketInfoTextarea()
	// заменяем поле адреса на выпадающий список с адресами складов для внутренних перемещений
	transformAddressInputToSelect(addLoadPointForm, domesticStocks)
	transformAddressInputToSelect(addUnloadPointForm, domesticStocks)
	$('.selectpicker').selectpicker()
	// установка минимальной даты загрузки/выгрузки
	setMinValidDate({ isInternalMovement: 'true' })
}