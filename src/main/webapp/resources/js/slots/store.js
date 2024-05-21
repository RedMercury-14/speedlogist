import { dateHelper } from "../utils.js"
import { eventColors } from "./constants.js"
import { getEndTime, getEventBGColor, groupeByNumStockDelyvery } from "./dataUtils.js"
import { editableRules, colorRules } from "./rules.js"
import { stocks } from "./virtualStocks.js"

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector("#login").value
const role = document.querySelector("#role").value

const maxPall = {
	externalMovement: 0,
	internalMovement: 0,
}

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
		maxPallRestrictions: null,
		currentMaxPall: 0,
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

	setMaxPallRestrictions(maxPallRestrictionsList) {
		const grouped = maxPallRestrictionsList.reduce((acc, curr) => {
			const key = curr.stockId
			if (!acc[key]) acc[key] = []
			acc[key].push(curr)
			return acc
		}, {})
		this._state.maxPallRestrictions = grouped
	},
	getMaxPallRestrictions() {
		return this._state.maxPallRestrictions
	},
	getMaxPallByDate(stockId, dateStr) {
		const maxPallRestrictionStock = this._state.maxPallRestrictions[stockId]
		if (!maxPallRestrictionStock) return this._state.stocks.find(({ id }) => id === stockId).maxPall
		const maxPallRestriction = maxPallRestrictionStock.find(({ date }) => date === dateStr)
		if (!maxPallRestriction) return this._state.stocks.find(({ id }) => id === stockId).maxPall
		return maxPallRestriction.maxPall
	},
	getMaxPallDataByPeriod(stockId, startDateStr, numDays) {
		const datesArray = dateHelper.getDateStrsArray(startDateStr, numDays)
		return datesArray.map(dateStr => { 
			return {
				date: dateStr,
				maxPall: this.getMaxPallByDate(stockId, dateStr),
			}
		})
	},

	setCurrentMaxPall(currentMaxPall) {
		this._state.currentMaxPall = currentMaxPall
	},
	getCurrentMaxPall() {
		return this._state.currentMaxPall
	},

	getPallCount(stock, dateStr) {
		const isExternalMovementEvent = event => event.extendedProps.data.isInternalMovement !== 'true'
		const isInternalMovementEvent = event => event.extendedProps.data.isInternalMovement === 'true'
		const eventsByDate = event => event.start.split('T')[0] === dateStr
		return stock.events
			.filter(eventsByDate)
			.reduce((acc, event) => {
				const numberOfPall = Number(event.extendedProps.data.pall)
				if (isExternalMovementEvent(event)) {
					acc.externalMovement = acc.externalMovement + numberOfPall
				}
				if (isInternalMovementEvent(event)) {
					acc.internalMovement = acc.internalMovement + numberOfPall
				}
				return acc
			}, {
				externalMovement: 0,
				internalMovement: 0,
			})
	},
	getPallCountForExternalMovement(stock, dateStr) {
		const isExternalMovementEvent = event => event.extendedProps.data.isInternalMovement !== 'true'
		const eventsByDate = event => event.start.split('T')[0] === dateStr && isExternalMovementEvent(event)
		return stock.events
			.filter(eventsByDate)
			.reduce((acc, event) => {
				const numberOfPall = Number(event.extendedProps.data.pall)
				return acc + numberOfPall
			}, 0)
	},
	getPallCountForInternalMovement(stock, dateStr) {
		const isInternalMovementEvent = event => event.extendedProps.data.isInternalMovement === 'true'
		const eventsByDate = event => event.start.split('T')[0] === dateStr && isInternalMovementEvent(event)
		return stock.events
			.filter(eventsByDate)
			.reduce((acc, event) => {
				const numberOfPall = Number(event.extendedProps.data.pall)
				return acc + numberOfPall
			}, 0)
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
		const marketInfo = orderData.hasOwnProperty('marketInfo')
			? orderData.marketInfo
			: orderData.fcEvent.extendedProps.data.marketInfo
				? orderData.fcEvent.extendedProps.data.marketInfo
				: ''
		const index = this._state.orders.findIndex(o => o.marketNumber === marketNumber)
		this._state.orders[index] = {
			...this._state.orders[index],
			status: orderData.status,
			timeDelivery,
			idRamp: orderData.idRamp,
			loginManager: orderData.loginManager,
			marketInfo,
		}
		return this._state.orders[index]
	},
	deleteOrder(orderData) {
		const marketNumber = orderData.marketNumber
		const index = this._state.orders.findIndex(o => o.marketNumber === marketNumber)
		this._state.orders.splice(index, 1)
		return this._state.orders
	},
	// добавление нового заказа из Маркета
	addNewOrderFromMarket(order) {
		this._state.orders.push(order)
		return this._state.orders
	},
	// обновить заказ данными из Маркета
	updateOrderFromMarket(order) {
		const marketNumber = order.marketNumber
		const index = this._state.orders.findIndex(o => o.marketNumber === marketNumber)
		this._state.orders[index] = order
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
	getEvent(stockId, fcEvent) {
		const eventId = fcEvent.id
		const stockIndex = this._state.stocks.findIndex(stock => stock.id === stockId)
		if (stockIndex === -1) return null
		return this._state.stocks[stockIndex].events.find(event => event.id === eventId)
	},

	subscribe (observer) {
		this._callSubscriber = observer
	},
}

window.store = store
