<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<title>Отдел закупок</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurement.css">
</head>
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container my-container">
		<h3 class="container">
			<a class="my-link" href="<spring:url value="/main/procurement/add-order" />">Создание заявки</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a>
		</h3>
	</div>

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>