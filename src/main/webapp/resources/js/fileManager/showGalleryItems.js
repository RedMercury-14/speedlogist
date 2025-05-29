import { bootstrap5overlay } from "../bootstrap5overlay/bootstrap5overlay.js"
import { FILE_TYPE_ICONS } from "./constants.js"
import { extractFileNameFromContentDisposition, getImageSize } from "./utils.js"
import { snackbar } from "../snackbar/snackbar.js"

// отображение галереи с изображениями
export async function showGalleryItems(lightbox, galleryItems) {
	if (!galleryItems || !galleryItems.length) {
		snackbar.show('Файлы отсутствуют')
		return
	}

	const timeoutId = setTimeout(() => bootstrap5overlay.showOverlay(), 300)

	const description = ''
	const itemsWithSizes = await Promise.all(
		galleryItems.map(async (src, i) => {
			const id = Number(src.split('/').pop())

			try {
				const response = await fetch(src, { method: 'HEAD' }) // HEAD запрос — только заголовки
				const contentType = response.headers.get('Content-Type') || ''
				const contentDisposition = response.headers.get('Content-Disposition') || ''
				const isImage = contentType.startsWith('image/')

				let fileName = extractFileNameFromContentDisposition(contentDisposition)
				if (!fileName) {
					fileName = decodeURIComponent(src.split('/').pop().split('?')[0])
				}

				if (isImage) {
					const size = await getImageSize(src)
					return {
						id: id,
						src: src,
						title: fileName,
						alt: fileName,
						width: size.width,
						height: size.height,
						description: description,
					}
				} else {
					// Не изображение
					const iconSrc = FILE_TYPE_ICONS[contentType] || FILE_TYPE_ICONS['default']
					return {
						id: id,
						src: iconSrc,
						downloadLink: src,
						title: fileName,
						alt: fileName,
						width: 240,
						height: 240,
						description: description,
					}
				}
			} catch (error) {
				console.error('Ошибка загрузки:', error)
				return {
					id: id,
					src: FILE_TYPE_ICONS['default'],
					downloadLink: src,
					title: `Файл ${i + 1}`,
					alt: `Файл ${i + 1}`,
					width: 500,
					height: 500,
					description: 'Ошибка загрузки файла',
				}
			}
		})
	)

	clearTimeout(timeoutId)
	bootstrap5overlay.hideOverlay()
	lightbox.loadAndOpen(0, itemsWithSizes)
}
