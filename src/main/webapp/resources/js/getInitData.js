import { slotsSettings } from "./globalRules/slotsRules.js"
import { getOrdersForSlotsBaseUrl } from "./slots/constants.js"
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
	if (url.includes('delivery-schedule-to')) {
		return '../../api/slots/delivery-schedule/getUnicContractCodeHasCounterpartyTO'
	}

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

	// приход паллет
	if (url.includes('procurement/calculated') || url.includes('orl/calculated')) {
		const baseUrl ='../../api/get-pallets/'
		const PAGE_NAME = 'orlCalculated'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 7, 7)
		return`${baseUrl}${dateStart}&${dateEnd}`
	}

	// 398 отчет
	if (url.includes('orl/report/398')) {
		return '../../../api/orl/task/getlist'
	}

	// Список контрагентов
	if (url.includes('logistics/counterpartiesList')) {
		return '../../api/manager/getAllShops'
	}

	// История решений по заказам
	if (url.includes('permission/list')) {
		const baseUrl ='../../../api/procurement/permission/getList/'
		const PAGE_NAME = 'permissionList'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 14, 0)
		return`${baseUrl}${dateStart}&${dateEnd}`
	}

	// Архив актов
	if (url.includes('documentflow/documentlist')) {
		const baseUrl ='../../../api/logistics/documentflow/documentlist/'
		const PAGE_NAME = 'documentlist'
		const DATES_KEY = `searchDates_to_${PAGE_NAME}`
		const { dateStart, dateEnd } = dateHelper.getDatesToFetch(DATES_KEY, 14, 0)
		return`${baseUrl}${dateStart}&${dateEnd}`
	}

	return ''
}