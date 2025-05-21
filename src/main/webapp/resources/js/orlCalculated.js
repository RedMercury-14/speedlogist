import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { getPalletsCalculatedBaseUrl } from './globalConstants/urls.js'
import { uiIcons } from './uiIcons.js'
import { dateHelper, debounce, getData } from './utils.js'


const PAGE_NAME = 'orlCalculated'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const tableData = {
	default: [],
	smooth: [],
}

let pallLineChart
const pallChartConfig = {
	type: 'line',
	data: {
		datasets: [
			{
				label: '1700',
				fill: false,
				borderColor: '#1f77b4',
				pointRadius: 4,
				pointHoverRadius: 7,
				data: [],
				backgroundColor: '#1f77b4',
			},
			{
				label: '1800',
				fill: false,
				borderColor: '#ff7f0e',
				pointRadius: 4,
				pointHoverRadius: 7,
				data: [],
				backgroundColor: '#ff7f0e',
			}
		],
	},
	options: {
		responsive: true,
		plugins: {
			zoom: {
				zoom: { wheel: { enabled: true },mode: 'y', },
			},
		},
		scales: {
			x: { 
				beginAtZero: true,
				type: 'time',
				time: {
					unit: 'day',
					tooltipFormat: 'EEE, dd.MM',
					displayFormats: {
						day: 'EEE, dd.MM'
					}
				},
				adapters: {
					date: {
						locale: dateFnsLocaleRu
					}
				},
			},
			y: { beginAtZero: true, },
		},
	}
}

const columnDefs = [
	{
		headerName: 'Дата', field: 'deliveryDate',
		flex: 1,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: 'Наименование контрагента', field: 'counterpartyName', flex: 4, },
	{ headerName: 'Склад', field: 'numStock', },
	{ headerName: 'Паллеты', field: 'amountOfPallets', aggFunc: 'sum', },
	{ headerName: 'Код контрагента', field: 'counterpartyCode', flex: 2, },
	{ headerName: 'Номер контракта', field: 'counterpartyContractCode', flex: 2, },
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		flex: 1,
		minWidth: 100,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
		enableRowGroup: true,
	},

	autoGroupColumnDef: {
		minWidth: 150,
	},
	rowGroupPanelShow: 'always',

	animateRows: true,
	suppressDragLeaveHidesColumns: true,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	getContextMenuItems: getContextMenuItems,
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	},
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
}

document.addEventListener('DOMContentLoaded', async () => {
	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	gridOptions.api.showLoadingOverlay()
	restoreColumnState()

	// автозаполнение полей дат в форме поиска заявок
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 7)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// создание графика
	const ctx = document.getElementById('pallLineChart')
	ctx.width = window.innerWidth
	ctx.height = 250
	pallLineChart = new Chart(ctx, pallChartConfig)
	// сброс масштаба графика
	const resetChartZoomBtn = document.getElementById('resetZoom')
	resetChartZoomBtn.addEventListener('click', (e) => {
		pallLineChart.resetZoom()
		e.target.blur()
	})

	// смена типа форматирования данных графика
	const changeChartDataFormatSelect = document.getElementById('changeChartDataFormat')
	changeChartDataFormatSelect.addEventListener('change', (e) => {
		const dataFormatterType = e.target.value
		if (!dataFormatterType) return
		updateTable(gridOptions, tableData[dataFormatterType])
		updatePallChart(pallLineChart, tableData[dataFormatterType])
	})

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit',searchFormSubmitHandler)

	// отображение стартовых данных
	if (window.initData) {
		await initStartData()
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData()
		})
	}
})

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})


// установка стартовых данных
async function initStartData() {
	const pallets = window.initData.body ? window.initData.body : []
	tableData.default = pallets
	tableData.smooth = redistributePallets(pallets)
	updateTable(gridOptions, tableData.default)
	updatePallChart(pallLineChart, tableData.default)
	window.initData = null
}

// получение данных
async function getPalletsData(dateStart, dateEnd) {
	const url = `${getPalletsCalculatedBaseUrl}${dateStart}&${dateEnd}`
	const res = await getData(url)
	if (!res) return []
	return res.body ? res.body : []
}

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showNoRowsOverlay()
}
function updateTable(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(mapCallback)
}
function mapCallback(item) {
	return {
		...item,
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const result = [
		{
			name: `Сбросить настройки фильтров`,
			action: () => {
				gridFilterLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
			icon: uiIcons.cancel,
		},
		
		"separator",
		"excelExport",
	]

	return result
}

// обновление данных графика
function updatePallChart(pallLineChart, items) {
	if (!items || !items.length) return
	const groupedByStock = items.reduce((acc, item) => {
		let groupKey = item.numStock;
		if (!acc[groupKey]) {
			acc[groupKey] = []
		}
		acc[groupKey].push(item)
		return acc
	}, {})

	const chartData1700 = getFormattedPallChartData(groupedByStock[1700])
	const chartData1800 = getFormattedPallChartData(groupedByStock[1800])
	pallLineChart.data.datasets[0].data = chartData1700
	pallLineChart.data.datasets[1].data = chartData1800
	pallLineChart.update()
}
function getFormattedPallChartData(pallChartData) {
	if (!pallChartData) return []
	return pallChartData
		.sort((a, b) => a.deliveryDate - b.deliveryDate)
		.map(item => ({
			x: new Date(item.deliveryDate),
			y: item.amountOfPallets,
		}))
		.reduce((acc, curr) => {
			const existing = acc.find(item => item.x.getTime() === curr.x.getTime())
			if (existing) existing.y += curr.y
			else acc.push(curr)
			return acc
		}, [])
		
}

// обработчик отправки формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	gridOptions.api.showLoadingOverlay()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	const pallets = await getPalletsData(data.date_from, data.date_to)
	tableData.default = pallets
	tableData.smooth = redistributePallets(pallets)
	const changeChartDataFormatSelect = document.getElementById('changeChartDataFormat')
	const dataFormatterType = changeChartDataFormatSelect.value
	updateTable(gridOptions, tableData[dataFormatterType])
	updatePallChart(pallLineChart, tableData[dataFormatterType])
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

function dateValueFormatter(params) {
	let date = params.value
	if (!date) return ''
	if (!Date.parse(date)) date = Number(date)
	return dateHelper.getFormatDate(date)
}
function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	if (!Date.parse(date1)) date1 = Number(date1)
	if (!Date.parse(date2)) date2 = Number(date2)
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}

// пересчет паллет по алгоритму 66/33 паллет в день с группировкой по датам
function redistributePallets(data, defaultLogisticLeg = 7) {
	if (!data) return []

	const finalData = []
	const overflowQueue = []

	data.forEach(item => {
		let remainingPallets = item.amountOfPallets
		let currentDate = item.deliveryDate
		const logisticLeg = item.logisticLeg || defaultLogisticLeg
		const threshold = 66 + 33 * (logisticLeg - 1)
		const dailyLimit = remainingPallets > threshold ? 66 : 33

		while (remainingPallets > 66) {
			finalData.push({ ...item, amountOfPallets: 66, deliveryDate: currentDate })
			remainingPallets -= 66

			while (remainingPallets > 0) {
				currentDate -= 86400000
				const palletsToAdd = Math.min(dailyLimit, remainingPallets)
				overflowQueue.push({ ...item, amountOfPallets: palletsToAdd, deliveryDate: currentDate })
				remainingPallets -= palletsToAdd
			}
		}
		
		if (remainingPallets > 0) {
			finalData.push({ ...item, amountOfPallets: remainingPallets, deliveryDate: item.deliveryDate })
		}
	})

	return [...finalData, ...overflowQueue].sort((a, b) => b.deliveryDate - a.deliveryDate)
}