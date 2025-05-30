import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { ResetStateToolPanel, dateComparator, dateValueFormatter, deselectAllCheckboxes, gridColumnLocalState, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js'
import { debounce, getData, dateHelper, getStatus, rowClassRules, disableButton, enableButton, isAdmin, isObserver } from './utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { ajaxUtils } from './ajaxUtils.js'
import { uiIcons } from './uiIcons.js'
import {
	addDataToRouteForm,
	addListnersToPoint,
	changeCargoInfoInputsRequired,
	changeForm,
	changeFormToDefault,
	changeTnvdInputRequired,
	dangerousInputOnChangeHandler,
	inputEditBan,
	isInvalidPointForms,
	isValidTnvdValue,
	orderCargoInputOnChangeHandler,
	orderPallInputOnChangeHandler,
	orderWeightInputOnChangeHandler,
	toggleForReductionInputsVisible,
	typeTruckOnChangeHandler,
} from "./procurementFormUtils.js"
import { checkCombineRoutes, excelStyles, mapCallbackForProcurementControl, mergeRoutePoints, procurementExcelExportParams } from './procurementControlUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { getAddressHTML, getAddressInfoHTML, getCargoInfoHTML, getCustomsAddressHTML, getDateHTML, getTimeHTML, getTnvdHTML } from './procurementFormHtmlUtils.js'
import { getComment, getPointsData } from './procurementFormDataUtils.js'
import {
	createAhoRouteUrl,
	createRouteUrl,
	getDataHasOrderBaseUrl,
	getOrdersForLogistBaseUrl,
	getOrdersHasCounterpartyUrl,
	getOrdersLinksBaseUrl
} from './globalConstants/urls.js'

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'ProcurementControlLogist'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const createRouteLocalUrl = (way) => way === 'АХО' ? createAhoRouteUrl : createRouteUrl

const role = document.querySelector('#role').value

const FORM_TYPE = 'routeForm'

// let hasConfirmRouteDataResponse = true

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
	{
		headerName: 'Дата создания заявки', field: 'dateCreate',
		valueFormatter: dateValueFormatter, comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: 'Дата загрузки (первая)', field: 'loadDateToView',
		valueFormatter: dateValueFormatter, comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: 'Дата выгрузки (последняя)', field: 'unloadDateToView',
		valueFormatter: dateValueFormatter, comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: 'Слот на выгрузку', field: 'timeDeliveryToView', },
	// { headerName: 'Дата и время выгрузки', field: 'unloadWindowToView', width: 200, },
	// { headerName: 'Продолжительность выгрузки', field: 'onloadTime', width: 200, },
	{ headerName: 'Тип маршрута', field: 'wayToView', },
	{ headerName: 'Номер из Маркета', field: 'marketNumber', },
	{ headerName: 'Погрузочный номер', field: 'loadNumber', },
	{ headerName: 'Условия поставки', field: 'incoterms', wrapText: true, autoHeight: true, },
	{ headerName: 'Точки загрузки', field: 'loadPointsToView', wrapText: true, autoHeight: true, },
	{ headerName: 'Точки выгрузки', field: 'unloadPointsToView', wrapText: true, autoHeight: true, },
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
	{ headerName: 'Цена маршрута', field: 'routePrice', wrapText: true, autoHeight: true,},
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
	{ headerName: 'Начальная сумма заказа без НДС', field: 'marketOrderSumFirst', },
	{ headerName: 'Конечная сумма заказа с НДС', field: 'marketOrderSumFinal', },
	{ headerName: 'Связь', field: 'link', },
]

if (isAdmin(role)) {
	columnDefs.push({
		headerName: 'Изменения статуса', field: 'changeStatus',
		wrapText: true, autoHeight: true,
	})
}

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
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	getRowId: (params) => params.data.idOrder,
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

document.addEventListener('DOMContentLoaded', async () => {
	const orderSearchForm = document.querySelector('#orderSearchForm')
	const routeForm = document.querySelector('#routeForm')
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const methodLoadInput = document.querySelector('#methodLoad')
	const typeTruckInput = document.querySelector('#typeTruck')
	const wayInput = document.querySelector('#way')
	const dangerousInput = document.querySelector('#dangerous')
	// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
	const forReduction = document.querySelector('#forReduction')

	// отрисовка таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	// отображение стартовых данных
	if (window.initData) {
		await initStartData(orderSearchForm)
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData(orderSearchForm)
		})
	}

	// автозаполнение полей дат в форме поиска заявок
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
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
		typeTruckOnChangeHandler(e)
		const points = document.querySelectorAll('.point')
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа маршрута
	wayInput.addEventListener('change', (e) => changeTnvdInputRequired(e))

	// обработчик на поле Опасный груз
	// dangerousInput && dangerousInput.addEventListener('change', dangerousInputOnChangeHandler)

	// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
	forReduction.addEventListener('change', (e) => toggleForReductionInputsVisible(e.target.checked))

	// листнер на очистку формы маршрута при закрытии модального окна
	$('#routeModal').on('hide.bs.modal', (e) => {
		deselectAllCheckboxes(gridOptions)
		clearRouteForm()
	})

	// делаем контейнер с точками сортируемым с помощью перетаскивания
	Sortable.create(pointList, {
		handle: '.dragItem',
		animation: 150
	})

	bootstrap5overlay.hideOverlay()
})

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// установка стартовых данных
async function initStartData(orderSearchForm) {
	await updateTable(gridOptions, orderSearchForm, window.initData)
	window.initData = null

	// получение настроек таблицы из localstorage
	restoreColumnState()
	restoreFilterState()
}

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}
async function updateTable(gridOptions, searchForm, data) {
	gridOptions.api.showLoadingOverlay()

	const dateStart = searchForm.date_from.value
	const dateEnd = searchForm.date_to.value
	const counterparty = searchForm.searchName.value
	const getOrderUrl = counterparty.length
		? `${getOrdersHasCounterpartyUrl}${dateStart}&${dateEnd}&${counterparty}`
		: `${getOrdersForLogistBaseUrl}${dateStart}&${dateEnd}`

	const orders = data
		? data
		: await getData(getOrderUrl)

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
	return data.map(mapCallbackForProcurementControl)
}

function getContextMenuItems(params) {
	if (!params.node) return

	const routeData = params.node.data
	const idOrder = routeData.idOrder
	const status = routeData.status
	const selectedRowsData = params.api.getSelectedRows()

	const isVerifySelectedOrderStatus = (order) => order.status === 20 || order.status === 40
	const isVerifySelectedOrder = selectedRowsData.every(order => isVerifySelectedOrderStatus(order))

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
			disabled: (status !== 20 && status !== 40) || selectedRowsData.length || isObserver(role),
			action: async () => {
				createRouteBySingleOrder(routeData)
			},
			icon: uiIcons.cardText,
		},
		{
			name: `Создать маршрут по выделенным заявкам`,
			disabled: !selectedRowsData.length || selectedRowsData.length < 2 || !isVerifySelectedOrder || isObserver(role),
			action: () => {
				createRouteByOrders(selectedRowsData)
			},
			icon: uiIcons.checks,
		},
		{
			name: `Подтвердить и отправить данные`,
			// disabled: status !== 60 || !hasConfirmRouteDataResponse,
			disabled: status !== 60 || isObserver(role),
			action: () => {
				// hasConfirmRouteDataResponse = false
				confirmRouteData(idOrder)
			},
			icon: uiIcons.checkAll,
		},
		"separator",
		"excelExport",
	]

	return result
}

// получение связанных заказов
async function getLinkedOrders(link) {
	const res = await getData(`${getOrdersLinksBaseUrl}${link}`)
	if (res && res.status === '200') {
		return res.linkOrders.map(order => {
			return {
				...order,
				addressesToView: order.addresses.filter(address => address.isCorrect)
			}
		})
	}
	return []
}

// редирект на страницу заказа
function showOrder(idOrder) {
	window.location.href = `ordersLogist/order?idOrder=${idOrder}`
}

// создание маргшрута по одному заказу
async function createRouteBySingleOrder(orderData) {
	const orderLink = orderData.link
	if (orderLink) {
		alert(
			`ВНИМАНИЕ, заявка ${orderData.idOrder} объединена с другими заказами.`
			+ ` В форме создания маршрута будут данные всех связанных заказов.`
			+ ` Если в точках загрузки/выгрузки совпадают адреса, то точки будут`
			+ ` объединены с суммированием паллет, веса и объема груза.`
			+ ` Пожалуйста, проверьте/отредактируйте название и точки маршрута.`
		)
		const linkedOrders = await getLinkedOrders(orderLink)
		if (!linkedOrders || linkedOrders.length === 0) {
			alert(`Не удалось получить данные связанных заказов.`)
			return
		}
		createRouteByOrders(linkedOrders)
		return
	}

	const addressesByOrder = orderData.addressesToView.map(address => ({ ...address, idOrder: orderData.idOrder }))
	const updatedOrderData = { ...orderData, idOrders: [orderData.idOrder], addresses: addressesByOrder }
	createRoute(updatedOrderData)
}
// создание маршрута по нескольким заказам
function createRouteByOrders(orderData) {
	const firstOrder = orderData[0]

	const errorMessage = checkCombineRoutes(orderData)
	if (errorMessage) {
		alert(errorMessage)
		deselectAllCheckboxes(gridOptions)
		return
	}

	const processField = (field) =>
		Array.from(new Set(orderData?.map(order => order[field]).filter(Boolean))).join(' + ')

	const idOrders = orderData.map(order => order.idOrder)
	const counterparty = processField('counterparty')
	const contact = processField('contact')
	const control = orderData.some(order => order.control)
	const loadNumber = orderData.map(order => order.loadNumber).join('/')
	const marketInfo = processField('marketInfo')
	const stacking = orderData.every(order => order.stacking)
	const cargo = processField('cargo')
	const temperature = processField('temperature')
	const comment = processField('comment')

	const addresses = orderData.reduce((acc, order) => {
		const addressesByOrder = order.addressesToView.map(address => ({ ...address, idOrder: order.idOrder }))
		acc.push(...addressesByOrder)
		return acc
	}, [])
	const mergedRoutePoints = mergeRoutePoints(addresses)
	const unitedOrderData = {
		...firstOrder, idOrders, addresses: mergedRoutePoints,
		counterparty, contact, control, loadNumber, marketInfo,
		stacking, cargo, temperature, comment,
	}
	createRoute(unitedOrderData)
}

// функция создания маршрута
function createRoute(orderData) {
	const routeForm = document.querySelector('#routeForm')
	changeForm(orderData, FORM_TYPE)
	addDataToRouteForm(orderData, routeForm, createPoint)
	changeEditingRules(orderData, routeForm)
	openRouteModal()
}

// отправка данных по маршруту
function confirmRouteData(idOrder) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	ajaxUtils.get({
		url: getDataHasOrderBaseUrl+idOrder,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			const { status, message } = res
			snackbar.show(message)
			// hasConfirmRouteDataResponse = true
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// метод создания точки маршрута в форме
function createPoint(order, pointData, index) {
	const point = document.createElement('div')
	const pointIndex = index + 1
	const date = dateHelper.getDateForInput(pointData.date)
	const pointType = pointData.type ? pointData.type : ''
	const idOrder = pointData.idOrder ? pointData.idOrder : ''
	const time = pointData.time ? pointData.time.slice(0,5) : ''
	const tnvd = pointData.tnvd ? pointData.tnvd : ''
	const bodyAddress = pointData.bodyAddress ? pointData.bodyAddress : ''
	const customsAddress = pointData.customsAddress ? pointData.customsAddress : ''

	const isInternalMovement = order.isInternalMovement
	const way = order.way
	const EAEUImport = false
	const props = { order, isInternalMovement, EAEUImport, way, pointType, pointIndex }

	const dateHTML = getDateHTML({ ...props, value: date })
	const timeHTML = getTimeHTML({ ...props, value: time })
	const tnvdHTML = getTnvdHTML({ ...props, value: tnvd })
	const cargoInfoHTML = getCargoInfoHTML({ ...props, pointData })
	const addressHTML = getAddressHTML({ ...props, value: bodyAddress })
	const addressInfoHTML = getAddressInfoHTML({ ...props, pointData })
	const customsAddressHTML = getCustomsAddressHTML({ ...props, value: customsAddress })

	point.className = 'accordion'
	point.innerHTML = `
		<div class='card point'>
			<form class='pointForm' id='pointform_${pointIndex}' name='pointform_${pointIndex}' action=''>
				<div class='card-header d-flex justify-content-between dragItem' id='heading1'>
					<h5 class='d-flex align-items-center mb-0'>
						Точка ${pointIndex}: ${pointType}
					</h5>
					<input type='hidden' class='form-control' name='type' id='type' value='${pointType}'>
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
							${cargoInfoHTML}
						</div>
						${tnvdHTML}
						${addressHTML}
						<div class='row-container form-group'>
							${addressInfoHTML}
						</div>
						${customsAddressHTML}
					</div>
				</div>
			</form>
		</div>
	`

	// добавляем кнопку удаления точки из формы
	const controlBtnsContainer = point.querySelector('.control-btns')
	const deleteBtn = document.createElement('button')
	deleteBtn.className = 'btn btn-outline-danger'
	deleteBtn.title = 'Удалить точку'
	deleteBtn.innerHTML = uiIcons.trash
	deleteBtn.addEventListener('click', (e) => deletePoint(e, point))
	controlBtnsContainer.append(deleteBtn)

	addListnersToPoint(point, way, pointIndex)

	return point
}

function deletePoint(e, point) {
	point.remove()
}

function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./ordersLogist/order?idOrder=${data.idOrder}`

	const isLinkedOrder = data.link
	const isLinkedOrderLabel = isLinkedOrder ? ' <span class="text-danger"> (объединен)</span>' : ''

	return `<a class="text-primary" href="${link}">${params.value}${isLinkedOrderLabel}</a>`
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
	updateTable(gridOptions, e.target)
}

// обработчик формы создания марщрута
function routeFormSubmitHandler(e) {
	e.preventDefault()

	const routeForm = e.target
	const data = routeFormDataFormatter(routeForm)

	// проверяем, заполнена ли предыдущая точка
	if (isInvalidPointForms(e.target)) return

	// проверка наличия хотя бы одной точки загрузки
	if (!data.points.find(point => point.type === 'Загрузка')) {
		snackbar.show('Необходимо добавить точку загрузки!')
		return
	}

	// проверка наличия хотя бы одной точки выгрузки
	if (!data.points.find(point => point.type === 'Выгрузка')) {
		snackbar.show('Необходимо добавить точку выгрузки!')
		return
	}

	// проверка начала маршрута следования с точки загрузки
	if (data.points[0].type !== 'Загрузка') {
		snackbar.show('Маршрут должен начинаться с точки загрузки!')
		return
	}

	if (!isValidTnvdValue(data)) {
		snackbar.show('Неверное значение кода ТН ВЭД!')
		return
	}

	disableButton(e.submitter)

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)

	ajaxUtils.postJSONdata({
		url: createRouteLocalUrl(data.way),
		token: token,
		data: data,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				snackbar.show('Маршрут создан!')
				const orderSearchForm = document.querySelector('#orderSearchForm')
				updateTable(gridOptions, orderSearchForm)
				closeRouteModal()
			} else if (res.status === '100') {
				res.message && snackbar.show(res.message)
			} else if (res.status === '105') {
				res.message && showMessageModal(res.message)
			} else {
				snackbar.show('Возникла ошибка - обновите страницу!')
			}
			enableButton(e.submitter)
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			enableButton(e.submitter)
		}
	})
}

// форматирование данных формы маршрута
function routeFormDataFormatter(routeForm) {
	const formData = new FormData(routeForm)
	const data = Object.fromEntries(formData)

	const points = getPointsData(data.way)
	const updatedPoints = points.map(point => ({ ...point, number: point.pointNumber }))
	const idOrders = data.idOrders.split(',').map(Number)

	const dateDelivery = points.length && points[points.length - 1].date
		? points[points.length - 1].date
		: ''

	// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
	const forReduction = data.forReduction === 'on'
	const startPriceForReduction = data.startPriceForReduction ? Number(data.startPriceForReduction) : null
	const currencyForReduction = data.currencyForReduction ? data.currencyForReduction : null

	return {
		...data,
		idOrders,
		dateDelivery,
		points: updatedPoints,
		comment: getComment(data),
		// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
		forReduction,
		startPriceForReduction,
		currencyForReduction,
	}
}

// получение id всех заявок в маршруте
function getIdOrders(routeForm) {
	const idOrders = []
	const pointForms = routeForm.querySelectorAll('.pointForm')
	pointForms.forEach((form) => {
		idOrders.push(Number(form.idOrder.value))
	})
	const updatedIdOrders = Array.from(new Set(idOrders))
	return updatedIdOrders
}

// очистка формы маршрута
function clearRouteForm() {
	const routeForm = document.querySelector('#routeForm')
	const pointList = routeForm.querySelector('#pointList')

	routeForm.reset()
	changeFormToDefault()
	pointList.innerHTML = ''
}

// изменение правил редактирования формы
function changeEditingRules(order, form) {
	changeCounterpartyLabel(true)
	inputEditBan(form, '#counterparty', false)

	const points = document.querySelectorAll('.point')
	const way = order.way
	const isInternalMovement = order.isInternalMovement === 'true'

	points.forEach((point, i) => {
		const pointIndex = i + 1
		const dateInput = point.querySelector(`#date_${pointIndex}`)
		dateInput && dateInput.removeAttribute('min')
		inputEditBan(point, `#date_${pointIndex}`, true)

		const timeInput = point.querySelector(`#time_${pointIndex}`)
		timeInput && dateInput.removeAttribute('min')
		inputEditBan(point, `#time_${pointIndex}`, true)
	})
}

// изменение названия поля counterparty
function changeCounterpartyLabel(isRequired) {
	const counterpartyInput = document.querySelector('#counterparty')
	const counterpartyContainer = counterpartyInput.parentElement
	const counterpartyLabel = counterpartyContainer.querySelector('label')
	const labelText = isRequired ? 'Название маршрута <span class="text-red">*</span>' : 'Название маршрута'
	counterpartyLabel.innerHTML = labelText
}

function convertToDayMonthTime(eventDateStr) {
	const date = new Date(eventDateStr)
	const formatter = new Intl.DateTimeFormat('ru', {
		day: '2-digit',
		month: 'long', 
		hour: '2-digit',
		minute: '2-digit'
	})
	return formatter.format(date)
}

function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerHTML = message
	$('#displayMessageModal').modal('show')
}