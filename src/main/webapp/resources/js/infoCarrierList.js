import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import {
	dateComparator, DateTimeEditor, dateTimeValueFormatter, gridColumnLocalState,
	gridFilterLocalState, ResetStateToolPanel, SubmitButtonTextEditor
} from './AG-Grid/ag-grid-utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData, isObserver, } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { ajaxUtils } from './ajaxUtils.js'
import { getInfoCarrierListBaseUrl, sendEmailInfoCarrierBaseUrl, updateInfoCarrierUrl } from './globalConstants/urls.js'

const PAGE_NAME = 'infoCarrierList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'green-row': params => params.data && params.data.status === 20,
	'red-row': params => params.data && params.data.status === 30,
}

const columnDefs = [
	{
		headerName: "№", field: "id",
		sort: "desc", minWidth: 60, flex: 1,
	},
	{
		headerName: "Дата поступления заявки", field: "dateTimeCreate",
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{ headerName: "Рынок грузоперевозок", field: "cargoTransportMarket", },
	{ headerName: "Форма собственности", field: "ownershipType", },
	{ headerName: "Название организации", field: "carrierName", },
	{ headerName: "Кол-во предлагаемых авто", field: "offeredVehicleCount", },
	{
		headerName: "ГП, т", field: "vehicleCapacity",
		valueFormatter: stringifyListValueFormatter,
		filterParams: { valueFormatter: stringifyListValueFormatter, },
	},
	{
		headerName: "ПВ, палл", field: "palletCapacity",
		valueFormatter: stringifyListValueFormatter,
		filterParams: { valueFormatter: stringifyListValueFormatter, },
	},
	{
		headerName: "Тип кузова", field: "bodyType",
		valueFormatter: stringifyListValueFormatter,
		filterParams: { valueFormatter: stringifyListValueFormatter, },
	},
	{ headerName: "Наличие гидроборта", field: "hasTailLift", },
	{ headerName: "Наличие навигации", field: "hasNavigation", },
	{ headerName: "Город расположения транспорта", field: "vehicleLocationCity", },
	{ headerName: "Телефон для связи", field: "contactPhone", },
	{ headerName: "ФИО", field: "contactCarrier", },
	{ headerName: "Адрес эл. почты", field: "emailAddress", },
	{ headerName: "Примечание пользователя", field: "notes", minWidth: 240, },
	{
		headerName: "Статус заявки", field: "status",
		cellClass: 'px-2 text-center font-weight-bold',
		valueFormatter: params => getStatusToView(params.value),
		filterParams: { valueFormatter: params => getStatusToView(params.value), },
	},
	{
		headerName: "Предлагаемый тариф", field: "offeredRate",
		cellClass: 'px-2 text-center editCell',
		editable: !isObserver(role),
		cellEditor: SubmitButtonTextEditor,
		cellEditorPopup: true,
		cellEditorParams: {
			maxLength: 10000000,
		},
	},
	{
		headerName: "Дата связи с перевозчиком", field: "carrierContactDate",
		cellClass: 'px-2 text-center editCell',
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
		editable: !isObserver(role), cellEditor: DateTimeEditor, cellEditorPopup: true,
	},
	{
		headerName: "Cсылка на регистрацию", field: "dateSendRegLink",
		minWidth: 140,
		cellClass: 'px-0 text-center',
		// valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
		cellRenderer: params => {
			if (params.data.dateSendRegLink) return dateHelper.getFormatDateTime(params.data.dateSendRegLink)
			if (!params.data.emailAddress) return null

			const sendRegLinkBtn = document.createElement("button")
			sendRegLinkBtn.textContent = 'Выслать'
			sendRegLinkBtn.className = 'btn btn-success btn-sm'
			sendRegLinkBtn.addEventListener("click", () => sendRegLink(params))
			return sendRegLinkBtn
		}
	},
	{ headerName: "Ответственный специалист ОТЛ", field: "otlResponsibleSpecialist", },
	{
		headerName: "Примечание специалиста ОТЛ", field: "comment",
		minWidth: 240,
		cellClass: 'px-2 text-center editCell',
		editable: !isObserver(role),
		cellEditor: SubmitButtonTextEditor,
		cellEditorPopup: true,
		cellEditorParams: {
			maxLength: 10000000,
		},
	},
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		flex: 2,
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
	getRowId: (params) => params.data.id,
	onCellValueChanged: updateCarrierInfo,
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	},
	statusBar: {
		statusPanels: [
			{ statusPanel: 'agTotalAndFilteredRowCountComponent', align: 'left' },
		],
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

	const reviews = await getCarrierInfoData(dateStart, dateEnd)
	updateTable(gridOptions, reviews)

	// листнер на отправку формы поиска заявок
	searchDataForm.addEventListener('submit',searchFormSubmitHandler)
})

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})


// получение данных
async function getCarrierInfoData(dateStart, dateEnd) {
	const url = `${getInfoCarrierListBaseUrl}${dateStart}&${dateEnd}`
	const res = await getData(url)
	if (!res) return []
	return res.objects ? res.objects : []
}

// обработчик отправки формы поиска
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	try {
		gridOptions.api.showLoadingOverlay()
		const formData = new FormData(e.target)
		const data = Object.fromEntries(formData)
		const reviews = await getCarrierInfoData(data.date_from, data.date_to)
		updateTable(gridOptions, reviews)
	} catch (error) {
		snackbar.show('Ошибка получения данных')
	}
}

// обновление данных обращения обратной связи
function updateCarrierInfo(params) {
	const rowData = params.data
	const fieldName = params.colDef.field
	const oldValue = params.oldValue
	const newValue = getValueForUpdate(fieldName, params.value)
	const oldStatus = params.data.status
	const newStatus = oldStatus === 20 || fieldName === 'carrierContactDate' ? 20 : 10
	const newRowData = { ...rowData, [fieldName]: newValue, status: newStatus }

	gridOptions.api.showLoadingOverlay()

	ajaxUtils.postJSONdata({
		url: updateInfoCarrierUrl,
		data: newRowData,
		successCallback: (res) => {
			gridOptions.api.hideOverlay()

			if (res.status === '200') {
				const updatedReview = res.object
				updateTableRow(gridOptions, updatedReview)
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '100') {
				const oldRowData = { ...rowData, [fieldName]: oldValue }
				updateTableRow(gridOptions, oldRowData)
				const message = res.message ? res.message : 'Неизвестная ошибка, значение не изменено'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			const oldRowData = { ...rowData, [fieldName]: oldValue }
			updateTableRow(gridOptions, oldRowData)
			gridOptions.api.hideOverlay()
		}
	})
}


function sendRegLink(params) {
	const id = params.data.id

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.get({
		url: sendEmailInfoCarrierBaseUrl + id,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				const updatedReview = res.object
				updateTableRow(gridOptions, updatedReview)
				const successMessage = res.message || 'Ссылка на регистрацию отправлена'
				snackbar.show(successMessage)
				return
			}

			if (res.status === '100') {
				const errorMessage = res.message || 'Ошибка получения данных'
				snackbar.show(errorMessage)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			snackbar.show('Ошибка получения данных')
		}
	})
}


// методы таблицы
function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
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
	const statusToView = getStatusToView(item.status)
	return {
		...item,
		statusToView,
	}
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const status = rowNode.data.status

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

// коллбэк бля редактирования ячеек таблицы
function getValueForUpdate(fieldName, value) {
	if (!value) return null
	switch (fieldName) {
		case 'carrierContactDate':
			// return dateHelper.getISODateTime(value)
			return value
		case 'comment':
			return value.trim()
		case 'offeredRate':
			return value.trim()
		default:
			return null
	}
}

function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] }
	)
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


function getStatusToView(status) {
	switch (status) {
		case 10:
			return 'Создана'
		case 20:
			return 'Обработана'
		case 30:
			return 'Отклонена'
		default:
			return `Неизвестный статус (${status})`
	}
}

function stringifyListValueFormatter(params) {
	const value = params.value
	if (!value) return ''
	return value.split(',').filter(Boolean).join(', ')
}