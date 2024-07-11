import { snackbar } from "./snackbar/snackbar.js"
import { autocomplete } from './autocomplete/autocomplete.js'
import { countries } from './global.js'
import { ajaxUtils } from "./ajaxUtils.js"
import {
	addDataToCountryInputs,
	changeCargoInfoInputsRequired,
	changeTemperatureInputRequired,
	changeTnvdInputRequired,
	controlUKZSelectOnChangeHandler,
	showIncotermsInput,
	showIncotermsInsuranseInfo
} from "./procurementFormUtils.js"
import { disableButton, enableButton, getData } from "./utils.js"

const editProcurement = "../../../api/manager/editProcurement"
const token = $("meta[name='_csrf']").attr("content")

let error = false

window.onload = () => {
	addDataToCountryInputs()

	const editOrderForm = document.querySelector('#orderForm')
	const cancelBtn = document.querySelector('#cancelBtn')
	const counrtyInputs = document.querySelectorAll('.country-input')
	const typeTruckInput = document.querySelector('#typeTruck')
	const methodLoadInput = document.querySelector('#methodLoad')
	const wayInput = document.querySelector('#way')
	const points = document.querySelectorAll('.point')

	// проверка наличия заявки по номеру из маркета
	const marketNumberMessageElem = document.querySelector('#marketNumberMessage')
	const marketNumberInput = document.querySelector('#marketNumber')
	marketNumberInput.addEventListener('change', async (e) => {
		marketNumberInputOnChangeHandler(e, marketNumberMessageElem)
	})

	// установка значения адреса таможни точки выгрузки
	const controlUKZSelect = document.querySelector('select#control')
	controlUKZSelect && controlUKZSelect.addEventListener('change', (e) => {
		points.forEach(point => {
			if (point.dataset.type === 'Выгрузка') {
				controlUKZSelectOnChangeHandler(e, point, true)
			}
		})
	})

	// листнер на изменение способа загрузки
	methodLoadInput.addEventListener('change', (e) => {
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа кузова
	typeTruckInput.addEventListener('change', (e) => {
		const typeTruck = e.target.value
		changeTemperatureInputRequired(typeTruck)
		showIncotermsInput(typeTruck)
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})

	// листнер на изменение типа маршрута
	wayInput.addEventListener('change', (e) => changeTnvdInputRequired(e))

	// листнер на изменение условий поставки
	const incotermsInput = document.querySelector('#incoterms')
	incotermsInput && incotermsInput.addEventListener('change', showIncotermsInsuranseInfo)

	editOrderForm.addEventListener('submit', (e) => orderFormSubmitHandler(e))

	// листнер на отмену создания заявки
	cancelBtn.addEventListener('click', () => {
		window.location.href = '../orders'
	})

	// автозаполнение всплывающего окна при заполнении поля страны
	for (let i = 0; i < counrtyInputs.length; i++) {
		const counrtyInput = counrtyInputs[i]
		autocomplete(counrtyInput, countries)
	}

	points.forEach(point => changeCargoInfoInputsRequired(point))
}

// обработчики отправки форм
function orderFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = orderFormDataFormatter(formData)

	if (!validateForm(data)) {
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: editProcurement,
		token: token,
		data: data,
		successCallback: (res) => {
			if (res.status === '200') {
				snackbar.show('Заявка изменена!')
				setTimeout(() => {
					window.location.href = '../orders'
				}, 500)
			} else {
				snackbar.show('Возникла ошибка - обновите страницу!')
				enableButton(e.submitter)
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
		}
	})
}

// форматирование данных формы
function orderFormDataFormatter(formData) {
	const data = Object.fromEntries(formData)

	const points = []

	let i = 1
	while (data[`type_${i}`]) {
		const point = {
			pointNumber: i,
			idAddress: +data[`idAddress_${i}`],
			oldIdaddress: +data[`oldIdaddress_${i}`],
			isCorrect: data[`isCorrect_${i}`] === 'true' ? true : false,
			type: data[`type_${i}`],
			date: data[`date_${i}`],
			time: data[`time_${i}`],
			cargo: data[`pointCargo_${i}`],
			pall: data[`pall_${i}`],
			weight: data[`weight_${i}`],
			volume: data[`volume_${i}`],
			tnvd: data[`tnvd_${i}`] ? data[`tnvd_${i}`] : '',
			bodyAdress: data[`country_${i}`] + '; ' + data[`pointAddress_${i}`].replace(/;/g, ','),
			customsAddress: data[`customsAddress_${i}`],
			timeFrame: data[`timeFrame_${i}`],
			contact: data[`pointContact_${i}`],
		}

		points.push(point)
		i++
	}

	const control = data.control === 'Да'
	const stacking = data.stacking === 'Да'

	const dateDelivery = points.length && points[points.length - 1].date
		? points[points.length - 1].date : ''

	return {
		idOrder: +data.idOrder,
		contertparty: data.contertparty,
		contact: data.contact,
		control,
		way: data.way,
		marketNumber: data.marketNumber,
		comment: data.comment,
		temperature: data.temperature,
		typeLoad: data.typeLoad ? data.typeLoad : '',
		methodLoad: data.methodLoad ? data.methodLoad : '',
		typeTruck: data.typeTruck ? data.typeTruck : '',
		stacking,
		cargo: data.cargo,
		dateDelivery,
		points,
		needUnloadPoint: data.needUnloadPoint,
		loadNumber: data.loadNumber,
		isInternalMovement: data.isInternalMovement,
	}
}

// валидация формы
function validateForm(data) {
	if (error) {
		snackbar.show('Проверьте данные!')
		return false
	}

	return true
}

function marketNumberInputOnChangeHandler(e, messageElem) {
	const marketNumber = e.target.value
	if (marketNumber) {
		checkMarketCode(marketNumber, messageElem)
	} else {
		error = false
		messageElem.innerHTML = ''
	}
}

async function checkMarketCode(marketNumber, messageElem) {
	const res = await getData(`../../../api/procurement/checkMarketCode/${marketNumber}`)
	if (res.status === '200') {
		if (res.message === 'true') {
			error = true
			messageElem.innerHTML = 'Заявка с таким номером уже существует'
		} else {
			error = false
			messageElem.innerHTML = ''
		}
	}
}
