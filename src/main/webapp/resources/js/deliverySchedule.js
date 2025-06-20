import { getDefaultGridOptions, restoreColumnState, restoreFilterState, supressInputInLargeTextEditor } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { showScheduleItem } from './deliverySchedule/showScheduleItem.js'
import {
	changeScheduleOptions, confirmScheduleItem, createCounterpartyDatalist,
	createOptions, deleteScheduleItem, deliveryScheduleColumnDefs,
	deliveryScheduleColumnDefsForAdmin,
	deliveryScheduleRowClassRules, deliveryScheduleSideBar,
	editScheduleItem,getSupplies, onNoteChangeHandler, showMessageModal,
	unconfirmScheduleItem
} from './deliverySchedule/utils.js'
import { checkScheduleData, isValidScheduleValues, getFormErrorMessage, checkScheduleOrderRating } from './deliverySchedule/validation.js'
import {
	addScheduleRCItemUrl,
	changeIsImportBaseUrl,
	changeIsNotCalcBaseUrl,
	downloadReportBaseUrl,
	editScheduleRCItemUrl,
	getCountScheduleDeliveryHasWeekUrl,
	getCountScheduleOrderHasWeekUrl,
	getScheduleNumContractBaseUrl,
	getScheduleRCUrl,
	loadRCExcelUrl,
	sendScheduleRCDataToMailUrl
} from './globalConstants/urls.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import {
	blurActiveElem, disableButton, enableButton,
	getData, getScheduleStatus, hideLoadingSpinner, isAdmin,
	isOrderSupport, showLoadingSpinner
} from './utils.js'

const PAGE_NAME = 'deliverySchedule'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value
const login = document.querySelector('#login').value.toLowerCase()


let error = false
let table
let scheduleData
const stocks = ['1700', '1800', '1250', '1200']

const columnDefs = [
	...deliveryScheduleColumnDefs,
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
		headerName: 'Кол-во паллет в машине', field: 'machineMultiplicity',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
	{
		headerName: 'Связь поставок', field: 'connectionSupply',
		cellClass: 'px-1 py-0 text-center',
		width: 100,
	},
	{
		headerName: 'Номер склада', field: 'numStock',
		cellClass: 'px-1 py-0 text-center font-weight-bold',
		width: 75,
	},
	{
		headerName: 'Не учитывать в расчете ОРЛ', field: 'isNotCalc',
		cellClass: 'px-1 py-0 text-center font-weight-bold grid-checkbox',
		width: 100,
		editable: isAdmin(role) || login === 'romashkok%!dobronom.by',
		onCellValueChanged: onIsNotCalcCahngeHandler,
	},
	{
		headerName: 'Импорт', field: 'isImport',
		cellClass: 'px-1 py-0 text-center font-weight-bold grid-checkbox',
		width: 100,
		editable: isAdmin(role) || login === 'romashkok%!dobronom.by',
		onCellValueChanged: onIsImportChangeHandler,
	},
]

// доболнительные колонки для админа
if (isAdmin(role) || login === 'romashkok%!dobronom.by') {
	columnDefs.push(
		...deliveryScheduleColumnDefsForAdmin
	)
}

const defaultGridOptions = getDefaultGridOptions({
	localStorageKey: LOCAL_STORAGE_KEY,
	enableColumnStateSaving: true,
	enableFilterStateSaving: true,
})

const gridOptions = {
	...defaultGridOptions,
	columnDefs: columnDefs,
	rowClassRules: deliveryScheduleRowClassRules,
	defaultColDef: {
		...defaultGridOptions.defaultColDef,
		flex: null,
		minWidth: null
	},
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idSchedule,
	sideBar: deliveryScheduleSideBar(LOCAL_STORAGE_KEY),
	// запред ввода в модалке редактирования
	onCellEditingStarted: (event) => {
		if (event.colDef.field === "history") {
			supressInputInLargeTextEditor()
		}
	},
}

document.addEventListener('DOMContentLoaded', async () => {
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
	// форма скачивания отчета
	const downloadReportForm = document.querySelector('#downloadReportForm')
	downloadReportForm.addEventListener('submit', downloadReportFormSubmitHandler)

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

	// чекбоксы "Кратно машине" и поля с кратностью
	const addMultiplicity = addScheduleItemForm.querySelector('#machineMultiplicity')
	const editMultiplicity = editScheduleItemForm.querySelector('#machineMultiplicity')
	const addMultipleOfTruck = document.querySelector('#AddMultipleOfTruck')
	const editMultipleOfTruck = document.querySelector('#editMultipleOfTruck')
	addMultipleOfTruck.addEventListener('change', (e) => onMultipleOfTruckChangeHandler(e, addMultiplicity))
	editMultipleOfTruck.addEventListener('change', (e) => onMultipleOfTruckChangeHandler(e, editMultiplicity))

	// проверка номера контракта
	$('.counterpartyContractCode').change(checkContractNumber)
	// обновление опций графика при открытии модалки создания графика поставки
	$('#addScheduleItemModal').on('shown.bs.modal', (e) => changeScheduleOptions(addScheduleItemForm, ''))
	// очистка форм при закрытии модалки
	$('#addScheduleItemModal').on('hidden.bs.modal', (e) => {
		clearForm(e, addScheduleItemForm)
		blurActiveElem(e)
	})
	$('#editScheduleItemModal').on('hidden.bs.modal', (e) => {
		clearForm(e, editScheduleItemForm)
		blurActiveElem(e)
	})
	$('#sendExcelModal').on('hidden.bs.modal', (e) => {
		clearForm(e, sendExcelForm)
		blurActiveElem(e)
	})

	// рейтинг дней заказов
	$('#showRatingBtn').popover({
		html: true,
		content: 'ㅤㅤЗагрузка...ㅤㅤ'
	}).on('shown.bs.popover', getOrderRatingContent)

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
	checkScheduleData(scheduleData)
	// заполняем datalist кодов и названий контрагентов
	createCounterpartyDatalist(scheduleData)
	window.initData = null

	// получение настроек таблицы из localstorage
	restoreColumnState(gridOptions)
	restoreFilterState(gridOptions)
}


function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}
async function updateTable(gridOptions, data) {
	const res = data
		? { body: data }
		: await getData(getScheduleRCUrl)

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
// установка данных в таблицу
function setScheduleData(numStock) {
	const numStockData = numStock
		? scheduleData.filter((item) => item.numStock === numStock)
		: scheduleData
	const mappingData = getMappingData(numStockData)
	gridOptions.api.setRowData(mappingData)
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
	if (!rowNode) return

	const status = rowNode.data.status

	const confirmUnconfirmItem = status === 10 || status === 0
		? {
			name: `Подтвердить график`,
			disabled: (!isAdmin(role) && !isOrderSupport(role)) || (status !== 10 && status !== 0),
			action: () => {
				confirmScheduleItem(rowNode)
			},
			icon: uiIcons.check,
		}
		: {
			name: `Снять подтверждение с графика`,
			disabled: (!isAdmin(role) && !isOrderSupport(role)) || status === 0,
			action: () => {
				unconfirmScheduleItem(rowNode)
			},
			icon: uiIcons.x_lg,
		}

	const result = [
		{
			name: `Показать график`,
			action: () => {
				showScheduleItem(rowNode)
			},
			icon: uiIcons.table,
		},
		"separator",
		confirmUnconfirmItem,
		{
			name: `Редактировать график`,
			disabled: (!isAdmin(role) && !isOrderSupport(role)),
			action: () => {
				editScheduleItem(rowNode, setDataToForm)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Удалить график`,
			disabled: (!isAdmin(role) && !isOrderSupport(role)) || status === 0,
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

// обработчик изменения значения чекбокса "Кратно машине"
function onMultipleOfTruckChangeHandler(e, multiplicity) {
	const checked = e.target.checked
	toggleMultiplicityVisible(checked, multiplicity)
}

// переключение отображения поля кратности машины
function toggleMultiplicityVisible(checked, multiplicity) {
	if (checked) {
		multiplicity.required = true
		multiplicity.parentElement.classList.remove('d-none')
	} else {
		multiplicity.required = false
		multiplicity.parentElement.classList.add('d-none')
		multiplicity.value = ''
	}
}

// обработчик смены склада
function onNumStockSelectChangeHandler(e) {
	const numStock = Number(e.target.value)
	setScheduleData(numStock)
}

// обработчик изменения значения "Не учитывать в расчете ОРЛ"
async function onIsNotCalcCahngeHandler(params) {
	const data = params.data
	const idSchedule = data.idSchedule
	const rowNode = params.node
	await changeIsNotCalc(idSchedule, rowNode)
}
// обработчик изменения значения "Импорт"
async function onIsImportChangeHandler(params) {
	const data = params.data
	const idSchedule = data.idSchedule
	const rowNode = params.node
	await changeIsImport(idSchedule, rowNode)
}

// запрос на изменение значения "Не учитывать в расчете ОРЛ"
async function changeIsNotCalc(idSchedule, rowNode) {
	if (!isAdmin(role) && login !== 'romashkok%!dobronom.by') return
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeIsNotCalcBaseUrl}${idSchedule}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		// изменение обрабатывается таблицей
	} else {
		// обновляем данные с сервера
		updateTable()
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}

// получение рейтинга дней заказов по дням недели
async function getOrderRating() {
	const response = await getData(getCountScheduleOrderHasWeekUrl)
	if (!response || !response.object) return null
	return response.object
}

// получение рейтинга дней поставок по дням недели
async function getDeliveryRating() {
	const response = await getData(getCountScheduleDeliveryHasWeekUrl)
	if (!response || !response.object) return null
	return response.object
}

// запрос на изменение значения "Импорт"
async function changeIsImport(idSchedule, rowNode) {
	if (!isAdmin(role) && login !== 'romashkok%!dobronom.by') return
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${changeIsImportBaseUrl}${idSchedule}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		// изменение обрабатывается таблицей
	} else {
		// обновляем данные с сервера
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

// отправка данных на почту
async function sendScheduleDataToMail(e) {
	const btn = e.target
	btn.disabled = true

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 0)
	const res = await getData(sendScheduleRCDataToMailUrl)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
	btn.disabled = false

	if (res && res.status === '200') {
		snackbar.show(res.message)
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
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
		url: loadRCExcelUrl,
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
async function addScheduleItemFormHandler(e) {
	e.preventDefault()

	disableButton(e.submitter)

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)
	
	// проверка значений графика
	if (!isValidScheduleValues(data)) {
		snackbar.show(
			'Обнаружены ошибки в значения дней заказа или поставки.\n'
			+ 'Проверьте данные графика!'
		)
		enableButton(e.submitter)
		return
	}

	// ошибки в логике графика
	const errorMessage = getFormErrorMessage(data, error)
	if (errorMessage) {
		snackbar.show(errorMessage)
		enableButton(e.submitter)
		return
	}

	const orderRatingValidResult = await checkScheduleOrderRating(data, getOrderRating)
	if (!orderRatingValidResult.isValid) {
		snackbar.show(orderRatingValidResult.message)
		enableButton(e.submitter)
		return
	}

	ajaxUtils.postJSONdata({
		url: addScheduleRCItemUrl,
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
async function editScheduleItemFormHandler(e) {
	e.preventDefault()

	disableButton(e.submitter)

	if (!isAdmin(role) && !isOrderSupport(role)) {
		snackbar.show('Недостаточно прав!')
		enableButton(e.submitter)
		return
	}

	const formData = new FormData(e.target)
	const data = scheduleItemDataFormatter(formData)

	// проверка значений графика
	if (!isValidScheduleValues(data)) {
		snackbar.show(
			'Обнаружены ошибки в значения дней заказа или поставки.\n'
			+ 'Проверьте данные графика!'
		)
		enableButton(e.submitter)
		return
	}

	// ошибки в логике графика
	const errorMessage = getFormErrorMessage(data, error)
	if (errorMessage) {
		snackbar.show(errorMessage)
		enableButton(e.submitter)
		return
	}

	ajaxUtils.postJSONdata({
		url: editScheduleRCItemUrl,
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

function downloadReportFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const dateStart = formData.get('dateStart')
	const dateEnd = formData.get('dateEnd')

	disableButton(e.submitter)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	fetch(`${downloadReportBaseUrl}${dateStart}&${dateEnd}`)
		.then(res => res.status === 200 ? res.blob() : Promise.reject('Ошибка: статус ответа не 200'))
		.then(blob => {
			const url = window.URL.createObjectURL(blob)
			const a = document.createElement('a')
			a.style.display = 'none'
			a.href = url
			a.download = `report_${dateStart}_${dateEnd}.xlsx`
			document.body.appendChild(a)
			a.click()
			window.URL.revokeObjectURL(url)
			document.body.removeChild(a)
			snackbar.show('Скачивание файла...')
		})
		.catch((e) => {
			console.error('Ошибка при скачивании файла:', e)

			// Резервный метод: скачивание через ссылку
			const a = document.createElement('a')
			a.style.display = 'none'
			a.href = `${downloadReportBaseUrl}${dateStart}&${dateEnd}`
			a.download = `report_${dateStart}_${dateEnd}.xlsx`
			document.body.appendChild(a)
			a.click()
			document.body.removeChild(a)
			snackbar.show('Файл скачивается через резервный метод...')
		})
		.finally(() => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			enableButton(e.submitter)
			$('#downloadReportModal').modal('hide')
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
	const machineMultiplicity = data.machineMultiplicity ? Number(data.machineMultiplicity) : null
	const connectionSupply = data.connectionSupply ? Number(data.connectionSupply) : null
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
		machineMultiplicity,
		connectionSupply
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

	const machineMultiplicityInput = editScheduleItemForm.machineMultiplicity
	toggleMultiplicityVisible(scheduleItem.multipleOfTruck, machineMultiplicityInput)
	machineMultiplicityInput.value = scheduleItem.machineMultiplicity ? scheduleItem.machineMultiplicity : ''

	editScheduleItemForm.connectionSupply.value = scheduleItem.connectionSupply ? scheduleItem.connectionSupply : ''

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

// очистка формы
function clearForm(e, form) {
	form.reset()
}

// получение контента для отображения рейтинга дней заказов
async function getOrderRatingContent() {
	const orderRating = await getOrderRating()
	const deliveryRating = await getDeliveryRating()

	if (!orderRating && !deliveryRating) {
		$('.popover-body').html('Ошибка загрузки данных.')
		return
	}

	$('.popover-body').html(
		`<div>понедельник: ${orderRating.monday || 'н/д'}/${deliveryRating.monday || 'н/д'}</div>`
		+ `<div>вторник: ${orderRating.tuesday || 'н/д'}/${deliveryRating.tuesday || 'н/д'}</div>`
		+ `<div>среда: ${orderRating.wednesday || 'н/д'}/${deliveryRating.wednesday || 'н/д'}</div>`
		+ `<div>четверг: ${orderRating.thursday || 'н/д'}/${deliveryRating.thursday || 'н/д'}</div>`
		+ `<div>пятница: ${orderRating.friday || 'н/д'}/${deliveryRating.friday || 'н/д'}</div>`
		+ `<div>суббота: ${orderRating.saturday || 'н/д'}/${deliveryRating.saturday || 'н/д'}</div>`
		+ `<div>воскресенье: ${orderRating.sunday || 'н/д'}/${deliveryRating.sunday || 'н/д'}</div>`
	)
}