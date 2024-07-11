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
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"
	type="text/javascript"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<h1>Страница магазина</h1>
<div class="container">
<div class="row">
    <form:form method="post" action="shop" >
			<label>Выберите дату:</label>				
			<input type="date" name="dateStart" value="<c:out value="${dateNow}" />"/>
	<br>
   <p><input type="submit" value="Отобразить"></p>   
  </form:form>
  <table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Дата</th>
					<th>Перевозчик</th>
					<th>ФИО водителя</th>					
					<th>Колл-во паллет</th>
					<th>Вес</th>	
					<th>Управление</th>
				</tr>
			</thead>				
					<form:form method="post" action="shop/proof">					 
					 <input type="hidden" value="${shop.numshop}" name="idShop" />
					<!-- ===========================================================================-->
					<c:url var="feedbackLink" value="shop/feedback">
						<c:param name="idRouteHasShop" value="${routeHasShop.idRouteHasShop}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="confirmLink" value="shop/confirm">
						<c:param name="routeId" value="${route.idRoute}" />
						<c:param name="routeHasShopId" value="${routeHasShop.idRouteHasShop}" />
					</c:url>
					<!-- ===========================================================================-->
					<tr>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.user.companyName}</td>
						<td>${route.driver.name} ${route.driver.surname} ${route.driver.telephone}</td>
						<td>${routeHasShop.pall}</td>
						<td>${routeHasShop.weight}</td>
						<td>
							<c:choose>
								<c:when test="${route.user != null && routeHasShop.status == null}">
								<a href="${feedbackLink}">Написать претензию</a>
							| <a href="${confirmLink}" onclick="if (!(confirm('Машина действительно прибыла? В случае ошибки удалить статуc будет невозможно!'))) return false">Подтвердить прибытие</a>
								</c:when>
								<c:when test="${route.user != null && routeHasShop.status != null}">
								<a href="${feedbackLink}">Написать претензию</a>
								</c:when>
							<c:otherwise>
							</c:otherwise>
			</c:choose></td>
					</tr>
           			</form:form> 	
			</table>
			<!--<form:form action="${pageContext.request.contextPath}/main/carrier" ><input type="submit" value="Назад"></form:form>-->
  </div>
</div>
</body>
</html>