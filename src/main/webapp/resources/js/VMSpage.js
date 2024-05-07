import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { dateComparator } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { dateHelper, getData, hideLoadingSpinner, showLoadingSpinner } from './utils.js'



const stocks = ['1700', '1250', '1200']
const dateStart = '2024-04-29'
const dateEnd = '2024-06-04'
const maxPall = [ 1300, 1400, 1500, 1600, 1700 ]

let currentId = 0

function getRandomStock() {
	return stocks[Math.floor(Math.random() * stocks.length)]
}

function getRandomDate(start, end) {
	return new Date(start.getTime() + Math.random() * (end.getTime() - start.getTime()))
}

function getRandomMaxPall() {
	return maxPall[Math.floor(Math.random() * maxPall.length)]
}

const data = []
for (let i = 0; i < 100; i++) {
	currentId++
	data.push({
		restrictionId: i + 1,
		numStock: getRandomStock(),
		date: dateHelper.getFormatDate(getRandomDate(new Date(dateStart), new Date(dateEnd))),
		maxPall: getRandomMaxPall(),
	})
}

const restrictions = data.reduce((acc, item) => {
	if (acc.find(i => i.numStock === item.numStock && i.date === item.date)) {
		return acc
	}
	acc.push(item)
	return acc
}, [])


const token = $("meta[name='_csrf']").attr("content")

let error = false
let table

const columnDefs = [
	{ 
		headerName: 'Дата', field: 'date',
		cellClass: 'px-2 text-center',
		sort: 'desc',
		comparator: dateComparator,
	},
	{ 
		headerName: 'Склад', field: 'numStock',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Паллетовместимость', field: 'maxPall',
		cellClass: 'px-2 text-center',
		valueFormatter: params => params.value + ' паллет',
	},
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2',
		flex: 2,
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
	getRowId: (params) => params.data.restrictionId,
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	animateRows: true,
}

window.onload = async () => {
	const numStockButtonsContainer = document.querySelector("#numStockButtons")
	const numStockButtons = numStockButtonsContainer.querySelectorAll(".btn")
	const addRestrictionForm = document.querySelector("#addRestrictionForm")
	const editRestrictionForm = document.querySelector("#editRestrictionForm")
	const gridDiv = document.querySelector('#myGrid')

	renderTable(gridDiv, gridOptions, restrictions)

	numStockButtonsContainer.addEventListener("click", (e) => numStockButtonsHandler(e, numStockButtons))
	addRestrictionForm.addEventListener("submit", (e) => addRestrictionFormHandler(e))
	editRestrictionForm.addEventListener("submit", (e) => editRestrictionFormHandler(e))
	$('#addRestrictionModal').on('hidden.bs.modal', (e) => addRestrictionForm.reset())
	$('#editRestrictionModal').on('hidden.bs.modal', (e) => editRestrictionForm.reset())
}

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
async function updateTable() {
	// const restrictions = await getData(getAllShopsUrl)

	// if (!shops || !shops.length) {
	// 	gridOptions.api.setRowData([])
	// 	gridOptions.api.showNoRowsOverlay()
	// 	return
	// }

	// const mappingData = getMappingData(shops)

	// gridOptions.api.setRowData(mappingData)
	// gridOptions.api.hideOverlay()
}
function getMappingData(data) {

	return data
}
function getContextMenuItems(params) {
	const rowNode = params.node
	const restriction = rowNode.data
	console.log("🚀 ~ getContextMenuItems ~ rowNode:", rowNode)
	const result = [
		{
			name: `Редактировать ограничение`,
			action: () => {
				editRestriction(restriction)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Удалить ограничение`,
			action: () => {
				deleteRestriction(restriction)
			},
			icon: uiIcons.trash,
		},
		"separator",
		"copy",
		"export",
	]

	return result
}


function numStockButtonsHandler(e, numStockButtons) {
	const target = e.target
	const numStock = target.dataset.stock

	if (!numStock) return

	if (target.classList.contains('btn-primary')) {
		showAllRestrictions()
		for (const button of numStockButtons) {
			button.classList.remove('btn-primary')
			button.classList.add('btn-outline-primary')
		}
		target.blur()
		return
	}

	if (target.classList.contains('btn')) {
		for (const button of numStockButtons) {
			if (button !== target) {
				button.classList.remove('btn-primary')
				button.classList.add('btn-outline-primary')
			}
		}
		target.classList.remove('btn-outline-primary')
		target.classList.add('btn-primary')
		target.blur()
		showStockRestrictions(numStock)
	}
}


// обработчик отправки формы добавления магазина
function addRestrictionFormHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = restrictionFormDataFormatter(formData)
	console.log("🚀 ~ addRestrictionFormHandler ~ data:", data)

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	const newItem = {
		restrictionId: currentId + 1,
		numStock: data.numStock,
		date: dateHelper.changeFormatToView(data.date),
		maxPall: Number(data.maxPall),
	}
	gridOptions.api.applyTransaction({ add: [newItem], addIndex: 0, })
	currentId++
	$(`#addRestrictionModal`).modal('hide')


// 	ajaxUtils.postJSONdata({
// 		url: addRestrictionUrl,
// 		token: token,
// 		data: data,
// 		successCallback: (res) => {
// 			snackbar.show(res.message)
// 			updateTable()
// 			$(`#addRestrictionModal`).modal('hide')
// 		}
// 	})
}


// обработчик отправки формы редактирования магазина
function editRestrictionFormHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = restrictionFormDataFormatter(formData)
	console.log("🚀 ~ editRestrictionFormHandler ~ data:", data)

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	updateCellData(data.restrictionId, 'maxPall', Number(data.maxPall))
	$(`#editRestrictionModal`).modal('hide')

	// ajaxUtils.postJSONdata({
	// 	url: editRestrictionUrl,
	// 	token: token,
	// 	data: data,
	// 	successCallback: (res) => {
	// 		snackbar.show(res.message)
	// 		updateTable()
	// 		$(`#editRestrictionModal`).modal('hide')
	// 	}
	// })
}

// форматирование данных магазина для отправки на сервер
function restrictionFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)

	return {
		...data
	}
}


// редактирование магазина
function editRestriction(restriction) {
	setEditRestrictionForm(restriction)
	$(`#editRestrictionModal`).modal('show')
}

// удаление магазина
function deleteRestriction(restriction) {
	const restrictionId = restriction.restrictionId
	gridOptions.api.applyTransaction({ remove: [{ restrictionId }] })

	// ajaxUtils.postJSONdata({
	// 	url: deleteRestrictionUrl,
	// 	token: token,
	// 	data: { numRestriction: numRestriction },
	// 	successCallback: (res) => {
	// 		snackbar.show(res.message)
	// 		updateTable()
	// 		$(`#addRestrictionModal`).modal('hide')
	// 	}
	// })
}

// заполнение формы редактирования магазина данными
function setEditRestrictionForm(restriction) {
	const editRestrictionForm = document.querySelector("#editRestrictionForm")
	const inputDate = dateHelper.changeFormatToInput(restriction.date)

	editRestrictionForm.restrictionId.value = restriction.restrictionId
	editRestrictionForm.date.value = inputDate
	editRestrictionForm.numStock.value = restriction.numStock
	editRestrictionForm.maxPall.value = restriction.maxPall
}


// показать ограничения выбранного склада
function showStockRestrictions(numStock) {
	if (!numStock) return
	const numStockFilterComponent = gridOptions.api.getFilterInstance('numStock')
	numStockFilterComponent.setModel({ values: [ numStock ] })
	gridOptions.api.onFilterChanged()
}
// показать все ограничения
function showAllRestrictions() {
	const numStockFilterComponent = gridOptions.api.getFilterInstance('numStock')
	numStockFilterComponent.setModel(null)
	gridOptions.api.onFilterChanged()
}

// обновить данные в строке таблицы
function updateCellData(rowId, columnName, newValue) {
	const rowNode = gridOptions.api.getRowNode(rowId)
	rowNode.setDataValue(columnName, newValue)
	gridOptions.api.flashCells({ rowNodes: [rowNode] })
}