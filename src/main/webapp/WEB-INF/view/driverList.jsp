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
<h1>Управление персоналом</h1>
добавить, редоктировать, удалить и пр
<div class="container">
		<div class="row">	
				<a href = "<spring:url value="/main/carrier/controlpark/driverlist/add"/>">Добавить водителя</a> <br>
			<table  class="table">
				<tr>
					<th>ФИО</th>
					<th>Логин</th>
					<th>Номер водительского удостоверения</th>
					<th>Моб. телефон</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="driver" items="${drivers}">

					<!-- ===========================================================================-->
					<c:url var="updateLink" value="/main/carrier/controlpark/driverlist/update">
						<c:param name="driverId" value="${driver.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="/main/carrier/controlpark/driverlist/delete">
						<c:param name="driverId" value="${driver.idUser}" />
					</c:url>
					<!-- ===========================================================================-->

					<tr>
						<td>${driver.surname} ${driver.name} ${driver.patronymic}</td>
						<td>${driver.login}</td>
						<td>${driver.numDriverCard}</td>
						<td>${driver.telephone}</td>

						<td>
							<a href="${updateLink}">Редактировать</a>
							| <a href="${deleteLink}"
							onclick="if (!(confirm('Вы действительно хотите удалить водителя из вашего списка сотрудников?'))) return false">Удалить</a>
						</td>

					</tr>

				</c:forEach>

			</table>			
		</div>
	</div>
</body>
</html>