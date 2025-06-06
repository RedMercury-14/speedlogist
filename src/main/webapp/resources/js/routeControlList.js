window.onload = () => {
	const rows = document.querySelectorAll('.route')
	const allCheckboxes = document.querySelectorAll('.route input[type=checkbox]')
	const impCheckboxes = document.querySelectorAll('input[data-way=Импорт]')
	const withoutNDS = document.querySelector('.withoutNDS')
	const withNDS = document.querySelector('.withNDS')

	var num = 0

	for (let i = 0; i < rows.length; i++) {
		const row = rows[i]
		const checkbox = allCheckboxes[i]
		const isImportRoute = row.dataset.way === 'Импорт'

		// обработка маршрутов без авто и водителя
		const numTruck = row.querySelector('#numTruck').innerHTML
		if (numTruck.length == 3) {
			checkbox.disabled = true
			checkbox.title = "Нельзя создать акт, не прикрепив автомобиль."
		}

		// обработка ИМПОРТНЫХ маршрутов
		if (isImportRoute) {
			checkbox.addEventListener('click', (event) => {
				const targetRow = event.target.parentElement.parentElement
				if (event.target.checked) {
					// включаем поле установки даты
					targetRow.querySelector('input[type=date]').disabled = false
					// отключаем остальные чекбоксы
					disableCheckboxes(allCheckboxes)
					// отключаем чекбокс с НДС и устанавливаем без НДС
					withNDS.disabled = true
					withoutNDS.checked = true
				} else {
					// отключаем поле установки даты
					targetRow.querySelector('input[type=date]').disabled = true
					// включаем остальные чекбоксы
					enableCheckboxes(allCheckboxes)
					// включаем чекбокс с НДС 
					withNDS.disabled = false
					withoutNDS.checked = false
				}
			})

		// обработка всех маршрутов, кроме экспедиционных
		} else {
			checkbox.addEventListener('click', (event) => {
				const targetRow = event.target.parentElement.parentElement
				if (event.target.checked) {
					targetRow.querySelector('input[type=date]').disabled = false
					num++
				} else {
					targetRow.querySelector('input[type=date]').disabled = true
					num--
				}

				// можно выбрать не больше 10 маршрутов
				if (num >= 10) {
					disableCheckboxes(allCheckboxes)
				} else {
					enableCheckboxes(allCheckboxes)
				}

				// пока есть хотя бы 1 выбраный чекбокс, все ИМПОРТНЫЕ чекбоксы должны быть отключены
				if (isAnyCheckboxChecked(allCheckboxes)) {
					disableCheckboxes(impCheckboxes)
				}
			})
		}
	}


	changeFooterPosition()

	const addRegCertificateForm = document.querySelector('#addRegCertificateForm')
	addRegCertificateForm && addRegCertificateForm.addEventListener('submit', addRegCertificateFormSubmitHandler)

	// showRegCertificateModal()
}

function disableCheckboxes(checkboxes) {
	for (let j = 0; j < checkboxes.length; j++) {
		const checkbox = checkboxes[j];
		if (!checkbox.checked) checkbox.disabled = true;
	}
}

function enableCheckboxes(checkboxes) {
	for (let j = 0; j < checkboxes.length; j++) {
		const checkbox = checkboxes[j];
		const row = checkbox.parentElement.parentElement;
		if (row.querySelector('#numTruck').innerHTML.length == 3) {
			checkbox.disabled = true;
			checkbox.title = "Нельзя создать акт, не прикрепив автомобиль.";
		} else {
			checkbox.disabled = false;
		}
	}
}

function isAnyCheckboxChecked(сheckboxes) {
	let isAnyCheckboxChecked = false
	for (let j = 0; j < сheckboxes.length; j++) {
		const checkbox = сheckboxes[j]
		if (checkbox.checked) {
			isAnyCheckboxChecked = true
			break
		}
	}
	return isAnyCheckboxChecked
}

function changeFooterPosition() {
	const viewWidth = window.innerWidth
	const viewHeight = window.innerHeight
	const bodyHeight = document.body.offsetHeight
	
	if (viewWidth < 500 && (bodyHeight + 83) < viewHeight) {
		document.querySelector('footer').style.position = 'fixed'
	}
}

function showRegCertificateModal() {
	const propertySizeInput = document.querySelector('#propertySize')
	const registrationCertificateInput = document.querySelector('#registrationCertificate')

	if (!propertySizeInput || !registrationCertificateInput) return

	const propertySize = propertySizeInput && propertySizeInput.value
	const registrationCertificate = registrationCertificateInput && registrationCertificateInput.value

	if (propertySize === 'ИП' && registrationCertificate === '') {
		$('#regCertificateModal').modal('show')
	}
}

function addRegCertificateFormSubmitHandler(e) {
	e.preventDefault()

	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	console.log(data)

	$('#regCertificateModal').modal('hide')
}