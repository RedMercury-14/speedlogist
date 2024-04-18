<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Копирование заявки</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurementForm2.css">
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
					<c:choose>
						<c:when test="${order.isInternalMovement == 'true'}">
							<h3 class="mb-0">Форма создания заявки (Внутренние перевозки)</h3>
						</c:when>
						<c:otherwise>
							<h3 class="mb-0">Форма создания заявки (${order.way})</h3>
						</c:otherwise>
					</c:choose>
					<input type="hidden" class="form-control" name="isInternalMovement" id="isInternalMovement" value="${order.isInternalMovement}">
					<input type="hidden" class="form-control" name="needUnloadPoint" id="needUnloadPoint" value="${order.needUnloadPoint}">
					<input type="hidden" class="form-control" name="idOrder" id="idOrder" value="${order.idOrder}">
				</div>
				<div class="card-body">
					<div class="form-container">
						<div class="form-section left">
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Наименование контрагента: <span class="text-red">*</span></label>
								<input type="text" class="form-control" name="contertparty" id="contertparty" placeholder="Наименование контрагента (поставщика)" value='${order.counterparty}' required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Контактное лицо контрагента: </label>
								<input type="text" class="form-control" name="contact" id="contact" placeholder="ФИО, тел." value='${order.contact}'>
							</div>
							<c:choose>
								<c:when test="${order.way == 'Импорт'}">
									<div class="form-group input-row-container">
										<span class="col-form-label text-muted font-weight-bold">Сверка УКЗ: <span class="text-red">*</span></span>
										<select id="control" name="control" class="form-control" required>
											<option value="" hidden disabled selected>Выберите способ загрузки товара</option>
											<c:choose>
												<c:when test="${order.control == true}">
													<option value="Да" selected>Да, сверять УКЗ</option>
													<option value="Нет">Нет, не сверять УКЗ</option>
												</c:when>
												<c:otherwise>
													<option value="Да">Да, сверять УКЗ</option>
													<option value="Нет" selected>Нет, не сверять УКЗ</option>
												</c:otherwise>
											</c:choose>
										</select>
									</div>
								</c:when>
								<c:otherwise>
									<input type="hidden" class="form-control" name="control" id="control" value="Нет" required readonly>
								</c:otherwise>
							</c:choose>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Тип маршрута: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="way" id="way" value="${order.way}" required readonly>
							</div>
							<c:choose>
								<c:when test="${order.isInternalMovement == 'true' || order.way == 'Экспорт'}">
									<div class="form-group input-row-container none">
										<span class="text-muted font-weight-bold">Номер заказа из Маркета:</span>
										<input type="number" class="form-control" name="marketNumber" id="marketNumber">
									</div>
								</c:when>
								<c:otherwise>
									<div class="form-group input-row-container">
										<span class="text-muted font-weight-bold">Номер заказа из Маркета: <span class="text-red">*</span></span>
										<input type="number" class="form-control" name="marketNumber" id="marketNumber" required>
									</div>
								</c:otherwise>
							</c:choose>
							<div class="error-message" id="marketNumberMessage"></div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Погрузочный номер: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="loadNumber" id="loadNumber" required>
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
									<c:choose>
										<c:when test="${order.typeLoad == 'Задняя'}">
											<option selected>Задняя</option>
											<option>Боковая</option>
											<option>Задняя+боковая</option>
											<option>Полная растентовка</option>
										</c:when>
										<c:when test="${order.typeLoad == 'Боковая'}">
											<option>Задняя</option>
											<option selected>Боковая</option>
											<option>Задняя+боковая</option>
											<option>Полная растентовка</option>
										</c:when>
										<c:when test="${order.typeLoad == 'Задняя+боковая'}">
											<option>Задняя</option>
											<option>Боковая</option>
											<option selected>Задняя+боковая</option>
											<option>Полная растентовка</option>
										</c:when>
										<c:when test="${order.typeLoad == 'Полная растентовка'}">
											<option>Задняя</option>
											<option>Боковая</option>
											<option>Задняя+боковая</option>
											<option selected>Полная растентовка</option>
										</c:when>
										<c:otherwise>
											<option>Задняя</option>
											<option>Боковая</option>
											<option>Задняя+боковая</option>
											<option>Полная растентовка</option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Способ загрузки: <span class="text-red">*</span></span>
								<select id="methodLoad" name="methodLoad" class="form-control" required>
									<option value="" hidden disabled selected>Выберите способ загрузки товара</option>
									<c:choose>
										<c:when test="${order.methodLoad == 'На паллетах'}">
											<option selected>На паллетах</option>
											<option>Навалом</option>
										</c:when>
										<c:when test="${order.methodLoad == 'Навалом'}">
											<option>На паллетах</option>
											<option selected>Навалом</option>
										</c:when>
										<c:otherwise>
											<option>На паллетах</option>
											<option>Навалом</option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Тип кузова: <span class="text-red">*</span></span>
								<select id="typeTruck" name="typeTruck" class="form-control" required>
									<option value="" hidden disabled selected>Выберите тип кузова</option>
									<c:choose>
										<c:when test="${order.typeTruck == 'Открытый'}">
											<option selected>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:when>
										<c:when test="${order.typeTruck == 'Тент'}">
											<option>Открытый</option>
											<option selected>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:when>
										<c:when test="${order.typeTruck == 'Изотермический'}">
											<option>Открытый</option>
											<option>Тент</option>
											<option selected>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:when>
										<c:when test="${order.typeTruck == 'Мебельный фургон'}">
											<option>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option selected>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:when>
										<c:when test="${order.typeTruck == 'Рефрижератор'}">
											<option>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option selected>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:when>
										<c:when test="${order.typeTruck == 'Контейнер 20 футов'}">
											<option>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option selected>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:when>
										<c:when test="${order.typeTruck == 'Контейнер 40 футов'}">
											<option>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option selected>Контейнер 40 футов</option>
										</c:when>
										<c:otherwise>
											<option>Открытый</option>
											<option>Тент</option>
											<option>Изотермический</option>
											<option>Мебельный фургон</option>
											<option>Рефрижератор</option>
											<option>Контейнер 20 футов</option>
											<option>Контейнер 40 футов</option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>
							<c:choose>
								<c:when test="${order.typeTruck == 'Контейнер 20 футов' || order.typeTruck == 'Контейнер 40 футов'}">
									<div id="incoterms-container" class="form-group input-row-container">
										<span class="text-muted font-weight-bold">
											<a class="my-link" href="/speedlogist/api/procurement/downdoad/incoterms" download>
												Условия поставки: <span class="text-red">*</span>
											</a>
										</span>
										<select id="incoterms" name="incoterms" class="form-control" required>
											<option value="" hidden disabled selected>Выберите подходящие условия</option>
											<c:choose>
												<c:when test="${order.incoterms == 'FAS – Free Alongside Ship'}">
													<option selected>FAS – Free Alongside Ship</option>
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
												</c:when>
												<c:when test="${order.incoterms == 'FOB – Free on Board'}">
													<option>FAS – Free Alongside Ship</option>
													<option selected>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'CFR – Cost and Freight'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option selected>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'CIF – Cost, Insurance & Freight'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option selected>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'EXW – Ex Works'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option selected>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'FCA – Free Carrier'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option selected>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'CPT – Carriage Paid To'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option selected>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'CIP – Carriage and Insurance Paid to'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option selected>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'DAP – Delivered At Place'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option selected>DAP – Delivered At Place</option>
													<option>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'DPU – Delivered At Place Unloaded'}">
													<option>FAS – Free Alongside Ship</option>
													<option>FOB – Free on Board</option>
													<option>CFR – Cost and Freight</option>
													<option>CIF – Cost, Insurance & Freight</option>
													<option>EXW – Ex Works</option>
													<option>FCA – Free Carrier</option>
													<option>CPT – Carriage Paid To</option>
													<option>CIP – Carriage and Insurance Paid to</option>
													<option>DAP – Delivered At Place</option>
													<option selected>DPU – Delivered At Place Unloaded</option>
													<option>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:when test="${order.incoterms == 'DDP – Delivered Duty Paid'}">
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
													<option selected>DDP – Delivered Duty Paid</option>
												</c:when>
												<c:otherwise>
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
												</c:otherwise>
											</c:choose>
										</select>
									</div>
								</c:when>
								<c:otherwise>
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
								</c:otherwise>
							</c:choose>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold" title="Возможность размещения паллеты на паллету">Штабелирование: <span class="text-red">*</span></span>
								<select id="stacking" name="stacking" class="form-control" title="Возможность размещения паллеты на паллету" required>
									<option value="" hidden disabled selected>Выберите один из пунктов</option>
									<c:choose>
										<c:when test="${order.stacking == true}">
											<option selected>Да</option>
											<option>Нет</option>
										</c:when>
										<c:when test="${order.stacking == false}">
											<option>Да</option>
											<option selected>Нет</option>
										</c:when>
										<c:otherwise>
											<option>Да</option>
											<option>Нет</option>
										</c:otherwise>
									</c:choose>
								</select>
							</div>
							<div class="form-group input-row-container">
								<span class="text-muted font-weight-bold">Груз: <span class="text-red">*</span></span>
								<input type="text" class="form-control" name="cargo" id="cargo" placeholder="Наименование" value='${order.cargo}' required>
							</div>
							<div class="form-group input-row-container mb-0">
								<c:choose>
									<c:when test="${order.typeTruck == 'Изотермический' || order.typeTruck == 'Рефрижератор'}">
										<span class="text-muted font-weight-bold">Температура: <span class="text-red">*</span></span>
										<input type="text" class="form-control" name="temperature" id="temperature" placeholder="Температурные условия" value="${order.temperature}" required>
									</c:when>
									<c:otherwise>
										<span class="text-muted font-weight-bold">Температура:</span>
										<input type="text" class="form-control" name="temperature" id="temperature" placeholder="Температурные условия" value="${order.temperature}">
									</c:otherwise>
								</c:choose>
							</div>
						</div>
					</div>
					<div class="comment-container px-3">
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold">Комментарии:</label>
							<textarea type="text" class="form-control" name="comment" id="comment" placeholder="Комментарии" value='${order.comment}'>${order.comment}</textarea>
						</div>
					</div>

	<!-- Контейнер с точками маршрута -->
					<h4>Точки маршрута:</h4>
					<div class="point-container">
						<c:forEach var="point" items="${order.addressesToView}" varStatus="loop">
							<c:choose>
								<c:when test="${order.needUnloadPoint == 'true'}">
									<c:choose>
										<c:when test="${point.type == 'Загрузка'}">
											<div class="card point" data-type="${point.type}">
												<div class="card-header">
													<h5 class="d-flex align-items-center mb-0">Точка ${loop.index + 1}: ${point.type} </h5>
													<input type="hidden" class="form-control" name="type_${loop.index + 1}" id="type" value="${point.type}">
												</div>
												<div class="card-body">
													<div class="row-container info-container form-group">
														<div class="pointDate">
															<div class="d-flex align-items-center position-relative">
																<label class="col-form-label text-muted font-weight-bold mr-2">Дата <span class="text-red">*</span></label>
																<span id="statusInfoLabel" class="status-info-label">!</span>
																<div id="statusInfo" class="status-info">
																	<p class="mb-0">При создании заявки до 12:00 текущего дня минимальная дата загрузки/выгрузки - завтра, после 12:00 - послезавтра</p>
																</div>
															</div>
															<input type="date" class="form-control date-input" name="date_${loop.index + 1}" id="loadDate" data-index="${loop.index}" required>
														</div>
														<div class="pointTime">
															<label class="col-form-label text-muted font-weight-bold ">Время <span class="text-red">*</span></label>
															<select name="time_${loop.index + 1}" id="loadTime" data-index="${loop.index}" class="form-control" required>
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
														<div class="cargoName">
															<label class="col-form-label text-muted font-weight-bold">Наименование груза <span class="text-red">*</span></label>
															<input type="text" class="form-control" name="pointCargo_${loop.index + 1}" id="pointCargo" placeholder="Наименование" value='${point.cargo}' required>
														</div>
														<div class="cargoPall">
															<label class="col-form-label text-muted font-weight-bold">Паллеты, шт</label>
															<input type="number" class="form-control" name="pall_${loop.index + 1}" id="pall" placeholder="Паллеты, шт" min="0" value="${point.pall}" required>
														</div>
														<div class="cargoWeight">
															<label class="col-form-label text-muted font-weight-bold">Масса, кг</label>
															<input type="number" class="form-control" name="weight_${loop.index + 1}" id="weight" placeholder="Масса, кг" min="0" value="${point.weight}">
														</div>
														<div class="cargoVolume">
															<label class="col-form-label text-muted font-weight-bold">Объем, м.куб.</label>
															<input type="number" class="form-control" name="volume_${loop.index + 1}" id="volume" placeholder="Объем, м.куб." min="0" value="${point.volume}">
														</div>
													</div>
													<div class="form-group">
														<label class="col-form-label text-muted font-weight-bold">Коды ТН ВЭД: <span class="text-red">*</span></label>
														<textarea class="form-control" name="tnvd_${loop.index + 1}" id="tnvd" placeholder="Коды ТН ВЭД" required>${point.tnvd}</textarea>
													</div>
													<div class="row-container addresses-container form-group">
														<div class="address-flex-elem">
															<label class="col-form-label text-muted font-weight-bold">Адрес склада <span class="text-red">*</span></label>
															<div class="address-container">
																<div class="autocomplete">
																	<input type="text" class="form-control country-input" name="country_${loop.index + 1}" id="country" placeholder="Страна" required>
																</div>
																<input type="text" class="form-control address-input" name="pointAddress_${loop.index + 1}" id="pointAddress" placeholder="Город, улица и т.д." value='${point.bodyAddress}' required>
															</div>
														</div>
														<div class="customsAddress-flex-elem">
															<label class="col-form-label text-muted font-weight-bold">Адрес таможенного пункта</label>
															<input type="text" class="form-control" name="customsAddress_${loop.index + 1}" id="customsAddress" placeholder="Страна, город, улица и т.д." value='${point.customsAddress}'>
														</div>
													</div>
													<div class="row-container">
														<div class="timeFrame-container">
															<label class="col-form-label text-muted font-weight-bold">Время работы склада <span class="text-red">*</span></label>
															<input type="text" class="form-control" name="timeFrame_${loop.index + 1}" id="timeFrame" value="${point.timeFrame}" required>
														</div>
														<div class="contact-container">
															<label class="col-form-label text-muted font-weight-bold">Контактное лицо на складе <span class="text-red">*</span></label>
															<input type="text" class="form-control" name="pointContact_${loop.index + 1}" id="pointContact" placeholder="ФИО, телефон" value='${point.contact}' required>
														</div>
													</div>
												</div>
											</div>
										</c:when>
									</c:choose>
								</c:when>
								<c:otherwise>
									<div class="card point" data-type="${point.type}">
										<div class="card-header">
											<h5 class="d-flex align-items-center mb-0">Точка ${loop.index + 1}: ${point.type} </h5>
											<input type="hidden" class="form-control" name="type_${loop.index + 1}" id="type" value="${point.type}">
										</div>
										<div class="card-body">
											<div class="row-container info-container form-group">
												<div class="pointDate">
													<c:choose>
														<c:when test="${point.type == 'Загрузка'}">
															<div class="d-flex align-items-center position-relative">
																<label class="col-form-label text-muted font-weight-bold mr-2">Дата <span class="text-red">*</span></label>
																<span id="statusInfoLabel" class="status-info-label">!</span>
																<div id="statusInfo" class="status-info">
																	<p class="mb-0">При создании заявки до 12:00 текущего дня минимальная дата загрузки - через 2 дня, после 12:00 - через 3 дня</p>
																</div>
															</div>
															<input type="date" class="form-control date-input" name="date_${loop.index + 1}" id="loadDate" data-index="${loop.index}" required>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${order.way == 'РБ'}">
																	<div class="d-flex align-items-center position-relative">
																		<label class="col-form-label text-muted font-weight-bold mr-2">Дата <span class="text-red">*</span></label>
																	</div>
																	<input type="date" class="form-control date-input" name="date_${loop.index + 1}" id="unloadDate" data-index="${loop.index}" required>
																</c:when>
																<c:otherwise>
																	<div class="d-flex align-items-center position-relative">
																		<label class="col-form-label text-muted font-weight-bold mr-2">Дата</label>
																	</div>
																	<input type="date" class="form-control date-input" name="date_${loop.index + 1}" id="unloadDate" data-index="${loop.index}">
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
												</div>
												<div class="pointTime">
													<c:choose>
														<c:when test="${point.type == 'Загрузка'}">
															<label class="col-form-label text-muted font-weight-bold ">Время <span class="text-red">*</span></label>
															<select name="time_${loop.index + 1}" id="loadTime" data-index="${loop.index}" class="form-control" required>
														</c:when>
														<c:otherwise>
															<c:choose>
																<c:when test="${order.way == 'Импорт'}">
																	<label class="col-form-label text-muted font-weight-bold">Время</label>
																	<select name="time_${loop.index + 1}" id="unloadTime" data-index="${loop.index}" class="form-control">
																</c:when>
																<c:otherwise>
																	<label class="col-form-label text-muted font-weight-bold none">Время</label>
																	<select name="time_${loop.index + 1}" id="unloadTime" data-index="${loop.index}" class="form-control none">
																</c:otherwise>
															</c:choose>
														</c:otherwise>
													</c:choose>
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
												<div class="cargoName">
													<label class="col-form-label text-muted font-weight-bold">Наименование груза <span class="text-red">*</span></label>
													<input type="text" class="form-control" name="pointCargo_${loop.index + 1}" id="pointCargo" placeholder="Наименование" value='${point.cargo}' required>
												</div>
												<div class="cargoPall">
													<label class="col-form-label text-muted font-weight-bold">Паллеты, шт</label>
													<input type="number" class="form-control" name="pall_${loop.index + 1}" id="pall" placeholder="Паллеты, шт" min="0" value="${point.pall}" required>
												</div>
												<div class="cargoWeight">
													<label class="col-form-label text-muted font-weight-bold">Масса, кг</label>
													<input type="number" class="form-control" name="weight_${loop.index + 1}" id="weight" placeholder="Масса, кг" min="0" value="${point.weight}">
												</div>
												<div class="cargoVolume">
													<label class="col-form-label text-muted font-weight-bold">Объем, м.куб.</label>
													<input type="number" class="form-control" name="volume_${loop.index + 1}" id="volume" placeholder="Объем, м.куб." min="0" value="${point.volume}">
												</div>
											</div>
											<c:choose>
												<c:when test="${point.type == 'Загрузка'}">
													<div class="form-group">
														<c:choose>
															<c:when test="${order.way == 'РБ'}">
																<label class="col-form-label text-muted font-weight-bold">Коды ТН ВЭД:</label>
																<textarea class="form-control" name="tnvd_${loop.index + 1}" id="tnvd" placeholder="Коды ТН ВЭД">${point.tnvd}</textarea>
															</c:when>
															<c:otherwise>
																<label class="col-form-label text-muted font-weight-bold">Коды ТН ВЭД: <span class="text-red">*</span></label>
																<textarea class="form-control" name="tnvd_${loop.index + 1}" id="tnvd" placeholder="Коды ТН ВЭД" required>${point.tnvd}</textarea>
															</c:otherwise>
														</c:choose>
													</div>
												</c:when>
											</c:choose>
											<div class="row-container addresses-container form-group">
												<div class="address-flex-elem">
													<label class="col-form-label text-muted font-weight-bold">Адрес склада <span class="text-red">*</span></label>
													<c:choose>
														<c:when test="${order.isInternalMovement == 'true'}">
															<input type="hidden" id="pointBodyAddress" value='${point.bodyAddress}'>
															<div class="address-container">
																<div class="autocomplete">
																	<input type="text" class="form-control country-input" name="country_${loop.index + 1}" id="country" placeholder="Страна" value="BY Беларусь" required readonly>
																</div>
																<select name="pointAddress_${loop.index + 1}" id="pointAddress" class="form-control address-input" required>
																	<option value="" disabled selected hidden>Выберите склад</option>
																</select>
															</div>
														</c:when>
														<c:otherwise>
															<div class="address-container">
																<div class="autocomplete">
																	<c:choose>
																		<c:when test="${order.way == 'РБ'}">
																			<input type="text" class="form-control country-input" name="country_${loop.index + 1}" id="country" placeholder="Страна" required readonly>
																		</c:when>
																		<c:otherwise>
																			<c:choose>
																				<c:when test="${order.way == 'Импорт' && point.type == 'Выгрузка'}">
																					<input type="text" class="form-control country-input" name="country_${loop.index + 1}" id="country" placeholder="Страна" required readonly>
																				</c:when>
																				<c:when test="${order.way == 'Экспорт' && point.type == 'Загрузка'}">
																					<input type="text" class="form-control country-input" name="country_${loop.index + 1}" id="country" placeholder="Страна" required readonly>
																				</c:when>
																				<c:otherwise>
																					<input type="text" class="form-control country-input" name="country_${loop.index + 1}" id="country" placeholder="Страна" required>
																				</c:otherwise>
																			</c:choose>
																		</c:otherwise>
																	</c:choose>
																</div>
																<input type="text" class="form-control address-input" name="pointAddress_${loop.index + 1}" id="pointAddress" placeholder="Город, улица и т.д." value='${point.bodyAddress}' required>
															</div>
														</c:otherwise>
													</c:choose>
												</div>
												<c:choose>
													<c:when test="${order.way == 'РБ'}">
														<div class="customsAddress-flex-elem none">
															<label class="col-form-label text-muted font-weight-bold">Адрес таможенного пункта</label>
															<input type="text" class="form-control" name="customsAddress_${loop.index + 1}" id="customsAddress" placeholder="Страна, город, улица и т.д." value='${point.customsAddress}'>
														</div>
													</c:when>
													<c:otherwise>
														<div class="customsAddress-flex-elem">
															<label class="col-form-label text-muted font-weight-bold">Адрес таможенного пункта</label>
															<input type="text" class="form-control" name="customsAddress_${loop.index + 1}" id="customsAddress" placeholder="Страна, город, улица и т.д." value='${point.customsAddress}'>
														</div>
													</c:otherwise>
												</c:choose>
											</div>
											<div class="row-container">
												<div class="timeFrame-container">
													<label class="col-form-label text-muted font-weight-bold">Время работы склада <span class="text-red">*</span></label>
													<input type="text" class="form-control" name="timeFrame_${loop.index + 1}" id="timeFrame" value="${point.timeFrame}" required>
												</div>
												<div class="contact-container">
													<label class="col-form-label text-muted font-weight-bold">Контактное лицо на складе <span class="text-red">*</span></label>
													<input type="text" class="form-control" name="pointContact_${loop.index + 1}" id="pointContact" placeholder="ФИО, телефон" value='${point.contact}' required>
												</div>
											</div>
										</div>
									</div>
								</c:otherwise>
							</c:choose>
						</c:forEach>
					</div>
				</div>
				<div class="card-footer">
					<button id="cancelBtn" class="btn btn-secondary btn-lg" type="button">Отмена</button>
					<button class="btn btn-primary btn-lg" type="submit">Создать заявку</button>
				</div>
			</form>
		</div>
		<div id="snackbar"></div>
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

	<jsp:include page="footer.jsp" />
	<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/procurementCopy.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>