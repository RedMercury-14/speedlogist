/**
 * Ограничения паллетовместимости для слотов.
 * Указывается номер склада, дата и максимальное количество паллет для стандартных поставок и внутренних перемещений.
 * По умолчанию используется максимальная паллетовместимость склада.
 * 
 * Обозначения:
 * - `stockId` - номер склада, строка
 * - `date` - дата ограничения, строка в формате YYYY-MM-DD
 * - `maxPall` - объект для указания паллетовместимости
 *   - `externalMovement` - максимальное количество паллет для обычных поставок, целое число
 *   - `internalMovement` - максимальное количество паллет для внутренних перемещений, целое число
  */
export const MAX_PALL_RESTRICTIONS = [
	// ------------------------------------
	// ------------ 1700 склад ------------
	// ------------------------------------
	


	// ------------------------------------
	// ------------ 1800 склад ------------
	// ------------------------------------
	{
		stockId: '1800',
		date: '2024-12-21',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-22',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-23',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-24',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-25',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-26',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-27',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-28',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	{
		stockId: '1800',
		date: '2024-12-29',
		maxPall: {
			externalMovement: 1200,
			internalMovement: 500,
		},
	},
	


	// ------------------------------------
	// ------------ 1100 склад ------------
	// ------------------------------------
	
]