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
<style>
.raz { 
  -moz-appearance: textfield;
}
.raz::-webkit-inner-spin-button { 
  display: none;
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
<div class="row">
<c:choose>
<c:when test="${route.comments == 'international'}">
<div class="container"> <h3>Международный маршрут №${route.idRoute} ${route.routeDirection}</h3></div>
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
					<c:choose><c:when test="${route.startPrice != null}"><th>Стоимость перевозки</th></c:when></c:choose>			
					
					<th>
					<c:choose>
							<c:when test="${route.user != null}">
							</c:when>
						<c:otherwise>								
								Предложение
						</c:otherwise>
						</c:choose>
						</th>
					
				</tr>
				<c:set var="rate" value="${user.rate}"/>
			</thead>
					 <form:form method="get" action="./tenderOffer" >
					 <input type="hidden" value="${route.idRoute}" name="id" />
					<tr>
						<td>${route.routeDirection}</td>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.isSanitization}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>						
						<c:choose><c:when test="${route.startPrice != null}"><td>${route.startPrice}</td></c:when></c:choose>
						<c:choose>
							<c:when test="${flag}">
								<td>Ваше предложение ${userCost} EUR</td>
							</c:when>
							<c:otherwise>
								<td><input type="number" name="cost" size="5" required="true" class="raz"> EUR</td>
								<td>
									<input type="submit" value="поддержать цену" name="agree" class= "agree">
									<input type="hidden" value="0" name="price" size = "1"/>				
								</td>
							</c:otherwise>
						</c:choose>
						
						
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
           			</form:form>             			
			</table>
</c:when>
<c:otherwise>
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
					<th>Последняя предложенная скидка</th>
					<th>
					<c:choose>
							<c:when test="${route.user != null}">
							</c:when>
						<c:otherwise>								
								Предложение
						</c:otherwise>
						</c:choose>
						</th>
					
				</tr>
				<c:set var="rate" value="${user.rate}"/>
			</thead>
					 <form:form method="post" action="./tenderUpdate" >
					 <input type="hidden" value="${route.idRoute}" name="id" />
					<tr>
						<td>${route.routeDirection}</td>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.isSanitization}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>						
						<td>${route.cost[rate]}</td>
						<td>${route.finishPrice}</td>
						<td>
						<c:choose>
							<c:when test="${route.user != null}">
							</c:when>
							<c:otherwise>
								<c:choose>
									<c:when test="${route.finishPrice != null}">	
										<input type="submit" value="предложить скидку в %" name="agree">
										<input value="${route.finishPrice+5}" name="price" size = "1"/>%
									</c:when>							
								<c:otherwise>								
										<input type="submit" value="поддержать цену" name="agree">
										<input type="hidden" value="0" name="price" size = "1"/>
								</c:otherwise>
								</c:choose>	
							</c:otherwise>
						</c:choose>					
						</td>
						
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
				<td>${point.shop.address}				
				</td></c:forEach></tr>
           			</form:form>             			
			</table>
</c:otherwise>
</c:choose>
<input type="button" onclick="history.back();" value="Назад"/>
</div>
</div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/tenderpage.js"></script>
</body>
</html>