import { dateHelper, isAdmin, isLogist, isSlotsObserver} from "../utils.js"
import { adminLogins } from "./constants.js"
import {
	getMinUnloadDateForLogist,
	getMinUnloadDateForManager,
	getMinUnloadDateForSupplierOrder,
	getPallCount,
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

	if (isSlotsObserver(currentRole)) return false

	if (
		(
			isAdmin(currentRole)
			&& !isNotEditableStatuses(status)
			&& isEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin) 
			&& isPickupOrder
			&& isManagerEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin)
			&& isSupplierOrder
			&& isSupplierOrderEditableDate(now, order)
		)
		|| (
			isLogist(currentRole)
			&& isLogistEditableStatuses(status)
			&& isLogistEditableDate(now, order))
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

	if (isSlotsObserver(currentRole)) return false

	return (
			isAdmin(currentRole)
			&& (isPickupOrder || isSupplierOrder)
			&& isEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin) 
			&& isPickupOrder
			&& isManagerEditableDate(now, order)
		)
		|| (
			!isAnotherUser(orderLogin, currentLogin)
			&& isSupplierOrder
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
	return status >= 20 && status <= 60
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
	return status === 70 || status === 100
}
export function isEditableDate(nowMs, order) {
	const timeDelivery = order.timeDelivery
	const eventDate = new Date(timeDelivery).getTime()
	return eventDate >= nowMs
}


// правила доступа к методам работы с БД
export function methodAccessRules(method, order, currentLogin, currentRole) {
	switch (method) {
		case 'load': return loadMethodAccessRules(order, currentLogin, currentRole)
		case 'update': return updateMethodAccessRules(order, currentLogin, currentRole)
		case 'delete': return deleteMethodAccessRules(order, currentLogin, currentRole)
		case 'confirm': return confirmMethodAccessRules(order, currentLogin, currentRole)
		default: return true
	}
}
function loadMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole)) return false
	return !isLogist(currentRole) && (!isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole))
}
function updateMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole)) return false
	return !isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole) || isLogist(currentRole)
}
function deleteMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole)) return false
	return !isLogist(currentRole) && (!isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole))
}
function confirmMethodAccessRules(order, currentLogin, currentRole) {
	const orderLogin = order.loginManager.toLowerCase()
	if (isSlotsObserver(currentRole)) return false
	return !isLogist(currentRole) && (!isAnotherUser(orderLogin, currentLogin) || isAdmin(currentRole))
}


export function colorRules(order, currentLogin, currentRole) {
	window.lastOrder = order
	const orderLogin = order.loginManager.toLowerCase()
	return isAdmin(currentRole)
			|| !isAnotherUser(orderLogin, currentLogin)
			|| isLogist(currentRole)
			|| isSlotsObserver(currentRole)
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

// проверка паллетовместимости склада
export function checkPallCount(numberOfPalls, maxPall) {
	const pallCountElem = document.querySelector('#pallCount')
	const maxPallElem = document.querySelector('#maxPall')

	if (!pallCountElem || !maxPallElem) {
		return false
	}

	const currentPallCount = Number(pallCountElem.innerText)
	const newPallCount = currentPallCount + numberOfPalls

	return newPallCount <= maxPall
}

// проверка паллетовместимости склада на соседние даты
export function checkPallCountForComingDates(currentStock, oldEventDateStr, eventDateStr, numberOfPalls) {
	const maxPall = currentStock.maxPall
	if (oldEventDateStr !== eventDateStr) {
		const pallCountOfSelectedDay = getPallCount(currentStock, eventDateStr)
		return pallCountOfSelectedDay + numberOfPalls <= maxPall
	}
	return true
}

// проверка на старый заказ текущего пользователя от поставщика 
export function isOldSupplierOrder(info, currentLogin) {
	const nowMs = Date.now()
	const { event: fcEvent } = info
	const eventDate = new Date(fcEvent.startStr).getTime()
	const order = fcEvent.extendedProps.data
	const orderLogin = order.loginManager.toLowerCase()
	const status = order.status
	const isSupplierOrder = status === 8 || status === 100

	return (isSupplierOrder) && !isAnotherUser(orderLogin, currentLogin) && eventDate < nowMs
}