import { ajaxUtils } from "./ajaxUtils.js"
import { bootstrap5overlay } from "./bootstrap5overlay/bootstrap5overlay.js"
import { createCarrierApplicationUrl } from "./globalConstants/urls.js"
import { snackbar } from "./snackbar/snackbar.js"
import { disableButton, enableButton } from "./utils.js"

const token = $("meta[name='_csrf']").attr("content")

const capacityValues = [ 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 20, 24 ]
const palletsValues = [ 4, 6, 8, 10, 12, 14, 16, 18, 20, 22, 24, 32, 36 ]


document.addEventListener('DOMContentLoaded', () => {
	const capCheckboxContainer = document.getElementById('capCheckboxes')
	const pallCheckboxContainer = document.getElementById('pallCheckboxes')

	capacityValues.forEach((num) => {
		capCheckboxContainer.insertAdjacentHTML('beforeend', getNumCheckboxHtml('capacity', num))
	})
	palletsValues.forEach((num) => {
		pallCheckboxContainer.insertAdjacentHTML('beforeend', getNumCheckboxHtml('pallets', num))
	})

	carrierDataForm.addEventListener('submit', carrierDataFormSubmitHandler)

	const requiredCheckboxes = document.querySelectorAll(".required-group input[type='checkbox']")
	requiredCheckboxes.forEach(cb => cb.addEventListener("change", toggleInvalidMessage))
})

function toggleInvalidMessage(e) {
	const group = e.target.closest(".required-group")
	const feedback = group.querySelector(".invalid-feedback")
	const checkboxes = group.querySelectorAll("input[type='checkbox']")
	const isAnyChecked = Array.from(checkboxes).some(cb => cb.checked)

	if (isAnyChecked) feedback.classList.remove("d-block")
}

function carrierDataFormSubmitHandler(e) {
	e.preventDefault()

	if (!isValidCheckboxes()) {
		snackbar.show('Форма не заполнена!')
		return
	}

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)
	const groupedData = groupFieldsByPrefix(data)
	console.log("🚀 ~ carrierDataFormSubmitHandler ~ groupedData:", groupedData)

	disableButton(e.submitter)
	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: createCarrierApplicationUrl,
		token: token,
		data: groupedData,
		successCallback: async (res) => {
			enableButton(e.submitter)
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
	
			if (res.status === '200') {
				e.target.reset()
				showSuccessMessage()
				setTimeout(() => {
					window.location.href = '/speedlogist/main'
				}, 3000);
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

function showSuccessMessage() {
	document.getElementById('form-container').classList.add('d-none')
	document.getElementById('success-message-container').classList.remove('d-none')
	document.querySelector('footer.to-bottom').classList.add('position-absolute')
}

function isValidCheckboxes() {
	let isFormValid = true

	const groups = document.querySelectorAll(".required-group")

	groups.forEach(group => {
		const checkboxes = group.querySelectorAll("input[type='checkbox']")
		const feedback = group.querySelector(".invalid-feedback")

		const isAnyChecked = Array.from(checkboxes).some(cb => cb.checked)

		if (!isAnyChecked) {
			feedback.classList.add("d-block")
			isFormValid = false
		} else {
			feedback.classList.remove("d-block")
		}
	})

	return isFormValid
}

function groupFieldsByPrefix(obj) {
	const result = {}
	
	Object.entries(obj).forEach(([key, value]) => {
		const match = key.match(/^([a-zA-Z]+)_(\d+)$/)
		
		if (match) {
			const [_, prefix, index] = match;
			(result[prefix] = result[prefix] || [])[(+index) - 1] = value
		} else {
			result[key] = value
		}
	})

	for (const key in result) {
		if (Array.isArray(result[key])) {
			result[key] = result[key].filter(item => item !== undefined)
		}
	}

	return result
}

function getNumCheckboxHtml(name, num) {
	return `<div class="form-check form-check-inline">
				<input class="form-check-input" type="checkbox" id="${name}_${num}" name="${name}_${num}" value="${num}">
				<label class="form-check-label" for="${name}_${num}">${num}</label>
			</div>`
}
