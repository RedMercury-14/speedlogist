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
<sec:authorize access="hasRole('ROLE_ADMIN')" var="authenticated" />
<div class="container">
		<div class="row">	
		<c:choose>
		<c:when test="${authenticated == false && check == null || check == 'step3' || check != 'international'}">
		<h1>Текущий автопарк ${user.check}</h1>
			<table  class="table">
				<tr>
					<th>Госномер тягача</th>
					<th>Марка</th>
					<th>Модель</th>
					<th>Госномер прицепа</th>
					<th>Грузоподъемность</th>
					<th>Паллетовместимость (е-палл)</th>
					<th>Тип кузова</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="truck" items="${trucks}">
					<tr>
						<td>${truck.numTruck}</td>
						<td>${truck.brandTruck}</td>
						<td>${truck.modelTruck}</td>
						<td>${truck.numTrailer}</td>
						<td>${truck.cargoCapacity}</td>
						<td>${truck.pallCapacity}</td>
						<td>${truck.typeTrailer}</td>
				</c:forEach>
			</table>
		</c:when>
		<c:when test="${authenticated}">
		<h1>Контроль автопарка перевозчика ${user.companyName} </h1>		
			<!-- <br><a href = "<spring:url value="/main/admin/carrier/park/add"/>">Добавить авто</a> <br> -->
			<table  class="table">
				<tr>
					<th>Госномер тягача</th>
					<th>Марка</th>
					<th>Модель</th>
					<th>Госномер прицепа</th>
					<th>Грузоподъемность</th>
					<th>Паллетовместимость (е-палл)</th>
					<th>Тип кузова</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="truck" items="${trucks}">

					<!-- ===========================================================================-->
					<c:url var="updateLink" value="/main/admin/carrier/park/update">
						<c:param name="truckId" value="${truck.idTruck}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="/main/admin/carrier/park/delete">
						<c:param name="truckId" value="${truck.idTruck}" />
					</c:url>
					<!-- ===========================================================================-->

					<tr>
						<td>${truck.numTruck}</td>
						<td>${truck.brandTruck}</td>
						<td>${truck.modelTruck}</td>
						<td>${truck.numTrailer}</td>
						<td>${truck.cargoCapacity}</td>
						<td>${truck.pallCapacity}</td>
						<td>${truck.typeTrailer}</td>

						<td>
							<a href="${updateLink}">Редактировать</a>
							| <a href="${deleteLink}"
							onclick="if (!(confirm('Вы действительно хотите удалить машину?'))) return false">Удалить</a>
						</td>
					</tr>
				</c:forEach>
			</table>		
		</c:when>
		<c:otherwise>
<h1>Добваление машин</h1>
		<p>Добавьте машины, которые будут учавствовать в перевозке</p>
		
			<br><a href = "<spring:url value="/main/carrier/controlpark/trucklist/add"/>">Добавить авто</a> <br>
			<table  class="table">
				<tr>
					<th>Госномер тягача</th>
					<th>Марка</th>
					<th>Модель</th>
					<th>Госномер прицепа</th>
					<th>Грузоподъемность</th>
					<th>Паллетовместимость (е-палл)</th>
					<th>Тип кузова</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="truck" items="${trucks}">

					<!-- ===========================================================================-->
					<c:url var="updateLink" value="/main/carrier/controlpark/trucklist/update">
						<c:param name="truckId" value="${truck.idTruck}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="/main/carrier/controlpark/trucklist/delete">
						<c:param name="truckId" value="${truck.idTruck}" />
					</c:url>
					<!-- ===========================================================================-->

					<tr>
						<td>${truck.numTruck}</td>
						<td>${truck.brandTruck}</td>
						<td>${truck.modelTruck}</td>
						<td>${truck.numTrailer}</td>
						<td>${truck.cargoCapacity}</td>
						<td>${truck.pallCapacity}</td>
						<td>${truck.typeTrailer}</td>

						<td>
							<a href="${updateLink}">Редактировать</a>
							| <a href="${deleteLink}"
							onclick="if (!(confirm('Вы действительно хотите удалить машину?'))) return false">Удалить</a>
						</td>
					</tr>
				</c:forEach>
			</table>	
			<c:choose>
			<c:when test="${check != 'international'}">
				<form:form action="trucklist" method="post">
					<input type="submit" value="Подтвердить автопарк">
				</form:form>
			</c:when>
			</c:choose>		
								
		</c:otherwise>
		</c:choose>							
		</div>
	</div>
</body>
</html>