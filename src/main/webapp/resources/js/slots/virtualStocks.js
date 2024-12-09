// виртуальное представление складов для отображения ивентов
export const stocks = [
	{
		id: '1700',
		name: 'Склад 1700',
		address: '223065, Беларусь, Луговослободской с/с, Минский р-н, Минская обл., РАД М4, 18км. 2а, склад W05',
		contact: '+375293473695',
		workingHoursStart: '00:00',
		workingHoursEnd: '24:00',
		shiftChange: ['08:00', '09:00', '20:00', '21:00'],
		internaMovementsTimes: ['12:00', '20:00'],
		internalMovementsRamps: ['170001'],
		maxPall: {
			externalMovement: 1300, // редактировать ПОСТОЯННУЮ паллетовместимость ЗДЕСЬ
			internalMovement: 500, // паллетовместимость внутренних перевозок
		},
		weekends: [],
		ramps: [
			{ id: "170001", title: "Рампа 1", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170002", title: "Рампа 2", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170003", title: "Рампа 3", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170004", title: "Рампа 4", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170005", title: "Рампа 5", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "170006", title: "Рампа 6 (Резерв)", businessHours: { startTime: '00:00', endTime: '24:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
		events: [],
	},
	{
		id: '1200',
		name: 'Склад 1200',
		address: '223039, Республика Беларусь, Минская область, Минский район, Хатежинский с/с, 1',
		contact: '+375447841737',
		workingHoursStart: '08:00',
		workingHoursEnd: '21:00',
		shiftChange: [],
		internaMovementsTimes: [],
		internalMovementsRamps: [],
		maxPall: {
			externalMovement: 600, // редактировать ПОСТОЯННУЮ паллетовместимость ЗДЕСЬ
			internalMovement: 500, // паллетовместимость внутренних перевозок
		},
		weekends: [],
		ramps: [
			{ id: "120001", title: "Рампа 1", businessHours: { startTime: '09:30', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120002", title: "Рампа 2", businessHours: { startTime: '09:30', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120003", title: "Рампа 3", businessHours: { startTime: '09:30', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120004", title: "Рампа 4", businessHours: { startTime: '09:30', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
		events: [],
	},
	{
		id: '1250',
		name: 'Склад 1250',
		address: '223050, Республика Беларусь, Минская область, Минский р-н, 9-ый км Московского шоссе',
		contact: '+375291984537',
		workingHoursStart: '09:00',
		workingHoursEnd: '22:00',
		shiftChange: [],
		internaMovementsTimes: [],
		internalMovementsRamps: [],
		maxPall: {
			externalMovement: 100, // редактировать ПОСТОЯННУЮ паллетовместимость ЗДЕСЬ
			internalMovement: 500, // паллетовместимость внутренних перевозок
		},
		weekends: [],
		ramps: [
			{ id: "125001", title: "Рампа 1", businessHours: { startTime: '10:00', endTime: '21:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
		events: [],
	},
	{
		id: '1100',
		name: 'Склад 1100',
		address: '223039, Республика Беларусь, Минская область, Минский район, Хатежинский с/с, 1',
		contact: '+375293146512, +375293158914',
		workingHoursStart: '08:00',
		workingHoursEnd: '20:00',
		shiftChange: [],
		internaMovementsTimes: [],
		internalMovementsRamps: [],
		maxPall: {
			externalMovement: 180, // редактировать ПОСТОЯННУЮ паллетовместимость ЗДЕСЬ
			internalMovement: 500, // паллетовместимость внутренних перевозок
		},
		weekends: [],
		ramps: [
			{ id: "110001", title: "Рампа 1", businessHours: { startTime: '09:00', endTime: '19:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "110002", title: "Рампа 2", businessHours: { startTime: '09:00', endTime: '19:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
		events: [],
	},
]