import { snackbar } from "./snackbar/snackbar.js"
import { autocomplete } from './autocomplete/autocomplete.js'
import { countries } from './global.js'
import { ajaxUtils } from "./ajaxUtils.js"
import {
	addDataToCountryInputs,
	changeCargoInfoInputsRequired,
	changeTemperatureInputRequired,
	changeTnvdInputRequired,
	controlUKZSelectOnChangeHandler,
	getOrderStatusByStockDelivery,
	getStockAddress,
	setMinValidDate,
	setOrderDataToOrderForm,
	setUnloadDateMinValue,
	setUnloadTimeMinValue,
	showIncotermsInput,
	showIncotermsInsuranseInfo,
	validatePointDates,
} from "./procurementFormUtils.js"
import { disableButton, enableButton, getData } from "./utils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"

const addNewProcurementUrl = (orderStatus) => orderStatus === 20
	? "../../../api/manager/addNewProcurement"
	: "../../../api/manager/addNewProcurementHasMarket"
const redirectUrl = (orderStatus) => orderStatus === 20 || disableSlotRedirect ? "../orders" : "../../slots"
const getInternalMovementShopsUrl = "../../../api/manager/getInternalMovementShops"
// const getOrderHasMarketNumberBaseUrl = "../../../api/procurement/getOrderHasMarketNumber/"
const getMarketOrderBaseUrl = `../../../api/manager/getMarketOrder/`

const token = $("meta[name='_csrf']").attr("content")

let error = false
// отключения переадресации в слоты
let disableSlotRedirect = false
let orderData = null
let orderStatus = 20

window.onload = async () => {
	const creareOrderForm = document.querySelector('#orderForm')
	const wayInput = document.querySelector('#way')
	const isInternalMovement = creareOrderForm.isInternalMovement.value
	const orderWay = wayInput.value

	// форма получения данных заказа по номеру из Маркета
	const setMarketNumberForm = document.querySelector('#setMarketNumberForm')
	setMarketNumberForm.addEventListener('submit', (e) => setMarketNumberFormSubmitHandler(e, creareOrderForm))

	// просим указать номер из маркета для заказов по РБ (контрагент) и импорта
	if (orderWay === 'Импорт' || (orderWay === 'РБ' && isInternalMovement !== 'true')) {
		showSetMarketNumberModal()
	}

	// если внутренние перевозки, то заполняем селекты адресами складов
	if (isInternalMovement === 'true') {
		const domesticStocksData = await getData(getInternalMovementShopsUrl)
		const domesticStocks = domesticStocksData.map(stock => `${stock.numshop}-${stock.address}`)

		const pointAddressSelects = document.querySelectorAll('#pointAddress')
		const pointBodyAddressInputs = document.querySelectorAll('#pointBodyAddress')

		pointAddressSelects.forEach((select, i) => {
			// добавляем в селект адреса складов
			domesticStocks.forEach(address => {
				const option = document.createElement('option')
				option.value = address
				option.innerHTML = address
				select.append(option)
			})

			// выбираем нужный склад
			const pointBodyAddress = pointBodyAddressInputs[i].value
			const pointAddress = pointBodyAddress.split('; ')[1]
			select.value = pointAddress
		})
		$('.address-input').selectpicker()
	} else {
		// добавляем информацию об адресах в точки
		addDataToCountryInputs()
	}

	const points = creareOrderForm.querySelectorAll('.point')
	const cancelBtn = document.querySelector('#cancelBtn')
	const counrtyInputs = document.querySelectorAll('.country-input')
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	const statusInfoLabels = document.querySelectorAll('#statusInfoLabel')
	const statusInfos = document.querySelectorAll('#statusInfo')


	// проверка наличия заявки по номеру из маркета
	const marketNumberMessageElem = document.querySelector('#marketNumberMessage')
	const marketNumberInput = document.querySelector('#marketNumber')
	marketNumberInput.addEventListener('change', async (e) => {
		marketNumberInputOnChangeHandler(e, marketNumberMessageElem)
	})
	
	// установка минимальной даты загрузки/выгрузки
	setMinValidDate({ isInternalMovement })
	
	// установка минимальных значений даты и времени выгрузки при изменении даты
	const loadDateInputs = document.querySelectorAll('#loadDate')
	const loadTimeSelectElems = document.querySelectorAll('#loadTime')
	const unloadDateInputs = document.querySelectorAll('#unloadDate')
	const unloadTimeInputs = document.querySelectorAll('#unloadTime')
	const lastLoadDateInput = loadDateInputs[loadDateInputs.length - 1]
	const lastLoadTimeInput = loadTimeSelectElems[loadTimeSelectElems.length - 1]
	loadDateInputs.forEach(input => {
		input.addEventListener('change', (e) => {
			unloadDateInputs.forEach(input => setUnloadDateMinValue(e, input))
		})
	})
	for (let i = 0; i < unloadDateInputs.length; i++) {
		unloadDateInputs[i].addEventListener('change', (e) => {
			setUnloadTimeMinValue(lastLoadDateInput, lastLoadTimeInput, e.target, unloadTimeInputs[i], orderWay)
		})
	}
	loadTimeSelectElems.forEach(select => {
		select.addEventListener('change', (e) => {
			unloadTimeInputs.forEach(input => (input.value = ''))
		})
	})

	// установка значения адреса таможни точки выгрузки
	const controlUKZSelect = document.querySelector('select#control')
	controlUKZSelect && controlUKZSelect.addEventListener('change', (e) => {
		points.forEach(point => {
			if (point.dataset.type === 'Выгрузка') {
				controlUKZSelectOnChangeHandler(e, point, true)
			}
		})
	})

	// листнер на изменение способа загрузки
	methodLoadInput.addEventListener('change', (e) => {
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа кузова
	typeTruckInput.addEventListener('change', (e) => {
		const typeTruck = e.target.value
		changeTemperatureInputRequired(typeTruck)
		showIncotermsInput(typeTruck)
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа маршрута
	wayInput.addEventListener('change', (e) => changeTnvdInputRequired(e))

	// листнер на изменение условий поставки
	const incotermsInput = document.querySelector('#incoterms')
	incotermsInput.addEventListener('change', showIncotermsInsuranseInfo)

	// информационное окно
	for (let i = 0; i < statusInfoLabels.length; i++) {
		const statusInfoLabel = statusInfoLabels[i]
		const statusInfo = statusInfos[i]
		statusInfoLabel.addEventListener('mouseover', (e) => statusInfo.classList.add('show'))
		statusInfoLabel.addEventListener('mouseout', (e) => statusInfo.classList.remove('show'))
	}

	creareOrderForm.addEventListener('submit', (e) => orderFormSubmitHandler(e))

	// листнер на отмену создания заявки
	cancelBtn.addEventListener('click', () => {
		window.location.href = '../orders'
	})

	// автозаполнение всплывающего окна при заполнении поля страны
	for (let i = 0; i < counrtyInputs.length; i++) {
		const counrtyInput = counrtyInputs[i]
		autocomplete(counrtyInput, countries)
	}

	points.forEach(point => changeCargoInfoInputsRequired(point))

	// обработчик отключения редиректа на слоты
	const disableSlotRedirectCheckbox = document.querySelector('#disableSlotRedirect')
	disableSlotRedirectCheckbox.addEventListener('change', (e) => disableSlotRedirect = e.target.checked)
}

// обработчики отправки форм
function orderFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = orderFormDataFormatter(formData)

	if (!validateForm(data)) {
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
				}, 500);
			} else {
				snackbar.show('Возникла ошибка - обновите страницу')
				enableButton(e.submitter)
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}

// форматирование данных формы
function orderFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)

	const points = []

	let i = 1
	while (data[`type_${i}`]) {
		const point = {
			pointNumber: i,
			type: data[`type_${i}`],
			date: data[`date_${i}`] ? data[`date_${i}`] : '',
			time: data[`time_${i}`] ? data[`time_${i}`] : '',
			cargo: data[`pointCargo_${i}`],
			pall: data[`pall_${i}`],
			weight: data[`weight_${i}`],
			volume: data[`volume_${i}`],
			tnvd: data[`tnvd_${i}`] ? data[`tnvd_${i}`] : '',
			bodyAdress: data[`country_${i}`] + '; ' + data[`pointAddress_${i}`].replace(/;/g, ','),
			customsAddress: data[`customsAddress_${i}`],
			timeFrame: data[`timeFrame_${i}`],
			contact: data[`pointContact_${i}`],
		}

		points.push(point)
		i++
	}

	const control = data.control === 'Да'
	const stacking = data.stacking === 'Да'

	const isInternalMovement = data.isInternalMovement === 'true'

	const dateDelivery = points.length && points[points.length - 1].date
		? points[points.length - 1].date : ''

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
		contact: data.contact,
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
		needUnloadPoint: data.needUnloadPoint === 'true' ? 'true' : 'false',
		loadNumber: data.loadNumber,
		isInternalMovement: isInternalMovement ? 'true' : 'false',
		status,
		idOrder: orderData ? orderData.idOrder : null,
		numStockDelivery,
	}
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
	const res = await getData(`../../../api/procurement/checkMarketCode/${marketNumber}`)
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
	points.forEach(point => {
		const pointType = point.dataset.type
		const cargoInput = point.querySelector('#pointCargo')
		const pallInput = point.querySelector('#pall')
		const weightInput = point.querySelector('#weight')
		const volumeInput = point.querySelector('#volume')
		const pointAddressInput = point.querySelector('#pointAddress')

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
