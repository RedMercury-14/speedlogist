import { ws } from './global.js';
import { AG_GRID_LOCALE_RU } from '../js/AG-Grid/ag-grid-locale-RU.js'
import { cookieHelper, debounce, getData } from './utils.js';
import { dateComparator, gridFilterLocalState } from './AG-Grid/ag-grid-utils.js';

const getActiveTendersUrl = `../../api/carrier/getActiveInternationalTenders`
const getMessagesBaseUrl = `../../api/info/message/routes/`

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

const columnDefs = [
	{
		headerName: "Дата загрузки", field: 'dateToView',
		cellClass: 'px-2 text-center',
		comparator: dateComparator,
	},
	{ 
		headerName: "Название тендера", field: "routeDirection",
		tooltipField: 'routeDirection',
		cellRenderer: 'agGroupCellRenderer',
		cellRendererParams: {
			innerRenderer: routeLinkRenderer,
		},
		flex: 3,
		getQuickFilterText: params => {
			return params.value
		}
	},
	{ headerName: "Направление", field: "way", cellClass: 'px-2 font-weight-bold text-center', },
	{ headerName: "Предложенная цена", field: "price", cellClass: 'px-2 font-weight-bold text-center', },
	{ headerName: "Ваше предложение", field: "myOffer", cellClass: 'px-2 font-weight-bold text-center', }
]
const gridOptions = {
	columnDefs: columnDefs,
	rowClassRules: rowClassRules,
	defaultColDef: {
		headerClass: 'px-2',
		cellClass: 'px-2',
		flex: 1,
		resizable: true,
		sortable: true,
		suppressMenu: true,
		filter: true,
		floatingFilter: true,
	},
	onFilterChanged: debouncedSaveFilterState,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	masterDetail: true,
	detailRowHeight: 215,
	detailCellRendererParams: {
		detailGridOptions: {
			columnDefs: [
				{ headerName: "Дата загрузки", field: 'loadDate', flex: 2, },
				{ headerName: "Груз", field: 'cargo', flex: 2, },
				{ headerName: "Паллеты", field: 'totalLoadPall', flex: 1, },
				{ headerName: "Масса", field: 'totalCargoWeight', flex: 1, },
				{ headerName: "Дополнительная информация", field: 'info', tooltipField: 'info', cellRenderer: textInColumnRenderer },
			],
			defaultColDef: {
				wrapText: true,
				autoHeight: true,
				resizable: true,
				flex: 3,
				suppressMenu: true,
				wrapHeaderText: true,
				autoHeaderHeight: true,
			},
			enableBrowserTooltips: true,
			localeText: AG_GRID_LOCALE_RU,
		},
		getDetailRowData: (params) => {
			params.successCallback([params.data]);
		},
	}
}

window.onload = async () => {
	const goTGBotBtn = document.querySelector('#goTGBotBtn')
	const resetTableFiltersBtn = document.querySelector('#resetTableFilters')

	const gridDiv = document.querySelector('#myGrid')
	const filterTextBox = document.querySelector('#filterTextBox')
	const tenders = await getData(getActiveTendersUrl)
	const myMessages = await getData(getMessagesBaseUrl + 'from_me')
	const user = await getData('../../api/getThisUser')

	await renderTable(gridDiv, gridOptions, tenders, myMessages, user)

	restoreFilterState()

	filterTextBox.addEventListener('input', (e) => {
		gridOptions.api.setQuickFilter(e.target.value)
	})

	goTGBotBtn.addEventListener('click', goTGBotBtnClickHandler)
	resetTableFiltersBtn.addEventListener('click', resetFilterState)

	tgBotModal()

	ws.onmessage = (e) => onMessage(JSON.parse(e.data));
	// $.getJSON('../../api/info/message/routes/from_me', function(data) {
	// 	$.each(data, function(key, val) {
	// 		var rowItem = document.querySelectorAll('.tenders-item');
	// 		for (let i = 0; i < rowItem.length; i++) {
	// 			var rowItemI = rowItem[i];
	// 			var target = rowItemI.querySelector('.none').innerHTML;
	// 			if (target == val.idRoute) {
	// 				rowItemI.classList.add("activRow");
	// 				rowItemI.querySelector('#offer').innerHTML = val.text + " " + val.currency;
	// 			}
	// 		}
	// 	})
	// });
	function onMessage(msg) {
		if (msg.currency) {
			updateTable(gridOptions, tenders, myMessages, user)
		}
		// setTimeout(() => changeCost(), 100);
	};
	// setTimeout(() => changeCost(), 300);

	// function changeCost() {
	// 	var routeItem = document.querySelectorAll('.tenders-item');
	// 	for (let i = 0; i < routeItem.length; i++) {
	// 		var routeItemI = routeItem[i];
	// 		var idRoute = routeItemI.querySelector('.none').innerHTML;
	// 		process(routeItemI, idRoute);
	// 	}
	// }
	
	
	//временно!
	//подсвечивает для регионалов уже принятые тендеры
	// var routeItem = document.querySelectorAll('.tenders-item');
	// for (let i = 0; i < routeItem.length; i++) {
	// 	var routeItemI = routeItem[i];
	// 	var idRoute = routeItemI.querySelector('.none').innerHTML;
	// 	var regionalTenderButtonAgreeStat = localStorage.getItem('regionalTenderButtonAgree?'+idRoute);
	// 	if(regionalTenderButtonAgreeStat != null){
	// 		processForRegional(routeItemI, idRoute, regionalTenderButtonAgreeStat)
	// 	}
	// }	
	// function processForRegional(routeItemI, idRoute, regionalTenderButtonAgreeStat){
	// 	routeItemI.classList.add("activRow");
	// 	routeItemI.querySelector('#offer').innerHTML = regionalTenderButtonAgreeStat+'%'
	// }




	// function process(routeItemI, idRoute) {
	// 	fetch(`../../api/info/message/routes/${idRoute}`).then(function(response) {
	// 		response.json().then(function(message) {
	// 			const targetCost = routeItemI.querySelector('.targetCost')
	// 			const currentCost = routeItemI.querySelector('.currentCost')
	// 			const offer = routeItemI.querySelector('#offer')
	// 			if (message.length != 0) {
	// 				if (targetCost) targetCost.innerHTML = message[message.length - 1].text + " BYN"
	// 				if (currentCost) currentCost.innerHTML = message[message.length - 1].text + " BYN"
	// 			} else {
	// 				$.getJSON(`../../api/route/${idRoute}`, function(data) {
	// 					if (targetCost)
	// 					if (targetCost != null) {
	// 						targetCost.innerHTML = data.startPrice + " BYN";
	// 					}

	// 				});
	// 			}
	// 			var target = routeItemI;
	// 			if (offer.innerHTML == '') {

	// 			} else if (
	// 				(targetCost && offer.innerHTML != targetCost.innerHTML)
	// 				|| (currentCost && offer.innerHTML != currentCost.innerHTML)
	// 			) {
	// 				target.classList.add("attentionRow");
	// 				console.log(target);
	// 			}

	// 		});
	// 	});
	// }
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
	console.log('UPDATE TABLE')

	const mappingData = await getMappingData(data, messages, user)

	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
	!data.length && gridOptions.api.showNoRowsOverlay()
}

async function getMappingData(data, messages, user) {
	return await Promise.all(data.map( async (tender) => {
		const userUnp = user.numYNP
		const idRoute = tender.idRoute
		const rhsItem = tender.roteHasShop[0]
		const cargo = rhsItem && rhsItem.cargo ? rhsItem.cargo : ''
		const type = tender.typeTrailer ? `Тип прицепа: ${tender.typeTrailer}; ` : ''
		const temp = tender.temperature ? `Температура: ${tender.temperature} °C; ` : ''
		const vol = rhsItem && rhsItem.volume ? `Объем: ${rhsItem.volume}` : ''
		const info = type + temp + vol
		const dateToView = tender.dateLoadPreviously ? tender.dateLoadPreviously.split('-').reverse().join('.') : ''
		const loadDate = `${dateToView},  ${tender.timeLoadPreviously}`
		const myMessage = messages.find(m => m.idRoute === idRoute.toString()) || null

		const routeMessages = await getData(getMessagesBaseUrl + idRoute)

		const price = routeMessages && routeMessages.length !== 0
			? `${routeMessages[0].text} ${routeMessages[0].currency}`
			: tender.startPrice
				? `${tender.startPrice} BYN` : ''

		const myOffer = routeMessages && routeMessages.length !== 0 && routeMessages[0].ynp === userUnp
			? `${routeMessages[0].text} ${routeMessages[0].currency}`
			: myMessage
				? `${myMessage.text} ${myMessage.currency}` : ''

		return {
			...tender,
			dateToView,
			loadDate,
			cargo,
			info,
			myMessage,
			myOffer,
			price
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