import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { ajaxUtils } from './ajaxUtils.js'
import { testData } from './delData.js'
import { snackbar } from "./snackbar/snackbar.js"
import { uiIcons } from './uiIcons.js'
import { changeGridTableMarginTop, getData, hideLoadingSpinner, showLoadingSpinner } from './utils.js'

const loadExcelUrl = '../../api/slots/delivery-schedule/load'


const token = $("meta[name='_csrf']").attr("content")

let error = false
let table

const options1 = [
	"",
	"з",
	"Понедельник",
	"Вторник",
	"Среда",
	"Четверг",
	"Пятница",
	"Суббота",
	"Воскресенье",
	"з/Понедельник",
	"з/Вторник",
	"з/Среда",
	"з/Четверг",
	"з/Пятница",
	"з/Суббота",
	"з/Воскресенье"
]
const options2 = [
	"",
	"з",
	"з/н0/Понедельник",
	"з/н0/Вторник",
	"з/н0/Среда",
	"з/н0/Четверг",
	"з/н0/Пятница",
	"з/н0/Суббота",
	"з/н0/Воскресенье",
	"з/н1/Понедельник",
	"з/н1/Вторник",
	"з/н1/Среда",
	"з/н1/Четверг",
	"з/н1/Пятница",
	"з/н1/Суббота",
	"з/н1/Воскресенье",
	"з/н2/Понедельник",
	"з/н2/Вторник",
	"з/н2/Среда",
	"з/н2/Четверг",
	"з/н2/Пятница",
	"з/н2/Суббота",
	"з/н2/Воскресенье",
	"н0/Понедельник",
	"н0/Вторник",
	"н0/Среда",
	"н0/Четверг",
	"н0/Пятница",
	"н0/Суббота",
	"н0/Воскресенье",
	"н1/Понедельник",
	"н1/Вторник",
	"н1/Среда",
	"н1/Четверг",
	"н1/Пятница",
	"н1/Суббота",
	"н1/Воскресенье",
	"н2/Понедельник",
	"н2/Вторник",
	"н2/Среда",
	"н2/Четверг",
	"н2/Пятница",
	"н2/Суббота",
	"н2/Воскресенье"
]

const columnDefs = [
	{
		headerName: 'Код контрагента', field: 'counterpartyCode',
		cellClass: 'px-1 py-0 text-center',
		width: 120,
	},
	{
		headerName: 'Наименование контрагента', field: 'name',
		cellClass: 'px-1 py-0 text-center',
		width: 300,
	},
	{
		headerName: 'Номер контракта', field: 'counterpartyContractCode',
		cellClass: 'px-1 py-0 text-center',
		width: 150,
	},
	{
		headerName: 'Пометка "Сроки/Неделя"', field: 'note',
		cellClass: 'px-1 py-0 text-center',
		width: 125,
	},
	{
		headerName: 'Пн', field: 'monday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Вт', field: 'tuesday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Ср', field: 'wednesday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Чт', field: 'thursday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Пт', field: 'friday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Сб', field: 'saturday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Вс', field: 'sunday',
		cellClass: 'px-1 py-0 text-center',
		width: 135,
	},
	{
		headerName: 'Кол-во поставок', field: 'supplies',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
	// {
	// 	headerName: 'Расчет стока до Y-ой поставки', field: 'stockCalculation',
	// 	cellClass: 'px-1 py-0 text-center',
	// 	width: 100,
	// },
	{
		headerName: 'Примечание', field: 'comment',
		cellClass: 'px-1 py-0 text-center',
		width: 110,
	},
	{
		headerName: 'Кратно поддону', field: 'multipleOfPallet',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
	{
		headerName: 'Кратно машине', field: 'multipleOfTruck',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
	{
		headerName: 'Номер склада', field: 'numStock',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Описание контракта', field: 'description',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'Дата последнего расчета', field: 'dateLasCalculation',
		cellClass: 'px-1 py-0 text-center',
	},
	{
		headerName: 'tz', field: 'tz',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
	{
		headerName: 'tp', field: 'tp',
		cellClass: 'px-1 py-0 text-center',
		width: 75,
	},
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2',
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
	// const addShopForm = document.querySelector("#addShopForm")
	const sendExcelForm = document.querySelector("#sendExcelForm")
	// const editShopForm = document.querySelector("#editShopForm")
	const gridDiv = document.querySelector('#myGrid')

	const data = testData

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	renderTable(gridDiv, gridOptions, data)

	// addShopForm.addEventListener("submit", (e) => addShopFormHandler(e))
	sendExcelForm.addEventListener("submit", (e) => sendExcelFormHandler(e))
	// editShopForm.addEventListener("submit", (e) => editShopFormHandler(e))
	// $('#numshop').change(checkSopNumber)
	// $('#addShopModal').on('hidden.bs.modal', (e) => addShopForm.reset())
	// $('#editShopModal').on('hidden.bs.modal', (e) => editShopForm.reset())
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
	const data = testData

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

	return data
}
function getContextMenuItems(params) {
	// const shop = params.node.data
	// const numshop = params.node.data.numshop
	const result = [
		{
			name: `Редактировать поставку`,
			action: () => {
				editDelivery()
			},
			icon: uiIcons.pencil,
		},
		// {
		// 	name: `Удалить магазин`,
		// 	action: () => {
		// 		deleteShop(numshop)
		// 	},
		// 	icon: uiIcons.trash,
		// },
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
function sendExcelFormHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: loadExcelUrl,
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
function editDelivery(shop) {
	// setEditShopForm(shop)
	$(`#editDeliveryModal`).modal('show')
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
