import { eventColors } from "./constants.js"
import { getEndTime, getEventBGColor, groupeByNumStockDelyvery } from "./dataUtils.js"
import { editableRules, colorRules } from "./rules.js"
import { stocks } from "./virtualStocks.js"

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector("#login").value
const role = document.querySelector("#role").value

export const store = {
	_state: {
		token,
		login,
		role,
		slotToConfirm: null,
		stocks,
		dropZone: [],
		orders: null,
		currentStock: null,
		currentDate: null,
	},
	_callSubscriber(state) {
		console.log('subscriber is not defind')
	},

	getState() {
		return this._state
	},

	getToken() {
		return this._state.token
	},
	getLogin() {
		return this._state.login
	},
	getRole() {
		return this._state.role
	},

	setslotToConfirm(fcEvent) {
		this._state.slotToConfirm = fcEvent
	},
	getSlotToConfirm() {
		return this._state.slotToConfirm
	},

	getOrders() {
		return this._state.orders
	},
	getCurrentStockOrders() {
		const stockId = this._state.currentStock.id
		return this._state.orders.filter(order => order.numStockDelivery === stockId)
	},
	setOrders(orders) {
		this._state.orders = orders
	},
	getOrderByMarketNumber(marketNumber) {
		return this._state.orders.find(order => order.marketNumber === marketNumber)
	},
	updateOrder(orderData) {
		const marketNumber = orderData.marketNumber
		const timeDelivery = orderData.timeDelivery ? new Date(orderData.timeDelivery).getTime() : null
		const index = this._state.orders.findIndex(o => o.marketNumber === marketNumber)
		this._state.orders[index] = {
			...this._state.orders[index],
			status: orderData.status,
			timeDelivery,
			idRamp: orderData.idRamp,
			loginManager: orderData.loginManager
		}
		return this._state.orders[index]
	},
	deleteOrder(orderData) {
		const marketNumber = orderData.marketNumber
		const index = this._state.orders.findIndex(o => o.marketNumber === marketNumber)
		this._state.orders.splice(index, 1)
		return this._state.orders
	},


	setStocks(stocks) {
		this._state.stocks = stocks
	},
	getStocks() {
		return this._state.stocks
	},
	getStockById(stockId) {
		return this._state.stocks.find(stock => stock.id === stockId)
	},
	setStockEvents() {
		const orders = this._state.orders
		const groupedOrders = groupeByNumStockDelyvery(orders)
		let ordersByCallendar = []

		for (const key in groupedOrders) {
			if (Object.hasOwnProperty.call(groupedOrders, key)) {
				const ordersByStock = groupedOrders[key]
				const res = ordersByStock.filter(order => order.idRamp)
				ordersByCallendar.push(...res)
			}
		}

		ordersByCallendar.forEach(order => {
			const stockId = `${order.idRamp}`.slice(0, -2)
			const stock = this._state.stocks.find(stock => stock.id === stockId)
			const end = getEndTime(order.timeDelivery, order.timeUnload).format()
			const editable = order.loginManager ? editableRules(order, this._state.login, this._state.role) : true
			const bgColor = colorRules(order, this._state.login, this._state.role)
				? getEventBGColor(order.status)
				: eventColors.disabled
			const event = {
				resourceId: `${order.idRamp}`,
				id: `${order.marketNumber}`,
				title: order.counterparty,
				start: moment(order.timeDelivery).format(),
				end,
				extendedProps: { data: order },
				startEditable: editable,
				durationEditable: false,
				resourceEditable: editable,
				backgroundColor: bgColor,
				borderColor: eventColors.borderColor,
				textColor: 'black',
				// constraint: stockId === '1700' ? null : 'businessHours',
			}

			if (stockId !== '1700') event.constraint = 'businessHours'

			stock.events.push(event)
		})
	},


	setCurrentStock(stock) {
		this._state.currentStock = stock
	},
	getCurrentStock() {
		return this._state.currentStock
	},

	setCurrendDate(date) {
		this._state.currentDate = date
	},
	getCurrentDate() {
		return this._state.currentDate
	},


	getDropZone() {
		return this._state.dropZone
	},
	addEventToDropZone(order) {
		this._state.dropZone.push(order)
	},
	removeEventFromDropZone(id) {
		this._state.dropZone = this._state.dropZone.filter(order => order.marketNumber !== id)
	},


	addEvent(orderData, updatedOrder) {
		const stockId = `${orderData.idRamp}`.slice(0, -2)
		const idRamp = orderData.idRamp
		const fcEvent = orderData.fcEvent
		const login = updatedOrder.loginManager
		const editable = login ? editableRules(updatedOrder, this._state.login, this._state.role) : true
		const bgColor = colorRules(updatedOrder, this._state.login, this._state.role)
			? getEventBGColor(updatedOrder.status)
			: eventColors.disabled
		const newEvent = {
			resourceId: idRamp,
			id: fcEvent.id,
			title: fcEvent.title,
			start: fcEvent.startStr ? fcEvent.startStr : fcEvent.start,
			end: fcEvent.endStr ? fcEvent.endStr : fcEvent.end,
			extendedProps: { data: updatedOrder },
			startEditable: editable,
			durationEditable: false,
			resourceEditable: editable,
			backgroundColor: bgColor,
			borderColor: eventColors.borderColor,
			textColor: 'black',
			// constraint: stockId === '1700' ? null : 'businessHours',
		}

		if (stockId !== '1700') newEvent.constraint = 'businessHours'

		const index = this._state.stocks.findIndex(stock => stock.id === stockId)
		this._state.stocks[index].events.push(newEvent)
		this._callSubscriber(this._state)
	},
	updateEvent(orderData, updatedOrder) {
		const stockId = `${orderData.idRamp}`.slice(0, -2)
		const idRamp = orderData.idRamp
		const fcEvent = orderData.fcEvent
		const eventId = fcEvent.id
		const login = updatedOrder.loginManager
		const editable = login ? editableRules(updatedOrder, this._state.login, this._state.role) : true
		const bgColor = colorRules(updatedOrder, this._state.login, this._state.role)
			? getEventBGColor(updatedOrder.status)
			: eventColors.disabled
		const stockIndex = this._state.stocks.findIndex(stock => stock.id === stockId)
		const eventIndex = this._state.stocks[stockIndex].events.findIndex(event => event.id === eventId)
		this._state.stocks[stockIndex].events[eventIndex] = {
			...this._state.stocks[stockIndex].events[eventIndex],
			resourceId: idRamp,
			start: fcEvent.startStr ? fcEvent.startStr : fcEvent.start,
			end: fcEvent.endStr ? fcEvent.endStr : fcEvent.end,
			extendedProps: { data: updatedOrder },
			startEditable: editable,
			durationEditable: false,
			resourceEditable: editable,
			backgroundColor: bgColor,
			borderColor: eventColors.borderColor,
			textColor: 'black',
			// constraint: stockId === '1700' ? null : 'businessHours',
		}

		if (stockId !== '1700') this._state.stocks[stockIndex].events[eventIndex].constraint = 'businessHours'

		this._callSubscriber(this._state)
	},
	removeEvent(stockId, fcEvent) {
		const eventId = fcEvent.id
		const stockIndex = this._state.stocks.findIndex(stock => stock.id === stockId)
		const eventIndex = this._state.stocks[stockIndex].events.findIndex(event => event.id === eventId)
		this._state.stocks[stockIndex].events.splice(eventIndex, 1)
		this._callSubscriber(this._state)
	},

	subscribe (observer) {
		this._callSubscriber = observer
	},
}

window.store = store
