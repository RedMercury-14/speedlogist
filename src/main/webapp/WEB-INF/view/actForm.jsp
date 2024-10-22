<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<style type="text/css">
		.none {
			display: none;
		}
		.navbar {
			background-color: #0e377b !important;
		}
		.my-container {
			margin-top: 83px;
			font-family: var(--font-family-sans-serif);
			margin-bottom: 80px;
		}
		.my-container .table td {
			padding: 0.25rem;
		}
		.active {
			background: #c4ffe1db;
		}
		::-webkit-input-placeholder {
			color: red;
		}
		:-moz-placeholder {
			color: red;
		}
		::-moz-placeholder {
			color: red;
		}
		:-ms-input-placeholder {
			color: red;
		}
		#documentType {
			color: red;
		}
		#documentType option {
			color: #000;
		}
		.requisites {
			width: 320px;
			height: 300px;
			max-width: 320px;
			display: inline-block;
		}
		thead tr {
			font-weight: bold;
		}
		.table-bordered .whiteBorderRight {
			border-right-color: #fff;
		}
		@media (max-width: 1534px) {
			.table-scroll {
				overflow-x: scroll;
			}
		}
		footer.to-bottom {
			margin-top: 51px;
			position: fixed;
		}
	</style>
	<title>Редактор актов</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container-fluid my-container">
		<input type="hidden" value="${user.numContract}" id="numContractFromServer">
		<!-- <h1>Редактор акта выполненных работ</h1> -->
		<div align="center">
			<h3 class="mb-0">Акт</h3>
		</div>
		<div align="center">
			<h3 class="mb-0">сдачи-приемки выполненных работ на оказание транспортных услуг</h3>
		</div>
		<form:form method="post" id="getformact">
			<input type="hidden" value="${dateNow}" name="dateOfAct" />
			<div class="d-flex justify-content-between mb-1">
				<div style="display: inline-block;">${dateNow}</div>
				<div style="display: inline-block; ">
					<input type="text" name="city" placeholder="Город заполнения акта" required>
				</div>
			</div>
			<div>
				Мы, нижеподписавшиеся: представитель Перевозчика ${user.companyName},
				в лице директора ${user.director} действующего на основании Устава
				<!-- <select name="documentType" id="documentType">
					<option value="Устава">Устава</option>
					<option value="Свидетельства">Свидетельства</option>
				</select> -->
				одной стороны, и представитель Заказчика ЗАО «Доброном» в лице
				заместителя генерального директора Якубова Евгения Владимировича,
				действующего на основании доверенности №3 от 31.12.2022 года,
				с другой стороны, составили настоящий акт о том, что услуги,
				оказанные на основании договора перевозки №
				<!-- <select name="" id="">
					<option value="${user.numContract}">${user.numContract}</option>
					<option value="0000000 от 01.01.1999">0000000 от 01.01.1999</option>
				</select> -->
				<input type="text" name="numContract" required readonly> 
				от
				<input type="text" name="dateContract" required readonly> 
				выполнены в полном объеме и стороны	претензий друг к другу не имеют.
			</div>
			<input type="hidden" value="<sec:authentication property="principal.username" />"id="login">
			<div class="form-group">
				<c:out value="${errorMessage}" />
			</div>
			<div class="table-scroll">
				<table class="table table-bordered">
					<thead class="text-center">
						<tr>
							<th>Дата загрузки</th>
							<th>Дата выгрузки</th>
							<th>№ рейса</th>
							<th>Маршрут</th>
							<th>№ ТС</th>
							<th>№ Путевого листа</th>
							<th>№ ТТН/CMR</th>
							<th>Объем Груза (тонн)</th>
							<th>Сумма без НДС</th>
							<th>Сумма НДС</th>
							<c:forEach var="route" items="${routes}" end="0">
								<c:choose>
									<c:when test="${route.way == 'Импорт'}">
										<th class="p-0"></th>
									</c:when>
									<c:otherwise>
										<th>Платные дороги</th>
									</c:otherwise>
								</c:choose>
							</c:forEach>
							<th>Сумма c НДС</th>
						</tr>
					</thead>
					<c:forEach var="route" items="${routes}">
						<input type="hidden" value="${route.idRoute}" name="idRoute" />
						<input type="hidden" value="${isNDS}" name="isNDS" />
						<tr>
							<td>${route.simpleDateStart}</td>
							<td>
								<input type=text name="dateUnload" value="${route.dateUnload}"
									style="width: 100px;"
									required readonly>
							</td>
							<td>${route.idRoute}</td>
							<td style="min-width: 150px;">${route.routeDirection}</td>
							<c:choose>
								<c:when test="${route.truck.numTruck != null}">
									<td>
										<input type="text" name="numTruckAndTrailer"
											placeholder="Номер тягача и прицепа"
											value="${route.truck.numTruck} / ${route.truck.numTrailer}"
											required>
									</td>
								</c:when>
								<c:otherwise>
									<td>
										<input type="text" name="numTruckAndTrailer"
											placeholder="Номер тягача и прицепа" required>
									</td>
								</c:otherwise>
							</c:choose>
							<td>
								<input type="text" name="numWayList" placeholder="Путевой лист"
									style="width: 155px;"
									required>
							</td>
							<td>
								<textarea name="cmr" id="cmr" required></textarea>
							</td>
							<td>
								<input type="text" name="сargoWeight" placeholder="Вес по документам"
									style="width: 155px;"
									required>
							</td>
							<c:choose>
								<c:when test="${route.expeditionCost != null && route.expeditionCost != 0}">
									<td>${route.finishPrice - route.expeditionCost} ${route.startCurrency}</td>
								</c:when>
								<c:otherwise>
									<td>${route.finishPrice} ${route.startCurrency}</td>
								</c:otherwise>
							</c:choose>
							<c:choose>
								<c:when test="${isNDS}">
									<td>${route.nds} ${route.startCurrency}</td>
									<c:choose>
										<c:when test="${route.way == 'Импорт'}">
											<td class="p-0">
												<input type="hidden" name="costWay" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
													style="width: 100px;"
													required>
											</td>
										</c:when>
										<c:otherwise>
											<td>
												<input type="text" name="costWay" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
													style="width: 100px;"
													required>
											</td>
										</c:otherwise>
									</c:choose>
									<td>${route.finishPrice + route.nds} ${route.startCurrency}</td>
								</c:when>
								<c:otherwise>
									<td>0</td>
									<c:choose>
										<c:when test="${route.way == 'Импорт'}">
											<td class="p-0">
												<input type="hidden" name="costWay" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
													style="width: 100px;"
													required>
											</td>
										</c:when>
										<c:otherwise>
											<td>
												<input type="text" name="costWay" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
													style="width: 100px;"
													required>
											</td>
										</c:otherwise>
									</c:choose>
									<c:choose>
										<c:when test="${route.expeditionCost != null && route.expeditionCost != 0}">
											<td>${route.finishPrice - route.expeditionCost} ${route.startCurrency}</td>
										</c:when>
										<c:otherwise>
											<td>${route.finishPrice} ${route.startCurrency}</td>
										</c:otherwise>
									</c:choose>
								</c:otherwise>
							</c:choose>
						</tr>
						<c:choose>
							<c:when test="${route.way == 'Импорт'}">
								<tr class="expedition">
									<td class="whiteBorderRight">
										<span class="font-weight-bold position-absolute">Услуги экспедитора:</span>
									</td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td></td>
									<td>${route.expeditionCost} ${route.startCurrency}</td>
									<td>0</td>
									<td class="p-0"></td>
									<td>${route.expeditionCost} ${route.startCurrency}</td>
								</tr>
								<tr class="expedition">
									<td class="whiteBorderRight">
										<span class="font-weight-bold position-absolute">Итого:</span>
									</td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td class="whiteBorderRight"></td>
									<td></td>
									<td>${route.finishPrice} ${route.startCurrency}</td>
									<td>0</td>
									<td class="p-0"></td>
									<td>${route.finishPrice} ${route.startCurrency}</td>
								</tr>
							</c:when>
					</c:choose>
					</c:forEach>
				</table>
			</div>
			<div class="requisites">
				<p class="mb-0">Заказчик:</p>
				ЗАО Доброном: Республика Беларусь,
				220112, г. Минск, ул. Янки Лучины, 5
				р/с BY61ALFA30122365100050270000 ( BYN) открытый в Закрытое акционерное общество
				«Альфа-банк»
				Юридический адрес: Ул. Сурганова, 43-47 220013 Минск, Республика Беларусь
				УНП 101541947
				Closed Joint-Stock Company «Alfa-Bank»
				SWIFT – ALFABY2X
				р/с BY24ALFA30122365100010270000 (USD)
				р/с BY09ALFA30122365100020270000(EUR)
				р/с BY91 ALFA 3012 2365 1000 3027 0000 (RUB.)
			</div>
			<div class="requisites" style="float: right;">
				<p class="mb-0">Перевозчик:</p>
				<textarea name="requisitesCarrier" style="height:250px; width: 320px" required
					readonly>${user.requisites}
				</textarea>
			</div>
		</form:form>
		<div align="center">
			<input type="submit" value="Сформировать и скачать акт"
				class="btn btn-lg btn-primary"
				form="getformact"
				onclick="if (!(confirm('После формирования и отправки акта, маршрут будет считаться завершенным. Он пропадёт из списка перевозок. Вы уверены что хотите завершить маршрут?'))) return false"
				id="get">
		</div>
		<br>
		<div align="center">
			<form:form
				action="${pageContext.request.contextPath}/main/carrier/transportation/routecontrole">
				<input type="submit" value="Назад"
					class="btn btn-secondary">
			</form:form>
		</div>
	</div>
	<jsp:include page="footer.jsp" />
	<script type="module" src="${pageContext.request.contextPath}/resources/js/actForm.js"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>