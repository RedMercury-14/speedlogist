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
<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
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
<div class="container">
		<div class="row">
		<h1>Редaктирование автомобиля</h1>
		</div>		
		<form:form modelAttribute="truck" method="POST" action="./save">
		<input type="hidden" value="${truck.idTruck}" name="id"/>
			<div class="form-group">
				<label>Введите госномер авто:</label>
				<form:input  path="numTruck" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите марку авто:</label>
				<form:input path="brandTruck" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Введите модель авто:</label>
				<form:input path="modelTruck" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Введите госномер прицепа (если имеется):</label>
				<form:input path="numTrailer" class="form-control"/>				
			</div>
			<div class="form-group">
			<label>Выберите тип прицепа:</label>
 					<p><select name="typeTrailer" required>
 						<option></option>
  						<option>Открытый</option>
  						<option>Тент</option>
 						<option>Изотермический</option>
  						<option>Мебельный фургон</option>
 						<option>Рефрижератор</option>
 						</select></p>		
			</div>	
			<div class="form-group">
				<label>Введите грузоподъёмность(в киллограммах):</label>
				<form:input path="cargoCapacity" class="form-control" required="true"/>				
			</div>
			<div class="form-group">
				<label>Введите паллетовместимость (е-палл):</label>
				<form:input path="pallCapacity" class="form-control" required="true"/>				
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