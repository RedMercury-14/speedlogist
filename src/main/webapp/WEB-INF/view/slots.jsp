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
	<title>Слоты на выгрузку</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/daterangepicker/moment.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/FullCalendar/index.global.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/slots.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>

<body class="no-scroll">
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="fluid-container calendar-wrapper my-container">

		<div class="left-sidebar">
			<div class="">
				<p class="left-sidebar-title">1: выберите склад</p>
				<select class="form-control" name="stockNumber" id="stockNumber">
					<option selected disabled value="">Выберите склад</option>
				</select>
			</div>

			<div>
				<p class="left-sidebar-title">2: добавьте заказ по номеру из Маркета</p>
				<button id="addNewOrder" class="btn btn-primary w-100" disabled>+ Добавить заказ</button>
			</div>

			<div class="d-flex flex-column">
				<p class="left-sidebar-title">3: перетащите заказ в свободный слот</p>
				<p class="mobile-tooltip align-self-center d-none">для перетаскивания удерживайте палец на заказе не менее 1с</p>
				<div class="events-container">
					<p>Зона нераспределенных заказов</p>
					<div id='external-events' class="align-self-center"></div>
				</div>
			</div>
		</div>

		<div class="pallInfo-container">
			<div class="pallInfo">
				<span id="pallCount" class="text-success">0</span>
				<span>/</span>
				<span id="maxPall">0</span>
				<span>палл.</span>
			</div>
		</div>

		<div style="width: calc(100% - 100px);" id='calendar'></div>

		<div class="sidebar">
			<button aria-label="close sidebar" type="button" class="close-button">
				<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16">
					<path fill-rule="evenodd" d="M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z"/>
					<path fill-rule="evenodd" d="M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"/>
				</svg>
			</button>

			<ul class="sidebar-menu p-0">
				<li class="menu-item" data-item="orderList">
					<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-table" viewBox="0 0 16 16">
						<path d="M0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm15 2h-4v3h4V4zm0 4h-4v3h4V8zm0 4h-4v3h3a1 1 0 0 0 1-1v-2zm-5 3v-3H6v3h4zm-5 0v-3H1v2a1 1 0 0 0 1 1h3zm-4-4h4V8H1v3zm0-4h4V4H1v3zm5-3v3h4V4H6zm4 4H6v3h4V8z"/>
					</svg>
				</li>
			</ul>

			<div class="sidebar-content position-relative">
				<div class="status-info-container">
					<span id="statusInfoLabel" class="status-info-label">?</span>
					<div id="statusInfo" class="status-info">
						<ul><span class="led"></span><span>5 - виртуальный заказ</span></ul>
						<ul><span class="led led-orange"></span><span>6 - Заказ на самовывоз</span></ul>
						<ul><span class="led led-turquoise"></span><span>7 - Слот на самовывоз, не подтвержден</span></ul>
						<ul><span class="led led-light-purple"></span><span>8 - Слот от поставщика, не подтвержден</span></ul>
						<ul><span class="led led-grey"></span><span>10 - Заказ на самовывоз отменен</span></ul>
						<ul><span class="led led-dark-turquoise"></span><span>20 - Подтвержденный слот на самовывоз</span></ul>
						<ul><span class="led led-yellow"></span><span>30 - Маршрут на самовывоз (нет на бирже)</span></ul>
						<ul><span class="led led-red"></span><span>40 - Маршрут на самовывоз отменен</span></ul>
						<ul><span class="led led-light-green"></span><span>50 - Маршрут на самовывоз (на бирже)</span></ul>
						<ul><span class="led led-dark-green"></span><span>60 - Машина на самовывоз найдена</span></ul>
						<ul><span class="led led-blue"></span><span>70 - Маршрут на самовывоз завершен</span></ul>
						<ul><span class="led led-purple"></span><span>100 - Слот от поставщика, подтвержден</span></ul>
					</div>
				</div>
				<div class="item-content" id="orderList">
					<h2 class="ml-2">Список заказов</h2>
					<div class="ag-grid-container">
						<div id="myGrid" style="height: 100%; width: 100%;" class="ag-theme-alpine"></div>
					</div>
				</div>
			</div>
		</div>

		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно обновления страницы -->
	<div class="modal fade" id="reloadWindowModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="reloadWindowModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-dialog-centered">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h2 class="modal-title text-center" id="reloadWindowModalLabel">Связь с сервером потеряна. Пожалуйста, обновите страницу!</h2>
				</div>
				<div class="modal-body">
					<div class="modal-buttons d-flex justify-content-center">
						<button id="reloadWindowButton" class="btn btn-primary btn-lg" type="button">Обновить страницу</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно информации об ивенте -->
	<div class="modal fade" id="eventInfoModal" tabindex="-1" aria-labelledby="eventInfoModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h5 class="modal-title" id="eventInfoModalLabel">Информация о заказе</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div id="eventInfo"></div>
				</div>
				<div class="modal-footer">
					<button id="confirmSlot" data-action="save" type="button" class="btn btn-secondary"></button>
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/slots.js" type="module"></script>
</body>
</html>