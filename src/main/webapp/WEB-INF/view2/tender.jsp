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
	<title>Текущие тендеры</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-locale-RU.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tender.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="my-container container-fluid">
		<div class="title-container mt-1">
			<h1 class="title mt-0">Текущие тендеры</h1>
			<button id="resetTableFilters" class="btn btn-secondary">Сбросить фильтры</button>
		</div>
		<p class="tender-message mb-2">
			Telegram бот с уведомлениями о новых тендерах: 
			<a href="http://t.me/speedlogist_bot" target="_blank">@speedlogist_bot</a>
		</p>
		<div class="d-flex py-1">
			<input type="text" id="filterTextBox" placeholder="Поиск тендера...">
			<a href="/speedlogist/main/carrier/tender/history">
				<button class="ml-1 text-nowrap">Проверить тендеры</button>
			</a>
		</div>
	</div>
	<div class="container-fluid px-0">
		<div id="myGrid" class="ag-theme-alpine"></div>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<!-- Модальное окно -->
	<div class="modal fade" id="TGBotModal" tabindex="-1" aria-labelledby="TGBotModalLabel" aria-hidden="true">
		<div class="modal-dialog">
		<div class="modal-content">
			<div class="modal-header">
				<h5 class="modal-title" id="TGBotModalLabel">Теперь и в Telegram!</h5>
				<button type="button" class="close" data-dismiss="modal" aria-label="Close">
					<span aria-hidden="true">&times;</span>
				</button>
			</div>
			<div class="modal-body">
				<p class="text-center mb-0">Получайте мгновенные уведомления о тендерах в нашем Telegram боте! Достаточно перейти на страницу бота и нажать кнопку СТАРТ</p>
			</div>
			<div class="modal-footer">
				<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				<button id="goTGBotBtn" type="button" class="btn btn-primary">Перейти</button>
			</div>
		</div>
		</div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/tender.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>