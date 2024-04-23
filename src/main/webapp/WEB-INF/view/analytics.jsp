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
			margin-top: 64px;
		}
		.list-container {
			min-width: 400px;
			max-width: 600px;
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
	<div class="container my-container">
		<br>
		<div class="px-5 d-flex align-items-center justify-content-between">
			<h3 class="mb-2">Аналитика</h3>
			<div>
				<p class="mb-1">Логин: SologubA@dobronom.by</p>
				<p class="mb-1">Пароль: 14A26B19c4</p>
			</div>
		</div>
		<br>
		<br>
		<div class="list-container">
			<ul class="list-group">
				<li class="list-group-item">
					<div class="d-flex align-items-center justify-content-between">
						<span>Нехватка товара на РЦ 2</span>
						<a
							target="_blank"
							href="https://app.fabric.microsoft.com/reportEmbed?reportId=5f4a775a-276a-4ec5-80ad-371e1ccef0c1&autoAuth=true&ctid=898331df-b42e-494c-b68e-d7245f2daef3">
							<button class="btn btn-outline-primary">Посмотреть</button>
						</a>
					</div>
				</li>
			</ul>
		</div>
		<!-- <div class="frame-container">
			<iframe
				title="Нехватка товара на РЦ 2"
				width="100%" height="800px"
				src="https://app.fabric.microsoft.com/reportEmbed?reportId=5f4a775a-276a-4ec5-80ad-371e1ccef0c1&autoAuth=true&ctid=898331df-b42e-494c-b68e-d7245f2daef3"
				frameborder="0"
				allowFullScreen="true">
			</iframe>
		</div> -->
	</div>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>