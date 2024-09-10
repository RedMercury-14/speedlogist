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
	 * Метод `getMinValidDate` возвращает минимальную допустимую дату точки
	 * выгрузки для заявок и слотов в формате "YYYY-MM-DD".
	 * @returns {string} строку даты в формате "YYYY-MM-DD".
	 */
	getMinValidDate(order) {
		const isInternalMovement = order && order.isInternalMovement
		const RBway = order && order.way === 'РБ'
		const ahoWay = order && order.way === 'АХО'
		const now = new Date()
		const day = now.getDay()
		const noonToday = this.getNoon(now)
		const TimeOfToday = this.getTimesOfDay(now, '11:00:00')

		// правила для внутренних перемещений
		if (isInternalMovement === 'true') {
			// до 12 - на завтра, после 12 - на послезавтра
			const tomorrow = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 1)
			const dayAfterTomorrow = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 2)
			return now < noonToday
				? this.getDateForInput(tomorrow)
				: this.getDateForInput(dayAfterTomorrow)
		}

		// правила для перевозок по РБ и перевозок АХО
		if (RBway || ahoWay) {
			// если пятница, после 11:00, то на вторник
			if (day === 5 && now > TimeOfToday) {
				const tuesday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 4)
				return this.getDateForInput(tuesday)
			}

			// если суббота, то на вторник
			if (day === 6) {
				const tuesday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 3)
				return this.getDateForInput(tuesday)
			}

			// если воскресенье, то на вторник
			if (day === 0) {
				const tuesday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 2)
				return this.getDateForInput(tuesday)
			}

			// для иных случаев: до 11 - завтра, после 11 - на послезавтра
			const tomorrow = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 1)
			const dayAfterTomorrow = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 2)
			return now < TimeOfToday
				? this.getDateForInput(tomorrow)
				: this.getDateForInput(dayAfterTomorrow)
		}

		// правила для Импорта и Экспорта
		// если пятница, после 12:00, то на понедельник
		if (day === 5 && now <= noonToday) {
			const monday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 3)
			return this.getDateForInput(monday)
		}

		// если пятница, после 12:00, то на вторник
		if (day === 5 &&  now > noonToday) {
			const tuesday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 4)
			return this.getDateForInput(tuesday)
		}

		// если суббота, то на среду
		if (day === 6) {
			const wednesday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 4)
			return this.getDateForInput(wednesday)
		}

		// если воскресенье, то на среду
		if (day === 0) {
			const wednesday = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 3)
			return this.getDateForInput(wednesday)
		}

		// для иных случаев: до 12 - послезавтра, после 12 - на третий день
		const dayAfterTomorrow = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 2)
		const thirdDay = new Date(now.getTime() + this.DAYS_TO_MILLISECONDS * 3)
		return now < noonToday
			? this.getDateForInput(dayAfterTomorrow)
			: this.getDateForInput(thirdDay)
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
	getDatesToFetch(datesKey, dayNumber = 7, ) {
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
		const oldDate = new Date(now - this.DAYS_TO_MILLISECONDS * dayNumber)
		const oldDateMonth = this.pad(oldDate.getMonth() + 1)
		const oldDateDay = this.pad(oldDate.getDate())
		const dateStart = `${oldDate.getFullYear()}-${oldDateMonth}-${oldDateDay}`
		const dateEnd = `${currentDate.getFullYear()}-${currentDateMonth}-${currentDateDay}`
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

export function showLoadingSpinner(button) {
	button.innerHTML = `
		<span class="spinner-border spinner-border-sm" role="status" aria-hidden="true"></span>
		Загрузка...
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
	'orange-row': params => params.node.data.status === 6,
	'turquoise-row': params => params.node.data.status === 7,
	'light-purple-row': params => params.node.data.status === 8,
	'grey-row': params => params.node.data.status === 10,
	'yellow-row': params => params.node.data.status === 30,
	'red-row': params => params.node.data.status === 40,
	'light-green-row': params => params.node.data.status === 50,
	'dark-green-row': params => params.node.data.status === 60,
	'blue-row': params => params.node.data.status === 70,
	'purple-row': params => params.node.data.status === 100,
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
export function isProcurement(role) {
	return role === '[ROLE_PROCUREMENT]'
}
export function isSlotsObserver(role) {
	return role === '[ROLE_SLOTOBSERVER]'
}
export function isStockProcurement(role) {
	return role === '[ROLE_STOCKPROCUREMENT]'
}
export function isOderSupport(role) {
	return role === '[ROLE_ORDERSUPPORT]'
}
export function isCarrier(role) {
	return role === '[ROLE_CARRIER]'
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

// функция получения матрицы визуализации графика поставки
export function getDeliveryScheduleMatrix(schedule, note) {
	const daysDictionary = {
		"понедельник": 'Пн',
		"вторник": 'Вт',
		"среда": 'Ср',
		"четверг": 'Чт',
		"пятница": 'Пт',
		"суббота": 'Сб',
		"воскресенье": 'Вс',
	}
	const weekNumbers = {
		"н0": 1,
		"н1": 2,
		"н2": 3,
		"н3": 4,
		"н4": 5,
	}
	const days = Object.keys(daysDictionary)
	const shortDays = Object.values(daysDictionary)
	const weekNumberKeys = Object.keys(weekNumbers)
	const matrix = [
		['', ...shortDays ],
		['н0', '', '', '', '', '', '', ''],
		['н1', '', '', '', '', '', '', ''],
		['н2', '', '', '', '', '', '', ''],
		['н3', '', '', '', '', '', '', ''],
		['н4', '', '', '', '', '', '', ''],
	]
	const isWeekIndicated = note === 'неделя'
	const orderRow = matrix[1]
	let orderCounter = 1

	// обработка дней заказов
	schedule.forEach((entry, index) => {
		if (entry) {
			const parts = entry.split('/')
			const orderDay = getOrderDay(parts, index)

			// расставляем дни заказов на текущей неделе
			if (orderDay) {
				matrix[0].forEach((day, dayIndex) => {
					if (day === orderDay) {
						orderRow[dayIndex] = `з${orderCounter}`
						orderCounter++
					}
				})
			}
		}
	})

	// обработка дней поставок
	schedule.forEach((entry, index) => {
		if (entry) {
			const parts = entry.split('/')
			const deliveryDay = getDeliveryDay(parts)
			if (deliveryDay) {
				const deliveryWeek = deliveryDay ? getDeliveryWeek(parts, deliveryDay, index) : ''
				const orderIndex = shortDays.indexOf(deliveryDay) + 1
				const targetOrder = orderRow[orderIndex]
				const deliveryCounter = findDigitAfterZ(targetOrder)
				const deliveryCol = index + 1
				const deliveryRow = weekNumbers[deliveryWeek]

				// расставляем дни поставок
				const targetCell = matrix[deliveryRow][deliveryCol]
				if (targetCell) {
					matrix[deliveryRow][deliveryCol] += `/п${deliveryCounter}`
				} else {
					matrix[deliveryRow][deliveryCol] = `п${deliveryCounter}`
				}
			}
		}
	})
	return matrix

	// получение дня заказа
	function getOrderDay(parts, index) {
		return parts.includes('з') ? shortDays[index] : ''
	}
	// получение дня поставки
	function getDeliveryDay(parts) {
		return parts.reduce((acc, part) => {
			if (days.includes(part)) {
				return daysDictionary[part]
			}
			return acc
		}, '')
	}
	// получение ключа для номера недели
	function getDeliveryWeek(parts, deliveryDay, index) {
		let deliveryWeek
		if (isWeekIndicated) {
			deliveryWeek = parts.reduce((acc, part) => {
				if (weekNumberKeys.includes(part)) {
					return part
				}
				return acc
			}, '')
		} else {
			const dayNumber = shortDays.indexOf(deliveryDay)
			deliveryWeek = index > dayNumber ? 'н0' : 'н1'
		}
		return deliveryWeek
	}
	// поиск цифры после буквы 'з'
	function findDigitAfterZ(str) {
		const match = str.match(/з(\d)/)
		return match ? match[1] : ''
	}
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