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
<title>Логистика</title>

<!-- reference our style sheet -->

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
</head>
<body>
<jsp:include page="header.jsp"/>
<h1>logistics</h1>

  <form:form enctype="multipart/form-data" method="post" action="./logistics/addshop?${_csrf.parameterName}=${_csrf.token}" >
  <sec:csrfInput />
  <label for="make">Добавить магазины</label>
   <p><input type="file" name="file">
   <br>
   <input type="submit" value="Отправить"></p>
  </form:form>
  <br><br>
    <form:form enctype="multipart/form-data" method="post" action="./logistics/upload?${_csrf.parameterName}=${_csrf.token}" >
  <sec:csrfInput />
  
  <label for="make">Создать маршруты</label>
   <p><input type="file" name="file">
   <br><div class="form-group">
			<label>Выберите день:</label>				
			<input type="date" name="dateStart"/></div>
	<br>
   <input type="submit" value="Отправить"></p>   
  </form:form>  
  <a href = "<spring:url value="/main/logistics/routemanager"/>">Менеджер маршрутов</a>
  <br>
  <a href = "<spring:url value="/main/logistics/international"/>">Менеджер международных маршрутов</a>
</body>
</html>