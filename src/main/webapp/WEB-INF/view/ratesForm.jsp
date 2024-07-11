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
		<div class="row">
		<h1>Добавление тарифа</h1>
		</div>		
		<form:form modelAttribute="rate" method="POST">
		
			<div class="form-group">
				<label>Каста:</label>
				<form:input path="caste" class="form-control" required="true"/>
				
			</div>
					
			<div class="form-group">
				<label>Введите грузоподъемность (кг):</label>
				<form:input path="weight" class="form-control" required="true"/>
			</div>
				<br>
			<div class="form-group">
				<label>Введите паллетовместимость:</label>
				<form:input path="pall" class="form-control" required="true"/>
			</div>
			<br>
			<div class="form-group">
				<label>Выберите тип транспорта:</label>
				<select name="type" required>
				<option></option>
				<option value="изотерма">Изотерма</option>
				<option value="рефрижератор">Рефрижератор</option>
				</select>
			</div>
			<br>
			<div class="form-group">
				<label>Введите тариф за 1 км до 400 км:</label>
				<form:input path="before400" class="form-control" required="true"/>
			</div>
					<br>
			<div class="form-group">
				<label>Введите тариф за 1 км свыше 400 км:</label>
				<form:input path="after400" class="form-control" required="true"/>
			</div>
			<div class="form-group">
				<label>Введите часовой тариф:</label>
				<form:input path="hour" class="form-control" required="true"/>
			</div>
			<div class="form-group">
			<c:out value="${errorMessage}" />
			</div>
			<br>
						<td><input type="submit" value="Сохранить" class="save" /></td>
		
		</form:form>
</div>
</body>
</html>