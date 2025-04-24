import { ajaxUtils } from './ajaxUtils.js';
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'

// названия полей маршрутов из таблицы
const routeFieldNames = [
	"idRoute",
	"isNDS",
	"dateUnload",
	"numTruckAndTrailer",
	"numWayList",
	"cmr",
	"сargoWeight",
	"costWay",
]


document.addEventListener("DOMContentLoaded", () => {
	const tableScrollElem = document.querySelector('.table-scroll')
	const table = document.getElementById('table')
	const windowHeight = window.innerHeight
	if (table.offsetHeight > windowHeight / 2) {
		tableScrollElem.classList.add('table-scroll-y')
	}

	const documentTypeSelect = document.getElementById("documentType");
	const hiddenBlock1 = document.getElementById("hiddenBlock1");
	const hiddenBlock2 = document.getElementById("hiddenBlock2");
	const hiddenBlock1Doc = document.getElementById("hiddenBlock1-doc");
	const hiddenBlock2Doc = document.getElementById("hiddenBlock2-doc");

	documentTypeSelect.addEventListener("change", () => {
		const selectedValue = documentTypeSelect.value;

		if (selectedValue === "устава") {
			// Показываем блоки для "Юр. лицо"
			hiddenBlock1.style.display = "inline";
			hiddenBlock1Doc.style.display = "inline";

			// Скрываем блоки для "ИП"
			hiddenBlock2.style.display = "none";
			hiddenBlock2Doc.style.display = "none";
		} else if (selectedValue === "свидетельства") {
			// Показываем блоки для "ИП"
			hiddenBlock2.style.display = "inline";
			hiddenBlock2Doc.style.display = "inline";

			// Скрываем блоки для "Юр. лицо"
			hiddenBlock1.style.display = "none";
			hiddenBlock1Doc.style.display = "none";
		}
	});


	const cmrInputs = document.querySelectorAll('.cmr');
	cmrInputs.forEach(cmrInput => {
		cmrInput.addEventListener('input', (e) => {
			if (e.data === ', ' || e.target.value.includes(', ')) {
				e.target.value = e.target.value.replace(', ', ';')
			}
		})
	})


	var inputs = document.querySelectorAll('input[type=text]');
	var textareas = document.querySelectorAll('textarea');
	var target = true;
	document.querySelector('#get').addEventListener('mousedown', () => {
		for (let i = 0; i < inputs.length; i++) {
			var input = inputs[i];
			if(input.value == ''){
				target = false;
				break;
			}
		}
		for (let i = 0; i < textareas.length; i++) {
			var textarea = textareas[i];
			if(textarea.value == ''){
				target = false;
				break;
			}
		}
		target = true;
	})


	var numContract = document.querySelector('#numContractFromServer').value;
	document.querySelector('input[name=numContract]').value = numContract.split(' от ')[0];
	document.querySelector('input[name=dateContract]').value = numContract.split(' от ')[1];

	$('[data-toggle="tooltip"]').tooltip()
});


// отправка формы акта
document.addEventListener('submit', (e) => {

	if (!(confirm(
		'После формирования и отправки акта, маршрут будет считаться завершенным. Он пропадёт из списка перевозок. Вы уверены что хотите завершить маршрут?'
	))) return false

	// setTimeout(() => {
	// 	// редирект на таблицу маршрутов
	// 	window.location.href = '../routecontrole'
	// }, 1000)

	// e.preventDefault()
	// bootstrap5overlay.showOverlay()

	// const formData = new FormData(e.target)
	// const data = Object.fromEntries(formData)
	// data.routes = getRoutesData(e.target, routeFieldNames)
	// const cleanedData = getCleanedData(data, routeFieldNames)

	// console.log("🚀 ~ document.addEventListener ~ data:", cleanedData)

	// $.ajax({
	// 	url: window.location.href,
	// 	method: "POST",
	// 	xhrFields: { responseType: "blob" },
	// 	data: data,
	// 	success: function (data, status, xhr) {
	// 		const blob = new Blob([data], { type: "application/pdf" })
	// 		const contentDisposition = xhr.getResponseHeader("Content-Disposition")
	// 		const filename = contentDisposition && contentDisposition.match(/filename="?([^"]+)"?/i)?.[1]
	// 		const link = document.createElement("a")

	// 		link.href = window.URL.createObjectURL(blob)
	// 		link.download = filename || `Act_N${data.idRoute}.pdf`
	// 		document.body.appendChild(link)
	// 		link.click()
	// 		document.body.removeChild(link)
	// 		bootstrap5overlay.hideOverlay()
	// 		// редирект на таблицу маршрутов
	// 		// window.location.href = '../routecontrole'
	// 	},
	// 	error: function (xhr, status, error) {
	// 		console.error("Ошибка:", status, error)
	// 		bootstrap5overlay.hideOverlay()
	// 	},
	// })
})


// получение данных о маршрутах
function getRoutesData(form, fieldNames) {
	const routes = []
	const routeRows = form.querySelectorAll('.routeRow')

	routeRows.forEach((row) => {
		const data = {}
		fieldNames.forEach((fieldName) => {
			const field = row.querySelector(`*[name="${fieldName}"]`)
			if (field) data[fieldName] = field.value
		})
		return routes.push(data)
	})

	return routes
}

// удаление лишних полей из данных формы
function getCleanedData(data, fieldNames) {
	const cleanedData = { ...data }
	fieldNames.forEach((fieldName) => {
		delete cleanedData[fieldName]
	})
	return cleanedData
}

