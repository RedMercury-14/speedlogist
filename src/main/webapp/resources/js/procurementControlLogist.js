import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js'
import { debounce, getData, dateHelper, getStatus, changeGridTableMarginTop, rowClassRules, disableButton, enableButton } from './utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { ajaxUtils } from './ajaxUtils.js'
import { uiIcons } from './uiIcons.js'
import {
	changeCargoInfoInputsRequired,
	changeTemperatureInputRequired,
	changeTnvdInputRequired,
	showIncotermsInput,
} from "./procurementFormUtils.js"
import { excelStyles, getPointToView, getRouteInfo, pointSorting, procurementExcelExportParams } from './procurementControlUtils.js'
import { autocomplete } from './autocomplete/autocomplete.js'
import { countries } from './global.js'

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'ProcurementControlLogist'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`
const getOrderBaseUrl ='../../api/manager/getOrdersForLogist/'
const getSearchOrderBaseUrl ='../../api/manager/getOrdersHasCounterparty/'
const createRouteUrl ='../../api/manager/createNewRoute'
const getDataHasOrderBaseUrl ='../../api/manager/getDataHasOrder2/'

let hasConfirmRouteDataResponse = true

let map

// -----------------------------Leafleat map--------------------------------------//
// конфигурация какрты
const config = {
	minZoom: 6,
	maxZoom: 18,
	zoomControl: false
}
// начальные координаты и масштаб карты
const zoom = 11;
const lat = 53.875;
const lng = 27.415;

// создание карты
function createMap() {
	if (map) return

	map = L.map("map", config).setView([lat, lng], zoom)
	L.tileLayer("http://{s}.tile.osm.org/{z}/{x}/{y}.png").addTo(map)
	L.control.zoom({ position: 'topright' }).addTo(map)
}

function getAddresses(routeForm) {
	const addresses = []
	const pointForms = routeForm.querySelectorAll('.pointForm')

	pointForms.forEach(form => {
		const address = form.pointAddress.value
			.split('; ')
			.slice(1)
			.join('; ')

			addresses.push(address)
	})

	return addresses
}

// -----------------------------Leafleat map--------------------------------------//

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const columnDefs = [
	{
		field: '', colId: 'selectionRow',
		width: 30,
		pinned: 'left', lockPinned: true,
		checkboxSelection: true,
		suppressMovable: true, suppressMenu: true,
		resizable: false, sortable: false, filter: false,
	},
	{ 
		headerName: 'ID заявки', field: 'idOrder', colId: 'idOrder',
		cellClass: 'group-cell', pinned: 'left', width: 100,
		cellRenderer: 'agGroupCellRenderer',
		cellRendererParams: {
			innerRenderer: orderLinkRenderer,
		},
	},
	{ 
		headerName: 'Наименование контрагента', field: 'counterparty', colId: 'counterparty',
		pinned: 'left', width: 240, wrapText: true, autoHeight: true,
	},
	{ headerName: 'Дата создания заявки', field: 'dateCreateToView', comparator: dateComparator, },
	{ headerName: 'Дата загрузки (первая)', field: 'loadDateToView', comparator: dateComparator, },
	{ headerName: 'Дата выгрузки (последняя)', field: 'unloadDateToView', comparator: dateComparator, },
	// { headerName: 'Дата и время выгрузки', field: 'unloadWindowToView', width: 200, },
	// { headerName: 'Продолжительность выгрузки', field: 'onloadTime', width: 200, },
	{ headerName: 'Тип маршрута', field: 'way', },
	{ headerName: 'Номер из Маркета', field: 'marketNumber', },
	{ headerName: 'Погрузочный номер', field: 'loadNumber', },
	{ headerName: 'Условия поставки', field: 'incoterms', wrapText: true, autoHeight: true, },
	{ headerName: 'Точки загрузки', field: 'loadPoints', wrapText: true, autoHeight: true, },
	{ headerName: 'Точки выгрузки', field: 'unloadPoints', wrapText: true, autoHeight: true, },
	{ headerName: 'Тип кузова', field: 'typeTruck', },
	{ headerName: 'Паллеты', field: 'summPall', width: 100, },
	{
		headerName: 'Объем', field: 'summVolume', width: 100,
		valueFormatter: (params) => params.value && `${params.value} м.куб.`
	},
	{
		headerName: 'Масса', field: 'summWeight', width: 100,
		valueFormatter: (params) => params.value && `${params.value} кг`
	},
	{ 
		headerName: 'Комментарии', field: 'comment', 
		width: 240, cellClass: 'font-weight-bold more-text',
		wrapText: true, autoHeight: true,
	},
	{
		headerName: 'Статус', field: 'statusToView',
		wrapText: true, autoHeight: true,
	},
	{ headerName: 'Маршруты', field: 'routeInfo', wrapText: true, autoHeight: true,},
	{ headerName: 'Логист', field: 'logistToView', },
	{ headerName: 'Контактное лицо контрагента', field: 'contact', wrapText: true, autoHeight: true, },
	{ headerName: 'Сверка УКЗ', field: 'controlToView', width: 100, },
	{ headerName: 'Менеджер', field: 'manager', wrapText: true, autoHeight: true, },
	{ headerName: 'Телефон менеджера', field: 'telephoneManager', wrapText: true, autoHeight: true, },
	{ headerName: 'Тип загрузки авто', field: 'typeLoad', },
	{ headerName: 'Способ загрузки авто', field: 'methodLoad', },
	{ headerName: 'Груз', field: 'cargo', },
	{ headerName: 'Температурные условия', field: 'temperature', },
	{ headerName: 'Штабелирование', field: 'stackingToView', },
	{ headerName: 'Склад доставки (из Маркета)', field: 'numStockDelivery', },
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
	},
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	rowSelection: 'multiple',
	suppressRowClickSelection: true,
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
	},
	masterDetail: true,
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: {
			columnDefs: [
				{ headerName: "№", field: 'pointNumber', flex: 1, },
				{ headerName: "Тип точки", field: 'type', },
				{ headerName: "Дата", field: 'dateToView', },
				{
					headerName: "Время", field: 'time',
					valueFormatter: (params) => params.value && `${params.value.slice(0, 5)}`
				},
				{ headerName: "Коды ТН ВЭД", field: 'tnvd', flex: 6, },
				{ headerName: "Информация о грузе", field: 'info', tooltipField: 'info', flex: 8, },
				{ headerName: "Адрес склада", field: 'bodyAddress', flex: 8, },
				{ headerName: "Время работы", field: 'timeFrame', },
				{ headerName: "Контактное лицо", field: 'contact', flex: 6, },
				{ headerName: "Адрес таможни", field: 'customsAddress', flex: 8, },
			],
			defaultColDef: {
				headerClass: 'px-1',
				cellClass: 'px-2',
				wrapText: true,
				autoHeight: true,
				resizable: true,
				flex: 4,
				suppressMenu: true,
				wrapHeaderText: true,
				autoHeaderHeight: true,
			},
			enableBrowserTooltips: true,
			localeText: AG_GRID_LOCALE_RU,
		},
		getDetailRowData: (params) => {
			params.successCallback(params.data.addressesToView);
		},
	},
	defaultExcelExportParams: procurementExcelExportParams,
	excelStyles: excelStyles,
}

window.onload = async () => {
	const orderSearchForm = document.querySelector('#orderSearchForm')
	const routeForm = document.querySelector('#routeForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const gridDiv = document.querySelector('#myGrid')
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	const wayInput = document.querySelector('#way')

	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
	const orders = await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	// отрисовка таблицы
	renderTable(gridDiv, gridOptions, orders)

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()

	// автозаполнение полей дат в форме поиска заявок
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit', async (e) => searchFormSubmitHandler(e))

	// листнер на отправку формы создания маршрута
	routeForm.addEventListener('submit', routeFormSubmitHandler)

	// листнер на изменение способа загрузки
	methodLoadInput.addEventListener('change', (e) => {
		const points = document.querySelectorAll('.point')
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа кузова
	typeTruckInput.addEventListener('change', (e) => {
		const points = document.querySelectorAll('.point')
		const typeTruck = e.target.value
		changeTemperatureInputRequired(typeTruck)
		showIncotermsInput(typeTruck)
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа маршрута
	wayInput.addEventListener('change', (e) => changeTnvdInputRequired(e))

	// листнер на отрисовку карты при открытии модального окна
	// $('#routeModal').on('show.bs.modal', (e) => setTimeout(() => createMap(), 200))
	// листнер на очистку формы маршрута при закрытии модального окна
	$('#routeModal').on('hide.bs.modal', (e) => clearRouteForm())

	// делаем контейнер с точками сортируемым с помощью перетаскивания
	Sortable.create(pointList, {
		handle: '.dragItem',
		animation: 150
	})
}

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData.sort((a,b) => b.idOrder - a.idOrder))
	gridOptions.api.hideOverlay()
}
async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const orderSearchForm = document.querySelector('#orderSearchForm')

	const dateStart = orderSearchForm.date_from.value
	const dateEnd = orderSearchForm.date_to.value
	const counterparty = orderSearchForm.searchName.value

	const orders = counterparty.length
		? await getData(`${getSearchOrderBaseUrl}${dateStart}&${dateEnd}&${counterparty}`)
		: await getData(`${getOrderBaseUrl}${dateStart}&${dateEnd}`)

	if (!orders || !orders.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(orders)
	const sortedData = mappingData.sort((a,b) => b.idOrder - a.idOrder)

	gridOptions.api.setRowData(sortedData)
	gridOptions.api.hideOverlay()
}

function getMappingData(data) {
	return data.map(order => {
		const dateCreateToView = dateHelper.getFormatDate(order.dateCreate)
		const dateDeliveryToView = dateHelper.getFormatDate(order.dateDelivery)
		const controlToView = order.control ? 'Да' : 'Нет'
		const telephoneManagerToView = order.telephoneManager ? `; тел. ${order.telephoneManager}` : ''
		const managerToView = `${order.manager}${telephoneManagerToView}`
		const statusToView = getStatus(order.status)
		const stackingToView = order.stacking ? 'Да' : 'Нет'
		const logistToView = order.logist && order.logistTelephone
			? `${order.logist}, тел. ${order.logistTelephone}`
			: order.logist
				? order.logist
				: order.logistTelephone
					? order.logistTelephone : ''

		const unloadWindowToView = order.onloadWindowDate && order.onloadWindowTime
			? `${dateHelper.getFormatDate(order.onloadWindowDate)} ${order.onloadWindowTime.slice(0, 5)}`
			: ''

		const filtredAdresses = order.addresses.filter(address => address.isCorrect)
		const addressesToView = filtredAdresses
			.sort(pointSorting)
			.map(getPointToView)

		const loadPoints = filtredAdresses
			.filter(address => address.type === "Загрузка")
			.sort((a, b) => a.idAddress - b.idAddress)
			.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
			.join(' ')

		const unloadPoints = filtredAdresses
			.filter(address => address.type === "Выгрузка")
			.sort((a, b) => a.idAddress - b.idAddress)
			.map((address, i) => `${i + 1}) ${address.bodyAddress}`)
			.join(' ')

		const loadDateToView = addressesToView.length ? addressesToView[0].dateToView : ''

		const unloadPointsArr = addressesToView.length
			? addressesToView
				.filter(point => point.type === 'Выгрузка')
				.sort((a, b) => b.date - a.date)
			: []

		const unloadDateToView = unloadPointsArr.length ? unloadPointsArr[0].dateToView : ''

		const summPall = filtredAdresses
			.filter(address => address.type === "Загрузка")
			.reduce((acc, address) => {
				if (address.pall) {
					const pall = Number(address.pall)
					acc += pall
					return acc
				}
			}, 0)

		const summVolume = filtredAdresses
			.filter(address => address.type === "Загрузка")
			.reduce((acc, address) => {
				if (address.volume) {
					const volume = Number(address.volume)
					acc += volume
					return acc
				}
			}, 0)

		const summWeight = filtredAdresses
			.filter(address => address.type === "Загрузка")
			.reduce((acc, address) => {
				if (address.weight) {
					const weight = Number(address.weight)
					acc += weight
					return acc
				}
			}, 0)

		const routeInfo = getRouteInfo(order)

		return {
			...order,
			dateCreateToView,
			dateDeliveryToView,
			controlToView,
			addressesToView,
			loadDateToView,
			unloadDateToView,
			managerToView,
			statusToView,
			stackingToView,
			logistToView,
			unloadWindowToView,
			loadPoints,
			unloadPoints,
			summPall: summPall ? summPall : null,
			summVolume: summVolume ? summVolume : null,
			summWeight: summWeight ? summWeight : null,
			routeInfo,
		}
	})
}

function getContextMenuItems(params) {
	if (!params.node) return

	const routeData = params.node.data
	const idOrder = routeData.idOrder
	const status = routeData.status

	const selectedRowsData = params.api.getSelectedRows()
	const isVeryfySelectedOrder = !selectedRowsData.filter(order => order.status !== 20).length

	const result = [
		{
			name: `Просмотреть заявку`,
			action: () => {
				showOrder(idOrder)
			},
			icon: uiIcons.fileText,
		},
		{
			name: `Создать маршрут`,
			disabled: (status !== 20 && status !== 40) || selectedRowsData.length,
			action: () => {
				createRoute(routeData)
			},
			icon: uiIcons.cardText,
		},
		{
			name: `Создать маршрут по выделенным заявкам`,
			disabled: !selectedRowsData.length || !isVeryfySelectedOrder,
			action: () => {
				createRouteByOrders(selectedRowsData)
			},
			icon: uiIcons.checks,
		},
		{
			name: `Подтвердить и отправить данные`,
			disabled: status !== 60 || !hasConfirmRouteDataResponse,
			action: () => {
				hasConfirmRouteDataResponse = false
				confirmRouteData(idOrder)
			},
			icon: uiIcons.checkAll,
		},
		"separator",
		"excelExport",
	]

	return result
}

function showOrder(idOrder) {
	window.location.href = `ordersLogist/order?idOrder=${idOrder}`
}
// функции создания маршрута
function createRoute(orderData) {
	const routeForm = document.querySelector('#routeForm')
	const addressesByOrder = orderData.addressesToView.map(address => ({ ...address, idOrder: orderData.idOrder }))
	const updatedOrderData = { ...orderData, addressesToView: addressesByOrder }
	showIncotermsInput(updatedOrderData.typeTruck)
	addDataToRouteForm(updatedOrderData, routeForm)
	openRouteModal()
}
function createRouteByOrders(orderData) {
	const routeForm = document.querySelector('#routeForm')
	const addresses = orderData.reduce((acc, order) => {
		const addressesByOrder = order.addressesToView.map(address => ({ ...address, idOrder: order.idOrder }))
		acc.push(...addressesByOrder)
		return acc
	}, [])

	const unitedOrderData = { ...orderData[0], addressesToView: addresses }
	showIncotermsInput(unitedOrderData.typeTruck)
	addDataToRouteForm(unitedOrderData, routeForm)
	openRouteModal()
}
function confirmRouteData(idOrder) {
	ajaxUtils.get({
		url: getDataHasOrderBaseUrl+idOrder,
		successCallback: (res) => {
			const { status, message } = res
			snackbar.show(message)
			hasConfirmRouteDataResponse = true
		}
	})
}

// функция добавления данных в форму маршрута
function addDataToRouteForm(data, routeForm) {
	const counterpartyValue = routeForm.querySelector('#counterpartyValue')
	const contactValue = routeForm.querySelector('#contactValue')
	const controlValue = routeForm.querySelector('#controlValue')
	const wayValue = routeForm.querySelector('#wayValue')
	const marketNumberValue = routeForm.querySelector('#marketNumberValue')
	const loadNumberValue = routeForm.querySelector('#loadNumberValue')
	const pointList = routeForm.querySelector('#pointList')

	const points = data.addressesToView

	routeForm.counterparty.value = data.counterparty
	counterpartyValue.innerText = data.counterparty
	routeForm.contact.value = data.contact
	contactValue.innerText = data.contact
	routeForm.control.value = data.control ? 'Да' : 'Нет'
	controlValue.innerText = data.control ? 'Да, сверять УКЗ' : 'Нет, не сверять УКЗ'
	routeForm.marketNumber.value = data.marketNumber ? data.marketNumber : ''
	marketNumberValue.innerText = data.marketNumber ? data.marketNumber : ''
	routeForm.loadNumber.value = data.loadNumber ? data.loadNumber : ''
	loadNumberValue.innerText = data.loadNumber ? data.loadNumber : ''
	routeForm.way.value = data.way
	wayValue.innerText = data.way
	routeForm.comment.value = data.comment
	routeForm.typeLoad.value = data.typeLoad
	routeForm.methodLoad.value = data.methodLoad
	routeForm.typeTruck.value = data.typeTruck
	routeForm.incoterms.value = data.incoterms ? data.incoterms : ''
	routeForm.stacking.value = data.stacking ? 'Да' : 'Нет'
	routeForm.cargo.value = data.cargo
	routeForm.temperature.value = data.temperature

	points.forEach((point, i) => {
		const pointElement = createPoint(data, point, i)
		pointList.append(pointElement)
	})
}

function createPoint(routeData, pointData, index) {
	const point = document.createElement('div')
	const pointIndex = index + 1
	const date = dateHelper.getDateForInput(pointData.date)
	const type = pointData.type ? pointData.type : ''
	const idOrder = pointData.idOrder ? pointData.idOrder : ''
	const time = pointData.time ? pointData.time : ''
	const cargo = pointData.cargo ? pointData.cargo : ''
	const pall = pointData.pall ? pointData.pall : ''
	const weight = pointData.weight ? pointData.weight : ''
	const volume = pointData.volume ? pointData.volume : ''
	const tnvd = pointData.tnvd ? pointData.tnvd : ''
	const bodyAddress = pointData.bodyAddress ? pointData.bodyAddress : ''
	const [ country, address ] = bodyAddress ? bodyAddress.split('; ') : ['null', 'null']
	const customsAddress = pointData.customsAddress ? pointData.customsAddress : ''
	const timeFrame = pointData.timeFrame ? pointData.timeFrame : ''
	const contact = pointData.contact ? pointData.contact : ''

	const tnvdRequired = routeData.way === "РБ" ? '' : 'required'
	const tnvdRequiredMarker = routeData.way === "РБ" ? '' : '<span class="text-red">*</span>'

	const unloadDateRequired = routeData.way === "РБ" ? 'required readonly' : ''
	const unloadDateRequiredMarker = routeData.way === "РБ" ? '<span class="text-red">*</span>' : ''

	const unloadTimeRequired = routeData.way === "РБ" ? 'required readonly' : ''
	const unloadTimeRequiredMarker = routeData.way === "РБ" ? '<span class="text-red">*</span>' : ''

	const tnvdHTML = type === 'Загрузка'
		? `<div class='form-group'>
				<label class='col-form-label text-muted font-weight-bold'>Коды ТН ВЭД ${tnvdRequiredMarker}</label>
				<textarea class='form-control' name='tnvd' id='tnvd' placeholder='Коды ТН ВЭД' ${tnvdRequired}></textarea>
			</div>`
		: ''

	const dateHTML = type === 'Загрузка'
		? `<div class='pointDate'>
				<label class='col-form-label text-muted font-weight-bold '>Дата <span class='text-red'>*</span></label>
				<input type='date' class='form-control' name='date' id='date' value='${date}' required readonly>
			</div>
		`
		: `<div class='pointDate'>
				<label class='col-form-label text-muted font-weight-bold '>Дата ${unloadDateRequiredMarker}</label>
				<input type='date' class='form-control' name='date' id='date' value='${date}' ${unloadDateRequired}>
			</div>
		`

	const timeHTML = type === 'Загрузка'
		? `<div class='pointTime'>
				<label class='col-form-label text-muted font-weight-bold '>Время <span class='text-red'>*</span></label>
				<input type='time' class='form-control' name='time' id='time' value='${time}' required readonly>
			</div>
		`
		: `<div class='pointTime'>
				<label class='col-form-label text-muted font-weight-bold '>Время ${unloadTimeRequiredMarker}</label>
				<input type='time' class='form-control' name='time' id='time' value='${time}' ${unloadTimeRequired}>
			</div>
		`

	point.className = 'accordion'
	point.innerHTML = `
		<div class='card point'>
			<form class='pointForm' id='pointform_${pointIndex}' name='pointform_${pointIndex}' action=''>
				<div class='card-header d-flex justify-content-between dragItem' id='heading1'>
					<h5 class='d-flex align-items-center mb-0'>
						Точка ${pointIndex}: ${type}
					</h5>
					<input type='hidden' class='form-control' name='type' id='type' value='${type}'>
					<input type='hidden' class='form-control' name='idOrder' id='idOrder' value='${idOrder}'>
					<div class='control-btns'>
						<button class='accordion-btn' type='button' data-toggle='collapse' data-target='#collapse1' aria-expanded='false' aria-controls='collapse1'>
							Показать/скрыть
						</button>
					</div>
				</div>
				<div id='collapse1' class='collapse show' aria-labelledby='heading1'>
					<div class='card-body'>
						<div class='row-container info-container form-group'>
							${dateHTML}
							${timeHTML}
							<div class='cargoName'>
								<label class='col-form-label text-muted font-weight-bold'>Наименование груза <span class='text-red'>*</span></label>
								<input type='text' class='form-control' name='pointCargo' id='pointCargo' placeholder='Наименование' value='${cargo}' required>
							</div>
							<div class='cargoPall'>
								<label class='col-form-label text-muted font-weight-bold'>Паллеты, шт</label>
								<input type='number' class='form-control' name='pall' id='pall' placeholder='Паллеты, шт' min='0' value='${pall}'>
							</div>
							<div class='cargoWeight'>
								<label class='col-form-label text-muted font-weight-bold'>Масса, кг</label>
								<input type='number' class='form-control' name='weight' id='weight' placeholder='Масса, кг' min='0' value='${weight}'>
							</div>
							<div class='cargoVolume'>
								<label class='col-form-label text-muted font-weight-bold'>Объем, м.куб.</label>
								<input type='number' class='form-control' name='volume' id='volume' placeholder='Объем, м.куб.' min='0' value='${volume}'>
							</div>
						</div>
						${tnvdHTML}
						<div class='row-container addresses-container form-group'>
							<div>
								<label class='col-form-label text-muted font-weight-bold'>Адрес склада <span class='text-red'>*</span></label>
								<div class="form-group address-container">
									<div class="autocomplete">
										<input type="text" class="form-control country" name="country" id="country" placeholder="Страна" value='${country}' required>
									</div>
									<input type="text" class="form-control" name="address" id="address" placeholder="Город, улица и т.д." value='${address}' required>
								</div>
							</div>
							<div>
								<label class='col-form-label text-muted font-weight-bold'>Адрес таможенного пункта</label>
								<input type='text' class='form-control' name='customsAddress' id='customsAddress' placeholder='Страна, город, улица и т.д.' value='${customsAddress}'>
							</div>
						</div>
						<div class='row-container'>
							<div class='timeFrame-container'>
								<label class='col-form-label text-muted font-weight-bold'>Время работы склада <span class='text-red'>*</span></label>
								<input type='text' class='form-control' name='timeFrame' id='timeFrame' placeholder='С ЧЧ:ММ по ЧЧ:ММ' value='${timeFrame}' required>
							</div>
							<div class='contact-container'>
								<label class='col-form-label text-muted font-weight-bold'>Контактное лицо на складе <span class='text-red'>*</span></label>
								<input type='text' class='form-control' name='pointContact' id='pointContact' placeholder='ФИО, телефон' value='${contact}' required>
							</div>
						</div>
					</div>
				</div>
			</form>
		</div>
	`

	const tnvdInput = point.querySelector('#tnvd')
	if (tnvdInput) tnvdInput.value = tnvd

	// автозаполнение выпадающего списка стран для адресов
	const counrtyInput = point.querySelector('#country')
	autocomplete(counrtyInput, countries)

	const deleteBtn = document.createElement('button')
	deleteBtn.className = 'btn btn-outline-danger'
	deleteBtn.title = 'Удалить точку'
	deleteBtn.innerHTML = uiIcons.trash
	deleteBtn.addEventListener('click', (e) => deletePoint(e, point))

	const controlBtnsContainer = point.querySelector('.control-btns')
	controlBtnsContainer.append(deleteBtn)

	return point
}
function deletePoint(e, point) {
	point.remove()
}

function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./ordersLogist/order?idOrder=${data.idOrder}`

	return `<a class="text-primary" href="${link}">${params.value}</a>`
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

// функции управления модальным окном
function openRouteModal() {
	$('#routeModal').modal('show')
}
function closeRouteModal() {
	$('#routeModal').modal('hide')
}

// обработчик формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	updateTable()
}

// обработчик формы создания марщрута
function routeFormSubmitHandler(e) {
	e.preventDefault()

	const routeForm = e.target
	const data = routeFormDataFormatter(routeForm)

	if (validatePointForms(routeForm)) return

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: createRouteUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show('Маршрут создан!')
				updateTable()
				closeRouteModal()
			} else if (res.status === '100') {
				snackbar.show(res.message)
			} else {
				snackbar.show('Возникла ошибка - обновите страницу!')
			}
			enableButton(e.submitter)
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}

// проверка наличия всех обязательных данных о точках
function validatePointForms(routeForm) {
	const pointForms = routeForm.querySelectorAll('.pointForm')
	const isValidPointForms = []

	pointForms.forEach(form => {
		const isValidForm = form.reportValidity()
		isValidPointForms.push(isValidForm)
	})

	return isValidPointForms.includes(false)
}

// форматирование данных формы маршрута
function routeFormDataFormatter(routeForm) {
	const formData = new FormData(routeForm)
	const data = Object.fromEntries(formData)
	
	const points = []
	const idOrders = []

	const pointForms = routeForm.querySelectorAll('.pointForm')
	pointForms.forEach((form, i) => {
		points.push({
			number: i + 1,
			type: form.type.value,
			date: form.date.value,
			time: form.time.value,
			cargo: form.pointCargo.value,
			pall: form.pall.value,
			weight: form.weight.value,
			volume: form.volume.value,
			tnvd: form.tnvd ? form.tnvd.value : null,
			bodyAdress: `${form.country.value}; ${form.address.value}`,
			customsAddress: form.customsAddress.value,
			timeFrame: form.timeFrame.value,
			contact: form.pointContact.value,
		})

		idOrders.push(Number(form.idOrder.value))
	})

	const updatedIdOrders = Array.from(new Set(idOrders))
	const dateDelivery = points.length && points[points.length - 1].date
		? points[points.length - 1].date
		: ''

	return {
		...data,
		idOrders: updatedIdOrders,
		dateDelivery,
		points,
	}
}
// очистка формы маршрута
function clearRouteForm() {
	const routeForm = document.querySelector('#routeForm')
	const pointList = routeForm.querySelector('#pointList')

	routeForm.reset()
	pointList.innerHTML = ''
}
