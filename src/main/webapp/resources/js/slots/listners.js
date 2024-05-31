import { addNewStockOption } from "./calendarUtils.js"
import { addOnClickToMenuItemListner, closeSidebar } from "./sidebar.js"

export function slotInfoListners() {
	const eventInfoBtn = document.querySelector('#eventInfoBtn')
	const yardInfoBtn = document.querySelector('#yardInfoBtn')
	const eventInfo = document.querySelector('#eventInfo')
	const yardInfo = document.querySelector('#yardInfo')

	eventInfoBtn.addEventListener('click', (e) => {
		eventInfo.classList.remove('none')
		yardInfo.classList.add('none')
	})
	yardInfoBtn.addEventListener('click', (e) => {
		yardInfo.classList.remove('none')
		eventInfo.classList.add('none')
	})
}

export function eventInfoModalClosedListner() {
	$('#eventInfoModal').on('hidden.bs.modal', (e) => {
		const eventInfoBtn = document.querySelector('#eventInfoBtn')
		const yardInfoBtn = document.querySelector('#yardInfoBtn')
		const eventInfo = document.querySelector('#eventInfo')
		const yardInfo = document.querySelector('#yardInfo')

		yardInfoBtn.parentElement.classList.remove('active')
		eventInfoBtn.parentElement.classList.add('active')
		eventInfo.classList.remove('none')
		yardInfo.classList.add('none')
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