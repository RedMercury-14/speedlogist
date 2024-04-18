<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>

<head>
	<meta charset="UTF-8">
	<title>Вход</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/signin.css">
</head>
<body>
	<!-- PRELOADER START -->
	<div id="loader-wrapper">
		<div class="loader">
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		<div class="ball"></div>
		</div> 
	</div>
	<!-- PRELOADER END -->

	<jsp:include page="headerNEW.jsp" />

	<div class="container center">
		<div class="my-card">
			<h1>Вход</h1>
			<c:url value="/main/signinpost" var="loginVar" />
			<form:form action="${loginVar}" modelAttribute="user" method="POST">
				<div class="form-group">
					<label for="make">Логин</label>
					<input name="login" class="form-control my-input" />
				</div>
				<div class="form-group">
					<label for="model">Пароль</label>
					<input type="password" name="password" class="form-control my-input" />
				</div>
				<sec:csrfInput />
				<c:if test="${param.error != null}">
					<p class="error-message">Неверный логин или пароль</p>
				</c:if>
				<div class="submit-container">
					<button type="Войти" id="btn-save" class="my-btn signin-btn">Войти</button>
				</div>
			</form:form>
		</div>
	</div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/js/preloader.js'></script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>