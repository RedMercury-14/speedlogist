import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { ResetStateToolPanel, dateComparator, dateValueFormatter, deselectAllCheckboxes, gridColumnLocalState, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js'
import { debounce, getData, dateHelper, getStatus, rowClassRules, isAdmin, isStockProcurement, isSlotsObserver, isObserver } from './utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { uiIcons } from './uiIcons.js'
import { checkCombineOrders, excelStyles, mapCallbackForProcurementControl, procurementExcelExportParams } from "./procurementControlUtils.js"
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { ajaxUtils } from './ajaxUtils.js'
import { getChangeOrderStatusBaseUrl, getOrderBaseUrl, getOrdersForStockProcurementBaseUrl, getOrdersHasCounterpartyUrl, setOrderLinkingUrl } from './globalConstants/urls.js'

const PAGE_NAME = 'ProcurementControl'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const role = document.querySelector('#role').value
const getSearchOrderBaseUrl = isStockProcurement(role) ? getOrdersForStockProcurementBaseUrl : getOrderBaseUrl

const columnDefs = [
	{
		field: '', colId: 'selectionRow',
		width: 30, lockPosition: 'left',
		pinned: 'left', lockPinned: true,
		checkboxSelection: true,
		suppressMovable: true, suppressMenu: true,
		resizable: false, sortable: false, filter: false,
	},
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
	{
		headerName: 'Дата создания заявки', field: 'dateCreate',
		valueFormatter: dateValueFormatter, comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: 'Дата загрузки (первая)', field: 'loadDateToView',
		valueFormatter: dateValueFormatter, comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: 'Дата выгрузки (последняя)', field: 'unloadDateToView',
		valueFormatter: dateValueFormatter, comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: 'Слот на выгрузку', field: 'timeDeliveryToView', },
	// { headerName: 'Дата и время выгрузки', field: 'unloadWindowToView', width: 200, },
	// { headerName: 'Продолжительность выгрузки', field: 'onloadTime', width: 200, },
	{ headerName: 'Тип маршрута', field: 'wayToView', },
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
	{ headerName: 'Начальная сумма заказа без НДС', field: 'marketOrderSumFirst', },
	{ headerName: 'Конечная сумма заказа с НДС', field: 'marketOrderSumFinal', },
	{ headerName: 'Связь', field: 'link', },
]

if (isAdmin(role)) {
	columnDefs.push({
		headerName: 'Изменения статуса', field: 'changeStatus',
		wrapText: true, autoHeight: true,
	})
}

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
	getRowId: (params) => params.data.idOrder,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	rowSelection: 'multiple',
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
		getDetailRowData: async (params) => {
			params.successCallback(params.data.addressesToView)
		},
	},
	defaultExcelExportParams: procurementExcelExportParams,
	excelStyles: excelStyles,
}

document.addEventListener('DOMContentLoaded', async () => {
	const orderSearchForm = document.querySelector('#orderSearchForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	// отображение стартовых данных
	if (window.initData) {
		initStartData()
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', () => {
			initStartData()
		})
	}

	// автозаполнение полей дат в форме поиска заявок
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit', async (e) => searchFormSubmitHandler(e))

	bootstrap5overlay.hideOverlay()
})

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// установка стартовых данных
function initStartData() {
	updateTable(gridOptions, window.initData)
	window.initData = null

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()
}

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

	gridOptions.api.setRowData(mappingData.sort((a,b) => b.idOrder - a.idOrder))
	gridOptions.api.hideOverlay()
}

function getMappingData(data) {
	return data.map(mapCallbackForProcurementControl)
}

function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return
	const idOrder = rowNode.data.idOrder
	const status = rowNode.data.status

	const selectedRows = params.api.getSelectedNodes()
	const selectedRowsData = selectedRows.map(rowNode => rowNode.data)
	const isVeryfyCombineOrders = !selectedRowsData.filter(order => order.status !== 20 && order.status !== 6).length

	const result = [
		{
			name: `Просмотреть заявку`,
			action: () => {
				showOrder(idOrder)
			},
			icon: uiIcons.fileText,
		},
		{
			disabled: isSlotsObserver(role),
			name: `Копировать заявку`,
			action: () => {
				copyOrder(idOrder)
			},
			icon: uiIcons.files,
		},
		{
			disabled: (status === 10 || status === 70) || isSlotsObserver(role) || isObserver(role),
			name: `Редактировать заявку`,
			action: () => {
				editOrder(idOrder)
			},
			icon: uiIcons.pencil,
		},
		{
			disabled: (status !== 6 && status !== 7 && status !== 20 && status !== 40) || isSlotsObserver(role) || isObserver(role),
			name: `Отменить заявку`,
			action: () => {
				deleteOrder(idOrder, rowNode)
			},
			icon: uiIcons.trash,
		},
		"separator",
		{
			disabled: selectedRowsData.length < 2 || !isVeryfyCombineOrders || isSlotsObserver(role),
			name: `Объединить выделенные заказы`,
			action: () => {
				const errorMessage = checkCombineOrders(selectedRowsData)
				if (errorMessage) {
					alert(errorMessage)
					deselectAllCheckboxes(gridOptions)
					return
				}

				const ids = selectedRowsData.map(order => order.idOrder)
				setOrderLinking(ids, selectedRows)
				deselectAllCheckboxes(gridOptions)
			},
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
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(`${getChangeOrderStatusBaseUrl}${idOrder}&${deleteStatus}`)
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Заявка удалена')
		rowNode.setDataValue('status', deleteStatus)
		rowNode.setDataValue('statusToView', deleteStatusText)
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}

// связывание заказов
function setOrderLinking(idOrderArray, selectedRows) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: setOrderLinkingUrl,
		token: token,
		data: idOrderArray,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				const orderLink = idOrderArray[0]
				selectedRows.forEach(rowNode => rowNode.setDataValue('link', orderLink))
				snackbar.show(`Заказы ${idOrderArray} объединены`)
				return
			}

			console.log(res)
			const message = res && res.message ? res.message : 'Неизвестная ошибка'
			snackbar.show(message)
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./orders/order?idOrder=${data.idOrder}`

	const isLinkedOrder = data.link
	const isLinkedOrderLabel = isLinkedOrder ? ' <span class="text-danger"> (объединен)</span>' : ''

	return `<a class="text-primary" href="${link}">${params.value}${isLinkedOrderLabel}</a>`
}

async function searchFormSubmitHandler(e) {
	e.preventDefault()
	gridOptions.api.showLoadingOverlay()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const dateStart = data.date_from
	const dateEnd = data.date_to
	const counterparty = data.searchName

	const orders = counterparty.length
		? await getData(`${getOrdersHasCounterpartyUrl}${dateStart}&${dateEnd}&${counterparty}`)
		: await getData(`${getSearchOrderBaseUrl}${dateStart}&${dateEnd}`)

	updateTable(gridOptions, orders)
	gridOptions.api.hideOverlay()
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
