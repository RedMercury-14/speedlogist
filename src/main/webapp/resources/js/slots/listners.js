import { createOptions } from "../deliverySchedule/utils.js"
import { reasonForUpdate } from "../globalRules/slotsRules.js"
import { addNewStockOption } from "./calendarUtils.js"
import { addOnClickToMenuItemListner, closeSidebar } from "./sidebar.js"

export function slotInfoListners() {
	const eventInfoBtns = document.querySelector('#eventInfoBtns')
	const eventInfo = document.querySelector('#eventInfo')
	const routesInfo = document.querySelector('#routesInfo')
	const yardInfo = document.querySelector('#yardInfo')

	eventInfoBtns.addEventListener('click', (e) => {
		const btn = e.target
		const btnId = btn.id

		if (btnId === 'eventInfoBtn') {
			eventInfo.classList.remove('none')
			routesInfo.classList.add('none')
			yardInfo.classList.add('none')
		}
		if (btnId === 'routesInfoBtn') {
			eventInfo.classList.add('none')
			routesInfo.classList.remove('none')
			yardInfo.classList.add('none')
		}
		if (btnId === 'yardInfoBtn') {
			eventInfo.classList.add('none')
			routesInfo.classList.add('none')
			yardInfo.classList.remove('none')
		}
	})
}

export function eventInfoModalClosedListner() {
	$('#eventInfoModal').on('hidden.bs.modal', (e) => {
		const eventInfoBtn = document.querySelector('#eventInfoBtn')
		const routesInfoBtn = document.querySelector('#routesInfoBtn')
		const yardInfoBtn = document.querySelector('#yardInfoBtn')
		const eventInfo = document.querySelector('#eventInfo')
		const routesInfo = document.querySelector('#routesInfo')
		const yardInfo = document.querySelector('#yardInfo')

		yardInfoBtn.parentElement.classList.remove('active')
		routesInfoBtn.parentElement.classList.remove('active')
		eventInfoBtn.parentElement.classList.add('active')
		eventInfo.classList.remove('none')
		yardInfo.classList.add('none')
		routesInfo.classList.add('none')
	})
}

export function sidebarListners() {
	const menuItems = document.querySelectorAll(".menu-item")
	const buttonClose = document.querySelector(".close-button")
	menuItems.forEach((item) => addOnClickToMenuItemListner(item))
	buttonClose.addEventListener("click", () => closeSidebar())
	document.addEventListener("keydown", (e) => (e.key === "Escape") && closeSidebar())
}

export function reloadBtnListner() {
	const reloadWindowButton = document.querySelector('#reloadWindowButton')
	reloadWindowButton.addEventListener('click', (e) => window.location.reload())
}

export function confitmSlotBtnListner(handler) {
	const confirmSlotBtn = document.querySelector('#confirmSlot')
	confirmSlotBtn.addEventListener('click', handler)
}

export function sendSlotToSupplierBtnListner(handler) {
	const sendSlotToSupplierBtn = document.querySelector('#sendSlotToSupplier')
	sendSlotToSupplierBtn.addEventListener('click', handler)
}

export function copySlotInfoBtnListner(handler) {
	const copySlotInfoBtn = document.querySelector('#copySlotInfo')
	copySlotInfoBtn.addEventListener('click', handler)
}

export function statusInfoLabelLIstners() {
	const statusInfoLabel = document.querySelector('#statusInfoLabel')
	const statusInfo = document.querySelector('#statusInfo')
	statusInfoLabel.addEventListener('mouseover', (e) => statusInfo.classList.add('show'))
	statusInfoLabel.addEventListener('mouseout', (e) => statusInfo.classList.remove('show'))
}

export function stockSelectListner(stocks, calendarInstance, handler) {
	const stockSelect = document.querySelector("#stockNumber")
	stockSelect.value = ''
	stocks.forEach(stock => addNewStockOption(stockSelect, stock))
	stockSelect.addEventListener("change", (e) => handler(e, calendarInstance))
}

export function addNewOrderBtnListner(eventContainer, handler) {
	const addNewOrderButton = document.querySelector("#addNewOrder")
	addNewOrderButton.addEventListener('click', (e) => handler(e, eventContainer))
}

export function slotSearchFormListner(handler) {
	const slotSearchForm = document.querySelector('#slotSearchForm')
	slotSearchForm.addEventListener('submit', (e) => handler(e))
}

export function adminActionListner(handler) {
	const adminActionSelect = document.querySelector('#adminAction')
	adminActionSelect && adminActionSelect.addEventListener('change', (e) => handler(e))
}

export function updateSlotReasonFormListner(handler) {
	const updateSlotReasonForm = document.querySelector('#updateSlotReasonForm')
	updateSlotReasonForm && updateSlotReasonForm.addEventListener('submit', (e) => handler(e))
}

export function updateSlotReasonSelectListner() {
	const updateSlotOtherReasonInput = document.querySelector('#updateSlotOtherReason')
	const updateSlotReasonSelect = document.querySelector('#updateSlotReason')

	createOptions(reasonForUpdate, updateSlotReasonSelect)

	updateSlotReasonSelect && updateSlotReasonSelect.addEventListener('change', (e) => {
		if (!updateSlotOtherReasonInput) return
		if (e.target.value === 'Иное') {
			updateSlotOtherReasonInput.parentElement.classList.remove('none')
			updateSlotOtherReasonInput.setAttribute('required', true)
		} else {
			updateSlotOtherReasonInput.parentElement.classList.add('none')
			updateSlotOtherReasonInput.removeAttribute('required')
		}
	})
}

export function updateSlotReasonCancelListner(handler) {
	const updateSlotReasonCancelBtn = document.querySelector('#updateSlotReasonCancel')
	updateSlotReasonCancelBtn && updateSlotReasonCancelBtn.addEventListener('click', handler)
}
