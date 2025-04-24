import { snackbar } from "./snackbar/snackbar.js"

const PBI_DATA = {
	rc: {
		title: "Нехватка товара на РЦ",
		src: "https://app.powerbi.com/reportEmbed?reportId=df49078e-9c34-4073-ac9b-7a1e61eceadf&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108"
	},
	logistic: {
		title: 'Аналитика Биржи',
		src: 'https://app.powerbi.com/reportEmbed?reportId=36af3ce2-6396-414b-bf00-ac156158500b&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108',
	},
	slots: {
		title: 'Аналитика нулей',
		src: 'https://app.powerbi.com/reportEmbed?reportId=3880d301-7fd1-4114-a6ac-8a3821046315&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108',
	},
	zero: {
		title: 'Изменения матрицы',
		src: 'https://app.powerbi.com/reportEmbed?reportId=5f56609f-d984-4cf5-a5ed-53d8eb4f2e03&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108',
	},
	changeMatrix: {
		title: 'Аналитика Слотов',
		src: 'https://app.powerbi.com/reportEmbed?reportId=4f272d07-3afa-43ad-ad2a-eee2a38bb656&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108',
	},
	serviceLvl: {
		title: 'Сервис Lvl',
		src: 'https://app.powerbi.com/reportEmbed?reportId=36fdd992-a3dd-4e0f-88a9-d60614aa1f89&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108',
	},
}


document.addEventListener('DOMContentLoaded', () => {
	const queryParams = getQueryParams()
	const page = queryParams.pageName
	const pageData = PBI_DATA[page]

	changePage(pageData)

	const pbLoginSpan = document.querySelector('#pbLogin')
	const pbPassSpan = document.querySelector('#pbPass')
	const copyLoginBtn = document.querySelector('#copyPBLogin')
	const copyPassBtn = document.querySelector('#copyPBPass')

	copyLoginBtn.onclick = () => copyTextToClipboard(pbLoginSpan.innerText)
	copyPassBtn.onclick = () => copyTextToClipboard(pbPassSpan.innerText)
})


function changePage(pageData) {
	if (!pageData) return
	const pageTitle = document.getElementById('pageTitle')
	const frameContainer = document.getElementById('frameContainer')

	pageTitle.innerText = pageData.title
	frameContainer.innerHTML = ''

	renderAnaliticFrame(pageData)
}

function renderAnaliticFrame(pageData) {
	const frameContainer = document.getElementById('frameContainer')
	const analiticFrame = document.createElement('iframe')

	const vh = Math.max(document.documentElement.clientHeight || 0, window.innerHeight || 0)
	const frameHight = vh - 155

	analiticFrame.width = '100%'
	analiticFrame.height = `${frameHight}px`
	analiticFrame.frameborder = '0'
	analiticFrame.allowFullScreen = 'true'
	analiticFrame.title = pageData.title
	analiticFrame.src = pageData.src

	frameContainer.append(analiticFrame)
}

function getQueryParams() {
	return new Proxy(
		new URLSearchParams(window.location.search),
		{ get: (searchParams, prop) => searchParams.get(prop), }
	)
}

function copyTextToClipboard(text) {
	navigator.clipboard.writeText(text)
		.then(() => {
			snackbar.show('Скопировано')
		})
		.catch(err => {
			snackbar.show('Ошибка копирования')
			console.error('Ошибка копирования: ', err)
		})
}