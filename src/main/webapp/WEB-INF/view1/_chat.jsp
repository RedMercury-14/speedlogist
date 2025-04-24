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
	<title>Приход</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/_chat.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div class="container my-container">
		<br>
		<div class="">
			<h2 class="m-0 text-center">Чат с машиной</h2>
		</div>
		<div class="chat-container d-flex flex-column" id="chat-box">
			<!-- Сообщения будут добавляться сюда -->
		</div>
		<div class="w-50 mx-auto d-flex flex-column align-items-end mt-3">
			<textarea id="message-input" rows="3" class="form-control" placeholder="Введите сообщение..."></textarea>
			<button class="btn btn-primary mt-3" id="send-btn">Отправить</button>
		</div>

		<div id="snackbar"></div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/_chat.js" type="module"></script>
</body>
</html>