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
	<title>Маршрутизатор 4000</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/map.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/leaflet/leaflet.css"/>
	<script src="${pageContext.request.contextPath}/resources/js/leaflet/leaflet.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/leaflet/leaflet.draw.css"/>
	<script src="${pageContext.request.contextPath}/resources/js/leaflet/leaflet.draw.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/leaflet/leaflet.geometryutil.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>
<body class="active-sidebar">
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<sec:authorize access="isAuthenticated()">  
		<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
		<sec:authentication property="name" var="login"/>
	</sec:authorize>
	<input type="hidden" value="${login}" id="login">
	<input type="hidden" value="${roles}" id="role">

	<!-- регулировка отступа контейнера с контентом -->
	<!-- <div class="fluid-container my-container" style="margin-top: 60px; height: calc(100vh - 60px);"> -->
	<div class="fluid-container my-container">
			
		<!-- боковая панель с интерфейсом управления картой -->
		<div class="sidebar">
			<button aria-label="close sidebar" type="button" class="close-button">
				<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-x-lg" viewBox="0 0 16 16">
					<path fill-rule="evenodd" d="M13.854 2.146a.5.5 0 0 1 0 .708l-11 11a.5.5 0 0 1-.708-.708l11-11a.5.5 0 0 1 .708 0Z"/>
					<path fill-rule="evenodd" d="M2.146 2.146a.5.5 0 0 0 0 .708l11 11a.5.5 0 0 0 .708-.708l-11-11a.5.5 0 0 0-.708 0Z"/>
				</svg>
			</button>

			<!-- меню с кнопками вкладок -->
			<ul class="sidebar-menu">
				<!-- кнопка вкладки построения маршрута -->
				<li class="menu-item active-item" data-item="route">
					<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-list" viewBox="0 0 16 16">
						<path fill-rule="evenodd" d="M2.5 12a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5zm0-4a.5.5 0 0 1 .5-.5h10a.5.5 0 0 1 0 1H3a.5.5 0 0 1-.5-.5z"/>
					</svg>
				</li>

				<!-- кнопка вкладки построения маршрута с textarea -->
				<li class="menu-item" data-item="routeArea">
					<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-ruled" viewBox="0 0 16 16">
						<path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm2-1a1 1 0 0 0-1 1v4h10V2a1 1 0 0 0-1-1H4zm9 6H6v2h7V7zm0 3H6v2h7v-2zm0 3H6v2h6a1 1 0 0 0 1-1v-1zm-8 2v-2H3v1a1 1 0 0 0 1 1h1zm-2-3h2v-2H3v2zm0-3h2V7H3v2z"/>
					</svg>
				</li>

				<!-- кнопка вкладки контроля расстояний -->
				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_MANAGER]' || roles == '[ROLE_TOPMANAGER]' || roles == '[ROLE_SHOW]'}">
						<li class="menu-item" data-item="distanceControl">
							<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-table" viewBox="0 0 16 16">
								<path d="M0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm15 2h-4v3h4V4zm0 4h-4v3h4V8zm0 4h-4v3h3a1 1 0 0 0 1-1v-2zm-5 3v-3H6v3h4zm-5 0v-3H1v2a1 1 0 0 0 1 1h3zm-4-4h4V8H1v3zm0-4h4V4H1v3zm5-3v3h4V4H6zm4 4H6v3h4V8z"/>
							</svg>
						</li>
					</c:when>
				</c:choose>

				<!-- кнопка вкладки тестового оптимизатора -->
				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_LOGISTDELIVERY]' || roles == '[ROLE_SHOW]'}">
						<li class="menu-item" data-item="optimizeRoute">
							<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-star-fill" viewBox="0 0 16 16">
								<path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
							</svg>
						</li>
					</c:when>
				</c:choose>

				<!-- кнопка вкладки настроек маршрутизатора -->
				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_TOPMANAGER]'}">
						<li class="menu-item last-item" data-item="settings">
							<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-gear"
								viewBox="0 0 16 16">
								<path d="M8 4.754a3.246 3.246 0 1 0 0 6.492 3.246 3.246 0 0 0 0-6.492zM5.754 8a2.246 2.246 0 1 1 4.492 0 2.246 2.246 0 0 1-4.492 0z" />
								<path d="M9.796 1.343c-.527-1.79-3.065-1.79-3.592 0l-.094.319a.873.873 0 0 1-1.255.52l-.292-.16c-1.64-.892-3.433.902-2.54 2.541l.159.292a.873.873 0 0 1-.52 1.255l-.319.094c-1.79.527-1.79 3.065 0 3.592l.319.094a.873.873 0 0 1 .52 1.255l-.16.292c-.892 1.64.901 3.434 2.541 2.54l.292-.159a.873.873 0 0 1 1.255.52l.094.319c.527 1.79 3.065 1.79 3.592 0l.094-.319a.873.873 0 0 1 1.255-.52l.292.16c1.64.893 3.434-.902 2.54-2.541l-.159-.292a.873.873 0 0 1 .52-1.255l.319-.094c1.79-.527 1.79-3.065 0-3.592l-.319-.094a.873.873 0 0 1-.52-1.255l.16-.292c.893-1.64-.902-3.433-2.541-2.54l-.292.159a.873.873 0 0 1-1.255-.52l-.094-.319zm-2.633.283c.246-.835 1.428-.835 1.674 0l.094.319a1.873 1.873 0 0 0 2.693 1.115l.291-.16c.764-.415 1.6.42 1.184 1.185l-.159.292a1.873 1.873 0 0 0 1.116 2.692l.318.094c.835.246.835 1.428 0 1.674l-.319.094a1.873 1.873 0 0 0-1.115 2.693l.16.291c.415.764-.42 1.6-1.185 1.184l-.291-.159a1.873 1.873 0 0 0-2.693 1.116l-.094.318c-.246.835-1.428.835-1.674 0l-.094-.319a1.873 1.873 0 0 0-2.692-1.115l-.292.16c-.764.415-1.6-.42-1.184-1.185l.159-.291A1.873 1.873 0 0 0 1.945 8.93l-.319-.094c-.835-.246-.835-1.428 0-1.674l.319-.094A1.873 1.873 0 0 0 3.06 4.377l-.16-.292c-.415-.764.42-1.6 1.185-1.184l.292.159a1.873 1.873 0 0 0 2.692-1.115l.094-.319z" />
							</svg>
						</li>
					</c:when>
				</c:choose>
			</ul>

			<!-- вкладки боковой панели -->
			<div class="sidebar-content">

				<!-- вкладка построения маршрута -->
				<div class="item-content active-content" id="route">
					<h2>Маршруты</h2>
					<div class="content">
						<form id="routeForm" action="">
							<div id="routeInputsContainer" class="route-container mb-2"></div>
							<div class="distance-container mb-3">
								<span class="text-muted font-weight-bold">Общее расстояние:</span>
								<span id="distanceInfo" class="text-muted font-weight-bold"></span>
							</div>
							<div class="formButton-container">
								<button class="btn btn-primary" type="submit">Построить маршрут</button>
								<button id="clearForm" class="btn btn-secondary" type="reset">Очистить форму</button>
							</div>
						</form>
					</div>
				</div>

				<!-- вкладка построения маршрута с textarea -->
				<div class="item-content" id="routeArea">
					<h2>Маршруты</h2>
					<div class="content">
						<form id="routeAreaForm" class="routeArea-form" action="">
							<div class="route-container routeAreaForm-container mb-2">
								<div class="number-container">
									<span class="text-muted font-weight-bold">№</span>
									<span class="text-muted font-weight-bold">1</span>
									<span class="text-muted font-weight-bold">2</span>
									<span class="text-muted font-weight-bold">3</span>
									<span class="text-muted font-weight-bold">4</span>
									<span class="text-muted font-weight-bold">5</span>
									<span class="text-muted font-weight-bold">6</span>
									<span class="text-muted font-weight-bold">7</span>
									<span class="text-muted font-weight-bold">8</span>
									<span class="text-muted font-weight-bold">9</span>
									<span class="text-muted font-weight-bold">10</span>
									<span class="text-muted font-weight-bold">11</span>
									<span class="text-muted font-weight-bold">12</span>
									<span class="text-muted font-weight-bold">13</span>
									<span class="text-muted font-weight-bold">14</span>
									<span class="text-muted font-weight-bold">15</span>
									<span class="text-muted font-weight-bold">16</span>
									<span class="text-muted font-weight-bold">17</span>
									<span class="text-muted font-weight-bold">18</span>
									<span class="text-muted font-weight-bold">19</span>
									<span class="text-muted font-weight-bold">20</span>
									<span class="text-muted font-weight-bold">21</span>
									<span class="text-muted font-weight-bold">22</span>
									<span class="text-muted font-weight-bold">23</span>
									<span class="text-muted font-weight-bold">24</span>
									<span class="text-muted font-weight-bold">25</span>
								</div>
								<div class="input-container">
									<span class="text-muted font-weight-bold">Номер магазина</span>
									<textarea class="route-textarea" id="routeTextarea" name="routeTextarea" cols="7" rows="25"></textarea>
								</div>
								<div id="routeAreaContainer" class="info-container">
									<span class="text-muted font-weight-bold">Адрес</span>
									<span class="text-muted font-weight-bold">Расст-е</span>
								</div>
							</div>
							<div class="distance-container mb-3">
								<span class="text-muted font-weight-bold">Общее расстояние:</span>
								<span id="distanceInfo" class="text-muted font-weight-bold"></span>
							</div>
							<div class="formButton-container">
								<button class="btn btn-primary" type="submit">Построить маршрут</button>
								<button class="btn btn-secondary" type="reset">Очистить форму</button>
							</div>
						</form>
					</div>
				</div>

				<!-- вкладка для рассчёта расстояний при загрузке развоза-->
				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_MANAGER]' || roles == '[ROLE_TOPMANAGER]' || roles == '[ROLE_SHOW]'}">
						<div class="item-content" id="distanceControl">
							<h2>Контроль расстояний</h2>
							<div class="content">
								<form id="distanceControlForm" action="">
									<div class="form-group mb-0">
										<label class="col-form-label text-muted font-weight-bold">Загрузите файл Excel</label>
										<input type="file" class="form-control btn-outline-secondary" name="excel" id="excel" accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel" required>
									</div>
									<div class="formButton-container">
										<button id="noReport" class="btn btn-primary" type="submit">Рассчитать без отчета</button>
										<button id="withReport" class="btn btn-primary" type="submit">Рассчитать с отчетом</button>
									</div>
								</form>
								<div class="distanceControlUtils hidden">
									<input type="number" id="goToShop" placeholder="Поиск магазина...">
									<button id="clearMap">Очистить карту</button>
									<a id="downloadReportLink" class="none" href="<spring:url value="/resources/others/razvoz.xlsx"/>" download>Скачать отчет</a>
								</div>
								<div id="distanceControlGrid" class="ag-theme-alpine"></div>
							</div>
						</div>
					</c:when>
				</c:choose>

				<!-- вкладка тестового оптимизатора -->
				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_LOGISTDELIVERY]' || roles == '[ROLE_SHOW]'}">
						<div class="item-content" id="optimizeRoute">
							<h2>Тестовый оптимизатор</h2>
							<div class="content">
								<div class="accordion" id="accordion">
									<div class="d-flex justify-content-between align-items-center" id="headingOne">
										<span class="h6 font-weight-bold text-muted">Форма оптимизатора</span>
										<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
											Показать/скрыть
										</span>
									</div>
									<div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion">
										<form id="optimizeRouteForm" class="routeArea-form" action="">
											<c:choose>
												<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_SHOW]'}">
													<div class="form-group row-container stock-container justify-content-between">
														<div class="row-container">
															<label class="col-form-label text-muted font-weight-bold">СКЛАД</label>
															<input class="form-control form-control-sm" type="number" name="stock" required>
														</div>
														<label class="d-flex justify-content-end align-items-center">
															<span class="text-muted font-weight-bold mr-1">Показать магазины</span>
															<input class="toggler" id="showOptimizerShops" type="checkbox">
														</label>
													</div>
													<div class="form-group row-container stock-container">
														<label class="col-form-label text-muted font-weight-bold">Коэф. трубы</label>
														<input class="form-control form-control-sm" type="number" step="0.01" min="0.01" value="2.0" name="iteration">
														<label class="col-form-label text-muted font-weight-bold" title="Максимальное количество магазинов в маршруте">Макс. магаз.</label>
														<input class="form-control form-control-sm" type="number" step="1" min="1" value="22" name="maxShopsInRoute" title="Максимальное количество магазинов в маршруте">
													</div>
												</c:when>
												<c:otherwise>
													<div class="form-group row-container stock-container justify-content-between">
														<div class="row-container">
															<label class="col-form-label text-muted font-weight-bold">СКЛАД</label>
															<input class="form-control form-control-sm" type="number" name="stock" required>
														</div>
														<label class="d-flex justify-content-end align-items-center">
															<span class="text-muted font-weight-bold mr-1">Показать магазины</span>
															<input class="toggler" id="showOptimizerShops" type="checkbox">
														</label>
														<input class="form-control form-control-sm" type="hidden" name="iteration" value="2.0">
														<input class="form-control form-control-sm" type="hidden" name="maxShopsInRoute" value="22">
													</div>
												</c:otherwise>
											</c:choose>
											<div class="route-container routeAreaForm-container mb-2">
												<div id="optimizeRouteNumberContainer" class="number-container"></div>
												<div class="input-container">
													<span class="text-muted font-weight-bold">Номер магазина</span>
													<textarea class="route-textarea" id="optimizeRouteShopNum" name="routeTextarea" cols="7" rows="1000" required></textarea>
												</div>
												<div class="input-container">
													<span class="text-muted font-weight-bold">Паллеты</span>
													<textarea class="route-textarea" id="optimizeRoutePall" name="pallTextarea" cols="7" rows="1000" required></textarea>
												</div>
												<div class="input-container">
													<span class="text-muted font-weight-bold">Вес груза,кг</span>
													<textarea class="route-textarea" id="optimizeRouteTonnage" name="tonnageTextarea" cols="7" rows="1000" required></textarea>
												</div>
												<div id="optimizeRouteCleaningInputsContainer" class="cleaningInputs-container"></div>
												<div id="optimizeRoutePallReturnInputsContainer" class="pallReturnInputs-container"></div>
											</div>
											<div class="car-inputs-container">
												<div class="truckListinputs">
													<input type="date" class="" name="currentDate" id="currentDate">
													<select class="" type="number" id="truckListsSelect" name="truckListsSelect">
														<option selected disabled value="">Выберите список автомобилей</option>
													</select>
													<button type="button" name="clearCarInputs" id="clearCarInputs">Очистить поля</button>
												</div>
												<div class="car-inputs-container__header p-1 pr-2">
													<div class="input-table">
														<span class="text-muted font-weight-bold">Машина</span>
														<span class="text-muted font-weight-bold text-nowrap">2 рейса</span>
														<span class="text-muted font-weight-bold">Кол-во</span>
														<span class="text-muted font-weight-bold">Палл.</span>
														<span class="text-muted font-weight-bold">Тоннаж</span>
													</div>
												</div>
												<div class="car-inputs-container__body p-1">
													<div class="input-table" id="carInputsTable"></div>
												</div>
											</div>
											<div class="row-container pallSum-container">
												<span class="text-muted ">Всего паллет:</span>
												<span class="text-muted font-weight-bold" id="palletsNeeded">0</span>
												<span class="text-muted ">Общая паллетовместимость:</span>
												<span class="text-muted font-weight-bold" id="totalPallets">0</span>
											</div>
											<div class="formButton-container">
												<button class="btn btn-sm btn-primary" data-version="v3" type="submit">Построить маршруты v.3</button>
												<button class="btn btn-sm btn-primary" data-version="v5" type="submit">Построить маршруты v.5</button>
												<button class="btn btn-sm btn-secondary" type="reset">Очистить форму</button>
											</div>
										</form>
									</div>

									<div class="border-top pb-2"></div>

									<div class="d-flex justify-content-between align-items-center" id="headingTwo">
										<span class="h6 font-weight-bold text-muted">Маршруты</span>
										<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapseTwo">
											Показать/скрыть
										</span>
									</div>
									<div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordion">
										<div id="optimizeRouteGrid" class="ag-theme-alpine"></div>
										<div id="emptyTruckContainer"></div>
										<button type="button" class="mt-2 btn btn-secondary" data-toggle="modal" data-target="#displayDataModal">
											Показать StackTrace
										</button>
									</div>
								</div>
							</div>
						</div>
					</c:when>
				</c:choose>

				<!-- вкладка настроек -->
				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_TOPMANAGER]'}">
						<div class="item-content" id="settings">
							<h2>Настройки</h2>
							<div class="content">
								<div class="accordion" id="settings-accordion">

									<!-- форма настроек маршрутизатора -->
									<div class="d-flex justify-content-between align-items-center" id="headingThree">
										<span class="h5 font-weight-bold text-muted">Настройки маршрутизатора</span>
										<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseThree" aria-expanded="true" aria-controls="collapseThree">
											Показать/скрыть
										</span>
									</div>
									<div id="collapseThree" class="collapse show" aria-labelledby="headingThree" data-parent="#settings-accordion">
										<form id="routingParamsForm" action="">
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadClassPRIMARY">Региональная дорога</label>
												<input class="form-control" type="number" name="roadClassPRIMARY" id="roadClassPRIMARY" min="0" max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadClassSECONDARY">Второстепенная дорога</label>
												<input class="form-control" type="number" name="roadClassSECONDARY" id="roadClassSECONDARY" min="0" max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadClassTERTIARY">Районная дорога</label>
												<input class="form-control" type="number" name="roadClassTERTIARY" id="roadClassTERTIARY" min="0"max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadClassRESIDENTIAL">Уличная дорога</label>
												<input class="form-control" type="number" name="roadClassRESIDENTIAL" id="roadClassRESIDENTIAL" min="0"max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadClassUNCLASSIFIED">Дорога без класса</label>
												<input class="form-control" type="number" name="roadClassUNCLASSIFIED" id="roadClassUNCLASSIFIED"min="0" max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadEnvironmentFERRY">Паромная переправа</label>
												<input class="form-control" type="number" name="roadEnvironmentFERRY" id="roadEnvironmentFERRY" min="0"max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="maxAxleLoad">Макс. нагрузка на ось</label>
												<input class="form-control" type="number" name="maxAxleLoad" id="maxAxleLoad" min="1" max="20" step="1"title="от 1 до 20">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="maxAxleLoadCoeff">Коэф. макс. нагрузки на ось</label>
												<input class="form-control" type="number" name="maxAxleLoadCoeff" id="maxAxleLoadCoeff" min="0" max="1"step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="surfaceASPHALT">Асфальтированная дорога</label>
												<input class="form-control" type="number" name="surfaceASPHALT" id="surfaceASPHALT" min="0" max="1"step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="surfaceCOMPACTED">Просёлочная дорога</label>
												<input class="form-control" type="number" name="surfaceCOMPACTED" id="surfaceCOMPACTED" min="0" max="1"step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="surfaceGRAVEL">Гравийная дорога</label>
												<input class="form-control" type="number" name="surfaceGRAVEL" id="surfaceGRAVEL" min="0" max="1"step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="surfaceMISSING">Накатанная дорога</label>
												<input class="form-control" type="number" name="surfaceMISSING" id="surfaceMISSING" min="0" max="1"step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold" title="roadClassMOTORWAYTOLL">Платная дорога(М1)</label>
												<input class="form-control" type="number" name="roadClassMOTORWAYTOLL" id="roadClassMOTORWAYTOLL"min="0" max="1" step="0.01" title="от 0 до 1">
											</div>
											<div class="form-group row-container mb-0">
												<label class="col-form-label text-muted font-weight-bold"title="distanceInfluence">distanceInfluence</label>
												<input class="form-control" type="number" name="distanceInfluence" id="distanceInfluence" min="0" max="100" step="0.01" title="от 0 до 100">
											</div>
											<div class="distance-container my-3">
												<span class="text-muted font-weight-bold">Общее расстояние:</span>
												<span id="distanceInfoInSettings" class="text-muted font-weight-bold"></span>
											</div>
											<button class="btn btn-primary" type="submit">Построить маршрут</button>
											<div class="d-flex">
												<c:choose>
													<c:when test="${login == 'catalina!%ricoh' || login =='pedagog%!sport' || login == 'yakubove%%'}">
														<button id="saveRoutingParams" class="btn btn-sm btn-secondary mt-1" type="button">Сохранить настройки</button>
													</c:when>
												</c:choose>
												<button id="loadRoutingParams" class="btn btn-sm btn-secondary mt-1" type="button">Загрузить настройки</button>
											</div>
										</form>
									</div>

									<!-- форма настроек оптимизатора -->
									<c:choose>
										<c:when test="${login == 'catalina!%ricoh' || login =='pedagog%!sport' || login == 'yakubove%%'}">
											<div class="border-top my-2"></div>
		
											<div class="d-flex justify-content-between align-items-center" id="headingFour">
												<span class="h5 font-weight-bold text-muted">Настройки оптимизатора</span>
												<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseFour" aria-expanded="true" aria-controls="collapseFour">
													Показать/скрыть
												</span>
											</div>
											<div id="collapseFour" class="collapse" aria-labelledby="headingFour" data-parent="#settings-accordion">
												<form id="optimizeRouteParamsForm" action="">
													<div class="border-bottom mb-2" id="optimizeRouteParamsMainCheckbox"></div>
													<div class="d-flex flex-wrap border-bottom mb-2" id="optimizeRouteParamsCheckboxes"></div>
													<div class="border-bottom mb-2" id="optimizeRouteParamsSelect"></div>
													<div class="border-bottom mb-2" id="optimizeRouteParamsInputs"></div>
													<div class="d-flex">
														<button id="loadRoutingParams" class="btn btn-primary mt-1" type="submit">Сохранить настройки</button>
													</div>
												</form>
											</div>
										</c:when>
									</c:choose>

								</div>
							</div>
						</div>
					</c:when>
				</c:choose>
			</div>
		</div>

		<!-- карта Leafleat -->
		<div id="map" class="my-shadow"></div>
	</div>
	<div id="snackbar"></div>

	<!-- Модальное окно для добавления полигона -->
	<div class="modal fade" id="poligonControlModal" tabindex="-1" aria-labelledby="poligonControlModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5" id="poligonControlModalLabel">Добавить полигон</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="poligonControlForm" action="">
					<div class="modal-body">
						<div class="error-message" id="messagePalygonName"></div>
						<div class="form-group">
							<input type="text" class="form-control" name="polygonName" id="polygonName" placeholder="Название полигона" required>
						</div>
						<div class="form-group">
							<select id="polygonAction" name="polygonAction" class="form-control" required>
								<option value="" hidden disabled selected>Выберите действие для полигона</option>
								<option value="trafficRestrictions">Ограничить движение</option>
								<option value="trafficBan">Запретить движение</option>
								<option value="trafficSpecialBan">Запретить движение для загруженных машин</option>
								<option value="crossDocking">Указать зону для кросс-докинга</option>
								<option value="weightDistribution">Указать зону распределения по весу</option>
							</select>
						</div>
						<div class="form-group none">
							<select id="crossDockingPoint" name="crossDockingPoint" class="form-control">
								<option value="" hidden disabled selected>Выберите место кросс-докинга</option>
							</select>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для вывода данных -->
	<div class="modal fade" id="displayDataModal" tabindex="-1" aria-labelledby="displayDataModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 mt-0" id="displayDataModalLabel">Вывод данных</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="form-group">
						<textarea class="w-100" name="displayDataInput" id="displayDataInput" cols="147" rows="25"></textarea>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно для отображения текста -->
	<div class="modal fade" id="displayMessageModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="displayMessageModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header justify-content-center bg-color">
					<h5 class="modal-title h3" id="displayMessageModalLabel">Сообщение</h5>
				</div>
				<div class="modal-body">
					<div id="messageContainer"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Ок, понятно</button>
				</div>
			</div>
		</div>
	</div>

</body>
<script src="${pageContext.request.contextPath}/resources/js/map.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</html>