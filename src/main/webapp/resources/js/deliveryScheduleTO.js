import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { showScheduleItem } from './deliverySchedule/showScheduleItem.js'
import {
	changeScheduleOptions, confirmScheduleItem,
	createCounterpartyDatalist, createOptions,
	deleteScheduleItem, deliveryScheduleColumnDefs,
	deliveryScheduleColumnDefsForAdmin,
	deliveryScheduleRowClassRules, deliveryScheduleSideBar,
	editScheduleItem, getSupplies, getTextareaData, onNoteChangeHandler,
	showMessageModal, unconfirmScheduleItem
} from './deliverySchedule/utils.js'
import { checkScheduleData, getFormErrorMessage, isValidScheduleValues } from './deliverySchedule/validation.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import {
	changeGridTableMarginTop, debounce, disableButton, enableButton,
	getData, getScheduleStatus, hideLoadingSpinner, isAdmin,
	isORL, showLoadingSpinner
} from './utils.js'

const loadExcelUrl = '../../api/slots/delivery-schedule/loadTO'

const getAllScheduleUrl = '../../api/slots/delivery-schedule/getListTO'
const getScheduleByContractBaseUrl = '../../api/slots/delivery-schedule/getListTOContract/'
const getScheduleByCounterpartyBaseUrl = '../../api/slots/delivery-schedule/getListTOСounterparty/'

const addScheduleItemUrl = '../../api/slots/delivery-schedule/createTO'
const editScheduleItemUrl = '../../api/slots/delivery-schedule/editTOByCounterpartyAndShop'
const changeIsDayToDayBaseUrl = '../../api/slots/delivery-schedule/changeDayToDay/'

const editTOByCounterpartyContractCodeOnlyUrl = '../../api/slots/delivery-schedule/editTOByCounterpartyContractCodeOnly'
const setCodeNameBaseUrl = '../../api/slots/delivery-schedule/changeNameOfQuantum/'

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
let counterpartyList

const columnDefs = [
	...deliveryScheduleColumnDefs,
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
		editable: isAdmin(role) || isORL(role),
		onCellValueChanged: onIsDayToDayChangeHandler,
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
	{
		headerName: 'Кодовое имя контрагента', field: 'codeNameOfQuantumCounterparty',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 150,
	},
	{
		headerName: 'Квант', field: 'quantum',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 100,
	},
	{
		headerName: 'Единицы измерения', field: 'quantumMeasurements',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 100,
	},
	{
		headerName: 'Примечание', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		width: 300,
	},
]

// доболнительные колонки для админа
if (isAdmin(role)) {
	columnDefs.push(
		...deliveryScheduleColumnDefsForAdmin
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

	// форма установки кодовых слов
	const setCodeNameForm = document.querySelector('#setCodeNameForm')
	setCodeNameForm && setCodeNameForm.addEventListener('submit', setCodeNameFormHandler)

	// чекбоксы пометки "Неделя"
	const addNoteCheckbox = addScheduleItemForm.querySelector('#addNote')
	addNoteCheckbox && addNoteCheckbox.addEventListener('change', onNoteChangeHandler)
	const editNoteCheckbox = editScheduleItemForm.querySelector('#editNote')
	editNoteCheckbox && editNoteCheckbox.addEventListener('change', onNoteChangeHandler)

	// настройка автозаполнения полей контрагента
	const contractCodeList = addScheduleItemForm.querySelector('#contractCodeList')
	const addCounterpartyCodeInput = addScheduleItemForm.querySelector('#counterpartyCode')
	const addCounterpartyNameInput = addScheduleItemForm.querySelector('#name')
	const codeNameFormCounterpartyCodeInput = setCodeNameForm.querySelector('#counterpartyCode')
	const codeNameFormCounterpartyNameInput = setCodeNameForm.querySelector('#name')
	addCounterpartyCodeInput.addEventListener('change', (e) => {
		autocompleteCounterpartyInfo(e, addCounterpartyNameInput, contractCodeList)
	})
	addCounterpartyNameInput.addEventListener('change', (e) => {
		autocompleteCounterpartyInfo(e, addCounterpartyCodeInput, null)
	})
	codeNameFormCounterpartyCodeInput.addEventListener('change', (e) => {
		autocompleteCounterpartyInfo(e, codeNameFormCounterpartyNameInput, null)
	})

	// переключение видимости поля "Ед. измерения" в зависимости от значения поля "Квант"
	const addQuantumInput = addScheduleItemForm.querySelector('#quantum')
	const editQuantumInput = editScheduleItemForm.querySelector('#quantum')
	addQuantumInput.addEventListener('change', (e) => onQuantumChangeHandler(e, addScheduleItemForm))
	editQuantumInput.addEventListener('change', (e) => onQuantumChangeHandler(e, editScheduleItemForm))

	// обновление опций графика при открытии модалки создания графика поставки
	$('#addScheduleItemModal').on('shown.bs.modal', (e) => changeScheduleOptions(addScheduleItemForm, ''))
	// очистка форм при закрытии модалки
	$('#addScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, addScheduleItemForm))
	$('#editScheduleItemModal').on('hidden.bs.modal', (e) => clearForm(e, editScheduleItemForm))
	$('#sendExcelModal').on('hidden.bs.modal', (e) => clearForm(e, sendExcelForm))
	$('#setCodeNameModal').on('hidden.bs.modal', (e) => clearForm(e, setCodeNameForm))

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
	const counterpartyData = window.initData.counterparty
	window.initData = null

	counterpartyList = getCounterpartyList(
		counterpartyData.map(el => ({ ...el, counterpartyContractCode: el.counterpartyContractCodeUnic })
	))

	// создание списков названий и кодов контрагентов
	createCounterpartyDatalist(counterpartyList)
}

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
	if (isAdmin(role) || isORL(role)) {
		checkScheduleData(scheduleData)
	}

	// const counterpartyList = getCounterpartyList(scheduleData)
	// console.log("🚀 ~ loadScheduleData ~ counterpartyList:", counterpartyList)
}

function getCounterpartyList(scheduleData) {
	return scheduleData.reduce((acc, scheduleItem) => {
		const { name, counterpartyCode, counterpartyContractCode } = scheduleItem
		const counterparty = acc.find((item) => item.counterpartyCode === counterpartyCode)

		if (!counterparty) {
			acc.push({ name, counterpartyCode, contractCodes: [ counterpartyContractCode ] })
		} else {
			if (!counterparty.contractCodes.find((item) => item === counterpartyContractCode)) {
				counterparty.contractCodes.push(counterpartyContractCode)
			}
		}
		return acc
	}, [])
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

	const confirmUnconfirmItems = status === 10 || status === 0
		? [
			{
				name: `Подтверждение графиков`,
				disabled: (!isAdmin(role) && !isORL(role)) || (status !== 10 && status !== 0),
				icon: uiIcons.check,
				subMenu: [
					{
						name: `Подтвердить текущий график`,
						action: () => confirmScheduleItem(role, rowNode),
					},
					{
						name: `Подтвердить графики по текущему коду контракта (с указанием кодового слова)`,
						action: () => confirmScheduleItemsByContract(role, rowNode),
					}
				]
			}
		]
		: [
			{
				name: 'Снять подтверждение',
				disabled: (!isAdmin(role) && !isORL(role)) || status === 0,
				icon: uiIcons.x_lg,
				subMenu: [
					{
						name: `Снять подтверждение с текущего графика`,
						action: () => unconfirmScheduleItem(role, rowNode),
					},
					{
						name: `Снять подтверждение с графиков по текущему коду контракта`,
						action: () => unconfirmScheduleItemsByContract(role, rowNode),
					}
				]
			}
		]

	const result = [
		{
			name: `Показать текущий график`,
			action: () => {
				showScheduleItem(rowNode)
			},
			icon: uiIcons.table,
		},
		"separator",
		...confirmUnconfirmItems,
		{
			name: `Добавить новые ТО по текущему коду контракта (копирование графика)`,
			disabled: status !== 20 && status !== 10,
			action: () => {
				addShopsByContract(rowNode)
			},
			icon: uiIcons.plusLg,
		},
		{
			name: `Редактировать графики по текущему коду контракта`,
			disabled: !isAdmin(role) && !isORL(role),
			action: () => {
				editScheduleItem(rowNode, setDataToForm)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Изменить значение "Сегодня на сегодня" по текущему коду контракта`,
			disabled: !isAdmin(role) && !isORL(role),
			action: () => {
				changeIsDayToDayByContract(role, rowNode)
			},
			icon: uiIcons.card_checklist,
		},
		{
			name: `Удаление графиков`,
			disabled: (!isAdmin(role) && !isORL(role)),
			icon: uiIcons.trash,
			subMenu: [
				{
					name: `Удалить текущий график`,
					disabled: (!isAdmin(role) && !isORL(role)) || status === 0,
					action: () => {
						deleteScheduleItem(role, rowNode)
					},
				},
				{
					name: `Удалить все графики по текущему коду контракта`,
					disabled: !isAdmin(role) && !isORL(role),
					action: () => {
						deleteScheduleItemsByContract(role, rowNode)
					},
				},
			]
		},
		"separator",
		"copy",
		"export",
	]

	return result
}

// подтверждение графиков поставок по номеру контракта
async function confirmScheduleItemsByContract(role, rowNode) {
	if (!isAdmin(role) && !isORL(role)) return
	const status = 20
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return

	const payload = {
		counterpartyContractCode,
		status
	}

	// проверка на отсутствие кодового слова хотя бы у одного графика
	const schedulesByContract = scheduleData.filter(item => item.counterpartyContractCode === counterpartyContractCode)
	const isMissingCodeName = schedulesByContract.some(item => !item.codeNameOfQuantumCounterparty)
	if (isMissingCodeName) {
		const counterpartyName = scheduleItem.name
		const codeNameOfQuantumCounterparty = prompt(`Введите кодовое слово для контрагента ${counterpartyName} по контракту ${counterpartyContractCode}:`)
		if (!codeNameOfQuantumCounterparty) {
			snackbar.show('Кодовое слово не указано, графика не подтверждены!')
			return
		}
		payload.codeNameOfQuantumCounterparty = codeNameOfQuantumCounterparty
	}

	editTOByCounterpartyContractCodeOnly(payload)
}
// снятие подтверждения с графиков поставок по номеру контракта
async function unconfirmScheduleItemsByContract(role, rowNode) {
	if (!isAdmin(role) && !isORL(role)) return
	const status = 10
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return
	editTOByCounterpartyContractCodeOnly({
		counterpartyContractCode,
		status
	})
}
// удаление графиков поставок по номеру контракта
async function deleteScheduleItemsByContract(role, rowNode) {
	if (!isAdmin(role) && !isORL(role)) return
	const status = 0
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return
	if (!confirm(
		`Вы действительно хотите удалить ВСЕ графики по коду контракта ${counterpartyContractCode}?`
	)) return
	editTOByCounterpartyContractCodeOnly({
		counterpartyContractCode,
		status
	})
}
// запрос на массовое изменение значения "Сегодня на сегодня" по номеру контракта
function changeIsDayToDayByContract(role, rowNode) {
	if (!isAdmin(role) && !isORL(role)) return
	const scheduleItem = rowNode.data
	const isDayToDay = !scheduleItem.isDayToDay
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return
	if (!confirm(
		`Вы действительно хотите изменить значение "Сегодня на сегодня" для всех графиков по коду контракта ${counterpartyContractCode}?`
	)) return
	editTOByCounterpartyContractCodeOnly({
		counterpartyContractCode,
		isDayToDay
	})
}

// обработчик изменения поля "Квант"
function onQuantumChangeHandler(e, form) {
	const value = e.target.value
	quantumMeasurementsVisibleToggler(form, value)
}

// переключение видимости поля "Единицы измерения"
function quantumMeasurementsVisibleToggler(form, value) {
	const quantumMeasurementsContainer = form.querySelector('.quantumMeasurements-container')
	if (value) {
		form.quantumMeasurements.required = true
		quantumMeasurementsContainer.classList.remove('invisible')
	} else {
		form.quantumMeasurements.required = false
		form.quantumMeasurements.value = ''
		quantumMeasurementsContainer.classList.add('invisible')
	}
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

// обработчик изменения значения "Сегодня на сегодня"
async function onIsDayToDayChangeHandler(params) {
	if (!isAdmin(role) && !isORL(role)) return
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
	if (!res || !res.body) {
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

	// проверка значений графика
	if (!isValidScheduleValues(data)) {
		snackbar.show(
			'Обнаружены ошибки в значения дней заказа или поставки.\n'
			+ 'Проверьте данные графика!'
		)
		return
	}

	// ошибки в логике графика
	const errorMessage = getFormErrorMessage(data, error)
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
				$(`#addScheduleItemModal`).modal('hide')
				snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				getScheduleUrl = getScheduleUrl
					? getScheduleUrl
					: `${getScheduleByContractBaseUrl}${data.counterpartyContractCode}`
				await getScheduleData(getScheduleUrl)
				updateTable(gridOptions, scheduleData)
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

	if (!isAdmin(role) && !isORL(role)) return

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)

	// проверка значений графика
	if (!isValidScheduleValues(data)) {
		snackbar.show(
			'Обнаружены ошибки в значения дней заказа или поставки.\n'
			+ 'Проверьте данные графика!'
		)
		return
	}

	// ошибки в логике графика
	const errorMessage = getFormErrorMessage(data, error)
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
				$(`#editScheduleItemModal`).modal('hide')
				snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				await getScheduleData(getScheduleUrl)
				updateTable(gridOptions, scheduleData)
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
// обработчик формы установки кодового слова
function setCodeNameFormHandler(e) {
	e.preventDefault()

	if (!isAdmin(role) && !isORL(role)) return

	const formData = new FormData(e.target)
	const counterpartyCode = formData.get('counterpartyCode')
	let codeNameOfQuantumCounterparty = formData.get('codeNameOfQuantumCounterparty')
	if (!codeNameOfQuantumCounterparty) codeNameOfQuantumCounterparty = 'null'

	disableButton(e.submitter)
	
	ajaxUtils.get({
		url: `${setCodeNameBaseUrl}${counterpartyCode}&${codeNameOfQuantumCounterparty}`,
		successCallback: async (res) => {
			enableButton(e.submitter)
			if (res.status === '200') {
				$(`#setCodeNameModal`).modal('hide')
				const message = res.message ? res.message : 'Кодовое имя установлено'
				snackbar.show(message)
				// получаем обновленные данные и обновляем таблицу
				if (getScheduleUrl) {
					await getScheduleData(getScheduleUrl)
					updateTable(gridOptions, scheduleData)
				}
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
			if (res.status === '105') {
				$(`#setCodeNameModal`).modal('hide')
				showMessageModal(res.message)
				return
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}
// запрос на изменение значения "Сегодня на сегодня"
async function changeIsDayToDay(idSchedule, rowNode) {
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
// метод редактирования графиков по коду контракта (изменяет только указанные поля)
function editTOByCounterpartyContractCodeOnly(data) {
	ajaxUtils.postJSONdata({
		url: editTOByCounterpartyContractCodeOnlyUrl,
		token: token,
		data: data,
		successCallback: async (res) => {
			if (res && res.status === '200') {
				res.message && snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				await getScheduleData(getScheduleUrl)
				updateTable(gridOptions, scheduleData)
			} else {
				console.log(res)
				const message = res && res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
			}
		},
		errorCallback: () => {
		}
	})
}
// добавление нового ТО по номеру
async function addShopsByContract(rowNode) {
	const scheduleItem = rowNode.data
	const counterpartyContractCode = scheduleItem.counterpartyContractCode
	if (!counterpartyContractCode) return

	// номера магазинов от 2 до 5 цифр через пробел
	const regex = /^(?!.*\b0\d{1,4}\b)\b[1-9]\d{1,4}\b(?:\s+\b[1-9]\d{1,4}\b)*$/
	const value = prompt(
		`Введите номер нового ТО по коду контракта ${counterpartyContractCode}. `
		+ `Для указания нескольких ТО введите их номера через ПРОБЕЛ:`
	)

	if (!value && !regex.test(value)) {
		snackbar.show('Некорректный ввод. Введите корректные номера ТО через пробел.')
		return
	}

	const newShops = value.split(' ').map(Number)
	const numStocks = getNumStocksByContract(scheduleItem.counterpartyContractCode)
	const existShops = newShops.reduce((acc, shop) => {
		if (numStocks.includes(shop)) {
			acc.push(shop)
		}
		return acc
	}, [])

	if (existShops.length !== 0) {
		alert(
			`Новые ТО не добавлены!`
			+ `\nТО с номерами ${existShops.join(', ')} и кодом контракта ${counterpartyContractCode} уже существуют.`
			+ `\nПроверьте введенные данные!`
		)
		return
	}

	const data = {
		supplies: scheduleItem.supplies,
		type: scheduleItem.type,
		toType: scheduleItem.toType,
		counterpartyCode: scheduleItem.counterpartyCode,
		name: scheduleItem.name,
		counterpartyContractCode,
		numStock: newShops,
		comment: scheduleItem.comment ? scheduleItem.comment : null,
		note: scheduleItem.note ? scheduleItem.note : null,
		monday: scheduleItem.monday ? scheduleItem.monday : null,
		tuesday: scheduleItem.tuesday ? scheduleItem.tuesday : null,
		wednesday: scheduleItem.wednesday ? scheduleItem.wednesday : null,
		thursday: scheduleItem.thursday ? scheduleItem.thursday : null,
		friday: scheduleItem.friday ? scheduleItem.friday : null,
		saturday: scheduleItem.saturday ? scheduleItem.saturday : null,
		sunday: scheduleItem.sunday ? scheduleItem.sunday : null,
		orderFormationSchedule: scheduleItem.orderFormationSchedule ? scheduleItem.orderFormationSchedule : null,
		orderShipmentSchedule: scheduleItem.orderShipmentSchedule ? scheduleItem.orderShipmentSchedule : null,
		isDayToDay: scheduleItem.isDayToDay ? scheduleItem.isDayToDay : null,
		quantum: scheduleItem.quantum ? scheduleItem.quantum : null,
		quantumMeasurements: scheduleItem.quantumMeasurements ? scheduleItem.quantumMeasurements : null,
		codeNameOfQuantumCounterparty: scheduleItem.codeNameOfQuantumCounterparty ? scheduleItem.codeNameOfQuantumCounterparty : null,
		status: scheduleItem.status ? scheduleItem.status : 10,
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: addScheduleItemUrl,
		token: token,
		data: data,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			if (res.status === '200') {
				snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				getScheduleUrl = getScheduleUrl
					? getScheduleUrl
					: `${getScheduleByContractBaseUrl}${data.counterpartyContractCode}`
				await getScheduleData(getScheduleUrl)
				updateTable(gridOptions, scheduleData)
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
			if (res.status === '105') {
				showMessageModal(res.message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}


// форматирование данных графика поставки для отправки на сервер
function scheduleItemDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const note = data.note ? 'неделя' : null
	const supplies = getSupplies(data)
	const counterpartyCode = Number(data.counterpartyCode)
	const counterpartyContractCode = Number(data.counterpartyContractCode)
	const numStock = getTextareaData(data.numStock)
	const orderFormationSchedule = data.orderFormationSchedule && note ? data.orderFormationSchedule : null
	const orderShipmentSchedule = data.orderShipmentSchedule && note ? data.orderShipmentSchedule : null
	const quantum = data.quantum ? Number(data.quantum) : null

	let res = {
		...data,
		monday: data.monday ? data.monday : null,
		tuesday: data.tuesday ? data.tuesday : null,
		wednesday: data.wednesday ? data.wednesday : null,
		thursday: data.thursday ? data.thursday : null,
		friday: data.friday ? data.friday : null,
		saturday: data.saturday ? data.saturday : null,
		sunday: data.sunday ? data.sunday : null,
		comment: data.comment ? data.comment : null,
		note,
		supplies,
		counterpartyCode,
		counterpartyContractCode,
		numStock,
		orderFormationSchedule,
		orderShipmentSchedule,
		quantum,
		codeNameOfQuantumCounterparty,
		status: 10,
		codeNameOfQuantumCounterparty: null,
		isDayToDay: null,
	}
	return res
}

// заполнение формы редактирования магазина данными
function setDataToForm(scheduleItem) {
	const editScheduleItemForm = document.querySelector('#editScheduleItemForm')
	const quantumMeasurements = scheduleItem.quantumMeasurements ? scheduleItem.quantumMeasurements : ''

	// создаем опции в селектах с установкой графика
	changeScheduleOptions(editScheduleItemForm, scheduleItem.note)
	// отображение поля "Единицы измерения"
	quantumMeasurementsVisibleToggler(editScheduleItemForm, quantumMeasurements)

	// заполняем скрытые поля
	editScheduleItemForm.supplies.value = scheduleItem.supplies ? scheduleItem.supplies : ''
	editScheduleItemForm.type.value = scheduleItem.type ? scheduleItem.type : ''

	// заполняем видимые поля
	editScheduleItemForm.toType.value = scheduleItem.toType ? scheduleItem.toType : ''
	editScheduleItemForm.counterpartyCode.value = scheduleItem.counterpartyCode ? scheduleItem.counterpartyCode : ''
	editScheduleItemForm.name.value = scheduleItem.name ? scheduleItem.name : ''
	editScheduleItemForm.counterpartyContractCode.value = scheduleItem.counterpartyContractCode ? scheduleItem.counterpartyContractCode : ''

	editScheduleItemForm.numStock.value = ''

	editScheduleItemForm.comment.value = scheduleItem.comment ? scheduleItem.comment : ''
	editScheduleItemForm.note.checked = scheduleItem.note === 'неделя'
	editScheduleItemForm.orderFormationSchedule.value = scheduleItem.orderFormationSchedule ? scheduleItem.orderFormationSchedule : ''
	editScheduleItemForm.orderShipmentSchedule.value = scheduleItem.orderShipmentSchedule ? scheduleItem.orderShipmentSchedule : ''
	editScheduleItemForm.quantum.value = scheduleItem.quantum ? scheduleItem.quantum : ''
	editScheduleItemForm.quantumMeasurements.value = quantumMeasurements

	// заполняем график
	editScheduleItemForm.monday.value = scheduleItem.monday ? scheduleItem.monday : ''
	editScheduleItemForm.tuesday.value = scheduleItem.tuesday ? scheduleItem.tuesday : ''
	editScheduleItemForm.wednesday.value = scheduleItem.wednesday ? scheduleItem.wednesday : ''
	editScheduleItemForm.thursday.value = scheduleItem.thursday ? scheduleItem.thursday : ''
	editScheduleItemForm.friday.value = scheduleItem.friday ? scheduleItem.friday : ''
	editScheduleItemForm.saturday.value = scheduleItem.saturday ? scheduleItem.saturday : ''
	editScheduleItemForm.sunday.value = scheduleItem.sunday ? scheduleItem.sunday : ''
}

// получение массива ТО по номеру контракта
function getNumStocksByContract(counterpartyContractCode) {
	return scheduleData
		.filter(item => item.counterpartyContractCode === counterpartyContractCode)
		.map(item => item.numStock)
}

// функция автозаполнения полей с инфой о контрагенте при изменении значения этих полей
function autocompleteCounterpartyInfo(e, autocompleteInput, contractCodeList) {
	const targetId = e.target.id
	const isCounterpartyCode = targetId === 'counterpartyCode'
	const searchValue = isCounterpartyCode ? +e.target.value : e.target.value
	const comparisonField = isCounterpartyCode ? 'counterpartyCode' : 'name'
	const fieldToFill = isCounterpartyCode ? 'name' : 'counterpartyCode'
	const counterparty = counterpartyList.find((item) => item[comparisonField] === searchValue)
	if (counterparty) {
		autocompleteInput.value = counterparty[fieldToFill]

		// информация о контрактах контрагента
		if (contractCodeList) {
			contractCodeList.innerHTML = ''
			createOptions(counterparty.contractCodes, contractCodeList)
		}
	}
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