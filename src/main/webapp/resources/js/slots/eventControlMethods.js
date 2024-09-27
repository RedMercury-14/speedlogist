import { snackbar } from "../snackbar/snackbar.js"
import { dateHelper, isAdmin, isLogist } from "../utils.js"
import { addUpdateTableRow, removeTableRow, updateTableData, updateTableRow } from "./agGridUtils.js"
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
	// addUpdateTableRow(gridOptions, updatedOrder) -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
	// добавляем ивент в виртуальный склад
	store.addEvent(orderData, updatedOrder)
	const currentStock = store.getCurrentStock()
	// обновляем количество паллет для склада
	changePallCapacity(orderData, currentStock, updatedOrder)

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
	const oldOrder = store.getOrderByMarketNumber(orderData.marketNumber)
	if (!oldOrder) return
	const oldOrderStockId = `${oldOrder.idRamp}`.slice(0,-2)
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(orderData)
	const updatedStockId = `${updatedOrder.idRamp}`.slice(0,-2)

	// если склады не совпадают
	if ((oldOrderStockId !== updatedStockId)) {
		// удаляем старый ивент из виртуального склада
		store.removeEvent(oldOrderStockId, oldOrder.marketNumber)
		// добавляем новый ивент в виртуальный склад
		store.addEvent(orderData, updatedOrder)
		// обновляем заказ в таблице
		updateTableRow(gridOptions, updatedOrder)
	} else {
		// обновляем заказ в таблице
		updateTableRow(gridOptions, updatedOrder)
		// обновляем ивент в виртуальном складе
		store.updateEvent(orderData, updatedOrder)
	}

	const currentStock = store.getCurrentStock()
	if (!currentStock) return
	// обновляем количество паллет для склада
	changePallCapacityForUpdateCE(orderData, currentStock, oldOrder, updatedOrder, isAnotherUser)
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
	// // удаляем заказ из стора
	// store.deleteOrder(orderData) -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ

	// обновляем заказ в таблице
	updateTableRow(gridOptions, updatedOrder)

	// // обновляем/удаляем заказ в таблице в зависимости от роли
	// isLogist(role) || isAdmin(role) -- ДЛЯ РАБОТЫ БЕЗ ВИРТУАЛЬНЫХ ЗАКАЗОВ
	// 	? updateTableRow(gridOptions, updatedOrder)
	// 	: removeTableRow(gridOptions, orderData)

	// удаляем ивент из стора
	store.removeEvent(orderData.stockId, orderData.marketNumber)
	// обновляем количество паллет для склада
	changePallCapacity(orderData, currentStock, updatedOrder)

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
	store.removeEvent(orderData.stockId, orderData.marketNumber)

	const currentStock = store.getCurrentStock()
	const stockId = orderData.stockId
	const order = orderData.fcEvent.extendedProps.data
	// обновляем заказ в таблице
	if (stockIsVisible(currentStock, stockId)) {
		updateTableData(gridOptions, store.getCurrentStockOrders())
	}
	// обновляем количество паллет для склада
	changePallCapacity(orderData, currentStock, order)
}
export function updateOrderAndEvent(order, currentLogin, currentRole, method) {
	const stockId = order.idRamp.slice(0, -2)
	// обновляем заказ в сторе
	const updatedOrder = store.updateOrder(order)
	// получение ивента календаря (слота)
	const event = store.getEvent(stockId, order.marketNumber)

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

function changePallCapacity(orderData, currentStock, order) {
	const currentDate = store.getCurrentDate()
	const stockId = orderData.stockId
	const eventDate = orderData.startDateStr.split('T')[0]
	if (stockAndDayIsVisible(currentStock, currentDate, stockId, eventDate)) {
		const maxPall = store.getCurrentMaxPall()
		const orderType = getOrderType(order)
		const currentPallCount = store.getPallCount(currentStock, currentDate)
		updatePallInfo(currentPallCount, maxPall, orderType)
	}
}

function changePallCapacityForUpdateCE(orderData, currentStock, oldOrder, order, isAnotherUser) {
	const oldEvent = orderData.oldEvent
	const oldOrderStockId = `${oldOrder.idRamp}`.slice(0,-2)
	const currentDate = store.getCurrentDate()
	const maxPall = store.getCurrentMaxPall()
	const eventDateStr = orderData.startDateStr.split('T')[0]
	const orderType = getOrderType(order)
	const currentPallCount = store.getPallCount(currentStock, currentDate)

	// для текущего пользователя
	if (!isAnotherUser) {
		const oldEventDateStr = oldEvent
			? oldEvent.startStr.split('T')[0]
			: new Date(oldOrder.timeDelivery).toISOString().split('T')[0]

		// если даты не совпадают, то обновляем паллетовместимость
		if (oldEventDateStr !== eventDateStr) {
			updatePallInfo(currentPallCount, maxPall, orderType)
			return
		}
	}

	// для других пользователей
	if (isAnotherUser) {
		const oldStockId = oldEvent
			? oldEvent.extendedProps.data.numStockDelivery
			: oldOrderStockId
		const oldEventDateStr = oldEvent
			? oldEvent.start.split('T')[0]
			: new Date(oldOrder.timeDelivery).toISOString().split('T')[0]
		const stockId = orderData.stockId

		// если даты и склады совпадают, то не обновляем паллетовместимость
		if ((oldEventDateStr === eventDateStr) && (stockId === oldStockId)) {
			return
		}

		// если виден склад и дата старого ивента
		if (stockAndDayIsVisible(currentStock, currentDate, oldStockId, oldEventDateStr)) {
			updatePallInfo(currentPallCount, maxPall, orderType)
			return
		}

		// если виден склад и дата нового ивента
		if (stockAndDayIsVisible(currentStock, currentDate, stockId, eventDateStr)) {
			updatePallInfo(currentPallCount, maxPall, orderType)
			return
		}
	}
}