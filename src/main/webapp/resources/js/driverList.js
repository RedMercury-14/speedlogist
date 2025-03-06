import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { snackbar } from './snackbar/snackbar.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { dateHelper, getData, getDocumentValues } from './utils.js'
import { ajaxUtils } from './ajaxUtils.js'
import { uiIcons } from './uiIcons.js'
import { deleteDriverBaseUrl, getDriversUrl, saveNewDriverUrl, updateDriverUrl } from './globalConstants/urls.js'

const token = $("meta[name='_csrf']").attr("content")

let error = false

class CustomNoRowsOverlay {
	init(params) {
		this.eGui = document.createElement("div")
		this.eGui.innerHTML = `<div class="ag-overlay-loading-center" style="background-color: #b4bebe; pointer-events: auto;"></div>`

		const overlay = this.eGui.querySelector('.ag-overlay-loading-center')
		const noRowOverlayBtn = document.createElement('button')

		noRowOverlayBtn.type = 'button'
		noRowOverlayBtn.className = 'btn tools-btn'
		noRowOverlayBtn.innerText = '+ Добавить водителя'
		noRowOverlayBtn.addEventListener('click', () => addDriver())
		overlay.append(noRowOverlayBtn)
	}

	getGui() {
		return this.eGui
	}
}

const columnDefs = [
	{ headerName: 'ФИО', field: 'fio', flex: 1, },
	{ headerName: 'Телефон', field: 'telephone', cellClass: 'text-center', flex: 1, minWidth: 150, },
	{ headerName: 'Паспорт', field: 'numPass', },
	{ headerName: 'Водительское удостоверение', field: 'numDriverCard', },
]
const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2',
		flex: 2,
		minWidth: 250,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
	},
	suppressRowClickSelection: true,
	getContextMenuItems: getContextMenuItems,
	noRowsOverlayComponent: CustomNoRowsOverlay,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
}

window.onload = async () => {
	const gridDiv = document.querySelector('#myGrid')
	const drivers = await getData(getDriversUrl)

	renderTable(gridDiv, gridOptions, drivers)

	const addDriverBtn = document.querySelector('#addDriverBtn')
	const createDriverForm = document.querySelector('#createDriverForm')
	const driverImgInput = document.querySelector("#drivercard_file")
	const driverImgContainer = document.querySelector("#driverImageContainer")

	$('#driverModal').on('hide.bs.modal', (e) => createDriverForm.classList.remove('was-validated'))
	addDriverBtn.addEventListener('click', (e) => {
		driverImgContainer.innerHTML = ''
		createDriverForm.reset()
		addDriver()
	})
	driverImgInput.addEventListener("change", (e) => addImgToView(e, driverImgContainer))
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
}
async function updateTable() {
	gridOptions.api.showLoadingOverlay()

	const drivers = await getData(getDriversUrl)

	if (!drivers || !drivers.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(drivers)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(driver => {
		const fio = `${driver.surname && driver.surname} ${driver.name && driver.name} ${driver.patronymic && driver.patronymic}`

		return {
			...driver,
			fio
		}
	})
}
function getContextMenuItems(params) {
	const rowNode = params.node

	if (!rowNode) return

	const idUser = rowNode.data.idUser

	const result = [
		{
			name: `Добавить водителя`,
			action: () => {
				addDriver()
			},
			icon: uiIcons.personPlus,
		},
		// {
		// 	name: `Редактировать водителя`,
		// 	action: () => {
		// 		editDriver(rowNode.data)
		// 	},
		// 	icon: uiIcons.pencil,
		// },
		{
			name: `Удалить водителя`,
			action: () => {
				deleteDriver(idUser)
			},
			icon: uiIcons.trash,
		},
		// "separator",
		// "export",
	];

	return result;
}


// функции управления списком
function addDriver() {
	const createDriverForm = document.querySelector('#createDriverForm')
	const driverImgInput = document.querySelector("#drivercard_file")
	const driverImgContainer = document.querySelector("#driverImageContainer")

	createDriverForm.removeEventListener('submit', updateDriverFormCallback)
	createDriverForm.addEventListener('submit', addDiverFormCallback)
	driverImgContainer.innerHTML = ''
	driverImgInput.required = true
	driverImgInput.parentElement.classList.remove('none')
	createDriverForm.reset()
	openDriverModal('Добавить водителя')
}
function editDriver(data) {
	const createDriverForm = document.querySelector('#createDriverForm')
	const driverImgInput = document.querySelector("#drivercard_file")
	const driverImgContainer = document.querySelector("#driverImageContainer")

	createDriverForm.removeEventListener('submit', addDiverFormCallback)
	createDriverForm.addEventListener('submit', updateDriverFormCallback)
	driverImgContainer.innerHTML = ''
	driverImgInput.required = false
	driverImgInput.parentElement.classList.add('none')
	createDriverForm.reset()
	openDriverModal('Редактировать водителя')
	addDataToDriverForm(data, createDriverForm)
}
function deleteDriver(idUser) {
	if (!(confirm('Вы действительно хотите удалить водителя?'))) return false
	fetch(`${deleteDriverBaseUrl}?driverId=${idUser}`)
		.then(res => {
			if (res.ok) {
				updateTable()
				snackbar.show('Водитель удален!')
			} else {
				snackbar.show('Ошибка!')
			}
		})
		.catch(err => {
			const errorStatus = err.status ? err.status : ''
			snackbar.show(`Ошибка ${errorStatus}!`)
			console.log(err)
		})
}

// обработчик отправки данных формы для ДОБАВЛЕНИЯ водителя
function addDiverFormCallback(e) {
	e.preventDefault()
	
	if (e.target.checkValidity() === false || error) {
		e.target.classList.add('was-validated')
		return
	}

	bootstrap5overlay.showOverlay()

	const formData = new FormData(e.target)
	formData.delete('idUser')
	const updatedFormData = updateDriverFormData(formData)

	ajaxUtils.postMultipartFformData({
		url: saveNewDriverUrl,
		token: token,
		data: updatedFormData,
		successCallback: (response) => {
			if (response) {
				console.log(response)
				snackbar.show('Водитель добавлен!')
				updateTable()
				$(`#driverModal`).modal('hide')
			} else {
				snackbar.show('Ошибка: возможно, такой водитель уже существует')
			}
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}
// обработчик отправки данных формы для ОБНОВЛЕНИЯ данных водителя
function updateDriverFormCallback(e) {
	e.preventDefault()
	
	if (e.target.checkValidity() === false || error) {
		e.target.classList.add('was-validated')
		return
	}

	bootstrap5overlay.showOverlay()

	const formData = new FormData(e.target)
	formData.delete('drivercard_file')
	const updatedFormData = updateDriverFormData(formData)

	const data = Object.fromEntries(updatedFormData)
	console.log(data)

	ajaxUtils.postMultipartFformData({
		url: updateDriverUrl,
		token: token,
		data: updatedFormData,
		successCallback: (response) => {
			if (response) {
				console.log('response', response)
				snackbar.show('Данные обновлены!')
				updateTable()
				$(`#driverModal`).modal('hide')
			} else {
				snackbar.show('Произошла ошибка, исправьте данные')
			}
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}

// форматирование данных формы
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

// добавление данных в форму при редактировании данных водителя
function addDataToDriverForm(data, driverForm) {
	const [ numpassValue1, numpassValue2, numpassValue3, numpassValue4 ] = getDocumentValues(data.numPass)
	const [ numDriverCardValue1, numDriverCardValue2, numDriverCardValue3, numDriverCardValue4 ] = getDocumentValues(data.numDriverCard)

	driverForm.idUser.value = data.idUser
	driverForm.surname.value = data.surname
	driverForm.name.value = data.name
	driverForm.patronymic.value = data.patronymic ? data.patronymic : ''
	driverForm.tel.value = data.telephone

	driverForm.numpass_1.value = numpassValue1
	driverForm.numpass_2.value = numpassValue2
	driverForm.numpass_3.value = numpassValue3
	driverForm.numpass_4.value = numpassValue4

	driverForm.numdrivercard_1.value = numDriverCardValue1
	driverForm.numdrivercard_2.value = numDriverCardValue2
	driverForm.numdrivercard_3.value = numDriverCardValue3
	driverForm.numdrivercard_4.value = numDriverCardValue4
}

// добавление изображения в форму
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

function openDriverModal(title) {
	$('#driverModalLabel').html(title)
	$('#driverModal').modal('show')
}
