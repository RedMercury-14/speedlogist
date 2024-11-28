import { BtnCellRenderer } from './ArrowBtn.js'
import { store } from './store.js'
import { getData, isObserver } from '../../utils.js'
import {
	renderTable,
	setStoreInStatusPanel,
	trucksColumnDefs,
	trucksGridOptions,
	updateTableData,
} from './agGridUtils.js'
import { snackbar } from '../../snackbar/snackbar.js'
import {
	changeContentMarginTop,
	getTruckDateForAjax,
	getTruckLists,
	groupTrucksByDate,
	nextDate,
	prevDate,
	truckAdapter,
	updateTruckListsOptions
} from './trucksUtils.js'
import { wsSlotUrl } from '../../global.js'
import {
	wsSlotOnCloseHandler,
	wsSlotOnErrorHandler,
	wsSlotOnMessageHandler,
	wsSlotOnOpenHandler
} from './wsHandlers.js'
import { uiIcons } from '../../uiIcons.js'
import { ajaxUtils } from '../../ajaxUtils.js'
import { bootstrap5overlay } from '../../bootstrap5overlay/bootstrap5overlay.js'


const freeTrucksColumnDefs = [
	...trucksColumnDefs,
	{
		field: "",
		cellClass: 'p-0',
		width: 70,
		resizable: false,
		pinned: 'right', lockPosition: true,
		cellRenderer: BtnCellRenderer,
		cellRendererParams: {
			type: 'toSelectedList',
			clicked: toSelectedList,
		},
	}
]
const selectedTrucksColumnDefs = [
	{
		field: "",
		cellClass: 'p-0',
		width: 70,
		resizable: false,
		pinned: 'left', lockPosition: true,
		cellRenderer: BtnCellRenderer,
		cellRendererParams: {
			type: 'toAllList',
			clicked: toAllList,
		},
	},
	...trucksColumnDefs,
]

const freeTrucksGridOptions = {
	...trucksGridOptions,
	columnDefs: freeTrucksColumnDefs,
	getContextMenuItems: getContextMenuItemsForFreeTrucks,
}
const selectedTrucksGridOptions = {
	...trucksGridOptions,
	columnDefs: selectedTrucksColumnDefs,
	getContextMenuItems: getContextMenuItemsForSelectedTrucks,
}


window.onload = async function() {
	// инициализация приложения
	await init()

	const currentDateInput = document.querySelector('#currentDate')
	// кнопка переключения даты вперед
	const datePrevBtn = document.querySelector('#datePrev')
	// кнопка переключения даты назад
	const dateNextBtn = document.querySelector('#dateNext')
	// селект выбора отображаемого списка машин
	const truckListsSelect = document.querySelector('#truckListsSelect')
	// форма создания нового списка машин
	const addNewListForm = document.querySelector('#addNewListForm')
	// форма удаления текущего списка
	const removeCurrentListForm = document.querySelector('#removeCurrentListForm')
	// кнопка перезагрузки страницы в модльном окне
	const reloadWindowButton = document.querySelector('#reloadWindowButton')
	// div таблицы свободных машин
	const freeTrucksGridDiv = document.querySelector('#freeTrucksGrid')
	// div таблицы выбранных машин
	const selectedTrucksGridDiv = document.querySelector('#selectedTrucksGrid')

	// изменение отступа для контейнера таблиц
	changeContentMarginTop(freeTrucksGridDiv, selectedTrucksGridDiv)

	// установака начальной даты
	currentDateInput.value = store.getCurrentDate()

	currentDateInput.addEventListener('change', changeCurrentDateHandler)
	datePrevBtn.addEventListener('click', () => prevDate(currentDateInput))
	dateNextBtn.addEventListener('click', () => nextDate(currentDateInput))
	truckListsSelect.addEventListener('change', truckListsSelectChangeHandler)
	addNewListForm.addEventListener('submit', addNewListSubmitFormHandler)
	removeCurrentListForm.addEventListener('submit', removeCurrentListSubmitFormHandler)
	reloadWindowButton.addEventListener('click', (e) => window.location.reload())

	// обработчик закрытия модального окна сохранения нового списка
	$('#addNewListModal').on('hide.bs.modal', (e) => addNewListForm.reset())

	// получение стартовых данных
	const freeTrucks = store.getFreeTrucksByCurrentDate()
	const selectedTrucks = []

	// рендер таблиц машин
	renderTable(freeTrucksGridDiv, freeTrucksGridOptions, freeTrucks)
	renderTable(selectedTrucksGridDiv, selectedTrucksGridOptions, selectedTrucks)

	// устанавливаем опции селекта списков машин
	updateTruckListsOptions(store.getListsByCurrentDate())
}


// установка получение и установка стартовых данных
async function init() {
	// подключение вебсокета
	const wsSlot = new WebSocket(wsSlotUrl)
	wsSlot.onopen = wsSlotOnOpenHandler
	wsSlot.onclose = wsSlotOnCloseHandler
	wsSlot.onerror = wsSlotOnErrorHandler
	wsSlot.onmessage = (e) => wsSlotOnMessageHandler(e, freeTrucksGridOptions, selectedTrucksGridOptions)

	// данные о машинах
	const response = await getData('../../api/logistics/deliveryShops/getTGTrucks')
	const trucksData = response.status === '200'
		? response.body ? response.body : []
		: []

	const mappedTruckData = trucksData.map(truckAdapter)

	// машины по датам
	const trucks = groupTrucksByDate(mappedTruckData)
	// списки машин
	const lists = getTruckLists(mappedTruckData)

	store.setTrucks(trucks)
	store.setLists(lists)

	// добавляем стор в строки состояния таблиц
	setStoreInStatusPanel(freeTrucksGridOptions, store)
	setStoreInStatusPanel(selectedTrucksGridOptions, store)
}


// контекстное меню таблицы свободных машин
function getContextMenuItemsForFreeTrucks(params) {
	const selectedRowsData = params.api.getSelectedRows()

	const result = [
		{
			disabled: !selectedRowsData.length || isObserver(store.getRole()),
			name: `Добавить в список выделенные машины`,
			action: () => {
				const currentNameList = store.getCurrentNameList()
				if (!currentNameList) {
					snackbar.show('Не выбран ни один список!')
					return
				}

				loadTruckList(selectedRowsData, currentNameList, 'toSelect')
			},
			icon: uiIcons.toLeftArrow
		},

		"separator",
		"excelExport",
	]

	return result
}
// контекстное меню таблицы выбранных машин
function getContextMenuItemsForSelectedTrucks(params) {
	const selectedRowsData = params.api.getSelectedRows()

	const result = [
		{
			disabled: !selectedRowsData.length || isObserver(store.getRole()),
			name: `Удалить из списка выделенные машины`,
			action: () => {
				loadTruckList(selectedRowsData, null, 'toAll')
			},
			icon: uiIcons.toRightArrow
		},

		"separator",
		"excelExport",
	]

	return result
}

// обработчик изменения отображаемой даты
function changeCurrentDateHandler(e) {
	const date = e.target.value
	// сохраняем текущую дату
	store.setCurrentDate(date)
	// обнуляем текущий список машин
	store.setCurrentNameList(null)
	// свободные машины на текущую дату
	const freeTrucks = store.getFreeTrucksByCurrentDate()
	// списки машин на текущую дату
	const truckLists = store.getListsByCurrentDate()
	// обновляем таблицы
	updateTableData(freeTrucksGridOptions, freeTrucks)
	updateTableData(selectedTrucksGridOptions, [])
	// обновляем селект выбора списка авто
	updateTruckListsOptions(truckLists)

	// const totalTrucks = store.getTrucksByCurrentDate()
	// // updateTotalTrucksInStatusPanel(freeTrucksGridOptions, totalTrucks)
	// // updateTotalTrucksInStatusPanel(selectedTrucksGridOptions, totalTrucks)

	// console.log(freeTrucksGridOptions.api.getStatusPanel(2))
}

// обработчик изменения отображаемого списка машин
function truckListsSelectChangeHandler(e) {
	const nameList = e.target.value

	if (!nameList) {
		store.setCurrentNameList(null)
		updateTableData(freeTrucksGridOptions, store.getFreeTrucksByCurrentDate())
		updateTableData(selectedTrucksGridOptions, [])
	}

	store.setCurrentNameList(nameList)
	const trucks = store.getTrucksByNameList(nameList)
	updateTableData(freeTrucksGridOptions, store.getFreeTrucksByCurrentDate())
	updateTableData(selectedTrucksGridOptions, trucks)
}

// обработчик формы сохранения нового списка
function addNewListSubmitFormHandler(e) {
	e.preventDefault()

	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const formData = new FormData(e.target)
	const nameList = formData.get('nameList')
	const currentDate = store.getCurrentDate()
	// проверка наличия списка на текущую дату
	const isExist = store.getListByNameAndDate(nameList, currentDate)
	// получение данных для отображения в таблице выбранных машин
	const tableData = isExist ? store.getTrucksByNameList(nameList) : []
	// добавляем новый список, если такого списка нет
	if (!isExist) store.addList(nameList)
	// устанавливаем название текущего списка
	store.setCurrentNameList(nameList)
	// добавляем в селект выбора списка авто новый элемент и выбираем его
	updateTruckListsOptions(store.getListsByCurrentDate(), nameList)
	// обновляем таблицу выбранных машин
	updateTableData(selectedTrucksGridOptions, tableData)

	if (isExist) snackbar.show(`Список ${nameList} уже существует!`)
	$('#addNewListModal').modal('hide')
}

// обработчик формы удаления текущего списка
function removeCurrentListSubmitFormHandler(e) {
	e.preventDefault()

	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const currentNameList = store.getCurrentNameList()
	if (!currentNameList) {
		snackbar.show('Не выбран ни один список!')
		return
	}

	const selectedTrucks = store.getTrucksByNameList(currentNameList)

	loadTruckList(selectedTrucks, null, 'toAll', true)
}

// обработчик удаления текущего списка
function deleteCurrentList() {
	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	setTimeout(() => {
		const nameList = store.getCurrentNameList()
		store.removeList(nameList)
		updateTruckListsOptions(store.getListsByCurrentDate(), '')
		snackbar.show(`Список ${nameList} удален!`)
		$('#removeCurrentListModal').modal('hide')
	}, 200);
}

// ДЕЙСТВИЯ ТЕКУЩЕГО ПОЛЬЗОВАТЕЛЯ функции для перемещения машин между таблицами
async function toSelectedList(e, button, params) {
	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const currentNameList = store.getCurrentNameList()
	if (!currentNameList) {
		snackbar.show('Не выбран ни один список!')
		return
	}
	const action = 'toSelect'
	const truck = params.node.data

	// блокируем кнопку после нажатия
	button.disabled = true
	store.setLastClickedBtn(button)
	// отправляем данные
	loadTruck(truck, currentNameList, action)
}
async function toAllList(e, button, params) {
	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const action = 'toAll'
	const truck = params.node.data
	// блокируем кнопку после нажатия
	button.disabled = true
	store.setLastClickedBtn(button)
	// отправляем данные
	loadTruck(truck, null, action)
}

// загрузка машины на сервер
/**
 * @param {{ idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>, }} truck
 * @param {string} nameList
 * @param {string} action
 */
function loadTruck(truck, nameList, action) {
	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 200)
	const truckData = getTruckDateForAjax([truck], nameList, action)

	ajaxUtils.postJSONdata({
		url: `../../api/logistics/deliveryShops/update`,
		token: store.getToken(),
		data: truckData[0],
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			store.getLastClickedBtn().disabled = false

			if (res.status === '200') {
				// ОБРАБОТКА ОТВЕТА ПРОИСХОДИТ ЧЕРЕЗ WS
				return
			}

			if (res.status === '100') {
				snackbar.show(res.info)
			}
		},
		errorCallback: () => {
			store.getLastClickedBtn().disabled = false
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// загрузка списка машин на сервер
/**
 * @param {Array<{ idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>, }>} trucks
 * @param {string} nameList
 * @param {string} action
 * @param {boolean} deleteList
 */
function loadTruckList(trucks, nameList, action, deleteList) {
	if (isObserver(store.getRole())) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 200)
	const truckData = getTruckDateForAjax(trucks, nameList, action)

	ajaxUtils.postJSONdata({
		url: `../../api/logistics/deliveryShops/updateList`,
		token: store.getToken(),
		data: truckData,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				// ОБРАБОТКА ОТВЕТА ПРОИСХОДИТ ЧЕРЕЗ WS

				// удаляем текущий список
				if (deleteList) deleteCurrentList()
				return
			}

			if (res.status === '100') {
				snackbar.show(res.info)
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

