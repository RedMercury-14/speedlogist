export const userMessages = {
	start: 'Для начала работы выберите склад',
	dateDropError: (unloadDate) => `Невозможно установить заказ на эту дату! Минимальная дата выгрузки ${unloadDate}`,
	dateConfirmError: (unloadDate) => `Невозможно подтвердить заказ на эту дату! Минимальная дата выгрузки ${unloadDate}`,
	pallDropError: 'Невозможно установить поставку: паллетовместимость склада превышена!',
	eventRemove: 'Поставка удалена с рампы склада. Можете установить поставку снова',
	checkEventId: 'Заказ с таким номером уже добавлен!',
	orderNotFound: 'Заказ с таким номером не найден!',
	actionNotCompleted: 'Не удалось выполнить действие, обновите страницу!',
	operationNotAllowed: 'Данная операция запрещена, недостаточно прав!',
	messageLogistIsShort: 'Сообщение должно быть не менее 10 символов!',
	shiftChangeError: 'Невозможно установить заказ на это время! Время выгрузки заказа пересекается со временем пересменки склада!',
	internalMovementTimeError: (stock) => `Невозможно установить заказ на это время! Время с ${stock.internaMovementsTimes[0]} до ${stock.internaMovementsTimes[1]} на рампе ${stock.internalMovementsRamps[0]} зарезервировано для внутренних перемещений!`,
	internalMovementTimeForLogistError: (stock) => `Невозможно установить заказ на это время! Время с ${stock.internaMovementsTimes[0]} до ${stock.internaMovementsTimes[1]} на рампе ${stock.internalMovementsRamps[0]} зарезервировано!`,
	orderNotForSlot: 'Данный заказ не нуждается в слотах - проверьте склад в Маркете',
	isScheduleMatch: 'Поставка соответствует графику',
	isScheduleNotMatch: 'Поставка не соответствует графику',
	contractCodeNotFound: 'Не найден код контракта в заказе',
	errorReadingSchedule: 'Ошибка при чтении графика поставок',
	contractCodeIsMissing: 'Код контракта отсутствует в графике поставок',
}

export const slotStocks = ['1700','1200','1230','1214', '1240','1250','1100']

export const eventColors = {
	disabled: '#65979f',
	default: '#0195ae',
	borderColor: '#0e377b',
	foundEvent: '#ff8100'
}

export const roles = ['manager', 'admin']
export const adminLogins = [ "catalina!%ricoh" ]

export const getOrdersForSlotsBaseUrl = '../api/manager/getOrdersForSlots4/'
export const preloadOrderUrl = `../api/slot/preload`
export const loadOrderUrl = `../api/slot/load`
export const updateOrderUrl = `../api/slot/update`
export const deleteOrderUrl = `../api/slot/delete`
export const confirmSlotUrl = `../api/slot/save`
export const editMarketInfoBaseUrl = `../api/manager/editMarketInfo/`
export const getMarketOrderUrl = `../api/manager/getMarketOrder/`
export const checkScheduleBaseUrl = '../api/slots/delivery-schedule/checkSchedule/'
export const checkSlotBaseUrl = '../api/slot/getTest/'
export const checkBookingBaseUrl = '../api/manager/testMarketOrderStatus/'
export const setOrderLinkingUrl = '../api/slots/order-linking/set'

export const messagePattern = {
	idMessage: null,
	fromUser: null,
	toUser: null,
	text: null,
	idRoute: null,
	idOrder: null,
	status: '300',
	companyName: null,
	comment: null,
	datetime: null,
	currency: null,
	url: null,
	nds: null,
	fullName: null,
	ynp: null,
	date: null,
	action: null,
	payload: null
}
