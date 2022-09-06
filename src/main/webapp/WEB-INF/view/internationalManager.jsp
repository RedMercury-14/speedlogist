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
[data-tooltip] {
    position: relative; /* Относительное позиционирование */ 
   }
   [data-tooltip]::after {
    content: attr(data-tooltip); /* Выводим текст */
    position: absolute; /* Абсолютное позиционирование */
    width: 200px; /* Ширина подсказки */
    left: 0; top: 0; /* Положение подсказки */
    background: #d3d3d3; /* Синий цвет фона */
    color: #000000; /* Цвет текста */
    padding: 0.5em; /* Поля вокруг текста */
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3); /* Параметры тени */
    pointer-events: none; /* Подсказка */
    opacity: 0; /* Подсказка невидима */
    transition: 1s; /* Время появления подсказки */
   } 
   [data-tooltip]:hover::after {
    opacity: 1; /* Показываем подсказку */
    top: 2em; /* Положение подсказки */
   }
/*    вверху подсказка */
/* MODAL */
body {font-family: Arial, Helvetica, sans-serif;}

/* Модальный (фон) */
.modal {
  display: none; /* Скрыто по умолчанию */
  position: fixed; /* Оставаться на месте */
  z-index: 1; /* Сидеть на вершине */
  padding-top: 100px; /* Расположение коробки */
  left: 0;
  top: 0;
  width: 100%; /* Полная ширина */
  height: 100%; /* Полная высота */
  overflow: auto; /* Включите прокрутку, если это необходимо */
  background-color: rgb(0,0,0); /* Цвет запасной вариант  */
  background-color: rgba(0,0,0,0.4); /*Черный с непрозрачностью */
}

/* Модальное содержание */
.modal-content {
  position: relative;
  background-color: #fefefe;
  margin: auto;
  padding: 0;
  border: 1px solid #888;
  width: 80%;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
  -webkit-animation-name: animatetop;
  -webkit-animation-duration: 0.4s;
  animation-name: animatetop;
  animation-duration: 0.4s
}

/* Добавить анимацию */
@-webkit-keyframes animatetop {
  from {top:-300px; opacity:0} 
  to {top:0; opacity:1}
}

@keyframes animatetop {
  from {top:-300px; opacity:0}
  to {top:0; opacity:1}
}

/* Кнопка закрытия */
.closer {
  color: white;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.closer:hover,
.closer:focus {
  color: #000;
  text-decoration: none;
  cursor: pointer;
}

.modal-header {
  padding: 2px 16px;
  background-color: #5cb85c;
  color: white;
}

.modal-body {padding: 2px 16px;}

.modal-footer {
  padding: 2px 16px;
  background-color: #5cb85c;
  color: white;
}
/* end */
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
.endRow{
		background: #9ee9ffdb;
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
	<sec:authorize access="isAuthenticated()">  
        	<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
    	</sec:authorize>
		<input type="hidden" value="${roles}" id="role">
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
				маршрут</a> | <a href="<spring:url value="/main/logistics/international/disposition"/>">Диспозиция</a>
			<table	class="table table-bordered border-primary table-hover table-condensed"	id="sort">
				<thead class="text-center">
					<tr>
						<th>Название маршрута</th>
						<th>Дата загрузки</th>
						<th>Время загрузки (планируемое)</th>
						<th>Выставляемая стоимость</th>
						<th>Перевозчик</th>
						<th>Номер машины</th>
						<th>Данные по водителю</th>
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
							<td><p data-tooltip = "${route.truck.typeTrailer}">${route.truck.numTruck} / ${route.truck.numTrailer}</p></td>
							<td><a id="myBtn">${route.driver.surname} ${route.driver.name} ${route.driver.patronymic}</a></td>
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
									<c:when test="${route.statusRoute == 6}">
										Маршрут завершен.
									</c:when>
									<c:otherwise>
										Ожидание подтверждения
									</c:otherwise>
								</c:choose></td>							
					</form:form>
						<!-- The Modal -->
<div id="myModal" class="modal">
  <!-- Модальное содержание -->
  <div class="modal-content">
    <div class="modal-header">
      <span class="closer">&times;</span>
      <h2>Данные по водителю</h2>
    </div>
    <div class="modal-body">
      <p>${route.driver.surname} ${route.driver.name} ${route.driver.patronymic}</p>
      <p>Паспортные данные: ${route.driver.numPass}</p>
      <p>Номер телефона: ${route.driver.telephone}</p>
    </div>
  </div>
</div>
				</c:forEach>
			</table>
			<ul class="right-click-menu">
				<li id="l1">Истоpия предложений</li>
				<li id="l2">Отправить тендер</li>
				<li id="l3">Показать точки выгрузок</li>
				<li id="l4">Редактор маршрутов</li>
				<li id="l5">Завершить маршрут</li>
			</ul>
		</div>
	</div>

	<script
		src="${pageContext.request.contextPath}/resources/js/internationalManager.js" type="module"></script>

</body>
</html>