import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js"
import { dateHelper, debounce, getData, getRouteStatus, isAdmin, isObserver } from "./utils.js"
import { ws } from './global.js'
import { wsHead } from './global.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from "./uiIcons.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { ajaxUtils } from "./ajaxUtils.js"
import { getMemoryRouteMessageBaseUrl, getNumMessageBaseUrl, getProposalBaseUrl, getRoutesBaseUrl, routeUpdateBaseUrl } from "./globalConstants/urls.js"

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'internationalManagerNew'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const ROW_INDEX_KEY = `AG_Grid_rowIndex_to_${PAGE_NAME}`
const role = document.querySelector('#role').value


export const rowClassRules = {
	'finishRow': params => params.data && params.data.statusRoute === '4',
	'attentionRow': params => params.data && params.data.statusRoute === '0',
	'cancelRow': params => params.data && params.data.statusRoute === '5',
	'endRow': params => params.data && params.data.statusRoute === '6',
	'oncePersonRoute': params => params.data && params.data.statusRoute === '8',
	'carrierDataSent': params => params.data && params.data.statusRoute === '9',
	'activRow': params => params.data && params.data.offerCount !== 0,
	'savedRow': params => params.data && params.data.isSavedRow === true,
}

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let table
let isInitDataLoaded = false

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
	{ headerName: 'ID заявки', field: 'idOrder', cellRenderer: idOrderRenderer, },
	{ headerName: 'Сверка УКЗ', field: 'ukz', wrapText: true, autoHeight: true, },
	{ headerName: 'Груз', field: 'cargo', wrapText: true, autoHeight: true, },
	{ headerName: 'Тип загрузки авто', field: 'typeLoad', },
	{ headerName: 'Тип кузова', field: 'typeTrailer', },
	{ headerName: 'Способ загрузки авто', field: 'methodLoad', },
	{ headerName: 'Температурные условия', field: 'temperature', wrapText: true, autoHeight: true, },
	{ headerName: 'Контактное лицо контрагента', field: 'contact', wrapText: true, autoHeight: true, },
	{ headerName: 'Общий вес', field: 'totalCargoWeight', valueFormatter: params => params.value + ' кг' },
	{ headerName: 'Комментарии', field: 'userComments', wrapText: true, autoHeight: true, minWidth: 240, width: 640, },
	{ headerName: 'Логист', field: 'logistInfo', wrapText: true, autoHeight: true, },
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


document.addEventListener('DOMContentLoaded', async () => {
	const routeSearchForm = document.querySelector('#routeSearchForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	gridDiv.addEventListener('click', gridTableClickHandler)

	// автозаполнение полей дат в форме поиска заявок
	const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	routeSearchForm.addEventListener('submit', searchFormSubmitHandler)

	// отображение стартовых данных
	if (window.initData) {
		await initStartData(routeSearchForm)
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData(routeSearchForm)
		})
	}

	// обработчик получения сообщений о предложениях
	ws.onmessage = onMessageHandler

	bootstrap5overlay.hideOverlay()
})


window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// установка стартовых данных
async function initStartData(routeSearchForm) {
	await updateTable(gridOptions, routeSearchForm, window.initData)
	displaySavedRow(gridOptions, ROW_INDEX_KEY)
	isInitDataLoaded = true
	window.initData = null

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()
}

// обработчик формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	await updateTable(gridOptions, e.target)
	displaySavedRow(gridOptions, ROW_INDEX_KEY)
	isInitDataLoaded = true
}

// обработчик сообщений WebSocket
async function onMessageHandler(e) {
	const message = JSON.parse(e.data)
	if (!message) return
	if (!isInitDataLoaded) return

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

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}

async function updateTable(gridOptions, searchForm, data) {
	gridOptions.api.showLoadingOverlay()

	const dateStart = searchForm.date_from.value
	const dateEnd = searchForm.date_to.value

	const routes = data
		? data
		: await getData(`${getRoutesBaseUrl}${dateStart}&${dateEnd}`)

	if (!routes || !routes.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}
	// console.log(data)
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
		const offerCount = route.statusRoute === '1'
			? await getData(getNumMessageBaseUrl + idRoute)
			: 0

		const isSavedRow = false
		const orderInfo = getOrderInfo(route)
		const idOrder =  orderInfo.idOrder
		const contact = orderInfo.contact
		const ukz = orderInfo.control
		const cargo = orderInfo.cargo
		const typeLoad = orderInfo.typeLoad
		const typeTruck = orderInfo.typeTruck
		const methodLoad = orderInfo.methodLoad
		const temperature = orderInfo.temperature
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
			idOrder,
			contact,
			ukz,
			cargo,
			// typeLoad,
			// typeTruck,
			// methodLoad,
			temperature,
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
			disabled: status !== '0' || isObserver(role),
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
			disabled: isObserver(role),
			name: `Редактировать маршрут`,
			icon: uiIcons.pencil,
			action: () => {
				editRoute(idRoute)
			},
		},
		{
			name: `Завершить маршрут`,
			disabled: status !== '4' || isObserver(role),
			icon: uiIcons.checkObject,
			action: () => {
				completeRoute(idRoute)
			},
		},
		{
			name: `Отменить тендер`,
			disabled: status === '5' || isObserver(role),
			icon: uiIcons.cancel,
			action: () => {
				cancelTender(idRoute)
			},
		},
		"separator",
		"excelExport",
		"separator",
		{
			name: `Скачать заявку для перевозчика`,
			icon: uiIcons.fileArrowDown,
			action: () => {
				getProposal(idRoute)
			},
		}
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

// рендерер ID заявки со ссылкой
function idOrderRenderer(params) {
	const value = params.value
	if (!value) return ''
	const idOrders = value.split('; ')
	const linkHTML = (idOrder) => {
		const link = `./ordersLogist/order?idOrder=${idOrder}`
		return `<a class="text-primary" href="${link}">${idOrder}</a>`
	}
	return idOrders.map(idOrder => linkHTML(idOrder)).join('<br>')
}

// асинхронное обновление количества предложений для конкретного маршрута
async function updateOfferCount(idRoute) {
	const offerCount = await getData(getNumMessageBaseUrl + idRoute)
	const rowNode = gridOptions.api.getRowNode(idRoute)
	if(!rowNode) return
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
function displaySavedRow(gridOptions, key) {
	const rowId = localStorage.getItem(key)
	if (!rowId) return

	const rowNode = gridOptions.api.getRowNode(rowId)
	if (!rowNode) {
		localStorage.removeItem(key)
		return
	}
	gridOptions.api.applyTransaction({ update: [{ ...rowNode.data, isSavedRow: true} ] })
	gridOptions.api.ensureNodeVisible(rowNode, 'top')
	localStorage.removeItem(key)

	// отображаем строку ещё раз после установки ширины строк
	setTimeout(() => {
		gridOptions.api.ensureNodeVisible(rowNode, 'top')
	}, 500)

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
	const newStatus = '1'
	const url = `${routeUpdateBaseUrl}${idRoute}&${newStatus}`
	const columnName = 'statusRoute'

	const headMessage = {
		fromUser: "logist",
		toUser: "international",
		text: 'Маршрут ' + routeDirection + ' доступен для торгов.',
		url: `/speedlogist/main/carrier/tender/tenderpage?routeId=${idRoute}`,
		idRoute: idRoute,
		status: newStatus
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 500)

	ajaxUtils.get({
		url: url,
		successCallback: (res) => {
			if (res && res.status && res.status === '200') {
				updateCellData(idRoute, columnName, newStatus)
				snackbar.show('Тендер отправлен на биржу')
				sendHeadMessage(headMessage)
			}
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
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

	const routeFinishInfo = await getData(`${getMemoryRouteMessageBaseUrl}${idRoute}`)

	if (!routeFinishInfo) return

	const isRouteCompleted = routeFinishInfo.filter(item => item.text === 'На_выгрузке').length > 0

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 500)

	if (isRouteCompleted) {
		fetch(url)
			.then(res => {
				updateCellData(idRoute, columnName, newValue)
				snackbar.show('Маршрут завершен')
				clearTimeout(timeoutId)
				bootstrap5overlay.hideOverlay()
			})
			.catch(err => errorCallback(err, timeoutId))
	} else {
		snackbar.show('Маршрут не может быть завершен, т.к. авто не прибыло на место разгрузки')
		clearTimeout(timeoutId)
		bootstrap5overlay.hideOverlay()
	}
}
function cancelTender(idRoute) {
	const newStatus = '5'
	const url = `${routeUpdateBaseUrl}${idRoute}&${newStatus}`
	const columnName = 'statusRoute'

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 500)

	ajaxUtils.get({
		url: url,
		successCallback: (res) => {
			if (res && res.status && res.status === '200') {
				updateCellData(idRoute, columnName, newStatus)
				snackbar.show('Маршрут отменен')
			}
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function errorCallback(error, timeoutId) {
	console.error(error)
	snackbar.show('Возникла ошибка - обновите страницу!')
	timeoutId && clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
}

function getProposal(idRoute) {
	fetch(getProposalBaseUrl + idRoute)
		.then(res => {
			console.log("🚀 ~ getProposal ~ res:", res)
			if (!res.ok) {
				throw new Error('Ошибка при получении файла')
			}
			res.blob().then(blob => {
				const link = document.createElement('a')
				link.href = window.URL.createObjectURL(blob)
				link.download = 'Заявка ' + idRoute + '.pdf'
				link.click()
			})
		})
		.catch(err => errorCallback(err, null))
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
	const user = route.user ? route.user : ''
	if (!user) return ''
	return user.companyName ? user.companyName : ''
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
function getOrderInfo(route) {
	const orders = route.ordersDTO
	const processField = (field) =>
		Array.from(new Set(orders?.map(order => order[field]).filter(Boolean))).join('\n')

	if (!orders || !orders.length) {
		return {
			idOrder: null,
			contact: null,
			control: null,
			cargo: null,
			typeLoad: null,
			typeTruck: null,
			methodLoad: null,
			temperature: null,
		}
	}

	return {
		idOrder: orders.map(order => order.idOrder).join('; '),
		contact: processField('contact'),
		control: orders.some(order => order.control) ? 'Необходима сверка УКЗ' : 'Нет',
		cargo: orders.map(order => order.cargo).filter(Boolean).join('\n'),
		typeLoad: processField('typeLoad'),
		typeTruck: processField('typeTruck'),
		methodLoad: processField('methodLoad'),
		temperature: processField('temperature'),
	}
}