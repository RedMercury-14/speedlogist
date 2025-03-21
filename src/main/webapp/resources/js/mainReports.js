import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { downloadReport330BaseUrl, getOrderBaseUrl, getOrderStatByTimeDeliveryAndOLUrl } from './globalConstants/urls.js'
import { getPointToView, pointSorting } from './procurementControlUtils.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { dateHelper, debounce, getData, getStatus, rowClassRules } from './utils.js'


const PAGE_NAME = 'analOrderTable'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value
const login = document.querySelector('#login').value.toLowerCase()

const REPORT330_MAX_DAYS_RANGE = 31

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)


const columnDefs = [
	{ headerName: 'О заявке', headerClass: 'px-2 h6 font-weight-bold', children: [
		{ 
			headerName: 'ID заявки', field: 'idOrder', colId: 'idOrder',
			cellClass: 'px-2 group-cell', width: 100,
			cellRenderer: 'agGroupCellRenderer',
			cellRendererParams: {
				innerRenderer: orderLinkRenderer,
			},
		},
		{ headerName: 'Наименование контрагента', field: 'counterparty', colId: 'counterparty', width: 240, },
		{ headerName: 'Тип маршрута', field: 'wayToView', width: 120, },
		{ field: 'status', hide: true },
		{ headerName: 'Статус', field: 'statusToView', },
		{ headerName: 'Дата создания заявки', field: 'dateCreate', valueFormatter: dateValueFormatter, comparator: dateComparator, width: 120, filterParams: { valueFormatter: dateValueFormatter, },},
		{ headerName: 'Создатель заявки', field: 'managerToView', width: 400, },
		{ headerName: 'Логист', field: 'logistToView', },
		{ headerName: 'Контактное лицо контрагента', field: 'contact', },
		{ headerName: 'Связь', field: 'link', },
		{
			headerName: 'История', field: 'changeStatus',
			cellClass: 'px-2 py-0 text-center',
			cellRenderer: BtnCellRenderer,
			cellRendererParams: {
				onClick: showHistory,
				label: 'Просмотр',
				className: 'btn btn-primary btn-sm',
			},
			filterParams: {
				valueFormatter: (params) => params.value ? params.value.replaceAll('\n', ' ') : null,
			},
		},
		{ headerName: 'Дата загрузки (первая)', field: 'loadDateToView', comparator: dateComparator, width: 120, },
		{ headerName: 'Дата выгрузки (последняя)', field: 'unloadDateToView', comparator: dateComparator, width: 120, },
		{ headerName: 'Комментарии', field: 'comment', width: 640, cellClass: 'font-weight-bold more-text', },
	]},

	{ headerName: 'О Транспорте', headerClass: 'px-2 h6 font-weight-bold', children: [
		{ headerName: 'Погрузочный номер', field: 'loadNumber', },
		{ headerName: 'Условия поставки', field: 'incoterms', },
		{ headerName: 'Маршруты', field: 'routeInfo', width: 640, },
		{ headerName: 'Сверка УКЗ', field: 'controlToView', width: 100, },
		{ headerName: 'Тип кузова', field: 'typeTruck', },
		{ headerName: 'Тип загрузки авто', field: 'typeLoad', },
		{ headerName: 'Способ загрузки авто', field: 'methodLoad', },
		{ headerName: 'Груз', field: 'cargo', },
		{ headerName: 'Температурные условия', field: 'temperature', },
		{ headerName: 'Штабелирование', field: 'stackingToView', },
	]},

	{ headerName: 'Слоты', headerClass: 'px-2 h6 font-weight-bold', children: [
		{ headerName: 'Слот на выгрузку', field: 'timeDelivery', valueFormatter: timeDeliveryValueFormatter, filterParams: { valueFormatter: timeDeliveryValueFormatter, },},
		{ headerName: 'Рампа', field: 'idRamp', },
		{ headerName: 'Длит-ть выгрузки', field: 'timeUnload', },
		{
			headerName: 'Последнее действие логистов', field: 'slotInfo',
			cellClass: 'px-2 py-0 text-center',
			cellRenderer: BtnCellRenderer,
			cellRendererParams: {
				onClick: showHistory,
				label: 'Просмотр',
				className: 'btn btn-primary btn-sm',
			},
			filterParams: {
				valueFormatter: (params) => params.value ? params.value.replaceAll('\n', ' ') : null,
			},
		},
		{ headerName: 'Первая загрузка слота', field: 'firstLoadSlot', valueFormatter: dateTimeValueFormatter, comparator: dateComparator, filterParams: { valueFormatter: dateTimeValueFormatter, },},
	]},

	{ headerName: 'Из Маркета', headerClass: 'px-2 h6 font-weight-bold', children: [
		{ headerName: 'Номер из Маркета', field: 'marketNumber', },
		{ headerName: 'Склад доставки (из Маркета)', field: 'numStockDelivery', width: 120, },
		{ headerName: 'Информация (из Маркета)', field: 'marketInfo', width: 440, },
		{ headerName: 'Дата расчета ОРЛ', field: 'dateOrderOrl', valueFormatter: dateValueFormatter, comparator: dateComparator, filterParams: { valueFormatter: dateValueFormatter, },},
		{ headerName: 'SKU', field: 'sku', width: 70, },
		{ headerName: 'Моно-паллеты', field: 'monoPall', width: 70, },
		{ headerName: 'Микс-паллеты', field: 'mixPall', width: 70, },
		{ headerName: 'Номер контракта', field: 'marketContractType', width: 70, },
		{ headerName: 'Код контракта', field: 'marketContractGroupId', },
		{ headerName: 'Тип контракта', field: 'marketContractNumber', },
		{ headerName: 'Код поставщика', field: 'marketContractorId', },
		{ headerName: 'Коды товаров', field: 'numProduct', valueFormatter: numProductValueFormatter, filterParams: { valueFormatter: numProductValueFormatter, },},
		{ headerName: 'Количество товара', field: 'orderLinesMap', width: 120, valueFormatter: orderLinesMapValueFormatter, filterParams: { valueFormatter: orderLinesMapValueFormatter, },},
		{ headerName: 'Начальная сумма заказа без НДС', field: 'marketOrderSumFirst', },
		{ headerName: 'Конечная сумма заказа с НДС', field: 'marketOrderSumFinal', },
	]},

	{ headerName: 'Со Двора', headerClass: 'px-2 h6 font-weight-bold', children: [
		{ headerName: 'Статус со Двора', field: 'statusYard', width: 100, },
		{ headerName: 'Время прибытия (Двор)', field: 'arrivalFactYard', valueFormatter: dateTimeValueFormatter, comparator: dateComparator, filterParams: { valueFormatter: dateTimeValueFormatter, },},
		{ headerName: 'Время регистрации (Двор)', field: 'registrationFactYard', valueFormatter: dateTimeValueFormatter, comparator: dateComparator, filterParams: { valueFormatter: dateTimeValueFormatter, },},
		{ headerName: 'Начало выгрузки (Двор)', field: 'unloadStartYard', valueFormatter: dateTimeValueFormatter, comparator: dateComparator, filterParams: { valueFormatter: dateTimeValueFormatter, },},
		{ headerName: 'Конец выгрузки (Двор)', field: 'unloadFinishYard', valueFormatter: dateTimeValueFormatter, comparator: dateComparator, filterParams: { valueFormatter: dateTimeValueFormatter, },},
		{ headerName: 'Длит-ть выгрузки (Двор)', field: 'timeUnloadYard', width: 120, },
		{ headerName: 'Паллеты (Двор)', field: 'pallFactYard', width: 100, },
		{ headerName: 'Масса (Двор)', field: 'weightFactYard', width: 100, },
	]},
]

const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		width: 140,
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
		minWidth: 200,
	},
	rowGroupPanelShow: 'always',
	
	animateRows: true,
	suppressDragLeaveHidesColumns: true,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getRowId: (params) => params.data.idOrder,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	getContextMenuItems: getContextMenuItems,
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
	masterDetail: true,
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: {
			columnDefs: [
				{ headerName: "№", field: 'pointNumber', flex: 1, },
				{ headerName: "Тип точки", field: 'type', },
				{ headerName: "Дата", field: 'dateToView', },
				{
					headerName: "Время", field: 'time',
					valueFormatter: (params) => params.value && `${params.value.slice(0, 5)}`
				},
				{ headerName: "Коды ТН ВЭД", field: 'tnvd', flex: 6, },
				{ headerName: "Информация о грузе", field: 'info', tooltipField: 'info', flex: 8, },
				{ headerName: "Адрес склада", field: 'bodyAddress', flex: 8, },
				{ headerName: "Время работы", field: 'timeFrame', },
				{ headerName: "Контактное лицо", field: 'contact', flex: 6, },
				{ headerName: "Адрес таможни", field: 'customsAddress', flex: 8, },
			],
			defaultColDef: {
				headerClass: 'px-1',
				cellClass: 'px-2',
				wrapText: true,
				autoHeight: true,
				resizable: true,
				flex: 4,
				suppressMenu: true,
				wrapHeaderText: true,
				autoHeaderHeight: true,
			},
			enableBrowserTooltips: true,
			localeText: AG_GRID_LOCALE_RU,
		},
		getDetailRowData: async (params) => {
			params.successCallback(params.data.addressesToView)
		},
	},
}

document.addEventListener('DOMContentLoaded', async () => {
	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	restoreColumnState()

	// автозаполнение полей дат в форме поиска заявок
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit',searchFormSubmitHandler)
	// отправак формы скачивания 330 отчёта
	report330Form.addEventListener('submit', report330FormSubmitHandler)
	// валидация ввода
	report330Form.report330_dateStart.addEventListener('input', (e) => validateDateFields(e.target, report330Form.report330_dateEnd))
	report330Form.report330_dateEnd.addEventListener('input', (e) => validateDateFields(report330Form.report330_dateStart, e.target))
	report330Form.report330_stocks.addEventListener('input', (e) => validateNumbersField(e.target))
	report330Form.report330_products.addEventListener('input', (e) => validateNumbersField(e.target))

	$('#report330Modal').on('hide.bs.modal', (e) => report330Form.reset())
})

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// установка стартовых данных
async function initStartData() {
	window.initData = null

	
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
	return data.map(mapCallbackForProcurementControl)
}

function mapCallbackForProcurementControl(order) {
	const controlToView = order.control ? 'Да' : 'Нет'
	const telephoneManagerToView = order.telephoneManager ? `; тел. ${order.telephoneManager}` : ''
	const managerToView = order.manager ? `${order.manager}${telephoneManagerToView}` : order.loginManager
	const statusToView = getStatus(order.status)
	const stackingToView = order.stacking ? 'Да' : 'Нет'
	const logistToView = order.logist && order.logistTelephone
		? `${order.logist}, тел. ${order.logistTelephone}`
		: order.logist
			? order.logist
			: order.logistTelephone
				? order.logistTelephone : ''

	const unloadWindowToView = order.onloadWindowDate && order.onloadWindowTime
		? `${dateHelper.getFormatDate(order.onloadWindowDate)} ${order.onloadWindowTime.slice(0, 5)}`
		: ''


	const filtredAdresses =  order.addresses ? order.addresses.filter(address => address.isCorrect) : []
	const loadPoints = filtredAdresses.filter(address => address.type === "Загрузка")

	const addressesToView = filtredAdresses
		.sort(pointSorting)
		.map(getPointToView)

	const loadPointsToView = addressesToView
		.filter(address => address.type === "Загрузка")
		.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
		.join(' ')

	const unloadPointsToView = addressesToView
		.filter(address => address.type === "Выгрузка")
		.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
		.join(' ')

	const loadDateToView = getFirstLoadDateToView(addressesToView)
	const unloadDateToView = getLastUnloadDateToView(addressesToView)

	const summPall = loadPoints.reduce((acc, address) => {
		if (address.pall) {
			const pall = Number(address.pall)
			acc += pall
			return acc
		}
	}, 0)

	const summVolume = loadPoints.reduce((acc, address) => {
		if (address.volume) {
			const volume = Number(address.volume)
			acc += volume
			return acc
		}
	}, 0)

	const summWeight = loadPoints.reduce((acc, address) => {
		if (address.weight) {
			const weight = Number(address.weight)
			acc += weight
			return acc
		}
	}, 0)

	const routeInfo = getRouteInfo(order)
	const routePrice = getRoutePrice(order)
	const wayToView = getWayToView(order)

	const timeUnloadYard = order.unloadStartYard && order.unloadFinishYard
		? dateHelper.getDiffTime(order.unloadFinishYard, order.unloadStartYard)
		: ''

	return {
		...order,
		controlToView,
		addressesToView,
		loadDateToView,
		unloadDateToView,
		managerToView,
		statusToView,
		stackingToView,
		logistToView,
		unloadWindowToView,
		loadPointsToView,
		unloadPointsToView,
		summPall: summPall ? summPall : null,
		summVolume: summVolume ? summVolume : null,
		summWeight: summWeight ? summWeight : null,
		routeInfo,
		routePrice,
		wayToView,
		timeUnloadYard,
	}
}
function getFirstLoadDateToView(addressesToView) {
	if (!addressesToView) return ''
	const loadPoints = addressesToView.filter(address => address.type === "Загрузка")
	if (!loadPoints.length || loadPoints.length === 0) return ''
	return loadPoints[0].dateToView
}
function getLastUnloadDateToView(addressesToView) {
	if (!addressesToView) return ''
	const unloadPoints = addressesToView
		.filter(address => address.type === "Выгрузка")
		.sort((a, b) => b.date - a.date)
	if (!unloadPoints.length || unloadPoints.length === 0) return ''
	return unloadPoints[0].dateToView
}
function getRouteInfo(order) {
	const routes = order.routes
	
	if (!routes || !routes.length) {
		return ''
	}

	return routes
		.sort((a,b) => b.idRoute - a.idRoute)
		.map((route, i) => `${i + 1}) Маршрут ${route.routeDirection}`)
		.join(' ************** ')
}
function getRoutePrice(order) {
	const routes = order.routes
	if (!routes || !routes.length) return ''

	const successRoutes = routes
		.sort((a, b) => b.idRoute - a.idRoute)
		.filter(route => route.statusRoute === '4' || route.statusRoute === '6' || route.statusRoute === '8')

	if (!successRoutes.length) return ''

	return successRoutes[0].finishPrice + ' ' + successRoutes[0].startCurrency
}
function getWayToView(order) {
	const way = order.way ? order.way : ''
	const isInternalMovement = order.isInternalMovement === 'true'
	return isInternalMovement ? 'Внутреннее перемещение' : way
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

// обработчик отправки формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	gridOptions.api.showLoadingOverlay()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	const dateStart = data.date_from
	const dateEnd = data.date_to
	const productCode = data.productCode
	const url = productCode
		? `${getOrderStatByTimeDeliveryAndOLUrl}${dateStart}&${dateEnd}&${productCode}`
		: `${getOrderBaseUrl}${dateStart}&${dateEnd}`
	const res = await getData(url)
	if (!res) return

	const orders = res.orders ? res.orders : res
	updateTable(gridOptions, orders)
	gridOptions.api.hideOverlay()
}

async function report330FormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const dateFromField = e.target.report330_dateStart
	const dateToField = e.target.report330_dateEnd
	const stocksField = e.target.report330_stocks
	const productsField = e.target.report330_products
	
	if (!validateDateFields(dateFromField, dateToField)) return
	if (!validateNumbersField(stocksField)) return
	if (!validateNumbersField(productsField)) return

	const {
		report330_dateStart: dateStart,
		report330_dateEnd: dateEnd,
		report330_stocks: stocks,
		report330_products: products,
	} = data

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 0)

	fetch(`${downloadReport330BaseUrl}${dateStart}&${dateEnd}&${stocks}&${products}`)
		.then(res => res.status === 200 ? res.blob() : Promise.reject('Ошибка: статус ответа не 200'))
		.then(blob => {
			const url = window.URL.createObjectURL(blob)
			const a = document.createElement('a')
			a.style.display = 'none'
			a.href = url
			// a.download = '398.zip'
			document.body.appendChild(a)
			a.click()
			window.URL.revokeObjectURL(url)
			document.body.removeChild(a)
			snackbar.show('Скачивание файла...')
		})
		.catch((e) => {
			console.error('Ошибка при скачивании файла:', e)
			snackbar.show('Ошибка при скачивании файла, повторите попытку позже')

			// // Резервный метод: скачивание через ссылку
			// const a = document.createElement('a')
			// a.style.display = 'none'
			// a.href = `/speedlogist/file/330/${dateStart}&${dateEnd}&${stocks}&${products}`
			// // a.download = '398.zip'
			// document.body.appendChild(a)
			// a.click()
			// document.body.removeChild(a)
			// snackbar.show('Файл скачивается через резервный метод...')
		})
		.finally(() => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		})
}

// валидация данных номеров складов и товаров
function validateNumbersField(field) {
	const regex = /^(\d+)(,\d+)*$/
	const value = field.value.trim()

	if (regex.test(value) || !value) {
		field.setCustomValidity('')
		return true
	} else {
		field.setCustomValidity('Введите номера через запятую без пробелов (например: 123,456)')
		field.reportValidity()
		return false
	}
}
// валидация диапазона дат
function validateDateFields(dateFromField, dateToField) {
	const fromValue = new Date(dateFromField.value).getTime()
	const toValue = new Date(dateToField.value).getTime()
	const diff = toValue - fromValue

	const validCondition = diff >= 0
		&& diff <= dateHelper.DAYS_TO_MILLISECONDS * REPORT330_MAX_DAYS_RANGE

	if (validCondition || !diff) {
		dateToField.setCustomValidity('')
		return true
	} else {
		dateToField.setCustomValidity('Допустимый диапазон дат - не более 31 дня')
		dateToField.reportValidity()
		return false
	}
}

// рендер ссылки на заказ
function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./orders/order?idOrder=${data.idOrder}`

	const isLinkedOrder = data.link
	const isLinkedOrderLabel = isLinkedOrder ? ' <span class="text-danger"> (объединен)</span>' : ''

	return `<a class="text-primary" href="${link}">${params.value}${isLinkedOrderLabel}</a>`
}
// отображение модального окна с историей
function showHistory(params) {
	const historyContainer = document.querySelector('#messageContainer')
	const history = params.value
	historyContainer.innerHTML = history
	$('#displayMessageModal').modal('show')
}

function dateValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDate(date)
}
function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
	
}
function timeDeliveryValueFormatter(params) {
	const time = params.value
	if (!time) return ''
	return dateHelper.convertToDayMonthTime(time)
}
function numProductValueFormatter(params) {
	const numProduct = params.value
	if (!numProduct) return ''
	const numProductArray = numProduct.split('^')
	return numProductArray.filter(Boolean).join(', ')
}
function orderLinesMapValueFormatter(params) {
	const orderLinesMap = params.value
	if (!orderLinesMap) return ''
	const orderLinesKeys = Object.keys(orderLinesMap)
	if (orderLinesKeys.length === 0) return ''
	return orderLinesKeys.reduce((acc, key) => {
		const orderLine = orderLinesMap[key]
		const orderLineStr = `${key}: ${orderLine}`
		return acc ? `${acc}; ${orderLineStr}` : orderLineStr
	}, '')
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

function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}