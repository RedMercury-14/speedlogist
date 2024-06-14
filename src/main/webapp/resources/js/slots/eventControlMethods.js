import { snackbar } from "../snackbar/snackbar.js"
import { isAdmin, isLogist } from "../utils.js"
import { updateTableData, updateTableRow } from "./agGridUtils.js"
import { createDraggableElement, updatePallInfo } from "./calendarUtils.js"
import { userMessages } from "./constants.js"
import { getOrderDataForAjax, getOrderType, getPallCoutnAction, stockAndDayIsVisible, stockIsVisible } from "./dataUtils.js"
import { store } from "./store.js"

/* -------------- методы для управления иветнтами ------------------ */
export function addCalendarEvent(gridOptions, orderData, isAnotherUser) {
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(orderData)
	// обновляем заказ в таблице
	updateTableRow(gridOptions, updatedOrder)
	// добавляем ивент в виртуальный склад
	store.addEvent(orderData, updatedOrder)
	
	// обновляем количество паллет для склада
	const currentStock = store.getCurrentStock()
	const currentDate = store.getCurrentDate()
	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	if (stockAndDayIsVisible(currentStock, currentDate, stockId, eventDate)) {
		const maxPall = store.getCurrentMaxPall()
		const orderType = getOrderType(updatedOrder)
		const currentPallCount = store.getPallCount(currentStock, currentDate)
		updatePallInfo(currentPallCount, maxPall, orderType, 'increment')
	}

	if (!isAnotherUser) {
		// удаляем ивент из календаря, так как он уже добавлен в стор
		orderData.fcEvent.remove()
		// удаляем ивент из контейнера, так как он уже добавлен в календарь
		document.querySelector(`#event_${orderData.marketNumber}`)?.remove()
		// удаляем ивент из очереди ожидания добавления в календарь
		store.removeEventFromDropZone(orderData.marketNumber)
	}
}
export function updateCalendarEvent(gridOptions, orderData, isAnotherUser) {
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(orderData)
	// обновляем заказ в таблице
	updateTableRow(gridOptions, updatedOrder)
	// обновляем ивент в виртуальном складе
	store.updateEvent(orderData, updatedOrder)

	// обновляем количество паллет для склада
	const oldEvent = orderData.oldEvent
	if (!oldEvent) return

	const currentStock = store.getCurrentStock()
	if (!currentStock) return

	const currentDate = store.getCurrentDate()
	const maxPall = store.getCurrentMaxPall()
	const numberOfPall = Number(orderData.numberOfPalls)
	const eventDateStr = orderData.startDateStr.split('T')[0]
	const orderType = getOrderType(updatedOrder)
	const currentPallCount = store.getPallCount(currentStock, currentDate)

	// для текущего пользователя
	if (!isAnotherUser) {
		const oldEventDateStr = oldEvent.startStr.split('T')[0]

		// если даты совпадают, то не обновляем паллетовместимость
		if (oldEventDateStr === eventDateStr) return

		const action = getPallCoutnAction(eventDateStr, oldEventDateStr)
		updatePallInfo(currentPallCount, maxPall, orderType, action)
		return
	}

	// для других пользователей
	if (isAnotherUser) {
		const oldStockId = oldEvent.extendedProps.data.numStockDelivery
		const oldEventDateStr = oldEvent.start.split('T')[0]
		const stockId = orderData.stockId

		// если даты и склады совпадают, то не обновляем паллетовместимость
		if ((oldEventDateStr === eventDateStr) && (stockId === oldStockId)) {
			return
		}

		// если виден склад и дата старого ивента
		if (stockAndDayIsVisible(currentStock, currentDate, oldStockId, oldEventDateStr)) {
			updatePallInfo(currentPallCount, maxPall, orderType, 'decrement')
			return
		}

		// если виден склад и дата нового ивента
		if (stockAndDayIsVisible(currentStock, currentDate, stockId, eventDateStr)) {
			updatePallInfo(currentPallCount, maxPall, orderType, 'increment')
			return
		}
	}
}
export function deleteCalendarEvent(gridOptions, orderData, isAnotherUser) {
	const login = store.getLogin()
	const role = store.getRole()
	const currentStock = store.getCurrentStock()
	// удаляем данные о слоте
	orderData.timeDelivery = null
	orderData.idRamp = null
	// если логист или админ, то не удаляем логин
	if ((!isLogist(role) && !isAdmin(role)) && !isAnotherUser) orderData.loginManager = null
	if ((isLogist(role) || isAdmin(role)) && isAnotherUser) orderData.loginManager = null
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(orderData)
	// обновляем заказ в таблице
	updateTableRow(gridOptions, updatedOrder)
	// удаляем ивент из стора
	store.removeEvent(orderData.stockId, orderData.fcEvent)
	// обновляем количество паллет для склада
	const currentDate = store.getCurrentDate()
	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	if (stockAndDayIsVisible(currentStock, currentDate, stockId, eventDate)) {
		const maxPall = store.getCurrentMaxPall()
		const orderType = getOrderType(updatedOrder)
		const currentPallCount = store.getPallCount(currentStock, currentDate)
		updatePallInfo(currentPallCount, maxPall, orderType, 'decrement')
	}

	if (!isAnotherUser && currentStock) {
		// добавляем ивент в очередь ожидания
		store.addEventToDropZone(updatedOrder)
		// показываем сообщение об удалении ивента из календаря
		snackbar.show(userMessages.eventRemove)
		// пересоздаём ивент в контейнере
		const eventContainer = document.querySelector("#external-events")
		createDraggableElement(eventContainer, updatedOrder, login, currentStock)
	}
}
export function deleteCalendarEventFromTable(gridOptions, orderData) {
	// удаляем заказ в сторе
	store.deleteOrder(orderData)
	// удаляем ивент из стора
	store.removeEvent(orderData.stockId, orderData.fcEvent)

	const currentStock = store.getCurrentStock()
	const currentDate = store.getCurrentDate()
	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	// обновляем заказ в таблице
	if (stockIsVisible(currentStock, stockId)) {
		updateTableData(gridOptions, store.getCurrentStockOrders())
	}
	// обновляем количество паллет для склада
	if (stockAndDayIsVisible(currentStock, currentDate, stockId, eventDate)) {
		const maxPall = store.getCurrentMaxPall()
		const order = orderData.fcEvent.extendedProps.data
		const orderType = getOrderType(order)
		const currentPallCount = store.getPallCount(currentStock, currentDate)
		updatePallInfo(currentPallCount, maxPall, orderType, 'decrement')
	}
}
export function updateOrderAndEvent(order, currentLogin, currentRole, method) {
	const stockId = order.idRamp.slice(0, -2)
	const marketNumber = order.marketNumber
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(order)
	// получение ивента календаря (слота)
	const event = store.getEvent(stockId, { id: marketNumber })

	if (event) {
		// обновляем ивент в виртуальном складе
		const fakeInfo = {
			oldEvent: null,
			event: {
				...event,
				startStr: event.start,
				extendedProps: { data: order }
			},
		}
		const orderData = getOrderDataForAjax(fakeInfo, { id: stockId }, currentLogin, currentRole, method)
		store.updateEvent(orderData, updatedOrder)
	}
}