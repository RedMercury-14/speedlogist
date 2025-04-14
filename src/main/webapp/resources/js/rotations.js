import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { approveCreateRotationUrl, downloadRotationFAQUrl, getActualRotationsExcelUrl, getRotationListUrl, loadRotationExcelUrl, preCreateRotationUrl, updateRotationUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData, hideLoadingSpinner, isAdmin, isObserver, isRetail, showLoadingSpinner } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { ajaxUtils } from './ajaxUtils.js'

const PAGE_NAME = 'rotationList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const role = document.querySelector('#role').value

const TO_LIST_REG = /^(?:Сеть|\b[1-9]\d{1,4}\b(?:,\b[1-9]\d{1,4}\b)*)$/

const NOW_DATE_MS = new Date().setHours(0, 0, 0, 0)

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'grey-row': params => params.data && params.data.status === 20,
	'red-row': params => params.data && params.data.status === 10,
	'inactive-overlay': params => params.data && params.data.status === 30 && !params.data.isValidByPeriod,
}
const columnDefs = [
	{
		headerName: "№", field: "idRotation",
		minWidth: 60, width: 60,
	},
	{ headerName: "Код товара", field: "goodIdNew", width: 100, },
	{ headerName: "Наименование товара", field: "goodNameNew", },
	{
		headerName: "Дата начала ротации", field: "startDate", width: 115,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: "Дата окончания ротации", field: "endDate", width: 115,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: "Статус", field: "statusText", width: 100, cellClass: 'px-2 text-center font-weight-bold',},
	{ headerName: "Код аналога", field: "goodIdAnalog", width: 100, },
	{ headerName: "Наименование аналога", field: "goodNameAnalog", },
	{
		headerName: "Список ТО / Сеть", field: "toList",
		editable: true,
		cellEditorPopup: true,
		cellEditor: 'agLargeTextCellEditor',
	},
	{
		headerName: "Учитывать остатки старого кода?", field: "countOldCodeRemains",
		width: 100,
		cellDataType: false,
		valueFormatter: (params) => params.value ? "Да" : "Нет",
		filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
	},
	{ headerName: "Порог ТЗ старого кода", field: "limitOldCode", width: 100, },
	{ headerName: "Коэффициент переноса продаж старого кода на новый", field: "coefficient", width: 100, },
	{
		headerName: "Переносим продажи старого кода к продажам нового, если есть продажи у нового?", field: "transferOldToNew",
		width: 130,
		cellDataType: false,
		valueFormatter: (params) => params.value ? "Да" : "Нет",
		filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
	},
	{
		headerName: "Распределяем новую позицию, если есть остаток старого кода на РЦ?", field: "distributeNewPosition",
		width: 120,
		cellDataType: false,
		valueFormatter: (params) => params.value ? "Да" : "Нет",
		filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
	},
	{ headerName: "Порог остатка старого кода на ТО (шт/кг)", field: "limitOldPositionRemain", width: 100, },
	{ headerName: "ФИО инициатора ротации", field: "rotationInitiator", },
	{
		headerName: "Дата подтверждения", field: "approveDate", width: 115,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
]
if (isAdmin(role)) {
	columnDefs.push(
		{ headerName: "История", field: "history", },
	)
}
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: "px-2 font-weight-bold",
		cellClass: "px-2 text-center",
		// flex: 1,
		minWidth: 100,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
		enableRowGroup: true,
	},
	animateRows: true,
	suppressDragLeaveHidesColumns: true,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idRotation,
	// запред ввода в модалке редактирования
	onCellEditingStarted: (event) => {
		if (event.colDef.cellEditor === "agLargeTextCellEditor") {
			setTimeout(() => {
				const modal = document.querySelector(".ag-large-text")
				if (modal) {
					const textarea = modal.querySelector("textarea")
					if (textarea) {
						textarea.readOnly = true
					}
				}
			}, 100)
		}
	},
	sideBar: {
		toolPanels: [
			{
				id: "columns",
				labelDefault: "Columns",
				labelKey: "columns",
				iconKey: "columns",
				toolPanel: "agColumnsToolPanel",
				toolPanelParams: {
					suppressRowGroups: true,
					suppressValues: true,
					suppressPivots: true,
					suppressPivotMode: true,
				},
			},
			{
				id: "filters",
				labelDefault: "Filters",
				labelKey: "filters",
				iconKey: "filter",
				toolPanel: "agFiltersToolPanel",
			},
			{
				id: "resetState",
				iconKey: "menu",
				labelDefault: "Сброс настроек",
				toolPanel: ResetStateToolPanel,
				toolPanelParams: {
					localStorageKey: LOCAL_STORAGE_KEY,
				},
			},
		],
	},
}

document.addEventListener('DOMContentLoaded', async () => {
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	restoreColumnState()

	const rotationData = await getRotationData()
	updateTable(gridOptions, rotationData)

	rotationForm.addEventListener('submit', rotationFormSubmitHandler)
	sendExcelForm.addEventListener('submit', sendExcelFormHandler)
	updateCoefficientForm.addEventListener('submit', updateCoefficientFormHandler)

	const downloadExcelBtn = document.getElementById('downloadExcel')
	downloadExcelBtn.addEventListener('click', downloadExcelHandler)

	// кнопка скачивания файла с инструкцией
	const downloadFAQBtn = document.querySelector('#downloadFAQ')
	downloadFAQBtn.addEventListener('click', () => window.open(downloadRotationFAQUrl, '_blank'))
	// отмена мигания кнопки через 10 сек
	setTimeout(() => downloadFAQBtn.classList.remove('softGreenBlink'), 10000)

	$('#rotationModal').on('hidden.bs.modal', (e) => {
		rotationForm.reset()
	})
})


// обработчик отправки формы создания новой ротации
function rotationFormSubmitHandler(e) {
	e.preventDefault()

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const payload = {
		...data,
		idRotation: data.idRotation ? Number(data.idRotation) : '',
		goodIdNew: data.goodIdNew ? Number(data.goodIdNew) : '',
		goodIdAnalog: data.goodIdAnalog ? Number(data.goodIdAnalog) : '',
		countOldCodeRemains: data.countOldCodeRemains === 'Да',
		limitOldCode: data.limitOldCode ? Number(data.limitOldCode) : '',
		coefficient: data.coefficient ? Number(data.coefficient) : '',
		transferOldToNew: data.transferOldToNew === 'Да',
		distributeNewPosition: data.distributeNewPosition === 'Да',
		limitOldPositionRemain: data.limitOldPositionRemain ? Number(data.limitOldPositionRemain) : '',
		toList: data.toList.trim(),
	}

	const startDate = new Date(data.startDate).getTime()
	const endDate = new Date(data.endDate).getTime()
	if (startDate > endDate) {
		snackbar.show('Дата начала ротации не может быть больше даты окончания ротации')
		return
	}

	if (!TO_LIST_REG.test(payload.toList)) {
		snackbar.show('Укажите список ТО через запятую без пробелов либо слово "Сеть" с учетом регистра')
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: preCreateRotationUrl,
		data: data,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				const data = await getRotationData()
				updateTable(gridOptions, data)
				$(`#rotationModal`).modal('hide')
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '205') {
				$(`#rotationModal`).modal('hide')
				const isApprove = confirm(`${res.message} Подтверждаете создание новой ротации?`)
				if (isApprove) {
					approveCreateRotation(payload)
				} else {
					alert('Ротация не создана')
				}
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// обработчик отправки формы загрузки таблицы эксель
function sendExcelFormHandler(e) {
	e.preventDefault()

	if (!isAdmin(role)) return

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadRotationExcelUrl,
		data: file,
		successCallback: async (res) => {
			hideLoadingSpinner(submitButton, 'Загрузить')

			if (res.status === '200') {
				const data = await getRotationData()
				updateTable(gridOptions, data)
				snackbar.show('Данные успешно загружены')
				$(`#sendExcelModal`).modal('hide')
				return
			}

			if (res.status === '100') {
				const errorMessage = res.message || 'Ошибка загрузки данных'
				snackbar.show(errorMessage)
				return
			}
		},
		errorCallback: () => hideLoadingSpinner(submitButton, 'Загрузить')
	})
}

// обработчик отправки формы изменения коэффициента переноса продаж
function updateCoefficientFormHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	const payload = {
		idRotation: data.idRotation ? Number(data.idRotation) : null,
		coefficient: data.coefficient ? Number(data.coefficient) : null,
		goodIdNew: data.goodIdNew ? Number(data.goodIdNew) : null,
		goodIdAnalog: data.goodIdAnalog ? Number(data.goodIdAnalog) : null,
		status: data.status ? Number(data.status) : null,
	}

	updateRotation(payload, true)
}

// обработчик скачивания Excel-файла
function downloadExcelHandler(e) {
	const url = getActualRotationsExcelUrl
	window.open(url, '_blank')
}

// получение данных
async function getRotationData() {
	try {
		const res = await getData(getRotationListUrl)
		return res ? res.reviews : []
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}

// подтверждение создания новой ротации
function approveCreateRotation(rotation) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: approveCreateRotationUrl,
		data: rotation,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				const data = await getRotationData()
				updateTable(gridOptions, data)
				$(`#rotationModal`).modal('hide')
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})

}

// обновление данных ротации
function updateRotation(payload, isModal) {
	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: updateRotationUrl,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				const data = await getRotationData()
				updateTable(gridOptions, data)
				isModal && $(`#updateCoefficientModal`).modal('hide')
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

function getPayloadForUpdate(rotation) {
	return {
		idRotation: rotation.idRotation,
		coefficient: rotation.coefficient,
		goodIdNew: rotation.goodIdNew,
		goodIdAnalog: rotation.goodIdAnalog,
		status: rotation.status,
	}
}

// методы таблицы
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
	return data.map(mapCallback)
}
function mapCallback(item) {
	const isValidByPeriod = isValidRotationByPeriod(item)
	const statusText = getRotationStatusText(item)
	return {
		...item,
		isValidByPeriod,
		statusText,
	}
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const status = rowNode.data.status
	const approveDate = rowNode.data.approveDate
	const validDateToChangeCoeff = approveDate + dateHelper.DAYS_TO_MILLISECONDS * 2
	const isChangableCoeff = (Date.now() < validDateToChangeCoeff && status === 30) || status === 20

	const items = [
		{
			disabled: status !== 20 || (!isRetail(role) && !isAdmin(role)),
			name: `Подтвердить ротацию`,
			action: () => confirmRotation(rowNode),
		},
		{
			disabled: !isChangableCoeff || (!isRetail(role) && !isAdmin(role)),
			name: "Изменить коэффициент переноса продаж старого кода на новую ротацию",
			action: () => {
				updateCoefficientForm.idRotation.value = rowNode.data.idRotation
				updateCoefficientForm.goodIdNew.value = rowNode.data.goodIdNew
				updateCoefficientForm.goodIdAnalog.value = rowNode.data.goodIdAnalog
				updateCoefficientForm.status.value = rowNode.data.status
				updateCoefficientForm.coefficient.value = rowNode.data.coefficient
				$('#updateCoefficientModal').modal('show')
			},
		},
		{
			name: "Отменить ротацию",
			disabled: status === 10,
			action: () => {
				deleteRotation(rowNode)
			},
		},
		"separator",
		{
			name: "Сбросить настройки колонок",
			action: () => {
				gridColumnLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
		},
		{
			name: `Сбросить настройки фильтров`,
			action: () => {
				gridFilterLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
		},
		"separator",
		"excelExport"
	]

	return items
}
// подтверждение
function confirmRotation(rowNode) {
	const rotation = rowNode.data
	const payload = getPayloadForUpdate(rotation)
	payload.status = 30
	payload.approveDate = new Date().toISOString().slice(0, 10)
	// payload.approveDate = "2025-04-10"
	updateRotation(payload, rowNode)
}
// снятие подтверждения
function unconfirmRotation(rowNode) {
	const rotation = rowNode.data
	const payload = getPayloadForUpdate(rotation)
	payload.status = 20
	updateRotation(payload, rowNode)
}
// отмена
function deleteRotation(rowNode) {
	const rotation = rowNode.data
	const payload = getPayloadForUpdate(rotation)
	payload.status = 10
	updateRotation(payload, rowNode)
}

// определение, что ротация сейчас действует
function isValidRotationByPeriod(rotation) {
	const startDate = rotation.startDate
	const endDate = rotation.endDate
	if (!startDate || !endDate) return false
	if (
		NOW_DATE_MS >= startDate
		&& NOW_DATE_MS < endDate
	) return true
	return false
}

// статус действующей ротации
function getValidRotationStatusText(rotation) {
	const startDate = rotation.startDate
	const endDate = rotation.endDate
	if (!startDate || !endDate) return 'Не действует'
	if (NOW_DATE_MS < startDate) return 'Период действия ещё не наступил'
	if (NOW_DATE_MS > endDate) return 'Период действия окончен'
	if (
		NOW_DATE_MS >= startDate
		&& NOW_DATE_MS < endDate
	) return 'Действует'
	return 'Не действует'
}

// статус ротации
function getRotationStatusText(rotation) {
	const status = rotation.status

	switch (status) {
		case 10:
			return 'Отменена'
		case 20:
			return 'Ожидает подтверждения'
		case 30:
			return getValidRotationStatusText(rotation)
		default:
			return `Неизвестный статус (${status})`
	}
}


// конверторы дат для таблицы
function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}
function dateValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDate(date)
}
function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
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
	messageContainer.innerHTML = message
	$('#displayMessageModal').modal('show')
}
