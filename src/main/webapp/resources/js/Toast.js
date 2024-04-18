import { ajaxUtils } from "./ajaxUtils.js"

const audio = new Audio('/speedlogist/resources/audio/notification.mp3')

export function createToast(token, message) {
	const container = document.querySelector('#toasts')
	const toast = document.createElement('div')
	const id = message.idMessage
	const dateTime = getDateTimeToView(message.datetime)
	const text = message.url ? `${message.text} <a href="${message.url}">Перейти</a>` : message.text

	toast.className = ('toast fade show')
	toast.id = id
	toast.setAttribute('role', 'alert')
	toast.setAttribute('aria-live', 'assertive')
	toast.setAttribute('aria-atomic', 'true')
	toast.setAttribute('data-autohide', 'false')

	toast.innerHTML = createToastHtml('Непрочитанное сообщение', dateTime, text)

	addCloseListener(toast)
	addClickLinkListner(toast, token, message)

	container && container.appendChild(toast)

	// запись сообщения в базу данных при закрытии окна сообщения
	$(`#${id}`).on('hidden.bs.toast', () => saveMessage(token, message))

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
function addClickLinkListner(toast, token, message) {
	const link = toast.querySelector('a')
	link && link.addEventListener('click', (e) => {
		e.preventDefault()

		// запись сообщения в базу данных
		saveMessage(token, message)

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
		url: "/speedlogist/api/mainchat/massage/add",
		token: token,
		data: message
	})
}

function getDateTimeToView(dateTime) {
	const [ date, time ] = dateTime.split('; ')
	const reverseDate = date.split('-').reverse().join('-')
	const dateObj = new Date(reverseDate)
	dateObj.setSeconds(1)

	const now = new Date()
	now.setHours(0)
	now.setMinutes(0)
	now.setSeconds(0)

	return dateObj < now ? dateTime : time
}
