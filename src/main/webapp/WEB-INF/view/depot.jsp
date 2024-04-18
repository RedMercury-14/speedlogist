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
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/map.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.4/dist/leaflet.css" integrity="sha256-p4NxAoJBhIIN+hmNHrzRCf9tD/miZyoHS5obTRR9BMY=" crossorigin="" />
	<script src="https://unpkg.com/leaflet@1.9.4/dist/leaflet.js" integrity="sha256-20nQCchB9co0qIjJZRGuk2/Z9VM+kNiyxNV1lvTlZBo=" crossorigin=""></script>
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/leaflet.draw/1.0.4/leaflet.draw.css" integrity="sha512-gc3xjCmIy673V6MyOAZhIW93xhM9ei1I+gLbmFjUHIjocENRsLX/QUE1htk5q1XV2D/iie/VQ8DXI6Vu8bexvQ==" crossorigin="anonymous" referrerpolicy="no-referrer" />
	<script src="https://cdnjs.cloudflare.com/ajax/libs/leaflet.draw/1.0.4/leaflet.draw.js" integrity="sha512-ozq8xQKq6urvuU6jNgkfqAmT7jKN2XumbrX1JiB3TnF7tI48DPI4Gy1GXKD/V3EExgAs1V+pRO7vwtS1LHg0Gw==" crossorigin="anonymous" referrerpolicy="no-referrer"></script>
</head>
<body class="active-sidebar">
	<jsp:include page="headerNEW.jsp" />
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

				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_MANAGER]' || roles == '[ROLE_TOPMANAGER]'}">

						<!-- кнопка вкладки построения маршрута с textarea -->
						<li class="menu-item" data-item="routeArea">
							<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-file-ruled" viewBox="0 0 16 16">
								<path d="M2 2a2 2 0 0 1 2-2h8a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H4a2 2 0 0 1-2-2V2zm2-1a1 1 0 0 0-1 1v4h10V2a1 1 0 0 0-1-1H4zm9 6H6v2h7V7zm0 3H6v2h7v-2zm0 3H6v2h6a1 1 0 0 0 1-1v-1zm-8 2v-2H3v1a1 1 0 0 0 1 1h1zm-2-3h2v-2H3v2zm0-3h2V7H3v2z"/>
							</svg>
						</li>

						<!-- кнопка вкладки контроля расстояний -->
						<li class="menu-item" data-item="distanceControl">
							<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-table" viewBox="0 0 16 16">
								<path d="M0 2a2 2 0 0 1 2-2h12a2 2 0 0 1 2 2v12a2 2 0 0 1-2 2H2a2 2 0 0 1-2-2V2zm15 2h-4v3h4V4zm0 4h-4v3h4V8zm0 4h-4v3h3a1 1 0 0 0 1-1v-2zm-5 3v-3H6v3h4zm-5 0v-3H1v2a1 1 0 0 0 1 1h3zm-4-4h4V8H1v3zm0-4h4V4H1v3zm5-3v3h4V4H6zm4 4H6v3h4V8z"/>
							</svg>
						</li>

						<c:choose>
							<c:when test="${login == 'catalina!%ricoh' || login == 'yakubove%%'}">

								<!-- кнопка вкладки поиска по адресу -->
								<li class="menu-item" data-item="addressSearch">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-geo-alt-fill" viewBox="0 0 16 16">
										<path d="M8 16s6-5.686 6-10A6 6 0 0 0 2 6c0 4.314 6 10 6 10zm0-7a3 3 0 1 1 0-6 3 3 0 0 1 0 6z"/>
									</svg>
								</li>

								<!-- кнопка вкладки тестового оптимизатора -->
								<li class="menu-item" data-item="optimizeRoute">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-star-fill" viewBox="0 0 16 16">
										<path d="M3.612 15.443c-.386.198-.824-.149-.746-.592l.83-4.73L.173 6.765c-.329-.314-.158-.888.283-.95l4.898-.696L7.538.792c.197-.39.73-.39.927 0l2.184 4.327 4.898.696c.441.062.612.636.282.95l-3.522 3.356.83 4.73c.078.443-.36.79-.746.592L8 13.187l-4.389 2.256z"/>
									</svg>
								</li>

								<!-- кнопка вкладки загрузки магазинов -->
								<li class="menu-item" data-item="shopLoads">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-shop" viewBox="0 0 16 16">
										<path d="M2.97 1.35A1 1 0 0 1 3.73 1h8.54a1 1 0 0 1 .76.35l2.609 3.044A1.5 1.5 0 0 1 16 5.37v.255a2.375 2.375 0 0 1-4.25 1.458A2.371 2.371 0 0 1 9.875 8 2.37 2.37 0 0 1 8 7.083 2.37 2.37 0 0 1 6.125 8a2.37 2.37 0 0 1-1.875-.917A2.375 2.375 0 0 1 0 5.625V5.37a1.5 1.5 0 0 1 .361-.976l2.61-3.045zm1.78 4.275a1.375 1.375 0 0 0 2.75 0 .5.5 0 0 1 1 0 1.375 1.375 0 0 0 2.75 0 .5.5 0 0 1 1 0 1.375 1.375 0 1 0 2.75 0V5.37a.5.5 0 0 0-.12-.325L12.27 2H3.73L1.12 5.045A.5.5 0 0 0 1 5.37v.255a1.375 1.375 0 0 0 2.75 0 .5.5 0 0 1 1 0zM1.5 8.5A.5.5 0 0 1 2 9v6h1v-5a1 1 0 0 1 1-1h3a1 1 0 0 1 1 1v5h6V9a.5.5 0 0 1 1 0v6h.5a.5.5 0 0 1 0 1H.5a.5.5 0 0 1 0-1H1V9a.5.5 0 0 1 .5-.5zM4 15h3v-5H4v5zm5-5a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1v3a1 1 0 0 1-1 1h-2a1 1 0 0 1-1-1v-3zm3 0h-2v3h2v-3z"/>
									</svg>
								</li>

								<!-- кнопка вкладки с тестовыми кнопками -->
								<li class="menu-item" data-item="testing">
									<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-hammer" viewBox="0 0 16 16">
										<path d="M9.972 2.508a.5.5 0 0 0-.16-.556l-.178-.129a5.009 5.009 0 0 0-2.076-.783C6.215.862 4.504 1.229 2.84 3.133H1.786a.5.5 0 0 0-.354.147L.146 4.567a.5.5 0 0 0 0 .706l2.571 2.579a.5.5 0 0 0 .708 0l1.286-1.29a.5.5 0 0 0 .146-.353V5.57l8.387 8.873A.5.5 0 0 0 14 14.5l1.5-1.5a.5.5 0 0 0 .017-.689l-9.129-8.63c.747-.456 1.772-.839 3.112-.839a.5.5 0 0 0 .472-.334z"/>
									</svg>
								</li>
							</c:when>
						</c:choose>

						<c:choose>
							<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_TOPMANAGER]'}">
							<!-- кнопка вкладки настроек маршрутизатора -->
								<li class="menu-item last-item" data-item="settings">
									<svg xmlns="http://www.w3.org/2000/svg" width="20" height="20" fill="currentColor" class="bi bi-gear"
										viewBox="0 0 16 16">
										<path d="M8 4.754a3.246 3.246 0 1 0 0 6.492 3.246 3.246 0 0 0 0-6.492zM5.754 8a2.246 2.246 0 1 1 4.492 0 2.246 2.246 0 0 1-4.492 0z" />
										<path d="M9.796 1.343c-.527-1.79-3.065-1.79-3.592 0l-.094.319a.873.873 0 0 1-1.255.52l-.292-.16c-1.64-.892-3.433.902-2.54 2.541l.159.292a.873.873 0 0 1-.52 1.255l-.319.094c-1.79.527-1.79 3.065 0 3.592l.319.094a.873.873 0 0 1 .52 1.255l-.16.292c-.892 1.64.901 3.434 2.541 2.54l.292-.159a.873.873 0 0 1 1.255.52l.094.319c.527 1.79 3.065 1.79 3.592 0l.094-.319a.873.873 0 0 1 1.255-.52l.292.16c1.64.893 3.434-.902 2.54-2.541l-.159-.292a.873.873 0 0 1 .52-1.255l.319-.094c1.79-.527 1.79-3.065 0-3.592l-.319-.094a.873.873 0 0 1-.52-1.255l.16-.292c.893-1.64-.902-3.433-2.541-2.54l-.292.159a.873.873 0 0 1-1.255-.52l-.094-.319zm-2.633.283c.246-.835 1.428-.835 1.674 0l.094.319a1.873 1.873 0 0 0 2.693 1.115l.291-.16c.764-.415 1.6.42 1.184 1.185l-.159.292a1.873 1.873 0 0 0 1.116 2.692l.318.094c.835.246.835 1.428 0 1.674l-.319.094a1.873 1.873 0 0 0-1.115 2.693l.16.291c.415.764-.42 1.6-1.185 1.184l-.291-.159a1.873 1.873 0 0 0-2.693 1.116l-.094.318c-.246.835-1.428.835-1.674 0l-.094-.319a1.873 1.873 0 0 0-2.692-1.115l-.292.16c-.764.415-1.6-.42-1.184-1.185l.159-.291A1.873 1.873 0 0 0 1.945 8.93l-.319-.094c-.835-.246-.835-1.428 0-1.674l.319-.094A1.873 1.873 0 0 0 3.06 4.377l-.16-.292c-.415-.764.42-1.6 1.185-1.184l.292.159a1.873 1.873 0 0 0 2.692-1.115l.094-.319z" />
									</svg>
								</li>
							</c:when>
						</c:choose>

					</c:when>
				</c:choose>
			</ul>

			<!-- вкладки боковой панели -->
			<div class="sidebar-content">

				<!-- вкладка построения маршрута -->
				<div class="item-content active-content" id="route">
					<h2>Маршруты</h2>
					<div class="content">
						<div class="toggler-container">
							<label>
								<input class="toggler" id="allShopsToggler" type="checkbox"/>
								<span class="text-muted font-weight-bold">Показать все магазины</span>
							</label>
						</div>
						<form id="routeForm" action="">
							<div id="routeInputsContainer" class="route-container mb-2"></div>
							<div class="distance-container mb-3">
								<span class="text-muted font-weight-bold">Общее расстояние:</span>
								<span id="distanceInfo" class="text-muted font-weight-bold"></span>
							</div>
							<div class="formButton-container">
								<button class="btn btn-primary" type="submit">Построить</button>
								<button id="clearForm" class="btn btn-secondary" type="reset">Очистить форму</button>
							</div>
						</form>
					</div>
				</div>

				<c:choose>
					<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_MANAGER]' || roles == '[ROLE_TOPMANAGER]'}">

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
										<button class="btn btn-primary" type="submit">Построить</button>
										<button class="btn btn-secondary" type="reset">Очистить форму</button>
									</div>
								</form>
							</div>
						</div>

						<!-- вкладка для рассчёта расстояний при загрузке развоза-->
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

						<c:choose>
							<c:when test="${login == 'catalina!%ricoh' || login == 'yakubove%%'}">

								<!-- вкладка поиска по адресу -->
								<div class="item-content" id="addressSearch">
									<h2>Поиск по адресу</h2>
									<div class="content">
										<form id="addressSearchForm" action="">
											<input class="form-control" type="text" name="testingInput" id="testingInput" placeholder="Адрес на беларуском языке">
											<button class="btn btn-primary" type="submit">Поиск</button>
										</form>
									</div>
								</div>

								<!-- вкладка тестового оптимизатора -->
								<div class="item-content" id="optimizeRoute">
									<h2>Тестовый оптимизатор</h2>
									<div class="content">
										<div class="accordion" id="accordion">
											<div class="d-flex justify-content-between align-items-center" id="headingOne">
												<span class="h5 font-weight-bold text-muted">Форма оптимизатора</span>
												<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseOne" aria-expanded="true" aria-controls="collapseOne">
													Показать/скрыть
												</span>
											</div>
											<div id="collapseOne" class="collapse show" aria-labelledby="headingOne" data-parent="#accordion">
												<form id="optimizeRouteForm" class="routeArea-form" action="">
													<div class="form-group row-container stock-container">
														<label class="col-form-label text-muted font-weight-bold">СКЛАД</label>
														<input class="form-control" type="number" name="stock" required>
														<label class="col-form-label text-muted font-weight-bold">Число итераций</label>
														<input class="form-control" type="number" name="iteration" required>
													</div>
													<div class="route-container routeAreaForm-container mb-2">
														<div id="optimizeRouteNumberContainer" class="number-container"></div>
														<div class="input-container">
															<span class="text-muted font-weight-bold">Номер магазина</span>
															<textarea class="route-textarea" id="optimizeRouteShopNum" name="routeTextarea" cols="7" rows="500" required></textarea>
														</div>
														<div class="input-container">
															<span class="text-muted font-weight-bold">Паллеты</span>
															<textarea class="route-textarea" id="optimizeRoutePall" name="pallTextarea" cols="7" rows="500" required></textarea>
														</div>
														<div class="input-container">
															<span class="text-muted font-weight-bold">Вес груза,кг</span>
															<textarea class="route-textarea" id="optimizeRouteTonnage" name="tonnageTextarea" cols="7" rows="500" required></textarea>
														</div>
														<div id="optimizeRouteCleaningInputsContainer" class="cleaningInputs-container"></div>
													</div>
													<div class="car-inputs-container">
														<div class="car-inputs-container__header p-1 pr-2">
															<div class="input-table">
																<span class="text-muted font-weight-bold">Машина</span>
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
														<button class="btn btn-primary" type="submit">Отправить</button>
														<button class="btn btn-secondary" type="reset">Очистить форму</button>
													</div>
												</form>
											</div>

											<div class="border-top pb-2"></div>

											<div class="d-flex justify-content-between align-items-center" id="headingTwo">
												<span class="h5 font-weight-bold text-muted">Маршруты</span>
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

								<!-- вкладка загрузки магазинов -->
								<div class="item-content" id="shopLoads">
									<h2>Загрузка магазинов</h2>
									<div class="content">
										<form id="shopLoadsForm" class="routeArea-form" action="">
											<div class="route-container routeAreaForm-container mb-2">
												<div id="shopLoadsNumberContainer" class="number-container"></div>
												<div class="input-container">
													<span class="text-muted font-weight-bold">Номер магазина</span>
													<textarea class="route-textarea" id="shopLoadsShopNum" name="shopTextarea" cols="7" rows="500" required></textarea>
												</div>
												<div class="input-container">
													<span class="text-muted font-weight-bold">Паллеты</span>
													<textarea class="route-textarea" id="shopLoadsPall" name="pallTextarea" cols="7" rows="500" required></textarea>
												</div>
												<div class="input-container">
													<span class="text-muted font-weight-bold">Тоннаж</span>
													<textarea class="route-textarea" id="shopLoadsTonnage" name="tonnageTextarea" cols="15" rows="500" required></textarea>
												</div>
											</div>
											<div class="formButton-container">
												<button class="btn btn-primary" type="submit">Отправить</button>
												<button class="btn btn-secondary" type="reset">Очистить форму</button>
											</div>
										</form>
									</div>
								</div>

								<!-- вкладка тестовых кнопок -->
								<div class="item-content" id="testing">
									<h2>Тестовые кнопки</h2>
									<div class="content">
										<div style="display: flex; flex-direction: column; gap: 20px;">
											<button id="testBtn1" class="btn btn-primary" type="button">Тестовая кнопка 1</button>
											<button id="testBtn2" class="btn btn-warning" type="button">Тестовая кнопка 2</button>
											<button id="testBtn3" class="btn btn-info" type="button">Тестовая кнопка 3</button>
											<button id="testBtn4" class="btn btn-success" type="button">Тестовая кнопка 4</button>
										</div>
										<br>
									</div>
								</div>
							</c:when>
						</c:choose>

						<c:choose>
							<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_TOPMANAGER]'}">
							<!-- вкладка настроек -->
								<div class="item-content" id="settings">
									<h2>Настройки</h2>
									<div class="content">
										<div class="accordion" id="settings-accordion">
											<div class="d-flex justify-content-between align-items-center" id="headingThree">
												<span class="h5 font-weight-bold text-muted">Настройки маршрутизатора</span>
												<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseThree" aria-expanded="true" aria-controls="collapseThree">
													Показать/скрыть
												</span>
											</div>
											<div id="collapseThree" class="collapse show" aria-labelledby="headingThree" data-parent="#settings-accordion">
												<!-- форма настроек маршрутизатора -->
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
															<c:when test="${login == 'catalina!%ricoh' || login == 'yakubove%%'}">
																<button id="saveRoutingParams" class="btn btn-secondary mt-1" type="button">Сохранить настройки</button>
															</c:when>
														</c:choose>
														<button id="loadRoutingParams" class="btn btn-secondary mt-1" type="button">Загрузить настройки</button>
													</div>
												</form>
											</div>
		
											<div class="border-top my-2"></div>
		
											<div class="d-flex justify-content-between align-items-center" id="headingFour">
												<span class="h5 font-weight-bold text-muted">Настройки оптимизатора</span>
												<span class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseFour" aria-expanded="true" aria-controls="collapseFour">
													Показать/скрыть
												</span>
											</div>
											<div id="collapseFour" class="collapse" aria-labelledby="headingFour" data-parent="#settings-accordion">
												<!-- форма настроек оптимизатора -->
												<span class="text-muted">Настройки оптимизатора</span>
											</div>
										</div>
									</div>
								</div>
							</c:when>
						</c:choose>

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
				<form id="poligonControlForm" action="" class="needs-validation" novalidate>
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
						<textarea name="displayDataInput" id="displayDataInput" cols="147" rows="25"></textarea>
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>
</body>
<script src="${pageContext.request.contextPath}/resources/js/map.js" type="module"></script>
<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</html>