<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<style type="text/css">
		.navbar {
			background-color: #0e377b !important;
		}
	</style>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Автопарк и управление</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/controlPark.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container my-container">
		<h1>Автопарк и управление</h1><br>
		<h3 class="container">
			<a class="my-link" href="<spring:url value="/main/carrier/controlpark/trucklist" />">Управление автопарком</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/carrier/controlpark/driverlist" />">Управление персоналом</a>
		</h3>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>