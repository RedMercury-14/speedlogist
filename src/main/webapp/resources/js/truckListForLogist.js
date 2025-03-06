import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { snackbar } from './snackbar/snackbar.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { dateHelper, getData } from './utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { uiIcons } from './uiIcons.js'
import { editTruckUrl, getCarByIdUserBaseUrl, isContainTruckBaseUrl, verifyCarBaseUrl } from './globalConstants/urls.js'

const token = $("meta[name='_csrf']").attr("content")
const viewportWidth = window.innerWidth

let error = false

const idCarrier = getCarrierId()

const rowClassRules = {
	'grey-row': params => !params.node.data.verify
}

const columnDefs = [
	{ headerName: 'Номер', field: 'numTruck', checkboxSelection: true,},
	{ headerName: 'Марка', field: 'brandTruck', flex: 1, },
	{ headerName: 'Модель', field: 'modelTruck', flex: 1, },
	{ headerName: 'Номер прицепа', field: 'numTrailer', },
	{ headerName: 'Тип кузова/прицепа', field: 'typeTrailer', minWidth: 70, },
	{ headerName: 'Грузоподъемность', field: 'cargoCapacity', cellClass: 'text-center', minWidth: 70, },
	{ headerName: 'Паллетовместимость', field: 'pallCapacity', cellClass: 'text-center', minWidth: 70, },
	{ headerName: 'Объем', field: 'volumeTrailer', flex: 1, cellClass: 'text-center', minWidth: 70, },
	{ headerName: 'Техпаспорт', field: 'technicalCertificate', minWidth: 70, },
	// { headerName: 'Подтвержден?', field: 'verify', cellClass: 'checkbox-value' },
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2',
		flex: 2,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
	},
	rowSelection: 'multiple',
	suppressRowClickSelection: true,
	getContextMenuItems: getContextMenuItems,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
}

window.onload = async () => {
	const gridDiv = document.querySelector('#myGrid')
	const trucks = await getData(getCarByIdUserBaseUrl + idCarrier)

	renderTable(gridDiv, gridOptions, trucks)

	const createTruckForm = document.querySelector('#createTruckForm')
	const numTruckInput = document.querySelector('#numTruck')
	const truckImgInput = document.querySelector("#technical_certificate_file")
	const truckImgContainer = document.querySelector("#truckImageContainer")

	createTruckForm.addEventListener('submit', updateTruckFormCallback)
	$('#truckModal').on('hide.bs.modal', (e) => createTruckForm.classList.remove('was-validated'))
	// numTruckInput.addEventListener('change', onNumTruckInputHandler)
	truckImgInput.addEventListener("change", (e) => addImgToView(e, truckImgContainer))
}

function getCarrierId() {
	const url = new URL(window.location.href)
	const searchParams = new URLSearchParams(url.search)
	return searchParams.get('idCarrier')
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
	gridOptions.api.hideOverlay()

	if (viewportWidth < 992) {
		autoSizeAll(false)
	}
}
async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const trucks = await getData(getCarByIdUserBaseUrl + idCarrier)

	if (!trucks || !trucks.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(trucks)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(truck => {
		const isVerify = truck.verify === null ? true : truck.verify

		return {
			...truck,
			verify: isVerify
		}
	})
}
function getContextMenuItems(params) {
	const rowNode = params.node

	if (!rowNode) return

	const idTruck = rowNode.data.idTruck
	const isVerify = rowNode.data.verify
	const selectedRows = params.api.getSelectedRows()
	const idsTruckInArray = selectedRows.map(truck => truck.idTruck)
	const isVeryfySelectedTruck = selectedRows.map(truck => truck.verify).includes(true)

	const verifyOneTruckBtnName = isVerify ? 'Убрать подтверждение': `Подтвердить автомобиль`
	const verifyOneTruckBtnIcon = isVerify ? uiIcons.x_lg : uiIcons.check

	const result = [
		{
			name: verifyOneTruckBtnName,
			action: () => {
				verifyOneTruck(idTruck, isVerify)
			},
			icon: verifyOneTruckBtnIcon,
		},
		{
			name: `Подтвердить выбранные автомобили`,
			disabled: !selectedRows.length || isVeryfySelectedTruck,
			action: () => {
				verifyTrucks(idsTruckInArray)
			},
			icon: uiIcons.checkAll,
		},
		{
			name: `Редактировать автомобиль`,
			disabled: isVerify,
			action: () => {
				editTruck(rowNode.data)
			},
			icon: uiIcons.pencil,
		},
		"separator",
		"export",
	];

	return result;
}
function autoSizeAll(skipHeader) {
	const allColumnIds = []
	gridOptions.columnApi.getColumns().forEach((column) => {
		allColumnIds.push(column.getId())
	})

	gridOptions.columnApi.autoSizeColumns(allColumnIds, skipHeader)
}

async function verifyOneTruck(truckId, isVerify) {
	const successMessage = isVerify
		? 'Подтверждение отменено'
		: `Подтверждение прошло успешно!`
	const res = await getData(verifyCarBaseUrl+truckId)

	if (res && res.status === '200') {
		snackbar.show(successMessage)
		updateTable()
	} else {
		console.log(res)
		snackbar.show(`Ошибка!`)
	}
}
function verifyTrucks(idList) {
	try {
		const request = idList.map(id => {
			fetch(verifyCarBaseUrl+id)
		})
	
		Promise.allSettled(request)
			.then( async (responses) => {
				setTimeout(() => {
					snackbar.show(`Подтверждение прошло успешно!`)
					updateTable()
				}, 100);
			})
	} catch (error) {
		snackbar.show('Упс, что-то пошло не так...')
		console.log(error)
	}
}
function editTruck(data) {
	const createTruckForm = document.querySelector('#createTruckForm')
	const TCfileInput = document.querySelector('#technical_certificate_file')
	const truckImgContainer = document.querySelector("#truckImageContainer")

	truckImgContainer.innerHTML = ''
	TCfileInput.required = false
	TCfileInput.parentElement.classList.add('none')
	openCarModal('Редактировать машину')
	addDataToTruckForm(data, createTruckForm)
}

// обработчик отправки данных формы для ОБНОВЛЕНИЯ машины
function updateTruckFormCallback(e) {
	e.preventDefault()
	
	if (e.target.checkValidity() === false || error) {
		e.target.classList.add('was-validated')
		return
	}

	bootstrap5overlay.showOverlay()

	const formData = new FormData(e.target)
	formData.delete('technical_certificate_file')
	const updatedFormData = updateTruckFormData(formData)

	const data = Object.fromEntries(updatedFormData)
	console.log(data)

	ajaxUtils.postMultipartFformData({
		url: editTruckUrl,
		token: token,
		data: updatedFormData,
		successCallback: (response) => {
			if (response) {
				console.log('response', response)
				snackbar.show('Данные обновлены!')
				updateTable()
				$(`#truckModal`).modal('hide')
			} else {
				snackbar.show('Произошла ошибка, исправьте данные')
			}
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}

// проверка наличия машины в базе по госномеру
function onNumTruckInputHandler(e) {
	const input = e.target

	$.ajax({
		url: isContainTruckBaseUrl + input.value,
		method: 'get',
		dataType: 'json',
		success: function(hasTruck){
			console.log(hasTruck)
			if (hasTruck) {
				$('#messageNumTruck').text('Машина с таким номером уже зарегистрирована')
				input.classList.add('is-invalid')
				error = true
			}
			else {
				$('#messageNumTruck').text('')
				input.classList.remove('is-invalid')
				error = false
			}
		}
	})
}

// форматирование данных формы
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

// добавление данных в форму при редактировании машины
function addDataToTruckForm(data, truckForm) {
	const technicalCertificateData = data.technicalCertificate.split(' ')
	const [ value1, value2, value3, value4 ] = getTechnicalCertificateValues(technicalCertificateData)
	const dimensionsBodyData = data.dimensionsBody.split('/')
	const infoData = data.info.split('; ')

	truckForm.idTruck.value = data.idTruck
	truckForm.brandTruck.value = data.brandTruck
	truckForm.modelTruck.value = data.modelTruck
	truckForm.numTruck.value = data.numTruck
	truckForm.ownerTruck.value = data.ownerTruck
	truckForm.numTrailer.value = data.numTrailer
	truckForm.brandTrailer.value = data.brandTrailer

	truckForm.number_axes.value = data.number_axes
	truckForm.technical_certificate_1.value = value1
	truckForm.technical_certificate_2.value = value2
	truckForm.technical_certificate_3.value = value3
	truckForm.technical_certificate_4.value = value4

	truckForm.typeTrailer.value = data.typeTrailer
	truckForm.hitch_type.value = data.hitchType
	truckForm.type_of_load.value = data.typeLoad
	truckForm.cargoCapacity.value = Number(data.cargoCapacity) / 1000
	truckForm.volume_trailer.value = Number(data.volumeTrailer)
		
	truckForm.dimensions_1.value = dimensionsBodyData[0]
	truckForm.dimensions_2.value = dimensionsBodyData[1]
	truckForm.dimensions_3.value = dimensionsBodyData[2]

	truckForm.pallCapacity.value = Number(data.pallCapacity)
	
	truckForm.check_1.checked = infoData.includes('Гидроборт')
	truckForm.check_2.checked = infoData.includes('GPS-навигация')
	truckForm.check_3.checked = infoData.includes('Ремни')
	truckForm.check_4.checked = infoData.includes('Стойки')
	truckForm.check_5.checked = infoData.includes('Пневмоподушки')
}
function getTechnicalCertificateValues(TCArray) {
	const array = TCArray
	const technicalCertificate_1 = array.shift()
	const technicalCertificate_2 = Number(array.shift().slice(0, -1))
	const technicalCertificate_4 = array.pop().split('.').reverse().join('-')
	array.shift()
	array.pop()
	const technicalCertificate_3 = array.join(' ')

	return [
		technicalCertificate_1,
		technicalCertificate_2,
		technicalCertificate_3,
		technicalCertificate_4
	]
	
}

function openCarModal(title) {
	$('#truckModalLabel').html(title)
	$('#truckModal').modal('show')
}
