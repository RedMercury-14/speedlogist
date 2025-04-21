export const buttons = {
	registerDownloadButton(lightbox) {
		lightbox.pswp.ui.registerElement({
			name: 'downloadButton',
			title: 'Скачать изображение',
			order: 9,
			isButton: true,
			html: {
				isCustomSVG: true,
				inner:'</use><path d="M20.5 14.3 17.1 18V10h-2.2v7.9l-3.4-3.6L10 16l6 6.1 6-6.1ZM23 23H9v2h14Z" id="pswp__icn-download"></path>',
				outlineID: 'pswp__icn-download'
			},
			onClick: async () => {
				const currentSlide = lightbox.pswp.currSlide
				if (currentSlide) {
					try {
						const imgSrc = currentSlide.data.src
						const image = await fetch(imgSrc)
						const imageBlog = await image.blob()
						const imageURL = URL.createObjectURL(imageBlog)
						const a = document.createElement('a')
						a.href = imageURL
						a.download = imageURL.split('/').pop()
						document.body.appendChild(a)
						a.click()
						document.body.removeChild(a)
					} catch (error) {
						console.error('Ошибка при скачивании изображения')
						console.error(error)
					}
				}
			}
		})
	},

	registerRotateLeftBtn(lightbox) {
		lightbox.pswp.ui.registerElement({
			name: 'rotateLeft',
			title: 'Повернуть влево',
			order: 9,
			isButton: true,
			html: {
				isCustomSVG: true,
				inner:
					'<path d="M13.887 6.078C14.258 6.234 14.5 6.598 14.5 7V8.517C18.332 8.657 21.258 10.055 23.15 12.367 24.519 14.041 25.289 16.13 25.496 18.409A1 1 0 0123.504 18.591C23.327 16.645 22.68 14.952 21.601 13.633 20.156 11.867 17.831 10.653 14.5 10.517V12A1.002 1.002 0 0112.779 12.693L10.304 10.121A1.002 1.002 0 0110.324 8.713L12.8 6.286A1 1 0 0113.887 6.078ZM7.5 16A1.5 1.5 0 006 17.5V24.5A1.5 1.5 0 007.5 26H17.5A1.5 1.5 0 0019 24.5V17.5A1.5 1.5 0 0017.5 16H7.5Z" id="pswp__icn-rotate"/>',
				outlineID: 'pswp__icn-rotate',
			},
			// onClick: (e, el) => {
			// 	const pswpInstance = lightbox.pswp;
			// 	if (pswpInstance && pswpInstance.currSlide) {
			// 		rotateAndScaleImage(pswpInstance.currSlide.content.element, -90)
			// 	}
			// }
			onClick: (e, el, pswpInstance) => {
				rotateImage(pswpInstance, 'toLeft')
			},
			onInit: (el, pswpInstance) => {
				pswpInstance.on('contentRemove', () => {
					if (!pswpInstance.currSlide?.content.element) {
						return
					}
	  
					const item = pswpInstance.currSlide.content.element
					item.style.transform = `${item.style.transform.replace(
						`rotate(-${item.dataset.rotateAngel}deg)`,
						'',
					)}`
					delete item.dataset.rotateAngel
				})
			},
		})
	},

	registerRotateRightBtn(lightbox) {
		lightbox.pswp.ui.registerElement({
			name: 'rotateLeft',
			title: 'Повернуть вправо',
			order: 9,
			isButton: true,
			html: {
				isCustomSVG: true,
				inner:
					'<use class="pswp__icn-shadow" xlink:href="#pswp__icn-rotate" transform="scale(-1,1) translate(-32,0)"></use>'
					+ '<path transform="scale(-1,1) translate(-32,0)" d="M13.887 6.078C14.258 6.234 14.5 6.598 14.5 7V8.517C18.332 8.657 21.258 10.055 23.15 12.367 24.519 14.041 25.289 16.13 25.496 18.409A1 1 0 0123.504 18.591C23.327 16.645 22.68 14.952 21.601 13.633 20.156 11.867 17.831 10.653 14.5 10.517V12A1.002 1.002 0 0112.779 12.693L10.304 10.121A1.002 1.002 0 0110.324 8.713L12.8 6.286A1 1 0 0113.887 6.078ZM7.5 16A1.5 1.5 0 006 17.5V24.5A1.5 1.5 0 007.5 26H17.5A1.5 1.5 0 0019 24.5V17.5A1.5 1.5 0 0017.5 16H7.5Z" id="pswp__icn-rotate"/>',
			},
			// onClick: (e, el) => {
			// 	const pswpInstance = lightbox.pswp;
			// 	if (pswpInstance && pswpInstance.currSlide) {
			// 		rotateAndScaleImage(pswpInstance.currSlide.content.element, 90)
			// 	}
			// }
			onClick: (e, el, pswpInstance) => {
				rotateImage(pswpInstance, 'toRight')
			},
			onInit: (el, pswpInstance) => {
				pswpInstance.on('contentRemove', () => {
					if (!pswpInstance.currSlide?.content.element) {
						return
					}
	  
					const item = pswpInstance.currSlide.content.element
					item.style.transform = `${item.style.transform.replace(
						`rotate(-${item.dataset.rotateAngel}deg)`,
						'',
					)}`
					delete item.dataset.rotateAngel
				})
			},
		})
	}
}

export const thumbnails = {
	registerThumbnails(lightbox) {
		lightbox.pswp.ui.registerElement({
			name: 'thumbnails',
			order: 9,
			isButton: false,
			appendTo: 'wrapper',
			html: '<div class="pswp-thumbnails"></div>',
			onClick: (e, el) => {
				const thumbnail = e.target.closest('.thumbnail')
				if (thumbnail) {
					const index = parseInt(thumbnail.dataset.index, 10)
					lightbox.pswp.goTo(index)
				}
			}
		})
	},

	createThumbnails(lightbox) {
		const pswpInstance = lightbox.pswp
		const thumbnailsContainer = pswpInstance.element.querySelector('.pswp-thumbnails')
		if (!thumbnailsContainer) {
			console.warn('Thumbnails container not found')
			return
		}
	
		thumbnailsContainer.innerHTML = ''    // Очистка контейнера
	
		const galleryItems = pswpInstance.options.dataSource
		if (!galleryItems || galleryItems.length === 0) {
			console.warn('No items found for thumbnails')
			return
		}
	
		// Создаем миниатюры на основе текущих слайдов
		galleryItems.forEach((item, index) => {
			if (!item.src) {
				console.warn(`Missing src for item at index ${index}`)
				return
			}
	
			const thumb = document.createElement('div')
			thumb.className = 'thumbnail'
			thumb.dataset.index = index
		
			thumb.innerHTML = `<img src="${item.src}" alt="${item.alt}" style="width: 60px; height: auto; cursor: pointer;">`
			thumbnailsContainer.appendChild(thumb)
		
			// Обработчик клика по миниатюре
			thumb.addEventListener('click', () => pswpInstance.goTo(index))
		})
	
		// Добавляем "active" для текущего слайда
		const updateActiveThumbnail = () => {
			thumbnailsContainer.querySelectorAll('.thumbnail').forEach((thumb) => {
				thumb.classList.remove('active')
			});
			const activeThumbnail = thumbnailsContainer.querySelector(`.thumbnail[data-index="${pswpInstance.currIndex}"]`)
			if (activeThumbnail) {
				activeThumbnail.classList.add('active')
			}
		}
	
		pswpInstance.on('change', updateActiveThumbnail) // Обновляем активный слайд при смене
		updateActiveThumbnail() // Устанавливаем для первого слайда
	},

	destroyThumbnails(lightbox) {
		const pswpInstance = lightbox.pswp
		const thumbnailsContainer = pswpInstance.element.querySelector('.pswp-thumbnails')
		if (thumbnailsContainer) {
			thumbnailsContainer.innerHTML = ''
		}
	}
}


export const caption = {
	registerCaption(lightbox) {
		lightbox.pswp.ui.registerElement({
			name: 'custom-caption',
			order: 9,
			isButton: false,
			appendTo: 'root',
			html: 'Caption text',
			onInit: (el, pswp) => {
				lightbox.pswp.on('change', () => {
					const currSlideTitle = lightbox.pswp.currSlide.data.title
					el.innerHTML = currSlideTitle || ''
				})
			}
		})
	}
}

function rotateImage(pswpInstance, type) {
	if (!pswpInstance.currSlide?.content.element) {
		return
	}

	const item = pswpInstance.currSlide.content.element

	const prevRotateAngle = Number(item.dataset.rotateAngel) || 0
	const rotateAngle = type === 'toLeft'
		? prevRotateAngle === 270 ? 0 : prevRotateAngle + 90
		: prevRotateAngle === 0 ? 270 : prevRotateAngle - 90

	item.style.transform = `${item.style.transform.replace(
		`rotate(-${prevRotateAngle}deg)`,
		'',
	)} rotate(-${rotateAngle}deg)`
	item.dataset.rotateAngel = String(rotateAngle)
}

// Функция для поворота и масштабирования изображения
function rotateAndScaleImage(imgElement, angle) {
	if (!imgElement) return

	// Сохраняем исходные размеры изображения (если они еще не сохранены)
	if (!imgElement.dataset.startWidth || !imgElement.dataset.startHeight) {
		imgElement.dataset.startWidth = imgElement.style.width || imgElement.width
		imgElement.dataset.startHeight = imgElement.style.height || imgElement.height
	}
	
	const startWidth = parseInt(imgElement.dataset.startWidth, 10)
	const startHeight = parseInt(imgElement.dataset.startHeight, 10)
	
	// Получаем текущий угол поворота
	const currentRotation = imgElement.dataset.rotation
		? parseInt(imgElement.dataset.rotation, 10)
		: 0
	
	// Вычисляем новый угол
	const newRotation = (currentRotation + angle) % 360
	imgElement.dataset.rotation = newRotation

	// Определяем, нужно ли поменять ширину и высоту
	const isRotated = Math.abs(newRotation) % 180 === 90

	// Вычисляем размеры изображения с учетом поворота
	const adjustedWidth = isRotated ? startHeight : startWidth
	const adjustedHeight = isRotated ? startWidth : startHeight

	// Получаем размеры окна
	const viewportWidth = window.innerWidth
	const viewportHeight = window.innerHeight

	// Рассчитываем масштаб так, чтобы изображение вписывалось в окно
	const scale = Math.min(viewportWidth / adjustedWidth, viewportHeight / adjustedHeight)

	// Применяем поворот и масштаб
	imgElement.style.transform = `rotate(${newRotation}deg) scale(${scale})`
	imgElement.style.transition = 'transform 0.3s'
}
