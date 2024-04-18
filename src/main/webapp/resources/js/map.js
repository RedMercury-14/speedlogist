import { snackbar } from "./snackbar/snackbar.js"
import { debounce, getData, getEncodedString, hideLoadingSpinner, isAdmin, isManager, isTopManager, randomColor, showLoadingSpinner } from './utils.js'
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
	getModifiedGeojson,
	getNewPolygonLayer,
	hidePoligonControl,
	leafletDrawLayerEventHandlers,
	polygonNameInputHandler,
	showPoligonControl,
} from "./map/leafletDrawUtils.js"
import { showShops, toogleAllShops } from "./map/shopMarkersUtils.js"
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
	createCarInputs,
	createCleaningInputsColumn,
	createNumbersColumn,
	createRouteInputsTable,
	createRouteTextareaTable,
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

const testOptimizationUrl = `../api/map/myoptimization2`

const getAllShopsUrl = '../api/manager/getAllShops'
const getAllPolygonsUrl = '../api/map/getAllPolygons'
const sendGeojsonDataUrl = `../api/map/savePolygon`
const deletePolygonBaseUrl = `../api/map/delPolygon/`
const checkNamePolygonBaseUrl = `../api/map/checkNamePolygon/`
const getRouterParamsUrl = '../api/map/getDefaultParameters'
const setRouterParamsUrl = '../api/map/setDefaultParameters'
const getRoutingListUrl = '../api/map/way/4'
const getServerMessageUrl = '../api/map/getStackTrace'
const sendExcelFileUrl = '../api/map/5'
const sendExcelFileWithReportUrl = '../api/map/6'

const token = $("meta[name='_csrf']").attr("content")

const OPTIMIZE_ROUTE_DATA_KEY = "NEW_optimizeRouteData"

// обработчик отправки формы загрузки магазинов
function shopLoadsFormHandler(e) {
	e.preventDefault()

	const data = getShopLoadsFormData(e.target)
	console.log("🚀 ~ file: map.js:42 ~ shopLoadsFormHandler ~ data:", data)
	// место для отправки формы
	
}

function testBtn1ClickHandler(event) {
	console.log('testBtn1 cliicked!')
}
function testBtn2ClickHandler(event) {
	console.log('testBtn2 cliicked!')
}
function testBtn3ClickHandler(event) {
	console.log('testBtn3 cliicked!')
}
function testBtn4ClickHandler(event) {
	console.log('testBtn4 cliicked!')
}



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
	minZoom: 6,
	maxZoom: 18,
	zoomControl: false
}

// начальные координаты и масштаб карты
const zoom = 11
const lat = 53.875
const lng = 27.415

// маркер для встраивания в канвас
L.canvasMarker = (...options) => new CanvasMarker(...options)

// создание карты
const map = L.map("map", config).setView([lat, lng], zoom)
L.tileLayer("http://{s}.tile.osm.org/{z}/{x}/{y}.png").addTo(map)

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

// добавление кнопок масштабирования в правый верхник край
L.control.zoom({ position: 'topright' }).addTo(map)


// -----------------------------Leaflet draw--------------------------------------//
let drawEvent = currentDrawEvent

// локаль для библиотеки рисования
L.drawLocal = drawLocales('ru')

// добавление кнопок кнопок контроля для рисования
map.addControl(new customControl())
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

window.onload = async () => {
	addSmallHeaderClass()

	// кнопки боковой панели
	const menuItems = document.querySelectorAll(".menu-item")
	const buttonClose = document.querySelector(".close-button")
	menuItems.forEach((item) => addOnClickToMenuItemListner(item))
	buttonClose.addEventListener("click", () => closeSidebar())
	document.addEventListener("keydown", (e) => (e.key === "Escape") && closeSidebar())

	// тестовые кнопки
	const testBtn1 = document.querySelector('#testBtn1')
	const testBtn2 = document.querySelector('#testBtn2')
	const testBtn3 = document.querySelector('#testBtn3')
	const testBtn4 = document.querySelector('#testBtn4')
	testBtn1 && testBtn1.addEventListener('click', testBtn1ClickHandler)
	testBtn2 && testBtn2.addEventListener('click', testBtn2ClickHandler)
	testBtn3 && testBtn3.addEventListener('click', testBtn3ClickHandler)
	testBtn4 && testBtn4.addEventListener('click', testBtn4ClickHandler)

	// контейнеры для таблиц с информацией о точках маршрута
	const routeInputsContainer = document.querySelector('#routeInputsContainer')
	const routeAreaContainer = document.querySelector('#routeAreaContainer')
	routeInputsContainer && createRouteInputsTable(25, routeInputsContainer)
	routeAreaContainer && createRouteTextareaTable(25, routeAreaContainer)

	// контейнеры с нумерацией
	const optimizeRouteNumberContainer = document.querySelector('#optimizeRouteNumberContainer')
	const shopLoadsNumberContainer = document.querySelector('#shopLoadsNumberContainer')
	optimizeRouteNumberContainer && createNumbersColumn(500, optimizeRouteNumberContainer)
	shopLoadsNumberContainer && createNumbersColumn(500, shopLoadsNumberContainer)
	
	// контейнер с инпутами для оказания чисток в форме оптимизатора
	const optimizeRouteCleaningInputsContainer = document.querySelector('#optimizeRouteCleaningInputsContainer')
	optimizeRouteCleaningInputsContainer && createCleaningInputsColumn(500, optimizeRouteCleaningInputsContainer)

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
	const addressSearchForm = document.querySelector("#addressSearchForm")
	const optimizeRouteForm = document.querySelector("#optimizeRouteForm")
	const shopLoadsForm = document.querySelector("#shopLoadsForm")
	const routingParamsForm = document.querySelector("#routingParamsForm")
	const poligonControlForm = document.querySelector("#poligonControlForm")
	routeForm && routeForm.addEventListener("submit", routeFormHandler)
	routeAreaForm && routeAreaForm.addEventListener("submit", routeAreaFormHandler)
	distanceControlForm && distanceControlForm.addEventListener("submit", (e) => distanceControlFormHandler(e, distanceControlGridDiv))
	addressSearchForm && addressSearchForm.addEventListener("submit", addressSearchFormHandler)
	optimizeRouteForm && optimizeRouteForm.addEventListener("submit", (e) => optimizeRouteFormHandler(e, optimizeRouteGridDiv))
	shopLoadsForm && shopLoadsForm.addEventListener("submit", shopLoadsFormHandler)
	routingParamsForm && routingParamsForm.addEventListener("submit", (e) => routingParamsFormHandler(e, routeForm))
	poligonControlForm && poligonControlForm.addEventListener('submit', poligonControlFormSubmitHandler)

	// кнопки управления параметрами маршрутизатора
	const saveRoutingParamsBtn = document.querySelector("#saveRoutingParams")
	const loadRoutingParamsBtn = document.querySelector("#loadRoutingParams")
	saveRoutingParamsBtn && saveRoutingParamsBtn.addEventListener('click', (e) => saveRouterParams(routingParamsForm, setRouterParamsUrl, token))
	loadRoutingParamsBtn && loadRoutingParamsBtn.addEventListener('click', async (e) => loadRouterParams(routingParamsForm, getRouterParamsUrl))

	// переключатель отображения на карте всех магазинов
	const allShopsToggler = document.querySelector("#allShopsToggler")
	allShopsToggler && allShopsToggler.addEventListener('click', (e) => toogleAllShops(e, map))

	// проверка имени полигона
	const polygonNameInput = document.querySelector("#polygonName")
	polygonNameInput && polygonNameInput.addEventListener('change', (e) => polygonNameInputHandler(e, checkNamePolygonBaseUrl))

	// кастомные кнопки контроля для рисования
	const logJSON = document.querySelector(".log")
	const clearJSON = document.querySelector(".clear")
	logJSON && logJSON.addEventListener("click", (e) => logJSONonClickCallback(drawnItems))
	clearJSON && clearJSON.addEventListener("click", (e) => clearJSONOnClickCallback(drawnItems, map))

	// добавление инпутов с машинами в форме тестового оптимизатора
	const carInputsTable = document.querySelector('#carInputsTable')
	carInputsTable && createCarInputs(50, carInputsTable)

	// загрузка параметров маршрутизатора
	routingParamsForm && setRouterParams(routingParamsForm, getRouterParamsUrl)

	// автозаполнение формы оптимизации маршрутов данными из localstorage
	optimizeRouteForm && setOptimizeRouteFormData(optimizeRouteForm, OPTIMIZE_ROUTE_DATA_KEY)

	// отображение полигонов и элементов рисования
	const role = document.querySelector("#role").value
	if ( isAdmin(role)) {
		displayPolygons()
		showPoligonControl()
		displayShops()
	}
	if (isTopManager(role)) {
		displayPolygons()
		showPoligonControl()
		displayShops()
	}
	if (isManager(role)) {
		displayPolygons()
		displayShops()
	}


	// -------------------------------------------------------------------------------//
	// ---------------- расчёт количества паллет и паллетовместимости ----------------//
	// -------------------------------------------------------------------------------//

	const optimizeRoutePallTextarea = document.querySelector("#optimizeRoutePall")
	const countInputs = document.querySelectorAll('#optimizeRouteForm .carCount')
	const tonnageInputs = document.querySelectorAll('#optimizeRouteForm .maxPall')
	const palletsNeededElem = document.querySelector('#palletsNeeded')
	const totalPalletsElem = document.querySelector('#totalPallets')
	
	// debounced-функции расчёта сумм паллет
	const debouncedCalcTotalPallets = debounce(calcTotalPallets, 500)
	const debouncedCalcPalletsNeeded = debounce(calcPalletsNeeded, 500)
	
	// добавление листнера для расчёта необходимости магазинов в паллетах
	optimizeRoutePallTextarea && optimizeRoutePallTextarea.addEventListener('input', debouncedCalcPalletsNeeded)

	// добавление листнеров для расчёта общей паллетовместимости указанных машин
	countInputs.forEach((input) => input.addEventListener('input', debouncedCalcTotalPallets))
	tonnageInputs.forEach((input) => input.addEventListener('input', debouncedCalcTotalPallets))

	// функция для расчёта и отображения общей паллетовместимости указанных машин в форме оптимизатора
	function calcTotalPallets() {
		const palletsNeeded = Number(palletsNeededElem.innerText)
		const totalPallets = getTotalPallets()
		totalPalletsElem.innerText = totalPallets
		updateTotalPalletsElemClassName(palletsNeeded, totalPallets)
	}
	
	function getTotalPallets() {
		let totalPallets = 0

		countInputs.forEach((input, i) => {
			const pallets = Number(input.value)
			const cars = Number(tonnageInputs[i].value)

			if(Number.isFinite(pallets) && Number.isFinite(cars)) {
				totalPallets += pallets * cars
			}
		})

		return totalPallets
	}

	// функция для расчёта и отображения необходимости магазинов в паллетах в форме оптимизатора
	function calcPalletsNeeded(e) {
		const pallInArray = getTextareaData(e.target)
		const palletsNeeded = pallInArray.reduce((sum, pall) => sum + Number(pall), 0)
		const totalPallets = Number(totalPalletsElem.innerText)

		if (!Number.isFinite(palletsNeeded) || !Number.isFinite(totalPallets)) return
		
		palletsNeededElem.innerText = palletsNeeded
		updateTotalPalletsElemClassName(palletsNeeded, totalPallets)
	}

	// обновление цвета текста элемента с общей паллетовместимостью
	function updateTotalPalletsElemClassName(palletsNeeded, totalPallets) {
		const className = palletsNeeded <= totalPallets ? 'text-success' : 'text-danger'
		totalPalletsElem.className = `font-weight-bold ${className}`
	}
}

// -------------------------------------------------------------------------------//
// -------- отображение магазинов на карте при вводе в форме оптимизатора --------//
// -------------------------------------------------------------------------------//
async function displayShops() {
	const shops = await getData(getAllShopsUrl)
	if (!shops || shops.length === 0) return
	const optimizeRouteShopNum = document.querySelector("#optimizeRouteShopNum")
	if (!optimizeRouteShopNum) return
	optimizeRouteShopNum.addEventListener('change', (e) => optimizeRouteShopNumChangeCallback(e, shops))
}

// обработчик изменения значений номеров магазинов в форме оптимизатора
function optimizeRouteShopNumChangeCallback(e, shops) {
	const shopNums = getTextareaData(e.target)
	const shopsToView = shops.filter(shop => shopNums.includes(`${shop.numshop}`))
	showShops(shopsToView, map)
}

// -------------------------------------------------------------------------------//
// ------------ функция отображения полигонов и элементов рисования --------------//
// -------------------------------------------------------------------------------//
async function displayPolygons() {
	const allPolygons = await getData(getAllPolygonsUrl)

	if (!allPolygons || allPolygons.length === 0 ) return

	const modifyPolygons = allPolygons.map(polygon => {
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

	const modifiedGeoJSON = getModifiedGeojson(testGeoJSON, deletePolygonBaseUrl)
	modifiedGeoJSON.addTo(map)
}

// -------------------------------------------------------------------------------//
// ------------- функция автозаполнения формы оптимизации маршрутов --------------//
// -------------------------------------------------------------------------------//
function setOptimizeRouteFormData(form, storageKey) {
	const optimizeRouteItem = localStorage.getItem(storageKey)
	if (!optimizeRouteItem) return

	const data = JSON.parse(optimizeRouteItem)

	form.stock.value = data.stock
	form.iteration.value = data.iteration

	form.routeTextarea.value = data.shops.join('\n')
	form.pallTextarea.value = data.palls.join('\n')
	form.tonnageTextarea.value = data.tonnage.join('\n')

	data.cleanings.forEach((value, i) => {
		form.cleaning[i].checked = value
	})

	data.cars && data.cars.forEach((car, i) => {
		form.carName && (form.carName[i].value = car.carName)
		form.carCount && (form.carCount[i].value = car.carCount)
		form.maxPall && (form.maxPall[i].value = car.maxPall)
		form.maxTonnage && (form.maxTonnage[i].value = car.maxTonnage)
	})
}

// -------------------------------------------------------------------------------//
//------ функция отображения пустых машин в таблице маршрутов оптимизатора -------//
// -------------------------------------------------------------------------------//
function displayEmptyTruck(emptyTrucks) {
	const emptyTruckContainer = document.querySelector('#emptyTruckContainer')

	if (emptyTrucks.length === 0) {
		emptyTruckContainer.innerHTML = `<span>Свободных машин: 0</span>`
		return
	}

	const emptyTruckToView = emptyTrucks.reduce((acc, truck) => {
		const type = truck.type
		acc.hasOwnProperty(type) ? acc[type] += 1 : acc[type] = 1
		return acc
	}, {})

	const emptyTruckToViewStr = Object.entries(emptyTruckToView)
		.map(([type, count]) => `<span>${type}: ${count}</span>`)
		.join('')

	emptyTruckContainer.innerHTML = `<span>Свободные машины</span>` + emptyTruckToViewStr
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

// обработчик формы поиска точки по адресу
async function addressSearchFormHandler(e) {
	e.preventDefault()

	removeLayersfromMap()

	const string = e.target.testingInput.value
	const query = new URLSearchParams({
		q: string,
		limit: '10',
		reverse: 'false',
		key: '90c3a2ff-3918-441c-9bcf-49790be9efca'
	}).toString()

	const data = await getData(`https://graphhopper.com/api/1/geocode?${query}`)

	if (!data) {
		snackbar.show('Ответ отсутствует')
		return
	}

	if (data.hits.length) {
		const points = data.hits
		const index = points.findIndex(point => point.country === 'Беларусь')

		if (index) {
			const element = points.splice(index, 1)[0]
			points.unshift(element)
		}
	
		const point = points[0]
		const coord = point.point
		const marker = new L.marker(coord)

		let bounds
		
		if (point.extent && point.extent.length === 4) {
			const corner1 = L.latLng(point.extent[1], point.extent[0])
			const corner2 = L.latLng(point.extent[3], point.extent[2])
			bounds = L.latLngBounds(corner1, corner2)
		} else {
			bounds = L.latLngBounds([coord])
		}

		map.addLayer(marker)
		map.fitBounds(bounds)
	} else {
		snackbar.show('Адрес не найден')
	}
}

// обработчик отправки формы при создании полигона
function poligonControlFormSubmitHandler(e) {
	e.preventDefault()

	const form = e.target
	const nameInput = form.querySelector('#polygonName')

	if (nameInput.classList.contains('is-invalid') || form.reportValidity() === false) {
		// form.classList.add('was-validated')
		return
	}

	const formData = new FormData(form)
	const geojsonInfo = Object.fromEntries(formData)
	const name = geojsonInfo.polygonName
	const encodedName = getEncodedString(name)
	const action = geojsonInfo.polygonAction
	const layer = getNewPolygonLayer(name, encodedName, action, deletePolygonBaseUrl)
	const polygon = layer.toGeoJSON()

	if (polygon.properties.type === 'circle') {
		drawnItems.addLayer(layer)
		drawEvent = null
		form.reset()
		closePoligonControlModal()
		snackbar.show('Полигон добавлен на карту в тестовом режиме')
		return
	}

	ajaxUtils.postJSONdata({
		url: sendGeojsonDataUrl,
		token: token,
		data: polygon,
		successCallback: (res) => {
			drawnItems.addLayer(layer)
			drawEvent = null
			form.reset()
			closePoligonControlModal()
		}
	})
}

// обработчик отправки формы тестового оптимизатора
function optimizeRouteFormHandler(e, gridDiv) {
	e.preventDefault()

	const submitButton = e.submitter
	const submitButtonText = submitButton.innerText
	const data = getOptimizeRouteFormData(e.target)
	localStorage.setItem(OPTIMIZE_ROUTE_DATA_KEY, JSON.stringify(data))
	showLoadingSpinner(submitButton)
	
	ajaxUtils.postJSONdata({
		url: testOptimizationUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			hideLoadingSpinner(submitButton, submitButtonText)
			document.querySelector('#displayDataInput').value = res.stackTrace
			// $('#displayDataModal').modal('show')
			$('#collapseTwo').collapse('show')
			const data = getFormatDataToOptimizeRouteTable(res)
			displayOptimizeRouteTable(gridDiv, data)
			displayEmptyTruck(res.emptyTrucks)
		},
		errorCallback: () => hideLoadingSpinner(submitButton, submitButtonText)
	})
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

// функция очистки таблиц с информацией по точкам
function clearRouteTable() {
	const addressInfoElements = document.querySelectorAll(`.addressInfo`)
	const pointInfoElements = document.querySelectorAll(`.pointInfo`)
	const distanceInfoElements = document.querySelectorAll('#distanceInfo')

	addressInfoElements.forEach(elem => {
		elem.innerHTML = ''
	})
	pointInfoElements.forEach(elem => {
		elem.innerHTML = ''
	})
	distanceInfoElements.forEach(elem => {
		elem.innerHTML = ''
	})
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
function getMarkerToShop(icon, shop, generalRouteId = null) {
	const coord = { lat: shop.lat, lng: shop.lng }	
	const popupHtml = `
		<div class="font-weight-bold">№ ${shop.numshop}</div>
		<div>
			<span class="font-weight-bold">Адрес: </span>
			<span>${shop.address}</span>
		</div>
		<div>
			<span class="font-weight-bold">Потребность, паллет: </span>
			<span>${shop.needPall}</span>
		</div>
		<div class="font-weight-bold">Ограничения:</div>
		<div class="d-flex">
			<div class="mr-3">
				<span class="">Длина, м: </span>
				<span>${shop.length}</span>
			</div>
			<div>
				<span class="">Ширина, м: </span>
				<span>${shop.width}</span>
			</div>
		</div>
		<div class="d-flex">
			<div class="mr-3">
				<span class="">Высота, м: </span>
				<span>${shop.height}</span>
			</div>
			<div>
				<span class="">Паллеты: </span>
				<span>${shop.maxPall}</span>
			</div>
		</div>
	`
	return new L.marker(coord, {
				icon: icon,
				routeId: generalRouteId
			})
			.bindPopup(popupHtml, { offset: [0, -15] })
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
function addMarkersToMap(markers) {
	markers.forEach(marker => map.addLayer(marker))
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

// функции добавления информации о маршруте
function addRouteInfo(data, i) {
	const index = i + 2
	const distanceToView = Math.round(data.distance *10 / 1000) / 10

	const firstElements = document.querySelectorAll(`#pointInfo1`)
	const restElements = document.querySelectorAll(`#pointInfo${index}`)

	if (i === 0) {
		firstElements.forEach(elem => {
			elem.innerHTML = `0 км`
		})
	}

	restElements.forEach(elem => {
		elem.innerHTML = `${distanceToView} км`
	})
}
function addAddressInfo(data, i) {
	const index = i + 2
	const firstElements = document.querySelectorAll(`#addressInfo1`)
	const restElements = document.querySelectorAll(`#addressInfo${index}`)

	if (i === 0) {
		firstElements.forEach(elem => {
			elem.innerHTML = data.startShop.address
		})
	}

	restElements.forEach(elem => {
		elem.innerHTML = data.endShop.address
	})
}
function addDistanceInfo(fullDistance) {
	const distanceInfoElements = document.querySelectorAll('#distanceInfo')
	const distanceInfoInSettings = document.querySelector('#distanceInfoInSettings')

	distanceInfoElements.forEach(elem => {
		elem.innerHTML = `${fullDistance} км`
	})
	if (distanceInfoInSettings) distanceInfoInSettings.innerHTML = `${fullDistance} км`
}


// изменение положения сонтейнера с контентом
function addSmallHeaderClass() {
	const navbar = document.querySelector('.navbar')
	const height = navbar.offsetHeight
	
	if (height < 65) {
		const container = document.querySelector('.my-container')
		container.classList.add('smallHeader')
	}
}
