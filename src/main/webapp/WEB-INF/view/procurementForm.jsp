<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Создание заявки</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurementForm2.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/autocomplete.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrapSelect/bootstrapSelect.css">
	<script src='${pageContext.request.contextPath}/resources/js/popper/popper.js'></script>
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
	<div class="container my-container">
		<div class="card">
			<form id="orderForm" action="" method="post">
				<div class="card-header d-flex justify-content-between">
					<h3 class="mb-0" id="formName">Форма создания заявки</h3>
					<button id="clearOrderForm" class="btn btn-secondary" type="button">Очистить форму</button>
				</div>
				<div class="card-body">
					<div class="form-container">
						<div class="form-section left">
							<input type="hidden" class="form-control" name="isInternalMovement" id="isInternalMovement" value="false">
							<input type="hidden" class="form-control" name="needUnloadPoint" id="needUnloadPoint" value="false">
							<div class="form-group">
								<label for="counterparty" class="col-form-label text-muted font-weight-bold">Наименование контрагента <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="counterparty" id="counterparty" placeholder="Наименование контрагента (поставщика)" required>
							</div>
							<div class="form-group">
								<label for="fio" class="col-form-label text-muted font-weight-bold">Контактное лицо контрагента</label>
								<div class="form-group contact-inputs">
									<input type="text" class="form-control" name="fio" id="fio" placeholder="ФИО">
									<input type="text" class="form-control" name="tel" id="tel" autocomplete="off" placeholder="Телефон">
								</div>
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
								<input type="number" class="form-control" name="marketNumber" id="marketNumber">
							</div>
							<div class="error-message" id="marketNumberMessage"></div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Погрузочный номер: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="loadNumber" id="loadNumber" required>
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
					<div class="comment-container px-3">
						<div class="form-group">
							<label for="comment" class="col-form-label text-muted font-weight-bold">Комментарии:</label>
							<textarea type="text" class="form-control" name="comment" id="comment" placeholder="Комментарии" value='${order.comment}'>${order.comment}</textarea>
						</div>
					</div>

					<h3>Точки маршрута:</h3>
					<div id="pointList" class="point-container"></div>
					<div class="button-container my-3">
						<button id="addLoadPoint"  type="submit" class="btn btn-outline-secondary">+ точка загрузки</button>
						<button id="addUnloadPoint" type="submit" class="btn btn-outline-secondary none">+ точка выгрузки</button>
						<button id="deleteLastPoint" type="button" class="btn btn-outline-danger">Удалить последнюю точку</button>
						<div class="text-red font-italic py-2">Важно: добавляйте точки в порядке их прохождения машиной (начните с точки загрузки)</div>
					</div>
					<div class="disableSlotRedirect-container">
						<input id="disableSlotRedirect" type="checkbox">
						<label for="disableSlotRedirect">Отключить переадресацию в слоты</label>
					</div>
				</div>
				<div class="card-footer d-flex justify-content-center">
					<button id="formSubmitBtn" class="btn btn-primary btn-lg" type="submit">Создать заявку</button>
				</div>
			</form>
		</div>
		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно типа маршрута -->
	<div class="modal fade" id="wayTypeModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="wayTypeModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="wayTypeModalLabel">Выберите тип маршрута</h5>
				</div>
				<div class="modal-body">
					<div id="wayButtons" class="modal-buttons">
						<button data-value="РБ" class="btn btn-primary btn-lg" type="button" >РБ</button>
						<button data-value="Импорт" class="btn btn-primary btn-lg" type="button">Импорт</button>
						<button data-value="Экспорт" class="btn btn-primary btn-lg" type="button">Экспорт</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно заказ уточнения по РБ -->
	<div class="modal fade" id="RBModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="RBModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title " id="RBModalLabel">Выберите необходимый вариант заявки</h5>
				</div>
				<div class="modal-body">
					<div id="RBButtons" class="modal-buttons">
						<button data-value="counterparty" class="btn btn-primary btn-lg" type="button">Заказ от контрагента</button>
						<button data-value="domestic" class="btn btn-primary btn-lg" type="button">Внутреннее перемещение</button>
						<button data-value="aho" class="btn btn-primary btn-lg" data-dismiss="modal" type="button">Перевозка АХО/СГИ</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно оклейка, УКЗ, СИ, акциз -->
	<div class="modal fade" id="fullImportModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="fullImportModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="fullImportModalLabel">Требуется оклейка, УКЗ, СИ, акцизы?</h5>
				</div>
				<div class="modal-body">
					<div id="fullImportButtons" class="modal-buttons">
						<button data-value="Да" class="btn btn-primary btn-lg" type="button">Да</button>
						<button data-value="Нет" class="btn btn-primary btn-lg" type="button">Нет</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно оклейка, УКЗ, СИ, акциз -->
	<div class="modal fade" id="EAEUImportModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="EAEUImportModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="EAEUImportModalLabel">Импорт из Таможенного Союза?</h5>
				</div>
				<div class="modal-body">
					<div id="EAEUImportButtons" class="modal-buttons">
						<button data-value="Да" class="btn btn-primary btn-lg" type="button">Да</button>
						<button data-value="Нет" class="btn btn-primary btn-lg" type="button">Нет</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно указания номера из Маркета -->
	<div class="modal fade" id="setMarketNumberModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="setMarketNumberModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="setMarketNumberModalLabel">Укажите номер из Маркета</h5>
				</div>
				<div class="modal-body">
					<form id="setMarketNumberForm" action="">
						<div class="form-group input-row-container">
							<input type="number" class="form-control form-control-lg" name="setMarketNumber" id="setMarketNumber" placeholder="Номер из Маркета" required>
							<button class="btn btn-primary btn-lg text-nowrap" type="submit">Загрузить заказ</button>
						</div>
					</form>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно промежуточной точки загрузки -->
	<div class="modal fade" id="middleUnloadPointModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="middleUnloadPointModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="middleUnloadPointModalLabel">Необходима промежуточная точка загрузки?</h5>
				</div>
				<div class="modal-body">
					<p>При наличии выгрузки и загрузки по одному адресу необходимо указывать как точку выгрузки, так и точку загрузки!</p>
					<div id="middleUnloadPointButtons" class="modal-buttons">
						<button data-value="Да" class="btn btn-primary btn-lg" data-dismiss="modal" type="button">Да (откроется окно точки ЗАГРУЗКИ)</button>
						<button data-value="Нет" class="btn btn-primary btn-lg" data-dismiss="modal" type="button">Нет (откроется окно точки ВЫГРУЗКИ)</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно типа заявки для роли закупок внутренних перемещений -->
	<div class="modal fade" id="stockProcFormTypeModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="stockProcFormTypeModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="stockProcFormTypeModalLabel">Выберите тип заявки</h5>
				</div>
				<div class="modal-body">
					<div id="stockProcFormTypeButtons" class="modal-buttons">
						<button data-value="internalMovement" class="btn btn-primary btn-lg" data-dismiss="modal" type="button">Внутренние перемещения</button>
						<button data-value="aho" class="btn btn-primary btn-lg" data-dismiss="modal" type="button">Перевозка АХО/СГИ</button>
					</div>
				</div>
			</div>
		</div>
	</div>



	<!-- Модальное окно информации о страховании груза -->
	<div class="modal fade" id="incotermsInsuranceModal" tabindex="-1" aria-labelledby="incotermsInsuranceModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header pb-2">
					<h1 class="modal-title my-0" id="incotermsInsuranceModalLabel">Страхование грузов</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<p class="info-text">
						При выборе данных условий перевозки рекомендованно застраховать груз.
						Рекомендации по страхованию грузов можно посмотреть
						<a class="my-link" href="/speedlogist/api/procurement/downdoad/incotermsInsurance" download>
							здесь
						</a>.
					</p>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Ок, понятно</button>
				</div>
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
	<script src='${pageContext.request.contextPath}/resources/js/bootstrapSelect/bootstrapSelect.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/bootstrapSelect/defaults-ru_RU.js'></script>
	<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/procurementFormImport.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>