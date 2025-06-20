import { dateHelper } from '../../utils.js'

const token = $("meta[name='_csrf']").attr("content")
const login = document.querySelector("#login").value
const role = document.querySelector("#role").value
const today = dateHelper.getDateForInput(new Date())

export const store = {
	_state: {
		token,
		login,
		role,
		currentDate: today, // текущая отображаемая дата
		trucks: null, // машины сгруппированы по датам
	},
	_callSubscriber(state) {
		console.log('subscriber is not defind')
	},
	subscribe(observer) {
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


	setTrucks(trucks) {
		this._state.trucks = trucks
	},
	getTrucks() {
		return this._state.trucks
	},

	getTrucksByCurrentDate() {
		const trucksByCurrentDate = this._state.trucks[this._state.currentDate]
		return trucksByCurrentDate ? trucksByCurrentDate : []
	},

	// методы для управления данными машин
	addTruck(truck) {
		const truckDate = truck.dateRequisition
		// создать новый массив по дате, если его нет в this._state.trucks
		if (!this._state.trucks[truckDate]) this._state.trucks[truckDate] = []
		this._state.trucks[truckDate].push(truck)
		this._callSubscriber(this._state)
	},

	addTrucks(trucks) {
		trucks.forEach(truck => {
			const truckDate = truck.dateRequisition
			// создать новый массив по дате, если его нет в this._state.trucks
			if (!this._state.trucks[truckDate]) this._state.trucks[truckDate] = []
			this._state.trucks[truckDate].push(truck)
		})
		this._callSubscriber(this._state)
	},

	removeTruck(truck) {
		const truckDate = truck.dateRequisition
		const trucks = this._state.trucks[truckDate]
		const index = trucks.findIndex(t => t.idTGTruck === truck.idTGTruck)
		trucks.splice(index, 1)
		this._callSubscriber(this._state)
	},
}

window.truckStore = store
