import { ajaxUtils } from '../../ajaxUtils.js'
import { snackbar } from "../../snackbar/snackbar.js"
import { deletePreorderTruckUrl, getCarrierTGTrucksUrl, preorderTrucksForDateUrl } from "../../globalConstants/urls.js"
import { getData } from '../../utils.js'
import { bootstrap5overlay } from '../../bootstrap5overlay/bootstrap5overlay.js'
import { truckAdapter } from '../trucks/trucksUtils.js'
import { store } from './store.js'

// получение данных о машинах
export async function getTruckData() {
	const response = await getData(getCarrierTGTrucksUrl)
	if (!response) return []
	const trucksData = response.status === '200'
		? response.trucks ? response.trucks : []
		: []
	return trucksData
}

// отправка данных о заявке
export function sendTruckData(truckData, method) {
	const modal = method === 'addTruck'
		? $('#addNewTruckModal') : method === 'copyTruckToDate'
			? $('#copyTruckToDateModal') : null

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: preorderTrucksForDateUrl,
		data: truckData,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				modal && modal.modal('hide')
				res.message && snackbar.show(res.message)
				// получаем обновленные данные и обновляем таблицу
				const trucks = res.objects.map(truckAdapter)
				store.addTrucks(trucks)
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
			if (res.status === '105') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// удаление машины
export function deleteTruck(truck) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: deletePreorderTruckUrl,
		data: { idTGTruck: truck.idTGTruck },
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				snackbar.show(res.message || 'Выполнено!')
				store.removeTruck(truck)
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
			if (res.status === '105') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}