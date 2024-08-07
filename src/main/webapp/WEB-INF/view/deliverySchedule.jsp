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
	<title>График поставок контрагентов</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/deliverySchedule.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>График поставок контрагентов</h3></strong>
		</div>
		<div class="toolbar">
			<!-- <select class="btn tools-btn font-weight-bold" name="numStockSelect" id="numStockSelect"></select> -->
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addScheduleItemModal">
				+ Добавить новый контракт
			</button>
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#sendExcelModal">
				Загрузить Excel
			</button>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- модальное окно создание графика поставки -->
	<div class="modal fade" id="addScheduleItemModal" tabindex="-1" aria-labelledby="addScheduleItemModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="addScheduleItemModalLabel">Создание графика поставки</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addScheduleItemForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<!-- <input type="hidden" name="idSchedule" id="idSchedule"> -->
							<input type="hidden" name="supplies" id="supplies">
							<!-- <input type="hidden" name="numStock" id="numStock">
							<input type="hidden" name="description" id="description">
							<input type="hidden" name="dateLasCalculation" id="dateLasCalculation">
							<input type="hidden" name="tz" id="tz">
							<input type="hidden" name="tp" id="tp"> -->

							<div class="mb-3">
								<label class="sr-only" for="counterpartyCode">Код контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Код контрагента</div>
									</div>
									<input type="number" class="form-control" name="counterpartyCode" id="counterpartyCode" min="0" placeholder="Код контрагента" required>
								</div>
							</div>
							<div class="mb-3">
								<label class="sr-only" for="name">Наименование контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Наименование</div>
									</div>
									<input type="text" class="form-control" name="name" id="name" placeholder="Наименование контрагента" required>
								</div>
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
								<label class="sr-only" for="comment">Примечание</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Примечание</div>
									</div>
									<input type="text" class="form-control" name="comment" id="comment" placeholder="Примечание">
								</div>
							</div>
							<div class="mb-3">
								<label class="sr-only" for="comment">Расчет стока до Y-й поставки</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Расчет стока до Y-й поставки</div>
									</div>
									<select id="runoffCalculation" name="runoffCalculation" class="form-control" required>
										<option value="" selected hidden disabled></option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
									</select>
								</div>
							</div>
							<div class="input-row-container flex-wrap">
								<div class="form-check form-group">
									<input type="checkbox" class="form-check-input" name="multipleOfPallet" id="multipleOfPallet">
									<label for="multipleOfPallet" class="form-check-label text-muted font-weight-bold">Кратно поддону</label>
								</div>
								<div class="form-check form-group">
									<input type="checkbox" class="form-check-input" name="multipleOfTruck" id="multipleOfTruck">
									<label for="multipleOfTruck" class="form-check-label text-muted font-weight-bold">Кратно машине</label>
								</div>
							</div>

							<div class="mb-2 text-muted font-weight-bold text-center">График поставок</div>
							<div class="mb-3">
								<label class="sr-only" for="note">Неделя/Сроки</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Неделя/Сроки</div>
									</div>
									<select id="note" name="note" class="form-control">
										<option value=""></option>
										<option value="неделя">Неделя</option>
										<option value="сроки">Сроки</option>
									</select>
								</div>
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
							<!-- <input type="hidden" name="numStock" id="numStock">
							<input type="hidden" name="description" id="description">
							<input type="hidden" name="dateLasCalculation" id="dateLasCalculation">
							<input type="hidden" name="tz" id="tz">
							<input type="hidden" name="tp" id="tp"> -->

							<div class="mb-3">
								<label class="sr-only" for="counterpartyCode">Код контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Код контрагента</div>
									</div>
									<input type="number" class="form-control" name="counterpartyCode" id="counterpartyCode" min="0" placeholder="Код контрагента" required>
								</div>
							</div>
							<div class="mb-3">
								<label class="sr-only" for="name">Наименование контрагента</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Наименование</div>
									</div>
									<input type="text" class="form-control" name="name" id="name" placeholder="Наименование контрагента" required>
								</div>
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
								<label class="sr-only" for="comment">Примечание</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Примечание</div>
									</div>
									<input type="text" class="form-control" name="comment" id="comment" placeholder="Примечание">
								</div>
							</div>
							<div class="mb-3">
								<label class="sr-only" for="comment">Расчет стока до Y-й поставки</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Расчет стока до Y-й поставки</div>
									</div>
									<select id="runoffCalculation" name="runoffCalculation" class="form-control" required>
										<option value="" selected hidden disabled></option>
										<option value="1">1</option>
										<option value="2">2</option>
										<option value="3">3</option>
										<option value="4">4</option>
									</select>
								</div>
							</div>
							<div class="input-row-container flex-wrap">
								<div class="form-check form-group">
									<input type="checkbox" class="form-check-input" name="multipleOfPallet" id="multipleOfPallet">
									<label for="multipleOfPallet" class="form-check-label text-muted font-weight-bold">Кратно поддону</label>
								</div>
								<div class="form-check form-group">
									<input type="checkbox" class="form-check-input" name="multipleOfTruck" id="multipleOfTruck">
									<label for="multipleOfTruck" class="form-check-label text-muted font-weight-bold">Кратно машине</label>
								</div>
							</div>

							<div class="mb-2 text-muted font-weight-bold text-center">График поставок</div>
							<div class="mb-3">
								<label class="sr-only" for="note">Неделя/Сроки</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Неделя/Сроки</div>
									</div>
									<select id="note" name="note" class="form-control">
										<option value=""></option>
										<option value="неделя">Неделя</option>
										<option value="сроки">Сроки</option>
									</select>
								</div>
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
					<h1 class="modal-title fs-5" id="sendExcelModalLabel">Загрузить магазины</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="sendExcelForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Загрузите файл Excel</label>
								<input type="file" class="form-control btn-outline-secondary" name="excel"
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
		<div class="modal-dialog">
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

</body>
<script src="${pageContext.request.contextPath}/resources/js/deliverySchedule.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</html>