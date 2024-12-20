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
			<a class="my-link" href="<spring:url value="/main/analytics/shortage" />">Нехватка товаров на РЦ</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/analytics/logistics" />">Аналитика Биржи</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/analytics/zero" />">Аналитика нулей</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/analytics/changing-matrix" />">Изменение матрицы</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/analytics/needs" />">Аналитика Слотов</a>
		</h3>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>