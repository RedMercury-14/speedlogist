import { createToast, playNewToastSound } from './Toast.js'
import { wsTenderMessagesUrl } from './global.js'
import { SmartWebSocket } from './utils.js'

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('input[id=role]').value
const login = document.querySelector('#login').value

if (role === '[ROLE_CARRIER]') {
	new SmartWebSocket(`${wsTenderMessagesUrl}?user=${encodeURIComponent(login)}`, {
		reconnectInterval: 5000,
		maxReconnectAttempts: 5,
		onMessage: socketMessageHandler,
		onClose: () => alert('Соединение с сервером потеряно. Перезагрузите страницу')
	})
}

// обработчик сообщений от сервера для текущего логина и всех international
// обработчик должен отображать новые сообщения и звуковой сигнал
function socketMessageHandler(e) {
	const data = JSON.parse(e.data)

	if (data.status === '200') {
		if (data.wspath !== 'carrier-tenders') return

		const action = data.action

		// уведомления перевозчикам
		if (action === 'notification') {

			const toastOption = {
				date: new Date().getTime(),
				toUser: data.toUser,
				text: data.text,
				url: data.url,
				autoCloseTime: 7000
			}
		
			createToast(toastOption)
			playNewToastSound()
		}
	}
}
