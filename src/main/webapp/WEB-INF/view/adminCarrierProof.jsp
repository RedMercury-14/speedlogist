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
<c:choose>
	<c:when test="${international}">
								<div class="container">
									<div class="row">
									<h1>Заблокированные международные перевозчики</h1>
									<table  class="table">
				<tr>
					<th>Ниминование юрлица</th>
					<th>ФИО сотрудника</th>
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
				<form:form method="post">
				<input type="hidden" value="${carrier.numYNP}" name="carrierYNP" />
					<!-- ===========================================================================-->
					<c:url var="updateLink" value="carrier/update">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="carrier/delete">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->

					<tr>
						<td>${carrier.companyName}</td>
						<td>${carrier.director}</td>
						<td>${carrier.surname} ${carrier.name} ${carrier.patronymic}</td>
						<td>${carrier.telephone}</td>
						<td>${carrier.address}</td>
						<td>${carrier.loyalty}</td>
						<td>в разр.</td>
						<td>${carrier.rate}</td>

						<td>
							<p><input type="submit" value="Разблокировать"></p>
						</td>

					</tr>
					</form:form>
				</c:forEach>
				
			</table>
									</div></div>
	</c:when>
	<c:when test="${proof}">
		<div class="container">
									<div class="row">
									<h1>Неподтвержденные международные перевозчики</h1>
									<table  class="table">
				<tr>
					<th>Ниминование юрлица</th>
					<th>eMail</th>
					<th>ФИО сотрудника</th>
					<th>ФИО директора</th>
					<th>Телефон</th>
					<th>Адрес</th>
					<th>Номер УНП</th>
					<th>Номер договора</th>
					<th>Реквизиты</th>
					<th>Прочее</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="carrier" items="${carriers}">
				<form:form method="post">
				<input type="hidden" value="${carrier.idUser}" name="carrierId" />
					<!-- ===========================================================================-->
					<c:url var="updateLink" value="carrier/update">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="carrier/delete">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->

					<tr>
						<td>${carrier.companyName}</td>
						<td>${carrier.eMail}</td>
						<td>${carrier.surname} ${carrier.name} ${carrier.patronymic}</td>
						<td>${carrier.director}</td>
						<td>${carrier.telephone}</td>
						<td>${carrier.address}</td>
						<td>${carrier.numYNP}</td>
						<td>${carrier.numContract}</td>
						<td>${carrier.requisites}</td>
						<td>в разр.</td>

						<td>
							<p><input type="submit" value="Подтвердить"></p>
						</td>

					</tr>
					</form:form>
				</c:forEach>
				
			</table>
									</div></div>
	</c:when>
	<c:otherwise>
		<c:choose>
								<c:when test="${isBlock == true}">
								<div class="container">
									<div class="row">
									<h1>Заблокированные перевозчики</h1>
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
				<form:form method="post" action="block">
				<input type="hidden" value="${carrier.idUser}" name="carrierId" />
					<!-- ===========================================================================-->
					<c:url var="updateLink" value="carrier/update">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="carrier/delete">
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
							<p><input type="submit" value="Разблокировать"></p>
						</td>

					</tr>
					</form:form>
				</c:forEach>
				
			</table>
									</div></div>	
								</c:when>
							<c:otherwise>
								<div class="container">
									<div class="row">
									<h1>Неподтвержденные перевозчики</h1>
									<table  class="table">
				<tr>
					<th>Ниминование юрлица</th>
					<th>ФИО директора</th>
					<th>Телефон</th>
					<th>Адрес</th>
					<th>Лояльность</th>
					<th>Тариф</th>
					<th>Прочее</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="carrier" items="${carriers}">
				<form:form method="post" action="proof">
				<input type="hidden" value="${carrier.idUser}" name="carrierId" />
					<!-- ===========================================================================-->
					<c:url var="updateLink" value="carrier/update">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					<c:url var="deleteLink" value="carrier/delete">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>
					<!-- ===========================================================================-->
					 <c:url var="showPark" value="park">
						<c:param name="carrierId" value="${carrier.idUser}" />
					</c:url>

					<tr>
						<td>${carrier.companyName}</td>
						<td>${carrier.surname} ${carrier.name} ${carrier.patronymic}</td>
						<td>${carrier.telephone}</td>
						<td>${carrier.address}</td>
						<td>${carrier.loyalty}</td>				
						<td><input name="rate" size="2" required/></td>
						<td><a href="${showPark}">Автопарк</a></td>

						<td>
							<p><input type="submit" value="Подтвердить" name="update"></p>
						</td>

					</tr>
					</form:form>
				</c:forEach>
				
			</table>
									</div></div>
							</c:otherwise>
</c:choose>
	</c:otherwise>
</c:choose>


</body>
</html>