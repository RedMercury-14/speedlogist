import { dateHelper } from "../utils.js";

// утилиты для преобразования данных
export function extractKeys(mappings) {
	return Object.keys(mappings);
}
export function lookupValue(mappings, key) {
	return mappings[key];
}
export function lookupKey(mappings, name) {
	const keys = Object.keys(mappings);

	for (let i = 0; i < keys.length; i++) {
		const key = keys[i];

		if (mappings[key] === name) {
		return key;
		}
	}
}

// функции созранения, загрузки и сброса настроек колонок таблицы
export const gridColumnLocalState = {
	saveState(gridOptions, key) {
		localStorage.setItem(
			key + '_columnState',
			JSON.stringify(gridOptions.columnApi.getColumnState())
		)
	},
	restoreState(gridOptions, key) {
		const settings = localStorage.getItem(key + '_columnState')
		settings &&
		gridOptions.columnApi.applyColumnState({
			state: JSON.parse(settings),
			applyOrder: true,
		})
	},
	resetState(gridOptions, key) {
		gridOptions.columnApi.resetColumnState()
		localStorage.removeItem(key + '_columnState')
	}
}

export const gridFilterLocalState = {
	saveState(gridOptions, key) {
		localStorage.setItem(
			key + '_filterState',
			JSON.stringify(gridOptions.api.getFilterModel())
		)
	},
	restoreState(gridOptions, key) {
		const model = localStorage.getItem(key + '_filterState')
		model &&
		gridOptions.api.setFilterModel(JSON.parse(model))
	},
	resetState(gridOptions, key) {
		gridOptions.api.setFilterModel(null)
		localStorage.removeItem(key + '_filterState')
	}
}

export const gridFilterSessionState = {
	saveState(gridOptions, key) {
		sessionStorage.setItem(
			key + '_filterState',
			JSON.stringify(gridOptions.api.getFilterModel())
		)
	},
	restoreState(gridOptions, key) {
		const model = sessionStorage.getItem(key + '_filterState')
		model &&
		gridOptions.api.setFilterModel(JSON.parse(model))
	},
	resetState(gridOptions, key) {
		gridOptions.api.setFilterModel(null)
		sessionStorage.removeItem(key + '_filterState')
	}
}

export function autoSizeAll(gridOptions, skipHeader) {
	const allColumnIds = []
	gridOptions.columnApi.getColumns().forEach((column) => {
		allColumnIds.push(column.getId())
	})

	gridOptions.columnApi.autoSizeColumns(allColumnIds, skipHeader)
}

export class DateTimeEditor {
	eInput
	cancelBeforeStart

	init(params) {
		this.eInput = document.createElement("input")
		this.eInput.type = "datetime-local"
		this.eInput.classList.add("form-control")
		this.eInput.classList.add("datetime-input")

		if (params.value) {
			const [ date, time ] = params.value.split(' ')
			this.eInput.value = 
				`${dateHelper.changeFormatToInput(date)}T${time.slice(0, 5)}`
		}
	}


	getGui() {
		return this.eInput
	}

	afterGuiAttached() {
		this.eInput.focus()
	}

	isCancelAfterEnd() {}

	getValue() {
		const value = this.eInput.value
		if (value) {
			const [ date, time ] = this.eInput.value.split('T')
			return `${dateHelper.changeFormatToView(date)} ${time}`
		} else {
			return ''
		}
	}

	destroy() {}

	isPopup() {
		return false
	}
}

export class DateEditor {
	eInput
	cancelBeforeStart

	init(params) {
		this.eInput = document.createElement("input")
		this.eInput.type = "date"
		this.eInput.classList.add("form-control")
		this.eInput.classList.add("date-input")

		if (params.value) {
			this.eInput.value = dateHelper.changeFormatToInput(params.value)
		}
	}

	getGui() {
		return this.eInput
	}

	afterGuiAttached() {
		this.eInput.focus()
	}

	isCancelAfterEnd() {}

	getValue() {
		const value = this.eInput.value
		return value ? dateHelper.changeFormatToView(value) : ''
	}

	destroy() {}

	isPopup() {
		return false
	}
}

export class TimeEditor {
	eInput
	cancelBeforeStart

	init(params) {
		this.eInput = document.createElement("input")
		this.eInput.type = "time"
		this.eInput.classList.add("form-control")
		this.eInput.classList.add("time-input")

		if (params.value) {
			this.eInput.value = params.value
		}
	}


	getGui() {
		return this.eInput
	}

	afterGuiAttached() {
		this.eInput.focus()
	}

	isCancelAfterEnd() {}

	getValue() {
		const value = this.eInput.value
		return value ? value : ''
	}

	destroy() {}

	isPopup() {
		return false
	}
}

export class ResetStateToolPanel {
	init(params) {
		this.eGui = document.createElement("div")
		this.eGui.style.textAlign = "center"
		this.eGui.style.width = "100%"
		this.eGui.style.marginTop = "10px"

		this.eGui.append(this.getElements(params))
	}

	getGui() {
		return this.eGui
	}

	refresh() {}

	getElements(params) {
		const container = document.createElement("div")
		const resetColumnStateBtn = document.createElement("button")
		const resetFilterStateBtn = document.createElement("button")

		container.style = "display: flex; flex-direction: column; gap: 5px; padding: 0 5px;"
		resetColumnStateBtn.className = "btn btn-outline-secondary font-weight-bold"
		resetFilterStateBtn.className = "btn btn-outline-secondary font-weight-bold"

		resetColumnStateBtn.innerText = "Сбросить настройки колонок"
		resetFilterStateBtn.innerText = "Сбросить настройки фильтров"

		resetColumnStateBtn.addEventListener('click', (e) => this.resetColumnState(e, params))
		resetFilterStateBtn.addEventListener('click', (e) => this.resetFilterState(e, params))

		container.append(resetColumnStateBtn, resetFilterStateBtn)

		return container
	}

	resetColumnState(e, params) {
		gridColumnLocalState.resetState(params, params.localStorageKey)
	}

	resetFilterState(e, params) {
		gridFilterLocalState.resetState(params, params.localStorageKey)
	}
}

export function cell(text, styleId) {
	return {
		styleId: styleId,
		data: {
			type: /^\d+$/.test(text) ? 'Number' : 'String',
			value: String(text),
		}
	}
}

export function dateComparator(date1, date2) {
	const date1Number = dateToNum(date1)
	const date2Number = dateToNum(date2)

	if (date1Number === null && date2Number === null) return 0
	if (date1Number === null) return -1
	if (date2Number === null) return 1
	return date1Number - date2Number
}

function dateToNum(date) {
	if (date === undefined || date === null || date.length !== 10) {
		return null
	}

	// форматируем даты, которые начинаются с года
	const arr = date.split('-')
	if (arr[0].length === 4) {
		arr.reverse()
	}
	date = arr.join('-')

	const yearNumber = date.substring(6, 10)
	const monthNumber = date.substring(3, 5)
	const dayNumber = date.substring(0, 2)

	return yearNumber * 10000 + monthNumber * 100 + dayNumber
}

// выделение ("мигание") строки с изменениями
export function highlightRow(gridOptions, rowNode) {
	gridOptions.api.flashCells({ rowNodes: [rowNode] })
}

// отмена выделения строк через чекбоксы
export function deselectAllCheckboxes(gridOptions) {
	gridOptions.api.deselectAll('checkboxSelected')
}

export class BtnCellRenderer {
	init(params) {
		this.params = params

		this.emptyElem = document.createElement('span')
		this.eGui = document.createElement("button")
		this.eGui.className = this.params.className || ''
		this.eGui.id = this.params.id || ''
		this.eGui.innerText = this.params.label || this.params.dynamicLabel(this.params) || ''

		this.btnClickedHandler = this.btnClickedHandler.bind(this)
		this.eGui.addEventListener("click", this.btnClickedHandler)
	}

	getGui() {
		return this.params.value ? this.eGui : this.emptyElem
	}

	btnClickedHandler(event) {
		this.params.onClick(this.params)
	}

	destroy() {
		this.eGui.removeEventListener("click", this.btnClickedHandler)
	}
}

// автоматическое выделение значения в поте фильтрации по колонке (коллбек на открытие фильтра)
export function autoSelectFilerValue(params) {
	setTimeout(() => {
		const input = params.api.getFilterInstance(params.column.getColId())?.getGui()?.querySelector('input');
		if (input) {
			input.focus();
			requestAnimationFrame(() => input.select());
		}
	}, 0);
}
