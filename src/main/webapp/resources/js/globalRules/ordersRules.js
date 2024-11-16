
// список условий поставки, для которых нужно предлагать оформить страхование
export const INCOTERMS_INSURANCE_LIST = [
	'FAS – Free Alongside Ship',
	'FOB – Free on Board',
	'CFR – Cost and Freight',
	'EXW – Ex Works',
	'FCA – Free Carrier',
	'CPT – Carriage Paid To',
]


// список номеров складов, заказы на которые будут отображаться в слотах
export const slotStocks = [
	'1700',
	'1200',
	'1230',
	'1214',
	// '1240',
	'1250',
	'1100'
]


// метод получения адреса склада по номеру
export function getStockAddress(stockNumber) {
	switch (stockNumber) {
		case '1700': return 'Склад 1700, 223065, Минская обл., Минский р-н, Луговослободской с/с, РАД М4, 18км. 2а, склад W05'
		case '1200': return 'Склад 1200, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		case '1230': return 'Склад 1230, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		case '1214': return 'Склад 1214, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		// case '1240': return 'Склад 1250, 223050, Минская обл., Минский р-н, 9-ый км Московского шоссе'
		case '1250': return 'Склад 1250, 223050, Минская обл., Минский р-н, 9-ый км Московского шоссе'
		case '1100': return 'Склад 1100, 223039, Минская обл., Минский р-н, Хатежинский с/с, 1'
		default: return ''
	}
}


// получение статуса заявки в зависимости от склада доставки
export function getOrderStatusByStockDelivery(numStockDelivery) {
	switch (numStockDelivery) {
		case '1700':
		case '1200':
		case '1230':
		case '1214':
		// case '1240':
		case '1250':
		case '1100':
		case 1700:
		case 1200:
		case 1230:
		case 1214:
		// case 1240:
		case 1250:
		case 1100:
			return 6 // нуждается в слотах
		default:
			return 20 // не нуждается в слотах, можно создавать маршрут
	}
}


// получение id виртуального склада, на котором будут отображаться поставки складов
export function getVirtualStockId(stockId) {
	switch (stockId) {
		case '1700':
			return '1700'
		case '1200':
		case '1230':
		case '1214':
			return '1200'
		// case '1240':
		case '1250':
			return '1250'
		case '1100':
			return '1100'
	}
}
