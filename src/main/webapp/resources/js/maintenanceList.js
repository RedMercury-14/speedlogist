import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js"
import { changeGridTableMarginTop, dateHelper, debounce, getData, getRouteStatus } from "./utils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from "./uiIcons.js"
import { ajaxUtils } from "./ajaxUtils.js"

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'maintenanceList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const ROW_INDEX_KEY = `AG_Grid_rowIndex_to_${PAGE_NAME}`

const getAhoRouteBaseUrl = '../../api/procurement/getMaintenanceList/'

const addAhoRouteUrl = `../../api/procurement/maintenance/add`
const editAhoRouteUrl = `../../api/manager/maintenance/edit`

export const rowClassRules = {
	// 'finishRow': params => params.node.data.statusRoute === '4',
	// 'attentionRow': params => params.node.data.statusRoute === '0',
	// 'cancelRow': params => params.node.data.statusRoute === '5',
	// 'endRow': params => params.node.data.statusRoute === '6',
	// 'oncePersonRoute': params => params.node.data.statusRoute === '8',
	// 'activRow': params => params.node.data.offerCount !== 0,
	// 'savedRow': params => params.node.data.isSavedRow === true,
}

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let table

let error



const columnDefs = [
	{ headerName: 'ID', field: 'idRoute', minWidth: 60, width: 80, pinned: 'left',},
	// { headerName: 'Тип', field: 'simpleWay', minWidth: 50, width: 50, },
	{ headerName: 'Название маршрута', field: 'routeDirection', minWidth: 240, width: 640, wrapText: true, autoHeight: true, },
	// { headerName: 'Контрагент', field: 'counterparty', wrapText: true, autoHeight: true, },
	{ headerName: 'Дата загрузки', field: 'dateLoadPreviously', comparator: dateComparator, },
	{ headerName: 'Время загрузки', field: 'timeLoadPreviously', },
	{ headerName: 'Дата доставки', field: 'dateUnloadPreviouslyStock', comparator: dateComparator, },
	{ headerName: 'Время доставки', field: 'timeUnloadPreviouslyStock', },
	// { headerName: 'Дата и время выгрузки', field: 'unloadToView', wrapText: true, autoHeight: true, },
	// { headerName: 'Выставляемая стоимость', field: 'finishPriceToView', },
	// { headerName: 'Экономия', field: 'economy', },
	{ headerName: 'Перевозчик', field: 'carrier', wrapText: true, autoHeight: true, },
	// {
	// 	headerName: 'Номер машины / прицепа', field: 'truckInfo',
	// 	wrapText: true, autoHeight: true,
	// 	cellRenderer: truckInfoRenderer,
	// },
	// { headerName: 'Данные по водителю', field: 'driverInfo',  wrapText: true, autoHeight: true,},
	// { headerName: 'Заказчик', field: 'customer', wrapText: true, autoHeight: true, minWidth: 160, width: 160, },
	{ headerName: 'Паллеты', field: 'loadPallTotal', },
	{ headerName: 'Масса груза', field: 'cargoWeightTotal', },
	{ headerName: 'Тип транспорта', field: 'typeTrailer', },
	{ headerName: 'Информация о грузе', field: 'cargoInfo', },
	{ headerName: 'Информация о транспорте', field: 'truckInfo', },
	{ headerName: 'Маршрут', field: 'userComments', wrapText: true, autoHeight: true, minWidth: 240, width: 640, },
	// { headerName: 'Начальная стоимость перевозки', field: 'startRouteCostInfo', wrapText: true, autoHeight: true, },
	// {
	// 	headerName: 'Статус', field: 'statusRoute',
	// 	cellClass: 'px-2 text-center font-weight-bold',
	// 	minWidth: 160, width: 160,
	// 	wrapText: true, autoHeight: true,
	// 	valueGetter: params => getRouteStatus(params.data.statusRoute),
	// },
	// {
	// 	headerName: 'Предложения', field: 'offerCount',
	// 	minWidth: 160, width: 160,
	// 	wrapText: true, autoHeight: true,
	// 	cellRenderer: offerCountRenderer,
	// },
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2 text-center',
		width: 160,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	// номер маршрута используется как ID строки
	// в таблице для транзакций с изменениями
	getRowId: (params) => params.data.idRoute,
	// выделение строк и ячеек при изменении данных
	enableCellChangeFlash: true,

	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,

	// rowSelection: 'multiple',
	// suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	getContextMenuItems: getContextMenuItems,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
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
	}
}


window.addEventListener("load", async () => {
	// изменение отступа для таблицы
	changeGridTableMarginTop()

	const dateStart = '2024-07-10'
	const dateEnd = '2024-09-10'
	const routes = await getData(`${getAhoRouteBaseUrl}${dateStart}&${dateEnd}`)

	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	await renderTable(gridDiv, gridOptions, routes)

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	const addAhoRouteForm = document.querySelector('#addAhoRouteForm')
	const editAhoRouteForm = document.querySelector('#editAhoRouteForm')
	const addCarrierForm = document.querySelector('#addCarrierForm')

	addAhoRouteForm.addEventListener('submit', ahoRouteFormSubmitHandler)
	editAhoRouteForm.addEventListener('submit', ahoRouteFormSubmitHandler)
	addCarrierForm.addEventListener('submit', addCarrierSubmitHandler)

	$('#addAhoRouteModal').on('hide.bs.modal', (e) => addAhoRouteForm.reset())
	$('#editAhoRouteModal').on('hide.bs.modal', (e) => editAhoRouteForm.reset())
	$('#addCarrierModal').on('hide.bs.modal', (e) => addCarrierForm.reset())
	
	await addCarriersToSelect()
})

async function addCarriersToSelect() {
	const getAllCarrierUrl = `../../api/manager/getAllCarrier`
	const carriers = await getData(getAllCarrierUrl)
	const carrierSelect = document.querySelector('#carrier')
	carriers.forEach((carrier) => {
		const optionElement = document.createElement('option')
		optionElement.value = carrier.idUser
		optionElement.text = carrier.companyName
		carrierSelect.append(optionElement)
	})
	addSearchInSelectOptions(carrierSelect)
}


// отображение модального окна назначения перевозчика
function showAddCarrierModal() {
	$('#addCarrierModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
function hideAddCarrierModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#addCarrierModal').modal('hide')
}
// поиск в списке селекта
function addSearchInSelectOptions(select) {
	const container = select.parentElement
	const input = container.querySelector('#searchInOptions')
	if (!input) return
	const searchItems = select.querySelectorAll('option')

	input.addEventListener('input', function (e) {
		const target = e.target
		const val = target.value.trim().toUpperCase()
		const fragment = document.createDocumentFragment()

		if (!target.classList.contains('keyboard__key')) return

		for (const elem of searchItems) {
			elem.remove()

			if (val === '' || elem.textContent.toUpperCase().includes(val)) {
				fragment.append(elem)
			}
		}

		select.append(fragment)
	})
}

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})


// обработчик формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	updateTable()
}
function addCarrierSubmitHandler(e) {
	e.preventDefault()
	
	const formData = new FormData(e.target)
	
}

function ahoRouteFormSubmitHandler(e) {
	e.preventDefault()

	const formId = e.target.id
	const url = formId === 'addAhoRouteForm' ? addAhoRouteUrl : editAhoRouteUrl

	const formData = new FormData(e.target)
	const data = ahoRouteFormDataFormatter(formData)
	console.log("🚀 ~ editCarrierSubmitHandler ~ data:", data)

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	ajaxUtils.postJSONdata({
		url: url,
		token: token,
		data: data,
		successCallback: (res) => {
			console.log(res)
			// snackbar.show(res.message)
			// $(`#addShopModal`).modal('hide')
		}
	})
}

function ahoRouteFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	return data
}

// -------------------------------------------------------------------------------//
// ----------------------- Функции для таблицы AG-Grid ---------------------------//
// -------------------------------------------------------------------------------//

async function renderTable(gridDiv, gridOptions, data) {
	table = new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = await getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const routeSearchForm = document.querySelector('#routeSearchForm')

	const dateStart = '2024-07-10'
	const dateEnd = '2024-09-10'

	const routes = await getData(`${getAhoRouteBaseUrl}${dateStart}&${dateEnd}`)

	if (!routes || !routes.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = await getMappingData(routes)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function getMappingData(data) {
	return data.map(route => {
		const idRoute = route.idRoute

		// const unloadToView = getUnloadToView(route)
		// const finishPriceToView = getFinishPriceToView(route)
		// const economy = getEconomy(route)
		// const carrier = getCarrier(route)
		// const truckOwner = getTruckOwner(route)
		// const truckInfo = getTruckInfo(route)
		// const driverInfo = getDriverInfo(route)
		// const cargoInfo = getCargoInfo(route)
		// const startRouteCostInfo = getStartRouteCostInfo(route)

		// const counterparty = getCounterparty(route)



		return {
			...route,
			// offerCount,
			// isSavedRow,
			// unloadToView,
			// finishPriceToView,
			// economy,
			// carrier,
			// truckOwner,
			// truckInfo,
			// driverInfo,
			// cargoInfo,
			// startRouteCostInfo,
			// statusRouteToView,
			// counterparty,
		}
	})
}

function getContextMenuItems(params) {
	if (!params.node) return

	const routeData = params.node.data
	const idRoute = routeData.idRoute
	const routeDirection = routeData.routeDirection
	const status = routeData.statusRoute

	const selectedRowsData = params.api.getSelectedRows()
	const isVerifySelectedRoutes = !selectedRowsData.filter(route => route.statusRoute !== '0').length

	const result = [
		{
			name: `Редактировать заявку`,
			// icon: uiIcons.offer,
			action: () => {
				// addCarrier(routeData)
			},
		},
		{
			name: `Удалить заявку`,
			// icon: uiIcons.offer,
			action: () => {
				// addCarrier(routeData)
			},
		},
		{
			name: `Назначить перевозчика`,
			// icon: uiIcons.offer,
			action: () => {
				addCarrier(routeData)
			},
		},
		{
			name: `Удалить перевозчика`,
			// icon: uiIcons.offer,
			action: () => {
				// addCarrier(routeData)
			},
		},
		{
			name: `Указать пробег`,
			// icon: uiIcons.offer,
			action: () => {
				// addCarrier(routeData)
			},
		},
		{
			name: `Закрыть маршрут`,
			// icon: uiIcons.offer,
			action: () => {
				// addCarrier(routeData)
			},
		},
		
		"separator",
		"excelExport",
	]

	return result
}

function addCarrier(routeData) {
	const addCarrierForm = document.querySelector('#addCarrierForm')
	addCarrierForm.idRoute.value = routeData.idRoute
	addCarrierForm.routeDirection.value = routeData.routeDirection
	showAddCarrierModal()
}

// функция обновления данных ячейки таблицы
function updateCellData(id, columnName, newValue) {
	const rowNode = gridOptions.api.getRowNode(id)
	rowNode.setDataValue(columnName, newValue)
}

// выделение ("мигание") строки с изменениями
function highlightRow(rowNode) {
	gridOptions.api.flashCells({ rowNodes: [rowNode] })
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





// функции получения данных для таблицы
function getUnloadToView(route) {
	if (!route) return ''
	const dateUnload = route.dateUnloadPreviouslyStock ? route.dateUnloadPreviouslyStock : ''
	const timeUnload = route.timeUnloadPreviouslyStock ? route.timeUnloadPreviouslyStock.slice(0,5) : ''
	return `${dateUnload} ${timeUnload}`
}
function getFinishPriceToView(route) {
	if (!route) return ''
	const finishPrice = route.finishPrice ? route.finishPrice : ''
	const currency = route.startCurrency ? route.startCurrency : ''
	const expeditionCost = route.expeditionCost ? route.expeditionCost : ''

	const res = expeditionCost
		? `${finishPrice} ${currency} (${expeditionCost} ${currency})`
		: `${finishPrice} ${currency}`

	return res
}
function getEconomy(route) {
	return ''
}
function getCarrier(route) {
	if (!route) return ''
	const driver = route.driver ? route.driver : ''
	if (!driver) return ''
	return driver.companyName ? driver.companyName : ''
}
function getTruckOwner(route) {
	if (!route) return ''
	const truck = route.truck ? route.truck : ''
	if (!truck) return ''
	return truck.ownerTruck ? truck.ownerTruck : ''
}
function getTruckInfo(route) {
	if (!route) return ''
	const truck = route.truck ? route.truck : ''
	if (!truck) return ''
	const numTruck = truck.numTruck ? truck.numTruck : ''
	const numTrailer = truck.numTrailer ? truck.numTrailer : ''
	const typeTrailer = truck.typeTrailer ? truck.typeTrailer : ''
	return `${numTruck} / ${numTrailer},  ${typeTrailer}`
}
function getDriverInfo(route) {
	if (!route) return ''
	const driver = route.driver ? route.driver : ''
	if (!driver) return ''
	const name = driver.name ? driver.name : ''
	const surname = driver.surname ? driver.surname : ''
	const patronymic = driver.patronymic ? driver.patronymic : ''
	return `${surname} ${name} ${patronymic}`
}
function getCargoInfo(route) {
	if (!route) return ''
	const rhsItem = route.roteHasShop[0]
	if (!rhsItem) return ''
	const pall = route.totalLoadPall ? route.totalLoadPall : ''
	const volume = rhsItem.volume ? rhsItem.volume : ''
	return `${pall} / ${volume}`
}
function getStartRouteCostInfo(route) {
	if (!route) return ''
	const currency = route.startCurrency ? route.startCurrency : 'BYN'
	const startPrice = route.startPrice ? route.startPrice : ''
	const optimalCost = route.optimalCost ? route.optimalCost : ''

	const res = startPrice
		? `${startPrice} ${currency}`
		: optimalCost
			? `${optimalCost} ${currency} - оптимальная`
			: ''

	return res
}
function getCounterparty(route) {
	if (!route) return ''
	const routeDirection = route.routeDirection ? route.routeDirection : ''
	const array = routeDirection.split('>')
	if (array.length < 2) return ''
	const counterparty = array[0].replace('<', '')
	return counterparty
}