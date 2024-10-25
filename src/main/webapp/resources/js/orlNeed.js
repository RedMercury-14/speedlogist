import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ajaxUtils } from "./ajaxUtils.js"
import { snackbar } from "./snackbar/snackbar.js"
import { changeGridTableMarginTop, dateHelper, getData, hideLoadingSpinner, showLoadingSpinner } from "./utils.js"

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

document.addEventListener('DOMContentLoaded', async () => {
	// изменение отступа для таблицы
	changeGridTableMarginTop()

	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions, orlNeedData)

	// установка фильтра даты
	const filterDateInput = document.querySelector('#filterDate')
	const filterDate = dateHelper.getDateForInput(new Date())
	setFilterDate(filterDate)

	// обработка изменений фильтра даты
	filterDateInput && filterDateInput.addEventListener('change', (e) => {
		const filterDate = e.target.value
		updateTable(gridOptions, filterDate)
	})

	// обработка отправки формы отправки excel файла
	const sendExcelForm = document.querySelector('#sendExcelForm')
	sendExcelForm && sendExcelForm.addEventListener('submit', sendExcelFormHandler)

	// отображение стартовых данных
	if (window.initData) {
		await initStartData(filterDate)
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData(filterDate)
		})
	}
})

// установка стартовых данных
async function initStartData(filterDate) {
	orlNeedData = window.initData.body
	await updateTable(gridOptions, filterDate, orlNeedData)
	window.initData = null
}

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}
async function updateTable(gridOptions, filterDate, data) {
	if (!filterDate) {
		filterDate = dateHelper.getDateForInput(new Date())
	}

	const res = data
		? { body: data }
		: await getData(getOrlNeedBaseUrl + filterDate)

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
			hideLoadingSpinner(submitButton, submitButtonText)

			if (res.status === '200') {
				snackbar.show(res.message)
				setFilterDate(date)
				updateTable(gridOptions, date)
				$(`#sendExcelModal`).modal('hide')
			}

			if (res.status === '100') {
				snackbar.show(res.message)
				return
			}
		},
		errorCallback: () => hideLoadingSpinner(submitButton, submitButtonText)
	})
}

// установка даты для отображения данных
function setFilterDate(value) {
	const filterDateInput = document.querySelector('#filterDate')
	filterDateInput && (filterDateInput.value = value)
}
