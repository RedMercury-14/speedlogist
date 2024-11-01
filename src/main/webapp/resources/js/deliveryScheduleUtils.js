import { ResetStateToolPanel } from "./AG-Grid/ag-grid-utils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { snackbar } from "./snackbar/snackbar.js"
import { dateHelper, getData, getDeliveryScheduleMatrix, getScheduleStatus, isAdmin, isOderSupport } from "./utils.js"

const changeScheduleStatusBaseUrl = '../../api/slots/delivery-schedule/changeStatus/'

export const SUPPLY_REG = /(понедельник|вторник|среда|четверг|пятница|суббота|воскресенье)/
export const SUPPLY_REG_GLOBAL = /(понедельник|вторник|среда|четверг|пятница|суббота|воскресенье)/g
export const ORDER_REG = /^з$|з\//

export const errorMessages = {
	formError: 'Ошибка заполнения формы',
	isNotCompareOrdersAndSupplies: 'Не совпадают дни поставок и заказов',
	isNotValidSupplyWeekIndexes: 'Ошибка в указании номера недели для поставки',
	isNotOrdersAndSupplies: 'Не указаны дни заказа и поставки',
	isNotSuppliesEqualToOrders: 'Количество поставок и заказов не совпадает',
	isNotValidSuppliesNumber: 'Указанное количество поставок не совпадает с фактическим',
}


export const defaultOptions = [
	"",
	"з",
	"понедельник",
	"вторник",
	"среда",
	"четверг",
	"пятница",
	"суббота",
	"воскресенье",
	"з/понедельник",
	"з/вторник",
	"з/среда",
	"з/четверг",
	"з/пятница",
	"з/суббота",
	"з/воскресенье"
]
export const weekOptions = [
	"",
	"з",
	"з/н0/понедельник",
	"з/н0/вторник",
	"з/н0/среда",
	"з/н0/четверг",
	"з/н0/пятница",
	"з/н0/суббота",
	"з/н0/воскресенье",
	"з/н1/понедельник",
	"з/н1/вторник",
	"з/н1/среда",
	"з/н1/четверг",
	"з/н1/пятница",
	"з/н1/суббота",
	"з/н1/воскресенье",
	"з/н2/понедельник",
	"з/н2/вторник",
	"з/н2/среда",
	"з/н2/четверг",
	"з/н2/пятница",
	"з/н2/суббота",
	"з/н2/воскресенье",
	"з/н3/понедельник",
	"з/н3/вторник",
	"з/н3/среда",
	"з/н3/четверг",
	"з/н3/пятница",
	"з/н3/суббота",
	"з/н3/воскресенье",
	"з/н4/понедельник",
	"з/н4/вторник",
	"з/н4/среда",
	"з/н4/четверг",
	"з/н4/пятница",
	"з/н4/суббота",
	"з/н4/воскресенье",
	"н0/понедельник",
	"н0/вторник",
	"н0/среда",
	"н0/четверг",
	"н0/пятница",
	"н0/суббота",
	"н0/воскресенье",
	"н1/понедельник",
	"н1/вторник",
	"н1/среда",
	"н1/четверг",
	"н1/пятница",
	"н1/суббота",
	"н1/воскресенье",
	"н2/понедельник",
	"н2/вторник",
	"н2/среда",
	"н2/четверг",
	"н2/пятница",
	"н2/суббота",
	"н2/воскресенье",
	"н3/понедельник",
	"н3/вторник",
	"н3/среда",
	"н3/четверг",
	"н3/пятница",
	"н3/суббота",
	"н3/воскресенье",
	"н4/понедельник",
	"н4/вторник",
	"н4/среда",
	"н4/четверг",
	"н4/пятница",
	"н4/суббота",
	"н4/воскресенье"
]

export const deliveryScheduleRowClassRules = {
	'grey-row': params => params.node.data.status === 10,
	'red-row': params => params.node.data.status === 0,
}

export const deliveryScheduleColumnDefs = [
	{
		headerName: 'Код контрагента', field: 'counterpartyCode',
		cellClass: 'px-1 py-0 text-center',
		width: 120, pinned: 'left',
	},
	{
		headerName: 'Наименование контрагента', field: 'name',
		cellClass: 'px-1 py-0 text-center',
		width: 300,
	},
	{
		headerName: 'Номер контракта', field: 'counterpartyContractCode',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 150,
	},
	{
		headerName: 'Пометка "Неделя"', field: 'note',
		cellClass: 'px-1 py-0 text-center',
		cellClassRules: {
			'blue-cell': params => params.value === 'неделя',
		},
		width: 125,
	},
	{
		headerName: 'Статус', field: 'status',
		cellClass: 'px-1 py-0 text-center',
		hide: true,
		width: 80,
	},
	{
		headerName: 'Статус', field: 'statusToView',
		cellClass: 'px-1 py-0 text-center',
		width: 180,
	},
	{
		headerName: 'Пн', field: 'monday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Вт', field: 'tuesday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Ср', field: 'wednesday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Чт', field: 'thursday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Пт', field: 'friday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Сб', field: 'saturday',
		cellClass: 'px-1 py-0 text-center ',
		width: 135,
	},
	{
		headerName: 'Вс', field: 'sunday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Кол-во поставок', field: 'supplies',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
]

export const deliveryScheduleSideBar = (localStorageKey) => ({
	toolPanels: [
		{
			id: 'columns',
			labelDefault: 'Columns',
			labelKey: 'columns',
			iconKey: 'columns',
			toolPanel: 'agColumnsToolPanel',
			toolPanelParams: {
				suppressRowGroups: true,
				suppressValues: true,
				suppressPivots: true,
				suppressPivotMode: true,
			},
		},
		{
			id: 'filters',
			labelDefault: 'Filters',
			labelKey: 'filters',
			iconKey: 'filter',
			toolPanel: 'agFiltersToolPanel',
		},
		{
			id: 'resetState',
			iconKey: 'menu',
			labelDefault: 'Сброс настроек',
			toolPanel: ResetStateToolPanel,
			toolPanelParams: {
				localStorageKey: localStorageKey,
			},
		},
	],
})

export function dateFormatter(params) {
	return params.value ? dateHelper.getFormatDate(params.value) : ''
}

// функция наполнения списков кодов и названий контрагентов
export function createCounterpartyDatalist(scheduleData) {
	const counterpartyCodeListElem = document.querySelector('#counterpartyCodeList')
	const counterpartyNameListElem = document.querySelector('#counterpartyNameList')

	if (!counterpartyCodeListElem || !counterpartyNameListElem) return

	const counterpartyCodeList = new Set()
	const counterpartyNameList = new Set()

	scheduleData.forEach(item => {
		counterpartyCodeList.add(item.counterpartyCode)
		counterpartyNameList.add(item.name.trim())
	})

	counterpartyCodeList.forEach(code => {
		const option = document.createElement('option')
		option.value = code
		counterpartyCodeListElem.append(option)
	})

	counterpartyNameList.forEach(name => {
		const option = document.createElement('option')
		option.value = name
		counterpartyNameListElem.append(option)
	})
}



// отображение графика поставки
export function showScheduleItem(rowNode) {
	const scheduleItem = rowNode.data
	const scheduleData = [
		scheduleItem.monday,
		scheduleItem.tuesday,
		scheduleItem.wednesday,
		scheduleItem.thursday,
		scheduleItem.friday,
		scheduleItem.saturday,
		scheduleItem.sunday,
	]
	const schedule = scheduleData.map((item) => item ? item : '')
	const note = scheduleItem.note ? scheduleItem.note : ''
	const matrix = getDeliveryScheduleMatrix(schedule, note)
	renderScheduleItem(schedule)
	renderMatrix(matrix)
	$(`#showScheduleModal`).modal('show')
}


// получение количества поставок по данным графика поставок
export function getSupplies(data) {
	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	return schedule.filter(el => SUPPLY_REG.test(el)).length
}

// получение количества заказов по данным графика поставок
export function getOrders(data) {
	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	return schedule.filter(el => ORDER_REG.test(el)).length
}


// изменение набора опций для графика
export function changeScheduleOptions(form, note) {
	const scheduleSelects = form.querySelectorAll('.scheduleSelect')
	const optionData = note === 'неделя'
		? weekOptions
		: defaultOptions
	scheduleSelects.forEach(select => select.innerHTML = '')
	scheduleSelects.forEach(select => createOptions(optionData, select))
}

// создание опций
export function createOptions(optionData, select) {
	optionData.forEach((option) => {
		const optionElement = document.createElement('option')
		optionElement.value = option
		optionElement.text = option
		select.append(optionElement)
	})
}

// очистка формы
function clearForm(e, form) {
	form.reset()
}


// отображение модального окна с сообщением
export function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerText = message
	$('#displayMessageModal').modal('show')
}

// отрисовка графика поставки
function renderScheduleItem(schedule) {
	const days = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс']

	const container = document.querySelector('#scheduleItemContainer')
	container.innerHTML = ''

	days.forEach(day => {
		const cellDiv = document.createElement('div')
		cellDiv.textContent = day
		cellDiv.classList.add('header-cell')
		container.append(cellDiv)
	})

	schedule.forEach(item => {
		const cellDiv = document.createElement('div')
		cellDiv.textContent = item
		container.append(cellDiv)
	})
}

// отрисовка матрицы графика поставки
function renderMatrix(matrix) {
	const container = document.querySelector('#matrixContainer')
	container.innerHTML = ''

	matrix.forEach(row => {
		row.forEach(cell => {
			const cellDiv = document.createElement('div')
			if (row[0] === '' || cell === row[0]) {
				cellDiv.classList.add('header-cell')
				cellDiv.textContent = cell
			} else {
				const parts = cell.split('/')
				parts.forEach((part, i) => {
					const span = document.createElement('span')
					span.textContent = part
					span.classList.add(getColorClass(part))
					cellDiv.append(span)
					if (parts[i + 1]) cellDiv.append('/')
				})
			}
			container.append(cellDiv)
		})
	})
}
function getColorClass(value) {
	const count = getCount(value)
	return `color-${count}`
}
function getCount(value) {
	const digitReg = /\d/
	const countMatching = value.match(digitReg)
	return countMatching ? countMatching[0] : 0
}

// проверка соответствия количества поставок и заказов
export function isSuppliesEqualToOrders(data) {
	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	const supplies = schedule.filter(el => SUPPLY_REG.test(el)).length
	const orders = schedule.filter(el => ORDER_REG.test(el)).length
	return supplies === orders
}

// проверка наличия хотя бы одного заказа и одной поставк
export function isOrdersAndSupplies(data) {
	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	const supplies = schedule.filter(el => SUPPLY_REG.test(el)).length
	const orders = schedule.filter(el => ORDER_REG.test(el)).length
	return supplies > 0 && orders > 0
}


// проверка, что график заполнен правильно
export function isValidSchedule(data) {
	if (!data) return {
		isValid: false,
		message: ''
	}

	let isValid = true
	const errorMessageData = []

	if (!compareOrdersAndSupplies(data)) {
		isValid = false
		errorMessageData.push(errorMessages.isNotCompareOrdersAndSupplies)
	}

	if (!checkSupplyWeekIndexes(data)) {
		isValid = false
		errorMessageData.push(errorMessages.isNotValidSupplyWeekIndexes)
	}

	if (!isOrdersAndSupplies(data)) {
		isValid = false
		errorMessageData.push(errorMessages.isNotOrdersAndSupplies)
	}

	if (!isSuppliesEqualToOrders(data)) {
		isValid = false
		errorMessageData.push(errorMessages.isNotSuppliesEqualToOrders)
	}

	if (!checkSuppliesNumber(data)) {
		isValid = false
		errorMessageData.push(errorMessages.isNotValidSuppliesNumber)
	}

	return {
		isValid,
		message: errorMessageData.join('; ')
	}
}

export function getErrorMessage(data, error) {
	if (!isOrdersAndSupplies(data)) return `${errorMessages.isNotOrdersAndSupplies}, проверьте данные!`
	if (!isSuppliesEqualToOrders(data)) return `${errorMessages.isNotSuppliesEqualToOrders}, проверьте данные!`
	if (!compareOrdersAndSupplies(data)) return `${errorMessages.isNotCompareOrdersAndSupplies}, проверьте данные!`
	if (!checkSupplyWeekIndexes(data)) return `${errorMessages.isNotValidSupplyWeekIndexes}, проверьте данные!`
	if (error) return `${errorMessages.formError}!`
	return ''
}

// проверка, что дни поставок и заказов совпадают
export function compareOrdersAndSupplies(data) {
	const schedule = {
		"понедельник": data.monday,
		"вторник": data.tuesday,
		"среда": data.wednesday,
		"четверг": data.thursday,
		"пятница": data.friday,
		"суббота": data.saturday,
		"воскресенье": data.sunday,
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
export function checkSupplyWeekIndexes(data) {
	if (data.note !== 'неделя') return true
	if (data.supplies < 2) return true

	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	const supplies = schedule.filter(el => SUPPLY_REG.test(el))
	return checkWeekIndexes(supplies)
}

// рекурсивная проверка номеров недели, где каждый элемент сравнивается с последующими
function checkWeekIndexes(arr) {
	if (arr.length < 2) return true

	const WEEK_INDEX_REG = /(?<=н)\d+/g
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
			const elIndex = el.match(WEEK_INDEX_REG)[0]
			return firstIndex > elIndex
		}
		return true
	}) && checkWeekIndexes(arr)
}

// проверка фактического количества поставок и указанного
export function checkSuppliesNumber(data) {
	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	const supplies = schedule.filter(el => SUPPLY_REG.test(el)).length
	return supplies === Number(data.supplies)
}

// отображение графиков с ошибками
export function checkScheduleDate(scheduleData) {
	const title = 'Обнаружены ошибки в следующих графиках:\n\n'
	if (!scheduleData) return
	const errorLines = scheduleData
		.filter(schedule => !isValidSchedule(schedule).isValid)
		.map((schedule, index) => {
			const { message } = isValidSchedule(schedule)
			return `${index + 1}. Склад ${schedule.numStock}, Номер контракта: ${schedule.counterpartyContractCode}, ${schedule.name}, Ошибки: ${message}`
		})
		.join('\n')
	if (errorLines === '') return
	showMessageModal(title + errorLines)
}

// редактирование графика поставки
export function editScheduleItem(rowNode, setDataToForm) {
	const scheduleItem = rowNode.data
	setDataToForm(scheduleItem)
	$(`#editScheduleItemModal`).modal('show')
}

// подтверждение графика поставки
export function confirmScheduleItem(role, rowNode) {
	const scheduleItem = rowNode.data
	const idSchedule = scheduleItem.idSchedule
	if (!idSchedule) return
	const confirmStatus = 20
	changeScheduleStatus({ role, idSchedule, status: confirmStatus, rowNode })
}
// снятие подтверждения графика поставки
export function unconfirmScheduleItem(role, rowNode) {
	const scheduleItem = rowNode.data
	const idSchedule = scheduleItem.idSchedule
	if (!idSchedule) return
	const unconfirmStatus = 10
	changeScheduleStatus({ role, idSchedule, status: unconfirmStatus, rowNode })
}
// удалить графика поставки
export function deleteScheduleItem(role, rowNode) {
	const scheduleItem = rowNode.data
	const idSchedule = scheduleItem.idSchedule
	if (!idSchedule) return
	const deleteStatus = 0
	changeScheduleStatus({ role, idSchedule, status: deleteStatus, rowNode })
}

// запрос на изменение статуса графика поставки
export async function changeScheduleStatus(props) {
	const { role, status, idSchedule, rowNode } = props
	if (!isAdmin(role) && !isOderSupport(role)) return
	const statusText = getScheduleStatus(status)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeScheduleStatusBaseUrl}${idSchedule}&${status}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res.status === '200') {
		snackbar.show('Выполнено!')
		rowNode.setDataValue('status', status)
		rowNode.setDataValue('statusToView', statusText)
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}

// получение данных из поля textarea
export function getTextareaData(value) {
	const isTabSeparator = value.includes('\t')
	const data = isTabSeparator
		? value.split('\t')
			.map(point =>  point.includes('\n') ? point.slice(0, -1) : point)
			.filter(point => point !== '')
		: value.split('\n').filter(point => point !== '')

	return data
}