/**
 * Правила для расчета минимальной времени выгрузки в зависимости от времени загрузки.
 * В данных правилах указывается количество часов, которое должно пройти с момента последней загрузки
 * Данные указываются в часах.
 * Правила по умолчанию указаны в поле `default`.
 */
export const RULES_FOR_MIN_UNLOAD_TIME = {
	default: 4,
	'АХО': 4,
	'РБ': 4,
	'Импорт': 4,
	'Экспорт': 24,
}