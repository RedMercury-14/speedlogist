/**
 * Правила для расчета минимальной даты выгрузки в зависимости от сегодняшнего дня.
 * Дата выгрузки рассчитывается исходя из текущего времени и правил, указанных в объекте `getMinUnloadDate`.
 * В данных правилах указывается время, до которого нужно успеть создать заявку, а также сдвиг в днях от сегодняшнего дня.
 * Правила по умолчанию указаны в объекте `default`.
 * 
 * Обозначения:
 * - `boundaryTime` - время, до которого нужно успеть создать заявку. Формат - строка вида HH:MM:SS
 * - `daysOffset` - объекс, содержащий сдвиги в днях в зависимости от дня недели и времени
 *   - `beforeBoundaryTime` - сдвиг в днях до `boundaryTime`
 *   - `afterBoundaryTime` - сдвиг в днях после `boundaryTime`
 *   - `specialCases` - массив специальных случаев, в которых нужно изменить сдвиг
 *     - `day` - день недели (0 - воскресенье, 1 - понедельник, ..., 6 - суббота)
 *     - `offset` - сдвиг в днях
 *     - `boundaryStatus` - статус сдвига ('before' или 'after'). Данный статус определяет,
 *                          до или после `boundaryTime` применять сдвиг для указанного дня.
 *                          Если не указан, то правило действует вне зависимости от `boundaryTime`.
 */
export const RULES_FOR_MIN_UNLOAD_DATE = {
	default: {
		boundaryTime: '11:00:00',
		daysOffset: {
			beforeBoundaryTime: 1, // если ДО 11:00, то через 1 день (на завтра)
			afterBoundaryTime: 2, // если ПОСЛЕ 11:00, то через 2 дня (на послезавтра)
			specialCases: [
				{ day: 5, offset: 3, boundaryStatus: 'before' }, // если пятница, ДО 12:00, то через 3 дня (на вторник)
				{ day: 5, offset: 4, boundaryStatus: 'after' }, // если пятница, ПОСЛЕ 12:00, то через 4 дня (на вторник)
				{ day: 6, offset: 3 }, // если суббота, то через 3 дня (на вторник)
				{ day: 0, offset: 2 }, // если воскресенье, то через 2 дня (на вторник)
			]
		}
	},

	// правила для перевозок АХО
	aho: {
		boundaryTime: '12:00:00',
		daysOffset: {
			beforeBoundaryTime: 1,
			afterBoundaryTime: 2,
			specialCases: [
				{ day: 4, offset: 4, boundaryStatus: 'after' },
				{ day: 5, offset: 3, boundaryStatus: 'before' },
				{ day: 5, offset: 4, boundaryStatus: 'after' },
				{ day: 6, offset: 3 },
				{ day: 0, offset: 2 },
			]
		}
	},

	// правила для перевозок Внутренних перемещений
	internalMovement: {
		boundaryTime: '11:00:00',
		daysOffset: {
			beforeBoundaryTime: 1,
			afterBoundaryTime: 2,
			specialCases: [
				{ day: 5, offset: 4, boundaryStatus: 'after' },
				{ day: 6, offset: 3 },
				{ day: 0, offset: 2 },
			]
		}
	},

	// правила для перевозок по РБ
	wayRB: {
		boundaryTime: '11:00:00',
		daysOffset: {
			beforeBoundaryTime: 1,
			afterBoundaryTime: 2,
			specialCases: [
				{ day: 5, offset: 4, boundaryStatus: 'after' },
				{ day: 6, offset: 3 },
				{ day: 0, offset: 2 },
			]
		}
	},

	// правила для Импорта
	wayImport: {
		boundaryTime: '12:00:00',
		daysOffset: {
			beforeBoundaryTime: 2,
			afterBoundaryTime: 3,
			specialCases: [
				{ day: 5, offset: 3, boundaryStatus: 'before' },
				{ day: 5, offset: 4, boundaryStatus: 'after' },
				{ day: 6, offset: 4 },
				{ day: 0, offset: 3 },
			]
		}
	},

	// правила для Экспорта
	wayExport: {
		boundaryTime: '12:00:00',
		daysOffset: {
			beforeBoundaryTime: 2,
			afterBoundaryTime: 3,
			specialCases: [
				{ day: 5, offset: 3, boundaryStatus: 'before' },
				{ day: 5, offset: 4, boundaryStatus: 'after' },
				{ day: 6, offset: 4 },
				{ day: 0, offset: 3 },
			]
		}
	},
}
