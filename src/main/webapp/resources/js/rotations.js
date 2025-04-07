import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { approveCreateRotationUrl, getRotationListUrl, loadRotationExcelUrl, preCreateRotationUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData, hideLoadingSpinner, isAdmin, showLoadingSpinner } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { ajaxUtils } from './ajaxUtils.js'

const PAGE_NAME = 'rotationList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const role = document.querySelector('#role').value

const TO_LIST_REG = /^(?:Сеть|\b[1-9]\d{1,4}\b(?:,\b[1-9]\d{1,4}\b)*)$/

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)


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
	{ headerName: "Действует?", field: "valid", },
	{ headerName: "Код аналога", field: "goodIdAnalog", width: 100, },
	{ headerName: "Наименование аналога", field: "goodNameAnalog", },
	{
		headerName: "Список ТО / Сеть", field: "toList",
		editable: true,
		cellEditorPopup: true,
		cellEditor: 'agLargeTextCellEditor',
		onCellValueChanged: params => {
			
		}
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
]
const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
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
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	restoreColumnState()

	const rotationData = await getRotationData()
	console.log("🚀 ~ document.addEventListener ~ rotationData:", rotationData)
	updateTable(gridOptions, rotationData)

	rotationForm.addEventListener('submit', rotationFormSubmitHandler)
	sendExcelForm.addEventListener('submit', sendExcelFormHandler)
})


// обработчик отправки формы создания новой ротации
function rotationFormSubmitHandler(e) {
	e.preventDefault()
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

			if (res === '200') {
				const data = await getRotationData()
				updateTable(gridOptions, data)
				snackbar.show('Данные успешно загружены')
				$(`#sendExcelModal`).modal('hide')
				return
			}

			if (res === '100') {
				const errorMessage = res.message || 'Ошибка загрузки данных'
				snackbar.show(errorMessage)
				return
			}
		},
		errorCallback: () => hideLoadingSpinner(submitButton, 'Загрузить')
	})
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
	return {
		...item,
	}
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const items = [
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


function roundNumber(num, fraction) {
	return Math.round((Number(num) + Number.EPSILON) * fraction) / fraction
}



