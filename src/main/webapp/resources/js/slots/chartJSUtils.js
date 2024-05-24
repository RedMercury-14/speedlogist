import { dateHelper } from "../utils.js"

export const pallChartConfig = {
	type: 'line',
	data: {
		datasets: [{
			label: 'Паллетовместимость',
			fill: false,
			borderColor: '#1f77b4',
			pointRadius: 7,
			pointHoverRadius: 10,
			data: [],
			backgroundColor: [],
		}]
	},
}

export function updateChartData(chart, data, bgData) {
	chart.data.datasets[0].data = data
	chart.data.datasets[0].backgroundColor = bgData
	chart.update()
}

export function getFormattedPallChartData(pallChartData) {
	return pallChartData.map(item => ({
		x: dateHelper.changeFormatToView(item.date).slice(0,5),
		y: item.maxPall.externalMovement
	}))
}

export function getMarkerBGColorData(pallChartData) {
	return pallChartData.map(item => {
		return dateHelper.isWeekend(item.date)
			? '#ff7f0e'
			: '#1f77b4'
	})
}
