import { getStockAddress } from "./procurementFormUtils.js"
import { dateHelper, getInputValue } from "./utils.js"

export function getDateHTML(isInternalMovement, type, way, index, value) {
	const inputValue = value ? value : ''
	const minValidDate = getMinValidDate(isInternalMovement, way)
	const typeClassName = type === 'Загрузка' ? 'loadDate' : 'unloadDate'

	if (way === 'АХО') {
		return `<div class='pointDate'>
					<div class="d-flex align-items-center position-relative">
						<label for='date_${index}' class='col-form-label text-muted font-weight-bold mr-2'>Дата <span class="text-red">*</span></label>
					</div>
					<input value='${inputValue}' type='date' class='form-control ${typeClassName}' name='date' id='date_${index}' min='${minValidDate}' required>
				</div>
			`
	}
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


export function getTimeHTML(type, way, index, value) {
	const inputValue = value ? value : ''
	const timeOptions = getTimeOptions(inputValue)
	const typeClassName = type === 'Загрузка' ? 'loadTime' : 'unloadTime'
	const noneClassName = type === 'Выгрузка' && way !== 'Импорт' ? 'none' : ''
	const required = type === 'Загрузка' ? 'required' : ''
	const requiredMarker = type === 'Загрузка' ? '<span class="text-red">*</span>' : ''
	const timeRemark = way !== 'РБ' ? '<span class="time-mark text-muted">По местному времени</span>' : ''

	if (way === 'АХО') {
		return `<div class='pointTime'>
					<label for='time_${index}' class='col-form-label text-muted font-weight-bold '>Время</label>
					<select id='time_${index}' name="time" class="form-control ${typeClassName}">
						<option value="" hidden disabled selected> --:-- </option>
						${timeOptions}
					</select>
				</div>
			`
	}
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


export function getTnvdHTML(type, way, index, value) {
	if (type !== 'Загрузка') return ''
	if (way === 'АХО') return ''
	const inputValue = value ? value : ''
	const tnvdRequired = way === "РБ" ? '' : 'required'
	const tnvdRequiredMarker = way === "РБ" ? '' : '<span class="text-red">*</span>'
	return `<div class='form-group'>
				<label for="tnvd_${index}" class='col-form-label text-muted font-weight-bold'>Коды ТН ВЭД ${tnvdRequiredMarker}</label>
				<textarea class='form-control' name='tnvd' id='tnvd_${index}' placeholder='Коды ТН ВЭД' ${tnvdRequired}>${inputValue}</textarea>
			</div>`
}


export function getCargoInfoHTML(order, isInternalMovement, way, index, pointData) {
	const { pallRequiredAttr, weightRequiredAttr, volumeRequiredAttr } = getRequiredAttrs()
	let { pointCargo, pall, weight, volume } = getCargoInfo(order, way, index, pointData)

	const ahoReadonlyAttr = way === 'АХО' ? 'readonly' : ''
	const ahoNoneClassName = way === 'АХО' ? 'none' : ''
	const pallReadonlyAttr =
		(!isInternalMovement && way === 'РБ')
		|| way === 'АХО'
		|| way === 'Импорт'
		? 'readonly' : ''

	return `<div class='cargoName'>
				<label for='pointCargo_${index}' class='col-form-label text-muted font-weight-bold'>Наименование груза <span class='text-red'>*</span></label>
				<input type='text' class='form-control' name='pointCargo' id='pointCargo_${index}' placeholder='Наименование' value='${pointCargo}' ${ahoReadonlyAttr} required>
			</div>
			<div class='cargoPall'>
				<label for='pall_${index}' class='col-form-label text-muted font-weight-bold'>Паллеты, шт</label>
				<input type='number' class='form-control' name='pall' id='pall_${index}' placeholder='Паллеты, шт' min='0' value='${pall}' ${pallRequiredAttr} ${pallReadonlyAttr}>
			</div>
			<div class='cargoWeight'>
				<label for='weight_${index}' class='col-form-label text-muted font-weight-bold'>Масса, кг</label>
				<input type='number' class='form-control' name='weight' id='weight_${index}' placeholder='Масса, кг' min='0' value='${weight}' ${ahoReadonlyAttr} ${weightRequiredAttr}>
			</div>
			<div class='cargoVolume ${ahoNoneClassName}'>
				<label for='volume_${index}' class='col-form-label text-muted font-weight-bold'>Объем, м.куб.</label>
				<input type='number' class='form-control' name='volume' id='volume_${index}' placeholder='Объем, м.куб.' min='0' value='${volume}' ${ahoReadonlyAttr} ${volumeRequiredAttr}>
			</div>`
}
function getCargoInfo(order, way, pointIndex, pointData) {
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

	// если заявка АХО
	if (way === 'АХО') {
		cargoInfo.pointCargo = getInputValue(document, '#cargo')
		cargoInfo.pall = getInputValue(document, '#orderPall')
		cargoInfo.weight = getInputValue(document, '#orderWeight')
		return cargoInfo
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


export function getAddressHTML(order, type, way, index, value) {
	const addressName = type === 'Загрузка' ? 'загрузки' : 'выгрузки'
	const country = getCountry(type, way, value)
	const countryReadonlyAttr = way === 'РБ' || way === 'АХО' ? 'readonly' : ''
	if (way === 'HHH') { // way === 'Импорт' && type === 'Загрузка'
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
						<input type="text" class="form-control country" ${countryReadonlyAttr} name="country" id="country_${index}" placeholder="Страна" autocomplete="off" value='${country}' required>
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
	if (way === 'РБ' || way === 'АХО') country = 'BY Беларусь'
	if (type === 'Загрузка' && way === 'Экспорт') country = 'BY Беларусь'
	if (addressValue) country = addressValue.split('; ')[0]
	return country
}


export function getAddressInfoHTML(type, way, index, pointData) {
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

	const textareaRows = way === 'РРР' ? 3 : 1 // way === 'Импорт' 

	// если есть данные (для маршрутов, копирования и редактирования), то общее поле контакта
	const contactInputs = pointData
		? `<textarea class="form-control contact" rows="${textareaRows}" name="pointContact" id="pointContact_${index}" placeholder="ФИО, телефон">${pointData.contact}</textarea>`
		: `
			<input type="text" class="form-control" name="pointContact_fio" id="pointContact_fio_${index}" placeholder="ФИО" required>
			<input type="text" class="form-control" name="pointContact_tel" id="pointContact_tel_${index}" placeholder="Телефон" required>
		`
	const addressName = type === 'Загрузка' ? 'загрузки' : 'выгрузки'
	return way === 'РРР' // way === 'Импорт' 
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


export function getCustomsAddressHTML(EAEUImport, type, way, index, value) {
	const addressName = type === 'Загрузка' ? '(таможня отправления)' : '(таможня назначения)'
	if (way === 'РБ' || way === 'АХО' || EAEUImport) return ''
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

	// для Импорта


	// const customsInPointAddress = getCustomsInPointAddress(value)
	// const { country, postIndex, region, city, street, building, buildingBody } = getImportCustomsAddressObj(value, customsInPointAddress)
	// const customsInPointAddressSelected = customsInPointAddress ? 'selected' : ''
	// const header = type === 'Загрузка'
	// 	? `<label for="customsInPointAddress_${index}" class='col-form-label text-muted font-weight-bold'>Место таможенного оформления ${addressName} <span class='text-red'>*</span></label>
	// 		<div class="customsInPointAddress-container pb-3">
	// 			<label class="sr-only" for="customsInPointAddress_${index}">Затаможка на месте?</label>
	// 			<div class="input-group">
	// 				<div class="input-group-prepend">
	// 					<div class="input-group-text">Затаможка на месте загрузки?</div>
	// 				</div>
	// 				<select id="customsInPointAddress_${index}" name="customsInPointAddress" class="form-control" required>
	// 					<option value="" selected hidden disabled></option>
	// 					<option ${customsInPointAddressSelected} value="Да">Да</option>
	// 					<option value="Нет">Нет</option>
	// 				</select>
	// 			</div>
	// 		</div>
	// 	`
	// 	: `<label for="customsCountry_${index}" class='col-form-label text-muted font-weight-bold'>Место таможенного оформления ${addressName} <span class='text-red'>*</span></label>`
	// let noneClassName = type === 'Загрузка' ? 'none' : ''
	// if (value && !customsInPointAddress) noneClassName = ''
	// const required = type === 'Загрузка' ? '' : 'required'
	// return `<div>
	// 			${header}
	// 			<div class="address-container--import customsContainer_${index} ${noneClassName}">
	// 				<div class="row-container">
	// 					<div class="autocomplete">
	// 						<input value='${country}' type="text" class="form-control country withoutСommas" name="customsCountry" id="customsCountry_${index}" placeholder="Страна *" ${required}>
	// 					</div>
	// 					<input value='${postIndex}' type="number" class="form-control postIndex withoutСommas" name="customsPostIndex" id="customsPostIndex_${index}" placeholder="Индекс *" ${required}>
	// 					<input value='${region}' type="text" class="form-control region withoutСommas" name="customsRegion" id="customsRegion_${index}" placeholder="Регион/область *" ${required}>
	// 				</div>
	// 				<div class="row-container">
	// 					<input value='${city}' type="text" class="form-control city withoutСommas" name="customsCity" id="customsCity_${index}" placeholder="Город *" ${required}>
	// 					<input value='${street}' type="text" class="form-control street withoutСommas" name="customsStreet" id="customsStreet_${index}" placeholder="Улица *" ${required}>
	// 					<input value='${building}' type="text" class="form-control building withoutСommas" name="customsBuilding" id="customsBuilding_${index}" placeholder="Здание *" ${required}>
	// 					<input value='${buildingBody}' type="text" class="form-control buildingBody withoutСommas" name="customsBuildingBody" id="customsBuildingBody_${index}" placeholder="Корпус">
	// 				</div>
	// 			</div>
	// 		</div>
	// 	`
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
