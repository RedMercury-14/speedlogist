class MapStore {
	constructor() {
		this.markers = []
		this.polylines = []
		this.polygons = []
		this.shops = []
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
}

export const mapStore = new MapStore()

window.mapStore = mapStore