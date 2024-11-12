import { ajaxUtils } from "../ajaxUtils.js"
import { bootstrap5overlay } from "../bootstrap5overlay/bootstrap5overlay.js"
import { snackbar } from "../snackbar/snackbar.js"
import { getData, isLogist } from "../utils.js"
import { errorHandler_100status, errorHandler_105status, hideEventInfoPopup, showMessageModal } from "./calendarUtils.js"
import {
	checkBookingBaseUrl,
	checkSlotBaseUrl,
	confirmSlotUrl,
	deleteOrderUrl,
	editMarketInfoBaseUrl,
	getMarketOrderUrl,
	loadOrderUrl,
	preloadOrderUrl,
	slotsSettings,
	slotStocks,
	updateOrderUrl,
	userMessages
} from "./constants.js"
import { getOrderDataForAjax } from "./dataUtils.js"
import { renderOrderCalendar } from "./deliveryCalendar.js"
import { addCalendarEvent, deleteCalendarEvent, updateCalendarEvent, updateOrderAndEvent } from "./eventControlMethods.js"
import { methodAccessRules } from "./rules.js"
import { store } from "./store.js"

/* -------------- методы для обновления БД ------------------ */
export function preloadOrder(info, orderTableGridOption, orderDateClickHandler) {
	const method = 'preload'
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
		url: preloadOrderUrl,
		token: store.getToken(),
		data: orderData,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				if (!data.planResponce) {
					// нет данных о графике - загружаем заказ в БД
					loadOrder(info, orderTableGridOption)
				} else {
					const plan = data.planResponce

					// нет данных о датах заказов и поставок
					if (!plan.dates) {
						loadOrder(info, orderTableGridOption)
						return
					}

					// нет данных о датах заказов и поставок
					if (plan.dates.length === 0) {
						loadOrder(info, orderTableGridOption)
						return
					}

					// получаем даты заказов и поставок, ожидаем указание нужной даты
					const orderDates = plan.dates       // Заказы
					const deliveryDates = plan.deliveryDates    // Поставки
					renderOrderCalendar(orderDates, deliveryDates, orderDateClickHandler)
					$('#orderCalendarModal').modal('show')
				}
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
export function loadOrder(info, orderTableGridOption, dateOrderOrl) {
	const method = 'load'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	if (dateOrderOrl) orderData.dateOrderOrl = dateOrderOrl

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
export function updateOrder(info, orderTableGridOption, isComplexUpdate) {
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
export function deleteOrder(info, orderTableGridOption, deleteByAdmin) {
	const method = 'delete'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	// для админа - удалять заказ без переноса в дроп-зону
	const isAnotherUser = !!deleteByAdmin

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
				deleteCalendarEvent(orderTableGridOption, orderData, isAnotherUser)
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
			// info.revert()
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
export function confirmSlot(fcEvent, action, orderTableGridOption) {
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
export function editMarketInfo(agGridParams, orderTableGridOption, errorCallback) {
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
			errorCallback(order, oldMarketInfo, orderTableGridOption)

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
			errorCallback(order, oldMarketInfo, orderTableGridOption)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}
// метод получения данных о заказе из меркета
export function getOrderFromMarket(marketNumber, eventContainer, successCallback) {
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
// проверка слота
export function checkSlot(info) {
	const method = 'checkSlot'
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()

	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	const idOrder = order.idOrder

	
	// проверка доступа к методу
	if (!methodAccessRules(method, order, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}
	
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: checkSlotBaseUrl + idOrder,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				data.info && showMessageModal(data.info)
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

// проверка на бронь
export function checkBooking(info) {
	const method = 'checkBooking'
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()

	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	const marketNumber = order.marketNumber

	
	// проверка доступа к методу
	if (!methodAccessRules(method, order, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	
	ajaxUtils.get({
		url: checkBookingBaseUrl + marketNumber,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				errorHandler_100status(null, data)
				return
			}

			if (data.status === '100') {
				errorHandler_100status(null, data)
				return
			}

			if (data.status === '105') {
				errorHandler_105status(null, data)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// проверка ивентов на бронь 
export async function checkEventsForBooking(events) {
	const method = 'checkBooking'
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()

	// проверка доступа к методу
	if (!methodAccessRules(method, null, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const messages = await Promise.all(events.map( async (event) => {
		const order = event.extendedProps.data
		const marketNumber = order.marketNumber
		const res = await getData(checkBookingBaseUrl + marketNumber)
		return res.info ? res.info : ''
	}))
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (messages && messages.length !== 0) {
		const messagesStr = messages.filter(Boolean).join('<br>')
		showMessageModal(messagesStr)
	}
}