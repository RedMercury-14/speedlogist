import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js"
import { changeGridTableMarginTop, dateHelper, debounce, getAhoStatusRoute, getData, isCarrier } from "./utils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from "./uiIcons.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"

const PAGE_NAME = 'maintenanceList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const ROW_INDEX_KEY = `AG_Grid_rowIndex_to_${PAGE_NAME}`

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
let error


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
	{ headerName: 'Информация о транспорте', field: 'truckInfo', wrapText: true, autoHeight: true, },
	{ headerName: 'Дополнительная информация', field: 'logistComment', wrapText: true, autoHeight: true, minWidth: 240, },
	{
		headerName: 'Комментарии заказчика, информация о точках маршрута', field: 'userComments',
		wrapText: true, autoHeight: true, minWidth: 240, width: 440,
		valueFormatter: userCommentsValueFormatter,
		filterParams: {
			valueFormatter: userCommentsValueFormatter,
		},
	},
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


window.addEventListener("load", async () => {
	// изменение отступа для таблицы
	changeGridTableMarginTop()

	const routeSearchForm = document.querySelector('#routeSearchForm')
	const addCarrierForm = document.querySelector('#addCarrierForm')
	const addMileageForm = document.querySelector('#addMileageForm')
	const addFinishPriceForm = document.querySelector('#addFinishPriceForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)

	// автозаполнение полей дат в форме поиска заявок
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	routeSearchForm.addEventListener('submit', searchFormSubmitHandler)

	const res = await getData(`${getAhoRouteBaseUrl}${dateStart}&${dateEnd}`)
	ahoRouteData = res.body

	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	await renderTable(gridDiv, gridOptions, ahoRouteData)

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	// обработчик формы назначения перевозчика на маршрут
	addCarrierForm.addEventListener('submit', addCarrierSubmitHandler)
	// обработчик формы установки пробега по маршруту
	addMileageForm.addEventListener('submit', addMileageSubmitHandler)
	// обработчик формы установки стоимости по маршруту
	addFinishPriceForm.addEventListener('submit', addFinishPriceSubmitHandler)
	// очистка форм
	$('#addCarrierModal').on('hide.bs.modal', (e) => resetCarrierForm(e, addCarrierForm))
	$('#addMileageModal').on('hide.bs.modal', (e) => addMileageForm.reset())
	$('#addFinishPriceModal').on('hide.bs.modal', (e) => addFinishPriceForm.reset())

	// добавляем перевозчиков в список
	await addCarriersToSelect()
})


window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// добавление перевозчиков в выпадающий список формы назначения перевозчика
async function addCarriersToSelect() {
	const getAllCarrierUrl = `../../api/manager/getAllCarrier`
	const carriers = await getData(getAllCarrierUrl)
	const carrierSelect = document.querySelector('#carrier')
	carriers.forEach((carrier) => {
		const optionElement = document.createElement('option')
		optionElement.value = carrier.idUser
		optionElement.text = carrier.companyName
		carrierSelect.append(optionElement)
	})
	addSearchInSelectOptions(carrierSelect)
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
	updateTable()
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
// очищение формы назначения превозчика
function resetCarrierForm(e, form) {
	form.reset()
	const input = e.target.querySelector('#searchInOptions')
	input.value = ''
	const inputEvent = new Event('input')
	input.dispatchEvent(inputEvent)
}

// -------------------------------------------------------------------------------//
// ----------------------- Функции для таблицы AG-Grid ---------------------------//
// -------------------------------------------------------------------------------//

async function renderTable(gridDiv, gridOptions, data) {
	table = new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = await getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const routeSearchForm = document.querySelector('#routeSearchForm')

	const dateStart = routeSearchForm.date_from.value
	const dateEnd = routeSearchForm.date_to.value

	const res = await getData(`${getAhoRouteBaseUrl}${dateStart}&${dateEnd}`)
	ahoRouteData = res.body

	if (!ahoRouteData || !ahoRouteData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = await getMappingData(ahoRouteData)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function getMappingData(data) {
	return data.map(route => {
		const cargoInfo = getCargoInfo(route)
		const truckInfo = getTruckInfo(route)
		const addressInfo = getAddressesInfo(route)
		const carrier = getCarrier(route)
		return {
			...route,
			carrier,
			truckInfo,
			cargoInfo,
			addressInfo,
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

	const logistResult = [
		{
			disabled: isAddCarrierDisabled,
			name: `Назначить перевозчика`,
			icon: uiIcons.personPlus,
			action: () => {
				addCarrier(routeData)
			},
		},
		{
			disabled: isRemoveCarrierDisabled,
			name: `Удалить перевозчика`,
			icon: uiIcons.trash,
			action: () => {
				removeCarrier(idRoute)
			},
		},
		{
			disabled: isAddMileageDisabled,
			name: `Добавить пробег`,
			icon: uiIcons.route2,
			action: () => {
				addMileage(routeData)
			},
		},
		{
			disabled: isRemoveMileageDisabled,
			name: `Удалить пробег`,
			icon: uiIcons.eraser,
			action: () => {
				removeMileage(idRoute)
			},
		},
		{
			disabled: isAddFinishPriceDisabled,
			name: `Указать стоимость перевозки`,
			icon: uiIcons.banknotes,
			action: () => {
				addFinishPrice(routeData)
			},
		},
		{
			disabled: isRemoveFinishPriceDisabled,
			name: `Удалить стоимость`,
			icon: uiIcons.banknotesRemoved,
			action: () => {
				removeFinishPrice(idRoute)
			},
		},
		{
			disabled: isCloseRouteDisabled,
			name: `Завершить маршрут`,
			icon: uiIcons.checks,
			action: () => {
				closeRoute(idRoute)
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
function addCarrier(routeData) {
	const idCarrier = routeData.user ? routeData.user.idUser : ''
	const addCarrierForm = document.querySelector('#addCarrierForm')
	addCarrierForm.idRoute.value = routeData.idRoute
	addCarrierForm.routeDirection.value = routeData.routeDirection
	addCarrierForm.carrier.value = idCarrier
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
function getTruckInfo(route) {
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


// методы изменения данных на сервере
async function setCarrier(idRoute, idCarrier) {
	if (isCarrier(role)) return
	const url = `${addCarrierBaseUrl}${idRoute}&${idCarrier}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)
	
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
		hideAddCarrierModal()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
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
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
		hideAddCarrierModal()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}
async function setMileage(idRoute, mileage) {
	const url = `${setMileageBaseUrl}${idRoute}&${mileage}`
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	const res = await getData(url)
	
	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
		hideAddMileageModal()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
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
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
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
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
		hideAddFinishPriceModal()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
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
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
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
	
	if (res.status === '200') {
		snackbar.show('Выполнено!')
		updateTable()
		hideAddCarrierModal()
	} else {
		console.log(res)
		const message = res.message ? res.message : 'Неизвестная ошибка'
		snackbar.show(message)
	}
}