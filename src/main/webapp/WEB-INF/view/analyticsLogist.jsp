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
			<h3 class="mb-2">Аналитика Биржи</h3>
			<div>
				<p class="mb-1">Логин: SologubA@dobronom.by</p>
				<p class="mb-1">Пароль: 14A26B19c4</p>
			</div>
		</div>
		<br>
		<div class="frame-container">
			<iframe
				title="Нехватка товара на РЦ 2"
				width="100%" height="780px"
				src="https://app.fabric.microsoft.com/reportEmbed?reportId=e7b378cd-2feb-460a-99f5-a6b2b7c6ea1b&autoAuth=true&ctid=898331df-b42e-494c-b68e-d7245f2daef3"
				frameborder="0"
				allowFullScreen="true">
			</iframe>
		</div>
	</div>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>