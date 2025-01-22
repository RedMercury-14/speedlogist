// -------------------------------------------------------------------------------//
// ------- функции отображения на карте и удаления с карты всех магазинов --------//
// -------------------------------------------------------------------------------//

import { isLogisticsDeliveryPage } from "../utils.js"
import { getTextareaData } from "./formDataUtils.js"
import { mapStore } from "./mapStore.js"

// функция переключения видимости магазинов из оптимизатора на карте
export function optimizerShopToggler(e, map) {
	const showOptimizerShops = e.target.checked

	if (showOptimizerShops) {
		const optimizeRouteShopNum = document.querySelector("#optimizeRouteShopNum")
		const shops = mapStore.getShops()
		const shopNums = getTextareaData(optimizeRouteShopNum)
		const shopsToView = shops.filter(shop => shopNums.includes(`${shop.numshop}`))
		showShops(shopsToView, map)
	} else {
		hideShops(map)
	}
}

// функция отображения магазинов на карте
export function showShops(shops, map) {
	// очитска карты от маркеров
	mapStore.getShopsToView().forEach(marker => map.removeLayer(marker))

	// добавление нужных маркеров
	const shopsToView = []
	shops.forEach(shop => shopsToView.push(getCanvasShopMarker(shop)))
	shopsToView.forEach(marker => map.addLayer(marker))
	mapStore.setShopsToView(shopsToView)
}

// функция удаления магазинов с карты
export function hideShops(map) {
	const shopsToView = mapStore.getShopsToView()
	if (shopsToView.length) {
		shopsToView.forEach(marker => {
			map.removeLayer(marker)
		})
	}
}

// функция переключения видимости всех магазинов на карте
export function toogleAllShops(e, map, allShops) {
	const showPoligonControlElements = e.target.checked

	showPoligonControlElements
		? showAllShops(map, allShops)
		: hideAllShops(map)
}

function showAllShops(map, allShops) {
	const allShopsToView = mapStore.getAllShopsToView()
	if (allShopsToView.length === 0) {
		if (!allShops || allShops.length === 0) return
		allShops.map(shop => allShopsToView.push(getCanvasShopMarker(shop)))
		mapStore.setAllShopsToView(allShopsToView)
	}
	allShopsToView.forEach(marker => map.addLayer(marker))
}
function hideAllShops(map) {
	const allShopsToView = mapStore.getAllShopsToView()
	if (allShopsToView.length) {
		allShopsToView.forEach(marker => {
			map.removeLayer(marker)
		})
	}
}

// маркер магазина
function getCanvasShopMarker(shop) {
	const container = document.createElement('div')
	const popupHtml = getShopPopupHtml(shop)
	container.className = 'shopPopup'
	container.innerHTML = popupHtml

	// добавление листнера для формы в попапе
	const form = container.querySelector('#shopPopupForm')
	form && form.addEventListener('submit', shopPopupFormSubmitHandler)

	const imgSrc = getImageSrc(shop)

	return L.canvasMarker(
			{ lat: shop.lat, lng: shop.lng },
			{
				img: { url: imgSrc, size: [24, 24], },
				numshop: shop.numshop,
			}
		)
		.bindPopup(container, { offset: [0, -15] })
}

// попап для маркера магазина
function getShopPopupHtml(shop) {
	const numShop = shop.numshop
	const shopForm = `
		<form action="" id="shopPopupForm">
			<input type="hidden" name="numshop" value="${numShop}" />
			<div>
				<div class="font-weight-bold">Ограничение подъезда (Д/Ш/В), м:</div>
				<div class="row-container">
					<input class="entranceLength" type="number" name="length" id="length_${numShop}"/>
					<input class="entranceWidth" type="number" name="width" id="width_${numShop}"/>
					<input class="entranceHeight" type="number" name="height" id="height_${numShop}"/>
				</div>
			</div>
			<div>
				<div class="font-weight-bold">Время приема товаров:</div>
				<div class="row-container">
					<input class="receptionTimeStart" type="text" name="receptionTimeStart" id="receptionTimeStart_${numShop}"/>
					<span> - </span>
					<input class="receptionTimeEnd" type="text" name="receptionTimeEnd" id="receptionTimeEnd_${numShop}"/>
				</div>
			</div>
			<div>
				<div class="font-weight-bold">Чистки:</div>
				<textarea class="pallCleaning" name="pallCleaning" id="pallCleaning_${numShop}" cols="30" rows="2"></textarea>
			</div>
			<div>
				<div class="font-weight-bold">Комментарии:</div>
				<textarea class="shopComment" name="shopComment" id="shopComment_${numShop}" cols="30" rows="2"></textarea>
			</div>
			<button type="submit">Сохранить</button>
		</form>
	`
	return `
		<div class="font-weight-bold">№ ${numShop}</div>
		<div>
			<span class="font-weight-bold">Адрес: </span>
			<span>${shop.address}</span>
		</div>
		<div>
			<span class="font-weight-bold">Время работы: </span>
			<span>${shop.workStart} - ${shop.workfinish}</span>
		</div>
		<div>
			<span class="font-weight-bold">Координаты: </span>
			<span>${shop.lat}, ${shop.lng}</span>
		</div>
		<div class="font-weight-bold">Ограничения :</div>
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
}

// картинка маркера магазина
function getImageSrc(shop) {
	const isStorage = shop.address.toLowerCase().includes('склад') || shop.type === 'Склад'
	const hasRestriction = shop.length || shop.width || shop.height || shop.maxPall
	const isCrossDocking = shop.type === 'Кросс-докинг'
	const isSupplier = shop.type === 'Поставщик'

	const baseUrl =  isLogisticsDeliveryPage() ? '../../../speedlogist/resources/img/' : '../../speedlogist/resources/img/'
	
	if (isCrossDocking) return `${baseUrl}cross-docking_80x80_2.png`
	if (isSupplier) return `${baseUrl}supplier_64x64.png`

	const imgSrc = isStorage
		? `${baseUrl}warehouse_32x32.png`
		: hasRestriction
			? `${baseUrl}shop_restr_32x32_2.png`
			: `${baseUrl}shop_32x32.png`
	return imgSrc
}

// обработчик формы попапа маркера магазина
function shopPopupFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	console.log('data:', data)

}
