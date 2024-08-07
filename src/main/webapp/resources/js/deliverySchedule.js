import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { changeGridTableMarginTop, debounce, getData, hideLoadingSpinner, showLoadingSpinner } from './utils.js'

const loadExcelUrl = '../../api/slots/delivery-schedule/load'
const getScheduleUrl = '../../api/slots/delivery-schedule/getList'
const addScheduleItemUrl = '../../api/slots/delivery-schedule/create'
const editScheduleItemUrl = '../../api/slots/delivery-schedule/edit'
const getScheduleNumContractBaseUrl = '../../api/slots/delivery-schedule/getScheduleNumContract/'


const PAGE_NAME = 'deliverySchedule'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

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
		headerName: 'Пометка "Сроки/Неделя"', field: 'note',
		cellClass: 'px-1 py-0 text-center',
		cellClassRules: {
			'blue-cell': params => params.value === 'неделя',
		},
		width: 125,
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
		cellClass: 'px-1 py-0 text-center blue-cell',
		width: 100,
	},
	{
		headerName: 'Примечание', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		width: 300,
	},
	{
		headerName: 'Кратно поддону', field: 'multipleOfPallet',
		cellClass: 'px-1 py-0 text-center grid-checkbox',
		width: 75,
	},
	{
		headerName: 'Кратно машине', field: 'multipleOfTruck',
		cellClass: 'px-1 py-0 text-center grid-checkbox',
		width: 75,
	},
	// {
	// 	headerName: 'Номер склада', field: 'numStock',
	// 	cellClass: 'px-1 py-0 text-center font-weight-bold',
	// 	width: 75,
	// },
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

const gridOptions = {
	columnDefs: columnDefs,
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

window.onload = async () => {
	const gridDiv = document.querySelector('#myGrid')

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
	// const numStockSelect = document.querySelector("#numStockSelect")
	// createNumStockOptions(numStockSelect)
	// numStockSelect && numStockSelect.addEventListener('change', onNumStockSelectChangeHandler)

	// выпадающие списки выбора пометки "Неделя/Сроки"
	const noteSelectInAddForm = addScheduleItemForm.querySelector('#note')
	noteSelectInAddForm && noteSelectInAddForm.addEventListener('change', onNoteSelectChangeHandler)
	const noteSelectInEditForm = editScheduleItemForm.querySelector('#note')
	noteSelectInEditForm && noteSelectInEditForm.addEventListener('change', onNoteSelectChangeHandler)

	// получение данных графика
	const res = await getData(getScheduleUrl)
	scheduleData = res.body

	// изменение отступа для таблицы
	changeGridTableMarginTop()
	// создание таблицы
	renderTable(gridDiv, gridOptions, scheduleData)
	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	// проверка номера контракта
	$('.counterpartyContractCode').change(checkContractNumber)
	// обновление опций графика при открытии модалки создания графика поставки
	$('#addScheduleItemModal').on('shown.bs.modal', (e) => changeScheduleOptions(addScheduleItemForm, ''))
	// очистка форм при закрытии модалки
	$('#addScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, addScheduleItemForm))
	$('#editScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, editScheduleItemForm))
}

function renderTable(gridDiv, gridOptions, data) {
	table = new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
async function updateTable() {
	const res = await getData(getScheduleUrl)
	scheduleData = res.body

	if (!scheduleData || !scheduleData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(scheduleData)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(getMappingScheduleItem)
}
function getMappingScheduleItem(scheduleItem) {
	return {
		...scheduleItem,
		name: scheduleItem.name.trim()
	}
}
function getContextMenuItems(params) {
	const scheduleItem = params.node.data
	const result = [
		{
			name: `Редактировать график поставки`,
			action: () => {
				editScheduleItem(scheduleItem)
			},
			icon: uiIcons.pencil,
		},
		"separator",
		"copy",
		"export",
	]

	return result
}

// редактирование графика поставки
function editScheduleItem(scheduleItem) {
	setDataToForm(scheduleItem)
	$(`#editScheduleItemModal`).modal('show')
}

// обработчик смены склада
function onNumStockSelectChangeHandler(e) {
	const numStock = e.target.value
	const numStockData = scheduleData.filter((item) => item.numStock === numStock)
	updateTable(numStockData)
}

// обработчик смены пометки Сроки/Неделя
function onNoteSelectChangeHandler(e) {
	const note = e.target.value
	const form = e.target.form
	changeScheduleOptions(form, note)
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

// обработчик отправки формы загрузки таблицы эксель
function sendExcelFormHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadExcelUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			snackbar.show(res[200])
			updateTable()
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

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	ajaxUtils.postJSONdata({
		url: addScheduleItemUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show(res.message)
				updateTable()
				$(`#addScheduleItemModal`).modal('hide')
			}

			if (res.status === '105') {
				$(`#addScheduleItemModal`).modal('hide')
				showMessageModal(res.message)
				return
			}
		}
	})
}

// обработчик отправки формы редактирования графика поставки
function editScheduleItemFormHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	ajaxUtils.postJSONdata({
		url: editScheduleItemUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show(res.message)
				updateTable()
				$(`#editScheduleItemModal`).modal('hide')
				return
			}

			if (res.status === '105') {
				$(`#editScheduleItemModal`).modal('hide')
				showMessageModal(res.message)
				return
			}
			
		}
	})
}

// форматирование данных графика поставки для отправки на сервер
function scheduleItemDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const supplies = getSupplies(data)
	const multipleOfPallet = !!data.multipleOfPallet
	const multipleOfTruck = !!data.multipleOfTruck
	const counterpartyCode = Number(data.counterpartyCode)
	const counterpartyContractCode = Number(data.counterpartyContractCode)
	const runoffCalculation = Number(data.runoffCalculation)
	let res = {
		...data,
		supplies,
		multipleOfPallet,
		multipleOfTruck,
		counterpartyCode,
		counterpartyContractCode,
		runoffCalculation,
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
	const reg = /^з$|з\//
	const schedule = [
		data.monday,
		data.tuesday,
		data.wednesday,
		data.thursday,
		data.friday,
		data.saturday,
		data.sunday,
	]
	return schedule.filter(el => reg.test(el)).length
}

// заполнение формы редактирования магазина данными
function setDataToForm(scheduleItem) {
	const editScheduleItemForm = document.querySelector('#editScheduleItemForm')

	// создаем опции в селектах с установкой графика
	changeScheduleOptions(editScheduleItemForm, scheduleItem.note)

	// заполняем скрытые поля
	editScheduleItemForm.idSchedule.value = scheduleItem.idSchedule ? scheduleItem.idSchedule : ''
	editScheduleItemForm.supplies.value = scheduleItem.supplies ? scheduleItem.supplies : ''
	// editScheduleItemForm.numStock.value = scheduleItem.numStock ? scheduleItem.numStock : ''
	// editScheduleItemForm.description.value = scheduleItem.description ? scheduleItem.description : ''
	// editScheduleItemForm.dateLasCalculation.value = scheduleItem.dateLasCalculation ? scheduleItem.dateLasCalculation : ''
	// editScheduleItemForm.tz.value = scheduleItem.tz ? scheduleItem.tz : ''
	// editScheduleItemForm.tp.value = scheduleItem.tp ? scheduleItem.tp : ''

	// заполняем видимые поля
	editScheduleItemForm.counterpartyCode.value = scheduleItem.counterpartyCode ? scheduleItem.counterpartyCode : ''
	editScheduleItemForm.name.value = scheduleItem.name ? scheduleItem.name : ''
	editScheduleItemForm.counterpartyContractCode.value = scheduleItem.counterpartyContractCode ? scheduleItem.counterpartyContractCode : ''
	editScheduleItemForm.comment.value = scheduleItem.comment ? scheduleItem.comment : ''
	editScheduleItemForm.runoffCalculation.value = scheduleItem.runoffCalculation ? scheduleItem.runoffCalculation : ''
	editScheduleItemForm.note.value = scheduleItem.note ? scheduleItem.note : ''
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
