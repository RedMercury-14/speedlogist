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
	<title>Актуальные международные перевозки</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tenderPreview.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel='stylesheet' href="${pageContext.request.contextPath}/resources/css/font-awesome/css/all.min.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container my-container">
		<br>
		<h3 class="mt-2 mb-2">Актуальные международные мершруты</h3>
		<sec:authorize access="authenticated" var="authenticated" />
		<c:choose>
			<c:when test="${!authenticated}">
				<p class="mb-2">
					Для того, чтобы стать участником биржи, 
					<a class="text-primary" href="/speedlogist/main/registration">зарегистрируйтесь</a> 
					на платформе или отправьте 
					<a class="text-primary" href="/speedlogist/main/carrier-application-form">заявку на сотрудничество</a>.
				</p>
			</c:when>
		</c:choose>

		<sec:authorize access="isAuthenticated()">
			<sec:authentication property="principal.authorities" var="roles" />
		</sec:authorize>
		<c:choose>
			<c:when test="${roles == '[ROLE_CARRIER]'}">
				<p class="mb-2">
					<a class="text-primary" href="/speedlogist/main/carrier/tender">Перейти на биржу</a> 
				</p>
			</c:when>
		</c:choose>
		<br>

		<div class="row" id="cardsContainer"></div>

		<div class="d-flex justify-content-center">
			<button id="showMore" class="btn btn-outline-primary" type="button">Показать ещё...</button>
		</div>

		<div id="snackbar"></div>

	</div>

	<jsp:include page="footer.jsp" />

	<script src="${pageContext.request.contextPath}/resources/js/tenderPreview.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>