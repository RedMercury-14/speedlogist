import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, BtnsCellRenderer, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { aproofQualityFoodCardUrl, getAllAcceptanceQualityFoodCardUrl, getClosedAcceptanceQualityBaseUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData } from './utils.js'
import PhotoSwipeLightbox from './photoSwipe/photoswipe-lightbox.esm.min.js'
import PhotoSwipeDynamicCaption  from './photoSwipe/photoswipe-dynamic-caption-plugin.esm.js'
import PhotoSwipe from './photoSwipe/photoswipe.esm.min.js'
import { buttons, caption, thumbnails } from './photoSwipe/photoSwipeHelper.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { uiIcons } from './uiIcons.js'
import { ajaxUtils } from './ajaxUtils.js'

const PAGE_NAME = 'acceptanceQuality'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const token = $("meta[name='_csrf']").attr("content")

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let lightbox

class CardStatusCellRenderer {
	init(params) {
		this.params = params

		this.valueSpan = document.createElement('span')
		this.valueSpan.innerText = getCardStatusText(params.data)
		this.eGui = document.createElement("button")
		this.eGui.className = this.params.className || ''
		this.eGui.id = this.params.id || ''
		this.eGui.innerText = this.params.label || this.params.dynamicLabel(this.params) || ''

		this.btnClickedHandler = this.btnClickedHandler.bind(this)
		this.eGui.addEventListener("click", this.btnClickedHandler)
	}

	getGui() {
		return this.params.value === 100 ? this.eGui : this.valueSpan
	}

	btnClickedHandler(event) {
		this.params.onClick(this.params)
	}

	destroy() {
		this.eGui.removeEventListener("click", this.btnClickedHandler)
	}
}

const detailColumnDefs = [
	{ headerName: 'Продукт', field: 'productName', flex: 5, },
	{
		headerName: 'Выборка', field: 'sampleSize',
		valueFormatter: (params) => `${params.value} ${params.data?.unit || "кг"}`
	},
	{
		headerName: 'ВД (вес/процент)', field: 'totalInternalDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalInternalDefectWeight} ${params.data?.unit || "кг"} / ${data.totalInternalDefectPercentage}%`
		},
	},
	{
		headerName: 'Брак (вес/процент/процент с ПК)', field: 'totalDefectPercentage',
		flex: 3,
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalDefectWeight} ${params.data?.unit || "кг"} / ${data.totalDefectPercentage}% / ${data.totalDefectPercentageWithPC}%`
		},
	},
	{
		headerName: 'ЛН (вес/процент)', field: 'totalLightDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalLightDefectWeight} ${params.data?.unit || "кг"} / ${data.totalLightDefectPercentage}%`
		},
	},
	{
		headerName: '', field: 'idAcceptanceQualityFoodCard',
		cellClass: 'px-1 py-0 text-center small-row',
		minWidth: 100, flex: 1,
		cellRenderer: BtnsCellRenderer,
		cellRendererParams: {
			onClick: cardRowActionOnClickHandler,
			buttonList: [
				{ className: 'btn btn-light border btn-sm', id: 'showImages', icon: uiIcons.images, title: 'Показать фото' },
				{ className: 'btn btn-light border btn-sm', id: 'showInfo', icon: uiIcons.info, title: 'Подробнее' },
			],
		},
	},
	{
		headerName: 'Статус карточки', field: 'cardStatus',
		cellClass: 'px-1 py-0 text-center small-row font-weight-bold',
		minWidth: 125, flex: 1,
		cellRenderer: CardStatusCellRenderer,
		cellRendererParams: {
			onClick: (params) => showApproveCardModal(params.data),
			label: 'Подтвердить',
			className: 'btn btn-success border btn-sm',
		},
	},
]
const detailGridOptions = {
	columnDefs: detailColumnDefs,
	defaultColDef: {
		headerClass: 'px-1',
		cellClass: 'px-2 text-center',
		wrapText: true,
		autoHeight: true,
		resizable: true,
		flex: 2,
		minWidth: 100,
		suppressMenu: true,
		wrapHeaderText: true,
		autoHeaderHeight: true,
	},
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idAcceptanceQualityFoodCard,
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
		headerName: "Импорт", field: "isImport",
		valueFormatter: (params) => params.value ? "Да" : "Нет",
		filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
	},
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
	// листнер на отправку формы установки статуса карточки
	approveCardForm.addEventListener('submit', approveCardFormSubmitHandler)
	approveCardForm2.addEventListener('submit', approveCardFormSubmitHandler)

	const cardStatusSelect = document.getElementById('status')
	const cardManagerPercentInputsContainer = document.querySelectorAll('.managerPercentInput')
	const managerPercentTypeSelect = document.getElementById('managerPercent_type')
	const managerPercentValueInput = document.getElementById('managerPercent_value')
	cardStatusSelect.addEventListener('change', (e) => {
		cardStatusSelectChangeHandler(e, managerPercentTypeSelect, managerPercentValueInput, cardManagerPercentInputsContainer)
	})

	const cardStatusSelect2 = document.getElementById('status2')
	const cardManagerPercentInputsContainer2 = document.querySelectorAll('.managerPercentInput2')
	const managerPercentTypeSelect2 = document.getElementById('managerPercent_type2')
	const managerPercentValueInput2 = document.getElementById('managerPercent_value2')
	cardStatusSelect2.addEventListener('change', (e) => {
		cardStatusSelectChangeHandler(e, managerPercentTypeSelect2, managerPercentValueInput2, cardManagerPercentInputsContainer2)
	})

	$('#approveCardModal').on('hidden.bs.modal', (e) => {
		approveCardForm.reset()
		managerPercentTypeSelect.setAttribute('disabled', '')
		managerPercentTypeSelect.removeAttribute('required')
		managerPercentValueInput.setAttribute('disabled', '')
		managerPercentValueInput.removeAttribute('required')
		cardManagerPercentInputsContainer.forEach(container => {
			container.classList.add('d-none')
		})
	})
	$('#qualityCardInfoModal').on('hidden.bs.modal', (e) => {
		approveCardForm2.reset()
		managerPercentTypeSelect2.setAttribute('disabled', '')
		managerPercentTypeSelect2.removeAttribute('required')
		managerPercentValueInput2.setAttribute('disabled', '')
		managerPercentValueInput2.removeAttribute('required')
		cardManagerPercentInputsContainer2.forEach(container => {
			container.classList.add('d-none')
		})
	})
})


window.addEventListener("unload", () => {
	const date_fromInput = document.querySelector('#date_from')
	const date_toInput = document.querySelector('#date_to')

	// запоминаем даты для запроса данных из БД
	dateHelper.setDatesToFetch(DATES_KEY, date_fromInput.value, date_toInput.value)
})

// инициализация галереи
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

// отображение галереи с изображениями
async function showGalleryItems(data) {
	const galleryItems = data.images
	if (!galleryItems.length) {
		snackbar.show('Фото отсутствуют')
		return
	}

	bootstrap5overlay.showOverlay()

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

	bootstrap5overlay.hideOverlay()
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
// обработчик отправки формы статуса карточки
function approveCardFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	const payload = {
		idAcceptanceQualityFoodCard: data.idAcceptanceQualityFoodCard ?  Number(data.idAcceptanceQualityFoodCard) : null,
		status: data.status ? Number(data.status) : null,
		comment: data.comment ? data.comment.trim() : null,
		managerPercent: data.managerPercent_type && data.managerPercent_value
			? `${data.managerPercent_type} ${data.managerPercent_value}%` : null,
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	ajaxUtils.postJSONdata({
		token,
		url: aproofQualityFoodCardUrl,
		data: payload,
		successCallback: (res) => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()

			if (res.status === '200') {
				// обновить карточку в detail строки таблицы
				const cardData = res.object
				const rowNode = gridOptions.api.getRowNode(data.idAcceptanceFoodQuality)
				const rowData = rowNode.data
				const updatedRowData = {
					...rowData,
					cards: rowData.cards.map(card => {
						if (card.idAcceptanceQualityFoodCard === cardData.idAcceptanceQualityFoodCard) {
							return recalculateCard(cardData)
						}
						return card
					})
				}
				updateTableRow(gridOptions, updatedRowData)
				res.message && snackbar.show(res.message)
				$(`#qualityCardInfoModal`).modal('hide')
				$(`#approveCardModal`).modal('hide')
				return
			}

			if (res.status === '100') {
				const message = res.message ? res.message : 'Неизвестная ошибка'
				snackbar.show(message)
				return
			}
		},
		errorCallback: () => {
			clearTimeout(timeoutId)
			bootstrap5overlay.hideOverlay()
		}
	})
}

// обработчик изменения значения статуса карточки в выпадающем списке формы
function cardStatusSelectChangeHandler(e, typeSelect, valueInput, inputsContainer) {
	const selectedStatus = e.target.value

	if (selectedStatus === '154') {
		typeSelect.removeAttribute('disabled',)
		typeSelect.setAttribute('required', '')
		valueInput.removeAttribute('disabled',)
		valueInput.setAttribute('required', '')
		inputsContainer.forEach(container => {
			container.classList.remove('d-none')
		})
		typeSelect.focus()
	} else {
		typeSelect.setAttribute('disabled', '')
		typeSelect.removeAttribute('required')
		valueInput.setAttribute('disabled', '')
		valueInput.removeAttribute('required')
		inputsContainer.forEach(container => {
			container.classList.add('d-none')
		})
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
		const res = await getData(url)
		return res ? res : []
	} catch (error) {
		console.error(error)
		snackbar.show('Ошибка получения данных')
	}
}

// методы таблицы
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

// получение данных карточек
function getCardsData (params) {
	const rowData = params.data
	if (!rowData.cards) {
		const idAcceptanceFoodQuality = rowData.idAcceptanceFoodQuality
		getAcceptanceQualityCards(idAcceptanceFoodQuality)
			.then(cards => {
				if (cards.length) {
					cards = cards.map(recalculateCard)
				}
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

function recalculateCard(card) {
	const sampleSize = parseFloat(card.sampleSize) || 0
	const withPC = card.unit !== "шт"
	return {
		...card,
		...recalculateDefects("internalDefectsQualityCardList", sampleSize, card.internalDefectsQualityCardList, card.isImport, withPC),
		...recalculateDefects("totalDefectQualityCardList", sampleSize, card.totalDefectQualityCardList, card.isImport, withPC),
		...recalculateDefects("lightDefectsQualityCardList", sampleSize, card.lightDefectsQualityCardList, card.isImport, withPC),
	}
}

// конверторы дат для таблицы
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

// статусы строк качества товара
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

// отображение модального окна с сообщением
function showMessageModal(message) {
	const messageContainer = document.querySelector('#messageContainer')
	messageContainer.innerHTML = message
	$('#displayMessageModal').modal('show')
}

// отображение модального окна с карточкой
function showCardModal(card) {
	if (!card) return

	const formatDate = dateHelper.getFormatDateTime(card.dateCard)
	const sampleSizeUnit = card.unit === 'шт' ? 'шт' : 'кг'

	const cardStatusText = document.getElementById('cardStatusText')

	// отображение формы действия по карточке
	if (card.cardStatus === 100) {
		cardStatusText.classList.add('d-none')
		approveCardForm2.classList.remove('d-none')
	} else {
		approveCardForm2.classList.add('d-none')
		cardStatusText.classList.remove('d-none')
	}

	// заполнение полей
	approveCardForm2.idAcceptanceFoodQuality.value = card.idAcceptanceFoodQuality
	approveCardForm2.idAcceptanceQualityFoodCard.value = card.idAcceptanceQualityFoodCard
	fillField('cardStatusText', getCardStatusText(card))
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
	document.querySelectorAll('.sampleSizeUnit').forEach((el) => (el.textContent = sampleSizeUnit))

	// Заполнение таблиц с дефектами
	fillDefectsTable('#internalDefectsList', card.internalDefectsQualityCardList, ['weight', 'percentage', 'description'])
	fillDefectsTable('#lightDefectsList', card.lightDefectsQualityCardList, ['weight', 'percentage', 'description'])
	fillDefectsTable('#totalDefectsList', card.totalDefectQualityCardList, ['weight', 'percentage', 'percentageWithPC', 'description'])

	// кнопка просмотра фото
	const showImagesBtnContainer = document.getElementById('showImagesBtnContainer')
	showImagesBtnContainer.innerHTML = ''
	const showImagesBtn = document.createElement('button')
	showImagesBtn.className = 'btn btn-secondary'
	showImagesBtn.type = 'button'
	showImagesBtn.textContent = 'Посмотреть фото'
	showImagesBtn.onclick = (e) => showGalleryItems(card)
	showImagesBtnContainer.append(showImagesBtn)

	$('#qualityCardInfoModal').modal('show')
}

// отображение модального окна с подтверждением принятия качества
function showApproveCardModal(card) {
	if (!card) return
	approveCardForm.idAcceptanceFoodQuality.value = card.idAcceptanceFoodQuality
	approveCardForm.idAcceptanceQualityFoodCard.value = card.idAcceptanceQualityFoodCard
	$('#approveCardModal').modal('show')
}

// расчет суммы отдельных дефектов
function recalculateDefects(type, sampleSize, defects, isImport, withPC) {
	let totalWeight = 0
	let totalPercentage = 0
	let totalPercentageWithPC = 0

	const pcThreshold = 10 // порог для ПК (%)
	const defaultPercentageFactor = 100
	const pcPercentageFactorBeforeTreshold = isImport ? 160 : 140 // процент ПК при браке до 10%
	const pcPercentageFactorAftertTreshold = 200 // процент ПК при браке свыше 10%
	

	const updatedDefects = defects.map((defect) => {
		const weight = parseFloat(defect.weight) || 0
		totalWeight += weight

		if (type === "totalDefectQualityCardList") {
			const percentage = sampleSize ? (weight / sampleSize) * defaultPercentageFactor : 0;
			const percentageWithPC = withPC && sampleSize
				? (percentage <= pcThreshold
					? (weight / sampleSize) * pcPercentageFactorBeforeTreshold
					: (weight / sampleSize) * pcPercentageFactorAftertTreshold)
						: 0
			totalPercentage += percentage
			totalPercentageWithPC += percentageWithPC
			return { ...defect, percentage: percentage.toFixed(2), percentageWithPC: percentageWithPC.toFixed(2) }
		} else {
			const percentage = sampleSize ? (weight / sampleSize) * defaultPercentageFactor : 0
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

// заполнение элемента по id
function fillField(fieldId, data) {
	const field = document.getElementById(fieldId)
	field.textContent = data
}

// заполнение таблицы дефектов
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

// получение описания для изображений
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

// получение размера картинки
function getImageSize(src) {
	return new Promise((resolve, reject) => {
		const img = new Image()
		img.onload = (e) => resolve({ width: img.width, height: img.height })
		img.onerror = () => reject(new Error('Не удалось загрузить изображение'))
		img.src = src
	})
}

// форматирование статусов карточек
function getCardStatusText(card) {
	if (!card) return ''

	const status = card.cardStatus
	if (status === 154) {
		return `Принята с процентом: ${card.managerPercent}`
	}

	switch (status) {
		case 10:
			return 'Создана'
		case 100:
			return 'Закрыта'
		case 140:
			return 'Не принята УЗ'
		case 150:
			return 'Принята УЗ'
		case 152:
			return 'Принята с переборкой'
		case 154:
			return 'Принята с процентом брака'
		case 156:
			return 'Принята под реализацию'
		default:
			return `Неизвестный статус (${status})`
	}
}

// обработчик кнопок в строке карточки товара
function cardRowActionOnClickHandler(e, params) {
	if (e.buttonId === 'showImages') {
		showGalleryItems(params.data)
		return
	}

	if (e.buttonId === 'showInfo') {
		showCardModal(params.data)
		return
	}
}

function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] }
	)
}