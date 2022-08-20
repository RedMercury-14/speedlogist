<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
<label><h3>Шапка тендера</h3></label>
<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">
			<thead class="text-center">
				<tr>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Начальные стоимости перевозки</th>
					
				</tr>
			</thead>
				<form:form modelAttribute="route" method="post">
					<tr>
						<td><input type = "date" name="date" value="${route.dateLoadPreviously}" required="true" /></td>
						<td><input type = "time" name="timeOfLoad" value="${route.timeLoadPreviously}" /></td>
						<td><form:input path="temperature" value="${route.temperature}" size="2"/></td>
						<td><form:input path="totalLoadPall" value="${route.totalLoadPall}" size="2"/></td>
						<td><form:input path="totalCargoWeight" value="${route.totalCargoWeight}" size="2"/></td>
						<td><form:input path="routeDirection" value="${route.routeDirection}" required="true"/></td>
						<td><form:input path="startPrice" value="${route.startPrice}" size="2"/></td>						
						<tr><th>Номер точки</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.order}</td>                
				</c:forEach></tr>
							<tr><th>Вес</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.weight}</td>                
				</c:forEach></tr>
							<tr><th>Паллеты</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.pall}</td>                
				</c:forEach></tr>							
							<tr><th>Адрес</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.address}				
				</td></c:forEach></tr>
				<input type="submit" value="Создать маршрут">				
            </form:form>   			
			</table>
			</div>
			</div>
</body>
</html>