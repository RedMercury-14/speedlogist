import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { createNewSupplierUrl, postUserIsExistUrl } from "./globalConstants/urls.js"
import { snackbar } from "./snackbar/snackbar.js"


const loginRegex = /^[a-zA-Z0-9@#$%^&*()_+!~\-=\[\]{}|;:',.?\/]+$/

document.addEventListener('DOMContentLoaded', () => {
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
})


function newSupplierFormSubmitHandler(e) {
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