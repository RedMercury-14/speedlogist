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

	<div id="overlay" class="">
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
									<input type="hidden" class="form-control" name="isInternalMovement" id="isInternalMovement">
									<input type="hidden" class="form-control" name="needUnloadPoint" id="needUnloadPoint">
									<div class="form-group">
										<label for="counterparty" class="col-form-label text-muted font-weight-bold">Наименование контрагента <span class="text-red">*</span></label>
										<input type="text" class="form-control" name="counterparty" id="counterparty" placeholder="Наименование контрагента (поставщика)" readonly required>
									</div>
									<div class="form-group">
										<label for="fio" class="col-form-label text-muted font-weight-bold">Контактное лицо контрагента</label>
										<input type="text" class="form-control" name="contact" id="contact" placeholder="ФИО, телефон" readonly>
									</div>
									<div class="form-group none">
										<label for="recipient" class="col-form-label text-muted font-weight-bold">Получатель</label>
										<input type="text" class="form-control" name="recipient" id="recipient" placeholder="Получатель">
									</div>
									<div id="control-container" class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Сверка УКЗ:</span>
										<select id="control" name="control" class="form-control">
											<option value="" hidden disabled>Выберите один из пунктов</option>
											<option value="Да">Да, сверять УКЗ</option>
											<option value="Нет" selected>Нет, не сверять УКЗ</option>
										</select>
									</div>
									<div id="control-container" class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Необходим TIR:</span>
										<select id="tir" name="tir" class="form-control">
											<option value="" hidden disabled>Выберите один из пунктов</option>
											<option value="Да">Да, необходим TIR для оформления</option>
											<option value="Нет" selected>Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Тип маршрута: <span class="text-red">*</span></span>
										<input type="text" class="form-control" name="way" id="way" placeholder="" required readonly>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Необходим гидроборт:</span>
										<select id="hydrolift" name="hydrolift" class="form-control">
											<option value="" hidden selected disabled>Выберите один из пунктов</option>
											<option value="Да">Да, гидроборт необходим</option>
											<option value="Нет">Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container mb-0">
										<div class="form-group none">
											<span class="text-muted font-weight-bold">Длина кузова, м:</span>
											<input type="number" class="form-control mt-2" list="carBodyLengthValues" name="carBodyLength" id="carBodyLength" min="0" step="0.01" placeholder="Длина в метрах">
											<datalist id="carBodyLengthValues">
												<option>3</option>
												<option>4</option>
												<option>5</option>
												<option>6</option>
												<option>7</option>
												<option>8</option>
											</datalist>
										</div>
										<div class="form-group none">
											<span class="text-muted font-weight-bold">Ширина кузова, м:</span>
											<input type="number" class="form-control mt-2" list="carBodyWidthValues" name="carBodyWidth" id="carBodyWidth" min="0" step="0.01" placeholder="Ширина в метрах">
											<datalist id="carBodyWidthValues">
												<option>1.5</option>
												<option>2</option>
												<option>2.5</option>
												<option>3</option>
											</datalist>
										</div>
										<div class="form-group none">
											<span class="text-muted font-weight-bold">Высота кузова, м:</span>
											<input type="number" class="form-control mt-2" list="carBodyHeightValues" name="carBodyHeight" id="carBodyHeight" min="0" step="0.01" placeholder="Ширина в метрах">
											<datalist id="carBodyHeightValues">
												<option>1.8</option>
												<option>2</option>
												<option>2.35</option>
												<option>2.5</option>
											</datalist>
										</div>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Номер заказа из Маркета:</span>
										<input type="number" class="form-control" name="marketNumber" id="marketNumber" readonly>
									</div>
									<div class="error-message" id="marketNumberMessage"></div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Погрузочный номер:</span>
										<input type="text" class="form-control" name="loadNumber" id="loadNumber" readonly>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold text-wrap">Информация из Маркета:</span>
										<textarea type="text" rows="1" class="form-control" name="marketInfo" id="marketInfo" placeholder="Комментарии из Маркета" readonly></textarea>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold text-wrap">Информация о маршруте:</span>
										<textarea type="text" class="form-control" name="routeComments" id="routeComments" placeholder="Например, строгие погран. переходы, порты, иное"></textarea>
									</div>
								</div>
								<div class="separationLine"></div>
								<div class="form-section right">
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Количество заявок:</span>
										<input type="number" class="form-control" name="orderCount" id="orderCount" placeholder="Количество заявок" value="1" min="0" step="1" readonly>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Тип загрузки: <span class="text-red">*</span></span>
										<select id="typeLoad" name="typeLoad" class="form-control" required>
											<option value="" hidden disabled selected>Выберите тип загрузки авто</option>
											<option>Задняя</option>
											<option>Боковая</option>
											<option>Задняя+боковая</option>
											<option>Полная растентовка</option>
											<option>Верхняя</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Способ загрузки: <span class="text-red">*</span></span>
										<select id="methodLoad" name="methodLoad" class="form-control" required>
											<option value="" hidden disabled selected>Выберите способ загрузки товара</option>
											<option>На паллетах</option>
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
											<option>Бус</option>
											<option>Манипулятор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
											<!-- <option>Контейнер 20 футов (Dry Freight)</option>
											<option>Контейнер 40 футов (Dry Freight)</option>
											<option>Контейнер 20 футов (High Cube)</option>
											<option>Контейнер 40 футов (High Cube)</option>
											<option>Контейнер рефрижератор 20 футов (Refer)</option>
											<option>Контейнер рефрижератор 40 футов (Refer)</option> -->
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
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Место поставки: </span>
										<input type="text" class="form-control" name="deliveryLocation" id="deliveryLocation" placeholder="Место поставки">
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold" title="Возможность размещения паллеты на паллету">Штабелирование: <span class="text-red">*</span></span>
										<select id="stacking" name="stacking" class="form-control" title="Возможность размещения паллеты на паллету" required>
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option>Да</option>
											<option>Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Груз: <span class="text-red">*</span></span>
										<input type="text" class="form-control" name="cargo" id="cargo" placeholder="Наименование" required>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Грузоподъемность, т:</span>
										<input type="number" class="form-control" name="truckLoadCapacity" id="truckLoadCapacity" placeholder="Грузоподъемность, т" min="0">
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Объем кузова, м.куб.:</span>
										<input type="number" class="form-control" name="truckVolume" id="truckVolume" placeholder="Объем, м.куб." min="0">
									</div>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Температура:</span>
										<input type="text" class="form-control" name="temperature" id="temperature" placeholder="Температурные условия">
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold text-wrap">Фитосанитарный груз:</span>
										<select id="phytosanitary" name="phytosanitary" class="form-control">
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option value="Да">Да</option>
											<option value="Нет" selected>Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold text-wrap">Ветеринарный груз:</span>
										<select id="veterinary" name="veterinary" class="form-control">
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option value="Да">Да</option>
											<option value="Нет" selected>Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Опасный груз:</span>
										<select id="dangerous" name="dangerous" class="form-control">
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option value="Да">Да</option>
											<option value="Нет" selected>Нет</option>
										</select>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">UN номер:</span>
										<input type="text" class="form-control" name="dangerousUN" id="dangerousUN" placeholder="Например, UN2074">
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Класс опасности:</span>
										<select id="dangerousClass" name="dangerousClass" class="form-control">
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option value="1">класс 1 - взрывчатые вещества и изделия</option>
											<option value="2">класс 2 - газы</option>
											<option value="3">класс 3 - легковоспламеняющиеся жидкости</option>
											<option value="4.1">класс 4.1 - легковоспламеняющиеся твердые вещества, самореактивные вещества и твердые десенсибилизированные взрывчатые вещества</option>
											<option value="4.2">класс 4.2 - вещества, способные к самовозгоранию</option>
											<option value="4.3">класс 4.3 - вещества, выделяющие легковоспламеняющиеся газы при соприкосновении с водой</option>
											<option value="5.1">класс 5.1 - окисляющие вещества</option>
											<option value="5.2">класс 5.2 - органические пероксиды</option>
											<option value="6.1">класс 6.1 - токсичные вещества</option>
											<option value="6.2">класс 6.2 - инфекционные вещества</option>
											<option value="7">класс 7 - радиоактивные материалы</option>
											<option value="8">класс 8 - коррозионные вещества</option>
											<option value="9">класс 9 - прочие опасные вещества и изделия</option>
										</select>
									</div>
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Группа упаковки:</span>
										<select id="dangerousPackingGroup" name="dangerousPackingGroup" class="form-control">
											<option value="" hidden disabled selected>Выберите один из пунктов</option>
											<option value="1">I группа - вещества с высокой степенью опасности</option>
											<option value="2">II группа - вещества со средней степенью опасности</option>
											<option value="3">III группа - вещества с низкой степенью опасности</option>
										</select>
									</div>
									<div class="form-group input-row-container text-wrap none">
										<span class="text-muted font-weight-bold text-wrap">Коды ограничений проезда через тоннели:</span>
										<textarea class="form-control" name="dangerousRestrictionCodes" id="dangerousRestrictionCodes" rows="2" placeholder="Например, (B/D)"></textarea>
									</div>
								</div>
							</div>
							<div class="comment-container">
								<div class="form-group">
									<label for="comment" class="col-form-label text-muted font-weight-bold">Комментарии:</label>
									<textarea type="text" class="form-control" name="comment" id="comment" placeholder="Комментарии" value='${order.comment}'>${order.comment}</textarea>
								</div>
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

	<datalist id="times">
		<option value="" hidden disabled selected> --:-- </option>
		<option value="00:00">00:00</option>
		<option value="00:30">00:30</option>
		<option value="01:00">01:00</option>
		<option value="01:30">01:30</option>
		<option value="02:00">02:00</option>
		<option value="02:30">02:30</option>
		<option value="03:00">03:00</option>
		<option value="03:30">03:30</option>
		<option value="04:00">04:00</option>
		<option value="04:30">04:30</option>
		<option value="05:00">05:00</option>
		<option value="05:30">05:30</option>
		<option value="06:00">06:00</option>
		<option value="06:30">06:30</option>
		<option value="07:00">07:00</option>
		<option value="07:30">07:30</option>
		<option value="08:00">08:00</option>
		<option value="08:30">08:30</option>
		<option value="09:00">09:00</option>
		<option value="09:30">09:30</option>
		<option value="10:00">10:00</option>
		<option value="10:30">10:30</option>
		<option value="11:00">11:00</option>
		<option value="11:30">11:30</option>
		<option value="12:00">12:00</option>
		<option value="12:30">12:30</option>
		<option value="13:00">13:00</option>
		<option value="13:30">13:30</option>
		<option value="14:00">14:00</option>
		<option value="14:30">14:30</option>
		<option value="15:00">15:00</option>
		<option value="15:30">15:30</option>
		<option value="16:00">16:00</option>
		<option value="16:30">16:30</option>
		<option value="17:00">17:00</option>
		<option value="17:30">17:30</option>
		<option value="18:00">18:00</option>
		<option value="18:30">18:30</option>
		<option value="19:00">19:00</option>
		<option value="19:30">19:30</option>
		<option value="20:00">20:00</option>
		<option value="20:30">20:30</option>
		<option value="21:00">21:00</option>
		<option value="21:30">21:30</option>
		<option value="22:00">22:00</option>
		<option value="22:30">22:30</option>
		<option value="23:00">23:00</option>
		<option value="23:30">23:30</option>
	</datalist>

	<jsp:include page="footer.jsp" />
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/procurementControlLogistImport.js" type="module"></script>
</body>
</html>