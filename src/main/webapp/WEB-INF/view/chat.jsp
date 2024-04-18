<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
<style type="text/css">
.container-message {
	border: 2px solid #dedede;
	background-color: #f1f1f1;
	border-radius: 5px;
	padding: 10px;
	margin: 10px 0;
}

.darker {
	border-color: #ccc;
	background-color: #ddd;
}

.container-message::after {
	content: "";
	clear: both;
	display: table;
}

.container-message img {
	float: left;
	max-width: 60px;
	width: 100%;
	margin-right: 20px;
	border-radius: 50%;
}

.container-message img.right {
	float: right;
	margin-left: 20px;
	margin-right: 0;
}

.time-right {
	float: right;
	color: #aaa;
}

.time-left {
	float: left;
	color: #999;
}

.right {
	float: right;
}
/* аккордеон */
.accordion {
	background-color: #ff000063;
	color: #444;
	cursor: pointer;
	padding: 18px;
	width: 100%;
	border: none;
	text-align: left;
	outline: none;
	font-size: 15px;
	transition: 0.4s;
}

.accordion_old {
	background-color: #eee;
	color: #444;
	cursor: pointer;
	padding: 18px;
	width: 100%;
	border: none;
	text-align: left;
	outline: none;
	font-size: 15px;
	transition: 0.4s;
}

.active, .accordion:hover {
	background-color: #ccc;
}

.panel {
	padding: 0 18px;
	display: none;
	background-color: white;
	overflow: hidden;
	border: #0000004a;
}
</style>
<title>Чат</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
	type="text/javascript"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/vue@2/dist/vue.js"></script>
<script src="https://cdn.jsdelivr.net/npm/vue-resource@1.5.3"></script>
</head>
<body>
	<jsp:include page="header.jsp" />
	<sec:authorize access="isAuthenticated()">
		<strong><sec:authentication property="principal.authorities"
				var="roles" /></strong>
	</sec:authorize>
	<sec:authorize access="authenticated" var="authenticated" />
	<input type="hidden" value="<sec:authentication property="name" />" id="login">
	<input type="hidden" value="${roles}" id="role">
	<h2 class="container">История сообщений</h2>
	<div class="container">
	<div class="right">
		<input type="button" onclick="history.back();" value="Назад" />
	</div>
	</div>
	<br>
	
	<div id="list" class="container"></div>
	<script type="module"
		src="${pageContext.request.contextPath}/resources/js/chat.js"></script>
</body>
</html>