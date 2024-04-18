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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurementForm.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/autocomplete.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
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
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Наименование контрагента <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="contertparty" id="contertparty" placeholder="Наименование контрагента (поставщика)" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Контактное лицо контрагента</label>
								<div class="form-group contact-container">
									<input type="text" class="form-control" name="fio" id="fio" placeholder="ФИО">
									<input type="text" class="form-control" name="tel" id="tel" placeholder="Телефон">
								</div>
							</div>
							<div id="control-container" class="form-group input-row-container none">
								<span class="text-muted font-weight-bold">Сверка УКЗ: <span class="text-red">*</span></span>
								<select id="control" name="control" class="form-control" required>
									<option value="" hidden disabled>Выберите один из пунктов</option>
									<option value="Да">Да, сверять УКЗ</option>
									<option value="Нет" selected>Нет, не сверять УКЗ</option>
								</select>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Тип маршрута: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="way" id="way" placeholder="" required readonly>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Номер заказа из Маркета:</span>
								<input type="number" class="form-control" name="marketNumber" id="marketNumber">
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Погрузочный номер: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="loadNumber" id="loadNumber" required>
							</div>
							<div class="error-message" id="marketNumberMessage"></div>
							<div class="form-group">
								<textarea type="text" class="form-control" name="comment" id="comment" placeholder="Комментарии"></textarea>
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
									<option>Да</option>
									<option>Нет</option>
								</select>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Груз: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="cargo" id="cargo" placeholder="Наименование" required>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Температура:</span>
								<input type="text" class="form-control" name="temperature" id="temperature" placeholder="Температурные условия">
							</div>
						</div>
					</div>

					<h3>Точки маршрута:</h3>
					<div class="point-container">
						<div class="text-red font-italic">Не указана ни одна точка маршрута!</div>
					</div>
					<div class="button-container my-3">
						<button id="addLoadPoint"  type="submit" class="btn btn-outline-secondary">+ точка загрузки</button>
						<button id="addUnloadPoint" type="submit" class="btn btn-outline-secondary">+ точка выгрузки</button>
						<div class="text-red font-italic py-2">Важно: добавляйте точки в порядке их прохождения машиной (начните с точки загрузки)</div>
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

	<!-- Модальное окно для добавления точки загрузки -->
	<div class="modal fade" id="addLoadPointModal" tabindex="-1" aria-labelledby="addLoadPointModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header pb-2">
					<h1 class="modal-title my-0" id="addLoadPointModalLabel">Добавить точку загрузки</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addLoadPointForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<input type="hidden" class="form-control" name="type" id="type" value="Загрузка">
							<div class="date-container">
								<div class="form-group input-row-container">
									<label class="text-muted font-weight-bold" for="date">Дата загрузки<span class="text-red">*</span></label>
									<input type="date" class="form-control date-input" name="date" id="loadDate" required>
									<span id="statusInfoLabel" class="status-info-label">!</span>
									<div id="statusInfo" class="status-info">
										<p class="mb-0">При создании заявки до 12:00 текущего дня минимальная дата загрузки - через 2 дня, после 12:00 - через 3 дня</p>
									</div>
								</div>
								<div class="form-group input-row-container">
									<label class="text-muted font-weight-bold" for="time">Время загрузки <span class="text-red">*</span></label>
									<!-- <input type="time" class="form-control" name="time" id="time" step="1800" required> -->
									<select id="loadTime" name="time" class="form-control" required>
										<option value="" hidden disabled selected> --:-- </option>
										<option class="font-weight-bold" value="00:00">00:00</option>
										<option value="00:30">00:30</option>
										<option class="font-weight-bold" value="01:00">01:00</option>
										<option value="01:30">01:30</option>
										<option class="font-weight-bold" value="02:00">02:00</option>
										<option value="02:30">02:30</option>
										<option class="font-weight-bold" value="03:00">03:00</option>
										<option value="03:30">03:30</option>
										<option class="font-weight-bold" value="04:00">04:00</option>
										<option value="04:30">04:30</option>
										<option class="font-weight-bold" value="05:00">05:00</option>
										<option value="05:30">05:30</option>
										<option class="font-weight-bold" value="06:00">06:00</option>
										<option value="06:30">06:30</option>
										<option class="font-weight-bold" value="07:00">07:00</option>
										<option value="07:30">07:30</option>
										<option class="font-weight-bold" value="08:00">08:00</option>
										<option value="08:30">08:30</option>
										<option class="font-weight-bold" value="09:00">09:00</option>
										<option value="09:30">09:30</option>
										<option class="font-weight-bold" value="10:00">10:00</option>
										<option value="10:30">10:30</option>
										<option class="font-weight-bold" value="11:00">11:00</option>
										<option value="11:30">11:30</option>
										<option class="font-weight-bold" value="12:00">12:00</option>
										<option value="12:30">12:30</option>
										<option class="font-weight-bold" value="13:00">13:00</option>
										<option value="13:30">13:30</option>
										<option class="font-weight-bold" value="14:00">14:00</option>
										<option value="14:30">14:30</option>
										<option class="font-weight-bold" value="15:00">15:00</option>
										<option value="15:30">15:30</option>
										<option class="font-weight-bold" value="16:00">16:00</option>
										<option value="16:30">16:30</option>
										<option class="font-weight-bold" value="17:00">17:00</option>
										<option value="17:30">17:30</option>
										<option class="font-weight-bold" value="18:00">18:00</option>
										<option value="18:30">18:30</option>
										<option class="font-weight-bold" value="19:00">19:00</option>
										<option value="19:30">19:30</option>
										<option class="font-weight-bold" value="20:00">20:00</option>
										<option value="20:30">20:30</option>
										<option class="font-weight-bold" value="21:00">21:00</option>
										<option value="21:30">21:30</option>
										<option class="font-weight-bold" value="22:00">22:00</option>
										<option value="22:30">22:30</option>
										<option class="font-weight-bold" value="23:00">23:00</option>
										<option value="23:30">23:30</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Характеристики груза <span class="text-red">*</span></label>
								<div class="form-group cargo-container">
									<input type="text" class="form-control" name="pointCargo" id="pointCargo" placeholder="Наименование" required>
									<input type="number" class="form-control" name="pall" id="pall" placeholder="Паллеты, шт" min="0" required>
									<input type="number" class="form-control" name="weight" id="weight" placeholder="Масса, кг" min="0" required>
									<input type="number" class="form-control" name="volume" id="volume" placeholder="Объем, м.куб." min="0">
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Коды ТН ВЭД <span class="text-red">*</span></label>
								<textarea class="form-control" name="tnvd" id="tnvd" placeholder="Коды ТН ВЭД" required></textarea>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес склада загрузки <span class="text-red">*</span></label>
								<div class="form-group address-container">
									<div class="autocomplete">
										<input type="text" class="form-control country" name="country" id="country" placeholder="Страна" required>
									</div>
									<input type="text" class="form-control" name="address" id="address" placeholder="Город, улица и т.д." required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Время работы склада загрузки <span class="text-red">*</span></label>
								<div class="form-group timeFrame-container">
									С
									<input type="time" class="form-control" name="timeFrame_from" id="timeFrame_from" required>
									по
									<input type="time" class="form-control" name="timeFrame_to" id="timeFrame_to" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Контактное лицо на складе <span class="text-red">*</span></label>
								<div class="form-group contact-container">
									<input type="text" class="form-control" name="pointContact_fio" id="pointContact_fio" placeholder="ФИО" required>
									<input type="text" class="form-control" name="pointContact_tel" id="pointContact_tel" placeholder="Телефон" required>
								</div>
							</div>
							<div class="form-group customs-container">
								<label class="col-form-label text-muted font-weight-bold">Адрес таможенного пункта</label>
								<div class="form-group address-container">
									<div class="autocomplete">
										<input type="text" class="form-control country" name="customsCountry" id="customsCountry" placeholder="Страна">
									</div>
									<input type="text" class="form-control" name="customsAddress" id="customsAddress" placeholder="Адрес">
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить точку</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для добавления точки выгрузки -->
	<div class="modal fade" id="addUnloadPointModal" tabindex="-1" aria-labelledby="addUnloadPointModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header pb-2">
					<h1 class="modal-title my-0" id="addUnloadPointModalLabel">Добавить точку выгрузки</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addUnloadPointForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<input type="hidden" class="form-control" name="type" id="type" value="Выгрузка">
							<div class="date-container">
								<div class="form-group input-row-container unloadDate-container">
									<label class="text-muted font-weight-bold" for="date">Дата выгрузки <span class="text-red">*</span></label>
									<input type="date" class="form-control date-input" name="date" id="unloadDate" required>
								</div>
								<div class="form-group input-row-container unloadTime-container none">
									<label class="text-muted font-weight-bold" for="time">Время выгрузки</label>
									<!-- <input type="time" class="form-control" name="time" id="time" step="1800"> -->
									<select id="unloadTime" name="time" class="form-control">
										<option value="" hidden disabled selected> --:-- </option>
										<option class="font-weight-bold" value="00:00">00:00</option>
										<option value="00:30">00:30</option>
										<option class="font-weight-bold" value="01:00">01:00</option>
										<option value="01:30">01:30</option>
										<option class="font-weight-bold" value="02:00">02:00</option>
										<option value="02:30">02:30</option>
										<option class="font-weight-bold" value="03:00">03:00</option>
										<option value="03:30">03:30</option>
										<option class="font-weight-bold" value="04:00">04:00</option>
										<option value="04:30">04:30</option>
										<option class="font-weight-bold" value="05:00">05:00</option>
										<option value="05:30">05:30</option>
										<option class="font-weight-bold" value="06:00">06:00</option>
										<option value="06:30">06:30</option>
										<option class="font-weight-bold" value="07:00">07:00</option>
										<option value="07:30">07:30</option>
										<option class="font-weight-bold" value="08:00">08:00</option>
										<option value="08:30">08:30</option>
										<option class="font-weight-bold" value="09:00">09:00</option>
										<option value="09:30">09:30</option>
										<option class="font-weight-bold" value="10:00">10:00</option>
										<option value="10:30">10:30</option>
										<option class="font-weight-bold" value="11:00">11:00</option>
										<option value="11:30">11:30</option>
										<option class="font-weight-bold" value="12:00">12:00</option>
										<option value="12:30">12:30</option>
										<option class="font-weight-bold" value="13:00">13:00</option>
										<option value="13:30">13:30</option>
										<option class="font-weight-bold" value="14:00">14:00</option>
										<option value="14:30">14:30</option>
										<option class="font-weight-bold" value="15:00">15:00</option>
										<option value="15:30">15:30</option>
										<option class="font-weight-bold" value="16:00">16:00</option>
										<option value="16:30">16:30</option>
										<option class="font-weight-bold" value="17:00">17:00</option>
										<option value="17:30">17:30</option>
										<option class="font-weight-bold" value="18:00">18:00</option>
										<option value="18:30">18:30</option>
										<option class="font-weight-bold" value="19:00">19:00</option>
										<option value="19:30">19:30</option>
										<option class="font-weight-bold" value="20:00">20:00</option>
										<option value="20:30">20:30</option>
										<option class="font-weight-bold" value="21:00">21:00</option>
										<option value="21:30">21:30</option>
										<option class="font-weight-bold" value="22:00">22:00</option>
										<option value="22:30">22:30</option>
										<option class="font-weight-bold" value="23:00">23:00</option>
										<option value="23:30">23:30</option>
									</select>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Характеристики груза <span class="text-red">*</span></label>
								<div class="form-group cargo-container">
									<input type="text" class="form-control" name="pointCargo" id="pointCargo" placeholder="Наименование" required>
									<input type="number" class="form-control" name="pall" id="pall" placeholder="Паллеты, шт" min="0" required>
									<input type="number" class="form-control" name="weight" id="weight" placeholder="Масса, кг" min="0" required>
									<input type="number" class="form-control" name="volume" id="volume" placeholder="Объем, м.куб." min="0">
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Адрес склада выгрузки <span class="text-red">*</span></label>
								<div class="form-group address-container">
									<div class="autocomplete">
										<input type="text" class="form-control country" name="country" id="country" placeholder="Страна" required>
									</div>
									<input type="text" class="form-control" name="address" id="address" placeholder="Город, улица и т.д." required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Время работы склада выгрузки <span class="text-red">*</span></label>
								<div class="form-group timeFrame-container">
									С
									<input type="time" class="form-control" name="timeFrame_from" id="timeFrame_from" required>
									по
									<input type="time" class="form-control" name="timeFrame_to" id="timeFrame_to" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Контактное лицо на складе <span class="text-red">*</span></label>
								<div class="form-group contact-container">
									<input type="text" class="form-control" name="pointContact_fio" id="pointContact_fio" placeholder="ФИО" required>
									<input type="text" class="form-control" name="pointContact_tel" id="pointContact_tel" placeholder="Телефон" required>
								</div>
							</div>
							<div class="form-group customs-container">
								<label class="col-form-label text-muted font-weight-bold">Адрес таможенного пункта</label>
								<div class="form-group address-container">
									<div class="autocomplete">
										<input type="text" class="form-control country" name="customsCountry" id="customsCountry" placeholder="Страна">
									</div>
									<input type="text" class="form-control" name="customsAddress" id="customsAddress" placeholder="Адрес">
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить точку</button>
					</div>
				</form>
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

	<jsp:include page="footer.jsp" />
	<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/procurementForm.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>