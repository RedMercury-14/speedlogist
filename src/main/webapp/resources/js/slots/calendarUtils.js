import { snackbar } from "../snackbar/snackbar.js"
import { dateHelper } from "../utils.js"
import { Draggable, eventColors, userMessages } from "./constants.js"
import { convertToDDMMYYYY, convertToDayMonthTime, getEventBGColor, getSlotStatus, getSlotStatusYard } from "./dataUtils.js"
import { editableRulesToConfirmBtn, hasOrderInYard, isAnotherUser, isBackgroundEvent } from "./rules.js"

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
	const stockId = currentStock.id
	const status = order.status
	const bgColor = status === 5
		? getEventBGColor(8) : status === 6
			? getEventBGColor(7) : getEventBGColor(status)

	const singleSlotElem = document.createElement("div")
	singleSlotElem.id = `event_${order.marketNumber}`
	singleSlotElem.className = status > 8 && status !== 100 ? "fc-event text-dark" : 'fc-event'
	singleSlotElem.style.backgroundColor = bgColor
	singleSlotElem.innerHTML = createSingleSlotHTML(order)
	container.appendChild(singleSlotElem)

	const event = {
		id: order.marketNumber,
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

	if (stockId !== '1700') event.constraint = 'businessHours'

	new Draggable(
		singleSlotElem,
		{ eventData: (eventEl) => event }
	)
}

// функция создания контента ивента в дропзоне
function createSingleSlotHTML(order) {
	const [h,m] = order.timeUnload.split(":")
	const dateDelivery = convertToDDMMYYYY(order.dateDelivery)

	return `
		<div class="single-slot">
			<div class="single-slot__id">ID: ${order.idOrder}</div>
			<div class="single-slot__marketNumber">Номер из Маркета: ${order.marketNumber}</div>
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

	// жирная граница ивента для статусов Двора
	eventElem.className = hasOrderInYard(order) ? 'fc-event-main-frame boldBorder' : 'fc-event-main-frame'
	eventElem.style.cursor = 'move'
	eventElem.innerHTML = `
		<div class="fc-event-time">${timeText}</div>
		<div class="fc-event-title-container">
			<div class="fc-event-title fc-sticky">${id} ${title}</div>
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
export function showEventInfoPopup(fcEvent, currentLogin, currentRole) {
	const { extendedProps } = fcEvent
	const { data } = extendedProps
	const status = data.status
	const orderLogin = data.loginManager
	const isConfirmedSlot = status > 8 
	const text = isConfirmedSlot ? 'Снять подтверждение слота' : 'Подтвердить слот'
	const isNotAnimated = isConfirmedSlot || isAnotherUser(orderLogin, currentLogin)
	const action = isConfirmedSlot ? 'unSave' : 'save'

	const eventInfo = document.querySelector('#eventInfo')
	const yardInfo = document.querySelector('#yardInfo')
	const confirmSlotBtn = document.querySelector('#confirmSlot')
	eventInfo.innerHTML = createEventInfoHTML(fcEvent)
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
			Склад: ${stock}
			Рампа: ${ramp}
		</div>
		<div class="event-info__start">Начало выгрузки: ${eventStartDate}</div>
		<div class="event-info__duration">Длительность выгрузки: ${h} ч ${m} мин</div>
		<div class="event-info__id">ID заявки: ${idOrder}</div>
		<div class="event-info__marketNumber">Номер из Маркета: ${marketNumber}</div>
		<div class="event-info__dateDelivery">Дата доставки: ${dateDeliveryView}</div>
		<div class="event-info__counterparty">Контрагент: ${counterparty}</div>
		<div class="event-info__cargo">Груз: ${cargo}</div>
		<div class="event-info__pall">Паллеты: ${pall}</div>
		<div class="event-info__manager">Менеджер: ${loginManager}</div>
		<div class="event-info__manager">Информация (из Маркета): ${marketInfo}</div>
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
		<div class="event-info__marketNumber">Номер из Маркета: ${order.marketNumber}</div>
		<div class="event-info__arrivalFactYard">Прибытие на склад: ${arrivalFactYard}</div>
		<div class="event-info__registrationFactYard">Регистрация: ${registrationFactYard}</div>
		<div class="event-info__unloadStartYard">Начало выгрузки (факт): ${unloadStartYard}</div>
		<div class="event-info__unloadFinishYard">Конец выгрузки (факт): ${unloadFinishYard}</div>
		<div class="event-info__pallFactYard">Зарегистрировано паллет: ${pallFactYard}</div>
		<div class="event-info__weightFactYard">Масса груза: ${weightFactYard}</div>
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


// функция обработки ошибок от сервера
export function customErrorCallback(info, data, method) {
	if (method === 'load' || method === 'update') info.revert()
	if (data.status === '100') {
		const errorMessage = data.message
			? data.message + '. Обновите страницу!'
			: userMessages.actionNotCompleted
		snackbar.show(errorMessage)
	} else {
		snackbar.show(userMessages.actionNotCompleted)
	}
}


// отображение модального окна обновления страницы
export function showReloadWindowModal() {
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
	messageContainer.innerText = message
	$('#displayMessageModal').modal('show')
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
	setTimeout(() => {
		const container = calendar.el
		const letfHeaderToolbarContainer = container.querySelector('.fc-header-toolbar .fc-toolbar-chunk')
		const changeCalendarDateInput = document.createElement('input')
		changeCalendarDateInput.type = 'date'
		changeCalendarDateInput.classList.add('form-control', 'changeCalendarDate')
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
	}, 500)
}
