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
.none { 
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
<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
<c:choose>
<c:when test="${route.comments == 'international' && route.startPrice == null && route.user == null}">
<div class="container">
<div class="row">
<h3>Международный маршрут №${route.idRoute} ${route.routeDirection}</h3>
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
					 <form:form method="get" action="./tenderOffer">
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
								<td>Ваше предложение ${userCost} руб</td>
							</c:when>
							<c:otherwise>
								<td><input type="number" name="cost" size="5" required="true" class="raz"> руб</td>
								<td>
									<input type="submit" value="поддержать цену" name="agree" class= "agreeinternational">
									<input type="hidden" value="0" name="price" size = "1"/>				
								</td>
							</c:otherwise>
						</c:choose>
						</form:form>             			
			</table><br>
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
			<form:form modelAttribute="route" method="post">
					<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.position}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
           			</div>
</div>
</c:when>
<c:when test="${route.startPrice != null && route.user == null}">
<div class="container">
<div class="row">
<h3>Международный маршрут №${route.idRoute} ${route.routeDirection}</h3>
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
					<th>Последняя предложенная цена</th>
					<th>Колличество участников биржи</th>
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
					 <form:form method="get" action="./tenderOffer">
					 <input type="hidden" value="${route.idRoute}" name="id" />
					<tr>
						<td class="none">${route.startPrice}</td>
						<td>${route.routeDirection}</td>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.isSanitization}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>						
						<td class="lastCost"></td>
						<td class="numUsers"></td>
						<td><input type="number" name="cost" size="5" required="true" class="raz"> руб</td>
						<td>
						<c:choose>
							<c:when test="${route.user != null}">
							</c:when>
							<c:otherwise>	
								<input type="submit" value="предложить цену" class="agreeinternational">										
							</c:otherwise>
						</c:choose>					
						</td>
           			</form:form>             			
			</table><br>
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
			<form:form modelAttribute="route" method="post">
					<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.position}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
			</div>
</div>
</c:when>
<c:when test="${route.comments == 'international' && route.user != null}">
<div class="container">
<div class="row">
<h1>Информация по маршруту</h1><br>
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
						<td>${route.finishPrice}</td>
					</form:form>             			
			</table><br>
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
			<form:form modelAttribute="route" method="post">
					<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.order}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
			
			<div class="container">
			<label><h3>Статусы маршрута</h3></label></div>
			<table class="table table-bordered border-primary table-hover table-condensed">
				<thead class="text-center">
				<tr>
					<th><input type="button" value="Подача машины"></th>
					<th><input type="button" value="На месте зазгрузки"></th>
					<th><input type="button" value="Начали загружать"></th>
					<th><input type="button" value="Загружена"></th>
					<th><input type="button" value="На таможне отправления"></th>
					<th><input type="button" value="Затаможена"></th>
				</tr>
				<tr>
					<th><input type="button" value="В пути"></th>
					<th><input type="button" value="Проходит границу"></th>
					<th><input type="button" value="На таможне назначения"></th>
					<th><input type="button" value="Растаможена"></th>					
					<th><input type="button" value="На выгрузке"></th>				
				</tr>
			</thead>
			</table>
			</div>
</div>
</c:when>
<c:otherwise>
<div class="container">
<div class="row">
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
           			</form:form>             			
			</table><br>
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
			<form:form modelAttribute="route" method="post">
					<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.order}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
			</div>
</div>
</c:otherwise>
</c:choose>
<div class="container"><input type="button" onclick="history.back();" value="Назад"/></div>
<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/tenderPage.js"></script>
</body>
</html>