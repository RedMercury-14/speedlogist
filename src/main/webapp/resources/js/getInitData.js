import { getOrdersForSlotsBaseUrl, slotsSettings } from "./slots/constants.js"
import { getDatesToSlotsFetch } from "./slots/dataUtils.js"
import { dateHelper, getData, isCarrier, isLogisticsDeliveryPage, isStockProcurement } from "./utils.js"

const carrentUrl = window.location.href
const initDataUrl = getInitDataUrl(carrentUrl)

// получение и сохранение стартовых данных
initDataUrl && getData(initDataUrl).then((res) => successCallback(res))


function successCallback(response) {
	if (carrentUrl.includes('logistics/maintenance')) {
		const getAllCarrierUrl = `../../api/manager/getAllCarrier`
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
		const getDefaultOrderBaseUrl ='../../api/manager/getOrders/'
		const getOrdersForStockProcurementBaseUrl ='../../api/manager/getOrdersForStockProcurement/'
		const role = document.head.querySelector("meta[name='role']").content
		const getOrderBaseUrl = isStockProcurement(role) ? getOrdersForStockProcurementBaseUrl : getDefaultOrderBaseUrl
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
		return `${getOrderBaseUrl}${dateStart}&${dateEnd}`
	}

	// Менеджер заявок
	if (url.includes('logistics/ordersLogist')) {
		const PAGE_NAME = 'ProcurementControlLogist'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const getOrderBaseUrl ='../../api/manager/getOrdersForLogist/'
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY)
		return`${getOrderBaseUrl}${dateStart}&${dateEnd}`
	}

	// New Менеджер международных маршрутов
	if (url.includes('logistics/internationalNew')) {
		const PAGE_NAME = 'internationalManagerNew'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const getRouteBaseUrl = '../../api/manager/getRouteForInternational/'
		const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)
		return`${getRouteBaseUrl}${dateStart}&${dateEnd}`
	}

	// Список перевозчиков
	if(url.includes('logistics/internationalCarrier')) {
		return `../../api/manager/getAllCarrier`
	}

	// Список магазинов
	if (url.includes('logistics/shopControl')) {
		return '../../api/manager/getAllShops'
	}

	// Менеджер маршрутов АХО/СГИ
	if (url.includes('logistics/maintenance')) {
		const PAGE_NAME = 'maintenanceList'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const role = document.head.querySelector("meta[name='role']").content
		const methodBase = isCarrier(role) ? 'carrier' : 'logistics'
		const getAhoRouteBaseUrl = `../../api/${methodBase}/getMaintenanceList/`
		const { dateStart, dateEnd } = dateHelper.getDatesToRoutesFetch(DATES_KEY)
		return `${getAhoRouteBaseUrl}${dateStart}&${dateEnd}`
	}

	// График поставок на ТО
	// if (url.includes('delivery-schedule-to')) {
	// 	return '../../api/slots/delivery-schedule/getListTO'
	// }

	// График поставок на РЦ
	if (url.includes('delivery-schedule') && !url.includes('delivery-schedule-to')) {
		return '../../api/slots/delivery-schedule/getListRC'
	}

	// Остаток товара на складах
	if (url.includes('order-support/orders') || url.includes('procurement/ordersBalance')) {
		return '../../api/order-support/getStockRemainder'
	}

	// Потребности
	if (url.includes('orl/need')) {
		const filterDate = dateHelper.getDateForInput(new Date())
		const getOrlNeedBaseUrl = `../../api/orl/need/getNeed/`
		return `${getOrlNeedBaseUrl}${filterDate}`
	}

	if (url.includes('slots') && !url.includes('delivery-schedule')) {
		const { startDateStr, endDateStr } = getDatesToSlotsFetch(
			slotsSettings.DAY_COUNT_BACK,
			slotsSettings.DAY_COUNT_FORVARD
		)
		return `${getOrdersForSlotsBaseUrl}${startDateStr}&${endDateStr}`
	}

	if (url.includes('logistics-delivery/router') || url.includes('depot')) {
		const apiUrl = isLogisticsDeliveryPage() ? '../../api/' : '../api/'
		return `${apiUrl}manager/getAllShops`
	}

	return ''
}