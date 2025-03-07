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
	noImportOverlapMessage: (noImportTimes) => `Невозможно установить заказ на это время! Импортные заказы запрещено устанавливать с ${noImportTimes[0]} до ${noImportTimes[1]}!`
}

export const slotStocks = ['1700', '1800', '1200','1230','1214', '1240','1250','1100']

export const stocks24h = ['1700', '1800']

export const eventColors = {
	disabled: '#65979f',
	default: '#0195ae',
	borderColor: '#0e377b',
	foundEvent: '#ff8100'
}

export const roles = ['manager', 'admin']
export const adminLogins = [ "catalina!%ricoh" ]

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

export const routeStatusText = {
	0:'Тендер ожидает подтверждения на бирже',
	1:'Тендер на бирже, идут торги',
	2:'Тендер завершен, нет машины',
	3:'Тендер завершен, нет водителя',
	4:'Тендер завершен, машина и водитель приняты',
	5:'Тендер отменен',
	6:'Перевозка закончена',
	7:'Маршрут закрыт',
	8:'Маршрут условно подтвержден (один перевозчик)',
	9:'Маршрут удален',
	10:'Маршрут в архиве',
	200:'Ожидает назначения перевозчика',
	210:'Перевозчик назначен',
	220:'Указан пробег',
	225:'Указан пробег и стоимость перевозки',
	230:'Завершен',
}

export const routeStatusColor = {
	0:'#ffffb2',
	1:'#ddfadd',
	2:'#b2d9b2',
	3:'#b2d9b2',
	4:'#b2d9b2',
	5:'#ffb2b2',
	6:'#b2e7ff',
	7:'#b2e7ff',
	8:'#ddfadd',
	9:'#d9d9d9',
	10:'#ddfadd',
	200:'#494f5252',
	210:'#ffffff',
	220:'#dce37266',
	225:'#c4ffe1db',
	230:'#9ee9ffdb',
}

// фоновые зоны в слотах
export const backgroundEvents = [
	// зона внутренних перемещений
	{
		id: `internalMovementZone1700`,
		resourceId: `170001`,
		display: 'background',
		startTime: '09:00',
		endTime: '20:00',
		eventOverlap: true,
		title: '',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},
	{
		id: `internalMovementZone1800`,
		resourceId: `180001`,
		display: 'background',
		startTime: '09:00',
		endTime: '20:00',
		eventOverlap: true,
		title: '',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},


	// зона отмены проверки паллетовместимости
	{
		id: `extraPallZone180001`,
		resourceId: `180001`,
		display: 'background',
		startTime: '00:00',
		endTime: '07:00',
		eventOverlap: true,
		title: '',
		backgroundColor: '#03A9F4',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},
	{
		id: `extraPallZone180002`,
		resourceId: `180002`,
		display: 'background',
		startTime: '00:00',
		endTime: '07:00',
		eventOverlap: true,
		title: '',
		backgroundColor: '#03A9F4',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},
	{
		id: `extraPallZone180003`,
		resourceId: `180003`,
		display: 'background',
		startTime: '00:00',
		endTime: '07:00',
		eventOverlap: true,
		title: '',
		backgroundColor: '#03A9F4',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},
	{
		id: `extraPallZone180004`,
		resourceId: `180004`,
		display: 'background',
		startTime: '00:00',
		endTime: '07:00',
		eventOverlap: true,
		title: '',
		backgroundColor: '#03A9F4',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},
	{
		id: `extraPallZone180005`,
		resourceId: `180005`,
		display: 'background',
		startTime: '00:00',
		endTime: '07:00',
		eventOverlap: true,
		title: '',
		backgroundColor: '#03A9F4',
		extendedProps: {
			data: {
				pall: 0,
			}
		},
	},
]
