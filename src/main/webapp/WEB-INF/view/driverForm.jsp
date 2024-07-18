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
<c:choose>
		<c:when test="${check == 'international'}">
		<h1>Добавить водителя</h1>				
		<form:form modelAttribute="user" method="POST" action="./save">
		<input type="hidden" value="${user.idUser}" name="id"/>
			<div class="form-group">
				<label>Введите имя:</label>
				<form:input  path="name" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите фамилию:</label>
				<form:input path="surname" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Введите отчество:</label>
				<form:input path="patronymic" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Введите серию и номер паспорта:</label>
				<form:input path="seriesAndNumberPass" class="form-control" required="true" placeholder="МР2223344"/>
			</div>
			<div class="form-group">
				<label>Кем выдан паспорт:</label>
				<form:input path="issuedBy" class="form-control" required="true" placeholder="Советским РУВД г.Минск"/>
			</div>
			<div class="form-group">
				<label>Дата выдачи:</label>
				<form:input path="validityPass" class="form-control" required="true" placeholder="05.05.2015"/>
			</div>
			<div class="form-group">
				<label>Личный номер:</label>
				<form:input path="personalNumberPass" class="form-control" required="true"/>
			</div>
									
			<div class="form-group">
				<label>Введите номер мобильного телефона:</label>
				<form:input path="telephone" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
			<br>
						<td><input type="submit" value="Сохранить" class="save" /></td>
		
		</form:form>
		</c:when>
		
		<c:otherwise>
		<h1>Редaктирование водителя</h1>				
		<form:form modelAttribute="user" method="POST" action="./save">
		<input type="hidden" value="${user.idUser}" name="id"/>
			<div class="form-group">
				<label>Введите имя:</label>
				<form:input  path="name" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите фамилию:</label>
				<form:input path="surname" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Введите отчество:</label>
				<form:input path="patronymic" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Данные паспорта (MP2569865; выдан Советским РУВД г.Минска 05.05.2020; HT54687EА898484 ):</label>
				<form:input path="numPass" class="form-control" required="true" />
			</div>		
			
				<c:choose>
							<c:when test="${user.name == null}">	
									
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
				<form:input path="confirmPassword"  class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Введите номер водительского удостоверения:</label>
				<form:input path="numDriverCard" class="form-control"/>
			</div>
							</c:when>
							<c:otherwise>
							</c:otherwise>
				</c:choose>		
			<div class="form-group">
				<label>Введите номер мобильного телефона:</label>
				<form:input path="telephone" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
			<br>
						<td><input type="submit" value="Сохранить" class="save" /></td>
		
		</form:form>
		</c:otherwise>
</c:choose>
</div>

</body>
</html>