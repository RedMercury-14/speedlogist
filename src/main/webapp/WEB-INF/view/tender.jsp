<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>    
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
	<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
	type="text/javascript"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container-fluid">
<div class="table-responsive">
<h1>Текущие заказы</h1>
<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Название маршрута</th>
					<th>Дата загрузки</th>
					<th>Время загрузки (планируемое)</th>
					<th>Санобработка</th>
					<th>Температура</th>
					<th>Общее колличество паллет</th>
					<th>Общий вес</th>					
					<th>Стоимость перевозки</th>
					<th>Колличество точек выгрузок</th>
					<c:set var="rate" value="${user.rate}"/>
				</tr>
			</thead>
				<c:forEach var="route" items="${routes}">
					 <form:form method="post" action="./tenderUpdate">					 
					 <input type="hidden" value="${route.idRoute}" name="id" />
					 <c:url var="showTenderPage" value="/main/carrier/tender/tenderpage">	
					 	<c:param name="routeId" value="${route.idRoute}" />					
					</c:url>		
					<tr>
						<td> <a href="${showTenderPage}">${route.routeDirection}</a></td>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.isSanitization}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>						
						<td>${route.cost[rate]}</td>
						<td>${route.numPoint}</td>	
					</tr>
           			</form:form>        
			</c:forEach>			
			</table>
			<form:form action="${pageContext.request.contextPath}/main/carrier" ><input type="submit" value="Назад"></form:form>
</div>
</div>
</body>
</html>