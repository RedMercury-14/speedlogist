import { autocomplete } from './autocomplete/autocomplete.js';
import { countries } from './global.js';
import { dateHelper, inputBan, setInputValue } from './utils.js';

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
	if (orderWay === 'РБ' || orderWay === 'Экспорт' || orderWay === 'АХО') {
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
	const pallInput = DOMobj.querySelector('#pall') || DOMobj.querySelector('input[name="pall"]')
	const weightInput = DOMobj.querySelector('#weight') || DOMobj.querySelector('input[name="weight"]')
	const volumeInput = DOMobj.querySelector('#volume') || DOMobj.querySelector('input[name="volume"]')

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
	const temperatureInput = document.querySelector('#temperature')
	const temperatureTitle = temperatureInput && temperatureInput.parentElement.querySelector('span')

	const condition = typeTruck === 'Изотермический'
					|| typeTruck === 'Рефрижератор'
					|| typeTruck.includes('рефрижератор')

	if (temperatureInput) temperatureInput.required = condition
	if (temperatureTitle) temperatureTitle.innerHTML = condition
		? 'Температура: <span class="text-red">*</span>'
		: 'Температура:'
}

// изменение значения аттрибута "required" для поля с кодами ТН ВЭД
export function changeTnvdInputRequired(e) {
	const tnvdInputs = document.querySelectorAll('#tnvd')

	for (let i = 0; i < tnvdInputs.length; i++) {
		const tnvdInput = tnvdInputs[i]
		const tnvdTitle = tnvdInput && tnvdInput.parentElement.querySelector('label')
		const condition = e.target.value === 'РБ'

		if (tnvdInput && condition) {
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
		// showFormField('deliveryLocation', '', true)
	} else {
		incotermsContainer.classList.add('none')
		incotermsInput.setAttribute("disabled", true)
		// hideFormField('deliveryLocation')
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
	const tnvdInput = document.querySelector('#tnvd')
	const tnvdTitle = tnvdInput && tnvdInput.parentElement.querySelector('label')

	tnvdInput && tnvdInput.removeAttribute('required')
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
	const addressInput = form.querySelector('input[name="address"]')
	const value = addressInput.value
	const addressContainer = addressInput.parentElement
	const addressSelect = createAddressSelect(addresses, value)

	addressInput.remove()
	addressContainer.append(addressSelect)
}

function createAddressSelect(addresses, value) {
	const select = document.createElement('select')
	select.name = 'address'
	select.id = 'address'
	select.classList.add('selectpicker')
	select.setAttribute('required', 'true')
	select.setAttribute('data-live-search', 'true')
	select.setAttribute('data-size', '8')
	select.setAttribute('data-width', '75%')

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

	select.value = value
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
	const counterpartyInput = document.querySelector('#counterparty')
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
	form.counterparty.value = orderData.counterparty
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
		case '1700': return 'Склад 1700, 223065, Минская обл., Минский р-н, Луговослободской с/с, РАД М4, 18км. 2а, склад W05'
		case '1200': return 'Склад 1200, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		case '1230': return 'Склад 1230, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		case '1214': return 'Склад 1214, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		case '1250': return 'Склад 1250, 223050, Минская обл., Минский р-н, 9-ый км Московского шоссе'
		case '1100': return 'Склад 1100, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		default: return ''
	}
}

// изменяем аттрибут disabled всех опций, кроме выбранной и пустой
export function changeEditingOptions(select, canEdit) {
	const options = select.options
	// изменяем аттрибут disabled всех опций, кроме выбранной и пустой
	for (let i = 0; i < options.length; i++) {
		const option = options[i];
		const isTarget = option.value !== select.value && option.value !== ''
		if (isTarget) option.disabled = !canEdit
	}
}

// отображение поля формы
export function showFormField(id, value, isRequired) {
	const field = document.querySelector(`#${id}`)
	if (field) {
		const container = field.parentElement
		field.parentElement.classList.remove('none')
		field.value = value
		if (isRequired) {
			field.required = true
			let label
			label = container.querySelector('span')
			if (!label) label = container.querySelector('label')
			const labelText = label.innerText
			if (labelText.includes('*')) return
			const requiredMarker = ' <span class="text-red">*</span>'
			label.innerHTML = labelText + (isRequired ? requiredMarker : '')
		}
	}
}

// скрытие поля формы
export function hideFormField(id) {
	const field = document.querySelector(`#${id}`)
	if (field) {
		const container = field.parentElement
		container.classList.add('none')
		const requiredMarker = ' <span class="text-red">*</span>'
		const label = container.querySelector('span') || container.querySelector('label')
		label.innerHTML = label.innerHTML.replace(requiredMarker, '')
		field.value = ''
		field.required = false
	}
}


// ====================================================================================
// ========================== функции для новой формы заявки ==========================
// ====================================================================================

// проверка наличия всех обязательных данных о точках
export function isInvalidPointForms(routeForm) {
	const pointForms = routeForm.querySelectorAll('.pointForm')
	if (!pointForms.length) return false
	const isValidPointForms = []

	pointForms.forEach(form => {
		const isValidForm = form.reportValidity()
		isValidPointForms.push(isValidForm)
	})

	return isValidPointForms.includes(false)
}

// обработчик изменения значения поля Опасный груз
export function dangerousInputOnChangeHandler(e) {
	const value = e.target.value
	dangerousInfoVisibleToggler(value === 'Да')
}

// обработчик изменения Типа кузова
export function typeTruckOnChangeHandler(e) {
	const typeTruck = e.target.value
	changeTemperatureInputRequired(typeTruck)
	showIncotermsInput(typeTruck)
	// changeTruckLoadCapacityValue(typeTruck)
	// truckVolumeVisibleToggler(typeTruck)
}

// отображение поля Объем кузова в зависимости от Типа кузова
export function truckVolumeVisibleToggler(typeTruck, way) {
	const wayInput = document.querySelector('#way')
	const wayValue = way ? way :  wayInput && wayInput.value
	if (wayValue !== 'Импорт') return
	if (typeTruck.includes('Контейнер') || typeTruck === 'Открытый') {
		hideFormField('truckVolume')
	} else {
		showFormField('truckVolume', '', true)
	}
}

// изменение значения поля Грузоподъемность в зависимости от Типа кузова
export function changeTruckLoadCapacityValue(typeTruck) {
	const wayInput = document.querySelector('#way')
	const way = wayInput && wayInput.value
	if (way !== 'Импорт') return
	const truckLoadCapacity = document.querySelector('#truckLoadCapacity')
	if (!truckLoadCapacity) return
	if (typeTruck.includes("Контейнер")) {
		truckLoadCapacity.value = 22
	} else {
		truckLoadCapacity.value = ""
	}
}

export function dangerousInfoVisibleToggler(dangerous) {
	if (dangerous) {
		showDangerousInfo()
	} else {
		hideDangerousInfo()
	}
}

export function showDangerousInfo() {
	showFormField('dangerousUN', '', true)
	showFormField('dangerousClass', '', true)
	showFormField('dangerousPackingGroup', '', true)
	showFormField('dangerousRestrictionCodes', '', true)
}
export function hideDangerousInfo() {
	hideFormField('dangerousUN')
	hideFormField('dangerousClass')
	hideFormField('dangerousPackingGroup')
	hideFormField('dangerousRestrictionCodes')
}

// валидация полей адреса, запрещает вводить запятые
export function addressFieldInputValidation(point, index) {
	const reg = /[,]/g
	const withoutСommasInputs = []
	const countryInput = point.querySelector(`#country_${index}`)
	const customsCountryInput = point.querySelector(`#customsCountry_${index}`)
	const postIndexInput = point.querySelector(`#postIndex_${index}`)
	const regionInput = point.querySelector(`#region_${index}`)
	const cityInput = point.querySelector(`#city_${index}`)
	const streetInput = point.querySelector(`#street_${index}`)
	const buildingInput = point.querySelector(`#building_${index}`)
	const buildingBodyInput = point.querySelector(`#buildingBody_${index}`)
	const customsPostIndexInput = point.querySelector(`#customsPostIndex_${index}`)
	const customsRegionInput = point.querySelector(`#customsRegion_${index}`)
	const customsCityInput = point.querySelector(`#customsCity_${index}`)
	const customsStreetInput = point.querySelector(`#customsStreet_${index}`)
	const customsBuildingInput = point.querySelector(`#customsBuilding_${index}`)
	const customsBuildingBodyInput = point.querySelector(`#customsBuildingBody_${index}`)
	withoutСommasInputs.push(
		countryInput, customsCountryInput, postIndexInput, regionInput, cityInput,
		streetInput, buildingInput, buildingBodyInput, customsPostIndexInput, customsRegionInput,
		customsCityInput, customsStreetInput, customsBuildingInput, customsBuildingBodyInput
	)
	withoutСommasInputs.forEach(input => input && input.addEventListener('input', (e) => inputBan(e, reg)))
}

// автозаполнение выпадающего списка стран для адресов в точке
export function autocompleteCountryList(point, index) {
	const countryInput = point.querySelector(`#country_${index}`)
	const customsCountryInput = point.querySelector(`#customsCountry_${index}`)
	autocomplete(countryInput, countries)
	if (customsCountryInput) autocomplete(customsCountryInput, countries)
}

// отображение полей с таможенным оформлением в точке
export function customsFieldsVisibilityToggler(point, index) {
	const customsInPointAddress = point.querySelector(`#customsInPointAddress_${index}`)
	customsInPointAddress && customsInPointAddress.addEventListener('change', (e) => {
		const customsContainer = document.querySelector(`.customsContainer_${index}`)
		const customsInputs = customsContainer.querySelectorAll('input')
		if (e.target.value === 'Нет') {
			customsContainer.classList.remove('none')
			customsInputs.forEach(input => input.id !== `customsBuildingBody_${index}` && (input.required = true))
		} else {
			customsContainer.classList.add('none')
			customsInputs.forEach(input => input.id !== `customsBuildingBody_${index}` && (input.required = false))
		}
	})
}

// отображение информационного окна в точке
export function statusInfoVisibilityToggler(point, index) {
	const statusInfoLabel = point.querySelector(`#statusInfoLabel_${index}`)
	const statusInfo = point.querySelector(`#statusInfo_${index}`)
	statusInfoLabel && statusInfoLabel.addEventListener('mouseover', (e) => statusInfo.classList.add('show'))
	statusInfoLabel && statusInfoLabel.addEventListener('mouseout', (e) => statusInfo.classList.remove('show'))
}

// блокирование полей с режимом работы взависимости от чекбокса "Не работают"
export function timeFrameInputsDisabledToggler(point, index) {
	const saturdayTimeFrame_fromInput = point.querySelector(`#saturdayTimeFrame_from_${index}`)
	const saturdayTimeFrame_toInput = point.querySelector(`#saturdayTimeFrame_to_${index}`)
	const sundayTimeFrame_fromInput = point.querySelector(`#sundayTimeFrame_from_${index}`)
	const sundayTimeFrame_toInput = point.querySelector(`#sundayTimeFrame_to_${index}`)
	const saturdayTimeFrame_NotWorkCheckbox = point.querySelector(`#saturdayTimeFrame_NotWork_${index}`)
	saturdayTimeFrame_NotWorkCheckbox && saturdayTimeFrame_NotWorkCheckbox.addEventListener('change', (e) => {
		const isDisabled = e.target.checked
		saturdayTimeFrame_fromInput.disabled = isDisabled
		saturdayTimeFrame_toInput.disabled = isDisabled
	})
	const sundayTimeFrame_NotWorkCheckbox = point.querySelector(`#sundayTimeFrame_NotWork_${index}`)
	sundayTimeFrame_NotWorkCheckbox && sundayTimeFrame_NotWorkCheckbox.addEventListener('change', (e) => {
		const isDisabled = e.target.checked
		sundayTimeFrame_fromInput.disabled = isDisabled
		sundayTimeFrame_toInput.disabled = isDisabled
	})
}

// обработчик при изменении даты выгрузки в точке
export function pointUnloadDateOnChangeHandler(point, way) {
	const unloadDateInput = point.querySelector('.unloadDate')
	const unloadTimeSelect = point.querySelector('.unloadTime')
	unloadDateInput && unloadTimeSelect && unloadDateInput.addEventListener('change', (e) => {
		const loadDateInputs = document.querySelectorAll('.loadDate')
		const loadTimeSelectElems = document.querySelectorAll('.loadTime')
		const lastLoadDateInput = loadDateInputs[loadDateInputs.length - 1]
		const lastLoadTimeInput = loadTimeSelectElems[loadTimeSelectElems.length - 1]
		setUnloadTimeMinValue(lastLoadDateInput, lastLoadTimeInput, e.target, unloadTimeSelect, way)
	})
}

// обработчик при изменении даты загрузки в точке
export function pointLoadDateOnChangeHandler(point) {
	const loadDateInput = point.querySelector('.loadDate')
	loadDateInput && loadDateInput.addEventListener('change', (e) => {
		const unloadDateInputs = document.querySelectorAll('.unloadDate')
		unloadDateInputs.forEach(input => setUnloadDateMinValue(e, input))
	})
}

// обработчик при изменении времени загрузки в точке
export function pointLoadTimeOnChangeHandler(point) {
	const loadTimeSelect = point.querySelector('.loadTime')
	loadTimeSelect && loadTimeSelect.addEventListener('change', (e) => {
		const unloadTimeSelects = document.querySelectorAll('.unloadTime')
		unloadTimeSelects.forEach(input => (input.value = ''))
	})
}

// изменение формы с данными заявки
// (для копирования, редактирования и создания маршрута)
export function changeForm(orderData, formType) {
	const typeTruck = orderData.typeTruck
	const dangerous = orderData.dangerous
	const way = orderData.way
	const isInternalMovement = orderData.isInternalMovement === 'true' 
	const EAEUImport = orderData.EAEUImport === 'true'

	// смена названия формы для копирования и редактирования
	if (formType === 'copy' || formType === 'edit') {
		const typeNames = {
			copy: 'создания',
			edit: 'редактирования'
		}
		const formName = isInternalMovement
			? `Форма ${typeNames[formType]} заявки (внутреннее перемещение)`
			: `Форма ${typeNames[formType]} заявки (${way})`
		setFormName(formName)
	}

	// // отображение дополнительных полей для Импорта
	if (way === 'Импорт') {
	// 	showFormField('recipient', 'ЗАО "Доброном"', true)
		showFormField('control', '', true)
	// 	showFormField('routeComments', '', false)
	// 	showFormField('truckLoadCapacity', '', true)
	// 	showFormField('truckVolume', '', true)
	// 	showFormField('phytosanitary', '', true)
	// 	showFormField('veterinary', '', true)
	// 	showFormField('dangerous', '', true)
	// 	truckVolumeVisibleToggler(typeTruck, way)
	// 	dangerousInfoVisibleToggler(dangerous)
	} else {
	// 	hideFormField('recipient')
		hideFormField('control')
	// 	hideFormField('routeComments')
	// 	hideFormField('truckLoadCapacity')
	// 	hideFormField('truckVolume')
	// 	hideFormField('phytosanitary')
	// 	hideFormField('veterinary')
	// 	hideFormField('dangerous')
	}

	// if (EAEUImport && way === 'Импорт') {
	// 	showFormField('tir', '', true)
	// } else {
	// 	hideFormField('tir')
	// }

	if (isInternalMovement || way === 'Экспорт') {
		hideFormField('marketNumber')
		hideFormField('marketInfo')
	} else {
		showFormField('marketNumber', '', true)
		showFormField('marketInfo', '', false)
	}

	if (way === 'АХО') {
		hideFormField('contact')
		transformToAhoComment()
		showFormField('orderPall', '', true)
		showFormField('orderWeight', '', true)
		hideFormField('loadNumber')
		hideFormField('marketNumber')
		hideFormField('marketInfo')
		hideFormField('stacking')
	}

	showIncotermsInput(typeTruck)
	changeTemperatureInputRequired(typeTruck)
}

// установка слушателей для полей точек в формах заявок и маршрута
export function addListnersToPoint(point, way, index) {
	// установка минимального значения даты выгрузки при изменении даты загрузки
	pointLoadDateOnChangeHandler(point)
	// обнуление значений времени выгрузки при изменении времени загрузки
	pointLoadTimeOnChangeHandler(point)
	// установка минимального значеня времени выгрузки при изменении даты выгрузки
	pointUnloadDateOnChangeHandler(point, way)
	// отображение информационного окна
	statusInfoVisibilityToggler(point, index)
	// автозаполнение выпадающего списка стран для адресов
	autocompleteCountryList(point, index)
	// отображение полей с таможенным оформлением
	customsFieldsVisibilityToggler(point, index)
	// запрет на ввод запятой и точки с запятой в поля для подробного адреса
	addressFieldInputValidation(point, index)
	// обработка чекбоксов режима работы
	timeFrameInputsDisabledToggler(point, index)
}

// добавление данных в форму заявки
// (для копирования, редактирования и создания маршрута)
export function addDataToRouteForm(data, routeForm, createPointMethod) {
	const pointList = routeForm.querySelector('#pointList')
	const points = data.addresses

	routeForm.isInternalMovement.value = data.isInternalMovement
	routeForm.counterparty.value = data.counterparty
	routeForm.contact.value = data.contact ? data.contact : ''
	routeForm.recipient.value = data.recipient ? data.recipient : ''
	routeForm.control.value = data.control ? 'Да' : 'Нет'
	routeForm.tir.value = data.tir ? 'Да' : 'Нет'
	routeForm.way.value = data.way
	routeForm.marketNumber.value = data.marketNumber ? data.marketNumber : ''
	routeForm.loadNumber.value = data.loadNumber ? data.loadNumber : ''
	routeForm.marketInfo.value = data.marketInfo ? data.marketInfo : ''
	routeForm.routeComments.value = data.routeComments ? data.routeComments : ''
	routeForm.comment.value = data.comment
	routeForm.typeLoad.value = data.typeLoad
	routeForm.methodLoad.value = data.methodLoad
	routeForm.typeTruck.value = data.typeTruck
	routeForm.incoterms.value = data.incoterms ? data.incoterms : ''
	routeForm.deliveryLocation.value = data.deliveryLocation ? data.deliveryLocation : ''
	routeForm.stacking.value = data.stacking ? 'Да' : 'Нет'
	routeForm.cargo.value = data.cargo
	routeForm.truckLoadCapacity.value = data.truckLoadCapacity ? data.truckLoadCapacity : ''
	routeForm.truckVolume.value = data.truckVolume ? data.truckVolume : ''
	routeForm.temperature.value = data.temperature
	routeForm.phytosanitary.value = data.phytosanitary ? data.phytosanitary : ''
	routeForm.veterinary.value = data.veterinary ? data.veterinary : ''
	routeForm.dangerous.value = data.dangerous ? data.dangerous : ''
	routeForm.dangerousUN.value = data.dangerousUN ? data.dangerousUN : ''
	routeForm.dangerousClass.value = data.dangerousClass ? data.dangerousClass : ''
	routeForm.dangerousPackingGroup.value = data.dangerousPackingGroup ? data.dangerousPackingGroup : ''
	routeForm.dangerousRestrictionCodes.value = data.dangerousRestrictionCodes ? data.dangerousRestrictionCodes : ''

	// поля для АХО
	if (data.way === 'АХО') {
		const point = points[0]
		if (!point) return
		routeForm.orderPall.value = point.pall ? point.pall : ''
		routeForm.orderWeight.value = point.weight ? point.weight : ''
	}

	points.forEach((point, i) => {
		const pointElement = createPointMethod(data, point, i)
		pointList.append(pointElement)
	})
}

// запрет на редактирование поля формы
export function inputEditBan(container, selector, value) {
	const input = container.querySelector(selector)
	if (!input) return

	const selectOptions = input.options
	if (selectOptions) {
		input.setAttribute('readonly', '')
		// блокируем все опции, кроме выбранной
		for (let i = 0; i < selectOptions.length; i++) {
			const option = selectOptions[i]
			if (option.value !== input.value) {
				option.disabled = value
			}
		}
	} else {
		input.readOnly = value
	}
}

// получение статута заявки в зависимости от склада доставки
export function getOrderStatusByStockDelivery(numStockDelivery) {
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

// метод изменения поле комментарий для формы заявки АХО
export function transformToAhoComment() {
	const comment = document.querySelector('#comment')
	const commentContainer = comment.parentElement
	const commentLabel = commentContainer.querySelector('label')
	commentLabel.innerText = 'Дополнительная информация'
	comment.setAttribute('placeholder', 'Размеры груза, дополнительные требования к авто и др.')
}

// обработчик изменения поля Кол-во паллет для АХО
export function orderPallInputOnChangeHandler(e) {
	const value = e.target.value
	changePointInfo('pall', value)
}
// обработчик изменения поля Масса груза для АХО
export function orderWeightInputOnChangeHandler(e) {
	const value = e.target.value
	changePointInfo('weight', value)
}
// обработчик изменения поля Груз
export function orderCargoInputOnChangeHandler(e) {
	const value = e.target.value
	changePointInfo('pointCargo', value)
}

// метод изменения значения поля в точке маршрута
function changePointInfo(inputName, value) {
	if (!value) return
	const points = document.querySelectorAll('.point')
	for (let i = 0; i < points.length; i++) {
		const point = points[i]
		const pointIndex = i + 1
		setInputValue(point, `#${inputName}_${pointIndex}`, value)
	}
}
