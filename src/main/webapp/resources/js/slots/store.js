import { dateHelper } from "../utils.js"
import { eventColors, stocks24h } from "./constants.js"
import { getEndTime, getEventBGColor, getEventBorderColor, groupeByNumStockDelyvery } from "./dataUtils.js"
import { editableRules, colorRules } from "./rules.js"
import { stocks } from "../globalRules/virtualStocksForSlots.js"
import { getVirtualStockId } from "../globalRules/ordersRules.js"

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector("#login").value
const role = document.querySelector("#role").value

export const store = {
	_state: {
		needMultiplicity: false,
		isReady: false,
		isComplexUpdate: false,
		token,
		login,
		role,
		calendarInfo: null,
		slotToConfirm: null,
		stocks,
		dropZone: [],
		dropZoneList: new Set(),
		orders: null,
		currentStock: null,
		currentDate: null,
		maxPallRestrictions: null,
		currentMaxPall: 0,
	},
	_callSubscriber(state) {
		console.log('subscriber is not defind')
	},

	getNeedMultiplicity() {
		return this._state.needMultiplicity
	},
	setNeedMultiplicity(needMultiplicity) {
		this._state.needMultiplicity = needMultiplicity
	},

	getReady() {
		return this._state.isReady
	},
	setReady(isReady) {
		this._state.isReady = isReady
	},

	getComplexUpdate() {
		return this._state.isComplexUpdate
	},
	setComplexUpdate(isComplexUpdate) {
		this._state.isComplexUpdate = isComplexUpdate
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

	setCalendarInfo(info) {
		this._state.calendarInfo = info
	},
	getCalendarInfo() {
		return this._state.calendarInfo
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
		const eventsByDate = event => event.start && event.start.split('T')[0] === dateStr
		return stock.events
			.filter(eventsByDate)
			.reduce((acc, event) => {
				const numberOfPall = Number(event.extendedProps.data.pall)
				// acc.externalMovement = acc.externalMovement + numberOfPall
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
		const currentStockId = this._state.currentStock.id
		return this._state.orders.filter(order => {
			const stockId = order.idRamp ? String(order.idRamp).slice(0, -2) : order.numStockDelivery
			return getVirtualStockId(stockId) === currentStockId
		})
	},
	setOrders(orders) {
		this._state.orders = orders
	},
	getOrderByMarketNumber(marketNumber) {
		return this._state.orders.find(order => order.marketNumber === marketNumber)
	},
	getOrderById(idOrder) {
		return this._state.orders.find(order => order.idOrder === idOrder)
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
		// если заказа нет, то создаем его
		if (index === -1 && orderData.idOrder) {
			const newOrder = {
				...orderData.fcEvent.extendedProps.data,
				status: orderData.status,
				timeDelivery,
				idRamp: Number(orderData.idRamp),
				loginManager: orderData.loginManager,
				marketInfo,
			}
			this._state.orders.push(newOrder)
			return newOrder
		}
		this._state.orders[index] = {
			...this._state.orders[index],
			status: orderData.status,
			timeDelivery,
			idRamp: Number(orderData.idRamp),
			loginManager: orderData.loginManager,
			marketInfo,
			link: orderData.link,
			// statusForSupplier: orderData.statusForSupplier
		}
		// для сообщений от Двора
		if (orderData.hasOwnProperty('statusYard')) {
			this._state.orders[index] = {
				...this._state.orders[index],
				statusYard: orderData.statusYard,
				unloadFinishYard: orderData.unloadFinishYard !== 'null' ? orderData.unloadFinishYard : null,
				unloadStartYard: orderData.unloadStartYard !== 'null' ? orderData.unloadStartYard : null,
				weightFactYard: orderData.weightFactYard !== 'null' ? orderData.weightFactYard : null,
				pallFactYard: orderData.pallFactYard !== 'null' ? orderData.pallFactYard : null,
				arrivalFactYard: orderData.arrivalFactYard !== 'null' ? orderData.arrivalFactYard : null,
				registrationFactYard: orderData.registrationFactYard !== 'null' ? orderData.registrationFactYard : null,
			}
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
				borderColor: getEventBorderColor(order),
				textColor: 'black',
				constraint: stocks24h.includes(stockId) ? null : 'businessHours',
			}

			stock.events.push(event)
		})
	},

	// добавление фона для зоны внутренних перемещений
	setBGEvents(events) {
		events.forEach(bgEvent => {
			const stockId = bgEvent.resourceId.slice(0, -2)
			this._state.stocks.forEach(stock => {
				if (stock.id === stockId) stock.events.push(bgEvent)
			})
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

	getDropZoneList() {
		return 

	},
	addToDropZoneList(eventId) {
		this._state.dropZoneList.add(eventId)
	},
	removeFromDropZoneList(eventId) {
		return this._state.dropZoneList.delete(eventId)
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
			borderColor: getEventBorderColor(updatedOrder),
			textColor: 'black',
			constraint: stocks24h.includes(stockId) ? null : 'businessHours',
		}

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
			borderColor: getEventBorderColor(updatedOrder),
			textColor: 'black',
			constraint: stocks24h.includes(stockId) ? null : 'businessHours',
		}

		this._callSubscriber(this._state)
	},
	removeEvent(stockId, eventId) {
		const stockIndex = this._state.stocks.findIndex(stock => stock.id === stockId)
		const eventIndex = this._state.stocks[stockIndex].events.findIndex(event => event.id === eventId)
		this._state.stocks[stockIndex].events.splice(eventIndex, 1)
		this._callSubscriber(this._state)
	},
	getEvent(stockId, eventId) {
		if (stockId === 'all') {
			return this._state.stocks.flatMap(stock => stock.events).find(event => event.id === eventId)
		}
		const stockIndex = this._state.stocks.findIndex(stock => stock.id === stockId)
		if (stockIndex === -1) return null
		return this._state.stocks[stockIndex].events.find(event => event.id === eventId)
	},
	getCalendarEvents() {
		return this._state.stocks.flatMap(stock => stock.events)
	},
	getTodayEvents() {
		const currentDate = this.getCurrentDate()
		const currentStock = this.getCurrentStock()
		if (!currentStock) return null
		return currentStock.events.filter(event => event.start && moment(event.start).isSame(currentDate, 'day'))
	},

	subscribe (observer) {
		this._callSubscriber = observer
	},
}

window.store = store
