import { FILE_TYPE_ICONS } from "./constants.js"

// добавление изображения в форму
export function addImgToView(event, imgContainer, inputId) {
	imgContainer.innerHTML = ''

	const files = event.target.files
	if (!files) return

	for (let i = 0; i < files.length; i++) {
		const file = files[i]
		const fileType = file.type

		if (!fileType.startsWith('image/')) {
			const src = FILE_TYPE_ICONS[fileType] || FILE_TYPE_ICONS['default']
			const imgCard = createImgCard(file, src, inputId)
			imgContainer.append(imgCard)
			continue
		}

		const reader = new FileReader()
		reader.readAsDataURL(file)
		reader.onload = () => {
			const src = reader.result
			const imgCard = createImgCard(file, src, inputId)
			imgContainer.append(imgCard)
		}
	}

	return
}

function createImgCard(file, src, inputId) {
	const imgCard = document.createElement('div')
	imgCard.className = 'position-relative'

	const imgCardTitle = document.createElement('div')
	imgCardTitle.className = 'img-title text-center p-1 text-muted text-wrap'
	imgCardTitle.textContent = file.name

	const newImg = document.createElement("img")
	newImg.src = src

	const deleteBtn = document.createElement('button')
	deleteBtn.type = 'button'
	deleteBtn.className = 'img-delete-btn btn btn-danger btn-sm position-absolute h5 m-0 px-2 py-0'
	deleteBtn.innerHTML = '&times;'

	deleteBtn.addEventListener('click', (e) => {
		e.preventDefault()
		removeFileFromFileList(inputId, file.name)
		imgCard.remove()
	})

	imgCard.append(newImg, imgCardTitle, deleteBtn)
	return imgCard
}

function removeFileFromFileList(inputId, fileName) {
	const input = document.getElementById(inputId)
	if (!input) return

	const dt = new DataTransfer()
	const { files } = input

	const deleteIndex = [...files].findIndex(file => file.name === fileName)

	for (let i = 0; i < files.length; i++) {
		const file = files[i]
		if (deleteIndex !== i) {
			dt.items.add(file)
		}
	}

	document.getElementById(inputId).files = dt.files
}