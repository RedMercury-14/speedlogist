import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { dateComparator, dateValueFormatter, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, disableButton, enableButton, getData, hideLoadingSpinner, isAdmin, isOrderSupport, showLoadingSpinner } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { uiIcons } from './uiIcons.js'
import { ajaxUtils } from './ajaxUtils.js'
import { editProductControlUrl, getAllProductControlUrl, loadProductControlExcelUrl } from './globalConstants/urls.js'

const PAGE_NAME = 'goodAccommodations'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'grey-row': params => params.data && params.data.status === 10,
	'red-row': params => params.data && params.data.status === 30,
}

const columnDefs = [
	{
		headerName: "№", field: "idGoodAccommodation",
		sort: "desc", minWidth: 60, flex: 1,
	},
	{ headerName: "Код товара", field: "productCode", },
	{ headerName: "Штрихкод", field: "barcode", },
	{ headerName: "Наименование продукта", field: "goodName", flex: 4, },
	{ headerName: "Наименование товарной гр.", field: "productGroup", flex: 8, },
	{
		headerName: "Склады", field: "stocksToView",
		cellClass: "px-2 text-center font-weight-bold",
	},
	{
		headerName: "Дата создания", field: "dateCreate",
		valueFormatter: dateValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateValueFormatter, },
	},
	{
		headerName: "Статус", field: "status",
		cellClass: "px-2 text-center font-weight-bold",
		valueFormatter: getStatusToView,
		filterParams: { valueFormatter: getStatusToView, },
	},
	{ headerName: "Инициатор", field: "initiatorName", },
	{ headerName: "Email инициатора", field: "initiatorEmail", },
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		flex: 2,
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
	getRowId: (params) => params.data.idGoodAccommodation,
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	},
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

	const data = await getGoodAccommodationsData()
	updateTable(gridOptions, data)

	editProductControlForm.addEventListener('submit', editProductControlFormSubmitHandler)
	sendExcelForm.addEventListener('submit', sendExcelFormHandler)

	$('#editProductControlModal').on('hidden.bs.modal', (e) => {
		editProductControlForm.reset()
	})

})


// обработчик отправки формы загрузки таблицы эксель
function sendExcelFormHandler(e) {
	e.preventDefault()

	if (!isAdmin(role)) return

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)
	disableButton(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadProductControlExcelUrl,
		data: file,
		successCallback: async (res) => {
			hideLoadingSpinner(submitButton, 'Загрузить')
			enableButton(submitButton)

			if (res.status === '200') {
				const data = await getGoodAccommodationsData()
				updateTable(gridOptions, data)
				snackbar.show('Данные успешно загружены')
				e.target.reset()
				$(`#sendExcelModal`).modal('hide')
				return
			}

			if (res.status === '100') {
				const errorMessage = res.message || 'Ошибка загрузки данных'
				snackbar.show(errorMessage)
				return
			}
		},
		errorCallback: () => {
			hideLoadingSpinner(submitButton, 'Загрузить')
			enableButton(submitButton)
		}
	})
}
// обработчик отправки формы статуса карточки
function editProductControlFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	console.log("🚀 ~ editProductControlFormSubmitHandler ~ data:", data)
	// const payload = {
	// 	goodId: Number(data.goodId),
	// 	stock: Number(data.stock)
	// }

	// const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	// ajaxUtils.postJSONdata({
	// 	token,
	// 	url: editProductControlUrl,
	// 	data: payload,
	// 	successCallback: async (res) => {
	// 		clearTimeout(timeoutId)
	// 		bootstrap5overlay.hideOverlay()

	// 		if (res.status === '200') {
	// 			const data = await getGoodAccommodationsData()
	// 			updateTable(gridOptions, data)
	// 			$('#editProductControlModal').modal('hide')
	// 			res.message && snackbar.show(res.message)
	// 			return
	// 		}

	// 		if (res.status === '100') {
	// 			const message = res.message ? res.message : 'Неизвестная ошибка'
	// 			snackbar.show(message)
	// 			return
	// 		}
	// 	},
	// 	errorCallback: () => {
	// 		clearTimeout(timeoutId)
	// 		bootstrap5overlay.hideOverlay()
	// 	}
	// })
}

function editProductControl(payload) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: editProductControlUrl,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				console.log(res)

				const data = res.object
				const productControl = mapCallback(data)
				updateTableRow(gridOptions, productControl)
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

// получение данных
async function getGoodAccommodationsData() {
	try {
		const url = `${getAllProductControlUrl}`
		const res = await getData(url)
		return res && res.objects ? res.objects : []
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}

// методы таблицы
function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
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
	const stocksToView = item.stocks ? item.stocks.split(';').filter(Boolean).join(', ') : ''
	return {
		...item,
		stocksToView,
	};
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const status = rowNode.data.status

	const items = [
		{
			name: "Подтвердить",
			disabled: (!isAdmin(role) && !isOrderSupport(role)) || status === 20,
			action: () => {
				confirmProductControl(rowNode.data)
			},
			icon: uiIcons.check,
		},
		{
			name: "Отменить",
			disabled: (!isAdmin(role) && !isOrderSupport(role)) || status === 30,
			action: () => {
				cancelProductControl(rowNode.data)
			},
			icon: uiIcons.trash,
		},
		{
			name: "Редактировать список складов",
			disabled: (!isAdmin(role) && !isOrderSupport(role)),
			action: () => {
				editStocks(rowNode.data)
			},
			icon: uiIcons.pencil,
		},
		// {
		// 	name: "Редактировать",
		// 	disabled: (!isAdmin(role) && !isOrderSupport(role)),
		// 	action: () => {
		// 		openEditProductControlForm(rowNode.data)
		// 	},
		// 	icon: uiIcons.pencil,
		// },
		"separator",
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

// статусы строк
function getStatusToView(params) {
	const status = params.value
	switch (status) {
		case 10:
			return "Ожидает подтверждения";
		case 20:
			return "Действует";
		case 30:
			return "Неактивен";
		default:
			return `Неизвестный статус (${status})`;
	}
}

// подтверждение правила
async function confirmProductControl(rowData) {
	const payload = {
		idGoodAccommodation: rowData.idGoodAccommodation,
		barcode: rowData.barcode,
		goodName: rowData.goodName,
		productCode: rowData.productCode,
		productGroup: rowData.productGroup,
		status: 20,
		stocks: rowData.stocks,
	}

	editProductControl(payload)
}
// отмена правила
async function cancelProductControl(rowData) {
	const payload = {
		idGoodAccommodation: rowData.idGoodAccommodation,
		barcode: rowData.barcode,
		goodName: rowData.goodName,
		productCode: rowData.productCode,
		productGroup: rowData.productGroup,
		status: 30,
		stocks: rowData.stocks,
	}

	editProductControl(payload)
}
// изменение номеров складов для правила
async function editStocks(rowData) {
	// номера складов от 2 до 5 цифр через запятую
	const regex = /^(?!.*\b0\d{1,4}\b)\b[1-9]\d{1,4}\b(?:,\b[1-9]\d{1,4}\b)*$/

	const stocksToView = rowData.stocksToView.replaceAll(' ','')
	const productCode = rowData.productCode

	const value = prompt(
		`Введите номер склада для продукта с кодом ${productCode}. `
		+ `Для указания нескольких складов введите их номера через ЗАПЯТУЮ (без пробела):`
		+ `\nСтарое значение: ${stocksToView}`
	)

	if (!value) return
	if (!regex.test(value)) {
		snackbar.show('Некорректный ввод. Введите корректные номера складов через запятую (без пробела).')
		return
	}

	let newStocks = value.replaceAll(',', ';')
	newStocks = ';' + newStocks + ';'

	const payload = {
		idGoodAccommodation: rowData.idGoodAccommodation,
		barcode: rowData.barcode,
		goodName: rowData.goodName,
		productCode: rowData.productCode,
		productGroup: rowData.productGroup,
		status: rowData.status,
		stocks: newStocks,
	}

	editProductControl(payload)
}

// редактирование правила через форму
function openEditProductControlForm(rowData) {
	setDataToForm(rowData)
	$('#editProductControlModal').modal('show')
}

// заполнение формы редактирования
function setDataToForm(data) {
	editProductControlForm.idGoodAccommodation.value = data.idGoodAccommodation ? data.idGoodAccommodation : ''
	editProductControlForm.dateCreate.value = data.dateCreate ? data.dateCreate : ''
	editProductControlForm.status.value = data.status ? data.status : ''
	editProductControlForm.initiatorName.value = data.initiatorName ? data.initiatorName : ''
	editProductControlForm.initiatorEmail.value = data.initiatorEmail ? data.initiatorEmail : ''

	// заполняем видимые поля
	editProductControlForm.productCode.value = data.productCode ? data.productCode : ''
	editProductControlForm.barcode.value = data.barcode ? data.barcode : ''
	editProductControlForm.goodName.textContent = data.goodName ? data.goodName : ''
	editProductControlForm.productGroup.textContent = data.productGroup ? data.productGroup : ''
	editProductControlForm.stocks.value = data.stocksToView ? data.stocksToView.replaceAll(' ','') : ''
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


function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [ rowData ] }
	)
}