<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<style>
		.navbar {
			background-color: #0e377b !important;
		}
		.my-container {
			margin-top: 83px;
		}
	</style>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Аналитика</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="fluid-container my-container">
		<div class=" px-5 d-flex align-items-center justify-content-between">
			<h3 class="mb-2">Аналитика Нулей</h3>
			<div>
				<p class="mb-1">Логин: powerbi@proanalysesl.com</p>
				<p class="mb-1">Пароль: Unks!89r</p>
			</div>
		</div>
		<br>
		<div class="frame-container">
			<iframe
				title="нули"
				width="100%" height="780px"
				src="https://app.powerbi.com/reportEmbed?reportId=5f56609f-d984-4cf5-a5ed-53d8eb4f2e03&autoAuth=true&ctid=a9af5edf-b4be-4591-ba34-a3a96434b108"
				frameborder="0"
				allowFullScreen="true">
			</iframe>

		</div>
	</div>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>