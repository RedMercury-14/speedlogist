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
		const { img, label } = layer.options
		const p = layer._point.round()
		const ctx = this._ctx

		// Рисуем иконку
		ctx.drawImage(
			img.el,
			p.x - img.size[0] / 2,
			p.y - img.size[1] / 2,
			img.size[0],
			img.size[1]
		)

		// Рисуем подпись снизу, если задана
		if (label) {
			const fontSize = 15
			const paddingX = 4
			const paddingY = 2
			const text = label.toString()

			ctx.font = `bold ${fontSize}px Arial`
			ctx.textAlign = 'center'
			ctx.textBaseline = 'top'

			// Измерим ширину текста
			const textWidth = ctx.measureText(text).width
			const textHeight = fontSize

			const bgWidth = textWidth + paddingX * 2
			const bgHeight = textHeight + paddingY * 2

			const bgX = p.x - bgWidth / 2
			const bgY = p.y + img.size[1] / 2 + 2

			// Подложка
			ctx.fillStyle = 'rgba(0, 0, 0, 0.6)'
			ctx.fillRect(bgX, bgY, bgWidth, bgHeight)

			// Текст
			ctx.fillStyle = 'white'
			ctx.fillText(text, p.x, bgY + paddingY)
		}
	},
})