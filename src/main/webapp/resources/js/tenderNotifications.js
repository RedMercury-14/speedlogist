import { createToast, playNewToastSound } from './Toast.js'
import { wsTenderMessagesUrl } from './global.js'
import { getNewTenderNotificationFlagUrl } from './globalConstants/urls.js'
import { getData, SmartWebSocket } from './utils.js'

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('input[id=role]').value
const login = document.querySelector('#login').value

let newTenderNotificationFlag = false

if (role === '[ROLE_CARRIER]') {
	new SmartWebSocket(`${wsTenderMessagesUrl}?user=${encodeURIComponent(login)}`, {
		reconnectInterval: 5000,
		maxReconnectAttempts: 5,
		onMessage: socketMessageHandler,
		onClose: () => alert('Соединение с сервером потеряно. Перезагрузите страницу')
	})

	newTenderNotificationFlag = await getData(getNewTenderNotificationFlagUrl)
}

// обработчик сообщений от сервера для текущего логина и всех international
// обработчик должен отображать новые сообщения и звуковой сигнал
function socketMessageHandler(e) {
	const data = JSON.parse(e.data)

	if (data.status === '200') {
		if (data.wspath !== 'carrier-tenders') return

		const action = data.action

		// уведомления перевозчикам
		if (action === 'notification' || action === 'new-tender') {

			if (action === 'new-tender' && !newTenderNotificationFlag) return

			const toastOption = {
				date: new Date().getTime(),
				toUser: data.toUser,
				text: data.text,
				url: data.url,
				autoCloseTime: 10000
			}
		
			createToast(toastOption)
			playNewToastSound()
		}
	}
}
