import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, BtnsCellRenderer, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { aproofQualityFoodCardUrl, createArrayOfPriceProtocolUrl, createPriceProtocolUrl, getAllAcceptanceQualityFoodCardUrl, getClosedAcceptanceQualityBaseUrl, getPriceProtocolListUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { uiIcons } from './uiIcons.js'
import { ajaxUtils } from './ajaxUtils.js'

const PAGE_NAME = 'priceProtocolList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let formItemsIndex = 0;

const columnDefs = [
	{
		headerName: "№", field: "idPriceProtocol",
		sort: "desc",
		minWidth: 60, width: 80,
	},
	{ headerName: "Штрих-код товара", field: "barcode", width: 120, },
	{ headerName: "Код товара", field: "productCode", width: 160, },
	{ headerName: "Код ТНВЭД", field: "tnvCode", width: 120, },
	{ headerName: "Наименование товара", field: "name", width: 120, },
	{ headerName: "Прейскурантная цена производителя без НДС (BYN)", field: "priceProducer", width: 120, },
	{ headerName: "Себестоимость импортера без надбавки и без НДС (гр.11 ТТН)", field: "costImporter", width: 120, },
	{ headerName: "Надбавка импортера, % (гр.11 ТТН)", field: "markupImporterPercent", width: 120, },
	{ headerName: "Скидка с отпускной цены, %", field: "discountPercent", width: 120, },
	{ headerName: "Оптовая скидка, %", field: "wholesaleDiscountPercent", width: 120, },
	{ headerName: "Отпускная цена без НДС (гр. 4 ТТН), BYN", field: "priceWithoutVat", width: 120, },
	{ headerName: "Оптовая надбавка, %", field: "wholesaleMarkupPercent", width: 120, },
	{ headerName: "Ставка НДС", field: "vatRate", width: 120, },
	{ headerName: "Цена с НДС, BYN", field: "priceWithVat", width: 120, },
	{ headerName: "Страна происхождения", field: "countryOrigin", width: 120, },
	{ headerName: "Производитель", field: "manufacturer", width: 120, },
	{ headerName: "Штук/кг в упаковке", field: "unitPerPack", width: 120, },
	{ headerName: "Срок годности в днях", field: "shelfLifeDays", width: 120, },
	{ headerName: "Текущая отпускная цена, BYN", field: "currentPrice", width: 120, },
	{ headerName: "% изменения отпускной цены", field: "priceChangePercent", width: 120, },
	{
		headerName: "Дата последнего изменения цены", field: "lastPriceChangeDate", width: 140,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: "Дата начала действия", field: "dateValidTo", width: 140,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: "Дата окончания действия", field: "dateValidTo", width: 140,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{ headerName: "Номер договора", field: "contractNumber", },
	{
		headerName: "Дата договора", field: "contractDate", width: 140,
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
]
const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		flex: 1,
		minWidth: 100,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
		enableRowGroup: true,
	},
	animateRows: true,
	suppressDragLeaveHidesColumns: true,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idPriceProtocol,
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
}


document.addEventListener('DOMContentLoaded', async () => {
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	restoreColumnState()

	// // автозаполнение полей дат в форме поиска заявок
	// const date_fromInput = document.querySelector('#date_from')
	// const date_toInput = document.querySelector('#date_to')
	// const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 0)
	// date_fromInput.value = dateStart
	// date_toInput.value = dateEnd

	const data = await getPriceProtocolData()
	updateTable(gridOptions, data)

	// листнер на отправку формы поиска заявок
	// orderSearchForm.addEventListener('submit', searchFormSubmitHandler)
	// листнер на отправку формы установки статуса карточки
	createPriceProtocolForm.addEventListener('submit', createPriceProtocolFormSubmitHandler)

	$('#createPriceProtocolModal').on('hidden.bs.modal', (e) => {
		createPriceProtocolForm.reset()
	})


	const addFormItemBtn = document.getElementById('addFormItem')
	addFormItemBtn.addEventListener('click', () => addProductItem())
})


// window.addEventListener("unload", () => {
// 	const date_fromInput = document.querySelector('#date_from')
// 	const date_toInput = document.querySelector('#date_to')

// 	// запоминаем даты для запроса данных из БД
// 	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
// })




function addProductItem() {
	const container = document.getElementById('productItemsContainer')
	const template = document.getElementById('productItemTemplate')

	const clone = template.content.cloneNode(true)
	clone.querySelectorAll('[data-name]').forEach((el) => {
		const field = el.getAttribute('data-name')
		el.setAttribute('name', `${field}_${formItemsIndex}`)
	})

	const deleteBtn = clone.querySelector('.btn-close')
	deleteBtn.addEventListener('click', removeProductItem)

	container.appendChild(clone)
	formItemsIndex++
}
function removeProductItem(e) {
	e.target.closest('.product-item').remove()
}


// обработчик отправки формы поиска
async function searchFormSubmitHandler(e) {
	try {
		e.preventDefault()
		gridOptions.api.showLoadingOverlay()
		const formData = new FormData(e.target)
		const data = Object.fromEntries(formData)
		const acceptanceQualityData = await getPriceProtocolData(data.date_from, data.date_to)
		updateTable(gridOptions, acceptanceQualityData)
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}
// обработчик отправки формы статуса карточки
function createPriceProtocolFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	console.log("🚀 ~ createPriceProtocolFormSubmitHandler ~ data:", data)

	const tradsformedData = transformFormData(data)
	const payload = {
		...tradsformedData,
		array: tradsformedData.array.map(getFormatedPayload)
	}
	console.log("🚀 ~ createPriceProtocolFormSubmitHandler ~ payload:", payload)
	// const payload = getFormatedPayload(data)
	// console.log("🚀 ~ createPriceProtocolFormSubmitHandler ~ payload:", payload)

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		token,
		url: createArrayOfPriceProtocolUrl,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				const data = await getPriceProtocolData()
				updateTable(gridOptions, data)
				$('#createPriceProtocolModal').modal('hide')
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '100') {
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

function getFormatedPayload(data) {
	return {
		...data,
		priceProducer: data.priceProducer ? Number(data.priceProducer) : null,
		costImporter: data.costImporter ? Number(data.costImporter) : null,
		markupImporterPercent: data.markupImporterPercent ? Number(data.markupImporterPercent) : null,
		discountPercent: data.discountPercent ? Number(data.discountPercent) : null,
		wholesaleDiscountPercent: data.wholesaleDiscountPercent ? Number(data.wholesaleDiscountPercent) : null,
		priceWithoutVat: data.priceWithoutVat ? Number(data.priceWithoutVat) : null,
		wholesaleMarkupPercent: data.wholesaleMarkupPercent ? Number(data.wholesaleMarkupPercent) : null,
		vatRate: data.vatRate ? Number(data.vatRate) : null,
		priceWithVat: data.priceWithVat ? Number(data.priceWithVat) : null,
		shelfLifeDays: data.shelfLifeDays ? Number(data.shelfLifeDays) : null,
		currentPrice: data.currentPrice ? Number(data.currentPrice) : null,
		priceChangePercent: data.priceChangePercent ? Number(data.priceChangePercent) : null,
	}
}

function transformFormData(data) {
	const result = {
		array: [],
	}

	const itemMap = {}

	for (const [key, value] of Object.entries(data)) {
		const match = key.match(/^(.+?)_(\d+)$/)

		if (match) {
			const field = match[1]
			const index = match[2]

			if (!itemMap[index]) {
				itemMap[index] = {}
			}

			itemMap[index][field] = value
		} else {
			result[key] = value
		}
	}

	result.array = Object.entries(itemMap)
		.sort(([a], [b]) => Number(a) - Number(b))
		.map(([, value]) => value)

	return result;
}


// получение данных
async function getPriceProtocolData() {
	try {
		const url = `${getPriceProtocolListUrl}`
		const res = await getData(url)
		return res && res.object ? res.object : []
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}

// методы таблицы
function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showNoRowsOverlay()
}
function updateTable(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(mapCallback)
}
function mapCallback(item) {
	return {
		...item,
	};
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const items = [
		{
			name: "Сбросить настройки колонок",
			action: () => {
				gridColumnLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
		},
		{
			name: `Сбросить настройки фильтров`,
			action: () => {
				gridFilterLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
		},
		"separator",
		"excelExport"
	]

	return items
}

// конверторы дат для таблицы
function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}
function dateValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDate(date)
}
function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
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

// отображение модального окна с сообщением
function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerHTML = message
	$('#displayMessageModal').modal('show')
}

// отображение модального окна с
function showCreatePriceProtocolModal(card) {
	if (!card) return
	approveCardForm.idAcceptanceFoodQuality.value = card.idAcceptanceFoodQuality
	approveCardForm.idAcceptanceQualityFoodCard.value = card.idAcceptanceQualityFoodCard
	$('#createPriceProtocolModal').modal('show')
}


function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] }
	)
}