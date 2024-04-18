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
<title>Дополнительная информация</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>

<jsp:include page="header.jsp"/>
<div class="container">
		<div class="row">
		<h1>Дополнительная информация</h1>
		<div id="time"></div>
		</div>	
		<div class="form-group">	
			<c:out value="${errorMessage}" />
		</div>
		<h2>Внесите дополнительную информацию, которая потребуется для корректного создания отчётов и актов выполненных работ.</h2>
		<form:form enctype="multipart/form-data" modelAttribute="user" method="POST" action="${pageContext.request.contextPath}/main/carrier/updateUserData?${_csrf.parameterName}=${_csrf.token}"> <!-- ну такое себе -->
			<div class="form-group"  style="display: inline-block;">
				<label>Введите номер договора:</label>
				<input name="numContract" class="form-control"  required/>							
			</div>
			<div class="form-group" style="display: inline-block; padding-left: 30px">
				<label>от:</label>
				<input name="dateContract" class="form-control" type="date"  required/>							
			</div>
			<div class="form-group">
				<label>Введите реквизиты (банковские) фирмы:</label>
				<textarea name="requisites" class="form-control"  required></textarea>
			</div>
			<div class="form-group">
				<label>Введите eMail:</label>
				<input name="eMail" class="form-control"  required/>							
			</div>
			<div class="form-group">
				<label>ФИО деректора (полностью):</label>
				<input name="director" class="form-control" required/>
			</div>
			
			<div class="form-group">
				<sec:csrfInput />
  				<label for="make">Прикрепить договор</label>
   				<p><input type="file" name="file" required>
  						
			</div>
			<br>
						<td><input type="submit" value="Внести данные" class="save" id="send" /></td>		
		</form:form>
		
</div>
</body>
</html>