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
	<title>Список поставщиков</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/counterpartiesList.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>Список поставщиков</h3></strong>
		</div>
		<div class="toolbar">
			<button type="button" id="addShopBtn" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addShopModal">
				+ Добавить поставщика
			</button>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- добавление поставщика -->
	<div class="modal fade" id="addShopModal" tabindex="-1" aria-labelledby="addShopModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header bg-color">
					<h1 class="modal-title fs-5 mt-0" id="addShopModalLabel">Добавить нового поставщика</h1>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addShopForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group w-50">
								<label class="col-form-label text-muted font-weight-bold">Выберите тип <span class="text-red">*</span></label>
								<select id="type" name="type" class="form-control" required>
									<option value="" hidden disabled selected>Выберите один из пунктов</option>
									<option value="Поставщик">Поставщик</option>
								</select>
							</div>
							<div class="input-row-container">
								<div class="form-group w-75" >
									<label class="col-form-label text-muted font-weight-bold">Наименование поставщика</label>
									<input type="text" class="form-control" list="counterpartiesList" name="name" id="name" placeholder="Начните ввод" required>
								</div>
								<div class="form-group number-container w-25" >
									<label class="col-form-label text-muted font-weight-bold">Код поставщика<span class="text-red">*</span></label>
									<input type="number" class="form-control" name="numshop" id="numshop" placeholder="Код поставщика" min="0" required>
									<div class="error-message" id="messageNumshop"></div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="address" id="address" placeholder="Адрес поставщика" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Координаты <span class="text-red">*</span></label>
								<div class="form-group input-row-container">
									<input type="text" class="form-control" name="lat" id="lat" placeholder="Широта" required>
									<input type="text" class="form-control" name="lng" id="lng" placeholder="Долгота" required>
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

	<!-- редактирование поставщика -->
	<div class="modal fade" id="editShopModal" tabindex="-1" aria-labelledby="editShopModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header bg-color">
					<h1 class="modal-title fs-5 mt-0" id="editShopModalLabel">Редактирование поставщика</h1>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editShopForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group w-50">
								<label class="col-form-label text-muted font-weight-bold">Выберите тип <span class="text-red">*</span></label>
								<select id="type" name="type" class="form-control" required>
									<option value="" hidden disabled selected>Выберите один из пунктов</option>
									<option value="Поставщик">Поставщик</option>
								</select>
							</div>
							<div class="input-row-container">
								<div class="form-group w-75" >
									<label class="col-form-label text-muted font-weight-bold">Наименование поставщика</label>
									<input type="text" class="form-control" list="counterpartiesList" name="name" id="name" placeholder="Начните ввод" readonly required>
								</div>
								<div class="form-group number-container w-25" >
									<label class="col-form-label text-muted font-weight-bold">Код поставщика<span class="text-red">*</span></label>
									<input type="number" class="form-control" name="numshop" id="numshop" placeholder="Код поставщика" min="0" readonly required>
									<div class="error-message" id="messageNumshop"></div>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="address" id="address" placeholder="Адрес поставщика" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Координаты <span class="text-red">*</span></label>
								<div class="form-group input-row-container">
									<input type="text" class="form-control" name="lat" id="lat" placeholder="Широта" required>
									<input type="text" class="form-control" name="lng" id="lng" placeholder="Долгота" required>
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

	<datalist id="counterpartiesList">
		<!-- здест располагается список контрагентов -->
	</datalist>
</body>
<script src="${pageContext.request.contextPath}/resources/js/counterpartiesList.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</html>