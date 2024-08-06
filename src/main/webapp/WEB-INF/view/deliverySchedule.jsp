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
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#sendExcelModal">
				Загрузить Excel
			</button>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- модальное окно редактирования поставки -->
	<div class="modal fade" id="editDeliveryModal" tabindex="-1" aria-labelledby="editDeliveryModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="editDeliveryModalLabel">Редактирование поставки</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editDeliveryForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div>
								<label class="sr-only" for="counterpartyCode">Код контрагента</label>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<div class="input-group-text">Код контрагента</div>
									</div>
									<input type="text" class="form-control" name="counterpartyCode" id="counterpartyCode" placeholder="Код контрагента">
								</div>
							</div>
							<div>
								<label class="sr-only" for="counterpartyName">Наименование контрагента</label>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<div class="input-group-text">Наименование</div>
									</div>
									<input type="text" class="form-control" name="counterpartyName" id="counterpartyName" placeholder="Наименование контрагента">
								</div>
							</div>
							<div>
								<label class="sr-only" for="contractNumber">Номер контракта</label>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<div class="input-group-text">Номер контракта</div>
									</div>
									<input type="text" class="form-control" name="contractNumber" id="contractNumber" placeholder="Номер контракта">
								</div>
							</div>
							<div>
								<label class="sr-only" for="note">Примечание</label>
								<div class="input-group mb-3">
									<div class="input-group-prepend">
										<div class="input-group-text">Примечание</div>
									</div>
									<input type="text" class="form-control" name="note" id="note" placeholder="Примечание">
								</div>
							</div>
							<div class="mb-3">
								<label class="sr-only" for="note">Неделя/Сроки</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Неделя/Сроки <span class="text-red">*</span></div>
									</div>
									<select id="deliveryFrequency" name="deliveryFrequency" class="form-control" required>
										<option value="" hidden disabled selected>Выберите один из пунктов</option>
										<option value=""></option>
										<option value="Магазин">Неделя</option>
										<option value="Склад">Сроки</option>
									</select>
								</div>
							</div>

							<div class="mb-2 text-muted font-weight-bold text-center">График поставок</div>
							<div id="scheduleContainer" class="scheduleContainer mb-3">
								<div>
									<label class="sr-only" for="note">Пн</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пн</div>
										</div>
										<select id="mon" name="mon" class="scheduleSelect form-control" required></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="note">Вт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вт</div>
										</div>
										<select id="tue" name="tue" class="scheduleSelect form-control" required></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="note">Ср</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Ср</div>
										</div>
										<select id="wed" name="wed" class="scheduleSelect form-control" required></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="note">Чт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Чт</div>
										</div>
										<select id="thu" name="thu" class="scheduleSelect form-control" required></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="note">Пт</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Пт</div>
										</div>
										<select id="fri" name="fri" class="scheduleSelect form-control" required></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="note">Сб</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Сб</div>
										</div>
										<select id="sat" name="sat" class="scheduleSelect form-control" required></select>
									</div>
								</div>
								<div>
									<label class="sr-only" for="note">Вс</label>
									<div class="input-group">
										<div class="input-group-prepend">
											<div class="input-group-text">Вс</div>
										</div>
										<select id="sun" name="sun" class="scheduleSelect form-control" required></select>
									</div>
								</div>
							</div>

							<div class="input-row-container flex-wrap">
								<div class="form-check form-group">
									<input type="checkbox" class="form-check-input" name="palletMultiple" id="palletMultiple">
									<label for="palletMultiple" class="form-check-label text-muted font-weight-bold">Кратно поддону</label>
								</div>
								<div class="form-check form-group">
									<input type="checkbox" class="form-check-input" name="carMultiple" id="carMultiple">
									<label for="carMultiple" class="form-check-label text-muted font-weight-bold">Кратно машине</label>
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

</body>
<script src="${pageContext.request.contextPath}/resources/js/deliverySchedule.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</html>