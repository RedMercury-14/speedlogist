import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js"
import {
	changeGridTableMarginTop,
	cutToInteger,
	dateHelper,
	debounce,
	disableButton,
	enableButton,
	getAhoStatusRoute,
	getData,
	isCarrier,
	isObserver,
	isSlotsObserver,
	removeSingleQuotes
} from "./utils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from "./uiIcons.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { isInvalidPointForms } from "./procurementFormUtils.js"
import { ajaxUtils } from "./ajaxUtils.js"

const PAGE_NAME = 'maintenanceList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const ROW_INDEX_KEY = `AG_Grid_rowIndex_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value
const methodBase = isCarrier(role) ? 'carrier' : 'logistics'
const getAhoRouteBaseUrl = `../../api/${methodBase}/getMaintenanceList/`
const addCarrierBaseUrl = `../../api/logistics/maintenance/setCarrier/`
const clearCarrierBaseUrl = `../../api/logistics/maintenance/clearCarrier/`
const setMileageBaseUrl = `../../api/${methodBase}/maintenance/setMileage/`
const clearMileageBaseUrl = `../../api/${methodBase}/maintenance/clearMileage/`
const setFinishPriceBaseUrl = `../../api/logistics/maintenance/setCost/`
const clearFinishPriceBaseUrl = `../../api/logistics/maintenance/clearCost/`
const closeRouteBaseUrl = `../../api/logistics/maintenance/closeRoute/`
const editRHSUrl = `../../api/logistics/editRouteHasShop`

const getAllCarrierUrl = `../../api/manager/getAllCarrier`
const getTrucksByCarrierBaseUrl =`../../api/carrier/getCarByIdUser/`
const getDriverByCarrierBaseUrl =`../../api/carrier/getDriverByIdUser/`

export const rowClassRules = {
	'activRow': params => params.node.data.statusRoute === '200',
	'attentionRow': params => params.node.data.statusRoute === '220',
	'finishRow': params => params.node.data.statusRoute === '225',
	'endRow': params => params.node.data.statusRoute === '230',
}

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let table
let ahoRouteData
let allCarriers
let error
let pointsCounter = 0


const columnDefs = [
	{ headerName: 'ID', field: 'idRoute', minWidth: 60, width: 80, pinned: 'left',},
	{ headerName: 'Название маршрута', field: 'routeDirection', minWidth: 240, width: 240, wrapText: true, autoHeight: true, },
	{
		headerName: 'Дата загрузки', field: 'dateLoadPreviously',
		width: 120,
		comparator: dateComparator,
		valueFormatter: dateValueFormatter,
		filterParams: {
			valueFormatter: dateValueFormatter,
		},
	},
	{
		headerName: 'Время загрузки', field: 'timeLoadPreviously',
		width: 80,
		valueFormatter: timeValueFormatter,
		filterParams: {
			valueFormatter: timeValueFormatter,
		},
	},
	{
		headerName: 'Дата доставки', field: 'dateUnloadPreviouslyStock',
		width: 120,
		comparator: dateComparator,
		valueFormatter: dateValueFormatter,
		filterParams: {
			valueFormatter: dateValueFormatter,
		},
	},
	{
		headerName: 'Время доставки', field: 'timeUnloadPreviouslyStock',
		width: 80,
		valueFormatter: timeValueFormatter,
		filterParams: {
			valueFormatter: timeValueFormatter,
		},
	},
	{ headerName: 'Перевозчик', field: 'carrier', },
	// { headerName: 'Транспорт перевозчика', field: 'truckInfo',  wrapText: true, autoHeight: true, },
	{
		headerName: 'Статус', field: 'statusRoute',
		wrapText: true, autoHeight: true,
		valueFormatter: statusRouteValueFormatter,
		filterParams: {
			valueFormatter: statusRouteValueFormatter,
		},
	},
	{
		headerName: 'Пробег', field: 'kmInfo',
		valueFormatter: kmInfoValueFormatter,
		filterParams: {
			valueFormatter: kmInfoValueFormatter,
		},
	},
	{
		headerName: 'Стоимость перевозки', field: 'finishPrice',
		width: 120,
		valueFormatter: finishPriceFormatter,
		filterParams: {
			valueFormatter: finishPriceFormatter,
		},
	},
	{ headerName: 'Маршрут', field: 'addressInfo', wrapText: true, autoHeight: true, minWidth: 240, },
	{ headerName: 'Информация о грузе', field: 'cargoInfo', wrapText: true, autoHeight: true, },
	{ headerName: 'Информация о транспорте из заявки', field: 'needTruckInfo', wrapText: true, autoHeight: true, },
	{ headerName: 'Дополнительная информация', field: 'logistComment', wrapText: true, autoHeight: true, minWidth: 240, },
	{
		headerName: 'Комментарии заказчика, информация о точках маршрута', field: 'userComments',
		wrapText: true, autoHeight: true, minWidth: 240, width: 440,
		valueFormatter: userCommentsValueFormatter,
		filterParams: {
			valueFormatter: userCommentsValueFormatter,
		},
	},
	{ headerName: 'Инициатор заявки', field: 'customer', wrapText: true, autoHeight: true, width: 270, },
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2 text-center',
		width: 160,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	// номер маршрута используется как ID строки
	// в таблице для транзакций с изменениями
	getRowId: (params) => params.data.idRoute,
	// выделение строк и ячеек при изменении данных
	enableCellChangeFlash: true,

	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,

	suppressDragLeaveHidesColumns: true,
	getContextMenuItems: getContextMenuItems,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	sideBar: {
		toolPanels: [
			{
				id: 'columns',
				labelDefault: 'Columns',
				labelKey: 'columns',
				iconKey: 'columns',
				toolPanel: 'agColumnsToolPanel',
				toolPanelParams: {
					suppressRowGroups: true,
					suppressValues: true,
					suppressPivots: true,
					suppressPivotMode: true,
				},
			},
			{
				id: 'filters',
				labelDefault: 'Filters',
				labelKey: 'filters',
				iconKey: 'filter',
				toolPanel: 'agFiltersToolPanel',
			},
			{
				id: 'resetState',
				iconKey: 'menu',
				labelDefault: 'Сброс настроек',
				toolPanel: ResetStateToolPanel,
				toolPanelParams: {
					localStorageKey: LOCAL_STORAGE_KEY,
				},
			},
		],
	}
}


document.addEventListener('DOMContentLoaded', async () => {
	// изменение отступа для таблицы
	changeGridTableMarginTop()

	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	const routeSearchForm = document.querySelector('#routeSearchForm')
	const addCarrierForm = document.querySelector('#addCarrierForm')
	const addMileageForm = document.querySelector('#addMileageForm')
	const addFinishPriceForm = document.querySelector('#addFinishPriceForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const editPointsForm = document.querySelector('#editPointsForm')
	const pointList = editPointsForm.querySelector('#pointList')
	const addNewPointBtn = editPointsForm.querySelector('#addNewPoint')

	// автозаполнение полей дат в форме поиска заявок
	const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	routeSearchForm.addEventListener('submit', searchFormSubmitHandler)
	// обработчик формы назначения перевозчика на маршрут
	addCarrierForm.addEventListener('submit', addCarrierSubmitHandler)
	// обработчик формы установки пробега по маршруту
	addMileageForm.addEventListener('submit', addMileageSubmitHandler)
	// обработчик формы установки стоимости по маршруту
	addFinishPriceForm.addEventListener('submit', addFinishPriceSubmitHandler)
	// обработчик формы редактирования точек маршрута
	editPointsForm.addEventListener('submit', editPointsSubmitHandler)
	// обработчик нажатия на кнопку "+ Добавить точку"
	addNewPointBtn.addEventListener('click', addNewPointHandler)
	// очистка форм
	$('#addCarrierModal').on('hide.bs.modal', (e) => resetCarrierForm(e, addCarrierForm))
	$('#addMileageModal').on('hide.bs.modal', (e) => addMileageForm.reset())
	$('#addFinishPriceModal').on('hide.bs.modal', (e) => addFinishPriceForm.reset())
	$('#editPointsModal').on('hide.bs.modal', (e) => resetEditPointsForm(editPointsForm, pointList))

	// делаем контейнер с точками сортируемым с помощью перетаскивания
	Sortable.create(pointList, { handle: '.dragItem', animation: 150 })

	// добавляем перевозчиков в список
	const carrierSelect = document.querySelector('#carrier')
	const truckSelect = document.querySelector('#truck')
	const driverSelect = document.querySelector('#driver')

	// отображение стартовых данных
	if (window.initData) {
		await initStartData(carrierSelect, truckSelect, driverSelect)
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData(carrierSelect, truckSelect, driverSelect)
		})
	}
})


window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// установка стартовых данных
async function initStartData(carrierSelect, truckSelect, driverSelect) {
	ahoRouteData = window.initData.routes
	allCarriers = window.initData.carriers
	updateTable(gridOptions, ahoRouteData)
	await addCarriersToSelect(allCarriers, carrierSelect, truckSelect, driverSelect)
	window.initData = null

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()
}

// добавление перевозчиков в выпадающий список формы назначения перевозчика
async function addCarriersToSelect(carriers, carrierSelect, truckSelect, driverSelect) {
	carriers.forEach((carrier) => {
		const optionElement = document.createElement('option')
		optionElement.value = carrier.idUser
		optionElement.text = `${carrier.companyName} / ${carrier.numYNP}`
		carrierSelect.append(optionElement)
	})
	addSearchInSelectOptions(carrierSelect)

	// // загружаем список авто перевозчика
	// carrierSelect.addEventListener('change', async (e) => {
	// 	const idCarrier = e.target.value
	// 	await addTrucksToSelect(truckSelect, idCarrier)
	// 	await addDriverToSelect(driverSelect, idCarrier)
	// })
}

// добавление авто перевозчика в выпадающий список формы назначения перевозчика
async function addTrucksToSelect(truckSelect, idCarrier) {
	truckSelect.innerHTML = ''
	const trucks = await getData(getTrucksByCarrierBaseUrl + idCarrier)
	if (!trucks) return
	trucks.forEach((truck) => {
		const optionElement = document.createElement('option')
		optionElement.value = truck.idTruck
		const truckText = getTruckText(truck)
		optionElement.text = truckText
		truckSelect.append(optionElement)
	})
}
// добавление водителей перевозчика в выпадающий список формы назначения перевозчика
async function addDriverToSelect(driverSelect, idCarrier) {
	driverSelect.innerHTML = ''
	const drivers = await getData(getDriverByCarrierBaseUrl + idCarrier)
	console.log("🚀 ~ addDriverToSelect ~ drivers:", drivers)
	if (!drivers) return
	drivers.forEach((driver) => {
		const optionElement = document.createElement('option')
		optionElement.value = driver.idUser
		const driverText = getDriverText(driver)
		optionElement.text = driverText
		driverSelect.append(optionElement)
	})
}

// отображение модального окна назначения перевозчика
function showAddCarrierModal() {
	$('#addCarrierModal').modal('show')
}
function hideAddCarrierModal() {
	$('#addCarrierModal').modal('hide')
}
// отображение модального окна установки пробега
function showAddMileageModal() {
	$('#addMileageModal').modal('show')
}
function hideAddMileageModal() {
	$('#addMileageModal').modal('hide')
}
// отображение модального окна установки стоимости перевозки
function showAddFinishPriceModal() {
	$('#addFinishPriceModal').modal('show')
}
function hideAddFinishPriceModal() {
	$('#addFinishPriceModal').modal('hide')
}
// отображение модального окна редактирования точек маршрута
function showEditPointsModal() {
	$('#editPointsModal').modal('show')
}
function hideEditPointsModal() {
	$('#editPointsModal').modal('hide')
}

// поиск в списке селекта
function addSearchInSelectOptions(select) {
	const container = select.parentElement
	const input = container.querySelector('#searchInOptions')
	if (!input) return
	const searchItems = select.querySelectorAll('option')

	input.addEventListener('input', function (e) {
		const target = e.target
		const val = target.value.trim().toUpperCase()
		const fragment = document.createDocumentFragment()

		if (!target.classList.contains('keyboard__key')) return

		for (const elem of searchItems) {
			elem.remove()

			if (val === '' || elem.textContent.toUpperCase().includes(val)) {
				fragment.append(elem)
			}
		}

		select.append(fragment)
	})
}

// обработчик формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	updateTable(gridOptions)
}
// обработчик формы назначения перевозчика
async function addCarrierSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const idRoute = formData.get('idRoute')
	const idCarrier = formData.get('carrier')
	await setCarrier(idRoute, idCarrier)
}
// обработчик формы назначения перевозчика
async function addMileageSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const idRoute = formData.get('idRoute')
	const mileage = formData.get('mileage')
	await setMileage(idRoute, mileage)
}
// обработчик формы назначения перевозчика
async function addFinishPriceSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const idRoute = formData.get('idRoute')
	const finishPrice = formData.get('finishPrice')
	await setFinishPrice(idRoute, finishPrice)
}
// обработчик формы редактирования точек маршрута
function editPointsSubmitHandler(e) {
	e.preventDefault()
	const form = e.target
	// проверяем, заполнена ли предыдущая точка
	if (isInvalidPointForms(form)) return
	// получаем данные
	const formData = new FormData(form)
	const idRoute = formData.get('idRoute')
	const pointsData = getPointsData(form)
	// валидация данных о точках
	if (!isValidPointsData(pointsData)) return
	// отправляем данные на сервер
	setEditedRHS(e, {
		idRoute,
		routeHasShops: pointsData
	})
}
// обработчик нажатия на кнопку "+ Добавить точку"
function addNewPointHandler(e) {
	const pointList = document.querySelector('#pointList')
	createPoint(pointList, null, pointsCounter)
	e.target.blur()
}
// очистка формы назначения превозчика
function resetCarrierForm(e, form) {
	form.reset()
	const input = e.target.querySelector('#searchInOptions')
	input.value = ''
	const inputEvent = new Event('input')
	input.dispatchEvent(inputEvent)
}
// очистка формы редактирования точек маршрута
function resetEditPointsForm(editPointsForm, pointList) {
	pointList.innerHTML = ''
	editPointsForm.reset()
	pointsCounter = 0
}

// -------------------------------------------------------------------------------//
// ----------------------- Функции для таблицы AG-Grid ---------------------------//
// -------------------------------------------------------------------------------//

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}

async function updateTable(gridOptions, data) {
	gridOptions.api.showLoadingOverlay()

	const routeSearchForm = document.querySelector('#routeSearchForm')
	const dateStart = routeSearchForm.date_from.value
	const dateEnd = routeSearchForm.date_to.value

	const res = data
		? { body: data }
		: await getData(`${getAhoRouteBaseUrl}${dateStart}&${dateEnd}`)

	ahoRouteData = res.body

	if (!ahoRouteData || !ahoRouteData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(ahoRouteData)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

function getMappingData(data) {
	return data.map(route => {
		const cargoInfo = getCargoInfo(route)
		const needTruckInfo = getNeedTruckInfo(route)
		const addressInfo = getAddressesInfo(route)
		const carrier = getCarrier(route)
		const truckInfo = getTruckInfo(route)
		return {
			...route,
			carrier,
			needTruckInfo,
			cargoInfo,
			addressInfo,
			truckInfo,
		}
	})
}

function getContextMenuItems(params) {
	if (!params.node) return
	const routeData = params.node.data
	const idRoute = routeData.idRoute
	const status = routeData.statusRoute

	const isAddCarrierDisabled = status !== '200' && status !== '210'
	const isRemoveCarrierDisabled = status !== '210'
	const isAddMileageDisabled = status !== '210' && status !== '220'
	const isCarrierAddMileageDisabled = status !== '210'
	const isRemoveMileageDisabled = status !== '220'
	const isAddFinishPriceDisabled = status !== '220' && status !== '225'
	const isRemoveFinishPriceDisabled = status !== '225'
	const isCloseRouteDisabled = status !== '225'

	if (isSlotsObserver(role)) return []

	const logistResult = [
		{
			disabled: isAddCarrierDisabled || isObserver(role),
			name: `Назначить перевозчика`,
			icon: uiIcons.personPlus,
			action: () => {
				addCarrier(routeData)
			},
		},
		{
			disabled: isRemoveCarrierDisabled || isObserver(role),
			name: `Удалить перевозчика`,
			icon: uiIcons.trash,
			action: () => {
				removeCarrier(idRoute)
			},
		},
		{
			disabled: isAddMileageDisabled || isObserver(role),
			name: `Добавить пробег`,
			icon: uiIcons.route2,
			action: () => {
				addMileage(routeData)
			},
		},
		{
			disabled: isRemoveMileageDisabled || isObserver(role),
			name: `Удалить пробег`,
			icon: uiIcons.eraser,
			action: () => {
				removeMileage(idRoute)
			},
		},
		{
			disabled: isAddFinishPriceDisabled || isObserver(role),
			name: `Указать стоимость перевозки`,
			icon: uiIcons.banknotes,
			action: () => {
				addFinishPrice(routeData)
			},
		},
		{
			disabled: isRemoveFinishPriceDisabled || isObserver(role),
			name: `Удалить стоимость`,
			icon: uiIcons.banknotesRemoved,
			action: () => {
				removeFinishPrice(idRoute)
			},
		},
		{
			disabled: isCloseRouteDisabled || isObserver(role),
			name: `Завершить маршрут`,
			icon: uiIcons.checks,
			action: () => {
				closeRoute(idRoute)
			},
		},
		"separator",
		{
			name: `Редактировать точки маршрута`,
			disabled: isObserver(role) || status === '200', // определить доступ к методу
			icon: uiIcons.pencil,
			action: () => {
				editPoints(routeData)
			},
		},
		"separator",
		"excelExport",
	]

	const carierResult = [
		// {
		// 	disabled: isCarrierAddMileageDisabled,
		// 	name: `Добавить пробег`,
		// 	icon: uiIcons.route2,
		// 	action: () => {
		// 		addMileage(routeData)
		// 	},
		// }
	]

	return isCarrier(role) ? carierResult : logistResult
}

// обработчики нажатий кнопок контекстного меню
async function addCarrier(routeData) {
	const idCarrier = routeData.user ? routeData.user.idUser : ''
	const addCarrierForm = document.querySelector('#addCarrierForm')
	addCarrierForm.idRoute.value = routeData.idRoute
	addCarrierForm.routeDirection.value = routeData.routeDirection
	addCarrierForm.carrier.value = idCarrier
	
	// if (idCarrier) {
	// 	await addTrucksToSelect(addCarrierForm.truck, idCarrier)
	// 	const idTruck = routeData.truck ? routeData.truck.idTruck : ''
	// 	addCarrierForm.truck.value = idTruck

	// 	await addDriverToSelect(addCarrierForm.driver, idCarrier)
	// 	const idDriver = routeData.driver ? routeData.driver.idUser : ''
	// 	addCarrierForm.driver.value = idDriver
	// }

	showAddCarrierModal()
}
function addMileage(routeData) {
	const mileage = routeData.kmInfo ? routeData.kmInfo : ''
	const addMileageForm = document.querySelector('#addMileageForm')
	addMileageForm.idRoute.value = routeData.idRoute
	addMileageForm.routeDirection.value = routeData.routeDirection
	addMileageForm.mileage.value = mileage
	showAddMileageModal()
}
function addFinishPrice(routeData) {
	const finishPrice = routeData.finishPrice ? routeData.finishPrice : ''
	const addFinishPriceForm = document.querySelector('#addFinishPriceForm')
	addFinishPriceForm.idRoute.value = routeData.idRoute
	addFinishPriceForm.routeDirection.value = routeData.routeDirection
	addFinishPriceForm.finishPrice.value = finishPrice
	showAddFinishPriceModal()
}
function editPoints(routeData) {
	const editPointsForm = document.querySelector('#editPointsForm')
	const pointList = editPointsForm.querySelector('#pointList')
	const editPointsModalLabel = document.querySelector('#editPointsModalLabel')
	const idRoute = routeData.idRoute
	const points = routeData.roteHasShop
	editPointsForm.idRoute.value = idRoute
	editPointsModalLabel.innerText = `Редактирование точек маршрута №${idRoute}`
	points.forEach((point, i) => createPoint(pointList, point, i))
	showEditPointsModal()
}

// функция обновления данных ячейки таблицы
function updateCellData(id, columnName, newValue) {
	const rowNode = gridOptions.api.getRowNode(id)
	rowNode.setDataValue(columnName, newValue)
}

// выделение ("мигание") строки с изменениями
function highlightRow(rowNode) {
	gridOptions.api.flashCells({ rowNodes: [rowNode] })
}

// форматирование ячейки со статусом маршрута
function statusRouteValueFormatter(params) {
	const status = params.value
	return getAhoStatusRoute(status)
}
// форматирование ячейки спробегом по маршруту
function kmInfoValueFormatter(params) {
	return params.value ? `${params.value} км` : ''
}
// форматирование ячейки с датой загрузки/выгрузки
function dateValueFormatter(params) {
	return params.value ? dateHelper.changeFormatToView(params.value) : ''
}
// форматирование ячейки со временем загрузки/выгрузки
function timeValueFormatter(params) {
	return params.value ? params.value.slice(0,5) : ''
}
// форматирование ячейки с ценой
function finishPriceFormatter(params) {
	return params.value ? `${params.value} BYN` : ''
}
// форматирование ячейки с комментарием
function userCommentsValueFormatter(params) {
	return params.value ? params.value.replace(/null/gi, '') : ''
}

// функции управления состоянием колонок
function saveColumnState() {
	gridColumnLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreColumnState() {
	gridColumnLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}


// функции получения данных для таблицы
function getAddressesInfo(route) {
	if (!route) return ''
	const rhs = route.roteHasShop
	if (!rhs) return ''
	if (!rhs.length) return ''

	return rhs
		.reduce((acc, item) => {
			const index = item.order ? item.order : ''
			const type = item.position ? item.position : ''
			const address = item.address ? item.address : ''
			acc.push(`${index}) ${type}: ${address}`)
			return acc
		}, [])
		.join(' ● ')
}
function getCargoInfo(route) {
	if (!route) return ''
	const rhsItem = route.roteHasShop[0]
	if (!rhsItem) return ''
	const cargo = rhsItem.cargo ? rhsItem.cargo : ''
	const pall = route.totalLoadPall ? `${route.totalLoadPall} палл` : ''
	const weight = route.totalCargoWeight ? `${route.totalCargoWeight} кг` : ''
	return [ cargo, pall, weight ].filter(item => item).join(' ● ')
}
function getNeedTruckInfo(route) {
	if (!route) return ''
	const typeTrailer = route.typeTrailer ? route.typeTrailer : ''
	const typeLoad = route.typeLoad ? route.typeLoad : ''
	const methodLoad = route.methodLoad ? route.methodLoad : ''
	return [ typeTrailer, typeLoad, methodLoad ].filter(item => item).join(' ● ')
}
function getCarrier(route) {
	if (!route) return ''
	const user = route.user ? route.user : ''
	if (!user) return ''
	return user.companyName ? user.companyName : ''
}
function getTruckInfo(route) {
	if (!route) return ''
	const truck = route.truck ? route.truck : ''
	return getTruckText(truck)
}
function getTruckText(truck) {
	if (!truck) return ''
	const truckText = []
	const numTruck = truck.numTruck ? truck.numTruck : ''
	const numTrailer = truck.numTrailer ? truck.numTrailer : ''
	const typeTrailer = truck.typeTrailer ? truck.typeTrailer : ''
	const cargoCapacity = truck.cargoCapacity ? `${truck.cargoCapacity} кг` : ''
	const pallCapacity = truck.pallCapacity ? `${truck.pallCapacity} палл` : ''
	truckText.push(numTruck, numTrailer, typeTrailer, cargoCapacity, pallCapacity)
	return truckText.filter(Boolean).join(' / ')
}
function getDriverText(driver) {
	if (!driver) return ''
	const name = driver.name ? driver.name : ''
	const surname = driver.surname ? driver.surname : ''
	const patronymic = driver.patronymic ? driver.patronymic : ''
	return `${surname} ${name} ${patronymic}`
}


// методы изменения данных на сервере
async function setCarrier(idRoute, idCarrier) {
	if (isCarrier(role)) return
	const url = `${addCarrierBaseUrl}${idRoute}&${idCarrier}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)
	
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
		hideAddCarrierModal()
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function removeCarrier(idRoute) {
	if (isCarrier(role)) return
	const url = `${clearCarrierBaseUrl}${idRoute}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
		hideAddCarrierModal()
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function setMileage(idRoute, mileage) {
	const url = `${setMileageBaseUrl}${idRoute}&${mileage}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
		hideAddMileageModal()
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function removeMileage(idRoute) {
	if (isCarrier(role)) return
	const url = `${clearMileageBaseUrl}${idRoute}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function setFinishPrice(idRoute, finishPrice) {
	if (isCarrier(role)) return
	const url = `${setFinishPriceBaseUrl}${idRoute}&${finishPrice}&BYN`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
		hideAddFinishPriceModal()
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function removeFinishPrice(idRoute) {
	if (isCarrier(role)) return
	const url = `${clearFinishPriceBaseUrl}${idRoute}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function closeRoute(idRoute) {
	if (isCarrier(role)) return
	const url = `${closeRouteBaseUrl}${idRoute}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()

	if (res && res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable(gridOptions)
		hideAddCarrierModal()
	} else {
		console.log(res)
		const message = res && res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
function setEditedRHS(submitEvent, data) {
	const submitter = submitEvent.submitter
	bootstrap5overlay.showOverlay()
	disableButton(submitter)

	ajaxUtils.postJSONdata({
		url: editRHSUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			enableButton(submitter)
			bootstrap5overlay.hideOverlay()

			if (res && res.status === '200') {
				snackbar.show('Выполнено!')
				updateTable(gridOptions)
				hideEditPointsModal()
			} else {
				console.log(res)
				const message = res && res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
			}
		},
		errorCallback: () => {
			enableButton(submitter)
			bootstrap5overlay.hideOverlay()
		}
	})
}


// создание точки маршрута
function createPoint(pointList, pointsData, index) {
	const pointElement = getPointElement(pointsData, index)
	pointList.append(pointElement)
	pointsCounter++
}
// получение HTML точки
function getPointElement(pointsData, index) {
	const point = document.createElement('div')

	const idRouteHasShop = pointsData && pointsData.idRouteHasShop ? pointsData.idRouteHasShop : ''
	const pointIndex = index + 1
	const cargoInfoHTML = getCargoInfoHTML(pointsData, pointIndex)
	const addressHTML = getAddressHTML(pointsData, pointIndex)

	point.className = 'card point mb-3 border-secondary'
	point.innerHTML = `
		<form class='pointForm' id='pointform_${pointIndex}' name='pointform_${pointIndex}' action=''>
			<div class='card-header d-flex justify-content-between dragItem'>
				<h5 class='d-flex align-items-center mb-0'>Точка маршрута</h5>
				<span class="h3 m-0 dragText">Зажмите здесь и перетаскивайте</span>
				<button type="button" class="btn btn-outline-danger deleteBtn" title="Удалить точку маршрута">
					<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
						<path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"></path>
						<path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"></path>
					</svg>
				</button>
			</div>
			<input type='hidden' id='idRouteHasShop_${pointIndex}' name='idRouteHasShop' value='${idRouteHasShop}'>
			<div class='card-body py-2'>
				${addressHTML}
				<div class='form-row'>
					${cargoInfoHTML}
				</div>
			</div>
		</form>`

	// удаление точки
	const deleteBtn = point.querySelector('.deleteBtn')
	deleteBtn.addEventListener('click', (e) => deletePoint(e, point))

	return point
}
// HTML информации о грузе
function getCargoInfoHTML(pointsData, pointIndex) {
	 const pointCargo = pointsData && pointsData.cargo ? pointsData.cargo : ''
	 const pall = pointsData && pointsData.pall ? pointsData.pall : ''
	 const weight = pointsData && pointsData.weight ? pointsData.weight : ''
	 const volume = pointsData && pointsData.volume ? pointsData.volume : ''

	return `<div class='form-group col-md-6'>
				<label for='pointCargo_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Наименование груза</label>
				<input type='text' class='form-control' name='pointCargo' id='pointCargo_${pointIndex}' placeholder='Наименование' value='${pointCargo}' required>
			</div>
			<div class='form-group col-md-2'>
				<label for='pall_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Паллеты, шт</label>
				<input type='number' class='form-control' name='pall' id='pall_${pointIndex}' placeholder='Паллеты, шт' min='0' max='20' step="1" value='${pall}'>
			</div>
			<div class='form-group col-md-2'>
				<label for='weight_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Масса, кг</label>
				<input type='number' class='form-control' name='weight' id='weight_${pointIndex}' placeholder='Масса, кг' min='0' max='99999' step="1" value='${weight}'>
			</div>
			<div class='form-group col-md-2'>
				<label for='volume_${pointIndex}' class='col-form-label text-muted font-weight-bold'>Объем, м.куб.</label>
				<input type='number' class='form-control' name='volume' id='volume_${pointIndex}' placeholder='Объем, м.куб.' min='0' max='99' step='0.1' value='${volume}'>
			</div>`
}
// HTML информации об адресе
function getAddressHTML(pointsData, pointIndex) {
	const addressValue = pointsData && pointsData.address ? pointsData.address : ''
	const pointType = pointsData && pointsData.position ? pointsData.position : ''
	const country = 'BY Беларусь'
	const address = addressValue ? addressValue.split('; ')[1] : ''
	const isLabelSelected = !pointType ? 'selected' : ''
	const isLoadSelected = pointType === 'Загрузка' ? 'selected' : ''
	const isUnloadSelected = pointType === 'Выгрузка'  ? 'selected' : ''
	return `<div class="form-row">
				<div class="col col-md-2">
				<label for="country_${pointIndex}" class="col-form-label text-muted font-weight-bold">Тип точки</label>
					<select class='form-control' name='position' id='position_${pointIndex}' value='${pointType}' required>
						<option ${isLabelSelected} disabled value=''>Выберите тип</option>
						<option ${isLoadSelected} value='Загрузка'>Загрузка</option>
						<option ${isUnloadSelected} value='Выгрузка'>Выгрузка</option>
					</select>
				</div>
				<div class="col col-md-2">
					<label for="country_${pointIndex}" class="col-form-label text-muted font-weight-bold">Страна</label>
					<input type="text" class="form-control" name="country" id="country_${pointIndex}" placeholder="Страна" autocomplete="off" value='${country}' readonly>
				</div>
				<div class="col">
					<label for="country_${pointIndex}" class="col-form-label text-muted font-weight-bold">Адрес</label>
					<input type="text" class="form-control" name="address" id="address_${pointIndex}" autocomplete="off" placeholder="Город, улица и т.д." value='${address}' required>
				</div>
			</div>`
}
// удаление точки маршрута
function deletePoint(e, point) {
	const res = confirm('Подтвердите удаление точки маршрута')
	if (res) point.remove()
	else e.target.blur()
}
// получение данных точек маршрута
function getPointsData(form) {
	const points = []
	const pointForms = form.querySelectorAll('.pointForm')
	pointForms.forEach((pointForm, i) => {
		const address = `${pointForm.country.value}; ${pointForm.address.value.replace(/;/g, ',')}`
		points.push({
			address: removeSingleQuotes(address),
			cargo: pointForm.pointCargo ? removeSingleQuotes(pointForm.pointCargo.value) : null,
			idRouteHasShop: pointForm.idRouteHasShop.value ? Number(pointForm.idRouteHasShop.value) : null,
			order: i + 1,
			pall: pointForm.pall.value ? pointForm.pall.value : null,
			position: pointForm.position.value,
			volume: pointForm.volume.value ? pointForm.volume.value : null,
			weight: pointForm.weight.value ? cutToInteger(pointForm.weight.value) : null,
		})
	})

	return points
}
// валидация данных точек маршрута
function isValidPointsData(pointsData) {
	if (pointsData.length < 1) {
		snackbar.show('Необходимо добавить точки загрузки и выгрузки!')
		return false
	}

	if (!pointsData.find(point => point.position === 'Загрузка')) {
		snackbar.show('Необходимо добавить точку загрузки!')
		return false
	}

	if (!pointsData.find(point => point.position === 'Выгрузка')) {
		snackbar.show('Необходимо добавить точку выгрузки!')
		return false
	}

	return true
}
