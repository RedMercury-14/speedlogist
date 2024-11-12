import { getScheduleArrayFromScheduleObj } from "./utils.js"

// отображение графика поставки в модальном окне
export function showScheduleItem(rowNode) {
	const scheduleItem = rowNode.data
	const scheduleData = getScheduleArrayFromScheduleObj(scheduleItem)
	const schedule = scheduleData.map((item) => item ? item : '')
	const note = scheduleItem.note ? scheduleItem.note : ''
	const matrix = getDeliveryScheduleMatrix(schedule, note)
	renderScheduleItem(schedule)
	renderMatrix(matrix)
	$(`#showScheduleModal`).modal('show')
}

// функция получения матрицы визуализации графика поставки
function getDeliveryScheduleMatrix(schedule, note) {
	const daysDictionary = {
		"понедельник": 'Пн',
		"вторник": 'Вт',
		"среда": 'Ср',
		"четверг": 'Чт',
		"пятница": 'Пт',
		"суббота": 'Сб',
		"воскресенье": 'Вс',
	}
	const weekNumbers = {
		"н0": 1,
		"н1": 2,
		"н2": 3,
		"н3": 4,
		"н4": 5,
	}
	const days = Object.keys(daysDictionary)
	const shortDays = Object.values(daysDictionary)
	const weekNumberKeys = Object.keys(weekNumbers)
	const matrix = [
		['', ...shortDays ],
		['н0', '', '', '', '', '', '', ''],
		['н1', '', '', '', '', '', '', ''],
		['н2', '', '', '', '', '', '', ''],
		['н3', '', '', '', '', '', '', ''],
		['н4', '', '', '', '', '', '', ''],
	]
	const isWeekIndicated = note === 'неделя'
	const orderRow = matrix[1]
	let orderCounter = 1

	// обработка дней заказов
	schedule.forEach((entry, index) => {
		if (entry) {
			const parts = entry.split('/')
			const orderDay = getOrderDay(parts, index)

			// расставляем дни заказов на текущей неделе
			if (orderDay) {
				matrix[0].forEach((day, dayIndex) => {
					if (day === orderDay) {
						orderRow[dayIndex] = `з${orderCounter}`
						orderCounter++
					}
				})
			}
		}
	})

	// обработка дней поставок
	schedule.forEach((entry, index) => {
		if (entry) {
			const parts = entry.split('/')
			const deliveryDay = getDeliveryDay(parts)
			if (deliveryDay) {
				const deliveryWeek = deliveryDay ? getDeliveryWeek(parts, deliveryDay, index) : ''
				const orderIndex = shortDays.indexOf(deliveryDay) + 1
				const targetOrder = orderRow[orderIndex]
				const deliveryCounter = findDigitAfterZ(targetOrder)
				const deliveryCol = index + 1
				const deliveryRow = weekNumbers[deliveryWeek]

				// расставляем дни поставок
				const targetCell = matrix[deliveryRow][deliveryCol]
				if (targetCell) {
					matrix[deliveryRow][deliveryCol] += `/п${deliveryCounter}`
				} else {
					matrix[deliveryRow][deliveryCol] = `п${deliveryCounter}`
				}
			}
		}
	})
	return matrix

	// получение дня заказа
	function getOrderDay(parts, index) {
		return parts.includes('з') ? shortDays[index] : ''
	}
	// получение дня поставки
	function getDeliveryDay(parts) {
		return parts.reduce((acc, part) => {
			if (days.includes(part)) {
				return daysDictionary[part]
			}
			return acc
		}, '')
	}
	// получение ключа для номера недели
	function getDeliveryWeek(parts, deliveryDay, index) {
		let deliveryWeek
		if (isWeekIndicated) {
			deliveryWeek = parts.reduce((acc, part) => {
				if (weekNumberKeys.includes(part)) {
					return part
				}
				return acc
			}, '')
		} else {
			const dayNumber = shortDays.indexOf(deliveryDay)
			deliveryWeek = index > dayNumber ? 'н0' : 'н1'
		}
		return deliveryWeek
	}
	// поиск цифры после буквы 'з'
	function findDigitAfterZ(str) {
		const match = str.match(/з(\d)/)
		return match ? match[1] : ''
	}
}

// отрисовка графика поставки
function renderScheduleItem(schedule) {
	const days = ['Пн', 'Вт', 'Ср', 'Чт', 'Пт', 'Сб', 'Вс']

	const container = document.querySelector('#scheduleItemContainer')
	container.innerHTML = ''

	days.forEach(day => {
		const cellDiv = document.createElement('div')
		cellDiv.textContent = day
		cellDiv.classList.add('header-cell')
		container.append(cellDiv)
	})

	schedule.forEach(item => {
		const cellDiv = document.createElement('div')
		cellDiv.textContent = item
		container.append(cellDiv)
	})
}

// отрисовка матрицы графика поставки
function renderMatrix(matrix) {
	const container = document.querySelector('#matrixContainer')
	container.innerHTML = ''

	matrix.forEach(row => {
		row.forEach(cell => {
			const cellDiv = document.createElement('div')
			if (row[0] === '' || cell === row[0]) {
				cellDiv.classList.add('header-cell')
				cellDiv.textContent = cell
			} else {
				const parts = cell.split('/')
				parts.forEach((part, i) => {
					const span = document.createElement('span')
					span.textContent = part
					span.classList.add(getColorClass(part))
					cellDiv.append(span)
					if (parts[i + 1]) cellDiv.append('/')
				})
			}
			container.append(cellDiv)
		})
	})
}
function getColorClass(value) {
	const count = getCount(value)
	return `color-${count}`
}
function getCount(value) {
	const digitReg = /\d/
	const countMatching = value.match(digitReg)
	return countMatching ? countMatching[0] : 0
}
