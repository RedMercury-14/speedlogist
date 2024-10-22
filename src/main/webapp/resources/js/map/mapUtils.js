import { dateHelper } from "../utils.js"
import { checkPointInPolygon } from "./checkPointInPolygon.js"
import { getTextareaData } from "./formDataUtils.js"

// функция для обновления данных формы оптимизатора
export function addCrossDocking(optimizeRouteFormData, alllShops, polygons) {
	const shopNums = optimizeRouteFormData.shops
	const shops = getShopsByShopNum(shopNums, alllShops)
	const crossDockingPolygons = getCrossDockingPolygons(polygons)
	const mappedShops = addCrossDockingName(shops, crossDockingPolygons)

	return {
		...optimizeRouteFormData,
		shopsWithCrossDocking: mappedShops
	}
}

// получение объектов магазинов по номеру
function getShopsByShopNum(shopNums, alllShops) {
	return shopNums.map(num => {
		const shop = alllShops.find(shop => shop.numshop === Number(num))
		const { numshop, lat, lng, } = shop
		return {
			numshop,
			lat,
			lng,
		}
	})
}
// получение полигонов кросс-докинга
function getCrossDockingPolygons(polygons) {
	return polygons
		.filter(polygon => polygon.properties.action === 'crossDocking')
		.map(polygon => ({
			...polygon,
			properties: {
				...polygon.properties,
			},
			geometry: {
				...polygon.geometry,
				// меняем координаты местами
				coordinates: polygon.geometry.coordinates.map(coord => [coord[1], coord[0]])
			}
		}))
}
// добавление названия полигона, если магазин входит в зону кросс-докинга
function addCrossDockingName(shops, crossDockingPolygons) {
	return shops.map(shop => {
		// проверяем, в какую кросс-докинг зону входит магазин
		const crossDockingPolygon = crossDockingPolygons.find(polygon => {
			const coordinates = polygon.geometry.coordinates
			const shopCoordinates = [shop.lat, shop.lng]
			const isInside = checkPointInPolygon(shopCoordinates, coordinates)
			return isInside
		})

		const polygonName = crossDockingPolygon ? crossDockingPolygon.properties.name : null

		return {
			...shop,
			polygonName
		}
	})
}

// адаптирование полигона под хранилище приложения
export function adaptPolygonToStore(polygon) {
	return {
		...polygon,
		properties: {
			...polygon.properties,
		},
		geometry: {
			...polygon.geometry,
			// меняем координаты местами
			coordinates: polygon.geometry.coordinates[0]
		}
	}
}

// добавляем опции точек для кроссдокинга
export function addCrossDockingPointOptions(allShops, crossDockingPointSelect) {
	const crossDockingPoints = allShops.filter(shop => shop.type === "Кросс-докинг")
	crossDockingPoints.forEach(point => {
		const option = document.createElement('option')
		option.value = point.numshop
		option.text = `№${point.numshop} ${point.address}`
		crossDockingPointSelect.appendChild(option)
	})
}

// переключение видимости поля точки кросс-докиг зоны в форме создания полигона
export function crossDockingPointVisibleToggler(action) {
	const crossDockingPoint = document.querySelector(`#crossDockingPoint`)
	if (!crossDockingPoint) return
	if (action === 'crossDocking') {
		crossDockingPoint.required = true
		crossDockingPoint.parentElement.classList.remove('none')
	} else {
		crossDockingPoint.required = false
		crossDockingPoint.parentElement.classList.add('none')
	}
}

// функция автозаполнения формы оптимизации маршрутов
export function setOptimizeRouteFormData(form, storageKey) {
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

	setLocalCarsData(data, form)
}

export function setLocalCarsData(data, form) {
	if (!data.cars) return
	data.cars.length >= 100 && (data.cars.length = 100)
	data.cars && data.cars.forEach((car, i) => {
		form.carName && (form.carName[i].value = car.carName)
		form.secondRound && (form.secondRound[i].checked = car.secondRound)
		form.carCount && (form.carCount[i].value = car.carCount)
		form.maxPall && (form.maxPall[i].value = car.maxPall)
		form.maxTonnage && (form.maxTonnage[i].value = car.maxTonnage)
	})
}

export function setTrucksData(data, form) {
	data.forEach((truck, i) => {
		form.carName && (form.carName[i].value = truck.numTruck)
		form.secondRound && (form.secondRound[i].checked = truck.secondRound)
		form.carCount && (form.carCount[i].value = 1)
		form.maxPall && (form.maxPall[i].value = truck.pall)
		form.maxTonnage && (form.maxTonnage[i].value = truck.cargoCapacity)
	})
}

// функция отображения пустых машин в таблице маршрутов оптимизатора 
export function displayEmptyTruck(emptyTrucks) {
	const emptyTruckContainer = document.querySelector('#emptyTruckContainer')

	if (emptyTrucks.length === 0) {
		emptyTruckContainer.innerHTML = `<span>Свободных машин: 0</span>`
		return
	}

	const emptyTruckCount = emptyTrucks.length
	emptyTruckContainer.innerHTML = `<span>Свободных машин: ${emptyTruckCount}</span>`

	// const emptyTruckToView = emptyTrucks.reduce((acc, truck) => {
	// 	const type = truck.type
	// 	acc.hasOwnProperty(type) ? acc[type] += 1 : acc[type] = 1
	// 	return acc
	// }, {})

	// const emptyTruckToViewStr = Object.entries(emptyTruckToView)
	// 	.map(([type, count]) => `<span>${type}: ${count}</span>`)
	// 	.join('')

	// emptyTruckContainer.innerHTML = `<span>Свободные машины</span>` + emptyTruckToViewStr
}

// функция очистки таблиц с информацией по точкам
export function clearRouteTable() {
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

// маркер точки маршрута с попапом
export function getMarkerToShop(icon, shop, generalRouteId = null) {
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

// функции добавления информации о маршруте
export function addRouteInfo(data, i) {
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
export function addAddressInfo(data, i) {
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
export function addDistanceInfo(fullDistance) {
	const distanceInfoElements = document.querySelectorAll('#distanceInfo')
	const distanceInfoInSettings = document.querySelector('#distanceInfoInSettings')

	distanceInfoElements.forEach(elem => {
		elem.innerHTML = `${fullDistance} км`
	})
	if (distanceInfoInSettings) distanceInfoInSettings.innerHTML = `${fullDistance} км`
}


// изменение положения сонтейнера с контентом
export function addSmallHeaderClass() {
	const navbar = document.querySelector('.navbar')
	const height = navbar.offsetHeight
	
	if (height < 65) {
		const container = document.querySelector('.my-container')
		container.classList.add('smallHeader')
	}
}

// адептер для машин с сервера
export function truckAdapter(truck) {
	return {
		...truck,
		dateRequisition: dateHelper.getDateForInput(truck.dateRequisition),
		cargoCapacity: truck.cargoCapacity ? Number(truck.cargoCapacity) * 1000 : null,
	}
}

// обновление опций списков машин
export function updateTruckListsOptions(truckLists) {
	const truckListsSelect = document.querySelector("#truckListsSelect")

	// стартовый элемент
	// СВОБОДНЫЕ МАШИНЫ ДЛЯ ТЕСТИРОВАНИЯ
	truckListsSelect.innerHTML = `
		<option selected disabled value=''>Выберите список автомобилей</option>
		<option value='freeCars'>Свободные машины</option>
	`

	// опции названий списков машин
	truckLists.forEach((list) => {
		const option = document.createElement("option")
		const nameList = list.nameList
		option.value = nameList
		option.text = nameList
		truckListsSelect.appendChild(option)
	})

	// опция для ручного редактирования
	const option = document.createElement('option')
	option.value = 'manual'
	option.text = 'Ручной ввод'
	truckListsSelect.appendChild(option)

	// инициируем обновление полей машни
	truckListsSelect.dispatchEvent(new Event("change"))
}

// функция очистки формы создания полигона
export function clearPoligonControlForm(form) {
	crossDockingPointVisibleToggler('')
	form.reset()
}

// функция получения суммы паллет выделенных магазинов из формы оптимизатора
export function getSelectedShopsPallSum(layer, shopsToView) {
	if (shopsToView.length === 0) return null

	const polygon = layer.toGeoJSON()
	const coordinates = polygon.geometry.coordinates[0]
	const adaptCoordinates = coordinates.map(coord => [coord[1], coord[0]])

	// получаем объекты магазинов
	const shops = shopsToView.map(marker => {
		const coord = marker.getLatLng()
		return {
			numshop: marker.options.numshop,
			lat: coord.lat,
			lng: coord.lng,
		}
	})

	// получаем магазины внутри полигона
	const selectedShops = shops.filter(shop => checkPointInPolygon([shop.lat, shop.lng], adaptCoordinates))
	if (selectedShops.length === 0) return null

	// получаем номера магазинов и паллеты из формы оптимизатора
	const optimizeRouteShopNum = document.querySelector("#optimizeRouteShopNum")
	const optimizeRoutePall = document.querySelector("#optimizeRoutePall")
	const shopNums = getTextareaData(optimizeRouteShopNum)
	const palls = getTextareaData(optimizeRoutePall)

	// определяем индексы найденых магазинов в поле формы оптимизатора
	const selectedShopIndexes = selectedShops.map(shop => shopNums.findIndex(shopNum => shopNum === `${shop.numshop}`))
	return selectedShopIndexes.reduce((acc, index) => acc + Number(palls[index]), 0)
}