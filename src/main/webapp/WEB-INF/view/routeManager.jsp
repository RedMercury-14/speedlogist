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
         </style>
<title>Менеджер маршрутов</title>
<!-- reference our style sheet -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
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
					<th><p class="text-center">Номер маршрута</p></th>
					<th>Номер машины</th>
					<th>Номер склада</th>
					<th>Дата загрузки</th>
					<th>Время загрузки (планируемое)</th>
					<th>Финальная стоимость</th>
					<th>Температура</th>
					<th>Общее колличество паллет</th>
					<th>Общий вес</th>
					<th>Название маршрута</th>
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
					<tr>
						<td id = "idRoute">${route.idRoute}</td>
						<td>${route.truck.numTruck}; ${route.driver.name} ${route.driver.surname}, ${route.user.companyName}</td>
						<td id="numStock">${route.numStock}</td>
						<td>${route.dateLoadPreviously}</td>
						<td id = "timeLoadPreviously">${route.timeLoadPreviously}</td>
						<td>${route.startPrice} руб</td>
						<td id="temperature">${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>
						<td>${route.routeDirection}</td>
						<td id="cost"> <c:forEach var="cost" items="${route.cost}">
						${cost} руб;
						</c:forEach></td>
						<td>${route.finishPrice}%</td>
						<td id = "time">${route.time}</td>
						<td>
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

<script src="${pageContext.request.contextPath}/resources/tablesort/src/tablesort.js"></script>
<script src="${pageContext.request.contextPath}/resources/tablesort/test/tape.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.dotsep.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.date.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.number.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.monthname.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.filesize.js"></script>

<script>
	table = document.getElementById('sort');
	console.log(table);
	new Tablesort(table);
</script>
			</div>			
		</div>
	</div>


</body>
</html>