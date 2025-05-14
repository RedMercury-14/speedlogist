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
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carrier.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div class="container-fluid my-container">
		<!-- <h1>Страница перевозчика</h1> -->
		<c:out value="${errorMessage}" />
		<div class="card-container">

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/computer_chart.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/tender"/>">
					<div class="st-caption">
						<h5>Текущие тендеры</h5>
					</div>
				</a>
			</div>

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/truck.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/transportation"/>">
					<div class="st-caption">
						<h5>Текущие перевозки</h5>
					</div>
				</a>
			</div>

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/trucks.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/controlpark/trucklist"/>">
					<div class="st-caption">
						<h5>Управление автопарком</h5>
					</div>
				</a>
			</div>

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/driver.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/controlpark/driverlist"/>">
					<div class="st-caption">
						<h5>Управление персоналом</h5>
					</div>
				</a>
			</div>

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/folder.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/transportation/routecontrole"/>">
					<div class="st-caption">
						<h5>Акты</h5>
					</div>
				</a>
			</div>

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/archive.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/transportation/archive"/>">
					<div class="st-caption">
						<h5>Архив перевозок</h5>
					</div>
				</a>
			</div>

			<div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/archive.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/tender/history"/>">
					<div class="st-caption">
						<h5>История тендеров</h5>
					</div>
				</a>
			</div>

			<!-- <div class="services-thumb">
				<figure class="st-feature">
					<img src="${pageContext.request.contextPath}/resources/img/mainPage/images/archive.jpg" alt="">
				</figure>
				<a href="<spring:url value="/main/carrier/delivery-shop" />">
					<div class="st-caption">
						<h5 class="badge-new">Заявки авто на развоз</h5>
					</div>
				</a>
			</div> -->
		</div>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/tenderNotifications.js" type="module"></script>
</body>

</html>