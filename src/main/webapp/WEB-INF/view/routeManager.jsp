<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
<meta name="viewport" content="width=device-width, initial-scale=1">
         <style>
             .text {
                 margin: auto;
                 text-align: center;
                 font-size: 32px;
                 line-height: 1.5;
                 text-shadow: 0 0 15px rgba(255,255,255,.5), 0 0 10px rgba(255,255,255,.5);
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
.noneRow{
		background: none;
}
         </style>
<title>Менеджер маршрутов</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<!-- reference our style sheet -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
    <form:form method="post" action="./routemanager" >
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart" value="<c:out value="${dateNow}" />"/>
	по<input type="date" name="dateFinish" value="<c:out value="${dateTomorrow}" />"/>
	<br>
   <input type="submit" value="Отобразить"></p>   
  </form:form>
</div>
<div class="container-fluid">
<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">
			<thead class="text-center">
				<tr>
					<th>Название маршрута</th>
					<th>Номер машины</th>
					<th>Номер склада</th>
					<th>Дата загрузки</th>
					<th>Время загрузки (планируемое)</th>
					<th>Финальная стоимость</th>
					<th>Тип прицепа</th>
					<th>Температура</th>
					<th>Комментарий</th>
					<th>Общее колличество паллет</th>
					<th>Общий вес</th>					
					<th>Начальные стоимости перевозки</th>
					<th>Текущая скидка</th>
					<th>Время торгов</th>
					<th>Статус</th>	
					
				</tr>
			</thead>
				<!-- loop over and print our customers -->
				<c:forEach var="route" items="${routes}">
					 <form:form method="post" action="./rouadUpdate">
					 <input type="hidden" value="${route.idRoute}" name="id" />
					<tr id="${route.idRoute}">
						<td id="routeDirection">${route.routeDirection}</td>
						<td id = "idRoute" style="display: none;">${route.idRoute}</td>
						<td>${route.truck.numTruck}; ${route.driver.name} ${route.driver.surname}, ${route.user.companyName}</td>
						<td id="numStock">${route.numStock}</td>
						<td>${route.dateLoadPreviously}</td>
						<td id = "timeLoadPreviously">${route.timeLoadPreviously}</td>
						<td>${route.startPrice} BYN</td>
						<td id="typeTrailer"><select class="sel">
 						<option>${route.typeTrailer}</option>
  						<option value="Открытый">Открытый</option>
  						<option value="Тент">Тент</option>
 						<option value="Изотермический">Изотермический</option>
  						<option value="Мебельный фургон">Мебельный фургон</option>
 						<option value="Рефрижератор">Рефрижератор</option>
 						</select></td>
						<td id="temperature">${route.temperature}</td>
						<td id="userComments">${route.userComments}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>						
						<td id="cost"> <c:forEach var="cost" items="${route.cost}">
						${cost} BYN;
						</c:forEach></td>
						<td id="finishPrice">${route.finishPrice}%</td>
						<td id = "time">${route.time}</td>
						<td id="status">
							<c:choose>
								<c:when test="${route.statusStock == 1}">
									<p>Oтправлено на склад <br>
								</c:when>
							</c:choose>	
						<c:choose>
				<c:when test="${route.statusRoute == 1}">	
				Маршрут на бирже. Идут торги
				</c:when>
				<c:when test="${route.statusRoute == 2}">	
				Тендер завершен. Отсутствует машина
				</c:when>
				<c:when test="${route.statusRoute == 3}">	
				Тендер завершен. Отсутствует водитель
				</c:when>
				<c:when test="${route.statusRoute == 4}">	
				Тендер завершен. Машина и водитель приняты.
				</c:when>
				<c:when test="${route.statusRoute == 5}">	
				Тендер отменен!
				</c:when>
				<c:otherwise>
					Ожидание подтверждения
				</c:otherwise>
			</c:choose>
			</td>                    	 
            </form:form>        
			</c:forEach>			
			</table>
			<ul class="right-click-menu">
            <li id="l1">Отправить на склад</li>
            <li id="l2">Отправить тендер</li>
            <li id="l3">Показать точки выгрузок</li>
            <li id="l4">Редактор маршрутов</li>
        </ul>
<script	src="${pageContext.request.contextPath}/resources/js/routeManager.js" type="module"></script>

			</div>			
		</div>
	</div>


</body>
</html>