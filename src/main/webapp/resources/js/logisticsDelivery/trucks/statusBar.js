// компонент отображения общего тоннажа
export class CargoCapacitySumStatusBarComponent {
	init(params) {
		this.params = params

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value"

		var label = document.createElement("span")
		label.innerText = "Тоннаж: "
		this.eGui.appendChild(label)

		this.eCount = document.createElement("span")
		this.eCount.className = "ag-status-name-value-value"

		this.eGui.appendChild(this.eCount)

		params.api.addEventListener("gridReady", this.onGridReady.bind(this))
		params.api.addEventListener("modelUpdated", this.onModelUpdated.bind(this))
	}

	getGui() {
		return this.eGui
	}

	destroy() {
		this.params.api.removeEventListener("gridReady", this.onGridReady)
		this.params.api.removeEventListener("modelUpdated", this.onModelUpdated)
	}

	onGridReady() {
		this.eCount.innerText = this.getCargoCapacitySum() + ""
	}

	onModelUpdated() {
		this.eCount.innerText = this.getCargoCapacitySum() + ""
	}

	getCargoCapacitySum() {
		const rowNodes = []
		this.params.api.forEachNode(node => rowNodes.push(node))
		return rowNodes
			.reduce((sum, rowNode) => sum + Number(rowNode.data.cargoCapacity), 0)
			.toFixed(1)
	}
}

// компонент отображения суммы паллет
export class PallSumStatusBarComponent {
	init(params) {
		this.params = params

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value"

		var label = document.createElement("span")
		label.innerText = "Паллет: "
		this.eGui.appendChild(label)

		this.eCount = document.createElement("span")
		this.eCount.className = "ag-status-name-value-value"

		this.eGui.appendChild(this.eCount)

		params.api.addEventListener("gridReady", this.onGridReady.bind(this))
		params.api.addEventListener("modelUpdated", this.onModelUpdated.bind(this))
	}

	getGui() {
		return this.eGui
	}

	destroy() {
		this.params.api.removeEventListener("gridReady", this.onGridReady)
		this.params.api.removeEventListener("modelUpdated", this.onModelUpdated)
	}

	onGridReady() {
		this.eCount.innerText = this.getPallSum() + ""
	}

	onModelUpdated() {
		this.eCount.innerText = this.getPallSum() + ""
	}

	getPallSum() {
		const rowNodes = []
		this.params.api.forEachNode(node => rowNodes.push(node))
		return rowNodes
			.reduce((sum, rowNode) => sum + rowNode.data.pall, 0)
			.toFixed(0)
	}
}

// компонент отображения количества строк
export class CountStatusBarComponent {
	init(params) {
		this.params = params

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value"

		var label = document.createElement("span")
		label.innerText = "Машин: "
		this.eGui.appendChild(label)

		this.eCount = document.createElement("span")
		this.eCount.className = "ag-status-name-value-value"

		this.eGui.appendChild(this.eCount)

		params.api.addEventListener("gridReady", this.onGridReady.bind(this))
		params.api.addEventListener("modelUpdated", this.onModelUpdated.bind(this))
	}

	getGui() {
		return this.eGui
	}

	destroy() {
		this.params.api.removeEventListener("gridReady", this.onGridReady)
		this.params.api.removeEventListener("modelUpdated", this.onModelUpdated)
	}

	onGridReady() {
		this.eCount.innerText = this.params.api.getModel().getRowCount() + ""
	}

	onModelUpdated() {
		this.eCount.innerText = this.params.api.getModel().getRowCount() + ""
	}
}
