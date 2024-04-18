// виртуальное представление складов для отображения ивентов
export const stocks = [
	{
		id: '1700',
		name: 'Склад 1700',
		workingHoursStart: '00:00',
		workingHoursEnd: '24:00',
		maxPall: 1700,
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
		workingHoursStart: '08:00',
		workingHoursEnd: '21:00',
		maxPall: 500,
		weekends: [],
		ramps: [
			{ id: "120001", title: "Рампа 1", businessHours: { startTime: '09:00', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120002", title: "Рампа 2", businessHours: { startTime: '09:00', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
			{ id: "120003", title: "Рампа 3", businessHours: { startTime: '09:00', endTime: '20:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
		events: [],
	},
	{
		id: '1250',
		name: 'Склад 1250',
		workingHoursStart: '09:00',
		workingHoursEnd: '22:00',
		maxPall: 500,
		weekends: [],
		ramps: [
			{ id: "125001", title: "Рампа 1", businessHours: { startTime: '10:00', endTime: '21:00' , daysOfWeek: [ 0, 1, 2, 3, 4, 5, 6 ]}, },
		],
		events: [],
	},
]