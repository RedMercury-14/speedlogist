import { snackbar } from "./snackbar/snackbar.js"

document.addEventListener('DOMContentLoaded', () => {
	const pbLoginSpan = document.querySelector('#pbLogin')
	const pbPassSpan = document.querySelector('#pbPass')
	const copyLoginBtn = document.querySelector('#copyPBLogin')
	const copyPassBtn = document.querySelector('#copyPBPass')

	copyLoginBtn.onclick = () => copyTextToClipboard(pbLoginSpan.innerText)
	copyPassBtn.onclick = () => copyTextToClipboard(pbPassSpan.innerText)
})

function copyTextToClipboard(text) {
	navigator.clipboard.writeText(text)
		.then(() => {
			snackbar.show('Скопировано')
		})
		.catch(err => {
			snackbar.show('Ошибка копирования')
			console.error('Ошибка копирования: ', err)
		})
}