import { AG_GRID_LOCALE_RU } from "../AG-Grid/ag-grid-locale-RU.js"
import { cell } from "../AG-Grid/ag-grid-utils.js"
import { inputColorRenderer } from "./agGridUtils.js"

// -------------------------------------------------------------------------------//
// -------------- Конфигурация таблицы оптимизации маршрутов ---------------------//
// -------------------------------------------------------------------------------//

const excelHeader = [
	cell('id', 'header'),
	cell('Номер', 'header'),
	cell('Адрес', 'header'),
	cell('Паллеты', 'header'),
	cell('Общий вес', 'header'),
	cell('Вес', 'header'),
	cell('Расстояние, км', 'header'),
]
const getExcelRows = (params) => {
	const routeResponse = params.node.data
	const rows = params.node.data.points.map((point, i) => {
		return ({
				cells: [
					cell((i === 0 ? params.node.data.id : ''), 'body'),
					cell(point.numshop, 'body'),
					cell(point.address, 'body'),
					cell((point.needPall ? point.needPall : 0), 'body'),
					cell(routeResponse.targetWeigth, 'body'),
					cell((point.endShop.weight ? point.endShop.weight : 0), 'body'),
					cell(point.distanceToView, 'body'),
				]
			})
	})
	return rows
}
const defaultExcelExportParams = {
	getCustomContentBelowRow: (params) => getExcelRows(params),
	processCellCallback: (params) => '',
	columnWidth: 120,
	fileName: 'ag-grid.xlsx',
	skipColumnHeaders: true,
	columnWidth: 120,
	prependContent: [
		{ cells: excelHeader },
	],
}
const defaultExcelStyles = [
	{
		id: "header",
		interior: {
			color: "#aaaaaa",
			pattern: "Solid",
		},
		alignment: {
			horizontal: "Center",
			vertical: "Center",
		},
	},
]

// столбцы таблицы
const optimizeRouteColumnDefs = [
	{ 
		headerName: 'ID', field: 'id',
		cellClass: 'group-cell',
		width: 30,
		flex: 1,
		cellRenderer: 'agGroupCellRenderer',
	},
	{
		headerName: 'Машина', field: 'vehicle', cellClass: 'text-center', width: 70,
		valueGetter: params => params.data.vehicle ? params.data.vehicle.name : null,
	},
	{
		headerName: 'Загружено', field: 'vehicle', cellClass: 'text-center',
		valueGetter: params => params.data.vehicle ? params.data.vehicle.targetPall : null,
		cellClassRules: { 'green-cell': params => params.data.vehicle && params.data.vehicle.targetPall === params.data.vehicle.pall }
	},
	{
		headerName: 'Всего палл.', field: 'vehicle', cellClass: 'text-center',
		valueGetter: params => params.data.vehicle ? params.data.vehicle.pall : null,
		cellClassRules: { 'green-cell': params => params.data.vehicle && params.data.vehicle.targetPall === params.data.vehicle.pall }
	},
	{ headerName: 'Расст.', field: 'fullDistance', cellClass: 'text-center', },
	{ headerName: 'Вес', field: 'targetWeigth', cellClass: 'text-center', },
	{
		headerName: 'Цвет', field: 'color', cellClass: 'p-0 text-center', width: 40,
		cellRenderer: inputColorRenderer,
	},
]

// настройки таблицы
const optimizeRouteConfig = {
	rowClassRules: {
		'red-row': (params) => params.data.id === "Незавершенные",
	},
	columnDefs: optimizeRouteColumnDefs,
	defaultColDef: {
		headerClass: 'px-1',
		cellClass: 'px-2',
		width: 60,
		resizable: true,
		suppressMenu: true,
		filter: false,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	enableRangeSelection: true,
	masterDetail: true,
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: {
			columnDefs: [
				{ headerName: '№', field: 'numshop', },
				{ headerName: 'Адрес', field: 'address', width: 220, },
				{ headerName: 'Палл.', field: 'needPall', cellClass: 'px-2 text-center', },
				{ headerName: 'Расст.', field: 'distanceToView', cellClass: 'px-2 text-center', },
			],
			defaultColDef: {
				headerClass: 'px-1',
				cellClass: 'px-2',
				width: 60,
				resizable: true,
				suppressMenu: true,
				filter: false,
				wrapText: true,
				autoHeight: true,
				wrapHeaderText: true,
				autoHeaderHeight: true,
			},
			enableBrowserTooltips: true,
			localeText: AG_GRID_LOCALE_RU,
		},
		getDetailRowData: (params) => {
			params.successCallback(params.data.points)
		},
	},
	defaultExcelExportParams: defaultExcelExportParams,
	excelStyles: defaultExcelStyles,
}

export default optimizeRouteConfig
