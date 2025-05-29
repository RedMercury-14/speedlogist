export function extractFileNameFromContentDisposition(disposition) {
	if (!disposition) return null

	// 1. Пытаемся получить filename* (UTF-8 с url-кодировкой)
	const utf8Match = disposition.match(/filename\*\s*=\s*UTF-8''([^;\n]*)/i)
	if (utf8Match) {
		try {
			return decodeURIComponent(utf8Match[1])
		} catch (e) {
			console.warn('Ошибка decodeURIComponent для filename*', e)
		}
	}

	// 2. Пытаемся получить обычный filename
	const fallbackMatch = disposition.match(/filename="?([^"]+)"?/i)
	if (fallbackMatch) {
		let rawName = fallbackMatch[1]

		try {
			// Пробуем перекодировать из latin1 → utf8
			const bytes = new Uint8Array([...rawName].map(c => c.charCodeAt(0)))
			const decoded = new TextDecoder('utf-8').decode(bytes)
			return decoded
		} catch (e) {
			console.warn('Ошибка перекодировки filename', e)
			return rawName // как fallback
		}
	}

	return null
}

// получение размера картинки
export function getImageSize(src) {
	return new Promise((resolve, reject) => {
		const img = new Image()
		img.onload = (e) => resolve({ width: img.width, height: img.height })
		img.onerror = () => reject(new Error('Не удалось загрузить изображение'))
		img.src = src
	})
}