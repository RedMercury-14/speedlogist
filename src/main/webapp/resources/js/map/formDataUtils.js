// -------------------------------------------------------------------------------//
// -------------------- функции получения данных из форм -------------------------//
// -------------------------------------------------------------------------------//

// функция получения данных для POST-запроса построения маршрута
export function getPointsData(routeForm, routeParams = {}) {
	const pointsData = Object.fromEntries(new FormData(routeForm))

	const points = []
	let i = 1
	while (pointsData[`point${i}`]) {
		const shopNum = pointsData[`point${i}`]
		points.push(shopNum)
		i++
	}

	const [ startPoint, ...restPoints ] = points

	return {
		startPoint: startPoint,
		points: restPoints,
		parameters: routeParams
	}
}

// функция получения данных для POST-запроса построения маршрута ИЗ ОБЩЕГО ПОЛЯ НОМЕРОВ
export function getPointsDataFromTextarea(routeTextareaForm, routeParams = {}) {
	const points = getTextareaData(routeTextareaForm.routeTextarea)
	const [ startPoint, ...restPoints ] = points

	return {
		startPoint: startPoint,
		points: restPoints,
		parameters: routeParams
	}
}

// функция получения данных для POST-запроса тестовой формы оптимизатора
export function getOptimizeRouteFormData(optimizeRouteForm, params = {}) {
	const stock = optimizeRouteForm.stock.value || ''
	const iteration = optimizeRouteForm.iteration.value || ''
	const shops = getTextareaData(optimizeRouteForm.routeTextarea)
	const palls = getTextareaData(optimizeRouteForm.pallTextarea)
	const tonnage = getTextareaData(optimizeRouteForm.tonnageTextarea)
	const checkboxes = optimizeRouteForm.querySelectorAll('input[name="cleaning"]')
	const cleanings = getCheckboxesData(checkboxes)
	cleanings.length = shops.length
	const cars = getCarsData(optimizeRouteForm)

	const big = {
		count: optimizeRouteForm.carCount[0].value || '',
		tonnage: optimizeRouteForm.maxPall[0].value || '',
		maxMileage: optimizeRouteForm.maxTonnage[0].value || ''
	}
	const middle = {
		count: optimizeRouteForm.carCount[1].value || '',
		tonnage: optimizeRouteForm.maxPall[1].value || '',
		maxMileage: optimizeRouteForm.maxTonnage[1].value || ''
	}
	const little = {
		count: optimizeRouteForm.carCount[2].value || '',
		tonnage: optimizeRouteForm.maxPall[2].value || '',
		maxMileage: optimizeRouteForm.maxTonnage[2].value || ''
	}

	return {
		stock,
		iteration,
		shops,
		palls,
		tonnage,
		cleanings,
		cars,
		big,
		middle,
		little,
		params,
	}
}

// функция получения данных для POST-запроса тестовой формы загрузки магазинов
export function getShopLoadsFormData(shopLoadsForm) {
	const shops = getTextareaData(shopLoadsForm.shopTextarea)
	const palls = getTextareaData(shopLoadsForm.pallTextarea)
	const tonnage = getTextareaData(shopLoadsForm.tonnageTextarea)

	return {
		shops,
		palls,
		tonnage
	}
}

// функция получения данных из textarea
export function getTextareaData(textarea) {
	const values = textarea.value
	const isTabSeparator = values.includes('\t')
	const data = isTabSeparator
		? values.split('\t')
			.map(point =>  point.includes('\n') ? point.slice(0, -1) : point)
			.filter(point => point !== '')
		: values.split('\n').filter(point => point !== '')

	return data
}

// функция получения данных из инпутов
function getInputsData(inputs) {
	const data = []
	inputs.forEach(input => {
		const value = input.value
		value ? data.push(value) : data.push(null)
	})
	return data
}

// функция получения данных из чекбоксов
function getCheckboxesData(checkboxes) {
	const data = []
	checkboxes.forEach(checkbox => {
		const value = checkbox.checked
		value ? data.push(value) : data.push(false)
	})
	return data
}

function getCarsData(form) {
	const cars = []

	const carNameInputs = form.querySelectorAll('.carName')
	const secondRoundInputs = form.querySelectorAll('.secondRound')
	const carCountInputs = form.querySelectorAll('.carCount')
	const maxPallInputs = form.querySelectorAll('.maxPall')
	const maxTonnageInputs = form.querySelectorAll('.maxTonnage')

	carNameInputs.forEach((input, index) => {
		const carName = input.value
		if (!carName) return
		const secondRound = secondRoundInputs[index].checked
		const carCount = carCountInputs[index].value
		const maxPall = maxPallInputs[index].value
		const maxTonnage = maxTonnageInputs[index].value

		cars.push({
			carName,
			secondRound: secondRound,
			carCount,
			maxPall,
			maxTonnage
		})
	})

	return cars
}
