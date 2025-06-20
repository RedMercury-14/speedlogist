import { getDefaultGridOptions } from '../../AG-Grid/ag-grid-utils.js'
import { CargoCapacitySumStatusBarComponent, CountStatusBarComponent, PallSumStatusBarComponent, RowLegengStatusBarComponent } from './statusBar.js'

const LOCAL_STORAGE_KEY = 'AG_Grid_settings_to_logisticksDelivery'

export const trucksColumnDefs = [
	{ headerName: 'id', field: 'idTGTruck', minWidth: 60, flex: 1, sort: 'desc', hide: true, },
	// { headerName: 'Номер', field: 'numTruck', flex: 2, },
	// { headerName: 'Контакты водителя', field: 'fio', flex: 4, wrapText: true, autoHeight: true, },
	// { headerName: 'Модель', field: 'modelTruck', width: 150, },
	{ headerName: 'Перевозчик', field: 'companyName', flex: 4, wrapText: true, autoHeight: true, },
	{ headerName: 'Тип', field: 'typeTrailer', flex: 2, },
	{ headerName: 'Тоннаж', field: 'cargoCapacity', flex: 1, cellClass: 'px-1 text-center font-weight-bold fs-1rem', },
	{ headerName: 'Паллеты', field: 'pall', flex: 1, cellClass: 'px-1 text-center font-weight-bold fs-1rem', },
	{ headerName: 'Склад загрузки', field: 'typeStock', flex: 2, cellClass: 'px-1 text-center', wrapText: false, autoHeight: false,},
	{ headerName: 'Доп. инф-я', field: 'otherInfo', flex: 4, wrapText: true, autoHeight: true,},
]


const defaultGridOptions = getDefaultGridOptions({
	localStorageKey: LOCAL_STORAGE_KEY,
	enableColumnStateSaving: true,
	enableFilterStateSaving: false,
})

export const trucksGridOptions = {
	...defaultGridOptions,
	defaultColDef: {
		...defaultGridOptions.defaultColDef,
		flex: 3,
		lockPinned: true,
		minWidth: 10,
	},
	rowClassRules: {
		'light-green-row': params => params.node.data.status === 50,
		'light-orange-row': params => params.node.data.secondRound,
	},
	getRowId: (params) => params.data.idTGTruck,
	rowSelection: 'multiple',
	suppressRowClickSelection: false,
	statusBar: {
		statusPanels: [
			{ statusPanel: RowLegengStatusBarComponent, align: 'left', },
			{ statusPanel: CountStatusBarComponent, statusPanelParams: null, },
			{ statusPanel: PallSumStatusBarComponent, statusPanelParams: null, },
			{ statusPanel: CargoCapacitySumStatusBarComponent, statusPanelParams: null, },
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

// установка стора в параметры строки состояния
export function setStoreInStatusPanel(gridOptions, store) {
	gridOptions.statusBar.statusPanels.forEach(item => {
		item.statusPanelParams = {
			...item.statusPanelParams,
			appStore: store,
		}
	})
}
