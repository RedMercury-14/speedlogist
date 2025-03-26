import { openModal } from './acceptanceQualityMainCardsModal.js';
import {
    closedAcceptanceQualityUrl,
    inProcessAcceptanceQualityUrl,
    unprocessedAcceptanceQualityUrl
} from "./globalConstants/urls.js"

const BASE_URL = 'http://localhost:8080/speedlogist_war'; // или путь для продакшн сервера


document.addEventListener('DOMContentLoaded', function() {
    const gridDiv = document.querySelector('#grid');  // Ссылка на элемент с таблицей

    const gridOptions = {
        columnDefs: [
            { headerName: "id", field: "idAcceptanceFoodQuality", sort: "desc", minWidth: 80, width: 80 },
            { headerName: "ID Маркет", field: "idOrder", width: 120, },
            { headerName: "Фирма", field: "firmNameAccept", width: 160, },
            { headerName: "Гос номер", field: "carNumber", width: 120, },
            {
                headerName: "Дата план", field: "datePlanAcceptInMs", width: 140,
                valueFormatter: dateTimeValueFormatter,
                comparator: dateComparator,
                filterParams: { valueFormatter: dateTimeValueFormatter, },
            },
            { headerName: "Тип выгрузки", field: "unloadingTypeToView", width: 120, },
            { headerName: "Вес (кг)", field: "cargoWeight", width: 100, },
            { headerName: "SKU", field: "sku", minWidth: 50, width: 50 },
            { headerName: "ТТН", field: "ttn", width: 100, },
            { headerName: "О товаре", field: "infoAcceptance" },
            {
                headerName: "Дата старт", field: "dateStartProcessInMs", width: 140,
                valueFormatter: dateTimeValueFormatter,
                comparator: dateComparator,
                filterParams: { valueFormatter: dateTimeValueFormatter, },
            },
            {
                headerName: "Дата стоп", field: "dateStopProcessInMs", width: 140,
                valueFormatter: dateTimeValueFormatter,
                comparator: dateComparator,
                filterParams: { valueFormatter: dateTimeValueFormatter, },
            },
            {
                headerName: "Длительность", field: "durationProcess", width: 100,
                valueFormatter: (params) => params.value ? `${Math.floor(params.value / 60000)} мин.` : "Неизвестно",
                filterParams: { valueFormatter: (params) => params.value ? `${Math.floor(params.value / 60000)} мин.` : "Неизвестно", },
            },
            { headerName: "Работники", field: "workers", },
            {
                headerName: "Статус от работника", field: "userOrderStatus",
                valueFormatter: (params) => getStatusToView(params.value),
            },
            {
                headerName: "Общий статус", field: "qualityProcessStatus",
                cellClass: "text-center font-weight-bold",
                valueFormatter: (params) => getStatusToView(params.value),
            },
        ],
        defaultColDef: {
            sortable: true,
            filter: true
        },
        getContextMenuItems: (params) => getContextMenuItems(params),
        onGridReady: function (params) {
            console.log('ag-Grid готов!');
            gridOptions.api = params.api; // Сохраняем api после инициализации
            gridOptions.api.setRowData([]);    // Устанавливаем пустую таблицу при инициализации
            gridOptions.api.sizeColumnsToFit();
        }
    };

    const transformOrder = (item) => {
        const orderUserStatus = Array.isArray(item.acceptanceFoodQualityUsers)
            ? item.acceptanceFoodQualityUsers : [];
        // const foundUser = orderUserStatus.find((u) => u?.user?.idAcc === user?.idUser);
        // const userOrderStatus = foundUser ? foundUser.status : 0;
        const acceptance = item.acceptance || {};
        return {
            ...acceptance,
            ...item,
            idAcceptanceFoodQuality: item.idAcceptanceFoodQuality,
            carNumberToView: acceptance.carNumber || "Неизвестно",
            unloadingTypeToView: acceptance.unloadingType === 1 ? "Ручная" : "Автоматическая",
            ttn: acceptance.ttnInList?.map((ttn) => ttn.ttnName).join(", ") || "Нет данных",
            datePlanAcceptInMs: acceptance.datePlanAccept ? new Date(acceptance.datePlanAccept).getTime() : null,
            datePlanAcceptToView: acceptance.datePlanAccept ? datetoViewConverter(acceptance.datePlanAccept) : '',
            dateStartProcessInMs: item.dateStartProcess ? new Date(item.dateStartProcess).getTime() : null,
            dateStopProcessInMs: item.dateStopProcess ? new Date(item.dateStopProcess).getTime() : null,
            durationProcessToView: item.durationProcess ? `${Math.floor(item.durationProcess / 60000)} мин.` : "Неизвестно",
            pauseStatusToView: item.qualityProcessStatus === 50 ? "На паузе" : "",
            workers: item.acceptanceFoodQualityUsers?.map((user) => user.userYard.login).join(", ") || "Нет данных",
            // userOrderStatus,
        };
    };

    function getStatusToView(status) {
        switch (status) {
            case 0:
                return "Новый";
            case 10:
                return "В процессе";
            case 50:
                return "На паузе";
            case 100:
                return "Закрыт";
            default:
                return `Неизвестный статус (${status})`;
        }
    }


    const getContextMenuItems = (params) => {
        const rowNode = params.node;
        if (!rowNode) return [];

        const qualityOrderRow = rowNode.data;
        const userOrderStatus = qualityOrderRow.userOrderStatus
        const qualityProcessStatus = qualityOrderRow.qualityProcessStatus
        const isPause = userOrderStatus === 50

        const items = [
            {
                disabled: userOrderStatus === 0,
                name: "Показать карточки товаров",
                action: () => {

                    const content = qualityOrderRow;
                    openModal(content);
                    // setSelectedOrder(qualityOrderRow);
                    // setOpenCardListModal(true);
                },
            },
            "separator",
            "excelExport"
        ]

        return items;
    };

    function dateComparator(date1, date2) {
        if (!date1 || !date2) return 0
        const date1Value = new Date(date1).getTime()
        const date2Value = new Date(date2).getTime()
        return date1Value - date2Value
    }


    function dateTimeValueFormatter(params) {
        const value = params.value
        if (!value) return ''
        const date = new Date(value)
        const day = pad(date.getDate())
        const month = pad(date.getMonth() + 1)
        const year = date.getFullYear()
        const hours = pad(date.getHours())
        const minutes = pad(date.getMinutes())
        return `${day}.${month}.${year} ${hours}:${minutes}`

    }

    function pad(num) {
        return num > 9 ? `${num}` : `0${num}`;
    }



    function datetoViewConverter(dateStr) {
        if (!dateStr) return ''
        const date = new Date(dateStr)
        const day = pad(date.getDate())
        const month = pad(date.getMonth() + 1)
        const year = date.getFullYear()
        const hours = pad(date.getHours())
        const minutes = pad(date.getMinutes())
        return `${day}.${month}.${year} ${hours}:${minutes}`
    }

    function pad(value) {
        return value < 10 ? `0${value}` : value
    }

    const updateTable = (data) => {
        const gridApi = gridOptions.api;

        if (!data || !data.length) {
            gridApi.setRowData([]);
            gridApi.showNoRowsOverlay();
            return;
        }
        const transformedData = data.map(transformOrder);
        gridApi.setRowData(transformedData);
        gridApi.hideOverlay();
    };

    const loadData = async () => {
        gridOptions.api.showLoadingOverlay();

        const status = document.getElementById('status').value;
        const startDate = document.getElementById('startDate').value;
        const endDate = document.getElementById('endDate').value;

        const fetchData = await getAcceptanceQualityData(status, startDate, endDate);

        if (!fetchData) {
            gridOptions.api.hideOverlay();
            return;
        }

        updateTable(fetchData);
        gridOptions.api.hideOverlay();
    };

    async function getAcceptanceQualityData(status, startDate, endDate) {

        let url = '';

        // Выбираем URL в зависимости от статуса
        switch (status) {
            case "new":
                url = `${BASE_URL}${unprocessedAcceptanceQualityUrl}`;
                break;
            case "inProcess":
                url = `${BASE_URL}${inProcessAcceptanceQualityUrl}`;
                break;
            case "closed":
                url = `${BASE_URL}${closedAcceptanceQualityUrl}?startDate=${startDate}&endDate=${endDate}`;
                break;
            default:
                console.error("Неизвестный статус:", status);
                return null;
        }

        try {
            // Выполняем GET-запрос с помощью AJAX
            const data = await new Promise((resolve, reject) => {
                $.ajax({
                    type: "GET",
                    url: url,
                    success: (res) => {
                        console.log(`Получены данные для статуса ${status}:`, res);
                        resolve(res); // Преобразуем результат в промис
                    },
                    error: (err) => {
                        const errorStatus = err.status ? err.status : '';
                        snackbar.show(`Ошибка ${errorStatusText[errorStatus]}!`);
                        console.error(`Ошибка при загрузке данных для статуса ${status}:`, err);
                        reject(err); // Пробрасываем ошибку через промис
                    }
                });
            });

            return data; // Возвращаем данные из успешного запроса
        } catch (error) {
            console.error('Ошибка при выполнении GET-запроса:', error);
            return null; // Возвращаем null в случае ошибки
        }
    }


    document.getElementById('loadDataButton').addEventListener('click', loadData);

    // Проверяем, загружена ли библиотека
    if (!window.agGrid) {
        alert('Ошибка: ag-Grid не загружен!');
        return;
    }

    // Проверяем доступность "Enterprise"
    if (!agGrid.Grid) {
        alert('Ошибка: agGrid.Grid не инициализирован!');
        return;
    }

    // Создаем таблицу
    new agGrid.Grid(gridDiv, gridOptions);


    console.log(acceptanceData);





});
