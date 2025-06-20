import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel, supressInputInLargeTextEditor } from './AG-Grid/ag-grid-utils.js'
import { snackbar } from './snackbar/snackbar.js'
import { debounce, getData, isObserver, } from './utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { ajaxUtils } from './ajaxUtils.js'
import { createNewSupplierUrl, getAllSuppliersUrl, postUserIsExistUrl } from './globalConstants/urls.js'

const PAGE_NAME = 'suppliersList'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const role = document.querySelector('#role').value

const loginRegex = /^[a-zA-Z0-9@#$%^&*()_+!~\-=\[\]{}|;:',.?\/]+$/

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)


const columnDefs = [
	{ headerName: "Логин", field: "login", },
	{ headerName: "ФИО", field: "fio", },
	{ headerName: "E-mail", field: "eMail", },
	{ headerName: "Телефон", field: "telephone", },
	{ headerName: "Код контрагента", field: "counterpartyCode", },
	{ headerName: "Нименование контрагента", field: "companyName", },
	{ headerName: "Форма собственности", field: "propertySize", },
	{ headerName: "Адрес", field: "address", },
	{ headerName: "УНП", field: "numYNP", },
	{
		headerName: "Банковские реквизиты фирмы", field: "requisites",
		wrapText: false, autoHeight: false,
		editable: true, cellEditorPopup: true, cellEditor: 'agLargeTextCellEditor',
	},
]
const gridOptions = {
	columnDefs: columnDefs,
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
	getRowId: (params) => params.data.idUser,
	// запред ввода в модалке редактирования
	onCellEditingStarted: (event) => {
		if (event.colDef.field === "requisites") {
			supressInputInLargeTextEditor()
		}
	},
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	},
	statusBar: {
		statusPanels: [
			{ statusPanel: 'agTotalAndFilteredRowCountComponent', align: 'left' },
		],
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
	// создание таблицы
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	gridOptions.api.showLoadingOverlay()
	restoreColumnState()

	const messageLoginElem = newSupplierForm.querySelector('#messageLogin')
	newSupplierForm.addEventListener('submit', newSupplierFormSubmitHandler)
	newSupplierForm.login.addEventListener('change', (e) => isExistLogin(e.target.value, messageLoginElem))
	newSupplierForm.login.addEventListener('input', (e) => checkLoginOnInput(e))

	$('#newSupplierModal').on('hidden.bs.modal', () => {
		newSupplierForm.reset()
		messageLoginElem.innerText = ''
		messageLoginElem.className = ''
		newSupplierForm.login.classList.remove('is-invalid')
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
	const data = window.initData ? window.initData : []
	updateTable(gridOptions, data)
	window.initData = null
}

// получение данных
async function getSupplierList() {
	const url = `${getAllSuppliersUrl}`
	const res = await getData(url)
	return res ? res : []
}

// форма создания нового аккаунта поставщика
function newSupplierFormSubmitHandler(e) {
	e.preventDefault()

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
	}

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const isValid = loginRegex.test(data.login)
	if (!isValid) {
		snackbar.show('Некорректный логин! Используйте только разрешённые символы')
		return
	}

	const payload = {
		...data,
		address: data.address ? data.address : null,
		propertySize: data.propertySize ? data.propertySize : null,
		numYNP: data.numYNP ? data.numYNP : null,
		counterpartyCode: data.counterpartyCode ? Number(data.counterpartyCode) : null,
		companyName: data.companyName ? data.companyName : null,
		requisites: data.requisites ? data.requisites : null,
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: createNewSupplierUrl,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				alert('Новый пользователь успешно создан! Письмо с логином и паролем отправлено на указанный при регистрации электронный адрес')
				const data = await getSupplierList()
				updateTable(gridOptions, data)
				$('#newSupplierModal').modal('hide')
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
function checkLoginOnInput(e) {
	const login = e.target.value
	const isValid = loginRegex.test(login)

	if (login && !isValid) {
		e.target.classList.add('is-invalid')
	} else {
		e.target.classList.remove('is-invalid')
	}
}
function isExistLogin(login, messageLoginElem) {
	const payload = { Login: login }

	const isValid = loginRegex.test(login)
	if (!isValid) return

	$.ajax({
		type: "POST",
		url: postUserIsExistUrl,
		data: JSON.stringify(payload),
		contentType: 'application/json',
		dataType: 'json',
		success: function (res) {
			messageLoginElem.className = 'text-danger'
			messageLoginElem.innerText = res.message
		},
		error: function (err) {
			messageLoginElem.className = 'text-success'
			messageLoginElem.innerText = 'Логин доступен'
		}
	})
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
	const fio = item.surname + ' ' + item.name + ' ' + item.patronymic
	return {
		...item,
		fio: fio.trim()
	}
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const items = [
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
