<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Графики поставок на ТО</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/deliverySchedule.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0">

		<sec:authorize access="isAuthenticated()">
			<sec:authentication property="principal.authorities" var="roles" />
			<sec:authentication property="name" var="login"/>
		</sec:authorize>

		<div class="title-container">
			<strong><h3>Графики поставок на ТО</h3></strong>
			<div class="search-form-container">
				<form action="" id="searchData">
					<div class="input-row-container">
						<input class="form-control form-control-sm" type="text" name="searchValue" id="searchValue" placeholder="Наименование контрагента или номер контракта..." required>
						<button class="btn btn-outline-secondary text-nowrap btn-sm" type="submit">Загрузить данные</button>
						<button class="btn btn-outline-secondary text-nowrap btn-sm" type="button" id="loadAllData">Загрузить все данные</button>
					</div>
				</form>
			</div>
		</div>
		<div class="toolbar">
			<!-- <select class="btn tools-btn font-weight-bold" name="numStockSelect" id="numStockSelect">
				<option value="">Все склады</option>
			</select> -->
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addScheduleItemModal">
				+ Добавить новый график
			</button>
			<c:choose>
				<c:when test="${roles == '[ROLE_ADMIN]'}">
					<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#sendExcelModal">
						Загрузить Excel
					</button>
				</c:when>
			</c:choose>
			<c:choose>
				<c:when test="${roles == '[ROLE_ADMIN]' || login == 'romashkok%!dobronom.by'}">
					<!-- <button type="button" id="sendScheduleDataToMail" class="btn tools-btn font-weight-bold text-muted ml-auto">
						Отправить данные
					</button> -->
				</c:when>
			</c:choose>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- модальное окно создание графика поставки -->
	<div class="modal fade" id="addScheduleItemModal" tabindex="-1" aria-labelledby="addScheduleItemModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="addScheduleItemModalLabel">Создание графика поставки на ТО</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addScheduleItemForm" action="">
					<div class="modal-body">
						<div class="inputs-container">

							<input type="hidden" name="supplies" id="supplies">
							<input type="hidden" name="type" id="type" value="ТО">

							<div class="mb-3">
								<label class="sr-only" for="toType">Холодный или Сухой</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Холодный или Сухой</div>
									</div>
									<select id="toType" name="toType" class="form-control" required>
										<option value="" selected hidden disabled>Выберите вариант</option>
										<option value="сухой">Сухой</option>
										<option value="холодный">Холодный</option>
									</select>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="counterpartyCode">Код контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Код контрагента</div>
									</div>
									<input type="number" class="form-control" name="counterpartyCode" id="counterpartyCode" list="counterpartyCodeList" min="0" placeholder="Код контрагента" required>
								</div>
								<datalist id="counterpartyCodeList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="name">Наименование</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Наименование</div>
									</div>
									<input type="text" class="form-control" name="name" id="name" list="counterpartyNameList" placeholder="Наименование контрагента" required>
								</div>
								<datalist id="counterpartyNameList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="counterpartyContractCode">Номер контракта</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Номер контракта</div>
									</div>
									<input type="number" class="form-control counterpartyContractCode" name="counterpartyContractCode" id="counterpartyContractCode" min="0" placeholder="Номер контракта" required>
								</div>
								<div class="error-message" id="messageNumshop"></div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="numStock">Номера ТО</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Номера ТО</div>
									</div>
									<textarea
										class="form-control numStock" name="numStock" id="numStock" rows="3"
										placeholder="Просто скопируйте сюда строку или столбец с номерами магазинов" required></textarea>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="comment">Примечание</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Примечание</div>
									</div>
									<input type="text" class="form-control" name="comment" id="comment" placeholder="Примечание">
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="orderFormationSchedule">График формирования заказа</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">График формирования заказа</div>
									</div>
									<select id="orderFormationSchedule" name="orderFormationSchedule" class="form-control">
										<option value="" selected hidden disabled>Выберите вариант</option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="orderShipmentSchedule">График отгрузки заказа</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">График отгрузки заказа</div>
									</div>
									<select id="orderShipmentSchedule" name="orderShipmentSchedule" class="form-control">
										<option class="text-muted" value="" selected hidden disabled>Выберите вариант</option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<h5 class="mt-2 mb-0 text-muted font-weight-bold text-center">График поставок</h5>
							<div class="form-check form-group">
								<input type="checkbox" class="form-check-input" name="note" id="addNote">
								<label for="addNote" class="form-check-label text-muted font-weight-bold">Пометка "Неделя"</label>
							</div>
							<div id="scheduleContainer" class="scheduleContainer mb-3">
								<div>
									<label class="sr-only" for="monday">Пн</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пн</div>
										</div>
										<select id="monday" name="monday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="tuesday">Вт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вт</div>
										</div>
										<select id="tuesday" name="tuesday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="wednesday">Ср</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ср</div>
										</div>
										<select id="wednesday" name="wednesday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="thursday">Чт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Чт</div>
										</div>
										<select id="thursday" name="thursday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="friday">Пт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пт</div>
										</div>
										<select id="friday" name="friday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="saturday">Сб</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Сб</div>
										</div>
										<select id="saturday" name="saturday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="sunday">Вс</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вс</div>
										</div>
										<select id="sunday" name="sunday" class="scheduleSelect form-control"></select>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<!-- модальное окно редактирования графика поставки -->
	<div class="modal fade" id="editScheduleItemModal" tabindex="-1" aria-labelledby="editScheduleItemModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="editScheduleItemModalLabel">Редактирование графика поставки</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editScheduleItemForm" action="">
					<div class="modal-body">
						<div class="inputs-container">

							<input type="hidden" name="idSchedule" id="idSchedule">
							<input type="hidden" name="supplies" id="supplies">
							<input type="hidden" name="type" id="type" value="ТО">

							<div class="mb-3">
								<label class="sr-only" for="toType">Холодный или Сухой</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Холодный или Сухой</div>
									</div>
									<select id="toType" name="toType" class="form-control" required>
										<option value="" selected hidden disabled>Выберите вариант</option>
										<option value="сухой">Сухой</option>
										<option value="холодный">Холодный</option>
									</select>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="counterpartyCode">Код контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Код контрагента</div>
									</div>
									<input type="number" class="form-control" name="counterpartyCode" id="counterpartyCode" list="counterpartyCodeList" min="0" placeholder="Код контрагента" required>
								</div>
								<datalist id="counterpartyCodeList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="name">Наименование</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Наименование</div>
									</div>
									<input type="text" class="form-control" name="name" id="name" list="counterpartyNameList" placeholder="Наименование контрагента" required>
								</div>
								<datalist id="counterpartyNameList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="counterpartyContractCode">Номер контракта</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Номер контракта</div>
									</div>
									<input type="number" class="form-control counterpartyContractCode" name="counterpartyContractCode" id="counterpartyContractCode" min="0" placeholder="Номер контракта" required>
								</div>
								<div class="error-message" id="messageNumshop"></div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="numStock">Номер ТО</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Номер ТО</div>
									</div>
									<input type="number" class="form-control numStock" name="numStock" id="numStock" min="0" placeholder="Номер ТО" required>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="comment">Примечание</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Примечание</div>
									</div>
									<input type="text" class="form-control" name="comment" id="comment" placeholder="Примечание">
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="orderFormationSchedule">График формирования заказа</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">График формирования заказа</div>
									</div>
									<select id="orderFormationSchedule" name="orderFormationSchedule" class="form-control">
										<option value="" selected hidden disabled>Выберите вариант</option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="orderShipmentSchedule">График отгрузки заказа</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">График отгрузки заказа</div>
									</div>
									<select id="orderShipmentSchedule" name="orderShipmentSchedule" class="form-control">
										<option class="text-muted" value="" selected hidden disabled>Выберите вариант</option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<h5 class="mt-2 mb-0 text-muted font-weight-bold text-center">График поставок</h5>
							<div class="form-check form-group">
								<input type="checkbox" class="form-check-input" name="note" id="editNote">
								<label for="editNote" class="form-check-label text-muted font-weight-bold">Пометка "Неделя"</label>
							</div>
							<div id="scheduleContainer" class="scheduleContainer mb-3">
								<div>
									<label class="sr-only" for="monday">Пн</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пн</div>
										</div>
										<select id="monday" name="monday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="tuesday">Вт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вт</div>
										</div>
										<select id="tuesday" name="tuesday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="wednesday">Ср</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ср</div>
										</div>
										<select id="wednesday" name="wednesday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="thursday">Чт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Чт</div>
										</div>
										<select id="thursday" name="thursday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="friday">Пт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пт</div>
										</div>
										<select id="friday" name="friday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="saturday">Сб</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Сб</div>
										</div>
										<select id="saturday" name="saturday" class="scheduleSelect form-control"></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="sunday">Вс</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вс</div>
										</div>
										<select id="sunday" name="sunday" class="scheduleSelect form-control"></select>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Сохранить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- модальное окно загрузки таблицы Эксель -->
	<div class="modal fade" id="sendExcelModal" tabindex="-1" aria-labelledby="sendExcelModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="sendExcelModalLabel">Загрузить графики</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="sendExcelForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Укажите, какой это график</label>
								<select id="toType" name="toType" class="form-control" required>
									<option value="" selected hidden disabled>Выберите вариант</option>
									<option value="сухой">сухой</option>
									<option value="холодный">холодный</option>
								</select>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Загрузите файл Excel</label>
								<input type="file" class="form-control btn-outline-secondary p-1" name="excel"
									id="excel" required
									accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Загрузить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для отображения текста -->
	<div class="modal fade" id="displayMessageModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="displayMessageModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="displayMessageModalLabel">Сообщение</h5>
				</div>
				<div class="modal-body">
					<div id="messageContainer"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Ок, понятно</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно для визуализации графика -->
	<div class="modal fade" id="showScheduleModal" tabindex="-1" aria-labelledby="showScheduleModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h4 class="modal-title" id="showScheduleModalLabel">График поставки</h4>
				</div>
				<div class="modal-body">
					<div class="d-flex flex-column align-items-center">
						<div class="scheduleItem-container" id="scheduleItemContainer"></div>
						<br>
						<h5 class="modal-title">Визуализация графика</h5>
						<div class="matrix-container" id="matrixContainer"></div>
						<br>
						<ul class="matrix-legend">
							<li>
								<span class="font-weight-bold">з</span> - заказ
							</li>
							<li>
								<span class="font-weight-bold">п</span> - поставка
							</li>
							<li>
								<span class="font-weight-bold">н0</span> - текущая неделя
							</li>
							<li>
								<span class="font-weight-bold">н1</span> - следующая неделя
							</li>
						</ul>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="${pageContext.request.contextPath}/resources/js/deliveryScheduleTO.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</html>