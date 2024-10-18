import { showReloadWindowModal } from "./calendarUtils.js"
import { addCalendarEvent, deleteCalendarEvent, deleteCalendarEventFromTable, updateCalendarEvent } from "./eventControlMethods.js"
import { store } from "./store.js"

/* -------------- обработчики сообщений WebSocket ------------------ */
export function wsSlotOnOpenHandler(e) {
}
export function wsSlotOnMessageHandler(e, gridOptions) {
	const data = JSON.parse(e.data)

	if (data.status === '120') {

	} else if (data.status === '200') {
		// игнорируем сообщения, если приложение ещё не готово к работе
		if (!store.getReady()) return

		// ЗАГЛУШКА , ЕСЛИ НЕТ ПАРАМЕТРА payload
		if (!data.payload) return
		const orderData = JSON.parse(data.payload)
		const fromUser = data.fromUser.toLowerCase()
		const login = store.getLogin()

		// заглушка для игнорирования сообщений о машинах
		if (data.WSPath !== 'slot') return

		if (fromUser === login) return

		const action = data.action

		if (action === 'load') {
			addCalendarEvent(gridOptions, orderData, true)
		}

		if (action === 'update' || action === 'save' || action === 'unsave') {
			updateCalendarEvent(gridOptions, orderData, true)
		}

		if (action === 'delete') {
			deleteCalendarEvent(gridOptions, orderData, true)
		}

		// удаление заказа из таблицы контроля заявок
		if (action === 'delete from table') {
			const marketNumber = orderData.marketNumber
			const stockId = orderData.idRamp ? orderData.idRamp.slice(0, -2) : orderData.numStockDelivery
			const event = store.getEvent(stockId, marketNumber)
			if (!event) return
			const timeDelivery = orderData.timeDelivery.split('.')[0]
			const startDateStr = timeDelivery.replace(' ', 'T')
			const modifiedOrderData = {
				idOrder: Number(orderData.idOrder),
				idRamp: null,
				marketNumber: marketNumber,
				timeDelivery,
				loginManager: orderData.loginManager,
				stockId,
				startDateStr,
				numberOfPalls: Number(orderData.pall),
				fcEvent: event,
				status: 5,
				messageLogist: null,
			}
			deleteCalendarEventFromTable(gridOptions, modifiedOrderData)
		}

		// получение информации со Двора
		if (action === 'changeStatusYard') {
			const stockId = orderData.idRamp ? orderData.idRamp.slice(0, -2) : orderData.numStockDelivery
			const event = store.getEvent(stockId, orderData.marketNumber)
			if (!event) return
			const order = event.extendedProps.data
			const timeDelivery = orderData.timeDelivery.split('.')[0]
			const startDateStr = timeDelivery.replace(' ', 'T')
			const modifiedOrderData = {
				...orderData,
				statusYard: Number(orderData.statusYard),
				fcEvent: event,
				idOrder: Number(orderData.idOrder),
				messageLogist: null,
				numberOfPalls: Number(orderData.pall),
				startDateStr,
				status: order.status,
				stockId,
				timeDelivery,
			}
			updateCalendarEvent(gridOptions, modifiedOrderData, true)
		}
	}
}
export function wsSlotOnCloseHandler(e) {
	showReloadWindowModal()
}
export function wsSlotOnErrorHandler(e) {
}