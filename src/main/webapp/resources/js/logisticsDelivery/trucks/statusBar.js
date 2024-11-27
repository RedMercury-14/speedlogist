// компонент отображения общего тоннажа
export class CargoCapacitySumStatusBarComponent {
	init(params) {
		this.params = params
		this.appStore = params.appStore

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value"

		const label = document.createElement("span")
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
		this.updateStatusBar()
	}

	onModelUpdated() {
		this.updateStatusBar()
	}

	updateStatusBar() {
		this.eCount.innerText = `${this.getCargoCapacitySum()} (${this.getTotalCargoCapacitySum()})`
	}

	getCargoCapacitySum() {
		const rowNodes = []
		this.params.api.forEachNodeAfterFilter(node => rowNodes.push(node))
		return rowNodes
			.reduce((sum, rowNode) => sum + Number(rowNode.data.cargoCapacity), 0)
			.toFixed(1)
	}

	getTotalCargoCapacitySum() {
		return this.appStore.getTrucksByCurrentDate()
			.reduce((sum, truck) => sum + Number(truck.cargoCapacity), 0)
			.toFixed(0)
	}
}

// компонент отображения суммы паллет
export class PallSumStatusBarComponent {
	init(params) {
		this.params = params
		this.appStore = params.appStore

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value"

		const label = document.createElement("span")
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
		this.updateStatusBar()
	}

	onModelUpdated() {
		this.updateStatusBar()
	}

	updateStatusBar() {
		this.eCount.innerText = this.getPallSum() + " " + `(${this.getTotalPallSum()})`
	}

	getPallSum() {
		const rowNodes = []
		this.params.api.forEachNodeAfterFilter(node => rowNodes.push(node))
		return rowNodes
			.reduce((sum, rowNode) => sum + rowNode.data.pall, 0)
			.toFixed(0)
	}

	getTotalPallSum() {
		return this.appStore.getTrucksByCurrentDate()
			.reduce((sum, truck) => sum + truck.pall, 0)
			.toFixed(0)
	}
}

// компонент отображения количества строк
export class CountStatusBarComponent {
	init(params) {
		this.params = params
		this.appStore = params.appStore

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value"

		const label = document.createElement("span")
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
		this.updateStatusBar()
	}

	onModelUpdated() {
		this.updateStatusBar()
	}

	updateStatusBar() {
		this.eCount.innerText = `${this.getRowCount()} (${this.getTotalTrucks()})`
	}

	getRowCount() {
		return this.params.api.getModel().getRowCount()
	}

	getTotalTrucks() {
		return this.appStore.getTrucksByCurrentDate().length
	}
}

// компонент отображения легенды
export class RowLegengStatusBarComponent {
	init(params) {
		this.params = params

		this.eGui = document.createElement("div")
		this.eGui.className = "ag-status-name-value d-flex"

		const label = document.createElement("span")
		label.className = 'font-weight-bold'
		label.innerText = "Машины на 2 рейса -"
		this.eGui.appendChild(label)
		
		this.coloredBlock = document.createElement("span")
		this.coloredBlock.className = "light-orange-row"
		this.coloredBlock.style.display = "inline-block"
		this.coloredBlock.style.height = "20px"
		this.coloredBlock.style.width = "40px"
		this.coloredBlock.style.marginLeft = "5px"

		this.eGui.appendChild(this.coloredBlock)
	}

	getGui() {
		return this.eGui
	}

	destroy() {}
}
