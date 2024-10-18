import { dateHelper } from '../../utils.js'

// изменение машины в зависимости от действия
export function getTruckDateForAjax(trucks, nameList, action) {
	return trucks.map((truck) => {
		switch (action) {
			case 'toSelect': {
				return { 
					...truck, 
					nameList,
					status: 50
				}
			}
			case 'toAll': {
				return { 
					...truck, 
					nameList,
					status: 10
				}
			}
			default:
				return truck
		}
	})
	
}

// функция адаптирования объекта машины при загрузке из БД
export function truckAdapter(truck) {
	return {
		...truck,
		dateRequisition: dateHelper.getDateForInput(truck.dateRequisition),
	}
}

// функция адаптирования объекта машины при загрузке из сообщения WS
export function truckAdapterFromWS(truck) {
	return {
		...truck,
		idTGTruck: toNumberFromWS(truck.idTGTruck),
		numTruck: toStringFromWS(truck.numTruck),
		modelTruck: toStringFromWS(truck.modelTruck),
		fio: toStringFromWS(truck.fio),
		otherInfo: toStringFromWS(truck.otherInfo),
		pall: toNumberFromWS(truck.pall),
		typeTrailer: toStringFromWS(truck.typeTrailer),
		dateRequisition: toStringFromWS(truck.dateRequisition),
		cargoCapacity: toStringFromWS(truck.cargoCapacity),
		chatIdUserTruck: toNumberFromWS(truck.chatIdUserTruck),
		nameList: toStringFromWS(truck.nameList),
		idList: toNumberFromWS(truck.idList),
		status: toNumberFromWS(truck.status),
		companyName: toStringFromWS(truck.companyName),
		secondRound: toBoleanFromWS(truck.secondRound),
	}
}
function toNumberFromWS(value) {
	return value === 'null' ? null : Number(value)
}
function toStringFromWS(value) {
	return value === 'null' ? null : value
}
function toBoleanFromWS(value) {
	return value === 'null'
		? false
		: value === 'true' || value === true
			? true
			: false
}

// функция получения действия для обновления машины
export function getUpdateAction(truck) {
	const status = truck.status
	return status === 50 ? 'toSelected' : 'toFree'
}

// функция обновления опций селекта списков машин
export function updateTruckListsOptions(truckLists, nameListToSelect) {
	const truckListsSelect = document.querySelector("#truckListsSelect")

	// выбираем стартовый элемент, если нет списков
	const defaultOptionSelected = truckLists.length === 0 ? "selected" : ""

	// стартовый элемент
	truckListsSelect.innerHTML = `<option ${defaultOptionSelected} disabled value=''>Выберите список автомобилей</option>`

	// опции названий списков машин
	truckLists.forEach((list) => {
		const option = document.createElement("option")
		option.value = list.nameList
		option.text = list.nameList
		// выделяем выбранный элемент, если он есть
		if (nameListToSelect === list.nameList) option.selected = true
		truckListsSelect.appendChild(option)
	})

	if (nameListToSelect === "") {
		truckListsSelect.selectedIndex = 0
		truckListsSelect.dispatchEvent(new Event("change"))
		return
	}

	// выделяем вторую опцию, если список не пустой и нет выбранного эдемента
	if (truckLists.length !== 0 && !nameListToSelect) {
		truckListsSelect.selectedIndex = 1
		truckListsSelect.dispatchEvent(new Event("change"))
		return
	}
}

// функции управдения датой
export function nextDate(dateInput) {
	const currentDate = dateInput.value
	const nextMs = new Date(currentDate).getTime() + dateHelper.DAYS_TO_MILLISECONDS
	const nextDate = dateHelper.getDateForInput(nextMs)

	dateInput.value = nextDate
	dateInput.dispatchEvent(new Event("change"))
}
export function prevDate(dateInput) {
	const currentDate = dateInput.value
	const prevMs = new Date(currentDate).getTime() - dateHelper.DAYS_TO_MILLISECONDS
	const prevDate = dateHelper.getDateForInput(prevMs)

	dateInput.value = prevDate
	dateInput.dispatchEvent(new Event("change"))
}

// изменение отступа контейнера с контентом в зависимости от высоты хэдэра
export function changeContentMarginTop(freeTrucksGridDiv, selectedTrucksGridDiv) {
	const navbar = document.querySelector('.navbar')
	const height = navbar.offsetHeight

	if (height < 65) {
		const myContainer = document.querySelector('.my-container')
		myContainer.classList.add('smallHeader')
		freeTrucksGridDiv.classList.add('smallHeader')
		selectedTrucksGridDiv.classList.add('smallHeader')
	}
}

// функция получения списков машин
export function getTruckLists(trucks) {
	return trucks
		.reduce((acc, truck) => {
			const nameList = truck.nameList
			const date = truck.dateRequisition
			if (nameList) {
				// добавляем список машин, если его нет в списке
				const isExist = !!acc.find(list => list.nameList === nameList && list.date === date)
				if (!isExist) acc.push({ nameList: nameList, date: truck.dateRequisition })
			}
			return acc
		}, [])
}

// функция группировки машин по дате
export function groupTrucksByDate(trucks) {
	return trucks
		.reduce((acc, truck) => {
			const date = truck.dateRequisition
			if (!acc[date]) acc[date] = []
			acc[date].push(truck)
			return acc
		}, {})
}