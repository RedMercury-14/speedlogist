
///////////////////////////////////////////////
// списки машин для развоза
///////////////////////////////////////////////
/** получение данных о машинах перевозчика */
export const getCarrierTGTrucksUrl = '/speedlogist/api/carrier/delivery-shop/getTrucks'
/** указания номера телефона из тг бота для перевозчика */
export const setTGTelNumberBaseUrl = '/speedlogist/api/carrier/delivery-shop/link/'
/** получение списка всех заявленных машин */
export const getTGTrucksUrl = '/speedlogist/api/logistics/deliveryShops/getTGTrucks'
/** загрузка машины на сервер */
export const loadTruckUrl = '/speedlogist/api/logistics/deliveryShops/update'
/** загрузка списка машин на сервер */
export const loadTruckListUrl = '/speedlogist/api/logistics/deliveryShops/updateList'


///////////////////////////////////////////////
// слоты
///////////////////////////////////////////////
/** получение заказов для отображения в слотах */
export const getOrdersForSlotsBaseUrl = '/speedlogist/api/manager/getOrdersForSlots4/'
/** предустановка слота */
export const preloadOrderUrl = `/speedlogist/api/slot/preload`
/** установка слота */
export const loadOrderUrl = `/speedlogist/api/slot/load`
/** обновление положения слота */
export const updateOrderUrl = `/speedlogist/api/slot/update`
/** удаление слота */
export const deleteOrderUrl = `/speedlogist/api/slot/delete`
/** подтверждение или снятие подтверждения слота */
export const confirmSlotUrl = `/speedlogist/api/slot/save`
/** редактирование комментария из Маркета */
export const editMarketInfoBaseUrl = `/speedlogist/api/manager/editMarketInfo/`
/** получение информации о заказе из Маркета */
export const getMarketOrderBaseUrl = `/speedlogist/api/manager/getMarketOrder/`
/** ручное выполнение проверок слота */
export const checkSlotBaseUrl = '/speedlogist/api/slot/getTest/'
/** проверка слота на бронь */
export const checkBookingBaseUrl = '/speedlogist/api/manager/testMarketOrderStatus/'
/** связывание заказов */
export const setOrderLinkingUrl = '/speedlogist/api/slots/order-linking/set'
/** получение отчета по перемещениям слотов между 1700 и 1800 */
export const getBalanceBaseUrl = '/speedlogist/api/balance2/'
/** получение маршрутов к заказу */
export const getRoutesHasOrderBaseUrl = `/speedlogist/api/manager/getRoutesHasOrder/`
/** проверка совпадения заказа с графика поставок */
export const checkScheduleBaseUrl = '/speedlogist/api/slots/delivery-schedule/checkSchedule/'


///////////////////////////////////////////////
// список перевозчиков
///////////////////////////////////////////////
/** изменение номера договора перевозчика */
export const changeNumContractUrl = `/speedlogist/api/manager/changeNumDocument`
/** блокировка/разблокировка перевозчика */
export const changeIsBlockedBaseUrl = `/speedlogist/api/manager/blockCarrier/`



///////////////////////////////////////////////
// архив перевозок
///////////////////////////////////////////////
/** получение архива перевозок */
export const getArchiveRoutesBaseUrl = `/speedlogist/api/carrier/getroutes`


///////////////////////////////////////////////
// список поставщиков
///////////////////////////////////////////////
/** получение списка контрагентов */
export const getCounterpartiesListUrl = '/speedlogist/api/logistics/getCounterpartiesList'


///////////////////////////////////////////////
// список магазинов
///////////////////////////////////////////////
/** загрузка экселя со списком магазинов */
export const loadShopsUrl = '/speedlogist/api/map/loadShop'
/** получение списка магазинов */
export const getAllShopsUrl = '/speedlogist/api/manager/getAllShops'
/** добавление магазина */
export const addShopUrl = "/speedlogist/api/manager/addShop"
/** редактирование магазина */
export const editShopUrl = "/speedlogist/api/manager/editShop"
/** удаление магазина */
export const deleteShopUrl = '/speedlogist/api/manager/deleteShop'
/** проверка наличия магазина в базе */
export const checkExistShopBaseUrl = '/speedlogist/api/manager/existShop/'


///////////////////////////////////////////////
// графики поставок на РЦ
///////////////////////////////////////////////
/** запрос на изменение статуса графика поставки */
export const changeScheduleStatusBaseUrl = '/speedlogist/api/slots/delivery-schedule/changeStatus/'
/** загрузка на сервер таблицы эксель */
export const loadRCExcelUrl = '/speedlogist/api/slots/delivery-schedule/loadRC'
/** получение всех графиков поставок на РЦ */
export const getScheduleRCUrl = '/speedlogist/api/slots/delivery-schedule/getListRC'
/** создание графика поставок на РЦ */
export const addScheduleRCItemUrl = '/speedlogist/api/slots/delivery-schedule/createRC'
/** редактирование графика поставок на РЦ */
export const editScheduleRCItemUrl = '/speedlogist/api/slots/delivery-schedule/editRC'
/** проверка наличия номера контракта в базе */
export const getScheduleNumContractBaseUrl = '/speedlogist/api/slots/delivery-schedule/getScheduleNumContract/'
/** изменение значения "Не учитывать в расчете ОРЛ" */
export const changeIsNotCalcBaseUrl = '/speedlogist/api/slots/delivery-schedule/changeIsNotCalc/'
/** изменение значения "Импорт" */
export const changeIsImportBaseUrl = '/speedlogist/api/slots/delivery-schedule/changeIsImport/'
/** отправка данных на почту */
export const sendScheduleRCDataToMailUrl = '/speedlogist/api/orl/sendEmail'
/** скачивание отчета по графикам поставок на РЦ */
export const downloadReportBaseUrl = '/speedlogist/file/get-order-statistic/'
/** количество заказов по дням недели для графиков поставок на РЦ */
export const getCountScheduleOrderHasWeekUrl = '/speedlogist/api/delivery-schedule/getCountScheduleOrderHasWeek'
/** количество поставок по дням недели для графиков поставок на РЦ */
export const getCountScheduleDeliveryHasWeekUrl = '/speedlogist/api/delivery-schedule/getCountScheduleDeliveryHasWeek'


///////////////////////////////////////////////
// графики поставок на ТО
///////////////////////////////////////////////
/** получение данных контрагентов */
export const getUnicContractCodeHasCounterpartyTOUrl = '/speedlogist/api/slots/delivery-schedule/getUnicContractCodeHasCounterpartyTO'
/** загрузка на сервер таблицы эксель */
export const loadTOExcelUrl = '/speedlogist/api/slots/delivery-schedule/loadTO'
/** получение всех графиков поставок на ТО */
export const getScheduleTOUrl = '/speedlogist/api/slots/delivery-schedule/getListTO'
/** получение графиков поставок на ТО по номеру контракта */
export const getScheduleTOByContractBaseUrl = '/speedlogist/api/slots/delivery-schedule/getListTOContract/'
/** получение графиков поставок на ТО по наименованию контрагента */
export const getScheduleTOByCounterpartyBaseUrl = '/speedlogist/api/slots/delivery-schedule/getListTOСounterparty/'
/** создание графиков поставок на ТО */
export const addScheduleTOItemUrl = '/speedlogist/api/slots/delivery-schedule/createTO'
/** редактирование графиков поставок на ТО */
export const editScheduleTOItemUrl = '/speedlogist/api/slots/delivery-schedule/editTOByCounterpartyAndShop'
/** изменение значения "Сегодня на сегодня" */
export const changeIsDayToDayBaseUrl = '/speedlogist/api/slots/delivery-schedule/changeDayToDay/'
/** метод редактирования графиков по коду контракта (изменяет только указанные поля) */
export const editTOByCounterpartyContractCodeOnlyUrl = '/speedlogist/api/slots/delivery-schedule/editTOByCounterpartyContractCodeOnly'
/** метод удаления всех графиков по коду контракта */
export const deleteAllTempSchedulesBaseUrl = '/speedlogist/api/slots/delivery-schedule/delScheduleByNumContract/'
/** установка кодового слова */
export const setCodeNameBaseUrl = '/speedlogist/api/slots/delivery-schedule/changeNameOfQuantum/'
/** скачивание файла с инструкцией */
export const downloadScheduleTOFaqUrl = '/speedlogist/file/delivery-schedule-to/downdoad/instruction-trading-objects'
/** отправка данных на почту */
export const sendScheduleTODataToMailUrl = '/speedlogist/api/orl/sendEmailTO'


///////////////////////////////////////////////
// документооборот
///////////////////////////////////////////////
/** получение списка сформированных актов */
export const getActsBaseUrl = `/speedlogist/api/logistics/documentflow/documentlist/`
/** подписание, отмена и установка даты получения документов акта */
export const setActStatusUrl = `/speedlogist/api/logistics/documentflow/documentlist/setActStatus/`
/** установка даты получения документов акта */
export const saveDocumentsArrivedDateUrl = `/speedlogist/api/logistics/documentflow/documentlist/saveDocumentsArrivedDate/`


///////////////////////////////////////////////
// список водителей
///////////////////////////////////////////////
/** получение списка водителей */
export const getThisCarrierDriversUrl ='/speedlogist/api/carrier/getMyDrivers'
/** создание нового водителя */
export const saveNewDriverUrl = '/speedlogist/api/carrier/saveNewDriver'
/** редактирование водителя */
export const updateDriverUrl ='/speedlogist/api/carrier/editDriver'
/** удаление водителя */
export const deleteDriverBaseUrl ='driverlist/delete'


///////////////////////////////////////////////
// internationalForm.js - старое создание маршрутов
///////////////////////////////////////////////
/** добавление точек в маршрут */
export const addPointsUrl ='/speedlogist/api/route/addpoints'


///////////////////////////////////////////////
// менеджер международных маршрутов - старый
///////////////////////////////////////////////
/** получение отчета по маршрутам */
export const getRoutesReportBaseUrl ='/speedlogist/api/manager/getReport/'
/** просмотр предложений по тендеру */
export const tenderOfferBaseUrl ='./international/tenderOffer'
/** обновление тендера - отправка на биржу, отмена тендера */
export const routeUpdateBaseUrl ='/speedlogist/api/logistics/routeUpdate/'

export const sendMessageToCarriersBaseUrl = '/speedlogist/main/carrier/tender/tenderpage' // отправка сообщения о тендере всем перевозчикам
export const toRouteShowBaseUrl = '../logistics/international/routeShow' // переход на страницу отображения точек выгрузок
export const toEditRouteBaseUrl = '/speedlogist/main/logistics/international/editRoute' // переход на страницу редактора маршрутов
export const getMemoryRouteMessageBaseUrl = '/speedlogist/api/memory/message/routes/' // запрос на завершение маршрута
export const toRouteEndBaseUrl = '/speedlogist/main/logistics/international/routeEnd' // завершение маршрута


///////////////////////////////////////////////
// New менеджер международных маршрутов
///////////////////////////////////////////////
/** получение количества сообщений по маршруту */
export const getNumMessageBaseUrl ='/speedlogist/api/info/message/numroute/'
export const getRoutesBaseUrl = '/speedlogist/api/manager/getRouteForInternational/' // получение данных маршрутов
export const getProposalBaseUrl = `/speedlogist/api/logistics/getProposal/` // скачивание заявки на перевозку
export const confirmTenderOfferUrl = '/speedlogist/api/logistics/internationalNew/confrom' // подтверждение предложения (старые предложения)
export const getOffersForReductionByIdRouteBaseUrl = '/speedlogist/api/logistics/tenders/get-bids-by-id-route/' // получение предложжений для тендера на понижение
export const makeWinnerTenderForReductionOfferUrl = '/speedlogist/api/logistics/tenders/make-bid-winner' // подтверждение предложения тендера на понижение
export const makeTenderForReductionUrl = '/speedlogist/api/logistics/tenders/make-tender-for-reduction' // превращение обычного тендера в тендер на понижение
export const cancelOfferForLogistUrl = '/speedlogist/api/logistics/tenders/delete-bid' // отмена предложения по тендеру логистом

///////////////////////////////////////////////
// internationalStockSopport.js - Таблица Башкирова
///////////////////////////////////////////////
export const getOrderForStockSupportBaseUrl ='/speedlogist/api/stock-support/getOrders/' // получение заказов
export const getOrdersHasCounterpartyUrl ='/speedlogist/api/manager/getOrdersHasCounterparty/' // получение заказов по наименованию контрагента
export const addUnloadPointUrl = '/speedlogist/api/stock-support/addAdress' // добавление точки выгрузки


///////////////////////////////////////////////
// Поиск заявок по коду товара
///////////////////////////////////////////////
export const getOrderStatByTimeDeliveryAndOLUrl ='/speedlogist/api/procurement/getOrderStat/paramTimeDeliveryAndOL/'
//export const downloadReport330BaseUrl = '/speedlogist/file/330/' // метод действует но устарел
export const downloadReport330BaseUrl = '/speedlogist/file/330V2/'


///////////////////////////////////////////////
// Контроль заявок
///////////////////////////////////////////////
export const getOrderBaseUrl ='/speedlogist/api/manager/getOrders/'
export const getOrdersForStockProcurementBaseUrl ='speedlogist/api/manager/getOrdersForStockProcurement/'
export const getChangeOrderStatusBaseUrl ='/speedlogist/api/manager/changeOrderStatus/'


///////////////////////////////////////////////
// Менеджер маршрутов АХО/СГИ
///////////////////////////////////////////////
export const getAhoRouteBaseUrl = `/speedlogist/api/logistics/getMaintenanceList/` // получение маршрутов АХО
export const addCarrierBaseUrl = `/speedlogist/api/logistics/maintenance/setCarrier/` // установка перевозчика на маршрут АХО
export const clearCarrierBaseUrl = `/speedlogist/api/logistics/maintenance/clearCarrier/` // удаление перевозчика с маршрута АХО
export const setMileageBaseUrl = `/speedlogist/api/logistics/maintenance/setMileage/`
export const clearMileageBaseUrl = `/speedlogist/api/logistics/maintenance/clearMileage/`
export const setFinishPriceBaseUrl = `/speedlogist/api/logistics/maintenance/setCost/`
export const clearFinishPriceBaseUrl = `/speedlogist/api/logistics/maintenance/clearCost/`
export const closeRouteBaseUrl = `/speedlogist/api/logistics/maintenance/closeRoute/`
export const editRHSUrl = `/speedlogist/api/logistics/editRouteHasShop`
export const getAllCarrierUrl = `/speedlogist/api/manager/getAllCarrier`
export const getTrucksByCarrierBaseUrl =`/speedlogist/api/carrier/getCarByIdUser/`
export const getDriverByCarrierBaseUrl =`/speedlogist/api/carrier/getDriverByIdUser/`
export const getAhoRouteForCarrierBaseUrl = `/speedlogist/api/carrier/getMaintenanceList/`
export const setMileageForCarrierBaseUrl = `/speedlogist/api/carrier/maintenance/setMileage/`
export const clearMileageForCarrierBaseUrl = `/speedlogist/api/carrier/maintenance/clearMileage/`


///////////////////////////////////////////////
// Маршрутизатор
///////////////////////////////////////////////
export const mapOptimization3Url = `/speedlogist/api/map/myoptimization3`
export const mapOptimization5Url = `/speedlogist/api/map/myoptimization5`
export const saveOptimizeRouteParamsUrl = `/speedlogist/api/map/set`
export const getAllPolygonsUrl = `/speedlogist/api/map/getAllPolygons`
export const sendGeojsonDataUrl = `/speedlogist/api/map/savePolygon`
export const deletePolygonBaseUrl = `/speedlogist/api/map/delPolygon/`
export const checkNamePolygonBaseUrl = `/speedlogist/api/map/checkNamePolygon/`
export const getRouterParamsUrl = `/speedlogist/api/map/getDefaultParameters`
export const setRouterParamsUrl = `/speedlogist/api/map/setDefaultParameters`
export const getRoutingListUrl = `/speedlogist/api/map/way/4`
export const getMapStackTraceUrl = `/speedlogist/api/map/getStackTrace`
export const sendMapExcelFileUrl = `/speedlogist/api/map/5`
export const sendMapExcelFileWithReportUrl = `/speedlogist/api/map/6`


///////////////////////////////////////////////
// myMessage.js
///////////////////////////////////////////////
export const getMessagesByLoginBaseUrl = '/speedlogist/api/mainchat/messagesList'


///////////////////////////////////////////////
// Остаток товара на складах
///////////////////////////////////////////////
export const getStockRemainderUrl = '/speedlogist/api/order-support/getStockRemainder'
export const setNewBalanceBaseUrl = '/speedlogist/api/order-support/setNewBalance/'
export const setMaxDayBaseUrl = '/speedlogist/api/order-support/setMaxDay/'
export const changeExceptionBaseUrl = '/speedlogist/api/order-support/changeException/'
export const blockProductUrl = '/speedlogist/api/order-support/blockProduct'


///////////////////////////////////////////////
// страница Загрузить отчет
///////////////////////////////////////////////
export const send487ReportUrl = `/speedlogist/api/order-support/control/487` // загрузка на сервер 487 отчета
export const send490ReportUrl = `/speedlogist/api/order-support/control/490` // загрузка на сервер 490 отчета
export const sendPromotionsReportUrl = `/speedlogist/api/order-support/control/promotions`
export const sendTempSchedulesReportUrl = `/speedlogist/api/order-support/control/loadSchedules`
export const loadFileTestUrl = `/speedlogist/file/loadFileTest`


///////////////////////////////////////////////
// Приход паллет
///////////////////////////////////////////////
export const getPalletsCalculatedBaseUrl ='/speedlogist/api/get-pallets/'


///////////////////////////////////////////////
// Приход паллет
///////////////////////////////////////////////
export const getOrlNeedBaseUrl = `/speedlogist/api/orl/need/getNeed/`
export const loadOrlNeedExcelUrl = `/speedlogist/api/orl/need/load`


///////////////////////////////////////////////
// 398 отчет
///////////////////////////////////////////////
export const getReport398List = '/speedlogist/api/orl/task/getlist'
export const addTask398Url ='/speedlogist/api/orl/task/addTask398/'
export const downloadReport398Url ='/speedlogist/file/orl/download/zip398'


///////////////////////////////////////////////
// История решений по заказам
///////////////////////////////////////////////
export const getPermissionListBaseUrl ='/speedlogist/api/procurement/permission/getList/'


///////////////////////////////////////////////
// Менеджер заявок
///////////////////////////////////////////////
export const getOrdersForLogistBaseUrl ='/speedlogist/api/manager/getOrdersForLogist/'
export const createRouteUrl = '/speedlogist/api/manager/createNewRoute'
export const createAhoRouteUrl = '/speedlogist/api/manager/maintenance/add'
export const getDataHasOrderBaseUrl ='/speedlogist/api/manager/getDataHasOrder2/'
export const getOrdersLinksBaseUrl = `/speedlogist/api/logistics/getOrdersLinks/` // получение связанных заказов


///////////////////////////////////////////////
// Создание, Копирование и Редактирование заявок
///////////////////////////////////////////////
export const getOrderByIdBaseUrl = `/speedlogist/api/procurement/getOrderById/` // получение заказа для копирования или редактирвания
export const getInternalMovementShopsUrl = "/speedlogist/api/manager/getInternalMovementShops"
export const addNewAhoOrderUrl = "/speedlogist/api/manager/addNewProcurementByMaintenance"
export const addNewOrderUrl = "/speedlogist/api/manager/addNewProcurement"
export const addNewOrderHasMarketUrl = "/speedlogist/api/manager/addNewProcurementHasMarket"
export const editOrderUrl = "/speedlogist/api/manager/editProcurement"


///////////////////////////////////////////////
// Регистрация пользователя
///////////////////////////////////////////////
export const registrationUserUrl = "/speedlogist/api/user/registration"
export const sendFileAgreeUrl = "/speedlogist/file/sendFileAgree"
export const sendContractUrl = "/speedlogist/file/sendContract"
export const checkLoginUrl = "/speedlogist/api/user/isexists"
export const checkNumYnpUrl = "/speedlogist/api/user/isexistsUNP"


///////////////////////////////////////////////
// registrationWorker.js
///////////////////////////////////////////////
export const postUserIsExistUrl = "/speedlogist/api/user/isexists"



///////////////////////////////////////////////
// routeForm.js
///////////////////////////////////////////////
export const getRouteBaseUrl = "/speedlogist/api/route/"
export const addRoutePatternConformBaseUrl = "/speedlogist/main/logistics/international/addRoutePattern/confrom"
export const getRouteShowBaseUrl = "/speedlogist/api/logistics/international/routeShow"


///////////////////////////////////////////////
// routeManager.js
///////////////////////////////////////////////
export const toRouadUpdateUrl = "../logistics/rouadUpdate"
export const toTenderpageUrl = "../carrier/tender/tenderpage"
export const setRouteTemperatureUrl = "/speedlogist/api/route/temperature"
export const setRouteTimeLoadPreviouslyUrl = "/speedlogist/api/route/timeLoadPreviously"
export const setRouteUserCommentsUrl = "/speedlogist/api/route/userComments"
export const setRouteTypeTrailerUrl = "/speedlogist/api/route/typeTrailer"
export const setRouteTimeUrl = "/speedlogist/api/route/time"


///////////////////////////////////////////////
// Текущие тендеры
///////////////////////////////////////////////
export const getActiveTendersUrl = `/speedlogist/api/carrier/tenders/all`
export const getInfoRouteMessageBaseUrl = `/speedlogist/api/info/message/routes/`
export const getThisUserUrl = '/speedlogist/api/getThisUser'
export const setTenderOfferUrl = '/speedlogist/api/carrier/tenders/get-bid'
export const deleteTenderOfferUrl = '/speedlogist/api/carrier/tenders/delete-bid'
export const getThisUserIdUrl = '/speedlogist/api/get-this-user'
export const getNewTenderNotificationFlagUrl = '/speedlogist/api/user/get-new-tender-notification' // получение флага для получения уведомлений о новых тендерах
export const setNewTenderNotificationFlagUrl = '/speedlogist/api/user/new-tender-notification' // изменение флага для получения уведомлений о новых тендерах



///////////////////////////////////////////////
// Текущие маршруты
///////////////////////////////////////////////
export const getMyActualRoutesUrl = '/speedlogist/api/carrier/get-actual-carrier-routes'
export const setRouteParametersUrl = '/speedlogist/api/carrier/transportation/set-route-parameters'


///////////////////////////////////////////////
// История тендеров
///////////////////////////////////////////////
export const getTenderHistoryUrl = '/speedlogist/api/carrier/getStatusTenderForMe'


///////////////////////////////////////////////
// tenderOffer.js
///////////////////////////////////////////////
export const nbrbExratesRatesBaseUrl = 'https://www.nbrb.by/api/exrates/rates/' // запрос на конвертацию суммы по курсу НБ РБ
export const checkOrderForStatusBaseUrl = '/speedlogist/api/logistics/checkOrderForStatus/'


///////////////////////////////////////////////
// tenderOffer.js
///////////////////////////////////////////////
export const setTenderCostFromCarrierUrl = `/speedlogist/api/carrier/cost`
export const getInfoParticipantsMessageBaseUrl = '/speedlogist/api/info/message/participants/'


///////////////////////////////////////////////
// Toast.js
///////////////////////////////////////////////
export const saveMainchatMessageUrl = '/speedlogist/api/mainchat/massage/add'


///////////////////////////////////////////////
// Управление автопарком
///////////////////////////////////////////////
export const getThisCarrierCarsUrl ='/speedlogist/api/carrier/getMyCar/'
export const isContainTruckBaseUrl ='/speedlogist/api/carrier/isContainCar/'
export const saveNewTruckUrl = '/speedlogist/api/carrier/saveNewTruck'
export const editTruckUrl ='/speedlogist/api/carrier/editTruck'
export const deleteTruckBaseUrl ='trucklist/delete'


///////////////////////////////////////////////
// Контроль автопарка (список автомобилей перевозчика)
///////////////////////////////////////////////
export const getCarByIdUserBaseUrl ='/speedlogist/api/carrier/getCarByIdUser/'
export const verifyCarBaseUrl = '/speedlogist/api/manager/changeVertCar/'


///////////////////////////////////////////////
// Форма обратной связи
///////////////////////////////////////////////
export const createUserReviewUrl = '/speedlogist/api/reviews/create'
export const getReviewsBaseUrl = '/speedlogist/api/reviews/get-reviews/'
export const updateUserReviewUrl = '/speedlogist/api/reviews/update-review'


///////////////////////////////////////////////
// Отдел качества
///////////////////////////////////////////////
export const getUnprocessedAcceptanceQualityUrl = '/speedlogist/tsd/unprocessedAcceptanceQuality'
export const getInProcessAcceptanceQualityUrl = '/speedlogist/tsd/inProcessAcceptanceQuality'
export const getClosedAcceptanceQualityBaseUrl = '/speedlogist/tsd/closedAcceptanceQuality'
export const getAllAcceptanceQualityFoodCardUrl = '/speedlogist/tsd/getAllAcceptanceQualityFoodCard'
export const aproofQualityFoodCardUrl = '/speedlogist/tsd/aproofQualityFoodCard'


///////////////////////////////////////////////
// Таблица ротаций
///////////////////////////////////////////////
export const loadRotationExcelUrl = '/speedlogist/api/rotations/load'
export const getRotationListUrl = '/speedlogist/api/rotations/get-rotations'
export const preCreateRotationUrl = '/speedlogist/api/rotations/pre-creation'
export const approveCreateRotationUrl = '/speedlogist/api/rotations/create'
export const sendEmailRotationsUrl = '/speedlogist/api/rotations/send-email-rotations'
export const updateRotationUrl = '/speedlogist/api/rotations/update-rotation'
export const getActualRotationsExcelUrl = '/speedlogist/file/rotations/get-actual-rotations-excel'
export const downloadRotationFAQUrl = '/speedlogist/file/rotations/download/instruction-rotations'


///////////////////////////////////////////////
// Протокол согласования цены (713)
///////////////////////////////////////////////
export const getPriceProtocolListUrl = '/speedlogist/api/procurement/price-protocol/getList'
export const createPriceProtocolUrl = '/speedlogist/api/procurement/price-protocol/create'
export const createArrayOfPriceProtocolUrl = '/speedlogist/api/procurement/price-protocol/createArray'
export const loadPriceProtocolExcelUrl = '/speedlogist/api/procurement/price-protocol/load'


///////////////////////////////////////////////
// Актуальные предложения международных перевозок
///////////////////////////////////////////////
export const getTenderPreviewBaseUrl = '/speedlogist/api/get-tender-preview/'


///////////////////////////////////////////////
// Форма отправки данных о сотрудничестве
///////////////////////////////////////////////
export const createCarrierApplicationUrl = '/speedlogist/api/carrier-application/create'


///////////////////////////////////////////////
// 
///////////////////////////////////////////////
export const getAllProductControlUrl = '/speedlogist/api/procurement/product-control/getAll'
export const loadProductControlExcelUrl = '/speedlogist/api/procurement/product-control/load'
export const editProductControlUrl = '/speedlogist/api/procurement/product-control/edit'

