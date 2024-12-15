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
	{
		stockId: '1700',
		date: '2024-11-01',
		maxPall: {
			externalMovement: 1400,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-11-02',
		maxPall: {
			externalMovement: 1400,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-13',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-14',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-15',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-16',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-17',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-18',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-19',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-20',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-12-21',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},


	// ------------------------------------
	// ------------ 1100 склад ------------
	// ------------------------------------
	{
		stockId: '1100',
		date: '2024-11-29',
		maxPall: {
			externalMovement: 90,
			internalMovement: 500,
		},
	},
	{
		stockId: '1100',
		date: '2024-11-30',
		maxPall: {
			externalMovement: 90,
			internalMovement: 500,
		},
	},
	{
		stockId: '1100',
		date: '2024-12-01',
		maxPall: {
			externalMovement: 90,
			internalMovement: 500,
		},
	},
]