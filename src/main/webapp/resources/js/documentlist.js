import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { autoSelectFilerValue, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { snackbar } from './snackbar/snackbar.js'
import { uiIcons } from './uiIcons.js'
import { dateHelper, debounce, getData } from './utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { getActsBaseUrl, saveDocumentsArrivedDateUrl, setActStatusUrl } from './globalConstants/urls.js'

const token = $("meta[name='_csrf']").attr("content")
const PAGE_NAME = 'documentlist'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

const formTitleDict = {
	confirm: 'Вы хотите подписать акт?',
	cancel: 'Вы хотите отменить акт?',
}

const actRowClassRules = {
	'confirmRow': params => params.data && params.data.statusToView.includes('Подписан'),
	'cancelRow': params => params.data && params.data.status === 'del',
}


const columnDefs = [
	{ headerName: 'id', field: 'idAct', flex: 1, hide: true, sort: 'desc' },
	{ headerName: 'Номер акта', field: 'numAct', flex: 1 },
	{
		headerName: 'Дата создания акта', field: 'actDateTime',
		valueFormatter: dateTimeValueFormatter,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{ headerName: 'Номера рейсов', field: 'idRoutesToView', },
	{
		headerName: 'Стоимость после тендера', field: 'totalCost',
		valueFormatter: (params) => `${params.value} ${params.data.currency}`
	},
	{
		headerName: 'НДС', field: 'totalNds', flex: 1,
		valueFormatter: (params) => params.value && params.value !== 0
			? `${params.value} ${params.data.currency}`
			: params.value
	},
	{
		headerName: 'Платные дороги', field: 'totalWay', flex: 1,
		valueFormatter: (params) => params.value && params.value !== 0
			? `${params.value} ${params.data.currency}`
			: params.value
	},
	{
		headerName: 'Общая стоимость', field: 'finalCostWithNds',
		valueFormatter: (params) => `${params.value} ${params.data.currency}`
	},
	{
		headerName: 'Статус', field: 'statusToView',
		filterParams: { valueGetter: statusToViewFIlterGetter, },
	},
	{ headerName: 'Комментарий', field: 'comment', },
	{ headerName: 'Ответственные за маршруты', field: 'logistToView', },
	{ headerName: 'Перевозчик', field: 'carrierToView', },
	{
		headerName: 'Дата поступления документов', field: 'documentsArrived',
		valueFormatter: dateTimeValueFormatter,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{ headerName: 'Ответственный за поступление документов', field: 'userDocumentsArrived', },
]

const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: actRowClassRules,
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
	getRowId: (params) => params.data.idAct,
	getContextMenuItems: getContextMenuItems,
	onFilterOpened: autoSelectFilerValue, // автовыделение значения в фильтре
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

	// автозаполнение полей дат в форме поиска заявок
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 7)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	orderSearchForm.addEventListener('submit', searchFormSubmitHandler)
	confirmForm.addEventListener('submit', confirmFormSubmitHandler)
	documentsArrivedForm.addEventListener('submit', documentsArrivedFormSubmitHandler)
	allDataForm.addEventListener('submit', allDataFormSubmitHandler)

	$('#confirmModal').on('hide.bs.modal', resetConfirmForm)
	$('#documentsArrivedModal').on('hide.bs.modal', resetDocumentsArrivedForm)
	$('#actRowsModal').on('hide.bs.modal', () => {
		setTimeout(() => {
			document.getElementById('actRowsModalLabel').innerText = ''
			document.getElementById('actInfoToActRowsModal').innerHTML = ''
			destroyActRowsTable()
			allDataForm.reset()
			allDataForm.classList.remove('d-none')
		}, 300)
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

window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})


// установка стартовых данных
async function initStartData() {
	const acts = window.initData.acts ? window.initData.acts : []
	updateTable(gridOptions, acts)
	window.initData = null
	bootstrap5overlay.hideOverlay()
}

// получение данных
async function getActsData(dateStart, dateEnd) {
	const url = `${getActsBaseUrl}${dateStart}&${dateEnd}`
	const res = await getData(url)
	if (!res) return []
	return res.acts ? res.acts : []
}

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showNoRowsOverlay()
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
	const idRoutesToView = item.idRoutes ? item.idRoutes.split(';').filter(Boolean).join(', ') : ''
	const actDateTime = item.time ? actTimeToMilliseconds(item.time) : null
	const finalCostWithNds = item.finalCost + item.nds
	const statusToView = getActStatus(item)
	const carrierToView = item.carrier ? getUnicValues(item.carrier).join(', ') : ''
	const logistToView = item.logist ? getUnicValues(item.logist).join(', ') : ''

	return {
		...item,
		actDateTime,
		idRoutesToView,
		finalCostWithNds,
		statusToView,
		carrierToView,
		logistToView,
	}
}
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const data = rowNode.data
	if(!data) return

	const status = data.status
	const documentsArrivedDate = data.documentsArrived


	const result = [
		{
			disabled: status !== '1',
			name: 'Подписать акт',
			action: () => {
				showConfirmForm({
					...data,
					command: 'confirm'
				})
			},
			icon: uiIcons.check,
			cssClasses: ['text-info'],
		},
		{
			disabled: status !== '1',
			name: 'Отменить акт',
			action: () => {
				showConfirmForm({
					...data,
					command: 'cancel'
				})
			},
			icon: uiIcons.cancel,
			cssClasses: ['text-danger'],
		},
		{
			disabled: !!documentsArrivedDate || status === 'del',
			name: 'Указать дату получения документов',
			action: () => {
				showDocumentsArrivedForm(data)
			},
			icon: uiIcons.calendarCheck,
		},

		"separator",

		{
			name: 'Показать данные акта',
			action: () => {
				showActRowsModal(data)
			},
			icon: uiIcons.table,
		},

		"separator",

		{
			name: `Сбросить настройки колонок`,
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
		"excelExport",
	]

	return result
}
function updateTableRow(gridOptions, act) {
	const rowId = Number(act.idAct)
	const rowNode = gridOptions.api.getRowNode(rowId)
	if (!rowNode) return
	const resultCallback = () => highlightRow(gridOptions, rowNode)
	gridOptions.api.applyTransactionAsync({ update: [ mapCallback(act) ] }, resultCallback)
}
function highlightRow(gridOptions, rowNode) {
	gridOptions.api.flashCells({ rowNodes: [rowNode] })
}

// обработчик отправки формы поиска заявок
async function searchFormSubmitHandler(e) {
	e.preventDefault()
	gridOptions.api.showLoadingOverlay()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	const acts = await getActsData(data.date_from, data.date_to)
	updateTable(gridOptions, acts)
}
// обработчик отправки формы подписания/отмены акта
async function confirmFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: setActStatusUrl,
		token: token,
		data: data,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				hideConfirmForm()
				updateTableRow(gridOptions, res.object)
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
// обработчик отправки формы установки даты получения документов
async function documentsArrivedFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	const payload = {
		idAct: Number(data.idAct),
		documentsArrived: `${data.documentsArrived.replace('T', ' ')}:00`
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: saveDocumentsArrivedDateUrl,
		token: token,
		data: payload,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
			console.log(res)
			if (res.status === '200') {
				hideDocumentsArrivedForm()
				updateTableRow(gridOptions, res.object)
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

async function allDataFormSubmitHandler(e) {
	e.preventDefault()

	const dateInput = e.target.documentsArrived
	const commentInput = e.target.comment
	const submitter = e.submitter
	const command = submitter.dataset.command
	const { isValid, actionUrl } = allDataFormValidation(command, dateInput, commentInput)

	if (isValid) {
		const formData = new FormData(e.target)
		const data = Object.fromEntries(formData)

		if (data.status !== '1') return

		const payload = {
			idAct: Number(data.idAct),
			comment: data.comment,
			command
		}

		if (command === 'confirm') {
			payload.documentsArrived = data.documentsArrived
				? `${data.documentsArrived.replace('T', ' ')}:00` : null
		}

		const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

		ajaxUtils.postJSONdata({
			url: actionUrl,
			token: token,
			data: payload,
			successCallback: (res) => {
				clearTimeout(timeoutId)
				bootstrap5overlay.hideOverlay()
	
				if (res.status === '200') {
					updateTableRow(gridOptions, res.object)
					hideActRowsModal()
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
}

// валидация формы из окна просмотра строк акта
function allDataFormValidation(command, dateInput, commentInput) {
	let actionUrl = ""
	let isValid = true

	if (command === 'confirm') {
		dateInput.required = true

		if (!dateInput.checkValidity()) {
			dateInput.reportValidity()
			isValid = false
			dateInput.required = false
			commentInput.required = false
		} else {
			actionUrl = setActStatusUrl
		}
	} else if (command === 'cancel') {
		commentInput.required = true

		if (!commentInput.checkValidity()) {
			commentInput.reportValidity()
			isValid = false
			dateInput.required = false
			commentInput.required = false
		} else {
			actionUrl = setActStatusUrl
		}
	}
	return { isValid, actionUrl }
}

// модальное окно формы подписания/отмены акта
function showConfirmForm(data) {
	data && setDataToConfirmForm(data)
	$('#confirmModal').modal('show')
}
function hideConfirmForm() {
	$('#confirmModal').modal('hide')
}

// модальное окно формы установки даты получения документов
function showDocumentsArrivedForm(data) {
	data && setDataToDocumentsArrivedForm(data)
	$('#documentsArrivedModal').modal('show')
}
function hideDocumentsArrivedForm() {
	$('#documentsArrivedModal').modal('hide')
}

// модальное окно таблицы строк акта
function showActRowsModal(data) {
	setActDataToActRowsModal(data)
	data && createActRowsTable(data)
	$('#actRowsModal').modal('show')
}
function hideActRowsModal() {
	$('#actRowsModal').modal('hide')
}

// добавление данных в форму подтверждения
function setDataToConfirmForm(data) {
	const command = data.command
	const formTitle = formTitleDict[command]
	const confirmModalLabel = document.getElementById('confirmModalLabel')
	const actInfo = document.getElementById('actInfoConfirm')
	actInfo.textContent = getActInfo(data)
	confirmModalLabel.textContent = formTitle
	confirmForm.idAct.value = data.idAct
	confirmForm.command.value = command

	if (command === 'cancel') {
		confirmForm.comment.required = true
		confirmForm.comment.placeholder = 'Обязательно укажите причину отмены'
		
	}
}
// очистка формы подтверждения
function resetConfirmForm() {
	const confirmModalLabel = document.getElementById('confirmModalLabel')
	const actInfo = document.getElementById('actInfoConfirm')
	actInfo.textContent = ''
	confirmModalLabel.textContent = ''
	confirmForm.reset()
	confirmForm.comment.required = false
	confirmForm.comment.placeholder = 'Комментарий'
}

function setDataToDocumentsArrivedForm(data) {
	const actInfo = document.getElementById('actInfoToDocumentsArrived')
	actInfo.textContent = getActInfo(data)
	documentsArrivedForm.idAct.value = data.idAct
}
function resetDocumentsArrivedForm() {
	const actInfo = document.getElementById('actInfoToDocumentsArrived')
	actInfo.textContent = ''
	documentsArrivedForm.reset()
}

// добавление данных в модальное окно отображения строк акта
function setActDataToActRowsModal(act) {
	const info = `
		<span class="mb-1"><b>Дата создания:</b> ${act.time}</span>
		<span class="mb-1"><b>Перевозчик:</b> ${act.carrierToView}</span>
		<span class="mb-1"><b>Статус:</b> ${act.statusToView}</span>
	`

	document.getElementById('actRowsModalLabel').innerText = `Акт №${act.numAct}`
	document.getElementById('actInfoToActRowsModal').innerHTML = info

	allDataForm.documentsArrived.removeAttribute('readonly')

	const status = act.status
	const documentsArrived = act.documentsArrived

	if (status !== '1') {
		allDataForm.classList.add('d-none')
	} else {
		allDataForm.idAct.value = act.idAct
		allDataForm.status.value = status
		allDataForm.documentsArrived.value = dateHelper.getISODateTime(documentsArrived)
		documentsArrived && allDataForm.documentsArrived.setAttribute('readonly', true)
	}
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

function dateValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDate(date)
}
function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
}
function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}
function statusToViewFIlterGetter(params) {
	const value = params.data.statusToView
	if (value === 'Ожидает подписи') return 'Ожидает подписи'
	if (value.includes('Отменен')) return 'Отменен'
	if (value.includes('Подписан')) return 'Подписан'
	return ''
}


// получение даты создания акта в мс
function actTimeToMilliseconds(dateStr) {
	const [datePart, timePart] = dateStr.split(' ')
	const [day, month, year] = datePart.split('-').map(Number)
	const [hours, minutes] = timePart.split(':').map(Number)
	const date = new Date(year, month - 1, day, hours, minutes)
	return date.getTime()
}

// получение строкового представления статуса акта
function getActStatus(act) {
	const dateRegex = /^(\d{2})-(\d{2})-(\d{4}) (\d{2}):(\d{2})$/
	const status = act.status

	if (status === '1') {
		return 'Ожидает подписи'
	}

	if (status === 'del') {
		const cancelDate = act.cancel
		return `Отменен ${cancelDate}`
	}

	if (dateRegex.test(status)) {
		return `Подписан ${status}`
	}

	return ''
}

// получение массива унакальных значений из строки
function getUnicValues(carrierStr) {
	return [
		...new Set(carrierStr.split('^').filter(Boolean))
	]
}

// получение краткой информации об акте
function getActInfo(act) {
	const currency = act.currency
	return `Акт №${act.numAct} от ${act.time} на сумму ${act.finalCostWithNds} ${currency} (в том числе НДС ${act.nds} ${currency})`
}

// создание таблицы строк акта
function createActRowsTable(act) {
	const adapter = str => str ? str.split('^').filter(Boolean) : []
	const getData = key => adapter(act[key])

	const dateLoadData = getData('columnDateLoad')
	const dateUnloadData = getData('columnDateUnload')
	const idRouteData = act.idRoutes.split(';').filter(Boolean)
	const nameRouteData = getData('columnNameRoute')
	const numTruckData = getData('columnNumTruck')
	const numRouteListData = getData('columnNumRouteList')
	const numDocumentData = getData('columnNumDocument')
	const weigthCargoData = getData('columnVeigthCargo')
	const finalPriceData = getData('columnSummCost')
	const ndsData = getData('columnNdsSumm')
	const tollRoadsData = getData('columnTollRoads')
	const totalData = getData('columnTotal')

	const { totalCost, totalWay, totalNds, currency, finalCostWithNds, totalExpeditionCost } = act

	const rows = dateLoadData.map((_, i) => `
		<tr>
			<td>${dateLoadData[i]}</td>
			<td>${dateUnloadData[i]}</td>
			<td>${idRouteData[i]}</td>
			<td>${nameRouteData[i]}</td>
			<td class="carNumber">${numTruckData[i]}</td>
			<td>${numRouteListData[i]}</td>
			<td>${numDocumentData[i]}</td>
			<td>${weigthCargoData[i]}</td>
			<td>${finalPriceData[i]} ${currency}</td>
			<td>${ndsData[i] || 0} ${currency}</td>
			<td>${tollRoadsData[i] || 0} ${currency}</td>
			<td>${totalData[i]} ${currency}</td>
		</tr>
	`).join('')

	const expeditionCostRow = totalExpeditionCost
		? `<tr class="table-info">
			<td class="font-weight-bold" scope="row" colspan="8">Услуги экспедитора</td>
			<td>${totalExpeditionCost} ${currency}</td>
			<td>${totalNds} %</td>
			<td></td>
			<td>${totalExpeditionCost} ${currency}</td>
		</tr>`
		: ''

	document.getElementById('actRows').innerHTML = rows
		+ expeditionCostRow
		+ `<tr class="table-success">
			<td class="font-weight-bold" scope="row" colspan="8">Итого</td>
			<td>${totalCost} ${currency}</td>
			<td>${totalNds} ${currency}</td>
			<td>${totalWay} ${currency}</td>
			<td>${finalCostWithNds} ${currency}</td>
		</tr>
	`
}

// удаление таблицы строк акта
function destroyActRowsTable() {
	const actRowsElem = document.getElementById('actRows')
	actRowsElem.innerHTML = ''
}
