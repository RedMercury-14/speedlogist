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
	<title>Перевозчики</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<!-- AG-Grid -->
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-locale-RU.js"></script>
	
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/adminCarrier.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp"/>

	<div class="container-fluid px-1" style="margin-top: 85px;">
		<div class="title-container">
			<strong><p>Таблица перевозчиков</p></strong>
		</div>
		<div class="toolbar">
			<button id="allUsers" class="btn tools-btn">Все</button>
			<button id="confirmedUsers" class="btn tools-btn">Подтвержденные</button>
			<button id="unconfirmedUsers" class="btn tools-btn">Неподтвержденные</button>
			<button id="blockedUsers" class="btn tools-btn">Заблокированные</button>
			<button id="unblockedUsers" class="btn tools-btn">Разблокированные</button>
		</div>
		<div id="myGridToViewOrder" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>

		<div class="modal fade" id="fileModal" tabindex="-1" aria-labelledby="fileModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-xl">
				<div class="modal-content">
					<div class="modal-header">
						<h5 class="modal-title" id="fileModalLabel">Согласие на обработку данных</h5>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body"></div>
					<div class="modal-footer"></div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" />
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/adminCarrier.js"></script>
</body>
</html>