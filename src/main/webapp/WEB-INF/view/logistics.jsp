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
		<!-- <form:form enctype="multipart/form-data" method="post" action="./logistics/addshop?${_csrf.parameterName}=${_csrf.token}">
			<sec:csrfInput />
			<label for="make">Добавить магазины</label>
			<p><input type="file" name="file">
				<br>
				<input type="submit" value="Отправить">
			</p>
		</form:form>
		<br><br>
		<form:form enctype="multipart/form-data" method="post" action="./logistics/upload?${_csrf.parameterName}=${_csrf.token}">
			<sec:csrfInput />
			<label for="make">Создать маршруты</label>
			<p><input type="file" name="file">
				<br>
			<div class="form-group">
				<label>Выберите день:</label>
				<input type="date" name="dateStart" />
			</div>
			<br>
			<input type="submit" value="Отправить"></p>
		</form:form> -->

		<h3 class="container">
			<!-- <a class="my-link" href="<spring:url value="/main/logistics/routemanager" />">Менеджер маршрутов (развоз по магазинам)</a>
			<br> -->
			<a class="my-link" href="<spring:url value="/main/logistics/internationalCarrier" />">Список международных перевозчиков</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/international" />">Менеджер международных маршрутов</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/ordersLogist" />">Менеджер заявок</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/documentflow" />">Документооборот</a>
			<br>
			<a class="my-link" href="<spring:url value="/main/logistics/shopControl" />">Список магазинов</a>
		</h3>
	
		<%-- <form enctype="multipart/form-data" method="post" action="./logistics/sendEmail?${_csrf.parameterName}=${_csrf.token}"> --%>
		  <%-- <sec:csrfInput /> --%>
		  <!--   <label for="make">Добавить файл</label> -->
		  <!--    <p><input type="file" name="file" required> -->
		  <!--    <br> -->
		  <!--    <input name="subject" placeholder="Тема письма"><br> -->
		  <!--    <input name="text" placeholder="Текст письма"><br> -->
		  <!--    <input type="submit" value="Отправить"></p> -->
		  <%-- </form> --%>
	</div>

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>