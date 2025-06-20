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
	<title>Маршруты развоза</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/logisticsDeliveryRoutes.css">
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
		</sec:authorize>

		<div class="title-container justify-content-center">
			<strong><h3>Маршруты развоза</h3></strong>
		</div>

		<div class="grid-container" id="gridContainer">

			<div class="grid-column left">
				<div class="toolbar">

					<select class="btn tools font-weight-bold" name="routesSelectLeft" id="routesSelectLeft">
						<option selected disabled value="">Выберите маршрут</option>
						<!-- здесь будет список маршрутов -->
					</select>

					<button type="button" class="btn tools tools-btn font-weight-bold text-muted" id="uploadStockExcelDataBtn" data-toggle="modal" data-target="#uploadStockExcelDataModal">
						<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="currentColor">
							<path d="M200-120q-33 0-56.5-23.5T120-200v-560q0-33 23.5-56.5T200-840h360v80H200v560h560v-360h80v360q0 33-23.5 56.5T760-120H200Zm120-160v-80h320v80H320Zm0-120v-80h320v80H320Zm0-120v-80h320v80H320Zm360-80v-80h-80v-80h80v-80h80v80h80v80h-80v80h-80Z"/>
						</svg>
					</button>

					<button type="button" class="btn tools tools-btn font-weight-bold text-muted" id="uploadRouteExcelDataBtn" data-toggle="modal" data-target="#uploadRouteExcelDataModal">
						<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
							<path d="M360-120q-66 0-113-47t-47-113v-327q-35-13-57.5-43.5T120-720q0-50 35-85t85-35q50 0 85 35t35 85q0 39-22.5 69.5T280-607v327q0 33 23.5 56.5T360-200q33 0 56.5-23.5T440-280v-400q0-66 47-113t113-47q66 0 113 47t47 113v327q35 13 57.5 43.5T840-240q0 50-35 85t-85 35q-50 0-85-35t-35-85q0-39 22.5-70t57.5-43v-327q0-33-23.5-56.5T600-760q-33 0-56.5 23.5T520-680v400q0 66-47 113t-113 47ZM240-680q17 0 28.5-11.5T280-720q0-17-11.5-28.5T240-760q-17 0-28.5 11.5T200-720q0 17 11.5 28.5T240-680Zm480 480q17 0 28.5-11.5T760-240q0-17-11.5-28.5T720-280q-17 0-28.5 11.5T680-240q0 17 11.5 28.5T720-200ZM240-720Zm480 480Z"/>
						</svg>
					</button>

					<button type="button" class="ml-auto btn tools tools-btn font-weight-bold text-muted" id="toggleRightTableVisible">
						<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-window-split" viewBox="0 0 16 16">
							<path d="M2.5 4a.5.5 0 1 0 0-1 .5.5 0 0 0 0 1Zm2-.5a.5.5 0 1 1-1 0 .5.5 0 0 1 1 0Zm1 .5a.5.5 0 1 0 0-1 .5.5 0 0 0 0 1Z"/>
							<path d="M2 1a2 2 0 0 0-2 2v10a2 2 0 0 0 2 2h12a2 2 0 0 0 2-2V3a2 2 0 0 0-2-2H2Zm12 1a1 1 0 0 1 1 1v2H1V3a1 1 0 0 1 1-1h12ZM1 13V6h6.5v8H2a1 1 0 0 1-1-1Zm7.5 1V6H15v7a1 1 0 0 1-1 1H8.5Z"/>
						</svg>
					</button>

				</div>

				<div id="myGridLeft" class="ag-theme-alpine"></div>

			</div>

			<div class="grid-column right">
				<div class="toolbar">

					<select class="btn tools font-weight-bold" name="routesSelectRight" id="routesSelectRight">
						<option selected disabled value="">Выберите маршрут</option>
						<!-- здесь будет список маршрутов -->
					</select>

				</div>

				<div id="myGridRight" class="ag-theme-alpine"></div>

			</div>
		</div>

		<div id="snackbar"></div>

	</div>

	<!-- Модальное окно загрузки задания по складам -->
	<div class="modal fade" id="uploadStockExcelDataModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="uploadStockExcelDataModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="uploadStockExcelDataModalLabel">Загрузка задания по складам</h5>
				</div>
				<form id="uploadStockExcelDataForm">
					<div class="modal-body">
						<div class="form-group">
							<label for="dateTask" class="mb-2 text-muted font-weight-bold">Дата задания</label>
							<input type="date" class="form-control w-auto" name="dateTask" id="dateTask" required>
						</div>
						<label for="excel1700" class="col-form-label text-muted font-weight-bold">Excel 1700</label>
						<input type="file" class="form-control btn-outline-secondary"
								name="excel1700" id="excel1700"
								accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
								required
						>
						<label for="excel1800" class="col-form-label text-muted font-weight-bold">Excel 1800</label>
						<input type="file" class="form-control btn-outline-secondary"
								name="excel1800" id="excel1800"
								accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
								required
						>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Загрузить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно загрузки маршрутов -->
	<div class="modal fade" id="uploadRouteExcelDataModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="uploadRouteExcelDataModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="uploadRouteExcelDataModalLabel">Загрузка задания с маршрутами</h5>
				</div>
				<form id="uploadRouteExcelDataForm">
					<div class="modal-body">
						<div class="form-group">
							<label for="dateTask" class="mb-2 text-muted font-weight-bold">Дата задания</label>
							<input type="date" class="form-control w-auto" name="dateTask" id="dateTask" required>
						</div>
						<label for="routesExcel" class="col-form-label text-muted font-weight-bold">Excel с маршрутами</label>
						<input type="file" class="form-control btn-outline-secondary"
								name="routesExcel" id="routesExcel"
								accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
								required
						>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Загрузить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
					</div>
				</form>
			</div>
		</div>
	</div>

</body>
<script src="${pageContext.request.contextPath}/resources/js/logisticsDeliveryRoutes.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</html>