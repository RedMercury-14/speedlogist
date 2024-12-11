<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%> 
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%> 
<%@ taglib uri="http://www.springframework.org/security/tags" 
	prefix="sec"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%> 
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<meta name="role" content="${roles}" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<title>Менеджер маршрутов АХО/СГИ</title>
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/maintenanceList.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<script src="${pageContext.request.contextPath}/resources/js/sortableJS/sortable.min.js"></script>
</head>
<body id="body">
	<jsp:include page="headerNEW.jsp" />		
	<sec:authorize access="isAuthenticated()">  
        	<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
    	</sec:authorize>
		<input type="hidden" value="${roles}" id="role">
	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>
	<div class="fluid-container my-container">
		<div class="title-container">
			<strong><h3>Менеджер маршрутов АХО/СГИ</h3></strong>
		</div>

		<div class="accordion">
			<div class="search-form-container">
				<button class="accordion-btn collapsed" data-toggle="collapse" href="#routeSearchForm" role="button" aria-expanded="true" aria-controls="routeSearchForm">
					Поиск заявок
				</button>
				<form class="collapse" action="" id="routeSearchForm">
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">С</label>
						<input class="form-control" type="date" name="date_from" id="date_from" required>
					</div>
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">по</label>
						<input class="form-control" type="date" name="date_to" id="date_to" required>
					</div>
					<button class="btn btn-outline-secondary" type="submit">Отобразить</button>
				</form>
			</div>
		</div>

		<div id="myGrid" class="ag-theme-alpine"></div>

		<div id="snackbar"></div>
	</div>


	<!-- Модальное окно создания заявки АХО -->
	<div class="modal fade" id="addAhoRouteModal" tabindex="-1" aria-labelledby="addAhoRouteModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header align-items-center text-white">
					<h5 class="modal-title" id="addAhoRouteModalLabel">Создание заявки на перевозку АХО</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addAhoRouteForm" name="addAhoRouteForm" action="">
					<div class="modal-body">
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Название маршрута:</span>
							<input type="text" class="form-control" name="routeDirection" id="routeDirection">
						</div>
						<div class="input-row-container form-group">
							<div class="input-column-container">
								<span class="text-muted font-weight-bold">Дата загрузки</span>
								<input type="date" class="form-control" name="dateLoadPreviously" id="dateLoadPreviously" required>
							</div>
							<div class="input-column-container">
								<span class="text-muted font-weight-bold">Время загрузки</span>
								<input list="times" type="time" id="timeLoadPreviously" name="timeLoadPreviously" class="form-control" required>
							</div>
							<div class="input-column-container">
								<span class="text-muted font-weight-bold">Кол-во паллет, шт</span>
								<input type="number" id="loadPallTotal" name="loadPallTotal" class="form-control" required>
							</div>
							<div class="input-column-container">
								<span class="text-muted font-weight-bold text-nowrap">Масса груза, кг</span>
								<input type="number" id="cargoWeightTotal" name="cargoWeightTotal" class="form-control" required>
							</div>
						</div>
						<div class="input-column-container form-group">
							<span class="text-muted font-weight-bold text-nowrap">Информация о грузе</span>
							<textarea type="text" class="form-control" rows="2" name="cargoInfo" id="cargoInfo" required
							placeholder="Информация о грузе"></textarea>
						</div>
						<div class="input-row-container align-items-center form-group">
							<span class="text-muted font-weight-bold text-nowrap">Тип транспорта:</span>
							<select id="typeTrailer" name="typeTrailer" class="form-control" required>
								<option value="" hidden disabled selected>Выберите тип транспорта</option>
								<option>Открытый</option>
								<option>Тент</option>
								<option>Изотермический</option>
								<option>Мебельный фургон</option>
								<option>Рефрижератор</option>
							</select>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Требования к транспорту:</span>
							<textarea type="text" class="form-control" rows="4" name="truckInfo" id="truckInfo" required
							placeholder="всё касательно машины (длинна, высота, гидроборт) и способ загрузки (задняя боковая верхняя)"></textarea>
						</div>
						<div class="input-row-container form-group">
							<div class="input-column-container">
								<span class="text-muted font-weight-bold">Дата доставки</span>
								<input type="date" class="form-control" name="dateUnloadPreviouslyStock" id="dateUnloadPreviouslyStock" required>
							</div>
							<div class="input-column-container">
								<span class="text-muted font-weight-bold">Время доставки</span>
								<input list="times" type="time" id="timeUnloadPreviouslyStock" name="timeUnloadPreviouslyStock" class="form-control" required>
							</div>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Маршрут:</span>
							<textarea type="text" class="form-control" rows="6" name="userComments" id="userComments" required
							placeholder="маршрут и время работы точек"></textarea>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
						<button class="btn btn-primary" type="submit">Создать заявку</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно редактирования заявки АХО -->
	<div class="modal fade" id="editAhoRouteModal" tabindex="-1" aria-labelledby="editAhoRouteModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header align-items-center text-white">
					<h5 class="modal-title" id="editAhoRouteModalLabel">Редактирование заявки на перевозку АХО</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editAhoRouteForm" name="editAhoRouteForm" action="">
					<div class="modal-body">
						<input type="hidden" id="idRoute" name="idRoute">
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Название маршрута:</span>
							<input type="text" class="form-control" name="routeDirection" id="routeDirection" required>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Маршрут:</span>
							<textarea type="text" class="form-control" rows="6" name="routeInfo" id="routeInfo" required></textarea>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
						<button class="btn btn-primary" type="submit">Подтвердить изменения</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно редактирования точек маршрута АХО -->
	<div class="modal fade" id="editPointsModal" tabindex="-1" data-backdrop="static" data-keyboard="false" aria-labelledby="editPointsModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header align-items-center text-white">
					<h3 class="modal-title" id="editPointsModalLabel">Редактирование точек маршрута</h3>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="editPointsForm" name="editPointsForm" action="">
					<div class="modal-body">
						<input type="hidden" id="idRoute" name="idRoute">
						<div class="" id="pointList"></div>
						<div class="d-flex">
							<div class="">
								<button id="addNewPoint" type="button" class="btn btn-outline-secondary font-weight-bold">+ Добавить точку</button>
							</div>
							<div class="col">
								<span class="text-danger ">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-exclamation-triangle" viewBox="0 0 16 20">
										<path d="M7.938 2.016A.13.13 0 0 1 8.002 2a.13.13 0 0 1 .063.016.146.146 0 0 1 .054.057l6.857 11.667c.036.06.035.124.002.183a.163.163 0 0 1-.054.06.116.116 0 0 1-.066.017H1.146a.115.115 0 0 1-.066-.017.163.163 0 0 1-.054-.06.176.176 0 0 1 .002-.183L7.884 2.073a.147.147 0 0 1 .054-.057zm1.044-.45a1.13 1.13 0 0 0-1.96 0L.165 13.233c-.457.778.091 1.767.98 1.767h13.713c.889 0 1.438-.99.98-1.767L8.982 1.566z"/>
										<path d="M7.002 12a1 1 0 1 1 2 0 1 1 0 0 1-2 0zM7.1 5.995a.905.905 0 1 1 1.8 0l-.35 3.507a.552.552 0 0 1-1.1 0L7.1 5.995z"/>
									</svg>
									Порядок точек определяется положением точек относительно друг друга.
									Изменяйте порядок, перетаскивая точки и меняя их местами.
								</span>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-primary" type="submit">Подтвердить изменения</button>
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно с добавления перевозчика вручную -->
	<div class="modal fade" id="addCarrierModal" tabindex="-1" aria-labelledby="addCarrierModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header align-items-center text-white">
					<h5 class="modal-title" id="addCarrierModalLabel">Назначить перевозчика на маршрут</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addCarrierForm" name="addCarrierForm" action="">
					<div class="modal-body">
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Номер маршрута:</span>
							<input type="text" class="form-control" name="idRoute" id="idRoute" readonly required>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Название маршрута:</span>
							<textarea type="text" class="form-control" rows="5" name="routeDirection" id="routeDirection" readonly required></textarea>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Выбрать перевозчика:</span>
							<input id="searchInOptions" class="keyboard__key w-75 p-2" placeholder="Поиск по названию или УНП">
							<select id="carrier" name="carrier" class="form-control" required>
								<option value="" hidden disabled selected>Выберите перевозчика</option>
								<!-- здесь будет список всех перевозчиков -->
							</select>
						</div>
						<!-- <div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Выбрать транспорт:</span>
							<select id="truck" name="truck" class="form-control" required>
								<option value="" hidden disabled selected>Выберите авто</option>
								
							</select>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Выбрать водителя:</span>
							<select id="driver" name="driver" class="form-control" required>
								<option value="" hidden disabled selected>Выберите водителя</option>
								
							</select>
						</div> -->
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
						<button class="btn btn-primary" type="submit">Назначить перевозчика</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно с установки пробега на маршрут -->
	<div class="modal fade" id="addMileageModal" tabindex="-1" aria-labelledby="addMileageModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header align-items-center text-white">
					<h5 class="modal-title" id="addMileageModalLabel">Установка пробега по маршруту</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addMileageForm" name="addMileageForm" action="">
					<div class="modal-body">
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Номер маршрута:</span>
							<input type="text" class="form-control" name="idRoute" id="idRoute" readonly required>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Название маршрута:</span>
							<textarea type="text" class="form-control" rows="5" name="routeDirection" id="routeDirection" readonly required></textarea>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Укажите пробег в км:</span>
							<input type="number" id="mileage" name="mileage" min="0" class="form-control">
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
						<button class="btn btn-primary" type="submit">Подтвердить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно с установкой цены перевозки -->
	<div class="modal fade" id="addFinishPriceModal" tabindex="-1" aria-labelledby="addFinishPriceModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header align-items-center text-white">
					<h5 class="modal-title" id="addFinishPriceModalLabel">Установка цены за перевозку</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addFinishPriceForm" name="addFinishPriceForm" action="">
					<div class="modal-body">
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Номер маршрута:</span>
							<input type="text" class="form-control" name="idRoute" id="idRoute" readonly required>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Название маршрута:</span>
							<textarea type="text" class="form-control" rows="5" name="routeDirection" id="routeDirection" readonly required></textarea>
						</div>
						<div class="form-group input-column-container">
							<span class="text-muted font-weight-bold">Укажите цену за перевозку в BYN:</span>
							<input type="number" id="finishPrice" name="finishPrice" min="0" class="form-control">
						</div>
					</div>
					<div class="modal-footer">
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Отмена</button>
						<button class="btn btn-primary" type="submit">Подтвердить</button>
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

	<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
	<script	src="${pageContext.request.contextPath}/resources/js/maintenanceList.js" type="module"></script>
</body>
</html>