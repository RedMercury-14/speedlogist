// -------------------------------------------------------------------------------//
// ---------------- расчёт количества паллет и паллетовместимости ----------------//
// -------------------------------------------------------------------------------//

import { debounce } from "../utils.js"
import { getTextareaData } from "./formDataUtils.js"

const optimizeRoutePallTextarea = document.querySelector("#optimizeRoutePall")
const palletsNeededElem = document.querySelector('#palletsNeeded')
const totalPalletsElem = document.querySelector('#totalPallets')

// debounced-функции расчёта сумм паллет
export const debouncedCalcTotalPallets = debounce(calcTotalPallets, 500)
export const debouncedCalcPalletsNeeded = debounce(calcPalletsNeeded, 500)

// функция расчета паллетт и паллетовместимости
export function calcPallets() {
	calcTotalPallets()
	optimizeRoutePallTextarea && calcPalletsNeeded({ target: optimizeRoutePallTextarea })
}

// добавление листнера для расчёта суммы паллет указанных в форме магазинов
export function addListnersToPallTextarea() {
	optimizeRoutePallTextarea && optimizeRoutePallTextarea.addEventListener('input', debouncedCalcPalletsNeeded)
}

// добавление листнеров для расчёта общей паллетовместимости указанных машин
export function addListnersToCarInputs(container) {
	const countInputs = container.querySelectorAll('#optimizeRouteForm .carCount')
	const maxPallInputs = container.querySelectorAll('#optimizeRouteForm .maxPall')
	countInputs.forEach((input) => input.addEventListener('input', debouncedCalcTotalPallets))
	maxPallInputs.forEach((input) => input.addEventListener('input', debouncedCalcTotalPallets))
}


// функция для расчёта и отображения общей паллетовместимости указанных машин в форме оптимизатора
function calcTotalPallets() {
	const palletsNeeded = Number(palletsNeededElem.innerText)
	const totalPallets = getTotalPallets()
	totalPalletsElem.innerText = totalPallets
	updateTotalPalletsElemClassName(palletsNeeded, totalPallets)
}

// функция для расчёта и отображения необходимости магазинов в паллетах в форме оптимизатора
function calcPalletsNeeded(e) {
	const pallInArray = getTextareaData(e.target)
	const palletsNeeded = pallInArray
		.reduce((sum, value) => {
			let pall = value
			if (value === '0.3' || value === '0,3') pall = 0.3333
			sum += Number(pall)
			return sum
		}, 0)
	const totalPallets = Number(totalPalletsElem.innerText)

	if (!Number.isFinite(palletsNeeded) || !Number.isFinite(totalPallets)) return
	
	palletsNeededElem.innerText = palletsNeeded.toFixed(2)
	updateTotalPalletsElemClassName(palletsNeeded, totalPallets)
}

function getTotalPallets() {
	const countInputs = document.querySelectorAll('#optimizeRouteForm .carCount')
	const maxPallInputs = document.querySelectorAll('#optimizeRouteForm .maxPall')

	let totalPallets = 0

	countInputs.forEach((input, i) => {
		const cars = Number(input.value)
		const pallets = Number(maxPallInputs[i].value)

		if(Number.isFinite(pallets) && Number.isFinite(cars)) {
			totalPallets += cars * pallets
		}
	})

	return totalPallets
}

// обновление цвета текста элемента с общей паллетовместимостью
function updateTotalPalletsElemClassName(palletsNeeded, totalPallets) {
	const className = palletsNeeded <= totalPallets ? 'text-success' : 'text-danger'
	totalPalletsElem.className = `font-weight-bold ${className}`
}
