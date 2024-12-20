import { RULES_FOR_MIN_UNLOAD_TIME } from "./minUnloadTimeRules.js"

export const slotsSettings = {
	DAY_COUNT_BACK: 15,    // выборка дней назад для получения заказов
	DAY_COUNT_FORVARD: 120,    // выборка дней вперед для получения заказов

	LOGIST_MESSAGE_MIN_LENGHT: 10,    // минимальная длина сообщения от логиста

	PALL_CHART_DATA_DAY_COUNT: 14,    // кол-во дней для отрисовки графика паллетовместимости

	UNLOAD_DATE_HOUR_DELAY_FOR_RB: RULES_FOR_MIN_UNLOAD_TIME['РБ'],    // задержка для расчета времени ДЛЯ РБ выгрузки в зависимости от даты загрузки
	UNLOAD_DATE_HOUR_DELAY_FOR_IMPORT: RULES_FOR_MIN_UNLOAD_TIME['Импорт'],    // задержка для расчета времени ДЛЯ ИМПОРТА выгрузки в зависимости от даты загрузки

	MOVE_ORDERS_DATE_START_FACTOR: 2,    // изменение начальной даты от сегодня для отчета по перемещениям слотов между 1700 и 1800
	MOVE_ORDERS_DATE_END_FACTOR: 3,    // изменение конечной даты от сегодня для отчета по перемещениям слотов между 1700 и 1800
}

// причины переноса слота
export const reasonForUpdate = [
	'Перенос по просьбе УЗ',
	'Перенос по просьбе менеджера ОЗ',
	'Перенос загрузки под имеющееся авто',
	'Задержка на загрузке',
	'Отмена авто',
	'Простой авто на нашем складе, не успевают забрать второй рейс',
	'Иное',
]

// фоновые зоны в слотах
export const backgroundEvents = [
	// зона внутренних перемещений
	{
		id: `internalMovementZone1700`,
		resourceId: `170001`,
		display: 'background',
		startTime: '12:00',
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
]

