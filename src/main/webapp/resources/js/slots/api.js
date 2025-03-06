import { ajaxUtils } from "../ajaxUtils.js"
import { bootstrap5overlay } from "../bootstrap5overlay/bootstrap5overlay.js"
import {
	checkBookingBaseUrl,
	checkScheduleBaseUrl,
	checkSlotBaseUrl,
	confirmSlotUrl,
	deleteOrderUrl,
	editMarketInfoBaseUrl,
	getBalanceBaseUrl,
	getMarketOrderBaseUrl,
	getRoutesHasOrderBaseUrl,
	loadOrderUrl,
	preloadOrderUrl,
	setOrderLinkingUrl,
	updateOrderUrl
} from "../globalConstants/urls.js"
import { slotStocks } from "../globalRules/ordersRules.js"
import { snackbar } from "../snackbar/snackbar.js"
import { getData, isLogist } from "../utils.js"
import { updateTableData } from "./agGridUtils.js"
import { errorHandler_100status, errorHandler_105status, getMultiplicity, hideEventInfoPopup, showMessageModal } from "./calendarUtils.js"
import { userMessages } from "./constants.js"
import { getMoveOrdersReportDates, getOrderDataForAjax } from "./dataUtils.js"
import { renderOrderCalendar } from "./deliveryCalendar.js"
import { updateOrderAndEvent } from "./eventControlMethods.js"
import { methodAccessRules } from "./rules.js"
import { store } from "./store.js"

/* -------------- методы для обновления БД ------------------ */
export function preloadOrder(info, orderDateClickHandler) {
	const method = 'preload'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	// ВРЕМЕННО ДЛЯ 1800
	// Фильтр для самовывоза
	// if (currentStock.id === '1800'
	// 	&& (
	// 		orderData.status === 5
	// 		|| orderData.status === 8
	// 		|| orderData.status === 100
	// 	)
	// ) {
	// 	alert('Только для самовывоза!')
	// 	info.revert()
	// 	return
	// }

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
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
				// if (data.schedule) {
				// 	store.setNeedMultiplicity(true)
				// }

				if (!data.planResponce) {
					// нет данных о графике - загружаем заказ в БД
					loadOrder(info)
				} else {
					const plan = data.planResponce

					// нет данных о датах заказов и поставок
					if (!plan.dates) {
						loadOrder(info)
						return
					}

					// нет данных о датах заказов и поставок
					if (plan.dates.length === 0) {
						loadOrder(info)
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
export function loadOrder(info, dateOrderOrl) {
	const method = 'load'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	if (dateOrderOrl) orderData.dateOrderOrl = dateOrderOrl

	// ВРЕМЕННО ДЛЯ 1800
	// Фильтр для самовывоза
	// if (currentStock.id === '1800'
	// 	&& (
	// 		orderData.status === 5
	// 		|| orderData.status === 8
	// 		|| orderData.status === 100
	// 	)
	// ) {
	// 	alert('Только для самовывоза!')
	// 	info.revert()
	// 	return
	// }

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	// if (store.getNeedMultiplicity()) {
	// 	store.setNeedMultiplicity(false)
	// 	const multiplicity = getMultiplicity()
	// 	if (!multiplicity) {
	// 		info.revert()
	// 		return
	// 	}
	// 	orderData.multiplicity = multiplicity
	// }

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
export function updateOrder(info, reasonText) {
	const method = 'update'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const isComplexUpdate = store.getComplexUpdate()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	// проверка доступа к методу
	if (!methodAccessRules(method, orderData, currentLogin, currentRole)) {
		info.revert()
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	orderData.isComplexUpdate = isComplexUpdate

	// просьба указать причину переноса слота для логистов
	if (isLogist(currentRole) && reasonText) {
		orderData.messageLogist = reasonText
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
export function deleteOrder(info, deleteByAdmin) {
	const method = 'delete'
	const currentStock = store.getCurrentStock()
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()
	const orderData = getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method)

	orderData.deleteByAdmin = deleteByAdmin

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
				const eventId = orderData.marketNumber
				store.addToDropZoneList(eventId)
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
export function confirmSlot(fcEvent, action) {
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

	// проверка доступа к методу
	if (!methodAccessRules(method, order, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

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
	const method = 'getOrderFromMarket'
	const currentLogin = store.getLogin()
	const currentRole = store.getRole()

	// проверка доступа к методу
	if (!methodAccessRules(method, null, currentLogin, currentRole)) {
		snackbar.show(userMessages.operationNotAllowed)
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.get({
		url: getMarketOrderBaseUrl + marketNumber,
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
		return res && res.info ? `${marketNumber}: ${res.info}` : ''
	}))
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (messages && messages.length !== 0) {
		const messagesStr = messages.filter(Boolean).join('<br>')
		showMessageModal(messagesStr)
	}
}

// связывание заказов
export function setOrderLinking(idOrderArray, gridOptions) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: setOrderLinkingUrl,
		token: store.getToken(),
		data: idOrderArray,
		successCallback: (data) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (data.status === '200') {
				data.info && showMessageModal(data.info)
				const orderLink = idOrderArray[0]
				idOrderArray.forEach(id => {
					const order = store.getOrderById(id)
					order.link = orderLink
					store.updateOrder(order)
				})
				updateTableData(gridOptions, store.getCurrentStockOrders())
				snackbar.show(`Заказы ${idOrderArray} объединены`)
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

// получение отчета по перемещениям слотов между 1700 и 1800
export function getMoveOrdersReport(action) {
	const todayMs = new Date().setHours(0, 0, 0, 0)
	const { date1, date2 } = getMoveOrdersReportDates(todayMs)
	const stoctId = action === '1700to1800' ? '1700' : '1800'
	window.open(`${getBalanceBaseUrl}${date1}&${date2}&${stoctId}`, '_blank')
}

// получение маршрутов для конкретного заказа
export async function getRoutesInfo(idOrder) {
	const routes = await getData(getRoutesHasOrderBaseUrl + idOrder)
	return routes
}

// проверка совпадения заказа с графика поставок
export async function checkSchedule(order, eventDateStr) {
	const isInternalMovement = order.isInternalMovement
	if (isInternalMovement === 'true') return {
		flag: true,
		message: null,
		body: null
	}
	const num = order.marketContractType
	if (!num) return {
		flag: true,
		message: userMessages.contractCodeNotFound,
		body: null
	}
	const scheduleData = await getData(`${checkScheduleBaseUrl}${num}&${eventDateStr}`)
	if (!scheduleData) {
		return {
			flag: true,
			message: userMessages.errorReadingSchedule,
			body: null
		}
	}
	if (!scheduleData.body) {
		return {
			flag: true,
			message: userMessages.contractCodeIsMissing,
			body: null
		}
	}
	if (!scheduleData.flag) {
		return {
			...scheduleData,
			message: userMessages.isScheduleNotMatch,
		}
	}

	return scheduleData
}