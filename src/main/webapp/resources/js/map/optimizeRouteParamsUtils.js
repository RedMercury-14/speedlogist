// --- РЕДАКТИРОВАТЬ ЗДЕСЬ --- параметры главного переключателя для настроек оптимизатора 
export const mainCheckboxParams = {
	// название переменной: подпись в интерфейсе
	optimizeRouteMainCheckbox: 'Главный Чекбокс',
}

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
}

// --- РЕДАКТИРОВАТЬ ЗДЕСЬ --- параметры полей ввода для настроек оптимизатора
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

// --- РЕДАКТИРОВАТЬ ЗДЕСЬ --- параметры выпадающего списка для настроек оптимизатора
export const selectParams = {
	// название переменной: подпись в интерфейсе
	algorithm: 'Алгоритм',
}

// --- РЕДАКТИРОВАТЬ ЗДЕСЬ --- параметры опций выпадающего списка для настроек оптимизатора
export const selectOptions = {
	// значение опции: текст
	// здесь же редактируется и количество опций
	algorithm1: 'Алгоритм 1',
	algorithm2: 'Алгоритм 2',
	algorithm3: 'Алгоритм 3',
}



export const mainCheckboxHTML = (id, label) => (`
	<div class="toggler-container border-0">
		<label>
			<input class="toggler" id="${id}" name="${id}" type="checkbox"/>
			<span class="text-danger font-weight-bold">${label}</span>
		</label>
	</div>
`)
export const checkboxHTML = (id, label) => (`
	<div class="toggler-container border-0">
		<label>
			<input class="toggler" id="${id}" name="${id}" type="checkbox"/>
			<span class="text-muted font-weight-bold">${label}</span>
		</label>
	</div>
`)
export const selectHTML = (id, label) => (`
	<div class="form-group row-container mb-1">
		<label class="font-weight-bold text-muted" for="${id}">${label}</label>
		<select id="stacking" name="stacking" class="form-control" title="Возможность размещения паллеты на паллету" required>
			<option value="" hidden disabled selected>Выберите один из пунктов</option>
			<option>Да</option>
			<option>Нет</option>
		</select>
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

export function createSelect(selectParams, selectOptions, container) {
	const selectId = Object.keys(selectParams)[0]
	const selectLabel = selectParams[selectId]

	const options = []
	const values = Object.keys(selectOptions)
	const textes = Object.values(selectOptions)
	values.forEach((value, index) => {
		options.push(`<option value="${value}">${textes[index]}</option>`)
	})

	const select = `
		<div class="form-group row-container mb-2">
			<label class="font-weight-bold text-muted" for="${selectId}">${selectLabel}</label>
			<select id="${selectId}" name="${selectId}" class="form-control px-2 py-1" title="${selectLabel}" required>
				${options.join('')}
			</select>
		</div>
	`

	container.innerHTML = select
}

// получение данных формы
export function getOptimizeRouteParamsFormData(form) {
	const result = {}
	const formData = new FormData(form)

	const mainChackboxKey = Object.keys(mainCheckboxParams)[0]
	result[mainChackboxKey] = formData.has(mainChackboxKey)

	const checkboxes = Object.keys(сheckboxParams)
	const checkboxValues = checkboxes.map(key => formData.has(key))
	checkboxes.forEach((key, index) => result[key] = checkboxValues[index])

	const selectKey = Object.keys(selectParams)[0]
	result[selectKey] = formData.get(selectKey)

	const inputs = Object.keys(inputParams)
	const inputValues = inputs.map(key => formData.get(key))
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