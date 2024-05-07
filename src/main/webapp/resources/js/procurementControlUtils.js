import { cell } from "./AG-Grid/ag-grid-utils.js"
import { dateHelper } from "./utils.js"

export const procurementExcelExportParams = {
	getCustomContentBelowRow: (params) => getRows(params),
	columnWidth: 120,
	fileName: "orders.xlsx",
}

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

export function getRouteInfo(order) {
	const routes = order.routes
	
	if (!routes || !routes.length) {
		return ''
	}

	return routes
		.sort((a,b) => b.idRoute - a.idRoute)
		.map((route, i) => `${i + 1}) Маршрут ${route.routeDirection}`)
		.join(' ************** ')
}

export function getRoutePrice(order) {
	const routes = order.routes
	if (!routes || !routes.length) return ''

	const successRoutes = routes
		.sort((a, b) => b.idRoute - a.idRoute)
		.filter(route => route.statusRoute === '4' || route.statusRoute === '6' || route.statusRoute === '8')

	if (!successRoutes.length) return ''

	return successRoutes[0].finishPrice + ' ' + successRoutes[0].startCurrency
}
