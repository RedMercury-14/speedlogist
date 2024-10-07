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
		currentNameList: null, // текущий список машин
		trucks: null, // машины сгруппированы по датам
		lists: [], // Array<{ nameList: string, date: string }>
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
	 * @returns {string}
	 */
	getCurrentDate() {
		return this._state.currentDate
	},


	/**
	 * @param {string} nameList
	 */
	setCurrentNameList(nameList) {
		this._state.currentNameList = nameList
	},

	/**
	 * @returns {string}
	 */
	getCurrentNameList() {
		return this._state.currentNameList
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
	 * @returns {Array<{
	 * 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	 * }>}
	 */
	getFreeTrucksByCurrentDate() {
		const trucksByDate = this._state.trucks[this._state.currentDate]
		if (!trucksByDate) return []
		return trucksByDate.filter(truck => !truck.nameList)
	},

	/**
	 * @param {number} idTGTruck
	 * @returns {{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }|null}
	 */
	getOldTruckByCurrentDate(idTGTruck) {
		const trucksByDate = this._state.trucks[this._state.currentDate]
		if (!trucksByDate) return null
		const truck = trucksByDate.find(truck => truck.idTGTruck === idTGTruck)
		return truck ? { ...truck } : null
	},

	/**
	 * @param {string} nameList
	 * @returns {Array<{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>}
	 */
	getTrucksByNameList(nameList) {
		const trucksByNameList = this._state.trucks[this._state.currentDate]
		if (!trucksByNameList) return []
		return trucksByNameList.filter(truck => truck.nameList === nameList)
	},

	// обновление названия списка у машин
	/**
	 * @param {string} nameList
	 * @param {Array<{
	 * 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: string,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>} trucks
	 */
	updateNameListOfTrucks(nameList, trucks) {
		const trucksByDate = this._state.trucks[this._state.currentDate]
		const newTruckIds = new Set(trucks.map(truck => truck.idTGTruck))

		trucksByDate.forEach(truck => {
			if (newTruckIds.has(truck.idTGTruck)) {
				truck.nameList = nameList
			} else if (truck.nameList === nameList) {
				truck.nameList = null
			}
		})

		this._callSubscriber(this._state)
	},


	// установка и получение списка списков машин
	/**
	 * @param {Array<{ nameList: string, date: string }>} lists
	 */
	setLists(lists) {
		this._state.lists = lists
	},

	/**
	 * @returns {Array<{ nameList: string, date: string }>}
	 */
	getLists() {
		return this._state.lists
	},

	/**
	 * @returns {Array<{ nameList: string, date: string }>}
	 */
	getListsByCurrentDate() {
		return this._state.lists.filter(list => list.date === this._state.currentDate)
	},

	/**
	 * @param {string} nameList
	 * @returns {{ nameList: string, date: string }}
	 */
	getList(nameList) {
		return this._state.lists.find(list => list.nameList === nameList)
	},

	/**
	 * @param {string} nameList
	 * @param {string} date
	 * @returns {{ nameList: string, date: string }}
	 */
	getListByNameAndDate(nameList, date) {
		return this._state.lists.find(list => list.nameList === nameList && list.date === date)
	},

	/**
	 * @param {string} nameList
	 */
	addList(nameList) {
		this._state.lists.push({ nameList, date: this._state.currentDate })
	},

	addListByDate(nameList, date) {
		this._state.lists.push({ nameList, date })
	},

	/**
	 * @param {string} nameList
	 */
	removeList(nameList) {
		const index = this._state.lists.findIndex(list => list.nameList === nameList)
		this._state.lists.splice(index, 1)
	},

	/**
	 * @param {string} nameList
	 */
	updateNameList(nameList) {
		const list = this._state.lists.find(list => list.nameList === nameList)
		list.nameList = nameList
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
