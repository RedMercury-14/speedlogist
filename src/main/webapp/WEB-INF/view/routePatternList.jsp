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
<title>Шаблоны маршрутов</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"
	type="text/javascript"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
	<div class="container">
		<div class="table-responsive">
			<a href="<spring:url value="/main/admin/routePattern/add"/>">Создать
				маршрут</a> 
			<table	class="table table-bordered border-primary table-hover table-condensed"	id="sort">
				<thead class="text-center">
					<tr>
						<th>Название маршрута</th>
						<th>Общее колличество паллет</th>
						<th>Общий вес</th>						
						<th>Начальные стоимости перевозки</th>
					</tr>
				</thead>
				<c:forEach var="route" items="${routes}">
					<c:url var="deleteLink" value="/main/admin/routePattern/delete">
						<c:param name="idRoute" value="${route.idRoute}" />
					</c:url>
					<form:form method="post">
						<input type="hidden" value="${route.idRoute}" name="idRoute" />
						<sec:csrfInput />
						<tr>
							<td id="routeDirection"><a href="/speedlogist/main/admin/routePattern/showRoute?idRoute=${route.idRoute}">${route.routeDirection}</a></td>
							<td>${route.totalLoadPall}</td>
							<td>${route.totalCargoWeight}</td>							
							<td id="cost">${route.startPrice} BYN</td>	
							<td><a href="${deleteLink}"
							onclick="if (!(confirm('Are you sure you want to delete this customer?'))) return false">Удалить</a></td>
						</tr>							
					</form:form>
				</c:forEach>
			</table>			
		</div>
	</div>
</body>
</html>