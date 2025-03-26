import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { getAllAcceptanceQualityFoodCardUrl, getClosedAcceptanceQualityBaseUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData } from './utils.js'
import PhotoSwipeLightbox from './photoSwipe/photoswipe-lightbox.esm.min.js'
import PhotoSwipeDynamicCaption  from './photoSwipe/photoswipe-dynamic-caption-plugin.esm.js'
import PhotoSwipe from './photoSwipe/photoswipe.esm.min.js'
import { buttons, caption, thumbnails } from './photoSwipe/photoSwipeHelper.js'

const first = [
	"https://images.unsplash.com/photo-1609342122563-a43ac8917a3a?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1608481337062-4093bf3ed404?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1605973029521-8154da591bd7?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1526281216101-e55f00f0db7a?ixlib=rb-1.2.1&ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1418065460487-3e41a6c84dc5?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1505820013142-f86a3439c5b2?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1477322524744-0eece9e79640?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1469474968028-56623f02e42e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1585338447937-7082f8fc763d?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1476842384041-a57a4f124e2e?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1465311530779-5241f5a29892?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1461301214746-1e109215d6d3?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1610448721566-47369c768e70?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1510414842594-a61c69b5ae57?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1539678050869-2b97c7c359fd?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1446630073557-fca43d580fbe?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1596370743446-6a7ef43a36f9?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1464852045489-bccb7d17fe39?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1483728642387-6c3bdd6c93e5?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1510011560141-62c7e8fc7908?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80",
	"https://images.unsplash.com/photo-1471931452361-f5ff1faa15ad?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=2252&q=80",
	"https://images.unsplash.com/photo-1508766206392-8bd5cf550d1c?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1536&q=80",
	"https://images.unsplash.com/photo-1586276393635-5ecd8a851acc?ixid=MXwxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHw%3D&ixlib=rb-1.2.1&auto=format&fit=crop&w=1600&q=80"
]

const PAGE_NAME = 'acceptanceQuality'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let lightbox

const detailColumnDefs = [
	{ headerName: 'Продукт', field: 'productName', flex: 6, },
	{
		headerName: 'Выборка', field: 'sampleSize',
		valueFormatter: (params) => `${params.value} кг`
	},

	{
		headerName: 'ВД', field: 'totalInternalDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalInternalDefectWeight} кг / ${data.totalInternalDefectPercentage}%`
		},
	},
	{
		headerName: 'Брак', field: 'totalDefectPercentage',
		flex: 3,
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalInternalDefectWeight} кг / ${data.totalDefectPercentage}% / ${data.totalDefectPercentageWithPC}%`
		},
	},
	{
		headerName: 'ЛН', field: 'totalLightDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalLightDefectWeight} кг / ${data.totalLightDefectPercentage}%`
		},
	},
	{
		headerName: 'Фото', field: 'images',
		cellClass: 'px-1 py-0 text-center small-row',
		valueGetter: (params) => params.data.images ? 'Есть фото' : '',
		cellRenderer: BtnCellRenderer,
		cellRendererParams: {
			onClick:  (params => showGalleryItems(params.data)),
			label: 'Показать фото',
			className: 'btn btn-light border btn-sm w-100 text-nowrap',
		},
	},
	{
		headerName: 'Подробнее', field: 'idAcceptanceQualityFoodCard',
		cellClass: 'px-1 py-0 text-center small-row',
		cellRenderer: BtnCellRenderer,
		cellRendererParams: {
			onClick: showCardModal,
			label: 'Подробнее',
			className: 'btn btn-light border btn-sm',
		},
	},
]
const detailGridOptions = {
	columnDefs: detailColumnDefs,
	defaultColDef: {
		headerClass: 'px-1',
		cellClass: 'px-2',
		wrapText: true,
		autoHeight: true,
		resizable: true,
		flex: 2,
		suppressMenu: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
}

const columnDefs = [
	{
		headerName: "id", field: "idAcceptanceFoodQuality",
		sort: "desc",
		minWidth: 80, width: 80,
		cellRenderer: 'agGroupCellRenderer',
	},
	{ headerName: "ID Маркет", field: "idOrder", width: 120, },
	{ headerName: "Фирма", field: "firmNameAccept", width: 160, },
	{ headerName: "Гос номер", field: "carNumber", width: 120, },
	{
		headerName: "Дата план", field: "datePlanAcceptInMs", width: 140,
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{ headerName: "Тип выгрузки", field: "unloadingTypeToView", width: 120, },
	{ headerName: "Вес (кг)", field: "cargoWeight", width: 100, },
	{ headerName: "SKU", field: "sku", minWidth: 50, width: 50 },
	{ headerName: "ТТН", field: "ttn", width: 100, },
	{ headerName: "О товаре", field: "infoAcceptance" },
	{
		headerName: "Дата старт", field: "dateStartProcessInMs", width: 140,
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{
		headerName: "Дата стоп", field: "dateStopProcessInMs", width: 140,
		valueFormatter: dateTimeValueFormatter,
		comparator: dateComparator,
		filterParams: { valueFormatter: dateTimeValueFormatter, },
	},
	{
		headerName: "Длительность", field: "durationProcess", width: 100,
		valueFormatter: (params) => params.value ? `${Math.floor(params.value / 60000)} мин.` : "Неизвестно",
		filterParams: { valueFormatter: (params) => params.value ? `${Math.floor(params.value / 60000)} мин.` : "Неизвестно", },
	},
	{ headerName: "Работники", field: "workers", },
	{
		headerName: "Статус", field: "qualityProcessStatus",
		cellClass: "text-center font-weight-bold",
		valueFormatter: (params) => getStatusToView(params.value),
	},
]
const gridOptions = {
	columnDefs: columnDefs,
	defaultColDef: {
		headerClass: 'px-2 font-weight-bold',
		cellClass: 'px-2 text-center',
		flex: 1,
		minWidth: 100,
		resizable: true,
		suppressMenu: true,
		sortable: true,
		filter: true,
		floatingFilter: true,
		wrapText: true,
		autoHeight: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
		enableRowGroup: true,
	},
	animateRows: true,
	suppressDragLeaveHidesColumns: true,
	suppressRowClickSelection: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	onSortChanged: debouncedSaveColumnState,
	onColumnResized: debouncedSaveColumnState,
	onColumnMoved: debouncedSaveColumnState,
	onColumnVisible: debouncedSaveColumnState,
	onColumnPinned: debouncedSaveColumnState,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idAcceptanceFoodQuality,
	masterDetail: true,
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: detailGridOptions,
		getDetailRowData: getCardsData,
	},
	sideBar: {
		toolPanels: [
			{
				id: 'columns',
				labelDefault: 'Columns',
				labelKey: 'columns',
				iconKey: 'columns',
				toolPanel: 'agColumnsToolPanel',
				toolPanelParams: {
					suppressRowGroups: true,
					suppressValues: true,
					suppressPivots: true,
					suppressPivotMode: true,
				},
			},
			{
				id: 'filters',
				labelDefault: 'Filters',
				labelKey: 'filters',
				iconKey: 'filter',
				toolPanel: 'agFiltersToolPanel',
			},
			{
				id: 'resetState',
				iconKey: 'menu',
				labelDefault: 'Сброс настроек',
				toolPanel: ResetStateToolPanel,
				toolPanelParams: {
					localStorageKey: LOCAL_STORAGE_KEY,
				},
			},
		],
	},
}

const photoSwipeOptions = {
	pswpModule: PhotoSwipe,
	bgOpacity: 1,
	preloaderDelay: 0,
	preloadFirstSlide: false,
	wheelToZoom: true,
	errorMsg: 'Изображение не загружено',
	closeTitle: 'Закрыть',
	zoomTitle: 'Масштаб',
	arrowPrevTitle: 'Предыдущее изображение',
	arrowNextTitle: 'Следующее изображение',
	paddingFn: (viewportSize) => ({
		top: 30, bottom: 30, left: 70, right: 70
	}),
}

const photoSwipeDynamicCaptionOptions = {
	captionContent: (slide) => slide.data.description,
	type: 'aside',
}

document.addEventListener('DOMContentLoaded', async () => {
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)
	restoreColumnState()

	initGallery()

	// автозаполнение полей дат в форме поиска заявок
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')
	const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 0)
	date_fromInput.value = dateStart
	date_toInput.value = dateEnd

	const acceptanceQualityData = await getAcceptanceQualityData(dateStart, dateEnd)
	updateTable(gridOptions, acceptanceQualityData)

	// листнер на отправку формы поиска заявок
	orderSearchForm.addEventListener('submit', searchFormSubmitHandler)

	$('#qualityCardModal').on('hidden.bs.modal', (e) => {
		const showImagesBtnContainer = document.getElementById('showImagesBtnContainer')
		showImagesBtnContainer.innerHTML = ''
	})
})


window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

function initGallery() {
	lightbox = new PhotoSwipeLightbox(photoSwipeOptions)
	new PhotoSwipeDynamicCaption(lightbox, photoSwipeDynamicCaptionOptions)
	lightbox.on('uiRegister', () => {
		buttons.registerDownloadButton(lightbox)
		buttons.registerRotateLeftBtn(lightbox)
		buttons.registerRotateRightBtn(lightbox)
		thumbnails.registerThumbnails(lightbox)
		caption.registerCaption(lightbox)
	})
	lightbox.on('afterInit', () => {
		thumbnails.createThumbnails(lightbox)
	})
	lightbox.on('destroy', () => {
		thumbnails.destroyThumbnails(lightbox)
	})
	lightbox.init()
}

async function showGalleryItems(data) {
	const galleryItems = data.images
	if (!galleryItems.length) {
		snackbar.show('Фото отсутствуют')
		return
	}

	const description = getCardDescriptionText(data)
	const itemsWithSizes = await Promise.all(
		galleryItems.map(async (src, i) => {
			try {
				const size = await getImageSize(src)
				return {
					src: src,
					title: `Изображение ${i+1}`,
					alt: `Изображение ${i+1}`,
					width: size.width,
					height: size.height,
					description: description,
				}
			} catch (error) {
				// Запасные значения, если изображение не загрузилось
				return {
					src: src,
					title: `Изображение ${i+1}`,
					alt: `Изображение ${i+1}`,
					width: 1500,
					height: 900,
					description: description,
				}
			}
		})
	)

	lightbox.loadAndOpen(0, itemsWithSizes)
}

// обработчик отправки формы поиска
async function searchFormSubmitHandler(e) {
	try {
		e.preventDefault()
		gridOptions.api.showLoadingOverlay()
		const formData = new FormData(e.target)
		const data = Object.fromEntries(formData)
		const acceptanceQualityData = await getAcceptanceQualityData(data.date_from, data.date_to)
		updateTable(gridOptions, acceptanceQualityData)
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}


// получение данных качества товаров
async function getAcceptanceQualityData(dateStart, dateEnd) {
	try {
		const url = `${getClosedAcceptanceQualityBaseUrl}?startDate=${dateStart}&endDate=${dateEnd}`
		const res = await getData(url)
		return res ? res : []
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}

// получение данных карточек
async function getAcceptanceQualityCards(idAcceptanceQuality) {
	try {
		const url = `${getAllAcceptanceQualityFoodCardUrl}?idAcceptanceFoodQuality=${idAcceptanceQuality}`
//		const url = `http://10.10.1.22:14000/quality/files/${idAcceptanceQuality}`
		const res = await getData(url)
		return res ? res : []
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}


function renderTable(gridDiv, gridOptions) {
	new agGrid.Grid(gridDiv, gridOptions)
	gridOptions.api.setRowData([])
	gridOptions.api.showNoRowsOverlay()
}
function updateTable(gridOptions, data) {
	if (!data || !data.length) {
		gridOptions.api.setRowData([])
		gridOptions.api.showNoRowsOverlay()
		return
	}

	const mappingData = getMappingData(data)
	gridOptions.api.setRowData(mappingData)
	gridOptions.api.hideOverlay()
}
function getMappingData(data) {
	return data.map(mapCallback)
}
function mapCallback(item) {
	const acceptance = item.acceptance || {};
	return {
		...acceptance,
		...item,
		idAcceptanceFoodQuality: item.idAcceptanceFoodQuality,
		carNumberToView: acceptance.carNumber || "Неизвестно",
		unloadingTypeToView: acceptance.unloadingType === 1 ? "Ручная" : "Автоматическая",
		ttn: acceptance.ttnInList?.map((ttn) => ttn.ttnName).join(", ") || "Нет данных",
		datePlanAcceptInMs: acceptance.datePlanAccept ? new Date(acceptance.datePlanAccept).getTime() : null,
		datePlanAcceptToView: acceptance.datePlanAccept ? dateTimeValueFormatter(acceptance.datePlanAccept) : '',
		dateStartProcessInMs: item.dateStartProcess ? new Date(item.dateStartProcess).getTime() : null,
		dateStopProcessInMs: item.dateStopProcess ? new Date(item.dateStopProcess).getTime() : null,
		durationProcessToView: item.durationProcess ? `${Math.floor(item.durationProcess / 60000)} мин.` : "Неизвестно",
		pauseStatusToView: item.qualityProcessStatus === 50 ? "На паузе" : "",
		workers: item.acceptanceFoodQualityUsers?.map((user) => user.userYard.login).join(", ") || "Нет данных",
	};
}
function getContextMenuItems (params) {
	const rowNode = params.node
	if (!rowNode) return []

	const items = [
		{
			name: "Сбросить настройки колонок",
			action: () => {
				gridColumnLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
		},
		{
			name: `Сбросить настройки фильтров`,
			action: () => {
				gridFilterLocalState.resetState(params, LOCAL_STORAGE_KEY)
			},
		},
		"separator",
		"excelExport"
	]

	return items
}
function getCardsData (params) {
	const rowData = params.data
	if (!rowData.cards) {
		const idAcceptanceFoodQuality = rowData.idAcceptanceFoodQuality
		getAcceptanceQualityCards(idAcceptanceFoodQuality)
			.then(cards => {
				if (cards.length) {
					cards = cards.map(card => {
						const sampleSize = parseFloat(card.sampleSize) || 0;
						return {
							...card,
							...recalculateDefects("internalDefectsQualityCardList", sampleSize, card.internalDefectsQualityCardList),
							...recalculateDefects("totalDefectQualityCardList", sampleSize, card.totalDefectQualityCardList),
							...recalculateDefects("lightDefectsQualityCardList", sampleSize, card.lightDefectsQualityCardList),
						}
					})
				}
				console.log("🚀 ~ getCardsData ~ cards:", cards)
				gridOptions.api.applyTransaction({ update: [{ ...rowData, cards }]})
				params.successCallback(rowData.cards)
			})
			.catch(error => {
				console.error(error)
				params.successCallback([])
			})
	} else {
		params.successCallback(rowData.cards)
	}
}

function dateComparator(date1, date2) {
	if (!date1 || !date2) return 0
	const date1Value = new Date(date1).getTime()
	const date2Value = new Date(date2).getTime()
	return date1Value - date2Value
}
function dateTimeValueFormatter(params) {
	const date = params.value
	if (!date) return ''
	return dateHelper.getFormatDateTime(date)
}

// функции управления состоянием колонок
function saveColumnState() {
	gridColumnLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreColumnState() {
	gridColumnLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

// функции управления фильтрами колонок
function saveFilterState() {
	gridFilterLocalState.saveState(gridOptions, LOCAL_STORAGE_KEY)
}
function restoreFilterState() {
	gridFilterLocalState.restoreState(gridOptions, LOCAL_STORAGE_KEY)
}

function getStatusToView(status) {
	switch (status) {
		case 0:
			return "Новый";
		case 10:
			return "В процессе";
		case 50:
			return "На паузе";
		case 100:
			return "Закрыт";
		default:
			return `Неизвестный статус (${status})`;
	}
}

function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerHTML = message
	$('#displayMessageModal').modal('show')
}

function showCardModal(params) {
	const card = params.data
	if (!card) return

	const formatDate = dateHelper.getFormatDateTime(card.dateCard)

	// заполнение полей
	fillField('productName', card.productName)
	fillField('dateCard', formatDate)
	fillField('firmNameAccept', card.firmNameAccept)
	fillField('ttn', card.ttn)
	fillField('carNumber', card.carNumber)
	fillField('cargoWeightCard', card.cargoWeightCard)
	fillField('sampleSize', card.sampleSize)
	fillField('totalInternalDefectWeight', card.totalInternalDefectWeight)
	fillField('totalInternalDefectPercentage', card.totalInternalDefectPercentage)
	fillField('totalDefectWeight', card.totalDefectWeight)
	fillField('totalDefectPercentage', card.totalDefectPercentage)
	fillField('totalDefectPercentageWithPC', card.totalDefectPercentageWithPC)
	fillField('totalLightDefectWeight', card.totalLightDefectWeight)
	fillField('totalLightDefectPercentage', card.totalLightDefectPercentage)
	fillField('classType', card.classType)
	fillField('numberOfBrands', card.numberOfBrands)
	fillField('qualityOfProductPackaging', card.qualityOfProductPackaging)
	fillField('thermogram', card.thermogram)
	fillField('bodyTemp', card.bodyTemp)
	fillField('fruitTemp', card.fruitTemp)
	fillField('appearanceEvaluation', card.appearanceEvaluation)
	fillField('appearanceDefects', card.appearanceDefects)
	fillField('maturityLevel', card.maturityLevel)
	fillField('tasteQuality', card.tasteQuality)
	fillField('caliber', card.caliber)
	fillField('stickerDescription', card.stickerDescription)
	fillField('cardInfo', card.cardInfo)

	// Заполнение таблиц с дефектами
	fillDefectsTable('#internalDefectsList', card.internalDefectsQualityCardList, ['weight', 'percentage', 'description'])
	fillDefectsTable('#lightDefectsList', card.lightDefectsQualityCardList, ['weight', 'percentage', 'description'])
	fillDefectsTable('#totalDefectsList', card.totalDefectQualityCardList, ['weight', 'percentage', 'percentageWithPC', 'description'])

	const showImagesBtnContainer = document.getElementById('showImagesBtnContainer')
	const showImagesBtn = document.createElement('button')
	showImagesBtn.className = 'btn btn-secondary'
	showImagesBtn.type = 'button'
	showImagesBtn.textContent = 'Посмотреть фото'
	showImagesBtn.onclick = () => showGalleryItems(card)
	showImagesBtnContainer.append(showImagesBtn)

	$('#qualityCardModal').modal('show')
}

// расчет суммы отдельных дефектов
function recalculateDefects(type, sampleSize, defects) {
	let totalWeight = 0
	let totalPercentage = 0
	let totalPercentageWithPC = 0

	const updatedDefects = defects.map((defect) => {
		const weight = parseFloat(defect.weight) || 0
		totalWeight += weight

		if (type === "totalDefectQualityCardList") {
			const percentage = sampleSize ? (weight / sampleSize) * 100 : 0
			const percentageWithPC = sampleSize ? (percentage < 10 ? (weight / sampleSize) * 140 : (weight / sampleSize) * 200) : 0
			totalPercentage += percentage
			totalPercentageWithPC += percentageWithPC
			return { ...defect, percentage: percentage.toFixed(2), percentageWithPC: percentageWithPC.toFixed(2) }
		} else {
			const percentage = sampleSize ? (weight / sampleSize) * 100 : 0
			totalPercentage += percentage
			return { ...defect, percentage: percentage.toFixed(2) }
		}
	})

	return {
		[type]: updatedDefects,
		...(type === "internalDefectsQualityCardList" && { totalInternalDefectWeight: roundNumber(totalWeight, 100), totalInternalDefectPercentage: totalPercentage.toFixed(2) }),
		...(type === "totalDefectQualityCardList" && { totalDefectWeight: roundNumber(totalWeight, 100), totalDefectPercentage: totalPercentage.toFixed(2), totalDefectPercentageWithPC: totalPercentageWithPC.toFixed(2) }),
		...(type === "lightDefectsQualityCardList" && { totalLightDefectWeight: roundNumber(totalWeight, 100), totalLightDefectPercentage: totalPercentage.toFixed(2) }),
	}
}

function roundNumber(num, fraction) {
	return Math.round((Number(num) + Number.EPSILON) * fraction) / fraction
}

function fillField(fieldId, data) {
	const field = document.getElementById(fieldId)
	field.textContent = data
}

function fillDefectsTable(tableId, defects, columns) {
	const $tableBody = $(tableId)
	$tableBody.empty()
	
	defects.forEach(defect => {
		const $row = $('<tr>')
		columns.forEach(col => {
			if (col === 'weight') $row.append($('<td>').text(`${defect[col]} кг`))
			else if (col === 'percentage' || col === 'percentageWithPC') $row.append($('<td>').text(`${defect[col]}%`))
			else $row.append($('<td>').text(defect[col]))
		})
		$tableBody.append($row)
	})
}

function getCardDescriptionText(card) {
	return [
		card.productName ? card.productName : '',
		card.sampleSize ? `Выборка ${card.sampleSize} кг` : '',
		card.totalInternalDefectWeight
			? `ВД: ${card.totalInternalDefectWeight} кг / ${card.totalInternalDefectPercentage}%`
			: '',
		card.totalDefectWeight
			? `Брак: ${card.totalDefectWeight} кг / ${card.totalDefectPercentage}% / ${card.totalDefectPercentageWithPC}%` : '',
		card.totalLightDefectWeight
			? `ЛН: ${card.totalLightDefectWeight}кг / ${card.totalLightDefectPercentage}%` : '',
	].filter(Boolean).join('<br>')
}

function getImageSize(src) {
	return new Promise((resolve, reject) => {
		const img = new Image()
		img.onload = (e) => resolve({ width: img.width, height: img.height })
		img.onerror = () => reject(new Error('Не удалось загрузить изображение'))
		img.src = src
	})
}