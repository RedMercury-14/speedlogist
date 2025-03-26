import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { getReviewsBaseUrl, updateUserReviewUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { uiIcons } from './uiIcons.js'
import { dateHelper, debounce, getData, isAdmin, isObserver } from './utils.js'

const PAGE_NAME = 'reviewsTable'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const role = document.querySelector('#role').value

const columnDefs = [
	{
		headerName: 'Дата отзыва', field: 'reviewDate', sort: 'desc',
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{ headerName: 'Отправитель', field: 'sender',},
	// {
	// 	headerName: 'Требование ответа', field: 'needReply',
	// 	cellClass: (params) => params && params.value ? 'px-2 text-center font-weight-bold' : 'px-2 text-center',
	// 	valueFormatter: (params) => params && params.value ? 'Необходим ответ' : 'Нет',
	// 	cellRenderer: (params) => params.valueFormatted
	// },
	{ headerName: 'Email', field: 'email', },
	{ headerName: 'Тема', field: 'topic', },
	{ headerName: 'Сообщение', field: 'reviewBody', flex: 4, },
	{
		headerName: 'Дата ответа', field: 'replyDate',
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{
		headerName: 'Ответ', field: 'replyBody', flex: 4,
		cellClass: (params) => params && params.data.needReply && !params.data.replyBody ? 'px-2 text-center border-info' : 'px-2 text-center',
		editable: (params) => isAdmin(role) || isObserver(role) && params.data.needReply && params.data.email && !params.data.replyBody,
		cellEditor: 'agLargeTextCellEditor',
		cellEditorPopup: true,
		onCellValueChanged: updateReview,
		cellEditorParams: {
			maxLength: 10000000,
		},
	},
	{ headerName: 'Автор ответа', field: 'replyAuthor', },
	{
		headerName: 'Статус', field: 'statusToView',
		cellClass: statusToViewClasses,
	},
	{
		headerName: 'Комментарий', field: 'comment',
		editable: isAdmin(role) || isObserver(role),
		cellEditor: 'agLargeTextCellEditor',
		cellEditorPopup: true,
		onCellValueChanged: updateReview,
		cellEditorParams: {
			maxLength: 10000000,
		},
	},
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
	getRowId: (params) => params.data.idReview,
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
	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	gridOptions.api.showLoadingOverlay()
	restoreColumnState()

	// автозаполнение полей дат в форме поиска заявок
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 0)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	const reviews = await getReviewsData(dateStart, dateEnd)
	updateTable(gridOptions, reviews)

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit',searchFormSubmitHandler)

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

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})


// установка стартовых данных
async function initStartData() {
	// const reviews = window.initData.reviews ? window.initData.reviews : []
	// updateTable(gridOptions, reviews)
	window.initData = null
}

// получение данных
async function getReviewsData(dateStart, dateEnd) {
	const url = `${getReviewsBaseUrl}${dateStart}&${dateEnd}`
	const res = await getData(url)
	if (!res) return []
	return res.reviews ? res.reviews : []
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
	const statusToView = getStatusToView(item)
	return {
		...item,
		statusToView
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const result = [
		{
			name: `Сбросить настройки колонок`,
			action: () => {
				gridColumnLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
			icon: uiIcons.cancel,
		},
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

// обновление данных обращения обратной связи
function updateReview(params) {
	const rowData = params.data
	const fildName = params.colDef.field
	const oldValue = params.oldValue

	gridOptions.api.showLoadingOverlay()

	ajaxUtils.postJSONdata({
		url: updateUserReviewUrl,
		data: rowData,
		successCallback: (res) => {
			gridOptions.api.hideOverlay()

			if (res.status === '200') {
				const updatedReview = res.object
				updateTableRow(gridOptions, updatedReview)
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '100') {
				const oldRowData = { ...rowData }
				oldRowData[fildName] = oldValue
				updateTableRow(gridOptions, oldRowData)
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			const oldRowData = { ...rowData }
			oldRowData[fildName] = oldValue
			updateTableRow(gridOptions, oldRowData)
			gridOptions.api.hideOverlay()
		}
	})
}

// обработчик отправки формы поиска
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	try {
		gridOptions.api.showLoadingOverlay()
		const formData = new FormData(e.target)
		const data = Object.fromEntries(formData)
		const reviews = await getReviewsData(data.date_from, data.date_to)
		updateTable(gridOptions, reviews)
	} catch (error) {
		snackbar.show('Ошибка получения данных')
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

function dateValueFormatter(params) {
	let date = params.value
	if (!date) return ''
	if (!Date.parse(date)) date = Number(date)
	return dateHelper.getFormatDate(date)
}
function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
}
function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	if (!Date.parse(date1)) date1 = Number(date1)
	if (!Date.parse(date2)) date2 = Number(date2)
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}

export function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] }
	)
}

function getReviewStatus(status) {
	switch (status) {
		case 10:
			return 'Создано'
		case 20:
			return 'Ответ отправлен'
		default:
			return `Неизвестный статус (${status})`
	}
}

function getStatusToView(reviewItem) {
	const status = reviewItem.status
	const needReply = reviewItem.needReply

	if (status === 10) {
		return needReply ? 'Ожидается ответ' : 'Создано'
	}

	if (status === 20) {
		return 'Ответ отправлен'
	}

	return `Неизвестный статус (${status})`
}

function statusToViewClasses(params) {
	const defaultClasses = 'px-2 text-center'
	if (!params) return defaultClasses
	const data = params.data
	if (!data) return defaultClasses
	if (data.status === 20) return `text-success font-weight-bold ${defaultClasses}`
	if (data.status === 10 && data.needReply) return `text-warning font-weight-bold ${defaultClasses}`
	return defaultClasses
}