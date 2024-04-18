<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>

<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Управление</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/style.css"/>" />
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
	type="text/javascript"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp" />
	<div class="container">
	<c:out value="${errorMessage}" /><br>
	Время на сервере:  <c:out value="${time}" /><br>
	Размер кеша сообщений:  <c:out value="${sizeMainChat}" /><br>
	Размер кеша системных сообщений:  <c:out value="${sizeChatEnpoint}" /><br>
	C3P0:  <textarea rows="7" cols="30" readonly="true">${poolConnectInfo}</textarea><br>
	<h3><a href="<spring:url value="/main/admin/userlist" />">Управление персоналом и доступом</a></h3>
	<h3><a href="<spring:url value="/main/admin/shoplist" />">Управление магазинами</a></h3>
	<h3><a href="<spring:url value="/main/admin/tender" />">Текущие тендеры</a></h3>
	<h3><a href="<spring:url value="/main/admin/carrier" />">Управление перевозчиками (региональными)</a></h3>
	<h3><a href="<spring:url value="/main/admin/cost" />">Управление системой расчёта стоимости</a></h3>
	<h3><a href="<spring:url value="/main/admin/routePattern" />">Шаблоны маршрутов</a></h3>
	<h3><a href="<spring:url value="/main/admin/dashboard" />">Панель индикаторов</a></h3>
	<h3><a href="<spring:url value="/main/test" />" style="color: red">Маршрутизатор</a></h3>
</div>

	<div class="container">
	управление памятью
	<form action="./admin/memory" method="get">
		<input type="submit" value="Очистить память кеша сообщений (до 50)" name="1">
		<input type="submit" value="Очистить кеша системных сообщений (до 50)" name="2">
		<input type="submit" value="Сброс памяти кеша сообщений " name="3">
		<input type="submit" value="Сброс памяти кеша системных сообщений " name="4">
	</form>
</div>
</body>
</html>