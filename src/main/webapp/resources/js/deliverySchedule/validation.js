import {
	defaultOptions,
	getScheduleArrayFromScheduleObj,
	ORDER_REG, showMessageModal,
	SUPPLY_REG, SUPPLY_REG_GLOBAL, WEEK_INDEX_REG,
	weekOptions
} from "./utils.js"

const errorMessages = {
	formError: 'Ошибка заполнения формы',
	isNotCompareOrdersAndSupplies: 'Не совпадают дни поставок и заказов',
	isNotValidSupplyWeekIndexes: 'Ошибка в указании номера недели для поставки',
	isNotOrdersAndSupplies: 'Не указаны дни заказа и поставки',
	isNotSuppliesEqualToOrders: 'Количество поставок и заказов не совпадает',
	isNotValidSuppliesNumber: 'Указанное количество поставок не совпадает с фактическим',
}

// отображение графиков с ошибками 
export function checkScheduleData(scheduleData) {
	const title = 'Обнаружены ошибки в следующих графиках:\n\n'
	if (!scheduleData) return

	let errorIndex = 1
	const errorLines = scheduleData
		.map(schedule => {
			const errors = getScheduleErrors(schedule)
			return errors
				? `${errorIndex++}. Склад/ТО ${schedule.numStock}, Номер контракта: ${schedule.counterpartyContractCode}, ${schedule.name}. Ошибки: ${errors}`
				: null
		})
		.filter(line => line)
		.join('\n')

	if (errorLines) {
		showMessageModal(title + errorLines)
	}
}

// проверка корректности значений в полях графика
export function isValidScheduleValues(data) {
	const schedule = getScheduleArrayFromScheduleObj(data)
	const isWeekNote = data.note === 'неделя'
	return schedule.every(el => {
		if (!el) return true
		return isWeekNote
			? weekOptions.includes(el)
			: defaultOptions.includes(el)
	})
}

// метод получения ошибок при заполнении формы
export function getFormErrorMessage(data, error) {
	const errors = getScheduleErrors(data)
	if (errors) return `${errors}. Проверьте данные!`
	if (error) return `${errorMessages.formError}!`
	return ''
}


// проверка, что график заполнен правильно ПРИ МАССОВОЙ ПРОВЕРКЕ
function getScheduleErrors(data) {
	if (!data) return ''

	const errorMessageData = []
	// исключение для МИНСКХЛЕБПРОМ
	const isExeption = data.counterpartyCode === 9732 && data.type === 'ТО' && data.supplies > 4

	// Выполняем проверки, добавляем ошибки только если это не исключение
	if (!compareOrdersAndSupplies(data) && !isExeption) {
		errorMessageData.push(errorMessages.isNotCompareOrdersAndSupplies)
	}
	if (!checkSupplyWeekIndexes(data)) {
		errorMessageData.push(errorMessages.isNotValidSupplyWeekIndexes)
	}
	if (!isOrdersAndSupplies(data)) {
		errorMessageData.push(errorMessages.isNotOrdersAndSupplies)
	}
	if (!isSuppliesEqualToOrders(data) && !isExeption) {
		errorMessageData.push(errorMessages.isNotSuppliesEqualToOrders)
	}
	if (!checkSuppliesNumber(data)) {
		errorMessageData.push(errorMessages.isNotValidSuppliesNumber)
	}

	return errorMessageData.join('; ')
}


// проверка наличия хотя бы одного заказа и одной поставк
function isOrdersAndSupplies(data) {
	const schedule = getScheduleArrayFromScheduleObj(data)
	const supplies = schedule.filter(el => SUPPLY_REG.test(el)).length
	const orders = schedule.filter(el => ORDER_REG.test(el)).length
	return supplies > 0 && orders > 0
}

// проверка соответствия количества поставок и заказов
function isSuppliesEqualToOrders(data) {
	const schedule = getScheduleArrayFromScheduleObj(data)
	const supplies = schedule.filter(el => SUPPLY_REG.test(el)).length
	const orders = schedule.filter(el => ORDER_REG.test(el)).length
	return supplies === orders
}

// проверка, что дни поставок и заказов совпадают
function compareOrdersAndSupplies(data) {
	const schedule = {
		"понедельник": data.monday ? data.monday : null,
		"вторник": data.tuesday ? data.tuesday : null,
		"среда": data.wednesday ? data.wednesday : null,
		"четверг": data.thursday ? data.thursday : null,
		"пятница": data.friday ? data.friday : null,
		"суббота": data.saturday ? data.saturday : null,
		"воскресенье": data.sunday ? data.sunday : null,
	}

	// получаем список дней заказов
	const orderDays = Object.keys(schedule)
		.filter(key => ORDER_REG.test(schedule[key]))
		.sort()

	// получаем значения дней доставок
	const supplyValues = Object.values(schedule)
		.filter(value => SUPPLY_REG.test(value))
		.map(str => {
			const matchedDays = str.match(SUPPLY_REG_GLOBAL)
			return matchedDays ? matchedDays.join(' ') : ''
		})
		.sort()

	// проверяем, что количество поставок равно количеству заказов
	if (orderDays.length !== supplyValues.length) return false

	// сравниваем значения доставок и дней заказов
	return orderDays.every((day, index) => supplyValues[index].includes(day))
}

// проверка, правильно ли указаны пометки номера недели для поставок
function checkSupplyWeekIndexes(data) {
	if (data.note !== 'неделя') return true
	if (data.supplies < 2) return true

	const schedule = getScheduleArrayFromScheduleObj(data)
	const supplies = schedule.filter(el => SUPPLY_REG.test(el))
	return checkWeekIndexes(supplies)
}

// рекурсивная проверка номеров недели, где каждый элемент сравнивается с последующими
function checkWeekIndexes(arr) {
	if (arr.length < 2) return true

	const dayNumberDictionary = {
		"понедельник": 0,
		"вторник": 1,
		"среда": 2,
		"четверг": 3,
		"пятница": 4,
		"суббота": 5,
		"воскресенье": 6,
	}
	const first = arr.shift()

	return arr.every(el => {
		const firstIndex = first.match(WEEK_INDEX_REG)[0]
		const firstDay = first.match(SUPPLY_REG)[0]
		const firstDayNumber = dayNumberDictionary[firstDay]

		const elDay = el.match(SUPPLY_REG)[0]
		const elDayNumber = dayNumberDictionary[elDay]

		if (firstDayNumber > elDayNumber) {
			const weekMatch = el.match(WEEK_INDEX_REG)
			if (!weekMatch) return false
			const elIndex = weekMatch[0]
			return firstIndex > elIndex
		}
		return true
	}) && checkWeekIndexes(arr)
}

// проверка фактического количества поставок и указанного
function checkSuppliesNumber(data) {
	const schedule = getScheduleArrayFromScheduleObj(data)
	const supplies = schedule.filter(el => SUPPLY_REG.test(el)).length
	return supplies === Number(data.supplies)
}
