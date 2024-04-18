var rows = document.querySelectorAll('tr');
var num = 0;
for (let i = 1; i < rows.length; i++) {
	var row = rows[i];
	if (row.querySelector('#numTruck').innerHTML.length == 3) {
		row.querySelector('input[type=checkbox]').disabled = true;
		row.querySelector('input[type=checkbox]').title = "Нельзя создать акт, не прикрепив автомобиль.";
	}
	row.querySelector('input[type=checkbox]').addEventListener('click', (event) => {
		var targetRow = event.target.parentElement.parentElement;
		if (targetRow.querySelector('input[type=checkbox]').checked) {
			targetRow.querySelector('input[type=date]').disabled = false;
			num++
		} else {
			targetRow.querySelector('input[type=date]').disabled = true;
			num--
		}
		var checkboxes = document.querySelectorAll('input[type=checkbox]');
		//тут задаётся колличесвто акто в одном листе
		if (num >= 10) {
			for (let j = 0; j < checkboxes.length; j++) {
				var checkbox = checkboxes[j];
				if (!checkbox.checked) {
					checkbox.disabled = true;
				}
			}
		} else {
			for (let j = 0; j < checkboxes.length; j++) {
				var checkbox = checkboxes[j];
				var chechRow = checkbox.parentElement.parentElement;
				if (chechRow.querySelector('#numTruck').innerHTML.length == 3) {
					chechRow.querySelector('input[type=checkbox]').disabled = true;
					chechRow.querySelector('input[type=checkbox]').title = "Нельзя создать акт, не прикрепив автомобиль.";
				}else{
					checkbox.disabled = false;
				}				
			}
		}
	})
}
window.onload = () => {
	changeFooterPosition()

	const addRegCertificateForm = document.querySelector('#addRegCertificateForm')
	addRegCertificateForm && addRegCertificateForm.addEventListener('submit', addRegCertificateFormSubmitHandler)

	// showRegCertificateModal()
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