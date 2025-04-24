import { autocomplete } from './autocomplete/autocomplete.js'
import { countries } from './global.js'
import { RULES_FOR_MIN_UNLOAD_TIME } from './globalRules/minUnloadTimeRules.js'
import { INCOTERMS_INSURANCE_LIST, MAX_ONE_PALL_WEIGHT_KG, MAX_PALL_COUNT_FOR_AHO_ORDER } from './globalRules/ordersRules.js'
import { dateHelper, inputBan, setInputValue } from './utils.js'


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

	const delayFactor = RULES_FOR_MIN_UNLOAD_TIME[orderWay] || RULES_FOR_MIN_UNLOAD_TIME.default
	const delay = dateHelper.MILLISECONDS_IN_HOUR * delayFactor

	// для заявок по РБ и Экспорт задержка от загрузки 4 часа
	if (orderWay === 'РБ' || orderWay === 'Экспорт' || orderWay === 'АХО') {
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

// изменение значения аттрибута "required" для полей с количеством паллет, объемом и массой
export function changeCargoInfoInputsRequired(DOMobj) {
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	const pallInput = DOMobj.querySelector('#pall') || DOMobj.querySelector('input[name="pall"]')
	const weightInput = DOMobj.querySelector('#weight') || DOMobj.querySelector('input[name="weight"]')
	const volumeInput = DOMobj.querySelector('#volume') || DOMobj.querySelector('input[name="volume"]')

	// для АХО
	const wayInput = document.querySelector('#way')
	const way = wayInput && wayInput.value
	if (way === 'АХО') return

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

// автозаполнение значения страны в форме точки загрузки или выгрузки (Беларусь)
export function addBelarusValueToCountryInputs(pointForm) {
	const countryInput = pointForm.querySelector('#country')
	const customsCountryInput = pointForm.querySelector('#customsCountry')

	countryInput.value = 'BY Беларусь'
	// customsCountryInput.value = 'BY Беларусь'
	countryInput.setAttribute('readonly', 'true')
	// customsCountryInput.setAttribute('readonly', 'true')
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
	marketNumberInput.readOnly = true
	marketInfoSpan.value = orderData.marketInfo
	form.counterparty.value = orderData.counterparty
	form.loadNumber.value = orderData.marketNumber
	form.cargo.value = orderData.cargo
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

// проверка наличия всех обязательных данных о точках
export function isInvalidPointForms(routeForm) {
	const pointForms = routeForm.querySelectorAll('.pointForm')
	// возвращаем false, чтобы работали кнопки создания точек маршрута
	if (!pointForms.length) return false
	const isValidPointForms = []

	pointForms.forEach(form => {
		const isValidForm = form.reportValidity()
		isValidPointForms.push(isValidForm)
	})

	return isValidPointForms.includes(false)
}

export function isValidPallCount(order) {
	const way = order.way
	const points = order.points
	const pallCount = points
		.filter(point => point.type === 'Загрузка')
		.reduce((acc, point) => acc + Number(point.pall), 0)

	// для АХО
	if (way === 'АХО') return pallCount <= MAX_PALL_COUNT_FOR_AHO_ORDER

	return true
}

// проверка валидного значения ТН ВЭД в точках
export function isValidTnvdValue(order) {
	// Регулярное выражение для проверки 10-значных чисел, разделенных запятой и пробелом
	const regex = /^(?:\d{10})(?:, \d{10})*$/
	if (order.way === 'АХО') return true
	const points = order.points
	if (!points) return true
	return points.every(point => {
		const tnvd = point.tnvd
		if (!tnvd) return true
		return regex.test(tnvd)
	})
}

// проверка превышения массы одной паллеты
export function isValidPallWeight(order) {
	const points = order.points
	if (!points) return true
	return points.every(point => {
		const pallCount = Number(point.pall)
		const weight = point.weight ? Number(point.weight) : 0
		return weight / pallCount <= MAX_ONE_PALL_WEIGHT_KG
	}) 
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

	// отображение дополнительных полей для Импорта
	if (way === 'Импорт') {
	// 	showFormField('recipient', 'ЗАО "Доброном"', true)
	// 	showFormField('control', '', true)
	// 	showFormField('routeComments', '', false)
	// 	showFormField('truckLoadCapacity', '', true)
	// 	showFormField('truckVolume', '', true)
	// 	showFormField('phytosanitary', '', true)
	// 	showFormField('veterinary', '', true)
	// 	showFormField('dangerous', '', true)
	// 	truckVolumeVisibleToggler(typeTruck, way)
	// 	dangerousInfoVisibleToggler(dangerous)
	}

	// if (EAEUImport && way === 'Импорт') {
	// 	showFormField('tir', '', true)
	// }

	if (isInternalMovement || way === 'Экспорт') {
		hideFormField('marketNumber')
		hideFormField('marketInfo')
	}

	if (way === 'АХО') {
		hideFormField('contact')
		transformToAhoComment()
		showFormField('hydrolift', '', true)
		showFormField('carBodyLength', '', true)
		showFormField('carBodyWidth', '', true)
		showFormField('carBodyHeight', '', true)
		hideFormField('loadNumber')
		hideFormField('marketNumber')
		hideFormField('marketInfo')
		hideFormField('stacking')
		hideFormField('cargo')
		hideFormField('control')
	}

	// возможность создать тендер на понижение
	if (isInternalMovement || way === 'РБ' || way === 'Импорт' || way === 'Экспорт') {
		// const forReductionContainer = document.getElementById('forReduction-container')
		// forReductionContainer.classList.remove('none')

		showFormField('forReduction', 'on')
	}

	showIncotermsInput(typeTruck)
	changeTemperatureInputRequired(typeTruck)
}

// возвращение формы с данными заявки в стартовое состояние
export function changeFormToDefault() {
	showFormField('contact', '', false)
	// hideFormField('recipient')
	// hideFormField('control')
	// hideFormField('tir')
	hideFormField('hydrolift')
	hideFormField('carBodyLength')
	hideFormField('carBodyWidth')
	hideFormField('carBodyHeight')
	showFormField('marketNumber', '', false)
	showFormField('loadNumber', '', true)
	showFormField('marketInfo', '', false)
	// hideFormField('routeComments')
	hideFormField('deliveryLocation')
	hideFormField('stacking')
	showFormField('cargo', '', true)
	// hideFormField('truckLoadCapacity')
	// hideFormField('truckVolume')
	// hideFormField('phytosanitary')
	// hideFormField('veterinary')
	// hideFormField('dangerous')
	transformToDefaultComment()

	hideFormField('startPriceForReduction')
	hideFormField('currencyForReduction')
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

	routeForm.idOrders && (routeForm.idOrders.value = data.idOrders)
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
		const { hydrolift, carBodyLength, carBodyWidth, carBodyHeight, comment } = getTruckInfoFromComment(data)
		routeForm.hydrolift.value = hydrolift ? hydrolift : ''
		routeForm.carBodyLength.value = carBodyLength ? carBodyLength : ''
		routeForm.carBodyWidth.value = carBodyWidth ? carBodyWidth : ''
		routeForm.carBodyHeight.value = carBodyHeight ? carBodyHeight : ''
		routeForm.comment.value = comment ? comment : ''
	}

	points.forEach((point, i) => {
		const pointElement = createPointMethod(data, point, i)
		pointList.append(pointElement)
	})
}

// извлечение информации об авто из поля комментария (для АХО)
function getTruckInfoFromComment(data) {
	const commentVaue = data.comment
	const HYDROLIFT_REG = /Необходим гидроборт;/i
	const CAR_BODY_LENGTH_REG = /Длина кузова:\s*(\d+[.,]?\d*)\s*м;/i
	const CAR_BODY_WIDTH_REG = /Ширина кузова:\s*(\d+[.,]?\d*)\s*м;/i
	const CAR_BODY_HEIGHT_REG = /Высота кузова:\s*(\d+[., ]?\d*)\s*м;/i
	const hydrolift = commentVaue.match(HYDROLIFT_REG)
	const carBodyLength = commentVaue.match(CAR_BODY_LENGTH_REG)
	const carBodyWidth = commentVaue.match(CAR_BODY_WIDTH_REG)
	const carBodyHeight = commentVaue.match(CAR_BODY_HEIGHT_REG)
	const comment = commentVaue
		.replace(HYDROLIFT_REG, '')
		.replace(CAR_BODY_LENGTH_REG, '')
		.replace(CAR_BODY_WIDTH_REG, '')
		.replace(CAR_BODY_HEIGHT_REG, '')
		.trim()
	return {
		hydrolift: hydrolift ? 'Да' : 'Нет',
		carBodyLength: carBodyLength ? carBodyLength[1].trim() : '',
		carBodyWidth: carBodyWidth ? carBodyWidth[1].trim() : '',
		carBodyHeight: carBodyHeight ? carBodyHeight[1].trim() : '',
		comment: comment ? comment : ''
	}
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


// метод изменения поле комментарий для формы заявки АХО
export function transformToAhoComment() {
	const comment = document.querySelector('#comment')
	const commentContainer = comment.parentElement
	const commentLabel = commentContainer.querySelector('label')
	commentLabel.innerText = 'Дополнительная информация'
	comment.setAttribute('placeholder', 'Размеры груза, дополнительные требования к авто и др.')
}

// метод возвращения поля комментарии в первоначальный вид
export function transformToDefaultComment() {
	const comment = document.querySelector('#comment')
	const commentContainer = comment.parentElement
	const commentLabel = commentContainer.querySelector('label')
	commentLabel.innerText = 'Комментарии:'
	comment.setAttribute('placeholder', 'Комментарии')
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

// переключение видимости полей для установки цены на понижение
export function toggleForReductionInputsVisible(isVisible) {
	if (isVisible) {
		showFormField('startPriceForReduction', '', true)
		showFormField('currencyForReduction', '', true)
	} else {
		hideFormField('startPriceForReduction')
		hideFormField('currencyForReduction')
	}
}