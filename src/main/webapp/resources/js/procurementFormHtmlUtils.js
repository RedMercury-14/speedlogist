import { getStockAddress } from "./globalRules/ordersRules.js"
import { dateHelper, getInputValue } from "./utils.js"

export function getDateHTML({ isInternalMovement, pointType, way, pointIndex, value }) {
	const inputValue = value ? value : ''
	const minValidDate = getMinValidDate(isInternalMovement, way)
	const typeClassName = pointType === 'Загрузка' ? 'loadDate' : 'unloadDate'

	if (way === 'АХО') {
		return `<div class='pointDate'>
					<div class="d-flex align-items-center position-relative">
						<label for='date_${pointIndex}' class='col-form-label text-muted font-weight-bold mr-2'>Дата <span class="text-red">*</span></label>
					</div>
					<input value='${inputValue}' type='date' class='form-control ${typeClassName}' name='date' id='date_${pointIndex}' min='${minValidDate}' required>
				</div>
			`
	}
	const requiredMarker = way === "РБ" || way === 'Экспорт' || pointType === 'Загрузка' ? '<span class="text-red">*</span>' : ''
	const required = way === "РБ" || way === 'Экспорт' || pointType === 'Загрузка' ? 'required' : ''
	const infoMarker = pointType === 'Загрузка'
		? `<span id="statusInfoLabel_${pointIndex}" class="status-info-label">!</span>
			<div id="statusInfo_${pointIndex}" class="status-info">
				<p class="mb-1">При создании заявки до 11:00 текущего дня минимальная дата загрузки - завтра, после 11:00 - через 2 дня</p>
				<p class="mb-0">Для внутренних перемещений до 12:00 - завтра, после 12:00 - через 2 дня</p>
			</div>
		`
		: ''
	return `<div class='pointDate'>
				<div class="d-flex align-items-center position-relative">
					<label for='date_${pointIndex}' class='col-form-label text-muted font-weight-bold mr-2'>Дата ${requiredMarker}</label>
					${infoMarker}
				</div>
				<input value='${inputValue}' type='date' class='form-control ${typeClassName}' name='date' id='date_${pointIndex}' min='${minValidDate}' ${required}>
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


export function getTimeHTML({ pointType, way, pointIndex, value }) {
	const inputValue = value ? value : ''
	const timeOptions = getTimeOptions(inputValue)
	const typeClassName = pointType === 'Загрузка' ? 'loadTime' : 'unloadTime'
	const noneClassName = pointType === 'Выгрузка' && way !== 'Импорт' ? 'none' : ''
	const required = pointType === 'Загрузка' ? 'required' : ''
	const requiredMarker = pointType === 'Загрузка' ? '<span class="text-red">*</span>' : ''
	const timeRemark = way !== 'РБ' ? '<span class="time-mark text-muted">По местному времени</span>' : ''

	if (way === 'АХО') {
		return `<div class='pointTime'>
					<label for='time_${pointIndex}' class='col-form-label text-muted font-weight-bold '>Время</label>
					<select id='time_${pointIndex}' name="time" class="form-control ${typeClassName}">
						<option value="" hidden disabled selected> --:-- </option>
						${timeOptions}
					</select>
				</div>
			`
	}
	return `<div class='pointTime ${noneClassName}'>
				<label for='time_${pointIndex}' class='col-form-label text-muted font-weight-bold '>Время ${requiredMarker}</label>
				<select id='time_${pointIndex}' ${required} name="time" class="form-control ${typeClassName}">
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


export function getTnvdHTML({ pointType, way, pointIndex, value }) {
	if (pointType !== 'Загрузка') return ''
	if (way === 'АХО') return ''
	const inputValue = value ? value : ''
	const tnvdRequired = way === "РБ" ? '' : 'required'
	const tnvdRequiredMarker = way === "РБ" ? '' : '<span class="text-red">*</span>'
	const tooltipText = `Код ТН ВЭД состоит из 10 цифр. `
					+ `Если необходимо указать один код, просто укажте его без дополнительных знаков и символов. `
					+ `Для нескольких кодов укажите каждый код через запятую С ПРОБЕЛОМ.\n`
					+ `Примеры: `
					+ `1234567890 - верно\n`
					+ `1234567890, 0987654321 - верно\n`
					+ `1234567890,0987654321 - неверно, нет пробела после запятой\n`
					+ `12345678901 - неверно, 11 цифр\n`
					+ `1234567890, 12345 - неверно, второе число не 10-значное`

	const placeholderText = `Указывается один код ТН ВЭД или несколько кодов через запятую с пробелом. `
						+ `Для подробностей наведите курсор на красный значок вопроса`

	return `<div class='form-group'>
				<label for="tnvd_${pointIndex}" class='col-form-label text-muted font-weight-bold custom-tooltip'>
					Коды ТН ВЭД ${tnvdRequiredMarker}
					<sup class="px-1 font-weight-bold text-danger">?</sup>
					<span class="tooltiptext">${tooltipText}</span>
				</label>
				<textarea class='form-control' name='tnvd' id='tnvd_${pointIndex}' placeholder='${placeholderText}' ${tnvdRequired}>${inputValue}</textarea>
			</div>`
}


export function getCargoInfoHTML({ order, isInternalMovement, way, pointIndex, pointData, pointType }) {
	const { pallRequiredAttr, weightRequiredAttr, volumeRequiredAttr } = getRequiredAttrs(way)
	let { pointCargo, pall, weight, volume } = getCargoInfo(order, way, pointIndex, pointData, pointType)

	// const ahoReadonlyAttr = way === 'АХО' ? 'readonly' : ''
	const ahoReadonlyAttr = ''
	const ahoMaxPallAttr = way === 'АХО' ? 20 : ''
	const ahoNoneClassName = way === 'АХО' ? 'none' : ''
	const pallReadonlyAttr =
		(!isInternalMovement && way === 'РБ')
		// || way === 'АХО'
		|| way === 'Импорт'
		? 'readonly' : ''

	return `<div class='cargoName'>
				<label for='pointCargo_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Наименование груза <span class='text-red'>*</span></label>
				<input type='text' class='form-control' name='pointCargo' id='pointCargo_${pointIndex}' placeholder='Наименование' value='${pointCargo}' ${ahoReadonlyAttr} required>
			</div>
			<div class='cargoPall'>
				<label for='pall_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Паллеты, шт</label>
				<input type='number' class='form-control' name='pall' id='pall_${pointIndex}' placeholder='Паллеты, шт' min='0' step="1" max='${ahoMaxPallAttr}' value='${pall}' ${pallRequiredAttr} ${pallReadonlyAttr}>
			</div>
			<div class='cargoWeight'>
				<label for='weight_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Масса, кг</label>
				<input type='number' class='form-control' name='weight' id='weight_${pointIndex}' placeholder='Масса, кг' min='0' step="1" value='${weight}' ${ahoReadonlyAttr} ${weightRequiredAttr}>
			</div>
			<div class='cargoVolume ${ahoNoneClassName}'>
				<label for='volume_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Объем, м.куб.</label>
				<input type='number' class='form-control' name='volume' id='volume_${pointIndex}' placeholder='Объем, м.куб.' min='0' value='${volume}' ${ahoReadonlyAttr} ${volumeRequiredAttr}>
			</div>`
}
function getCargoInfo(order, way, pointIndex, pointData, pointType) {
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

	// если заявка АХО
	if (way === 'АХО') {
		if (pointIndex > 1 && pointType === 'Выгрузка') {
			return getPrevPointCargoInfo(pointIndex)
		}

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

function getRequiredAttrs(way) {
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')

	// для АХО
	if (way === 'АХО') return {
		pallRequiredAttr: 'required',
		weightRequiredAttr: 'required',
		volumeRequiredAttr: '',
	}

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


export function getAddressHTML({ order, pointType, way, pointIndex, value }) {
	const addressName = pointType === 'Загрузка' ? 'загрузки' : 'выгрузки'
	const country = getCountry(pointType, way, value)
	const countryReadonlyAttr = way === 'РБ' || way === 'АХО' ? 'readonly' : ''
	if (way === 'HHH') { // way === 'Импорт' && pointType === 'Загрузка'
		const { postIndex, region, city, street, building, buildingBody } = getImportAddressObj(value)
		return `
			<div class="form-group">
				<label for="country_${pointIndex}" class='col-form-label text-muted font-weight-bold'>Адрес ${addressName} <span class='text-red'>*</span></label>
				<div class="address-container--import">
					<div class="row-container">
						<div class="autocomplete">
							<input value='${country}' type="text" class="form-control country withoutСommas" name="country" id="country_${pointIndex}" autocomplete="off" placeholder="Страна *" required>
						</div>
						<input value='${postIndex}' type="number" class="form-control postIndex withoutСommas" name="postIndex" id="postIndex_${pointIndex}" placeholder="Индекс *" required>
						<input value='${region}' type="text" class="form-control region withoutСommas" name="region" id="region_${pointIndex}" autocomplete="off" placeholder="Регион/область *" required>
					</div>
					<div class="row-container">
						<input value='${city}' type="text" class="form-control city withoutСommas" name="city" id="city_${pointIndex}" placeholder="Город *" required>
						<input value='${street}' type="text" class="form-control street withoutСommas" name="street" id="street_${pointIndex}" placeholder="Улица *" required>
						<input value='${building}' type="text" class="form-control building withoutСommas" name="building" id="building_${pointIndex}" placeholder="Здание *" required>
						<input value='${buildingBody}' type="text" class="form-control buildingBody withoutСommas" name="buildingBody" id="buildingBody_${pointIndex}" placeholder="Корпус">
					</div>
				</div>
			</div>
		`
	}
	const address = getAddress(order, pointType, way, value)
	return `<div class="form-group">
				<label for="country_${pointIndex}" class="col-form-label text-muted font-weight-bold">Адрес ${addressName} <span class="text-red">*</span></label>
				<div class="form-group address-container">
					<div class="autocomplete">
						<input type="text" class="form-control country" ${countryReadonlyAttr} name="country" id="country_${pointIndex}" placeholder="Страна" autocomplete="off" value='${country}' required>
					</div>
					<input type="text" class="form-control address-input" name="address" id="address_${pointIndex}" autocomplete="off" placeholder="Город, улица и т.д." value='${address}' required>
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
function getAddress(order, pointType, way, addressValue) {
	let address = ''
	if (addressValue) {
		return addressValue.split('; ')[1]
	}
	if (!order || !order.numStockDelivery) return ''
	if ((pointType === 'Выгрузка' && way === 'Импорт')
		|| (pointType === 'Выгрузка' && way === 'РБ')
		|| (pointType === 'Загрузка' && way === 'Экспорт')
	) address = getStockAddress(order.numStockDelivery)
	return address
}
function getCountry(pointType, way, addressValue) {
	let country = ''
	if (pointType === 'Выгрузка' && way === 'Импорт') country = 'BY Беларусь'
	if (way === 'РБ' || way === 'АХО') country = 'BY Беларусь'
	if (pointType === 'Загрузка' && way === 'Экспорт') country = 'BY Беларусь'
	if (addressValue) country = addressValue.split('; ')[0]
	return country
}


export function getAddressInfoHTML({ pointType, way, pointIndex, pointData }) {
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
		? `<textarea class="form-control contact" rows="${textareaRows}" name="pointContact" id="pointContact_${pointIndex}" placeholder="ФИО, телефон">${pointData.contact}</textarea>`
		: `
			<input type="text" class="form-control" name="pointContact_fio" id="pointContact_fio_${pointIndex}" placeholder="ФИО" required>
			<input type="text" class="form-control" name="pointContact_tel" id="pointContact_tel_${pointIndex}" placeholder="Телефон" required>
		`
	const addressName = pointType === 'Загрузка' ? 'загрузки' : 'выгрузки'
	return way === 'РРР' // way === 'Импорт' 
		? `<div class="timeFrame-container--import">
				<span class="col-form-label text-muted font-weight-bold d-inline-block">Время работы точки ${addressName} <span class="text-red">*</span></span>
				<div class="timeFrame-inputs--import">
					<label for='weekdaysTimeFrame_from_${pointIndex}' class="grid-item1 col-form-label text-muted text-nowrap">Будние дни:</label>
					<span class="grid-item2 ">С</span>
					<input value='${weekdaysTF_from}' list="times" type="time" class="grid-item3 form-control" name="weekdaysTimeFrame_from" id="weekdaysTimeFrame_from_${pointIndex}" required>
					<span class="grid-item4 ">по</span>
					<input value='${weekdaysTF_to}' list="times" type="time" class="grid-item5 form-control" name="weekdaysTimeFrame_to" id="weekdaysTimeFrame_to_${pointIndex}" required>
					<label for='saturdayTimeFrame_from_${pointIndex}' class="grid-item6 col-form-label text-muted text-nowrap">Суббота:</label>
					<span class="grid-item7 ">С</span>
					<input value='${saturdayTF_from}' ${satutdayTFDisabledAttr} list="times" type="time" class="grid-item8 form-control" name="saturdayTimeFrame_from" id="saturdayTimeFrame_from_${pointIndex}" required>
					<span class="grid-item9 ">по</span>
					<input value='${saturdayTF_to}' ${satutdayTFDisabledAttr} list="times" type="time" class="grid-item10 form-control" name="saturdayTimeFrame_to" id="saturdayTimeFrame_to_${pointIndex}" required>
					<input class="grid-item11 " type="checkbox" ${saturdayTF_NotWorkCheckedAttr} name="saturdayTimeFrame_NotWork" id="saturdayTimeFrame_NotWork_${pointIndex}">
					<label class="grid-item12 form-check-label text-nowrap" for="saturdayTimeFrame_NotWork_${pointIndex}">Не работают</label>
					<label for='sundayTimeFrame_from_${pointIndex}' class="grid-item13 col-form-label text-muted text-nowrap">Воскресенье:</label>
					<span class="grid-item14 ">С</span>
					<input value='${sundayTF_from}' ${sundayTFDisabledAttr} list="times" type="time" class="grid-item15 form-control" name="sundayTimeFrame_from" id="sundayTimeFrame_from_${pointIndex}" required>
					<span class="grid-item16 ">по</span>
					<input value='${sundayTF_to}' ${sundayTFDisabledAttr} list="times" type="time" class="grid-item17 form-control" name="sundayTimeFrame_to" id="sundayTimeFrame_to_${pointIndex}" required>
					<input class="grid-item18 " type="checkbox" ${sundayTF_NotWorkCheckedAttr} name="sundayTimeFrame_NotWork" id="sundayTimeFrame_NotWork_${pointIndex}">
					<label class="grid-item19 form-check-label text-nowrap" for="sundayTimeFrame_NotWork_${pointIndex}">Не работают</label>
				</div>
			</div>
			<div class="contact-container--import">
				<label for="pointContact_fio_${pointIndex}" class="col-form-label text-muted font-weight-bold">Контактное лицо на точке ${addressName} <span class="text-red">*</span></label>
				<div class="contact-inputs--import">
					${contactInputs}
				</div>
			</div>
		`	
		: `<div class="timeFrame-container">
				<label for='timeFrame_from_${pointIndex}' class="col-form-label text-muted font-weight-bold">Время работы точки ${addressName} <span class="text-red">*</span></label>
				<div class="input-row-container">
					С
					<input value='${weekdaysTF_from}' list="times" type="time" class="form-control" name="timeFrame_from" id="timeFrame_from_${pointIndex}" required>
					по
					<input value='${weekdaysTF_to}' list="times" type="time" class="form-control" name="timeFrame_to" id="timeFrame_to_${pointIndex}" required>
				</div>
			</div>
			<div class="contact-container">
				<label for="pointContact_fio_${pointIndex}" class="col-form-label text-muted font-weight-bold">Контактное лицо на точке ${addressName} <span class="text-red">*</span></label>
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


export function getCustomsAddressHTML({ EAEUImport, pointType, way, pointIndex, value }) {
	const addressName = pointType === 'Загрузка' ? '(таможня отправления)' : '(таможня назначения)'
	if (way === 'РБ' || way === 'АХО' || EAEUImport) return ''
	if (way === 'Экспорт') {
		const customsCountry = getCountry(null, null, value)
		const customsAddress = getAddress(null, pointType, way, value)
		return `<div class="form-group">
					<label for="customsCountry_${pointIndex}" class="col-form-label text-muted font-weight-bold">Место таможенного оформления ${addressName}</label>
					<div class="form-group address-container">
						<div class="autocomplete">
							<input value='${customsCountry}' type="text" class="form-control country" name="customsCountry" id="customsCountry_${pointIndex}" placeholder="Страна">
						</div>
						<input value='${customsAddress}' type="text" class="form-control address-input" name="customsAddress" id="customsAddress_${pointIndex}" placeholder="Адрес">
					</div>
				</div>
			`
	}

	const customsCountry = getCountry(null, null, value)
	const customsAddress = getAddress(null, pointType, way, value)
	return `<div class="form-group">
				<label for="customsCountry_${pointIndex}" class="col-form-label text-muted font-weight-bold">Место таможенного оформления ${addressName}</label>
				<div class="form-group address-container">
					<div class="autocomplete">
						<input value='${customsCountry}' type="text" class="form-control country" name="customsCountry" id="customsCountry_${pointIndex}" placeholder="Страна">
					</div>
					<input value='${customsAddress}' type="text" class="form-control address-input" name="customsAddress" id="customsAddress_${pointIndex}" placeholder="Адрес">
				</div>
			</div>
		`

	// для Импорта


	// const customsInPointAddress = getCustomsInPointAddress(value)
	// const { country, postIndex, region, city, street, building, buildingBody } = getImportCustomsAddressObj(value, customsInPointAddress)
	// const customsInPointAddressSelected = customsInPointAddress ? 'selected' : ''
	// const header = pointType === 'Загрузка'
	// 	? `<label for="customsInPointAddress_${pointIndex}" class='col-form-label text-muted font-weight-bold'>Место таможенного оформления ${addressName} <span class='text-red'>*</span></label>
	// 		<div class="customsInPointAddress-container pb-3">
	// 			<label class="sr-only" for="customsInPointAddress_${pointIndex}">Затаможка на месте?</label>
	// 			<div class="input-group">
	// 				<div class="input-group-prepend">
	// 					<div class="input-group-text">Затаможка на месте загрузки?</div>
	// 				</div>
	// 				<select id="customsInPointAddress_${pointIndex}" name="customsInPointAddress" class="form-control" required>
	// 					<option value="" selected hidden disabled></option>
	// 					<option ${customsInPointAddressSelected} value="Да">Да</option>
	// 					<option value="Нет">Нет</option>
	// 				</select>
	// 			</div>
	// 		</div>
	// 	`
	// 	: `<label for="customsCountry_${pointIndex}" class='col-form-label text-muted font-weight-bold'>Место таможенного оформления ${addressName} <span class='text-red'>*</span></label>`
	// let noneClassName = pointType === 'Загрузка' ? 'none' : ''
	// if (value && !customsInPointAddress) noneClassName = ''
	// const required = pointType === 'Загрузка' ? '' : 'required'
	// return `<div>
	// 			${header}
	// 			<div class="address-container--import customsContainer_${pointIndex} ${noneClassName}">
	// 				<div class="row-container">
	// 					<div class="autocomplete">
	// 						<input value='${country}' type="text" class="form-control country withoutСommas" name="customsCountry" id="customsCountry_${pointIndex}" placeholder="Страна *" ${required}>
	// 					</div>
	// 					<input value='${postIndex}' type="number" class="form-control postIndex withoutСommas" name="customsPostIndex" id="customsPostIndex_${pointIndex}" placeholder="Индекс *" ${required}>
	// 					<input value='${region}' type="text" class="form-control region withoutСommas" name="customsRegion" id="customsRegion_${pointIndex}" placeholder="Регион/область *" ${required}>
	// 				</div>
	// 				<div class="row-container">
	// 					<input value='${city}' type="text" class="form-control city withoutСommas" name="customsCity" id="customsCity_${pointIndex}" placeholder="Город *" ${required}>
	// 					<input value='${street}' type="text" class="form-control street withoutСommas" name="customsStreet" id="customsStreet_${pointIndex}" placeholder="Улица *" ${required}>
	// 					<input value='${building}' type="text" class="form-control building withoutСommas" name="customsBuilding" id="customsBuilding_${pointIndex}" placeholder="Здание *" ${required}>
	// 					<input value='${buildingBody}' type="text" class="form-control buildingBody withoutСommas" name="customsBuildingBody" id="customsBuildingBody_${pointIndex}" placeholder="Корпус">
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
