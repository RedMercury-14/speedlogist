import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { addShopUrl, checkExistShopBaseUrl, deleteShopUrl, editShopUrl, getAllShopsUrl, getCounterpartiesListUrl } from './globalConstants/urls.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { blurActiveElem, changeGridTableMarginTop, createOptions, getData, groupBy, hideLoadingSpinner, isAdmin, showLoadingSpinner } from './utils.js'

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector("#login").value
const role = document.querySelector("#role").value

// логины, которым разрешено редактирование
const editableLogins = [
	'olga!%logist',
	'slesarevi!%power',
	'alexandra!%adam',
	'toplog',
]

let error = false
let table
let counterpartiesListByName
let counterpartiesListByCode

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
		headerName: 'Наименование поставщика', field: 'name',
		flex: 6,
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


document.addEventListener('DOMContentLoaded', async () => {
	// данные по контрагентам
	const counterpartiesList = await getCounterpartiesList()
	counterpartiesListByName = groupBy(counterpartiesList, 'name')
	counterpartiesListByCode = groupBy(counterpartiesList, 'counterpartyCode')

	// заполнение списка контрагентов
	const counterpartiesListElem = document.getElementById('counterpartiesList')
	const nameList = Object.keys(counterpartiesListByName)
	createOptions(nameList, counterpartiesListElem)

	// несколько кодов контрагентов
	const errors = nameList.reduce((acc, key) => {
		const codes = counterpartiesListByName[key]
		if (codes.length > 1) {
			acc[key] = codes
		}
		return acc
	}, {})
	console.log(errors)

	bootstrap5overlay.hideOverlay()

	// скрытие кнопок редактирования
	if (!isEditable(login, role)) hideEditableButtons()

	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	const addShopForm = document.querySelector("#addShopForm")
	const editShopForm = document.querySelector("#editShopForm")
	addShopForm.addEventListener("submit", (e) => addShopFormHandler(e))
	editShopForm.addEventListener("submit", (e) => editShopFormHandler(e))

	const addCounterpartyNameInput = addShopForm.querySelector('#name')
	const addCounterpartyCodeInput = addShopForm.querySelector('#numshop')
	addCounterpartyNameInput.addEventListener('change', (e) => counterpartyNameChangeHandler(e, addCounterpartyCodeInput))
	
	$('#numshop').change(checkShopNumber)
	$('#addShopModal').on('hidden.bs.modal', (e) => {
		blurActiveElem(e)
		addShopForm.reset()
	})
	$('#editShopModal').on('hidden.bs.modal', (e) => {
		blurActiveElem(e)
		editShopForm.reset()
	})

	// отображение стартовых данных
	if (window.initData) {
		await initStartData()
	} else {
		// подписка на кастомный ивент загрузки стартовых данных
		document.addEventListener('initDataLoaded', async () => {
			await initStartData()
		})
	}

})


// установка стартовых данных
async function initStartData() {
	const supplies = window.initData.filter(item => item.type === 'Поставщик')
	await updateTable(gridOptions, supplies)
	window.initData = null
}

// получение данных по контрагентам
async function getCounterpartiesList() {
	const counterpartiesListData = await getData(getCounterpartiesListUrl)
	return counterpartiesListData.list ? counterpartiesListData.list : []
}

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}
async function updateTable(gridOptions, data) {
	const shops = data
		? data
		: await getData(getAllShopsUrl)

	if (!shops || !shops.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const supplies = shops.filter(item => item.type === 'Поставщик')
	const mappingData = getMappingData(supplies)

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
			disabled: !isEditable(login, role),
			name: `Редактировать поставщика`,
			action: () => {
				editShop(shop)
			},
			icon: uiIcons.pencil,
		},
		{
			disabled: !isEditable(login, role),
			name: `Удалить поставщика`,
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

// обработчик изменения поля наименование контрагента
function counterpartyNameChangeHandler(e, addCounterpartyCodeInput) {
	const value = e.target.value
	const counterpartyCodes = counterpartiesListByName[value]
	if (counterpartyCodes) addCounterpartyCodeInput.value = counterpartyCodes[0].counterpartyCode
	// проверка номера
	checkShopNumber({ target: addCounterpartyCodeInput })
}

// обработчик отправки формы добавления поставщика
function addShopFormHandler(e) {
	e.preventDefault()

	if (!isEditable(login, role)) return

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
			updateTable(gridOptions)
			$(`#addShopModal`).modal('hide')
		}
	})
}

// обработчик отправки формы редактирования поставщика
function editShopFormHandler(e) {
	e.preventDefault()

	if (!isEditable(login, role)) return

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
			updateTable(gridOptions)
			$(`#editShopModal`).modal('hide')
		}
	})
}

// форматирование данных поставщика для отправки на сервер
function shopFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)
	// const address = data.address.includes(data.type)
	// 	? data.address : data.type + ' ' + data.address

	return {
		...data,
		// address,
		numshop: data.numshop ? Number(data.numshop) : '',
		maxPall: data.maxPall ? Number(data.maxPall) : '',
		width: data.width ? Number(data.width) : '',
		length: data.length ? Number(data.length) : '',
		height: data.height ? Number(data.height) : '',
		isTailLift: data.isTailLift ? 'true' : 'false',
		isInternalMovement: data.isInternalMovement ? 'true' : 'false',
	}
}

// проверка наличия наличия номера/кода в базе
async function checkShopNumber(e) {
	console.log("🚀 ~ checkShopNumber ~ e:", e)
	const input = e.target
	const hasShop = await getData(`${checkExistShopBaseUrl}${input.value}`)

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

// редактирование поставщика
function editShop(shop) {
	setEditShopForm(shop)
	$(`#editShopModal`).modal('show')
}

// удаление поставщика
function deleteShop(numshop) {
	if (!isEditable(login, role)) return

	ajaxUtils.postJSONdata({
		url: deleteShopUrl,
		token: token,
		data: { numshop: numshop },
		successCallback: (res) => {
			snackbar.show(res.message)
			updateTable(gridOptions)
			$(`#addShopModal`).modal('hide')
		}
	})
}

// заполнение формы редактирования поставщика данными
function setEditShopForm(shop) {
	const editShopForm = document.querySelector("#editShopForm")

	const code = shop.numshop
	const counterpartyName = shop.name
	
	editShopForm.name.value = counterpartyName
	editShopForm.numshop.value = code
	editShopForm.address.value = shop.address
	editShopForm.lat.value = shop.lat
	editShopForm.lng.value = shop.lng

	const typeOptions = editShopForm.type.options
	for (let i = 0; i < typeOptions.length; i++) {
		const option = typeOptions[i]
		if (option.value === shop.type) {
			option.selected = true
		}
	}
}

// проверка, разрешено ли редактирование
function isEditable(login, role) {
	return isAdmin(role) || editableLogins.includes(login)
}
function hideEditableButtons() {
	const addShopBtn = document.querySelector('#addShopBtn')
	addShopBtn.classList.add('d-none')
}