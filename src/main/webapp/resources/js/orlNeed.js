import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from "./AG-Grid/ag-grid-utils.js"
import { ajaxUtils } from "./ajaxUtils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { changeGridTableMarginTop, dateHelper, debounce, getData, hideLoadingSpinner, showLoadingSpinner } from "./utils.js"

const getOrlNeedBaseUrl = `../../api/orl/need/getNeed/`
const excelUrl = `../../api/orl/need/load`
const token = $("meta[name='_csrf']").attr("content")

let table
let orlNeedData

const columnDefs = [
	{
		headerName: 'ID', field: 'idOrderProduct',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Количество заказанного товара', field: 'quantity',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Наименование товара', field: 'nameProduct',
		cellClass: 'px-1 py-0 text-center',
		flex: 5,
	},
	{
		headerName: 'Комментарий', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		flex: 5,
	},
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2',
		flex: 1,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
}

window.addEventListener('load', async () => {
	const filterDateInput = document.querySelector('#filterDate')
	// установка фильтра даты
	const filterDate = dateHelper.getDateForInput(new Date())
	filterDateInput && (filterDateInput.value = filterDate)

	filterDateInput && filterDateInput.addEventListener('change', (e) => {
		const filterDate = e.target.value
		updateTable(filterDate)
	})

	const res = await getData(getOrlNeedBaseUrl + filterDate)
	orlNeedData = res.body
	
	const gridDiv = document.querySelector('#myGrid')
	// изменение отступа для таблицы
	changeGridTableMarginTop()
	// создание таблицы
	renderTable(gridDiv, gridOptions, orlNeedData)

	const sendExcelForm = document.querySelector('#sendExcelForm')
	sendExcelForm && sendExcelForm.addEventListener('submit', sendExcelFormHandler)
})

function renderTable(gridDiv, gridOptions, data) {
	table = new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
async function updateTable(filterDate) {
	if (!filterDate) {
		filterDate = dateHelper.getDateForInput(new Date())
	}
	const res = await getData(getOrlNeedBaseUrl + filterDate)
	orlNeedData = res.body

	if (!orlNeedData || !orlNeedData.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(orlNeedData)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(getMappingScheduleItem)
}
function getMappingScheduleItem(item) {
	return {
		...item,
	}
}

function sendExcelFormHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	if (!submitButton) return
	const submitButtonText = submitButton.innerText
	const formData = new FormData(e.target)

	const date = formData.get('date')

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: excelUrl,
		token: token,
		data: formData,
		successCallback: (res) => {
			snackbar.show(res[200])
			updateTable(date)
			$(`#sendExcelModal`).modal('hide')
			hideLoadingSpinner(submitButton, submitButtonText)
		},
		errorCallback: () => hideLoadingSpinner(submitButton, submitButtonText)
	})
}
