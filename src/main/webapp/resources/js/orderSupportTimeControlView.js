import { ajaxUtils } from "./ajaxUtils.js"
import { hideLoadingSpinner, showLoadingSpinner } from "./utils.js"

const send487ReportUrl = `../../api/order-support/control/487`
const token = $("meta[name='_csrf']").attr("content")

window.addEventListener('load', () => {
	const reportForm = document.querySelector('#reportForm')
	reportForm.addEventListener('submit', reportFormSubmitHandler)
})

function reportFormSubmitHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: send487ReportUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			if (res[200]) {
				document.querySelector('#stackTrace').value = res[200]
			}
			hideLoadingSpinner(submitButton, 'Загрузить отчет')
		},
		errorCallback: () => hideLoadingSpinner(submitButton, 'Загрузить отчет')
	})
}