import { ws, wsTenderMessagesUrl } from './global.js';
import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { cookieHelper, dateHelper, debounce, getData, isMobileDevice, SmartWebSocket } from './utils.js';
import { dateComparator, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js';
import { getActiveTendersUrl, getInfoRouteMessageBaseUrl, getThisUserUrl } from './globalConstants/urls.js';

const LOCAL_STORAGE_KEY = 'tenders_page'

const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'activRow': (params) => {
		if (params.data.price && params.data.myOffer) {
			const priceValue = Number(params.data.price.split(' ')[0])
			const myOfferValue = Number(params.data.myOffer.split(' ')[0])
			return priceValue === myOfferValue
		}
		return false
	},
	'attentionRow': (params) => {
		if (params.data.price && params.data.myOffer) {
			const priceValue = Number(params.data.price.split(' ')[0])
			const myOfferValue = Number(params.data.myOffer.split(' ')[0])
			return priceValue < myOfferValue
		}
		return false
	}
}

const isMobileView = isMobileDevice() || (window.innerWidth < 768)

const columnDefsForMobile = [
	{ 
		headerName: "Название тендера", field: "routeDirection",
		cellClass: 'px-0',
		wrapText: true, autoHeight: true,
		flex: 6, minWidth: 260,
		cellRenderer: 'agGroupCellRenderer',
		cellRendererParams: {
			innerRenderer: routeLinkRenderer,
		},
		getQuickFilterText: params => params.value
	},
	{ headerName: "Направление", field: "way", cellClass: 'px-1 font-weight-bold text-center', minWidth: 63, },
	{
		headerName: "Загрузка", field: 'loadDateToView',
		cellClass: 'px-0 text-center', minWidth: 80,
		comparator: dateComparator,
	},
	{
		headerName: "Выгрузка", field: 'unloadDateToView',
		cellClass: 'px-1 text-center', minWidth: 80,
		comparator: dateComparator,
	},
	{ 
		headerName: "Актуальная цена", field: "price",
		cellClass: 'px-1 font-weight-bold text-center',
		minWidth: 90,
		wrapText: true, autoHeight: true,
	},
	{ 
		headerName: "Ваше предложение", field: "myOffer",
		cellClass: 'px-1 font-weight-bold text-center',
		minWidth: 90,
		wrapText: true, autoHeight: true,
	}
]
const columnDefsForPC = [
	{ 
		headerName: "Название тендера", field: "routeDirection",
		flex: 6, minWidth: 260,
		cellRenderer: routeLinkRenderer,
		getQuickFilterText: params => params.value
	},
	{
		headerName: "Загрузка", field: 'loadDateToView',
		cellClass: 'px-2 text-center', minWidth: 80,
		comparator: dateComparator,
	},
	{ headerName: "Направление", field: "way", cellClass: 'px-2 font-weight-bold text-center', minWidth: 63, },
	// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
	// {
	// 	headerName: "На понижение", field: "forReduction",
	// 	cellClass: 'px-2 text-center',
	// 	minWidth: 85, cellDataType: false,
	// 	valueFormatter: (params) => params.value ? "Да" : "Нет",
	// 	filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
	// },
	{ headerName: "Дата загрузки", field: 'loadDateTimeToView', flex: 1, minWidth: 85, },
	{ headerName: "Дата выгрузки", field: 'unloadDateTimeToView', flex: 1, minWidth: 85, },
	{
		headerName: "Машина", field: 'carInfo',
		flex: 1, minWidth: 160,
	},
	{
		headerName: "Груз", field: 'cargoInfo',
		flex: 2, minWidth: 160,
	},
	{ 
		headerName: "Актуальная цена", field: "price",
		cellClass: 'px-2 font-weight-bold text-center',
		minWidth: 60,
	},
	{ 
		headerName: "Ваше предложение", field: "myOffer",
		cellClass: 'px-2 font-weight-bold text-center',
		minWidth: 60,
	},

]
const columnDefs = isMobileView ? columnDefsForMobile : columnDefsForPC
const detailCellRendererParams = {
	detailGridOptions: {
		columnDefs: [
			{ headerName: "Дата загрузки", field: 'loadDateTimeToView', flex: 2, minWidth: 85, },
			{ headerName: "Дата выгрузки", field: 'unloadDateTimeToView', flex: 2, minWidth: 85, },
			// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
			// {
			// 	headerName: "На понижение", field: "forReduction",
			// 	cellClass: 'px-2 text-center',
			// 	minWidth: 85, cellDataType: false,
			// 	valueFormatter: (params) => params.value ? "Да" : "Нет",
			// 	filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
			// },
			{
				headerName: "Машина", field: 'carInfo',
				flex: 2, minWidth: 160,
			},
			{
				headerName: "Груз", field: 'cargoInfo',
				wrapText: true, autoHeight: true,
				flex: 2, minWidth: 160,
			},
		],
		defaultColDef: {
			cellClass: 'px-1',
			wrapText: true,
			autoHeight: true,
			resizable: true,
			flex: 1,
			suppressMenu: true,
			wrapHeaderText: true,
			autoHeaderHeight: true,
		},
		suppressContextMenu: true,
		suppressDragLeaveHidesColumns: true,
		enableBrowserTooltips: true,
		localeText: AG_GRID_LOCALE_RU,
	},
	getDetailRowData: (params) => {
		params.successCallback([params.data]);
	},
}
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	headerHeight: 35,
	floatingFiltersHeight: 35,
	defaultColDef: {
		headerClass: 'px-1',
		cellClass: 'px-2',
		flex: 1,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
		wrapText: true, autoHeight: true,
	},
	onFilterChanged: debouncedSaveFilterState,
	suppressContextMenu: true,
	suppressRowClickSelection: true,
	suppressRowHoverHighlight: isMobileView,
	suppressMovableColumns: true,
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	masterDetail: isMobileView,
	detailRowHeight: 230,
	detailCellRendererParams: detailCellRendererParams
}

window.onload = async () => {
	const goTGBotBtn = document.querySelector('#goTGBotBtn')
	const resetTableFiltersBtn = document.querySelector('#resetTableFilters')

	const gridDiv = document.querySelector('#myGrid')
	const filterTextBox = document.querySelector('#filterTextBox')
	const tenders = await getData(getActiveTendersUrl)
	// ТЕНДЕРЫ НА ПОНИЖЕНИЕ
	// const tendersData = await getData(getActiveTendersUrl)
	// const tenders = tendersData.routes
	const myMessages = await getData(getInfoRouteMessageBaseUrl + 'from_me')
	const user = await getData(getThisUserUrl)

	await renderTable(gridDiv, gridOptions, tenders, myMessages, user)

	restoreFilterState()

	filterTextBox.addEventListener('input', (e) => {
		gridOptions.api.setQuickFilter(e.target.value)
	})

	goTGBotBtn.addEventListener('click', goTGBotBtnClickHandler)
	resetTableFiltersBtn.addEventListener('click', resetFilterState)

	// const forReductionSocket = new SmartWebSocket(wsTenderMessagesUrl, {
	// 	reconnectInterval: 5000,
	// 	maxReconnectAttempts: 5,
	// 	onMessage: forReductionSocketOnMessage,
	// 	onClose: () => alert('Соединение с сервером потеряно. Перезагрузите страницу')
	// })


	tgBotModal()

	ws.onmessage = (e) => onMessage(JSON.parse(e.data))

	function onMessage(msg) {
		if (msg.currency) {
			// обновляем таблицу только на свои сообщения
			const userUnp = user.numYNP
			if (msg.ynp === userUnp) updateTable(gridOptions, tenders, myMessages, user)
		}
	}
}

async function renderTable(gridDiv, gridOptions, data, messages, user) {
	new agGrid.Grid(gridDiv, gridOptions)

	if (!data || !data.length) {
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = await getMappingData(data, messages, user)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
async function updateTable(gridOptions, data, messages, user) {
	const mappingData = await getMappingData(data, messages, user)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
	!data.length && gridOptions.api.showNoRowsOverlay()
}

async function getMappingData(data, messages, user) {
	return await Promise.all(data.map( async (tender) => {
		const idRoute = tender.idRoute
		const rhsItem = tender.roteHasShop[0]
		const cargo = rhsItem && rhsItem.cargo ? rhsItem.cargo : ''
		const temp = tender.temperature ? `${tender.temperature} °C; ` : ''
		const vol = rhsItem && rhsItem.volume ? `${rhsItem.volume} м³` : ''
		const loadDateToView = tender.dateLoadPreviously ? tender.dateLoadPreviously.split('-').reverse().join('.') : ''
		const loadDateTimeToView = `${loadDateToView},  ${tender.timeLoadPreviously}`
		const myMessage = messages.find(m => m.idRoute === idRoute.toString()) || null

		const loadPoints = tender.roteHasShop
			.filter(point => point.position === "Загрузка")
			.map((point, i) => `${i + 1}) ${point.address}`)
			.join(' ')
		const unloadPoints = tender.roteHasShop
			.filter(point => point.position === "Выгрузка")
			.map((point, i) => `${i + 1}) ${point.address}`)
			.join(' ')

		const routeMessages = await getData(getInfoRouteMessageBaseUrl + idRoute)

		const price = getPrice(routeMessages, user, tender)
		const myOffer = getMyOffer(routeMessages, user, myMessage, tender)
		const carInfo = getCarInfo(tender)
		const cargoInfo = getCargoInfo(tender)
		const unloadDateToView = getUnloadDateToView(tender)
		const unloadDateTimeToView = getUnloadDateTimeToView(tender)

		return {
			...tender,
			loadDateToView,
			loadDateTimeToView,
			cargo,
			myMessage,
			myOffer,
			price,
			loadPoints,
			unloadPoints,
			cargoInfo,
			carInfo,
			unloadDateToView,
			unloadDateTimeToView,
		}
	}))
}

function routeLinkRenderer(params) {
	const dateReg = /\[(\d{1}|\d{2})-\d{2}-\d{4}\]/g
	
	let tenderName = params.value
	if (!dateReg.test(params.value)) {
		const index = params.value.lastIndexOf('N')
		const firstPart = params.value.slice(0, index)
		const secondPart = `[${params.data.simpleDateStart}] `
		const thirdPart = params.value.slice(index, params.value.length)

		tenderName = firstPart + secondPart + thirdPart
	}
	
	const idRoute = params.data.idRoute
	const link = `/speedlogist/main/carrier/tender/tenderpage?routeId=${idRoute}`
	return `<a class="text-primary" href="${link}">${tenderName}</a>`
}
function textInColumnRenderer(params) {
	const array = params.value.split('; ')
	return `
		<div class="d-flex flex-column pb-2">
			<span style="height: 30px;">${array[0]}</span>
			<span style="height: 30px;">${array[1] || ''}</span>
			<span style="height: 30px;">${array[2] || ''}</span>
		</div>
	`
}

// действия на сообщения от сокета тендеров
function forReductionSocketOnMessage(e) {
	console.log(e)

	const data = JSON.parse(e.data)
	console.log("🚀 ~ forReductionSocketOnMessage ~ data:", data)
}

// функции для модального окна ТГ бота
function tgBotModal() {
	const value = cookieHelper.getCookie('_speedLogistBot')

	if (value) return 

	setSpeedLogistBotCookie('ок')
	$('#TGBotModal').modal('show')
}
function goTGBotBtnClickHandler(e) {
	const link = document.createElement('a');
	link.target = '_blank'
	link.href = 'http://t.me/speedlogist_bot'
	document.body.appendChild(link)
	link.click()
	document.body.removeChild(link)

	$('#TGBotModal').modal('hide')
}
function setSpeedLogistBotCookie(value) {
	let date = new Date(Date.now() + 31562e7)
	date = date.toUTCString()
	cookieHelper.setCookie('_speedLogistBot', value, { expires: date, })
}

// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}
function resetFilterState() {
	gridFilterLocalState.resetState(gridOptions, LOCAL_STORAGE_KEY)
}

function getPrice(routeMessages, user, tender) {
	const forReductionTender = tender.forReduction

	if (forReductionTender) {
		const offers = tender.carrierBids
		if (offers.length === 0) {
			return tender.startPriceForReduction && tender.currencyForReduction
				? `${tender.startPriceForReduction} ${tender.currencyForReduction}` : ''
		}
		const actualOffer = offers.sort((a, b) => b.idCarrierBid - a.idCarrierBid)[0]
		return `${actualOffer.price} ${actualOffer.currency}`
	}

	// return routeMessages && routeMessages.length !== 0
	// 	? `${routeMessages[0].text} ${routeMessages[0].currency}`
	// 	: tender.startPrice
	// 		? `${tender.startPrice} BYN` : ''
	
	return ''
}
function getMyOffer(routeMessages, user, myMessage, tender) {
	const userUnp = user.numYNP
	const userId = user.idUser
	const forReductionTender = tender.forReduction

	if (forReductionTender) {
		const offers = tender.carrierBids
		if (offers.length === 0) return ''
		const myOffer = offers.filter(offer => offer.idUser === userId)[0]
		return myOffer ? `${myOffer.price} ${myOffer.currency}` : ''
	}

	return routeMessages && routeMessages.length !== 0 && routeMessages[0].ynp === userUnp
		? `${routeMessages[0].text} ${routeMessages[0].currency}`
		: myMessage
			? `${myMessage.text} ${myMessage.currency}` : ''
}
function getCargoInfo(tender) {
	if (!tender) return ''
	const rhsItem = tender.roteHasShop[0]
	const cargo = rhsItem && rhsItem.cargo ? rhsItem.cargo : ''
	const totalLoadPall = tender.totalLoadPall ? `${tender.totalLoadPall} палл` : ''
	const totalCargoWeight = tender.totalCargoWeight ? `${tender.totalCargoWeight} кг` : ''
	const volume = rhsItem && rhsItem.volume ? `${rhsItem.volume} м³` : ''
	return [ cargo, totalLoadPall, totalCargoWeight, volume ].filter(item => item).join(' ● ')
}
function getCarInfo(tender) {
	if (!tender) return ''
	const typeTrailer = tender.typeTrailer ? tender.typeTrailer : ''
	const temp = tender.temperature ? `${tender.temperature} °C; ` : ''
	return [ typeTrailer, temp ].filter(item => item).join(' ● ')
}
function getUnloadDateToView(tender) {
	if (!tender) return ''
	const slotDate = tender.dateUnloadPreviouslyStock ? dateHelper.changeFormatToView(tender.dateUnloadPreviouslyStock) : ''
	const dateUnloadActuallySimple = tender.dateUnloadActuallySimple ? tender.dateUnloadActuallySimple : ''

	if (slotDate) return slotDate
	if (dateUnloadActuallySimple) return dateUnloadActuallySimple
	return ''
}
function getUnloadDateTimeToView(tender) {
	if (!tender) return ''
	const slotDate = tender.dateUnloadPreviouslyStock ? dateHelper.changeFormatToView(tender.dateUnloadPreviouslyStock) : ''
	const slotTime = tender.timeUnloadPreviouslyStock ? tender.timeUnloadPreviouslyStock.slice(0,5) : ''

	const dateUnloadActuallySimple = tender.dateUnloadActuallySimple ? tender.dateUnloadActuallySimple : ''
	const timeUnloadActually = tender.timeUnloadActually ? tender.timeUnloadActually.replace('-', ':') : ''

	if (slotDate) {
		return [ slotDate, slotTime ].filter(item => item).join(', ')
	}

	if (dateUnloadActuallySimple) {
		return [ dateUnloadActuallySimple, timeUnloadActually ].filter(item => item).join(', ')
	}

	return ''
}