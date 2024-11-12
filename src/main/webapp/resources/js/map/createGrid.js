let gridRectangles = [] // Массив для хранения прямоугольников сетки
const drawnRectangles = new Set() // Хранит уникальные координаты юго-западного угла

// Функция для создания сетки
export function createGrid(props, rectangleClickHandler) {
	const { map, centerCoord, gridSizeKm } = props

	// Удаляем старую сетку, если она существует
	gridRectangles.forEach(rect => map.removeLayer(rect))
	gridRectangles = []
	drawnRectangles.clear() // Очищаем набор координат

	const center = L.latLng(centerCoord)
	const bounds = map.getBounds()
	const southWest = bounds.getSouthWest()
	const northEast = bounds.getNorthEast()

	// Функция для расчета смещения на `gridSizeKm` км в нужном направлении
	function getOffsetLatLng(latlng, distanceKm, angle) {
		return L.GeometryUtil.destination(latlng, angle, distanceKm * 1000)
	}

	// Начинаем построение сетки от центра
	for (let latShift = 0; true; latShift++) {
		// Вычисляем начальные позиции строк на севере и на юге
		const northRowStart = getOffsetLatLng(center, latShift * gridSizeKm, 0)
		const southRowStart = getOffsetLatLng(center, latShift * gridSizeKm, 180)

		// Прерываем генерацию, если строки на севере и юге вышли за пределы видимой области более чем на 1 линию
		if (northRowStart.lat > northEast.lat + gridSizeKm / 111 && southRowStart.lat < southWest.lat - gridSizeKm / 111) break

		// Генерируем строки на севере и юге относительно центра
		[northRowStart, southRowStart].forEach((rowStartLatLng) => {
			let colStartLatLng = rowStartLatLng

			// Движемся на восток и запад от начальной точки строки
			for (let lngShift = 0; true; lngShift++) {
				// Восточные и западные точки
				const eastCorner = getOffsetLatLng(colStartLatLng, lngShift * gridSizeKm, 90)
				const westCorner = getOffsetLatLng(colStartLatLng, lngShift * gridSizeKm, 270)

				// Прерываем генерацию, если столбцы на востоке и западе вышли за пределы более чем на 1 линию
				if (eastCorner.lng > northEast.lng + gridSizeKm / 111 && westCorner.lng < southWest.lng - gridSizeKm / 111) break

				// Создаем и добавляем прямоугольники для восточной и западной позиции, если их ещё нет
				[eastCorner, westCorner].forEach((startLatLng) => {
					const rectangleId = `${startLatLng.lat.toFixed(5)},${startLatLng.lng.toFixed(5)}`

					// Проверяем, существует ли прямоугольник
					if (!drawnRectangles.has(rectangleId)) {
						// Создаем северо-восточный угол
						const northEastCorner = getOffsetLatLng(getOffsetLatLng(startLatLng, gridSizeKm, 0), gridSizeKm, 90)

						// Добавляем прямоугольник на карту
						const rectangle = L.rectangle([startLatLng, northEastCorner], {
							color: '#ff7800',
							weight: 1,
							fillOpacity: 0.2,
						}).addTo(map)

						// Сохраняем прямоугольник и его координаты
						gridRectangles.push(rectangle)
						drawnRectangles.add(rectangleId)

						// Добавляем событие клика, чтобы показать координаты всех вершин
						rectangle.on('click', () => {
							const rectangleCoord = [
									[startLatLng.lat, startLatLng.lng],
									[northEastCorner.lat, northEastCorner.lng],
									[startLatLng.lat, northEastCorner.lng],
									[northEastCorner.lat, startLatLng.lng],
									[startLatLng.lat, startLatLng.lng],
							]
							rectangleClickHandler && rectangleClickHandler(rectangleCoord)
						})
					}
				})
			}
		})
	}
}