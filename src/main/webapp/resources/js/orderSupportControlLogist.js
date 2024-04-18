import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { DateEditor, DateTimeEditor, ResetStateToolPanel, TimeEditor, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js";
import { debounce, getData, dateHelper, getStatus, changeGridTableMarginTop, rowClassRules } from './utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { ajaxUtils } from './ajaxUtils.js'
import { uiIcons } from './uiIcons.js'
import { excelStyles, getPointToView, pointSorting, procurementExcelExportParams } from './procurementControlUtils.js';

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'OrderSupport'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const getOrderBaseUrl ='../../api/manager/getOrdersForLogist/'
const getSearchOrderBaseUrl ='../../api/manager/getOrdersHasCounterparty/'
const setWindowUnloadTimeBaseUrl = '../../api/manager/setWindowUnload/'
const setWindowUnloadDateBaseUrl = '../../api/manager/setWindowUnloadDate/'
const setUnloadDurationBaseUrl = '../../api/manager/setTimeUnload/'

const cellClassRules = {
	"needEdit": params => !params.value,
}

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const columnDefsL1 = [
	{ 
		headerName: 'ID заявки', field: 'idOrder', colId: 'idOrder',
		cellClass: 'group-cell', flex: 1,
		cellRenderer: 'agGroupCellRenderer',
		cellRendererParams: {
			innerRenderer: orderLinkRenderer,
		},
	},
	{ headerName: 'Номер из Маркета', field: 'marketNumber', cellClass: "px-2 text-center"},
	{
		headerName: 'Дата', field: 'dateToView', flex: 1, comparator: dateComparator,
		cellClass: 'px-2 text-center',
		minWidth: 100, maxWidth: 100,
		// minWidth: 140, maxWidth: 140,
		// editable: (params) => params.data.status === 17 || params.data.status === 20,
		// cellEditor: DateEditor,
		// onCellValueChanged: setUnloadWindowDate
	},
	{
		headerName: "Время", field: "time",
		cellClass: 'px-2 text-center', minWidth: 110, maxWidth: 110,
		valueFormatter: (params) => params.value ? params.value.slice(0,5) : '',
		editable: (params) => params.data.status === 17 || params.data.status === 20,
		cellEditor: TimeEditor,
		onCellValueChanged: setUnloadWindowTime,
		cellClassRules: cellClassRules,
	},
	{
		headerName: 'Наименование контрагента', field: 'counterparty',
		width: 240, wrapText: true, autoHeight: true,
	},
	{ headerName: "Адрес склада выгрузки", field: "bodyAddress", flex: 3, },
	{ headerName: "Информазия о загрузке", field: "loadPointInfo", flex: 3, },
	{ headerName: "Груз", field: "info", tooltipField: "info", flex: 3, },
	{ headerName: 'Статус', field: 'statusToView', wrapText: true, autoHeight: true, },
	{ headerName: "Время работы", field: "timeFrame", flex: 1, },
	{ headerName: "Контактное лицо", field: "contact", flex: 1, },
	// { headerName: "Адрес таможни", field: "customsAddress", },
]
const columnDefsL2 = [
	{
		field: '', pinned: 'left', width: 40,
		cellRenderer: 'agGroupCellRenderer',
	},
	{
		headerName: 'Наименование контрагента', field: 'counterparty', colId: 'counterparty',
		pinned: 'left', width: 240, wrapText: true, autoHeight: true,
	},
	{ headerName: 'Дата создания заявки', field: 'dateCreateToView', comparator: dateComparator, },
	{ headerName: 'Дата загрузки', field: 'loadDateToView', comparator: dateComparator, },
	{ headerName: 'Тип маршрута', field: 'way', },
	{ headerName: 'Номер из Маркета', field: 'marketNumber', },
	{ headerName: 'Погрузочный номер', field: 'loadNumber', },
	{ headerName: 'Условия поставки', field: 'incoterms', wrapText: true, autoHeight: true, },
	{ headerName: 'Точки загрузки', field: 'loadPoints', wrapText: true, autoHeight: true, },
	{ headerName: 'Точки выгрузки', field: 'unloadPoints', wrapText: true, autoHeight: true, },
	{ headerName: 'Тип кузова', field: 'typeTruck', },
	{ headerName: 'Паллеты', field: 'summPall', width: 100, },
	{
		headerName: 'Объем', field: 'summVolume', width: 100,
		valueFormatter: (params) => params.value && `${params.value} м.куб.`
	},
	{
		headerName: 'Масса', field: 'summWeight', width: 100,
		valueFormatter: (params) => params.value && `${params.value} кг`
	},
	{ 
		headerName: 'Комментарии', field: 'comment', 
		width: 240, cellClass: 'font-weight-bold more-text',
		wrapText: true, autoHeight: true,
	},
	{
		headerName: 'Статус', field: 'statusToView',
		wrapText: true, autoHeight: true,
	},
	{ headerName: 'Логист', field: 'logistToView', },
	{ headerName: 'Контактное лицо контрагента', field: 'contact', wrapText: true, autoHeight: true, },
	{ headerName: 'Сверка УКЗ', field: 'controlToView', width: 100, },
	{ headerName: 'Менеджер', field: 'manager', wrapText: true, autoHeight: true, },
	{ headerName: 'Телефон менеджера', field: 'telephoneManager', wrapText: true, autoHeight: true, },
	{ headerName: 'Тип загрузки авто', field: 'typeLoad', },
	{ headerName: 'Способ загрузки авто', field: 'methodLoad', },
	{ headerName: 'Груз', field: 'cargo', },
	{ headerName: 'Температурные условия', field: 'temperature', },
	{ headerName: 'Штабелирование', field: 'stackingToView', },
]
const columnDefsL3 = [
	{ headerName: "№", field: "pointNumber", flex: 1 },
	{ headerName: "Тип точки", field: "type" },
	{ headerName: 'Дата', field: 'dateToView', },
	{ headerName: "Время", field: "time", },
	{ headerName: "Коды ТН ВЭД", field: "tnvd", flex: 6 },
	{ headerName: "Информация о грузе", field: "info", tooltipField: "info", flex: 8 },
	{ headerName: "Адрес склада", field: "bodyAddress", flex: 8 },
	{ headerName: "Время работы", field: "timeFrame" },
	{ headerName: "Контактное лицо", field: "contact", flex: 6 },
	{ headerName: "Адрес таможни", field: "customsAddress", flex: 8 },
]
const gridOptions = {
	columnDefs: columnDefsL1,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: "px-1",
		cellClass: "px-2",
		wrapText: true,
		autoHeight: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		resizable: true,
		flex: 2,
		suppressMenu: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	getContextMenuItems: getContextMenuItems,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	masterDetail: true,
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: {
			columnDefs: columnDefsL2,
			rowClassRules: rowClassRules,
			defaultColDef: {
				headerClass: "px-2",
				cellClass: "px-2 text-center",
				width: 160,
				resizable: true,
				sortable: true,
				suppressMenu: true,
				filter: true,
				floatingFilter: true,
			},
			rowSelection: "multiple",
			suppressRowClickSelection: true,
			suppressDragLeaveHidesColumns: true,
			// getContextMenuItems: getContextMenuItems,
			enableBrowserTooltips: true,
			localeText: AG_GRID_LOCALE_RU,
			masterDetail: false,
			detailRowAutoHeight: true,
			detailCellRendererParams: {
				detailGridOptions: {
					columnDefs: columnDefsL3,
					defaultColDef: {
						headerClass: "px-1",
						cellClass: "px-2",
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
				getDetailRowData: (params) => params.successCallback(params.data.addressesToView),
			},
		},
		getDetailRowData: (params) => params.successCallback(params.data.order),
	},
}

window.onload = async () => {
	const orderSearchForm = document.querySelector('#orderSearchForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const gridDiv = document.querySelector('#myGrid')
	const cancelOrderForm = document.querySelector('#cancelOrderForm')

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
	const orders = await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)

	// отрисовка таблицы
	renderTable(gridDiv, gridOptions, orders)

	// получение настроек таблицы из localstorage
	// restoreColumnState()
	// restoreFilterState()

	// автозаполнение полей дат в форме поиска заявок
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit', searchFormSubmitHandler)

	// листнер на отправку формы отмены заявки
	cancelOrderForm.addEventListener('submit', cancelOrderFormSubmitHandler)

	// очистка формы при закрытии модального окна формы
	$('#cancelOrderModal').on('hide.bs.modal', (e) => cancelOrderForm.reset())
}

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	const unloadPointList = getUnloadPointList(mappingData)

	gridOptions.api.setRowData(unloadPointList)
	gridOptions.api.hideOverlay()
}
async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const orderSearchForm = document.querySelector('#orderSearchForm')

	const dateStart = orderSearchForm.date_from.value
	const dateEnd = orderSearchForm.date_to.value
	const counterparty = orderSearchForm.searchName.value

	const orders = counterparty.length
		? await getData(`${getSearchOrderBaseUrl}${dateStart}&${dateEnd}&${counterparty}`)
		: await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)

	if (!orders || !orders.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(orders)
	const unloadPointList = getUnloadPointList(mappingData)

	gridOptions.api.setRowData(unloadPointList)
	gridOptions.api.hideOverlay()
}

function getUnloadPointList(orders) {
	return orders
		.reduce((acc, order) => {
			order.addressesToView.forEach(address => {
				if (address.type === 'Выгрузка') {
					address.order = [ order ]
					address.idOrder = order.idOrder
					address.marketNumber = order.marketNumber
					address.counterparty = order.counterparty
					address.status = order.status
					address.statusToView = order.statusToView
					address.loadPointInfo = getLoadPointInfo(order)
						.map((info, i) => `${i + 1}) ${info}`)
						.join(' ************** ')

					acc.push(address)
				}
			})
			return acc
		}, [])
		.sort((a, b) => b.idOrder - a.idOrder)
}

function getLoadPointInfo(order) {
	return order.addressesToView
		.filter(address => address.type === 'Загрузка')
		.reduce((acc, loadPoint) => {
			const loadPointInfo = []
			loadPoint.dateToView && loadPointInfo.push(loadPoint.dateToView)
			loadPoint.time && loadPointInfo.push(loadPoint.time.slice(0,5))
			loadPoint.bodyAddress && loadPointInfo.push(loadPoint.bodyAddress)
			acc.push(loadPointInfo.join('; '))
			return acc
		}, [])
}

function getMappingData(data) {
	return data
		.filter(order => order.way === 'РБ')
		.map(order => {
			const dateCreateToView = dateHelper.getFormatDate(order.dateCreate)
			const dateDeliveryToView = dateHelper.getFormatDate(order.dateDelivery)
			const controlToView = order.control ? 'Да' : 'Нет'
			const telephoneManagerToView = order.telephoneManager ? `; тел. ${order.telephoneManager}` : ''
			const managerToView = `${order.manager}${telephoneManagerToView}`
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

			const filtredAdresses = order.addresses.filter(address => address.isCorrect)
			const addressesToView = filtredAdresses
				.sort(pointSorting)
				.map(getPointToView)

			const loadPoints = filtredAdresses
				.filter(address => address.type === "Загрузка")
				.sort((a, b) => a.idAddress - b.idAddress)
				.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
				.join(' ')

			const unloadPoints = filtredAdresses
				.filter(address => address.type === "Выгрузка")
				.sort((a, b) => a.idAddress - b.idAddress)
				.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
				.join(' ')

			const loadDateToView = addressesToView.length ? addressesToView[0].dateToView : ''

			const summPall = filtredAdresses
				.filter(address => address.type === "Загрузка")
				.reduce((acc, address) => {
					if (address.pall) {
						const pall = Number(address.pall)
						acc += pall
						return acc
					}
				}, 0)

			const summVolume = filtredAdresses
				.filter(address => address.type === "Загрузка")
				.reduce((acc, address) => {
					if (address.volume) {
						const volume = Number(address.volume)
						acc += volume
						return acc
					}
				}, 0)

			const summWeight = filtredAdresses
				.filter(address => address.type === "Загрузка")
				.reduce((acc, address) => {
					if (address.weight) {
						const weight = Number(address.weight)
						acc += weight
						return acc
					}
				}, 0)

			return {
				...order,
				dateCreateToView,
				dateDeliveryToView,
				controlToView,
				addressesToView,
				loadDateToView,
				managerToView,
				statusToView,
				stackingToView,
				logistToView,
				unloadWindowToView,
				loadPoints,
				unloadPoints,
				summPall: summPall ? summPall : null,
				summVolume: summVolume ? summVolume : null,
				summWeight: summWeight ? summWeight : null
			}
		})
}

function getContextMenuItems(params) {
	if (!params.node) return

	const orderData = params.node.data
	const idOrder = orderData.idOrder
	const status = orderData.status

	const result = [
		{
			name: `Просмотреть заявку`,
			action: () => {
				showOrder(idOrder)
			},
			icon: uiIcons.fileText,
		},
		{
			name: `Отменить заявку по дате выгрузки`,
			disabled: status !== 17 && status !== 20,
			action: () => {
				const unloadDate = orderData.dateToView
				deleteOrder(idOrder, unloadDate)
			},
			icon: uiIcons.trash,
		},
		"separator",
		"excelExport",
	]

	return result
}

function showOrder(idOrder) {
	window.location.href = `./orders/order?idOrder=${idOrder}`
}

function deleteOrder(idOrder, unloadDate) {
	$('#cancelOrderModal').modal('show')

	const canceledOrderId = document.querySelector('#canceledOrderId')
	const idOrderInput = document.querySelector('#idOrder')
	const canceledOrderDate = document.querySelector('#canceledOrderDate')

	canceledOrderId.innerText = idOrder
	idOrderInput.value = idOrder
	canceledOrderDate.innerText = unloadDate
}

function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./orders/order?idOrder=${data.idOrder}`

	return `<a class="text-primary" href="${link}">${params.value}</a>`
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

// обработчик формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	updateTable()
}

// обработчик формы отмены заявки по дате выгрузки
async function cancelOrderFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const idOrder = formData.get('idOrder')
	const newUnloadDate = formData.get('newUnloadDate')

	if (!idOrder || !newUnloadDate) {
		snackbar.show('Ошибка - недостаточно данных')
		return
	}

	ajaxUtils.get({
		url: `../../api/manager/deleteOrderHasOrderSupport/${idOrder}&${newUnloadDate}`,
		successCallback: (res) => {
			$('#cancelOrderModal').modal('hide')
			snackbar.show('Заявка отменена')
			updateTable()
		}
	})
}

// функция установки даты и времени выгрузки
function setUnloadWindowTime(params) {
	const idAddress = params.data.idAddress
	const time = params.newValue.slice(0, 5)

	if (!idAddress || !time) {
		snackbar.show('Окно на выгрузку не установлено')
		return
	}
	gridOptions.api.showLoadingOverlay()
	ajaxUtils.get({
		url: `${setWindowUnloadTimeBaseUrl}${idAddress}&${time}`,
		successCallback: (res) => {
			const message = res.message ? res.message : 'Неизвестная ошибка'
			snackbar.show(message)
			updateTable()
			gridOptions.api.hideOverlay()
		},
		errorCallback: updateTable
	})
}

function setUnloadWindowDate(params) {
	const rowNode = params.node
	const idAddress = params.data.idAddress
	const newValue = params.newValue
	const oldValue = params.oldValue
	const date = dateHelper.changeFormatToInput(newValue)

	if (!idAddress) {
		snackbar.show('Дата не изменена')
		return
	}

	if (!date) {
		rowNode.setDataValue('dateToView', oldValue)
		snackbar.show('Дата не изменена')
		return
	}

	gridOptions.api.showLoadingOverlay()

	ajaxUtils.get({
		url: `${setWindowUnloadDateBaseUrl}${idAddress}&${date}`,
		successCallback: (res) => {
			if (res.status === '200') {
				gridOptions.api.hideOverlay()
			} else {
				updateTable()
			}
			const message = res.message ? res.message : 'Неизвестная ошибка'
			snackbar.show(message)
		},
		errorCallback: updateTable
	})
}

// функция установки продолжительности выгрузки
function setUnloadDuration(params) {
	console.log(params)
	// const idOrder = params.data.idOrder
	// const duration = params.newValue.slice(0, 5)

	// if (!idOrder || !duration) {
	// 	snackbar.show('Продолжительность выгрузки не установлена')
	// 	return
	// }
	
	// ajaxUtils.get({
	// 	url: `${setUnloadDurationBaseUrl}${idOrder}&${duration}`,
	// 	successCallback: (res) => {
	// 		console.log(res)
	// 		const message = res.message ? res.message : 'Неизвестная ошибка'
	// 		snackbar.show(message)
	// 	},
	// 	errorCallback: updateTable
	// })
}
