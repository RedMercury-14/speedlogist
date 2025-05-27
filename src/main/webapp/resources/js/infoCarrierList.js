import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { dateComparator, dateTimeValueFormatter, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData, } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { ajaxUtils } from './ajaxUtils.js'
import { getCarrierApplicationListUrl } from './globalConstants/urls.js'

const PAGE_NAME = 'infoCarrierList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'grey-row': params => params.data && params.data.status === 10,
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
	{ headerName: "Предлагаемый нам тариф", field: "offeredRate", },
	{ headerName: "Примечание (тарификация и прочая важная информация)", field: "notes", flex: 6, },
	{ headerName: "Выслать ссылку на регистрацию", field: "", },
	{
		headerName: "Статус заявки", field: "statusToView",
		cellClass: statusToViewClasses,
	},
	{
		headerName: "Дата связи с перевозчиком", field: "carrierContactDate",
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{ headerName: "Ответственный специалист ОТЛ", field: "", },
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
	// date_fromInput.value = dateStart
	// date_toInput.value = dateEnd

	const reviews = await getCarrierInfoData(dateStart, dateEnd)
	updateTable(gridOptions, reviews)

	// листнер на отправку формы поиска заявок
	searchDataForm.addEventListener('submit',searchFormSubmitHandler)

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
async function getCarrierInfoData(dateStart, dateEnd) {
	// const url = `${getCarrierApplicationListUrl}${dateStart}&${dateEnd}`
	const url = `${getCarrierApplicationListUrl}`
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
		statusToView
	}
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const status = rowNode.data.status

	const items = [
		// {
		// 	name: "Подтвердить",
		// 	disabled: isObserver(role) || status === 20,
		// 	action: () => {
		// 		confirmProductControl(rowNode.data)
		// 	},
		// 	icon: uiIcons.check,
		// },
		// {
		// 	name: "Отменить",
		// 	disabled: isObserver(role) || status === 30,
		// 	action: () => {
		// 		cancelProductControl(rowNode.data)
		// 	},
		// 	icon: uiIcons.trash,
		// },
		// {
		// 	name: "Редактировать",
		// 	disabled: (!isAdmin(role) && !isOrderSupport(role)),
		// 	action: () => {
		// 		openEditProductControlForm(rowNode.data)
		// 	},
		// 	icon: uiIcons.pencil,
		// },
		// "separator",
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

function statusToViewClasses(params) {
	const defaultClasses = 'px-2 text-center'
	if (!params) return defaultClasses
	const data = params.data
	if (!data) return defaultClasses
	if (data.status === 20) return `text-success font-weight-bold ${defaultClasses}`
	if (data.status === 30) return `text-danger font-weight-bold ${defaultClasses}`
	return defaultClasses
}

function stringifyListValueFormatter(params) {
	const value = params.value
	if (!value) return ''
	return value.split(',').filter(Boolean).join(', ')
}