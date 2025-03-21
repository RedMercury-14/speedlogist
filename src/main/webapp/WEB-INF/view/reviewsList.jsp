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
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Список отзывов</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reviewsList.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tooltip.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0 position-relative">
		<div class="search-form-container">
			<form class="" action="" id="orderSearchForm">
				<span class="font-weight-bold text-muted mb-0">Отобразить отзывы</span>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">с</label>
					<input class="form-control" type="date" name="date_from" id="date_from" required>
				</div>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">по</label>
					<input class="form-control" type="date" name="date_to" id="date_to" required>
				</div>
				<button class="btn btn-outline-secondary font-weight-bold" type="submit">Отобразить</button>
			</form>
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<div id="snackbar"></div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/reviewsList.js' type="module"></script>
</body>
</html>