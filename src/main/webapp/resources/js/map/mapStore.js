import { dateHelper } from "../utils.js"

class MapStore {
	constructor() {
		this.markers = []
		this.polylines = []
		this.polygons = []
		this.shops = []
		this.groupedTrucks = null
		this.currentDate = dateHelper.getDateForInput(new Date()) // текущая отображаемая дата
		this.currentNameList = null // текущий список машин
		this.trucks = null // машины сгруппированы по датам
		this.lists = [] // Array<{ nameList: string, date: string }>
	}

	getMarkers() {
		return this.markers
	}

	addMarker(marker) {
		this.markers.push(marker)
	}

	removeMarker(id) {
		this.markers = this.markers.filter((marker) => marker.options.id !== id)
	}

	getPolylines() {
		return this.polylines
	}

	addPolyline(polyline) {
		this.polylines.push(polyline)
	}

	removePolyline(id) {
		this.polylines = this.polylines.filter((polyline) => polyline.options.id !== id)
	}

	clearMarkers() {
		this.markers = []
	}

	clearPolylines() {
		this.polylines = []
	}

	setShops(shops) {
		this.shops = shops
	}
	getShops() {
		return this.shops
	}

	setPolygons(polygons) {
		this.polygons = polygons
	}
	getPolygons() {
		return this.polygons
	}
	addPolygon(polygon) {
		this.polygons.push(polygon)
	}
	removePolygon(encodedName) {
		this.polygons = this.polygons.filter((polygon) => polygon.properties.name !== encodedName)
	}

	/**
	 * @param {string} date
	 */
	setCurrentDate(date) {
		this.currentDate = date
	}

	/**
	 * @returns {string}
	 */
	getCurrentDate() {
		return this.currentDate
	}

	getMaxTrucksDate() {
		const dates = Object.keys(this.trucks)
		const maxDate = Math.max(...dates.map(date => new Date(date)))
		return dateHelper.getDateForInput(maxDate)
	}


	/**
	 * @param {string} nameList
	 */
	setCurrentNameList(nameList) {
		this.currentNameList = nameList
	}

	/**
	 * @returns {string}
	 */
	getCurrentNameList() {
		return this.currentNameList
	}

	/**
	 * @param {Array<{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: number,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>} trucks
	 */
	setTrucks(trucks) {
		this.trucks = trucks
	}
	/**
	 * @returns {Array<{
	* 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	* 	typeTrailer: string, dateRequisition: string, cargoCapacity: number,
	* 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	* 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	* }>}
	 */
	getTrucks() {
		return this.trucks
	}

	/**
	 * @returns {Array<{
	 * 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: number,
	 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	 * }>}
	 */
	getFreeTrucksByCurrentDate() {
		const trucksByDate = this.trucks[this.currentDate]
		if (!trucksByDate) return []
		return trucksByDate.filter(truck => !truck.nameList)
	}

	/**
	 * @param {string} nameList
	 * @returns {Array<{
	 * 	idTGTruck: number, numTruck: string, modelTruck: string, pall: number,
	 * 	typeTrailer: string, dateRequisition: string, cargoCapacity: number,
	 * 	chatIdUserTruck: number, nameList: string, idList: number, status: number,
	 * 	companyName: string, truckForBot: string, dateRequisitionLocalDate: Array<number>,
	 * }>}
	 */
	getTrucksByNameList(nameList) {
		const trucksByNameList = this.trucks[this.currentDate]
		if (!trucksByNameList) return []
		return trucksByNameList.filter(truck => truck.nameList === nameList)
	}

	/**
	 * @param {Array<{ nameList: string, date: string }>} lists
	 */
	setLists(lists) {
		this.lists = lists
	}

	/**
	 * @returns {Array<{ nameList: string, date: string }>}
	 */
	getLists() {
		return this.lists
	}

	/**
	 * @returns {Array<{ nameList: string, date: string }>}
	 */
	getListsByCurrentDate() {
		return this.lists.filter(list => list.date === this.currentDate)
	}

	/**
	 * @param {string} nameList
	 * @returns {{ nameList: string, date: string }}
	 */
	getList(nameList) {
		return this.lists.find(list => list.nameList === nameList)
	}

	/**
	 * @param {string} nameList
	 * @param {string} date
	 * @returns {{ nameList: string, date: string }}
	 */
	getListByNameAndDate(nameList, date) {
		return this.lists.find(list => list.nameList === nameList && list.date === date)
	}
}

export const mapStore = new MapStore()

window.mapStore = mapStore