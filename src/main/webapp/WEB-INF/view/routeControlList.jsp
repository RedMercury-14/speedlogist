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
	<style type="text/css">
		.none {
			display: none;
		}
		.active {
			background: #c4ffe1db;
		}
		.qwe {
			transform: scale(1.6);
			opacity: 0.9;
			cursor: pointer;
		}
		.navbar {
			background-color: #0e377b !important;
		}
		.table-scroll {
			overflow-x: scroll;
			max-height: calc(100vh - 385px);
		}
		.table-scroll .table th {
			padding: 0.25rem 0.5rem;
		}
		.container-fluid {
			font-family: 'Open Sans', sans-serif;
			margin-top: 60px;
		}
		.container-fluid a {
			color: #003369;
		}
		.modal {
			font-family: var(--font-family-sans-serif);
		}
		.modal input::-webkit-outer-spin-button,
		.modal input::-webkit-inner-spin-button {
			-webkit-appearance: none;
			margin: 0;
		}
		.modal input[type=number] {
			appearance: textfield;
			-moz-appearance: textfield;
		}
		.modal input::placeholder {
			color: rgb(179, 179, 179);
		}
		.modal-info {
			text-align: justify;
		}
		footer.to-bottom {
			position: fixed;
			margin-top: 80px;
		}
		@media (max-width: 500px) {
			footer.to-bottom {
				position: relative;
			}
		}
	</style>
	<title>Акты и контроль</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container-fluid">
		<h3 class="mt-2 mb-2">Незавершенные перевозки</h3>
		<p class="mb-2">
			Здесь отображаются незавершенные рейсы. Чтобы завершить рейс, нужно выделить его и
			сформировать отчёт. После того как сформируется отчёт, рейс будет считаться завершенным. Не
			рекомендуется завершать рейс в программе, до непосредственного заверения рейса автомобилем.
		</p>
		<p class="text-info mb-2"><strong>По вопросам формирования актов звонить +375 44 738 36 65 Ольга</strong></p>
		<p class="text-danger mb-2">
			<strong>
				Нельзя создать акт, не прикрепив автомобиль. Авто нужно прикрепить к маршруту в
				<a class="" href="/speedlogist/main/carrier/transportation">Текущих перевозках</a>
			</strong>
		</p>
		<input type="hidden" value="<sec:authentication property="principal.username"/>" id="login">
		<input type="hidden" value='${user.companyName}' id="companyName">
		<input type="hidden" value='${user.propertySize}' id="propertySize">
		<input type="hidden" value='${user.registrationCertificate}' id="registrationCertificate">
		<div class="form-group">
			<h2 style="color: red;">
				<c:out value="${errorMessage}" />
			</h2>
		</div>
		<form method="get" action="routecontrole/getformact" id="getformact">
			<div class="d-flex align-items-center mb-2">
				<input class="btn btn-primary mr-3" type="submit" value="Сформировать отчёт" form="getformact">
				<div class="" id="myForm">
					<label>С НДС <input class="withNDS" type="radio" name="isNDS" value="${true}" required></label>
					<label style="padding-left: 50px">Без НДС <input class="withoutNDS" type="radio" name="isNDS" value="${false}" required></label>
				</div>
			</div>
			<div class="table-scroll">
				<table class="table table-bordered border-primary table-hover table-condensed"id="sort">
					<thead class="text-center">
						<tr>
							<th></th>
							<th>Название маршрута
								<input type="text" id="directionSearch" onkeyup="directionSearch1()" placeholder="Поиск по названиям">
							</th>
							<th>Дата выгрузки</th>
							<th>Машина</th>
							<th>Водитель</th>
							<th>Дата загрузки</th>
							<th>Время загрузки</th>
							<th>Груз</th>
							<th>Температура</th>
							<th>Общее колличество паллет</th>
							<th>Общий вес</th>
							<th>Стоимость перевозки</th>
							<th>Колличество точек</th>
						</tr>
					</thead>
					<c:forEach var="route" items="${routes}">
						<%-- <input type="hidden" value="${route.idRoute}" name="id" /> --%>
						<tr class="route" data-way="${route.way}">
							<td class="none" id="routeDirection">${route.routeDirection}</td>
							<td>
								<c:choose>
									<c:when test="${route.expeditionCost != null && route.expeditionCost != 0}">
										<input type="checkbox"
											name="targetAct"
											value="${route.idRoute}"
											class="qwe expeditionRouteCheckbox"
											data-way="${route.way}"
										/>
									</c:when>
									<c:otherwise>
										<input type="checkbox"
											name="targetAct"
											value="${route.idRoute}"
											class="qwe defaultRouteCheckbox"
											data-way="${route.way}"
										/>
									</c:otherwise>
								</c:choose>
							</td>
							<td>${route.routeDirection}</td>
							<td><input type="date" name="dateUnload" disabled="disabled" id="${route.idRoute}" required></td>
							<td class="none" id="idTarget">${route.idRoute}</td>
							<td id="numTruck">${route.truck.numTruck} / ${route.truck.numTrailer}</td>
							<td>${route.driver.surname} ${route.driver.name}</td>
							<td>${route.simpleDateStart}</td>
							<td>${route.timeLoadPreviously}</td>
							<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
								<td>${RHS.cargo}</td>
							</c:forEach>
							<td>${route.temperature}</td>
							<td>${route.totalLoadPall}</td>
							<td>${route.totalCargoWeight}</td>
							<td>
								${route.finishPrice} ${route.startCurrency}
								<c:choose>
									<c:when test="${route.expeditionCost != null && route.expeditionCost != 0}">
										<div class="expeditionCost">
											Из них экспедиторские услуги: ${route.expeditionCost} ${route.startCurrency}
										</div>
									</c:when>
								</c:choose>
							</td>
							<td>${route.numPoint}</td>
						</tr>
					</c:forEach>
				</table>
			</div>
		</form>
		<form:form action="${pageContext.request.contextPath}/main/carrier">
			<input class="btn btn-secondary my-2" type="submit"value="Назад">
		</form:form>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<!-- Модальное окно с требованием указать номер сертификата -->
	<div class="modal fade mt-5" id="regCertificateModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="regCertificateModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h4 class="modal-title" id="regCertificateModalLabel">Введите данные</h4>
				</div>
				<form id="addRegCertificateForm" action="">
					<div class="modal-body">
						<input type="hidden" class="form-control" name="login" id="login" value="${user.login}">
						<p class="modal-info">
							Для формирования актов о перевозках необходимо указать номер 
							<span class="font-weight-bold">свидетельства о регистрации ИП.</span>
							Данные будут сохранены в Ваш 
							аккаунт на данной платформе для дальнейшего использования в 
							при формировании актов.
						</p>
						<div class="form-group row">
							<div class="col-sm-6">
								<input class="form-control" name="registrationCertificate_num" id="registrationCertificate_num" type="number" placeholder="Номер" required />
							</div>
							<label class="col-sm-1 col-form-label">от</label>
							<div class="col-sm-4">
								<input class="form-control" name="registrationCertificate_date" id="registrationCertificate_date" type="date" required />
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary">Сохранить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<jsp:include page="footer.jsp" />
	<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/routeControlList.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/tenderNotifications.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script type="text/javascript">
		console.log('trst')
		function directionSearch1() {
			var input, filter, table, tr, td, i, txtValue;
			input = document.getElementById("directionSearch");
			filter = input.value.toUpperCase();
			table = document.getElementById("sort");
			tr = table.getElementsByTagName("tr");
			for (i = 0; i < tr.length; i++) {
				td = tr[i].getElementsByTagName("td")[2];
				if (td) {
					txtValue = td.textContent || td.innerText;
					if (txtValue.toUpperCase().indexOf(filter) > -1) {
						tr[i].style.display = "";
					} else {
						tr[i].style.display = "none";
					}
				}
			}
		}
	</script>
</body>
</html>