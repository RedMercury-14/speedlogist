import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { ajaxUtils } from './ajaxUtils.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { changeGridTableMarginTop, getData, hideLoadingSpinner, showLoadingSpinner } from './utils.js'

const getAllShopsUrl = '../../api/manager/getAllShops'
const loadShopsUrl = '../../api/map/loadShop'
const addShopUrl = "../../api/manager/addShop"
const editShopUrl = "../../api/manager/editShop"
const deleteShopUrl = '../../api/manager/deleteShop'

const token = $("meta[name='_csrf']").attr("content")

let error = false
let table

const columnDefs = [
	{ 
		headerName: 'Номер', field: 'numshop',
		cellClass: 'px-2 text-center', flex: 1
	},
	{ 
		headerName: 'Тип', field: 'type',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Адрес', field: 'address',
		flex: 6,
	},
	{ 
		headerName: 'Широта', field: 'lat',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Долгота', field: 'lng',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Гидроборт', field: 'isTailLift',
		cellClass: 'px-2 text-center',
		cellRenderer: params => params.value ? 'Да' : '',
	},
	{ 
		headerName: 'Внутренние', field: 'isInternalMovement',
		cellClass: 'px-2 text-center',
		cellRenderer: params => params.value ? 'Да' : '',
	},
	// { 
	// 	headerName: 'Ежедневные чистки', field: 'cleaning',
	// 	cellClass: 'px-2 text-center',
	// 	valueFormatter: params => params.value ? 'Да' : 'Нет',
	// },
	{ 
		headerName: 'Ограничение по паллетам', field: 'maxPall',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Органичения по ширине', field: 'width',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Органичения по длине', field: 'length',
		cellClass: 'px-2 text-center',
	},
	{ 
		headerName: 'Органичения по высоте', field: 'height',
		cellClass: 'px-2 text-center',
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
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
}

window.onload = async () => {
	const addShopForm = document.querySelector("#addShopForm")
	const addShopsInExcelForm = document.querySelector("#addShopsInExcelForm")
	const editShopForm = document.querySelector("#editShopForm")
	const gridDiv = document.querySelector('#myGrid')

	const shops = await getData(getAllShopsUrl)

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	renderTable(gridDiv, gridOptions, shops)

	addShopForm.addEventListener("submit", (e) => addShopFormHandler(e))
	addShopsInExcelForm.addEventListener("submit", (e) => addShopsInExcelFormHandler(e))
	editShopForm.addEventListener("submit", (e) => editShopFormHandler(e))
	$('#numshop').change(checkSopNumber)
	$('#addShopModal').on('hidden.bs.modal', (e) => addShopForm.reset())
	$('#editShopModal').on('hidden.bs.modal', (e) => editShopForm.reset())
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
	const shops = await getData(getAllShopsUrl)

	if (!shops || !shops.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(shops)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {

	return data
}
function getContextMenuItems(params) {
	const shop = params.node.data
	const numshop = params.node.data.numshop
	const result = [
		{
			name: `Редактировать магазин`,
			action: () => {
				editShop(shop)
			},
			icon: uiIcons.pencil,
		},
		{
			name: `Удалить магазин`,
			action: () => {
				deleteShop(numshop)
			},
			icon: uiIcons.trash,
		},
		"separator",
		"copy",
		"export",
	]

	return result
}

// обработчик отправки формы добавления магазина
function addShopFormHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = shopFormDataFormatter(formData)

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	ajaxUtils.postJSONdata({
		url: addShopUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			snackbar.show(res.message)
			updateTable()
			$(`#addShopModal`).modal('hide')
		}
	})
}

// обработчик отправки формы добавления магазинов в формате excel
function addShopsInExcelFormHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadShopsUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			snackbar.show(res[200])
			updateTable()
			$(`#addShopsInExcelModal`).modal('hide')
			hideLoadingSpinner(submitButton, 'Загрузить')
		},
		errorCallback: () => hideLoadingSpinner(submitButton, 'Загрузить')
	})
}

// обработчик отправки формы редактирования магазина
function editShopFormHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = shopFormDataFormatter(formData)

	if (error) {
		snackbar.show('Ошибка заполнения формы!')
		return
	}

	ajaxUtils.postJSONdata({
		url: editShopUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			snackbar.show(res.message)
			updateTable()
			$(`#editShopModal`).modal('hide')
		}
	})
}

// форматирование данных магазина для отправки на сервер
function shopFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	const address = data.address.includes(data.type)
		? data.address : data.type + ' ' + data.address

	// const cleaning = data.cleaning === 'Да'

	return {
		...data,
		address,
		// cleaning,
		numshop: data.numshop ? Number(data.numshop) : '',
		maxPall: data.maxPall ? Number(data.maxPall) : '',
		width: data.width ? Number(data.width) : '',
		length: data.length ? Number(data.length) : '',
		height: data.height ? Number(data.height) : '',
		isTailLift: data.isTailLift ? 'true' : 'false',
		isInternalMovement: data.isInternalMovement ? 'true' : 'false',
	}
}

// проверка наличия наличия номера магазина в базе
async function checkSopNumber(e) {
	const input = e.target
	const hasShop = await getData(`../../api/manager/existShop/${input.value}`)

	if (hasShop) {
		$('#messageNumshop').text('Такой магазин уже зарегистрирован')
		input.classList.add('is-invalid')
		error = true
	}
	else {
		$('#messageNumshop').text('')
		input.classList.remove('is-invalid')
		error = false
	}
}

// редактирование магазина
function editShop(shop) {
	setEditShopForm(shop)
	$(`#editShopModal`).modal('show')
}

// удаление магазина
function deleteShop(numshop) {
	ajaxUtils.postJSONdata({
		url: deleteShopUrl,
		token: token,
		data: { numshop: numshop },
		successCallback: (res) => {
			snackbar.show(res.message)
			updateTable()
			$(`#addShopModal`).modal('hide')
		}
	})
}

// заполнение формы редактирования магазина данными
function setEditShopForm(shop) {
	const editShopForm = document.querySelector("#editShopForm")
	const editShopModalLabel = document.querySelector("#editShopModalLabel")

	editShopModalLabel.innerText = `Редактирование точки №${shop.numshop}`
	editShopForm.numshop.value = shop.numshop
	editShopForm.address.value = shop.address
	editShopForm.lat.value = shop.lat
	editShopForm.lng.value = shop.lng
	editShopForm.maxPall.value = shop.maxPall
	editShopForm.width.value = shop.width
	editShopForm.length.value = shop.length
	editShopForm.height.value = shop.height
	editShopForm.isTailLift.checked = shop.isTailLift
	editShopForm.isInternalMovement.checked = shop.isInternalMovement

	const typeOptions = editShopForm.type.options
	for (let i = 0; i < typeOptions.length; i++) {
		const option = typeOptions[i]
		if (option.value === shop.type) {
			option.selected = true
		}
	}

	// const cleaningOptions = editShopForm.cleaning.options
	// for (let i = 0; i < cleaningOptions.length; i++) {
	// 	const option = cleaningOptions[i]
	// 	if (option.value === shop.cleaning) {
	// 		option.selected = true
	// 	}
	// }
}
