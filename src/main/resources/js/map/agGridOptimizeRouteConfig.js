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
	cell('Вес, кг', 'header'),
	cell('Расстояние, км', 'header'),
	cell('Машина', 'header'),
	cell('Паллеты/Вес', 'header'),
]
const getExcelRows = (params) => {
	const vehicle = params.node.data.vehicle
	const truckName = vehicle ? vehicle.name : ''
	const pallWeight = vehicle ? `${vehicle.pall}/${vehicle.weigth}` : ''
	const isEmptyShops = params.node.data.id === "Незавершенные"
	const rows = params.node.data.points.map((point, i) => {
		const weight = isEmptyShops
			? point.weight ? point.weight : 0
			: point.endShop.weight ? point.endShop.weight : 0

		return ({
				cells: [
					cell((i === 0 ? params.node.data.id : ''), 'body'),
					cell(point.numshop, 'body'),
					cell(point.address, 'body'),
					cell((point.needPall ? point.needPall : 0), 'body'),
					cell((weight), 'body'),
					cell(point.distanceToView ? point.distanceToView : '', 'body'),
					cell((i === 0 ? truckName : ''), 'body'),
					cell((i === 0 ? pallWeight : ''), 'body'),
				]
			})
	})
	
	return rows
}
const defaultExcelExportParams = {
	getCustomContentBelowRow: (params) => getExcelRows(params),
	processCellCallback: (params) => '',
	columnWidth: 120,
	fileName: 'optimization.xlsx',
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
		cellClass: 'group-cell', flex: 3,
		cellRenderer: 'agGroupCellRenderer',
	},
	{
		headerName: 'Машина', field: 'vehicle', flex: 4,
		valueGetter: params => params.data.vehicle ? params.data.vehicle.name : null,
	},
	{
		headerName: 'Загружено', field: 'vehicle',
		valueGetter: params => params.data.vehicle ? params.data.vehicle.targetPall : null,
		cellClassRules: { 'green-cell': params => params.data.vehicle && params.data.vehicle.targetPall === params.data.vehicle.pall }
	},
	{
		headerName: 'Всего палл.', field: 'vehicle',
		valueGetter: params => params.data.vehicle ? params.data.vehicle.pall : null,
		cellClassRules: { 'green-cell': params => params.data.vehicle && params.data.vehicle.targetPall === params.data.vehicle.pall }
	},
	{ headerName: 'Расст.', field: 'fullDistance', },
	{ headerName: 'Вес', field: 'targetWeigth', },
	{
		headerName: 'Цвет', field: 'color', cellClass: 'p-0 text-center',
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
		cellClass: 'px-1 text-center',
		flex: 2,
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
				{ headerName: 'Адрес', field: 'address', flex: 8, },
				{ headerName: 'Палл.', field: 'needPall', cellClass: 'px-2 text-center', },
				{ headerName: 'Расст.', field: 'distanceToView', cellClass: 'px-2 text-center', },
			],
			defaultColDef: {
				headerClass: 'px-1',
				cellClass: 'px-2',
				flex: 2,
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
