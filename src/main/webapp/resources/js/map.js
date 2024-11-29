import { snackbar } from "./snackbar/snackbar.js"
import { debounce,
	getData,
	getEncodedString,
	hideLoadingSpinner,
	isAdmin, isLogistDelivery, isLogisticsDeliveryPage, isManager,
	isObserver,
	isTopManager,
	randomColor,
	showLoadingSpinner
} from './utils.js'
import { ajaxUtils } from "./ajaxUtils.js"
import { drawLocales } from "./map/leafletDrawLocales.js"
import { CanvasMarker } from "./map/canvasMarker.js"
import { mapIcons } from "./map/mapIcons.js"
import { clearJSONOnClickCallback, customControl, logJSONonClickCallback } from "./map/leafletDrawCustomControl.js"
import { getRouterParams, loadRouterParams, saveRouterParams, setRouterParams } from "./map/routerParamsUtils.js"
import { addOnClickToMenuItemListner, closeSidebar } from "./map/sidebarUtils.js"
import {
	closePoligonControlModal,
	currentDrawEvent,
	drawControl,
	drawnItems,
	getLayerByEncodedName,
	getModifiedGeojson,
	getNewPolygonLayer,
	hidePoligonControl,
	leafletDrawLayerEventHandlers,
	showPoligonControl,
} from "./map/leafletDrawUtils.js"
import { optimizerShopToggler, toogleAllShops } from "./map/shopMarkersUtils.js"
import {
	getFormatDataForDistanceControlTable,
	getFormatDataToOptimizeRouteTable,
	getSelectedRows,
	renderTable,
	updateTable
} from "./map/agGridUtils.js"
import { mapStore } from "./map/mapStore.js"
import { uiIcons } from "./uiIcons.js"
import {
	AllShopsToggler,
	createCarInputs,
	createCleaningInputsColumn,
	createNumbersColumn,
	createRouteInputsTable,
	createRouteTextareaTable,
	createPallReturnInputsColumn,
} from "./map/htmlBuilder.js"
import {
	getOptimizeRouteFormData,
	getPointsData,
	getPointsDataFromTextarea,
	getShopLoadsFormData,
	getTextareaData,
} from "./map/formDataUtils.js"
import optimizeRouteConfig from "./map/agGridOptimizeRouteConfig.js"
import distanceControlConfig from "./map/agGridDistanceControlConfig.js"
import {
	checkboxHTML,
	createFormInputs,
	createSelect,
	getOptimizeRouteParamsFormData,
	inputParams,
	mainCheckboxHTML,
	mainCheckboxParams,
	numericInputHTML,
	selectOptions,
	selectParams,
	setOptimizeRouteParamsFormData,
	сheckboxParams,
} from "./map/optimizeRouteParamsUtils.js"
import {
	adaptPolygonToStore,
	addAddressInfo,
	addCrossDocking,
	addCrossDockingPointOptions,
	addDistanceInfo,
	addRouteInfo,
	addSmallHeaderClass,
	updateTruckListsOptions,
	clearRouteTable,
	displayEmptyTruck,
	getMarkerToShop,
	setLocalCarsData,
	setOptimizeRouteFormData,
	truckAdapter,
	clearPoligonControlForm,
	setTrucksData,
	showMessageModal,
	polygonActionSelectChangeHandler
} from "./map/mapUtils.js"
import { getTruckLists, groupTrucksByDate } from "./logisticsDelivery/trucks/trucksUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { addListnersToPallTextarea, calcPallets } from "./map/calcPallets.js"
import { createGrid } from "./map/createGrid.js"

const apiUrl = isLogisticsDeliveryPage() ? '../../api/' : '../api/'

const optimizationUrlDict = {
	v3: `${apiUrl}map/myoptimization3`,
	v5: `${apiUrl}map/myoptimization5`,
}

const saveOptimizeRouteParamsUrl = `${apiUrl}map/set`

const getAllShopsUrl = `${apiUrl}manager/getAllShops`
const getAllPolygonsUrl = `${apiUrl}map/getAllPolygons`
const sendGeojsonDataUrl = `${apiUrl}map/savePolygon`
const deletePolygonBaseUrl = `${apiUrl}map/delPolygon/`
const checkNamePolygonBaseUrl = `${apiUrl}map/checkNamePolygon/`
const getRouterParamsUrl = `${apiUrl}map/getDefaultParameters`
const setRouterParamsUrl = `${apiUrl}map/setDefaultParameters`
const getRoutingListUrl = `${apiUrl}map/way/4`
const getServerMessageUrl = `${apiUrl}map/getStackTrace`
const sendExcelFileUrl = `${apiUrl}map/5`
const sendExcelFileWithReportUrl = `${apiUrl}map/6`

const getTrucksBaseUrl = `${apiUrl}logistics/deliveryShops/getTGTrucks`

const token = $("meta[name='_csrf']").attr("content")

const OPTIMIZE_ROUTE_DATA_KEY = "NEW_optimizeRouteData"
const OPTIMIZE_ROUTE_PARAMS_KEY = "NEW_optimizeRouteParams"

const role = document.querySelector("#role").value

// -----------------------------------------------------------------------------------//
// -----------------------------AG-Grid settings--------------------------------------//
// -----------------------------------------------------------------------------------//

let distanceControlTable
let optimizeRouteTable

const debouncedGoToShop = debounce(goToShop, 500)

// -------------------------------------------------------------------------------//
// --------------- Конфигурация таблицы контроля расстояний ----------------------//
// -------------------------------------------------------------------------------//

// настройки таблицы
const distanceControlGridOptions = {
	...distanceControlConfig,
	getContextMenuItems: getContextMenuItemsForDistanceControlTable,
}
// контекстное меню
function getContextMenuItemsForDistanceControlTable(params) {
	const selectedRows = getSelectedRows(params)

	if (!selectedRows) return

	const isEmptyRows = selectedRows.find(row => !row.data.points)
	const pointList = selectedRows.map(rowNode => rowNode.data)

	const result = [
		{
			name: `Показать маршрут на карте`,
			disabled: pointList.length < 2 || isEmptyRows,
			action: () => {
				displayRouteFromDistanceControlTable(pointList)
				addMarkersToMap(mapStore.getMarkers())
			},
			icon: uiIcons.route,
		},
		"separator",
		"export",
	];

	return result;
}
// функция отображения таблицы
function displayDistanceControlTable(gridDiv, data) {
	if (distanceControlTable) {
		updateTable(distanceControlGridOptions, data)
	} else {
		distanceControlTable = renderTable(gridDiv, distanceControlGridOptions, data)
		distanceControlTable && document.querySelector('.distanceControlUtils').classList.remove('hidden')
	}
}
// отрисовка маршрута на карте
function displayRouteFromDistanceControlTable(pointList) {
	const routeColor = randomColor()
	const fullDistanceToView = getFullDistanceFromPointList(pointList)

	pointList.forEach((route, i, arr) => {
		const pointCount = arr.length
		createLocationPoints(route, i, pointCount, true)

		if (i !== 0) {
			addRouteToMap(route.points, i-1, { distance: fullDistanceToView }, routeColor)
		}
	})
}

function getFullDistanceFromPointList(pointList) {
	const fullDistance = pointList.reduce((acc, route, i) => {
		if (i !== 0) {
			acc += route.distance
		}
		return acc
	}, 0)

	return `${Math.round(fullDistance / 1000)}`
}
// поиск магазина в таблице
function goToShop(e) {
	const searchValue = e.target.value

	const data = []
	const api = distanceControlTable.gridOptions.api

	api.forEachNode((node) => {
		if (node.data.shopNum) {
			data.push(node)
		}
	})

	const filtredData = data.filter(node => {
		return node.data.shopNum.toString().includes(searchValue)
	})

	if (filtredData.length) {
		const rowIndex = filtredData[0].rowIndex
		api.ensureIndexVisible(rowIndex, 'top')
	}
}


// -------------------------------------------------------------------------------//
// -------------- Конфигурация таблицы оптимизации маршрутов ---------------------//
// -------------------------------------------------------------------------------//

// настройки таблицы
const optimizeRouteGridOptions = {
	...optimizeRouteConfig,
	getContextMenuItems: getContextMenuItemsForOptimizeRouteTable,
	onRowClicked: onOptimizeRouteTableRowClicked,
}
// контекстное меню
function getContextMenuItemsForOptimizeRouteTable(params) {
	if (!params.node) return

	const polylines = mapStore.getPolylines()
	const route = params.node.data
	const selectedRows = getSelectedRows(params)
	const isEmptyShopRows = selectedRows && selectedRows.find(row => row.data.id === 'Незавершенные')

	const result = [
		{
			name: `Выделить маршрут на карте`,
			disabled: route.id === 'Незавершенные',
			action: () => {
				highlightRoute(route, polylines)
			},
			icon: uiIcons.highlighter,
		},
		{
			name: `Показать маршрут на карте`,
			disabled: route.id === 'Незавершенные',
			action: () => {
				displayRouteFromOptimizeRouteTable(route)
				addMarkersToMap(mapStore.getMarkers())
			},
			icon: uiIcons.route,
		},
		{
			name: `Показать выделенные маршруты`,
			disabled: !selectedRows || isEmptyShopRows,
			action: () => {
				selectedRows.forEach(row => {
					displayRouteFromOptimizeRouteTable(row.data)
					addMarkersToMap(mapStore.getMarkers())
				})
			},
			icon: uiIcons.routes,
		},
		{
			name: `Показать все маршруты`,
			action: () => {
				const rowsData = []
				params.api.forEachNode(node => node.data.id !== 'Незавершенные' && rowsData.push(node.data))
				rowsData.forEach(data => {
					displayRouteFromOptimizeRouteTable(data)
					addMarkersToMap(mapStore.getMarkers())
				})
			},
			icon: uiIcons.routes,
		},
		{
			name: `Очистить карту`,
			action: () => {
				removeLayersfromMap()
			},
			icon: uiIcons.eraser,
		},
		"separator",
		{
			name: `Экспорт в Excel`,
			action: () => {
				optimizeRouteGridOptions.api.exportDataAsExcel()
			},
			icon: uiIcons.excel,
		},
	]

	return result
}
// обработчик клика по строке таблицы
function onOptimizeRouteTableRowClicked(params) {
	const route = params.node.data
	if (route.id === 'Незавершенные') return
	removeLayersfromMap()
	displayRouteFromOptimizeRouteTable(route)
	addMarkersToMap(mapStore.getMarkers())
}
// функция отображения таблицы
function displayOptimizeRouteTable(gridDiv, data) {
	if (optimizeRouteTable) {
		updateTable(optimizeRouteGridOptions, data)
	} else {
		optimizeRouteTable = renderTable(gridDiv, optimizeRouteGridOptions, data)
	}
}
// отрисовка маршрута на карте
function displayRouteFromOptimizeRouteTable(generalRoute) {
	const pointList = generalRoute.points
	const color = generalRoute.color
	const generalRouteId = generalRoute.id
	const popupInfo = {
		id: generalRouteId,
		distance: generalRoute.fullDistance,
		targetPall: generalRoute.vehicle.targetPall,
		pall: generalRoute.vehicle.pall,
		vehicle: generalRoute.vehicle.name
	}

	pointList.forEach((route, i) => {
		createColoredPoint(route, i, color, generalRouteId)
		if (i !== 0) addRouteToMap(route.points, i-1, popupInfo, color)
	})
}
// выделение маршрута на карте среди отображенных маршрутов
function highlightRoute(generalRoute, polylines) {
	if (polylines.length === 0) return

	const generalRouteId = generalRoute.id

	polylines.forEach(polyline => {
		if (polyline.options.routeId === generalRouteId) {
			polyline.setStyle({ weight: 7, opacity: 1 })
			polyline.bringToFront()
		} else {
			polyline.setStyle({ weight: 5, opacity: 0.2 })
			polyline.bringToBack()
		}
	
	})
}
// создание маркеров магазинов выбранного цвета
function createColoredPoint(route, index, color, generalRouteId) {
	const shopCount = index + 1
	const shop = route.endShop
	const needPall = shop.needPall ? `(${shop.needPall})` : ''
	const hasRestriction = shop.length || shop.width || shop.height || shop.maxPall
	const restrictionLabel = hasRestriction ? '<span class="font-weight-bold text-danger">!</span>' : ''
	const markerText = `<span>№${shopCount} ${restrictionLabel}${shop.numshop} <span class="text-danger">${needPall}</span></span>`
	const icon = mapIcons.smallColoredIcon(markerText, color)
	const marker = getMarkerToShop(icon, shop, generalRouteId)
	mapStore.addMarker(marker)
}


// -------------------------------------------------------------------------------//
// -----------------------------Leaflet map--------------------------------------//
// -------------------------------------------------------------------------------//

// конфигурация какрты
const canvasRenderer = L.canvas({ padding: 0.5 })
const config = {
	renderer: canvasRenderer,
	center: [53.875, 27.415],
	zoom: 11,
	minZoom: 6,
	maxZoom: 18,
	zoomControl: false,
}

// маркер для встраивания в канвас
L.canvasMarker = (...options) => new CanvasMarker(...options)

// создание карты
const map = L.map("map", config)
L.tileLayer('http://{s}.tile.osm.org/{z}/{x}/{y}.png', {
	attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map)

// обработчик клика по карте
map.on('click', (e) => {
	const polylines = mapStore.getPolylines()

	// устанавливает для всех полилиний изначальные настройки прозрачности и ширины
	polylines.forEach(polyline => {
		polyline.setStyle({
			opacity: 0.8,
			weight: 5
		})
	})
})

// добавлениепереключателя отображения всех точек
map.addControl(new AllShopsToggler())
// добавление кнопок масштабирования в правый верхник край
L.control.zoom({ position: 'topright' }).addTo(map)


// -----------------------------Leaflet draw--------------------------------------//
let drawEvent = currentDrawEvent

// локаль для библиотеки рисования
L.drawLocal = drawLocales('ru')

// добавление кнопок кнопок контроля для рисования
// map.addControl(new customControl())
map.addControl(drawControl)
hidePoligonControl()

// добавление объекта с полигонами на карту
drawnItems.addTo(map)

// добавление обработчиков ивентов рисования
map.on(L.Draw.Event.CREATED, leafletDrawLayerEventHandlers.onDrawLayerHandler)
map.on(L.Draw.Event.EDITED, leafletDrawLayerEventHandlers.onEditedLayersHandler)
map.on(L.Draw.Event.DELETED, leafletDrawLayerEventHandlers.onDeletedLayersHandler)

// -------------------------------------------------------------------------------//
// -------------------------------------------------------------------------------//
// -------------------------------------------------------------------------------//

document.addEventListener('DOMContentLoaded', async () => {
	// изменение размера контейнера для контента
	addSmallHeaderClass()

	// кнопки боковой панели
	const menuItems = document.querySelectorAll(".menu-item")
	const buttonClose = document.querySelector(".close-button")
	menuItems.forEach((item) => addOnClickToMenuItemListner(item, optimizerPolygonsVisibleToggler))
	buttonClose.addEventListener("click", () => closeSidebar(optimizerPolygonsVisibleToggler))
	document.addEventListener("keydown", (e) => (e.key === "Escape") && closeSidebar(optimizerPolygonsVisibleToggler))

	// контейнеры для таблиц с информацией о точках маршрута
	const routeInputsContainer = document.querySelector('#routeInputsContainer')
	const routeAreaContainer = document.querySelector('#routeAreaContainer')
	routeInputsContainer && createRouteInputsTable(25, routeInputsContainer)
	routeAreaContainer && createRouteTextareaTable(25, routeAreaContainer)

	// контейнеры с нумерацией
	const optimizeRouteNumberContainer = document.querySelector('#optimizeRouteNumberContainer')
	optimizeRouteNumberContainer && createNumbersColumn(1000, optimizeRouteNumberContainer)
	
	// контейнер с инпутами для оказания чисток в форме оптимизатора
	const optimizeRouteCleaningInputsContainer = document.querySelector('#optimizeRouteCleaningInputsContainer')
	optimizeRouteCleaningInputsContainer && createCleaningInputsColumn(1000, optimizeRouteCleaningInputsContainer)

	// контейнер с инпутами для оказания чисток в форме оптимизатора
	const optimizeRoutePallReturnInputsContainer = document.querySelector('#optimizeRoutePallReturnInputsContainer')
	optimizeRoutePallReturnInputsContainer && createPallReturnInputsColumn(1000, optimizeRoutePallReturnInputsContainer)

	// AG-Grid-контейнер, инпут поиска по магазинам, кнопка очистки карты
	const distanceControlGridDiv = document.querySelector('#distanceControlGrid')
	const optimizeRouteGridDiv = document.querySelector('#optimizeRouteGrid')
	const goToShopInput = document.querySelector('#goToShop')
	const clearMapBtn = document.querySelector('#clearMap')
	goToShopInput && goToShopInput.addEventListener('input', debouncedGoToShop)
	clearMapBtn && clearMapBtn.addEventListener('click', (e) => removeLayersfromMap())

	// формы
	const routeForm = document.querySelector("#routeForm")
	const routeAreaForm = document.querySelector("#routeAreaForm")
	const distanceControlForm = document.querySelector("#distanceControlForm")
	const optimizeRouteForm = document.querySelector("#optimizeRouteForm")
	const routingParamsForm = document.querySelector("#routingParamsForm")
	const poligonControlForm = document.querySelector("#poligonControlForm")
	const optimizeRouteParamsForm = document.querySelector('#optimizeRouteParamsForm')
	routeForm && routeForm.addEventListener("submit", routeFormHandler)
	routeAreaForm && routeAreaForm.addEventListener("submit", routeAreaFormHandler)
	distanceControlForm && distanceControlForm.addEventListener("submit", (e) => distanceControlFormHandler(e, distanceControlGridDiv))
	optimizeRouteForm && optimizeRouteForm.addEventListener("submit", (e) => optimizeRouteFormHandler(e, optimizeRouteGridDiv))
	routingParamsForm && routingParamsForm.addEventListener("submit", (e) => routingParamsFormHandler(e, routeForm))
	poligonControlForm && poligonControlForm.addEventListener('submit', poligonControlFormSubmitHandler)
	optimizeRouteParamsForm && optimizeRouteParamsForm.addEventListener('submit', optimizeRouteParamsFormHandler)

	// кнопки управления параметрами маршрутизатора
	const saveRoutingParamsBtn = document.querySelector("#saveRoutingParams")
	const loadRoutingParamsBtn = document.querySelector("#loadRoutingParams")
	saveRoutingParamsBtn && saveRoutingParamsBtn.addEventListener('click', (e) => saveRouterParams(routingParamsForm, setRouterParamsUrl, token))
	loadRoutingParamsBtn && loadRoutingParamsBtn.addEventListener('click', async (e) => loadRouterParams(routingParamsForm, getRouterParamsUrl))

	// переключатель отображения на карте магазинов из поля оптимизатора
	const showOptimizerShopsBtn = document.querySelector("#showOptimizerShops")
	showOptimizerShopsBtn && showOptimizerShopsBtn.addEventListener('click', (e) => optimizerShopToggler(e, map))

	// проверка имени полигона
	const polygonNameInput = document.querySelector("#polygonName")
	polygonNameInput && polygonNameInput.addEventListener('change', (e) => polygonNameInputHandler(e, checkNamePolygonBaseUrl))

	// селект выбора действия для полигона
	const polygonActionSelect = document.querySelector("#polygonAction")
	polygonActionSelect && polygonActionSelect.addEventListener('change', (e) => polygonActionSelectChangeHandler(e.target.value))

	// обработка закрытия модального окна создания полигона
	$('#poligonControlModal').on('hidden.bs.modal', (e) => clearPoligonControlForm(poligonControlForm))

	// кастомные кнопки контроля для рисования
	// const logJSON = document.querySelector(".log")
	// const clearJSON = document.querySelector(".clear")
	// logJSON && logJSON.addEventListener("click", (e) => logJSONonClickCallback(drawnItems))
	// clearJSON && clearJSON.addEventListener("click", (e) => clearJSONOnClickCallback(drawnItems, map))

	// добавление инпутов с машинами в форме тестового оптимизатора
	const carInputsTable = document.querySelector('#carInputsTable')
	carInputsTable && createCarInputs(100, carInputsTable)

	// дата для списков в форме оптимизатора
	const currentDateInput = document.querySelector('#currentDate')
	currentDateInput && currentDateInput.addEventListener('change', (e) => changeCurrentDateHandler(e, carInputsTable))

	// выпадающий список со списками машин для оптимизатора
	const truckListsSelect = document.querySelector('#truckListsSelect')
	truckListsSelect && updateTruckListsOptions(mapStore.getListsByCurrentDate())
	truckListsSelect && truckListsSelect.addEventListener('change', (e) => truckListsChangeHandler(e, optimizeRouteForm))

	// кнопка для очищения списка с инпутами машин
	const clearCarInputsBtn = document.querySelector('#clearCarInputs')
	clearCarInputsBtn && clearCarInputsBtn.addEventListener('click', (e) => clearCarInputs(e, carInputsTable, truckListsSelect))

	// загрузка параметров маршрутизатора
	routingParamsForm && setRouterParams(routingParamsForm, getRouterParamsUrl)

	// автозаполнение формы оптимизации маршрутов данными из localstorage
	optimizeRouteForm && setOptimizeRouteFormData(optimizeRouteForm, OPTIMIZE_ROUTE_DATA_KEY)

	// добавление листнера для расчёта необходимости магазинов в паллетах
	addListnersToPallTextarea()

	// создание формы настроек оптимизатора
	createOptimizeRouteParamsForm(optimizeRouteParamsForm)

	// отображение стартовых данных
	if (window.initData) {
		await initStartData(currentDateInput)
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData(currentDateInput)
		})
	}

	bootstrap5overlay.hideOverlay()
})


// получение стартовых данных
async function initStartData(currentDateInput) {
	const shops = window.initData
	// полигоны
	const allPolygons = await getData(getAllPolygonsUrl)
	// получение машин для оптимизатора
	const response = await getData(getTrucksBaseUrl)
	const trucksData = response.status === '200'
		? response.body ? response.body : []
		: []
	const mappedTruckData = trucksData.map(truckAdapter)
	// машины по датам
	const groupedTrucks = groupTrucksByDate(mappedTruckData)
	// списки машин
	const lists = getTruckLists(mappedTruckData)

	mapStore.setShops(shops)
	mapStore.setTrucks(groupedTrucks)
	mapStore.setLists(lists)
	mapStore.setPolygons(allPolygons)

	// переключатель отображения на карте всех магазинов
	const allShopsToggler = document.querySelector("#allShopsToggler")
	allShopsToggler && allShopsToggler.addEventListener('click', (e) => toogleAllShops(e, map, shops))

	// селект выбора точки для кросс-докинга
	const crossDockingPointSelect = document.querySelector("#crossDockingPoint")
	addCrossDockingPointOptions(shops, crossDockingPointSelect)

	// установака данных даты для списков машин
	currentDateInput && (currentDateInput.value = mapStore.getCurrentDate())
	currentDateInput && (currentDateInput.min = mapStore.getCurrentDate())
	currentDateInput && (currentDateInput.max = mapStore.getMaxTrucksDate())

	showContentByRole(role)
}

// отображение контента по ролям
function showContentByRole(role) {
	// получаем все полигоны, кроме полигонов оптимизатора
	const filtredPolygons = mapStore
		.getPolygons()
		.filter(polygon => polygon.properties.action !== 'crossDocking'
						&& polygon.properties.action !== 'weightDistribution'
		)

	if (isAdmin(role) || isLogistDelivery(role) || isObserver(role)) {
		displayPolygons(filtredPolygons)
		showPoligonControl()
		displayShops()
		calcPallets()
	}
	if (isTopManager(role)) {
		displayPolygons(filtredPolygons)
		showPoligonControl()
		displayShops()
	}
	if (isManager(role)) {
		displayPolygons(filtredPolygons)
		displayShops()
	}
}

//добавление маркеров на карту
function addMarkersToMap(markers) {
	markers.forEach(marker => map.addLayer(marker))
}

// -------------------------------------------------------------------------------//
// --------------- создание элементов формы настроек оптимизатора ----------------//
// -------------------------------------------------------------------------------//
function createOptimizeRouteParamsForm(optimizeRouteParamsForm) {
	const optimizeRouteParamsMainCheckbox = document.querySelector('#optimizeRouteParamsMainCheckbox')
	const optimizeRouteParamsCheckboxes = document.querySelector('#optimizeRouteParamsCheckboxes')
	const optimizeRouteParamsSelect = document.querySelector('#optimizeRouteParamsSelect')
	const optimizeRouteParamsInputs = document.querySelector('#optimizeRouteParamsInputs')
	optimizeRouteParamsMainCheckbox && createFormInputs(mainCheckboxParams, mainCheckboxHTML, optimizeRouteParamsMainCheckbox)
	optimizeRouteParamsCheckboxes && createFormInputs(сheckboxParams, checkboxHTML, optimizeRouteParamsCheckboxes)
	optimizeRouteParamsInputs && createFormInputs(inputParams, numericInputHTML, optimizeRouteParamsInputs)
	optimizeRouteParamsSelect && createSelect(selectParams, selectOptions, optimizeRouteParamsSelect)

	// автозаполнение формы настрорек оптимизатора
	optimizeRouteParamsForm && setOptimizeRouteParamsFormData(optimizeRouteParamsForm, OPTIMIZE_ROUTE_PARAMS_KEY)
}

// -------------------------------------------------------------------------------//
// -------- отображение магазинов на карте при вводе в форме оптимизатора --------//
// -------------------------------------------------------------------------------//
async function displayShops() {
	const shops = mapStore.getShops()
	if (!shops || shops.length === 0) return
	const optimizeRouteShopNum = document.querySelector("#optimizeRouteShopNum")
	if (!optimizeRouteShopNum) return
	optimizeRouteShopNum.addEventListener('change', (e) => optimizeRouteShopNumChangeCallback(e, shops))
}

// обработчик изменения значений номеров магазинов в форме оптимизатора
function optimizeRouteShopNumChangeCallback(e, shops) {
	const shopNums = getTextareaData(e.target)
	const shopsToView = shops.filter(shop => shopNums.includes(`${shop.numshop}`))
	mapStore.setShopsToView(shopsToView)
	// showShops(shopsToView, map)
}

// -------------------------------------------------------------------------------//
// ----------------------- функция отображения полигонов -------------------------//
// -------------------------------------------------------------------------------//
async function displayPolygons(polygons) {
	if (!polygons || polygons.length === 0 ) return

	const modifyPolygons = polygons.map(polygon => {
		return {
			...polygon,
			geometry: {
				...polygon.geometry,
				coordinates: [
					polygon.geometry.coordinates
				]
			}
		}
	})

	const testGeoJSON = {
		type: "FeatureCollection",
		features: modifyPolygons
	}

	const modifiedGeoJSON = getModifiedGeojson(testGeoJSON, deletePolygon)
	modifiedGeoJSON.addTo(map)
}

// показать выбраные полигоны
function showPolygons(polygons) {
	let displayedPolygonNames = []
	drawnItems.eachLayer(layer => displayedPolygonNames.push(layer.feature.properties.name))
	const polygonsToDisplay = polygons.filter(polygon => !displayedPolygonNames.includes(polygon.properties.name))
	displayPolygons(polygonsToDisplay)
}

// скрыть выбранные полигоны
function hidePolygons(polygons) {
	const polygonNames = polygons.map(polygon => polygon.properties.name)
	drawnItems.eachLayer(layer => {
		if (polygonNames.includes(layer.feature.properties.name)) {
			drawnItems.removeLayer(layer)
		}
	})
}

// переключение отображения полигонов оптимизатора
function optimizerPolygonsVisibleToggler(sidebarMenuItem) {
	const polygonsForOptymizer = mapStore.getPolygonsForOptymizer()

	sidebarMenuItem.dataset.item === 'optimizeRoute'
	&& sidebarMenuItem.classList.contains("active-item")
		? showPolygons(polygonsForOptymizer)
		: hidePolygons(polygonsForOptymizer)
}

// -------------------------------------------------------------------------------//
// --------------------------- обработчики форм ----------------------------------//
// -------------------------------------------------------------------------------//

// функции обработки формы создания маршрута
function routeFormHandler(e) {
	e.preventDefault()

	removeLayersfromMap()
	const pointsData = getPointsData(e.target)
	buildRoute(pointsData)
}

// функции обработки формы создания маршрута по общему полю
function routeAreaFormHandler(e) {
	e.preventDefault()

	removeLayersfromMap()
	const pointsData = getPointsDataFromTextarea(e.target)
	buildRoute(pointsData)
}

// функции обработки формы создания маршрута с настройками
function routingParamsFormHandler(e, routeForm) {
	e.preventDefault()

	removeLayersfromMap()
	const routeParams = getRouterParams(e.target)
	const pointsData = getPointsData(routeForm, routeParams)
	buildRoute(pointsData)
}

// функции обработки формы EXCEL
function distanceControlFormHandler(e, gridDiv) {
	e.preventDefault()

	const submitButton = e.submitter
	const submitButtonText = submitButton.innerText
	const submitButtonId = submitButton.id
	const withReport = submitButtonId === 'withReport'
	const actionUrl = withReport ? sendExcelFileWithReportUrl : sendExcelFileUrl
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)
	removeLayersfromMap()
	distanceControlTable && distanceControlTable.gridOptions.api.showLoadingOverlay()

	ajaxUtils.postMultipartFformData({
		url: actionUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			if (res) {
				snackbar.show('Рассчет завершен!')
				const data = getFormatDataForDistanceControlTable(res)
				displayDistanceControlTable(gridDiv, data)
				withReport && document.querySelector('#downloadReportLink').click()
			} else {
				snackbar.show('Ошибка в файле Excel')
			}

			hideLoadingSpinner(submitButton, submitButtonText)
			getData(getServerMessageUrl).then(message => {
				setTimeout(() => {
					alert(message.comment)
				}, 500)
			})
		},
		errorCallback: () => hideLoadingSpinner(submitButton, submitButtonText)
	})
}

// обработчик отправки формы при создании полигона
function poligonControlFormSubmitHandler(e) {
	e.preventDefault()

	const form = e.target
	const formData = new FormData(form)
	const polygonData = Object.fromEntries(formData)
	const name = polygonData.polygonName
	const action = polygonData.polygonAction
	const crossDockingPoint = polygonData.crossDockingPoint ? Number(polygonData.crossDockingPoint) : null
	const layer = getNewPolygonLayer(name, action, crossDockingPoint, deletePolygon)
	const polygon = layer.toGeoJSON()

	if (polygon.properties.type === 'circle') {
		drawnItems.addLayer(layer)
		drawEvent = null
		closePoligonControlModal()
		snackbar.show('Полигон добавлен на карту в тестовом режиме')
		return
	}

	if (action === 'calcPallSum') {
		const adaptedPolygon = adaptPolygonToStore(polygon)
		mapStore.addPolygon(adaptedPolygon)
		drawnItems.addLayer(layer)
		drawEvent = null
		closePoligonControlModal()
		return
	}

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	ajaxUtils.postJSONdata({
		url: sendGeojsonDataUrl,
		token: token,
		data: polygon,
		successCallback: (res) => {
			const adaptedPolygon = adaptPolygonToStore(polygon)
			mapStore.addPolygon(adaptedPolygon)
			drawnItems.addLayer(layer)
			drawEvent = null
			closePoligonControlModal()
		}
	})
}

// удаление полигона с сервера
function deletePolygon(props) {
	const { decodedName, name, action } = props
	const isConfirmDelete = confirm(`Вы действительно хотите удалить полигон ${decodedName}?`)

	if (!isConfirmDelete) return

	if (action === 'calcPallSum') {
		const layer = getLayerByEncodedName(name)
		layer && drawnItems.removeLayer(layer)
		mapStore.removePolygon(name)
		return
	}

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	ajaxUtils.get({
		url : deletePolygonBaseUrl + name,
		successCallback: () => {
			snackbar.show(`Полигон с именем ${decodedName} удалён`)
			const layer = getLayerByEncodedName(name)
			layer && drawnItems.removeLayer(layer)
			mapStore.removePolygon(name)
		}
	})
}

// проверка наличия имени полигона на сервере
export function polygonNameInputHandler(e, baseUrl) {
	const input = e.target
	const encodedName = getEncodedString(input.value)

	if (!encodedName) return

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

// обработчик отправки формы тестового оптимизатора
function optimizeRouteFormHandler(e, gridDiv) {
	e.preventDefault()

	const submitButton = e.submitter
	const version = submitButton.dataset.version
	const submitButtonText = submitButton.innerText
	const optimizeRouteParams = JSON.parse(localStorage.getItem(OPTIMIZE_ROUTE_PARAMS_KEY))
	const data = getOptimizeRouteFormData(e.target, optimizeRouteParams)

	const alllShops = mapStore.getShops()
	const polygons = mapStore.getPolygons()
	const updatedData = addCrossDocking(data, alllShops, polygons)

	localStorage.setItem(OPTIMIZE_ROUTE_DATA_KEY, JSON.stringify(data))
	showLoadingSpinner(submitButton)

	ajaxUtils.postJSONdata({
		url: optimizationUrlDict[version],
		token: token,
		data: updatedData,
		successCallback: (res) => {
			console.log("🚀 ~ optimizeRouteFormHandler ~ res:", res)
			hideLoadingSpinner(submitButton, submitButtonText)

			if (res.status === '200') {
				const solution = res.solution
				if (!solution) return
				if (Object.keys(solution.mapResponses).length === 0) return
				document.querySelector('#displayDataInput').value = solution.stackTrace
				// $('#displayDataModal').modal('show')
				$('#collapseTwo').collapse('show')
				const data = getFormatDataToOptimizeRouteTable(solution)
				displayOptimizeRouteTable(gridDiv, data)
				displayEmptyTruck(solution.emptyTrucks)
				return
			}

			if (res.status === '105') {
				res.info && showMessageModal(res.info)
				return
			}

			if (res.status === '100') {
				res.info && snackbar.show(res.info)
				return
			}

			snackbar.show('Ошибка на сервере')
		},
		errorCallback: () => hideLoadingSpinner(submitButton, submitButtonText)
	})
}

// обработчик отправки формы настроек тестового оптимизатора
function optimizeRouteParamsFormHandler(e) {
	e.preventDefault()

	const data = getOptimizeRouteParamsFormData(e.target)
	console.log("🚀 Настройки оптимизатора: ", data)
	localStorage.setItem(OPTIMIZE_ROUTE_PARAMS_KEY, JSON.stringify(data))
	snackbar.show('Настройки оптимизатора сохранены')
}

// обработчик изменения даты в форме оптимизатора
function changeCurrentDateHandler(e, carInputsTable) {
	const date = e.target.value
	// сохраняем текущую дату
	mapStore.setCurrentDate(date)
	const truckLists = mapStore.getListsByCurrentDate()
	// очищаем список с инпутами машин
	createCarInputs(100, carInputsTable)
	// обновляем селект выбора списка авто
	updateTruckListsOptions(truckLists)
}

// обработчик выбора списка машин в оптимизаторе
function truckListsChangeHandler(e, optimizeRouteForm) {
	const nameList = e.target.value
	
	if (!nameList) {
		calcPallets()
		return
	}

	// ручное заполнение
	if (nameList === 'manual') {
		// данные из localStorage
		const optimizeRouteItem = localStorage.getItem(OPTIMIZE_ROUTE_DATA_KEY)
		const data = optimizeRouteItem ? JSON.parse(optimizeRouteItem) : []
		createCarInputs(100, carInputsTable)
		setLocalCarsData(data, optimizeRouteForm)
		calcPallets()
		return
	}

	const trucks = nameList === 'freeCars'
		? mapStore.getFreeTrucksByCurrentDate()
		: mapStore.getTrucksByNameList(nameList)

	if (!trucks || trucks.length === 0) {
		// обнуляем поля машин
		createCarInputs(100, carInputsTable)
		calcPallets()
		return
	}

	// создаем и заполняем поля машинами из выбранного списка
	createCarInputs(trucks.length, carInputsTable)
	setTrucksData(trucks, optimizeRouteForm)
	calcPallets()
}

// очистка полей машин в форме оптимизатора
function clearCarInputs(e, carInputsTable, truckListsSelect) {
	createCarInputs(100, carInputsTable)
	truckListsSelect.value = ''
	truckListsSelect.dispatchEvent(new Event('change'))
}


// -------------------------------------------------------------------------------//
// -----------------------функции для построения маршрута ------------------------//
// -------------------------------------------------------------------------------//

// функция для построения маршрута
function buildRoute(pointsData) {
	ajaxUtils.postJSONdata({
		url: getRoutingListUrl,
		token: token,
		data: pointsData,
		successCallback: (res) => {
			displayRoute(res)
		}
	})
}

// функция отображения маршрута на карте
function displayRoute(routeList) {
	clearRouteTable()
	const fullDistance = Array.isArray(routeList)
		? routeList.reduce((acc, item) => {
			acc = acc + item.distance
			return acc
		}, 0)
		: routeList.distance

	const fullDistanceToView = Math.round(fullDistance / 1000)

	routeList.forEach((item, i, arr) => {
		const pointCount = arr.length + 1
		createLocationPoints(item, i, pointCount, false)
		addRouteToMap(item.points, i, { distance: fullDistanceToView })
		addRouteInfo(item, i)
		addAddressInfo(item, i)
	})
	
	addMarkersToMap(mapStore.getMarkers())
	addDistanceInfo(fullDistanceToView)
}

// функции создания точек маршрута
function createLocationPoints(route, index, pointCount, hasStartPoint) {
	if (!hasStartPoint) {
		if (index === 0) {
			const shop = route.startShop
			const markerText = `№1 ${shop.numshop}`
			const icon = mapIcons.startIcon(markerText)
			const startMarker = getMarkerToShop(icon, shop)
			mapStore.addMarker(startMarker)
		}
	
		const shopCount = index + 2
		const shop = route.endShop
		const markerText = `№${shopCount} ${shop.numshop}`
		const icon = shopCount !== pointCount
			? mapIcons.intermediateIcon(markerText)
			: mapIcons.finishIcon(markerText)
		const marker = getMarkerToShop(icon, shop)
		mapStore.addMarker(marker)
	} else {
		const shopCount = index + 1
		const shop = route.shopNum === route.startShop.numshop? route.startShop : route.endShop
		const markerText = `№${shopCount} ${shop.numshop}`
		const icon = shopCount === 1
			? mapIcons.smallStartIcon(markerText)
			: shopCount !== pointCount
				? mapIcons.smallIntermediateIcon(markerText) : mapIcons.smallFinishIcon(markerText)
		const marker = getMarkerToShop(icon, shop)
		mapStore.addMarker(marker)
	}
}

// функции добавления маршрута и маркеров на карту
function addRouteToMap(points, i, popopInfo = {}, routeColor = '#ff0000b3' ) {
	const polyline = new L.polyline(points, { 
		renderer: canvasRenderer,
		color: routeColor,
		opacity: 0.8,
		routeId: popopInfo.id ? popopInfo.id : null,
		weight: 5
	})

	polyline.on('mouseover', polilineMouseOverHandler)
	polyline.on('mouseout', polilineMouseOutHandler)
	// polyline.on('click', polilineClickHandler)

	const poputHTML = `
		${popopInfo.id ? `<div class="font-weight-bold">ID маршрута: ${popopInfo.id}</div>` : ''}
		${popopInfo.distance ? `<div>Расстояние: ${popopInfo.distance} км</div>` : ''}
		${popopInfo.targetPall ? `<div>Загружено: ${popopInfo.targetPall} палл.</div>` : ''}
		${popopInfo.pall ? `<div>Вместимость: ${popopInfo.pall} палл.</div>` : ''}
		${popopInfo.vehicle ? `<div>Машина: ${popopInfo.vehicle}</div>` : ''}
	`
	popopInfo && polyline.bindPopup(poputHTML, { offset: [0, 10] })

	map.addLayer(polyline)
	i === 0 && map.fitBounds(polyline.getBounds())
	mapStore.addPolyline(polyline)
}
// обработчики для линии маршрута
function polilineMouseOverHandler(e) {
	const targetColor = e.target.options.color

	map.eachLayer(layer => {
		if (layer._latlngs && layer.options.color === targetColor) {
			layer.bringToFront()
			layer.setStyle({
				opacity: 1,
				weight: 7
			})
		}
	})
}
function polilineMouseOutHandler(e) {
	const targetColor = e.target.options.color

	map.eachLayer(layer => {
		if (layer._latlngs && layer.options.color === targetColor) {
			layer.bringToBack()
			layer.setStyle({
				opacity: 0.8,
				weight: 5
			})
		}
	})
}
function polilineClickHandler(e) {
	const targerRouteId = e.target.options.routeId
	const polylines = mapStore.getPolylines()

	polylines.forEach(polyline => {
		if (polyline.options.routeId === targerRouteId) {
			polyline.setStyle({ weight: 7, opacity: 1 })
			polyline.bringToFront()
		} else {
			polyline.setStyle({ weight: 5, opacity: 0.2 })
			polyline.bringToBack()
		}
	
	})
}

// функция удаления маркеров и маршрута с карты, а также информации о маршруте
function removeLayersfromMap() {
	const pointInfoElements = document.querySelectorAll('.pointInfo')
	pointInfoElements.forEach(el => {
		el.innerHTML = ''
	})

	const markersToView = mapStore.getMarkers()
	const polylinesToView = mapStore.getPolylines()

	if (markersToView.length) {
		markersToView.forEach(marker => {
			map.removeLayer(marker)
		})
	}
	mapStore.clearMarkers()

	if (polylinesToView.length) {
		polylinesToView.forEach(marker => {
			map.removeLayer(marker)
		})
	}
	mapStore.clearPolylines()
}
