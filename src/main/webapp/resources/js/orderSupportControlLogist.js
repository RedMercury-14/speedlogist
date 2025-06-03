import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import {
	MultiColumnFilterToolPanel,
	ResetStateToolPanel,
	dateComparator,
	gridColumnLocalState,
	gridFilterLocalState,
	highlightRow,
} from "./AG-Grid/ag-grid-utils.js"
import { debounce, getData, dateHelper, isAdmin, isOrderSupport, isObserver, } from './utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { blockManyProductsUrl, blockProductUrl, changeExceptionBaseUrl, getStockRemainderUrl, setMaxDayBaseUrl, setNewBalanceBaseUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'skuControl'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`


const cellClassRules = {
	"productSurplus": params => params.value
					&& params.value !== 0
					&& params.value >= params.data.dayMax
					&& params.value !== 9999,
	"productShortage": params => (params.value || params.value === 0)
					&& (params.value < params.data.dayMax / 2)
					&& params.value !== 9999,
	"normalDayCount": params => params.value
					&& params.value !== 0
					&& params.value > params.data.dayMax / 2
					|| params.value === 9999,
}

const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let productData

const columnDefs = [
	{
		headerName: "Дата отчета", field: "dateUnload",
		cellClass: "px-1 text-center font-weight-bold",
		comparator: dateComparator,
	},
	{ headerName: "ID", field: "idProduct", minWidth: 30, },
	{ headerName: "Код товара", field: "codeProduct", },
	{ headerName: 'Наименование товара', field: 'name', flex: 4, minWidth: 300, wrapText: true, autoHeight: true, },
	{ headerName: "Склад", field: "numStock", },
	{ headerName: "Группа товара", field: "group", flex: 3, minWidth: 300, tooltipField: 'group' },
	{ headerName: "Рейтинг", field: "rating", },
	{
		headerName: "Остаток РЦ+запасники (дней)", field: "balanceStockAndReserves",
		cellClass: 'px-2 text-center',
		cellClassRules: cellClassRules,
		valueGetter: (params) => 
			(params.data.balanceStockAndReserves1700 || params.data.balanceStockAndReserves1700 === 0)
			&& (params.data.balanceStockAndReserves1800 || params.data.balanceStockAndReserves1800 === 0)
				? ''
				: params.data.balanceStockAndReserves,
		// editable: isAdmin(role) || isOrderSupport(role),
		// onCellValueChanged: editBalanceStockAndReserves,
	},
	{
		headerName: "Остаток РЦ+запасники для 1700 (дней)", field: "balanceStockAndReserves1700",
		cellClass: 'px-2 text-center',
		cellClassRules: cellClassRules,
		// editable: isAdmin(role) || isOrderSupport(role),
		// onCellValueChanged: editBalanceStockAndReserves,
	},
	{
		headerName: "Остаток РЦ+запасники для 1800 (дней)", field: "balanceStockAndReserves1800",
		cellClass: 'px-2 text-center',
		cellClassRules: cellClassRules,
		// editable: isAdmin(role) || isOrderSupport(role),
		// onCellValueChanged: editBalanceStockAndReserves,
	},
	{ headerName: "Реализация расчётная в день для 1700", field: "calculatedPerDay1700", },
	{ headerName: "Реализация расчётная в день для 1800", field: "calculatedPerDay1800", },
	{ headerName: "Реализация расчётная в день (общая)", field: "сalculatedPerDay", },
	// {
	// 	headerName: "Мин. кол-во дней", field: "dayMax",
	// 	cellClass: "px-1 text-center font-weight-bold",
	// 	editable: isAdmin(role) || isOrderSupport(role),
	// 	onCellValueChanged: editDayMax,
	// },
	{
		headerName: "Исключение", field: "isException",
		cellClass: 'py-0 flex-center', minWidth: 30,
		editable: isAdmin(role) || isOrderSupport(role),
		onCellValueChanged: editIsExeption,
	},
	// { headerName: "Акция", field: "promotionsInfo", },
	{
		headerName: "Заблокирован?", field: "isBlock",
		cellDataType: false,
		cellClass: 'px-2 text-center text-danger font-weight-bold',
		valueFormatter: (params) => params.value ? "Заблокирован" : "",
		filterParams: { valueFormatter: (params) => params.value ? "Заблокирован" : "", },
	},
	{
		headerName: "Старт блокировки", field: "blockDateStart",
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: "Конец блокировки", field: "blockDateFinish",
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: "Дата обновления данных", field: "dateCreate", comparator: dateComparator,},
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: "px-1",
		cellClass: "px-1 text-center",
		flex: 1,
		minWidth: 100,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		resizable: true,
		suppressMenu: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idProduct,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	suppressDragLeaveHidesColumns: true,
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	},
	statusBar: {
		statusPanels: [
			{ statusPanel: 'agTotalAndFilteredRowCountComponent', align: 'left' },
		],
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
				id: 'customFilters',
				iconKey: 'filter',
				labelKey: 'customFilters',
				labelDefault: 'Мультифильтр',
				toolPanel: MultiColumnFilterToolPanel,
				toolPanelParams: {
					columnDefs: columnDefs,
				},
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
	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	blockProductForm.addEventListener('submit', blockProductFormSubmitHandler)

	$('#blockProductModal').on('hidden.bs.modal', (e) => {
		blockProductForm.reset()
	})

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

// установка стартовых данных
async function initStartData() {
	productData = window.initData
	await updateTable(gridOptions, productData)
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
async function updateTable(gridOptions, data) {
	gridOptions.api.showLoadingOverlay()

	productData = data
		? data
		: await getData(getStockRemainderUrl)

	if (!productData || !productData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = productData.map(getMappingItem)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return []

	const data = rowNode.data

	const result = [
		{
			disabled: isObserver(role),
			name: `Блокировать товар на период`,
			action: () => {
				blockProductForm.idProduct.value = data.codeProduct
				blockProductForm.codes.value = data.codeProduct
				blockProductForm.actionType.value = 'blockOneProduct'
				$('#blockProductModal').modal('show')
			},
		},
		{
			disabled: isObserver(role),
			name: `Блокировать товар (по отфильтрованным строкам таблицы)`,
			action: () => {
				const filteredRows = getFiltredAndSortedRows(params)
				const strCodes = filteredRows.map(item => item.codeProduct).join(', ')
				blockProductForm.codes.value = strCodes
				blockProductForm.actionType.value = 'blockManyProducts'
				$('#blockProductModal').modal('show')
				
			},
		},
		"separator",
		"excelExport",
	]

	return result
}

function getMappingItem(item) {
	const promotionsInfo = getPromotionsInfo(item)
	const nowDateMs = new Date().setHours(0, 0, 0, 0)
	const isBlock = item.blockDateStart && item.blockDateFinish
		? item.blockDateStart <= nowDateMs && item.blockDateFinish >= nowDateMs
		: false
	return {
		...item,
		dateUnload: dateHelper.getFormatDate(item.dateUnload),
		dateCreate: dateHelper.getFormatDate(item.dateCreate),
		dayMax: Number(item.dayMax),
		balanceStockAndReserves: Number(item.balanceStockAndReserves),
		promotionsInfo,
		isBlock,
	}
}
function getPromotionsInfo(product) {
	if (!product) return ''
	const promotionDateStart = product.promotionDateStart
	const promotionDateEnd = product.promotionDateEnd
	if (!promotionDateStart && !promotionDateEnd) return ''
	const promotionDateStartStr = promotionDateStart ? `С ${dateHelper.getFormatDate(promotionDateStart)} ` : ''
	const promotionDateEndStr = promotionDateEnd ? `По ${dateHelper.getFormatDate(promotionDateEnd)}` : ''
	return promotionDateStartStr + promotionDateEndStr
}

function getFiltredAndSortedRows(params) {
	const filteredData = [];
	const rowCount = params.api.getDisplayedRowCount();

	for (let i = 0; i < rowCount; i++) {
		const rowNode = params.api.getDisplayedRowAtIndex(i);
		filteredData.push(rowNode.data);
	}

	return filteredData
}

// блокировка продукта на период
async function blockProductFormSubmitHandler(e) {
	e.preventDefault()

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	if (new Date(data.dateStart).getTime() > new Date(data.dateFinish).getTime()) {
		snackbar.show('Дата начала блокировки не может быть больше даты её окончания!')
		return
	}

	const isBlockManyProducts = data.actionType === 'blockManyProducts'
	const url = isBlockManyProducts ? blockManyProductsUrl : blockProductUrl
	const payload = isBlockManyProducts
		? {
			codes: data.codes.split(',').map(code => Number(code.trim())),
			dateStart: data.dateStart,
			dateFinish: data.dateFinish,
		}
		: {
			idProduct: Number(data.idProduct),
			dateStart: data.dateStart,
			dateFinish: data.dateFinish,
		}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: url,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				res.message && snackbar.show(res.message)
				$('#blockProductModal').modal('hide')

				if (isBlockManyProducts) {
					await updateTable(gridOptions, null)
				} else {
					const updatedReview = getMappingItem(res.object)
					updateTableRow(gridOptions, updatedReview)
				}
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// обработчики изменения ячеек
function editBalanceStockAndReserves(agGridParams) {
	const codeProduct = agGridParams.data.codeProduct
	const newValue = agGridParams.newValue ? agGridParams.newValue : ''
	const oldValue = agGridParams.oldValue ? agGridParams.oldValue : ''

	if (!newValue) return
	if (newValue === oldValue) return

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: `${setNewBalanceBaseUrl}${codeProduct}&${newValue}`,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				
				return
			}

			// устанавливаем старое значение
			updateCellData(agGridParams, 'balanceStockAndReserves', oldValue)
		},
		errorCallback: () => {
			// устанавливаем старое значение
			updateCellData(agGridParams, 'balanceStockAndReserves', oldValue)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function editDayMax(agGridParams) {
	const data = agGridParams.data
	const codeProduct = data.codeProduct
	const numStock = data.numStock
	const newValue = agGridParams.newValue ? agGridParams.newValue : ''
	const oldValue = agGridParams.oldValue ? agGridParams.oldValue : ''

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: `${setMaxDayBaseUrl}${codeProduct}&${numStock}&${newValue}`,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				
				return
			}

			// устанавливаем старое значение
			updateCellData(agGridParams, 'dayMax', oldValue)
		},
		errorCallback: () => {
			// устанавливаем старое значение
			updateCellData(agGridParams, 'dayMax', oldValue)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function editIsExeption(agGridParams) {
	const data = agGridParams.data
	const codeProduct = data.codeProduct
	const numStock = data.numStock
	const oldValue = !agGridParams.newValue

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: `${changeExceptionBaseUrl}${codeProduct}&${numStock}`,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				
				return
			}

			// устанавливаем старое значение
			updateCellData(agGridParams, 'isException', oldValue)
		},
		errorCallback: () => {
			// устанавливаем старое значение
			updateCellData(agGridParams, 'isException', oldValue)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] }
	)
}

// обновление ячейки
function updateCellData(agGridParams, fieldName, oldValue) {
	const data = agGridParams.data
	data[fieldName] = oldValue
	const resultCallback = () => highlightRow(gridOptions, agGridParams.node)
	gridOptions.api.applyTransactionAsync({ update: [data] }, resultCallback)
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

// конверторы дат для таблицы
function dateValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDate(date)
}