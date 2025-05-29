export function createImgCard(file, src, index) {
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
		removeFileFromFileList('addRouteImageInput', index)
		imgCard.remove()
	})

	imgCard.append(newImg, imgCardTitle, deleteBtn)
	return imgCard
}

function removeFileFromFileList(inputId, deleteIndex) {
	const dt = new DataTransfer()
	const input = document.getElementById(inputId)
	if (!input) return
	const { files } = input

	for (let i = 0; i < files.length; i++) {
		const file = files[i]
		if (deleteIndex !== i) {
			dt.items.add(file)
		}
	}

	document.getElementById(inputId).files = dt.files
}