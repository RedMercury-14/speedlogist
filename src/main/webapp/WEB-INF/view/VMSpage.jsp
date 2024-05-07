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
	<title>ВМС кабинет</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/VMSpage.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container-fluid my-container px-0">
		<div class="inner-container left-container">
			<div id="numStockButtons" class="">
				<button data-stock="1700" class="btn btn-outline-primary btn-lg font-weight-bold">Склад 1700</button>
				<button data-stock="1200" class="btn btn-outline-primary btn-lg font-weight-bold">Склад 1200</button>
				<button data-stock="1250" class="btn btn-outline-primary btn-lg font-weight-bold">Склад 1250</button>
			</div>
		</div>
		<div class="inner-container right-container">
			<div class="title-container">
				<strong><h3>Ограничения паллетовместимости</h3></strong>
			</div>
			<div class="toolbar">
				<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#addRestrictionModal">
					+ Ввести ограничение
				</button>
			</div>
			<div id="myGrid" class="ag-theme-alpine"></div>
		</div>
		<div id="snackbar"></div>
	</div>

	<div class="modal fade" id="addRestrictionModal" tabindex="-1" aria-labelledby="addRestrictionModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="addRestrictionModalLabel">Добавить ограничение</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addRestrictionForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Дата</label>
								<input type="date" class="form-control" name="date" id="date" required>
							</div>
							<div class="form-group" >
								<label class="col-form-label text-muted font-weight-bold">Номер склада</label>
								<select id="numStock" name="numStock" class="form-control" required>
									<option value="" hidden disabled selected>Выберите склад</option>
									<option value="1700">1700</option>
									<option value="1200">1200</option>
									<option value="1250">1250</option>
								</select>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Максимальное число паллет</label>
								<input type="number" class="form-control" step="10" name="maxPall" id="maxPall" placeholder="Паллетовместимость" required>
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

	<div class="modal fade" id="editRestrictionModal" tabindex="-1" aria-labelledby="editRestrictionModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="editRestrictionModalLabel">Редактирование</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editRestrictionForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<input type="hidden" name="restrictionId" id="restrictionId">
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Дата</label>
								<input type="date" class="form-control" name="date" id="date" required readonly>
							</div>
							<div class="form-group" >
								<label class="col-form-label text-muted font-weight-bold">Номер склада</label>
								<input type="text" class="form-control" name="numStock" id="numStock" required readonly>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Максимальное число паллет</label>
								<input type="number" class="form-control" step="10" name="maxPall" id="maxPall" placeholder="Паллетовместимость" required>
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

	<jsp:include page="footer.jsp" />
</body>
<script src="${pageContext.request.contextPath}/resources/js/VMSpage.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</html>