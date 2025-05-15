import { ajaxUtils } from "./ajaxUtils.js"
import { saveMainchatMessageUrl } from "./globalConstants/urls.js"
import { dateHelper } from "./utils.js"

const audio = new Audio('/speedlogist/resources/audio/notification.mp3')

export function createToast(option) {
	const container = document.querySelector('#toasts')
	const toast = document.createElement('div')
	const dateTime = getDateTimeToView(option.date)
	const messageBoby = option.url ? `${option.text} <a href="${option.url}">Перейти</a>` : option.text
	const title = option.title || 'Уведомление'
	const autoCloseTime = option.autoCloseTime

	toast.id = `${option.date}`
	toast.className = ('toast fade show')
	toast.setAttribute('role', 'alert')
	toast.setAttribute('aria-live', 'assertive')
	toast.setAttribute('aria-atomic', 'true')
	toast.setAttribute('data-autohide', 'true')
	toast.setAttribute('data-animation', 'true')
	toast.setAttribute('data-delay', `${autoCloseTime}`)

	toast.innerHTML = createToastHtml(title, dateTime, messageBoby)

	addCloseListener(toast)
	addClickLinkListner(toast)

	container && container.appendChild(toast)

	// запись сообщения в базу данных при закрытии окна сообщения
	// $(`#${id}`).on('hidden.bs.toast', () => saveMessage(token, option))

	// автозакрытие сообщения
	autoCloseTime && setTimeout(() => {
		$(`#${toast.id}`).toast('hide')
	}, autoCloseTime)

	return toast
}

// проигрывание звука для нового сообщения
export function playNewToastSound() {
	audio.play()
}

// формирование html для контейнера сообщения
function createToastHtml(title, dateTime, text) {
	return `
		<div class="toast-header">
			<div class="rounded mr-2" style="width: 20px; height: 20px; background-color: #0e377b;"></div>
			<strong class="mr-auto">${title}</strong>
			<small class="pl-1">${dateTime}</small>
			<button type="button" class="ml-2 mb-1 close" data-dismiss="toast" aria-label="Закрыть">
				<span aria-hidden="true">&times;</span>
			</button>
		</div>
		<div class="toast-body">${text}</div>
	`
}

// добавление обработчика для кнопку закрытия сообщения
function addCloseListener(toast) {
	const closeBtn = toast.querySelector('.close')
	closeBtn && closeBtn.addEventListener('click', (e) => {
		$(`#${toast.id}`).toast('hide')
	})
}

// добавление обработчика для ссылки в сообщении и запись ее в базу данных для
function addClickLinkListner(toast) {
	const link = toast.querySelector('a')
	link && link.addEventListener('click', (e) => {
		e.preventDefault()

		// запись сообщения в базу данных
		// saveMessage(token, message)

		// переход по ссылке через 200 мс
		setTimeout(() => {
			window.open(e.target.href, '_self')
		}, 200)
	})
}

// запись сообщения в базу данных
function saveMessage(token, message) {
	delete message.idMessage
	ajaxUtils.postJSONdata({
		url: saveMainchatMessageUrl,
		token: token,
		data: message
	})
}

function getDateTimeToView(dateTime) {
	const dateObj = new Date(dateTime)
	return dateHelper.getFormatDateTime(dateObj)
}
