import { TEST_ORDER_EXPORT, TEST_ORDER_IMPORT, TEST_ORDER_RB } from "./testOrderData.js"
import { dateHelper, getData, removeSingleQuotes } from "./utils.js"

// форматирование данных формы заявки
export function getOrderData(formData, orderDataFromMarket, orderStatus) {
	const data = Object.fromEntries(formData)
	const way = data.way
	const points = getPointsData(way)
	const contact = getCounterpartyContact(data)
	const control = data.control === 'Да'
	const stacking = data.stacking === 'Да'
	const isInternalMovement = data.isInternalMovement === 'true'
	const dateDelivery = getDateDelivery(points)
	const numStockDelivery = getNumStockDelivery(isInternalMovement, points, orderDataFromMarket)
	const status = isInternalMovement ? getOrderStatusByStockDelivery(numStockDelivery) : orderStatus
	const marketNumber = getMarketNumber(isInternalMovement, status, data)

	const recipient = data.recipient
	const tir = data.tir === 'Да'
	const routeComments = data.routeComments
	const deliveryLocation = data.deliveryLocation ? data.deliveryLocation : ''
	const truckLoadCapacity = data.truckLoadCapacity
	const truckVolume = data.truckVolume
	const phytosanitary = data.phytosanitary === 'Да'
	const veterinary = data.veterinary === 'Да'
	const dangerous = data.dangerous === 'Да'
	const dangerousUN = data.dangerousUN
	const dangerousClass = data.dangerousClass ? data.dangerousClass : ''
	const dangerousPackingGroup = data.dangerousPackingGroup ? data.dangerousPackingGroup : ''
	const dangerousRestrictionCodes = data.dangerousRestrictionCodes ? data.dangerousRestrictionCodes : ''

	return {
		contertparty: data.counterparty,
		contact,
		control,
		way: data.way,
		marketNumber,
		orderCount: data.orderCount,
		comment: data.comment,
		temperature: data.temperature,
		typeLoad: data.typeLoad ? data.typeLoad : '',
		methodLoad: data.methodLoad ? data.methodLoad : '',
		typeTruck: data.typeTruck ? data.typeTruck : '',
		incoterms: data.incoterms ? data.incoterms : '',
		stacking,
		cargo: data.cargo,
		dateDelivery: orderDataFromMarket ? dateHelper.getDateForInput(orderDataFromMarket.dateDelivery) : dateDelivery,
		points,
		needUnloadPoint: data.needUnloadPoint === 'true' ? 'true' : '',
		loadNumber: data.loadNumber,
		isInternalMovement: isInternalMovement ? 'true' : 'false',
		status,
		idOrder: orderDataFromMarket ? orderDataFromMarket.idOrder : null,
		numStockDelivery,
		recipient,
		tir,
		routeComments,
		truckLoadCapacity,
		truckVolume,
		phytosanitary,
		veterinary,
		dangerous,
		dangerousUN,
		dangerousClass,
		dangerousPackingGroup,
		deliveryLocation,
		dangerousRestrictionCodes,
	}
}

function getMarketNumber(isInternalMovement, status, data) {
	return isInternalMovement && status === 6
		? `${new Date().getTime()}`
		: data.marketNumber
}
function getNumStockDelivery(isInternalMovement, points, orderDataFromMarket) {
	return isInternalMovement && points.length && points[points.length - 1].date
		? points[points.length - 1].bodyAdress
			.split('; ')[1]
			.split('-')[0]
		: orderDataFromMarket
			? orderDataFromMarket.numStockDelivery : null
}
function getDateDelivery(points) {
	if (points.length === 0) return ''
	const lastPoint = points[points.length - 1]
	return lastPoint.date || ''
}
function getCounterpartyContact(data) {
	const { contact, fio, tel } = data
	if (contact) return contact
	if (fio && tel) return `${fio}, тел. ${tel}`
	if (fio) return `${fio}`
	if (tel) return `Тел. ${tel}`
	return ""
}


// получение данных из форм точек маршрута
export function getPointsData(way) {
	const points = []
	const pointForms = document.querySelectorAll('.pointForm')
	pointForms.forEach((form, i) => {
		const bodyAdress = getPointAddress(way, form)
		const timeFrame = getTimeFrame(way, form)
		const contact = getPointContact(way, form)
		const customsAddress = getCustomsAddress(way, form)
		points.push({
			pointNumber: i + 1,
			type: form.type.value,
			date: form.date.value,
			time: form.time.value,
			cargo: form.pointCargo ? removeSingleQuotes(form.pointCargo.value) : '',
			pall: form.pall.value,
			weight: form.weight.value,
			volume: form.volume.value,
			tnvd: form.tnvd ? form.tnvd.value : null,
			bodyAdress: removeSingleQuotes(bodyAdress),
			timeFrame,
			contact: removeSingleQuotes(contact),
			customsAddress: removeSingleQuotes(customsAddress),
		})
	})

	return points
}
function getPointAddress(orderWay, form) {
	const pointType = form.type.value
	// if (orderWay === 'Импорт' && pointType === 'Загрузка') {
	// 	let addressInfo = []
	// 	const separator = ', '
	// 	const country = form.country.value
	// 	const postIndex = form.postIndex.value
	// 	const region = form.region.value
	// 	const city = form.city.value
	// 	const street = form.street.value
	// 	const building = form.building.value
	// 	const buildingBody = form.buildingBody.value
	// 	addressInfo.push(postIndex, region, city, street, building, buildingBody)
	// 	const address = addressInfo.filter(item => item).join(separator).replace(/;/g, '.')
	// 	return `${country}; ${address}`
	// }
	const country = form.country.value
	const address = form.address.value.replace(/;/g, ',')
	return `${country}; ${address}`
}
function getTimeFrame(orderWay, form) {
	if (orderWay !== 'ррр') { // orderWay !== 'Импорт'
		const timeFrame_from = form.timeFrame_from.value
		const timeFrame_to = form.timeFrame_to.value
		return timeFrame_from && timeFrame_to
			? `С ${timeFrame_from} по ${timeFrame_to}`
			: timeFrame_from
				? `С ${timeFrame_from}`
				: timeFrame_to
					? `По ${timeFrame_to}` : ''
	}
	// для Импорта
	const WDTF_from = form.weekdaysTimeFrame_from.value
	const WDTF_to = form.weekdaysTimeFrame_to.value
	const satTF_from = form.saturdayTimeFrame_from.value
	const satTF_to = form.saturdayTimeFrame_to.value 
	const satTF_NotWork = form.saturdayTimeFrame_NotWork.checked
	const sunTF_from = form.sundayTimeFrame_from.value
	const sunTF_to = form.sundayTimeFrame_to.value 
	const sunTF_NotWork = form.sundayTimeFrame_NotWork.checked
	const WDTF = `Будние дни: с ${WDTF_from} по ${WDTF_to}`
	const satTF = satTF_NotWork
		? 'Суббота: не работают'
		: `Суббота: с ${satTF_from} по ${satTF_to}`
	const sunTF = sunTF_NotWork
		? 'Воскресенье: не работают'
		: `Воскресенье: с ${sunTF_from} по ${sunTF_to}`
	return `${WDTF}; ${satTF}; ${sunTF}`
}
function getPointContact(orderWay, form) { 
	const pointContactInput = form.pointContact
	const pointContact_fioInput = form.pointContact_fio
	const pointContact_telInput = form.pointContact_tel

	if (!pointContactInput &&
		!pointContact_fioInput &&
		pointContact_telInput) return ''

	if (pointContactInput) {
		return pointContactInput.value ? pointContactInput.value : ''
	}

	const pointContact_fio = form.pointContact_fio.value
	const pointContact_tel = form.pointContact_tel.value
	return pointContact_fio && pointContact_tel
		? `${pointContact_fio}, тел. ${pointContact_tel}`
		: pointContact_fio
			? `${pointContact_fio}`
			: pointContact_tel
				? `Тел. ${pointContact_tel}` : ''
}
function getCustomsAddress(orderWay, form) {
	if (orderWay === 'РБ') return ''
	if (orderWay === 'Экспорт') {
		const customsCountry = form.customsCountry.value
		const customsAddress = form.customsAddress.value
			? form.customsAddress.value.replace(/;/g, ',')
			: ''
		return customsCountry && customsAddress
			? `${customsCountry}; ${customsAddress}`
			: customsCountry
				? customsCountry
				: customsAddress
					? customsAddress : ''
	}
	const customsCountry = form.customsCountry.value
		const customsAddress = form.customsAddress.value
			? form.customsAddress.value.replace(/;/g, ',')
			: ''
		return customsCountry && customsAddress
			? `${customsCountry}; ${customsAddress}`
			: customsCountry
				? customsCountry
				: customsAddress
					? customsAddress : ''
	// // для Импорта
	// const customsInPointAddress = form.customsInPointAddress ? form.customsInPointAddress.value : ''
	// if (customsInPointAddress === 'Да') return 'Затаможка на месте загрузки'
	// if (!form.customsCountry) return ''
	// let addressInfo = []
	// const separator = ', '
	// const country = form.customsCountry.value
	// const postIndex = form.customsPostIndex.value
	// const region = form.customsRegion.value
	// const city = form.customsCity.value
	// const street = form.customsStreet.value
	// const building = form.customsBuilding.value
	// const buildingBody = form.customsBuildingBody.value
	// addressInfo.push(postIndex, region, city, street, building, buildingBody)
	// const address = addressInfo
	// 	.filter(item => item)
	// 	.join(separator)
	// 	.replace(/;/g, '.')
	// return `${country}; ${address}`
}

// получение статута заявки в зависимости от склада доставки
export function getOrderStatusByStockDelivery(numStockDelivery) {
	switch (numStockDelivery) {
		case '1700':
		case '1200':
		case '1230':
		case '1214':
		case '1250':
		case '1100':
		case 1700:
		case 1200:
		case 1230:
		case 1214:
		case 1250:
		case 1100:
			return 6
		default:
			return 20
	}
}

// получение заказа для копирования или редактирвания
export async function getOrderForForm(formType) {
	const urlParams = new URLSearchParams(window.location.search)
	const orderId = urlParams.get('idOrder')
	console.log("🚀 ~ window.onload= ~ orderId:", orderId)
	const getOrderByIdBaseUrl = `../../../api/procurement/getOrderById/`
	const res = await getData(`${getOrderByIdBaseUrl}${orderId}`)
	const order = res.body

	const sortedPoints = order.addresses.sort((a, b) => a.pointNumber - b.pointNumber)
	order.addresses = sortedPoints
	console.log("🚀 ~ getOrderForForm ~ oldOrder:", order)
	// удаляем значения даты и времени в точках заказадля формы копирования
	if (formType === 'copy') {
		order.addresses.forEach(address => {
			address.date = ''
			address.time = ''
		})
	}
	return order
}