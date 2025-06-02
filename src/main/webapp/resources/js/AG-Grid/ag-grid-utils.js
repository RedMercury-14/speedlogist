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

export class MultiColumnFilterToolPanel {
	init(params) {
		this.params = params;
		this.eGui = document.createElement("div");
		this.eGui.style.width = "100%"
		this.eGui.style.padding = "10px";
		this.eGui.style.display = "flex";
		this.eGui.style.flexDirection = "column";
		this.eGui.style.gap = "10px";

		this.eGui.append(this.getElements());
	}

	getGui() {
		return this.eGui;
	}

	refresh() {}

	getElements() {
		const container = document.createElement("div")
		container.className = "d-flex flex-column"
		container.style.gap = "10px"

		const title = document.createElement("h4")
		title.textContent = "Мультифильтр"
		title.className = "text-center mb-2"

		const columnLabel = document.createElement("label")
		columnLabel.textContent = "Выберите колонку:"
		columnLabel.className = "font-weight-bold"

		const columnSelect = document.createElement("select")
		columnSelect.className = "form-control p-1"

		const columns = this.params.columnDefs.filter(col => col.field)
		columns.forEach(col => {
			const option = document.createElement("option")
			option.value = col.field
			option.textContent = col.headerName || col.field
			columnSelect.appendChild(option)
		})

		const valueLabel = document.createElement("label")
		valueLabel.textContent = "Введите значения через запятую или с новой строки:"
		valueLabel.className = "font-weight-bold"

		const valueTextarea = document.createElement("textarea")
		valueTextarea.className = "form-control"
		valueTextarea.rows = 8
		valueTextarea.placeholder = "например:\nЯблоко, Банан, Апельсин\nили\nЯблоко,Банан,Апельсин\nили\nЯблоко\nБанан\nАпельсин"

		const buttonsContainer = document.createElement("div")
		buttonsContainer.className = "d-flex justify-content-center"
		buttonsContainer.style.gap = "5px"

		const applyBtn = document.createElement("button")
		applyBtn.className = "btn btn-info"
		applyBtn.textContent = "Применить"

		const clearBtn = document.createElement("button")
		clearBtn.className = "btn btn-outline-secondary"
		clearBtn.textContent = "Сбросить"

		const applyHint = document.createElement("small")
		applyHint.textContent = "Применить - Применяет фильтр к ВЫБРАННОЙ колонке"
		applyHint.style.color = "#666"
		applyHint.style.fontSize = "12px"

		const clearHint = document.createElement("small")
		clearHint.textContent = "Сбросить - Очищает фильтр ВЫБРАННОЙ колонки и сбрасывает поле ввода"
		clearHint.style.color = "#666"
		clearHint.style.fontSize = "12px"

		applyBtn.addEventListener("click", () => {
			const colId = columnSelect.value
			const raw = valueTextarea.value
			const values = raw.split(/[\n,]/).map(v => v.trim()).filter(Boolean)

			const filterInstance = this.params.api.getFilterInstance(colId)
			if (filterInstance && filterInstance.setModel) {
				filterInstance.setModel({ values })
				this.params.api.onFilterChanged()
			}
		})

		clearBtn.addEventListener("click", () => {
			const colId = columnSelect.value
			const filterInstance = this.params.api.getFilterInstance(colId)
			if (filterInstance && filterInstance.setModel) {
				filterInstance.setModel(null)
				this.params.api.onFilterChanged()
			}
			valueTextarea.value = ''
		})

		buttonsContainer.append(applyBtn, clearBtn)

		container.append(
			title,
			columnLabel,
			columnSelect,
			valueLabel,
			valueTextarea,
			buttonsContainer,
			applyHint,
			clearHint
		)

		return container
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

export function dateValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDate(date)
}
export function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
	
}
export function dateComparator(date1, date2) {
	if (!date1) return -1
	if (!date2) return 1
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
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

export class BtnsCellRenderer {
	init(params) {
		this.params = params
		this.buttons = params.buttonList

		this.emptyElem = document.createElement('span')
		this.eGui = document.createElement("div")
		this.eGui.style = "display: flex; gap: 5px; justify-content: center; align-items: center;"

		this.buttons.forEach((button) => {
			const btn = document.createElement("button")
			btn.className = button.className || ''
			btn.id = button.id || ''
			btn.title = button.title || ''
			btn.innerHTML = button.icon || button.label || button.dynamicLabel(this.params) || ''
			this.eGui.appendChild(btn)
		})

		this.btnsClickedHandler = this.btnsClickedHandler.bind(this)
		this.eGui.addEventListener("click", this.btnsClickedHandler)
	}

	getGui() {
		return this.params.value ? this.eGui : this.emptyElem
	}

	btnsClickedHandler(e) {
		const button = e.target.closest('button')
		if (!button) return

		this.params.onClick({
			...e,
			buttonId: button.id
		}, this.params)
	}

	destroy() {
		this.eGui.removeEventListener("click", this.btnsClickedHandler)
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

export class SubmitButtonTextEditor {
	init(params) {
		this.params = params
		this.originalValue = params.value
		this.currentValue = params.value

		this.valueConfirmed = false

		this.container = document.createElement('div')
		this.container.style.display = 'flex'
		this.container.style.flexDirection = 'column'
		this.container.style.gap = '10px'
		this.container.style.padding = '10px'
		this.container.style.width = '500px'
		this.container.style.height = '300px'
		this.container.style.backgroundColor = 'white'

		this.textarea = document.createElement('textarea')
		this.textarea.value = this.currentValue
		this.textarea.style.width = '100%'
		this.textarea.style.height = '100%'
		this.textarea.style.resize = 'none'

		if (params.maxLength) {
			this.textarea.maxLength = params.maxLength
		}

		this.textarea.addEventListener('keydown', this.keyDownListner.bind(this))

		this.saveButton = document.createElement('button')
		this.saveButton.textContent = 'Сохранить'
		this.saveButton.style.alignSelf = 'flex-end'
		this.saveButton.style.padding = '5px 15px'

		this.saveButton.addEventListener('click', this.saveBtnClickListner.bind(this))

		this.container.appendChild(this.textarea)
		this.container.appendChild(this.saveButton)

		setTimeout(() => {
			this.textarea.focus()
		}, 0)
	}

	getValue() {
		return this.valueConfirmed ? this.currentValue : this.originalValue
	}

	isCancelAfterEnd() {
		return !this.valueConfirmed
	}

	getGui() {
		return this.container
	}
	
	afterGuiAttached() {
		this.textarea.focus()
	}
	
	destroy() {
		this.saveButton.removeEventListener('click', this.saveBtnClickListner)
		this.textarea.removeEventListener('keydown', this.keyDownListner)
	}

	keyDownListner(event) {
		if (event.key === 'Enter') {
			if (event.shiftKey) {
				event.preventDefault()
				this.confirmAndClose()
			} else {
				event.stopPropagation()
			}
		}
	}

	saveBtnClickListner(event) {
		this.currentValue = this.textarea.value
		this.valueConfirmed = true
		this.params.api.stopEditing()
	}

	confirmAndClose() {
		this.currentValue = this.textarea.value
		this.valueConfirmed = true
		this.params.api.stopEditing()
	}
}
