import { wsTenderMessagesUrl } from './global.js';
import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { cookieHelper, dateHelper, debounce, getData, isMobileDevice, SmartWebSocket } from './utils.js';
import { dateComparator, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js';
import { getActiveTendersUrl, getNewTenderNotificationFlagUrl, getThisUserUrl, setNewTenderNotificationFlagUrl } from './globalConstants/urls.js';
import { createToast, playNewToastSound } from './Toast.js';
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js';
import { ajaxUtils } from './ajaxUtils.js';
import { snackbar } from './snackbar/snackbar.js';

const login = document.querySelector('#login')?.value
let user
let newTenderNotificationFlag = false

const LOCAL_STORAGE_KEY = 'tenders_page'

const debouncedSaveFilterState = debounce(saveFilterState, 300)

const rowClassRules = {
	'activRow': (params) => params.data.myOffer,
	'purpleRow': (params) => {
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
	getRowId: (params) => params.data.idRoute,
	onFilterChanged: debouncedSaveFilterState,
	suppressContextMenu: true,
	suppressRowClickSelection: true,
	suppressRowHoverHighlight: isMobileView,
	suppressMovableColumns: true,
	suppressDragLeaveHidesColumns: true,
	enableBrowserTooltips: true,
	enableCellChangeFlash: true,
	localeText: AG_GRID_LOCALE_RU,
	masterDetail: isMobileView,
	detailRowHeight: 230,
	detailCellRendererParams: detailCellRendererParams,
	overlayNoRowsTemplate: '<span class="h3">Нет актуальных тендеров</span>',
}

document.addEventListener('DOMContentLoaded', async () => {
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	const tendersData = await getData(getActiveTendersUrl)
	const tenders = tendersData.routes

	newTenderNotificationFlag = await getData(getNewTenderNotificationFlagUrl)
	user = await getData(getThisUserUrl)
	await updateTable(gridOptions, tenders)

	const newTenderNotificationCheckbox = document.getElementById('newTenderNotification')
	newTenderNotificationCheckbox.checked = newTenderNotificationFlag
	newTenderNotificationCheckbox.addEventListener('change', onNewTenderCheckboxChange)

	restoreFilterState()

	const filterTextBox = document.querySelector('#filterTextBox')
	filterTextBox.addEventListener('input', (e) => {
		gridOptions.api.setQuickFilter(e.target.value)
	})

	const goTGBotBtn = document.querySelector('#goTGBotBtn')
	goTGBotBtn.addEventListener('click', goTGBotBtnClickHandler)

	const resetTableFiltersBtn = document.querySelector('#resetTableFilters')
	resetTableFiltersBtn.addEventListener('click', resetFilterState)

	// вэбсокет тендеров
	new SmartWebSocket(`${wsTenderMessagesUrl}?user=${login}`, {
		reconnectInterval: 5000,
		maxReconnectAttempts: 5,
		onMessage: tenderSocketOnMessage,
		onClose: () => alert('Соединение с сервером потеряно. Перезагрузите страницу')
	})

	tgBotModal()

	// ws.onmessage = (e) => onMessage(JSON.parse(e.data))

	// function onMessage(msg) {
	// 	if (msg.currency) {
	// 		// обновляем таблицу только на свои сообщения
	// 		const userUnp = user.numYNP
	// 		if (msg.ynp === userUnp) updateTable(gridOptions, tenders)
	// 	}
	// }
})

function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showLoadingOverlay()
}
async function updateTable(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = await getMappingData(data)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}

async function getMappingData(data) {
	return await Promise.all(data.map(tenderMapCallback))
}
async function tenderMapCallback(tender) {
	const loadDateToView = tender.dateLoadPreviously ? tender.dateLoadPreviously.split('-').reverse().join('.') : ''
	const loadDateTimeToView = `${loadDateToView},  ${tender.timeLoadPreviously}`

	const loadPoints = tender.roteHasShop
		.filter(point => point.position === "Загрузка")
		.map((point, i) => `${i + 1}) ${point.address}`)
		.join(' ')
	const unloadPoints = tender.roteHasShop
		.filter(point => point.position === "Выгрузка")
		.map((point, i) => `${i + 1}) ${point.address}`)
		.join(' ')

	const actualCarrierBids = tender.carrierBids.filter(o => o.status === 20)
	const price = getPrice(tender, actualCarrierBids)
	const myOffer = getMyOffer(user, actualCarrierBids)
	const carInfo = getCarInfo(tender)
	const cargoInfo = getCargoInfo(tender)
	const unloadDateToView = getUnloadDateToView(tender)
	const unloadDateTimeToView = getUnloadDateTimeToView(tender)

	return {
		...tender,
		loadDateToView,
		loadDateTimeToView,
		myOffer,
		price,
		loadPoints,
		unloadPoints,
		cargoInfo,
		carInfo,
		unloadDateToView,
		unloadDateTimeToView,
		actualCarrierBids,
	}
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
async function tenderSocketOnMessage(e) {
	const data = JSON.parse(e.data)

	if (data.status === '120') {
		return
	}

	if (data.status === '200') {
		if (data.wspath !== 'carrier-tenders') return

		const { action, idRoute: targetIdRoute, carrierBid: bid } = data
		if (!action) return

		const routeNode = gridOptions.api.getRowNode(targetIdRoute)
		
		// установка нового предложения
		if (action === 'create') {
			if (!routeNode) return

			const route = routeNode.data
			const offers = route.actualCarrierBids

			if (!bid) return
			if (!targetIdRoute) return
			// обновляем предложение, если оно актуальное
			// удаляем старое предложение с тем же id
			const filteredOffers = offers.filter(offer => offer.idCarrierBid !== bid.idCarrierBid)
			// добавляем новое предложение в список
			const newOffers = [...filteredOffers, bid]
			// обновляем маршрут
			const newRoute = { ...route, actualCarrierBids: newOffers }
			// обновляем стоимость маршрута
			const newRoutePrice = getPrice(newRoute, newOffers)
			// обновляем предложение
			const newMyOffer = getMyOffer(user, newOffers)
			// обновляем строку в таблице
			const updatedRoute = { ...newRoute, price: newRoutePrice, myOffer: newMyOffer }
			gridOptions.api.applyTransaction({ update: [ updatedRoute ] })

		// отмена предложения
		} else if (action === 'delete') {
			if (!routeNode) return

			const route = routeNode.data
			const offers = route.actualCarrierBids

			if (!bid) return
			if (!targetIdRoute) return
			// удаляем предложение и, если нужно, обновляем актуальное предложение
			// удаляем старое предложение с тем же id
			const filteredOffers = offers.filter(offer => offer.idCarrierBid !== bid.idCarrierBid)
			// обновляем маршрут
			const newRoute = { ...route, actualCarrierBids: filteredOffers }
			// обновляем стоимость маршрута
			const newRoutePrice = getPrice(newRoute, filteredOffers)
			// обновляем предложение
			const newMyOffer = getMyOffer(user, filteredOffers)
			// обновляем строку в таблице
			const updatedRoute = { ...newRoute, price: newRoutePrice, myOffer: newMyOffer }
			gridOptions.api.applyTransaction({ update: [ updatedRoute ] })
		}

		// тендер отправлен на биржу
		else if (action === 'create-tender') {
			const newRoute = data.route
			if (!newRoute) return

			const mappedRoute = await tenderMapCallback(newRoute)
			gridOptions.api.applyTransaction({ add: [ mappedRoute ] })
		}

		// превращение закрытого тендера в тендер на понижение
		else if (action === 'change-tender-type') {
			if (!routeNode) return

			const updatedRoute = data.route
			if (!updatedRoute) return

			const mappedRoute = await tenderMapCallback(updatedRoute)
			gridOptions.api.applyTransaction({ update: [ mappedRoute ] })
		}

		// отмена тендера
		else if (action === 'cancel-tender') {
			if (!routeNode) return

			const route = routeNode.data
			if (!route) return

			gridOptions.api.applyTransaction({ remove: [ route ] })
		}

		// тендер завершен
		else if (action === 'finish-tender') {
			if (!routeNode) return

			const route = routeNode.data
			if (!route) return

			gridOptions.api.applyTransaction({ remove: [ route ] })
		}

		// уведомления перевозчикам
		else if (action === 'notification' || action === 'new-tender') {

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

function getPrice(tender, actualCarrierBids) {
	const forReductionTender = tender.forReduction

	if (forReductionTender) {
		if (actualCarrierBids.length === 0) {
			return tender.startPriceForReduction && tender.currencyForReduction
				? `${tender.startPriceForReduction} ${tender.currencyForReduction}` : ''
		}
		const actualOffer = actualCarrierBids.sort((a, b) => a.price - b.price)[0]
		return `${actualOffer.price} ${actualOffer.currency}`
	}

	return ''
}
function getMyOffer(user, actualCarrierBids) {
	const userId = user.idUser
	if (actualCarrierBids.length === 0) return ''
	const myOffer = actualCarrierBids.filter(offer => offer.idUser === userId)[0]
	return myOffer ? `${myOffer.price} ${myOffer.currency}` : ''
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

function onNewTenderCheckboxChange(e) {
	const flag = e.target.checked

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		url: setNewTenderNotificationFlagUrl,
		data: { newTenderNotification: flag },
		successCallback: async (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				newTenderNotificationFlag = flag
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				e.target.checked = !flag
				newTenderNotificationFlag = !flag
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			e.target.checked = !flag
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}