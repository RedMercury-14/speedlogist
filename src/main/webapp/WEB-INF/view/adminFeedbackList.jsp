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
			<table  class="table">
				<tr>
					<th>От магазина</th>
					<th>ФИО директора</th>
					<th>Водитель</th>
					<th>Текст</th>
					<th>Дата</th>
					<th>Маршрут</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="feedback" items="${feedback}">
				 <c:url var="showTenderPage" value="/main/admin/carrier/feedback/tender">
						<c:param name="idRouteHasShop" value="${feedback.idRouteHasShop}" />
					</c:url>

					<tr>
						<td>${feedback.shop.numshop}</td>
						<td>${feedback.from.surname} ${feedback.from.name}</td>
						<td>${feedback.user.surname} ${feedback.user.name}</td>
						<td><p><textarea rows="8" cols="45" readonly>${feedback.message}</textarea></p></td>
						<td>${feedback.date}</td>
						<td><a href="${showTenderPage}">Просмотреть маршрут</a></td>
					</tr>

				</c:forEach>

			</table>			
		</div>
	</div>
</body>
</html>