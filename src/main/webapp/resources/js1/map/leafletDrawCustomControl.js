// -------------------------------------------------------------------------------//
// ------ Кастомные элементы управления для библиотеки рисования leafletDraw -----//
// -------------------------------------------------------------------------------//

import { snackbar } from "../snackbar/snackbar.js"

export const customControl = L.Control.extend({
	options: {
		position: "topright",
	},

	onAdd: function () {
		const array = [
			{
				title: "вывести в консоль geojson",
				html: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-eye-fill" viewBox="0 0 16 16">
						<path d="M10.5 8a2.5 2.5 0 1 1-5 0 2.5 2.5 0 0 1 5 0z"/>
						<path d="M0 8s3-5.5 8-5.5S16 8 16 8s-3 5.5-8 5.5S0 8 0 8zm8 3.5a3.5 3.5 0 1 0 0-7 3.5 3.5 0 0 0 0 7z"/>
					</svg>`,
				className: "log link-button leaflet-bar",
			},
			{
				title: "Очистить карту от полигонов",
				html: `<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash-fill" viewBox="0 0 16 16">
						<path d="M2.5 1a1 1 0 0 0-1 1v1a1 1 0 0 0 1 1H3v9a2 2 0 0 0 2 2h6a2 2 0 0 0 2-2V4h.5a1 1 0 0 0 1-1V2a1 1 0 0 0-1-1H10a1 1 0 0 0-1-1H7a1 1 0 0 0-1 1H2.5zm3 4a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 .5-.5zM8 5a.5.5 0 0 1 .5.5v7a.5.5 0 0 1-1 0v-7A.5.5 0 0 1 8 5zm3 .5v7a.5.5 0 0 1-1 0v-7a.5.5 0 0 1 1 0z"/>
					</svg>`,
				className: "clear link-button leaflet-bar",
			},
		]

		const container = L.DomUtil.create("div", "leaflet-control leaflet-action-button")

		array.forEach((item) => {
			const button = L.DomUtil.create("a")
			button.href = "#"
			button.setAttribute("role", "button")

			button.title = item.title
			button.innerHTML = item.html
			button.className += item.className

			container.appendChild(button)
		})

		return container;
	},
})

// коллбэк для выведения в консоль объекта нарисованных элементов
export function logJSONonClickCallback(drawnItems) {
	const data = drawnItems.toGeoJSON()

	if (data.features.length === 0) {
		snackbar.show("Нет данных")
		return
	} else {
		snackbar.show("Данные в консоли")
	}

	console.log(data)
}

// коллбэк для очищения карты от нарисованных элементов
export function clearJSONOnClickCallback(drawnItems, map) {
	drawnItems.eachLayer((layer) => {
		map.removeLayer(layer)
	})
}