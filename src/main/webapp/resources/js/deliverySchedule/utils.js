import { ResetStateToolPanel } from "../AG-Grid/ag-grid-utils.js"
import { bootstrap5overlay } from "../bootstrap5overlay/bootstrap5overlay.js"
import { snackbar } from "../snackbar/snackbar.js"
import { dateHelper, getData, getScheduleStatus, isAdmin, isOrderSupport, isORL } from "../utils.js"

const changeScheduleStatusBaseUrl = '../../api/slots/delivery-schedule/changeStatus/'

export const SUPPLY_REG = /(понедельник|вторник|среда|четверг|пятница|суббота|воскресенье)/
export const SUPPLY_REG_GLOBAL = /(понедельник|вторник|среда|четверг|пятница|суббота|воскресенье)/g
export const ORDER_REG = /^з$|з\//
export const WEEK_INDEX_REG = /(?<=н)\d+/g


// опции для графика без пометки "неделя"
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
// опции для графика с пометкой "неделя"
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


// обработчик смены пометки Сроки/Неделя
export function onNoteChangeHandler(e) {
	const note = e.target.checked ? 'неделя' : ''
	const form = e.target.form
	changeScheduleOptions(form, note)
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


// отображение модального окна с сообщением
export function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerText = message
	$('#displayMessageModal').modal('show')
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

// удаление графика поставок
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
	if (!isAdmin(role) && !isOrderSupport(role) && !isORL(role)) return
	const statusText = getScheduleStatus(status)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeScheduleStatusBaseUrl}${idSchedule}&${status}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		rowNode.setDataValue('status', status)
		rowNode.setDataValue('statusToView', statusText)
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
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


// создание опций
export function createOptions(optionData, select) {
	optionData.forEach((option) => {
		const optionElement = document.createElement('option')
		optionElement.value = option
		optionElement.text = option
		select.append(optionElement)
	})
}


// функция получения массива с данными графика поставок
export function getScheduleArrayFromScheduleObj(schedule) {
	return [
		schedule.monday ? schedule.monday : null,
		schedule.tuesday ? schedule.tuesday : null,
		schedule.wednesday ? schedule.wednesday : null,
		schedule.thursday ? schedule.thursday : null,
		schedule.friday ? schedule.friday : null,
		schedule.saturday ? schedule.saturday : null,
		schedule.sunday ? schedule.sunday : null,
	]
}

// получение количества поставок по данным графика поставок
export function getSupplies(data) {
	const schedule = getScheduleArrayFromScheduleObj(data)
	return schedule.filter(el => SUPPLY_REG.test(el)).length
}

// получение количества заказов по данным графика поставок
export function getOrders(data) {
	const schedule = getScheduleArrayFromScheduleObj(data)
	return schedule.filter(el => ORDER_REG.test(el)).length
}

// получение данных из поля textarea
export function getTextareaData(value) {
	const isTabSeparator = value.includes('\t')
	const isLineSeparator = value.includes('\n')
	const isSpaceSeparator = value.includes(' ') && !isTabSeparator
	switch(true) {
		case isTabSeparator:
			return value
				.split('\t')
				.map(point => point.includes('\n') ? point.slice(0, -1) : point)
				.filter(point => point !== '')
		case isLineSeparator:
			return value.split('\n').filter(point => point !== '')
		case isSpaceSeparator:
			return value.split(' ').filter(point => point !== '')
		default:
			return value.split('\n').filter(point => point !== '')
	}
}
