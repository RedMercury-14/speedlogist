// --- РЕДАКТИРОВАТЬ ЗДЕСЬ --- параметры переключателей для настроек оптимизатора 
export const сheckboxParams = {
	// название переменной: подпись в интерфейсе
	// здесь же редактируется и количество переключателей
	optimizeRouteCheckbox1: 'Чекбокс 1',
	optimizeRouteCheckbox2: 'Чекбокс 2',
	optimizeRouteCheckbox3: 'Чекбокс 3',
	optimizeRouteCheckbox4: 'Чекбокс 4',
	optimizeRouteCheckbox5: 'Чекбокс 5',
	optimizeRouteCheckbox6: 'Чекбокс 6',
	optimizeRouteCheckbox7: 'Чекбокс 7',
	optimizeRouteCheckbox8: 'Чекбокс 8',
	optimizeRouteCheckbox9: 'Чекбокс 9',
	optimizeRouteCheckbox10: 'Чекбокс 10',
}

// --- РЕДАКТИРОВАТЬ ЗДЕСЬ --- параметры полей для настроек оптимизатора
export const inputParams = {
	// название переменной: подпись в интерфейсе
	// здесь же редактируется и количество полей
	optimizeRouteInput1: 'Поле 1',
	optimizeRouteInput2: 'Поле 2',
	optimizeRouteInput3: 'Поле 3',
	optimizeRouteInput4: 'Поле 4',
	optimizeRouteInput5: 'Поле 5',
	optimizeRouteInput6: 'Поле 6',
	optimizeRouteInput7: 'Поле 7',
	optimizeRouteInput8: 'Поле 8',
	optimizeRouteInput9: 'Поле 9',
	optimizeRouteInput10: 'Поле 10',
}


export const checkboxHTML = (id, label) => (`
	<div class="toggler-container border-0">
		<label>
			<input class="toggler" id="${id}" name="${id}" type="checkbox"/>
			<span class="text-muted font-weight-bold">${label}</span>
		</label>
	</div>
`)
export const numericInputHTML = (id, label) => (`
	<div class="form-group row-container mb-1">
		<label class="font-weight-bold text-muted" for="${id}">${label}</label>
		<input class="form-control" id="${id}" name="${id}" type="number" min="0" step="0.001"/>
	</div>
`)

// создание элементов формы
export function createFormInputs(params, inputHTML, container) {
	const checkboxes = []
	const ids = Object.keys(params)
	const labels = Object.values(params)

	for (let i = 0; i < ids.length; i++) {
		checkboxes.push(inputHTML(ids[i], labels[i]))
	}

	container.innerHTML = checkboxes.join('')
}

// получение данных формы
export function getOptimizeRouteParamsFormData(form) {
	const result = {}
	const formData = new FormData(form)
	const checkboxes = Object.keys(сheckboxParams)
	const inputs = Object.keys(inputParams)
	const checkboxValues = checkboxes.map(key => formData.has(key))
	const inputValues = inputs.map(key => formData.get(key))
	checkboxes.forEach((key, index) => result[key] = checkboxValues[index])
	inputs.forEach((key, index) => result[key] = inputValues[index])
	return result
}

// автозаполнение формы из локального хранилища
export function setOptimizeRouteParamsFormData(form, storageKey) {
	const optimizeRouteItem = localStorage.getItem(storageKey)
	if (!optimizeRouteItem) return
	const data = JSON.parse(optimizeRouteItem)
	const inputs = Object.keys(data)
	const values = Object.values(data)

	inputs.forEach((key, index) => {
		const input = form[key]
		const value = values[index]
		if (!input) return
		typeof value === 'boolean'
			? input.checked = value
			: input.value = value
	})
}