<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Менеджер заявок</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurementControl.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/autocomplete.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<script src="${pageContext.request.contextPath}/resources/js/sortableJS/sortable.min.js"></script>
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>Список заявок</h3></strong>
		</div>
		<div class="accordion">
			<div class="search-form-container">
				<button class="accordion-btn collapsed" data-toggle="collapse" href="#orderSearchForm" role="button" aria-expanded="true" aria-controls="orderSearchForm">
					Поиск заявок
				</button>
				<form class="collapse" action="" id="orderSearchForm">
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">С</label>
						<input class="form-control" type="date" name="date_from" id="date_from" required>
					</div>
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">по</label>
						<input class="form-control" type="date" name="date_to" id="date_to" required>
					</div>
					<input class="form-control" type="text" name="searchName" id="searchName" placeholder="Наименование контрагента...">
					<button class="btn btn-outline-secondary" type="submit">Отобразить</button>
				</form>
			</div>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно -->
	<div class="modal fade" id="routeModal" tabindex="-1" aria-labelledby="routeModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h3 class="mb-0">Форма создания маршрута</h3>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="routeForm" name="routeForm" action="">
					<div class="form-container">
						<div class="modal-body">
							<div class="routeInfo-container">
								<div class="form-section left">
									<div class="form-group">
										<span class="text-muted font-weight-bold">Наименование контрагента: </span>
										<input type="text" class="form-control" name="counterparty" id="counterparty" hidden>
										<span id="counterpartyValue"></span>
									</div>
									<div class="form-group">
										<span class="text-muted font-weight-bold">Контактное лицо контрагента: </span>
										<input type="text" class="form-control" name="contact" id="contact" hidden>
										<span id="contactValue"></span>
									</div>
									<div class="form-group">
										<span class="text-muted font-weight-bold">Сверка УКЗ: </span>
										<input type="text" class="form-control" name="control" id="control" hidden>
										<span id="controlValue"></span>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Тип маршрута:</span>
										<input type="text" class="form-control" name="way" id="way" hidden>
										<span id="wayValue"></span>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Номер заказа из Маркета:</span>
										<input type="text" class="form-control" name="marketNumber" id="marketNumber" hidden>
										<span id="marketNumberValue"></span>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Погрузочный номер:</span>
										<input type="text" class="form-control" name="loadNumber" id="loadNumber" hidden>
										<span id="loadNumberValue"></span>
									</div>
									<div class="form-group">
										<label class="col-form-label text-muted font-weight-bold">Комментарии:</label>
										<textarea type="text" class="form-control" rows="2" name="comment" id="comment" placeholder="Комментарии" value=""></textarea>
									</div>
								</div>
								<div class="separationLine"></div>
								<div class="form-section right">
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Тип загрузки: <span class="text-red">*</span></span>
										<select id="typeLoad" name="typeLoad" class="form-control" required>
											<option value="" hidden disabled selected>Выберите тип загрузки авто</option>
											<option>Задняя</option>
											<option>Боковая</option>
											<option>Задняя+боковая</option>
											<option>Полная растентовка</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Способ загрузки: <span class="text-red">*</span></span>
										<select id="methodLoad" name="methodLoad" class="form-control" required>
											<option value="" hidden disabled selected>Выберите способ загрузки товара</option>
											<option >На паллетах</option>
											<option>Навалом</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Тип кузова: <span class="text-red">*</span></span>
										<select id="typeTruck" name="typeTruck" class="form-control" required>
											<option value="" hidden disabled selected>Выберите тип кузова</option>
											<option>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</select>
									</div>
									<div id="incoterms-container" class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">
											<a class="my-link" href="/speedlogist/api/procurement/downdoad/incoterms" download>
												Условия поставки: <span class="text-red">*</span>
											</a>
										</span>
										<select id="incoterms" name="incoterms" class="form-control" disabled required>
											<option value="" hidden disabled selected>Выберите подходящие условия</option>
											<option>FAS – Free Alongside Ship</option>
											<option>FOB – Free on Board</option>
											<option>CFR – Cost and Freight</option>
											<option>CIF – Cost, Insurance & Freight</option>
											<option>EXW – Ex Works</option>
											<option>FCA – Free Carrier</option>
											<option>CPT – Carriage Paid To</option>
											<option>CIP – Carriage and Insurance Paid to</option>
											<option>DAP – Delivered At Place</option>
											<option>DPU – Delivered At Place Unloaded</option>
											<option>DDP – Delivered Duty Paid</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold" title="Возможность размещения паллеты на паллету">Штабелирование: <span class="text-red">*</span></span>
										<select id="stacking" name="stacking" class="form-control" title="Возможность размещения паллеты на паллету" required>
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option value="Да">Да</option>
											<option value="Нет">Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Груз: <span class="text-red">*</span></span>
										<input type="text" class="form-control" name="cargo" id="cargo" placeholder="Наименование" value="" required>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Температура:</span>
										<input type="text" class="form-control" name="temperature" id="temperature" placeholder="Температурные условия" value="">
									</div>
								</div>
								<!-- <div class="separationLine"></div>
								<div class="form-section map">
									<div id="map"></div>
								</div> -->
							</div>
							<h4>Точки маршрута:</h4>
							<div class="point-container" id="pointList"></div>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
						<button class="btn btn-primary" type="submit">Создать маршрут</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<jsp:include page="footer.jsp" />
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/procurementControlLogist.js" type="module"></script>
</body>
</html>