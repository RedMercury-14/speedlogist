// -------------------------------------------------------------------------------//
// --------------------------- боковая панель ------------------------------------//
// -------------------------------------------------------------------------------//

export function addOnClickToMenuItemListner(item, customCallback) {
	item.addEventListener("click", (e) => {
		const target = e.target

		if (target.classList.contains("active-item") || !document.querySelector(".active-sidebar")) {
			document.body.classList.toggle("active-sidebar")
		}

		showContent(target.dataset.item)
		addRemoveActiveItem(target, "active-item")
		customCallback && customCallback(target)
	})
}

function addRemoveActiveItem(target, className) {
	const element = document.querySelector(`.${className}`)
	target.classList.add(className)
	if (!element) return
	element.classList.remove(className)
}

function showContent(dataContent) {
	const idItem = document.querySelector(`#${dataContent}`)
	addRemoveActiveItem(idItem, "active-content")
}

export function closeSidebar(customCallback) {
	document.body.classList.remove("active-sidebar")
	const element = document.querySelector(".active-item")
	const activeContent = document.querySelector(".active-content")
	if (!element) return
	element.classList.remove("active-item")
	activeContent.classList.remove("active-content")
	customCallback && customCallback(element)
}