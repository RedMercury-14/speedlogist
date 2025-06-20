import { testRoutesData } from "./testData.js"
import { ajaxUtils } from '../../ajaxUtils.js'
import { snackbar } from "../../snackbar/snackbar.js"
import { uploadRouteExcelDataUrl, uploadStockExcelDataUrl } from "../../globalConstants/urls.js"
import { getData } from '../../utils.js'
import { bootstrap5overlay } from '../../bootstrap5overlay/bootstrap5overlay.js'

export async function getRoutesData() {
	// ТЕСТОВЫЕ ДАННЫЕ
	const routes = testRoutesData.map(route => {
		return {
			...route,
			shops: route.shops.map((shop, i) => {
				return {
					...shop,
					order: i + 1,
					id: `${route.id}_${shop.numshop}_${i}`
				}
			})
		}
	})
	return routes
}


export function uploadStockExcelData(form) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postMultipartFformData({
		url: uploadStockExcelDataUrl,
		data: new FormData(form),
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			console.log("🚀 ~ uploadStockExcelData ~ res:", res)
			snackbar.show('Данные успешно загружены!')
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

export function uploadRouteExcelData(form) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postMultipartFformData({
		url: uploadRouteExcelDataUrl,
		data: new FormData(form),
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			console.log("🚀 ~ uploadRouteExcelData ~ res:", res)
			snackbar.show('Данные успешно загружены!')
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}