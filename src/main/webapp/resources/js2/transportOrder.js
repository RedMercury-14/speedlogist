import { ajaxUtils } from "./ajaxUtils.js"
import { hideLoadingSpinner, showLoadingSpinner } from "./utils.js"

const token = $("meta[name='_csrf']").attr("content")

window.addEventListener('load', () => {
	const printBtn = document.querySelector('#printBtn')
	printBtn.addEventListener('click', printPage)
})

function printPage(e) {
	e.target.blur()
	window.print()
}
