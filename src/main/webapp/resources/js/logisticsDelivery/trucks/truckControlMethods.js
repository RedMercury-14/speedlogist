import { addTableRow, addTableRows, removeTableRow, removeTableRows } from './agGridUtils.js'
import { store } from './store.js'
import { getUpdateAction } from './trucksUtils.js'

// добавление новой машины
export function addNewTruck(truck, freeTrucksGridOptions, selectedTrucksGridOptions) {
	// добавляем машину в стор
	store.addTruck(truck)
	// обновляем таблицу свободных машин, если
	// дата машины совпадает с отображаемой датой
	if (truck.dateRequisition === store.getCurrentDate()) {
		addTableRow(freeTrucksGridOptions, truck)
	}
}

export function removeTruck(truck, freeTrucksGridOptions, selectedTrucksGridOptions) {
	// удаляем машину из стора
	store.removeTruck(truck)
	// обновляем таблицу свободных машин, если
	// дата машины совпадает с отображаемой датой
	if (truck.dateRequisition === store.getCurrentDate()) {
		removeTableRow(freeTrucksGridOptions, truck)
	}
}

export function updateTruck(truck, freeTrucksGridOptions, selectedTrucksGridOptions) {
	// получаем старые данные для сравнения
	const oldTruck = store.getOldTruckByCurrentDate(truck.idTGTruck)
	// обновляем машину в сторе
	store.updateTruck(truck)
	// выясняем, что сделать с машиной
	const updateAction = getUpdateAction(truck)

	// обновляем таблицу свободных машин, если
	// дата машины совпадает с отображаемой датой
	if (truck.dateRequisition === store.getCurrentDate()) {
		updateAction === 'toSelected'
			? removeTableRow(freeTrucksGridOptions, truck)
			: addTableRow(freeTrucksGridOptions, truck)
		
		// обновляем таблицу выбранных машин, если
		// список машины совпадает с отображаемым списком
		if (truck.nameList === store.getCurrentNameList()) {
			addTableRow(selectedTrucksGridOptions, truck)
		} else {
			// удаляем из таблицы выбранных машин, если
			// старый список совпадает с отображаемым списком
			if (oldTruck.nameList === store.getCurrentNameList()) {
				removeTableRow(selectedTrucksGridOptions, truck)
			}
		}
	}
}

export function updateTrucks(trucks, freeTrucksGridOptions, selectedTrucksGridOptions) {
	// получаем старые данные для сравнения
	const truck = trucks[0]
	const oldTruck = store.getOldTruckByCurrentDate(truck.idTGTruck)
	// обновляем машины в сторе
	store.updateTrucks(trucks)
	// выясняем, что сделать с машинами
	const updateAction = getUpdateAction(truck)

	// обновляем таблицу свободных машин, если
	// дата машины совпадает с отображаемой датой
	if (truck.dateRequisition === store.getCurrentDate()) {
		updateAction === 'toSelected'
			? removeTableRows(freeTrucksGridOptions, trucks)
			: addTableRows(freeTrucksGridOptions, trucks)
		
		// обновляем таблицу выбранных машин, если
		// список машины совпадает с отображаемым списком
		if (truck.nameList === store.getCurrentNameList()) {
			addTableRows(selectedTrucksGridOptions, trucks)
		} else {
			// удаляем из таблицы выбранных машин, если
			// старый список совпадает с отображаемым списком
			if (oldTruck.nameList === store.getCurrentNameList()) {
				removeTableRows(selectedTrucksGridOptions, trucks)
			}
		}
	}
}
