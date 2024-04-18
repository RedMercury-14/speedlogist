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
	<title>Маршрут</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/tenderPage.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<br>
	<br>
	<br>
	<br>
	<sec:authorize access="isAuthenticated()">
		<strong>
			<sec:authentication property="principal.authorities" var="roles" />
		</strong>
	</sec:authorize>
	<input type="hidden" value="<sec:authentication property="principal.username" var="targetLogin" />" id="login" >
	<input type="hidden" value="${user.surname} ${user.name}" id="fullName">
	<c:choose>
		<c:when test="${route.comments == 'international' && route.startPrice == null && route.user == null}">
			<div class="container my-container">
				<input type="hidden" value="1" />
				<form:form method="get" action="./tenderOffer">
					<input type="hidden" value="${route.idRoute}" name="id" />
					<input type="hidden" value="${userCost}" name="userCost" />
					<h3 class="route-title">Маршрут ${route.routeDirection} от ${route.simpleDateStart}</h3>
					<div class="card route-card">
						<div class="card-header offer-container">
							<c:choose>
								<c:when test="${flag}">
									<h5 class="route-subtitle">Ваше предложение ${userCost} ${userCurrency}</h5>
									<input type="submit" value="Отменить" name="notagree" class="notagreeinternational btn btn-danger">
								</c:when>
								<c:otherwise>
									<h5 class="route-subtitle">Предложение</h5>
									<input type="number" name="cost" size="3" required="true" class="raz form-control" min="0">
									<select class="form-control" id="currency">
										<option>BYN</option>
										<option>USD</option>
										<option>EUR</option>
										<option>RUB</option>
										<option>KZT</option>
									</select>
									<input type="submit" value="Поддержать цену" name="agree" class="agreeinternational btn btn-success">
									<input type="hidden" value="0" name="price" size="1" />
								</c:otherwise>
							</c:choose>
						</div>
						<div class="card-body pt-2 pb-2">
							<h5 class="route-subtitle">Данные о заказе</h5>
						</div>
						<c:choose>
							<c:when test="${route.dateUnloadPreviouslyStock != null}">
								<c:choose>
									<c:when test="${route.way == 'РБ'}">
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo text-danger">
												<span>Слот на выгрузку:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo">
												<span>Доставить до:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${route.loadNumber != null}">
								<div class="card-body pt-2 pb-2">
									<div class="dateUnloadInfo font-weight-bold">
										<span>Погрузочный номер:</span>
										<span>${route.loadNumber}</span>
									</div>
								</div>
							</c:when>
						</c:choose>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Дата загрузки:</strong>
								<span class="text-muted">${route.simpleDateStart}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Время загрузки (планируемое):</strong>
								<span class="text-muted">${route.timeLoadPreviously}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Груз:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.cargo}</span>
								</c:forEach>
							</p>
							<c:choose>
								<c:when test="${route.startPrice != null}">
									<p class="card-text d-flex flex-column">
										<strong>Стоимость перевозки:</strong>
										<span class="text-muted">${route.startPrice}</span>
									</p>
								</c:when>
							</c:choose>
							<p class="card-text d-flex flex-column">
								<strong>Комментарии:</strong>
								<span class="text-muted">${route.userComments}</span>
							</p>
						</div>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Паллеты:</strong>
								<span class="text-muted pall-content">${route.totalLoadPall} шт</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Масса:</strong>
								<span class="text-muted">${route.totalCargoWeight} кг</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Тип прицепа:</strong>
								<span class="text-muted">${route.typeTrailer}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Температура:</strong>
								<span class="text-muted">${route.temperature}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Объем:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.volume}</span>
								</c:forEach>
							</p>
						</div>
						<div class="card-body pt-0">
							<p class="card-text">
								<strong>Коды ТН ВЭД:</strong>
								<span class="text-muted pall-content">${route.tnvd}</span>
							</p>
						</div>
						<div class="card-footer">
							<label>
								<h5>Данные по точкам</h5>
							</label>
							<div class="table-scroll">
								<table class="table table-bordered table-condensed">
									<thead class="text-center">
										<tr>
											<th>Номер точки</th>
											<th>Масса</th>
											<th>Паллеты</th>
											<th>Адрес</th>
											<th>Таможня</th>
										</tr>
									</thead>
									<form:form modelAttribute="route" method="post">
										<c:forEach var="point" items="${route.roteHasShop}">
											<tr>
												<td>${point.position}</td>
												<td>${point.weight} кг</td>
												<td>${point.pall} шт</td>
												<td>${point.address}</td>
												<td>${point.customsAddress}</td>
											</tr>
										</c:forEach>
									</form:form>
								</table>
							</div>
							<c:choose>
								<c:when test="${route.logistInfo != null}">
									<div class="dateUnloadInfo mt-1">
										<span>Контакты логиста:</span>
										<span>${route.logistInfo}</span>
									</div>
								</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
							<c:forEach var="order" items="${route.orders}" end="0">
								<input type="hidden" value="${order.control}">
								<c:choose>
									<c:when test="${order.control == true}">
										<div class="dateUnloadInfo mt-1 text-danger">
											<span>
												Перед загрузкой необходимо предоставить фото
												<abbr title="Унифицированный контрольный знак">УКЗ</abbr>
												и
												<abbr title="Средство идентификации">СИ</abbr> логисту
											</span>
										</div>
									</c:when>
								</c:choose>
							</c:forEach>
						</div>
					</div>
				</form:form>
			</div>
		</c:when>

		<c:when test="${route.startPrice != null && route.user == null}">
			<div class="container my-container">
				<input type="hidden" value="2" />
				<form:form method="get" action="./tenderOffer">
					<input type="hidden" value="${route.idRoute}" name="id" />
					<h3 class="route-title">Маршрут №${route.idRoute} ${route.routeDirection}</h3>
					<div class="card route-card">
						
						<div class="card-header auction-container">
							<div class="d-flex">
								<button type="button" class="btn btn-info" onclick="this.nextElementSibling.stepDown()">-</button>
								<input type="number" min="0" readonly class="raz2" name="cost" step="${route.stepCost}">
								<button type="button" class="btn btn-info" onclick="this.previousElementSibling.stepUp()">+</button>
							</div>
							<select class="form-control" id="currency">
								<option>BYN</option>
							</select>
							<div>
								<c:choose>
									<c:when test="${route.user != null}"></c:when>
									<c:otherwise>
										<input type="submit" value="Предложить цену" class="agreeinternational btn btn-success">
									</c:otherwise>
								</c:choose>
							</div>
						</div>
						<div class="card-header offer-container">
							<div class="none" id="startPriceChoice">${route.startPrice}</div>
							<p class="card-text">
								<strong>Колличество участников биржи:</strong>
								<strong><span class="text-warning numUsers"></span></strong>
							</p>
							<br>
							<p class="card-text">
								<strong>Последняя предложенная цена:</strong>
								<strong>
									<span class="lastCost text-warning"></span>
									<span class="text-warning">BYN</span>
								</strong>
							</p>
						</div>
						<div class="card-body pt-2 pb-2">
							<h5 class="route-subtitle">Данные о заказе</h5>
						</div>
						<c:choose>
							<c:when test="${route.dateUnloadPreviouslyStock != null}">
								<c:choose>
									<c:when test="${route.way == 'РБ'}">
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo text-danger">
												<span>Слот на выгрузку:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo">
												<span>Доставить до:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${route.loadNumber != null}">
								<div class="card-body pt-2 pb-2">
									<div class="dateUnloadInfo font-weight-bold">
										<span>Погрузочный номер:</span>
										<span>${route.loadNumber}</span>
									</div>
								</div>
							</c:when>
						</c:choose>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Дата загрузки:</strong>
								<span class="text-muted">${route.simpleDateStart}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Время загрузки (планируемое):</strong>
								<span class="text-muted">${route.timeLoadPreviously}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Груз:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.cargo}</span>
								</c:forEach>
							</p>
							<c:choose>
								<c:when test="${route.startPrice != null}">
									<p class="card-text d-flex flex-column">
										<strong>Стоимость перевозки:</strong>
										<span class="text-muted">${route.startPrice} BYN</span>
									</p>
								</c:when>
							</c:choose>
							<p class="card-text d-flex flex-column">
								<strong>Комментарии:</strong>
								<span class="text-muted">${route.userComments}</span>
							</p>
						</div>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Паллеты:</strong>
								<span class="text-muted pall-content">${route.totalLoadPall} шт</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Масса:</strong>
								<span class="text-muted">${route.totalCargoWeight} кг</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Тип прицепа:</strong>
								<span class="text-muted">${route.typeTrailer}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Температура:</strong>
								<span class="text-muted">${route.temperature}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Объем:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.volume}</span>
								</c:forEach>
							</p>
						</div>
						<div class="card-body pt-0">
							<p class="card-text">
								<strong>Коды ТН ВЭД:</strong>
								<span class="text-muted pall-content">${route.tnvd}</span>
							</p>
						</div>
						<div class="card-footer">
							<label>
								<h5>Данные по точкам</h5>
							</label>
							<div class="table-scroll">
								<table class="table table-bordered table-condensed">
									<thead class="text-center">
										<tr>
											<th>Номер точки</th>
											<th>Масса</th>
											<th>Паллеты</th>
											<th>Адрес</th>
											<th>Таможня</th>
										</tr>
									</thead>
									<form:form modelAttribute="route" method="post">
										<c:forEach var="point" items="${route.roteHasShop}">
											<tr>
												<td>${point.position}</td>
												<td>${point.weight} кг</td>
												<td>${point.pall} шт</td>
												<td>${point.address}</td>
												<td>${point.customsAddress}</td>
											</tr>
										</c:forEach>
									</form:form>
								</table>
							</div>
							<c:choose>
								<c:when test="${route.logistInfo != null}">
									<div class="dateUnloadInfo mt-1">
										<span>Контакты логиста:</span>
										<span>${route.logistInfo}</span>
									</div>
								</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
							<c:forEach var="order" items="${route.orders}" end="0">
								<input type="hidden" value="${order.control}">
								<c:choose>
									<c:when test="${order.control == true}">
										<div class="dateUnloadInfo mt-1 text-danger">
											<span>
												Перед загрузкой необходимо предоставить фото
												<abbr title="Унифицированный контрольный знак">УКЗ</abbr>
												и
												<abbr title="Средство идентификации">СИ</abbr> логисту
											</span>
										</div>
									</c:when>
								</c:choose>
							</c:forEach>
						</div>
					</div>
				</form:form>
			</div>
		</c:when>

		<c:when test="${route.comments == 'international' && route.statusRoute >= '4' && route.user.numYNP != user.numYNP}">
			<div class="container my-container">
				<h1>Тендер завершен</h1>
			</div>
		</c:when>

		<c:when test="${route.comments == 'international' && route.user != null}">
			<div class="container my-container">
				<input type="hidden" value="3" />
				<form:form method="post" action="./tenderUpdate">
					<div class="card route-card">
						<input type="hidden" value="${route.idRoute}" name="id" />
						<c:set var="rate" value="${user.rate}" />
						<div class="card-header offer-container">
							<h3 class="route-title">Маршрут №${route.idRoute} ${route.routeDirection}</h3>
						</div>
						<div class="card-body pt-2 pb-2">
							<h5 class="route-subtitle">Данные о заказе</h5>
						</div>
						<c:choose>
							<c:when test="${route.dateUnloadPreviouslyStock != null}">
								<c:choose>
									<c:when test="${route.way == 'РБ'}">
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo text-danger">
												<span>Слот на выгрузку:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo">
												<span>Доставить до:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${route.loadNumber != null}">
								<div class="card-body pt-2 pb-2">
									<div class="dateUnloadInfo font-weight-bold">
										<span>Погрузочный номер:</span>
										<span>${route.loadNumber}</span>
									</div>
								</div>
							</c:when>
						</c:choose>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Дата загрузки:</strong>
								<span class="text-muted">${route.simpleDateStart}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Время загрузки (планируемое):</strong>
								<span class="text-muted">${route.timeLoadPreviously}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Груз:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.cargo}</span>
								</c:forEach>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Стоимость перевозки:</strong>
								<span class="text-muted">${route.finishPrice} ${route.startCurrency}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Комментарии:</strong>
								<span class="text-muted">${route.userComments}</span>
							</p>
						</div>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Паллеты:</strong>
								<span class="text-muted pall-content">${route.totalLoadPall} шт</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Масса:</strong>
								<span class="text-muted">${route.totalCargoWeight} кг</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Тип прицепа:</strong>
								<span class="text-muted">${route.typeTrailer}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Температура:</strong>
								<span class="text-muted">${route.temperature}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Объем:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.volume}</span>
								</c:forEach>
							</p>
						</div>
						<div class="card-body pt-0">
							<p class="card-text">
								<strong>Коды ТН ВЭД:</strong>
								<span class="text-muted pall-content">${route.tnvd}</span>
							</p>
						</div>
						<div class="card-footer">
							<label>
								<h5>Данные по точкам</h5>
							</label>
							<div class="table-scroll">
								<table class="table table-bordered table-condensed">
									<thead class="text-center">
										<tr>
											<th>Номер точки</th>
											<th>Масса</th>
											<th>Паллеты</th>
											<th>Адрес</th>
											<th>Таможня</th>
										</tr>
									</thead>
									<form:form modelAttribute="route" method="post">
										<c:forEach var="point" items="${route.roteHasShop}">
											<tr>
												<td>${point.position}</td>
												<td>${point.weight} кг</td>
												<td>${point.pall} шт</td>
												<td>${point.address}</td>
												<td>${point.customsAddress}</td>
											</tr>
										</c:forEach>
									</form:form>
								</table>
							</div>
							<c:choose>
								<c:when test="${route.logistInfo != null}">
									<div class="dateUnloadInfo mt-1">
										<span>Контакты логиста:</span>
										<span>${route.logistInfo}</span>
									</div>
								</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
							<c:forEach var="order" items="${route.orders}" end="0">
								<input type="hidden" value="${order.control}">
								<c:choose>
									<c:when test="${order.control == true}">
										<div class="dateUnloadInfo mt-1 text-danger">
											<span>
												Перед загрузкой необходимо предоставить фото
												<abbr title="Унифицированный контрольный знак">УКЗ</abbr>
												и
												<abbr title="Средство идентификации">СИ</abbr> логисту
											</span>
										</div>
									</c:when>
								</c:choose>
							</c:forEach>
						</div>
					</div>
				</form:form>
				<label>
					<h3>Статусы маршрута</h3>
				</label>
				<div class="table-scroll status-table">
					<table class="table table-bordered table-hover table-condensed">
						<thead class="text-center">
							<!-- 				внимание на disposition.js -->
							<tr>
								<th><input type="button" value="Подача машины" name="Подача_машины"></th>
								<th><input type="button" value="На месте зазгрузки" name="На_месте_зазгрузки"></th>
								<th><input type="button" value="Начали загружать" name="Начали_загружать"></th>
								<th><input type="button" value="Загружена" name="Загружена"></th>
								<th><input type="button" value="На таможне отправления" name="На_таможне_отправления"></th>
								<th><input type="button" value="Затаможена" name="Затаможена"></th>
							</tr>
							<tr>
								<th><input type="button" value="В пути" name="В_пути"></th>
								<th><input type="button" value="Проходит границу" name="Проходит_границу"></th>
								<th><input type="button" value="На таможне назначения" name="На_таможне_назначения"></th>
								<th><input type="button" value="Растаможена" name="Растаможена"></th>
								<th><input type="button" value="На выгрузке" name="На_выгрузке"></th>
							</tr>
						</thead>
					</table>
				</div>
			</div>
		</c:when>

		<c:when test="${regionalRoute != null && regionalRoute && route.user == null}">
			<div class="container-fluid my-container">
				<input type="hidden" value="4" />
				<div class="row">
					<h1>Текущие заказы </h1>
					<table class="table table-bordered border-primary table-hover table-condensed">
						<thead class="text-center">
							<tr>
								<th>Название маршрута</th>
								<th>Дата загрузки</th>
								<th>Время загрузки (планируемое)</th>
								<th>Комментарии</th>
								<th>Санобработка</th>
								<th>Температура</th>
								<th>Объем</th>
								<th>Общее колличество паллет</th>
								<th>Общий вес</th>
								<th>Стоимость перевозки</th>
								<th>Последняя предложенная скидка</th>
								<th>
									<c:choose>
										<c:when test="${route.user != null}">
										</c:when>
										<c:otherwise>
											Предложение
										</c:otherwise>
									</c:choose>
								</th>
							</tr>
							<c:set var="rate" value="${user.rate}" />
						</thead>
						<form:form method="post" action="./tenderUpdate">
							<input type="hidden" value="${route.idRoute}" name="id" />
							<tr>
								<td>${route.routeDirection}</td>
								<td>${route.dateLoadPreviously}</td>
								<td>${route.timeLoadPreviously}</td>
								<td>${route.userComments}</td>
								<td>${route.isSanitization}</td>
								<td>${route.temperature}</td>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<td>${RHS.volume}</td>
								</c:forEach>
								<td>${route.totalLoadPall}</td>
								<td>${route.totalCargoWeight}</td>
								<td>${route.cost[rate]}</td>
								<td>${route.finishPrice}%</td>
								<td>
									<c:choose>
										<c:when test="${route.user != null}">
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${route.finishPrice != null}">
													<input type="submit" value="предложить скидку в %" name="agree" id="regionalTenderButtonAgree">
													<input value="${route.finishPrice+5}" name="price" id="regionalTenderPriceAgree" />
												</c:when>
												<c:otherwise>
													<input type="submit" value="поддержать цену" name="agree" id="regionalTenderButtonAgree">
													<input type="hidden" value="0" name="price" id="regionalTenderPriceAgree" />
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</td>
						</form:form>
					</table><br>
					<label>
						<h3>Данные по точкам</h3>
					</label>
					<div class="table-scroll">
						<table class="table table-bordered border-primary table-hover table-condensed">
							<thead class="text-center">
								<tr>
									<th>Номер точки</th>
									<th>Масса</th>
									<th>Паллеты</th>
									<th>Адрес</th>
									<th>Таможня</th>
								</tr>
							</thead>
							<form:form modelAttribute="route" method="post">
								<c:forEach var="point" items="${route.roteHasShop}">
									<tr>
										<td>${point.order}</td>
										<td>${point.weight}</td>
										<td>${point.pall}</td>
										<td>${point.shop.address}</td>
										<td>${point.customsAddress}</td>
									</tr>
								</c:forEach>
							</form:form>
						</table>
					</div>
					<c:choose>
						<c:when test="${route.logistInfo != null}">
							<div class="dateUnloadInfo mt-1">
								<span>Контакты логиста:</span>
								<span>${route.logistInfo}</span>
							</div>
						</c:when>
						<c:otherwise></c:otherwise>
					</c:choose>
					<c:forEach var="order" items="${route.orders}" end="0">
						<input type="hidden" value="${order.control}">
						<c:choose>
							<c:when test="${order.control == true}">
								<div class="dateUnloadInfo mt-1 text-danger">
									<span>
										Перед загрузкой необходимо предоставить фото
										<abbr title="Унифицированный контрольный знак">УКЗ</abbr>
										и
										<abbr title="Средство идентификации">СИ</abbr> логисту
									</span>
								</div>
							</c:when>
						</c:choose>
					</c:forEach>
				</div>
			</div>
		</c:when>
	
		<c:when test="${regionalRoute != null && regionalRoute}">
			<div class="container my-container">
				<input type="hidden" value="5" />
				<h1>Просмотр маршрута по магазинам</h1>
				<form:form method="post" action="./tenderUpdate">
					<div class="card route-card">
						<input type="hidden" value="${route.idRoute}" name="id" />
						<c:set var="rate" value="${user.rate}" />
						<div class="card-header offer-container">
							<h3 class="route-title">Маршрут №${route.idRoute} ${route.routeDirection}</h3>
						</div>
						<div class="card-body pt-2 pb-2">
							<h5 class="route-subtitle">Данные о заказе</h5>
						</div>
						<c:choose>
							<c:when test="${route.dateUnloadPreviouslyStock != null}">
								<c:choose>
									<c:when test="${route.way == 'РБ'}">
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo text-danger">
												<span>Слот на выгрузку:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:when>
									<c:otherwise>
										<div class="card-body pt-2 pb-2">
											<div class="dateUnloadInfo">
												<span>Доставить до:</span>
												<span>${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</span>
											</div>
										</div>
									</c:otherwise>
								</c:choose>
							</c:when>
						</c:choose>
						<c:choose>
							<c:when test="${route.loadNumber != null}">
								<div class="card-body pt-2 pb-2">
									<div class="dateUnloadInfo font-weight-bold">
										<span>Погрузочный номер:</span>
										<span>${route.loadNumber}</span>
									</div>
								</div>
							</c:when>
						</c:choose>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Дата загрузки:</strong>
								<span class="text-muted">${route.simpleDateStart}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Время загрузки (планируемое):</strong>
								<span class="text-muted">${route.timeLoadPreviously}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Груз:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.cargo}</span>
								</c:forEach>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Стоимость перевозки:</strong>
								<span class="text-muted">${route.finishPrice} ${route.startCurrency}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Комментарии:</strong>
								<span class="text-muted">${route.userComments}</span>
							</p>
						</div>
						<div class="card-body pt-2">
							<p class="card-text d-flex flex-column">
								<strong>Паллеты:</strong>
								<span class="text-muted pall-content">${route.totalLoadPall} шт</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Масса:</strong>
								<span class="text-muted">${route.totalCargoWeight} кг</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Тип прицепа:</strong>
								<span class="text-muted">${route.typeTrailer}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Температура:</strong>
								<span class="text-muted">${route.temperature}</span>
							</p>
							<p class="card-text d-flex flex-column">
								<strong>Объем:</strong>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<span class="text-muted">${RHS.volume}</span>
								</c:forEach>
							</p>
						</div>
						<div class="card-body pt-0">
							<p class="card-text">
								<strong>Коды ТН ВЭД:</strong>
								<span class="text-muted pall-content">${route.tnvd}</span>
							</p>
						</div>
						<div class="card-footer">
							<label>
								<h5>Данные по точкам</h5>
							</label>
							<div class="table-scroll">
								<table class="table table-bordered table-condensed">
									<thead class="text-center">
										<tr>
											<th>Номер точки</th>
											<th>Масса</th>
											<th>Паллеты</th>
											<th>Адрес</th>
											<th>Таможня</th>
										</tr>
									</thead>
									<form:form modelAttribute="route" method="post">
										<c:forEach var="point" items="${route.roteHasShop}">
											<tr>
												<td>${point.position}</td>
												<td>${point.weight} кг</td>
												<td>${point.pall} шт</td>
												<td>${point.address}</td>
												<td>${point.customsAddress}</td>
											</tr>
										</c:forEach>
									</form:form>
								</table>
							</div>
							<c:choose>
								<c:when test="${route.logistInfo != null}">
									<div class="dateUnloadInfo mt-1">
										<span>Контакты логиста:</span>
										<span>${route.logistInfo}</span>
									</div>
								</c:when>
								<c:otherwise></c:otherwise>
							</c:choose>
							<c:forEach var="order" items="${route.orders}" end="0">
								<input type="hidden" value="${order.control}">
								<c:choose>
									<c:when test="${order.control == true}">
										<div class="dateUnloadInfo mt-1 text-danger">
											<span>
												Перед загрузкой необходимо предоставить фото
												<abbr title="Унифицированный контрольный знак">УКЗ</abbr>
												и
												<abbr title="Средство идентификации">СИ</abbr> логисту
											</span>
										</div>
									</c:when>
								</c:choose>
							</c:forEach>
						</div>
					</div>
				</form:form>
			</div>
		</c:when>

		<c:otherwise>
			<div class="container my-container">
				<input type="hidden" value="6" />
				<div class="table-responsive">
					<h1>Текущие заказы </h1>
					<table class="table table-bordered border-primary table-hover table-condensed">
						<thead class="text-center">
							<tr>
								<th>Название маршрута</th>
								<th>Дата загрузки</th>
								<th>Время загрузки (планируемое)</th>
								<th>Тип прицепа</th>
								<th>Груз</th>
								<th>Комментарии</th>
								<th>Температура</th>
								<th>Объем</th>
								<th>Общее колличество паллет</th>
								<th>Общий вес</th>
								<th>Стоимость перевозки</th>
								<th>Последняя предложенная скидка</th>
								<th>
									<c:choose>
										<c:when test="${route.user != null}">
										</c:when>
										<c:otherwise>
											Предложение
										</c:otherwise>
									</c:choose>
								</th>
							</tr>
							<c:set var="rate" value="${user.rate}" />
						</thead>
						<form:form method="post" action="./tenderUpdate">
							<input type="hidden" value="${route.idRoute}" name="id" />
							<tr>
								<td>${route.routeDirection}</td>
								<td>${route.simpleDateStart}</td>
								<td>${route.timeLoadPreviously}</td>
								<td>${route.typeTrailer}</td>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<td>${RHS.cargo}</td>
								</c:forEach>
								<td>${route.userComments}</td>
								<td>${route.temperature}</td>
								<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
									<td>${RHS.volume}</td>
								</c:forEach>
								<td>${route.totalLoadPall}</td>
								<td>${route.totalCargoWeight}</td>
								<td>${route.cost[rate]}</td>
								<td>${route.finishPrice}</td>
								<td>
									<c:choose>
										<c:when test="${route.user != null}">
										</c:when>
										<c:otherwise>
											<c:choose>
												<c:when test="${route.finishPrice != null}">
													<input type="submit" value="предложить скидку в %" name="agree">
													<input value="${route.finishPrice+5}" name="price" size="1" />%
												</c:when>
												<c:otherwise>
													<input type="submit" value="поддержать цену" name="agree">
													<input type="hidden" value="0" name="price" size="1" />
												</c:otherwise>
											</c:choose>
										</c:otherwise>
									</c:choose>
								</td>
						</form:form>
					</table>
					<br>
					<label>
						<h3>Данные по точкам</h3>
					</label>
					<div class="table-scroll">
						<table class="table table-bordered border-primary table-hover table-condensed">
							<thead class="text-center">
								<tr>
									<th>Номер точки</th>
									<th>Вес</th>
									<th>Паллеты</th>
									<th>Адрес</th>
									<th>Таможня</th>
								</tr>
							</thead>
							<form:form modelAttribute="route" method="post">
								<c:forEach var="point" items="${route.roteHasShop}">
									<tr>
										<td>${point.order}</td>
										<td>${point.weight}</td>
										<td>${point.pall}</td>
										<td>${point.address}</td>
										<td>${point.customsAddress}</td>
									</tr>
								</c:forEach>
							</form:form>
						</table>
					</div>
					<c:choose>
						<c:when test="${route.logistInfo != null}">
							<div class="dateUnloadInfo mt-1">
								<span>Контакты логиста:</span>
								<span>${route.logistInfo}</span>
							</div>
						</c:when>
						<c:otherwise></c:otherwise>
					</c:choose>
					<c:forEach var="order" items="${route.orders}" end="0">
						<input type="hidden" value="${order.control}">
						<c:choose>
							<c:when test="${order.control == true}">
								<div class="dateUnloadInfo mt-1 text-danger">
									<span>
										Перед загрузкой необходимо предоставить фото
										<abbr title="Унифицированный контрольный знак">УКЗ</abbr>
										и
										<abbr title="Средство идентификации">СИ</abbr> логисту
									</span>
								</div>
							</c:when>
						</c:choose>
					</c:forEach>
				</div>
			</div>
		</c:otherwise>
	</c:choose>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<!-- <div class="container">
		<button class="btn btn-outline-secondary" onclick="goBack()">Назад</button>
	</div>
	<script>
		function goBack() {
			window.history.back();
		}
	</script> -->
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/tenderPage.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/device.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>