import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { ResetStateToolPanel, dateComparator, gridColumnLocalState, gridFilterLocalState } from "./AG-Grid/ag-grid-utils.js";
import { debounce, getData, dateHelper, getStatus, rowClassRules } from './utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { ajaxUtils } from './ajaxUtils.js'
import { uiIcons } from './uiIcons.js'
import { excelStyles, getPointToView, pointSorting, procurementExcelExportParams } from './procurementControlUtils.js';
import { addUnloadPointUrl, getOrderForStockSupportBaseUrl, getOrdersHasCounterpartyUrl } from './globalConstants/urls.js'

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'BashkirovTable'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const MIN_UNLOAD_DATE_FACTOR = 0

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
	{ headerName: 'Дата загрузки', field: 'loadDateToView', comparator: dateComparator, },
	{ headerName: 'Тип маршрута', field: 'way', },
	{ headerName: 'Номер из Маркета', field: 'marketNumber', },
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
	// { headerName: 'Логист', field: 'logistToView', },
	{ headerName: 'Контактное лицо контрагента', field: 'contact', wrapText: true, autoHeight: true, },
	{ headerName: 'Сверка УКЗ', field: 'controlToView', width: 100, },
	{ headerName: 'Менеджер', field: 'manager', wrapText: true, autoHeight: true, },
	{ headerName: 'Телефон менеджера', field: 'telephoneManager', wrapText: true, autoHeight: true, },
	{ headerName: 'Тип загрузки авто', field: 'typeLoad', },
	{ headerName: 'Способ загрузки авто', field: 'methodLoad', },
	{ headerName: 'Груз', field: 'cargo', },
	{ headerName: 'Температурные условия', field: 'temperature', },
	{ headerName: 'Штабелирование', field: 'stackingToView', },
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: "px-2",
		cellClass: "px-2 text-center",
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
	rowSelection: "multiple",
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	getContextMenuItems: getContextMenuItems,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	sideBar: {
		toolPanels: [
			{
				id: "columns",
				labelDefault: "Columns",
				labelKey: "columns",
				iconKey: "columns",
				toolPanel: "agColumnsToolPanel",
				toolPanelParams: {
					suppressRowGroups: true,
					suppressValues: true,
					suppressPivots: true,
					suppressPivotMode: true,
				},
			},
			{
				id: "filters",
				labelDefault: "Filters",
				labelKey: "filters",
				iconKey: "filter",
				toolPanel: "agFiltersToolPanel",
			},
			{
				id: "resetState",
				iconKey: "menu",
				labelDefault: "Сброс настроек",
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
				{ headerName: "№", field: "pointNumber", flex: 1 },
				{ headerName: "Тип точки", field: "type" },
				{ headerName: "Дата", field: "dateToView" },
				{
					headerName: "Время",
					field: "time",
					valueFormatter: (params) => params.value && `${params.value.slice(0, 5)}`,
				},
				{ headerName: "Коды ТН ВЭД", field: "tnvd", flex: 6 },
				{ headerName: "Информация о грузе", field: "info", tooltipField: "info", flex: 8 },
				{ headerName: "Адрес склада", field: "bodyAddress", flex: 8 },
				{ headerName: "Время работы", field: "timeFrame" },
				{ headerName: "Контактное лицо", field: "contact", flex: 6 },
				{ headerName: "Адрес таможни", field: "customsAddress", flex: 8 },
			],
			defaultColDef: {
				headerClass: "px-1",
				cellClass: "px-2",
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
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const gridDiv = document.querySelector('#myGrid')

	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
	const orders = await getData(`${getOrderForStockSupportBaseUrl}${dateStart}&${dateEnd}`)

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

	// форма установки точки выгрузки
	const addUnloadPointForm = document.querySelector('#addUnloadPointForm')
	addUnloadPointForm.addEventListener('submit', async (e) => addUnloadPointFormSubmitHandler(e))

	// очистка формы при закрытии модального окна формы
	$('#addUnloadPointModal').on('hide.bs.modal', (e) => addUnloadPointForm.reset())
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
		? await getData(`${getOrdersHasCounterpartyUrl}${dateStart}&${dateEnd}&${counterparty}`)
		: await getData(`${getOrderForStockSupportBaseUrl}${dateStart}&${dateEnd}`)

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

		return {
			...order,
			dateCreateToView,
			dateDeliveryToView,
			controlToView,
			addressesToView,
			loadDateToView,
			managerToView,
			statusToView,
			stackingToView,
			logistToView,
			unloadWindowToView,
			loadPoints,
			unloadPoints,
			summPall: summPall ? summPall : null,
			summVolume: summVolume ? summVolume : null,
			summWeight: summWeight ? summWeight : null
		}
	})
}

function getContextMenuItems(params) {
	if (!params.node) return

	const orderData = params.node.data
	const idOrder = orderData.idOrder
	const status = orderData.status

	const result = [
		{
			name: `Просмотреть заявку`,
			action: () => {
				showOrder(idOrder)
			},
			icon: uiIcons.fileText,
		},
		{
			name: `Установить точку выгрузки`,
			disabled: status !== 15 && status !== 20,
			action: () => {
				addUnloadPoint(orderData)
			},
			icon: uiIcons.setUnloadPoint,
		},
		"separator",
		"excelExport",
	]

	return result
}

function showOrder(idOrder) {
	window.location.href = `./orders/order?idOrder=${idOrder}`
}

function addUnloadPoint(data) {
	const unloadPointForm = document.querySelector('#addUnloadPointForm')
	const idOrder = data.idOrder
	const lastLoadPoint = data.addressesToView
		.filter(address => address.type === 'Загрузка')
		.sort((a, b) => b.date - a.date)[0]

	setUnloadDateMinValue(lastLoadPoint.date)
	changeCargoInfoInputsRequired(unloadPointForm, data.methodLoad, data.typeTruck)
	addDataToForm(idOrder, lastLoadPoint, unloadPointForm)
	showUnloadPointModal()
}

function orderLinkRenderer(params) {
	const data = params.node.data
	const link = `./orders/order?idOrder=${data.idOrder}`

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

// обработчик формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	updateTable()
}

// отображение модального окна действий при импорте
function showUnloadPointModal() {
	$('#addUnloadPointModal').modal('show')
}
function hideUnloadPointModal() {
	$('#addUnloadPointModal').modal('hide')
}

// обработчик формы добавления точки выгрузки
function addUnloadPointFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const pointData = pointFormDataFormatter(formData)
	const data = {
		idOrder: +formData.get('idOrder'),
		unloadPoint: pointData,
	}

	ajaxUtils.postJSONdata({
		url: addUnloadPointUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			console.log(res)
			hideUnloadPointModal()
			updateTable()
			if (res.status === '200') {
				snackbar.show(res.message)
			} else {
				snackbar.show('Возникла ошибка - попробуйте очистить форму и заполнить заново')
			}
		}
	})
}

// форматирование данных из формы добавления точки выгрузки
function pointFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const timeFrame =
		data.timeFrame_from && data.timeFrame_to
			? `С ${data.timeFrame_from} по ${data.timeFrame_to}`
			: data.timeFrame_from
				? `С ${data.timeFrame_from}`
				: data.timeFrame_to
					? `По ${data.timeFrame_to}` : ''

	const contact =
		data.pointContact_fio && data.pointContact_tel
			? `${data.pointContact_fio}, тел. ${data.pointContact_tel}`
			: data.pointContact_fio
				? `${data.pointContact_fio}`
				: data.pointContact_tel
					? `Тел. ${data.pointContact_tel}` : ''

	const bodyAdress = `${data.country}; ${data.address}`

	const customsAddress = 
		data.customsCountry && data.customsAddress
			? `${data.customsCountry}; ${data.customsAddress}`
			: data.customsCountry
				? data.customsCountry
				: data.customsAddress
					? data.customsAddress : ''

	return {
		type: data.type,
		date: data.date ? data.date : '',
		time: data.time ? data.time : '',
		cargo: data.pointCargo ? data.pointCargo : '',
		pall: data.pall ? data.pall : '',
		weight: data.weight ? data.weight : '',
		volume: data.volume ? data.volume : '',
		tnvd:  data.tnvd ? data.tnvd : '',
		bodyAdress,
		customsAddress,
		timeFrame,
		contact,
	}
}

// установка минимальной даты выгрузки
function setUnloadDateMinValue(loadDate) {
	const now = new Date()
	let minDate = loadDate + dateHelper.DAYS_TO_MILLISECONDS * MIN_UNLOAD_DATE_FACTOR

	if (minDate < now) {
		minDate = now
	}

	const minDateStr = dateHelper.getDateForInput(minDate)
	const unloadDateInput = document.querySelector('#unloadDate')
	unloadDateInput.setAttribute('min', minDateStr)
}

// изменение значения аттрибута "required" для полей с количеством паллет, объемом и массой
function changeCargoInfoInputsRequired(form, methodLoad, typeTruck) {
	const pallContainer = document.querySelector('.form-group:has(>#pall)')
	const pallTitle = pallContainer && pallContainer.querySelector('label')
	const pallInput = form.querySelector('#pall')
	const weightContainer = document.querySelector('.form-group:has(>#weight)')
	const weightTitle = weightContainer && weightContainer.querySelector('label')
	const weightInput = form.querySelector('#weight')
	const volumeContainer = document.querySelector('.form-group:has(>#volume)')
	const volumeTitle = volumeContainer && volumeContainer.querySelector('label')
	const volumeInput = form.querySelector('#volume')

	if (typeTruck.includes('Контейнер')) {
		pallInput.required = false
		weightInput.required = false
		volumeInput.required = false
	} else {
		if (methodLoad === 'Навалом') {
			pallInput.required = false
			weightInput.required = false
			volumeInput.required = true
		} else {
			pallInput.required = true
			weightInput.required = true
			volumeInput.required = false
		}
	}

	pallTitle && (pallTitle.innerHTML = pallInput.required ? 'Паллеты, шт <span class="text-red">*</span>' : 'Паллеты, шт')
	weightTitle && (weightTitle.innerHTML = weightInput.required ? 'Масса, кг <span class="text-red">*</span>' : 'Масса, кг')
	volumeTitle && (volumeTitle.innerHTML = volumeInput.required ? 'Объем, м.куб. <span class="text-red">*</span>' : 'Объем, м.куб.')

}

// добавление данных в форму точки выгрузки
function addDataToForm(idOrder, data, form) {
	form.idOrder.value = idOrder
	form.pointCargo.value = data.cargo
	form.pall.value = data.pall
	form.weight.value = data.weight
	form.volume.value = data.volume
	form.country.value = 'BY Беларусь'
	form.customsCountry.value = 'BY Беларусь'
}
