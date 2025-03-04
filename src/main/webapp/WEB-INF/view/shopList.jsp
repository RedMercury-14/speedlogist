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
<div class="container">
<div class="row">
<h1>Список магазинов</h1>
<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер</th>
					<th>Адрес</th>
					<th>Директор</th>
					<th>Отзывы</th>
					<th>Управление</th>	
				</tr>
			</thead>
				<c:forEach var="shop" items="${shops}">
					 <form:form method="post" action="shoplist/addaccount">					 
					 <input type="hidden" value="${shop.numshop}" name="id" />
					<!-- ===========================================================================-->
					<c:url var="updateLink" value="shoplist/update">
						<c:param name="shopId" value="${shop.numshop}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="shoplist/delete">
						<c:param name="shopId" value="${shop.numshop}" />
					</c:url>
					<!-- ===========================================================================-->
					<tr>
						<td>${shop.numshop}</td>
						<td>${shop.address}</td>
						<td>${shop.director.name} ${shop.director.surname}</td>
						<td><a href="${showTenderPage}">Отзывы</a></td>	
						<td>
							<c:choose>
								<c:when test="${shop.director != null}">
								<a href="${updateLink}">Редактировать</a>
							| <a href="${deleteLink}"
							onclick="if (!(confirm('Вы действительно хотите удалить машину?'))) return false">Удалить</a>
								</c:when>
							<c:otherwise>
								<input type="submit" value="Создать аккаунт" name="update">
							</c:otherwise>
			</c:choose></td>
					</tr>
           			</form:form>        
			</c:forEach>			
			</table>
			<!--<form:form action="${pageContext.request.contextPath}/main/carrier" ><input type="submit" value="Назад"></form:form>-->
</div>
</div>

</body>
</html>