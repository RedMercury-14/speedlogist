<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Остаток товара на складах</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/orderSupportControlLogist.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>Остаток товара на складах</h3></strong>
		</div>
		<!-- <div class="accordion">
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
		</div> -->
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<div class="modal fade" id="blockProductModal" tabindex="-1" role="dialog" aria-labelledby="blockProductModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="blockProductModalLabel">Блокировка товара</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form class="" id="blockProductForm">
					<div class="modal-body">
						<input type="hidden" name="idProduct" id="idProduct">

						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="dateStart">Дата начала блокировки</label>
									<input type="date" name="dateStart" id="dateStart" class="form-control">
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="dateFinish">Дата окончания блокировки</label>
									<input type="date" name="dateFinish" id="dateFinish" class="form-control">
								</div>
							</div>
						</div>
					</div>

					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Сохранить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/orderSupportControlLogist.js" type="module"></script>
</body>
</html>