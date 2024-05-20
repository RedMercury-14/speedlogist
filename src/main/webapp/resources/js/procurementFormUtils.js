import { dateHelper } from './utils.js';

const IMPORT_CUSTOMS_ADDRESS = ' г. Минск, ул. Промышленная, 4'
const IMPORT_CUSTOMS_FULL_ADDRESS = 'BY Беларусь; г. Минск, ул. Промышленная, 4'

const INCOTERMS_INSURANCE_LIST = [
	'FAS – Free Alongside Ship',
	'FOB – Free on Board',
	'CFR – Cost and Freight',
	'EXW – Ex Works',
	'FCA – Free Carrier',
	'CPT – Carriage Paid To',
]

// установка значения СТРАНЫ из полей с адресами склада
export function addDataToCountryInputs() {
	const counrtyInputs = document.querySelectorAll('.country-input')
	const addressInputs = document.querySelectorAll('.address-input')

	addressInputs.forEach((addressInput, i) => {
		const value = addressInput.value
		const separator = ';'
		const separatorIndex = value.indexOf(separator)
		
		if (separatorIndex < 0) return

		const country = value.substring(0, separatorIndex)
		const address = value.substring(separatorIndex + 2)

		counrtyInputs[i].value = country
		addressInput.value = address
	})
}

// установка минимального значения ДАТЫ для точки выгрузки
export function setUnloadDateMinValue(e, unloadDateInput) {
	const loadDateInput = e.target
	unloadDateInput.value = ''
	unloadDateInput.setAttribute('min', loadDateInput.value)
}

// установка минимального значения ВРЕМЕНИ для точки выгрузки
export function setUnloadTimeMinValue(loadDateInput, loadTimeSelect, unloadDateInput, unloadTimeSelect, orderWay) {
	const loadDate = loadDateInput.value
	const loadTime = loadTimeSelect.value
	
	const unloadDate = unloadDateInput.value
	const unloadTimeOptions = unloadTimeSelect.options

	// для заявок по РБ и Экспорт задержка от загрузки 4 часа
	if (orderWay === 'РБ' || orderWay === 'Экспорт') {
		const delay = dateHelper.MILLISECONDS_IN_HOUR * 4
		if (loadDate === unloadDate) {
			// если даты совпадают, то проверяем список и блокируем время, меньше времени загрузки
			const loadDateInMs = dateHelper.getDateObj(loadDate, loadTime).getTime()
			const minValidDate = loadDateInMs + delay
			for (let i = 0; i < unloadTimeOptions.length; i++) {
				const option = unloadTimeOptions[i]
				const unloadDateObj = dateHelper.getDateObj(unloadDate, option.value).getTime()
				option.disabled = unloadDateObj < minValidDate
			}
		} else {
			// весь список открыт
			for (let i = 0; i < unloadTimeOptions.length; i++) {
				const option = unloadTimeOptions[i]
				option.disabled = false
			}
		}
	} else {
		// для заявок Импорт задержка от загрузки 24 часов
		const delay = dateHelper.DAYS_TO_MILLISECONDS
		const loadDayMs = new Date(loadDate).setHours(0,0,0,0)
		const unloadDayMs = new Date(unloadDate).setHours(0, 0, 0, 0)
		if (loadDate === unloadDate) {
			// если даты совпадают, то блокируем весь список
			for (let i = 0; i < unloadTimeOptions.length; i++) {
				const option = unloadTimeOptions[i]
				option.disabled = true
			}
		} else if (loadDayMs + delay === unloadDayMs) {
			// если дата выгрузки на 1 день больше даты загрузки,
			// то проверяем список и блокируем время, меньше времени загрузки
			const loadDateInMs = dateHelper.getDateObj(loadDate, loadTime).getTime() + delay
			const minValidDate = loadDateInMs
			for (let i = 0; i < unloadTimeOptions.length; i++) {
				const option = unloadTimeOptions[i]
				const unloadDateObj = dateHelper.getDateObj(unloadDate, option.value).getTime()
				option.disabled = unloadDateObj < minValidDate
			}
		} else {
			// весь список открыт
			for (let i = 0; i < unloadTimeOptions.length; i++) {
				const option = unloadTimeOptions[i]
				option.disabled = false
			}
		}
	}
}

// установка минимального значения ДАТЫ для точек загрузки и выгрузки
export function setMinValidDate(order) {
	const dateInputs = document.querySelectorAll('.date-input')
	const minValidDate = dateHelper.getMinValidDate(order)
	dateInputs.forEach(input => input.setAttribute('min', minValidDate))
}

// проверка правильности дат точек загрузки и выгрузки
export function validatePointDates(data) {
	const pointDates = data.points.map(point => point.date && new Date(point.date))
	const minValidDate = new Date(dateHelper.getMinValidDate(data))
	const isValid = pointDates.every(date => {
		if (!date) return true
		if (date >= minValidDate) return true
	})
	return isValid
}

// обработчик изменения значения инпута "Сверка УКЗ"
export function controlUKZSelectOnChangeHandler(e, DOMobj, isFullAddress) {
	const unloadCustomsAddress = DOMobj.querySelector('#customsAddress')
	const controlUKZ = e.target.value

	unloadCustomsAddress.value = (controlUKZ === 'Да')
		? isFullAddress
			? IMPORT_CUSTOMS_FULL_ADDRESS
			: IMPORT_CUSTOMS_ADDRESS
		: ''
}

// изменение значения аттрибута "required" для полей с количеством паллет, объемом и массой
export function changeCargoInfoInputsRequired(DOMobj) {
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	const pallInput = DOMobj.querySelector('#pall')
	const weightInput = DOMobj.querySelector('#weight')
	const volumeInput = DOMobj.querySelector('#volume')

	if (typeTruckInput.value.includes('Контейнер')) {
		pallInput.required = false
		weightInput.required = false
		volumeInput.required = false
	} else {
		if (methodLoadInput.value === 'Навалом') {
			pallInput.required = false
			weightInput.required = false
			volumeInput.required = true
		} else {
			pallInput.required = true
			weightInput.required = true
			volumeInput.required = false
		}
	}
}

// изменение значения аттрибута "required" для поля с температурой
export function changeTemperatureInputRequired(typeTruck) {
	const temperatureContainer = document.querySelector('.form-group:has(>#temperature)')
	const temperatureTitle = temperatureContainer && temperatureContainer.querySelector('span')
	const temperatureInput = document.querySelector('#temperature')

	const condition = typeTruck === 'Изотермический'
					|| typeTruck === 'Рефрижератор'

	if (temperatureInput) temperatureInput.required = condition
	if (temperatureTitle) temperatureTitle.innerHTML = condition
		? 'Температура: <span class="text-red">*</span>'
		: 'Температура:'
}

// изменение значения аттрибута "required" для поля с кодами ТН ВЭД
export function changeTnvdInputRequired(e) {
	const tnvdContainers = document.querySelectorAll('.form-group:has(>#tnvd)')
	const tnvdInputs = document.querySelectorAll('#tnvd')
	
	for (let i = 0; i < tnvdInputs.length; i++) {
		const tnvdContainer = tnvdContainers && tnvdContainers[i]
		const tnvdTitle = tnvdContainer && tnvdContainer.querySelector('label')
		const tnvdInput = tnvdInputs[i]
		const condition = e.target.value === 'РБ'
	
		if (condition) {
			tnvdInput.removeAttribute('required')
		} else {
			tnvdInput.setAttribute('required', 'true')
		}
	
		if (tnvdTitle) tnvdTitle.innerHTML = condition
			? 'Коды ТН ВЭД:'
			: 'Коды ТН ВЭД: <span class="text-red">*</span>'
	}
}

// отображение поля с условиями перевозки в зависимости от типа кузова
export function showIncotermsInput(typeTruck) {
	const incotermsContainer = document.querySelector("#incoterms-container")
	const incotermsInput = document.querySelector("#incoterms")

	if (typeTruck.includes("Контейнер")) {
		incotermsContainer.classList.remove('none')
		incotermsInput.removeAttribute("disabled")
	} else {
		incotermsContainer.classList.add('none')
		incotermsInput.setAttribute("disabled", true)
	}
}

// отображение модального окна с информацией о страховании груза
export function showIncotermsInsuranseInfo(e) {
	const incoterms = e.target.value

	if (INCOTERMS_INSURANCE_LIST.includes(incoterms)) {
		$('#incotermsInsuranceModal').modal('show')
	}
}


// удаление значения аттрибута "required" для поля с кодами ТН ВЭД
export function removeTnvdInputRequired() {
	const tnvdContainer = document.querySelector('.form-group:has(>#tnvd)')
	const tnvdTitle = tnvdContainer && tnvdContainer.querySelector('label')
	const tnvdInput = document.querySelector('#tnvd')

	tnvdInput.removeAttribute('required')
	tnvdTitle && (tnvdTitle.innerHTML = 'Коды ТН ВЭД:')
}

// добавление значения аттрибута "required" номера из Маркета
export function addMarketNumberInputRequired() {
	const marketNumberInput = document.querySelector('#marketNumber')
	const marketNumberLebel = marketNumberInput.parentElement.querySelector('span')
	marketNumberInput.setAttribute('required', 'true')
	marketNumberLebel.innerHTML = 'Номер из маркета <span class="text-red">*</span>'
}

// удаление значения аттрибута "required" для даты выгрузки
export function removeUnloadDateInputRequired(form) {
	// дата выгрузки необязательна
	const unloadDateContainer = form.querySelector('.unloadDate-container')
	const unloadDateLabel = unloadDateContainer.querySelector('label')
	const unloadDateInput = unloadDateContainer.querySelector('#unloadDate')

	unloadDateLabel.innerHTML = 'Дата выгрузки'
	unloadDateInput.removeAttribute('required')
}

// автозаполнение значения страны в форме точки загрузки или выгрузки (Беларусь)
export function addBelarusValueToCountryInputs(pointForm) {
	const countryInput = pointForm.querySelector('#country')
	const customsCountryInput = pointForm.querySelector('#customsCountry')

	countryInput.value = 'BY Беларусь'
	// customsCountryInput.value = 'BY Беларусь'
	countryInput.setAttribute('readonly', 'true')
	// customsCountryInput.setAttribute('readonly', 'true')
}

// автозаполнение формы выгрузки
export function addCargoInfoInUnloadForm(data) {
	const addUnloadPointForm = document.querySelector('#addUnloadPointForm')
	const pointCargoInput = addUnloadPointForm.querySelector('#pointCargo')
	const pallInput = addUnloadPointForm.querySelector('#pall')
	const weightInput = addUnloadPointForm.querySelector('#weight')
	const volumeInput = addUnloadPointForm.querySelector('#volume')

	pointCargoInput.value = data.cargo
	pallInput.value = data.pall
	weightInput.value = data.weight
	volumeInput.value = data.volume
}

// изменение текста кнопки создания заявки
export function changeSubmitButtonText(wayType) {
	const formSubmitBtn = document.querySelector('#formSubmitBtn')
	const submitBtnText = formSubmitBtn.innerText
	formSubmitBtn.innerText = submitBtnText + ` (${wayType})`
}

// скрываем кнопку добавления точки выгрузки и делаем ее неактивной и недоступной
export function hideAddUnloadPointButton() {
	const addUnloadPoint = document.querySelector('#addUnloadPoint')

	addUnloadPoint.setAttribute('disabled', 'true')
	addUnloadPoint.classList.add('none')
}

// замена поля адреса на выпадающий список
export function transformAddressInputToSelect(form, addresses) {
	const addressInput = form.querySelector('#address')
	const addressContainer = addressInput.parentElement
	const addressSelect = createAddressSelect(addresses)

	addressInput.remove()
	addressContainer.append(addressSelect)
}

function createAddressSelect(addresses) {
	const select = document.createElement('select')
	select.name = 'address'
	select.id = 'address'
	select.classList.add('form-control')
	select.setAttribute('required', 'true')

	const option = document.createElement('option')
	option.value = ''
	option.innerHTML = 'Выберите склад'
	option.selected = true
	option.disabled = true
	option.hidden = true
	select.append(option)

	addresses.forEach(address => {
		const option = document.createElement('option')
		option.value = address
		option.innerHTML = address
		select.append(option)
	})

	return select
}


// скрывает поле номера из Маркета
export function hideMarketNumberInput() {
	const marketNumberInput = document.querySelector('#marketNumber')
	const marketNumberContainer = marketNumberInput.parentElement
	marketNumberContainer.classList.add('none')
	marketNumberInput.removeAttribute('required')
}

export function hideMarketInfoTextarea() {
	const marketInfoTextarea = document.querySelector('#marketInfo')
	const marketInfoContainer = marketInfoTextarea.parentElement
	marketInfoContainer.classList.add('none')
	marketInfoTextarea.removeAttribute('required')
}

export function setCounterparty(counterparty) {
	const counterpartyInput = document.querySelector('#contertparty')
	counterpartyInput.value = counterparty
	// counterpartyInput.setAttribute('readonly', 'true')
}

export function setWayType(wayType) {
	const wayTypeInput = document.querySelector('#way')
	wayTypeInput.value = wayType
	wayTypeInput.setAttribute('readonly', 'true')
}

export function setFormName(formName) {
	const formNameElem = document.querySelector('#formName')
	formNameElem.innerText = formName
}

// заполняет данные заказа в форме заявки
export function setOrderDataToOrderForm(form, orderData) {
	const marketNumberInput = form.querySelector('#marketNumber')
	const marketInfoSpan = form.querySelector('#marketInfo')
	marketNumberInput.value = Number(orderData.marketNumber)
	marketNumberInput.setAttribute('readonly', 'true')
	marketInfoSpan.innerText = orderData.marketInfo
	form.contertparty.value = orderData.counterparty
	form.loadNumber.value = orderData.marketNumber
	form.cargo.value = orderData.cargo
}

// заполняет данные заказа в форме точки загрузки
export function setOrderDataToLoadPointForm(form, orderData) {
	const pallInput = form.querySelector('#pall')
	pallInput.value = Number(orderData.pall)
	pallInput.setAttribute('readonly', 'true')
	form.pointCargo.value = orderData.cargo
}

// заполняет данные заказа в форме точки выгрузки
export function setOrderDataToUnloadPointForm(form, orderData) {
	const pallInput = form.querySelector('#pall')
	pallInput.value = Number(orderData.pall)
	pallInput.setAttribute('readonly', 'true')
	form.pointCargo.value = orderData.cargo
	form.address.value = getStockAddress(orderData.numStockDelivery)
}

// показываем время выгрузки и делаем обязательным
export function showUnloadTime(addUnloadPointForm) {
	const unloadTimeContainer = addUnloadPointForm.querySelector('.unloadTime-container')
	const unloadTimeLabel = unloadTimeContainer.querySelector('label')
	const unloadTimeInput = unloadTimeContainer.querySelector('#unloadTime')
	unloadTimeContainer.classList.remove('none')
	unloadTimeLabel.innerHTML = 'Время выгрузки <span class="text-red">*</span>'
	unloadTimeInput.required = true
}

export function getStockAddress(stockNumber) {
	switch (stockNumber) {
		case '1700': return 'Склад 1700, Адрес: 223065, Беларусь, Луговослободской с/с,Минский р-н,Минская обл., РАД М4, 18км. 2а, склад W05'
		case '1200': return 'Склад 1200, 223039, Республика Беларусь, Минская область, Минский район, Хатежинский с/с, 1'
		case '1250': return 'Склад 1250, Адрес: 223050, Республика Беларусь, Минская область, Минский р-н, 9-ый км Московского шоссе'
		case '1100': return 'Склад 1100, 223039, Республика Беларусь, Минская область, Минский район, Хатежинский с/с, 1'
		default: return ''
	}
}

export function getOrderStatusByStockDelivery(numStockDelivery) {
	switch (numStockDelivery) {
		case '1700':
		case '1200':
		case '1250':
		case '1100':
		case 1700:
		case 1200:
		case 1250:
		case 1100:
			return 6
		default:
			return 20
	}
}