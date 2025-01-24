import { dateHelper, getData, isAdmin, isLogist, isObserver, isSlotsObserver} from "../utils.js"
import { adminLogins, checkScheduleBaseUrl, userMessages } from "./constants.js"
import {
	getMinUnloadDateForLogist,
	getMinUnloadDateForManager,
	getMinUnloadDateForSupplierOrder,
	getOrderType,
} from "./dataUtils.js"

// проверка, является ли пользователь админом приложения
export function isAdminForCalendar(login) {
	return adminLogins.includes(login.toLowerCase())
}


// проверка редактирования ивента по логину
export function editableRules(order, currentLogin, currentRole) {
	const now = new Date().getTime()
	const orderLogin = order.loginManager.toLowerCase()
	const status = order.status
	const isPickupOrder = status === 7
	const isSupplierOrder = status === 8
	
	if (isSlotsObserver(currentRole) || isObserver(currentRole)) return false

	if (
		(
			isAdmin(currentRole)
			&& !hasOrderInYard(order)
			&& isEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin) 
			&& isPickupOrder
			&& !hasOrderInYard(order)
			&& isManagerEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin)
			&& isSupplierOrder
			&& !hasOrderInYard(order)
			&& isSupplierOrderEditableDate(now, order)
		)
		|| (
			isLogist(currentRole)
			&& !hasOrderInYard(order)
			&& isLogistEditableStatuses(status)
			&& isLogistEditableDate(now, order)
		)
		// || (
		// 	isOldSlotNotInYard(order)
		// )
	) {
		return true
	} else {
		return false
	}
}

// проверка доступа к функции подтверждения слота
export function editableRulesToConfirmBtn(order, currentLogin, currentRole) {
	const now = new Date().getTime()
	const orderLogin = order.loginManager.toLowerCase()
	const status = order.status
	const isPickupOrder = status === 7
	const isSupplierOrder = status === 8 || status === 100

	if (isSlotsObserver(currentRole) || isObserver(currentRole)) return false

	return (
			isAdmin(currentRole)
			&& (isPickupOrder || isSupplierOrder)
			&& !hasOrderInYard(order)
			&& isEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin) 
			&& isPickupOrder
			&& !hasOrderInYard(order)
			&& isManagerEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin)
			&& isSupplierOrder
			&& !hasOrderInYard(order)
			&& isSupplierOrderEditableDate(now, order)
		)
}


// правила для менеджеров (закупки)
export function isManagerEditableStatuses(status) {
	return status === 7 || status === 8
}
export function isManagerEditableDate(nowMs, order) {
	const timeDelivery = order.timeDelivery
	const minUnloadDate = getMinUnloadDateForManager(nowMs, order)
	const eventDate = new Date(timeDelivery).getTime()
	return eventDate >= minUnloadDate
}


// правила для логистов
export function isLogistEditableStatuses(status) {
	return status >= 20 && status <= 70
}
export function isLogistEditableDate(nowMs, order) {
	const timeDelivery = order.timeDelivery
	const minUnloadDate = getMinUnloadDateForLogist(nowMs, order)
	const eventDate = new Date(timeDelivery).getTime()
	return eventDate >= minUnloadDate
}


// правила для заказов от поставщика
export function isSupplierOrderEditableDate(nowMs, order) {
	const timeDelivery = order.timeDelivery
	const minUnloadDate = getMinUnloadDateForSupplierOrder(nowMs, order)
	const eventDate = new Date(timeDelivery).getTime()
	return eventDate >= minUnloadDate
}


// общие правила
export function isNotEditableStatuses(status) {
	return status === 100
}
export function isEditableDate(nowMs, order) {
	const timeDelivery = order.timeDelivery
	const eventDate = new Date(timeDelivery).getTime()
	return eventDate >= nowMs
}


// правила доступа к методам работы с БД
export function methodAccessRules(method, order, currentLogin, currentRole) {
	switch (method) {
		case 'preload': return loadMethodAccessRules(order, currentLogin, currentRole)
		case 'load': return loadMethodAccessRules(order, currentLogin, currentRole)
		case 'update': return updateMethodAccessRules(order, currentLogin, currentRole)
		case 'delete': return deleteMethodAccessRules(order, currentLogin, currentRole)
		case 'confirm': return confirmMethodAccessRules(order, currentLogin, currentRole)
		case 'checkSlot': return (isAdmin(currentRole) || currentLogin === 'romashkok%!dobronom.by')
		case 'checkBooking': return (isAdmin(currentRole) || currentLogin === 'romashkok%!dobronom.by')
		case 'editMarketInfo': return !isSlotsObserver(currentRole) && !isObserver(currentRole)
		case 'getOrderFromMarket': return !isSlotsObserver(currentRole) && !isObserver(currentRole)
		default: return true
	}
}
function loadMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole) || isObserver(currentRole)) return false
	return !isLogist(currentRole) && (!isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole))
}
function updateMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole) || isObserver(currentRole)) return false
	return !isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole) || isLogist(currentRole)
}
function deleteMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole) || isObserver(currentRole)) return false
	return !isLogist(currentRole) && (!isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole))
}
function confirmMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole) || isObserver(currentRole)) return false
	return !isLogist(currentRole) && (!isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole))
}

export function removeDraggableElementRules(currentRole) {
	return !isAdmin(currentRole)
		&& !isLogist(currentRole)
		&& !isSlotsObserver(currentRole)
		&& !isObserver(currentRole)
}


export function colorRules(order, currentLogin, currentRole) {
	window.lastOrder = order
	const orderLogin = order.loginManager.toLowerCase()
	return isAdmin(currentRole)
			|| !isAnotherUser(orderLogin, currentLogin)
			|| isLogist(currentRole)
			|| isSlotsObserver(currentRole)
			|| isObserver(currentRole)
}


// проверка разных логинов
export function isAnotherUser(login, currentLogin) {
	if (!currentLogin) return true
	if (!login) return false
	return login.toLowerCase() !== currentLogin
}

// проверка наличия в слотах заказа с указанным номером из маркета (id)
export function checkEventId(id, stocks, dropeZone) {
	let res = false

	stocks.forEach(stock => {
		const event = stock.events.find(event => event.id === id)
		event && (res = true)
	})

	const event = dropeZone.find(order => order.marketNumber === id)
	event && (res = true)

	return res
}

// проверка даты начала ивента на корректность
export function isInvalidEventDate(info, minUnloadDate) {
	const { event: fcEvent } = info
	const newDate = new Date(fcEvent.startStr)
	return newDate < minUnloadDate
}

// проверка попадания ивента на пересменку. Пример shiftChangeArray: ['08:00', '09:00', '20:00', '21:00']
export function isOverlapWithShiftChange(info, shiftChangeArray) {
	if(!shiftChangeArray || shiftChangeArray.length === 0) return false
	const shiftChangeCount = shiftChangeArray.length / 2
	if (shiftChangeCount === 0) return false

	const { event: fcEvent } = info
	const currentDate = new Date(fcEvent.startStr)
	const eventStartMs = new Date(fcEvent.start).getTime()
	const eventEndMs = new Date(fcEvent.end).getTime()

	// одна пересменка в сутки
	if (shiftChangeCount === 1) {
		const [ SH1startH, SH1startM ] = shiftChangeArray[0].split(':')
		const [ SH1endH, SH1endM ] = shiftChangeArray[1].split(':')
		// время начала и конца пересменки
		const shiftСhangeStartMs = currentDate.setUTCHours(SH1startH, SH1startM,0, 0)
		const shiftСhangeEndMs = currentDate.setUTCHours(SH1endH, SH1endM,0, 0)

		// если ивент начинается раньше и заканчивается в течение получаса пересменки
		if (eventStartMs < shiftСhangeStartMs
			&& eventEndMs <= shiftСhangeStartMs + (dateHelper.MILLISECONDS_IN_HOUR / 2)
		) return false

		// если ивент начинается после пересменки
		if (eventStartMs >= shiftСhangeEndMs) return false
	}

	// две пересменки в сутки
	if (shiftChangeCount === 2) {
		const [ SH1startH, SH1startM ] = shiftChangeArray[0].split(':')
		const [ SH1endH, SH1endM ] = shiftChangeArray[1].split(':')
		const [ SH2startH, SH2startM ] = shiftChangeArray[2].split(':')
		const [ SH2endH, SH2endM ] = shiftChangeArray[3].split(':')
		// время начала и конца утренней пересменки
		const morningShiftСhangeStartMs = currentDate.setUTCHours(SH1startH, SH1startM, 0, 0)
		const morningShiftСhangeEndMs = currentDate.setUTCHours(SH1endH, SH1endM, 0, 0)
		// время начала и конца вечерней пересменки
		const eveningShiftСhangeStartMs = currentDate.setUTCHours(SH2startH, SH2startM, 0, 0)
		const eveningShiftСhangeEndMs = currentDate.setUTCHours(SH2endH, SH2endM, 0, 0)

		// если ивент начинается раньше утренней пересменки и заканчивается в течение получаса утренней пересменки
		if (eventStartMs < morningShiftСhangeStartMs
			&& eventEndMs <= morningShiftСhangeStartMs + (dateHelper.MILLISECONDS_IN_HOUR / 2)
		) return false

		// если ивент начинается позже утренней пересменки и раньше вечерней пересменки,
		// а заканчивается в течение получаса вечерней пересменки
		if (eventStartMs >= morningShiftСhangeEndMs
			&& eventStartMs < eveningShiftСhangeStartMs
			&& eventEndMs <= eveningShiftСhangeStartMs + (dateHelper.MILLISECONDS_IN_HOUR / 2)
		) return false

		// если ивент начинается после вечерней пересменки
		if (eventStartMs >= eveningShiftСhangeEndMs) return false
	}

	return true
}

// проверка попадания ивента на промежуток для внутренних перемещений (ВП)
export function isOverlapWithInternalMovementTime(info, internalMovementTimes, internalMovementsRamps) {
	if(!internalMovementTimes || internalMovementTimes.length < 2) return false

	const { event: fcEvent } = info
	const rampId = fcEvent._def.resourceIds[0]

	// если слот на ВП
	const isInternalMovement = fcEvent.extendedProps.data.isInternalMovement
	if (isInternalMovement === 'true') return false

	// если рампа не в числе рамп для ВП
	if (!internalMovementsRamps.includes(rampId)) return false

	const currentDate = new Date(fcEvent.startStr)
	const eventStartMs = new Date(fcEvent.start).getTime()
	const eventEndMs = new Date(fcEvent.end).getTime()
	const [ SH1startH, SH1startM ] = internalMovementTimes[0].split(':')
	const [ SH1endH, SH1endM ] = internalMovementTimes[1].split(':')

	// время начала и конца промежутка для ВП
	const internalMovementTimeStartMs = currentDate.setUTCHours(SH1startH, SH1startM,0, 0)
	const internalMovementTimeEndMs = currentDate.setUTCHours(SH1endH, SH1endM,0, 0)

	// если ивент начинается раньше и заканчивается до начала промежутка
	if (
		eventStartMs < internalMovementTimeStartMs
		&& eventEndMs <= internalMovementTimeStartMs
	) return false

	// если ивент начинается после промежутка
	if (eventStartMs >= internalMovementTimeEndMs) return false

	return true
}

// проверка установки слота логистом в зону внутренних перемещений
export function isOverlapWithInternalMovementTimeForLogists(info, internalMovementTimes, internalMovementsRamps, role) {
	if(!internalMovementTimes || internalMovementTimes.length < 2) return false

	const { event: fcEvent } = info
	const rampId = fcEvent._def.resourceIds[0]

	if (isLogist(role)) return false

	// если рампа не в числе рамп для ВП
	if (!internalMovementsRamps.includes(rampId)) return false

	const currentDate = new Date(fcEvent.startStr)
	const eventStartMs = new Date(fcEvent.start).getTime()
	const eventEndMs = new Date(fcEvent.end).getTime()
	const [ SH1startH, SH1startM ] = internalMovementTimes[0].split(':')
	const [ SH1endH, SH1endM ] = internalMovementTimes[1].split(':')

	// время начала и конца промежутка для ВП
	const internalMovementTimeStartMs = currentDate.setUTCHours(SH1startH, SH1startM,0, 0)
	const internalMovementTimeEndMs = currentDate.setUTCHours(SH1endH, SH1endM,0, 0)

	// если ивент начинается раньше и заканчивается до начала промежутка
	if (
		eventStartMs < internalMovementTimeStartMs
		&& eventEndMs <= internalMovementTimeStartMs
	) return false

	// если ивент начинается после промежутка
	if (eventStartMs >= internalMovementTimeEndMs) return false

	return true
}

// проверка паллетовместимости склада
export function checkPallCount(info, pallCount, maxPall, currentStock) {
	const { event: fcEvent } = info
	const order = fcEvent.extendedProps.data
	const orderType = getOrderType(order)

	//---------------------------------------------------------
	// ДЛЯ 1700 И 1800 не проверять паллетовместимость в период с 00:00 по 07:00
	if (orderType === 'externalMovement' && currentStock.id === '1800') {
		const startHours = fcEvent.start.getUTCHours()
		const endHours = fcEvent.end.getUTCHours()
		if (startHours < 7 && endHours < 7) return true
	}
	//---------------------------------------------------------

	const numberOfPalls = Number(order.pall)
	const pallCountValue = pallCount[orderType]
	const maxPallValue = maxPall[orderType]
	const newPallCount = pallCountValue + numberOfPalls

	return newPallCount <= maxPallValue
}

// проверка паллетовместимости склада на соседние даты
export function checkPallCountForComingDates(info, pallCount, maxPall, currentStock) {
	const { event: fcEvent } = info
	const oldEvent = info.oldEvent
	const order = fcEvent.extendedProps.data
	const orderType = getOrderType(order)

	//---------------------------------------------------------
	// ДЛЯ 1700 И 1800 не разрешать переносить слот с периода с 00:00 по 07:00 при перелимите
	if (orderType === 'externalMovement' && currentStock.id === '1800') {
		const endHours = fcEvent.end.getUTCHours()
		const oldEventStartHours = oldEvent.start.getUTCHours()
		const oldEventSndHours = oldEvent.end.getUTCHours()
		if (oldEventStartHours < 7 && oldEventSndHours < 7
			&& endHours >= 7
			&& pallCount.externalMovement > maxPall.externalMovement
		) return false
	}
	//---------------------------------------------------------

	const eventDateStr = fcEvent.startStr.split('T')[0]
	const oldEventDateStr = oldEvent.startStr.split('T')[0]
	const numberOfPalls = Number(order.pall)
	const pallCountValue = pallCount[orderType]
	const maxPallValue = maxPall[orderType]
	
	if (oldEventDateStr !== eventDateStr) {

		//---------------------------------------------------------
		// ДЛЯ 1700 И 1800 не проверять паллетовместимость в период с 00:00 по 07:00
		if (orderType === 'externalMovement' && currentStock.id === '1800') {
			const startHours = fcEvent.start.getUTCHours()
			const endHours = fcEvent.end.getUTCHours()
			if (startHours < 7 && endHours < 7) return true
		}
		//---------------------------------------------------------

		return pallCountValue + numberOfPalls <= maxPallValue
	}
	return true
}

// проверка на старый заказ текущего пользователя от поставщика
// (старым считается и сегодняшний заказ - т.е. заказ с датой начала меньше,
// чем 00:00 завтрашнего дня)
export function isOldSupplierOrder(info, currentLogin) {
	// const nowMs = Date.now()
	const tomorrowMs = new Date().setHours(24, 0, 0, 0)
	const { event: fcEvent } = info
	const eventDate = new Date(fcEvent.startStr).getTime()
	const order = fcEvent.extendedProps.data
	const orderLogin = order.loginManager.toLowerCase()
	const status = order.status
	const isSupplierOrder = status === 8 || status === 100

	return (isSupplierOrder) && !isAnotherUser(orderLogin, currentLogin) && eventDate < tomorrowMs
}


// проверка, что слот станый и не имеет статуса во Дворе
export function isOldSlotNotInYard(order) {
	const nowDateMs = new Date().setHours(0, 0, 0, 0)
	return order.timeDelivery && !hasOrderInYard(order) && (order.timeDelivery < nowDateMs)
}

// проверка, взят ли заказ в работу во Дворе
export function hasOrderInYard(order) {
	const statusYard = order.statusYard
	return statusYard === 20 || statusYard === 30 || statusYard === 40
}

export function isBackgroundEvent(fcEvent) {
	return fcEvent.display === 'background'
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

// ограничения по постановке слота в новогодний период
export function newYearRestrictions(fcEvent) {
	const startRestrictionDate = new Date('2024-12-31T12:00:00').getTime()
	const endRestrictionDate = new Date('2025-01-02T09:00:00').getTime()
	const startEvent = fcEvent.startStr.split('+')[0]
	const endEvent = fcEvent.endStr.split('+')[0]
	const start = new Date(startEvent).getTime()
	const end = new Date(endEvent).getTime()
	return end >= startRestrictionDate && start <= endRestrictionDate - 1
}


/**
 * Проверяет, попадает ли событие в пользовательские ограничения по времени и складу
 * @param {Object} fcEvent - объект календарного события, содержащий время начала/окончания
 * @param {Object} currentStock - объект текущего склада
 * @param {Object} restrictionsProps - объект, содержащий свойства ограничений:
* -    `startDateTimeStr`: дата/время начала периода ограничения в формате yyyy-mm-ddThh:mm:ss
* -    `endDateTimeStr`: дата/время окончания периода ограничения в формате yyyy-mm-ddThh:mm:ss
* -    `stockId`: необязательный идентификатор склада для ограничения по определенному складу
 * @returns {boolean} находится ли слот в пределах ограничений
 */
export function customRestrictions(fcEvent, currentStock, restrictionsProps) {
	const startRestrictionDate = new Date(restrictionsProps.startDateTimeStr).getTime()
	const endRestrictionDate = new Date(restrictionsProps.endDateTimeStr).getTime() 

	const startEvent = fcEvent.startStr.split('+')[0]
	const endEvent = fcEvent.endStr.split('+')[0]
	const start = new Date(startEvent).getTime()
	const end = new Date(endEvent).getTime()
	const stockId = currentStock.id

	const stockRestrictions = restrictionsProps.stockId ? stockId === restrictionsProps.stockId : true

	return end >= startRestrictionDate
		&& start <= endRestrictionDate - 1
		&& stockRestrictions
}

// проверка совпадения склада их Маркета и текущего склада
export function isMatchNumStockDelivery(numStockDelivery, currentStock) {
	if (!numStockDelivery || !currentStock) return true
	if (numStockDelivery !== currentStock.id) {
		const res = confirm(
			`Данный заказ предназначается для ${numStockDelivery} склада. `
			+ `Вы уверены, что хотите установить его на ${currentStock.id} склад?`
		)
		return res
	}
	return true
}