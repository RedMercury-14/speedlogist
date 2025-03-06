import { snackbar } from "../snackbar/snackbar.js"
import { dateHelper } from "../utils.js"
import { getRoutesInfo } from "./api.js"
import { eventColors, routeStatusColor, routeStatusText, userMessages } from "./constants.js"
import { convertToDDMMYYYY, convertToDayMonthTime, getEventBGColor, getSlotStatus, getSlotStatusYard, stockAndDayIsVisible, stockIsVisible } from "./dataUtils.js"
import { editableRulesToConfirmBtn, hasOrderInYard, isAnotherUser, isBackgroundEvent, removeDraggableElementRules } from "./rules.js"
import { store } from "./store.js"

export function addNewStockOption(select, stock) {
	const option = document.createElement("option")
	option.value = stock.id
	option.textContent = stock.name
	select.appendChild(option)
}

// обновление ивентов дропзоны
export function updateDropZone(dropZone, login, selectedStock) {
	const eventContainer = document.querySelector("#external-events")
	eventContainer.innerHTML = ''
	dropZone.forEach(order => createDraggableElement(eventContainer, order, login, selectedStock))
}

// создание перетаскиваемого элемента заказа (события)
export function createDraggableElement(container, order, login, currentStock) {
	// устанавливаем логин менеджера, если его нет
	!order.loginManager && (order.loginManager = login)
	const eventId = order.marketNumber
	const stockId = currentStock.id
	const status = order.status
	const bgColor = status === 5
		? getEventBGColor(8) : status === 6
			? getEventBGColor(7) : getEventBGColor(status)

	const singleSlotElem = document.createElement("div")
	singleSlotElem.id = `event_${eventId}`
	singleSlotElem.className = status > 8 && status !== 100 ? "fc-event text-dark" : 'fc-event'
	singleSlotElem.style.backgroundColor = bgColor
	singleSlotElem.innerHTML = createSingleSlotHTML(order)

	// кнопка удаления слота из дроп-зоны
	const role = store.getRole()
	if (removeDraggableElementRules(role)) {
		const closeBtn = createCloseEventButton({ isDraggable: true }, true)
		closeBtn.addEventListener('click',(e) => removeDraggableElement(e, singleSlotElem))
		singleSlotElem.prepend(closeBtn)
	}

	container.appendChild(singleSlotElem)

	const event = {
		id: eventId,
		title: order.counterparty,
		duration: order.timeUnload,
		extendedProps: { data: order },
		startEditable: true,
		durationEditable: false,
		resourceEditable: true,
		backgroundColor: bgColor,
		borderColor: eventColors.borderColor,
		textColor: 'black',
	}

	if (stockId !== '1700' || stockId != '1800') event.constraint = 'businessHours'

	new FullCalendar.Draggable(
		singleSlotElem,
		{ eventData: (eventEl) => event }
	)
}

// обработка удаления слота из дроп-зоны
function removeDraggableElement(e, draggableElement) {
	const eventId = draggableElement.id.split('_')[1]
	store.removeEventFromDropZone(eventId)
	draggableElement.remove()
}

// функция создания контента ивента в дропзоне
function createSingleSlotHTML(order) {
	const [h,m] = order.timeUnload.split(":")
	const dateDelivery = convertToDDMMYYYY(order.dateDelivery)

	const importLabel = order.way === "Импорт" || order.isImport
		? '<span class="text-danger">Импорт</span>'
		: ''

	return `
		<div class="single-slot">
			<div class="d-flex justify-content-between">
				<div class="single-slot__id">ID: ${order.idOrder}</div>
				${importLabel}
			</div>
			<div class="single-slot__marketNumber">№: ${order.marketNumber}</div>
			<div class="single-slot__marketNumber">Склад: ${order.numStockDelivery}</div>
			<div class="single-slot__dateDelivery">Дата доставки: ${dateDelivery}</div>
			<div class="single-slot__counterparty">Контрагент: ${order.counterparty}</div>
			<div class="single-slot__info">${order.cargo} ● ${order.pall} палл ● ${h} ч ${m} мин</div>
		</div>
	`
}

// функция создания контента ивента календаря
export function createEventElement(info) {
	const eventElem = document.createElement('div')
	const { event: fcEvent, timeText } = info
	const { id, title, extendedProps } = fcEvent
	const order = extendedProps.data

	// элемент подложки для других ивентов
	if (isBackgroundEvent(fcEvent)) {
		const backgroundEventClasses = 'fc-event fc-event-start fc-event-end fc-event-today fc-bg-event'
		eventElem.className = backgroundEventClasses
		return eventElem
	}

	const importLabel = order.way === "Импорт" || order.isImport
		? '<span class="text-danger">Импорт</span>'
		: ''

	// жирная граница ивента для статусов Двора
	eventElem.className = hasOrderInYard(order) ? 'fc-event-main-frame boldBorder' : 'fc-event-main-frame'
	eventElem.style.cursor = 'move'
	eventElem.innerHTML = `
		<div class="fc-event-time">${timeText}</div>
		<div class="fc-event-title-container">
			<div class="fc-event-title fc-sticky">${id} ${title}</div>
			${importLabel}
		</div>
	`
	return eventElem
}

// функция создания кнопки закрытия ивента календаря
export function createCloseEventButton(info, showBtn) {
	const closeBtn = document.createElement('button')
	closeBtn.type = 'button'
	closeBtn.disabled = !info.isDraggable && !showBtn
	closeBtn.className = 'close'
	closeBtn.ariaLabel = 'Закрыть'
	closeBtn.dataset.action = 'close'
	closeBtn.innerHTML = '&times;'

	return closeBtn
}

// функция создания кнопки вызова попапа с информацией
export function createPopupButton(info, currentLogin) {
	const order = info.event.extendedProps.data
	const status = order && order.status
	const orderLogin = order && order.loginManager
	const isConfirmedSlot = status > 8
	const isNotAnimated = isConfirmedSlot || isAnotherUser(orderLogin, currentLogin)

	const popupBtn = document.createElement('button')
	popupBtn.type = 'button'
	popupBtn.className = isNotAnimated ? 'popup' : 'popup text-danger border-danger animation__big-pulse'
	popupBtn.ariaLabel = 'Информация'
	popupBtn.dataset.action = 'popup'
	popupBtn.innerHTML = `i`

	return popupBtn
}

export function createCheckSlotBtn(info) {
	const checkSlotBtn = document.createElement('button')
	checkSlotBtn.type = 'button'
	checkSlotBtn.className = 'checkSlot'
	checkSlotBtn.ariaLabel = 'Тест слота'
	checkSlotBtn.dataset.action = 'checkSlot'
	checkSlotBtn.innerHTML = `T`

	return checkSlotBtn
}

export function createCheckBookingBtn(info) {
	const checkBookingBtn = document.createElement('button')
	checkBookingBtn.type = 'button'
	checkBookingBtn.className = 'checkBooking'
	checkBookingBtn.ariaLabel = 'Проверка на бронь'
	checkBookingBtn.dataset.action = 'checkBooking'
	checkBookingBtn.innerHTML = `B`

	return checkBookingBtn
}

export function createDeleteSlotBtn(info) {
	const checkBookingBtn = document.createElement('button')
	checkBookingBtn.type = 'button'
	checkBookingBtn.className = 'deleteSlot'
	checkBookingBtn.ariaLabel = 'Удалить слот'
	checkBookingBtn.dataset.action = 'deleteSlot'
	checkBookingBtn.innerHTML = `D`

	return checkBookingBtn
}

// функция добавления всплывающей подсказки с информацией
export function addTooltip(element, info) {
	const { event: fcEvent, timeText } = info
	const { title } = fcEvent

	const tooltipHtml = `
		<div>${timeText}</div>
		<div >${title}</div>
	`

	// const tooltip = new bootstrap.Tooltip(element, {
	// 	delay: { "show": 700, "hide": 100 },
	// 	html: true,
	// 	title: tooltipHtml,
	// })
}

// отображение модального окна с информацией об ивенте
export async function showEventInfoPopup(fcEvent, currentLogin, currentRole) {
	const { extendedProps } = fcEvent
	const { data } = extendedProps
	const status = data.status
	const orderLogin = data.loginManager
	const isConfirmedSlot = status > 8 
	const text = isConfirmedSlot ? 'Снять подтверждение слота' : 'Подтвердить слот'
	const isNotAnimated = isConfirmedSlot || isAnotherUser(orderLogin, currentLogin)
	const action = isConfirmedSlot ? 'unSave' : 'save'

	const withRoutes = status >= 30 && status <= 70
	const routes = withRoutes ? await getRoutesInfo(data.idOrder) : []

	const eventInfo = document.querySelector('#eventInfo')
	const routesInfo = document.querySelector('#routesInfo')
	const yardInfo = document.querySelector('#yardInfo')
	const confirmSlotBtn = document.querySelector('#confirmSlot')
	eventInfo.innerHTML = createEventInfoHTML(fcEvent)
	routesInfo.innerHTML = createRoutesInfoHTML(routes)
	yardInfo.innerHTML = createYardInfoHTML(fcEvent)
	confirmSlotBtn.innerText = text
	confirmSlotBtn.className = isNotAnimated ? 'btn btn-secondary' : 'btn btn-secondary animation__small-pulse'
	confirmSlotBtn.dataset.action = action
	confirmSlotBtn.disabled = !editableRulesToConfirmBtn(data, currentLogin, currentRole)

	$('#eventInfoModal').modal('show')
}
export function hideEventInfoPopup() {
	$('#eventInfoModal').modal('hide')
}

// функции создания контента модального окна с информацией об ивенте
function createEventInfoHTML(fcEvent) {
	const order = fcEvent.extendedProps.data
	const { marketNumber, dateDelivery, timeUnload, cargo, counterparty, loginManager, idRamp, idOrder, status, pall } = order
	const marketInfo = order.marketInfo ? order.marketInfo : ''
	const slotInfo = order.slotInfo ? order.slotInfo : ''
	const statusToView = getSlotStatus(status)
	const stock = `${idRamp}`.slice(0, -2)
	const ramp = `${idRamp}`.slice(-2)
	const [h,m] = timeUnload.split(":")
	const dateDeliveryView = convertToDDMMYYYY(dateDelivery)
	const eventStartDate = convertToDayMonthTime(fcEvent.startStr)

	return `
		<div class="event-info__status">
			<p class="mb-1 font-weight-bold">Статус заказа: ${statusToView}</p>
		</div>
		${slotInfo && `<div class="event-info__slotInfo font-weight-bold">${slotInfo}</div>`}
		<div class="event-info__ramp">
			<span class="text-muted font-weight-bold">Склад:</span> ${stock}
			<span class="text-muted font-weight-bold">Рампа:</span> ${ramp}
		</div>
		<div><span class="text-muted font-weight-bold">Начало выгрузки:</span> ${eventStartDate}</div>
		<div><span class="text-muted font-weight-bold">Длительность выгрузки:</span> ${h} ч ${m} мин</div>
		<div><span class="text-muted font-weight-bold">ID заявки:</span> ${idOrder}</div>
		<div><span class="text-muted font-weight-bold">Номер из Маркета:</span> ${marketNumber}</div>
		<div><span class="text-muted font-weight-bold">Дата доставки:</span> ${dateDeliveryView}</div>
		<div><span class="text-muted font-weight-bold">Контрагент:</span> ${counterparty}</div>
		<div><span class="text-muted font-weight-bold">Груз:</span> ${cargo}</div>
		<div><span class="text-muted font-weight-bold">Паллеты:</span> ${pall}</div>
		<div><span class="text-muted font-weight-bold">Менеджер:</span> ${loginManager}</div>
		<div><span class="text-muted font-weight-bold">Информация (из Маркета):</span> ${marketInfo}</div>
	`
}
function createRoutesInfoHTML(routes) {
	if (!routes || routes.length === 0) {
		return `
			<div class="event-info__status">
				<p class="mb-1 font-weight-bold">Маршруты не найдены</p>
			</div>
		`
	}

	const sortedRoutes = routes.sort((a,b) => b.idRoute - a.idRoute)
	const [ actualRoute, ...oldRoutes ] = sortedRoutes
	const { idRoute, routeDirection, user, logistInfo, statusRoute } = actualRoute
	const carrier = user && user.companyName ? user.companyName : ''
	const statusText = routeStatusText[statusRoute]

	const oldRoutesInfo = oldRoutes
		.map(route => {
			const date = dateHelper.getFormatDate(route.createDate)
			const timeToView = route.createTime ? `, ${route.createTime.slice(0, 5)}` : ''
			const textColor = routeStatusColor[route.statusRoute]
			return `
				<li class="mb-1 p-1" style="background-color: ${textColor};">
					Маршрут ${route.routeDirection} от ${date}${timeToView}  — ${routeStatusText[route.statusRoute]}
				</li>
			`
		})
		.join('')

	return `
		<div class="event-info__status">
			<p class="mb-1 font-weight-bold">Статус маршрута: ${statusText}</p>
		</div>
		<div><span class="text-muted font-weight-bold">Номер маршрута:</span> ${idRoute}</div>
		<div><span class="text-muted font-weight-bold">Название маршрута:</span> ${routeDirection}</div>
		<div><span class="text-muted font-weight-bold">Перевозчик:</span> ${carrier}</div>
		<div><span class="text-muted font-weight-bold">Логист:</span> ${logistInfo}</div>
		<div><span class="text-muted font-weight-bold">Старые маршруты:</span> </div>
		<ul class="ml-4">${oldRoutesInfo}</ul>
	`
}
function createYardInfoHTML(fcEvent) {
	const order = fcEvent.extendedProps.data
	const statusYard = getSlotStatusYard(order.statusYard)
	const arrivalFactYard = order.arrivalFactYard ? convertToDayMonthTime(order.arrivalFactYard) : ''
	const registrationFactYard = order.registrationFactYard ? convertToDayMonthTime(order.registrationFactYard) : ''
	const unloadStartYard = order.unloadStartYard ? convertToDayMonthTime(order.unloadStartYard) : ''
	const unloadFinishYard = order.unloadFinishYard ? convertToDayMonthTime(order.unloadFinishYard) : ''
	const pallFactYard = order.pallFactYard ? order.pallFactYard : ''
	const weightFactYard = order.weightFactYard ? `${order.weightFactYard} кг` : ''

	return `
		<div class="event-info__status">
			<p class="mb-1 font-weight-bold">Статус во Дворе: ${statusYard}</p>
		</div>
		<div><span class="text-muted font-weight-bold">Номер из Маркета:</span> ${order.marketNumber}</div>
		<div><span class="text-muted font-weight-bold">Прибытие на склад:</span> ${arrivalFactYard}</div>
		<div><span class="text-muted font-weight-bold">Регистрация:</span> ${registrationFactYard}</div>
		<div><span class="text-muted font-weight-bold">Начало выгрузки (факт):</span> ${unloadStartYard}</div>
		<div><span class="text-muted font-weight-bold">Конец выгрузки (факт):</span> ${unloadFinishYard}</div>
		<div><span class="text-muted font-weight-bold">Зарегистрировано паллет:</span> ${pallFactYard}</div>
		<div><span class="text-muted font-weight-bold">Масса груза:</span> ${weightFactYard}</div>
	`
}

// функция изменения ширины календаря для количества рамок заказа
export function setCalendarWidth(numberOfRamps) {
	const fcView = document.querySelector('.fc-view-harness .fc-view')
	const fcTable = document.querySelector('.fc-view-harness .fc-view > table')

	if (numberOfRamps > 8) {
		fcView.style.overflowX = 'scroll'
		fcTable.style.width = `${150 * numberOfRamps}px`
	} else {
		fcView.style = ''
		fcTable.style = ''
	}
}

export function showMobileTooltop() {
	const dragTooltipElems = document.querySelectorAll('.mobile-tooltip')
	dragTooltipElems.forEach(elem => elem.classList.remove('d-none'))
}

export function isMobileDevice() {
	return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
			.test(navigator.userAgent)
}


// обновление информации о паллетовместимости
export function setPallInfo(pallCount, maxPall) {
	if (!pallCount || !maxPall) return

	const externalPallCountElem = document.querySelector('#externalPallCount')
	const externalMaxPallElem = document.querySelector('#externalMaxPall')
	const internalPallCountElem = document.querySelector('#internalPallCount')
	const internalMaxPallElem = document.querySelector('#internalMaxPall')

	if (
		!externalPallCountElem
		|| !externalMaxPallElem
		|| !internalPallCountElem
		|| !internalMaxPallElem
	) {
		return
	}

	const externalPallCount = pallCount.externalMovement
	const externalMaxPall = maxPall.externalMovement
	const internalPallCount = pallCount.internalMovement
	const internalMaxPall = maxPall.internalMovement

	externalPallCountElem.innerText = externalPallCount
	externalMaxPallElem.innerText = externalMaxPall
	internalPallCountElem.innerText = internalPallCount
	internalMaxPallElem.innerText = internalMaxPall

	changePallCountElemColor(externalPallCountElem, externalPallCount, externalMaxPall)
	changePallCountElemColor(internalPallCountElem, internalPallCount, internalMaxPall)
}
export function updatePallInfo(currentPallCount, maxPall, orderType) {
	let pallCountElem
	if (orderType === 'externalMovement') {
		pallCountElem = document.querySelector('#externalPallCount')
	}
	if (orderType === 'internalMovement') {
		pallCountElem = document.querySelector('#internalPallCount')
	}
	if (!pallCountElem) return

	const newPallCountValue = currentPallCount[orderType]
	const maxPallValue = maxPall[orderType]
	if (newPallCountValue === undefined || Number.isNaN(newPallCountValue)) return

	pallCountElem.innerText = newPallCountValue
	changePallCountElemColor(pallCountElem, newPallCountValue, maxPallValue)
}
function changePallCountElemColor(pallCountElem, pallCount, maxPall) {
	const factor = pallCount / maxPall

	if (factor < 0.5) {
		pallCountElem.className = 'text-success'
	}
	if (factor >= 0.5) {
		pallCountElem.className = 'text-warning'
	}
	if (factor >= 1 || Number.isNaN(factor)) {
		pallCountElem.className = 'text-danger'
	}
}


// функции обработки ошибок от сервера
export function errorHandler_100status(info, data) {
	if (info !== null) info.revert()
	const errorMessage = data.info
		? data.info
		: userMessages.actionNotCompleted
	snackbar.show(errorMessage)
}
export function errorHandler_105status(info, data) {
	if (info !== null) info.revert()
	data.info && showMessageModal(data.info)
}


// отображение модального окна обновления страницы
export function showReloadWindowModal(text) {
	const defaultText = 'Связь с сервером потеряна. Пожалуйста, обновите страницу!'
	const displayText = text ? text : defaultText
	$('#reloadWindowModalLabel').text(displayText)
	$('#reloadWindowModal').modal('show')
	$('.modal-backdrop').addClass("whiteOverlay")
}
export function hideReloadWindowModal() {
	$('.modal-backdrop').removeClass("whiteOverlay")
	$('#reloadWindowModal').modal('hide')
}


// изменение положения календаря и боковой панели
export function addSmallHeaderClass() {
	const navbar = document.querySelector('.navbar')
	const height = navbar.offsetHeight
	
	if (height < 65) {
		const calendarWrapper = document.querySelector('.calendar-wrapper')
		const sidebar = document.querySelector('.sidebar')
		calendarWrapper.classList.add('smallHeader')
		sidebar.classList.add('smallHeader')
	}
}


// копирование информации о слоте в буфер обмена
export function copyToClipboard(text) {
	navigator.clipboard.writeText(text)
	snackbar.show('Информация о слоте скопирована в буфер обмена')
}

export function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerHTML = message
	setTimeout(() => {
		$('#displayMessageModal').modal('show')
	}, 300);
}

// добавление текущей даты в атрибуты календаря
export function setCurrentDateAttr(currentDate) {
	const calendarElem = document.querySelector('#calendar')
	const nowDate = new Date().toISOString().split('T')[0]

	calendarElem.dataset.date = currentDate === nowDate
		? 'now'
		: currentDate
}

// добавление id текущего склада в атрибуты календаря
export function setStockAttr(stockId) {
	const calendarElem = document.querySelector('#calendar')
	calendarElem.dataset.stock = stockId
}

// создание поля выбора даты для отображения в календаре
export function createCalendarDateInput(calendar) {
	const container = calendar.el
	const letfHeaderToolbarContainer = container.querySelector('.fc-header-toolbar .fc-toolbar-chunk')
	if (!letfHeaderToolbarContainer) return

	const changeCalendarDateInput = document.createElement('input')
	changeCalendarDateInput.type = 'date'
	changeCalendarDateInput.classList.add('form-control', 'form-control-sm', 'changeCalendarDate')
	letfHeaderToolbarContainer.classList.add('d-flex', 'align-items-center')

	// обработчик изменения даты
	changeCalendarDateInput.onchange = (e) => {
		const selectedDate = (e.target.value)
		if (!selectedDate) return
		calendar.gotoDate(selectedDate)
	}

	// удаляем содержимое поля с датой при использовании кнопок иправления детой из календаря
	const prevDayBtn = letfHeaderToolbarContainer.querySelector('.fc-prev-button')
	const nextDayBtn = letfHeaderToolbarContainer.querySelector('.fc-next-button')
	const todayBtn = letfHeaderToolbarContainer.querySelector('.fc-today-button')
	prevDayBtn.addEventListener('click', () => changeCalendarDateInput.value = '')
	nextDayBtn.addEventListener('click', () => changeCalendarDateInput.value = '')
	todayBtn.addEventListener('click', () => changeCalendarDateInput.value = '')

	letfHeaderToolbarContainer.append(changeCalendarDateInput)
}

// отображение выбранных даты и склада
export function displayStockAndDate(calendarApi, currentStock, currentDate, foundEvent) {
	const order = foundEvent.extendedProps.data
	const [ eventDate, eventTime ] = foundEvent.start.split('T')
	const idRamp = order.idRamp
	const numStock = `${idRamp}`.slice(0, -2)

	if (stockAndDayIsVisible(currentStock, currentDate, numStock, eventDate)) {
		calendarApi.scrollToTime(eventTime)
		return
	}

	if (stockIsVisible(currentStock, numStock)) {
		calendarApi.gotoDate(eventDate)
		calendarApi.scrollToTime(eventTime)
		return
	}
	
	const stockNumberSelect = document.querySelector('#stockNumber')
	stockNumberSelect.value = numStock
	stockNumberSelect.dispatchEvent(new Event('change'))
	calendarApi.gotoDate(eventDate)
	calendarApi.scrollToTime(eventTime)
}

// поиск слота на складах и его отображение
export function searchSlot(props) {
	const { searchValue, events, calendar, currentDate, currentStock } = props

	if (!events || events.length === 0) {
		snackbar.show('Слот не найден')
		return
	}

	const foundEvent = events.find(event => {
		return event.extendedProps.data.idOrder === Number(searchValue)
			|| event.extendedProps.data.marketNumber === searchValue
	})

	if (foundEvent) {
		const order = foundEvent.extendedProps.data
		const eventId = order.marketNumber
		// отображаем нужные дату и склад
		displayStockAndDate(calendar, currentStock, currentDate, foundEvent) 
		// подсвечиваем слот на 3 сек
		highlightSlot(calendar, eventId)
	} else {
		snackbar.show('Слот не найден')
	}
}

// подсвечивание слота в календаре
export function highlightSlot(calendarApi, eventId) {
	const calendarEvent = calendarApi.getEventById(eventId)
	if (!calendarEvent) return
	const prevColor = calendarEvent.backgroundColor
	calendarEvent.setProp('backgroundColor', eventColors.foundEvent)
	setTimeout(() => calendarEvent.setProp('backgroundColor', prevColor), 3000)
}

export function getMultiplicity() {
	const value = prompt(
		'Для корректной работы программы необходимо указать количество паллетомест в машине. '
		+ 'Это нужно будет сделать один раз для обновления графика поставок по данному контрагенту. '
		+ 'Пожалуйста, укажите ЦЕЛОЕ число паллетомест (от 1 до 99).'
	)

	if (!value) return

	const multiplicity = Number(value)

	if (isNaN(multiplicity)) {
		alert('Введено не число')
		return
	}

	if (!Number.isInteger(multiplicity) || multiplicity < 1 || multiplicity > 99) {
		alert('Введено некорректное число')
		return
	}

	return multiplicity
}