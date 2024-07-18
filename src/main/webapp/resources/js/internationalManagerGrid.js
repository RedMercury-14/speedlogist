import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js"
import { changeGridTableMarginTop, dateHelper, debounce, getData, getRouteStatus } from "./utils.js"
import { ws } from './global.js'
import { wsHead } from './global.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from "./uiIcons.js"

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'internationalManager'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const ROW_INDEX_KEY = `AG_Grid_rowIndex_to_${PAGE_NAME}`

const getOrderBaseUrl ='../../api/manager/getOrdersForLogist/'
const getRouteMessageBaseUrl = `../../api/info/message/numroute/`

export const rowClassRules = {
	'finishRow': params => params.node.data.statusRoute === '4',
	'attentionRow': params => params.node.data.statusRoute === '0',
	'cancelRow': params => params.node.data.statusRoute === '5',
	'endRow': params => params.node.data.statusRoute === '6',
	'oncePersonRoute': params => params.node.data.statusRoute === '8',
	'activRow': params => params.node.data.offerCount !== 0,

}

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let table

const columnDefs = [
	{
		field: '', colId: 'selectionRow',
		width: 30,
		pinned: 'left', lockPinned: true,
		checkboxSelection: true,
		suppressMovable: true, suppressMenu: true,
		resizable: false, sortable: false, filter: false,
	},
	{ headerName: 'ID', field: 'idRoute', },
	{ headerName: 'Тип', field: 'simpleWay', width: 50, },
	{ headerName: 'Название маршрута', field: 'routeDirection', width: 240, wrapText: true, autoHeight: true, },
	{ headerName: 'Дата загрузки', field: 'simpleDateStart', comparator: dateComparator, },
	{ headerName: 'Время загрузки (планируемое)', field: 'timeLoadPreviously', },
	{ headerName: 'Дата выгрузки', field: 'dateUnloadPreviouslyStock', comparator: dateComparator, },
	{ headerName: 'Время выгрузки', field: 'timeUnloadPreviouslyStock', },
	{ headerName: 'Выставляемая стоимость', field: 'finishPrice', },
	{ headerName: 'Экономия', field: '', },
	{ headerName: 'Перевозчик', field: '', },
	{ headerName: 'Номер машины', field: '', },
	{ headerName: 'Данные по водителю', field: '', },
	{ headerName: 'Заказчик', field: 'customer', wrapText: true, autoHeight: true, },
	{ headerName: 'Паллеты/Объем', field: 'totalLoadPall', },
	{ headerName: 'Общий вес', field: 'totalCargoWeight', },
	{ headerName: 'Комментарии', field: 'userComments', wrapText: true, autoHeight: true, },
	{ headerName: 'Начальные стоимости перевозки', field: 'startPrice', },
	{
		headerName: 'Статус и предложения', field: 'offerCount',
		wrapText: true, autoHeight: true,
		cellRenderer: tenderStatusRenderer,
	},
	{
		headerName: 'Статус', field: 'statusRoute', hide: true,
		wrapText: true, autoHeight: true,
		valueFormatter: (params) => getRouteStatus(params.value),
	},
	// {
	// 	headerName: 'Предложения', field: 'offerCount',
	// 	wrapText: true, autoHeight: true,
	// 	cellRenderer: tenderLinkRenderer,
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


window.addEventListener("load", async () => {
	const orderSearchForm = document.querySelector('#orderSearchForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const gridDiv = document.querySelector('#myGrid')
	gridDiv.addEventListener('click', gridTableClickHandler)

	const testTransaction = document.querySelector('#testTransaction')
	testTransaction && testTransaction.addEventListener('click', () => {

	})

	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)

	// автозаполнение полей дат в форме поиска заявок
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit', searchFormSubmitHandler)

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	const orders = await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)
	const routes = orders
		.map(order => order.routes[0])
		.filter(route => route)

	// отрисовка таблицы
	await renderTable(gridDiv, gridOptions, routes)

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	// отображение сохраненной строки таблицы
	displaySavedRowNode()

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

// обработчик кликов в таблице
function gridTableClickHandler(e) {
	const target = e.target

	if (target.id === 'tenderOfferLink') {
		e.preventDefault()
		const idRoute = target.dataset.idroute
		const status = target.dataset.status
		displayTenderOffer(idRoute, status)
	}
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
	console.log("🚀 ~ renderTable ~ mappingData:", mappingData)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const orderSearchForm = document.querySelector('#orderSearchForm')

	const dateStart = orderSearchForm.date_from.value
	const dateEnd = orderSearchForm.date_to.value

	const orders = await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)
	const routes = orders
		.map(order => order.routes[0])
		.filter(route => route)

	if (!orders || !orders.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(routes)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function getMappingData(data) {
	return await Promise.all(data.map( async (route) => {
		const idRoute = route.idRoute
		const rhsItem = route.roteHasShop[0]
		const cargo = rhsItem && rhsItem.cargo ? rhsItem.cargo : ''
		const type = route.typeTrailer ? `Тип прицепа: ${route.typeTrailer}; ` : ''
		const temp = route.temperature ? `Температура: ${route.temperature} °C; ` : ''
		const vol = rhsItem && rhsItem.volume ? `Объем: ${rhsItem.volume}` : ''
		const info = type + temp + vol
		const dateToView = route.dateLoadPreviously.split('-').reverse().join('.')
		const loadDate = `${dateToView},  ${route.timeLoadPreviously}`

		const offerCount = await getData(getRouteMessageBaseUrl + idRoute)

		return {
			...route,
			offerCount
		}
	}))
}

function getContextMenuItems(params) {
	if (!params.node) return

	const routeData = params.node.data
	const idRoute = routeData.idRoute
	const routeDirection = routeData.routeDirection

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
			icon: uiIcons.sendObject,
			action: () => {
				sendTender(idRoute, routeDirection)
			},
		},
		{
			name: `Отправить выделенные тендеры`,
			disabled: true,
			action: () => {

			},
		},
		{
			name: `Показать точки выгрузок`,
			disabled: true,
			action: () => {
				
			},
		},
		{
			name: `Завершить маршрут`,
			icon: uiIcons.checkObject,
			action: () => {
				completeRoute(idRoute)
			},
		},
		{
			name: `Отменить тендер`,
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

// рендерер статуса маршрута для таблицы
function tenderStatusRenderer(params) {
	const data = params.node.data
	const idRoute = data.idRoute
	const offerCount = data.offerCount
	const status = data.statusRoute
	const statusText = getRouteStatus(status)

	if (status === '8') {
		const link = `../admin/international/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">Посмотреть предложение</a>`
		return `${statusText} ${linkHTML}`
	} else if (status === '1') {
		const link = `./international/tenderOffer?idRoute=${idRoute}`
		const linkHTML = `<a class="text-primary" id="tenderOfferLink" data-idroute="${idRoute}" data-status="${status}" href="${link}">Посмотреть предложения (${offerCount})</a>`
		return `${statusText} ${linkHTML}`
	} else {
		return statusText
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
function displaySavedRowNode() {
	const rowId = localStorage.getItem(ROW_INDEX_KEY)
	if (!rowId) return

	const rowNode = gridOptions.api.getRowNode(rowId)
	gridOptions.api.ensureNodeVisible(rowNode, 'top')
	localStorage.removeItem(ROW_INDEX_KEY)
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
		? `../admin/international/tenderOffer?idRoute=${idRoute}`
		: `./international/tenderOffer?idRoute=${idRoute}`
	localStorage.setItem(ROW_INDEX_KEY, idRoute)
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
			sendHeadMessage(headMessage)
		})
		.catch(errorCallback)
}
function showUnloadPoints(idRoute) {
	var url = `../logistics/international/routeShow?idRoute=${idRoute}`;
	localStorage.setItem("mouseX", mouseX);
	localStorage.setItem("mouseY", mouseY);
	window.location.href = url;
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
			.then(res => updateCellData(idRoute, columnName, newValue))
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
		.then(res => updateCellData(idRoute, columnName, newValue))
		.catch(errorCallback)
}

function sendHeadMessage(message) {
	wsHead.send(JSON.stringify(message))
}



function errorCallback(error) {
	console.error(error)
	snackbar.show('Возникла ошибка - обновите страницу!')
} 