import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { dateComparator } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { dateHelper, getData, hideLoadingSpinner, showLoadingSpinner } from './utils.js'

const stocksData = [
	{
		id: '1700',
		name: 'Склад 1700',
		address: '223065, Беларусь, Луговослободской с/с, Минский р-н, Минская обл., РАД М4, 18км. 2а, склад W05',
		contact: '+375293473695',
		workingHoursStart: '00:00',
		workingHoursEnd: '24:00',
		shiftChange: ['08:00', '09:00', '20:00', '21:00'],
		maxPall: 1300,
		weekends: [],
		ramps: [
			{ id: "170001", title: "Рампа 1", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170002", title: "Рампа 2", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170003", title: "Рампа 3", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170004", title: "Рампа 4", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170005", title: "Рампа 5", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170006", title: "Рампа 6 (Резерв)", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
	},
	{
		id: '1200',
		name: 'Склад 1200',
		address: '223039, Республика Беларусь, Минская область, Минский район, Хатежинский с/с, 1',
		contact: '+375447841737',
		workingHoursStart: '08:00',
		workingHoursEnd: '21:00',
		shiftChange: [],
		maxPall: 600,
		weekends: [],
		ramps: [
			{ id: "120001", title: "Рампа 1", businessHours: { startTime: '09:00', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120002", title: "Рампа 2", businessHours: { startTime: '09:00', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120003", title: "Рампа 3", businessHours: { startTime: '09:00', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
	},
	{
		id: '1250',
		name: 'Склад 1250',
		address: '223050, Республика Беларусь, Минская область, Минский р-н, 9-ый км Московского шоссе',
		contact: '+375291984537',
		workingHoursStart: '09:00',
		workingHoursEnd: '22:00',
		shiftChange: [],
		maxPall: 100,
		weekends: [],
		ramps: [
			{ id: "125001", title: "Рампа 1", businessHours: { startTime: '10:00', endTime: '21:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
	},
]

// ========================== тестовые данные ограничений ==========================
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
		date: dateHelper.getDateForInput(getRandomDate(new Date(dateStart), new Date(dateEnd))),
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
console.log("🚀 ~ restrictions ~ restrictions:", restrictions)
//=======================================================================================

const token = $("meta[name='_csrf']").attr("content")

let error = false
let table

const columnDefs = [
	{ 
		headerName: 'Дата', field: 'dateToView',
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


//=========================== структура склада ===============================
let rowData = []
let inputRow = {}

function setRowData(newData) {
	rowData = newData
	gridOptionsForEdit.api.setRowData(rowData)
}
function setInputRow(newData) {
	inputRow = newData
	gridOptionsForEdit.api.setPinnedTopRowData([inputRow])
}

const columnDefsForEdit = [
	{ field: 'structure', headerName: 'Название', },
	{ field: 'pallCount', headerName: 'Всего паллет', },
	{ field: 'freePall', headerName: 'Свободно паллет', },
]
const gridOptionsForEdit = {
	rowData: null,
	columnDefs: columnDefsForEdit,
	pinnedTopRowData: [inputRow],

	defaultColDef: {
		flex: 1,
		editable: true,
		valueFormatter: (params) =>
			isEmptyPinnedCell(params)
				? createPinnedCellPlaceholder(params)
				: undefined,
	},

	getRowStyle: ({ node }) =>
		node.rowPinned ? { 'font-weight': 'bold', 'font-style': 'italic' } : 0,

	onCellEditingStopped: (params) => {
		if (isPinnedRowDataCompleted(params)) {
			// save data
			setRowData([...rowData, inputRow])
			//reset pinned row
			setInputRow({})
		}
	},
}

function isEmptyPinnedCell({ node, value }) {
	return (node.rowPinned === 'top' && !value)
}
function createPinnedCellPlaceholder({ colDef }) {
	return colDef.headerName[0].toUpperCase() + colDef.headerName.slice(1) + '...'
}
function isPinnedRowDataCompleted(params) {
	if (params.rowPinned !== 'top') return
	return columnDefsForEdit.every((def) => inputRow[def.field])
}

// const gridDiv = document.querySelector('#editGrid')
// const editTable = new agGrid.Grid(gridDiv, gridOptionsForEdit)
// gridOptionsForEdit.api.setRowData([])
//============================================================================








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
	return data.map(restriction => {
		const dateToView = dateHelper.changeFormatToView(restriction.date)

		return {
			...restriction,
			dateToView,
		}
	})
}
function getContextMenuItems(params) {
	const rowNode = params.node
	const restriction = rowNode.data

	const result = [
		{
			name: `Добавить ограничение`,
			action: () => {
				$('#addRestrictionModal').modal('show')
			},
			icon: uiIcons.clickBoadrPlus,
		},
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
		const stock = stocksData.filter(stock => stock.id === numStock)[0]
		setStockInfo(stock)
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

	// проверка наличия ограничения по дате и складу
	if (checkRestriction(data)) {
		alert('Ограничение на эту дату для выбранного склада уже установлено! Для изменения ограничения выполните операцию редактирования')
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

// показать информацию о выбранном складе
function setStockInfo(stock) {
	const stockCard = document.querySelector("#stockCard")

	const shiftChange = getShiftChange(stock)

	const stockCardHTML = `
		<div class="card-header">
			<h1 class="text-center my-1">${stock.name}</h1>
		</div>
		<ul class="list-group list-group-flush" >
			<li class="list-group-item">Номер склада: <strong>${stock.id}</strong></li>
			<li class="list-group-item">Адрес: <strong>${stock.address}</strong></li>
			<li class="list-group-item">Время работы: <strong>${stock.workingHoursStart} - ${stock.workingHoursEnd}</strong></li>
			<li class="list-group-item">Контакт: <strong>${stock.contact}</strong></li>
			<li class="list-group-item">Рампы: <strong>${stock.ramps.length}</strong></li>
			<li class="list-group-item">Паллетовместимость по умолчанию: <span class="badge badge-info">${stock.maxPall}</span></li>
			<li class="list-group-item">Пересменка: <strong>${shiftChange}</strong></li>
		</ul>
	`

	stockCard.innerHTML = stockCardHTML
}
function getShiftChange(stock) {
	if (!stock.shiftChange) return 'Нет'
	const shiftChange = stock.shiftChange

	let html = ''

	for (let i = 0; i < shiftChange.length; i+=2) {
		html += `<span class="badge badge-danger">${shiftChange[i]} - ${shiftChange[i+1]}</span> `
		if (i < shiftChange.length - 2) html += ' '
	}

	if (html === '') return 'Нет'
	return html
}


// проверка наличия ограничения по дате и складу
function checkRestriction(data) {
	let res = false
	restrictions.forEach(restriction => {
		if (restriction.numStock === data.numStock && restriction.date === data.date) {
			res = true
		}
	})
	return res
}