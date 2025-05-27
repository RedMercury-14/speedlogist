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
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<title>Менеджер международных маршрутов</title>
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/jszip/jszip.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/FileSaver/FileSaver.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-custom-caption.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-thumbnails.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-dynamic-caption-plugin.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/internationalManagerNew.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
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
			<strong><h3>Менеджер международных маршрутов</h3></strong>
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
					<!-- <input class="form-control" type="text" name="searchName" id="searchName" placeholder="Наименование контрагента..."> -->
					<button class="btn btn-outline-secondary" type="submit">Отобразить</button>
				</form>
			</div>
		</div>
	
		<div id="myGrid" class="ag-theme-alpine"></div>
	
		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно с основной информацией о маршруте -->
	<div class="modal fade" id="routeInfoModal" tabindex="-1" aria-labelledby="routeInfoModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header align-items-center bg-primary text-white">
					<h5 class="modal-title" id="routeInfoModalLabel">Основная информация</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div id="routeInfo"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно предложений по маршруту -->
	<div class="modal fade" id="tenderOffersModal" tabindex="-1" data-backdrop="static" data-keyboard="false" aria-labelledby="tenderOffersModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header align-items-center bg-primary text-white">
					<h5 class="modal-title" id="tenderOffersModalLabel">Предложения</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="mb-3 h5 text-center px-3 text-muted font-weight-bold" id="routeDirection"></div>
					<div id="tenderOffers" style="height: 60vh; width: 100%;" class="ag-theme-alpine"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно аналитики цены -->
	<div class="modal fade" id="priceAnalisysModal" tabindex="-1" data-backdrop="static" data-keyboard="false" aria-labelledby="priceAnalisysModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header align-items-center bg-primary text-white">
					<h5 class="modal-title" id="priceAnalisysModalLabel">Аналитика</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body pb-0">
					<div class="mb-2 text-center px-3 text-muted font-weight-bold" id="priceAnalisys_routeDirection"></div>
					<div class="d-flex justify-content-end mb-4">
						<div class="d-flex align-items-center mr-3">
							<p id="" class="mb-0 mr-2">
								<strong>Логин:</strong>
								<span id="pbLogin">
									powerbi@proanalysesl.com
								</span>
							</p>
							<button id="copyPBLogin" type="button" class="btn px-1 py-0" title="Копировать">
								<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#999999">
									<path d="M360-240q-33 0-56.5-23.5T280-320v-480q0-33 23.5-56.5T360-880h360q33 0 56.5 23.5T800-800v480q0 33-23.5 56.5T720-240H360Zm0-80h360v-480H360v480ZM200-80q-33 0-56.5-23.5T120-160v-560h80v560h440v80H200Zm160-240v-480 480Z"/>
								</svg>
							</button>
						</div>
						<div class="d-flex align-items-center">
							<p class="mb-0 mr-2">
								<strong>Пароль:</strong>
								<span id="pbPass">
									Unks!89r
								</span>
							</p>
							<button id="copyPBPass" type="button" class="btn px-1 py-0" title="Копировать">
								<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#999999">
									<path d="M360-240q-33 0-56.5-23.5T280-320v-480q0-33 23.5-56.5T360-880h360q33 0 56.5 23.5T800-800v480q0 33-23.5 56.5T720-240H360Zm0-80h360v-480H360v480ZM200-80q-33 0-56.5-23.5T120-160v-560h80v560h440v80H200Zm160-240v-480 480Z"/>
								</svg>
							</button>
						</div>
					</div>
					<iframe id="priceAnalisysReportFrame" frameborder="0" allowFullScreen="true"></iframe>
					<div id="priceAnalisys_container">
					</div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно для добавления изображений -->
	<div class="modal fade" id="addFilesToRouteModal" tabindex="-1" data-backdrop="static" data-keyboard="false" aria-labelledby="addFilesToRouteModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header align-items-center bg-primary text-white">
					<h5 class="modal-title" id="addFilesToRouteModalLabel">Прикрепить изображения к маршруту</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="addFilesToRouteForm">
					<div class="modal-body">
						<textarea class="form-control border-0 bg-transparent mb-2 h-auto" rows="4" name="routeDirection" disabled></textarea>
						<input type="number" name="idRoute" hidden>
						<div class="form-group">
							<!-- <label class="col-form-label text-muted font-weight-bold">Прикрепите изображения</label> -->
							<input type="file"
								multiple
								class="form-control btn btn-outline-secondary"
								name="files"
								id="addRouteImageInput"
								required
							>
						</div>
						<div class="text-muted mb-2">Предпросмотр:</div>
						<div id="routeImageContainer"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Сохранить данные</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для регистрации и редактирования машины на Прилесье -->
	<div class="modal fade" id="regTruckInPrilesieModal" tabindex="-1" data-backdrop="static" data-keyboard="false" aria-labelledby="regTruckInPrilesieModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header align-items-center bg-primary text-white">
					<h5 class="modal-title" id="regTruckInPrilesieModalLabel">Регистрация машины на Прилесье</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="regTruckInPrilesieForm">
					<div class="modal-body">
						<textarea class="form-control border-0 bg-transparent mb-2 h-auto" rows="3" name="routeDirection" disabled></textarea>
						<input type="number" name="idRoute" hidden>
						<input type="number" name="actionType" hidden>
						<div class="text-center text-muted h5">Укажите продежуток времени для заезда машины:</div>
						<div class="d-flex justify-content-center">
							<div class="input-row-container mr-2">
								<label class="text-muted font-weight-bold">С</label>
								<input class="form-control" type="datetime-local" name="dateStart" required>
							</div>
							<div class="input-row-container">
								<label class="text-muted font-weight-bold">по</label>
								<input class="form-control" type="datetime-local" name="dateEnd" required>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Сохранить данные</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модалка -->
	<div class="modal fade" id="prilesieDataModal" tabindex="-1" role="dialog" aria-labelledby="prilesieDataModalLabel" aria-hidden="true">
		<div class="modal-dialog" role="document">
			<div class="modal-content">
				<div class="modal-header bg-color">
					<h5 class="modal-title" id="prilesieDataModalLabel">Информация о въезде</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Закрыть">
					<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<p><strong>ID:</strong> <span id="modal-id"></span></p>
					<p><strong>Номер машины:</strong> <span id="modal-plate"></span></p>
					<p><strong>Поставщик:</strong> <span id="modal-supplier"></span></p>
					<p><strong>Склад:</strong> <span id="modal-warehouse"></span></p>
					<p><strong>Рампа:</strong> <span id="modal-ramp"></span></p>
					<p><strong>Начало:</strong> <span id="modal-start"></span></p>
					<p><strong>Конец:</strong> <span id="modal-end"></span></p>
					<p><strong>SMS:</strong> <span id="modal-sms"></span></p>
					<p><strong>Дата въезда:</strong> <span id="modal-date-on"></span></p>
					<p><strong>Дата выезда:</strong> <span id="modal-date-exit"></span></p>
					<p><strong>Статус выезда:</strong> <span id="modal-exit-ok"></span></p>
				</div>
			</div>
		</div>
	</div>


	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script	src="${pageContext.request.contextPath}/resources/js/internationalManagerNew.js" type="module"></script>
</body>
</html>