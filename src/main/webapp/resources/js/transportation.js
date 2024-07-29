import { ws } from './global.js';
import { wsHead } from './global.js';
import { snackbar } from './snackbar/snackbar.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js';
import { dateHelper } from './utils.js';
import { ajaxUtils } from './ajaxUtils.js';

let error = false

let targetTruckSelect
let targetDriverSelect

//try{}catch(e){}
//ws.onmessage = (e) => onMessage(JSON.parse(e.data));
function sendMessage(message) {
	ws.send(JSON.stringify(message));
}
function sendMessageHead(message) {
	wsHead.send(JSON.stringify(message));
}

window.onload = function() {
	const token = $("meta[name='_csrf']").attr("content")
	const createTruckForm = $('#createTruckForm')
	const createDriverForm = $('#createDriverForm')
	const truckSelects = document.querySelectorAll('#isTruck')
	const driverSelects = document.querySelectorAll('#isDriver')
	const truckImgInput = document.querySelector("#technical_certificate_file")
	const truckImgContainer = document.querySelector("#truckImageContainer")
	const driverImgInput = document.querySelector("#drivercard_file")
	const driverImgContainer = document.querySelector("#driverImageContainer")

	// const searchInSelectToggler = document.querySelector('#searchInSelectToggler')
	// searchInSelectToggler && searchInSelectToggler.addEventListener('click', searchInSelectTogglerOnClickHandler)

	for (let i = 0; i < truckSelects.length; i++) {
		const truckSelect = truckSelects[i]
		truckSelect.addEventListener('change', function(e) {
			if (this.value === 'addTruck') {
				targetTruckSelect = truckSelect
				openNewCarModal()
			}
		})
	}
	for (let i = 0; i < driverSelects.length; i++) {
		const driverSelect = driverSelects[i]
		driverSelect.addEventListener('change', function(e) {
			if (this.value === 'addDriver') {
				targetDriverSelect = driverSelect
				openNewDriverModal()
			}
		})
	}

	$('#truckModal').on('hide.bs.modal', function (e) {
		targetTruckSelect.value = ''
		createTruckForm.removeClass('was-validated')
	})
	$('#driverModal').on('hide.bs.modal', function (e) {
		targetDriverSelect.value = ''
		createDriverForm.removeClass('was-validated')
	})

	createTruckForm.on('submit', (e) => onSubmitFormCallback(e, '../../api/carrier/saveNewTruck', token, truckSelects))
	createDriverForm.on('submit', (e) => onSubmitFormCallback(e, '../../api/carrier/saveNewDriver', token, driverSelects))

	truckImgInput.addEventListener("change", (e) => addImgToView(e, truckImgContainer))
	driverImgInput.addEventListener("change", (e) => addImgToView(e, driverImgContainer))

	// проверка наличия машины в базе по госномеру
	// $('#numTruck').change(function(e) {
	// 	const input = e.target
	// 	$.ajax({
	// 		url: `../../api/carrier/isContainCar/${input.value}`,
	// 		method: 'get',
	// 		dataType: 'json',
	// 		success: function(hasTruck){
	// 			console.log(hasTruck)
	// 			if (hasTruck) {
	// 				$('#messageNumTruck').text('Машина с таким номером уже зарегистрирована')
	// 				input.classList.add('is-invalid')
	// 				error = true
	// 			}
	// 			else {
	// 				$('#messageNumTruck').text('')
	// 				input.classList.remove('is-invalid')
	// 				error = false
	// 			}
	// 		}
	// 	})
	// })

	changeFooterPosition()
	bootstrap5overlay.hideOverlay()
}

let rows = document.querySelectorAll('tr');
for (let i = 1; i < rows.length; i++) {
	let row = rows[i];
	let idRoute = row.querySelector('td[id=idTarget]').innerHTML;
	row.querySelector('#status').addEventListener('mousedown', (event) => {
		var index = row.querySelector('#option').options.selectedIndex
		var text = row.querySelector('#option').options[index].value
		sendStatus(text, idRoute)
	})
	if (row.querySelector('input[name=update]') != null) {
		row.querySelector('input[name=update]').addEventListener('mousedown', () => {
			var routeDirection = row.querySelector('td[id=routeDirection]').innerHTML;
			sendProofDriverAndCar(idRoute, routeDirection);
		})
	} else {
		row.querySelector('input[name=revers]') &&
		row.querySelector('input[name=revers]').addEventListener('mousedown', () => {
			var routeDirection = row.querySelector('td[id=routeDirection]').innerHTML;
			sendReversDriverAndCar(idRoute, routeDirection);
		})

	}
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

function openNewCarModal() {
	$('#truckModal').modal('show')
}
function openNewDriverModal() {
	$('#driverModal').modal('show')
}

function onSubmitFormCallback(e, url, token, selects) {
	e.preventDefault()

	bootstrap5overlay.showOverlay()

	if (e.target.checkValidity() === false || error) {
		e.target.classList.add('was-validated')
		return
	}

	const type = `${e.target.id.slice(6).slice(0, -4).toLowerCase()}`

	let formData = new FormData(e.target)

	if (type === 'driver') formData = updateDriverFormData(formData)
	else if (type === 'truck') formData = updateTruckFormData(formData)

	const successMessage = type === 'driver'
		? 'Ошибка: возможно, такой водитель уже существует'
		: 'Ошибка: возможно, такая машина уже существует'

	// отправка формы
	ajaxUtils.postMultipartFformData({
		url: url,
		token: token,
		data: formData,
		successCallback: (res) => {
			if (res) {
				console.log(res)
				$(`#${type}Modal`).modal('hide')
				addNewOption(type, selects, res)
			} else {
				snackbar.show(successMessage)
			}
			bootstrap5overlay.hideOverlay()
		},
		errorCallback: () => bootstrap5overlay.hideOverlay()
	})
}

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

function addNewOption(type, selects, data) {
	for (let i = 0; i < selects.length; i++) {
		const select = selects[i]

		const truckOptionName = data.numTrailer ? `${data.numTruck}/${data.numTrailer}` : `${data.numTruck}`

		const newOption =
			type === 'driver'
				? new Option(`${data.name} ${data.surname}`, data.idUser)
				: type === 'truck'
					? new Option(truckOptionName, data.idTruck)
					: null

		select.append(newOption)

		if (select === targetDriverSelect || select === targetTruckSelect) {
			newOption.selected = true
		}
	}
}

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

function changeFooterPosition() {
	const viewWidth = window.innerWidth
	const viewHeight = window.innerHeight
	const bodyHeight = document.body.offsetHeight
	
	if (viewWidth < 500 && (bodyHeight + 80) < viewHeight) {
		document.querySelector('footer').style.position = 'fixed'
	}
}


function searchInSelectTogglerOnClickHandler(e) {
	const hasSearchInSelect = e.target.checked
	console.log("🚀 ~ searchInSelectTogglerOnClickHandler ~ hasSearchInSelect:", hasSearchInSelect)

	if (hasSearchInSelect) {
		// показать алерт с сообщением о включении функции поиска
		// в выпадающих списках выбора водителя и авто и обновлении
		// страницы
		const answer = confirm(`Включить функцию поиска в выпадающих списках выбора водителя и авто?
Внимание, это может повлиять на производительность.
Страница будет обновлена.`)

		if (answer) {
			// записать в ЛС значение флага hasSearchInSelect как true
			// обновить страницу
		} else {
			// изменить чекбокс на false
			e.target.checked = false
		}

	} else {
		// показать алерт с сообщением о выключении функции поиска
		// в выпадающих списках выбора водителя и авто и обновлении
		// страницы
		const answer = confirm(`Выключить функцию поиска в выпадающих списках?`)

		if (answer) {
			// записать в ЛС значение флага hasSearchInSelect как false
			// обновить страницу
		} else {
			// изменить чекбокс на true
			e.target.checked = true
		}
	}
}

function createSelectWithSearch(selectId, options) {
	const select = document.createElement('select')
	select.name = selectId
	select.id = selectId
	select.classList.add('selectpicker')
	select.setAttribute('required', 'true')
	select.setAttribute('data-live-search', 'true')
	select.setAttribute('data-size', '8')
	select.setAttribute('data-width', '75%')

	select.append(options)

	// const option = document.createElement('option')
	// option.value = ''
	// option.innerHTML = 'Выберите склад'
	// option.selected = true
	// option.disabled = true
	// option.hidden = true
	// select.append(option)

	for (let i = 0; i < options.length; i++) {
		const item = options[i];
		const option = document.createElement('option')
		option.value = item.value
		option.innerHTML = item.text
		select.append(option)
		
	}

	return select
}