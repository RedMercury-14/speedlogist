<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
	<head>
		<meta charset="UTF-8">
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap-datepicker/datepicker.min.css"></link>
		<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/js/daterangepicker/daterangepicker.css" >
		<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/_orderToCar.css"></link>
		<title>Формирование машин</title>
	</head>

	<body>
		<jsp:include page="headerNEW.jsp" />

		<div class="container-fluid px-5 pt-5">
			<h1 class="pt-2 m-0 text-center">Формирование машин</h1>

			<div class="date-container py-3">
				<!-- <label for="ordersLoadInput">Заказы за </label> -->
				<input type="text" class="form-control my-shadow my-date-input" id="ordersLoadInput" name="ordersLoadInput">
			</div>

			<div class="body-container">

				<div class="left-container">
					<div id="myGridToOrders" class="ag-theme-balham"></div>
				</div>

				<div class="right-container">
					<div class="my-card my-shadow">
						<h5 class="carTitle">Сформированные машины</h5>
						<div id="myGridToViewCar" class="ag-theme-balham cars-table"></div>
					</div>
					<br>
					<div class="drop-col">
						<div id="eDropTarget" class="drop-target">Перетащите заказы сюда</div>
						<button id="createCarButton" class="my-btn btn btn-primary d-none">Сформировать машину</button>
						<div class="tile-container"></div>
					</div>
				</div>
			</div>
		</div>

		<!-- Модальное окно для указания даты и времени поставки -->
		<div class="modal fade" id="dateModal" tabindex="-1" aria-labelledby="dateModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header">
						<h1 class="modal-title fs-5" id="dateModalLabel">Выбрать дату поставки</h1>
						<button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Закрыть"></button>
					</div>
					<form id="createCarForm" action="">
						<div class="modal-body"></div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Отменить</button>
							<button type="submit" class="my-btn btn btn-primary">Создать машину</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</body>

	<script src="${pageContext.request.contextPath}/resources/js/daterangepicker/moment.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/daterangepicker/daterangepicker.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datepicker/datepicker.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/bootstrap-datepicker/datepicker-ru.min.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/_orderToCar.js"></script>


</html>