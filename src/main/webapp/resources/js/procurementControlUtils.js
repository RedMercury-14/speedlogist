import { cell } from "./AG-Grid/ag-grid-utils.js"
import { dateHelper, getStatus } from "./utils.js"

export function mapCallbackForProcurementControl(order) {
	const controlToView = order.control ? 'Да' : 'Нет'
	const telephoneManagerToView = order.telephoneManager ? `; тел. ${order.telephoneManager}` : ''
	const managerToView = `${order.manager}${telephoneManagerToView}`
	const statusToView = getStatus(order.status)
	const stackingToView = order.stacking ? 'Да' : 'Нет'
	const logistToView = order.logist && order.logistTelephone
		? `${order.logist}, тел. ${order.logistTelephone}`
		: order.logist
			? order.logist
			: order.logistTelephone
				? order.logistTelephone : ''

	const unloadWindowToView = order.onloadWindowDate && order.onloadWindowTime
		? `${dateHelper.getFormatDate(order.onloadWindowDate)} ${order.onloadWindowTime.slice(0, 5)}`
		: ''

	const timeDeliveryToView = order.timeDelivery ? dateHelper.convertToDayMonthTime(order.timeDelivery) : ''

	const filtredAdresses =  order.addresses ? order.addresses.filter(address => address.isCorrect) : []
	const loadPoints = filtredAdresses.filter(address => address.type === "Загрузка")

	const addressesToView = filtredAdresses
		.sort(pointSorting)
		.map(getPointToView)

	const loadPointsToView = addressesToView
		.filter(address => address.type === "Загрузка")
		.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
		.join(' ')

	const unloadPointsToView = addressesToView
		.filter(address => address.type === "Выгрузка")
		.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
		.join(' ')

	const loadDateToView = getFirstLoadDateToView(addressesToView)
	const unloadDateToView = getLastUnloadDateToView(addressesToView)

	const summPall = loadPoints.reduce((acc, address) => {
		if (address.pall) {
			const pall = Number(address.pall)
			acc += pall
			return acc
		}
	}, 0)

	const summVolume = loadPoints.reduce((acc, address) => {
		if (address.volume) {
			const volume = Number(address.volume)
			acc += volume
			return acc
		}
	}, 0)

	const summWeight = loadPoints.reduce((acc, address) => {
		if (address.weight) {
			const weight = Number(address.weight)
			acc += weight
			return acc
		}
	}, 0)

	const routeInfo = getRouteInfo(order)
	const routePrice = getRoutePrice(order)
	const wayToView = getWayToView(order)

	return {
		...order,
		controlToView,
		addressesToView,
		loadDateToView,
		unloadDateToView,
		managerToView,
		statusToView,
		stackingToView,
		logistToView,
		unloadWindowToView,
		loadPointsToView,
		unloadPointsToView,
		summPall: summPall ? summPall : null,
		summVolume: summVolume ? summVolume : null,
		summWeight: summWeight ? summWeight : null,
		routeInfo,
		routePrice,
		wayToView,
		timeDeliveryToView,
	}
}


// шаблон для экспорта таблицы заявок
export const procurementExcelExportParams = {
	getCustomContentBelowRow: (params) => getRows(params),
	processCellCallback: ({ value, formatValue }) => formatValue(value),
	columnWidth: 120,
	fileName: "orders.xlsx",
}

function getRows(params) {
	const rows = [
		{
			outlineLevel: 1,
			cells: [
				cell(""),
				cell("№", "header"),
				cell("Тип точки", "header"),
				cell("Дата", "header"),
				cell("Время", "header"),
				cell("Коды ТН ВЭД", "header"),
				cell("Паллеты", "header"),
				cell("Масса", "header"),
				cell("Объем", "header"),
				cell("Адрес", "header"),
				cell("Время работы", "header"),
				cell("Контактное лицо", "header"),
				cell("Адрес таможни", "header"),
			],
		},
	].concat(
		...params.node.data.addressesToView
			.sort((a, b) => a.type > b.type ? -1 : (a.type < b.type ? 1 : 0))
			.map((record, i) => [
			{
				outlineLevel: 1,
				cells: [
					cell(""),
					cell(i + 1, "body"),
					cell(record.type, "body"),
					cell(dateHelper.getFormatDate(record.date), "body"),
					cell(record.time, "body"),
					cell(record.tnvd, "body"),
					cell(record.pall, "body"),
					cell(record.weight, "body"),
					cell(record.volume, "body"),
					cell(record.bodyAddress, "body"),
					cell(record.timeFrame, "body"),
					cell(record.contact, "body"),
					cell(record.customsAddress, "body"),
				],
			},
		])
	)
	return rows
}

// стили для экспортируемой таблицы заявок
export const excelStyles = [
	{
		id: "header",
		interior: {
			color: "#aaaaaa",
			pattern: "Solid",
		},
	},
	{
		id: "body",
		interior: {
			color: "#dddddd",
			pattern: "Solid",
		},
	},
]


export function pointSorting(a, b) {
	if (!a.pointNumber) return 0
	if (!b.pointNumber) return 0
	if (a.pointNumber > b.pointNumber) return 1
	if (a.pointNumber < b.pointNumber) return -1
	return 0
}

export function getPointToView(address, i, addresses) {
	const dateToView = dateHelper.getFormatDate(address.date)
	const cargo = address.cargo ? `Груз: ${address.cargo}; ` : ''
	const pall = address.pall ? `Паллеты: ${address.pall} шт; ` : ''
	const weight = address.weight ? `Масса: ${address.weight} кг; ` : ''
	const volume = address.volume ? `Объем: ${address.volume} м.куб. ` : ''
	const info = cargo + pall + weight + volume

	const pointNumber = i + 1

	return {
		...address,
		dateToView,
		info,
		pointNumber,
	}
}

function getFirstLoadDateToView(addressesToView) {
	if (!addressesToView) return ''
	const loadPoints = addressesToView.filter(address => address.type === "Загрузка")
	if (!loadPoints.length || loadPoints.length === 0) return ''
	if (!loadPoints[0].dateToView) return ''
	return new Date(dateHelper.changeFormatToInput(loadPoints[0].dateToView)).getTime()
}

function getLastUnloadDateToView(addressesToView) {
	if (!addressesToView) return ''
	const unloadPoints = addressesToView
		.filter(address => address.type === "Выгрузка")
		.sort((a, b) => b.date - a.date)
	if (!unloadPoints.length || unloadPoints.length === 0) return ''
	if (!unloadPoints[0].dateToView) return ''
	return new Date(dateHelper.changeFormatToInput(unloadPoints[0].dateToView)).getTime()
}

function getRouteInfo(order) {
	const routes = order.routes
	
	if (!routes || !routes.length) {
		return ''
	}

	return routes
		.sort((a,b) => b.idRoute - a.idRoute)
		.map((route, i) => `${i + 1}) Маршрут ${route.routeDirection}`)
		.join(' ************** ')
}

function getRoutePrice(order) {
	const routes = order.routes
	if (!routes || !routes.length) return ''

	const successRoutes = routes
		.sort((a, b) => b.idRoute - a.idRoute)
		.filter(route => route.statusRoute === '4' || route.statusRoute === '6' || route.statusRoute === '8')

	if (!successRoutes.length) return ''

	return successRoutes[0].finishPrice + ' ' + successRoutes[0].startCurrency
}

function getWayToView(order) {
	const way = order.way ? order.way : ''
	const isInternalMovement = order.isInternalMovement === 'true'
	return isInternalMovement ? 'Внутреннее перемещение' : way
}

// проверка заказов на объединение
export function checkCombineOrders(orders) {
	const isExistOrders = orders.every(Boolean)
	if (!isExistOrders) {
		return 'Один или несколько заказов не найдены, проверьте данные!'
	}

	const firstOrder = orders[0]

	const isVerifyOrderWay = orders.every(order =>
		order.way === firstOrder.way
		&& order.isInternalMovement === firstOrder.isInternalMovement
	)
	if (!isVerifyOrderWay) {
		return 'Нельзя объединить заявки с разным типом маршрута!'
	}

	const isValidStatuses = orders.every(order => order.status === firstOrder.status)
	if (!isValidStatuses) {
		return 'Объединять можно только те заказы, у которых одинаковый статус!'
	}

	const isExistLink = orders.some(order => order.link)
	if (isExistLink) {
		return 'Один или несколько заказов уже имеют связь!'
	}

	if (firstOrder.status === 6 || firstOrder.status === 20) {
		const isIdenticalTrucks = orders.every(order => {
			return firstOrder.typeLoad === order.typeLoad
				&& firstOrder.typeTruck === order.typeTruck
				&& firstOrder.methodLoad === order.methodLoad
				&& firstOrder.incoterms === order.incoterms
		})
		if (!isIdenticalTrucks) {
			return 'Требования к машине в заказах не должны отличаться!'
		}
	}

	return ''
}

// проверка нескольких заказов перед созданием маршрута
export function checkCombineRoutes(orderData) {
	const firstOrder = orderData[0]

	const isVerifyOrderWay = orderData.every(order =>
		order.way === firstOrder.way
		&& order.isInternalMovement === firstOrder.isInternalMovement
	)
	if (!isVerifyOrderWay) {
		return 'Нельзя объединить заявки с разным типом маршрута!'
	}

	const isVerifyOrdersLink = orderData.every(order => firstOrder.link === order.link)
	if (!isVerifyOrdersLink) {
		return 'Невозможно создать маршрут - связи в заказах отличаются!'
	}

	const isIdenticalTrucks = orderData.every(order =>
		firstOrder.typeLoad === order.typeLoad
		&& firstOrder.typeTruck === order.typeTruck
		&& firstOrder.methodLoad === order.methodLoad
		&& firstOrder.incoterms === order.incoterms
	)
	if (!isIdenticalTrucks) {
		return 'Нельзя объединить заявки с разными требованиями к машине!'
	}
	return ''
}

// объединение точек маршрута
export function mergeRoutePoints(points) {
	const mergedPoints = {}

	points.forEach(point => {
		const key = `${point.bodyAddress}_${point.type}` // Ключ для группировки

		if (!mergedPoints[key]) {
			// Создаем новую точку в объекте, если такой еще нет
			mergedPoints[key] = { ...point }
			mergedPoints[key].pall = point.pall ? Number(point.pall) : null
			mergedPoints[key].weight = point.weight ? Number(point.weight) : null
			mergedPoints[key].volume = point.volume ? Number(point.volume) : null
		} else {
			// Обновляем существующую точку
			const existingPoint = mergedPoints[key]
			existingPoint.pall = (existingPoint.pall !== null || point.pall) 
				? (Number(existingPoint.pall || 0) + Number(point.pall || 0)) 
				: null
			existingPoint.weight = (existingPoint.weight !== null || point.weight) 
				? (Number(existingPoint.weight || 0) + Number(point.weight || 0)) 
				: null
			existingPoint.volume = (existingPoint.volume !== null || point.volume) 
				? (Number(existingPoint.volume || 0) + Number(point.volume || 0)) 
				: null
		}
	})

	// Преобразуем объект обратно в массив
	return Object.values(mergedPoints)
		.map(point => {
			// Если все значения равны 0 или отсутствуют, делаем null
			point.pall = point.pall === 0 ? null : point.pall
			point.weight = point.weight === 0 ? null : point.weight
			point.volume = point.volume === 0 ? null : point.volume
			return point
		})
		.sort((a, b) => a.idAddress - b.idAddress)
}
