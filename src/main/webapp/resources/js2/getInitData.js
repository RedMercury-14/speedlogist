import {
	getActsBaseUrl,
	getAhoRouteBaseUrl,
	getAhoRouteForCarrierBaseUrl,
	getAllCarrierUrl,
	getAllShopsUrl,
	getOrderBaseUrl,
	getOrdersForLogistBaseUrl,
	getOrdersForSlotsBaseUrl,
	getOrdersForStockProcurementBaseUrl,
	getOrlNeedBaseUrl,
	getPalletsCalculatedBaseUrl,
	getPermissionListBaseUrl,
	getReport398List,
	getRoutesBaseUrl,
	getScheduleRCUrl,
	getStockRemainderUrl,
	getUnicContractCodeHasCounterpartyTOUrl
} from "./globalConstants/urls.js"
import { slotsSettings } from "./globalRules/slotsRules.js"
import { getDatesToSlotsFetch } from "./slots/dataUtils.js"
import { dateHelper, getData, isCarrier, isStockProcurement } from "./utils.js"

const carrentUrl = window.location.href
const initDataUrl = getInitDataUrl(carrentUrl)

// получение и сохранение стартовых данных
initDataUrl && getData(initDataUrl).then((res) => successCallback(res))


function successCallback(response) {
	if (carrentUrl.includes('logistics/maintenance')) {
		getData(getAllCarrierUrl).then(carriers => {
			window.initData = { carriers, routes: response.body }
			const initEvent = new Event('initDataLoaded')
			document.dispatchEvent(initEvent)
		})
		return
	}

	// по умолчанию
	window.initData = response || []
	const initEvent = new Event('initDataLoaded')
	document.dispatchEvent(initEvent)
}


function getInitDataUrl(url) {
	// Контроль заявок
	if (url.includes('procurement/orders') && !url.includes('ordersBalance')) {
		const PAGE_NAME = 'ProcurementControl'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const role = document.head.querySelector("meta[name='role']").content
		const baseUrl = isStockProcurement(role) ? getOrdersForStockProcurementBaseUrl : getOrderBaseUrl
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
		return `${baseUrl}${dateStart}&${dateEnd}`
	}

	// Менеджер заявок
	if (url.includes('logistics/ordersLogist')) {
		const PAGE_NAME = 'ProcurementControlLogist'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
		return`${getOrdersForLogistBaseUrl}${dateStart}&${dateEnd}`
	}

	// New Менеджер международных маршрутов
	if (url.includes('logistics/internationalNew')) {
		const PAGE_NAME = 'internationalManagerNew'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)
		return`${getRoutesBaseUrl}${dateStart}&${dateEnd}`
	}

	// Список перевозчиков
	if(url.includes('logistics/internationalCarrier')) {
		return getAllCarrierUrl
	}

	// Список магазинов
	if (url.includes('logistics/shopControl')) {
		return getAllShopsUrl
	}

	// Менеджер маршрутов АХО/СГИ
	if (url.includes('logistics/maintenance')) {
		const PAGE_NAME = 'maintenanceList'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const role = document.head.querySelector("meta[name='role']").content
		const getBaseUrl = isCarrier(role) ? getAhoRouteForCarrierBaseUrl : getAhoRouteBaseUrl
		const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)
		return `${getBaseUrl}${dateStart}&${dateEnd}`
	}

	// График поставок на ТО
	if (url.includes('delivery-schedule-to')) {
		return getUnicContractCodeHasCounterpartyTOUrl
	}

	// График поставок на РЦ
	if (url.includes('delivery-schedule') && !url.includes('delivery-schedule-to')) {
		return getScheduleRCUrl
	}

	// Остаток товара на складах
	if (url.includes('order-support/orders') || url.includes('procurement/ordersBalance')) {
		return getStockRemainderUrl
	}

	// Потребности
	if (url.includes('orl/need')) {
		const filterDate = dateHelper.getDateForInput(new Date())
		return `${getOrlNeedBaseUrl}${filterDate}`
	}

	// Слоты
	if (url.includes('slots') && !url.includes('delivery-schedule')) {
		const { startDateStr, endDateStr } = getDatesToSlotsFetch(
			slotsSettings.DAY_COUNT_BACK,
			slotsSettings.DAY_COUNT_FORVARD
		)
		return `${getOrdersForSlotsBaseUrl}${startDateStr}&${endDateStr}`
	}

	// Маршрутизатор
	if (url.includes('logistics-delivery/router') || url.includes('depot')) {
		return getAllShopsUrl
	}

	// Приход паллет
	if (url.includes('procurement/calculated') || url.includes('orl/calculated')) {
		const PAGE_NAME = 'orlCalculated'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 7)
		return`${getPalletsCalculatedBaseUrl}${dateStart}&${dateEnd}`
	}

	// 398 отчет
	if (url.includes('orl/report/398')) {
		return getReport398List
	}

	// Список контрагентов
	if (url.includes('logistics/counterpartiesList')) {
		return getAllShopsUrl
	}

	// История решений по заказам
	if (url.includes('permission/list')) {
		const PAGE_NAME = 'permissionList'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 14, 0)
		return`${getPermissionListBaseUrl}${dateStart}&${dateEnd}`
	}

	// Архив актов
	if (url.includes('documentflow/documentlist')) {
		const PAGE_NAME = 'documentlist'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 14, 0)
		return`${getActsBaseUrl}${dateStart}&${dateEnd}`
	}

	return ''
}