<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html lang="ru">
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Ротации</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/rotations.css">
</head>
<body>

	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<sec:authorize access="isAuthenticated()">
		<sec:authentication property="principal.authorities" var="roles" />
		<sec:authentication property="name" var="login"/>
	</sec:authorize>

	<div class="container-fluid my-container px-0 position-relative">
		<div class="title-container">
			<strong><h3>Ротации</h3></strong>
		</div>
		<div class="toolbar">
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#rotationModal">
				<!-- <svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-plus-lg" viewBox="0 0 16 16">
					<path fill-rule="evenodd" d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2Z"/>
				</svg> -->
				+ Ротация
			</button>
			<c:choose>
				<c:when test="${roles == '[ROLE_ADMIN]'}">
					<button type="button" class="btn tools-btn font-weight-bold text-muted" title="Загрузить Excel" data-toggle="modal" data-target="#sendExcelModal">
						<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-upload" viewBox="0 0 16 16">
							<path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
							<path d="M7.646 1.146a.5.5 0 0 1 .708 0l3 3a.5.5 0 0 1-.708.708L8.5 2.707V11.5a.5.5 0 0 1-1 0V2.707L5.354 4.854a.5.5 0 1 1-.708-.708l3-3z"/>
						</svg>
						Excel
					</button>
				</c:when>
			</c:choose>
			<button type="button" id="downloadExcel" class="btn tools-btn font-weight-bold text-muted" title="Скачать Excel" >
				<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-download" viewBox="0 0 16 16">
					<path d="M.5 9.9a.5.5 0 0 1 .5.5v2.5a1 1 0 0 0 1 1h12a1 1 0 0 0 1-1v-2.5a.5.5 0 0 1 1 0v2.5a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2v-2.5a.5.5 0 0 1 .5-.5z"/>
					<path d="M7.646 11.854a.5.5 0 0 0 .708 0l3-3a.5.5 0 0 0-.708-.708L8.5 10.293V1.5a.5.5 0 0 0-1 0v8.793L5.354 8.146a.5.5 0 1 0-.708.708l3 3z"/>
				</svg>
				Excel
			</button>
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<div id="snackbar"></div>
	</div>

	<!-- модальное окно загрузки таблицы Эксель -->
	<div class="modal fade" id="sendExcelModal" tabindex="-1" aria-labelledby="sendExcelModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="sendExcelModalLabel">Загрузить Excel</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="sendExcelForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Загрузите файл Excel</label>
								<input type="file" class="form-control btn-outline-secondary p-1" name="excel"
									id="excel" required
									accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel">
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Загрузить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для отображения текста -->
	<div class="modal fade" id="displayMessageModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="displayMessageModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="displayMessageModalLabel">Сообщение</h5>
				</div>
				<div class="modal-body">
					<div id="messageContainer"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно с формой ротации -->
	<div class="modal fade" id="rotationModal" tabindex="-1" role="dialog" aria-labelledby="rotationModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="rotationModalLabel">Добавить ротацию</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form class="" id="rotationForm">
					<div class="modal-body p-4">
						<input type="hidden" name="idRotation" id="idRotation">

						<div class="row">
							<div class="col-md-2">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="goodIdNew">Код товара</label>
									<input type="number" name="goodIdNew" id="goodIdNew" min="0" step="1" placeholder="Код товара" class="form-control" required>
								</div>
							</div>
							<div class="col-md-10">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="goodNameNew">Наименование товара</label>
									<input type="text" name="goodNameNew" id="goodNameNew" class="form-control" placeholder="Наименование товара" required>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-md-6">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="startDate">Дата начала</label>
									<input type="date" name="startDate" id="startDate" class="form-control" required>
								</div>
							</div>
							<div class="col-md-6">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="endDate">Дата окончания</label>
									<input type="date" name="endDate" id="endDate" class="form-control" required>
								</div>
							</div>
						</div>

						<div class="row">
							<div class="col-md-2">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="goodIdAnalog">Код аналога</label>
									<input type="number" name="goodIdAnalog" id="goodIdAnalog" min="0" step="1" placeholder="Код аналога" class="form-control" required>
								</div>
							</div>
							<div class="col-md-10">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="goodNameAnalog">Наименование аналога</label>
									<input type="text" name="goodNameAnalog" id="goodNameAnalog" class="form-control" placeholder="Наименование аналога" required>
								</div>
							</div>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="toList">Список ТО / Сеть</label>
							<textarea class="form-control" name="toList" id="toList" rows="3" placeholder="Список ТО через запятую или Сеть" required></textarea>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="countOldCodeRemains">
								Учитывать остатки старого кода?
							</label>
							<select name="countOldCodeRemains" id="countOldCodeRemains" class="form-control" required>
								<option value="" selected hidden disabled>Выберите вариант</option>
								<option value="Да">Да</option>
								<option value="Нет">Нет</option>
							</select>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="limitOldCode">
								Порог ТЗ старого кода
							</label>
							<input type="number" name="limitOldCode" id="limitOldCode" min="4" max="20" step="1" placeholder="Целое число от 4 до 20" class="form-control" required>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="coefficient">
								Коэффициент переноса продаж старого кода на новый
							</label>
							<input type="number" name="coefficient" id="coefficient" min="-1" max="10" step="0.1" placeholder="Дробное число (до десятой) от -1 до 10" class="form-control" required>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="transferOldToNew">
								Переносим продажи старого кода к продажам нового, если есть продажи у нового?
							</label>
							<select name="transferOldToNew" id="transferOldToNew" class="form-control" required>
								<option value="" selected hidden disabled>Выберите вариант</option>
								<option value="Да">Да</option>
								<option value="Нет">Нет</option>
							</select>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="distributeNewPosition">
								Распределяем новую позицию, если есть остаток старого кода на РЦ?
							</label>
							<select name="distributeNewPosition" id="distributeNewPosition" class="form-control" required>
								<option value="" selected hidden disabled>Выберите вариант</option>
								<option value="Да">Да</option>
								<option value="Нет">Нет</option>
							</select>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="limitOldPositionRemain">
								Порог остатка старого кода на ТО (шт/кг)
							</label>
							<input type="number" name="limitOldPositionRemain" id="limitOldPositionRemain" min="0" placeholder="Целое число" step="1" class="form-control" required>
						</div>

						<!-- <div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="rotationInitiator">
								ФИО инициатора ротации
							</label>
							<input type="text" name="rotationInitiator" id="rotationInitiator" class="form-control" required>
						</div> -->
					</div>

					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Сохранить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- модальное окно редактирования коэффициента -->
	<div class="modal fade" id="updateCoefficientModal" tabindex="-1" aria-labelledby="updateCoefficientModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="updateCoefficientModalLabel">Укажите новый коэффициент переноса продаж</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="updateCoefficientForm" action="">
					<div class="modal-body">
						<div class="form-group">
							<input type="hidden" name="idRotation">
							<input type="hidden" name="goodIdNew">
							<input type="hidden" name="goodIdAnalog">
							<input type="hidden" name="status">
							<label class="text-muted font-weight-bold mb-1" for="updateCoefficient">
								Коэффициент переноса продаж старого кода на новый
							</label>
							<input type="number" name="coefficient" id="updateCoefficient" min="0" max="10" placeholder="0.0" step="0.1" class="form-control w-25" required>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Сохранить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/rotations.js"></script>
</body>
</html>
