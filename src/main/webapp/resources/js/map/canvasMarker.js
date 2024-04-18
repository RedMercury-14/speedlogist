// -------------------------------------------------------------------------------//
// --------------- Маркер для встраивания в канвас для leaflet -------------------//
// -------------------------------------------------------------------------------//

export const CanvasMarker = L.CircleMarker.extend({
	_updatePath() {
		if (!this.options.img.el) {
			const img = document.createElement('img')
			img.src = this.options.img.url
			this.options.img.el = img
			img.onload = () => {
				this.redraw()
			}
		} else {
			this._renderer._updateImg(this)
		}
	},
})

L.Canvas.include({
	_updateImg(layer) {
		const { img } = layer.options
		const p = layer._point.round()
		this._ctx.drawImage(img.el, p.x - img.size[0] / 2, p.y - img.size[1] / 2, img.size[0], img.size[1])
	},
})