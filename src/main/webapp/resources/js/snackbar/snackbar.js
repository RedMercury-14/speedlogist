// let lastTimeout;

function showSnackbar(message) {
	const snackbar = document.querySelector('#snackbar');
	if (snackbar == null) {
		console.warn("Element not found", "Не найден элемент с идентификатором snackbar");
		
	} else {
		if (snackbar.classList.contains("show")) {
			clearTimeout(lastTimeout);
			snackbar.className = snackbar.className.replace("show", "");
		}
		snackbar.innerHTML = message;
		snackbar.classList.add("show");
		lastTimeout = setTimeout(function() { snackbar.className = snackbar.className.replace("show", ""); }, 4000);
	}

}

export const snackbar = {
	lastTimeout: 0,
	show(message) {
		const snackbar = document.querySelector('#snackbar');
		if (snackbar == null) {
			console.warn("Element not found", "Не найден элемент с идентификатором snackbar");
			
		} else {
			if (snackbar.classList.contains("show")) {
				clearTimeout(this.lastTimeout);
				snackbar.className = snackbar.className.replace("show", "");
			}
			snackbar.innerHTML = message;
			snackbar.classList.add("show");
			this.lastTimeout = setTimeout(function() { snackbar.className = snackbar.className.replace("show", ""); }, 4000);
		}
	}
}