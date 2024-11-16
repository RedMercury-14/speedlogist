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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tooltip.css">
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
		</div>
		<div class="toolbar">
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addScheduleItemModal">
				+ Добавить график
			</button>
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#setCodeNameModal">
				Изменить кодовое имя
			</button>
			<c:choose>
				<c:when test="${roles == '[ROLE_ADMIN]'}">
					<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#sendExcelModal">
						Загрузить Excel
					</button>
				</c:when>
			</c:choose>
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
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- модальное окно создание графика поставки -->
	<div class="modal fade" id="addScheduleItemModal" tabindex="-1" aria-labelledby="addScheduleItemModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="addScheduleItemModalLabel">Создание графика поставок на ТО</h1>
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
									<input type="number" class="form-control counterpartyContractCode" name="counterpartyContractCode" id="counterpartyContractCode" list="contractCodeList" min="0" placeholder="Номер контракта" required>
								</div>
								<div class="error-message" id="messageNumshop"></div>
								<datalist id="contractCodeList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="numStock">Номера ТО</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text custom-tooltip">
											Номера ТО
											<sup class="px-1 font-weight-bold text-danger">?</sup>
											<span class="tooltiptext">Укажите номера ТО, для которых нужно создать график</span>
										</div>
									</div>
									<textarea
										class="form-control numStock" name="numStock" id="numStock" rows="3"
										placeholder="Просто скопируйте сюда строку или столбец с номерами магазинов или укажите номера магазинов через ПРОБЕЛ"
										required
									></textarea>
								</div>
							</div>

							<div class="form-row justify-content-between mx-0 mb-3">
								<div class="flex-grow-1 mr-2">
									<label class="sr-only" for="quantum">Квант</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text custom-tooltip">
												Квант
												<sup class="px-1 font-weight-bold text-danger">?</sup>
												<span class="tooltiptext">Укажите минимальный объем/кол-во/денежный эквивалент товара, который необходимо заказать, чтобы поставщик осуществил поставку.<br> В соседнем поле (появится после указания кванта) укажите единицы измерения кванта.</span>
											</div>
										</div>
										<input type="number" class="form-control" name="quantum" id="quantum" min="0" step="1" placeholder="Целое число">
									</div>
								</div>
								<div class="quantumMeasurements-container invisible">
									<label class="sr-only" for="quantumMeasurements">Ед. измерения</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ед. измерения</div>
										</div>
										<select id="quantumMeasurements" name="quantumMeasurements" class="form-control">
											<option value="" selected hidden disabled>Выберите вариант</option>
											<option value="c НДС">c НДС</option>
											<option value="без НДС">без НДС</option>
											<option value="кг">кг</option>
											<option value="упаковок">упаковок</option>
											<option value="штук">штук</option>
											<option value="Дал">Дал</option>
										</select>
									</div>
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
										<div class="input-group-text custom-tooltip">
											График формирования заказа
											<sup class="px-1 font-weight-bold text-danger">?</sup>
											<span class="tooltiptext">Поле заполняется только в случае оформления заказа 1 раз в две недели. Указывается четность недели. В случае, если заказ производится каждую неделю, поле заполнять НЕ НУЖНО</span>
										</div>
									</div>
									<select id="orderFormationSchedule" name="orderFormationSchedule" class="form-control">
										<option value="" selected hidden disabled>Выберите вариант</option>
										<option value=""></option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="orderShipmentSchedule">График отгрузки заказа</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text custom-tooltip">
											График отгрузки заказа
											<sup class="px-1 font-weight-bold text-danger">?</sup>
											<span class="tooltiptext">Поле заполняется только в случае оформления заказа 1 раз в две недели. Указывается четность недели. В случае, если заказ производится каждую неделю, поле заполнять НЕ НУЖНО</span>
										</div>
									</div>
									<select id="orderShipmentSchedule" name="orderShipmentSchedule" class="form-control">
										<option class="text-muted" value="" selected hidden disabled>Выберите вариант</option>
										<option value=""></option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<h5 class="mt-2 mb-0 text-muted font-weight-bold text-center">
								<span class="custom-tooltip">
									График поставок
									<sup class="px-1 font-weight-bold text-danger">?</sup>
									<span class="tooltiptext"><strong>Подсказка к заполнению полей:</strong><br><strong>День заказа: </strong>просто "з" — обозначает день, когда сделан заказ.<br><strong>День поставки: </strong>укажите день недели, когда был сделан заказ, например: "понедельник", "вторник".<br><strong>Заказ + постава в один день: </strong>формат "з/день недели", например: "з/понедельник".<br><strong>Поставки на другой неделе: </strong>формат "нX/день недели", где X — номер недели от заказа (от н0 до н10). Пример: "н2/четверг".<br><strong>Комбинированный формат: </strong>заказ, неделя и день недели — "з/нX/день недели", например: "з/н3/вторник".</span>
								</span>
							</h5>
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
										<input type="text" list="scheduleOptions" id="monday" name="monday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="tuesday">Вт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вт</div>
										</div>
										<input type="text" list="scheduleOptions" id="tuesday" name="tuesday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="wednesday">Ср</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ср</div>
										</div>
										<input type="text" list="scheduleOptions" id="wednesday" name="wednesday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="thursday">Чт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Чт</div>
										</div>
										<input type="text" list="scheduleOptions" id="thursday" name="thursday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="friday">Пт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пт</div>
										</div>
										<input type="text" list="scheduleOptions" id="friday" name="friday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="saturday">Сб</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Сб</div>
										</div>
										<input type="text" list="scheduleOptions" id="saturday" name="saturday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="sunday">Вс</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вс</div>
										</div>
										<input type="text" list="scheduleOptions" id="sunday" name="sunday" class="scheduleSelect form-control"></input>
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
					<h1 class="modal-title fs-5 mt-0" id="editScheduleItemModalLabel">Редактирование графика поставок</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editScheduleItemForm" action="">
					<div class="modal-body">
						<div class="inputs-container">

							<!-- <input type="hidden" name="idSchedule" id="idSchedule"> -->
							<input type="hidden" name="supplies" id="supplies">
							<input type="hidden" name="type" id="type" value="ТО">

							<div class="mb-3">
								<label class="sr-only" for="toType">Холодный или Сухой</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Холодный или Сухой</div>
									</div>
									<input type="text" readonly class="form-control" id="toType" name="toType" required>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="counterpartyCode">Код контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Код контрагента</div>
									</div>
									<input type="number" readonly class="form-control" name="counterpartyCode" id="counterpartyCode" list="counterpartyCodeList" min="0" placeholder="Код контрагента" required>
								</div>
								<datalist id="counterpartyCodeList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="name">Наименование</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Наименование</div>
									</div>
									<input type="text" readonly class="form-control" name="name" id="name" list="counterpartyNameList" placeholder="Наименование контрагента" required>
								</div>
								<datalist id="counterpartyNameList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="counterpartyContractCode">Номер контракта</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Номер контракта</div>
									</div>
									<input type="number" readonly class="form-control counterpartyContractCode" name="counterpartyContractCode" id="counterpartyContractCode" min="0" placeholder="Номер контракта" required>
								</div>
								<div class="error-message" id="messageNumshop"></div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="numStock">Номера ТО</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text custom-tooltip">
											Номера ТО
											<sup class="px-1 font-weight-bold text-danger">?</sup>
											<span class="tooltiptext">Укажите номера ТО, для которых вы хотите отредактировать график</span>
										</div>
									</div>
									<textarea
										class="form-control numStock" name="numStock" id="numStock" rows="3"
										placeholder="Просто скопируйте сюда строку или столбец с номерами магазинов или укажите номера магазинов через ПРОБЕЛ"
										required
									></textarea>
								</div>
							</div>

							<div class="form-row justify-content-between mx-0 mb-3">
								<div class="flex-grow-1 mr-2">
									<label class="sr-only" for="quantum">Квант</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text custom-tooltip">
												Квант
												<sup class="px-1 font-weight-bold text-danger">?</sup>
												<span class="tooltiptext">Укажите минимальный объем/кол-во/денежный эквивалент товара, который необходимо заказать, чтобы поставщик осуществил поставку</span>
											</div>
										</div>
										<input type="number" class="form-control" name="quantum" id="quantum" min="0" step="1" placeholder="Целое число">
									</div>
								</div>
								<div class="quantumMeasurements-container invisible">
									<label class="sr-only" for="quantumMeasurements">Ед. измерения</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ед. измерения</div>
										</div>
										<select id="quantumMeasurements" name="quantumMeasurements" class="form-control">
											<option value="" selected hidden disabled>Выберите вариант</option>
											<option value="c НДС">c НДС</option>
											<option value="без НДС">без НДС</option>
											<option value="кг">кг</option>
											<option value="упаковок">упаковок</option>
											<option value="штук">штук</option>
											<option value="Дал">Дал</option>
										</select>
									</div>
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
										<div class="input-group-text custom-tooltip">
											График формирования заказа
											<sup class="px-1 font-weight-bold text-danger">?</sup>
											<span class="tooltiptext">Поле заполняется только в случае оформления заказа 1 раз в две недели. Указывается четность недели. В случае, если заказ производится каждую неделю, поле заполнять НЕ НУЖНО</span>
										</div>
									</div>
									<select id="orderFormationSchedule" name="orderFormationSchedule" class="form-control">
										<option value="" selected hidden disabled>Выберите вариант</option>
										<option value=""></option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="orderShipmentSchedule">График отгрузки заказа</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text custom-tooltip">
											График отгрузки заказа
											<sup class="px-1 font-weight-bold text-danger">?</sup>
											<span class="tooltiptext">Поле заполняется только в случае оформления заказа 1 раз в две недели. Указывается четность недели. В случае, если заказ производится каждую неделю, поле заполнять НЕ НУЖНО</span>
										</div>
									</div>
									<select id="orderShipmentSchedule" name="orderShipmentSchedule" class="form-control">
										<option class="text-muted" value="" selected hidden disabled>Выберите вариант</option>
										<option value=""></option>
										<option value="ч">Четный</option>
										<option value="н">Нечетный</option>
									</select>
								</div>
							</div>

							<h5 class="mt-2 mb-0 text-muted font-weight-bold text-center">
								<span class="custom-tooltip">
									График поставок
									<sup class="px-1 font-weight-bold text-danger">?</sup>
									<span class="tooltiptext"><strong>Подсказка к заполнению полей:</strong><br><strong>День заказа: </strong>просто "з" — обозначает день, когда сделан заказ.<br><strong>День поставки: </strong>укажите день недели, когда был сделан заказ, например: "понедельник", "вторник".<br><strong>Заказ + постава в один день: </strong>формат "з/день недели", например: "з/понедельник".<br><strong>Поставки на другой неделе: </strong>формат "нX/день недели", где X — номер недели от заказа (от н0 до н10). Пример: "н2/четверг".<br><strong>Комбинированный формат: </strong>заказ, неделя и день недели — "з/нX/день недели", например: "з/н3/вторник".</span>
								</span>
							</h5>
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
										<input type="text" list="scheduleOptions" id="monday" name="monday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="tuesday">Вт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вт</div>
										</div>
										<input type="text" list="scheduleOptions" id="tuesday" name="tuesday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="wednesday">Ср</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ср</div>
										</div>
										<input type="text" list="scheduleOptions" id="wednesday" name="wednesday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="thursday">Чт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Чт</div>
										</div>
										<input type="text" list="scheduleOptions" id="thursday" name="thursday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="friday">Пт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пт</div>
										</div>
										<input type="text" list="scheduleOptions" id="friday" name="friday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="saturday">Сб</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Сб</div>
										</div>
										<input type="text" list="scheduleOptions" id="saturday" name="saturday" class="scheduleSelect form-control"></input>
									</div>
								</div>
								<div>
									<label class="sr-only" for="sunday">Вс</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вс</div>
										</div>
										<input type="text" list="scheduleOptions" id="sunday" name="sunday" class="scheduleSelect form-control"></input>
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


	<!-- модальное окно установки и редактирования кодового имени контрагента -->
	<div class="modal fade" id="setCodeNameModal" tabindex="-1" aria-labelledby="setCodeNameModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="setCodeNameModalLabel">Установка кодового имени контрагента</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="setCodeNameForm" action="">
					<div class="modal-body">
						<div class="inputs-container">

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
									<input type="text" readonly class="form-control" name="name" id="name" list="counterpartyNameList" placeholder="Наименование контрагента">
								</div>
								<datalist id="counterpartyNameList"></datalist>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="codeNameOfQuantumCounterparty">Кодовое имя</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Кодовое имя</div>
									</div>
									<input type="text" class="form-control" name="codeNameOfQuantumCounterparty" id="codeNameOfQuantumCounterparty" placeholder="Кодовое имя">
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

	<!-- Список опций для графика -->
	<datalist id="scheduleOptions"></datalist>

</body>
<script src="${pageContext.request.contextPath}/resources/js/deliveryScheduleTO.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</html>