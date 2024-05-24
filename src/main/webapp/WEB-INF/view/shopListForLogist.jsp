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
	<title>Список магазинов</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/shopList.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container-fluid my-container">
		<div class="title-container">
			<strong><h3>Список магазинов</h3></strong>
		</div>
		<div class="toolbar">
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addShopModal">
				+ Добавить магазин
			</button>
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addShopsInExcelModal">
				++ Загрузить магазины из Excel
			</button>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<div class="modal fade" id="addShopModal" tabindex="-1" aria-labelledby="addShopModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="addShopModalLabel">Добавить новый магазин или склад</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addShopForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="input-row-container shopNumber-container">
								<div class="form-group number-container">
									<label class="col-form-label text-muted font-weight-bold">Магазин или склад? <span class="text-red">*</span></label>
									<select id="type" name="type" class="form-control" required>
										<option value="" hidden disabled selected>Выберите один из пунктов</option>
										<option value="Магазин">Магазин</option>
										<option value="Склад">Склад</option>
									</select>
								</div>
								<div class="form-group number-container" >
									<label class="col-form-label text-muted font-weight-bold">Номер <span class="text-red">*</span></label>
									<input type="number" class="form-control" name="numshop" id="numshop" placeholder="Номер магазина/склада" min="0" required>
									<div class="error-message" id="messageNumshop"></div>
								</div>
								<!-- <div class="form-group number-container">
									<label class="col-form-label text-muted font-weight-bold">Ежедневные чистки <span class="text-red">*</span></label>
									<select id="cleaning" name="cleaning" class="form-control" required>
										<option value="" hidden disabled selected>Выберите один из пунктов</option>
										<option value="Да">Да</option>
										<option value="Нет">Нет</option>
									</select>
								</div> -->
							</div>
							<div class="form-check form-group">
								<input type="checkbox" class="form-check-input" name="isTailLift" id="isTailLift">
								<label for="isTailLift" class="form-check-label text-muted font-weight-bold">Обязательно ли наличие гидроборта?</label>
							</div>
							<div class="form-check form-group">
								<input type="checkbox" class="form-check-input" name="isInternalMovement" id="isInternalMovement">
								<label for="isInternalMovement" class="form-check-label text-muted font-weight-bold">Используется для внутреннего перемещения?</label>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="address" id="address" placeholder="Адрес магазина/склада" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Координаты <span class="text-red">*</span></label>
								<div class="form-group input-row-container">
									<input type="text" class="form-control" name="lat" id="lat" placeholder="Широта" required>
									<input type="text" class="form-control" name="lng" id="lng" placeholder="Долгота" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Ограничения по подъезду (Д/Ш/В), м</label>
								<div class="form-group input-row-container">
									<input type="number" class="form-control" step="0.01" name="length" id="length" placeholder="Максимальная длина">
									<input type="number" class="form-control" step="0.01" name="width" id="width" placeholder="Максимальная ширина">
									<input type="number" class="form-control" step="0.01" name="height" id="height" placeholder="Максимальная высота">
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Ограничения по вместимости (паллет в машине)</label>
								<div class="form-group input-row-container">
									<input type="number" class="form-control" name="maxPall" id="maxPall" placeholder="Максимальное колличество паллет в машине">
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

	<div class="modal fade" id="editShopModal" tabindex="-1" aria-labelledby="editShopModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="editShopModalLabel">Редактирование</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editShopForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="input-row-container flex-wrap">
								<div class="form-group number-container">
									<label class="col-form-label text-muted font-weight-bold">Магазин или склад? <span class="text-red">*</span></label>
									<select id="type" name="type" class="form-control" required>
										<option value="" hidden disabled selected>Выберите один из пунктов</option>
										<option value="Магазин">Магазин</option>
										<option value="Склад">Склад</option>
									</select>
								</div>
								<!-- <div class="form-group number-container">
									<label class="col-form-label text-muted font-weight-bold">Ежедневные чистки <span class="text-red">*</span></label>
									<select id="cleaning" name="cleaning" class="form-control" required>
										<option value="" hidden disabled selected>Выберите один из пунктов</option>
										<option value="Да">Да</option>
										<option value="Нет">Нет</option>
									</select>
								</div> -->
								<input type="hidden" class="form-control" name="numshop" id="numshop" placeholder="Номер магазина/склада" min="0" required>
							</div>
							<div class="form-check form-group">
								<input type="checkbox" class="form-check-input" name="isTailLift" id="isTailLift">
								<label for="isTailLift" class="form-check-label text-muted font-weight-bold">Обязательно ли наличие гидроборта?</label>
							</div>
							<div class="form-check form-group">
								<input type="checkbox" class="form-check-input" name="isInternalMovement" id="isInternalMovement">
								<label for="isInternalMovement" class="form-check-label text-muted font-weight-bold">Используется для внутреннего перемещения?</label>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="address" id="address" placeholder="Адрес магазина/склада" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Координаты <span class="text-red">*</span></label>
								<div class="form-group input-row-container">
									<input type="text" class="form-control" name="lat" id="lat" placeholder="Широта" required>
									<input type="text" class="form-control" name="lng" id="lng" placeholder="Долгота" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Ограничения по подъезду (Д/Ш/В), м</label>
								<div class="form-group input-row-container">
									<input type="number" class="form-control" step="0.01" name="length" id="length" placeholder="Максимальная длина">
									<input type="number" class="form-control" step="0.01" name="width" id="width" placeholder="Максимальная ширина">
									<input type="number" class="form-control" step="0.01" name="height" id="height" placeholder="Максимальная высота">
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Ограничения по вместимости (паллет в машине)</label>
								<div class="form-group input-row-container">
									<input type="number" class="form-control" name="maxPall" id="maxPall" placeholder="Максимальное колличество паллет в машине">
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

	<div class="modal fade" id="addShopsInExcelModal" tabindex="-1" aria-labelledby="addShopsInExcelModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="addShopsInExcelModalLabel">Загрузить магазины</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addShopsInExcelForm" action="">
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
<script src="${pageContext.request.contextPath}/resources/js/shopListForLogist.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</html>