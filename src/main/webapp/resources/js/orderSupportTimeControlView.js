import { ajaxUtils } from "./ajaxUtils.js"
import { hideLoadingSpinner, showLoadingSpinner } from "./utils.js"

const send487ReportUrl = `../../api/order-support/control/487`
const send490ReportUrl = `../../api/order-support/control/490`
const sendPromotionsReportUrl = `../../api/order-support/control/promotions`
const token = $("meta[name='_csrf']").attr("content")

window.addEventListener('load', () => {
	const reportForm = document.querySelector('#reportForm')
	reportForm.addEventListener('submit', reportFormSubmitHandler)
})

function reportFormSubmitHandler(e) {
	e.preventDefault()

	const submitButton = e.submitter
	if (!submitButton) return

	let url
	if (submitButton.dataset.type === '487') url = send487ReportUrl
	// if (submitButton.dataset.type === 'need') url = send490ReportUrl
	if (submitButton.dataset.type === 'promotions') url = sendPromotionsReportUrl

	const submitButtonText = submitButton.innerText
	const file = new FormData(e.target)

	showLoadingSpinner(submitButton)

	ajaxUtils.postMultipartFformData({
		url: url,
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