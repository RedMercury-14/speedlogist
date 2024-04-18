<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
	<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
<title>Архив перевозок</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/archive.css">
</head>
<body>
<jsp:include page="headerNEW.jsp"/>

<div class="container my-container">
<label>Выберите дни:</label>	
C <input type="date" name="dateStart" id="dateStart" value="<c:out value="${dateNow}"/>" />
				по <input type="date" id="dateFinish" name="dateFinish" value="<c:out value="${dateTomorrow}" />" />
			<br>
</div>
<br>
<div class="container" id="div">
	<table id="table"></table>
</div>

<!-- контейнер для отображения полученных сообщений -->
<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
<script	src="${pageContext.request.contextPath}/resources/js/archive.js" type="module"></script>
<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>