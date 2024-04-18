class MapStore {
	constructor() {
		this.markers = []
		this.polylines = []
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
}

export const mapStore = new MapStore()