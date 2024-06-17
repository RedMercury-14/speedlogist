// -------------------------------------------------------------------------------//
// ------------- функции для работы с таблицами agGrid для карты -----------------//
// -------------------------------------------------------------------------------//

import { randomColor } from "../utils.js"
import { mapStore } from "./mapStore.js"

// создание и отрисовка таблицы
export function renderTable(gridDiv, gridOptions, data) {
	const table = new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	gridOptions.api.setRowData(data)
	gridOptions.api.hideOverlay()

	return table
}

// обновление таблицы
export function updateTable(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	gridOptions.api.setRowData(data)
	gridOptions.api.hideOverlay()
}

// форматирование данных для таблицы контроля расстояний
export function getFormatDataForDistanceControlTable(routesResponse) {
	const data = Object.entries(routesResponse)
	const routeArray = getGroupedByIdRouteArray(data)
	const routes = getRoutesWithRouteDistances(routeArray)
	const dataToView = convertRoutesToPoints(routes)
	return dataToView
}

// форматирование данных для таблицы оптимизации маршрутов
export function getFormatDataToOptimizeRouteTable(routesResponse) {
	const routes = routesResponse.whiteWay.map(el => {
		const routeId = el.id
		const route = routesResponse.mapResponses[routeId]
		const fullDistance = route.reduce((acc, route) => {
			acc = acc + route.distance
			return acc
		}, 0)
		const color = randomColor()
		const targetWeigth = el.vehicle.targetWeigth

		const points = route.reduce((acc, route, i) => {
			if (i === 0) {
				const startPoint = {
					...route,
					numshop: route.startShop.numshop,
					address: route.startShop.address,
					endShop: { ...route.startShop },
					needPall: route.startShop.needPall,
					distanceToView: 0
				}
				acc.push(startPoint)
			}

			const point = {
					...route,
					numshop: route.endShop.numshop,
					address: route.endShop.address,
					needPall: route.endShop.needPall,
					distanceToView: Math.round(route.distance / 100) / 10
			}
			acc.push(point)
			return acc
		}, [])
	
		return {
			...el,
			color,
			route,
			points,
			fullDistance: Math.round(fullDistance / 1000),
			targetWeigth,
		}
	})

	if (routesResponse.emptyShop.length !== 0) {
		const emptyShopRow = {
			id: 'Незавершенные',
			points : routesResponse.emptyShop,
			overrun: null,
			fullDistance: null,
			status: null,
			problemShop: null,
			color: null,
			vehicle: null,
			targetWeigth: null,
		}
		routes.push(emptyShopRow)
	}

	return routes
}

// группировка маршрутов по routeId
function getGroupedByIdRouteArray(data) {
	return data
		.sort((a, b) => (a[0] - b[0]))
		.reduce((acc, el) => {

		const routeId = el[0]

		if (el[1]) {
			const routes = el[1].map(route => ({...route, routeId }))
			acc.push(routes)
		}

		return acc
	}, [])
}
// рассчёт общего расстояния для каждой группы маршрутов
function getRoutesWithRouteDistances(routes) {
	return routes.reduce((acc, el) => {
		const fullDistance = el.reduce((acc, route) => {
			acc = acc + route.distance
			return acc
		}, 0)

		acc.push(...el)
		acc.push({ fullDistance: Math.round(fullDistance / 1000) })
		return acc
	}, [])
}
// форматирование данных маршрутов для отображения в таблице
function convertRoutesToPoints(routes) {
	return routes.reduce((acc, route, i, arr) => {
		if (i === 0 || (i > 1 && arr[i-1].hasOwnProperty('fullDistance'))) {
			const point = {
				...route,
				shopNum: route.startShop.numshop,
				address: route.startShop.address,
				pall: 0,
				distanceToView: 0
			}

			acc.push(point)
		}

		const point = route.hasOwnProperty('fullDistance')
			? { distanceToView: route.fullDistance }
			: {
				...route,
				shopNum: route.endShop.numshop,
				address: route.endShop.address,
				pall: 0,
				distanceToView: Math.round(route.distance / 100) / 10
			}

		acc.push(point)
		return acc

	}, [])
}

// выделенные строки таблицы
export function getSelectedRows(params) {
	const selectedRows = []

	const selectedCells = params.api.getCellRanges()[0]

	if (!selectedCells) return

	const startRowIndex = selectedCells.startRow.rowIndex
	const endRowIndex = selectedCells.endRow.rowIndex

	const startIndex = startRowIndex < endRowIndex ? startRowIndex : endRowIndex
	const endIndex = startRowIndex > endRowIndex ? startRowIndex : endRowIndex

	for (let i = startIndex; i <= endIndex; i++) {
		const rowNode = params.api.getRowNode(i)
		selectedRows.push(rowNode)
	}
	return selectedRows
}

export class inputColorRenderer {
	init(params) {
		this.params = params
		this.value = params.value
		this.routeId = params.data.id

		if (this.value) {
			this.eGui = document.createElement("input")
			this.eGui.className = "form-control p-0"
			this.eGui.type = "color"
			this.eGui.value = this.value
			this.changeColorHandler = this.changeColorHandler.bind(this)
			this.eGui.addEventListener("change", this.changeColorHandler)
		} else {
			this.eGui = document.createElement("span")
		}
	}

	getGui() {
		return this.eGui
	}

	changeColorHandler(event) {
		const value = event.target.value
		const markers = mapStore.getMarkers()
		const routes = mapStore.getPolylines()
		const targetMarkers = markers.filter(marker => marker.options.routeId === this.routeId)
		const targetRoutes = routes.filter(route => route.options.routeId === this.routeId)
	
		this.params.node.setDataValue("color", value)

		if (targetMarkers.length === 0) return
		if (targetRoutes.length === 0) return

		targetMarkers.forEach(marker => this.changeMarkerColor(marker, value))
		targetRoutes.forEach(routeLayer => this.changeRouteColor(routeLayer, value))
	}

	changeMarkerColor(marker, color) {
		const icon = marker._icon
		if (!icon) return
		const svg = icon.querySelector("svg")
		svg.style.fill = color
	}

	changeRouteColor(routeLayer, color) {
		routeLayer.setStyle({ color })
	}

	destroy() {
		this.eGui.removeEventListener("click", this.changeColorHandler)
	}
}
