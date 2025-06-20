import { ajaxUtils } from "./ajaxUtils.js"
import { dateHelper } from "./utils.js"

document.addEventListener('DOMContentLoaded', () => {
	const numContract = document.querySelector('#numContractFromServer').value
	const [ num, date ] = numContract.split(' от ')

	editUserForm.numContract.value = num
	editUserForm.numContract_date.value = dateHelper.changeFormatToInput(date)
	editUserForm.dateContract.value = date

	editUserForm.numContract_date.addEventListener('change', (e) => {
		editUserForm.dateContract.value = dateHelper.changeFormatToView(e.target.value)
	})
})
