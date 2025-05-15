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
	<title>Управление персоналом</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/driverList.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>
	<div class="fluid-container grid-container">
		<div class="title-container">
			<strong><h3>Список водителей</h3></strong>
		</div>
		<div class="toolbar">
			<button type="button" class="btn tools-btn" id="addDriverBtn">
				+ Добавить водителя
			</button>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<!-- Модальное окно для добавления водителя -->
	<div class="modal fade" id="driverModal" tabindex="-1" aria-labelledby="driverModalLabel" aria-hidden="true">
		<div class="modal-dialog ">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="driverModalLabel">Добавить водителя</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="createDriverForm" action="" class="needs-validation" novalidate>
					<div class="modal-body">
						<input type="number" class="form-control" name="idUser" id="idUser" hidden>
						<div class="form-group">
							<input type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия" required>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="name" id="name" placeholder="Имя" required>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="patronymic" id="patronymic" placeholder="Отчество">
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="tel" id="tel" placeholder="Телефон" required>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold" for="numpass">Паспортные данные</label>
							<div class="form-group row-container numpass-container">
								<input type="text" class="form-control" name="numpass_1" id="numpass_1" placeholder="Серия" required>
								<input type="number" class="form-control" name="numpass_2" id="numpass_2" placeholder="Номер" required>
							</div>
							<div class="form-group row-container numpass-container">
								<input type="text" class="form-control" name="numpass_3" id="numpass_3" placeholder="Кем выдан" required>
								<span class="date-label">от</span>
								<input type="date" class="form-control" name="numpass_4" id="numpass_4" required>
							</div>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold" for="numdrivercard">Водительское удостоверение</label>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numdrivercard_1" id="numdrivercard_1" placeholder="Серия" required>
								<input type="number" class="form-control" name="numdrivercard_2" id="numdrivercard_2" placeholder="Номер" required>
							</div>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numdrivercard_3" id="numdrivercard_3" placeholder="Кем выдан" required>
								<span class="date-label">от</span>
								<input type="date" class="form-control" name="numdrivercard_4" id="numdrivercard_4" required>
							</div>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold">Фото водительского удостоверения</label>
							<input type="file" class="form-control btn btn-outline-secondary" name="drivercard_file" id="drivercard_file" accept=".png, .jpg, .jpeg" required>
						</div>
						<div id="driverImageContainer"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Сохранить данные</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script type="module" src='${pageContext.request.contextPath}/resources/js/driverList.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/tenderNotifications.js" type="module"></script>
</body>
</html>