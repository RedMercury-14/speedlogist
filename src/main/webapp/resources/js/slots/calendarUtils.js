import { snackbar } from "../snackbar/snackbar.js"
import { Draggable, eventColors, userMessages } from "./constants.js"
import { convertToDDMMYYYY, convertToDayMonthTime, getEventBGColor, getSlotStatus } from "./dataUtils.js"
import { editableRulesToConfirmBtn, isAnotherUser } from "./rules.js"

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
			? getEventBGColor(7) : getEventBGColor(order.status)

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
			<div class="single-slot__id">Номер из Маркета: ${order.marketNumber}</div>
			<div class="single-slot__id">Дата доставки: ${dateDelivery}</div>
			<div class="single-slot__cargo">Контрагент: ${order.counterparty}</div>
			<div class="single-slot__price">Груз: ${order.cargo}</div>
			<div class="single-slot__duration">Длительность выгрузки: ${h} ч ${m} мин</div>
		</div>
	`
}

// функция создания контента ивента календаря
export function createEventElement(info) {
	const eventElem = document.createElement('div')
	const { event: fcEvent, timeText } = info
	const { id, title, extendedProps } = fcEvent

	eventElem.className = 'fc-event-main-frame'
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

	const tooltip = new bootstrap.Tooltip(element, {
		delay: { "show": 700, "hide": 100 },
		html: true,
		title: tooltipHtml,
	})
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
	const confirmSlotBtn = document.querySelector('#confirmSlot')
	eventInfo.innerHTML = createEventInfoHTML(fcEvent)
	confirmSlotBtn.innerText = text
	confirmSlotBtn.className = isNotAnimated ? 'btn btn-secondary' : 'btn btn-secondary animation__small-pulse'
	confirmSlotBtn.dataset.action = action
	confirmSlotBtn.disabled = !editableRulesToConfirmBtn(data, currentLogin, currentRole)

	$('#eventInfoModal').modal('show')
}
export function hideEventInfoPopup() {
	$('#eventInfoModal').modal('hide')
}

// функция создания контента модального окна с информацией об ивенте
function createEventInfoHTML(fcEvent) {
	const order = fcEvent.extendedProps.data
	const { marketNumber, dateDelivery, timeUnload, cargo, counterparty, loginManager, idRamp, idOrder, status } = order
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
		<div class="event-info__ramp">
			Склад: ${stock}
			Рампа: ${ramp}
		</div>
		<div class="event-info__id">Начало выгрузки: ${eventStartDate}</div>
		<div class="event-info__duration">Длительность выгрузки: ${h} ч ${m} мин</div>
		<div class="event-info__id">ID заявки: ${idOrder}</div>
		<div class="event-info__marketNumber">Номер из Маркета: ${marketNumber}</div>
		<div class="event-info__dateDelivery">Дата доставки: ${dateDeliveryView}</div>
		<div class="event-info__cargo">Контрагент: ${counterparty}</div>
		<div class="event-info__price">Груз: ${cargo}</div>
		<div class="event-info__manager">Менеджер: ${loginManager}</div>
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
	const pallCountElem = document.querySelector('#pallCount')
	const maxPallElem = document.querySelector('#maxPall')

	if (!pallCountElem || !maxPallElem) {
		return
	}

	const currentMaxPall = maxPall ? maxPall : Number(maxPallElem.innerText)

	if (pallCount || pallCount === 0) {
		pallCountElem.innerText = pallCount
	}

	if (maxPall || pallCount === 0) {
		maxPallElem.innerText = maxPall
	}

	changePallCountElemColor(pallCountElem, pallCount, currentMaxPall)
}
export function updatePallInfo(numberOfPalls, action) {
	const pallCountElem = document.querySelector('#pallCount')
	const maxPallElem = document.querySelector('#maxPall')

	if (!pallCountElem || !maxPallElem) {
		return
	}

	const maxPall = Number(maxPallElem.innerText)
	const currentPallCount = Number(pallCountElem.innerText)

	let newPallCount

	if (action === 'increment') {
		newPallCount = currentPallCount + numberOfPalls
	} else if (action === 'decrement') {
		newPallCount = currentPallCount - numberOfPalls
	} else return

	pallCountElem.innerText = newPallCount

	changePallCountElemColor(pallCountElem, newPallCount, maxPall)
}
function changePallCountElemColor(pallCountElem, pallCount, maxPall) {
	const factor = pallCount / maxPall

	if (factor < 0.5) {
		pallCountElem.className = 'text-success'
	}
	if (factor >= 0.5) {
		pallCountElem.className = 'text-warning'
	}
	if (factor >= 1) {
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
