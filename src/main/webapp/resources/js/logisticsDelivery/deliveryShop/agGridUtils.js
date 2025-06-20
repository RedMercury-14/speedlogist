import { getDefaultGridOptions } from "../../AG-Grid/ag-grid-utils.js"
import { CargoCapacitySumStatusBarComponent, CountStatusBarComponent, PallSumStatusBarComponent, RowLegengStatusBarComponent } from "../trucks/statusBar.js"

const columnDefs = [
	{ headerName: 'id', field: 'idTGTruck', minWidth: 60, flex: 1, sort: 'desc', },
	{ headerName: 'Название', field: 'numTruck', flex: 2, },
	// { headerName: 'Контакты водителя', field: 'fio', flex: 4, wrapText: true, autoHeight: true, },
	// { headerName: 'Модель', field: 'modelTruck', width: 150, },
	{ headerName: 'Тип', field: 'typeTrailer', flex: 2, },
	{ headerName: 'Тоннаж', field: 'cargoCapacity', flex: 1, cellClass: 'px-1 text-center font-weight-bold fs-1rem', },
	{ headerName: 'Паллеты', field: 'pall', flex: 1, cellClass: 'px-1 text-center font-weight-bold fs-1rem', },
	{ headerName: 'Склад загрузки', field: 'typeStock', flex: 2, cellClass: 'px-1 text-center', },
	{
		headerName: 'На 2 рейса', field: 'secondRound', flex: 1,
		cellDataType: false,
		valueFormatter: (params) => params.value ? "Да" : "Нет",
		filterParams: { valueFormatter: (params) => params.value ? "Да" : "Нет", },
	},
	{ headerName: 'Доп. инф-я', field: 'otherInfo', flex: 4, wrapText: true, autoHeight: true,},
]

const defaultGridOptions = getDefaultGridOptions({
	localStorageKey: null,
	enableColumnStateSaving: false,
	enableFilterStateSaving: false,
})

export const trucksGridOptions = {
	...defaultGridOptions,
	columnDefs: columnDefs,
	defaultColDef: {
		...defaultGridOptions.defaultColDef,
		flex: 3,
		lockPinned: true,
	},
	rowClassRules: {
		'light-green-row': params => params.node.data.status === 50,
		'light-orange-row': params => params.node.data.secondRound,
	},
	getRowId: (params) => params.data.idTGTruck,
	// rowSelection: 'multiple',
	overlayNoRowsTemplate: '<span class="h3">На указанную дату не заявлено ни одно авто</span>',
	statusBar: {
		statusPanels: [
			{ statusPanel: RowLegengStatusBarComponent, align: 'left', },
			{ statusPanel: CountStatusBarComponent, statusPanelParams: null, },
			{ statusPanel: PallSumStatusBarComponent, statusPanelParams: null, },
			{ statusPanel: CargoCapacitySumStatusBarComponent, statusPanelParams: null, },
		],
	},
}

export function agGridMapCallback(truck) {
	return {
		...truck,
	}
}