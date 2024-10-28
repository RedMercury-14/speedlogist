// -------------------------------------------------------------------------------//
// ---------- Утилиты и настройки для библиотеки рисования leafletDraw -----------//
// -------------------------------------------------------------------------------//

import { getDecodedString, getEncodedString, isAdmin, isTopManager } from "../utils.js"
import { mapStore } from "./mapStore.js"
import { adaptPolygonToStore, getSelectedShopsPallSum } from "./mapUtils.js"

const POLYGON_ACTIONS_DICTIONARY = {
	trafficRestrictions: {
		text: "Ограничить движение",
		color: "gold",
	},
	trafficBan: {
		text: "Запретить движение",
		color: "red",
	},
	trafficSpecialBan: {
		text: "Запретить движение для загруженных машин",
		color: "purple",
	},
	crossDocking: {
		text: "Зона кросс-докинга",
		color: "#8a06ae",
	},
	calcPallSum: {
		text: "Зона для расчета количества паллет",
		color: "#17a2b8",
	},
}

export const drawControl = new L.Control.Draw({
	position: "topright",

	// // кнопки редактирования и удаления полигонов
	// edit: {
	// 	featureGroup: drawnItems,
	// 	poly: {
	// 		allowIntersection: false,
	// 	},
	// },


	// настройки фигур для рисования
	draw: {
		circle: false,
		polyline: false,
		rectangle: {
			allowIntersection: false,
			showArea: true,
			shapeOptions: {
				color: POLYGON_ACTIONS_DICTIONARY['calcPallSum']?.color,
				weight: 2,
			}
		},
		marker: false,
		circlemarker: false,
		polygon: {
			allowIntersection: false,
			showArea: true,
		},
	},
})

// глобальный объект с нарисованными на карте объектами
export const drawnItems = L.featureGroup({
	draw: {
		polyline: false,
	},
})

// глобальная переменная для передачи текущего ивента рисования
export let currentDrawEvent = null

let pallSumPolygonCounter = 0

// обработчики ивентов рисования
export const leafletDrawLayerEventHandlers = {
	// обработчик ивента при создании полигона
	onDrawLayerHandler(event) {
		currentDrawEvent = event

		if (event.layerType === 'rectangle') {
			// создание полигона для расчета количества паллет
			createPallSumPolygon()
			return
		}

		openPoligonControlModal()
	},

	// обработчик ивента при завершении режима редактирования
	onEditedLayersHandler(e) {
		const editedPolygons = []
		const layers = e.layers

		layers.eachLayer(layer => {
			const polygon = layer.toGeoJSON()
			editedPolygons.push(polygon)
		})

		console.log(editedPolygons)

		if (editedPolygons.length === 0) return
		// отправляем массив полигонов на сервер
	},

	// обработчик ивента при завершении режима удаления
	onDeletedLayersHandler(e) {
		const deletedPolygons = []
		const layers = e.layers

		layers.eachLayer(layer => {
			const polygon = layer.toGeoJSON()
			deletedPolygons.push(polygon)
		})

		console.log(deletedPolygons)

		if (deletedPolygons.length === 0) return
		// отправляем массив полигонов на сервер
	},
}

// создание полигона для расчета количества паллет
function createPallSumPolygon() {
	const name = `Расчет паллет-${++pallSumPolygonCounter}`
	const layer = getNewPolygonLayer(
		name,
		'calcPallSum',
		null,
		() => {
			const encodedName = getEncodedString(name)
			const layer = getLayerByEncodedName(encodedName)
			layer && drawnItems.removeLayer(layer)
			mapStore.removePolygon(encodedName)
		}
	)
	const polygon = layer.toGeoJSON()
	const adaptedPolygon = adaptPolygonToStore(polygon)
	mapStore.addPolygon(adaptedPolygon)
	drawnItems.addLayer(layer)
}

// создание полигона
export function getNewPolygonLayer(name, action, crossDockingPoint, onDeleteCallback) {
	// получаем событие карты из глобальной переменной событий
	const event = currentDrawEvent
	// получаем закодированное имя
	const encodedName = getEncodedString(name)

	let layer = event.layer
	let feature = (layer.feature = layer.feature || {})
	let type = event.layerType

	feature.type = feature.type || "Feature"
	let props = (feature.properties = feature.properties || {})

	// устанавливаем стили полигона
	layer.setStyle({
		color: POLYGON_ACTIONS_DICTIONARY[action]?.color,
		weight: 2,
	})

	// добавляем в поле props тип, закодированое имя и действие полигона
	props.type = type
	props.name = encodedName
	props.decodedName = name
	props.action = action
	props.crossDockingPoint = crossDockingPoint

	if (type === "circle") {
		props.radius = layer.getRadius()
	}

	// добавляем попап к полигону
	const popUp = getPopUpByPolygon(props, layer, onDeleteCallback)
	layer.bindPopup(popUp)

	return layer
}

// создание поп-апа для полигона
function getPopUpByPolygon(props, layer, onDeleteCallback) {
	const actionToView = POLYGON_ACTIONS_DICTIONARY[props.action]?.text
	const popup = document.createElement('div')
	let poputHTML = `
		<span class="font-weight-bold">Название:</span>
		<br>${props.decodedName}<br>
		<span class="font-weight-bold">Действие:</span>
		<br>${actionToView}<br>
	`

	if (props.action === 'crossDocking' && props.crossDockingPoint) {
		poputHTML += `<span class="font-weight-bold">Место кросс-докинга:</span><br>№${props.crossDockingPoint}<br>`
	}

	if (props.action === 'calcPallSum') {
		const shopsToView = mapStore.getShopsToView()
		const pallSum = getSelectedShopsPallSum(layer, shopsToView).toFixed(2)
		poputHTML += `
			<span class="font-weight-bold">Общее количество паллет: ${pallSum ? pallSum : 'Нет магазинов'}</span><br>
			<button class="calcPallSumBtn mt-1 btn btn-info btn-sm btn-block">Пересчитать сумму паллет</button>
		`
	}

	const role = document.querySelector('#role').value
	if (isAdmin(role) || isTopManager(role)) {
		poputHTML += `<button class="deletePolygonBtn mt-1 btn btn-secondary btn-sm btn-block">Удалить полигон</button>`
	}

	popup.innerHTML = poputHTML

	const deletePolygonBtn = popup.querySelector('.deletePolygonBtn')
	deletePolygonBtn && deletePolygonBtn.addEventListener('click', () => onDeleteCallback(props))

	const calcPallSumBtn = popup.querySelector('.calcPallSumBtn')
	calcPallSumBtn && calcPallSumBtn.addEventListener('click', () => {
		const shopsToView = mapStore.getShopsToView()
		const pallSum = getSelectedShopsPallSum(layer, shopsToView).toFixed(2)
		if (pallSum) alert(`Общее количество паллет: ${pallSum}`)
		else alert('Нет магазинов для расчета либо число паллет неизвестно')
	})
	
	return popup
}

// функции контроля видимости кнопок управления полигонами
export function showPoligonControl() {
	// document.querySelector('.leaflet-action-button').classList.remove('none')
	document.querySelector('.leaflet-draw').classList.remove('none')
}
export function hidePoligonControl() {
	// document.querySelector('.leaflet-action-button').classList.add('none')
	document.querySelector('.leaflet-draw').classList.add('none')
}

// функции управления модальным окном
export function openPoligonControlModal() {
	$('#poligonControlModal').modal('show')
}
export function closePoligonControlModal() {
	$('#poligonControlModal').modal('hide')
}

// получение модифицированного объекта полигона для добавления на карту
export function getModifiedGeojson(geojson, onDeleteCallback) {
	const feature = L.geoJSON(geojson, {
		style: function (feature) {
			const action = feature.properties.action
			const color = POLYGON_ACTIONS_DICTIONARY[action]?.color || "grey"

			return {
				color: color,
				weight: 2,
			}
		},
		pointToLayer: (feature, latlng) => {
			if (feature.properties.type === "circle") {
				return new L.circle(latlng, {
					radius: feature.properties.radius,
					
				})
			} else if (feature.properties.type === "circlemarker") {
				return new L.circleMarker(latlng, {
					radius: 10,
				})
			} else {
				return new L.Marker(latlng)
			}
		},
		onEachFeature: function (feature, layer) {
			drawnItems.addLayer(layer)
			const decodedName = getDecodedString(feature.properties.name)
			const props = { ...feature.properties, decodedName }
			const popup = getPopUpByPolygon(props, layer, onDeleteCallback)
			layer.bindPopup(popup)
		},
	})
	
	return feature
}

// получение полигона по закодированному имени
export function getLayerByEncodedName(encodedName) {
	const layers = drawnItems.getLayers()
	return layers.find(layer => layer.feature.properties.name === encodedName)
}