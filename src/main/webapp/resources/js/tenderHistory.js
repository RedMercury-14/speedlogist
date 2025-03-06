import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { debounce, getData } from './utils.js';
import { dateComparator, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js';
import { getTenderHistoryUrl } from './globalConstants/urls.js';

const LOCAL_STORAGE_KEY = 'tenderHistory_page'

const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'greenRow': (params) => params.data.statusRoute === 'green',
	'redRow': (params) => params.data.statusRoute === 'red',
}

const columnDefs = [
	{
		headerName: "Дата загрузки", field: 'dateToView',
		cellClass: 'px-1 text-center', minWidth: 60,
		comparator: dateComparator,
		sort: 'desc'
	},
	{ 
		headerName: "Название тендера", field: "routeDirection",
		tooltipField: 'routeDirection',
		wrapText: true, autoHeight: true,
		flex: 7,
		cellRenderer: routeLinkRenderer,
		getQuickFilterText: params => {
			return params.value
		}
	},
	{
		headerName: "Статус", field: "statusRoute",
		cellClass: 'px-1 font-weight-bold text-center', minWidth: 80,
		valueGetter: (params) => getTenderStatus(params.data.statusRoute)
	},
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2 py-1',
		cellClass: 'px-1',
		flex: 1,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
	},
	onFilterChanged: debouncedSaveFilterState,
	suppressContextMenu: true,
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
}

window.onload = async () => {
	const gridDiv = document.querySelector('#myGrid')
	const history = await getData(getTenderHistoryUrl)
	
	renderTable(gridDiv, gridOptions, history)
	restoreFilterState()

	const filterTextBox = document.querySelector('#filterTextBox')
	filterTextBox.addEventListener('input', (e) => {
		gridOptions.api.setQuickFilter(e.target.value)
	})

	const resetTableFiltersBtn = document.querySelector('#resetTableFilters')
	resetTableFiltersBtn.addEventListener('click', resetFilterState)
}

function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function updateTable(gridOptions, data) {
	console.log('UPDATE TABLE')

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
	!data.length && gridOptions.api.showNoRowsOverlay()
}

function getMappingData(data) {
	return data
		.filter((tender) => tender.comments === "international")
		.map((tender) => {
			const dateToView = tender.dateLoadPreviously ? tender.dateLoadPreviously.split('-').reverse().join('.') : ''
			return {
				...tender,
				dateToView,
			}
		})
}

function routeLinkRenderer(params) {
	const dateReg = /\[(\d{1}|\d{2})-\d{2}-\d{4}\]/g
	
	let tenderName = params.value
	if (!dateReg.test(params.value)) {
		const index = params.value.lastIndexOf('N')
		const firstPart = params.value.slice(0, index)
		const secondPart = `[${params.data.simpleDateStart}] `
		const thirdPart = params.value.slice(index, params.value.length)

		tenderName = firstPart + secondPart + thirdPart
	}
	
	const idRoute = params.data.idRoute
	const link = `/speedlogist/main/carrier/tender/tenderpage?routeId=${idRoute}`
	return `<a class="text-primary" href="${link}">${tenderName}</a>`
}


function getTenderStatus(statusRoute) {
	switch (statusRoute) {
		case "green":
			return 'Выигран'
		case "red":
			return 'Проигран'
		case "white":
			return 'Торгуется'
		default:
			return 'Неизвестный статус'
	}
}


// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}
function resetFilterState() {
	gridFilterLocalState.resetState(gridOptions, LOCAL_STORAGE_KEY)
}