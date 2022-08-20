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
<div class="container">
		<div class="row">	
				<a href = "<spring:url value="/main/admin/carrier/proof"/>">Неподтвержденные перевозчики</a>  |  <a href = "<spring:url value="/main/admin/carrier/block"/>">Заблокированные перевозчики</a> <br>
			<table  class="table">
				<tr>
					<th>Ниминование юрлица</th>
					<th>ФИО директора</th>
					<th>Телефон</th>
					<th>Адрес</th>
					<th>Лояльность</th>
					<th>Коментарий</th>
					<th>Тариф</th>
					<th>Прочее</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="carrier" items="${carriers}">

					<!-- ===========================================================================-->
					<c:url var="updateLink" value="carrier/update">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="carrier/delete">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="parkLink" value="carrier/park">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="feedbackLink" value="carrier/feedback">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->

					<tr>
						<td>${carrier.companyName}</td>
						<td>${carrier.surname} ${carrier.name} ${carrier.patronymic}</td>
						<td>${carrier.telephone}</td>
						<td>${carrier.address}</td>
						<td>${carrier.loyalty}</td>
						<td>в разр.</td>
						<td>${carrier.rate}</td>

						<td>
							<a href="${updateLink}">Редактировать</a>
							|<a href="${parkLink}">Автопарк перевозчика</a> |<a href="${deleteLink}"
							onclick="if (!(confirm('Уверены что хотите заблокировать перевозчика?'))) return false">Заблокировать</a>|
							<a href="${feedbackLink}">Претензии к перевозчику</a>
						</td>

					</tr>

				</c:forEach>

			</table>			
		</div>
	</div>
</body>
</html>