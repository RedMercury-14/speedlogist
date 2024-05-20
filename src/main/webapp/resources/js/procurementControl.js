import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js'
import { debounce, getData, dateHelper, getStatus, changeGridTableMarginTop, rowClassRules, isAdminByLogin, isAdmin, isStockProcurement } from './utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { uiIcons } from './uiIcons.js'
import { excelStyles, getPointToView, getRouteInfo, pointSorting, procurementExcelExportParams } from "./procurementControlUtils.js"

const PAGE_NAME = 'ProcurementControl'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const getDefaultOrderBaseUrl ='../../api/manager/getOrders/'
const getOrdersForStockProcurementBaseUrl ='../../api/manager/getOrdersForStockProcurement/'
const getSearchOrderBaseUrl ='../../api/manager/getOrdersHasCounterparty/'
const getChangeOrderStatusBaseUrl ='../../api/manager/changeOrderStatus/'

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const role = document.querySelector('#role').value
const getOrderBaseUrl = isStockProcurement(role) ? getOrdersForStockProcurementBaseUrl : getDefaultOrderBaseUrl

const columnDefs = [
	{ 
		headerName: 'ID заявки', field: 'idOrder', colId: 'idOrder',
		cellClass: 'group-cell', pinned: 'left', width: 100,
		cellRenderer: 'agGroupCellRenderer',
		cellRendererParams: {
			innerRenderer: orderLinkRenderer,
		},
	},
	{ 
		headerName: 'Наименование контрагента', field: 'counterparty', colId: 'counterparty',
		pinned: 'left', width: 240, wrapText: true, autoHeight: true,
	},
	{ headerName: 'Дата создания заявки', field: 'dateCreateToView', comparator: dateComparator, },
	{ headerName: 'Дата загрузки (первая)', field: 'loadDateToView', comparator: dateComparator, },
	{ headerName: 'Дата выгрузки (последняя)', field: 'unloadDateToView', comparator: dateComparator, },
	{ headerName: 'Слот на выгрузку', field: 'timeDeliveryToView', },
	// { headerName: 'Дата и время выгрузки', field: 'unloadWindowToView', width: 200, },
	// { headerName: 'Продолжительность выгрузки', field: 'onloadTime', width: 200, },
	{ headerName: 'Тип маршрута', field: 'way', },
	{ headerName: 'Номер из Маркета', field: 'marketNumber', },
	{ headerName: 'Погрузочный номер', field: 'loadNumber', },
	{ headerName: 'Условия поставки', field: 'incoterms', wrapText: true, autoHeight: true, },
	{ 
		headerName: 'Комментарии', field: 'comment', 
		width: 240, cellClass: 'font-weight-bold more-text',
		wrapText: true, autoHeight: true,
	},
	{ field: 'status', hide: true },
	{
		headerName: 'Статус', field: 'statusToView',
		wrapText: true, autoHeight: true,
	},
	{ headerName: 'Маршруты', field: 'routeInfo', wrapText: true, autoHeight: true,},
	{ headerName: 'Логист', field: 'logistToView', wrapText: true, autoHeight: true,},
	{ headerName: 'Контактное лицо контрагента', field: 'contact', wrapText: true, autoHeight: true, },
	{ headerName: 'Сверка УКЗ', field: 'controlToView', width: 100, },
	{ headerName: 'Создатель заявки', field: 'managerToView', wrapText: true, autoHeight: true,},
	{ headerName: 'Тип кузова', field: 'typeTruck', },
	{ headerName: 'Тип загрузки авто', field: 'typeLoad', },
	{ headerName: 'Способ загрузки авто', field: 'methodLoad', },
	{ headerName: 'Груз', field: 'cargo', },
	{ headerName: 'Температурные условия', field: 'temperature', },
	{ headerName: 'Штабелирование', field: 'stackingToView', },
	{ headerName: 'Склад доставки (из Маркета)', field: 'numStockDelivery', },
	{ headerName: 'Информация (из Маркета)', field: 'marketInfo', },

]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2 text-center',
		width: 160,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	getContextMenuItems: getContextMenuItems,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
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
	masterDetail: true,
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: {
			columnDefs: [
				{ headerName: "№", field: 'pointNumber', flex: 1, },
				{ headerName: "Тип точки", field: 'type', },
				{ headerName: "Дата", field: 'dateToView', },
				{
					headerName: "Время", field: 'time',
					valueFormatter: (params) => params.value && `${params.value.slice(0, 5)}`
				},
				{ headerName: "Коды ТН ВЭД", field: 'tnvd', flex: 6, },
				{ headerName: "Информация о грузе", field: 'info', tooltipField: 'info', flex: 8, },
				{ headerName: "Адрес склада", field: 'bodyAddress', flex: 8, },
				{ headerName: "Время работы", field: 'timeFrame', },
				{ headerName: "Контактное лицо", field: 'contact', flex: 6, },
				{ headerName: "Адрес таможни", field: 'customsAddress', flex: 8, },
			],
			defaultColDef: {
				headerClass: 'px-1',
				cellClass: 'px-2',
				wrapText: true,
				autoHeight: true,
				resizable: true,
				flex: 4,
				suppressMenu: true,
				wrapHeaderText: true,
				autoHeaderHeight: true,
			},
			enableBrowserTooltips: true,
			localeText: AG_GRID_LOCALE_RU,
		},
		getDetailRowData: (params) => {
			params.successCallback(params.data.addressesForTable);
		},
	},
	defaultExcelExportParams: procurementExcelExportParams,
	excelStyles: excelStyles,
}

window.onload = async () => {
	const orderSearchForm = document.querySelector('#orderSearchForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const gridDiv = document.querySelector('#myGrid')

	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
	const orders = await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)

	const role = document.querySelector("#role").value
	if (isAdmin(role)) {
		gridOptions.columnDefs.push({
			headerName: 'Изменения статуса', field: 'changeStatus',
			wrapText: true, autoHeight: true,
		})
	}

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	// отрисовка таблицы
	renderTable(gridDiv, gridOptions, orders)

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	// автозаполнение полей дат в форме поиска заявок
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit', async (e) => searchFormSubmitHandler(e))
}

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData.sort((a,b) => b.idOrder - a.idOrder))
	gridOptions.api.hideOverlay()
}
function updateTable(gridOptions, data) {
	console.log('UPDATE TABLE')

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData.sort((a,b) => b.idOrder - a.idOrder))
	gridOptions.api.hideOverlay()
}

function getMappingData(data) {
	return data.map(order => {
		const dateCreateToView = dateHelper.getFormatDate(order.dateCreate)
		const dateDeliveryToView = dateHelper.getFormatDate(order.dateDelivery)
		const controlToView = order.control ? 'Да' : 'Нет'
		const telephoneManagerToView = order.telephoneManager ? `; тел. ${order.telephoneManager}` : ''
		const managerToView = `${order.manager}${telephoneManagerToView}`
		const statusToView = getStatus(order.status)
		const stackingToView = order.stacking ? 'Да' : 'Нет'
		const logistToView = order.logist && order.logistTelephone
			? `${order.logist}, тел. ${order.logistTelephone}`
			: order.logist
				? order.logist
				: order.logistTelephone
					? order.logistTelephone : ''

		const unloadWindowToView = order.onloadWindowDate && order.onloadWindowTime
			? `${dateHelper.getFormatDate(order.onloadWindowDate)} ${order.onloadWindowTime.slice(0, 5)}`
			: ''
		
		const addressesForTable = order.addressesToView ? order.addressesToView : order.addresses
			.sort(pointSorting)
			.map(getPointToView)
		
		const loadDateToView = addressesForTable.length ? addressesForTable[0].dateToView : ''

		const unloadPointsArr = addressesForTable.length
			? addressesForTable
				.filter(point => point.type === 'Выгрузка')
				.sort((a, b) => b.date - a.date)
			: []

		const unloadDateToView = unloadPointsArr.length ? unloadPointsArr[0].dateToView : ''

		const routeInfo = getRouteInfo(order)

		const timeDeliveryToView = order.timeDelivery ? convertToDayMonthTime(order.timeDelivery) : ''

		return {
			...order,
			dateCreateToView,
			dateDeliveryToView,
			controlToView,
			addressesForTable,
			loadDateToView,
			unloadDateToView,
			managerToView,
			unloadWindowToView,
			statusToView,
			stackingToView,
			logistToView,
			routeInfo,
			timeDeliveryToView,
		}
	})
}

function getContextMenuItems(params) {
	const idOrder = params.node.data.idOrder
	const status = params.node.data.status
	const rowNode = params.node

	const result = [
		{
			name: `Просмотреть заявку`,
			action: () => {
				showOrder(idOrder)
			},
			icon: uiIcons.fileText,
		},
		{
			name: `Копировать заявку`,
			action: () => {
				copyOrder(idOrder)
			},
			icon: uiIcons.files,
		},
		{
			name: `Редактировать заявку`,
			disabled: status === 10 || status === 70,
			action: () => {
				editOrder(idOrder)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Отменить заявку`,
			disabled: status === 10 || status === 70,
			action: () => {
				deleteOrder(idOrder, rowNode)
			},
			icon: uiIcons.trash,
		},
		"separator",
		"excelExport",
	]

	return result
}
function showOrder(idOrder) {
	window.location.href = `orders/order?idOrder=${idOrder}`
}
function copyOrder(idOrder) {
	window.location.href = `orders/copy?idOrder=${idOrder}`
}
function editOrder(idOrder) {
	window.location.href = `orders/edit?idOrder=${idOrder}`
}
async function deleteOrder(idOrder, rowNode) {
	const deleteStatus = 10
	const deleteStatusText = getStatus(deleteStatus)
	const res = await getData(`${getChangeOrderStatusBaseUrl}${idOrder}&${deleteStatus}`)

	if (res.status === '200') {
		snackbar.show('Заявка удалена')
		rowNode.setDataValue('status', deleteStatus)
		rowNode.setDataValue('statusToView', deleteStatusText)
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}

function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./orders/order?idOrder=${data.idOrder}`

	return `<a class="text-primary" href="${link}">${params.value}</a>`
}

async function searchFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const dateStart = data.date_from
	const dateEnd = data.date_to
	const counterparty = data.searchName

	const orders = counterparty.length
		? await getData(`${getSearchOrderBaseUrl}${dateStart}&${dateEnd}&${counterparty}`)
		: await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)

	updateTable(gridOptions, orders)
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

function convertToDayMonthTime(eventDateStr) {
	const date = new Date(eventDateStr)
	const formatter = new Intl.DateTimeFormat('ru', {
		day: '2-digit',
		month: 'long', 
		hour: '2-digit',
		minute: '2-digit'
	})
	return formatter.format(date)
}