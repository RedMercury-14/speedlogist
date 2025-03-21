import { dateHelper } from '../../utils.js'

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector("#login").value
const role = document.querySelector("#role").value
const today = dateHelper.getDateForInput(new Date())

export const store = {
	_state: {
		lastClickedBtn: null,
		token,
		login,
		role,
		currentDate: today, // текущая отображаемая дата
		trucks: null, // машины сгруппированы по датам
	},
	_callSubscriber(state) {
		console.log('subscriber is not defind')
	},
	subscribe (observer) {
		this._callSubscriber = observer
	},

	getState() {
		return this._state
	},


	/**
	 * @returns {string}
	 */
	getToken() {
		return this._state.token
	},

	/**
	 * @returns {string}
	 */
	getLogin() {
		return this._state.login
	},

	/**
	 * @returns {string}
	 */
	getRole() {
		return this._state.role
	},

	getLastClickedBtn() {
		return this._state.lastClickedBtn
	},

	setLastClickedBtn(btn) {
		this._state.lastClickedBtn = btn
	},


	/**
	 * @param {string} date
	 */
	setCurrentDate(date) {
		this._state.currentDate = date
	},

	/**
	 * Возвращает текущую дату в формате YYYY-MM-DD
	 * @returns {string}
	 */
	getCurrentDate() {
		return this._state.currentDate
	},


	/**
	 * @param {Array<{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>} trucks
	 */
	setTrucks(trucks) {
		this._state.trucks = trucks
	},
	getTrucks() {
		return this._state.trucks
	},

	/**
	 * Возвращает список машин по текущей дате
	 * @returns {Array<{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>}
	 */
	getTrucksByCurrentDate() {
		const trucksByCurrentDate = this._state.trucks[this._state.currentDate]
		return trucksByCurrentDate ? trucksByCurrentDate : []
	},

	// методы для управления данными машин
	/**
	 * @param {{ idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>, }} truck
	 */
	addTruck(truck) {
		const truckDate = truck.dateRequisition
		// создать новый массив по дате, если его нет в this._state.trucks
		if (!this._state.trucks[truckDate]) this._state.trucks[truckDate] = []
		this._state.trucks[truckDate].push(truck)
		this._callSubscriber(this._state)
	},

	/**
	 * @param {{ idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>, }} truck
	 */
	updateTruck(truck) {
		const truckDate = truck.dateRequisition
		const trucks = this._state.trucks[truckDate]
		const index = trucks.findIndex(t => t.idTGTruck === truck.idTGTruck)
		if (index !== -1) trucks[index] = { ...truck }
		this._callSubscriber(this._state)
	},

	/**
	 * @param {Array<{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>} trucks
	 */
	updateTrucks(trucks) {
		const date = trucks[0].dateRequisition
		const trucksByDate = this._state.trucks[date]
		trucks.forEach(truck => {
			const index = trucksByDate.findIndex(t => t.idTGTruck === truck.idTGTruck)
			if (index !== -1) trucksByDate[index] = { ...truck }
		})
		this._callSubscriber(this._state)
	},

	/**
	 * @param {{ idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>, }} truck
	 */
	removeTruck(truck) {
		const truckDate = truck.dateRequisition
		const trucks = this._state.trucks[truckDate]
		const index = trucks.findIndex(t => t.idTGTruck === truck.idTGTruck)
		trucks.splice(index, 1)
		this._callSubscriber(this._state)
	},
}

window.truckStore = store
