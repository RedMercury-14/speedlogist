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
		currentRouteIdLeft: '',
		currentRouteIdRight: '',
		rightTableVisible: false,
		routeList: [],
		routes: [],
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
	 * @param { Array<{ id: Number, name: String }> } routeList
	 */
	setRouteList(routeList) {
		this._state.routeList = routeList
	},
	/**
	 * @returns { Array<{ id: Number, name: String }> }
	 */
	getRouteList() {
		return this._state.routeList
	},


	getRightTableVisible() {
		return this._state.rightTableVisible
	},
	setRightTableVisible(visible) {
		this._state.rightTableVisible = visible
	},


	getCurrentRouteIdLeft() {
		return this._state.currentRouteIdLeft
	},
	setCurrentRouteIdLeft(id) {
		this._state.currentRouteIdLeft = id
	},
	getCurrentRouteIdRight() {
		return this._state.currentRouteIdRight
	},
	setCurrentRouteIdRight(id) {
		this._state.currentRouteIdRight = id
	},


	setRoutes(routes) {
		this._state.routes = routes
	},
	getRoutes() {
		return this._state.routes
	},

	getRouteById(id) {
		return this._state.routes.find(route => route.id === id)
	},

	updateRouteShops(routeId, shops) {
		const route = this.getRouteById(routeId)
		route.shops = shops
		// this._callSubscriber(this._state)
	},

	
}


window.routeStore = store
