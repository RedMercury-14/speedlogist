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
		<h1>Редактирование перевозчика ${carrier.companyName}</h1>
		</div>		
		<form:form modelAttribute="carrier" method="POST" action="update"> 
			<input type="hidden" value="${carrier.idUser}" name="idUser" />		
			<div class="form-group">
				<label>Фамилию:</label>
				<form:input path="surname" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Имя:</label>
				<form:input path="name" class="form-control" required="true"/>
			</div>
			<br>			
			<div class="form-group">
				<label>Отчество:</label>
				<form:input path="patronymic" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Название фирмы:</label>
				<form:input path="companyName" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Номер УНП:</label>
				<form:input path="numYNP" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Телефон:</label>
				<form:input path="telephone" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Адрес:</label>
				<form:input path="address" class="form-control" required="true"/>
			</div>
<!-- 			<div class="form-group"> -->
<!-- 				<label>Тариф:</label> -->
<%-- 				<form:input path="rate" class="form-control" required="true"/> --%>
<!-- 			</div> -->
						
			<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
			<br>
			<input type="submit" value="Редактировать" class="save"/>
		
		</form:form>
</div>
</body>
</html>