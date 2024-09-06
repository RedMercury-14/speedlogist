import { snackbar } from "./snackbar/snackbar.js"
import { ajaxUtils } from "./ajaxUtils.js"
import {
	addDataToRouteForm,
	addListnersToPoint,
	changeCargoInfoInputsRequired,
	changeForm,
	dangerousInputOnChangeHandler,
	inputEditBan,
	showIncotermsInsuranseInfo,
	transformAddressInputToSelect,
	typeTruckOnChangeHandler
} from "./procurementFormUtils.js"
import { dateHelper, disableButton, enableButton, getData } from "./utils.js"
import { getOrderData, getOrderForForm } from "./procurementFormDataUtils.js"
import {
	getAddressHTML,
	getAddressInfoHTML,
	getCargoInfoHTML,
	getCustomsAddressHTML,
	getDateHTML,
	getTimeHTML,
	getTnvdHTML,
} from "./procurementFormHtmlUtils.js";

const editProcurement = "../../../api/manager/editProcurement"
const getInternalMovementShopsUrl = "../../../api/manager/getInternalMovementShops"
const token = $("meta[name='_csrf']").attr("content")

const FORM_TYPE = 'edit'

let error = false

let domesticStocks
let editableOrder

window.onload = async () => {
	// получаем внутренние склады
	const domesticStocksData = await getData(getInternalMovementShopsUrl)
	domesticStocks = domesticStocksData.map(stock => `${stock.numshop}-${stock.address}`)

	// здесь необходимо получить заказа по id и добавить в форму его данные
	editableOrder = await getOrderForForm(FORM_TYPE)

	const editOrderForm = document.querySelector('#orderForm')
	const cancelBtn = document.querySelector('#cancelBtn')
	const typeTruckInput = document.querySelector('#typeTruck')
	const methodLoadInput = document.querySelector('#methodLoad')
	const incotermsInput = document.querySelector('#incoterms')
	const dangerousInput = document.querySelector('#dangerous')

	// измеяем форму заявки и добавляем данные
	changeForm(editableOrder, FORM_TYPE)
	addDataToRouteForm(editableOrder, editOrderForm, createPoint)
	// изменяем редактирование формы
	const points = document.querySelectorAll('.point')
	changeEditingRules(editableOrder, editOrderForm, points)

	// листнер на изменение способа загрузки
	methodLoadInput.addEventListener('change', (e) => {
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})
	// листнер на изменение типа кузова
	typeTruckInput.addEventListener('change', (e) => {
		typeTruckOnChangeHandler(e)
		points.forEach(point => changeCargoInfoInputsRequired(point))
	})
	// листнер на изменение условий поставки
	incotermsInput && incotermsInput.addEventListener('change', showIncotermsInsuranseInfo)
	// обработчик на поле Опасный груз
	// dangerousInput && dangerousInput.addEventListener('change', dangerousInputOnChangeHandler)
	// обработчик отправки формы
	editOrderForm.addEventListener('submit', (e) => orderFormSubmitHandler(e))
	// листнер на отмену редактирования заявки
	cancelBtn.addEventListener('click', () => window.location.href = '../orders')
}


// обработчики отправки форм
function orderFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = getOrderData(formData, editableOrder, null)
	const updatedData = updateEditFormData(data)

	if (!validateForm(updatedData)) {
		return
	}

	disableButton(e.submitter)

	ajaxUtils.postJSONdata({
		url: editProcurement,
		token: token,
		data: updatedData,
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

// валидация формы
function validateForm(data) {
	if (error) {
		snackbar.show('Проверьте данные!')
		return false
	}

	return true
}

// обновление данных формы редактирования
function updateEditFormData(data) {
	const pointForms = document.querySelectorAll('.pointForm')
	// добавляем информацию о точках маршрута
	pointForms.forEach((form, i) => {
		const idAddress = form.idAddress.value
		const oldIdaddress = form.oldIdaddress.value
		const isCorrect = form.isCorrect.value
		data.points[i].idAddress = idAddress !== 'null' ? +idAddress : null
		data.points[i].oldIdaddress = oldIdaddress !== 'null' ? +oldIdaddress : 0
		data.points[i].isCorrect = isCorrect === 'true'
	})

	// удаляем статус, т.к. он не изменяется
	delete data.status

	return data
}


// метод создания точки маршрута для заявки
function createPoint(orderData, pointData, index) {
	const point = document.createElement('div')
	const pointIndex = index + 1
	const date = dateHelper.getDateForInput(pointData.date)
	const type = pointData.type ? pointData.type : ''
	const time = pointData.time ? pointData.time.slice(0,5) : ''
	const tnvd = pointData.tnvd ? pointData.tnvd : ''
	const bodyAddress = pointData.bodyAddress ? pointData.bodyAddress : ''
	const customsAddress = pointData.customsAddress ? pointData.customsAddress : ''

	const isInternalMovement = orderData.isInternalMovement === 'true'
	const way = orderData.way
	const EAEUImport = false

	const idAddress = pointData.idAddress
	const oldIdaddress = pointData.oldIdaddress
	const isCorrect = pointData.isCorrect

	const dateHTML = getDateHTML(isInternalMovement, type, way, pointIndex, date)
	const timeHTML = getTimeHTML(type, way, pointIndex, time)
	const tnvdHTML = getTnvdHTML(type, way, pointIndex, tnvd)
	const cargoInfoHTML = getCargoInfoHTML(orderData, isInternalMovement, way, pointIndex, pointData)
	const addressHTML = getAddressHTML(orderData, type, way, pointIndex, bodyAddress)
	const addressInfoHTML = getAddressInfoHTML(type, way, pointIndex, pointData)
	const customsAddressHTML = getCustomsAddressHTML(EAEUImport, type, way, pointIndex, customsAddress)

	point.className = 'card point'
	point.innerHTML = `
		<form class='pointForm' id='pointform_${pointIndex}' name='pointform_${pointIndex}' action=''>
			<div class='card-header d-flex justify-content-between'>
				<h5 class='d-flex align-items-center mb-0'>
					Точка ${pointIndex}: ${type}
				</h5>
				<input type='hidden' class='form-control' name='type' id='type' value='${type}'>
				<input type="hidden" class="form-control" name="idAddress" id="idAddress_${pointIndex}" value='${idAddress}'>
				<input type="hidden" class="form-control" name="oldIdaddress" id="oldIdaddress_${pointIndex}" value='${oldIdaddress}'>
				<input type="hidden" class="form-control" name="isCorrect" id="isCorrect_${pointIndex}" value='${isCorrect}'>
			</div>
			<div class='card-body'>
				<div class='row-container info-container form-group'>
					${dateHTML}
					${timeHTML}
					${cargoInfoHTML}
				</div>
				${tnvdHTML}
				${addressHTML}
				<div class='row-container form-group'>
					${addressInfoHTML}
				</div>
				${customsAddressHTML}
			</div>
		</form>
	`

	addListnersToPoint(point, way, pointIndex)

	// изменение полей адреса для заявок внутреннего перемещения
	if (isInternalMovement) {
		setTimeout(() => {
			transformAddressInputToSelect(point, domesticStocks)
			$('.selectpicker').selectpicker()
		}, 0)
	}

	return point
}

// изменение правил редактирования формы
function changeEditingRules(editableOrder, editOrderForm, points) {
	const orderStatus = editableOrder.status
	const way = editableOrder.way
	const isInternalMovement = editableOrder.isInternalMovement === 'true'

	inputEditBan(editOrderForm, '#marketNumber', true)

	// для заявок с маршрутом
	if (orderStatus > 20) {
		inputEditBan(editOrderForm, '#counterparty', true)
		inputEditBan(editOrderForm, '#contact', true)
		inputEditBan(editOrderForm, '#recipient', true)
		inputEditBan(editOrderForm, '#control', true)
		inputEditBan(editOrderForm, '#loadNumber', true)
		inputEditBan(editOrderForm, '#typeLoad', true)
		inputEditBan(editOrderForm, '#methodLoad', true)
		inputEditBan(editOrderForm, '#typeTruck', true)
		inputEditBan(editOrderForm, '#routeComments', true)
		inputEditBan(editOrderForm, '#incoterms', true)
		inputEditBan(editOrderForm, '#deliveryLocation', true)
		inputEditBan(editOrderForm, '#stacking', true)
		inputEditBan(editOrderForm, '#cargo', true)
		inputEditBan(editOrderForm, '#truckLoadCapacity', true)
		inputEditBan(editOrderForm, '#truckVolume', true)
		inputEditBan(editOrderForm, '#temperature', true)
		inputEditBan(editOrderForm, '#phytosanitary', true)
		inputEditBan(editOrderForm, '#veterinary', true)
		inputEditBan(editOrderForm, '#dangerous', true)
		inputEditBan(editOrderForm, '#dangerousUN', true)
		inputEditBan(editOrderForm, '#dangerousClass', true)
		inputEditBan(editOrderForm, '#dangerousPackingGroup', true)
		inputEditBan(editOrderForm, '#dangerousRestrictionCodes', true)
		inputEditBan(editOrderForm, '#comment', true)
	}

	points.forEach((point, i) => {
		const pointIndex = i + 1

		// для заявок с маршрутом
		if (orderStatus > 20) {
			const dateInput = point.querySelector(`#date_${pointIndex}`)
			dateInput && dateInput.removeAttribute('min')
			inputEditBan(point, `#date_${pointIndex}`, true)

			const timeInput = point.querySelector(`#time_${pointIndex}`)
			timeInput && dateInput.removeAttribute('min')
			inputEditBan(point, `#time_${pointIndex}`, true)

			inputEditBan(point, `#pointCargo_${pointIndex}`, true)
			inputEditBan(point, `#pall_${pointIndex}`, true)
			inputEditBan(point, `#weight_${pointIndex}`, true)
			inputEditBan(point, `#volume_${pointIndex}`, true)
			inputEditBan(point, `#tnvd_${pointIndex}`, true)
			inputEditBan(point, '.country', true)
			inputEditBan(point, `#customsCountry_${pointIndex}`, true)
			inputEditBan(point, `#timeFrame_from_${pointIndex}`, true)
			inputEditBan(point, `#timeFrame_to_${pointIndex}`, true)
			inputEditBan(point, `#pointContact_${pointIndex}`, true)
			inputEditBan(point, `#customsAddress_${pointIndex}`, true)
		}

		if (way === 'РБ') {
			inputEditBan(point, '.country', true)
		}

		if (way === 'РБ' && !isInternalMovement) {
			inputEditBan(point, '.country', true)
			inputEditBan(point, `#pall_${pointIndex}`, true)
		}

		if (way === 'Импорт') {
			inputEditBan(point, `#pall_${pointIndex}`, true)
		}
	})
}