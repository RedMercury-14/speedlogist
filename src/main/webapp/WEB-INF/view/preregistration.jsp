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
	<title>Выберите подходящий вариант</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/preregistration.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
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
	<div class="container center">
		<form:form method="post" action="./registration">
			<div class="my-container">
				<div class="slide" id="but1">
					<div class="cargoy-sb">
						<figure class="sb-thumb green">
							<img src="${pageContext.request.contextPath}/resources/img/belmap.jpg" alt="">
							<input class="reg-button btn btn-warning hidden" type="submit" value="Регистрация" name="but1">
							<div class="sb-caption">
								<h4>Я региональный перевозчик, развожу по РБ</h4>
							</div>
						</figure>
					</div>
				</div>
				<div class="slide" id="but2">
					<div class="cargoy-sb">
						<figure class="sb-thumb green">
							<img src="${pageContext.request.contextPath}/resources/img/worldmap.jpg" alt="">
							<input class="reg-button btn btn-warning hidden" type="submit" value="Регистрация" name="but3">
							<div class="sb-caption">
								<h4>Я международный перевозчик или экспедиция</h4>
							</div>
						</figure>
					</div>
				</div>
			</div>
			<div class="reg-buttons"></div>
		</form:form>
		<div class="message">
			<c:out value="${errorMessage}" />
		</div>
	</div>
	<jsp:include page="footer.jsp" />
	<script src='${pageContext.request.contextPath}/resources/js/preloader.js'></script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/preregistration.js" type="module"></script>
</body>
</html>