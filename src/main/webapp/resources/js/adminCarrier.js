import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterSessionState } from './AG-Grid/ag-grid-utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { snackbar } from './snackbar/snackbar.js'
import { changeGridTableMarginTop, debounce, getData } from './utils.js'

const changeNumContractUrl = `../../api/manager/changeNumDocument`
const changeIsBlockedBaseUrl = `../../api/manager/blockCarrier/`
const PAGE_NAME = 'adminCarrier'
const LOCAL_STORAGE_KEY = `AG_Grid_setting_to_${PAGE_NAME}`
const token = $("meta[name='_csrf']").attr("content")

let localData = []
const fileModal = $('#fileModal')

class BtnCellRenderer {
	init(params) {
		this.params = params;

		this.eGui = document.createElement("button")
		this.eGui.className = 'btn'
		this.eGui.id = "showFileBtn"
		this.eGui.innerHTML = "Отобразить"

		this.btnClickedHandler = this.btnClickedHandler.bind(this)
		this.eGui.addEventListener("click", this.btnClickedHandler)
	}

	getGui() {
		return this.eGui
	}

	btnClickedHandler(event) {
		this.params.clicked(this.params)
	}

	destroy() {
		this.eGui.removeEventListener("click", this.btnClickedHandler)
	}
}
const cellClassRules = {
	'unConfirmed-cell': params => params.value.includes('Нет')
}
const rowClassRules = {
	'unConfirmed-row': params => params.api.getValue('isConfirmed', params.node).includes('Нет')
}

const debouncedSaveFilterState = debounce(saveFilterState, 300)
const debouncedSaveColumnState = debounce(saveColumnState, 300)

const columnDefs = [
	// { headerName: "Логин", field: "login", },
	{
		headerName: "Нименование юрлица", field: "companyName",
		cellRenderer: truckListLinkRenderer
	},
	{
		headerName: "Тип", field: "userType",
		cellClass: 'text-center',
	},
	{
		headerName: "Подтвержден?", field: "isConfirmed",
		width: 120, cellClass: 'text-center',
		cellClassRules: cellClassRules,
	},
	{ headerName: "УНП", field: "numYNP", },
	{
		headerName: "Номер договора", field: "numContract",
		width: 180,
		editable: true,
		onCellValueChanged: onNumContractChanged,
	},
	{ headerName: "Автопарк", field: "numCar", cellClass: 'text-center', },
	{ headerName: "Контактное лицо", field: "fio", },
	{ headerName: "E-mail контактного лица", field: "eMail", },
	{ headerName: "Телефон контактного лица", field: "telephone", },
	{
		headerName: "Заблокировать", field: "block",
		width: 120, cellClass: 'row-center',
		editable: true,
		onCellValueChanged: onIsBlockedChanged,
	},
	{ headerName: "TIR", field: "tir", width: 60, cellClass: 'text-center', },
	{ headerName: "Характеристики транспорта", field: "characteristicsOfTruks", },
	{ headerName: "Направления перевозок", field: "directionOfTransportation", },
	{ headerName: "Форма собственности", field: "propertySize", },
	{ headerName: "Страна регистрации", field: "countryOfRegistration", },
	{ headerName: "ФИО руководителя", field: "director", },
	{ headerName: "Свидетельство о регистрации", field: "registrationCertificate", },
	// { headerName: "Банковские реквизиты фирмы", field: "requisites", width: 400, cellClass: 'full-text' },
	{ headerName: "Дочерние компании", field: "affiliatedCompanies", },
	{
		headerName: "Документ", field: "other",
		cellClass: 'btn-center',
		cellRenderer: BtnCellRenderer,
		cellRendererParams: {
			clicked: onShowFileClicked
		},
	},
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	rowHeight: 50,
	columnHoverHighlight: true,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2',
		autoHeight: true,
		width: 180,
		wrapHeaderText: true,
		autoHeaderHeight: true,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
	},
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	onFilterChanged: debouncedSaveFilterState,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
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
		],
	},
}

window.onload = async function() {
	const gridDiv = document.querySelector('#myGrid')
	const allUsersBtn = document.querySelector("#allUsers")
	const confirmedUsersBtn = document.querySelector("#confirmedUsers")
	const unconfirmedUsersBtn = document.querySelector("#unconfirmedUsers")
	const blockedUsersBtn = document.querySelector("#blockedUsers")
	const unblockedUsersBtn = document.querySelector("#unblockedUsers")

	const getAllCarrierUrl = `../../api/manager/getAllCarrier`
	
	const carrier = await getData(getAllCarrierUrl)

	if (!localData.length) {
		localData = carrier
	}

	// изменение отступа для таблицы
	changeGridTableMarginTop()

	renderTable(gridDiv, gridOptions, carrier)
	restoreColumnState()
	restoreFilterState()

	allUsersBtn.onclick = () => showAllUsers()
	confirmedUsersBtn.onclick = () => showConfirmedUsers()
	unconfirmedUsersBtn.onclick = () => showUnConfirmedUsers()
	blockedUsersBtn.onclick = () => showBlockedUsers()
	unblockedUsersBtn.onclick = () => showUnBlockedUsers()

	bootstrap5overlay.hideOverlay()
}

// функция для блокироваки/разблокировки перевозчика
async function changeIsBlocked(idUser) {
	try {
		const res = await fetch(changeIsBlockedBaseUrl + idUser)
		if (res.ok) {
			const data = await res.json()
			const message = data.message.includes('true')
				? 'Перевозчик заблокирован' : 'Перевозчик разблокирован'
			snackbar.show(message)
		} else {
			snackbar.show('Упс! Что-то не так...')
			const data = await res.json()
			console.log(data)
		}
	} catch (error) {
		const errorStatus = error.status ? error.status : ''
		snackbar.show(`Ошибка ${errorStatus}!`)
		console.log(error)
	}
}

// функция для изменения номера договора перевозчика
function changeNumContract(data, successCallback) {
	ajaxUtils.postJSONdata({
		url: changeNumContractUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show(res.message)
				successCallback && successCallback()
			} else {
				snackbar.show('Упс! Что-то не так...')
			}
		}
	})
}

function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData(data.map(user => {
		const fio = `${user.surname != null ? user.surname: ''} ${user.name != null ? user.name: ''} ${user.patronymic != null ? user.patronymic : ''}`
		const tir = user.tir ? 'Да' : 'Нет'
		const check = user.check

		const userType = check.includes('international')
			? 'Международный' : check.includes('regional')
				? 'Региональный' : check

		const isConfirmed = check.includes('&new') ? 'Нет' : 'Да'

		return {
			...user,
			fio,
			tir,
			userType,
			isConfirmed,
			workingShift: user.workingShift ? user.workingShift.idWorkingShift : '',
			edit: user.idUser
		}
	}))
	gridOptions.api.hideOverlay()
	!data.length && gridOptions.api.showNoRowsOverlay()
}

function truckListLinkRenderer(params) {
	const data = params.node.data
	const isRegional = data.check.includes('regional')
	const link = `./controlpark?idCarrier=${data.idUser}`

	return isRegional
				? `<a class="text-primary" href="${link}">${params.value}</a>`
				: params.value
}

// функции для фильтрации кнопками в тулбаре
function showAllUsers() {
	gridOptions.api.setFilterModel(null)
	gridOptions.api.onFilterChanged()
}
function showBlockedUsers() {
	const blockFilterComponent = gridOptions.api.getFilterInstance('block')
	const isConfirmedFilterComponent = gridOptions.api.getFilterInstance('isConfirmed')
	isConfirmedFilterComponent.setModel(null)
	blockFilterComponent.setModel({ values: ['true'] })
	gridOptions.api.onFilterChanged()
}
function showUnBlockedUsers() {
	const blockFilterComponent = gridOptions.api.getFilterInstance('block')
	const isConfirmedFilterComponent = gridOptions.api.getFilterInstance('isConfirmed')
	isConfirmedFilterComponent.setModel(null)
	blockFilterComponent.setModel({ values: ['false'] })
	gridOptions.api.onFilterChanged()
}
function showConfirmedUsers() {
	const blockFilterComponent = gridOptions.api.getFilterInstance('block')
	const isConfirmedFilterComponent = gridOptions.api.getFilterInstance('isConfirmed')
	blockFilterComponent.setModel(null)
	const isConfirmedsIncludesNew = isConfirmedFilterComponent
		.getFilterKeys()
		.filter((val) => val && !val.includes('Нет'))

	isConfirmedFilterComponent.setModel({ values: isConfirmedsIncludesNew })
	gridOptions.api.onFilterChanged()
}
function showUnConfirmedUsers() {
	const blockFilterComponent = gridOptions.api.getFilterInstance('block')
	const isConfirmedFilterComponent = gridOptions.api.getFilterInstance('isConfirmed')
	blockFilterComponent.setModel(null)
	const isConfirmedsUnincludesNew = isConfirmedFilterComponent
		.getFilterKeys()
		.filter((val) => val && val.includes('Нет'))

	isConfirmedFilterComponent.setModel({ values: isConfirmedsUnincludesNew })
	gridOptions.api.onFilterChanged()
}

// коллбэки для изменение данных в таблицу
function onIsBlockedChanged(e) {
	const data = e.data
	changeIsBlocked(data.idUser)
}
function onNumContractChanged(e) {
	const data = e.data
	const rowNode = e.node
	const idUser = data.idUser
	const numcontract = data.numContract ? data.numContract : null
	const body = { idUser, numcontract }

	changeNumContract(body, onSuccsessNumContractCallback(rowNode, numcontract))
}
function onSuccsessNumContractCallback(rowNode, numContract) {
	const isConfirmedValue = numContract ? 'Да' : 'Нет'
	rowNode.setDataValue('isConfirmed', isConfirmedValue)
}

async function onShowFileClicked(params) {
	const numYNP = params.data.numYNP
	const body = fileModal[0].querySelector('.modal-body')

	const fileLink = await getImageLink(numYNP)

	if (fileLink) {
		const img = `<img src="${fileLink}" class="fit-image">`
		body.innerHTML = img
		fileModal.modal('show')
	} else {
		alert('Изображение отсутствует!')
	}
	
}

async function getImageLink(numYNP) {
	if (await checkImageLink(numYNP, 'jpg')) {
		return `../../resources/others/fileAgree/${numYNP}.jpg`
	} else if (await checkImageLink(numYNP, 'png')) {
		return `../../resources/others/fileAgree/${numYNP}.png`
	} else if (await checkImageLink(numYNP, 'jpeg')) {
		return `../../resources/others/fileAgree/${numYNP}.jpeg`
	} else if (await checkImageLink(numYNP, 'JPG')) {
		return `../../resources/others/fileAgree/${numYNP}.JPG`
	} else if (await checkImageLink(numYNP, 'PNG')) {
		return `../../resources/others/fileAgree/${numYNP}.PNG`
	} else if (await checkImageLink(numYNP, 'JPEG')) {
		return `../../resources/others/fileAgree/${numYNP}.JPEG`
	} else {
		return null
	}
}

async function checkImageLink(numYNP, tag) {
	const baseUrl = `../../resources/others/fileAgree/${numYNP}.`
	const res = await fetch(baseUrl + tag)
	console.log(res.ok)
	return res.ok
}

// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterSessionState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterSessionState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// функции управления состоянием колонок
function saveColumnState() {
	gridColumnLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreColumnState() {
	gridColumnLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}
