<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Менеджер заявок</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurementControl.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/autocomplete.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>Менеджер заявок</h3></strong>
		</div>
		<div class="accordion">
			<div class="search-form-container">
				<button class="accordion-btn collapsed" data-toggle="collapse" href="#orderSearchForm" role="button" aria-expanded="true" aria-controls="orderSearchForm">
					Поиск заявок
				</button>
				<form class="collapse" action="" id="orderSearchForm">
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">С</label>
						<input class="form-control" type="date" name="date_from" id="date_from" required>
					</div>
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">по</label>
						<input class="form-control" type="date" name="date_to" id="date_to" required>
					</div>
					<input class="form-control" type="text" name="searchName" id="searchName" placeholder="Наименование контрагента...">
					<button class="btn btn-outline-secondary" type="submit">Отобразить</button>
				</form>
			</div>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно для добавления точки выгрузки -->
	<div class="modal fade" id="addUnloadPointModal" tabindex="-1" aria-labelledby="addUnloadPointModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header pb-2">
					<h1 class="modal-title my-0" id="addUnloadPointModalLabel">Добавить точку выгрузки</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addUnloadPointForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<input type="hidden" class="form-control" name="idOrder" id="idOrder">
							<input type="hidden" class="form-control" name="type" id="type" value="Выгрузка">
							<div class="date-container">
								<div class="form-group input-row-container">
									<label class="text-muted font-weight-bold" for="date">Дата выгрузки <span class="text-red">*</span></label>
									<input type="date" class="form-control date-input" name="date" id="unloadDate" required>
								</div>
								<div class="form-group input-row-container">
									<label class="text-muted font-weight-bold" for="time">Время выгрузки <span class="text-red">*</span></label>
									<select id="unloadTime" name="time" class="form-control" required>
										<option value="" hidden disabled selected> --:-- </option>
										<option class="font-weight-bold" value="00:00">00:00</option>
										<option value="00:30">00:30</option>
										<option class="font-weight-bold" value="01:00">01:00</option>
										<option value="01:30">01:30</option>
										<option class="font-weight-bold" value="02:00">02:00</option>
										<option value="02:30">02:30</option>
										<option class="font-weight-bold" value="03:00">03:00</option>
										<option value="03:30">03:30</option>
										<option class="font-weight-bold" value="04:00">04:00</option>
										<option value="04:30">04:30</option>
										<option class="font-weight-bold" value="05:00">05:00</option>
										<option value="05:30">05:30</option>
										<option class="font-weight-bold" value="06:00">06:00</option>
										<option value="06:30">06:30</option>
										<option class="font-weight-bold" value="07:00">07:00</option>
										<option value="07:30">07:30</option>
										<option class="font-weight-bold" value="08:00">08:00</option>
										<option value="08:30">08:30</option>
										<option class="font-weight-bold" value="09:00">09:00</option>
										<option value="09:30">09:30</option>
										<option class="font-weight-bold" value="10:00">10:00</option>
										<option value="10:30">10:30</option>
										<option class="font-weight-bold" value="11:00">11:00</option>
										<option value="11:30">11:30</option>
										<option class="font-weight-bold" value="12:00">12:00</option>
										<option value="12:30">12:30</option>
										<option class="font-weight-bold" value="13:00">13:00</option>
										<option value="13:30">13:30</option>
										<option class="font-weight-bold" value="14:00">14:00</option>
										<option value="14:30">14:30</option>
										<option class="font-weight-bold" value="15:00">15:00</option>
										<option value="15:30">15:30</option>
										<option class="font-weight-bold" value="16:00">16:00</option>
										<option value="16:30">16:30</option>
										<option class="font-weight-bold" value="17:00">17:00</option>
										<option value="17:30">17:30</option>
										<option class="font-weight-bold" value="18:00">18:00</option>
										<option value="18:30">18:30</option>
										<option class="font-weight-bold" value="19:00">19:00</option>
										<option value="19:30">19:30</option>
										<option class="font-weight-bold" value="20:00">20:00</option>
										<option value="20:30">20:30</option>
										<option class="font-weight-bold" value="21:00">21:00</option>
										<option value="21:30">21:30</option>
										<option class="font-weight-bold" value="22:00">22:00</option>
										<option value="22:30">22:30</option>
										<option class="font-weight-bold" value="23:00">23:00</option>
										<option value="23:30">23:30</option>
									</select>
								</div>
							</div>
							<div class="cargo-container d-flex">
								<div class="form-group" style="flex: 2;">
									<label class="text-muted font-weight-bold" for="date">Наименование груза <span class="text-red">*</span></label>
									<input type="text" class="form-control" name="pointCargo" id="pointCargo" placeholder="Наименование" required>
								</div>
								<div class="form-group" style="flex: 1;">
									<label class="text-muted font-weight-bold" for="date">Паллеты, шт <span class="text-red">*</span></label>
									<input type="number" class="form-control" name="pall" id="pall" placeholder="Паллеты, шт" min="0">
								</div>
								<div class="form-group" style="flex: 1;">
									<label class="text-muted font-weight-bold" for="date">Масса, кг <span class="text-red">*</span></label>
									<input type="number" class="form-control" name="weight" id="weight" placeholder="Масса, кг" min="0">
								</div>
								<div class="form-group" style="flex: 1;">
									<label class="text-muted font-weight-bold" for="date">Объем, м.куб. <span class="text-red">*</span></label>
									<input type="number" class="form-control" name="volume" id="volume" placeholder="Объем, м.куб." min="0">
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес склада выгрузки <span class="text-red">*</span></label>
								<div class="form-group address-container">
									<input type="text" class="form-control country" style="flex: 1;" name="country" id="country" value="BY Беларусь" required readonly>
									<input type="text" class="form-control" style="flex: 4;" name="address" id="address" placeholder="Город, улица и т.д." required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Время работы склада выгрузки <span class="text-red">*</span></label>
								<div class="form-group timeFrame-container">
									С
									<input type="time" class="form-control" name="timeFrame_from" id="timeFrame_from" required>
									по
									<input type="time" class="form-control" name="timeFrame_to" id="timeFrame_to" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Контактное лицо на складе <span class="text-red">*</span></label>
								<div class="form-group contact-container">
									<input type="text" class="form-control" name="pointContact_fio" id="pointContact_fio" placeholder="ФИО" required>
									<input type="text" class="form-control" name="pointContact_tel" id="pointContact_tel" placeholder="Телефон" required>
								</div>
							</div>
							<div class="form-group customs-container">
								<label class="col-form-label text-muted font-weight-bold">Адрес таможенного пункта <span class="text-red">*</span></label>
								<div class="form-group address-container">
									<input type="text" class="form-control country" style="flex: 1;" name="customsCountry" id="customsCountry" value="BY Беларусь" required readonly>
									<input type="text" class="form-control" style="flex: 4;" name="customsAddress" id="customsAddress" placeholder="Адрес" required>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить точку</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<jsp:include page="footer.jsp" />
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/internationalStockSopport.js" type="module"></script>
</body>
</html>