import { addNewTruck, removeTruck, updateTruck, updateTrucks } from './truckControlMethods.js'
import { truckAdapterFromWS } from './trucksUtils.js'

export function wsSlotOnOpenHandler(e) {
	console.log('Соединение установлено')
}

// обработка сообщений как для текущего пользователя, так и для других
export function wsSlotOnMessageHandler(e, freeTrucksGridOptions, selectedTrucksGridOptions) {
	const data = JSON.parse(e.data)
	if (data.WSPath !== 'TGBotRouting') return

	const status = data.status
	const action = data.action

	if (status === '200') {
		const truckStr = data.payload
		if (!truckStr) return

		const truckData = JSON.parse(truckStr)
		const adaptedTruckData = Array.isArray(truckData) ? truckData.map(truckAdapterFromWS) : truckAdapterFromWS(truckData)

		switch (action) {
			case 'add': {
				addNewTruck(adaptedTruckData, freeTrucksGridOptions, selectedTrucksGridOptions)
				return
			}
			case 'delete': {
				removeTruck(adaptedTruckData, freeTrucksGridOptions, selectedTrucksGridOptions)
				return
			}
			case 'update': {
				updateTruck(adaptedTruckData, freeTrucksGridOptions, selectedTrucksGridOptions)
				return
			}
			case 'updateList': {
				updateTrucks(adaptedTruckData, freeTrucksGridOptions, selectedTrucksGridOptions)
				return
			}
			default:
				return
		}
	}
}


export function wsSlotOnCloseHandler(e) {
	console.log('Соединение закрыто')
	$('#reloadWindowModal').modal('show')
}

export function wsSlotOnErrorHandler(e) {
	console.log('Ошибка соединения')
}

