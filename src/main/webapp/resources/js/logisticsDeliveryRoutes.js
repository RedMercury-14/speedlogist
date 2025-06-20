import { uiIcons } from "./uiIcons.js"
import { dateHelper} from "./utils.js"
import { nextDate, prevDate } from "./logisticsDelivery/trucks/trucksUtils.js"
import { renderTable, updateTable } from "./AG-Grid/ag-grid-utils.js"
import { agGridMapCallback, shopGridOptions } from "./logisticsDelivery/routes/agGridUtils.js"
import { getRoutesData, uploadRouteExcelData, uploadStockExcelData } from "./logisticsDelivery/routes/api.js"
import { store } from "./logisticsDelivery/routes/store.js"

const LEFT_TABLE_TYPE = 'left'
const RIGHT_TABLE_TYPE = 'right'


const gridOptionsLeft = {
	...shopGridOptions,
	context: {
		...shopGridOptions.context,
		tableType: LEFT_TABLE_TYPE,
	},
	getContextMenuItems: getContextMenuItems,
	onRowDragEnd: onRowDragEnd
}
const gridOptionsRight = {
	...shopGridOptions,
	context: {
		...shopGridOptions.context,
		tableType: RIGHT_TABLE_TYPE,
	},
	getContextMenuItems: getContextMenuItems,
	onRowDragEnd: onRowDragEnd
}

document.addEventListener('DOMContentLoaded', async () => {
	// инициализация приложения
	await init()

	// рендер таблицы машин
	const gridDivLeft = document.querySelector('#myGridLeft')
	const gridDivRight = document.querySelector('#myGridRight')
	renderTable(gridDivLeft, gridOptionsLeft)
	renderTable(gridDivRight, gridOptionsRight)

	setupDropBetweenGrids(gridOptionsLeft, gridOptionsRight)
	setupDropBetweenGrids(gridOptionsRight, gridOptionsLeft)

	const routesSelectLeft = document.getElementById('routesSelectLeft')
	const routesSelectRight = document.getElementById('routesSelectRight')

	routesSelectLeft.addEventListener('change', (e) => routesSelectChangeHandler(e, LEFT_TABLE_TYPE))
	routesSelectRight.addEventListener('change', (e) => routesSelectChangeHandler(e, RIGHT_TABLE_TYPE))

	const routeList = store.getRouteList()
	routeList.forEach((item) => {
		addOption(item, routesSelectLeft)
		addOption(item, routesSelectRight)
	})

	const toggleRightTableVisibleBtn = document.getElementById('toggleRightTableVisible')
	toggleRightTableVisibleBtn.addEventListener('click', rightTableToggler)

	// форма загрузки задания по складам
	uploadStockExcelDataForm.addEventListener('submit', uploadStockExcelDataFormSubmitHandler)
	const tomorrow = dateHelper.getTomorrowDate()
	uploadStockExcelDataForm.dateTask.min = tomorrow

	// форма загрузки маршрутов
	uploadRouteExcelDataForm.addEventListener('submit', uploadRouteExcelDataFormSubmitHandler)
	uploadRouteExcelDataForm.dateTask.min = tomorrow
})

// Добавим поддержку дропа с перемещением
function setupDropBetweenGrids(sourceGridOptions, targetGridOptions) {
	const dropZoneParams = targetGridOptions.api.getRowDropZoneParams({
		onDragStop: (params) => {
			const movedData = params.node.data
			// Удалить из исходной таблицы
			sourceGridOptions.api.applyTransaction({ remove: [movedData] })
		},
	})

	sourceGridOptions.api.addRowDropZone(dropZoneParams)
}


// установка получение и установка стартовых данных
async function init() {
	// данные
	const routes = await getRoutesData()
	const routeList = routes.map(route => ({ id: route.id, name: route.id }))
	// отправляем данные в стор
	store.setRoutes(routes)
	store.setRouteList(routeList)
	// подписка на обновления стора
	// store.subscribe((state) => {
	// 	const trucks = state.trucks[state.currentDate]
	// 	updateTable(gridOptions, trucks, agGridMapCallback)
	// })
}

// контекстное меню таблицы
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const truck = rowNode.data

	const result = [
		{
			name: `Копировать авто на`,
			icon: uiIcons.files,
			subMenu: [
				{
					name: `завтра`,
					// action: () => copyTruck('toTomorrow', truck),
				},
				{
					name: `дату ...`,
					// action: () => copyTruck('toDate', truck),
				},
			]
		},
		"separator",
		{
			name: `Отменить авто`,
			// action: () => deleteTruck(truck),
			icon: uiIcons.trash,
		},
		"separator",
		"excelExport",
	]

	return result
}


function onRowDragEnd(params) {
	const targetRouteId = params.context.routeId
	const tableType = params.context.tableType

	let targetGridOptions = params
	let sourceGridOptions
	let sourceRouteId
	let checkSourceTable

	// назначаем опциональные переменные
	if (tableType === LEFT_TABLE_TYPE) {
		sourceGridOptions = gridOptionsRight
		sourceRouteId = store.getCurrentRouteIdRight()
		checkSourceTable = () => store.getRightTableVisible() && store.getCurrentRouteIdRight()
	} else if (tableType === RIGHT_TABLE_TYPE) {
		sourceGridOptions = gridOptionsLeft
		sourceRouteId = store.getCurrentRouteIdLeft()
		checkSourceTable = () => store.getCurrentRouteIdLeft()
	}

	setTimeout(() => {
		// данные целевой таблицы
		const targetTableData = []
		targetGridOptions.api.forEachNode((node) => targetTableData.push(node.data))
		// магазины целевой таблицы по порядку
		const targetTableShops = targetTableData.map((shop, index) => ({ ...shop, order: index + 1 }))
		// обновление целевой таблицы
		targetGridOptions.api.applyTransactionAsync({ update: targetTableShops })
		// обновление данных в сторе
		store.updateRouteShops(targetRouteId, targetTableShops)

		// если видна таблица-источник и выбран маршрут
		if (checkSourceTable()) {
			// если маршруты в таблицах одинаковые
			if (targetRouteId === sourceRouteId) {
				// устанавливаем в таблицу-источник магазины из целевой, т.к. в ней произошли изменения
				updateTable(sourceGridOptions, targetTableShops)
			} else {
				// иначе берем данные из таблицы-источника
				const sourceTableData = []
				sourceGridOptions.api.forEachNode((node) => sourceTableData.push(node.data))
				// магазины таблицы-источника по порядку
				const sourceTableShops = sourceTableData.map((shop, index) => ({ ...shop, order: index + 1 }))
				// обновление таблицы-источника
				sourceGridOptions.api.applyTransactionAsync({ update: sourceTableShops })
				// обновление данных в сторе
				store.updateRouteShops(sourceRouteId, sourceTableShops)
			}
		}
	}, 100)
}



// обработчик изменения отображаемого маршрута
function routesSelectChangeHandler(e, type) {
	const value = e.target.value
	const routeId = value ? Number(value) : null
	const route = store.getRouteById(routeId)
	const shops = route ? route.shops : []

	if (type === LEFT_TABLE_TYPE) {
		store.setCurrentRouteIdLeft(routeId)
		updateTable(gridOptionsLeft, shops, agGridMapCallback)
		gridOptionsLeft.context.routeId = routeId
	} else if (type === RIGHT_TABLE_TYPE) {
		store.setCurrentRouteIdRight(routeId)
		updateTable(gridOptionsRight, shops, agGridMapCallback)
		gridOptionsRight.context.routeId = routeId
	}
}

// переключение видимости правой таблицы
function rightTableToggler(e) {
	const btn = e.currentTarget
	const gridContainer = document.getElementById('gridContainer')
	const routesSelectRight = document.getElementById('routesSelectRight')
	
	store.setRightTableVisible(!store.getRightTableVisible())
	routesSelectRight.value = ''
	routesSelectRight.dispatchEvent(new Event("change"))

	if (store.getRightTableVisible()) {
		btn.innerHTML = uiIcons.window
		gridContainer.classList.add('showRightTable')
	} else {
		btn.innerHTML = uiIcons.windowSplit
		gridContainer.classList.remove('showRightTable')
	}
}

// обработчик отправки формы с файлами Excel по складам
function uploadStockExcelDataFormSubmitHandler(e) {
	e.preventDefault()
	uploadStockExcelData(e.target)
}
// обработчик отправки формы с файлом Excel с маршрутами
function uploadRouteExcelDataFormSubmitHandler(e) {
	e.preventDefault()
	uploadRouteExcelData(e.target)
}




function addOption(list, select) {
	const option = document.createElement("option")
	option.value = list.id
	option.text = list.name
	select.append(option)
}



// установка данных в форму
function setDataToForm(form, data) {
	for (const key in data) {
		if (form[key] && data[key]) {
			form[key].value = data[key]
		}
	}
}
