import { uiIcons } from "./uiIcons.js"
import { dateHelper} from "./utils.js"
import { groupTrucksByDate, nextDate, prevDate, truckAdapter } from "./logisticsDelivery/trucks/trucksUtils.js"
import { store } from "./logisticsDelivery/deliveryShop/store.js"
import { deleteTruck, getTruckData, sendTruckData } from "./logisticsDelivery/deliveryShop/api.js"
import { agGridMapCallback, trucksGridOptions } from "./logisticsDelivery/deliveryShop/agGridUtils.js"
import { renderTable, updateTable } from "./AG-Grid/ag-grid-utils.js"


const gridOptions = {
	...trucksGridOptions,
	getContextMenuItems: getContextMenuItems,
}

document.addEventListener('DOMContentLoaded', async () => {
	// инициализация приложения
	await init()

	// рендер таблицы машин
	const gridDiv = document.querySelector('#myGrid')
	renderTable(gridDiv, gridOptions)

	// поле текущей даты
	const currentDateInput = document.querySelector('#currentDate')
	// кнопка переключения даты вперед
	const datePrevBtn = document.querySelector('#datePrev')
	// кнопка переключения даты назад
	const dateNextBtn = document.querySelector('#dateNext')
	// форма добавления авто
	const addNewTruckForm = document.querySelector('#addNewTruckForm')
	// форма установки даты копирования авто
	const copyTruckToDateForm = document.querySelector('#copyTruckToDateForm')

	// установака начальной даты
	currentDateInput.value = store.getCurrentDate()
	// обработка смены даты
	currentDateInput.addEventListener('change', changeCurrentDateHandler)
	datePrevBtn.addEventListener('click', () => prevDate(currentDateInput))
	dateNextBtn.addEventListener('click', () => nextDate(currentDateInput))
	// обработка отправки форм
	addNewTruckForm.addEventListener('submit', addNewTruckHandler)
	copyTruckToDateForm.addEventListener('submit', copyTruckToDateHandler)

	// установка минимальной даты для полей даты заявления авто
	const dateRequisitionInputs = document.querySelectorAll('.dateRequisition')
	for (const input of dateRequisitionInputs) {
		input.min = store.getCurrentDate()
	}

	// получение стартовых данных
	const trucks = store.getTrucksByCurrentDate()
	// рендер таблиц машин
	updateTable(gridOptions, trucks, agGridMapCallback)

	$('#addNewTruckModal').on('hide.bs.modal', () => addNewTruckForm.reset())
	$('#copyTruckToDateModal').on('hide.bs.modal', () => copyTruckToDateForm.reset())
})



// установка получение и установка стартовых данных
async function init() {
	// данные о машинах
	const truckData = await getTruckData()
	// адаптируем данные
	const mappedTruckData = truckData.map(truckAdapter)
	// машины по датам
	const trucks = groupTrucksByDate(mappedTruckData)
	// отправляем данные о машинах в стор
	store.setTrucks(trucks)
	// подписка на обновления стора
	store.subscribe((state) => {
		const trucks = state.trucks[state.currentDate]
		updateTable(gridOptions, trucks, agGridMapCallback)
	})
}

// контекстное меню таблицы
function getContextMenuItems(params) {
	const rowNode = params.node
	if (!rowNode) return

	const truck = rowNode.data

	const result = [
		{
			name: `Копировать авто на`,
			icon: uiIcons.files,
			subMenu: [
				{
					name: `завтра`,
					action: () => copyTruck('toTomorrow', truck),
				},
				{
					name: `дату ...`,
					action: () => copyTruck('toDate', truck),
				},
			]
		},
		"separator",
		{
			name: `Отменить авто`,
			action: () => deleteTruck(truck),
			icon: uiIcons.trash,
		},
		"separator",
		"excelExport",
	]

	return result
}

// обработчик изменения отображаемой даты
function changeCurrentDateHandler(e) {
	const date = e.target.value
	// сохраняем текущую дату
	store.setCurrentDate(date)
	// свободные машины на текущую дату
	const trucks = store.getTrucksByCurrentDate()
	// обновляем таблицы
	updateTable(gridOptions, trucks, agGridMapCallback)
}

// копирование авто
function copyTruck(type, truck) {
	const truckData = getNewTruckData(truck)
	if (type === 'toDate') {
		setDataToForm(addNewTruckForm, truckData)
		addNewTruckForm.isSecondRound.checked = truckData.isSecondRound === 'true'
		$('#addNewTruckModal').modal('show')
	} else {
		const nextMs = new Date(store.getCurrentDate()).getTime() + dateHelper.DAYS_TO_MILLISECONDS
		const tomorrow = dateHelper.getDateForInput(nextMs)
		truckData.dateRequisition = tomorrow
		sendTruckData(truckData, 'copyTruckToTomorrow')
	}
}

// обработка отправки формы заявления авто
function addNewTruckHandler(e) {
	e.preventDefault()
	const form = e.target
	const formData = new FormData(form)
	const data = Object.fromEntries(formData)
	const truckData = getNewTruckData(data)
	sendTruckData(truckData, 'addTruck')
}
// обработка отправки формы копирования авто на дату
function copyTruckToDateHandler(e) {
	e.preventDefault()
	const form = e.target
	const formData = new FormData(form)
	const data = Object.fromEntries(formData)
	const truckData = getNewTruckData(data)
	sendTruckData(truckData, 'copyTruckToDate')
}

// получение данных формы заявления авто
function getNewTruckData(data) {
	return {
		dateRequisition: data.dateRequisition ? data.dateRequisition : null,
		count: data.count ? Number(data.count) : 1,
		numTruck: data.numTruck ? data.numTruck : null,
		typeTrailer: data.typeTrailer ? data.typeTrailer : null,
		cargoCapacity: data.cargoCapacity ? Number(data.cargoCapacity).toFixed(1) : null,
		pall: data.pall ? Number(data.pall) : null,
		fio: data.fio ? data.fio : null,
		otherInfo: data.otherInfo ? data.otherInfo : null,
		typeStock: data.typeStock ? data.typeStock : null,
		isSecondRound: `${!!data.isSecondRound}`,
	}
}

// установка данных в форму
function setDataToForm(form, data) {
	for (const key in data) {
		if (form[key] && data[key]) {
			form[key].value = data[key]
		}
	}
}
