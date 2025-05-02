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


	// ------------------------------------
	// ------------ 1200 склад ------------
	// ------------------------------------
	{
		stockId: '1200',
		date: '2025-04-26',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-04-27',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-04-28',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-04-29',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-04-30',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-05-01',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-05-02',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-05-03',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},
	{
		stockId: '1200',
		date: '2025-05-04',
		maxPall: {
			externalMovement: 500,
			internalMovement: 400,
		},
	},


	// ------------------------------------
	// ------------ 1100 склад ------------
	// ------------------------------------
	
]