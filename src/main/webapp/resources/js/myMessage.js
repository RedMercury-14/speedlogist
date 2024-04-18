import { createToast, playNewToastSound } from './Toast.js'
import { wsHead } from './global.js'
import { getData } from './utils.js'

const token = $("meta[name='_csrf']").attr("content")
const role = document.querySelector('input[id=role]').value
const login = document.querySelector('#login').value

const myMessageKeys = ['international', login]

if (role === '[ROLE_CARRIER]') {
	wsHead.addEventListener('message', socketMessageHandler)
	showUnreadMessages()
}

// показывает сообщения от текущего логина
async function showUnreadMessages() {
	if (!login) return

	const unreadMessages = await getData(`/speedlogist/api/mainchat/messagesList&${login}`)

	if (!unreadMessages || unreadMessages.length === 0) return

	// const actualMessages = getActualMessages(unreadMessages)
	unreadMessages
		.sort(sortByMessageDate)
		.map(addIdAndComment)
		.forEach(message => createToast(token, message))
}

// обработчик сообщений от сервера для текущего логина и всех international
// обработчик должен отображать новые сообщения и звуковой сигнал
function socketMessageHandler(e) {
	const message = JSON.parse(e.data)
	const toUser = message.toUser

	if (!toUser || !myMessageKeys.includes(toUser)) return

	const idMessage = message.datetime.replace(/[^\d]/g, '')
	message.idMessage || (message.idMessage = idMessage)
	message.comment = login

	createToast(token, message)
	playNewToastSound()
}

function getActualMessages(messages) {
	const actualMessages = []

	const groupedMessages = messages.reduce((acc, message, i) => {
		const { text } = message
		const groupeName = text.trim().slice(0,10) + text.trim().slice(-5)
		acc[groupeName] = acc[groupeName] || []
		acc[groupeName].push(message)
		return acc
	}, {})

	for (const key in groupedMessages) {
		const sortedMessages = groupedMessages[key].sort(sortByMessageDate)
		actualMessages.push(sortedMessages[0])
	}

	return actualMessages.sort(sortByMessageDate)
}

function sortByMessageDate(a, b) {
	const dateA = getMessageDate(a.datetime)
	const dateB = getMessageDate(b.datetime)
	return dateB - dateA
}

function getMessageDate(str) {
	const [date, time] = str.split('; ')
	const reverseDate = date.split('-').reverse().join('-')
	return new Date(reverseDate + '; ' + time)
}

function addIdAndComment(message, i) {
	return {
		...message,
		idMessage: message.idMessage ? message.idMessage : i + 1,
		comment: login,
	}
}
