<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>

    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>

<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
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
	<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<title>Регистрация</title>
</head>
<body>

<jsp:include page="header.jsp"/>
<div class="container">
		<div class="row">
		<h1>Регистрация перевозчика</h1>
		<div id="time"></div>
		</div>		
		<form:form modelAttribute="user" method="POST" action="${pageContext.request.contextPath}/main/registration/form"> <!-- ну такое себе -->
		<form:input type="hidden" value="${user.check}" path="check" />
			<div class="form-group">
				<label>Введите номер договора:</label>
				<form:input path="numContract" class="form-control" id="numContract" required="true"/>							
			</div>
					
			<div class="form-group">
				<label>Введите логин (по логину будет проходить вход в систему):</label>
				<form:input path="login" class="form-control" id="login" required="true"/>
			</div>
			<div id="messageLogin"></div>
				<br>
			<div class="form-group">
				<label>Введите пароль:</label>
				<form:input path="password" class="form-control" id="password" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Повторите пароль:</label>
				<form:input path="confirmPassword" class="form-control" id="confirmPassword" required="true"/>
			</div>
			<div id="message"></div>
			<br>
			<div class="form-group">
				<label>Введите имя:</label>
				<form:input path="name" class="form-control" id="name" required="true"/>
			</div>
					<br>
			<div class="form-group">
				<label>Введите фамилию:</label>
				<form:input path="surname" class="form-control" id="surname" required="true"/>
			</div>
			<div class="form-group">
				<label>Введите название фирмы:</label>
				<form:input path="companyName" class="form-control" id="companyName" required="true"/>
			</div>
			<div class="form-group">
				<label>Введите номер УНП:</label>
				<form:input path="numYNP" class="form-control" id="numYNP" required="true"/>
			</div>
			<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
			<br>
						<td><input type="submit" value="Зарегистрироваться" class="save" id="send" /></td>		
		</form:form>
		
		<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/registration.js"></script>
		
</div>
</body>
</html>