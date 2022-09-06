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
<style type="text/css">
	.none{
		display: none;
	}
	.activRow{
		background: #c4ffe1db;
	}
	.attentionRow{
		background: #dce37266;
}
</style>
<title>Insert title here</title>
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

<div class="container">
<div class="table-responsive">
<h1>Текущие заказы</h1>
<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Название маршрута</th>
					<th>Дата загрузки</th>
					<th>Время загрузки (планируемое)</th>
					<th>Общее колличество паллет</th>
					<th>Общий вес</th>
					<th>Температура</th>
					<th>Предложенная цена за перевозку</th>
					<th>Ваше предложение</th>
					
								
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
						<td class="none">${route.idRoute}</td>
						<td> <a href="${showTenderPage}">${route.routeDirection}</a></td>
						<td width="100">${route.dateLoadPreviously}</td>
						<td width="50">${route.timeLoadPreviously}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>
						<td>${route.temperature}</td>
						<c:choose>
							<c:when test="${route.comments == 'international' && route.startPrice != null}">
								<td class="targetCost"></td>
							</c:when>
							<c:when test="${route.comments == 'international' && route.startPrice == null}">
								<td>Ваше предложение</td>
							</c:when>
							<c:otherwise>
								<td>${route.cost[rate]}</td>
							</c:otherwise>
						</c:choose>
						<td width="100"><div id="offer"></div></td>
						
												
					</tr>
           			</form:form>        
			</c:forEach>			
			</table>
			<form:form action="${pageContext.request.contextPath}/main/carrier" ><input type="submit" value="Назад"></form:form>
</div>
</div>
<script	src="${pageContext.request.contextPath}/resources/js/tender.js" type="module"></script>
</body>
</html>