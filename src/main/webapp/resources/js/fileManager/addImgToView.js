import { FILE_TYPE_ICONS } from "./constants.js"
import { createImgCard } from "./createImgCard.js"

// добавление изображения в форму
export function addImgToView(event, imgContainer) {
	imgContainer.innerHTML = ''

	const files = event.target.files
	if (!files) return

	for (let i = 0; i < files.length; i++) {
		const file = files[i]
		const fileType = file.type

		if (!fileType.startsWith('image/')) {
			const src = FILE_TYPE_ICONS[fileType] || FILE_TYPE_ICONS['default']
			const imgCard = createImgCard(file, src, i)
			imgContainer.append(imgCard)
			continue
		}

		const reader = new FileReader()
		reader.readAsDataURL(file)
		reader.onload = () => {
			const src = reader.result
			const imgCard = createImgCard(file, src, i)
			imgContainer.append(imgCard)
		}
	}

	return
}