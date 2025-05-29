import { ws } from './global.js';
import { wsHead } from './global.js';
import { snackbar } from './snackbar/snackbar.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js';
import { dateHelper, getData, isMobileDevice } from './utils.js';
import { ajaxUtils } from './ajaxUtils.js';
import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js';
import { BtnsCellRenderer } from './AG-Grid/ag-grid-utils.js';
import { uiIcons } from './uiIcons.js';
import { getMyActualRoutesUrl, getThisCarrierCarsUrl, getThisCarrierDriversUrl, saveNewDriverUrl, saveNewTruckUrl, setRouteParametersUrl } from './globalConstants/urls.js';

const token = $("meta[name='_csrf']").attr("content")

let error = false

let targetRowId

function sendMessage(message) {
	ws.send(JSON.stringify(message))
}
function sendMessageHead(message) {
	wsHead.send(JSON.stringify(message))
}

const isMobileView = isMobileDevice() || (window.innerWidth < 768)

const columnDefs = [
	{ 
		headerName: 'Наименование маршрута', field: 'routeDirection', colId: 'routeDirection',
		flex: 10, minWidth: 240, cellRenderer: routeLinkRenderer,
	},
	{
		headerName: 'Машина', field: 'truckToView',
		flex: 5, minWidth: 160,
		cellClass: 'px-2 text-center',
		cellRenderer: truckCellRenderer
	},
	{
		headerName: 'Водитель', field: 'driverToView',
		flex: 5, minWidth: 160,
		cellClass: 'px-2 text-center',
		cellRenderer: driverCellRenderer
	},
	{ headerName: 'Информация', field: 'cargoInfo', flex: 10, minWidth: 200, },
	{ headerName: 'Дата загрузки', field: 'dateLoadToView', },
	{ headerName: 'Дата выгрузки', field: 'dateUnloadToView', },
	{ headerName: 'Промежуток для заезда на Прилесье', field: 'dateRangePrilesie', minWidth: 160, },
	{
		headerName: 'Стоимость перевозки', field: 'cost',
		cellClass: 'px-2 text-center',
		cellRenderer: costCellRenderer
	},
	{
		headerName: 'Дата и время подачи машины', field: 'dateLoadTruck',
		flex: 5, minWidth: 170,
		cellClass: 'px-2 text-center',
		cellRenderer: dateLoadTruckCellRenderer
	},
	{
		headerName: 'Дата и время выгрузки', field: 'dateDelivery',
		flex: 5, minWidth: 170,
		cellClass: 'px-2 text-center',
		cellRenderer: dateDeliveryCellRenderer
	},
	{
		headerName: '', field: 'withActionBtns',
		minWidth: 60,
		cellRenderer: BtnsCellRenderer,
		filter: false,
		cellRendererParams: {
			onClick: rowBtnsOnClickHandler,
			buttonList: [
				{ className: 'btn btn-success border d-flex p-2', id: 'approveTruck', icon: uiIcons.checkCircle, title: 'Подтвердить машину' },
			],
		},
	},
]

const gridOptions = {
	columnDefs: columnDefs,
	getRowId: params => params.data.idRoute,
	defaultColDef: {
		headerClass: "px-2",
		cellClass: "px-2 text-center",
		flex: 1,
		minWidth: 100,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	getRowId: (params) => params.data.idRoute,
	singleClickEdit: true,
	suppressRowClickSelection: true,
	suppressDragLeaveHidesColumns: true,
	suppressMovableColumns: isMobileView,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	context: {},
}


document.addEventListener('DOMContentLoaded', async () => {
	const truckImgInput = document.querySelector("#technical_certificate_file")
	const truckImgContainer = document.querySelector("#truckImageContainer")
	const driverImgInput = document.querySelector("#drivercard_file")
	const driverImgContainer = document.querySelector("#driverImageContainer")

	// отправка форм
	createTruckForm.addEventListener('submit', (e) => createTruckFormSubmitHandler(e))
	createDriverForm.addEventListener('submit', (e) => createDriverFormSubmitHandler(e))

	// отображение превью добавленных изображений
	truckImgInput.addEventListener("change", (e) => addImgToView(e, truckImgContainer))
	driverImgInput.addEventListener("change", (e) => addImgToView(e, driverImgContainer))

	const gridDiv = document.querySelector('#myGrid')

	// получение данных
	const routes = await getData(getMyActualRoutesUrl)
	const sortedRoutes = routes.sort((a,b) => b.idRoute - a.idRoute)
	const trucks = await getData(getThisCarrierCarsUrl)
	const drivers = await getData(getThisCarrierDriversUrl)

	// добавление данных для выпадающих списков в контекст таблицы
	const truckOptionList = trucks.map(getTruckOptionData)
	const driverOptionList = drivers.map(getDriverOptionData)
	gridOptions.context = { truckOptionList, driverOptionList }

	// отрисовка таблицы
	renderTable(gridDiv, gridOptions, sortedRoutes)

	// закрытие модальных окон
	$('#truckModal').on('hide.bs.modal', function (e) {
		createTruckForm.classList.remove('was-validated')
		createTruckForm.reset()
		truckImgContainer.innerHTML = ''

	})
	$('#driverModal').on('hide.bs.modal', function (e) {
		createDriverForm.classList.remove('was-validated')
		createDriverForm.reset()
		driverImgContainer.innerHTML = ''
	})

	bootstrap5overlay.hideOverlay()
})

// установка водителя, машины и дат
function setRoureParameters(payload) {
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: setRouteParametersUrl,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				const data = res.route
				const route = mapCallback(data)
				gridOptions.api.applyTransactionAsync({ update: [route] })
				res.message && snackbar.show(res.message)
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

// форма добавления нового водителя
function createDriverFormSubmitHandler(e) {
	e.preventDefault()

	bootstrap5overlay.showOverlay()

	if (e.target.checkValidity() === false || error) {
		e.target.classList.add('was-validated')
		bootstrap5overlay.hideOverlay()
		return
	}

	let formData = new FormData(e.target)
	formData = updateDriverFormData(formData)

	ajaxUtils.postMultipartFformData({
		url: saveNewDriverUrl,
		token: token,
		data: formData,
		successCallback: (res) => {
			if (res) {
				$(`#driverModal`).modal('hide')
				setDriverToOptionList(targetRowId, res)
				snackbar.show('Новый водитель добавлен')
			} else {
				snackbar.show('Ошибка: возможно, такой водитель уже существует!')
			}
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}
// форма добавления нового авто
function createTruckFormSubmitHandler(e) {
	e.preventDefault()

	bootstrap5overlay.showOverlay()

	if (e.target.checkValidity() === false || error) {
		e.target.classList.add('was-validated')
		bootstrap5overlay.hideOverlay()
		return
	}

	let formData = new FormData(e.target)
	formData = updateTruckFormData(formData)

	ajaxUtils.postMultipartFformData({
		url: saveNewTruckUrl,
		token: token,
		data: formData,
		successCallback: (res) => {
			if (res) {
				$(`#truckModal`).modal('hide')
				setTruckToOptionList(targetRowId, res)
				snackbar.show('Новое авто добавлено')
			} else {
				snackbar.show('Ошибка: возможно, такое авто уже существует!')
			}
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}


// функции таблицы
function renderTable(gridDiv, gridOptions, data) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)

	gridOptions.api.setRowData(mappingData.sort((a,b) => b.idOrder - a.idOrder))
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(mapCallback)
}
function mapCallback(route) {
	const driverToView = getDriverInfo(route)
	const truckToView = getTruckInfo(route)
	const cargoInfo = getCargoInfo(route)
	const dateLoadToView = getDateLoadInfo(route)
	const dateUnloadToView = getDateUnloadInfo(route)
	const cost = getCostInfo(route)
	const dateLoadTruck = getDateLoadTruck(route)
	const dateDelivery = getDateDelivery(route)
	const withActionBtns = !route.driver
	const dateRangePrilesie = getDateRangePrilesie(route)

	return {
		...route,
		driverToView,
		truckToView,
		dateLoadToView,
		dateUnloadToView,
		cargoInfo,
		cost,
		dateLoadTruck,
		dateDelivery,
		withActionBtns,
		dateRangePrilesie,
	}
}
function getDateRangePrilesie(route) {
	if (!route) return ''
	const { dateTimeStartPrilesie, dateTimeEndPrilesie } = route
	if (!dateTimeStartPrilesie || !dateTimeEndPrilesie) return ''
	const dateStartToView = dateHelper.getFormatDateTime(dateTimeStartPrilesie)
	const dateEndToView = dateHelper.getFormatDateTime(dateTimeEndPrilesie)
	return `${dateStartToView} - ${dateEndToView}`
}
function rowBtnsOnClickHandler(e, params) {
	if (e.buttonId === 'approveTruck') {
		const rowData = params.data
		const idDriver = rowData._driverSelect?.value
		const idTruck = rowData._truckSelect?.value
		const dateLoad = rowData._loadDateInput?.value
		const dateUnload = rowData._unloadDateInput?.value
		const expeditionCost = rowData._expeditionCostInput?.value
		const isImport = !!rowData._expeditionCostInput

		if (!idDriver || !idTruck || !dateLoad || !dateUnload || (isImport && !expeditionCost)) {
			snackbar.show('Заполните все поля')
			return
		}

		const dateLoadMs = new Date(dateLoad).getTime()
		const dateUnloadMs = new Date(dateUnload).getTime()

		if (dateLoadMs > dateUnloadMs) {
			snackbar.show('Дата выгрузки не может быть раньше даты загрузки')
			return
		}

		const [ dateLoadActually, timeLoadActually ] = dateLoad.split('T')
		const [ dateUnloadActually, timeUnloadActually ] = dateUnload.split('T')

		const payload = {
			idRoute: Number(rowData.idRoute),
			idDriver: Number(idDriver),
			idTruck: Number(idTruck),
			dateLoadActually,
			timeLoadActually,
			dateUnloadActually,
			timeUnloadActually,
			expeditionCost: expeditionCost ? Number(expeditionCost) : null
		}

		setRoureParameters(payload)
	}
}

// обновление списка водителей в контексте таблицы (для опций выпадающего списка)
function setDriverToOptionList(rowId, newDriver) {
	const newOptionData = getDriverOptionData(newDriver)
	gridOptions.context.driverOptionList.push(newOptionData)

	const rowNode = gridOptions.api.getRowNode(rowId)
	if (rowNode) {
		const data = rowNode.data
		rowNode.setData({ ...data, selectedDriver: newDriver.idUser })
	}

	gridOptions.api.refreshCells({
		force: true,
		suppressFlash: true,
		columns: ['driverToView'],
	})

	targetRowId = null
}
// обновление списка авто в контексте таблицы (для опций выпадающего списка)
function setTruckToOptionList(rowId, newTruck) {
	const newOptionData = getTruckOptionData(newTruck)
	gridOptions.context.truckOptionList.push(newOptionData)

	const rowNode = gridOptions.api.getRowNode(rowId)
	if (rowNode) {
		const data = rowNode.data
		rowNode.setData({ ...data, selectedTruck: newTruck.idTruck })
	}

	gridOptions.api.refreshCells({
		force: true,
		suppressFlash: true,
		columns: ['truckToView'],
	})

	targetRowId = null
}
// данные опции списка авто
function getTruckOptionData(truck) {
	return {
		value: truck.idTruck,
		label: truck.numTrailer
			? `${truck.numTruck}/${truck.numTrailer}`
			: truck.numTruck,
	}
}
// данные опции списка водителей
function getDriverOptionData(driver) {
	return {
		value: driver.idUser,
		label: `${driver.surname} ${driver.name}`,
	}
}

// поиск в списке селекта
function addSearchInSelectOptions(searchInput, select) {
	const searchItems = select.querySelectorAll('option')

	searchInput.addEventListener('input', (e) => {
		const target = e.target
		const val = target.value.trim().toUpperCase()
		const fragment = document.createDocumentFragment()

		if (!target.classList.contains('keyboard__key')) return

		for (const elem of searchItems) {
			elem.remove()

			if (val === '' || elem.textContent.toUpperCase().includes(val)) {
				fragment.append(elem)
			}
		}

		select.append(fragment)
	})
}

// обновление данных форм
function updateDriverFormData(formData) {
	const data = Object.fromEntries(formData)
	const numpass = 
		data.numpass_1
		+ ' '
		+ data.numpass_2
		+ ', выдан '
		+ data.numpass_3
		+ ' от '
		+ dateHelper.changeFormatToView(data.numpass_4)
	
	const numdrivercard = 
		data.numdrivercard_1
		+ ' '
		+ data.numdrivercard_2
		+ ', выдано '
		+ data.numdrivercard_3
		+ ' от '
		+ dateHelper.changeFormatToView(data.numdrivercard_4)

	formData.append('numpass', numpass)
	formData.append('numdrivercard', numdrivercard)
	formData.delete('numdrivercard_1')
	formData.delete('numdrivercard_2')
	formData.delete('numdrivercard_3')
	formData.delete('numdrivercard_4')
	formData.delete('numpass_1')
	formData.delete('numpass_2')
	formData.delete('numpass_3')
	formData.delete('numpass_4')

	return formData
}
function updateTruckFormData(formData) {
	const data = Object.fromEntries(formData)
	const technical_certificate = 
		data.technical_certificate_1
		+ ' '
		+ data.technical_certificate_2
		+ ', выдан '
		+ data.technical_certificate_3
		+ ' от '
		+ dateHelper.changeFormatToView(data.technical_certificate_4)

	const infoData = [
		data.check_1 ? data.check_1 : '',
		data.check_2 ? data.check_2 : '',
		data.check_3 ? data.check_3 : '',
		data.check_4 ? data.check_4 : '',
		data.check_5 ? data.check_5 : '',
	]

	let info = ''
	infoData.forEach(str => {
		if (str) {
			info = info + `${str}; `
		}
	})

	const dimensions =
		data.dimensions_1
		+ '/' + data.dimensions_2
		+ '/' + data.dimensions_3

	const cargoCapacity = (Number(data.cargoCapacity) * 1000).toString()
	
	formData.append('technical_certificate', technical_certificate)
	formData.append('info', info)
	formData.append('dimensions', dimensions)
	formData.set('cargoCapacity', cargoCapacity),
	formData.delete('check_1')
	formData.delete('check_2')
	formData.delete('check_3')
	formData.delete('check_4')
	formData.delete('check_5')
	formData.delete('dimensions_1')
	formData.delete('dimensions_2')
	formData.delete('dimensions_3')
	formData.delete('technical_certificate_1')
	formData.delete('technical_certificate_2')
	formData.delete('technical_certificate_3')
	formData.delete('technical_certificate_4')

	return formData
}

// превью изображений в поле "файл"
function addImgToView(event, imgContainer) {
	const file = event.target.files[0]
	if (!file) return

	const reader = new FileReader()
	reader.readAsDataURL(file)
	reader.onload = () => {
		const newImg = document.createElement("img")
		newImg.src = reader.result
		imgContainer.innerHTML = ''
		imgContainer.append(newImg)
	}

	return
}

// функции рендера ячеек таблицы
function routeLinkRenderer(params) {
	const idRoute = params.data.idRoute
	const link = `/speedlogist/main/carrier/tender/tenderpage?routeId=${idRoute}`
	return `<a class="text-primary" href="${link}">${params.value}</a>`
}
function truckCellRenderer (params) {
	if (!params.data) return ''

	if (params.value) return params.value

	const container = document.createElement('div')
	const searshInput = document.createElement('input')
	searshInput.setAttribute('class', 'keyboard__key w-100 px-2')
	searshInput.setAttribute('placeholder', 'Поиск в списке')

	const select = document.createElement('select')
	select.setAttribute('class', 'form-control form-control-sm')

	const options = params.context.truckOptionList.map(option => `<option value="${option.value}">${option.label}</option>`)
	options.unshift(
		`<option selected disabled value="">Выберите авто</option>`
		+ `<option value="addTruck">+ Добавить авто</option>`
	)

	select.innerHTML = options.join(' ')

	addSearchInSelectOptions(searshInput, select)

	select.addEventListener('change', (e) => {
		if (e.target.value === 'addTruck') {
			e.target.value = ''
			targetRowId = params.data.idRoute
			$('#truckModal').modal('show')
		}
	})

	if (params.data.selectedTruck) {
		select.value = params.data.selectedTruck
	}

	container.append(searshInput, select)

	params.data._truckSelect = select

	return container
}
function driverCellRenderer(params) {
	if (!params.data) return ''

	if (params.value) return params.value

	const container = document.createElement('div')
	const searshInput = document.createElement('input')
	searshInput.setAttribute('class', 'keyboard__key w-100 px-2')
	searshInput.setAttribute('placeholder', 'Поиск в списке')

	const select = document.createElement('select')
	select.setAttribute('class', 'form-control form-control-sm')

	const options = params.context.driverOptionList.map(option => `<option value="${option.value}">${option.label}</option>`)
	options.unshift(
		`<option selected disabled value="">Выберите водителя</option>`
		+ `<option value="addDriver">+ Добавить водителя</option>`
	)

	select.innerHTML = options.join(' ')

	addSearchInSelectOptions(searshInput, select)

	select.addEventListener('change', (e) => {
		if (e.target.value === 'addDriver') {
			e.target.value = ''
			targetRowId = params.data.idRoute
			$('#driverModal').modal('show')
		}
	})

	if (params.data.selectedDriver) {
		select.value = params.data.selectedDriver
	}

	container.append(searshInput, select)

	params.data._driverSelect = select

	return container
}
function costCellRenderer(params) {
	const route = params.data

	if (!route) return ''
	if (!params.value) return ''

	if (route.way === 'Импорт' && route.expeditionCost === null && !route.driver) {
		const container = document.createElement('div')

		const valueSpan = document.createElement('span')
		valueSpan.textContent = params.value

		const div = document.createElement('div')
		div.className = 'pt-1 text-danger'
		div.textContent = 'Укажите комиссию экспедитора:'

		const input = document.createElement('input')
		input.setAttribute('type', 'number')
		input.setAttribute('name', 'expeditionCost')
		input.setAttribute('class', 'form-control mt-1')
		input.setAttribute('min', '0')
		input.setAttribute('max', route.finishPrice)
		input.setAttribute('required', true)

		const currencySpan = document.createElement('span')
		currencySpan.textContent = route.startCurrency

		container.append(valueSpan, div, input, currencySpan)

		params.data._expeditionCostInput = input

		return container

		// return `
		// 	<span>${params.value}</span>
		// 	<div class="pt-1 text-danger">Укажите комиссию экспедитора:</div>
		// 	<input type="number" class="form-control mt-1" name="expeditionCost" min="0" max="${route.finishPrice}" required>
		// 	<span>${route.startCurrency}</span>
		// `
	}

	return params.value
}
function dateLoadTruckCellRenderer(params) {
	const route = params.data

	if (!route) return ''

	const dateTimeInput = document.createElement('input')
	dateTimeInput.setAttribute('name', 'dateLoadActually')
	dateTimeInput.setAttribute('class', 'form-control form-control-sm loadDate')
	dateTimeInput.setAttribute('type', 'datetime-local')

	if (route.dateLoadPreviously) {
		dateTimeInput.setAttribute('min', `${route.dateLoadPreviously}T00:00`)
	}

	if (params.value) {
		dateTimeInput.value = params.value
		dateTimeInput.readOnly = true
	}

	route._loadDateInput = dateTimeInput

	return dateTimeInput
}
function dateDeliveryCellRenderer(params) {
	const route = params.data

	if (!route) return ''

	const dateTimeInput = document.createElement('input')
	dateTimeInput.setAttribute('name', 'dateUnloadActually')
	dateTimeInput.setAttribute('class', 'form-control form-control-sm loadDate')
	dateTimeInput.setAttribute('type', 'datetime-local')

	if (route.dateLoadPreviously) {
		dateTimeInput.setAttribute('min', `${route.dateLoadPreviously}T00:00`)
	}

	if (
		route.way === 'РБ'
		&& route.dateUnloadPreviouslyStock !== null
		&& route.timeUnloadPreviouslyStock !== null
	) {
		const container = document.createElement('div')
		const div1 = document.createElement('div')
		div1.className = 'font-weight-bold mb-1'
		div1.textContent = 'Слот на выгрузку:'

		const div2 = document.createElement('div')
		div2.className = 'text-danger mt-1 text-wrap'
		div2.textContent = 'Необходимо прибыть за 30 минут до назначенного времени!'

		const value = `${route.dateUnloadPreviouslyStock}T${route.timeUnloadPreviouslyStock}`
		dateTimeInput.value = value
		dateTimeInput.readOnly = true

		container.append(div1, dateTimeInput, div2)

		route._unloadDateInput = dateTimeInput
		return container
	}

	if (params.value) {
		dateTimeInput.value = params.value
		dateTimeInput.readOnly = true
	}

	route._unloadDateInput = dateTimeInput

	return dateTimeInput
}

// функции получения данных маршрута
function getTruckInfo(route) {
	if (!route) return ''
	const truck = route.truck ? route.truck : ''
	if (!truck) return ''
	const truckInfo = []
	truck.numTruck && truckInfo.push(truck.numTruck)
	truck.numTrailer && truckInfo.push(truck.numTrailer)
	return truckInfo.join(' / ')
}
function getDriverInfo(route) {
	if (!route) return ''
	const driver = route.driver ? route.driver : ''
	if (!driver) return ''
	const driverInfo = []
	driver.surname && driverInfo.push(driver.surname)
	driver.name && driverInfo.push(driver.name)
	driver.patronymic && driverInfo.push(driver.patronymic)
	return driverInfo.join(' ')
}
function getDateLoadInfo(route) {
	if (!route) return ''
	const dateLoadInfo = []
	route.simpleDateStart && dateLoadInfo.push(route.simpleDateStart)
	route.timeLoadPreviously && dateLoadInfo.push(route.timeLoadPreviously)
	return dateLoadInfo.join(' ')
}
function getDateUnloadInfo(route) {
	if (!route) return ''
	const dateUnloadInfo = []
	route.dateUnloadPreviouslyStock && dateUnloadInfo.push(route.dateUnloadPreviouslyStock)
	route.timeUnloadPreviouslyStock && dateUnloadInfo.push(route.timeUnloadPreviouslyStock)
	return dateUnloadInfo.join(' ')
}
function getCargoInfo(route) {
	if (!route) return ''

	const cargoInfo = []
	route.loadNumber && cargoInfo.push(`Погрузочный номер: ${route.loadNumber}`)
	route.roteHasShop && route.roteHasShop[0]?.cargo && cargoInfo.push(`Груз: ${route.roteHasShop[0].cargo}`)
	route.temperature && cargoInfo.push(`Температура: ${route.temperature}`)
	route.totalLoadPall && cargoInfo.push(`Паллеты: ${route.totalLoadPall} шт`)
	route.totalCargoWeight && cargoInfo.push(`Масса: ${route.totalCargoWeight} кг`)
	route.numPoint && cargoInfo.push(`Кол-во точек: ${route.numPoint}`)

	return cargoInfo.join(' ● ')
}
function getCostInfo(route) {
	if (!route) return ''
	if (route.way === 'Импорт' && route.expeditionCost !== null && route.driver) {
		const expeditionCost = route.expeditionCost ? route.expeditionCost : 0
		return `${route.finishPrice} ${route.startCurrency}. `
			+ `Стоимость экспедиторских услуг: ${expeditionCost} ${route.startCurrency}`
	}
	return `${route.finishPrice} ${route.startCurrency}`
}
function getDateLoadTruck(route) {
	if (!route) return ''
	const dateLoadActually = route.dateLoadActually
	const timeLoadActually = route.timeLoadActually
	if (!dateLoadActually || !timeLoadActually) return ''

	return `${dateLoadActually} ${timeLoadActually.replace('-', ':')}`
}
function getDateDelivery(route) {
	if (!route) return ''

	const dateUnloadActually = route.dateUnloadActually
	const timeUnloadActually = route.timeUnloadActually
	if (!dateUnloadActually || !timeUnloadActually) return ''

	return `${dateUnloadActually} ${timeUnloadActually.replace('-', ':')}`
}


function sendStatus(text, idRoute) {
	sendMessage({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'disposition',
		text: text,
		idRoute: idRoute,
		status: "1"
	})
};
function sendProofDriverAndCar(idRoute, routeDirection) {
	var companyName = document.querySelector('input[id=companyName]').value;
	sendMessageHead({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'system',
		text: `Перевозчиком ${companyName} заявлен новый водитель и авто на маршрут ${routeDirection}`,
		idRoute: idRoute,
		url: `/speedlogist/main/logistics/international/routeShow?idRoute=${idRoute}`,
		status: "1"
	})
};
function sendReversDriverAndCar(idRoute, routeDirection) {
	var companyName = document.querySelector('input[id=companyName]').value;
	sendMessageHead({
		fromUser: document.querySelector('input[id=login]').value,
		toUser: 'system',
		text: `Перевозчиком ${companyName} отменен водитель и авто в маршруе ${routeDirection}`,
		idRoute: idRoute,
		url: `/speedlogist/main/logistics/international/routeShow?idRoute=${idRoute}`,
		status: "1"
	})
};