// -------------------------------------------------------------------------------//
// -------------------- функции создания HTML-элементов --------------------------//
// -------------------------------------------------------------------------------//

import { calcPallets } from "./calcPallets.js"

// функция создания таблицы с полями для номеров магазинов
export function createRouteInputsTable(count, routeInputsContainer) {
	const header = `
		<span class="text-muted font-weight-bold">№</span>
		<span class="text-muted font-weight-bold">Номер магазина</span>
		<span class="text-muted font-weight-bold">Адрес</span>
		<span class="text-muted font-weight-bold">Расст-е</span>
	`

	const row = (index) => {
		const isRequired = index < 3 ? 'required' : ''
		return `
			<label class="text-muted font-weight-bold">${index}</label>
			<input class="form-control" type="number" name="point${index}" id="point${index}" ${isRequired}>
			<span id="addressInfo${index}" class="addressInfo"></span>
			<span id="pointInfo${index}" class="pointInfo"></span>
		`
	}

	const rows = getRows(header, row, count)
	routeInputsContainer.innerHTML = rows.join('')
}

// функция создания таблицы общим полем для номеров магазинов
export function createRouteTextareaTable(count, routeAreaContainer) {
	const header = `
		<span class="text-muted font-weight-bold">Адрес</span>
		<span class="text-muted font-weight-bold">Расст-е</span>
	`

	const row = (index) => {
		return `
			<span id="addressInfo${index}" class="addressInfo"></span>
			<span id="pointInfo${index}" class="pointInfo"></span>
		`
	}

	const rows = getRows(header, row, count)
	routeAreaContainer.innerHTML = rows.join('')
}

// функция создания колонки с нумерацией
export function createNumbersColumn(count, container) {
	const header = `<span class="text-muted font-weight-bold">№</span>`
	const row = (index) => `<span class="text-muted font-weight-bold">${index}</span>`
	const rows = getRows(header, row, count)
	container.innerHTML = rows.join('')
}

// функция создания колонки инпутов очистки
export function createCleaningInputsColumn(count, container) {
	const header = `<span class="text-muted font-weight-bold">Чистки</span>`
	const row = (index) => `<input class="form-control" type="checkbox" name="cleaning" id="cleaning_${index}">`
	const rows = getRows(header, row, count)
	container.innerHTML = rows.join('')
}

// функция получения массива строк для создания колонки либо таблицы
function getRows(header, row, rowCount) {
	const rows = []

	for (let i = 1; i <= rowCount; i++) {
		if (i === 1) {
			rows.push(header)
		}
		rows.push(row(i))
	}
	return rows
}

// функция создания строки инпутов машины
export function createCarInputs(count, container) {
	container.innerHTML = ''

	for (let i = 0; i < count; i++) {
		const nameInput = document.createElement('input')
		nameInput.type = 'text'
		nameInput.name = `carName`
		nameInput.className = 'form-control form-control-sm carName'

		const countInput = document.createElement('input')
		countInput.type = 'number'
		countInput.name = `carCount`
		countInput.className = 'form-control form-control-sm carCount'
		countInput.min = 0

		const pallInput = document.createElement('input')
		pallInput.type = 'number'
		pallInput.name = `maxPall`
		pallInput.className = 'form-control form-control-sm maxPall'
		pallInput.min = 0

		const tonnageInput = document.createElement('input')
		tonnageInput.type = 'number'
		tonnageInput.name = `maxTonnage`
		tonnageInput.className = 'form-control form-control-sm maxTonnage'
		tonnageInput.min = 0

		container.append(nameInput, countInput, pallInput, tonnageInput)
	}

	// расчёт количества паллет и паллетовместимости для формы оптимизатора
	calcPallets()
}

// переключатель Показать все точки
export const AllShopsToggler = L.Control.extend({
	position: 'topright',
	onAdd: function(map) {
		const togglerContainer = L.DomUtil.create("div")
		togglerContainer.classList.add("leaflet-control", "leaflet-bar", "toggler-container", "bg-white")
		togglerContainer.innerHTML = `
			<label>
				<span class=" font-weight-bold">Показать все точки</span>
				<input class="toggler" id="allShopsToggler" type="checkbox"/>
			</label>
		`
		return togglerContainer
	},
	onRemove: function(map) {}
})
