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
	<title>История тендеров</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-locale-RU.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tenderHistory.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="my-container container">
		<div class="title-container mt-1">
			<h1 class="title mt-0">История тендеров</h1>
			<button id="resetTableFilters" class="btn btn-secondary">Сбросить фильтры</button>
		</div>

		<input type="text" id="filterTextBox" placeholder="Поиск тендера...">
		<div id="myGrid" class="ag-theme-alpine"></div>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/tenderHistory.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>