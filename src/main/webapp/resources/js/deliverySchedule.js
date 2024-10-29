import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { changeGridTableMarginTop, dateHelper, debounce, disableButton, enableButton, getData, getDeliveryScheduleMatrix, getScheduleStatus, hideLoadingSpinner, isAdmin, isOderSupport, showLoadingSpinner } from './utils.js'

const loadExcelUrl = '../../api/slots/delivery-schedule/load'
const getScheduleUrl = '../../api/slots/delivery-schedule/getList'
const addScheduleItemUrl = '../../api/slots/delivery-schedule/create'
const editScheduleItemUrl = '../../api/slots/delivery-schedule/edit'
const getScheduleNumContractBaseUrl = '../../api/slots/delivery-schedule/getScheduleNumContract/'
const changeScheduleStatusBaseUrl = '../../api/slots/delivery-schedule/changeStatus/'
const changeIsNotCalcBaseUrl = '../../api/slots/delivery-schedule/changeIsNotCalc/'


const PAGE_NAME = 'deliverySchedule'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value
const login = document.querySelector('#login').value.toLowerCase()

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const SUPPLY_REG = /(понедельник|вторник|среда|четверг|пятница|суббота|воскресенье)/
const SUPPLY_REG_GLOBAL = /(понедельник|вторник|среда|четверг|пятница|суббота|воскресенье)/g
const ORDER_REG = /^з$|з\//

const errorMessages = {
	formError: 'Ошибка заполнения формы',
	isNotCompareOrdersAndSupplies: 'Не совпадают дни поставок и заказов',
	isNotValidSupplyWeekIndexes: 'Ошибка в указании номера недели для поставки',
	isNotOrdersAndSupplies: 'Не указаны дни заказа и поставки',
	isNotSuppliesEqualToOrders: 'Количество поставок и заказов не совпадает',
	isNotValidSuppliesNumber: 'Указанное количество поставок не совпадает с фактическим',
}

let error = false
let table
let scheduleData
const stocks = ['1700', '1250', '1200']
const defaultOptions = [
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
const weekOptions = [
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

export const rowClassRules = {
	'grey-row': params => params.node.data.status === 10,
	'red-row': params => params.node.data.status === 0,
}

const columnDefs = [
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
	{
		headerName: 'Расчет стока до Y-й поставки', field: 'runoffCalculation',
		cellClass: 'px-1 py-0 text-center',
		width: 100,
	},
	{
		headerName: 'Примечание', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		width: 300,
	},
	{
		headerName: 'Кратно поддону', field: 'multipleOfPalletToView',
		cellClass: 'px-1 py-0 text-center grid-checkbox',
		width: 75,
	},
	{
		headerName: 'Кратно машине', field: 'multipleOfTruckToView',
		cellClass: 'px-1 py-0 text-center grid-checkbox',
		width: 75,
	},
	{
		headerName: 'Номер склада', field: 'numStock',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 75,
	},
	{
		headerName: 'Не учитывать в расчете ОРЛ', field: 'isNotCalc',
		cellClass: 'px-1 py-0 text-center font-weight-bold grid-checkbox',
		width: 75,
		editable: isAdmin(role) || login === 'romashkok%!dobronom.by',
		onCellValueChanged: onIsNotCalcCahngeHandler,
	},
	// {
	// 	headerName: 'Описание контракта', field: 'description',
	// 	cellClass: 'px-1 py-0 text-center',
	// },
	// {
	// 	headerName: 'Дата последнего расчета', field: 'dateLasCalculation',
	// 	cellClass: 'px-1 py-0 text-center',
	// },
	// {
	// 	headerName: 'tz', field: 'tz',
	// 	cellClass: 'px-1 py-0 text-center',
	// 	width: 75,
	// },
	// {
	// 	headerName: 'tp', field: 'tp',
	// 	cellClass: 'px-1 py-0 text-center',
	// 	width: 75,
	// },
]

// доболнительные колонки для админа
if (isAdmin(role)) {
	columnDefs.push(
		{
			headerName: 'История', field: 'history',
			cellClass: 'px-1 py-0 text-center',
		},
		{
			headerName: 'Дата последнего расчета', field: 'dateLastCalculation',
			cellClass: 'px-1 py-0 text-center',
			valueFormatter: dateFormatter,
		},
		{
			headerName: 'Дата последнего изменения', field: 'dateLastChanging',
			cellClass: 'px-1 py-0 text-center',
			valueFormatter: dateFormatter,
		},
	)
}

const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2',
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idSchedule,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	sideBar: {
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
					localStorageKey: LOCAL_STORAGE_KEY,
				},
			},
		],
	},
}

document.addEventListener('DOMContentLoaded', async () => {
	// изменение отступа для таблицы
	changeGridTableMarginTop()

	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	// форма загрузки графика из Эксель
	const sendExcelForm = document.querySelector("#sendExcelForm")
	sendExcelForm && sendExcelForm.addEventListener("submit", sendExcelFormHandler)

	// форма создания графика поставки
	const addScheduleItemForm = document.querySelector('#addScheduleItemForm')
	addScheduleItemForm && addScheduleItemForm.addEventListener('submit', addScheduleItemFormHandler)
	// форма редактирования графика поставки
	const editScheduleItemForm = document.querySelector('#editScheduleItemForm')
	editScheduleItemForm && editScheduleItemForm.addEventListener('submit', editScheduleItemFormHandler)

	// выпадающий список выбора отображаемого склада
	const numStockSelect = document.querySelector("#numStockSelect")
	createNumStockOptions(numStockSelect)
	numStockSelect && numStockSelect.addEventListener('change', onNumStockSelectChangeHandler)

	const excelNumStock = document.querySelector('#sendExcelModal #numStock')
	const addNumStock = addScheduleItemForm.querySelector('#numStock')
	const editNumStock = editScheduleItemForm.querySelector('#numStock')

	// создаем опции складов
	createOptions(stocks, excelNumStock)
	createOptions(stocks, addNumStock)
	createOptions(stocks, editNumStock)

	// чекбоксы пометки "Неделя"
	const addNoteCheckbox = addScheduleItemForm.querySelector('#addNote')
	addNoteCheckbox && addNoteCheckbox.addEventListener('change', onNoteChangeHandler)
	const editNoteCheckbox = editScheduleItemForm.querySelector('#editNote')
	editNoteCheckbox && editNoteCheckbox.addEventListener('change', onNoteChangeHandler)

	const sendScheduleDataToMailBtn = document.querySelector('#sendScheduleDataToMail')
	sendScheduleDataToMailBtn && sendScheduleDataToMailBtn.addEventListener('click', sendScheduleDataToMail)

	// проверка номера контракта
	$('.counterpartyContractCode').change(checkContractNumber)
	// обновление опций графика при открытии модалки создания графика поставки
	$('#addScheduleItemModal').on('shown.bs.modal', (e) => changeScheduleOptions(addScheduleItemForm, ''))
	// очистка форм при закрытии модалки
	$('#addScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, addScheduleItemForm))
	$('#editScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, editScheduleItemForm))

	// отображение стартовых данных
	if (window.initData) {
		await initStartData()
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData()
		})
	}
})

// установка стартовых данных
async function initStartData() {
	scheduleData = window.initData.body
	await updateTable(gridOptions, scheduleData)
	// проверка, правильно ли заполнены графики
	checkScheduleDate(scheduleData)
	// заполняем datalist кодов и названий контрагентов
	createCounterpartyDatalist(scheduleData)
	window.initData = null

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()
}

// функция наполнения списков кодов и названий контрагентов
function createCounterpartyDatalist(scheduleData) {
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

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}
async function updateTable(gridOptions, data) {
	const res = data
		? { body: data }
		: await getData(getScheduleUrl)

	scheduleData = res.body

	if (!scheduleData || !scheduleData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const numStockSelect = document.querySelector("#numStockSelect")
	const numStock = Number(numStockSelect.value)
	setScheduleData(numStock)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(getMappingScheduleItem)
}
function getMappingScheduleItem(scheduleItem) {
	return {
		...scheduleItem,
		name: scheduleItem.name.trim(),
		statusToView: getScheduleStatus(scheduleItem.status),
		multipleOfPalletToView: scheduleItem.multipleOfPallet ? '+' : '',
		multipleOfTruckToView: scheduleItem.multipleOfTruck ? '+' : '',
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node
	const status = rowNode.data.status

	const confirmUnconfirmItem = status === 10 || status === 0
		? {
			name: `Подтвердить график поставки`,
			disabled: (!isAdmin(role) && !isOderSupport(role)) || (status !== 10 && status !== 0),
			action: () => {
				confirmScheduleItem(rowNode)
			},
			icon: uiIcons.check,
		}
		: {
			name: `Снять подтверждение с графика`,
			disabled: (!isAdmin(role) && !isOderSupport(role)) || status === 0,
			action: () => {
				unconfirmScheduleItem(rowNode)
			},
			icon: uiIcons.x_lg,
		}

	const result = [
		{
			name: `Показать график поставки`,
			action: () => {
				showScheduleItem(rowNode)
			},
			icon: uiIcons.table,
		},
		"separator",
		confirmUnconfirmItem,
		{
			name: `Редактировать график поставки`,
			disabled: (!isAdmin(role) && !isOderSupport(role)),
			action: () => {
				editScheduleItem(rowNode)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Удалить график поставки`,
			disabled: (!isAdmin(role) && !isOderSupport(role)) || status === 0,
			action: () => {
				deleteScheduleItem(rowNode)
			},
			icon: uiIcons.trash,
		},
		"separator",
		"copy",
		"export",
	]

	return result
}
function dateFormatter(params) {
	return params.value ? dateHelper.getFormatDate(params.value) : ''
}

// подтверждение графика поставки
function confirmScheduleItem(rowNode) {
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return
	const confirmStatus = 20
	changeScheduleStatus(confirmStatus, counterpartyContractCode, rowNode)
}

// снятие подтверждения графика поставки
function unconfirmScheduleItem(rowNode) {
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return
	const unconfirmStatus = 10
	changeScheduleStatus(unconfirmStatus, counterpartyContractCode, rowNode)
}

// редактирование графика поставки
function editScheduleItem(rowNode) {
	const scheduleItem = rowNode.data
	setDataToForm(scheduleItem)
	$(`#editScheduleItemModal`).modal('show')
}
// удалить графика поставки
async function deleteScheduleItem(rowNode) {
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return
	const deleteStatus = 0
	changeScheduleStatus(deleteStatus, counterpartyContractCode, rowNode)
}

// отображение графика поставки
function showScheduleItem(rowNode) {
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

// обработчик смены склада
function onNumStockSelectChangeHandler(e) {
	const numStock = Number(e.target.value)
	setScheduleData(numStock)
}

// установка данных в таблицу
function setScheduleData(numStock) {
	const numStockData = numStock
		? scheduleData.filter((item) => item.numStock === numStock)
		: scheduleData
	const mappingData = getMappingData(numStockData)
	gridOptions.api.setRowData(mappingData)
}

// обработчик смены пометки Сроки/Неделя
function onNoteChangeHandler(e) {
	const note = e.target.checked ? 'неделя' : ''
	const form = e.target.form
	changeScheduleOptions(form, note)
}

// обработчик изменения значения "Не учитывать в расчете ОРЛ"
async function onIsNotCalcCahngeHandler(params) {
	const data = params.data
	const idSchedule = data.idSchedule
	const rowNode = params.node
	await changeIsNotCalc(idSchedule, rowNode)
}


// запрос на изменение статуса графика поставки
async function changeScheduleStatus(status, counterpartyContractCode, rowNode) {
	if (!isAdmin(role) && !isOderSupport(role)) return
	const statusText = getScheduleStatus(status)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeScheduleStatusBaseUrl}${counterpartyContractCode}&${status}`)
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

// запрос на изменение значения "Не учитывать в расчете ОРЛ"
async function changeIsNotCalc(idSchedule, rowNode) {
	if (!isAdmin(role) && login !== 'romashkok%!dobronom.by') return
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeIsNotCalcBaseUrl}${idSchedule}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {

	} else {
		updateTable()
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}

// проверка наличия номера контракта в базе
async function checkContractNumber(e) {
	const input = e.target
	const formId = input.form.id
	const res = await getData(`${getScheduleNumContractBaseUrl}${input.value}`)
	const scheduleItem = res.body

	if (scheduleItem) {
		$(`#${formId} #messageNumshop`).text('Такой номер контракта уже существует')
		input.classList.add('is-invalid')
		error = true
	}
	else {
		$(`#${formId} #messageNumshop`).text('')
		input.classList.remove('is-invalid')
		error = false
	}
}

async function sendScheduleDataToMail(e) {
	const btn = e.target
	btn.disabled = true

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 0)
	const res = await getData(sendScheduleDataToMailUrl)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
	btn.disabled = false

	if (res.status === '200') {
		snackbar.show(res.message)
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}

// обработчик отправки формы загрузки таблицы эксель
function sendExcelFormHandler(e) {
	e.preventDefault()

	if (!isAdmin(role)) return

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadExcelUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			snackbar.show(res[200])
			updateTable(gridOptions)
			$(`#sendExcelModal`).modal('hide')
			hideLoadingSpinner(submitButton, 'Загрузить')
		},
		errorCallback: () => hideLoadingSpinner(submitButton, 'Загрузить')
	})
}

// обработчик отправки формы создания графика поставки
function addScheduleItemFormHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)

	if (!isOrdersAndSupplies(data)) {
		snackbar.show(`${errorMessages.isNotOrdersAndSupplies}, проверьте данные!`)
		return
	}

	if (!isSuppliesEqualToOrders(data)) {
		snackbar.show(`${errorMessages.isNotSuppliesEqualToOrders}, проверьте данные!`)
		return
	}

	if (!compareOrdersAndSupplies(data)) {
		snackbar.show(`${errorMessages.isNotCompareOrdersAndSupplies}, проверьте данные!`)
		return
	}

	if (!checkSupplyWeekIndexes(data)) {
		snackbar.show(`${errorMessages.isNotValidSupplyWeekIndexes}, проверьте данные!`)
		return
	}

	if (error) {
		snackbar.show(`${errorMessages.formError}!`)
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: addScheduleItemUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			enableButton(e.submitter)
			if (res.status === '200') {
				snackbar.show(res.message)
				updateTable(gridOptions)
				$(`#addScheduleItemModal`).modal('hide')
				return
			}

			if (res.status === '105') {
				$(`#addScheduleItemModal`).modal('hide')
				showMessageModal(res.message)
				return
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}

// обработчик отправки формы редактирования графика поставки
function editScheduleItemFormHandler(e) {
	e.preventDefault()

	if (!isAdmin(role) && !isOderSupport(role)) return

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)

	if (!isOrdersAndSupplies(data)) {
		snackbar.show(`${errorMessages.isNotOrdersAndSupplies}, проверьте данные!`)
		return
	}

	if (!isSuppliesEqualToOrders(data)) {
		snackbar.show(`${errorMessages.isNotSuppliesEqualToOrders}, проверьте данные!`)
		return
	}

	if (!compareOrdersAndSupplies(data)) {
		snackbar.show(`${errorMessages.isNotCompareOrdersAndSupplies}, проверьте данные!`)
		return
	}

	if (!checkSupplyWeekIndexes(data)) {
		snackbar.show(`${errorMessages.isNotValidSupplyWeekIndexes}, проверьте данные!`)
		return
	}

	if (error) {
		snackbar.show(`${errorMessages.formError}!`)
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: editScheduleItemUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			enableButton(e.submitter)
			if (res.status === '200') {
				snackbar.show(res.message)
				updateTable(gridOptions)
				$(`#editScheduleItemModal`).modal('hide')
				return
			}

			if (res.status === '105') {
				$(`#editScheduleItemModal`).modal('hide')
				showMessageModal(res.message)
				return
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}

// форматирование данных графика поставки для отправки на сервер
function scheduleItemDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const note = data.note ? 'неделя' : ''
	const supplies = getSupplies(data)
	const multipleOfPallet = !!data.multipleOfPallet
	const multipleOfTruck = !!data.multipleOfTruck
	const counterpartyCode = Number(data.counterpartyCode)
	const counterpartyContractCode = Number(data.counterpartyContractCode)
	const runoffCalculation = Number(data.runoffCalculation)
	const numStock = Number(data.numStock)
	let res = {
		...data,
		note,
		supplies,
		multipleOfPallet,
		multipleOfTruck,
		counterpartyCode,
		counterpartyContractCode,
		runoffCalculation,
		numStock,
	}
	if (data.idSchedule) {
		res = {
			...res,
			idSchedule: Number(data.idSchedule)
		}
	}
	return res
}

// получение количества поставок по данным графика поставок
function getSupplies(data) {
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
function getOrders(data) {
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

// заполнение формы редактирования магазина данными
function setDataToForm(scheduleItem) {
	const editScheduleItemForm = document.querySelector('#editScheduleItemForm')

	// создаем опции в селектах с установкой графика
	changeScheduleOptions(editScheduleItemForm, scheduleItem.note)

	// заполняем скрытые поля
	editScheduleItemForm.idSchedule.value = scheduleItem.idSchedule ? scheduleItem.idSchedule : ''
	editScheduleItemForm.supplies.value = scheduleItem.supplies ? scheduleItem.supplies : ''
	// editScheduleItemForm.description.value = scheduleItem.description ? scheduleItem.description : ''
	// editScheduleItemForm.dateLasCalculation.value = scheduleItem.dateLasCalculation ? scheduleItem.dateLasCalculation : ''
	// editScheduleItemForm.tz.value = scheduleItem.tz ? scheduleItem.tz : ''
	// editScheduleItemForm.tp.value = scheduleItem.tp ? scheduleItem.tp : ''
	
	// заполняем видимые поля
	editScheduleItemForm.counterpartyCode.value = scheduleItem.counterpartyCode ? scheduleItem.counterpartyCode : ''
	editScheduleItemForm.name.value = scheduleItem.name ? scheduleItem.name : ''
	editScheduleItemForm.counterpartyContractCode.value = scheduleItem.counterpartyContractCode ? scheduleItem.counterpartyContractCode : ''
	editScheduleItemForm.numStock.value = scheduleItem.numStock ? scheduleItem.numStock : ''
	editScheduleItemForm.comment.value = scheduleItem.comment ? scheduleItem.comment : ''
	editScheduleItemForm.runoffCalculation.value = scheduleItem.runoffCalculation ? scheduleItem.runoffCalculation : ''
	editScheduleItemForm.note.checked = scheduleItem.note === 'неделя'
	editScheduleItemForm.multipleOfPallet.checked = !!scheduleItem.multipleOfPallet
	editScheduleItemForm.multipleOfTruck.checked = !!scheduleItem.multipleOfTruck

	// заполняем график
	editScheduleItemForm.monday.value = scheduleItem.monday ? scheduleItem.monday : ''
	editScheduleItemForm.tuesday.value = scheduleItem.tuesday ? scheduleItem.tuesday : ''
	editScheduleItemForm.wednesday.value = scheduleItem.wednesday ? scheduleItem.wednesday : ''
	editScheduleItemForm.thursday.value = scheduleItem.thursday ? scheduleItem.thursday : ''
	editScheduleItemForm.friday.value = scheduleItem.friday ? scheduleItem.friday : ''
	editScheduleItemForm.saturday.value = scheduleItem.saturday ? scheduleItem.saturday : ''
	editScheduleItemForm.sunday.value = scheduleItem.sunday ? scheduleItem.sunday : ''
}

// создание опций складов
function createNumStockOptions(numStockSelect) {
	if (!numStockSelect) return
	stocks.forEach((stock) => {
		const option = document.createElement("option")
		option.value = stock
		option.text = `Склад ${stock}`
		numStockSelect.append(option)
	})
}

// изменение набора опций для графика
function changeScheduleOptions(form, note) {
	const scheduleSelects = form.querySelectorAll('.scheduleSelect')
	const optionData = note === 'неделя'
		? weekOptions
		: defaultOptions
	scheduleSelects.forEach(select => select.innerHTML = '')
	scheduleSelects.forEach(select => createOptions(optionData, select))
}

// создание опций
function createOptions(optionData, select) {
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

// функции управления состоянием колонок
function saveColumnState() {
	gridColumnLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreColumnState() {
	gridColumnLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// отображение модального окна с сообщением
function showMessageModal(message) {
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
function isSuppliesEqualToOrders(data) {
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
function isOrdersAndSupplies(data) {
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
function isValidSchedule(data) {
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

// проверка, что дни поставлк и заказов совпадают
function compareOrdersAndSupplies(data) {
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
function checkSupplyWeekIndexes(data) {
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

function checkSuppliesNumber(data) {
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
function checkScheduleDate(scheduleData) {
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
