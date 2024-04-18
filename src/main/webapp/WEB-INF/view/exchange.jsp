<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Страница перевозчика</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/mainPage/css/custom.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/exchange.css"> 
</head>

<body>
	<!-- PRELOADER START -->
	<div id="loader-wrapper">
		<div class="loader">
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		   <div class="ball"></div>
		</div> 
	</div>
	<!-- PRELOADER END -->

	<jsp:include page="headerNEW.jsp" />

	<div class="pages-hero">
		<div class="container">
			<div class="pages-title">
				<h1>О бирже</h1>
				<div class="page-nav">
					<p><a href="<spring:url value="/main"/>">Главная</a> &nbsp; | &nbsp; <a href="#">О бирже</a></p>
				</div>
			</div>
		</div>
	</div>

	<section class="exchange-content">
		<div class="container odd-container mt-5 mb-5">
			<div class="row">
				<div class="col-lg-6">
					<div class="careers-info">
						<h2>Шаг 1: Пополните Ваш автопарк!</h2>
						<h3>Управление автопарком</h3>
						<p><strong>Добавьте Ваш транспорт на платформу для возможности участия в тендерах</strong></p>
						<p>В дальнейшем все Ваши внесенные машины будут автоматически попадать в отчёты, учитываться при оптимизации маршрутов и создании автоматических заявок</p>
						<a href="./controlpark/trucklist">
							<button class="cargoy-btn mt-3">Добавить транспорт</button>
						</a>
					</div>
				</div>
				<div class="col-lg-6 mt-view">
					<figure class="careers-pic"><img src="${pageContext.request.contextPath}/resources/mainPage/img/images/trucks.png" alt=""></figure>
				</div>
			</div>
		</div>
		<div class="container even-container mt-5 mb-5">
			<div class="row">
				<div class="col-lg-6 mt-view">
					<figure class="careers-pic"><img src="${pageContext.request.contextPath}/resources/mainPage/img/images/tenders.png" alt=""></figure>
				</div>
				<div class="col-lg-6">
					<div class="careers-info">
						<h2>Шаг 2: Учавствуйте в торгах!</h2>
						<h3>Тендеры</h3>
						<p><strong>Просматривайте список тендеров и устанавливайте свою цену за перевозку</strong></p>
						<p>Для того, чтобы учавствоать в тендере, необходимо прислать договор и наш менеджер предоставит все права для участия</p>
						<a href="./tender">
							<button class="cargoy-btn mt-3">Просмотреть текущие тендеры</button>
						</a>
					</div>
				</div>
			</div>
		</div>
		<div class="container odd-container mt-5 mb-5">
			<div class="row">
				<div class="col-lg-6">
					<div class="careers-info">
						<h2>Шаг 3: Всё здесь!</h2>
						<h3>Ваши перевозки</h3>
						<p><strong>Управляйте Вашими перевозками: просматривайте информацию, назначайте машину и водителя, указывайте дату и время и изменяйте статус перевозки</strong></p>
						<p>В этом окне отображаются все перевозки, которые актуальны на текущий момент</p>
						<a href="./transportation">
							<button class="cargoy-btn mt-3">Просмотреть текущие перевозки</button>
						</a>
					</div>
				</div>
				<div class="col-lg-6 mt-view">
					<figure class="careers-pic"><img src="${pageContext.request.contextPath}/resources/mainPage/img/images/routes.png" alt=""></figure>
				</div>
			</div>
		</div>
		<div class="container even-container mt-5 mb-5">
			<div class="row">
				<div class="col-lg-6 mt-view">
					<figure class="careers-pic"><img src="${pageContext.request.contextPath}/resources/mainPage/img/images/drivers.png" alt=""></figure>
				</div>
				<div class="col-lg-6">
					<div class="careers-info">
						<h2>Шаг 4: Добавьте водителей</h2>
						<h3>Управление персоналом</h3>
						<p><strong>Добавьте Ваших водителей в систему для формирования заявок</strong></p>
						<p>Все добавленные водители автоматически сохраняются для последующих перевозок</p>
						<a href="./controlpark/driverlist">
							<button class="cargoy-btn mt-3">Добавить водителей</button>
						</a>
					</div>
				</div>
			</div>
		</div>
		<div class="container odd-container mt-5 mb-5">
			<div class="row">
				<div class="col-lg-6">
					<div class="careers-info">
						<h2>Шаг 5: Отчётность!</h2>
						<h2>Акты выполненных работ</h2>
						<p><strong>Формируйте и скачивайте акты по выполненым перевозкам </strong></p>
						<p>Отмечайте нужные маршруты для формирования актов. Для одного акта можно отметить до 10 маршрутов</p>
						<a href="./transportation/routecontrole">
							<button class="cargoy-btn mt-3">Просмотреть акты</button>
						</a>
					</div>
				</div>
				<div class="col-lg-6 mt-view">
					<figure class="careers-pic"><img src="${pageContext.request.contextPath}/resources/mainPage/img/images/acts.png" alt=""></figure>
				</div>
			</div>
		</div>
	</section>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<!--SCROLL TOP START-->
	<a href="#0" class="cd-top">Top</a>
	<!--SCROLL TOP START-->

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/main.js'></script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>

</html>