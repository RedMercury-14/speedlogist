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
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Аналитика</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/analytics.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="fluid-container my-container">
		<div class=" px-5 d-flex align-items-center justify-content-between">
			<h3 class="mb-2" id="pageTitle"></h3>
			<div class="mt-2">
				<div class="d-flex align-items-center mb-1">
					<p id="" class="mb-0 mr-2">
						Логин:
						<span id="pbLogin">
							powerbi@proanalysesl.com
						</span>
					</p>
					<button id="copyPBLogin" type="button" class="btn px-1 py-0" title="Копировать">
						<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#999999">
							<path d="M360-240q-33 0-56.5-23.5T280-320v-480q0-33 23.5-56.5T360-880h360q33 0 56.5 23.5T800-800v480q0 33-23.5 56.5T720-240H360Zm0-80h360v-480H360v480ZM200-80q-33 0-56.5-23.5T120-160v-560h80v560h440v80H200Zm160-240v-480 480Z"/>
						</svg>
					</button>
				</div>
				<div class="d-flex align-items-center">
					<p class="mb-0 mr-2">
						Пароль:
						<span id="pbPass">
							Unks!89r
						</span>
					</p>
					<button id="copyPBPass" type="button" class="btn px-1 py-0" title="Копировать">
						<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#999999">
							<path d="M360-240q-33 0-56.5-23.5T280-320v-480q0-33 23.5-56.5T360-880h360q33 0 56.5 23.5T800-800v480q0 33-23.5 56.5T720-240H360Zm0-80h360v-480H360v480ZM200-80q-33 0-56.5-23.5T120-160v-560h80v560h440v80H200Zm160-240v-480 480Z"/>
						</svg>
					</button>
				</div>
			</div>
		</div>

		<div class="frame-container" id="frameContainer">
			<!-- здесь будет фрейм PBI -->
			 <br>
			 <br>
			 <br>
			 <br>
			 <br>
			 <br>
			 <br>
			<h3 class="mt-5 text-muted text-center">Нет даных, проверьте запрос</h3>
		</div>

		<div id="snackbar"></div>
	</div>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/analyticsMain.js' type="module"></script>
</body>
</html>