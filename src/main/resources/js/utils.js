import { RULES_FOR_MIN_UNLOAD_DATE } from "./globalRules/minUnloadDateRules.js"

/**
 * Функция `getData` является асинхронной функцией, которая извлекает данные из указанного URL-адреса
 * и возвращает данные, если ответ успешен, в противном случае она возвращает значение `null`.
 * @param {string} url - это строка, представляющая URL-адрес, с которого вы хотите получить данные.
 * @returns данные, полученные по указанному URL-адресу
 */
export async function getData(url) {
	try {
		const res = await fetch(url)
		if (!res.ok) return null
		const data = await res.json()
		return data
	} catch (error) {
		console.error(error)
	}
}

/**
 * Функция `debounce` — это функция более высокого порядка, которая принимает функцию и продолжительность
 * тайм-аута в качестве аргументов и возвращает новую функцию, которая откладывает выполнение исходной
 * функции до тех пор, пока не пройдет определенное количество времени без каких-либо дальнейших вызовов.
 * @param {function} callee - это функция, которую вы хотите отменить. Это функция, которая будет вызываться
 * после истечения времени ожидания `timeoutMs`.
 * @param {number} timeoutMs - это количество времени в миллисекундах, в течение которого вызов функции `callee`
 * будет отменяться.
 * @returns {function} новую "debounced" функцию.
 */
export function debounce(callee, timeoutMs) {
	return function perform(...args) {
		let previousCall = this.lastCall

		this.lastCall = Date.now()

		if (previousCall && this.lastCall - previousCall <= timeoutMs) {
			clearTimeout(this.lastCallTimer);
		}

		this.lastCallTimer = setTimeout(() => callee(...args), timeoutMs)
	}
}

export function isMobileDevice() {
	return /Android|webOS|iPhone|iPad|iPod|BlackBerry|IEMobile|Opera Mini/i
			.test(navigator.userAgent)
}

export const dateHelper = {
	DAYS_TO_MILLISECONDS: 86400000,
	MILLISECONDS_IN_HOUR: 3600000,
	MONTH_NAMES: [
		'Январь', 'Февраль', 'Март', 'Апрель',
		'Май', 'Июнь', 'Июль', 'Август',
		'Сентябрь', 'Октябрь', 'Ноябрь', 'Декабрь'
	],


	/**
	 * Метод `pad` принимает на вход число и возвращает строковое
	 * представление этого числа с ведущим нулем, если число меньше 10.
	 * @param {number} num - это число, которое мы хотим дополнить
	 * ведущим нулем, если оно меньше 10.
	 * @returns возвращает строковое представление введенного числа
	 * с ведущим нулем, если число меньше 10.
	 */
	pad(num) {
		return num > 9 ? `${num}` : `0${num}`;
	},


	/**
	 * Метод `getNumberOfDays` возвращает количество дней в данном месяце.
	 * @param {number} monthNumber - это номер месяца, для которого вы хотите
	 * получить количество дней. Это должно быть число от 1 до 12, где 1
	 * представляет январь, 2 — февраль и т. д.
	 * @returns {number} возвращает количество дней в данном месяце.
	 */
	getNumberOfDays(monthNumber) {
		if (monthNumber === 2) {
			const currentYear = new Date().getFullYear()
			const lastDayNumberInFeb  = new Date(currentYear, 2, 0).getDate()
			return lastDayNumberInFeb
		} 
		if ( monthNumber === 1
			|| monthNumber === 3
			|| monthNumber === 5
			|| monthNumber === 7
			|| monthNumber === 8
			|| monthNumber === 10
			|| monthNumber === 12
		) {
			return 31
		} else {
			return 30
		}
	},


	/**
	 * Метод getMonthName принимает на вход номер месяца и возвращает
	 * соответствующее название месяца на русском языке.
	 * @param {number} monthNumber - это номер месяца, название которого
	 * вы хотите получить. Это должно быть число от 1 до 12, где 1
	 * представляет январь, 2 — февраль и т. д.
	 * @returns {string} название месяца, соответствующее указанному номеру месяца.
	 */
	getMonthName(monthNumber) {
		const index = monthNumber - 1
		return this.MONTH_NAMES[index]
	},


	/**
	 * Метод `getFormatDate` принимает дату в миллисекундах и возвращает
	 * отформатированную строку даты в формате "DD.MM.YYYY"
	 * @param { number } dateInMs - это число, представляющее дату в миллисекундах.
	 * @returns { string } возвращает отформатированную строку даты в формате
	 * "DD.MM.YYYY".
	 */
	getFormatDate(dateInMs) {
		if (!dateInMs) return ''
		const date = new Date(dateInMs)
		const day = this.pad(date.getDate())
		const month = this.pad(date.getMonth() + 1)
		const year = date.getFullYear()
		return `${day}.${month}.${year}`
	},

	/**
	 * Метод `getFormatDateTime` принимает дату в миллисекундах и возвращает
	 * отформатированную строку даты и времени в формате "DD.MM.YYYY HH:MM"
	 * @param { number } dateInMs - это число, представляющее дату в миллисекундах.
	 * @returns { string } возвращает отформатированную строку даты и времени в
	 * формате "DD.MM.YYYY HH:MM".
	 */
	getFormatDateTime(dateInMs) {
		if (!dateInMs) return ''
		const date = new Date(dateInMs)
		const day = this.pad(date.getDate())
		const month = this.pad(date.getMonth() + 1)
		const year = date.getFullYear()
		const hours = this.pad(date.getHours())
		const minutes = this.pad(date.getMinutes())
		return `${day}.${month}.${year} ${hours}:${minutes}`
	},


	/**
	 * Метод `getISODateTime` принимает дату в миллисекундах и возвращает
	 * отформатированную строку даты и времени в формате "YYYY-MM-DDTHH:MM"
	 * @param { number } dateInMs - это число, представляющее дату в миллисекундах.
	 * @returns { string } возвращает отформатированную строку даты и времени в
	 * формате "YYYY-MM-DDTHH:MM".
	 */
	getISODateTime(dateInMs) {
		if (!dateInMs) return ''
		const date = new Date(dateInMs);
		const year = date.getFullYear();
		const month = this.pad(date.getMonth() + 1);
		const day = this.pad(date.getDate());
		const hours = this.pad(date.getHours());
		const minutes = this.pad(date.getMinutes());
		return `${year}-${month}-${day}T${hours}:${minutes}`;
	},


	/**
	 * Метод `getDateForInput` принимает дату в миллисекундах либо
	 * объект `Date` и возвращает отформатированную строку даты в
	 * формате "YYYY-MM-DD"
	 * @param { number | Date } dateInMs - это число, представляющее дату 
	 * в миллисекундах.
	 * @returns { string } возвращает отформатированную строку даты в формате
	 * "YYYY-MM-DD".
	 */
	getDateForInput(dateInMs) {
		if (!dateInMs) return ''
		const date = new Date(dateInMs)
		const year = date.getFullYear()
		const month = this.pad(date.getMonth() + 1)
		const day = this.pad(date.getDate())
		return `${year}-${month}-${day}`
	},


	/**
	 * Метод `changeFormatToView` меняет формат даты с "YYYY-MM-DD" на "DD.MM.YYYY".
	 * @param {string} date - это строка, представляющая дату в формате "YYYY-MM-DD".
	 * @returns {string} дата в формате "DD.MM.YYYY".
	 */
	changeFormatToView(date) {
		return date.split('-').reverse().join('.')
	},


	/**
	 * Метод `changeFormatToInput` меняет формат даты с "DD.MM.YYYY" на "YYYY-MM-DD".
	 * @param {string} date - это строка, представляющая дату в формате "DD.MM.YYYY".
	 * @returns {string} дата в формате "YYYY-MM-DD".
	 */
	changeFormatToInput(date) {
		return date.split('.').reverse().join('-')
	},


	/**
	 * Метод `formatToDDMM` принимает объект даты и возвращает строку формата "DD.MM".
	 * @param {Date} date - объект даты.
	 * @returns {string} дата в формате "DD.MM".
	 */
	formatToDDMM(date) {
		const day = String(date.getDate()).padStart(2, '0')
		const month = String(date.getMonth() + 1).padStart(2, '0')
		return `${day}.${month}`
	},


	/**
	 * Метод `getDayNumber` возвращает номер дня данного объекта даты
	 * с ведущим нулем, если номер дня меньше 10.
	 * @param {Date} dateObj - является экземпляром объекта `Date`.
	 * @returns {string} возвращает номер дня для данного объекта даты. Если номер дня
	 * больше 9, он возвращает номер дня как есть. В противном случае
	 * возвращается номер дня с ведущим нулем.
	 */
	getDayNumber(dateObj) {
		return dateObj.getDate() > 9 ? `${dateObj.getDate()}` : `0${dateObj.getDate()}`
	},


	/**
	 * Метод 'getNoon' возвращает объект `Date`, представляющий полдень заданной даты.
	 * @param {Date} dateObj - является экземпляром объекта `Date`.
	 * @returns {Date} новый объект `Date`, представляющий полдень заданной даты.
	 */
	getNoon(dateObj) {
		const currentDate = this.getDateForInput(dateObj)
		return new Date(currentDate + ' 12:00:00')
	},

	/**
	 * Метод 'getTimesofDay' возвращает объект `Date`, представляющий указанное время заданной даты.
	 * @param {Date} dateObj - является экземпляром объекта `Date`.
	 * @returns {Date} новый объект `Date`, представляющий указанное время заданной даты.
	 */
	getTimesOfDay(dateObj, timeStr) {
		const currentDate = this.getDateForInput(dateObj)
		return new Date(currentDate + ` ${timeStr}`)
	},

	/**
	 * Метод `convertToDayMonthTime` принимает на вход строку даты или милисекунды
	 * и возвращает отформатированную строку даты в формате "DD MMMM".
	 * @param date - это строка или число милисекунд.
	 * @returns {string} возвращает отформатированную строку даты в формате "DD MMMM".
	 */
	convertToDayMonthTime(date) {
		const formatter = new Intl.DateTimeFormat('ru', {
			day: '2-digit',
			month: 'long', 
			hour: '2-digit',
			minute: '2-digit'
		})
		return formatter.format(new Date(date))
	},

	/**
	 * Метод `getShortDayName` возвращает короткое название дня недели.
	 * @param {*} dateMs - это строка или число милисекунд.
	 * @returns {string} возвращает короткое название дня недели.
	 */
	getShortDayName(date) {
		const shortDayNamesDict = {
			0: 'Вс', 1: 'Пн', 2: 'Вт', 3: 'Ср',
			4: 'Чт', 5: 'Пт', 6: 'Сб',
		}
		const dayNumber = new Date(date).getDay()
		return shortDayNamesDict[dayNumber]
	},

	/**
	 * Метод `getMinValidDate` возвращает минимальную допустимую дату точки
	 * выгрузки для заявок и слотов в формате "YYYY-MM-DD".
	 * @returns {string} строку даты в формате "YYYY-MM-DD".
	 */
	getMinValidDate(order) {
		const now = new Date()
		const nowMs = now.getTime()
		const isInternalMovement = order && order.isInternalMovement === 'true'

		const wayRules = {
			'АХО': RULES_FOR_MIN_UNLOAD_DATE.aho,
			'РБ': RULES_FOR_MIN_UNLOAD_DATE.wayRB,
			'Импорт': RULES_FOR_MIN_UNLOAD_DATE.wayImport,
			'Экспорт': RULES_FOR_MIN_UNLOAD_DATE.wayExport
		};

		const minUnloadDateRules = order
			? isInternalMovement 
				? RULES_FOR_MIN_UNLOAD_DATE.internalMovement
				: wayRules[order.way] || RULES_FOR_MIN_UNLOAD_DATE.default
			: RULES_FOR_MIN_UNLOAD_DATE.default

		const daysOffset = getDaysOffset(now, minUnloadDateRules)
		const unloadDate = new Date(nowMs + this.DAYS_TO_MILLISECONDS * daysOffset)
		return this.getDateForInput(unloadDate)
	},


	/**
	 * Метод `getDatesToFetch` получает диапазон дат для выборки на основе заданного
	 * количества дней и ключа для доступа к сохраненным датам в локальном хранилище.
	 * @param {string} datesKey — это ключ, используемый для хранения и получения
	 * сохраненного диапазона дат из localStorage.
	 * @param { number } dayNumber — это количество дней, на которое нужно вернуться
	 * с текущей даты. По умолчанию равно 7 (неделя).
	 * @returns { { dateStart: string, dateEnd: string } } либо сохраненные даты dateStart и dateEnd из
	 * localStorage, если они не старые, либо возвращает объект со свойствами dateStart
	 * и dateEnd, представляющими диапазон дат для выборки. Формат дат YYYY-MM-DD
	 */
	getDatesToFetch(datesKey, daysAgo = 7, daysAhead = 0 ) {
		const savedDatesStr = localStorage.getItem(datesKey)
		const now = Date.now()

		/* Этот блок кода проверяет, сохранены ли даты в localStorage. Если есть сохраненные даты, он проверяет,
		не старше ли сохраненные даты 3 часов. Если сохраненные даты не старые, он возвращает сохраненные даты.
		Если сохраненных дат нет или сохраненные даты старые, он вычисляет новый диапазон дат на основе текущей
		даты и указанного количества дней и возвращает новый диапазон дат. */
		if (savedDatesStr) {
			const savedDates = JSON.parse(savedDatesStr)
			const isOldDates = (now - savedDates.timestamp) > this.MILLISECONDS_IN_HOUR * 3

			if (!isOldDates) {
				return savedDates.dates
			}
		}

		const dateStart = this.getDateForInput(now - this.DAYS_TO_MILLISECONDS * daysAgo)
		const dateEnd = this.getDateForInput(now + this.DAYS_TO_MILLISECONDS * daysAhead)
		return { dateStart, dateEnd }
	}, 


	getDatesToRoutesFetch(datesKey, dayNumber = 5, ) {
		const savedDatesStr = localStorage.getItem(datesKey)
		const now = Date.now()

		/* Этот блок кода проверяет, сохранены ли даты в localStorage. Если есть сохраненные даты, он проверяет,
		не старше ли сохраненные даты 3 часов. Если сохраненные даты не старые, он возвращает сохраненные даты.
		Если сохраненных дат нет или сохраненные даты старые, он вычисляет новый диапазон дат на основе текущей
		даты и указанного количества дней и возвращает новый диапазон дат. */
		if (savedDatesStr) {
			const savedDates = JSON.parse(savedDatesStr)
			const isOldDates = (now - savedDates.timestamp) > this.MILLISECONDS_IN_HOUR * 3

			if (!isOldDates) {
				return savedDates.dates
			}
		}

		const currentDate = new Date(now)
		const currentDateMonth = this.pad(currentDate.getMonth() + 1)
		const currentDateDay = this.pad(currentDate.getDate())
		const newDate = new Date(now + this.DAYS_TO_MILLISECONDS * dayNumber)
		const newDateMonth = this.pad(newDate.getMonth() + 1)
		const newDateDay = this.pad(newDate.getDate())
		const dateStart = `${currentDate.getFullYear()}-${currentDateMonth}-${currentDateDay}`
		const dateEnd = `${newDate.getFullYear()}-${newDateMonth}-${newDateDay}`
		return { dateStart, dateEnd }
	},


	/**
	 * Метод `setDatesToFetch` устанавливает даты начала и окончания выборки данных
	 * и сохраняет их в `localStorage`.
	 * @param {string} datesKey — это ключ, используемый для хранения и получения
	 * сохраненного диапазона дат из localStorage.
	 * @param {string} dateStart - начальная дата диапазона дат в формате YYYY-MM-DD.
	 * @param {string} dateEnd - конечная дата диапазона дат в формате YYYY-MM-DD.
	 */
	setDatesToFetch(datesKey, dateStart, dateEnd) {
		const obj = {
			timestamp: Date.now(),
			dates : {
				dateStart,
				dateEnd
			}
		}
		localStorage.setItem(datesKey, JSON.stringify(obj))
	},


	/**
	 * Метод `getDateObj` принимает на вход строку даты и строку времени и
	 * возвращает новый объект `Date`.
	 * @param {string} date - дата в виде милисекунд, объекта или строки в формате "YYYY-MM-DD".
	 * @param {string} timeString - строка, представляющая время в формате "HH:MM".
	 * @returns возвращает новый объект `Date`, содержащий указанные дату и время.
	 */
	getDateObj(date, timeString) {
		const [ hours, minutes ] = timeString.split(':')
		
		if (typeof date === 'string') {
			const [ year, month, day ] = date.split('-')
			
			return new Date(Date.UTC(
				year,
				month - 1,
				day,
				hours, 
				minutes
			))
		}

		if (typeof date === 'object' ) {
			date.setHours(hours)
			date.setMinutes(minutes)
			return date
		}

		if (typeof date === 'number') {
			const newDate = new Date(date)
			newDate.setHours(hours)
			newDate.setMinutes(minutes)
			return newDate
		}

		return null
	},

	/**
	 * Метод `getDiffTime` принимает два объекта `Date` и возвращает
	 * строку, представляющую разницу между ними в формате "HH:MM".
	 * @param {Date} date1 - первый объект `Date`.
	 * @param {Date} date2 - второй объект `Date`.
	 * @returns {string} строку, представляющую разницу между двумя объектами `Date` в формате "HH:MM".
	 */
	getDiffTime(date1, date2) {
		const diff = date1 - date2
		const hours = Math.floor(diff / (1000 * 60 * 60))
		const minutes = Math.floor((diff % (1000 * 60 * 60)) / (1000 * 60))
		return `${hours}:${minutes < 10 ? '0' + minutes : minutes}`
	},

	getDateStrsArray(startDateStr, numDays) {
		const startDate = new Date(startDateStr)
		const datesArray = []
	
		for (let i = 0; i < numDays; i++) {
			const dateStr = this.getDateForInput(startDate.getTime() + i * 24 * 60 * 60 * 1000)
			datesArray.push(dateStr)
		}
	
		return datesArray
	},

	isWeekend(dateStr) {
		if (!dateStr) return false
		const date = new Date(dateStr)
		const day = date.getDay()
		return day === 0 || day === 6
	},

	parseTimeStrToObject(timeStr) {
		return timeStr
			.split(':')
			.reduce((acc, val, i) => {
				acc[['h', 'm', 's'][i]] = parseInt(val)
				return acc
			}, {})
	}
}

export const cookieHelper = {
	/**
	 * Функция `getCookie` извлекает значение файла `cookie` с заданным именем из файлов `cookie` документа.
	 * @param {string} name - это имя файла `cookie`, который необходимо получить.
	 * @returns {string | undefined} Функция `getCookie` возвращает значение файла `cookie` с указанным именем. Если файл `cookie`
	 * с указанным именем найден, функция возвращает декодированное значение файла `cookie`. Если файл `cookie`
	 * с указанным именем не найден, функция возвращает `undefined`
	 */
	getCookie(name) {
		let matches = document.cookie.match(
			new RegExp("(?:^|; )" + name.replace(/([\.$?*|{}\(\)\[\]\\\/\+^])/g, "\\$1") + "=([^;]*)")
		)
		return matches ? decodeURIComponent(matches[1]) : undefined
	},

	/**
	 * Функция `setCookie` устанавливает файл `cookie` с заданным именем, значением и дополнительными параметрами.
	 * @param {string} name - это имя файла `cookie`, который необходимо изменить.
	 * @param {string} value - это значение, которое вы хотите установить для файла `cookie`.
	 * @param {Object} options - представляет собой объект, который содержит дополнительные настройки для файла
	 * `cookie`. По умолчанию равен {}
	 */
	setCookie(name, value, options = {}) {
		options = {
			path: "/",
			...options,
		}

		if (options.expires instanceof Date) {
			options.expires = options.expires.toUTCString()
		}

		let updatedCookie = encodeURIComponent(name) + "=" + encodeURIComponent(value)

		for (let optionKey in options) {
			updatedCookie += "; " + optionKey
			let optionValue = options[optionKey]
			if (optionValue !== true) {
				updatedCookie += "=" + optionValue
			}
		}

		document.cookie = updatedCookie
	},

	/**
	 * Функция `deleteCookie` используется для удаления файла `cookie` путем установки
	 * срока его действия на прошлую дату.
	 * @param {string} name - имя файла `cookie`, который необходимо удалить.
	 */
	deleteCookie(name) {
		this.setCookie(name, "", {
			'max-age': -1
		})
	}
}

// получение количества дней сдвига от сегодняшнего дня
function getDaysOffset(now, minUnloadDateRules) {
	const day = now.getDay()
	const nowMs = now.getTime()
	const boundaryTimeStr = minUnloadDateRules.boundaryTime
	const specialCases = minUnloadDateRules.daysOffset.specialCases
	const { h, m, s } = dateHelper.parseTimeStrToObject(boundaryTimeStr)
	const boundaryTimeMs = now.setHours(h, m, s)
	const boundaryStatus = nowMs > boundaryTimeMs ? 'after' : 'before'

	let daysOffset

	daysOffset = nowMs > boundaryTimeMs
		? minUnloadDateRules.daysOffset.afterBoundaryTime
		: minUnloadDateRules.daysOffset.beforeBoundaryTime

	if (specialCases && specialCases.length !== 0) {
		const dayCase = specialCases.find(specialCase => specialCase.day === day &&
			(specialCase.boundaryStatus === boundaryStatus || specialCase.boundaryStatus === undefined)
		)

		if (dayCase) {
			daysOffset = dayCase.offset
		}
	}

	return daysOffset
}


// функция получения данных о документе типа серия/номер/кем выдан/дата
export function getDocumentValues(documentValue) {
	if (!documentValue) return ['','','','']

	const array = documentValue.split(' ')

	if (array.length < 4) {
		return [
			array[0] ? array[0] : '',
			array[1] ? array[1] : '',
			array[2] ? array[2] : '',
			array[3] ? array[3] : '',
		]
	}

	const value_1 = array.shift()
	const value_2 = Number(array.shift().slice(0, -1))
	const value_4 = array.pop().split('.').reverse().join('-')
	array.shift()
	array.pop()
	const value_3 = array.join(' ')

	return [
		value_1,
		value_2,
		value_3,
		value_4
	]
}

export function getEncodedString(str) {
	let encodedStr = ''
	for (let i = 0; i < str.length; i++) {
		const code = str.charCodeAt(i) + 's'
		encodedStr += code
	}

	return encodedStr.slice(0, -1)
}
export function getDecodedString(str) {
	if (!str) return ''

	return str
		.split('s')
		.map(charCode => String.fromCharCode(charCode))
		.join('')
}

export function randomColor(brightness = 0) {
	function randomChannel(brightness) {
		var r = 255-brightness
		var n = 0 | ((Math.random() * r) + brightness)
		var s = n.toString(16)
		return (s.length == 1) ? '0' + s : s
	}
	return '#' + randomChannel(brightness) + randomChannel(brightness) + randomChannel(brightness)
}

export function showLoadingSpinner(button, text='Загрузка...') {
	button.innerHTML = `
		${text}
		<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
	`
}
export function hideLoadingSpinner(button, text) {
	button.innerHTML = text
}

export function getStatus(status) {
	switch (status) {
		case 5:
			return 'Виртуальный заказ'
		case 6:
			return 'Ожидает слот на выгрузку'
		case 7:
			return 'Слот на выгрузку установлен. Ожидает подтверждения'
		case 8:
			return 'Заказ от перевозчика. Слот установлен, ожидает подтверждения'
		case 100:
			return 'Заказ от перевозчика. Подтвержден'
		case 10:
			return 'Отменен/удалён'
		case 15:
			return 'Ожидается точка выгрузки'
		case 17:
			return 'Ожидает подтверждения ОУЗ'
		case 20:
			return 'В обработке у логистов'
		case 30:
			return 'Маршрут создан, но не на бирже'
		case 40:
			return 'На бирже, не торгуется (маршрут не сыграл, но новый не создан)'
		case 50:
			return 'На бирже, торгуется'
		case 60:
			return 'Заказ в работе (есть перевозчик)'
		case 70:
			return 'Заказ завершен (товар приехал)'
		default:
			return 'Неизвестный статус'
	}
}

export function getRouteStatus(status) {
	switch (status) {
		case '0':
			return 'Ожидание подтверждения';
		case '1':
			return 'Маршрут на бирже';
		case '4':
			return 'Тендер завершен. Перевозчик принят';
		case '5':
			return 'Тендер отменен';
		case '6':
			return 'Маршрут завершен';
		case '8':
			return 'Контроль цены';
		case '9':
			return 'Данные перевозчика отправлены';
		default:
			return 'Неизвестный статус';
	}
}

export function getScheduleStatus(status) {
	switch (status) {
		case 0:
			return 'Удален'
		case 10:
			return 'Ожидает подтверждения'
		case 20:
			return 'В работе'
		default:
			return 'Неизвестный статус'
	}
}

// метод с расшифровкойц статусов маршрутов АХО
export function getAhoStatusRoute(status) {
	switch (status) {
		case '200':
			return 'Ожидает назначения перевозчика'
		case '210':
			return 'Перевозчик назначен'
		case '220':
			return 'Указан пробег'
		case '225':
			return 'Указан пробег и стоимость перевозки'
		case '230':
			return 'Завершен'
		default:
			return 'Неизвестно'
	}
}

export const rowClassRules = {
	'orange-row': params => params.data && params.data.status === 6,
	'turquoise-row': params => params.data && params.data.status === 7,
	'light-purple-row': params => params.data && params.data.status === 8,
	'grey-row': params => params.data && params.data.status === 10,
	'yellow-row': params => params.data && params.data.status === 30,
	'red-row': params => params.data && params.data.status === 40,
	'light-green-row': params => params.data && params.data.status === 50,
	'dark-green-row': params => params.data && params.data.status === 60,
	'blue-row': params => params.data && params.data.status === 70,
	'purple-row': params => params.data && params.data.status === 100,
}

// изменение положения таблицы в зависимости от размера хэдера
export function changeGridTableMarginTop() {
	const navbar = document.querySelector('.navbar')
	const height = navbar.offsetHeight
	
	if (height < 65) {
		const myContainer = document.querySelector('.my-container')
		const gridDiv = document.querySelector('#myGrid')
		myContainer.classList.add('smallHeader')
		gridDiv.classList.add('smallHeader')
	}
}

export function isAdminByLogin() {
	const loginInput = document.querySelector('#login')
	const login = loginInput && loginInput.value

	return login === 'catalina!%ricoh' || login === 'yakubove%%' || login === 'pedagog%!sport'
}
export function isLogist(role) {
	return isTopManager(role) || isManager(role)
}
export function isAdmin(role) {
	return role === '[ROLE_ADMIN]'
}
export function isTopManager(role) {
	return role === '[ROLE_TOPMANAGER]'
}
export function isManager(role) {
	return role === '[ROLE_MANAGER]'
}
export function isLogistDelivery(role) {
	return role === '[ROLE_LOGISTDELIVERY]'
}
export function isProcurement(role) {
	return role === '[ROLE_PROCUREMENT]'
}
export function isSlotsObserver(role) {
	return role === '[ROLE_SLOTOBSERVER]'
}
export function isStockProcurement(role) {
	return role === '[ROLE_STOCKPROCUREMENT]'
}
export function isOrderSupport(role) {
	return role === '[ROLE_ORDERSUPPORT]'
}
export function isCarrier(role) {
	return role === '[ROLE_CARRIER]'
}
export function isORL(role) {
	return role === '[ROLE_ORL]'
}
export function isObserver(role) {
	return role === '[ROLE_SHOW]'
}
export function isQualityManager(role) {
	return role === '[ROLE_QUALITYMANAGER]'
}
export function isQualityManagerAndProcurement(role) {
	return role === '[ROLE_QUALITYMANAGER_AND_PROCUREMENT]'
}
export function isRetail(role) {
	return role === '[ROLE_RETAIL]'
}

export function isLogisticsDeliveryPage() {
	return window.location.href.includes('logistics-delivery')
}

export function disableButton(button) {
	button.setAttribute('disabled', true)
}

export function enableButton(button) {
	button.removeAttribute('disabled')
}

export function removeSingleQuotes(str) {
	return str.replace(/'/g, '');
}

export function cutToInteger(value) {
	const strNum = value.toString()
	return strNum.split(/[,.]/)[0]
}

// Функция для генерации измененной матрицы с датами
export function getMatrixWithCalendar(matrix) {
	// Определяем начало первой недели (понедельник текущей недели)
	const today = new Date()
	const dayOfWeek = today.getDay() === 0 ? 7 : today.getDay() // Преобразование воскресенья в 7
	const startOfFirstWeek = new Date(today)
	startOfFirstWeek.setDate(today.getDate() - dayOfWeek + 1)

	// Создаем новую матрицу для обновленных данных
	const updatedSchedule = matrix.map(row => [...row]) // Копируем исходную матрицу

	// Обрабатываем строки, начиная со второй (где н0, н1 и т.д.)
	for (let i = 1; i < matrix.length; i++) {
		// Получаем даты для текущей недели (смещенной на i-1 недель от первой)
		const weekDates = getWeekDates(startOfFirstWeek, i - 1)

		// Обновляем каждую ячейку строки, кроме первой (где название недели)
		for (let j = 1; j < matrix[i].length; j++) {
			const date = weekDates[j - 1]
			const dateStr = dateHelper.formatToDDMM(date)
			// Если ячейка пуста, просто добавляем дату
			updatedSchedule[i][j] = matrix[i][j] ? `${matrix[i][j]} (${dateStr})` : `(${dateStr})`
		}
	}

	return updatedSchedule
}
// Функция для получения дат недели с учетом смещения на количество недель
function getWeekDates(startDate, weekOffset) {
	const firstDayOfWeek = new Date(startDate)
	firstDayOfWeek.setDate(firstDayOfWeek.getDate() + (weekOffset * 7)) // Смещение на неделю

	const weekDates = []
	for (let i = 0; i < 7; i++) {
		const date = new Date(firstDayOfWeek)
		date.setDate(firstDayOfWeek.getDate() + i)
		weekDates.push(date)
	}
	return weekDates
}

// функция создания матрицы по дням заказов
export function createScheduleMatrix(importantDates) {
	// Определяем начало текущей недели (понедельник текущей недели)
	const today = new Date()
	const dayOfWeek = today.getDay() === 0 ? 7 : today.getDay() // Преобразование воскресенья в 7
	const startOfWeek = new Date(today)
	startOfWeek.setDate(today.getDate() - dayOfWeek + 1)

	// Получаем даты для 5 недель
	const allWeeks = getAllWeekDates(startOfWeek)

	// Выделяем важные даты жирным
	const highlightedWeeks = highlightDatesInMatrix(allWeeks, importantDates)

	// Создаем финальную матрицу
	const scheduleMatrix = [
		["", "Пн", "Вт", "Ср", "Чт", "Пт", "Сб", "Вс"],
		["н-4", ...highlightedWeeks[0]],
		["н-3", ...highlightedWeeks[1]],
		["н-2", ...highlightedWeeks[2]],
		["н-1", ...highlightedWeeks[3]],
		["н0", ...highlightedWeeks[4]]
	]

	return scheduleMatrix
}
// Функция для получения всех дат в пределах 5 недель (до текущей)
function getAllWeekDates(startDate) {
	const allWeeks = [];

	for (let weekOffset = -4; weekOffset <= 0; weekOffset++) {
		const weekDates = [];
		const firstDayOfWeek = new Date(startDate);
		firstDayOfWeek.setDate(firstDayOfWeek.getDate() + (weekOffset * 7)); // Смещение на неделю

		for (let i = 0; i < 7; i++) {
			const date = new Date(firstDayOfWeek);
			date.setDate(firstDayOfWeek.getDate() + i);
			weekDates.push(date);
		}
		allWeeks.push(weekDates);
	}

	return allWeeks;
}
// Функция для выделения жирным совпадающих дат
function highlightDatesInMatrix(weeksMatrix, importantDates) {
	// Массив дат, которые нужно выделить жирным
	const formattedImportantDates = importantDates.map(date => dateHelper.formatToDDMM(new Date(date)))

	// Преобразуем матрицу недель
	return weeksMatrix.map(week =>
		week.map(day => {
			const formattedDay = dateHelper.formatToDDMM(day)
			if (formattedImportantDates.includes(formattedDay)) {
				// Если дата совпадает с одной из важных дат, выделяем жирным
				return `**${formattedDay}**`
			}
			return formattedDay
		})
	)
}

// Функция для отрисовки матрицы календаря заказов
export function renderScheduleMatrix(container, scheduleMatrix) {
	const table = document.createElement('table')
	table.className = 'table table-bordered text-center table-dark table-hover'
	
	scheduleMatrix.forEach((row, rowIndex) => {
		const tr = document.createElement('tr')

		row.forEach((cell, colIndex) => {
			const td = document.createElement('td')

			if (row[0] === '' || cell === row[0]) {
				td.classList.add('font-weight-bold')
			} else {
				td.classList.add('bg-secondary'); // Установка белого фона для всех ячеек
			}
			
			// Проверяем, является ли это важной датой
			if (cell.startsWith('**') && cell.endsWith('**')) {
				td.textContent = cell.replace(/\*\*/g, ''); // Убираем звездочки
				td.classList.add('bg-info', 'font-weight-bold'); // Классы для выделения
				td.classList.remove('bg-secondary'); // Классы для выделения
			} else {
				// Делаем даты надстрочными
				td.innerHTML = cell.replace(/(\d{2}\.\d{2})/, '<sup>$1</sup>'); // Оборачиваем даты в тег sup
			}

			// Проверяем, нужно ли сделать подстрочным индекс
			if (cell.startsWith('н')) {
				td.innerHTML = cell.replace(/н/g, 'н<sub>').replace(/$/, '</sub>') // Подстрочный индекс
			}

			tr.appendChild(td)
		})

		table.appendChild(tr)
	})

	container.appendChild(table)
}


// запрет ввода
export function inputBan(e, reg) {
	const input = e.target
	if (input.value.match(reg)) {
		input.value = input.value.replaceAll(reg, '')
	}
}

export function setInputValue(container, selector, value) {
	const input = container.querySelector(selector)
	if (!input) return

	const selectOptions = input.options
	if (selectOptions) {
		for (let i = 0; i < selectOptions.length; i++) {
			const option = selectOptions[i]
			if (option.value === value) {
				option.selected = true
			}
		}
	} else {
		input.value = value
	}
}

export function getInputValue(container, selector) {
	const input = container.querySelector(selector)
	if (!input) return ''

	return input.value
}

export function blurActiveElem(e) {
	const activeElem = document.activeElement
	if (!activeElem) return
	activeElem.blur()
}

// создание опций для элемента
export function createOptions(optionData, select) {
	optionData.forEach((option) => {
		const optionElement = document.createElement('option')
		optionElement.value = option
		optionElement.text = option
		select.append(optionElement)
	})
}

// группировка по ключу
export function groupBy(data, key) {
	return data.reduce((acc, currentValue) => {
		let groupKey = currentValue[key]
		if (!acc[groupKey]) {
			acc[groupKey] = []
		}
		acc[groupKey].push(currentValue)
		return acc
	}, {})
}


// обнаружение нескольких вкладок
export function detectMultipleTabs(callback) {
	// Генерируем уникальный идентификатор для вкладки
	const tabId = Math.random().toString(36).slice(2, 9)

	// Создаем канал
	const channel = new BroadcastChannel('tab-tracker')

	// Отправляем сообщение о том, что вкладка открыта
	channel.postMessage({ type: 'tabOpened', tabId })

	// Слушаем сообщения от других вкладок
	channel.onmessage = (event) => {
		if (event.data.type === 'tabOpened' && event.data.tabId !== tabId) {
			console.log('Другая вкладка открыта:', event.data.tabId)
			callback()
		}
	}
}


export class SmartWebSocket {
	constructor(url, options = {}) {
		this.url = url
		this.reconnectInterval = options.reconnectInterval || 3000
		this.maxReconnectAttempts = options.maxReconnectAttempts || 10
		this.onMessageCallback = options.onMessage || (() => {})
		this.onCloseCallback = options.onClose || (() => {})
		this.onErrorCallback = options.onError || (() => {})

		this.socket = null
		this.reconnectAttempts = 0
		this.isManuallyClosed = false
		this.isConnected = false
		this.messageQueue = []

		this.connect()
	}

	connect() {
		if (this.isManuallyClosed) return

		this.socket = new WebSocket(this.url)

		this.socket.addEventListener("open", () => {
			this.isConnected = true
			this.reconnectAttempts = 0

			while (this.messageQueue.length > 0) {
				this.socket.send(this.messageQueue.shift())
			}
		})

		this.socket.addEventListener("message", this.onMessageCallback)

		this.socket.addEventListener("close", (event) => {
			this.isConnected = false
			if (!this.isManuallyClosed) this.attemptReconnect()
			else this.onCloseCallback(event)
		})

		this.socket.addEventListener("error", (error) => {
			this.onErrorCallback(error)
			this.socket.close()
		})
	}

	attemptReconnect() {
		if (this.reconnectAttempts >= this.maxReconnectAttempts) {
			console.error("Переподключение остановлено — превышено число попыток")
			return
		}

		this.reconnectAttempts++
		console.log(
			`Попытка переподключения #${this.reconnectAttempts} через ${this.reconnectInterval / 1000} сек...`
		)
		setTimeout(() => this.connect(), this.reconnectInterval)
	}

	send(data) {
		const message = typeof data === "string" ? data : JSON.stringify(data)
		if (this.isConnected && this.socket.readyState === WebSocket.OPEN) {
			this.socket.send(message)
		} else {
			console.warn("Сокет недоступен, сообщение сохранено в очередь")
			this.messageQueue.push(message)
		}
	}

	close() {
		this.isManuallyClosed = true
		this.isConnected = false
		if (this.socket) {
			this.socket.close()
		}
		console.log("Сокет закрыт вручную")
	}
}