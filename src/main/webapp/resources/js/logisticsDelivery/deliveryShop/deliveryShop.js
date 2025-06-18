import { AG_GRID_LOCALE_RU } from "../../AG-Grid/ag-grid-locale-RU.js"
import { uiIcons } from "../../uiIcons.js"
import { dateHelper, disableButton, enableButton, getData } from "../../utils.js"
import {
	CargoCapacitySumStatusBarComponent,
	CountStatusBarComponent,
	PallSumStatusBarComponent,
	RowLegengStatusBarComponent
} from "../trucks/statusBar.js"
import { groupTrucksByDate, nextDate, prevDate, truckAdapter } from "../trucks/trucksUtils.js"
import { store } from "./store.js"
import { ajaxUtils } from '../../ajaxUtils.js'
import { snackbar } from "../../snackbar/snackbar.js"
import { getCarrierTGTrucksUrl, setTGTelNumberBaseUrl } from "../../globalConstants/urls.js"

const token = $("meta[name='_csrf']").attr("content")

const addNewTruckUrl = ''
const deleteTruckUrl = ''

const columnDefs = [
	{ headerName: 'id', field: 'idTGTruck', minWidth: 60, flex: 1, sort: 'desc', hide: true, },
	{ headerName: 'Номер', field: 'numTruck', flex: 2, },
	{ headerName: 'Контакты водителя', field: 'fio', flex: 4, wrapText: true, autoHeight: true, },
	// { headerName: 'Модель', field: 'modelTruck', width: 150, },
	{ headerName: 'Тип', field: 'typeTrailer', flex: 2, },
	{ headerName: 'Тоннаж', field: 'cargoCapacity', flex: 1, cellClass: 'px-1 text-center font-weight-bold fs-1rem', },
	{ headerName: 'Паллеты', field: 'pall', flex: 1, cellClass: 'px-1 text-center font-weight-bold fs-1rem', },
	{ headerName: 'Доп. инф-я', field: 'otherInfo', flex: 4, wrapText: true, autoHeight: true,},
]

const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-1 text-center',
		flex: 3,
		resizable: true,
		lockPinned: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	rowClassRules: {
		'light-green-row': params => params.node.data.status === 50,
		'light-orange-row': params => params.node.data.secondRound,
	},
	getRowId: (params) => params.data.idTGTruck,
	animateRows: true,
	// rowSelection: 'multiple',
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	overlayNoRowsTemplate: '<span class="h3">На указанную дату не заявлено ни одно авто</span>',
	statusBar: {
		statusPanels: [
			{ statusPanel: RowLegengStatusBarComponent, align: 'left', },
			{ statusPanel: CountStatusBarComponent, statusPanelParams: null, },
			{ statusPanel: PallSumStatusBarComponent, statusPanelParams: null, },
			{ statusPanel: CargoCapacitySumStatusBarComponent, statusPanelParams: null, },
		],
	},
}

document.addEventListener('DOMContentLoaded', async () => {
	// проверка наличия связи с тг ботом
	checkTGBotLink()

	// инициализация приложения
	await init()

	// поле текущей даты
	const currentDateInput = document.querySelector('#currentDate')
	// кнопка переключения даты вперед
	const datePrevBtn = document.querySelector('#datePrev')
	// кнопка переключения даты назад
	const dateNextBtn = document.querySelector('#dateNext')
	// таблица
	const gridDiv = document.querySelector('#myGrid')
	// кнопки заявления новых авто
	const addNewTruckBtn = document.querySelector('#addNewTruckBtn')
	const addNewTrucksBtn = document.querySelector('#addNewTrucksBtn')
	// форма добавления авто
	const addNewTruckForm = document.querySelector('#addNewTruckForm')
	// форма установки даты копирования авто
	const copyTruckToDateForm = document.querySelector('#copyTruckToDateForm')
	// форма указания номера телефона из тг бота
	const setTgTelNumberForm = document.querySelector('#setTgTelNumberForm')
	// кнопки ответа на вопрос об использовании тг бота
	const useTgBotBtn = document.querySelector('#useTgBot')
	const unuseTgBotBtn = document.querySelector('#unuseTgBot')
	useTgBotBtn && useTgBotBtn.addEventListener('click', (e) => {
		$('#confirmUsingTgBotModal').modal('hide')
		$('#setTgTelNumberModal').modal('show')
	})
	unuseTgBotBtn && unuseTgBotBtn.addEventListener('click', (e) => {
		$('#confirmUsingTgBotModal').modal('hide')
	})

	// установака начальной даты
	currentDateInput.value = store.getCurrentDate()
	// обработка смены даты
	currentDateInput.addEventListener('change', changeCurrentDateHandler)
	datePrevBtn.addEventListener('click', () => prevDate(currentDateInput))
	dateNextBtn.addEventListener('click', () => nextDate(currentDateInput))
	// обработка нажатий на кнопки завления авто
	addNewTruckBtn.addEventListener('click', () => addNewTruck('single'))
	addNewTrucksBtn.addEventListener('click', () => addNewTruck('multiple'))
	// обработка отправки форм
	addNewTruckForm.addEventListener('submit', addNewTruckHandler)
	copyTruckToDateForm.addEventListener('submit', copyTruckToDateHandler)
	setTgTelNumberForm.addEventListener('submit', setTgTelNumberHandler)

	// установка минимальной даты для полей даты заявления авто
	const dateRequisitionInputs = document.querySelectorAll('.dateRequisition')
	for (const input of dateRequisitionInputs) {
		input.min = store.getCurrentDate()
	}

	// получение стартовых данных
	const trucks = store.getTrucksByCurrentDate()
	// рендер таблиц машин
	renderTable(gridDiv, gridOptions, trucks)

	$('#addNewTruckModal').on('hide.bs.modal', () => addNewTruckForm.reset())
	$('#copyTruckToDateModal').on('hide.bs.modal', () => copyTruckToDateForm.reset())
})



// установка получение и установка стартовых данных
async function init() {
	// данные о машинах
	const truckData = await getTruckData()
	// адаптируем данные
	const mappedTruckData = truckData.map(truckAdapter)
	// машины по датам
	const trucks = groupTrucksByDate(mappedTruckData)
	// отправляем данные о машинах в стор
	store.setTrucks(trucks)
}

// получение данных о машинах
async function getTruckData() {
	const response = await getData(getCarrierTGTrucksUrl)
	console.log("🚀 ~ getTruckData ~ response:", response)
	if (!response) return []
	const trucksData = response.status === '200'
		? response.trucks ? response.trucks : []
		: []
	return trucksData
}
// обновление данных по машинам
async function updateTruckData() {
	await init()
	const trucks = store.getTrucksByCurrentDate()
	updateTableData(gridOptions, trucks)
}

// проверка наличия связи с тг ботом
function checkTGBotLink() {
	const isTgLinkInput = document.querySelector('#isTgLink')
	const isTgLink = isTgLinkInput.value === 'true'
	if (isTgLink) return
	$('#confirmUsingTgBotModal').modal('show')
}

function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData)
}
function updateTableData(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
}
function getMappingData(data,) {
	return data.map(truck => {
		return { ...truck }
	})
}
// контекстное меню таблицы
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const truck = rowNode.data

	const result = [
		{
			name: `Копировать авто на`,
			icon: uiIcons.files,
			subMenu: [
				{
					name: `завтра`,
					action: () => copyTruck('toTomorrow', truck),
				},
				{
					name: `дату ...`,
					action: () => copyTruck('toDate', truck),
				},
			]
		},
		"separator",
		{
			name: `Отменить авто`,
			action: () => deleteTruck(truck.idTGTruck),
			icon: uiIcons.trash,
		},
		"separator",
		"excelExport",
	]

	return result
}

// обработчик изменения отображаемой даты
function changeCurrentDateHandler(e) {
	const date = e.target.value
	// сохраняем текущую дату
	store.setCurrentDate(date)
	// свободные машины на текущую дату
	const trucks = store.getTrucksByCurrentDate()
	// обновляем таблицы
	updateTableData(gridOptions, trucks)
}

// заявление нового авто
function addNewTruck(formType) {
	const addNewTruckForm = document.querySelector('#addNewTruckForm')

	addNewTruckForm.formType.value = formType

	if (formType === 'single') {
		addNewTruckForm.count.value = 1
		addNewTruckForm.count.min = 1
		addNewTruckForm.count.readOnly = true
		addNewTruckForm.numTruck.disabled = false
		addNewTruckForm.fio.disabled = false
	} else {
		addNewTruckForm.count.min = 2
		addNewTruckForm.count.readOnly = false
		addNewTruckForm.numTruck.disabled = true
		addNewTruckForm.fio.disabled = true
	}

	$('#addNewTruckModal').modal('show')
}
// копирование авто
function copyTruck(type, truck) {
	if (type === 'toDate') {
		const copyTruckToDateForm = document.querySelector('#copyTruckToDateForm')
		setDataToForm(copyTruckToDateForm)
		$('#copyTruckToDateModal').modal('show')
	} else {
		const truckData = getNewTruckData(truck)
		const nextMs = new Date(store.getCurrentDate()).getTime() + dateHelper.DAYS_TO_MILLISECONDS
		const tomorrow = dateHelper.getDateForInput(nextMs)
		truckData.dateRequisition = tomorrow
		sendTruckData(truckData, 'copyTruckToTomorrow', null)
	}
	
}

// обработка отправки формы заявления авто
function addNewTruckHandler(e) {
	e.preventDefault()
	const form = e.target
	const formData = new FormData(form)
	const data = Object.fromEntries(formData)
	const truckData = getNewTruckData(data)
	sendTruckData(truckData, 'addTruck', e)
}
// обработка отправки формы копирования авто на дату
function copyTruckToDateHandler(e) {
	e.preventDefault()
	const form = e.target
	const formData = new FormData(form)
	const data = Object.fromEntries(formData)
	const truckData = getNewTruckData(data)
	sendTruckData(truckData, 'copyTruckToDate', e)
}
// обработка отправки формы указания номера телефона из тг бота
function setTgTelNumberHandler(e) {
	e.preventDefault()
	const form = e.target
	const formData = new FormData(form)
	const value = formData.get('tgTelNumber')
	if (!value) return
	const match = value.match(/\d+/g)
	const telNumber = match ? match.join('') : ''

	if (telNumber.length < 9) {
		snackbar.show('Номер телефона не корректен')
		return
	}

	disableButton(e.submitter)

	ajaxUtils.get({
		url: setTGTelNumberBaseUrl + telNumber,
		successCallback: (res) => {
			enableButton(e.submitter)
			if (res.status === '200') {
				$('#setTgTelNumberModal').modal('hide')
				updateTruckData()
				res.message && snackbar.show(res.message)
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message, 'error')
				return
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}

// получение данных формы заявления авто
function getNewTruckData(data) {
	return {
		dateRequisition: data.dateRequisition ? data.dateRequisition : null,
		count: data.count ? Number(data.count) : 1,
		numTruck: data.numTruck ? data.numTruck : null,
		typeTrailer: data.typeTrailer ? data.typeTrailer : null,
		cargoCapacity: data.cargoCapacity ? Number(data.cargoCapacity).toFixed(1) : null,
		pall: data.pall ? Number(data.pall) : null,
		fio: data.fio ? data.fio : null,
		otherInfo: data.otherInfo ? data.otherInfo : null,
	}
}

// установка данных в форму
function setDataToForm(form, data) {
	for (const key in data) {
		if (form[key] && data[key]) {
			form[key].value = data[key]
		}
	}
}

// отправка данных о заявке
function sendTruckData(truckData, method, submitEvent) {
	console.log("🚀 ~ sendTruckData ~ truckData:", truckData)
	console.log("🚀 ~ sendTruckData ~ method:", method)

	 const modal = method === 'addTruck'
	 	? $('#addNewTruckModal') : method === 'copyTruckToDate'
	 		? $('#copyTruckToDateModal') : null

	 disableButton(submitEvent.submitter)

	 ajaxUtils.postJSONdata({
	 	url: addNewTruckUrl,
	 	token: token,
	 	data: truckData,
	 	successCallback: async (res) => {
	 		enableButton(submitEvent.submitter)
	 		if (res.status === '200') {
	 			modal && modal.modal('hide')
	 			res.message && snackbar.show(res.message)
	 			// получаем обновленные данные и обновляем таблицу
	 			await updateTruckData()
	 			return
	 		}

	 		if (res.status === '100') {
	 			const message = res.message ? res.message : 'Неизвестная ошибка'
	 			snackbar.show(message)
	 			return
	 		}
	 		if (res.status === '105') {
	 			const message = res.message ? res.message : 'Неизвестная ошибка'
	 			snackbar.show(message)
	 			return
	 		}
	 	},
	 	errorCallback: () => {
	 		enableButton(submitEvent.submitter)
	 	}
	 })
}
// удаление машины
async function deleteTruck(truckId) {
	console.log("🚀 ~ deleteTruck ~ truckId:", truckId)
	
	 const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 100)
	 const res = await getData(`${deleteTruckUrl}${truckId}`)
	 clearTimeout(timeoutId)
	 bootstrap5overlay.hideOverlay()

	 if (res && res.status === '200') {
	 	snackbar.show('Выполнено!')
	 	// получаем обновленные данные и обновляем таблицу
	 	await updateTruckData()
	 } else {
	 	console.log(res)
	 	const message = res && res.message ? res.message : 'Неизвестная ошибка'
	 	snackbar.show(message)
	 }
}