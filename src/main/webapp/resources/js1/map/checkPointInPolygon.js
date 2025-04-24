/**Функция проверки вхождения точки в многоугольник на карте
 * ray-casting algorithm based on
 * https://wrf.ecse.rpi.edu/Research/Short_Notes/pnpoly.html
 * 
 * @param  { [number, number] } pointCoordInArray
 * @param  { Array<[number, number]> } polygonCoords
 * @return { boolean }
 */
export function checkPointInPolygon(pointCoordInArray, polygonCoords) {
	const x = pointCoordInArray[0], y = pointCoordInArray[1];
	
	let inside = false;
	for (let i = 0, j = polygonCoords.length - 1; i < polygonCoords.length; j = i++) {
		const xi = polygonCoords[i][0], yi = polygonCoords[i][1]
		const xj = polygonCoords[j][0], yj = polygonCoords[j][1]
		
		const intersect = ((yi > y) != (yj > y))
			&& (x < (xj - xi) * (y - yi) / (yj - yi) + xi)
		if (intersect) inside = !inside
	}
	
	return inside
}