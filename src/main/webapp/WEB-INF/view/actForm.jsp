<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Редактор актов</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/actForm.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<script src='${pageContext.request.contextPath}/resources/js/popper/popper.js'></script>
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="my-container">
		<input type="hidden" value="${user.numContract}" id="numContractFromServer">
		<!-- <h1>Редактор акта выполненных работ</h1> -->
	
		<!-- <button class="btn btn-outline-info position-absolute" data-toggle="modal" data-target="#helpModal">Помощь</button> -->

		<h5 class="mb-0 text-center">Акт</h5>
		<h5 class="mb-2 text-center">сдачи-приемки выполненных работ на оказание транспортных услуг</h5>

		<form:form method="post" id="getformact">
			<input type="hidden" value="${dateNow}" name="dateOfAct" />
			<div class="d-flex justify-content-between mb-2">
				<div class="font-weight-bold" style="display: inline-block;">${dateNow}</div>
				<div style="display: inline-block; ">
					<input class="header-input" type="text" name="city" placeholder="Город заполнения акта" required>
				</div>
			</div>
			<div class="header-text text-justify">
				Мы, нижеподписавшиеся: Исполнитель 
				<select class="header-input" name="documentType" id="documentType">
					<option value="устава">Юр. лицо</option>
					<option value="свидетельства">Индивидуальный предприниматель</option>
				</select>
				${user.companyName},
				в лице  
				<span id="hiddenBlock1">
					<input class="header-input" type="text" id="directOfOOO" name="directOfOOO" placeholder="ФИО полностью" />
				</span>
				<span id="hiddenBlock2" style="display: none;">
					${user.director} 
				</span>
				действующего на основании 
				<span id="hiddenBlock1-doc">
				    <input class="header-input" type="text" id="docOfOOO" name="docOfOOO" placeholder="Устава, доверенности" />
				</span>
				<span id="hiddenBlock2-doc" style="display: none;">
				    Свидетельства о государственной регистрации индивидуального предпринимателя
				    № <input class="header-input" type="text" id="numOfIP" name="numOfIP" placeholder="Введите номер документа" />
				    от <input class="header-input" type="text" id="dateOfIP" name="dateOfIP" placeholder="Введите дату документа" />
				</span>
				одной стороны, и представитель Заказчика ЗАО «Доброном» в лице
				заместителя генерального директора Якубова Евгения Владимировича,
				действующего на основании доверенности №3 от 31.12.2022 года,
				с другой стороны, составили настоящий акт о том, что услуги,
				оказанные на основании договора перевозки №

				<!-- <select name="" id="">
					<option value="${user.numContract}">${user.numContract}</option>
					<option value="0000000 от 01.01.1999">0000000 от 01.01.1999</option>
				</select> -->
				<input class="header-input" type="text" id="numContract" name="numContract" required style="color: red;"> 
				от
				<input class="header-input" type="text" id="dateContract" name="dateContract" required style="color: red;"> 
				выполнены в полном объеме и стороны	претензий друг к другу не имеют.
			</div>

			<input type="hidden" value="<sec:authentication property="principal.username" />"id="login">
			<div class="form-group">
				<c:out value="${errorMessage}" />
			</div>

			<div class="table-scroll">
				<table id="table" class="table table-bordered m-0">
					<thead class="text-center">
						<tr>
							<th>Дата загрузки</th>
							<th>Дата выгрузки</th>
							<th>№ рейса</th>
							<th>Маршрут</th>
							<th>
								<span data-toggle="tooltip" data-placement="left"
									title='Номер авто и прицепа через "/"'>
									№ ТС
									<sup class="text-danger">?</sup>
								</span>
							</th>
							<th>№ Путевого листа</th>
							<th>
								<span data-toggle="tooltip" data-placement="left"
									title="Номер ТТН или CMR">
									№ ТТН/CMR
									<sup class="text-danger">?</sup>
								</span>
							</th>
							<th>
								<span data-toggle="tooltip" data-placement="left"
								title="Масса груза в тоннах (по документам)">
									Объем груза (тонн)
									<sup class="text-danger">?</sup>
								</span>
							</th>
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
					<c:forEach var="route" items="${routes}" varStatus="loop">
						<tr class="routeRow">
							<input type="hidden" value="${route.idRoute}" name="idRoute" id="idRoute_route${loop.count}" />
							<input type="hidden" value="${isNDS}" name="isNDS" id="isNDS_route${loop.count}" />
							<td>${route.simpleDateStart}</td>
							<td>
								<input class="border-0" type=text name="dateUnload"
									id="dateUnload_route${loop.count}"
									value="${route.dateUnload}"
									required readonly>
							</td>
							<td>${route.idRoute}</td>
							<td style="min-width: 150px;">${route.routeDirection}</td>
							<c:choose>
								<c:when test="${route.truck.numTruck != null}">
									<td>
										<textarea type="text" name="numTruckAndTrailer"
											id="numTruckAndTrailer_route${loop.count}"
											placeholder="Номер тягача и прицепа"
											value="${route.truck.numTruck} / ${route.truck.numTrailer}"
											required></textarea>
									</td>
								</c:when>
								<c:otherwise>
									<td>
										<input type="text" name="numTruckAndTrailer"
											id="numTruckAndTrailer_route${loop.count}"
											placeholder="Номер тягача и прицепа"
											required>
									</td>
								</c:otherwise>
							</c:choose>
							<td>
<!-- 								<input type="text" name="numWayList" -->
								<textarea type="text" name="numWayList"
									id="numWayList_route${loop.count}"
									placeholder="Путевой лист"
									required></textarea>
							</td>
							<td>
								<textarea class="cmr" name="cmr" id="cmr_route${loop.count}" required></textarea>
							</td>
							<td>
								<textarea type="text" name="сargoWeight"
									id="сargoWeight_route${loop.count}"
									placeholder="Вес по документам"
									required></textarea>
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
												<input type="hidden" name="costWay" id="costWay_route${loop.count}" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
													required>
											</td>
										</c:when>
										<c:otherwise>
											<td>
												<input type="text" name="costWay" id="costWay_route${loop.count}" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
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
													required>
											</td>
										</c:when>
										<c:otherwise>
											<td>
												<input type="text" name="costWay" placeholder="платные дороги"
													value="0"
													onkeyup="this.value = this.value.replace(/[A-Za-zА-Яа-яЁё,]/g,'.');"
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
			<div class="d-flex justify-content-between mb-2">
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
				<div class="requisites">
					<p class="mb-0">Исполнитель:</p>
					<textarea class="border-0" name="requisitesCarrier"
						required readonly>${user.requisites}
					</textarea>
				</div>
			</div>
		</form:form>
		<div class="d-flex justify-content-center">
			<input type="submit" value="Сформировать и скачать акт"
				class="btn btn-primary mr-2"
				form="getformact"
				id="get">
			<form:form
				action="${pageContext.request.contextPath}/main/carrier/transportation/routecontrole">
				<input type="submit" value="Назад"
					class="btn btn-secondary">
			</form:form>
		</div>

		<div class="modal fade" id="helpModal" data-keyboard="false" tabindex="-1" aria-labelledby="helpModalLabel" aria-hidden="true">
			<div class="modal-dialog modal-lg">
				<div class="modal-content">
					<div class="modal-header justify-content-center bg-color">
						<h5 class="modal-title" id="helpModalLabel">Помощь</h5>
						<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">

					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</div>
			</div>
		</div>
	</div>

	<jsp:include page="footer.jsp" />

	<script type="module" src="${pageContext.request.contextPath}/resources/js/actForm.js"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>