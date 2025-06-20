import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { addTask398Url, downloadReport398Url, getReport398List } from './globalConstants/urls.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { changeGridTableMarginTop, dateHelper, debounce, disableButton, enableButton, getData, hideLoadingSpinner, isObserver, showLoadingSpinner } from './utils.js'

const PAGE_NAME = 'orlReport398'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)


const columnDefs = [
	{ headerName: 'id', field: 'idTask', hide: true, sort: 'desc', },
	{
		headerName: 'Дата создания', field: 'dateCreate',
		minWidth: 140,
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{
		headerName: 'Магазины', field: 'stocks',
		cellClass: 'px-1 py-0 text-center small-row',
		cellRenderer: BtnCellRenderer,
		cellRendererParams: {
			onClick: showInfoModal,
			dynamicLabel: getShowShopsLabel,
			className: 'btn btn-light border btn-sm',
		},
		filterParams: {
			valueFormatter: (params) => params.value ? params.value.replaceAll(',', ', ') : null,
		},
		flex: 2,
	},
	{ headerName: 'Вид расхода', field: 'bases', },
	{ headerName: 'Создал', field: 'userCreate', },
	{
		headerName: 'С', field: 'fromDate',
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: 'По', field: 'toDate',
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: 'Коммент', field: 'comment', },
	{ headerName: 'Статус', field: 'status', },
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		flex: 1,
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
	getRowId: (params) => params.data.idTask,
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
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	},
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
	restoreColumnState()

	// автозаполнение полей дат в форме поиска заявок
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 1, 0)
	const dateFromField = reportDataForm.dateFrom
	const dateToField = reportDataForm.dateTo
	dateFromField.value = dateStart
	dateToField.value = dateEnd
	dateFromField.addEventListener('change', (e) => validateDateFields(dateFromField, dateToField))
	dateToField.addEventListener('change', (e) => validateDateFields(dateFromField, dateToField))

	// листнер на отправку формы поиска заявок
	reportDataForm.addEventListener('submit',reportDataFormSubmitHandler)
	// листнер на проверку поля магазинов
	reportDataForm.shops.addEventListener('input', (e) => validateShopsField(e.target))
	// скачивание истории продаж
	reportDataForm.downloadReportBtn.addEventListener('click', downloadReport)

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
	const data = window.initData.list ? window.initData.list : []
	updateTable(gridOptions, data)
	window.initData = null
}

async function getListData() {
	const res = await getData(getReport398List)
	if (!res) return []
	return res.list ? res.list : []
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
	return data.map(mapCallback)
}
function mapCallback(item) {
	return {
		...item,
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const result = [
		{
			name: `Сбросить настройки фильтров`,
			action: () => {
				gridFilterLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
			icon: uiIcons.cancel,
		},
		
		"separator",
		"excelExport",
	]

	return result
}
function addTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ add: [rowData] }
	)
}


// обработчик отправки формы поиска заявок
async function reportDataFormSubmitHandler(e) {
	e.preventDefault()

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const submitter = e.submitter
	const submitType = submitter.dataset.submittype
	const url = submitType === 'loadData' ? addTask398Url : ''

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const dateFromField = e.target.dateFrom
	const dateToField = e.target.dateTo
	const shopsField = e.target.shops

	if (!validateDateFields(dateFromField, dateToField)) return
	if (!validateShopsField(shopsField)) return

	data.shops = data.shops.trim()
	data.whatBase = data.whatBase.trim()

	if (!url) return

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
			url: url,
			token: token,
			data: data,
			successCallback: async (res) => {
				clearTimeout(timeoutId)
				bootstrap5overlay.hideOverlay()
	
				if (res.status === '200') {
					shopsField.value = ''
					// const data = await getListData()
					// updateTable(gridOptions, data)
					addTableRow(gridOptions, res.task)
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

// валидация номеров магазинов
function validateShopsField(field) {
	const regex = /^(\d+)((,\s?\d+)*)$/
	const value = field.value.trim()

	if (regex.test(value)) {
		field.setCustomValidity('')
		return true
	} else {
		field.setCustomValidity('Введите номера магазинов через запятую (например: 123,456 или 123, 456)')
		field.reportValidity()
		return false
	}
}
// валидация диапазона дат
function validateDateFields(dateFromField, dateToField) {
	const fromValue = new Date(dateFromField.value).getTime()
	const toValue = new Date(dateToField.value).getTime()
	const diff = toValue - fromValue

	const validCondition = diff >= 0 && diff <= dateHelper.DAYS_TO_MILLISECONDS

	if (validCondition) {
		dateToField.setCustomValidity('')
		return true
	} else {
		dateToField.setCustomValidity('Допустимый диапазон дат - не более двух дней')
		dateToField.reportValidity()
		return false
	}
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

// скачивание истории продаж
function downloadReport(e) {
	showLoadingSpinner(e.target, 'Формирование архива')
	disableButton(e.target)
	fetch(downloadReport398Url)
		.then(res => res.status === 200 ? res.blob() : Promise.reject('Ошибка: статус ответа не 200'))
		.then(blob => {
			const url = window.URL.createObjectURL(blob)
			const a = document.createElement('a')
			a.style.display = 'none'
			a.href = url
			a.download = '398.zip'
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
			a.href = downloadReport398Url
			a.download = '398.zip'
			document.body.appendChild(a)
			a.click()
			document.body.removeChild(a)
			snackbar.show('Файл скачивается через резервный метод...')
		})
		.finally(() => {
			hideLoadingSpinner(e.target, 'Скачать историю продаж')
			enableButton(e.target)
		})
}

// отображение модального окна с историей
function showInfoModal(params) {
	const historyContainer = document.querySelector('#messageContainer')
	const history = params.value.replaceAll(',', ', ')
	historyContainer.innerHTML = history
	$('#displayMessageModal').modal('show')
}

function getShowShopsLabel(params) {
	return params.value
		? `Показать магазины (${params.value.split(',').length})`
		: 'Нет данных'
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

function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}