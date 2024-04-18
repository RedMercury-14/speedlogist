import { AG_GRID_LOCALE_RU } from "../AG-Grid/ag-grid-locale-RU.js"

// -------------------------------------------------------------------------------//
// -------------- Конфигурация таблицы оптимизации маршрутов ---------------------//
// -------------------------------------------------------------------------------//

const distanceControlColumnDefs = [
	{ 
		headerName: 'ID', field: 'routeId',
		width: 30,
	},
	{ 
		headerName: '№', field: 'shopNum',
	},
	{ 
		headerName: 'Адрес', field: 'address',
		width: 240,
	},
	{ 
		headerName: 'Палл.', field: 'pall',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Расст.', field: 'distanceToView',
		cellClass: 'px-2 text-center',
	},
]

// настройки таблицы
const distanceControlConfig = {
	columnDefs: distanceControlColumnDefs,
	defaultColDef: {
		headerClass: 'px-2',
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
	suppressMultiRangeSelection: true,
}

export default distanceControlConfig
