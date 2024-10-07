import { AG_GRID_LOCALE_RU } from '../../AG-Grid/ag-grid-locale-RU.js'
import { CargoCapacitySumStatusBarComponent, CountStatusBarComponent, PallSumStatusBarComponent } from './statusBar.js'

export const trucksColumnDefs = [
	{ headerName: 'id', field: 'idTGTruck', minWidth: 60, flex: 1, sort: 'desc', },
	{ headerName: 'Номер', field: 'numTruck', },
	// { headerName: 'Модель', field: 'modelTruck', width: 150, },
	{ headerName: 'Перевозчик', field: 'companyName', flex: 6, wrapText: true, autoHeight: true, },
	{ headerName: 'Тип', field: 'typeTrailer', },
	{ headerName: 'Паллеты', field: 'pall', flex: 2, },
	{ headerName: 'Тоннаж', field: 'cargoCapacity', flex: 2, },
]

export const trucksGridOptions = {
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-1 text-center',
		flex: 3,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	getRowId: (params) => params.data.idTGTruck,
	animateRows: true,
	rowSelection: 'multiple',
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	statusBar: {
		statusPanels: [
			{ statusPanel: CountStatusBarComponent, },
			{ statusPanel: PallSumStatusBarComponent, },
			{ statusPanel: CargoCapacitySumStatusBarComponent, },
		],
	},
}

export function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData)
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
export function getMappingData(data,) {
	return data.map(truck => {
		return { ...truck }
	})
}

// функции управления строками таблицы
export function addTableRow(gridOptions, rowData) {
	const rowId = rowData.idTGTruck
	gridOptions.api.applyTransactionAsync(
		{ add: [rowData] },
		() => flashCells(gridOptions, [rowId])
	)
}
export function removeTableRow(gridOptions, rowData) {
	const rowId = rowData.idTGTruck
	gridOptions.api.applyTransactionAsync(
		{ remove: [rowData] }
	)
}
export function updateTableRow(gridOptions, rowData) {
	const rowId = rowData.idTGTruck
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] },
		() => flashCells(gridOptions, [rowId])
	)
}
export function addTableRows(gridOptions, rowsData) {
	const rowIds = rowsData.map(row => row.idTGTruck)
	gridOptions.api.applyTransactionAsync(
		{ add: rowsData },
		() => flashCells(gridOptions, rowIds)
	)
}
export function removeTableRows(gridOptions, rowsData) {
	gridOptions.api.applyTransactionAsync(
		{ remove: rowsData }
	)
}

// выделение строки таблицына короткое время 
export function flashCells(gridOptions, rowIds) {
	const rowNodes = rowIds.map(id => gridOptions.api.getRowNode(id))
	gridOptions.api.flashCells({ rowNodes: rowNodes })
}

// получение данных таблицы
export function getTableData(gridOptions) {
	const data = []
	gridOptions.api.forEachNode(node => data.push(node.data))
	return data
}
