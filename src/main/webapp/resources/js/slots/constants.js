export const Draggable = FullCalendar.Draggable

export const slotsSettings = {
	DAY_COUNT_BACK: 30,    // выборка дней назад для получения заказов
	DAY_COUNT_FORVARD: 60,    // выборка дней вперед для получения заказов
	LOGIST_MESSAGE_MIN_LENGHT: 10,    // минимальная длина сообщения от логиста
	PALL_CHART_DATA_DAY_COUNT: 14,    // кол-во дней для отрисовки графика паллетовместимости
	UNLOAD_DATE_HOUR_DELAY_FOR_RB: 4,    // задержка для расчета времени ДЛЯ РБ выгрузки в зависимости от даты загрузки
	UNLOAD_DATE_HOUR_DELAY_FOR_IMPORT: 24,    // задержка для расчета времени ДЛЯ ИМПОРТА выгрузки в зависимости от даты загрузки
}

export const userMessages = {
	start: 'Для начала работы выберите склад',
	dateDropError: (unloadDate) => `Невозможно установить заказ на эту дату! Минимальная дата выгрузки ${unloadDate}`,
	pallDropError: 'Невозможно установить поставку: паллетовместимость склада превышена!',
	eventRemove: 'Поставка удалена с рампы склада. Можете установить поставку снова',
	checkEventId: 'Заказ с таким номером уже добавлен!',
	orderNotFound: 'Заказ с таким номером не найден!',
	actionNotCompleted: 'Не удалось выполнить действие, обновите страницу!',
	operationNotAllowed: 'Данная операция запрещена, недостаточно прав!',
	messageLogistIsShort: 'Сообщение должно быть не менее 10 символов!',
	shiftChangeError: 'Невозможно установить заказ на это время! Время выгрузки заказа пересекается со временем пересменки склада!',
	internalMovementTimeError: 'Невозможно установить заказ на это время! Время с 12:00 до 20:00 на 1 рампе 1700 склада зарезервировано для внутренних перемещений!',
	orderNotForSlot: 'Данный заказ не нуждается в слотах - проверьте склад в Маркете',
	isScheduleMatch: 'Поставка соответствует графику',
	isScheduleNotMatch: 'Поставка не соответствует графику',
	contractCodeNotFound: 'Не найден код контракта в заказе',
	errorReadingSchedule: 'Ошибка при чтении графика поставок',
	contractCodeIsMissing: 'Код контракта отсутствует в графике поставок',
}

export const slotStocks = ['1700','1200','1230','1214','1250','1100']

export const eventColors = {
	disabled: '#65979f',
	default: '#0195ae',
	borderColor: '#0e377b',
}

export const roles = ['manager', 'admin']
export const adminLogins = [ "catalina!%ricoh" ]

export const getOrdersForSlotsBaseUrl = '../api/manager/getOrdersForSlots4/'
export const loadOrderUrl = `../api/slot/load`
export const updateOrderUrl = `../api/slot/update`
export const deleteOrderUrl = `../api/slot/delete`
export const confirmSlotUrl = `../api/slot/save`
export const editMarketInfoBaseUrl = `../api/manager/editMarketInfo/`
export const getMarketOrderUrl = `../api/manager/getMarketOrder/`
export const checkScheduleBaseUrl = '../api/slots/delivery-schedule/checkSchedule/'

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
