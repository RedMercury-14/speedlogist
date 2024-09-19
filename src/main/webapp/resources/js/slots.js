import { snackbar } from "./snackbar/snackbar.js"
import { store } from "./slots/store.js"
import {
	confirmSlotUrl,
	deleteOrderUrl,
	editMarketInfoBaseUrl,
	eventColors,
	getMarketOrderUrl,
	getOrdersForSlotsBaseUrl,
	loadOrderUrl,
	slotStocks,
	slotsSettings,
	updateOrderUrl,
	userMessages,
} from "./slots/constants.js"
import { gridOptions, renderTable, showInternalMovementOrders, updateTableData, updateTableRow } from "./slots/agGridUtils.js"
import {
	createCloseEventButton,
	createEventElement,
	isMobileDevice,
	setCalendarWidth,
	showMobileTooltop,
	setPallInfo,
	createDraggableElement,
	errorHandler_100status,
	showReloadWindowModal,
	createPopupButton,
	showEventInfoPopup,
	hideEventInfoPopup,
	addSmallHeaderClass,
	updateDropZone,
	copyToClipboard,
	showMessageModal,
	setCurrentDateAttr,
	setStockAttr,
	createCalendarDateInput,
	errorHandler_105status,
} from "./slots/calendarUtils.js"
import { dateHelper, debounce, getData, isAdmin, isLogist, isSlotsObserver, isStockProcurement } from "./utils.js"
import { uiIcons } from "./uiIcons.js"
import { wsSlotUrl } from "./global.js"
import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { addCalendarEvent, deleteCalendarEvent, updateCalendarEvent, updateOrderAndEvent } from "./slots/eventControlMethods.js"
import {
	wsSlotOnCloseHandler,
	wsSlotOnErrorHandler,
	wsSlotOnMessageHandler,
	wsSlotOnOpenHandler,
} from "./slots/wsHandlers.js"
import {
	checkEventId,
	checkPallCount,
	checkPallCountForComingDates,
	checkSchedule,
	hasOrderInYard,
	isBackgroundEvent,
	isInvalidEventDate,
	isOldSlotNotInYard,
	isOldSupplierOrder,
	isOverlapWithInternalMovementTime,
	isOverlapWithShiftChange,
	methodAccessRules,
} from "./slots/rules.js"
import {
	convertToDayMonthTime,
	getDatesToSlotsFetch,
	getMinUnloadDate,
	getOrderDataForAjax,
	getSlotInfoToCopy,
} from "./slots/dataUtils.js"
import { gridColumnLocalState } from "./AG-Grid/ag-grid-utils.js"
import { tempMaxPallRestrictions } from "./slots/maxPallRestrictions.js"
import { updateChartData, getFormattedPallChartData, pallChartConfig, getMarkerBGColorData, } from "./slots/chartJSUtils.js"
import {
	addNewOrderBtnListner,
	confitmSlotBtnListner,
	copySlotInfoBtnListner,
	eventInfoModalClosedListner,
	reloadBtnListner,
	sidebarListners,
	slotInfoListners,
	statusInfoLabelLIstners,
	stockSelectListner,
} from "./slots/listners.js"


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
	resources: resourcesHandler,

	// диапазон дат календаря был первоначально установлен
	// или изменен каким-либо образом и был обновлен DOM
	datesSet: dateSetHandler,

	// события календаря
	events: eventsHandler,

	// контент события
	eventContent: eventContentHandler,

	// обработчик события встраивания ивента в DOM
	eventDidMount: eventDidMountHandler,

	eventDragStart: eventDragStartHandler,
	eventDragStop: eventDragStopHandler,

	// обработчик события перемещения собития камендаря
	// (срабатывает при установке события на новое место)
	eventDrop: eventDropHandler,

	// вызывается, когда внешний перетаскиваемый элемент со связанными
	// данными о событии перетаскивается в календарь (дроп)
	eventReceive: eventReceiveHandler,

	// обработчик события клика по собитию календаря
	eventClick: eventClickHandler,

	// обработчик события изменения количества событий на календаре
	// (срабатывает при изменении количества событий на календаре, 
	// а также при изменении отображаемой даты)
	eventsSet: debouncedEventsSetHandler,
}

window.onload = async function() {
	if(isMobileDevice()) {
		showMobileTooltop()
	}

	const gridDiv = document.querySelector('#myGrid')
	const eventContainer = document.querySelector("#external-events")

	// добавляем класс при хэдере меньшего размера
	addSmallHeaderClass()

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

	// получаем данные
	const { startDateStr, endDateStr } = getDatesToSlotsFetch(
		slotsSettings.DAY_COUNT_BACK,
		slotsSettings.DAY_COUNT_FORVARD
	)
	const marketData = await getData(`${getOrdersForSlotsBaseUrl}${startDateStr}&${endDateStr}`)
	// сохраняем заказы в стор
	store.setOrders(marketData)
	// добавляем ивенты на виртуальные склады
	store.setStockEvents()
	// сохраняем ограничения паллет
	store.setMaxPallRestrictions(tempMaxPallRestrictions)
	const stocks = store.getStocks()

	// добавляем склады в селект и вешаем обработчик
	stockSelectListner(stocks, calendar, stockSelectOnChangeHandler)
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

	// обновляем ивенты календаря при изменении стора
	store.subscribe(() => calendar.refetchEvents())
	// рендеринг календаря и таблицы
	calendar.render()
	renderTable(gridDiv, orderTableGridOption, [])
	// получаем настройки колонок таблицы
	restoreColumnState()
	// скрываем оверлей загрузки
	bootstrap5overlay.hideOverlay()
}

// контекстное меню таблицы заказов
function getContextMenuItemsForOrderTable(params) {
	const role = store.getRole()
	const order = params.node.data
	const marketNumber = order.marketNumber
	const idRamp = order.idRamp
	const status = order.status

	const result = [
		{
			disabled: !!idRamp || status !== 6 || isLogist(role) || isAdmin(role) || isSlotsObserver(role),
			name: `Создать слот заказа на самовывоз`,
			action: () => {
				const eventContainer = document.querySelector("#external-events")
				createNewOrder(marketNumber, eventContainer)
			},
			icon: uiIcons.clickBoadrPlus
		},
		{
			disabled: !!idRamp || status !== 5 || isLogist(role) || isAdmin(role) || isSlotsObserver(role),
			name: `Создать слот заказа от поставщика`,
			action: () => {
				const eventContainer = document.querySelector("#external-events")
				createNewOrder(marketNumber, eventContainer)
			},
			icon: uiIcons.clickBoadrPlus
		},
		{
			disabled: !!idRamp || status !== 5 || isLogist(role) || isAdmin(role) || isSlotsObserver(role),
			name: `Обновить заказ`,
			action: () => {
				// получаем обновленный заказ по номеру Маркета и обновляем в сторе и таблице
				getOrderFromMarket(marketNumber, null, updateOrderFromMarket)
			},
			icon: uiIcons.refresh
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

	if (checkEventId(marketNumber, stocks, dropeZone)) {
		snackbar.show(userMessages.checkEventId)
		return
	}

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
	updatePallChart(selectedStock)

	// подключаем кнопку "Добавить заказ" для закупок
	if (!isAdmin(role) && !isLogist(role) && !isSlotsObserver(role) && !isStockProcurement(role)) {
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
	// const role = store.getRole()
	// const order = fcEvent.extendedProps.data

	// // проверка даты начала ивента
	// const minUnloadDate = getMinUnloadDate(order, role)
	// const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
	// console.log("🚀 ~ confirmSlotBtnClickHandler ~ minUnloadDateStr:", minUnloadDateStr)
	// if (isInvalidEventDate({ event: fcEvent }, minUnloadDate)) {
	// 	snackbar.show(userMessages.dateDropError(minUnloadDateStr))
	// 	return
	// }

	const action = e.target.dataset.action
	const status = fcEvent.extendedProps.data.status
	if (action === 'unSave' && status === 20) return
	confirmSlot(fcEvent, action)
}

// обработчик нажатия на кнопку копирования информации о слоте
function copySlotInfoBtnClickHandler(e) {
	const fcEvent = store.getSlotToConfirm()
	const currentStock = store.getCurrentStock()
	const slotInfo = getSlotInfoToCopy(fcEvent, currentStock)
	copyToClipboard(slotInfo)
}


/* -------------- обработчики для календаря ------------------ */
function resourcesHandler(info, successCallback, failureCallback) {
	successCallback(slots)
}
function dateSetHandler(info) {
	const currentDateStr= info.startStr.split('T')[0]
	const currentStock = store.getCurrentStock()

	store.setCurrendDate(currentDateStr)

	// изменение информации о паллетах для данного склада
	if (currentStock) {
		const maxPall = store.getMaxPallByDate(currentStock.id, currentDateStr)
		const pallCount = store.getPallCount(currentStock, currentDateStr)
		// сохраняем в стор данные о паллетовместимости на текущем складе
		store.setCurrentMaxPall(maxPall)
		// изменяем данные по паллетовместимости у пользователя
		setPallInfo(pallCount, maxPall)
	}

	// добавляем для календаря текущую дату в атрибуты
	setCurrentDateAttr(currentDateStr)
}
function eventsHandler(info, successCallback, failureCallback) {
	const currentStock = store.getCurrentStock()
	const events = currentStock
		? store.getState().stocks.find(stock => stock.id === currentStock.id).events
		: []
	successCallback(events)
}
function eventContentHandler(info) {
	const login = store.getLogin()
	const eventElem = createEventElement(info)

	// если ивент - подложка
	if (isBackgroundEvent(info.event)) return eventElem

	const showBtn = isOldSupplierOrder(info, login) && !hasOrderInYard(info.event.extendedProps.data)
	const closeBtn = info.isDraggable || showBtn ? createCloseEventButton(info, showBtn) : ''
	const popupBtn = createPopupButton(info, login)

	return {
		domNodes: info.isDraggable || showBtn
			? [ eventElem, closeBtn, popupBtn ]
			: [ eventElem, popupBtn ]
	}
}
function eventDidMountHandler(info) {
	if(!info.isDraggable && !isBackgroundEvent(info.event)) {
		info.el.children[0].children[0].style.cursor = 'default'
		return
	}
}
function eventDragStartHandler(info) {
	const wsIsOpen = wsSlot.readyState === 1

	if (!wsIsOpen) {
		showReloadWindowModal()
		return
	}
}
function eventDragStopHandler(info) {
	// console.log(info)
}
async function eventDropHandler(info) {
	const role = store.getRole()
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data

	// проверка даты начала ивента
	const minUnloadDate = getMinUnloadDate(order, role)
	const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
	if (isInvalidEventDate(info, minUnloadDate)) {
	// if (isInvalidEventDate(info, minUnloadDate) && !isOldSlotNotInYard(order)) {
		info.revert()
		snackbar.show(userMessages.dateDropError(minUnloadDateStr))
		return
	}

	// проверка пересечения с пересменкой
	const currentStock = store.getCurrentStock()
	const shiftChange = currentStock.shiftChange
	if (isOverlapWithShiftChange(info, shiftChange)) {
		info.revert()
		snackbar.show(userMessages.shiftChangeError)
		return
	}

	// проверка пересечения со временем для внутренних перемещений
	const internaMovementsTimes = currentStock.internaMovementsTimes
	const internalMovementsRamps = currentStock.internalMovementsRamps
	if (isOverlapWithInternalMovementTime(info, internaMovementsTimes, internalMovementsRamps)) {
		info.revert()
		alert(userMessages.internalMovementTimeError)
		return
	}

	// проверка паллетовместимости склада на соседние даты
	const eventDateStr = fcEvent.startStr.split('T')[0]
	const pallCount = store.getPallCount(currentStock, eventDateStr)
	const maxPall = store.getMaxPallByDate(currentStock.id, eventDateStr)
	if (!checkPallCountForComingDates(info, pallCount, maxPall)) {
		info.revert()
		snackbar.show(userMessages.pallDropError)
		return
	}

	// проверка совпадения с графиком поставок
	// const scheduleData = await checkSchedule(order, eventDateStr)
	// if (scheduleData.message) alert (scheduleData.message)

	updateOrder(info, false)
}
async function eventReceiveHandler(info) {
	const role = store.getRole()
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	const eventDateStr = fcEvent.startStr.split('T')[0]
	
	// проверка даты начала ивента
	const minUnloadDate = getMinUnloadDate(order, role)
	const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
	if (isInvalidEventDate(info, minUnloadDate)) {
		info.revert()
		snackbar.show(userMessages.dateDropError(minUnloadDateStr))
		return
	}

	// проверка паллетовместимости склада
	const currentStock = store.getCurrentStock()
	const pallCount = store.getPallCount(currentStock, eventDateStr)
	const maxPall = store.getMaxPallByDate(currentStock.id, eventDateStr)
	if (!checkPallCount(info, pallCount, maxPall)) {
		info.revert()
		snackbar.show(userMessages.pallDropError)
		return
	}

	// проверка пересечения с пересменкой
	const shiftChange = currentStock.shiftChange
	if (isOverlapWithShiftChange(info, shiftChange)) {
		info.revert()
		snackbar.show(userMessages.shiftChangeError)
		return
	}

	// проверка пересечения со временем для внутренних перемещений
	const internaMovementsTimes = currentStock.internaMovementsTimes
	const internalMovementsRamps = currentStock.internalMovementsRamps
	if (isOverlapWithInternalMovementTime(info, internaMovementsTimes, internalMovementsRamps)) {
		info.revert()
		alert(userMessages.internalMovementTimeError)
		return
	}

	// проверка совпадения с графиком поставок
	// const scheduleData = await checkSchedule(order, eventDateStr)
	// if (scheduleData.message) alert (scheduleData.message)

	// для логиста и админа - сложный апдейт
	if (isAdmin(role) || isLogist(role)) {
		updateOrder(info, true)
		return
	}

	// загрузка заказа в БД
	loadOrder(info)
}
function eventClickHandler(info) {
	const { event: fcEvent, jsEvent } = info
	const wsIsOpen = wsSlot.readyState === 1
	const login = store.getLogin()
	const role = store.getRole()

	if (!wsIsOpen) {
		showReloadWindowModal()
		return
	}

	// обработчик нажатия на кнопку удаления события
	if (jsEvent.target.dataset.action === 'close') {
		// для логиста и админа - сложный апдейт
		if (isAdmin(role) || isLogist(role)) {
			const method = 'update'
			const currentStock = store.getCurrentStock()
			const orderData = getOrderDataForAjax(info, currentStock, login, role, method)
			deleteCalendarEvent(orderTableGridOption, orderData, false)
			return
		}

		// удаление заказа из БД
		deleteOrder(info)
		return
	}

	if (jsEvent.target.dataset.action === 'popup') {
		// обработчик клика на кнопку информации
		store.setslotToConfirm(fcEvent)
		showEventInfoPopup(fcEvent, login, role)
		return
	}
}
function eventsSetHandler(info) {
	// console.log('ivents: ', info)
}


/* -------------- методы для обновления БД ------------------ */
function loadOrder(info) {
	const method = 'load'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
		info.revert()
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: loadOrderUrl,
		token: store.getToken(),
		data: orderData,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				data.info && showMessageModal(data.info)
				addCalendarEvent(orderTableGridOption, orderData, false)
				return
			}

			if (data.status === '105') {
				errorHandler_105status(info, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(info, data)
			} else {
				snackbar.show(userMessages.actionNotCompleted)
			}
		},
		errorCallback: () => {
			info.revert()
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function updateOrder(info, isComplexUpdate) {
	const method = 'update'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
		info.revert()
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	// просьба указать причину переноса слота для логистов
	if (isLogist(currentRole)) {
		const messageLogist = prompt(
			`Укажите причину переноса (минимум ${slotsSettings.LOGIST_MESSAGE_MIN_LENGHT} символов): `
		)
		if (!messageLogist) {
			info.revert()
			return
		}
		if (messageLogist.length < slotsSettings.LOGIST_MESSAGE_MIN_LENGHT) {
			info.revert()
			snackbar.show(userMessages.messageLogistIsShort)
			return
		}
		orderData.messageLogist = messageLogist
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: updateOrderUrl,
		token: store.getToken(),
		data: orderData,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				data.info && showMessageModal(data.info)
				isComplexUpdate
					? addCalendarEvent(orderTableGridOption, orderData, false)
					: updateCalendarEvent(orderTableGridOption, orderData, false)
				return
			}

			if (data.status === '105') {
				errorHandler_105status(info, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(info, data)
			} else {
				snackbar.show(userMessages.actionNotCompleted)
			}
		},
		errorCallback: () => {
			info.revert()
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function deleteOrder(info) {
	const method = 'delete'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: deleteOrderUrl,
		token: store.getToken(),
		data: orderData,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				deleteCalendarEvent(orderTableGridOption, orderData, false)
				return
			}

			if (data.status === '105') {
				errorHandler_105status(info, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(info, data)
			} else {
				snackbar.show(userMessages.actionNotCompleted)
			}
		},
		errorCallback: () => {
			info.revert()
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function confirmSlot(fcEvent, action) {
	const method = 'confirm'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax({ event: fcEvent }, currentStock, currentLogin, currentRole, method)
	orderData.status = action === 'save' ? fcEvent.extendedProps.data.status === 8 ? 100 : 20: 8

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: confirmSlotUrl,
		token: store.getToken(),
		data: orderData,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				action === 'save' && data.info && showMessageModal(data.info)
				updateCalendarEvent(orderTableGridOption, orderData, false)
				hideEventInfoPopup()
				return
			}

			if (data.status === '105') {
				errorHandler_105status(null, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(null, data)
			} else {
				snackbar.show(userMessages.actionNotCompleted)
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
function editMarketInfo(agGridParams) {
	const method = 'editMarketInfo'
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()

	const order = agGridParams.data
	const idOrder = order.idOrder
	const marketInfo = agGridParams.newValue ? agGridParams.newValue : ''
	const oldMarketInfo = agGridParams.oldValue ? agGridParams.oldValue : ''

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: `${editMarketInfoBaseUrl}${idOrder}&${marketInfo}`,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				// обновляем заказ с сторе и ивент
				updateOrderAndEvent(order, currentLogin, currentRole, method)
				return
			}

			// устанавливаем старое значение
			setOldMarketInfo(order, oldMarketInfo, orderTableGridOption)

			if (data.status === '105') {
				errorHandler_105status(null, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(null, data)
			} else {
				snackbar.show(userMessages.actionNotCompleted)
			}
		},
		errorCallback: () => {
			// устанавливаем старое значение
			setOldMarketInfo(order, oldMarketInfo, orderTableGridOption)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
// метод получения данных о заказе из меркета
function getOrderFromMarket(marketNumber, eventContainer, successCallback) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: getMarketOrderUrl + marketNumber,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				// если склад не на слотах, не создаем поставку
				const order = data.order
				if (!slotStocks.includes(order.numStockDelivery)) {
					snackbar.show(userMessages.orderNotForSlot)
					return
				}
				successCallback(data, marketNumber, eventContainer)
				snackbar.show(data.message)
				return
			}

			if (data.status === '105') {
				errorHandler_105status(info, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(null, data)
			} else {
				snackbar.show(userMessages.actionNotCompleted)
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// функция создания нового заказа по информации из Маркета
function createNewOrderFromMarket(data, marketNumber, eventContainer) {
	const order = data.order
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

// коллбэк бля редактирования ячееек таблицы
function cellValueChangedHandler(params) {
	const columnName = params.column.colId

	if (columnName === "marketInfo") {
		editMarketInfo(params)
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

// обновление данных графика паллетовместимости
function updatePallChart(selectedStock) {
	const nowDateStr = new Date().toISOString().slice(0, 10)
	const pallChartData = store.getMaxPallDataByPeriod(
		selectedStock.id,
		nowDateStr,
		slotsSettings.PALL_CHART_DATA_DAY_COUNT
	)
	const chartData = getFormattedPallChartData(pallChartData)
	const bgData = getMarkerBGColorData(pallChartData)
	updateChartData(pallLineChart, chartData, bgData)
}
