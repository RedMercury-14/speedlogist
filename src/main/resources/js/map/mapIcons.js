// -------------------------------------------------------------------------------//
// -------------- набор иконок для маркеров для библиотеки leaflet ---------------//
// -------------------------------------------------------------------------------//

// набор иконок для маркеров
export const mapIcons = {
	startIcon: (markerText, isSmall = false) => L.divIcon({
		html: createIconHtml('#7cb342', markerText, isSmall),
		iconSize: [55, 41],
		iconAnchor: [16, 31]
	}),

	intermediateIcon: (markerText, isSmall = false) => L.divIcon({
		html: createIconHtml('#267fca', markerText, isSmall),
		iconSize: [55, 41],
		iconAnchor: [12, 31]
	}),

	finishIcon: (markerText, isSmall = false) => L.divIcon({
		html: createIconHtml('#f97777', markerText, isSmall),
		iconSize: [55, 41],
		iconAnchor: [12, 31]
	}),

	smallStartIcon: (markerText, isSmall = true) => L.divIcon({
		html: createIconHtml('#7cb342', markerText, isSmall),
		iconSize: [55, 41],
		iconAnchor: [10, 20]
	}),

	smallIntermediateIcon: (markerText, isSmall = true) => L.divIcon({
		html: createIconHtml('#267fca', markerText, isSmall),
		iconSize: [55, 41],
		iconAnchor: [7, 20]
	}),

	smallFinishIcon: (markerText, isSmall = true) => L.divIcon({
		html: createIconHtml('#f97777', markerText, isSmall),
		iconSize: [55, 41],
		iconAnchor: [7, 20]
	}),

	smallColoredIcon: (markerText, color, isSmall = true) => L.divIcon({
		html: createIconHtml(color, markerText, isSmall),
		iconSize: [82, 41],
		iconAnchor: [7, 20]
	}),
}

// функция создания иконок для маркеров
function createIconHtml(color, shopNum, isSmall) {
	const smallClass = isSmall ? `class="small"` : ''
	return `
		<svg ${smallClass} aria-hidden="true" focusable="false" role="img" xmlns="http://www.w3.org/2000/svg" viewBox="0 0 384 512" height="0" style="cursor: pointer; stroke: none; fill: ${color};">
			<path d="M172.268 501.67C26.97 291.031 0 269.413 0 192 0 85.961 85.961 0 192 0s192 85.961 192 192c0 77.413-26.97 99.031-172.268 309.67-9.535 13.774-29.93 13.773-39.464 0z"></path>
			<path d="M192 272c44.183 0 80-35.817 80-80s-35.817-80-80-80-80 35.817-80 80 35.817 80 80 80z" fill="white"></path>
			<text x="50%" y="55%" text-anchor="middle" fill="black" style="font-size: 210px;"></text>
		</svg>
		<div class="marker-text">${shopNum}</div>
	`
}