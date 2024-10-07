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
	<title>Создание списков автомобилей</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/logisticsDeliveryTrucks.css">
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

		<div class="title-container justify-content-center">
			<strong><h3>Создание списков автомобилей</h3></strong>
		</div>

		<div class="grid-container">

			<div>
				<div class="toolbar">
					<h5 class="m-0">Список свободных авто на</h5>

					<div class="btn-group">
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

				<div id="freeTrucksGrid" class="ag-theme-alpine"></div>

			</div>

			<div>
				<div class="toolbar">

					<select class="btn tools font-weight-bold" name="truckListsSelect" id="truckListsSelect">
						<option selected disabled value="">Выберите список автомобилей</option>
						<!-- здесь будет список названий списков машин -->
					</select>

					<button type="button" class="btn tools tools-btn font-weight-bold text-muted" id="addNewListBtn" data-toggle="modal" data-target="#addNewListModal">
						<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
							<path d="M120-320v-80h280v80H120Zm0-160v-80h440v80H120Zm0-160v-80h440v80H120Zm520 480v-160H480v-80h160v-160h80v160h160v80H720v160h-80Z"/>
						</svg>
					</button>

					<button type="button" class="btn tools tools-btn font-weight-bold text-muted" id="removeCurrentListBtn" data-toggle="modal" data-target="#removeCurrentListModal">
						<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
							<path d="m576-80-56-56 104-104-104-104 56-56 104 104 104-104 56 56-104 104 104 104-56 56-104-104L576-80ZM120-320v-80h280v80H120Zm0-160v-80h440v80H120Zm0-160v-80h440v80H120Z"/>
						</svg>
					</button>

				</div>

				<div id="selectedTrucksGrid" class="ag-theme-alpine"></div>

				<!-- Модальное окно для отображения текста -->
				<div class="modal fade" id="addNewListModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="addNewListModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header justify-content-center">
								<h5 class="modal-title" id="addNewListModalLabel">Создать новый список</h5>
							</div>
							<form action="" id="addNewListForm">
								<div class="modal-body">
									<div class="form-group">
										<label for="nameList" class="mb-2 text-muted font-weight-bold">Название списка</label>
										<input type="text" class="form-control" name="nameList" id="nameList" placeholder="Введите название списка" required>
									</div>
								</div>
								<div class="modal-footer">
									<button type="submit" class="btn btn-primary">Сохранить</button>
									<button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
								</div>
							</form>
						</div>
					</div>
				</div>

				<!-- Модальное окно для отображения текста -->
				<div class="modal fade" id="removeCurrentListModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="removeCurrentListModalLabel" aria-hidden="true">
					<div class="modal-dialog">
						<div class="modal-content">
							<div class="modal-header justify-content-center">
								<h5 class="modal-title" id="removeCurrentListModalLabel">Удалить текущий список?</h5>
							</div>
							<form action="" id="removeCurrentListForm">
								<div class="modal-body">
									<span>Все машины из данного списка переместятся в таблицу Список свободных авто, а список будет удален.</span>
								</div>
								<div class="modal-footer">
									<button type="submit" class="btn btn-danger">Да, удалить</button>
									<button type="button" class="btn btn-secondary" data-dismiss="modal">Отмена</button>
								</div>
							</form>
						</div>
					</div>
				</div>

			</div>
		</div>

		<div id="snackbar"></div>

	</div>

</body>
<script src="${pageContext.request.contextPath}/resources/js/logisticsDelivery/trucks/trucks.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</html>