<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>

<head>
<meta charset="UTF-8">
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
<title>Регистрация</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
		<div class="row">
		<h1>Регистрация аккаунта магазина ${shop.numshop}</h1>
		</div>		
		<form:form modelAttribute="user" method="POST" action="${pageContext.request.contextPath}/main/registration/form"> <!-- ну такое себе -->
			<input type="hidden" value="${shop.numshop}" name="idShop" />
			<input type="hidden" value='${user.department}' name="department" />
			<input type="hidden" value='${user.companyName}' name="companyName" />
			<input type="hidden" value="${user.idUser}" name="idUser" />					
			<div class="form-group">
				<label>Введите логин (по логину будет проходить вход в систему):</label>
				<form:input path="login" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Введите пароль:</label>
				<form:input path="password" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Повторите пароль:</label>
				<form:input path="confirmPassword" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Введите фамилию:</label>
				<form:input path="surname" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Введите имя:</label>
				<form:input path="name" class="form-control" required="true"/>
			</div>
			<br>			
			<div class="form-group">
				<label>Введите отчество:</label>
				<form:input path="patronymic" class="form-control" required="true"/>
			</div>			
			<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
			<br>
						<td><c:choose>
								<c:when test="${user.shop != null}">
								<input type="submit" value="Редактировать" class="save" name="flag"/>
								</c:when>
							<c:otherwise>
								<input type="submit" value="Зарегистрировать" class="save" />
							</c:otherwise>
						</c:choose></td>
		
		</form:form>
</div>
</body>
</html>