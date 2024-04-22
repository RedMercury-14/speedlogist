import { dateHelper, isAdmin, isLogist } from "../utils.js"
import { eventColors } from "./constants.js"

// получение минимальной даты слота
export function getMinUnloadDate(order, role) {
	const now = new Date().getTime()
	const status = order.status
	const isSupplierOrder = status === 5 || status === 8 || status === 100

	if (isAdmin(role)) return now
	if (isLogist(role)) return getMinUnloadDateForLogist(now, order)
	if (isSupplierOrder) return getMinUnloadDateForSupplierOrder(now, order)
	return getMinUnloadDateForManager(now, order)
}

// расчет минимальной даты слота для менеджеров
export function getMinUnloadDateForManager(now, order) {
	// + 2 дня от сегодня (пн на ср)
	const minValidDate = dateHelper.getMinValidDate()
	const nextDay = new Date(minValidDate).setHours(0,0,0,0)
	// + 4 или 24 часа от даты загрузки
	const unloadDateWithDelay = getUnloadDateWithDelay(order)
	return nextDay > unloadDateWithDelay ? nextDay : unloadDateWithDelay
}
// расчет минимальной даты слота для закаов от поставщика (8 статус)
export function getMinUnloadDateForSupplierOrder(now, order) {
	// // + 1 день от сегодня (пн на вт)
	// const dateDelivery = order.dateDelivery
	// const next = new Date(now + dateHelper.DAYS_TO_MILLISECONDS)
	// const nextDay = next.setHours(0, 0, 0, 0)
	// return nextDay > dateDelivery ? nextDay : dateDelivery

	return now
}
// расчет минимальной даты слота для логистов
export function getMinUnloadDateForLogist(now, order) {
	// + 1 день от сегодня (пн на вт)
	const next = new Date(now + dateHelper.DAYS_TO_MILLISECONDS)
	const nextDay = next.setHours(0, 0, 0, 0)
	return nextDay
}

// расчет  выгрузки в зависимости от даты загрузки (4ч для РБ и 24 часа для импорта)
function getUnloadDateWithDelay(order) {
	const way = order.way
	const { loadDate, loadTime } = getLastLoadDateTime(order)
	const [h, m, s] = loadTime.split(':')

	// если маршрут РБ - добавляем 4 часа
	if (way === 'РБ') {
		return new Date(loadDate).setHours(h, m, s, 0) + (4 * dateHelper.MILLISECONDS_IN_HOUR)
	}

	// для остальных маршрутов (импорт) добавляем 24 часа
	return new Date(loadDate).setHours(h, m, s, 0) + (24 * dateHelper.MILLISECONDS_IN_HOUR)
}


// получение времени последней загрузки заказа из адресов заказа
export function getLastLoadDateTime(order) {
	if (!order.addresses || order.addresses.length === 0) {
		return {
			loadDate: order.dateDelivery ? order.dateDelivery : new Date().setHours(0,0,0,0),
			loadTime: '00:00:00',
		}
	}

	const loadPoints = order.addresses
		.filter(point => point.type === 'Загрузка')
		.sort((a, b) => b.date - a.date)
	return {
		loadDate: loadPoints[0].date,
		loadTime: loadPoints[0].time,
	}
}

// получение данных для запроса на сервер
export function getOrderDataForAjax(info, currentStock, currentLogin, currentRole, method) {
	const { event: fcEvent } = info
	const stockId = currentStock.id
	const eventId = fcEvent.id
	const idRamp = fcEvent._def.resourceIds[0]
	const startDateStr = fcEvent.startStr
	const timeDelivery = startDateStr.replace('T', ' ').split('+')[0]
	const eventData = fcEvent.extendedProps.data
	const idOrder = eventData.idOrder
	const numberOfPalls = Number(eventData.pall)
	const orderLogin = eventData.loginManager
	const loginManager = isAdmin(currentRole) || isLogist(currentRole) ? orderLogin : currentLogin
	const messageLogist = null

	let status
	if (method === 'load') status = eventData.status === 5 ? 8 : 7
	if (method === 'update') status = eventData.status
	if (method === 'delete') status = eventData.status === 8 || eventData.status === 100 ? 5 : 6
	if (method === 'confirm') status = eventData.status

	return {
		idOrder,
		idRamp,
		marketNumber: eventId,
		timeDelivery,
		loginManager: loginManager,
		stockId,
		startDateStr,
		numberOfPalls,
		fcEvent,
		status,
		messageLogist,
	}
}


/* ------------ расчет паллетовместимости ------------ */
export function getPallCount(stock, dateStr) {
	return stock.events
		.filter(event => event.start.split('T')[0] === dateStr)
		.reduce((acc, event) => {
			const numberOfPall = Number(event.extendedProps.data.pall)
			return acc + numberOfPall
		}, 0)
}


// группировка заказов по номеру склада доставки
export function groupeByNumStockDelyvery(data) {
	const grouped = data.reduce((acc, curr) => {
		const idRamp = curr.idRamp
		const key = idRamp ? `${idRamp}`.slice(0, -2) : curr.numStockDelivery
		if (!acc[key]) acc[key] = []
		acc[key].push(curr)
		return acc
	}, {})

	return Object.keys(grouped).length !== 0
		? grouped
		: null
}


// получение даты и времени окончания заказа
export function getEndTime(startTimeStr, durationStr) {
	const [hours, minutes] = durationStr.split(':')
	const startDate = new Date(startTimeStr)
	startDate.setHours(startDate.getHours() + parseInt(hours))
	startDate.setMinutes(startDate.getMinutes() + parseInt(minutes))
	return moment(startDate)
}


// конвертеры даты
export function convertToDDMMYYYY(eventDateStr) {
	const date = new Date(eventDateStr)
	const formatter = new Intl.DateTimeFormat('ru', {
		year: "numeric",
		month: "numeric",
		day: "numeric",
	})
	return formatter.format(date)
}
export function convertToDayMonthTime(eventDateStr) {
	const date = new Date(eventDateStr)
	const formatter = new Intl.DateTimeFormat('ru', {
		day: '2-digit',
		month: 'long', 
		hour: '2-digit',
		minute: '2-digit'
	})
	return formatter.format(date)
}

// цвета для заказов
export function getEventBGColor(status) {
	switch (status) {
		case 7: return '#0fcbc9';
		case 8: return '#bdace7';
		case 10: return '#d9d9d9';
		case 15: return '#d9d9d9';
		case 17: return '#d9d9d9';
		case 20: return eventColors.default;
		case 30: return '#ffffb2';
		case 40: return '#ffb2b2';
		case 50: return '#ddfadd';
		case 60: return '#b2d9b2';
		case 70: return '#b2e7ff';
		case 100: return '#9872db';
		default: return '#d9d9d9';
	}
}


export function getSlotStatus(status) {
	switch (status) {
		case 5:
			return 'Виртуальный заказ'
		case 6:
			return 'Заказ на самовывоз'
		case 7:
			return 'Слот на самовывоз, не подтвержден'
		case 8:
			return 'Слот от поставщика, не подтвержден'
		case 100:
			return 'Слот от поставщика, подтвержден'
		case 10:
			return 'Заказ на самовывоз отменен'
		case 20:
			return 'Слот на самовывоз, подтвержден'
		case 30:
			return 'Маршрут на самовывоз (нет на бирже)'
		case 40:
			return 'Маршрут на самовывоз отменен'
		case 50:
			return 'Маршрут на самовывоз (на бирже)'
		case 60:
			return 'Машина на самовывоз найдена'
		case 70:
			return 'Маршрут на самовывоз завершен'
		default:
			return 'Неизвестный статус'
	}
}


// проверка отображения склада
export function stockIsVisible(stockId) {
	const currentStock = store.getCurrentStock()
	if (!currentStock) return false
	return currentStock.id === stockId
}

// проверка отображения склада и даты
export function stockAndDayIsVisible(stockId, eventDate) {
	const currentStock = store.getCurrentStock()
	const currentDate = store.getCurrentDate()
	if (!currentStock || !currentDate) return false
	return currentStock.id === stockId && currentDate === eventDate
}

// даты для получения слотов
export function getDatesToSlotsFetch(dayCount) {
	// плюс-минус указанное количество дней от сегодня
	const now = new Date()
	const startDate = new Date(now.getTime() - (dayCount * 86400000))
	const endDate = new Date(now.getTime() + (dayCount * 86400000))
	return {
		startDate,
		endDate,
		startDateStr: startDate.toISOString().split('T')[0],
		endDateStr: endDate.toISOString().split('T')[0],
	}
}

// получение данных слота для копирования
export function getSlotInfoToCopy(fcEvent, currentStock) {
	const order = fcEvent.extendedProps.data
	const [ date, time ] = fcEvent.startStr.split('T')
	const dateToView = dateHelper.changeFormatToView(date)
	const timeToView = time.slice(0, 5)
	const { counterparty, } = order

	const {address, contact} = currentStock

	const text = `
Подтверждение заказа «ЗАО Доброном»

Зарегистрированный поставщик: ${counterparty}

Дата отгрузки на РЦ: ${dateToView}

Плановое время выгрузки: ${timeToView} (слот на выгрузку на складе - необходимо стать на выгрузку в это время!)

Адрес склада выгрузки: ${address}

Контактный телефон приемки: ${contact}

ВНИМАНИЕ!!! Прибытие на РЦ и передача документов на склад не менее, чем за 30 (тридцать) минут до назначенного времени выгрузки, но не ранее, чем за 2 (два часа).
	`
	return text
}
