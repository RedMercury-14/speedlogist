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
	<title>Предварительная заявка авто</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/deliveryShop.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0">
		<sec:authorize access="isAuthenticated()">
			<sec:authentication property="principal.authorities" var="roles" />
		</sec:authorize>

		<input type="hidden" name="isTgLink" id="isTgLink" value="${isTgLink}">

		<div class="title-container my-0 mx-auto">
			<strong><h3>Предварительная заявка авто на</h3></strong>

			<div class="btn-group ">
				<button type="button" class="btn tools tools-btn px-0 font-weight-bold text-muted" id="datePrev">
					<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
						<path d="M560-240 320-480l240-240 56 56-184 184 184 184-56 56Z"/>
					</svg>
				</button>

				<input type="date" class="btn tools font-weight-bold" name="currentDate" id="currentDate">

				<button type="button" class="btn tools tools-btn px-0 font-weight-bold text-muted" id="dateNext">
					<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
						<path d="M504-480 320-664l56-56 240 240-240 240-56-56 184-184Z"/>
					</svg>
				</button>
			</div>
		</div>

		<div class="toolbar">
			<button type="button" class="ml-2 btn tools tools-btn font-weight-bold text-muted" id="addNewTruckBtn" data-toggle="modal" data-target="#addNewTruckModal">
				+
				<svg xmlns="http://www.w3.org/2000/svg" width="24" height="24" fill="currentColor" class="bi bi-truck" viewBox="0 0 16 16">
					<path d="M0 3.5A1.5 1.5 0 0 1 1.5 2h9A1.5 1.5 0 0 1 12 3.5V5h1.02a1.5 1.5 0 0 1 1.17.563l1.481 1.85a1.5 1.5 0 0 1 .329.938V10.5a1.5 1.5 0 0 1-1.5 1.5H14a2 2 0 1 1-4 0H5a2 2 0 1 1-3.998-.085A1.5 1.5 0 0 1 0 10.5v-7zm1.294 7.456A1.999 1.999 0 0 1 4.732 11h5.536a2.01 2.01 0 0 1 .732-.732V3.5a.5.5 0 0 0-.5-.5h-9a.5.5 0 0 0-.5.5v7a.5.5 0 0 0 .294.456zM12 10a2 2 0 0 1 1.732 1h.768a.5.5 0 0 0 .5-.5V8.35a.5.5 0 0 0-.11-.312l-1.48-1.85A.5.5 0 0 0 13.02 6H12v4zm-9 1a1 1 0 1 0 0 2 1 1 0 0 0 0-2zm9 0a1 1 0 1 0 0 2 1 1 0 0 0 0-2z"/>
				</svg>
			</button>
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<!-- Модальное окно добавления авто -->
		<div class="modal fade" id="addNewTruckModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="addNewTruckModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header justify-content-center">
						<h5 class="modal-title" id="addNewTruckModalLabel">Предварительная заявка авто</h5>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<form action="" id="addNewTruckForm">
						<div class="modal-body">

							<div class="mb-2">
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text text-muted font-weight-bold">Дата</div>
									</div>
									<input type="date" class="form-control dateRequisition" id="dateRequisition" name="dateRequisition" required>
								</div>
							</div>

							<div class="mb-2">
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text text-muted font-weight-bold">Количество авто</div>
									</div>
									<input type="number" class="form-control" id="count" name="count" min="1" placeholder="Укажите количество авто" required>
								</div>
							</div>

							<div class="mb-2">
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text text-muted font-weight-bold">Тип кузова</div>
									</div>
									<select class="form-control" id="typeTrailer" name="typeTrailer" required>
										<option value="" disabled selected>Выберите тип кузова</option>
										<option>Тент</option>
										<option>Изотермический</option>
										<option>Мебельный фургон</option>
										<option>Рефрижератор</option>
										<option>Бус</option>
									</select>
								</div>
							</div>

							<div class="mb-2">
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text text-muted font-weight-bold">Тоннаж (т)</div>
									</div>
									<input type="number" step="0.1" min="2.0" max="22.0" class="form-control" id="cargoCapacity" name="cargoCapacity" placeholder="Введите тоннаж" required>
								</div>
							</div>

							<div class="mb-2">
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text text-muted font-weight-bold">Паллетовместимость</div>
									</div>
									<input type="number" step="1" min="4" max="38" class="form-control" id="pall" name="pall" placeholder="Введите количество паллет" required>
								</div>
							</div>

							<div class="mb-2">
								<div class="input-group">
									<div class="input-group-prepend">
										<div class="input-group-text text-muted font-weight-bold">Склад загрузки</div>
									</div>
									<select class="form-control" id="typeStock" name="typeStock" required>
										<option value="" disabled selected>Выберите склад загрузки</option>
										<option value="Таборы (+2-+4)">Таборы (+2 - +4)</option>
										<option value="Прилесье (без температурного режима)">Прилесье (без температурного режима)</option>
										<option value="Виталюр 9-й км МКАД (-18)">Виталюр 9-й км МКАД (-18)</option>
									</select>
								</div>
							</div>

							<div class="form-check mt-3">
								<input class="form-check-input" type="checkbox" name="isSecondRound" id="isSecondRound">
								<label class="form-check-label" for="isSecondRound">
									Машины на 2 рейса
								</label>
							</div>

							<br>

							<div class="form-group">
								<label class="mb-2 text-muted font-weight-bold" for="otherInfo">Дополнительная информация</label>
								<textarea class="form-control" id="otherInfo" name="otherInfo" rows="3" placeholder="Введите дополнительную информацию"></textarea>
							</div>
						</div>
						<div class="modal-footer">
							<button type="submit" class="btn btn-primary">Заявить авто</button>
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
						</div>
					</form>
				</div>
			</div>
		</div>


		<!-- Модальное окно указания даты при копировании авто -->
		<div class="modal fade" id="copyTruckToDateModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="copyTruckToDateModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header justify-content-center">
						<h5 class="modal-title" id="copyTruckToDateModalLabel">Копирование авто</h5>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<form action="" id="copyTruckToDateForm">
						<div class="modal-body">
							<div class="form-group">
								<label for="dateRequisition" class="mb-2 text-muted font-weight-bold">Укажите дату заявления авто</label>
								<input type="date" class="form-control dateRequisition" name="dateRequisition" id="dateRequisition" required>
							</div>
							<input type="hidden" name="numTruck">
							<input type="hidden" name="typeTrailer">
							<input type="hidden" name="cargoCapacity">
							<input type="hidden" name="pall">
							<input type="hidden" name="fio">
							<input type="hidden" name="otherInfo">
						</div>
						<div class="modal-footer">
							<button type="submit" class="btn btn-primary">Копировать авто</button>
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
						</div>
					</form>
				</div>
			</div>
		</div>

		<div id="snackbar"></div>

		<jsp:include page="footer.jsp" />

	</div>

</body>
<script src="${pageContext.request.contextPath}/resources/js/deliveryShop.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</html>