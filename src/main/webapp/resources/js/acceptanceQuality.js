import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, BtnsCellRenderer, dateComparator, dateTimeValueFormatter, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { aproofQualityFoodCardUrl, getAllAcceptanceQualityFoodCardUrl, getClosedAcceptanceQualityBaseUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData, isMobileDevice, isObserver } from './utils.js'
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
const role = document.querySelector('#role').value

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
		headerName: 'Действия', field: 'idAcceptanceQualityFoodCard',
		cellClass: 'px-1 py-0 text-center small-row',
		minWidth: 130, flex: 1,
		cellRenderer: BtnsCellRenderer,
		cellRendererParams: {
			onClick: cardRowActionOnClickHandler,
			buttonList: [
				{ className: 'btn btn-light border btn-sm', id: 'showImages', icon: uiIcons.images, title: 'Показать фото' },
				{ className: 'btn btn-light border btn-sm', id: 'downloadImages', icon: uiIcons.download, title: 'Скачать все фото' },
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
	{
		headerName: 'Выборка', field: 'sampleSize',
		valueFormatter: (params) => `${params.value} ${params.data?.unit || "кг"}`
	},
	{
		headerName: 'ВД (вес/процент)', field: 'totalInternalDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalInternalDefectWeight} шт / ${data.totalInternalDefectPercentage}%`
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
]
const childCardsDetailGridOptions = {
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
	suppressMovableColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idAcceptanceQualityFoodCard,
}
const detailGridOptions = {
	columnDefs: [
		{
			headerName: 'Продукт', field: 'productName', flex: 5,
			cellRenderer: 'agGroupCellRenderer',
		},
		...detailColumnDefs.filter(col => col.field !== 'productName'),
	],
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
	suppressMovableColumns: true,
	enableBrowserTooltips: true,
	localeText: AG_GRID_LOCALE_RU,
	getContextMenuItems: getContextMenuItems,
	getRowId: (params) => params.data.idAcceptanceQualityFoodCard,
	masterDetail: true,
	groupDefaultExpanded: 1,
	isRowMaster: (dataItem) => {
		return dataItem.childCards && dataItem.childCards.length !== 0
	},
	detailRowAutoHeight: true,
	detailCellRendererParams: {
		detailGridOptions: childCardsDetailGridOptions,
		getDetailRowData: (params) => {
			params.successCallback(params.data.childCards);
		},
	},
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
		cellDataType: false,
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
		resizable: !isMobileDevice(),
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
	suppressMovableColumns: isMobileDevice(),
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
	statusBar: {
		statusPanels: [
			{ statusPanel: 'agTotalAndFilteredRowCountComponent', align: 'left' },
		],
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
	defaultExcelExportParams: {
		processCellCallback: ({ value, formatValue }) => formatValue(value)
	}
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

	if (isObserver(role)) {
		snackbar.show('Недостаточно прав!')
		return
	}

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
						if (card.childCards) {
							return {
								...card,
								childCards: card.childCards.map(childCard => {
									if (childCard.idAcceptanceQualityFoodCard === cardData.idAcceptanceQualityFoodCard) {
										return recalculateCard(cardData)
									}
									return childCard
								})
							}
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
					cards = groupChildCards(cards)
				}
				gridOptions.api.applyTransaction({ update: [{ ...rowData, cards, }]})
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
	// поле было добавлено в обновлении, в котором реализовано
	// сохранение рассчитанных данных карточки в базе.
	// Можно использовать как индикатор до/после обновления
	const pcFactor = card.pcFactor
	console.log("🚀 ~ recalculateCard ~ pcFactor:", pcFactor)

	return pcFactor ? card : {
		...card,
		...recalculateDefects("internalDefectsQualityCardList", sampleSize, card.internalDefectsQualityCardList, card),
		...recalculateDefects("totalDefectQualityCardList", sampleSize, card.totalDefectQualityCardList, card),
		...recalculateDefects("lightDefectsQualityCardList", sampleSize, card.lightDefectsQualityCardList, card),
	}
}

// перемещение дочерних карточек в материнскую по idMotherCard
function groupChildCards(cards) {
	const map = new Map(cards.map(card => [card.idAcceptanceQualityFoodCard, { ...card }]))

	cards.forEach(card => {
		if (card.idMotherCard) {
			const mother = map.get(card.idMotherCard)
			mother.childCards = mother.childCards || []
			mother.childCards.push(map.get(card.idAcceptanceQualityFoodCard))
		}
	})

	return cards
		.filter(card => !card.idMotherCard)
		.map(card => map.get(card.idAcceptanceQualityFoodCard))
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
	// fillField('bodyTemp', card.bodyTemp)
	fillField('fruitTemp', card.fruitTemp)
	// fillField('appearanceEvaluation', card.appearanceEvaluation)
	// fillField('appearanceDefects', card.appearanceDefects)
	fillField('maturityLevel', card.maturityLevel)
	fillField('tasteQuality', card.tasteQuality)
	fillField('caliber', card.caliber)
	// fillField('stickerDescription', card.stickerDescription)
	fillField('cardInfo', card.cardInfo)
	document.querySelectorAll('.sampleSizeUnit').forEach((el) => (el.textContent = sampleSizeUnit))

	// Заполнение таблиц с дефектами
	fillDefectsTable('#internalDefectsList', card.internalDefectsQualityCardList, ['weight', 'sampleSizeInternalDefect', 'percentage', 'description'], card)
	fillDefectsTable('#lightDefectsList', card.lightDefectsQualityCardList, ['weight', 'percentage', 'description'], card)
	fillDefectsTable('#totalDefectsList', card.totalDefectQualityCardList, ['weight', 'percentage', 'percentageWithPC', 'description'], card)

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
function recalculateDefects(type, sampleSize, defects, cardData) {
	const { sampleSizeInternalDefect, isImport, unit, pcFactor } = cardData
	const withPC = unit !== "шт"

	const sampleSizeInternalDefectUsed = sampleSizeInternalDefect || sampleSize

	let totalWeight = 0
	let totalPercentage = 0
	let totalPercentageWithPC = 0

	const defaultPercentageFactor = 100
	const pcPercentageFactor = pcFactor ? pcFactor * 100 : null

	const updatedDefects = defects.map((defect) => {
		const weight = parseFloat(defect.weight) || 0
		totalWeight += weight
		if (type === "totalDefectQualityCardList") {
			const percentage = sampleSize ? getPercentage(weight, sampleSize, defaultPercentageFactor) : 0
			const percentageWithPC = calculatePercentageWithPC(withPC, sampleSize, defect, weight, percentage, isImport, pcPercentageFactor)
			totalPercentage += percentage
			totalPercentageWithPC += percentageWithPC
			return { ...defect, percentage: percentage, percentageWithPC: percentageWithPC }
		} else if (type === "internalDefectsQualityCardList") {
			const percentage = sampleSizeInternalDefectUsed ? getPercentage(weight, sampleSizeInternalDefectUsed, defaultPercentageFactor) : 0
			totalPercentage += percentage
			return { ...defect, percentage: percentage }
		} else {
			const percentage = sampleSize ? getPercentage(weight, sampleSize, defaultPercentageFactor) : 0
			totalPercentage += percentage
			return { ...defect, percentage: percentage }
		}
	})

	return {
		[type]: updatedDefects,
		...(type === "internalDefectsQualityCardList" && { totalInternalDefectWeight: roundNumber(totalWeight, 100), totalInternalDefectPercentage: roundNumber(totalPercentage, 100) }),
		...(type === "totalDefectQualityCardList" && { totalDefectWeight: roundNumber(totalWeight, 100), totalDefectPercentage: roundNumber(totalPercentage, 100), totalDefectPercentageWithPC: roundNumber(totalPercentageWithPC, 100) }),
		...(type === "lightDefectsQualityCardList" && { totalLightDefectWeight: roundNumber(totalWeight, 100), totalLightDefectPercentage: roundNumber(totalPercentage, 100) }),
	}
}
function calculatePercentageWithPC(
	withPC,
	sampleSize,
	defect,
	weight,
	percentage,
	isImport,
	pcPercentageFactor
) {
	if (!withPC || !sampleSize) return 0

	if (defect && defect.pcCheck !== false) {
		if (pcPercentageFactor) {
			return getPercentage(weight, sampleSize, pcPercentageFactor)
		} else {
			return getPercentageWithPC(weight, sampleSize, percentage, isImport)
		}
	}

	return percentage
}
function getPercentageWithPC(weight, sampleSize, percentage, isImport) {
	const pcThreshold = 10 // порог для ПК (%)
	const pcPercentageFactorBeforeTreshold = isImport ? 160 : 140 // процент ПК при браке до 10%
	const pcPercentageFactorAftertTreshold = 200 // процент ПК при браке свыше 10%
	return percentage <= pcThreshold
			? getPercentage(weight, sampleSize, pcPercentageFactorBeforeTreshold)
			: getPercentage(weight, sampleSize, pcPercentageFactorAftertTreshold)
}
function getPercentage(weight, sampleSize, percentageFactor) {
	const percentage = roundNumber(((weight / sampleSize) * percentageFactor), 100)
	return percentage
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
function fillDefectsTable(tableId, defects, columns, card) {
	const sampleSizeUnit = tableId === '#internalDefectsList' ? 'шт' : card.unit === 'шт' ? 'шт' : 'кг'
	const sampleSizeInternalDefect = card.sampleSizeInternalDefect === null || card.sampleSizeInternalDefect === undefined
		? card.sampleSize : card.sampleSizeInternalDefect
	const $tableBody = $(tableId)
	$tableBody.empty()
	
	defects.forEach(defect => {
		const $row = $('<tr>')
		columns.forEach(col => {
			if (col === 'weight') $row.append($('<td>').text(`${defect[col]} ${sampleSizeUnit}`))
			else if (col === 'sampleSizeInternalDefect') $row.append($('<td>').text(`${sampleSizeInternalDefect} шт`))
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
			? `ВД: ${card.totalInternalDefectWeight} шт / ${card.totalInternalDefectPercentage}%`
			: '',
		card.totalDefectWeight
			? `Брак: ${card.totalDefectWeight} ${card.unit} / ${card.totalDefectPercentage}% / ${card.totalDefectPercentageWithPC}%` : '',
		card.totalLightDefectWeight
			? `ЛН: ${card.totalLightDefectWeight} ${card.unit} / ${card.totalLightDefectPercentage}%` : '',
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
		case 158:
			return 'Требуется дополнительная выборка (своими силами)'
		case 160:
			return 'Принята с дополнительной выборкой (силами поставщика)'
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

	if (e.buttonId === 'downloadImages') {
		downloadImagesAsZip(params.data.images)
		return
	}
}

function updateTableRow(gridOptions, rowData) {
	gridOptions.api.applyTransactionAsync(
		{ update: [rowData] }
	)
}

// скачивание архива картинок
async function downloadImagesAsZip(imageUrls) {
	const zip = new JSZip()

	bootstrap5overlay.showOverlay()
  
	const fetchPromises = imageUrls.map(async (url, i) => {
		try {
			const response = await fetch(url)
			const contentType = response.headers.get('Content-Type')
			const contentDisposition = response.headers.get('Content-Disposition')
			const ext = getExtensionFromContentType(contentType)
			const blob = await response.blob()
			let filename = getFilenameFromDisposition(contentDisposition)
			if (!filename) {
				filename = `image_${i + 1}${ext}`
			}
			zip.file(filename, blob)
		} catch (err) {
			bootstrap5overlay.hideOverlay()
			console.error('Ошибка загрузки файла:', url, err)
		}
	})

	await Promise.all(fetchPromises)

	const zipBlob = await zip.generateAsync({ type: 'blob' })
	saveAs(zipBlob, 'images.zip')

	bootstrap5overlay.hideOverlay()
}

// определение типа файла
function getExtensionFromContentType(contentType) {
	const map = {
		'image/jpeg': '.jpg',
		'image/jpg': '.jpg',
		'image/png': '.png',
		'image/gif': '.gif',
		'image/webp': '.webp',
		'image/bmp': '.bmp',
		'image/x-ms-bmp': '.bmp',
		'image/svg+xml': '.svg',
		'image/tiff': '.tiff',
		'image/x-icon': '.ico',
		'image/avif': '.avif',
		'image/heic': '.heic'
	};
	return map[contentType] || '.jpg'
}

// получение наименования файла
function getFilenameFromDisposition(contentDisposition) {
	if (!contentDisposition) return null

	const filenameMatch = contentDisposition.match(/filename[^;=\n]*=((['"]).*?\2|[^;\n]*)/)
	if (filenameMatch != null) {
		let filename = filenameMatch[1]
		return filename.replace(/['"]/g, '')
	}
	return null
}
