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
			ctx.font = '12px Arial'
			ctx.fillStyle = 'white'
			ctx.textAlign = 'center'
			ctx.textBaseline = 'top'
			ctx.lineWidth = 2

			// Смещение текста вниз от нижнего края иконки
			const textY = p.y + img.size[1] / 2 + 2

			// Обводка (для читаемости)
			ctx.strokeStyle = 'black'
			ctx.strokeText(label.toString(), p.x, textY)

			// Основной текст
			ctx.fillText(label.toString(), p.x, textY)
		}
	},
})