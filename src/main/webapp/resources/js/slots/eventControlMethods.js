import { snackbar } from "../snackbar/snackbar.js"
import { isAdmin, isLogist } from "../utils.js"
import { updateTableData, updateTableRow } from "./agGridUtils.js"
import { createDraggableElement, updatePallInfo } from "./calendarUtils.js"
import { userMessages } from "./constants.js"
import { stockAndDayIsVisible, stockIsVisible } from "./dataUtils.js"
import { store } from "./store.js"

/* -------------- методы для управления иветнтами ------------------ */
export function addCalendarEvent(gridOptions, orderData, isAnotherUser) {
	// обновляем количество паллет для склада
	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	if (stockAndDayIsVisible(stockId, eventDate)) {
		updatePallInfo(orderData.numberOfPalls, 'increment')
	}
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(orderData)
	// обновляем заказ в таблице
	updateTableRow(gridOptions, updatedOrder)
	// добавляем ивент в виртуальный склад
	store.addEvent(orderData, updatedOrder)

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
	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	if (stockAndDayIsVisible(stockId, eventDate)) {
		updatePallInfo(orderData.numberOfPalls, 'decrement')
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

	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	// обновляем заказ в таблице
	if (stockIsVisible(stockId)) {
		updateTableData(gridOptions, store.getCurrentStockOrders())
	}
	// обновляем количество паллет для склада
	if (stockAndDayIsVisible(stockId, eventDate)) {
		updatePallInfo(orderData.numberOfPalls, 'decrement')
	}
}