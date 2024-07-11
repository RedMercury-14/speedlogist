// -------------------------------------------------------------------------------//
// ---------- Утилиты и настройки для библиотеки рисования leafletDraw -----------//
// -------------------------------------------------------------------------------//

import { ajaxUtils } from "../ajaxUtils.js"
import { snackbar } from "../snackbar/snackbar.js"
import { getDecodedString, getEncodedString, isAdmin, isTopManager } from "../utils.js"

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
		polyline: false,
		rectangle: false,
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

// обработчики ивентов рисования
export const leafletDrawLayerEventHandlers = {
	// обработчик ивента при создании полигона
	onDrawLayerHandler(event) {
		currentDrawEvent = event
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

// создание полигона
export function getNewPolygonLayer(name, encodedName, action, deletePolygonBaseUrl) {
	// получаем событие карты из глобальной переменной событий
	const event = currentDrawEvent

	let layer = event.layer
	let feature = (layer.feature = layer.feature || {})
	let type = event.layerType

	feature.type = feature.type || "Feature"
	let props = (feature.properties = feature.properties || {})

	// добавляем в поле props тип, закодированое имя и действие полигона
	props.type = type
	props.name = encodedName
	props.action = action

	if (type === "circle") {
		props.radius = layer.getRadius()
	}

	// добавляем попап к полигону
	const popUp = getPopUpByPolygon(name, encodedName, action, deletePolygonBaseUrl)
	layer.bindPopup(popUp)

	return layer
}

// создание поп-апа для полигона
function getPopUpByPolygon(name, encodedName, action, deletePolygonBaseUrl) {
	const actionToView = POLYGON_ACTIONS_DICTIONARY[action]?.text
	const popup = document.createElement('div')
	let poputHTML = `
		<span class="font-weight-bold">Название:</span>
		<br>${name}<br>
		<span class="font-weight-bold">Действие:</span>
		<br>${actionToView}
	`

	const role = document.querySelector('#role').value
	if (isAdmin(role) || isTopManager(role)) {
		poputHTML += `<button class="deletePolygonBtn mt-1 btn btn-secondary btn-sm btn-block">Удалить полигон</button>`
	}

	popup.innerHTML = poputHTML
	const deletePolygonBtn = popup.querySelector('.deletePolygonBtn')
	deletePolygonBtn && deletePolygonBtn.addEventListener('click', () => deletePolygon(name, encodedName, deletePolygonBaseUrl))
	
	return popup
}

// функции контроля видимости кнопок управления полигонами
export function showPoligonControl() {
	document.querySelector('.leaflet-action-button').classList.remove('none')
	document.querySelector('.leaflet-draw').classList.remove('none')
}
export function hidePoligonControl() {
	document.querySelector('.leaflet-action-button').classList.add('none')
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
export function getModifiedGeojson(geojson, deletePolygonBaseUrl) {
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
			const encodedName = feature.properties.name
			const name = getDecodedString(encodedName)
			const popup = getPopUpByPolygon(name, encodedName, feature.properties.action, deletePolygonBaseUrl)
			layer.bindPopup(popup)
		},
	})
	
	return feature
}

// удаление полигона с сервера
function deletePolygon(name, encodedName, baseUrl) {
	const isConfirmDelete = confirm(`Вы действительно хотите удалить полигон ${name}?`)

	if (!isConfirmDelete) return

	ajaxUtils.get({
		url : baseUrl + encodedName,
		successCallback: () => {
			snackbar.show(`Полигон с именем ${name} удалён`)
			const layer = getLayerByEncodedName(encodedName)
			layer && drawnItems.removeLayer(layer)
		}
	})
}

// проверка наличия имени полигона на сервере
export function polygonNameInputHandler(e, baseUrl) {
	const input = e.target
	const encodedName = getEncodedString(input.value)

	if (!encodedName) return
	
	console.dir(input.validity.valid)
	ajaxUtils.get({
		url : baseUrl + encodedName,
		successCallback: (hasName) => {
			if (hasName) {
				$('#messagePalygonName').text('Полигон с таким именем уже существует')
				input.classList.add('is-invalid')
			} else {
				$('#messagePalygonName').text('')
				input.classList.remove('is-invalid')
			}
		},
	})
}

// получение полигона по закодированному имени
function getLayerByEncodedName(encodedName) {
	const layers = drawnItems.getLayers()
	return layers.find(layer => layer.feature.properties.name === encodedName)
}