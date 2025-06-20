import { getDefaultGridOptions } from "../../AG-Grid/ag-grid-utils.js"

const columnDefs = [
	{ headerName: 'Номер', field: 'numshop', flex: 1, rowDrag: true, },
	{ headerName: 'Порядок', field: 'order', flex: 1, },
	{ headerName: 'Адрес', field: 'address', flex: 5, cellClass: 'px-1 text-center', },
	{ headerName: 'Паллеты', field: 'pall', flex: 1, cellClass: 'px-1 text-center', },
	{ headerName: 'Масса', field: 'weight', flex: 1, cellClass: 'px-1 text-center', },
]

const defaultGridOptions = getDefaultGridOptions({
	localStorageKey: null,
	enableColumnStateSaving: false,
	enableFilterStateSaving: false,
})

export const shopGridOptions = {
	...defaultGridOptions,
	columnDefs: columnDefs,
	defaultColDef: {
		...defaultGridOptions.defaultColDef,
	},
	getRowId: node => node.data.id,
	rowDragManaged: true,
}

export function agGridMapCallback(shop) {
	return {
		...shop,
	}
}