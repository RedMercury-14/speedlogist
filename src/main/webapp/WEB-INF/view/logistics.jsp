<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Логистика</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/logistics.css">
</head>
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container my-container">
		<h3 class="container">
			<!-- <a class="my-link" href="<spring:url value="/main/logistics/routemanager" />">Менеджер маршрутов (развоз по магазинам)</a>
			<br> -->
			<a class="my-link" href="<spring:url value="/main/logistics/international" />">Менеджер международных маршрутов</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/internationalNew" />">New Менеджер международных маршрутов</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/internationalCarrier" />">Список международных перевозчиков</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/ordersLogist" />">Менеджер заявок</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/documentflow" />">Документооборот</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/shopControl" />">Список магазинов</a>
		</h3>
	</div>

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>