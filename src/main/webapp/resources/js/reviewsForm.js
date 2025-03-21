import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { createUserReviewUrl } from "./globalConstants/urls.js"
import { snackbar } from "./snackbar/snackbar.js"
import { disableButton, enableButton } from "./utils.js"

let stockParam = null

const token = $("meta[name='_csrf']").attr("content")

const SELECT_DEPART_VALUE = 'Выбрать отдел'

document.addEventListener('DOMContentLoaded', () => {
	const queryParams = getQueryParams()
	stockParam = queryParams.stock

	const needReplyCheckbox = document.getElementById('needReply')
	const topicSelect = document.getElementById('topic')
	const emailContainer = document.getElementById('email-group')
	const departmentContainer = document.getElementById('department-group')

	feedbackForm.addEventListener('submit', feedbackFormSubmitHandler)
	needReplyCheckbox.addEventListener('change', (e) => needReplyCheckboxChangeHandler(e, emailContainer))
	topicSelect.addEventListener('change', (e) => topicChangeHandler(e, departmentContainer))
})

function feedbackFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	data.needReply = !!data.needReply

	if (data.topic === SELECT_DEPART_VALUE) {
		data.topic = data.department
	}

	if (stockParam) {
		data.stock = stockParam
	}

	disableButton(e.submitter)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: createUserReviewUrl,
		token: token,
		data: data,
		successCallback: async (res) => {
			enableButton(e.submitter)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
	
			if (res.status === '200') {
				e.target.reset()
				showSuccessMessage()
				setTimeout(() => {
					window.location.href = '/speedlogist/main'
				}, 2000);
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			enableButton(e.submitter)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

function needReplyCheckboxChangeHandler(e, emailContainer) {
	const needReply = e.target.checked
	needReply
		? emailContainer.classList.remove('d-none')
		: emailContainer.classList.add('d-none')
	feedbackForm.email.required = needReply
}

function topicChangeHandler(e, departmentContainer) {
	const value = e.target.value

	if (value === SELECT_DEPART_VALUE) {
		departmentContainer.classList.remove('d-none')
		feedbackForm.department.required = true
	} else {
		departmentContainer.classList.add('d-none')
		feedbackForm.department.required = false
	}
}

function showSuccessMessage() {
	document.getElementById('form-container').classList.add('d-none')
	document.getElementById('success-message-container').classList.remove('d-none')
}

function getQueryParams() {
	return new Proxy(
		new URLSearchParams(window.location.search),
		{ get: (searchParams, prop) => searchParams.get(prop), }
	)
}