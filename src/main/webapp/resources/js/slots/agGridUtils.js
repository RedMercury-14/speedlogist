import { AG_GRID_LOCALE_RU } from "../AG-Grid/ag-grid-locale-RU.js"
import { dateComparator } from "../AG-Grid/ag-grid-utils.js"
import { dateHelper, rowClassRules } from "../utils.js"
import { convertToDayMonthTime } from "./dataUtils.js"

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
		const min = Number(order.pall) * 7
		const ms = min * 60 * 1000
		const start = new Date(order.timeDelivery)
		const end = new Date(start.getTime() + ms)
		timeDeliveryInfo =  `Старт: ${convertToDayMonthTime(order.timeDelivery)} \n Конец: ${convertToDayMonthTime(end)}`
	} else {
		timeDeliveryInfo = ''
	}

	return {
		...order,
		idRamp,
		dateDeliveryToView,
		timeDeliveryInfo
	}
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
