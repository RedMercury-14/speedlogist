import { getRoutesHasOrderBaseUrl } from "./globalConstants/urls.js"
import { dateHelper, getData } from "./utils.js"

const routeStatusText = {
	0:'Тендер ожидает подтверждения на бирже',
	1:'Тендер на бирже, идут торги',
	2:'Тендер завершен, нет машины',
	3:'Тендер завершен, нет водителя',
	4:'Тендер завершен, машина и водитель приняты',
	5:'Тендер отменен',
	6:'Перевозка закончена',
	7:'Маршрут закрыт',
	8:'Маршрут условно подтвержден (один перевозчик)',
	9:'Маршрут удален',
	10:'Маршрут в архиве',
	200:'Ожидает назначения перевозчика',
	210:'Перевозчик назначен',
	220:'Указан пробег',
	225:'Указан пробег и стоимость перевозки',
	230:'Завершен',
}

const routeStatusColor = {
	0:'#ffffb2',
	1:'#ddfadd',
	2:'#b2d9b2',
	3:'#b2d9b2',
	4:'#b2d9b2',
	5:'#ffb2b2',
	6:'#b2e7ff',
	7:'#b2e7ff',
	8:'#ddfadd',
	9:'#d9d9d9',
	10:'#ddfadd',
	200:'#494f5252',
	210:'#ffffff',
	220:'#dce37266',
	225:'#c4ffe1db',
	230:'#9ee9ffdb',
}

document.addEventListener('DOMContentLoaded', async () => {
	const routesContainer = document.querySelector(".routes-container")
	const idOrder = getIdOrder()
	const routes = await getData(getRoutesHasOrderBaseUrl+idOrder)
	createRouteListElement(routes, routesContainer)
})

function getIdOrder() {
	const params = new Proxy(
		new URLSearchParams(window.location.search),
		{ get: (searchParams, prop) => searchParams.get(prop), }
	)

	return params.idOrder
}

function createRouteListElement(routes, container) {
	if (!Array.isArray(routes) || routes.length === 0) return

	routes.forEach(route => {
		container.append(getRouteElement(route))
	})
}

function getRouteElement(route) {
	const element = document.createElement("span")
	const date = dateHelper.getFormatDate(route.createDate)
	const timeToView = route.createTime ? `, ${route.createTime.slice(0, 5)}` : ''
	element.className = "col-form-label font-weight-bold text-muted px-3"
	element.style.backgroundColor = routeStatusColor[route.statusRoute]
	element.innerText = `Маршрут ${route.routeDirection} от ${date}${timeToView}  — ${routeStatusText[route.statusRoute]}`
	return element
}
