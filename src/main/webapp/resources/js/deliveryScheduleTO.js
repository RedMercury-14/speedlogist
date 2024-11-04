import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import {
	changeScheduleOptions, checkScheduleDate, confirmScheduleItem, createCounterpartyDatalist,
	createOptions, dateFormatter, deleteScheduleItem, deliveryScheduleColumnDefs,
	deliveryScheduleRowClassRules, deliveryScheduleSideBar,
	editScheduleItem,
	getErrorMessage, getSupplies, getTextareaData, onNoteChangeHandler, showMessageModal, showScheduleItem,
	unconfirmScheduleItem
} from './deliveryScheduleUtils.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import {
	changeGridTableMarginTop, debounce, disableButton, enableButton,
	getData, getScheduleStatus, hideLoadingSpinner, isAdmin,
	isOderSupport, showLoadingSpinner
} from './utils.js'

const loadExcelUrl = '../../api/slots/delivery-schedule/loadTO'

const getAllScheduleUrl = '../../api/slots/delivery-schedule/getListTO'
const getScheduleByContractBaseUrl = '../../api/slots/delivery-schedule/getListTOContract/'
const getScheduleByCounterpartyBaseUrl = '../../api/slots/delivery-schedule/getListTOСounterparty/'

const addScheduleItemUrl = '../../api/slots/delivery-schedule/createTO'
const editScheduleItemUrl = '../../api/slots/delivery-schedule/editTO'
const changeIsDayToDayBaseUrl = '../../api/slots/delivery-schedule/changeDayToDay/'
const sendScheduleDataToMailUrl = '../../api/orl/sendEmail'

const PAGE_NAME = 'deliveryScheduleTO'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value
const login = document.querySelector('#login').value.toLowerCase()

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let error = false
let table
let scheduleData
let getScheduleUrl

const columnDefs = [
	...deliveryScheduleColumnDefs,
	{
		headerName: 'Примечание', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		width: 300,
	},
	{
		headerName: 'Номер TO', field: 'numStock',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 75,
	},
	{
		headerName: 'Название и адрес ТО', field: 'nameStock',
		cellClass: 'px-1 py-0 text-center',
		width: 275,
	},
	{
		headerName: 'Сегодня на сегодня', field: 'isDayToDay',
		cellClass: 'px-1 py-0 text-center font-weight-bold grid-checkbox',
		width: 75,
		editable: isAdmin(role),
		onCellValueChanged: onIsDayToDayCahngeHandler,
	},
	{
		headerName: 'График формирования заказа', field: 'orderFormationSchedule',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 75,
	},
	{
		headerName: 'График отгрузки заказа', field: 'orderShipmentSchedule',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 75,
	},
	{
		headerName: 'Холодный или Сухой', field: 'toType',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 100,
	},
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
	rowClassRules: deliveryScheduleRowClassRules,
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
	sideBar: deliveryScheduleSideBar(LOCAL_STORAGE_KEY),
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

	// форма поиска и загрузки данных графиков
	const searchDataForm = document.querySelector('#searchData')
	searchDataForm && searchDataForm.addEventListener('submit', searchDataFormHandler)

	// кнопка загрузки всех данных графиков
	const loadAllDataBtn = document.querySelector('#loadAllData')
	loadAllDataBtn && loadAllDataBtn.addEventListener('click', loadAllDataBtnClickHandler)

	// форма создания графика поставки
	const addScheduleItemForm = document.querySelector('#addScheduleItemForm')
	addScheduleItemForm && addScheduleItemForm.addEventListener('submit', addScheduleItemFormHandler)
	// форма редактирования графика поставки
	const editScheduleItemForm = document.querySelector('#editScheduleItemForm')
	editScheduleItemForm && editScheduleItemForm.addEventListener('submit', editScheduleItemFormHandler)

	// чекбоксы пометки "Неделя"
	const addNoteCheckbox = addScheduleItemForm.querySelector('#addNote')
	addNoteCheckbox && addNoteCheckbox.addEventListener('change', onNoteChangeHandler)
	const editNoteCheckbox = editScheduleItemForm.querySelector('#editNote')
	editNoteCheckbox && editNoteCheckbox.addEventListener('change', onNoteChangeHandler)

	// const sendScheduleDataToMailBtn = document.querySelector('#sendScheduleDataToMail')
	// sendScheduleDataToMailBtn && sendScheduleDataToMailBtn.addEventListener('click', sendScheduleDataToMail)

	// обновление опций графика при открытии модалки создания графика поставки
	$('#addScheduleItemModal').on('shown.bs.modal', (e) => changeScheduleOptions(addScheduleItemForm, ''))
	// очистка форм при закрытии модалки
	$('#addScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, addScheduleItemForm))
	$('#editScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, editScheduleItemForm))
	$('#sendExcelModal').on('hidden.bs.modal', (e) => clearForm(e, sendExcelForm))
})

// загрузка и установка данных графиков
async function loadScheduleData(url) {
	await getScheduleData(url)
	updateTable(gridOptions, scheduleData)

	// получение настроек таблицы из localstorage
	if (url === getAllScheduleUrl) {
		restoreColumnState()
		restoreFilterState()
	}

	// проверка, правильно ли заполнены графики
	checkScheduleDate(scheduleData)
}

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showNoRowsOverlay()
}
function updateTable(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
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
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const status = rowNode.data.status

	const confirmUnconfirmItem = status === 10 || status === 0
		? {
			name: `Подтвердить график поставки`,
			disabled: !isAdmin(role) || (status !== 10 && status !== 0),
			action: () => {
				confirmScheduleItem(role, rowNode)
			},
			icon: uiIcons.check,
		}
		: {
			name: `Снять подтверждение с графика`,
			disabled: !isAdmin(role) || status === 0,
			action: () => {
				unconfirmScheduleItem(role, rowNode)
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
			// disabled: !isAdmin(role),
			disabled: true,
			action: () => {
				editScheduleItem(rowNode, setDataToForm)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Удалить график поставки`,
			disabled: !isAdmin(role) || status === 0,
			action: () => {
				deleteScheduleItem(role, rowNode)
			},
			icon: uiIcons.trash,
		},
		"separator",
		"copy",
		"export",
	]

	return result
}

// обработчик формы загрузки данных графиков
async function searchDataFormHandler(e) {
	e.preventDefault()
	const form = e.target
	const searchValue = form.searchValue.value
	const submitButton = e.submitter
	const btnText = submitButton.textContent.trim()
	showLoadingSpinner(submitButton)
	disableButton(submitButton)
	// определяем тип данных формы поиска - номер или наименование
	 getScheduleUrl = !isNaN(searchValue)
		? `${getScheduleByContractBaseUrl}${searchValue}`
		: `${getScheduleByCounterpartyBaseUrl}${searchValue}`

	await loadScheduleData(getScheduleUrl)
	hideLoadingSpinner(submitButton, btnText)
	enableButton(submitButton)
}

// обработчик нажатия на кнопку "Загрузить все данные"
async function loadAllDataBtnClickHandler(e) {
	e.preventDefault()
	const btn = e.target
	const btnText = btn.textContent.trim()
	showLoadingSpinner(btn)
	disableButton(btn)
	getScheduleUrl = getAllScheduleUrl
	await loadScheduleData(getScheduleUrl)
	hideLoadingSpinner(btn, btnText)
	enableButton(btn)
}

// обработчик изменения значения "Не учитывать в расчете ОРЛ"
async function onIsDayToDayCahngeHandler(params) {
	const data = params.data
	const idSchedule = data.idSchedule
	const rowNode = params.node
	await changeIsDayToDay(idSchedule, rowNode)
}

// получение данных графиков поставок
async function getScheduleData(url) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 0)
	const res = await getData(url)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
	if (!res.body) {
		snackbar.show('Не удалось получить данные графика поставки')
		return []
	}
	scheduleData = res.body
	return res.body
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
		successCallback: async (res) => {
			snackbar.show(res[200])
			// получаем обновленные данные и обновляем таблицу
			getScheduleUrl = getAllScheduleUrl
			await getScheduleData(getScheduleUrl)
			updateTable(gridOptions, scheduleData)
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
	const errorMessage = getErrorMessage(data, error)
	
	if (errorMessage) {
		snackbar.show(errorMessage)
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: addScheduleItemUrl,
		token: token,
		data: data,
		successCallback: async (res) => {
			enableButton(e.submitter)
			if (res.status === '200') {
				snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				getScheduleUrl = getScheduleUrl
					? getScheduleUrl
					: `${getScheduleByContractBaseUrl}${data.counterpartyContractCode}`
				await getScheduleData(getScheduleUrl)
				updateTable(gridOptions, scheduleData)
				$(`#addScheduleItemModal`).modal('hide')
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
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

	if (!isAdmin(role)) return

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)
	const errorMessage = getErrorMessage(data, error)

	if (errorMessage) {
		snackbar.show(errorMessage)
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: editScheduleItemUrl,
		token: token,
		data: data,
		successCallback: async (res) => {
			enableButton(e.submitter)
			if (res.status === '200') {
				snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				await getScheduleData(getScheduleUrl)
				updateTable(gridOptions, scheduleData)
				$(`#editScheduleItemModal`).modal('hide')
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
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
// запрос на изменение значения "Не учитывать в расчете ОРЛ"
async function changeIsDayToDay(idSchedule, rowNode) {
	if (!isAdmin(role) && login !== 'romashkok%!dobronom.by') return
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeIsDayToDayBaseUrl}${idSchedule}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {

	} else {
		// возвращаем предыдущие данные
		updateTable(gridOptions, scheduleData)
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
// отправка данных на почту
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


// форматирование данных графика поставки для отправки на сервер
function scheduleItemDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const note = data.note ? 'неделя' : ''
	const supplies = getSupplies(data)
	const counterpartyCode = Number(data.counterpartyCode)
	const counterpartyContractCode = Number(data.counterpartyContractCode)
	const numStock = getTextareaData(data.numStock)

	let res = {
		...data,
		note,
		supplies,
		counterpartyCode,
		counterpartyContractCode,
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

// заполнение формы редактирования магазина данными
function setDataToForm(scheduleItem) {
	const editScheduleItemForm = document.querySelector('#editScheduleItemForm')

	// создаем опции в селектах с установкой графика
	changeScheduleOptions(editScheduleItemForm, scheduleItem.note)

	// заполняем скрытые поля
	editScheduleItemForm.idSchedule.value = scheduleItem.idSchedule ? scheduleItem.idSchedule : ''
	editScheduleItemForm.supplies.value = scheduleItem.supplies ? scheduleItem.supplies : ''
	editScheduleItemForm.type.value = scheduleItem.type ? scheduleItem.type : ''

	// заполняем видимые поля
	editScheduleItemForm.toType.value = scheduleItem.toType ? scheduleItem.toType : ''
	editScheduleItemForm.counterpartyCode.value = scheduleItem.counterpartyCode ? scheduleItem.counterpartyCode : ''
	editScheduleItemForm.name.value = scheduleItem.name ? scheduleItem.name : ''
	editScheduleItemForm.counterpartyContractCode.value = scheduleItem.counterpartyContractCode ? scheduleItem.counterpartyContractCode : ''
	editScheduleItemForm.numStock.value = scheduleItem.numStock ? scheduleItem.numStock : ''
	editScheduleItemForm.comment.value = scheduleItem.comment ? scheduleItem.comment : ''
	editScheduleItemForm.note.checked = scheduleItem.note === 'неделя'
	editScheduleItemForm.orderFormationSchedule.value = scheduleItem.orderFormationSchedule ? scheduleItem.orderFormationSchedule : ''
	editScheduleItemForm.orderShipmentSchedule.value = scheduleItem.orderShipmentSchedule ? scheduleItem.orderShipmentSchedule : ''

	// заполняем график
	editScheduleItemForm.monday.value = scheduleItem.monday ? scheduleItem.monday : ''
	editScheduleItemForm.tuesday.value = scheduleItem.tuesday ? scheduleItem.tuesday : ''
	editScheduleItemForm.wednesday.value = scheduleItem.wednesday ? scheduleItem.wednesday : ''
	editScheduleItemForm.thursday.value = scheduleItem.thursday ? scheduleItem.thursday : ''
	editScheduleItemForm.friday.value = scheduleItem.friday ? scheduleItem.friday : ''
	editScheduleItemForm.saturday.value = scheduleItem.saturday ? scheduleItem.saturday : ''
	editScheduleItemForm.sunday.value = scheduleItem.sunday ? scheduleItem.sunday : ''
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
