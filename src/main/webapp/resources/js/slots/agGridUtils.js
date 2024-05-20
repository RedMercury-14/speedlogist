import { AG_GRID_LOCALE_RU } from "../AG-Grid/ag-grid-locale-RU.js"
import { dateComparator } from "../AG-Grid/ag-grid-utils.js"
import { dateHelper, rowClassRules } from "../utils.js"
import { convertToDayMonthTime, getEndTime } from "./dataUtils.js"

const columnDefs = [
	{ headerName: "Дата доставки", field: "dateDeliveryToView", width: 80, comparator: dateComparator, },
	{ headerName: "ID", field: "idOrder", width: 100, },
	{ headerName: "Номер из Маркета", field: "marketNumber", width: 100, },
	{ headerName: "Статус", field: 'status', width: 60, },
	{ headerName: "Менеджер", field: "loginManager", },
	{ headerName: "Контрагент", field: "counterparty", width: 160, },
	{ headerName: "Паллеты", field: 'pall', width: 60, },
	{ headerName: "Груз", field: 'cargo', },
	{ headerName: "Длительность", field: 'timeUnload', width: 60, },
	{ headerName: "Рампа", field: "idRamp", width: 60, },
	{ headerName: "Время", field: "timeDeliveryInfo", width: 190, wrapText: true, autoHeight: true, },
	{ headerName: "Создатель заявки", field: "manager", width: 190, wrapText: true, autoHeight: true, },
	{ headerName: "Адрес загрузки", field: "loadAddress", width: 390, wrapText: true, autoHeight: true, },
	{
		headerName: "Информация (из Маркета)", field: "marketInfo",
		width: 190, wrapText: true, autoHeight: true,
		editable: true, cellEditor: 'agLargeTextCellEditor', cellEditorPopup: true, cellEditorPopupPosition: 'under',
	},
	{
		headerName: "Внутреннее перемещение", field: "isInternalMovement",
		width: 90, wrapText: true, autoHeight: true,
		valueGetter: (params) => params.data.isInternalMovement === 'true' ? 'Да': 'Нет',
	},
]

export const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: {
		...rowClassRules,
		'dark-turquoise-row': params => params.node.data.status === 20,
	},
	defaultColDef: {
		headerClass: 'px-1',
		cellClass: 'px-1 text-center',
		width: 120,
		wrapHeaderText: true,
		autoHeaderHeight: true,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
	},
	getRowId: (params) => params.data.idOrder,
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
}

export function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	gridOptions.api.setRowData(data)
}

export function updateTableData(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
}

function getMappingData(data) {
	return data
		.sort((a, b) => a.dateDelivery - b.dateDelivery)
		.map(mapCallback)
}

function mapCallback(order) {
	const dateDeliveryToView = dateHelper.getFormatDate(order.dateDelivery)
	const idRamp = order.idRamp ? `${order.idRamp}` : ''
	let timeDeliveryInfo

	if (order.timeDelivery) {
		const end = getEndTime(order.timeDelivery, order.timeUnload)
		timeDeliveryInfo =  `Старт: ${convertToDayMonthTime(order.timeDelivery)} \n Конец: ${convertToDayMonthTime(end)}`
	} else {
		timeDeliveryInfo = ''
	}

	const loadAddress = getLoadAddress(order)

	return {
		...order,
		idRamp,
		dateDeliveryToView,
		timeDeliveryInfo,
		loadAddress,
	}
}

function getLoadAddress(order) {
	if (!order || !order.addresses) return ''
	if (!order.addresses.length) return ''

	const loadPoint = order.addresses
		.sort((a,b) => a.date - b.date)
		.find(address => address.type === 'Загрузка')

	return loadPoint ? loadPoint.bodyAddress : ''
}

export async function updateTableRow(gridOptions, orderData) {
	const rowId = Number(orderData.idOrder)
	const rowNode = gridOptions.api.getRowNode(rowId)
	if (!rowNode) return
	const newOrder = mapCallback(orderData)
	const resultCallback = () => highlightRow(gridOptions, rowNode)
	gridOptions.api.applyTransactionAsync({ update: [newOrder] }, resultCallback)
}

// выделение ("мигание") строки с изменениями
function highlightRow(gridOptions, rowNode) {
	gridOptions.api.flashCells({ rowNodes: [rowNode] })
}

// функция обновления данных ячейки таблицы
export function updateCellData(rowId, columnName, newValue) {
	const rowNode = gridOptions.api.getRowNode(rowId)
	rowNode.setDataValue(columnName, newValue)
}

// отображение только внутренних перевозок
export function showInternalMovementOrders(gridOptions) {
	const isInternalMovementComponent = gridOptions.api.getFilterInstance('isInternalMovement')
	isInternalMovementComponent.setModel({ values: ['Да'] })
	gridOptions.api.onFilterChanged()
}
