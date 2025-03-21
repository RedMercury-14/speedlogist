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
	<title>История решений по заказам</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/orderReportHistory.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tooltip.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<sec:authorize access="isAuthenticated()">
		<sec:authentication property="principal.authorities" var="roles" />
	</sec:authorize>

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0">
		<form class="" action="" id="reportDataForm">
			<div class="search-form-container">
				<h5 class="title mb-0 text-center">Загрузка задания (398 отчёт)</h5>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">С</label>
					<input class="form-control" type="date" name="dateFrom" id="dateFrom" required>
				</div>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">По</label>
					<input class="form-control" type="date" name="dateTo" id="dateTo" required>
				</div>
				<button class="btn btn-outline-secondary font-weight-bold" data-submitType="loadData" type="submit">Загр. задание</button>
			</div>
		</form>

		<div id="myGrid" class="ag-theme-balham"></div>

		<div id="snackbar"></div>
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
	<script src="${pageContext.request.contextPath}/resources/js/orderReportHistory.js" type="module"></script>
</body>
</html>