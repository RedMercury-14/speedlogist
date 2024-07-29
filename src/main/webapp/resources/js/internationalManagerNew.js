import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js"
import { changeGridTableMarginTop, dateHelper, debounce, getData, getRouteStatus } from "./utils.js"
import { ws } from './global.js'
import { wsHead } from './global.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from "./uiIcons.js"

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'internationalManagerNew'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const ROW_INDEX_KEY = `AG_Grid_rowIndex_to_${PAGE_NAME}`

const getRouteBaseUrl = '../../api/manager/getRouteForInternational/'
const getRouteMessageBaseUrl = `../../api/info/message/numroute/`

export const rowClassRules = {
	'finishRow': params => params.node.data.statusRoute === '4',
	'attentionRow': params => params.node.data.statusRoute === '0',
	'cancelRow': params => params.node.data.statusRoute === '5',
	'endRow': params => params.node.data.statusRoute === '6',
	'oncePersonRoute': params => params.node.data.statusRoute === '8',
	'activRow': params => params.node.data.offerCount !== 0,
	'savedRow': params => params.node.data.isSavedRow === true,
}

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let table

const columnDefs = [
	// {
	// 	field: '', colId: 'selectionRow',
	// 	width: 30,
	// 	pinned: 'left', lockPinned: true,
	// 	checkboxSelection: true,
	// 	suppressMovable: true, suppressMenu: true,
	// 	resizable: false, sortable: false, filter: false,
	// },
	{ headerName: 'ID', field: 'idRoute', minWidth: 60, width: 80, pinned: 'left',},
	{ headerName: 'Тип', field: 'simpleWay', minWidth: 50, width: 50, },
	{ headerName: 'Название маршрута', field: 'routeDirection', minWidth: 240, width: 640, wrapText: true, autoHeight: true, },
	{ headerName: 'Контрагент', field: 'counterparty', wrapText: true, autoHeight: true, },
	{ headerName: 'Дата загрузки', field: 'simpleDateStart', comparator: dateComparator, },
	{ headerName: 'Время загрузки (планируемое)', field: 'timeLoadPreviously', },
	{ headerName: 'Дата и время выгрузки', field: 'unloadToView', wrapText: true, autoHeight: true, },
	{ headerName: 'Выставляемая стоимость', field: 'finishPriceToView', },
	// { headerName: 'Экономия', field: 'economy', },
	{ headerName: 'Перевозчик', field: 'carrier', wrapText: true, autoHeight: true, },
	{
		headerName: 'Номер машины / прицепа', field: 'truckInfo',
		wrapText: true, autoHeight: true,
		cellRenderer: truckInfoRenderer,
	},
	{ headerName: 'Данные по водителю', field: 'driverInfo',  wrapText: true, autoHeight: true,},
	{ headerName: 'Заказчик', field: 'customer', wrapText: true, autoHeight: true, minWidth: 160, width: 160, },
	{ headerName: 'Паллеты/Объем', field: 'cargoInfo', },
	{ headerName: 'Общий вес', field: 'totalCargoWeight', valueFormatter: params => params.value + ' кг' },
	{ headerName: 'Комментарии', field: 'userComments', wrapText: true, autoHeight: true, minWidth: 240, width: 640, },
	{ headerName: 'Начальная стоимость перевозки', field: 'startRouteCostInfo', wrapText: true, autoHeight: true, },
	{
		headerName: 'Статус', field: 'statusRoute',
		cellClass: 'px-2 text-center font-weight-bold',
		minWidth: 160, width: 160,
		wrapText: true, autoHeight: true,
		valueGetter: params => getRouteStatus(params.data.statusRoute),
	},
	{
		headerName: 'Предложения', field: 'offerCount',
		minWidth: 160, width: 160,
		wrapText: true, autoHeight: true,
		cellRenderer: offerCountRenderer,
	},
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

	// отображение сохраненной строки таблицы
	onRowDataUpdated: event => {
		const rowNode = displaySavedRowId(event, ROW_INDEX_KEY)
		// отображаем строку ещё раз после установки ширины строк
		setTimeout(() => {
			event.api.ensureNodeVisible(rowNode, 'top')
		}, 300)
	},

	rowSelection: 'multiple',
	suppressRowClickSelection: true,
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
	const routeSearchForm = document.querySelector('#routeSearchForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const gridDiv = document.querySelector('#myGrid')
	gridDiv.addEventListener('click', gridTableClickHandler)

	const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)

	// автозаполнение полей дат в форме поиска заявок
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	routeSearchForm.addEventListener('submit', searchFormSubmitHandler)

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	const routes = await getData(`${getRouteBaseUrl}${dateStart}&${dateEnd}`)

	// отрисовка таблицы
	await renderTable(gridDiv, gridOptions, routes)

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	// обработчик получения сообщений о предложениях
	ws.onmessage = onMessageHandler
})


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

// обработчик сообщений WebSocket
async function onMessageHandler(e) {
	const message = JSON.parse(e.data)
	if (!message) return

	// обновляем количество предложений
	if (message.idRoute !== null) {
		const idRoute = +message.idRoute
		updateOfferCount(idRoute)
	}
}

function sendHeadMessage(message) {
	wsHead.send(JSON.stringify(message))
}


// обработчик кликов в таблице
function gridTableClickHandler(e) {
	const target = e.target

	if (target.id === 'tenderOfferLink') {
		e.preventDefault()
		const idRoute = target.dataset.idroute
		const status = target.dataset.status
		displayTenderOffer(idRoute, status)
		return
	}

	if (target.id === 'truckInfoLink') {
		e.preventDefault()
		const idRoute = target.dataset.idroute
		const rowNode = gridOptions.api.getRowNode(idRoute)
		const route = rowNode.data
		showRouteInfoPopup(route)
		return
	}
}

// отображение модального окна с информацией об ивенте
function showRouteInfoPopup(route) {
	const routeInfo = document.querySelector('#routeInfo')
	routeInfo.innerHTML = createRouteInfoHTML(route)

	$('#routeInfoModal').modal('show')
}
function createRouteInfoHTML(route) {
	const {
		routeDirection,
		truck,
		truckInfo,
		truckOwner,
		carrier,
		driver,
		driverInfo,
		dateLoadActuallySimple,
		timeLoadActually,
		dateUnloadActuallySimple,
		timeUnloadActually,
		finishPrice,
		startCurrency,
	} = route

	const brandTruck = truck ? truck.brandTruck : ''
	const brandTrailer = truck ? truck.brandTrailer : ''
	const numPass = driver ? driver.numPass : ''
	const telephone = driver ? driver.telephone : ''
	const timeLoadActuallyToView = timeLoadActually ? timeLoadActually.replace('-', ':') : ''
	const timeUnloadActuallyToView = timeUnloadActually ? timeUnloadActually.replace('-', ':') : ''

	return `
			<h5>${routeDirection}</h5>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Перевозчик: </span>
				<span>${carrier}</span>
			</div>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Подвижной состав: </span>
				<span>${truckInfo},</span>
				<span>${brandTruck}</span>
				<span> / </span>
				<span>${brandTrailer}</span>
			</div>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Принадлежность транспорта: </span>
				<span>${truckOwner}</span>
			</div>
			<h6 class="mt-4 mb-1">Данные по водителю: </h6>
			<p class="mb-2">${driverInfo}</p>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Паспортные данные: </span>
				<span>${numPass}</span>
			</div>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Номер телефона: </span>
				<span>${telephone}</span>
			</div>
			<br />
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Дата подачи машины на загрузку: </span>
				<span>${dateLoadActuallySimple}; </span>
				<span class="font-weight-bold">время: </span>
				<span>${timeLoadActuallyToView}</span>
			</div>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Дата прибытия авто под выгрузку: </span>
				<span>${dateUnloadActuallySimple}; </span>
				<span class="font-weight-bold">время: </span>
				<span>${timeUnloadActuallyToView}</span>
			</div>
			<div class="routeInfo-item text-muted mb-2">
				<span class="font-weight-bold">Стоимость перевозки: </span>
				<span>${finishPrice} ${startCurrency}</span>
			</div>
		`
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

	const dateStart = routeSearchForm.date_from.value
	const dateEnd = routeSearchForm.date_to.value

	const routes = await getData(`${getRouteBaseUrl}${dateStart}&${dateEnd}`)

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
	return await Promise.all(data.map( async (route) => {
		const idRoute = route.idRoute

		const unloadToView = getUnloadToView(route)
		const finishPriceToView = getFinishPriceToView(route)
		const economy = getEconomy(route)
		const carrier = getCarrier(route)
		const truckOwner = getTruckOwner(route)
		const truckInfo = getTruckInfo(route)
		const driverInfo = getDriverInfo(route)
		const cargoInfo = getCargoInfo(route)
		const startRouteCostInfo = getStartRouteCostInfo(route)
		const statusRouteToView = getRouteStatus(route.statusRoute)

		const counterparty = getCounterparty(route)

		const offerCount = await getData(getRouteMessageBaseUrl + idRoute)

		const isSavedRow = false

		return {
			...route,
			offerCount,
			isSavedRow,
			unloadToView,
			finishPriceToView,
			economy,
			carrier,
			truckOwner,
			truckInfo,
			driverInfo,
			cargoInfo,
			startRouteCostInfo,
			statusRouteToView,
			counterparty,
		}
	}))
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
			name: `Истоpия предложений`,
			icon: uiIcons.offer,
			action: () => {
				displayTenderOffer(idRoute)
			},
		},
		{
			name: `Отправить тендер`,
			disabled: status !== '0',
			action: () => {
				sendTender(idRoute, routeDirection)
			},
		},
		{
			name: `Отправить выделенные тендеры`,
			// disabled: !selectedRowsData.length || !isVerifySelectedRoutes,
			disabled: true,
			action: () => {
				// ВРЕМЕННОЕ РЕШЕНИЕ ПО МАССОВОЙ ОТПРАВКЕ ТЕНДЕРОВ
				Promise.allSettled(selectedRowsData.map(route => {
					sendTender(route.idRoute, route.routeDirection)
				}))
			},
		},
		{
			name: `Показать точки выгрузок`,
			action: () => {
				showUnloadPoints(idRoute)
			},
		},
		{
			name: `Редактировать маршрут`,
			icon: uiIcons.pencil,
			action: () => {
				editRoute(idRoute)
			},
		},
		{
			name: `Завершить маршрут`,
			disabled: status !== '4',
			icon: uiIcons.checkObject,
			action: () => {
				completeRoute(idRoute)
			},
		},
		{
			name: `Отменить тендер`,
			disabled: status === '5',
			icon: uiIcons.cancel,
			action: () => {
				cancelTender(idRoute)
			},
		},
		"separator",
		"excelExport",
	]

	return result
}

// рендерер информации о машине
function truckInfoRenderer(params) {
	const data = params.node.data
	const idRoute = data.idRoute
	const truckInfo = data.truckInfo
	const truckInfoHTML = `<a class="text-primary" data-idroute="${idRoute}" id="truckInfoLink" href="">${truckInfo}</a>`
	return truckInfoHTML
}

// рендерер статуса и предложений маршрута
function tenderStatusRenderer(params) {
	const data = params.node.data
	const idRoute = data.idRoute
	const offerCount = data.offerCount
	const status = data.statusRoute
	const statusText = getRouteStatus(status)

	if (status === '8') {
		const link = `../admin/internationalNew/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">Посмотреть предложение</a>`
		return `${statusText} ${linkHTML}`
	} else if (status === '1') {
		const link = `./internationalNew/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">Посмотреть предложения (${offerCount})</a>`
		return `${statusText} ${linkHTML}`
	} else {
		return statusText
	}
}

// рендерер количества предложений
function offerCountRenderer(params) {
	const data = params.node.data
	const idRoute = data.idRoute
	const offerCount = data.offerCount
	const status = data.statusRoute

	if (status === '8') {
		const link = `../admin/internationalNew/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">Подтвердить предложение</a>`
		return `${linkHTML}`
	} else if (status === '1') {
		const link = `./internationalNew/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">Посмотреть предложения (${offerCount})</a>`
		return `${linkHTML}`
	} else {
		const link = `../admin/internationalNew/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">История предложений</a>`
		return `${linkHTML}`
	}
}

// асинхронное обновление количества предложений для конкретного маршрута
async function updateOfferCount(idRoute) {
	const offerCount = await getData(getRouteMessageBaseUrl + idRoute)
	const rowNode = gridOptions.api.getRowNode(idRoute)
	const item = rowNode.data
	const newItem = {
		...item,
		offerCount: offerCount
	}
	const resultCallback = () => highlightRow(rowNode)

	gridOptions.api.applyTransactionAsync({ update: [newItem] }, resultCallback)
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

// отображение сохраненной в locacstorage строки таблицы
function displaySavedRowId(gridOptions, key) {
	const rowId = localStorage.getItem(key)
	if (!rowId) return

	const rowNode = gridOptions.api.getRowNode(rowId)
	gridOptions.api.applyTransaction({ update: [{ ...rowNode.data, isSavedRow: true} ] })
	gridOptions.api.ensureNodeVisible(rowNode, 'top')
	localStorage.removeItem(key)
	return rowNode
}
// сохранение строки таблицы в locacstorage
function saveRowId(key, rowId) {
	localStorage.setItem(key, rowId)
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




//--------------------------------------------------------------------------------------------------------------------------
// функции для контекстного меню
function displayTenderOffer(idRoute, status) {
	const url = status === '8'
		? `../admin/internationalNew/tenderOffer?idRoute=${idRoute}`
		: `./internationalNew/tenderOffer?idRoute=${idRoute}`
	saveRowId(ROW_INDEX_KEY, idRoute)
	window.location.href = url
}
function sendTender(idRoute, routeDirection) {
	const url = `../logistics/rouadUpdate?id=${idRoute}&statRoute=1&comment=international`
	const columnName = 'statusRoute'
	const newValue = '1'

	const headMessage = {
		fromUser: "logist",
		toUser: "international",
		text: 'Маршрут ' + routeDirection + ' доступен для торгов.',
		url: `/speedlogist/main/carrier/tender/tenderpage?routeId=${idRoute}`,
		idRoute: idRoute,
		status: "1"
	}

	fetch(url)
		.then(res => {
			updateCellData(idRoute, columnName, newValue)
			snackbar.show('Тендер отправлен на биржу')
			sendHeadMessage(headMessage)
		})
		.catch(errorCallback)
}
function showUnloadPoints(idRoute) {
	var url = `../logistics/international/routeShow?idRoute=${idRoute}`;
	saveRowId(ROW_INDEX_KEY, idRoute)
	window.location.href = url;
}
async function editRoute(idRoute) {
	const url = `./international/editRoute?idRoute=${idRoute}`;
	saveRowId(ROW_INDEX_KEY, idRoute)
	window.location.href = url
	
}
async function completeRoute(idRoute) {
	const url = `/speedlogist/main/logistics/international/routeEnd?idRoute=${idRoute}`
	const columnName = 'statusRoute'
	const newValue = '6'

	const routeFinishInfo = await getData(`/speedlogist/api/memory/message/routes/${idRoute}`)

	if (!routeFinishInfo) return

	const isRouteCompleted = routeFinishInfo.filter(item => item.text === 'На_выгрузке').length > 0

	if (isRouteCompleted) {
		fetch(url)
			.then(res => {
				updateCellData(idRoute, columnName, newValue)
				snackbar.show('Маршрут завершен')
			})
			.catch(errorCallback)
	} else {
		snackbar.show('Маршрут не может быть завершен, т.к. авто не прибыло на место разгрузки')
	}
}
function cancelTender(idRoute) {
	const url = `../logistics/rouadUpdate?id=${idRoute}&statRoute=5&comment=international`
	const columnName = 'statusRoute'
	const newValue = '5'

	fetch(url)
		.then(res => {
			updateCellData(idRoute, columnName, newValue)
			snackbar.show('Маршрут отменен')
		})
		.catch(errorCallback)
}
function errorCallback(error) {
	console.error(error)
	snackbar.show('Возникла ошибка - обновите страницу!')
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