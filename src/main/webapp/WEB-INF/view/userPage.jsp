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
	<title>Личный кабинет</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css" />" />
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container my-container">
		<c:choose>
			<c:when test="${department}">
				<h3><a href="<spring:url value="/main/userpage/userlist" />">Мой персонал</a></h3>
			</c:when>
		</c:choose>
		<h3><a href="<spring:url value="/main/userpage/edit" />">Настройки</a></h3>
	</div>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>