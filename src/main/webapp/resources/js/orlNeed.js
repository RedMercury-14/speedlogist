import { AG_GRID_LOCALE_RU } from "./AG-Grid/ag-grid-locale-RU.js"
import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { snackbar } from "./snackbar/snackbar.js"
import { dateHelper, getData, hideLoadingSpinner, isObserver, showLoadingSpinner } from "./utils.js"

const getOrlNeedBaseUrl = `../../api/orl/need/getNeed/`
const excelUrl = `../../api/orl/need/load`
const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('#role').value

let table
let orlNeedData

const columnDefs = [
	{
		headerName: 'ID', field: 'idOrderProduct',
		cellClass: 'px-1 py-0 text-center',
		hide: true,
	},
	{
		headerName: 'Код товара', field: 'codeProduct',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Наименование товара', field: 'nameProduct',
		cellClass: 'px-1 py-0 text-center',
		flex: 5,
	},
	{
		headerName: 'Колличество в поддоне', field: 'quantityInPallet',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Заказ (остальные склады)', field: 'quantity',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Заказ 1700', field: 'quantity1700',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Заказ 1800', field: 'quantity1800',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Увеличенный заказ 1700', field: 'quantity1700Max',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Увеличенный заказ 1800', field: 'quantity1800Max',
		cellClass: 'px-1 py-0 text-center',
		flex: 2,
	},
	{
		headerName: 'Комментарий', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		flex: 3,
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
	const filterDateInput = document.querySelector('#filterDate')
	// кнопка переключения даты вперед
	const datePrevBtn = document.querySelector('#datePrev')
	// кнопка переключения даты назад
	const dateNextBtn = document.querySelector('#dateNext')

	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions, orlNeedData)

	// установка фильтра даты
	const filterDate = dateHelper.getDateForInput(new Date())
	setFilterDate(filterDate)

	// обработка изменений фильтра даты
	filterDateInput && filterDateInput.addEventListener('change', (e) => {
		const filterDate = e.target.value
		updateTable(gridOptions, filterDate)
	})

	// переключение отображаемой даты
	datePrevBtn.addEventListener('click', () => prevDate(filterDateInput))
	dateNextBtn.addEventListener('click', () => nextDate(filterDateInput))

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
	gridOptions.api.showLoadingOverlay()

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

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

	const submitButton = e.submitter
	if (!submitButton) return
	const submitButtonText = submitButton.innerText
	const formData = new FormData(e.target)

	const date = formData.get('date')

	bootstrap5overlay.showOverlay()

	ajaxUtils.postMultipartFformData({
		url: excelUrl,
		token: token,
		data: formData,
		successCallback: (res) => {
			bootstrap5overlay.hideOverlay()

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
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}

// установка даты для отображения данных
function setFilterDate(value) {
	const filterDateInput = document.querySelector('#filterDate')
	filterDateInput && (filterDateInput.value = value)
}

// функции управдения датой
function nextDate(dateInput) {
	const currentDate = dateInput.value
	const nextMs = new Date(currentDate).getTime() + dateHelper.DAYS_TO_MILLISECONDS
	const nextDate = dateHelper.getDateForInput(nextMs)

	dateInput.value = nextDate
	dateInput.dispatchEvent(new Event("change"))
}
function prevDate(dateInput) {
	const currentDate = dateInput.value
	const prevMs = new Date(currentDate).getTime() - dateHelper.DAYS_TO_MILLISECONDS
	const prevDate = dateHelper.getDateForInput(prevMs)

	dateInput.value = prevDate
	dateInput.dispatchEvent(new Event("change"))
}