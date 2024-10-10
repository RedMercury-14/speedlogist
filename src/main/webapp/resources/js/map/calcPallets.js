// -------------------------------------------------------------------------------//
// ---------------- расчёт количества паллет и паллетовместимости ----------------//
// -------------------------------------------------------------------------------//

import { debounce } from "../utils.js"
import { getTextareaData } from "./formDataUtils.js"

export function calcPallets() {
	const optimizeRoutePallTextarea = document.querySelector("#optimizeRoutePall")
	const countInputs = document.querySelectorAll('#optimizeRouteForm .carCount')
	const tonnageInputs = document.querySelectorAll('#optimizeRouteForm .maxPall')
	const palletsNeededElem = document.querySelector('#palletsNeeded')
	const totalPalletsElem = document.querySelector('#totalPallets')
	
	// debounced-функции расчёта сумм паллет
	const debouncedCalcTotalPallets = debounce(calcTotalPallets, 500)
	const debouncedCalcPalletsNeeded = debounce(calcPalletsNeeded, 500)
	
	// добавление листнера для расчёта необходимости магазинов в паллетах
	optimizeRoutePallTextarea && optimizeRoutePallTextarea.addEventListener('input', debouncedCalcPalletsNeeded)
	
	// добавление листнеров для расчёта общей паллетовместимости указанных машин
	countInputs.forEach((input) => input.addEventListener('input', debouncedCalcTotalPallets))
	tonnageInputs.forEach((input) => input.addEventListener('input', debouncedCalcTotalPallets))
	
	// функция для расчёта и отображения общей паллетовместимости указанных машин в форме оптимизатора
	function calcTotalPallets() {
		const palletsNeeded = Number(palletsNeededElem.innerText)
		const totalPallets = getTotalPallets()
		totalPalletsElem.innerText = totalPallets
		updateTotalPalletsElemClassName(palletsNeeded, totalPallets)
	}
	
	function getTotalPallets() {
		let totalPallets = 0
	
		countInputs.forEach((input, i) => {
			const pallets = Number(input.value)
			const cars = Number(tonnageInputs[i].value)
	
			if(Number.isFinite(pallets) && Number.isFinite(cars)) {
				totalPallets += pallets * cars
			}
		})
	
		return totalPallets
	}
	
	// функция для расчёта и отображения необходимости магазинов в паллетах в форме оптимизатора
	function calcPalletsNeeded(e) {
		const pallInArray = getTextareaData(e.target)
		const palletsNeeded = pallInArray.reduce((sum, pall) => sum + Number(pall), 0)
		const totalPallets = Number(totalPalletsElem.innerText)
	
		if (!Number.isFinite(palletsNeeded) || !Number.isFinite(totalPallets)) return
		
		palletsNeededElem.innerText = palletsNeeded
		updateTotalPalletsElemClassName(palletsNeeded, totalPallets)
	}
	
	// обновление цвета текста элемента с общей паллетовместимостью
	function updateTotalPalletsElemClassName(palletsNeeded, totalPallets) {
		const className = palletsNeeded <= totalPallets ? 'text-success' : 'text-danger'
		totalPalletsElem.className = `font-weight-bold ${className}`
	}
}
