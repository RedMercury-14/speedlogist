import { dateHelper } from "../utils.js"

// Функция для отрисовки календаря с выделенными заказами и поставками
export function renderOrderCalendar(orderDates, deliveryDates = [], orderDateClickHandler) {
	const container = document.querySelector('#orderCalendar')
	container.innerHTML = ''
	const table = document.createElement('table')
	table.className = 'table table-bordered text-center'

	// Создаем первую строку с днями недели
	const daysOfWeek = ["", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"]
	const trHeader = document.createElement('tr')
	daysOfWeek.forEach(day => {
		const th = document.createElement('th')
		th.textContent = day
		trHeader.appendChild(th)
	})
	table.appendChild(trHeader)

	const formatedOrderDates = orderDates.sort().map(date => dateHelper.getDateForInput(date))
	const formatedDeliveryDates = deliveryDates.sort().map(date => dateHelper.getDateForInput(date))
	const orderDatesToView = formatedOrderDates.map(date => dateHelper.formatToDDMM(new Date(date)))
	const deliveryDatesToView = formatedDeliveryDates.map(date => dateHelper.formatToDDMM(new Date(date)))
	
	const firstOrderDate = new Date(orderDates[0])
	const startDate = getStartOfWeek(firstOrderDate)
	const allDates = generateAllDates(startDate, 5)
	const startWeekIndex = getStartWeekIndex(allDates)
	const endWeekIndex = startWeekIndex + 5
	
	
	let dateIndex = 0 // Индекс для текущей даты из allDates

	// Формируем строки недели и заполняем их
	for (let week = startWeekIndex; week < endWeekIndex; week++) {
		const tr = document.createElement('tr')
		const weekLabel = document.createElement('td')
		weekLabel.innerHTML = `Н<sub>${week}</sub>`
		weekLabel.classList.add('week-label')
		tr.appendChild(weekLabel)

		// Заполняем дни недели датами
		for (let day = 0; day < 7; day++) {
			const td = document.createElement('td')

			const currentDate = allDates[dateIndex]
			const orderIndex = orderDatesToView.indexOf(currentDate)
			const deliveryIndex = deliveryDatesToView.indexOf(currentDate)

			const dateContainer = document.createElement('div')
			dateContainer.classList.add('date-container')
			dateContainer.textContent = currentDate

			td.appendChild(dateContainer)

			// Проверяем, если текущая дата есть в заказах или поставках
			if (orderIndex !== -1 || (deliveryDates && deliveryIndex !== -1)) {
				if (orderIndex !== -1) {
					td.classList.add('order-highlight')
					// Добавляем дату заказа в дата-атрибут для дальнейшего использования
					td.dataset.orderDate = formatedOrderDates[orderIndex]
					// Добавляе оьработчик клика на дату заказа
					td.addEventListener('click', orderDateClickHandler)
					// Добавляем обработчики наведения на дату заказа
					td.addEventListener('mouseenter', (e) => highlightPair(orderIndex, true))
					td.addEventListener('mouseleave', (e) => highlightPair(orderIndex, false))
				}

				if (deliveryDates && deliveryIndex !== -1) {
					td.classList.add('delivery-highlight')
				}

				// Добавляем идентификатор для подсветки пары
				td.dataset.pairIndex = orderIndex !== -1 ? orderIndex : deliveryIndex
			}

			tr.appendChild(td)
			dateIndex++
		}

		table.appendChild(tr)
	}

	container.appendChild(table)
}

// Функция для подсветки пары заказ-поставка при наведении
function highlightPair(pairIndex, highlight) {
	const allCells = document.querySelectorAll('td')

	allCells.forEach(cell => {
		if (cell.dataset.pairIndex == pairIndex) {
			if (highlight) {
				if (cell.classList.contains('order-highlight')) {
					cell.classList.add('highlight')
				} else if (cell.classList.contains('delivery-highlight')) {
					cell.classList.add('highlighted-delivery')
				}
			} else {
				cell.classList.remove('highlight')
				cell.classList.remove('highlighted-delivery')
			}
		}
	})
}

// Функция для генерации массива всех дат на основе стартовой даты
function generateAllDates(startDate, numWeeks) {
	const allDates = []
	const numDays = numWeeks * 7

	for (let i = 0; i < numDays; i++) {
		const day = String(startDate.getDate()).padStart(2, '0')
		const month = String(startDate.getMonth() + 1).padStart(2, '0')
		allDates.push(`${day}.${month}`)
		startDate.setDate(startDate.getDate() + 1)
	}

	return allDates
}

// функция для получения даты начала недели по произвольной дате
function getStartOfWeek(date) {
	const dayOfWeek = date.getDay() === 0 ? 7 : date.getDay()
	const startOfWeek = new Date(date)
	startOfWeek.setDate(date.getDate() - dayOfWeek + 1)
	return startOfWeek
}

// функция для получения индекса первой недели
function getStartWeekIndex(allDates) {
	const today = new Date()
	const todayDate = dateHelper.formatToDDMM(today)
	const todayDateIndex = allDates.findIndex(date => date === todayDate)
	if (!todayDateIndex) return -2
	return -(Math.floor(todayDateIndex / 7))
}

