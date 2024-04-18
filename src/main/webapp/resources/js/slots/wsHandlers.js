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
		// ЗАГЛУШКА , ЕСЛИ НЕТ ПАРАМЕТРА payload
		if (!data.payload) return
		const orderData = JSON.parse(data.payload)
		const fromUser = data.fromUser.toLowerCase()
		const login = store.getLogin()

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
			const timeDelivery = orderData.timeDelivery.split('.')[0]
			const startDateStr = timeDelivery.replace(' ', 'T')
			const modifiedOrderData = {
				idOrder: Number(orderData.status),
				idRamp: null,
				marketNumber: marketNumber,
				timeDelivery,
				loginManager: orderData.loginManager,
				stockId: orderData.numStockDelivery,
				startDateStr,
				numberOfPalls: Number(orderData.pall),
				fcEvent: {
					id: marketNumber
				},
				status: 5,
				messageLogist: null,
			}
			deleteCalendarEventFromTable(gridOptions, modifiedOrderData)
		}
	}
}
export function wsSlotOnCloseHandler(e) {
	showReloadWindowModal()
}
export function wsSlotOnErrorHandler(e) {
}