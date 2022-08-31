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
<style>
.text {
	margin: auto;
	text-align: center;
	font-size: 32px;
	line-height: 1.5;
	text-shadow: 0 0 15px rgba(255, 255, 255, .5), 0 0 10px
		rgba(255, 255, 255, .5);
}

.right-click-menu {
	margin: 0;
	padding: 0;
	position: fixed;
	list-style: none;
	background: #ddddddcc;
	border: 2px solid #ffffff00;
	border-radius: 2px;
	display: none;
}

.right-click-menu.active {
	display: block;
}

.right-click-menu li {
	width: 100%;
	padding: 10px;
	box-sizing: border-box;
	cursor: pointer;
	font-size: 15px;
}

.right-click-menu li:hover {
	background: #ffffff73;
}
.none{
	display: none;
}
.activRow{
		background: #494f5252;
	}
.finishRow{
		background: #c4ffe1db;
	}
.attentionRow{
		background: #dce37266;
}
</style>
<title>Менеджер международных маршрутов</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/other.css"/>" />
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<h3>Менеджер международных маршрутов</h3>
	</div>
	<div class="container">
		<form:form method="get">
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart"
				value="<c:out value="${dateNow}" />" />
	по<input type="date" name="dateFinish"
				value="<c:out value="${dateTomorrow}" />" />
			<br>
			<input type="submit" value="Отобразить">
			</p>
		</form:form>
	</div>
	<div class="container-fluid">
		<div class="table-responsive">
			<a href="<spring:url value="/main/logistics/international/add"/>">Создать
				маршрут</a>
			<table
				class="table table-bordered border-primary table-hover table-condensed"
				id="sort">
				<thead class="text-center">
					<tr>
						<th>Название маршрута</th>
						<th>Дата загрузки</th>
						<th>Время загрузки (планируемое)</th>
						<th>Выставляемая стоимость</th>
						<th>Перевозчик</th>
						<th>Общее колличество паллет</th>
						<th>Общий вес</th>						
						<th>Начальные стоимости перевозки</th>
						<th>Статус</th>

					</tr>
				</thead>
				<!-- loop over and print our customers -->
				<c:forEach var="route" items="${routes}">
				 	<c:url var="showOffer" value="/main/logistics/international/tenderOffer">	
					 	<c:param name="idRoute" value="${route.idRoute}" />					
					</c:url>
					<form:form method="post">
						<input type="hidden" value="${route.idRoute}" name="id" />
						<sec:csrfInput />
						<tr>				
							<td id="idRoute" class="none">${route.idRoute}</td>
							<td>${route.routeDirection}</td>
							<td>${route.dateLoadPreviously}</td>
							<td id="timeLoadPreviously">${route.timeLoadPreviously}</td>
							<td>${route.finishPrice} руб</td>
							<td>${route.user.companyName}</td>
							<td>${route.totalLoadPall}</td>
							<td>${route.totalCargoWeight}</td>							
							<td id="cost">${route.startPrice}</td>						
							<td id="status"><c:choose>
									<c:when test="${route.statusRoute == 1}">	
										Маршрут на бирже. <div class="resp"><a href="${showOffer}">Просмотреть предложения</a>
									<div class="coll"></div>
								</div>
									</c:when>
									<c:when test="${route.statusRoute == 4}">
										Тендер завершен. Перевозчик принят.
									</c:when>
									<c:otherwise>
										Ожидание подтверждения
									</c:otherwise>
								</c:choose></td>
					</form:form>
				</c:forEach>
			</table>
			<ul class="right-click-menu">
				<li id="l1">Истоия предложений</li>
				<li id="l2">Отправить тендер</li>
				<li id="l3">Показать точки выгрузок</li>
				<li id="l4">Редактор маршрутов</li>
				<li id="l5">Просмотреть данные по машине и водителю</li>
			</ul>
		</div>
	</div>
	<script
		src="${pageContext.request.contextPath}/resources/js/internationalManager.js"></script>

</body>
</html>