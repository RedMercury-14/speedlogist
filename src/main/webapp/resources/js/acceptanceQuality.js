import { AG_GRID_LOCALE_RU } from './AG-Grid/ag-grid-locale-RU.js'
import { BtnCellRenderer, BtnsCellRenderer, gridColumnLocalState, gridFilterLocalState, ResetStateToolPanel } from './AG-Grid/ag-grid-utils.js'
import { getAllAcceptanceQualityFoodCardUrl, getClosedAcceptanceQualityBaseUrl } from './globalConstants/urls.js'
import { snackbar } from './snackbar/snackbar.js'
import { dateHelper, debounce, getData } from './utils.js'
import PhotoSwipeLightbox from './photoSwipe/photoswipe-lightbox.esm.min.js'
import PhotoSwipeDynamicCaption  from './photoSwipe/photoswipe-dynamic-caption-plugin.esm.js'
import PhotoSwipe from './photoSwipe/photoswipe.esm.min.js'
import { buttons, caption, thumbnails } from './photoSwipe/photoSwipeHelper.js'
import { bootstrap5overlay } from './bootstrap5overlay/bootstrap5overlay.js'
import { uiIcons } from './uiIcons.js'

const PAGE_NAME = 'acceptanceQuality'
const LOCAL_STORAGE_KEY = `AG_Grid_settings_to_${PAGE_NAME}`
const DATES_KEY = `searchDates_to_${PAGE_NAME}`

const debouncedSaveColumnState = debounce(saveColumnState, 300)
const debouncedSaveFilterState = debounce(saveFilterState, 300)

let lightbox

// форма отправки действия по карточке для строк таблицы
class cardActionCellRenderer {
	init(params) {
		this.params = params
		this.cardAction = params.data.cardAction || ''

		this.cardActionSpan = document.createElement('span')
		this.cardActionSpan.textContent = this.cardAction

		this.eGui = document.createElement("form")
		this.eGui.className = 'form-inline'
		this.eGui.innerHTML = `
			<input type="hidden" name="idAcceptanceQualityFoodCard" value="${params.data.idAcceptanceQualityFoodCard}">
			<div class="form-group mr-2">
				<label for="cardAction" class="sr-only">Действие</label>
				<select name="cardAction" class="form-control form-control-sm" required>
					<option value="" selected hidden disabled>Выберите действие</option>
					<option value="confirm">Принять товар</option>
					<option value="rework">На переборку</option>
					<option value="unconfirm">Не принимать товар</option>
				</select>
			</div>
			<button type="submit" class="btn btn-primary btn-sm">${uiIcons.check}</button>
		`

		this.formSubmitHandler = this.formSubmitHandler.bind(this)
		this.eGui.addEventListener("submit", this.formSubmitHandler)
	}

	getGui() {
		return this.cardAction ? this.cardActionSpan : this.eGui
	}

	formSubmitHandler(event) {
		this.params.onSubmit(event)
	}

	destroy() {
		this.eGui.removeEventListener("submit", this.formSubmitHandler)
	}
}

const detailColumnDefs = [
	{ headerName: 'Продукт', field: 'productName', flex: 5, },
	{
		headerName: 'Выборка', field: 'sampleSize',
		valueFormatter: (params) => `${params.value} кг`
	},
	{
		headerName: 'ВД (вес/процент)', field: 'totalInternalDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalInternalDefectWeight} кг / ${data.totalInternalDefectPercentage}%`
		},
	},
	{
		headerName: 'Брак (вес/процент/процент с ПК)', field: 'totalDefectPercentage',
		flex: 3,
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalInternalDefectWeight} кг / ${data.totalDefectPercentage}% / ${data.totalDefectPercentageWithPC}%`
		},
	},
	{
		headerName: 'ЛН (вес/процент)', field: 'totalLightDefectPercentage',
		valueGetter: (params) => {
			const data = params.data
			return `${data.totalLightDefectWeight} кг / ${data.totalLightDefectPercentage}%`
		},
	},
	{
		headerName: '', field: 'idAcceptanceQualityFoodCard',
		cellClass: 'px-1 py-0 text-center small-row',
		minWidth: 100, flex: 1,
		cellRenderer: BtnsCellRenderer,
		cellRendererParams: {
			onClick: ((e, params) => {
				if (e.buttonId === 'showImages') {
					showGalleryItems(params.data)
					return
				}
		
				if (e.buttonId === 'showInfo') {
					showCardModal(params.data)
					return
				}
			}),
			buttonList: [
				{ className: 'btn btn-light border btn-sm', id: 'showImages', icon: uiIcons.images, title: 'Показать фото' },
				{ className: 'btn btn-light border btn-sm', id: 'showInfo', icon: uiIcons.info, title: 'Подробнее' },
			],
		},
	},
	{
		headerName: 'Статус карточки', field: 'idAcceptanceQualityFoodCard',
		cellClass: 'px-1 py-0 text-center small-row',
		minWidth: 240, flex: 3,
		cellRenderer: cardActionCellRenderer,
		cellRendererParams: {
			onSubmit: cardActionFormSubmitHandler
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
	// листнер на отправку формы установки статуса карточки
	cardActionForm.addEventListener('submit', cardActionFormSubmitHandler)
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
function cardActionFormSubmitHandler(e) {
	e.preventDefault()
	const formData = new FormData(e.target)
	const data = Object.fromEntries(formData)

	console.log(data)
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

	// отображение формы действия по карточке
	card.cardAction
		? cardActionForm.classList.add('d-none')
		: cardActionForm.classList.remove('d-none')
	cardActionForm.reset()

	// заполнение полей
	cardActionForm.idAcceptanceQualityFoodCard.value = card.idAcceptanceQualityFoodCard
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

	// кнопка просмотра фото
	const showImagesBtnContainer = document.getElementById('showImagesBtnContainer')
	showImagesBtnContainer.innerHTML = ''
	const showImagesBtn = document.createElement('button')
	showImagesBtn.className = 'btn btn-secondary'
	showImagesBtn.type = 'button'
	showImagesBtn.textContent = 'Посмотреть фото'
	showImagesBtn.onclick = (e) => showGalleryItems(card)
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