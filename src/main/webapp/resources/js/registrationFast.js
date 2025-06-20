import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { carrierFastRegistrationUrl, postUserIsExistUrl } from "./globalConstants/urls.js"
import { snackbar } from "./snackbar/snackbar.js"
import { dateHelper } from "./utils.js"


const loginRegex = /^[a-zA-Z0-9@#$%^&*()_+!~\-=\[\]{}|;:',.?\/]+$/

document.addEventListener('DOMContentLoaded', () => {
	const messageLoginElem = newCarrierForm.querySelector('#messageLogin')

	newCarrierForm.addEventListener('submit', newCarrierFormSubmitHandler)
	newCarrierForm.login.addEventListener('change', (e) => isExistLogin(e.target.value, messageLoginElem))
	newCarrierForm.login.addEventListener('input', (e) => checkLoginOnInput(e))
})


function newCarrierFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const isValid = loginRegex.test(data.login)
	if (!isValid) {
		snackbar.show('Некорректный логин! Используйте только разрешённые символы')
		return
	}

	const payload = {
		...data,
		numcontract: `${data.numcontract_num} от ${dateHelper.changeFormatToView(data.numcontract_date)}`
	}

	console.log(payload)

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: carrierFastRegistrationUrl,
		data: payload,
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				alert('Новый пользователь успешно создан! Письмо с логином и паролем отправлено на указанный при регистрации электронный адрес')

				const messageLoginElem = newCarrierForm.querySelector('#messageLogin')
				newCarrierForm.reset()
				messageLoginElem.innerText = ''
				messageLoginElem.className = ''
				newCarrierForm.login.classList.remove('is-invalid')
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