import { RULES_FOR_MIN_UNLOAD_TIME } from "./minUnloadTimeRules.js"

export const slotsSettings = {
	DAY_COUNT_BACK: 15,    // выборка дней назад для получения заказов
	DAY_COUNT_FORVARD: 120,    // выборка дней вперед для получения заказов

	LOGIST_MESSAGE_MIN_LENGHT: 10,    // минимальная длина сообщения от логиста

	PALL_CHART_DATA_DAY_COUNT: 14,    // кол-во дней для отрисовки графика паллетовместимости

	UNLOAD_DATE_HOUR_DELAY_FOR_RB: RULES_FOR_MIN_UNLOAD_TIME['РБ'],    // задержка для расчета времени ДЛЯ РБ выгрузки в зависимости от даты загрузки
	UNLOAD_DATE_HOUR_DELAY_FOR_IMPORT: RULES_FOR_MIN_UNLOAD_TIME['Импорт'],    // задержка для расчета времени ДЛЯ ИМПОРТА выгрузки в зависимости от даты загрузки
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