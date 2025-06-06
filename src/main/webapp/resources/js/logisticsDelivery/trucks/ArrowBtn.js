import { uiIcons } from '../../uiIcons.js'

export class BtnCellRenderer {
	init(params) {
		this.params = params

		this.eGui = document.createElement("button")
		this.eGui.className = 'btn btn-sm btn-block'
		this.eGui.innerHTML = params.type === 'toSelectedList' ? uiIcons.toLeftArrow : uiIcons.toRightArrow

		this.btnClickedHandler = this.btnClickedHandler.bind(this)
		this.eGui.addEventListener("click", this.btnClickedHandler)
	}

	getGui() {
		return this.eGui
	}

	btnClickedHandler(event) {
		this.params.clicked(event, this.eGui, this.params)
	}

	refresh(params) {
		return false
	}

	destroy() {
		this.eGui.removeEventListener("click", this.btnClickedHandler)
	}
}