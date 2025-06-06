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
	<title>Управление разрешениями товар-склад</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/goodAccommodations.css">
</head>
<body>

	<jsp:include page="headerNEW.jsp" />

	<sec:authorize access="isAuthenticated()">
		<sec:authentication property="principal.authorities" var="roles" />
		<sec:authentication property="name" var="login"/>
	</sec:authorize>

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0 position-relative">
		<div class="title-container">
			<strong><h3>Управление разрешениями товар-склад</h3></strong>
		</div>
		<div class="toolbar">
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
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<div id="snackbar"></div>
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

	<!-- Модальное окно  -->
	<div class="modal fade" id="editProductControlModal" tabindex="-1" role="dialog" aria-labelledby="editProductControlModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="editProductControlModalLabel">Редактирование</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form class="" id="editProductControlForm">
					<div class="modal-body">
						<input type="hidden" name="idGoodAccommodation" id="idGoodAccommodation">
						<input type="hidden" name="dateCreate" id="dateCreate">
						<input type="hidden" name="status" id="status">
						<input type="hidden" name="initiatorName" id="initiatorName">
						<input type="hidden" name="initiatorEmail" id="initiatorEmail">
						<div class="form-group mr-2">
							<label class="mb-1" for="goodId">Код товара</label>
							<input type="text" class="form-control" name="productCode" id="productCode" readonly required>
						</div>
						<div class="form-group mr-2">
							<label class="mb-1" for="goodId">Штрихкод</label>
							<input type="text" class="form-control" name="barcode" id="barcode" readonly required>
						</div>
						<div class="form-group mr-2">
							<label class="mb-1" for="goodId">Наименование продукта</label>
							<textarea class="form-control" name="goodName" id="goodName" rows="2" readonly required></textarea>
						</div>
						<div class="form-group mr-2">
							<label class="mb-1" for="goodId">Наименование товарной гр.</label>
							<textarea class="form-control" name="productGroup" id="productGroup" rows="3" readonly required></textarea>
						</div>
						<div class="form-group mr-2">
							<label class="mb-1" for="goodId"><strong class="text-muted">Склады</strong> (укажите номера складов через запятую)</label>
							<input type="text" class="form-control" name="stocks" id="stocks" required>
						</div>
					</div>

					<div class="modal-footer">
						<button type="submit" class="btn bg-color">Сохранить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/goodAccommodations.js"></script>
</body>
</html>
