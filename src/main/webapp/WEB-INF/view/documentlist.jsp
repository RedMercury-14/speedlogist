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
	<title>Архив актов выполненых работ</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/documentlist.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tooltip.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0">
		<div class="search-form-container">
			<form class="" action="" id="orderSearchForm">
				<span class="font-weight-bold text-muted mb-0">Архив актов </span>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">с</label>
					<input class="form-control" type="date" name="date_from" id="date_from" required>
				</div>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">по</label>
					<input class="form-control" type="date" name="date_to" id="date_to" required>
				</div>
				<button class="btn btn-outline-secondary font-weight-bold" type="submit">Отобразить</button>
			</form>
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<!-- модальное окно с формой подтверждения действия с актом -->
		<div class="modal fade" id="confirmModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="confirmModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title fs-5 m-0" id="confirmModalLabel"></h4>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<form id="confirmForm" action="">
						<div class="modal-body">
							<input type="hidden" name="idAct">
							<input type="hidden" name="command">

							<p id="actInfoConfirm"></p>

							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Укажите комментарий</label>
								<textarea class="form-control" name="comment" id="comment" placeholder="Комментарий"></textarea>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
							<button type="submit" class="btn btn-primary">Подтвердить действие</button>
						</div>
					</form>
				</div>
			</div>
		</div>

		<!-- модальное окно с формой указания времени получения документов -->
		<div class="modal fade" id="documentsArrivedModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="documentsArrivedModalLabel" aria-hidden="true">
			<div class="modal-dialog">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title fs-5 m-0" id="documentsArrivedModalLabel">Укажите дату получения документов</h4>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<form id="documentsArrivedForm" action="">
						<div class="modal-body">
							<input type="hidden" name="idAct">

							<p id="actInfoToDocumentsArrived"></p>

							<div class="form-group w-50">
								<label class="col-form-label text-muted font-weight-bold">Дата</label>
								<input type="datetime-local" class="form-control" name="documentsArrived" id="documentsArrived" required>
							</div>
						</div>
						<div class="modal-footer">
							<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
							<button type="submit" class="btn btn-primary">Установить дату</button>
						</div>
					</form>
				</div>
			</div>
		</div>


		<!-- модальное окно для отображения таблицы акта -->
		<div class="modal fade" id="actRowsModal" tabindex="-1" aria-labelledby="actRowsModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-xl">
				<div class="modal-content">
					<div class="modal-header">
						<h4 class="modal-title fs-5 m-0" id="actRowsModalLabel">Акт №</h4>
						<button type="button" class="close" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">
						<div class="table-scroll">
							<table id="table" class="table table-bordered table-striped m-0">
								<thead class="thead-dark text-center">
									<tr>
										<th scope="col">Дата загрузки</th>
										<th scope="col">Дата выгрузки</th>
										<th scope="col">№ рейса</th>
										<th scope="col">Маршрут</th>
										<th class="carNumber" scope="col">№ ТС</th>
										<th scope="col">№ Путевого листа</th>
										<th scope="col">№ ТТН/CMR</th>
										<th scope="col">Объем груза (тонн)</th>
										<th scope="col">Сумма без НДС</th>
										<th scope="col">Сумма НДС</th>
										<th scope="col">Платные дороги</th>
										<th scope="col">Сумма c НДС</th>
									</tr>
								</thead>
								<tbody id="actRows"></tbody>
							</table>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</div>
			</div>
		</div>

		<div id="snackbar"></div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/documentlist.js" type="module"></script>
</body>
</html>