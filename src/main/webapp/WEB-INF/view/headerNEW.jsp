<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="ru">
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    
	<!-- TITLE -->
	<title>SpeedLogist</title>

    <!-- BOOTSTRAP FRAMEWORK STYLES -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/bootstrap.min.css">
    <!-- MAIN CSS STYLE SHEET -->
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/reset.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/stylesheet.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/navbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/responsive.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/toast.css">

    <script src='${pageContext.request.contextPath}/resources/js/mainPage/jquery-3.4.1.min.js'></script>
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/bootstrap.min.js'></script>

</head>
<body>
	<header>
		<input type="hidden" value="${sessionCheck}" id="sessionCheck">
		<nav class="navbar navbar-expand-lg navbar-dark">
			<div class="container p-0">
				<a class="navbar-brand py-0" href="<spring:url value="/main"/>" ><img src="${pageContext.request.contextPath}/resources/img/mainPage/master/logo.png" alt="speedlogist_logo"></a>
				<sec:authorize access="isAuthenticated()">  
					<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
					<sec:authentication property="name" var="login"/>
				</sec:authorize>
				<button type="button" class="navbar-toggler collapsed" data-toggle="collapse" data-target="#main-nav">
					<span class="menu-icon-bar"></span>
					<span class="menu-icon-bar"></span>
					<span class="menu-icon-bar"></span>
				</button>
				<input type="hidden" value="${roles}" id="role">
				<input type="hidden" value="${login}" id="login">
				<div id="main-nav" class="collapse navbar-collapse">
					<ul class="navbar-nav ml-auto">
						<c:choose>
							<c:when test="${roles == '[ROLE_ADMIN]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Отдел логистики
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics/international" />">Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalNew" />">New Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalCarrier" />">Список перевозчиков</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/ordersLogist" />">Менеджер заявок</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/documentflow" />">Документооборот</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/shopControl" />">Список магазинов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/counterpartiesList" />">Список поставщиков</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/maintenance " />">Менеджер маршрутов АХО/СГИ</a>
									</div>
								</li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Развоз
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics-delivery/truck" />">Создание списка машин</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics-delivery/router" />">Маршрутизатор</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/delivery-shop" />">Кабинет перевозчика</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
								<!-- <li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li> -->
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Заказы, слоты
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/slots" />">Слоты</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/add-order" />">Создание заявки</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a>
										<a class="dropdown-item" href="<spring:url value="/main/order-support/orders" />">Остаток товара</a>
										<a class="dropdown-item" href="<spring:url value="/main/order-support/control" />">Загрузить отчёт</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/stock-support/orders" />">Таблица Башкиров</a> -->
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule" />">Графики поставок на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule-to" />">Графики поставок на ТО</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/need" />">Потребности</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/arrival" />">Приход</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/calculated" />">Приход паллет</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/report/398" />">398 отчёт</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/permission/list" />">История решений по заказам</a>
									</div>
								</li>
								<!-- <li><a class="nav-item nav-link" href="<spring:url value="/main/shop" />">Магазин</a></li> -->
								
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Аналитика</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=rc" />">Нехватка товаров на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=logistic" />">Аналитика Биржи</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=zero" />">Аналитика нулей</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=changeMatrix" />">Изменения матрицы</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=slots" />">Аналитика Слотов</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=serviceLvl" />">Сервис Lvl</a>
										<a class="dropdown-item" href="<spring:url value="/main/admin/reports/mainReports" />">Таблица заявок</a>
									</div>
								</li>
								
								<li><a class="nav-item nav-link" href="<spring:url value="/main/admin" />">Администрация</a></li>
							</c:when>	
							<c:when test="${roles == '[ROLE_SHOW]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Отдел логистики
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics/international" />">Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalNew" />">New Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalCarrier" />">Список перевозчиков</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/ordersLogist" />">Менеджер заявок</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/documentflow" />">Документооборот</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/shopControl" />">Список магазинов</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/logistics/counterpartiesList" />">Список поставщиков</a> -->
										<a class="dropdown-item" href="<spring:url value="/main/logistics/maintenance " />">Менеджер маршрутов АХО/СГИ</a>
									</div>
								</li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Развоз
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics-delivery/truck" />">Создание списка машин</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics-delivery/router" />">Маршрутизатор</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Заказы, слоты
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/slots" />">Слоты</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/add-order" />">Создание заявки</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a>
										<a class="dropdown-item" href="<spring:url value="/main/order-support/orders" />">Остаток товара</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule" />">Графики поставок на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule-to" />">Графики поставок на ТО</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/need" />">Потребности</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/orl/calculated" />">Приход паллет</a> -->
										<!-- <a class="dropdown-item" href="<spring:url value="/main/orl/report/398" />">398 отчёт</a> -->
									</div>
								</li>
								
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Аналитика</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=rc" />">Нехватка товаров на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=logistic" />">Аналитика Биржи</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=zero" />">Аналитика нулей</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=changeMatrix" />">Изменения матрицы</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=slots" />">Аналитика Слотов</a>
									</div>
								</li>
							</c:when>	
							<c:when test="${roles == '[ROLE_MANAGER]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Отдел логистики
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics/international" />">Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalNew" />">New Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalCarrier" />">Список перевозчиков</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/ordersLogist" />">Менеджер заявок</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/documentflow" />">Документооборот</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/shopControl" />">Список магазинов</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/logistics/counterpartiesList" />">Список поставщиков</a> -->
										<a class="dropdown-item" href="<spring:url value="/main/logistics/maintenance " />">Менеджер маршрутов АХО/СГИ</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li>
							</c:when>	
							<c:when test="${roles == '[ROLE_TOPMANAGER]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Отдел логистики
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics/international" />">Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalNew" />">New Менеджер международных маршрутов</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/internationalCarrier" />">Список перевозчиков</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/ordersLogist" />">Менеджер заявок</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/documentflow" />">Документооборот</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/shopControl" />">Список магазинов</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/logistics/counterpartiesList" />">Список поставщиков</a> -->
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=logistic" />">Аналитика Биржи</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=rc" />">Аналитика: Нехватка товаров на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/logistics/maintenance " />">Менеджер маршрутов АХО/СГИ</a>
									</div>
								</li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Развоз
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/logistics-delivery/truck" />">Создание списка машин</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_SHOP]' || roles == '[ROLE_SHOPMANAGER]'}">				
								<!-- <li><a class="nav-item nav-link" href="<spring:url value="/main/shop" />">Магазин</a></li> -->
							</c:when>	
							<c:when test="${roles == '[ROLE_STOCK]'}">				
								<li><a class="nav-item nav-link" href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_ANALYTICS]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Аналитика</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=rc" />">Нехватка товаров на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=logistic" />">Аналитика Биржи</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=zero" />">Аналитика нулей</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=changeMatrix" />">Изменения матрицы</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=slots" />">Аналитика Слотов</a>
									</div>
								</li>
							</c:when>
							<c:when test="${roles == '[ROLE_ORL]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Графики поставок</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule" />">Графики поставок на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule-to" />">Графики поставок на ТО</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/orl/need" />">Потребности</a></li>
								<!-- <li><a class="nav-item nav-link" href="<spring:url value="/main/procurement/ordersBalance" />">Остаток товара</a></li> -->
								<li><a class="nav-item nav-link" href="<spring:url value="/main/orl/calculated" />">Приход паллет</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/orl/report/398" />">398 отчёт</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_SLOTOBSERVER]'}">
								<li><a class="nav-item nav-link" href="<spring:url value="/main/logistics/maintenance " />">Менеджер маршрутов АХО/СГИ</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/procurement/ordersBalance" />">Остаток товара</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_CARRIER]'}">
								<li><a class="nav-item nav-link" href="<spring:url value="/main/carrier"/>">Мой кабинет</a></li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Меню</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/carrier/tender"/>">Текущие тендеры</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/transportation"/>">Текущие перевозки</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/controlpark/trucklist"/>">Управление автопарком</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/controlpark/driverlist"/>">Управление персоналом</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/transportation/routecontrole"/>">Акты</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/transportation/archive"/>">Архив перевозок</a>
										<a class="dropdown-item" href="<spring:url value="/main/carrier/tender/history"/>">История тендеров</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/carrier/maintenance " />">Менеджер маршрутов АХО/СГИ</a> -->
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/carrier/exchange"/>">О бирже</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_ORDERSUPPORT]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Заявки на перевозки</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/procurement/add-order" />">Создание заявки</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a>
									</div>
								</li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Cопровождение заказов</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/order-support/orders" />">Остаток товара</a>
										<a class="dropdown-item" href="<spring:url value="/main/order-support/control" />">Загрузить отчёт</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">ОРЛ</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule" />">Графики поставок на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/delivery-schedule-to" />">Графики поставок на ТО</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/ordersBalance" />">Остаток товара</a>
										<a class="dropdown-item" href="<spring:url value="/main/orl/need" />">Потребности</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/procurement/calculated" />">Приход паллет</a> -->
										<!-- <a class="dropdown-item" href="<spring:url value="/main/orl/report/398" />">398 отчёт</a> -->
										<!-- <a class="dropdown-item" href="<spring:url value="/main/orl/arrival" />">Приход</a> -->
									</div>
								</li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Аналитика</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=rc" />">Нехватка товаров на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=changeMatrix" />">Изменения матрицы</a>
										<a class="dropdown-item" href="<spring:url value="/main/analytics?pageName=slots" />">Аналитика Слотов</a>
									</div>
									</div>
								</li>
							</c:when>
							<c:when test="${roles == '[ROLE_STOCKSUPPORT]'}">
								<li><a class="nav-item nav-link" href="<spring:url value="/main/stock-support/orders" />">Менеджер заявок</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_PROCUREMENT]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Заявки на перевозки</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/procurement/add-order" />">Создание заявки</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li>
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										ОРЛ
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/slots/delivery-schedule" />">Графики поставок на РЦ</a>
										<a class="dropdown-item" href="<spring:url value="/main/slots/delivery-schedule-to" />">Графики поставок на ТО</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/ordersBalance" />">Остаток товара</a>
										<!-- <a class="dropdown-item" href="<spring:url value="/main/procurement/calculated" />">Приход паллет</a> -->
										<!-- <a class="dropdown-item" href="<spring:url value="/main/orl/report/398" />">398 отчёт</a> -->
									</div>
								</li>
							</c:when>
							<c:when test="${roles == '[ROLE_STOCKPROCUREMENT]'}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">Заявки на перевозки</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/procurement/add-order" />">Создание заявки</a>
										<a class="dropdown-item" href="<spring:url value="/main/procurement/orders" />">Контроль заявок</a>
									</div>
								</li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/slots" />">Слоты</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/procurement/ordersBalance" />">Остаток товара</a></li>
							</c:when>
							<c:when test="${roles == '[ROLE_LOGISTDELIVERY]'}">
								<li><a class="nav-item nav-link" href="<spring:url value="/main/logistics-delivery/truck" />">Создание списков автомобилей</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/logistics-delivery/router" />">Маршрутизатор</a></li>
							</c:when>
							<c:otherwise>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/registration" />">Регистрация</a></li>
								<li><a class="nav-item nav-link" href="<spring:url value="/main/signin"/>">Вход в систему</a></li>
							</c:otherwise>
						</c:choose>
						<sec:authorize access="authenticated" var="authenticated" />
						<c:choose>
							<c:when test="${authenticated}">
								<li class="dropdown">
									<a href="#" class="nav-item nav-link" data-toggle="dropdown">
										Welcome <sec:authentication property="name" />
										<span id="message-badge"></span>
										<input type="hidden" value="<sec:authentication property="name" />" id="login">
									</a>
									<div class="dropdown-menu">
										<a class="dropdown-item" href="<spring:url value="/main/userpage"/>">Кабинет пользователя</a>
										<a class="dropdown-item notification" href="<spring:url value="/main/message" />">
											<span>Входящие</span>
											<span class="badge"></span>
										</a>
										<a id="logout" class="dropdown-item" href="#"> Выход</a>
										<form id="logout-form" action="<c:url value="/logout"/>" method="POST">
											<sec:csrfInput />
										</form>
									</div>
								</li>
							</c:when>
						</c:choose>
					</ul>
				</div>
			</div>
		</nav>
	</header>

	<script src="<spring:url value="/resources/js/headerNEW.js"/>" type="module"></script>
</body>