import { ajaxUtils } from "./ajaxUtils.js"
import { hideLoadingSpinner, showLoadingSpinner } from "./utils.js"

const excelUrl = ``
const token = $("meta[name='_csrf']").attr("content")

window.addEventListener('load', () => {
	const reportForm = document.querySelector('#reportForm')
	reportForm.addEventListener('submit', reportFormSubmitHandler)
})

function reportFormSubmitHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	if (!submitButton) return
	const submitButtonText = submitButton.innerText
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: excelUrl,
		token: token,
		data: file,
		successCallback: (res) => {
			if (res[200]) {
				document.querySelector('#stackTrace').value = res[200]
			}
			hideLoadingSpinner(submitButton, submitButtonText)
		},
		errorCallback: () => hideLoadingSpinner(submitButton, submitButtonText)
	})
}