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
	<title>Поиск заявок</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainReports.css">
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
		<div class="search-form-container">
			<form class="" action="" id="orderSearchForm">
				<span class="font-weight-bold text-muted mb-0">Поиск заявок</span>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">с</label>
					<input class="form-control" type="date" name="date_from" id="date_from" required>
				</div>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">по</label>
					<input class="form-control" type="date" name="date_to" id="date_to" required>
				</div>
				<input class="form-control w-25" type="number" name="productCode" id="productCode" placeholder="Код продукта..." required>
				<button class="btn btn-outline-secondary font-weight-bold" type="submit">Отобразить</button>
			</form>
		</div>

		<div class="toolbar">
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#report330Modal">
				Скачать SL (по 330 отчёту)
			</button>
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<div id="snackbar"></div>
	</div>

	<!-- модальное окно формы 330 отчёта -->
	<div class="modal fade" id="report330Modal" tabindex="-1" aria-labelledby="report330ModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="report330ModalLabel">Скачать SL (по 330 отчёту)</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="report330Form" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="mb-3">
								<label class="sr-only" for="report330_dateStart">Даты (не больше 31 дня)</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Даты (не больше 31 дня)</div>
									</div>
									<div class="input-group-prepend">
										<div class="input-group-text">с</div>
									</div>
									<input type="date" class="form-control" name="report330_dateStart" id="report330_dateStart" required>
									<div class="input-group-prepend">
										<div class="input-group-text">по</div>
									</div>
									<input type="date" class="form-control" name="report330_dateEnd" id="report330_dateEnd" required>
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="report330_stocks">Склады</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Склады</div>
									</div>
									<textarea required class="form-control" name="report330_stocks" id="report330_stocks" placeholder="Номера складов через запятую без пробелов"></textarea>
									<!-- <div class="sub-text text-danger mt-1">Если склады не указаны, поиск будет выполнен по ВСЕМ складам</div> -->
								</div>
							</div>

							<div class="mb-3">
								<label class="sr-only" for="report330_products">Товары</label>
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text">Товары</div>
									</div>
									<textarea class="form-control" name="report330_products" id="report330_products" placeholder="Номера товаров через запятую без пробелов"></textarea>
								</div>
							</div>

						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Скачать отчёт</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для отображения текста -->
	<div class="modal fade" id="displayMessageModal" ddata-keyboard="false" tabindex="-1" aria-labelledby="displayMessageModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="displayMessageModalLabel">Сообщение</h5>
				</div>
				<div class="modal-body">
					<div id="messageContainer"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/mainReports.js" type="module"></script>
</body>
</html>