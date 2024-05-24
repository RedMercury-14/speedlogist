<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
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
<title>Регистрация нового работника</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
<jsp:include page="header.jsp"/><div class="container">
<form:form modelAttribute="user" method="POST" action="./save"> <!-- ну такое себе -->
			<div class="form-group">
				<label><c:out value="${errorMessage}" /></label>				
			</div>							
			<div class="form-group">
				<label>Введите логин (по логину будет проходить вход в систему):</label>
				<form:input path="login" class="form-control" id="login2" required="true"/>
				<div id="messageLogin"></div>
			</div>
				<br>
			<div class="form-group">
				<label>Введите пароль:</label>
				<form:input path="password" class="form-control" id="password" required="true"/>
			</div>
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
					<br>
			<div class="form-group">
				<label>Введите отчество:</label>
				<form:input path="patronymic" class="form-control" id="patronymic" required="true"/>
			</div>
			<c:choose>
				<c:when test="${!department}">
				<div class="form-group">
					<label>Введите название фирмы:</label>				
					<form:input path="companyName" class="form-control" id="companyName" required="true" value="Доброном"/>
				</div>
				</c:when>
			</c:choose>
			
			<div class="form-group">
				<label>Введите номер телефона</label>
				<form:input path="telephone" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Введите eMail</label>
				<form:input path="eMail" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Введите должность</label>
				<form:input path="department" class="form-control" required="true"/>
			</div>
			<c:choose>
				<c:when test="${!department}">
					<label>Выберите права</label>
			<select name="role" required="true">
 						<option></option>
  						<option value="1">Администратор</option>
  						<option value="2">Топ менеджер</option>
  						<option value="3">Менеджер</option>
  						<option value="4">Магазин</option>
  						<option value="6">Склад(Пока что тест карты)</option>
  						<option value="99">Отдел закупок</option>
  						<option value="10">Отдел сопровождения закупок</option>
  						<option value="11">Слот наблюдатель</option>
  						<option value="12">Аналитик</option>
  						<option value="13">Заказ транспорта для вн. пер-й</option>
 						</select>
				</c:when>
			</c:choose>
			
			
			<br>
						<td><input type="submit" value="Зарегистрировать" /></td>		
		</form:form>
		<div class="container"><input type="button" onclick="history.back();" value="Назад"/></div>
		</div>
		<br>
		<script	src="${pageContext.request.contextPath}/resources/js/registrationWorker.js" type="module"></script>
</body>
</html>