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
		date: '2024-10-07',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-08',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-09',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-10',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-11',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-12',
		maxPall: {
			externalMovement: 1400,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-13',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-14',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-15',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-16',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-17',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-18',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-19',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-20',
		maxPall: {
			externalMovement: 1425,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-21',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-22',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-23',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-24',
		maxPall: {
			externalMovement: 1600,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-25',
		maxPall: {
			externalMovement: 1500,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-26',
		maxPall: {
			externalMovement: 1400,
			internalMovement: 500,
		},
	},
	{
		stockId: '1700',
		date: '2024-10-27',
		maxPall: {
			externalMovement: 1300,
			internalMovement: 500,
		},
	},
]