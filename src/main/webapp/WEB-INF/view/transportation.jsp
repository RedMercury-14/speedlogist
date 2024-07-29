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
	<title>Текущие маршруты</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/transportation.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrapSelect/bootstrapSelect.css">
	<script src='${pageContext.request.contextPath}/resources/js/popper/popper.js'></script>
</head>
<body class="no-scroll">
	<jsp:include page="headerNEW.jsp" />
	<div id="overlay" class="">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>
	<div class="container-fluid px-1" style="margin-top: 80px;">
		<div class="d-flex justify-content-between">
			<h1 class="my-1 px-3">Текущие маршруты</h1>
			<!-- <div class="toggler-container d-flex align-items-end">
				<label>
					<span class="text-muted font-weight-bold">Поиск в списках</span>
					<input class="toggler" id="searchInSelectToggler" type="checkbox"/>
				</label>
			</div> -->
		</div>
		<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
		<input type="hidden" value='${user.companyName}' id="companyName">
		<div class="form-group mb-0">
			<div style="color: red;">
				<c:out value="${errorMessage}" />
			</div>
		</div>
		<div class="table-scroll">
			<table class="table table-bordered table-hover table-sm" id="sort">
				<thead class="text-center">
					<tr>
						<th class="name-col">
							Название маршрута
							<!-- <input type="text" id="directionSearch" onkeyup="directionSearch()" placeholder="Поиск по названиям"> -->
						</th>
						<th>Машина</th>
						<th>Водитель</th>
						<th>Информация</th>
						<th class="date-col">Дата и время выгрузки</th>
						<th class="price-col">Стоимость перевозки</th>
						<th class="date-col">Дата и время подачи машины</th>
						<th class="date-col">Дата и время выгрузки</th>
						<th class="control-col">Управление</th>
					</tr>
				</thead>
				<c:forEach var="route" items="${routes}">
					<form:form method="post" action="transportation/update">
						<input type="hidden" value="${route.idRoute}" name="id" />
						<c:url var="showTenderPage" value="/main/carrier/transportation/tenderpage">
							<c:param name="routeId" value="${route.idRoute}" />
						</c:url>
						<tr>
							<td class="none" id="routeDirection">${route.routeDirection}</td>
							<td><strong><a class="text-primary" href="${showTenderPage}">${route.routeDirection}</a></strong></td>
							<td class="none" id="idTarget">${route.idRoute}</td>
							<td>
								<c:choose>
									<c:when test="${route.truck != null}">
										<c:choose>
											<c:when test="${route.truck.numTrailer != null}">
												${route.truck.numTruck}/${route.truck.numTrailer}
											</c:when>
											<c:otherwise>
												${route.truck.numTruck}
											</c:otherwise>
										</c:choose>
									</c:when>
									<c:otherwise>
										<select id="isTruck" name="isTruck" class="form-control" required>
											<option></option>
											<option value="addTruck">+ Добавить машину</option>
											<c:forEach var="truck" items="${trucks}">
												<option value="<c:out value=" ${truck.idTruck}" />">
													<c:choose>
														<c:when test="${truck.numTrailer != null}">
															<c:out value="${truck.numTruck}/${truck.numTrailer}" />
														</c:when>
														<c:otherwise>
															<c:out value="${truck.numTruck}" />
														</c:otherwise>
													</c:choose>
												</option>
											</c:forEach>
										</select>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${route.driver != null}">
										${route.driver.surname} ${route.driver.name}
									</c:when>
									<c:otherwise>
										<select id="isDriver" name="isDriver" class="form-control" required>
											<option></option>
											<option value="addDriver">+ Добавить водителя</option>
											<c:forEach var="driver" items="${drivers}">
												<option value="<c:out value=" ${driver.idUser}" />">
													<c:out value="${driver.name}  ${driver.surname}" />
												</option>
											</c:forEach>
										</select>
									</c:otherwise>
								</c:choose>
							</td>
							<td class="info-cell">
								<c:choose>
									<c:when test="${route.loadNumber != null}">
										<div class="font-weight-bold">
											<span>Погрузочный номер:</span>
											<span>${route.loadNumber}</span>
										</div>
									</c:when>
								</c:choose>
								<div><span class="text-muted">Дата загрузки: </span>${route.simpleDateStart}</div>
								<div><span class="text-muted">Время загрузки: </span>${route.timeLoadPreviously}</div>
								<div>
									<span class="text-muted">Груз: </span>
									<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
										${RHS.cargo}
									</c:forEach>
								</div>
								<div><span class="text-muted">Температура: </span>${route.temperature}</div>
								<div><span class="text-muted">Паллеты: </span>${route.totalLoadPall}</div>
								<div><span class="text-muted">Масса: </span>${route.totalCargoWeight}</div>
								<div><span class="text-muted">Колличество точек: </span>${route.numPoint}</div>
							</td>
							<td class="text-center font-weight-bold">${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</td>
							<td class="text-center">
								<div>
									${route.finishPrice} ${route.startCurrency}
								</div>
								<c:choose>
									<c:when test="${route.way == 'Импорт'}">
										<c:choose>
											<c:when test="${route.driver == null}">
												<div class="pt-1 text-danger">Укажите стоимость экспедиторских услуг:</div>
												<input type="number" class="form-control mt-1" name="expeditionCost" id="expeditionCost" min="0" max="${route.finishPrice}">
												<span>${route.startCurrency}</span>
											</c:when>
											<c:when test="${route.expeditionCost != null}">
												<div class="pt-1">Стоимость экспедиторских услуг:</div>
												<div class="pt-1">${route.expeditionCost} ${route.startCurrency}</div>
											</c:when>
										</c:choose>
									</c:when>
								</c:choose>
							</td>
							<td>
								<input type="date" name="dateLoadActually" id="dateLoadActually" min="${route.dateLoadPreviously}" value="${route.dateLoadActually}" class="form-control" required="true">
								<script type="text/javascript">
									var object = document.querySelector('#dateLoadActually');
									if (object.value != null && object.value != "") {
										object.readOnly = true;
									}
								</script>
								<input type="time" name="timeLoadActually" id="timeLoadActually" value="${route.timeLoadActually}" class="form-control" required="true">
								<script type="text/javascript">
									var object = document.querySelector('#timeLoadActually');
									if (object.value != null && object.value != "") {
										object.readOnly = true;
									}
								</script>
							</td>
							<td>
								<c:choose>
									<c:when test="${route.way == 'РБ' && route.dateUnloadPreviouslyStock != null && route.timeUnloadPreviouslyStock != null}">
										<div class="font-weight-bold mb-1">Слот на выгрузку:</div>
										<input type="date" name="dateUnloadActually" id="dateUnloadActually" value="${route.dateUnloadPreviouslyStock}" class="form-control" required readonly>
										<input type="time" name="timeUnloadActually" id="timeUnloadActually" value="${route.timeUnloadPreviouslyStock}" class="form-control" required readonly>
										<div class="text-danger mt-1">Необходимо прибыть строго в назначенное время!</div>
									</c:when>
									<c:otherwise>
										<input type="date" name="dateUnloadActually" id="dateUnloadActually" min="${route.dateLoadPreviously}" value="${route.dateUnloadActually}" class="form-control" required="true">
										<script type="text/javascript">
											var object = document.querySelector('#dateUnloadActually');
											if (object.value != null && object.value != "") {
												object.readOnly = true;
											}
										</script>
										<input type="time" name="timeUnloadActually" id="timeUnloadActually" value="${route.timeUnloadActually}" class="form-control" required="true">
										<script type="text/javascript">
											var object = document.querySelector('#timeUnloadActually');
											if (object.value != null && object.value != "") {
												object.readOnly = true;
											}
										</script>
									</c:otherwise>
								</c:choose>
							</td>
							<td>
								<c:choose>
									<c:when test="${route.driver !=null}">
										<!-- 						<input type="submit" value="Изменить машину и водителя" name="revers"> -->
									</c:when>
									<c:otherwise>
										<input type="submit" class="btn btn-success" value="Подтвердить машину" name="update">
									</c:otherwise>
								</c:choose>
								<input type="button" class="btn btn-warning" value="Отправить статус" id="status">
								<select id="option" class="form-control">
									<!-- <option></option> -->
									<option value="" disabled selected>Выберите статус</option>
									<option value="Подача_машины">Подача машины</option>
									<option value="На_месте_зазгрузки">На месте зазгрузки</option>
									<option value="Начали_загружать">Начали загружать</option>
									<option value="Загружена">Загружена</option>
									<option value="На_таможне_отправления">На таможне отправления</option>
									<option value="Затаможена">Затаможена</option>
									<option value="В_пути">В пути</option>
									<option value="Проходит_границу">Проходит границу</option>
									<option value="На_таможне_назначения">На таможне назначения</option>
									<option value="Растаможена">Растаможена</option>
									<option value="На_выгрузке">На выгрузке</option>
								</select>
							</td>
						</tr>
					</form:form>
				</c:forEach>
			</table>
		</div>
		<div id="snackbar"></div>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<!-- Модальное окно для добавления машины -->
	<div class="modal fade" id="truckModal" tabindex="-1" aria-labelledby="truckModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="truckModalLabel">Создать новую машину</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="createTruckForm" name="createTruckForm" action="" class="needs-validation" novalidate>
					<div class="form-container">
						<div class="modal-body">
							<h3>Машина</h3>
							<div class="form-group">
								<input type="text" class="form-control" name="brandTruck" id="brandTruck" placeholder="Марка автомобиля" required>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" name="modelTruck" id="modelTruck" placeholder="Модель автомобиля" required>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" name="numTruck" id="numTruck" placeholder="Государственный номер" required>
								<div class="error-message" id="messageNumTruck"></div>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" name="ownerTruck" id="ownerTruck" placeholder="Принадлежность ТС" required>
								<div class="remark text-muted">Пример: ООО "КаргоГрузПроектТранс"</div>
							</div>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numTrailer" id="numTrailer" placeholder="Номер прицепа">
								<input type="text" class="form-control" name="brandTrailer" id="brandTrailer" placeholder="Марка прицепа">
							</div>
							<div class="form-group">
								<select id="number_axes" name="number_axes" class="form-control" required>
									<option value="" disabled selected>Выберите количество осей</option>
									<option>2 оси</option>
									<option>3 оси</option>
									<option>4 и более осей</option>
								</select>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold" for="technical_certificate">Технический паспорт</label>
								<div class="form-group row-container">
									<input type="text" class="form-control" name="technical_certificate_1" id="technical_certificate_1" placeholder="Серия" required>
									<input type="number" class="form-control" name="technical_certificate_2" id="technical_certificate_2" placeholder="Номер" required>
								</div>
								<div class="form-group row-container">
									<input type="text" class="form-control" name="technical_certificate_3" id="technical_certificate_3" placeholder="Кем выдан" required>
									<span class="date-label">от</span>
									<input type="date" class="form-control" name="technical_certificate_4" id="technical_certificate_4" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Фото технического паспорта</label>
								<input type="file" class="form-control btn btn-outline-secondary" name="technical_certificate_file" id="technical_certificate_file" accept=".png, .jpg, .jpeg" required>
							</div>
							<div id="truckImageContainer"></div>
						</div>
						<div class="separationLine"></div>
						<div class="modal-body">
							<h3>Кузов</h3>
							<div class="form-group">
								<select id="typeTrailer" name="typeTrailer" class="form-control" required>
									<option value="" disabled selected>Выберите тип кузова</option>
									<option>Открытый</option>
									<option>Тент</option>
									<option>Изотермический</option>
									<option>Мебельный фургон</option>
									<option>Рефрижератор</option>
								</select>
							</div>
							<div class="form-group">
								<select id="hitch_type" name="hitch_type" class="form-control" required>
									<option value="" disabled selected>Выберите тип сцепки</option>
									<option>Грузовик</option>
									<option>Полуприцеп</option>
									<option>Сцепка</option>
								</select>
							</div>
							<div class="form-group">
								<select id="type_of_load" name="type_of_load" class="form-control" required>
									<option value="" disabled selected>Выберите тип загрузки</option>
									<option>Задняя</option>
									<option>Боковая</option>
									<option>Задняя+боковая</option>
									<option>Полная растентовка</option>
								</select>
							</div>
							<div class="form-group position-relative">
								<input type="number" class="form-control" id="cargoCapacity" name="cargoCapacity" placeholder="Грузоподъёмность, т" min="1" max="22" step="0.01" required>
								<div class="invalid-feedback">
									Максимальное значение: 22
								</div>
							</div>
							<div class="form-group position-relative">
								<input type="number" class="form-control" id="volume_trailer" name="volume_trailer" placeholder="Объем, м. куб" min="1" max="120" step="1" required>
								<div class="invalid-feedback">
									Максимальное значение: 120, только целые числа
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold" for="dimensions_1">Внутренние габариты кузова (Д/Ш/В), м</label>
								<div class="form-group row-container dimensions-container">
									<input type="number" class="form-control" id="dimensions_1" name="dimensions_1" placeholder="Длина, м" min="0" max="20" step="0.01" required>
									<input type="number" class="form-control" id="dimensions_2" name="dimensions_2" placeholder="Ширина, м" min="0" max="20" step="0.01" required>
									<input type="number" class="form-control" id="dimensions_3" name="dimensions_3" placeholder="Высота, м" min="0" max="20" step="0.01" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold" for="pallCapacity">
									Паллетовместимость кузова
								</label>
								<input type="number" class="form-control" id="pallCapacity" name="pallCapacity" placeholder="Количество европаллет 1200*800" min="0" step="1" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Доп. информация</label>
								<div class="form-group info-container">
									<div class="form-check">
										<label class="form-check-label" for="check_1">Гидроборт</label>
										<input class="form-check-input" type="checkbox" value="Гидроборт" name="check_1" id="check_1">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_2">GPS-навигация</label>
										<input class="form-check-input" type="checkbox" value="GPS-навигация" name="check_2" id="check_2">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_3">Ремни</label>
										<input class="form-check-input" type="checkbox" value="Ремни" name="check_3" id="check_3">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_4">Стойки</label>
										<input class="form-check-input" type="checkbox" value="Стойки" name="check_4" id="check_4">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_5">Пневмоподушки</label>
										<input class="form-check-input" type="checkbox" value="Пневмоподушки" name="check_5" id="check_5">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить машину</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для добавления водителя -->
	<div class="modal fade" id="driverModal" tabindex="-1" aria-labelledby="driverModalLabel" aria-hidden="true">
		<div class="modal-dialog ">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="driverModalLabel">Добавить водителя</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="createDriverForm" action="" class="needs-validation" novalidate>
					<div class="modal-body">
						<div class="form-group">
							<input type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия" required>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="name" id="name" placeholder="Имя" required>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="patronymic" id="patronymic" placeholder="Отчество">
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="tel" id="tel" placeholder="Телефон" required>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold" for="numpass">Паспортные данные</label>
							<div class="form-group row-container numpass-container">
								<input type="text" class="form-control" name="numpass_1" id="numpass_1" placeholder="Серия" required>
								<input type="number" class="form-control" name="numpass_2" id="numpass_2" placeholder="Номер" required>
							</div>
							<div class="form-group row-container numpass-container">
								<input type="text" class="form-control" name="numpass_3" id="numpass_3" placeholder="Кем выдан" required>
								<span class="date-label">от</span>
								<input type="date" class="form-control" name="numpass_4" id="numpass_4" required>
							</div>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold" for="numdrivercard">Водительское удостоверение</label>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numdrivercard_1" id="numdrivercard_1" placeholder="Серия" required>
								<input type="number" class="form-control" name="numdrivercard_2" id="numdrivercard_2" placeholder="Номер" required>
							</div>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numdrivercard_3" id="numdrivercard_3" placeholder="Кем выдан" required>
								<span class="date-label">от</span>
								<input type="date" class="form-control" name="numdrivercard_4" id="numdrivercard_4" required>
							</div>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold">Фото водительского удостоверения</label>
							<input type="file" class="form-control btn btn-outline-secondary" name="drivercard_file" id="drivercard_file" accept=".png, .jpg, .jpeg" required>
						</div>
						<div id="driverImageContainer"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить водителя</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/js/bootstrapSelect/bootstrapSelect.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/bootstrapSelect/defaults-ru_RU.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/transportation.js' type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
	<script type="text/javascript">
		function directionSearch() {
			var input, filter, table, tr, td, i, txtValue;
			input = document.getElementById("directionSearch");
			filter = input.value.toUpperCase();
			table = document.getElementById("sort");
			tr = table.getElementsByTagName("tr");
			for (i = 0; i < tr.length; i++) {
				td = tr[i].getElementsByTagName("td")[1];
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