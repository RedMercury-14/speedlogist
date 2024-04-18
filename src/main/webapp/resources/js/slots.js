import { snackbar } from "./snackbar/snackbar.js"
import { store } from "./slots/store.js"
import {
	confirmSlotUrl,
	deleteOrderUrl,
	eventColors,
	getOrdersForSlotsBaseUrl,
	loadOrderUrl,
	updateOrderUrl,
	userMessages,
} from "./slots/constants.js"
import { gridOptions, renderTable, updateTableData } from "./slots/agGridUtils.js"
import { addOnClickToMenuItemListner, closeSidebar } from "./slots/sidebar.js"
import {
	addNewStockOption,
	addTooltip,
	createCloseEventButton,
	createEventElement,
	isMobileDevice,
	setCalendarWidth,
	showMobileTooltop,
	setPallInfo,
	getPallCount,
	createDraggableElement,
	customErrorCallback,
	showReloadWindowModal,
	createPopupButton,
	showEventInfoPopup,
	hideEventInfoPopup,
	addSmallHeaderClass,
	updateDropZone,
} from "./slots/calendarUtils.js"
import { debounce, getData, isAdmin, isLogist, isSlotsObserver } from "./utils.js"
import { uiIcons } from "./uiIcons.js"
import { wsSlotUrl } from "./global.js"
import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { addCalendarEvent, deleteCalendarEvent, updateCalendarEvent } from "./slots/eventControlMethods.js"
import {
	wsSlotOnCloseHandler,
	wsSlotOnErrorHandler,
	wsSlotOnMessageHandler,
	wsSlotOnOpenHandler,
} from "./slots/wsHandlers.js"
import { checkEventId, checkPallCount, isAnotherUser, isInvalidEventDate, isOldSupplierOrder, methodAccessRules } from "./slots/rules.js"
import { convertToDayMonthTime, getDatesToSlotsFetch, getMinUnloadDate, getOrderDataForAjax } from "./slots/dataUtils.js"

const debouncedEventsSetHandler = debounce(eventsSetHandler, 200)
const orderTableGridOption = {
	...gridOptions,
	getContextMenuItems: getContextMenuItemsForOrderTable,
}

const wsSlot = new WebSocket(wsSlotUrl)
wsSlot.onopen = wsSlotOnOpenHandler
wsSlot.onclose = wsSlotOnCloseHandler
wsSlot.onerror = wsSlotOnErrorHandler
wsSlot.onmessage = (e) => wsSlotOnMessageHandler(e, orderTableGridOption)

let calendar
let slots = []

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
		right: "resourceTimeGridDay,resourceTimeGridTwoDay",
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
	eventOverlap: false,
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

	// добавляем класс при хэдере меньшего размера
	addSmallHeaderClass()

	// кнопки боковой панели
	const menuItems = document.querySelectorAll(".menu-item")
	const buttonClose = document.querySelector(".close-button")
	menuItems.forEach((item) => addOnClickToMenuItemListner(item))
	buttonClose.addEventListener("click", () => closeSidebar())
	document.addEventListener("keydown", (e) => (e.key === "Escape") && closeSidebar())

	const calendarEl = document.querySelector("#calendar")
	const addNewOrderButton = document.querySelector("#addNewOrder")
	const stockSelect = document.querySelector("#stockNumber")
	const gridDiv = document.querySelector('#myGrid')
	const eventContainer = document.querySelector("#external-events")

	// кнопка подтверждения слота
	const confirmSlotBtn = document.querySelector('#confirmSlot')
	confirmSlotBtn.addEventListener('click', confirmSlotBtnClickHandler)

	// кнопка перезагрузки страницы
	const reloadWindowButton = document.querySelector('#reloadWindowButton')
	reloadWindowButton.addEventListener('click', (e) => window.location.reload())

	const statusInfoLabel = document.querySelector('#statusInfoLabel')
	const statusInfo = document.querySelector('#statusInfo')
	statusInfoLabel.addEventListener('mouseover', (e) => statusInfo.classList.add('show'))
	statusInfoLabel.addEventListener('mouseout', (e) => statusInfo.classList.remove('show'))

	calendar = new FullCalendar.Calendar(calendarEl, calendarOptions)

	// предотвращаем правый клик в календаре
	calendarEl.addEventListener('mousedown', (e) => {
		if (e.button === 2) e.preventDefault()
	})

	// получаем данные
	const { startDateStr, endDateStr } = getDatesToSlotsFetch(30)
	const marketData = await getData(`${getOrdersForSlotsBaseUrl}${startDateStr}&${endDateStr}`)


	// сохраняем заказы в стор
	store.setOrders(marketData)
	// добавляем ивенты на виртуальные склады
	store.setStockEvents()

	// добавляем склады в селект и вешаем обработчик
	const stocks = store.getStocks()
	stockSelect.value = ''
	stocks.forEach(stock => addNewStockOption(stockSelect, stock))
	stockSelect.addEventListener("change", (e) => stockSelectOnChangeHandler(e, calendar))

	// добавление нового заказа
	addNewOrderButton.addEventListener('click', (e) => addNewOrderButtonHandler(e, eventContainer))

	// обновляем ивенты календаря при изменении стора
	store.subscribe(() => calendar.refetchEvents())

	// рендеринг салендаря и таблицы
	calendar.render()
	renderTable(gridDiv, orderTableGridOption, [])

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
	createNewOrder(marketNumber, eventContainer)
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

	if (!isAdmin(role) && !isLogist(role) && !isSlotsObserver(role)) {
		const addNewOrderButton = document.querySelector("#addNewOrder")
		addNewOrderButton.removeAttribute("disabled")
	}

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
}

// обработчик нажатия на кнопку подтверждения/снятия подтверждения
function confirmSlotBtnClickHandler(e) {
	const fcEvent = store.getSlotToConfirm()
	const action = e.target.dataset.action
	const status = fcEvent.extendedProps.data.status
	if (action === 'unSave' && status === 20) return
	confirmSlot(fcEvent, action)
}


/* -------------- обработчики для календаря ------------------ */
function resourcesHandler(info, successCallback, failureCallback) {
	successCallback(slots)
}
function dateSetHandler(info) {
	const [ currentDateStr ] = info.startStr.split('T')
	const currentStock = store.getCurrentStock()

	const currentDate = info.startStr.split('T')[0]
	store.setCurrendDate(currentDate)

	// изменение информации о паллетах для данного склада
	if (currentStock) {
		const pallCount = getPallCount(currentStock, currentDateStr)
		setPallInfo(pallCount, currentStock.maxPall)
	}
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
	const showBtn = isOldSupplierOrder(info, login)
	const closeBtn = info.isDraggable || showBtn ? createCloseEventButton(info, showBtn) : ''
	const popupBtn = createPopupButton(info)

	// костыль чтобы избежать отображения подсказки до встраивания в календарь
	// if (!info.isDraggable) {
	// 	addTooltip(eventElem, info)
	// } else {
	// 	setTimeout(() => {
	// 		addTooltip(eventElem, info)
	// 	}, 5000)
	// }

	return {
		domNodes: info.isDraggable || showBtn
			? [ eventElem, closeBtn, popupBtn ]
			: [ eventElem, popupBtn ]
	}
}
function eventDidMountHandler(info) {
	if(!info.isDraggable) {
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
function eventDropHandler(info) {
	const role = store.getRole()
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data

	// проверка даты начала ивента
	const minUnloadDate = getMinUnloadDate(order, role)
	const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
	if (isInvalidEventDate(info, minUnloadDate)) {
		info.revert()
		snackbar.show(userMessages.dateDropError(minUnloadDateStr))
		return
	}

	updateOrder(info, false)
}
function eventReceiveHandler(info) {
	const role = store.getRole()
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	
	// проверка даты начала ивента
	const minUnloadDate = getMinUnloadDate(order, role)
	const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
	if (isInvalidEventDate(info, minUnloadDate)) {
		info.revert()
		snackbar.show(userMessages.dateDropError(minUnloadDateStr))
		return
	}

	// проверка паллетовместимости склада
	const numberOfPalls = Number(order.pall)
	const currentStock = store.getCurrentStock()
	const maxPall = currentStock.maxPall
	if (!checkPallCount(numberOfPalls, maxPall)) {
		info.revert()
		snackbar.show(userMessages.pallDropError)
		return
	}

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
	if (jsEvent.target.className === 'close') {
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

	if (jsEvent.target.className === 'popup') {
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
				addCalendarEvent(orderTableGridOption, orderData, false)
				return
			}

			customErrorCallback(info, data, method)
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
		const messageLogist = prompt('Укажите причину переноса (минимум 10 символов): ')
		if (messageLogist.length < 10) {
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
				isComplexUpdate
					? addCalendarEvent(orderTableGridOption, orderData, false)
					: updateCalendarEvent(orderTableGridOption, orderData, false)
				return
			}

			customErrorCallback(info, data, method)
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

			customErrorCallback(info, data, method)
		},
		errorCallback: () => {
			// info.revert()
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
				updateCalendarEvent(orderTableGridOption, orderData, false)
				hideEventInfoPopup()
				return
			}

			customErrorCallback(null, data, method)
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
