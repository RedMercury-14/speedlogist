import { slotsSettings } from "../globalRules/slotsRules.js"
import { snackbar } from "../snackbar/snackbar.js"
import { isAdmin, isLogist } from "../utils.js"
import { checkBooking, checkSlot, deleteOrder, loadOrder, preloadOrder, updateOrder } from "./api.js"
import {
	createCheckBookingBtn,
	createCheckSlotBtn,
	createCloseEventButton,
	createDeleteSlotBtn,
	createEventElement,
	createPopupButton,
	setCurrentDateAttr,
	setPallInfo,
	showEventInfoPopup,
	showReloadWindowModal
} from "./calendarUtils.js"
import { userMessages } from "./constants.js"
import { convertToDayMonthTime, getMinUnloadDate, getOrderDataForAjax } from "./dataUtils.js"
import { deleteCalendarEvent } from "./eventControlMethods.js"
import {
	checkPallCount,
	checkPallCountForComingDates,
	customRestrictions,
	hasOrderInYard,
	isBackgroundEvent,
	isInvalidEventDate,
	isLogistEditableStatuses,
	isMatchNumStockDelivery,
	isOldSupplierOrder,
	isOverlapWithNoImportTime,
	isOverlapWithInternalMovementTime,
	isOverlapWithInternalMovementTimeForLogists,
	isOverlapWithShiftChange,
	newYearRestrictions
} from "./rules.js"
import { store } from "./store.js"

/* -------------- обработчики для календаря ------------------ */
export function resourcesHandler(successCallback, slots) {
	successCallback(slots)
}
export function dateSetHandler(info) {
	const currentDateStr = info.startStr.split('T')[0]
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
export function eventsHandler(info, successCallback, failureCallback) {
	const currentStock = store.getCurrentStock()
	const events = currentStock
		? store.getState().stocks.find(stock => stock.id === currentStock.id).events
		: []
	successCallback(events)
}
export function eventContentHandler(info) {
	const login = store.getLogin()
	const role = store.getRole()
	const eventElem = createEventElement(info)
	const order = info.event.extendedProps.data

	// если ивент - подложка
	if (isBackgroundEvent(info.event)) return eventElem

	// иные случаеи отображения кнопки удаления слота (х)
	const showBtn = (isOldSupplierOrder(info, login) && !hasOrderInYard(order))     // для старых заказов от поставщика
				// || (isLogist(role) && isLogistEditableStatuses(order.status) && !hasOrderInYard(order))     // для заказов на самовывоз для логистов

	const closeBtn = info.isDraggable || showBtn ? createCloseEventButton(info, showBtn) : ''
	const popupBtn = createPopupButton(info, login)
	const checkSlotBtn = createCheckSlotBtn(info)
	const checkBookingBtn = createCheckBookingBtn(info)
	const deleteSlotBtn = createDeleteSlotBtn(info)

	const nodes = info.isDraggable || showBtn
		? [ eventElem, closeBtn, popupBtn ]
		: [ eventElem, popupBtn ]

	// кнопка проверки слота для админа
	if (isAdmin(role)) nodes.push(checkSlotBtn, checkBookingBtn, deleteSlotBtn)
	else if (login === 'romashkok%!dobronom.by') nodes.push(checkBookingBtn, deleteSlotBtn)

	return {
		domNodes: nodes
	}
}
export function eventDidMountHandler(info) {
	if(!info.isDraggable && !isBackgroundEvent(info.event)) {
		info.el.children[0].children[0].style.cursor = 'default'
		return
	}
}
export function eventDragStartHandler(info, wsSlot) {
	const wsIsOpen = wsSlot.readyState === 1

	if (!wsIsOpen) {
		showReloadWindowModal()
		return
	}
}
export function eventDragStopHandler(info) {
	// console.log(info)
}
export async function eventDropHandler(info) {
	const role = store.getRole()
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	const eventDateStr = fcEvent.startStr.split('T')[0]

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

	// запрет установки слота на 170006 рампу с 8 марта
	const rampId = fcEvent._def.resourceIds[0]
	const restrictionProps = {
		startDateTimeStr: '2025-03-08T00:00:00',
		endDateTimeStr: '2026-03-10T00:00:00',
		stockId: '1700'
	}
	if (rampId === '170006' && customRestrictions(fcEvent, currentStock, restrictionProps)) {
		info.revert()
		snackbar.show(
			'Рампа 6 на 1700 складе закрыта для установки слотов. Она будет удалена'
		)
		return
	}

	// проверка пересечения со временем для внутренних перемещений
	const internaMovementsTimes = currentStock.internaMovementsTimes
	const internalMovementsRamps = currentStock.internalMovementsRamps
	// время для внутренних перемещений отдано для установки слотов логистами ВНЕ ОЧЕРЕДИ
	if (isOverlapWithInternalMovementTimeForLogists(info, internaMovementsTimes, internalMovementsRamps, role)) {
		info.revert()
		alert(userMessages.internalMovementTimeForLogistError(currentStock))
		return
	}

	// проверка пересечения с зоной не для Импорта
	if (isOverlapWithNoImportTime(info, slotsSettings.NO_IMPORT_STOCK_IDS, slotsSettings.NO_IMPORT_TIMES)) {
		info.revert()
		snackbar.show(userMessages.noImportOverlapMessage(noImportTimes))
		return
	}

	// проверка паллетовместимости склада на соседние даты
	const pallCount = store.getPallCount(currentStock, eventDateStr)
	const maxPall = store.getMaxPallByDate(currentStock.id, eventDateStr)
	if (!checkPallCountForComingDates(info, pallCount, maxPall, currentStock)) {
		info.revert()
		snackbar.show(userMessages.pallDropError)
		return
	}

	// проверка совпадения с графиком поставок
	// const scheduleData = await checkSchedule(order, eventDateStr)
	// if (scheduleData.message) alert (scheduleData.message)

	// указываем, что это НЕ сложный апдейт
	store.setComplexUpdate(false)
	store.setCalendarInfo(info)

	// для логиста просим причину
	isLogist(role)
		? $('#updateSlotReasonModal').modal('show')
		: updateOrder(info)
}

export async function eventReceiveHandler(info, orderDateClickHandler) {
	// отменяем установку ивента в календарь, т.к. это обрабатывается вручную
	info.revert()

	const role = store.getRole()
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	const eventDateStr = fcEvent.startStr.split('T')[0]
	
	// проверка даты начала ивента
	const minUnloadDate = getMinUnloadDate(order, role)
	const minUnloadDateStr = convertToDayMonthTime(minUnloadDate)
	if (isInvalidEventDate(info, minUnloadDate)) {
		snackbar.show(userMessages.dateDropError(minUnloadDateStr))
		return
	}

	// проверка паллетовместимости склада
	const currentStock = store.getCurrentStock()
	const pallCount = store.getPallCount(currentStock, eventDateStr)
	const maxPall = store.getMaxPallByDate(currentStock.id, eventDateStr)
	if (!checkPallCount(info, pallCount, maxPall, currentStock)) {
		snackbar.show(userMessages.pallDropError)
		return
	}

	// запрет установки слота на 170006 рампу с 8 марта
	const rampId = fcEvent._def.resourceIds[0]
	const restrictionProps = {
		startDateTimeStr: '2025-03-08T00:00:00',
		endDateTimeStr: '2026-03-10T00:00:00',
		stockId: '1700'
	}
	if (rampId === '170006' && customRestrictions(fcEvent, currentStock, restrictionProps)) {
		info.revert()
		snackbar.show(
			'Рампа 6 на 1700 складе закрыта для установки слотов. Она будет удалена'
		)
		return
	}

	// проверка совпадения склада из Маркета и текущего склада
	const numStockDelivery = order.numStockDelivery
	if (!isMatchNumStockDelivery(numStockDelivery, currentStock)) {
		return
	}

	// проверка пересечения с пересменкой
	const shiftChange = currentStock.shiftChange
	if (isOverlapWithShiftChange(info, shiftChange)) {
		snackbar.show(userMessages.shiftChangeError)
		return
	}

	// проверка пересечения со временем для внутренних перемещений
	const internaMovementsTimes = currentStock.internaMovementsTimes
	const internalMovementsRamps = currentStock.internalMovementsRamps
	// время для внутренних перемещений отдано для установки слотов логистами ВНЕ ОЧЕРЕДИ
	if (isOverlapWithInternalMovementTimeForLogists(info, internaMovementsTimes, internalMovementsRamps, role)) {
		alert(userMessages.internalMovementTimeForLogistError(currentStock))
		return
	}

	// проверка пересечения с зоной не для Импорта
	if (isOverlapWithNoImportTime(info, slotsSettings.NO_IMPORT_STOCK_IDS, slotsSettings.NO_IMPORT_TIMES)) {
		info.revert()
		snackbar.show(userMessages.noImportOverlapMessage(noImportTimes))
		return
	}

	// проверка совпадения с графиком поставок
	// const scheduleData = await checkSchedule(order, eventDateStr)
	// if (scheduleData.message) alert (scheduleData.message)

	// для логиста и админа - сложный апдейт
	if (isAdmin(role) || isLogist(role)) {
		// указываем, что это сложный апдейт
		store.setComplexUpdate(true)
		store.setCalendarInfo(info)

		// для логиста просим причину
		isLogist(role)
			? $('#updateSlotReasonModal').modal('show')
			: updateOrder(info)
		return
	}

	// загрузка заказа в БД либо требование установки даты заказа
	const isInternalMovement = order.isInternalMovement === 'true'
	if (isInternalMovement) {
		loadOrder(info)
	} else {
		preloadOrder(info, orderDateClickHandler)
		store.setCalendarInfo(info)
	}
}
export function eventClickHandler(info, orderTableGridOption, wsSlot) {
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
			const eventId = orderData.marketNumber
			store.addToDropZoneList(eventId)
			deleteCalendarEvent(orderTableGridOption, orderData, false)
			return
		}

		// удаление заказа из БД
		deleteOrder(info)
		return
	}

	// обработчик клика на кнопку информации
	if (jsEvent.target.dataset.action === 'popup') {
		store.setslotToConfirm(fcEvent)
		showEventInfoPopup(fcEvent, login, role)
		return
	}

	// обработчик клика на кнопку проверки слота
	if (jsEvent.target.dataset.action === 'checkSlot') {
		checkSlot(info)
		return
	}

	// обработчик клика на кнопку проверки на бронь
	if (jsEvent.target.dataset.action === 'checkBooking') {
		checkBooking(info)
		return
	}

	// обработчик клика на кнопку проверки на бронь
	if (jsEvent.target.dataset.action === 'deleteSlot') {
		if (confirm(`Вы уверены, что хотите удалить слот?`)) {
			deleteOrder(info, true)
		}
		return
	}
}
export function eventsSetHandler(info) {
	// console.log('ivents: ', info)
}
