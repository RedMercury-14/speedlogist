import { snackbar } from "./snackbar/snackbar.js"
import { store } from "./slots/store.js"
import { eventColors, userMessages } from "./slots/constants.js"
import { gridOptions, renderTable, showInternalMovementOrders, updateTableData, updateTableRow } from "./slots/agGridUtils.js"
import {
	isMobileDevice,
	setCalendarWidth,
	showMobileTooltop,
	createDraggableElement,
	updateDropZone,
	copyToClipboard,
	setStockAttr,
	createCalendarDateInput,
	searchSlot,
} from "./slots/calendarUtils.js"
import {
	blurActiveElem, cookieHelper, debounce, isAdmin, isLogist, isObserver,
	isOrderSupport, isProcurement, isSlotsObserver, isStockProcurement
} from "./utils.js"
import { uiIcons } from "./uiIcons.js"
import { wsSlotUrl } from "./global.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import {
	wsSlotOnCloseHandler,
	wsSlotOnErrorHandler,
	wsSlotOnMessageHandler,
	wsSlotOnOpenHandler,
} from "./slots/wsHandlers.js"
import {
	checkEventId,
	isInvalidEventDate,
} from "./slots/rules.js"
import {
	convertToDayMonthTime,
	getMinUnloadDate,
	getSlotInfoToCopy,
} from "./slots/dataUtils.js"
import { deselectAllCheckboxes, gridColumnLocalState } from "./AG-Grid/ag-grid-utils.js"
import { pallChartConfig, updatePallChart, } from "./slots/chartJSUtils.js"
import {
	addNewOrderBtnListner,
	adminActionListner,
	confitmSlotBtnListner,
	copySlotInfoBtnListner,
	eventInfoModalClosedListner,
	reloadBtnListner,
	sidebarListners,
	slotInfoListners,
	slotSearchFormListner,
	statusInfoLabelLIstners,
	stockSelectListner,
	updateSlotReasonCancelListner,
	updateSlotReasonFormListner,
	updateSlotReasonSelectListner,
} from "./slots/listners.js"
import { MAX_PALL_RESTRICTIONS } from "./globalRules/maxPallRestrictions.js"
import {
	checkEventsForBooking,
	confirmSlot,
	editMarketInfo,
	getMoveOrdersReport,
	getOrderFromMarket,
	loadOrder,
	setOrderLinking,
	updateOrder,
} from "./slots/api.js"
import {
	dateSetHandler,
	eventClickHandler,
	eventContentHandler,
	eventDidMountHandler,
	eventDragStartHandler,
	eventDragStopHandler,
	eventDropHandler,
	eventReceiveHandler,
	eventsHandler,
	eventsSetHandler,
	resourcesHandler
} from "./slots/calendarHandlers.js"
import { checkCombineOrders } from "./procurementControlUtils.js"
import { backgroundEvents, slotsSettings } from "./globalRules/slotsRules.js"


const LOCAL_STORAGE_KEY = 'AG_Grid_column_settings_to_Slots'

const debouncedEventsSetHandler = debounce(eventsSetHandler, 200)
const debouncedSaveColumnState = debounce(saveColumnState, 300)

const role = store.getRole()

// опции таблицы
const orderTableGridOption = {
	...gridOptions,
	// запрет редактирования Информации из Маркета для логистов и наблюдателей
	columnDefs: gridOptions.columnDefs.map(columnDef => {
		return columnDef.field === "marketInfo" && (isLogist(role) || isSlotsObserver(role))
			? { ...columnDef, editable: false }
			: columnDef
	}),
	getContextMenuItems: getContextMenuItemsForOrderTable,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,

	onCellValueChanged: cellValueChangedHandler,
}

// вебсокет
const wsSlot = new WebSocket(wsSlotUrl)
wsSlot.onopen = wsSlotOnOpenHandler
wsSlot.onclose = wsSlotOnCloseHandler
wsSlot.onerror = wsSlotOnErrorHandler
wsSlot.onmessage = (e) => wsSlotOnMessageHandler(e, orderTableGridOption)

let calendar
let slots = []
let pallLineChart

const calendarOptions = {
	timeZone: "ru",
	locale: "ru",
	initialView: "resourceTimeGridDay",

	// firstDay: moment(),
	allDaySlot: false,
	hiddenDays: [],
	// validRange: (today) => {
	// 	return {
	// 		start: today,
	// 	}
	// },

	// для мобилок
	longPressDelay: 500,

	height: "100%",

	// slotMinTime: "06:00",
	// slotMaxTime: "22:00",
	slotDuration: "00:10",
	slotLabelInterval: "00:10",
	slotLabelFormat: {
		hour: "numeric",
		minute: "2-digit",
		omitZeroMinute: false,
		meridiem: "",
	},

	nowIndicator: true,
	navLinks: true,

	headerToolbar: {
		left: "prev,next today",
		center: "title",
		right: "",
	},

	viewDidMount: (info) => {
		createCalendarDateInput(calendar)
	},

	titleFormat: {
		year: 'numeric', month: 'long', day: 'numeric', weekday: 'short'
	},

	views: {
		resourceTimeGridTwoDay: {
			type: "resourceTimeGrid",
			duration: { days: 2 },
			buttonText: "2 days",
		},
	},

	droppable: true,
	selectable: false,
	selectOverlap: false,
	eventOverlap: (stillEvent, movingEvent) => stillEvent.display === 'background',
	eventDurationEditable: false,
	eventColor: eventColors.default,

	// рампы
	resources: (info, successCallback, failureCallback) => resourcesHandler(successCallback, slots),

	// диапазон дат календаря был первоначально установлен
	// или изменен каким-либо образом и был обновлен DOM
	datesSet: dateSetHandler,

	// события календаря
	events: eventsHandler,

	// контент события
	eventContent: eventContentHandler,

	// обработчик события встраивания ивента в DOM
	eventDidMount: eventDidMountHandler,

	eventDragStart: (info) => eventDragStartHandler(info, wsSlot),
	eventDragStop: eventDragStopHandler,

	// обработчик события перемещения собития камендаря
	// (срабатывает при установке события на новое место)
	eventDrop: async (info) => await eventDropHandler(info, orderTableGridOption),

	// вызывается, когда внешний перетаскиваемый элемент со связанными
	// данными о событии перетаскивается в календарь (дроп)
	eventReceive: async (info) => await eventReceiveHandler(info, orderTableGridOption, orderDateClickHandler),

	// обработчик события клика по собитию календаря
	eventClick: (info) => eventClickHandler(info, orderTableGridOption, wsSlot),

	// обработчик события изменения количества событий на календаре
	// (срабатывает при изменении количества событий на календаре, 
	// а также при изменении отображаемой даты)
	eventsSet: debouncedEventsSetHandler,
}

document.addEventListener('DOMContentLoaded', async function() {
	if(isMobileDevice()) {
		showMobileTooltop()
	}
	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, orderTableGridOption, [])
	// получаем настройки колонок таблицы
	restoreColumnState()

	const eventContainer = document.querySelector("#external-events")

	// создание календаря
	const calendarEl = document.querySelector("#calendar")
	calendar = new FullCalendar.Calendar(calendarEl, calendarOptions)
	// предотвращаем правый клик в календаре
	calendarEl.addEventListener('mousedown', (e) => {
		if (e.button === 2) e.preventDefault()
	})

	// создание графика для паллетовместимости
	const ctx = document.querySelector('#pallLineChart')
	pallLineChart = new Chart(ctx, pallChartConfig)

	// поиск слота в календаре
	slotSearchFormListner(slotSearchFormSubmitHandler)
	// добавление нового заказа
	addNewOrderBtnListner(eventContainer, addNewOrderButtonHandler)
	// кнопки боковой панели
	sidebarListners()
	// кнопка подтверждения слота
	confitmSlotBtnListner(confirmSlotBtnClickHandler)
	// кнопка перезагрузки страницы
	reloadBtnListner()
	// кнопка копирования информации о слоте
	copySlotInfoBtnListner(copySlotInfoBtnClickHandler)
	// иконка информации о статусах заказов
	statusInfoLabelLIstners()
	// кнопки информации о слоте
	slotInfoListners()
	// закрытие модального окна с информацией об ивенте
	eventInfoModalClosedListner()
	// обработка действия для админа
	adminActionListner(doAdminAction)
	// указание причины переноса слота
	updateSlotReasonFormListner(gettingReasonForUpdateSlot)
	// обработчик выбора причины переноса слота
	updateSlotReasonSelectListner()
	// отмена указания причины переноса слота
	updateSlotReasonCancelListner(cancelReasonForUpdateSlot)

	// отображение стартовых данных
	if (window.initData) {
		await initStartData()
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData()
		})
	}

	// снятие фокуса с активного элемента при закрытии модального окна
	$('#eventInfoModal').on('hide.bs.modal', blurActiveElem)
	$('#orderCalendarModal').on('hide.bs.modal', blurActiveElem)
	$('#displayMessageModal').on('hide.bs.modal', blurActiveElem)
	$('#pallChartModal').on('hide.bs.modal', blurActiveElem)
	$('#updateSlotReasonModal').on('hide.bs.modal', blurActiveElem)

	showSlotNewsModal()
})


// установка стартовых данных
async function initStartData() {
	// не загружаем заказы в стор со статусов 5 (виртуальные заказы)
	// const filtredOrders = window.initData.filter(order => order.status !== 5) -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
	const ordersWithoutAHO = window.initData.filter(order => order.way !== 'АХО')
	store.setOrders(ordersWithoutAHO)
	// добавляем ивенты на виртуальные склады
	store.setStockEvents()
	// добавляем фоновые зоны
	store.setBGEvents(backgroundEvents)
	// сохраняем ограничения паллет
	store.setMaxPallRestrictions(MAX_PALL_RESTRICTIONS)
	const stocks = store.getStocks()
	// добавляем склады в селект и вешаем обработчик
	stockSelectListner(stocks, calendar, stockSelectOnChangeHandler)
	// обновляем ивенты календаря при изменении стора
	store.subscribe(() => calendar.refetchEvents())
	// рендеринг календаря и таблицы
	calendar.render()
	// скрываем оверлей загрузки
	bootstrap5overlay.hideOverlay()
	// сообщаем о готовности приложения к работе
	store.setReady(true)
	window.initData = null
}

// контекстное меню таблицы заказов
function getContextMenuItemsForOrderTable(params) {
	const rowNode = params.node
	if(!rowNode) return
	const role = store.getRole()
	const login = store.getLogin()
	const order = rowNode.data
	const idOrder = order.idOrder
	const marketNumber = order.marketNumber
	const idRamp = order.idRamp
	const status = order.status

	const selectedRowsData = params.api.getSelectedRows()
	const isVeryfyCombineOrders = !selectedRowsData.filter(order => order.status !== 5 && order.status !== 6).length
	const isExistLink = selectedRowsData.some(order => order.link)

	const result = [
		{
			disabled: !!idRamp || status !== 6 || isLogist(role) || isAdmin(role) || isSlotsObserver(role) || isObserver(role),
			name: `Создать слот заказа на самовывоз`,
			action: () => {
				const eventContainer = document.querySelector("#external-events")
				createNewOrder(marketNumber, eventContainer)
				// createNewOrder(order, eventContainer) -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
			},
			icon: uiIcons.clickBoadrPlus
		},
		{
			disabled: !!idRamp || status !== 5 || isLogist(role) || isAdmin(role) || isSlotsObserver(role) || isObserver(role),
			name: `Создать слот заказа от поставщика`,
			action: () => {
				const eventContainer = document.querySelector("#external-events")
				createNewOrder(marketNumber, eventContainer)
				// createNewOrder(order, eventContainer) -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
			},
			icon: uiIcons.clickBoadrPlus
		},
		{
			disabled: !!idRamp || status !== 5 || isLogist(role) || isAdmin(role) || isSlotsObserver(role) || isObserver(role),
			name: `Обновить заказ`,
			action: () => {
				// получаем обновленный заказ по номеру Маркета и обновляем в сторе и таблице
				getOrderFromMarket(marketNumber, null, updateOrderFromMarket)
			},
			icon: uiIcons.refresh
		},
		"separator",
		{
			disabled: !!idRamp || (status !== 5 && status !== 6) || order.link
					|| isLogist(role) || isSlotsObserver(role) || isObserver(role),
			name: `Объединить заказ с ...`,
			action: () => {
				combineOrderByMarketNumber(marketNumber, orderTableGridOption)
			},
		},
		{
			disabled: selectedRowsData.length < 2 || !isVeryfyCombineOrders || isExistLink
					|| isLogist(role) || isSlotsObserver(role) || isObserver(role),
			name: `Объединить выделенные заказы`,
			action: () => {
				combineSelectedOrders(selectedRowsData, orderTableGridOption)
			},
		},
		"separator",
		"excelExport",
	]

	return result
}

// добавление нового заказа в дроп зону
function createNewOrder(marketNumber, eventContainer) {
	const stocks = store.getStocks()
	const dropeZone = store.getDropZone()
	const login = store.getLogin()
	const currentStock = store.getCurrentStock()
	// const marketNumber = order.marketNumber -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ

	if (checkEventId(marketNumber, stocks, dropeZone)) {
		snackbar.show(userMessages.checkEventId)
		return
	}

	// const order = store.getOrderByMarketNumber(marketNumber) -- УДАЛИТЬ ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
	const order = store.getOrderByMarketNumber(marketNumber)

	if (!order) {
		snackbar.show(userMessages.orderNotFound)
		return
	}

	store.addEventToDropZone(order)
	createDraggableElement(eventContainer, order, login, currentStock)
}

// обработчик добавления заказа при нажатии на кнопку
function addNewOrderButtonHandler(e, eventContainer) {
	const marketNumber = prompt('Введите номер из Маркета заказа для загрузки:')
	if (!marketNumber) return
	// проверка, есть ли заказ с таким номером
	const existingOrder = store.getOrderByMarketNumber(marketNumber)
	if (existingOrder) {
		snackbar.show('Заказ с таким номером уже существует')
		return
	}
	// получаем заказ из маркета и создаем ивент в дропзоне
	getOrderFromMarket(marketNumber, eventContainer, createNewOrderFromMarket)
}

// обработчик изменения текущего склада (выбора из списка)
function stockSelectOnChangeHandler(e, calendar) {
	const value = e.target.value
	const stocks = store.getStocks()
	const role = store.getRole()
	const login = store.getLogin()
	const dropZone = store.getState().dropZone
	const selectedStock = stocks.find(stock => stock.id == value)
	store.setCurrentStock(selectedStock)
	slots = selectedStock.ramps

	// обновляем данные паллетовместимости склада за период (модалка)
	const nowDateStr = new Date().toISOString().slice(0, 10)
	const pallChartData = store.getMaxPallDataByPeriod(
		selectedStock.id,
		nowDateStr,
		slotsSettings.PALL_CHART_DATA_DAY_COUNT
	)
	updatePallChart(pallLineChart, pallChartData)

	// подключаем кнопку "Добавить заказ" для закупок
	if (isProcurement(role) || isOrderSupport(role)) {
		const addNewOrderButton = document.querySelector("#addNewOrder")
		addNewOrderButton.removeAttribute("disabled")
	}

	// добавляем для календаря текущий склад в атрибуты
	setStockAttr(selectedStock.id)

	snackbar.show(`Выбран ${selectedStock.name}`)

	// установка ширины календаря
	setCalendarWidth(selectedStock.ramps.length)

	// установка временных рамок календаря для каждого дня
	calendar.setOption('slotMinTime', selectedStock.workingHoursStart)
	calendar.setOption('slotMaxTime', selectedStock.workingHoursEnd)
	// скрываем дни, которые являются выходными днями
	calendar.setOption('hiddenDays', selectedStock.weekends)
	// обновляем рампы и ивенты
	calendar.refetchResources()
	calendar.refetchEvents()

	// обновляем ивенты в дропзоне
	updateDropZone(dropZone, login, selectedStock)
	// обновляем таблицу заказов
	updateTableData(orderTableGridOption, store.getCurrentStockOrders())
	// отображаем только внутренние перемещения для соответствующей роли
	if (isStockProcurement(role)) showInternalMovementOrders(orderTableGridOption)
}

// обработчик нажатия на кнопку подтверждения/снятия подтверждения
function confirmSlotBtnClickHandler(e) {
	const fcEvent = store.getSlotToConfirm()
	const role = store.getRole()
	const order = fcEvent.extendedProps.data
	const action = e.target.dataset.action
	const status = order.status

	// проверка даты начала ивента при подтверждении слота
	if (action === 'save') {
		const minUnloadDate = getMinUnloadDate(order, role)
		const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
		if (isInvalidEventDate({ event: fcEvent }, minUnloadDate)) {
			snackbar.show(userMessages.dateConfirmError(minUnloadDateStr))
			return
		}
	}

	// 
	if (action === 'unSave' && status === 20) return
	confirmSlot(fcEvent, action, orderTableGridOption)
}

// обработчик нажатия на кнопку копирования информации о слоте
function copySlotInfoBtnClickHandler(e) {
	const fcEvent = store.getSlotToConfirm()
	const currentStock = store.getCurrentStock()
	const slotInfo = getSlotInfoToCopy(fcEvent, currentStock)
	copyToClipboard(slotInfo)
}

// обработчик отправки формы поиска слота в календаре
function slotSearchFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const searchValue = formData.get('searchValue')
	if (!searchValue) return

	const events = store.getCalendarEvents()
	const currentDate = store.getCurrentDate()
	const currentStock = store.getCurrentStock()
	searchSlot({
		searchValue,
		events,
		calendar,
		currentDate,
		currentStock
	})
}

// обработка выбора даты заказа согласно графику поставок
function orderDateClickHandler(e) {
	const orderDateCell = e.currentTarget
	const dateOrderOrl = orderDateCell.dataset.orderDate
	const info = store.getCalendarInfo()

	$("#orderCalendarModal").modal('hide')

	if (!dateOrderOrl) {
		info.revert()
		return
	}

	loadOrder(info, orderTableGridOption, dateOrderOrl)
}

// обработка выбора админского действия в слотах
async function doAdminAction(e) {
	const select = e.target
	const action = select.value

	if (!action) return

	if (action === 'checkBookingForCurrentDate') {
		select.value = ''
		select.blur()
		const events = store.getTodayEvents()
		if (!events || events.length === 0) {
			snackbar.show('На текущую дату нет заказов')
			return
		}
		const externalMovementEvents = events.filter(event =>
			event.extendedProps.data.isInternalMovement !== 'true'
		)
		await checkEventsForBooking(externalMovementEvents)
		return
	}

	// рекомендации по перемещениям
	if (action === '1700to1800' || action === '1800to1700') {
		select.value = ''
		select.blur()
		getMoveOrdersReport(action)
		return
	}
}

// указание причины обновления слота
function gettingReasonForUpdateSlot(e) {
	e.preventDefault()
	const form = e.target
	const formData = new FormData(form)
	const data = Object.fromEntries(formData)
	const reason = data.updateSlotReason === 'Иное'
		? data.updateSlotOtherReason
		: data.updateSlotReason

	const info = store.getCalendarInfo()
	if (!reason) {
		info.revert()
		return
	}

	updateOrder(info, orderTableGridOption, reason)
	form.reset()
	$("#updateSlotReasonModal").modal('hide')
}

// отмена пуказания причины переноса слота
function cancelReasonForUpdateSlot(e) {
	const info = store.getCalendarInfo()
	info.revert()
	const updateSlotReasonForm = document.querySelector('#updateSlotReasonForm')
	updateSlotReasonForm && updateSlotReasonForm.reset()
	$("#updateSlotReasonModal").modal('hide')
}

// объединение заказов с указанием номеров из Маркета
function combineOrderByMarketNumber(marketNumber, gridOptions) {
	const value = prompt(
		`Введите номер заказа из Маркета (или несколько номеров через пробел),`
		+ ` которые необходимо объединить с заказом ${marketNumber}:`
	)
	if (!value) return

	const regex = /^\d{8,13}(\s\d{8,13})*$/
	if (!regex.test(value)) {
		alert('Неверный формат ввода!')
		return
	}

	const numbers = value.split(' ')
	numbers.push(marketNumber)

	const unicNumbers =  numbers.filter((num, i, arr) => i === arr.indexOf(num))
	const orders = unicNumbers.map(num => store.getOrderByMarketNumber(num))

	const errorMessage = checkCombineOrders(orders)
	if (errorMessage) {
		alert(errorMessage)
		return
	}

	const ids = orders.map(order => order.idOrder)
	setOrderLinking(ids, gridOptions)
}

function combineSelectedOrders(selectedRowsData, gridOptions) {
	const errorMessage = checkCombineOrders(selectedRowsData)
	if (errorMessage) {
		alert(errorMessage)
		deselectAllCheckboxes(gridOptions)
		return
	}

	const ids = selectedRowsData.map(order => order.idOrder)
	setOrderLinking(ids, gridOptions)
	deselectAllCheckboxes(gridOptions)
}

// функция создания нового заказа по информации из Маркета
function createNewOrderFromMarket(data, marketNumber, eventContainer) {
	const order = data.order
	//-------------------------------------------------------------
	// ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
	// // добавляем заказ в стор
	// store.addNewOrderFromMarket(order) -- УДАЛИТЬ
	// // обновляем данные таблицы
	// updateTableData(orderTableGridOption, store.getCurrentStockOrders()) -- УДАЛИТЬ
	// // добавляем заказ в дроп зону
	// createNewOrder(order, eventContainer) ЗАМЕНА НА ORDER
	//-------------------------------------------------------------
	// добавляем заказ в стор
	store.addNewOrderFromMarket(order)
	// обновляем данные таблицы
	updateTableData(orderTableGridOption, store.getCurrentStockOrders())
	// добавляем заказ в дроп зону
	createNewOrder(marketNumber, eventContainer)
}
// функция обновления заказа по информации из Маркета
function updateOrderFromMarket(data, marketNumber, eventContainer) {
	const order = data.order
	// обновляем заказ в сторе
	store.updateOrderFromMarket(order)
	// обновляем данные таблицы
	updateTableData(orderTableGridOption, store.getCurrentStockOrders())
}

// функции управления состоянием колонок
function saveColumnState() {
	gridColumnLocalState.saveState(orderTableGridOption, LOCAL_STORAGE_KEY)
}
function restoreColumnState() {
	gridColumnLocalState.restoreState(orderTableGridOption, LOCAL_STORAGE_KEY)
}

// коллбэк бля редактирования ячеек таблицы
function cellValueChangedHandler(params) {
	const columnName = params.column.colId

	if (columnName === "marketInfo") {
		editMarketInfo(params, orderTableGridOption, setOldMarketInfo)
	}
}

// функция установки старого значения информации из Маркета
function setOldMarketInfo(order, oldValue, gridOption) {
	// устанавливаем старое значение
	order.marketInfo = oldValue
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(order)
	// вернуть старое значение в таблицу
	updateTableRow(gridOption, updatedOrder)
}

// функции для модального окна обновлений в слотах
function showSlotNewsModal() {
	const value = cookieHelper.getCookie('_slotNews2')
	if (value) return 
	setSlotNewsCookie('ок')
	$('#slotNewsModal').modal('show')
}
function setSlotNewsCookie(value) {
	let date = new Date(Date.now() + 31562e7)
	date = date.toUTCString()
	cookieHelper.setCookie('_slotNews2', value, { expires: date, })
}