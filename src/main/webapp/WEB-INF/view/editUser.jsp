<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
	<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
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
<title>Insert title here</title>
</head>
<body>
<jsp:include page="header.jsp"/>
<h1>Edit юзера</h1>

			<form:form action="saveUser" modelAttribute="user"  method="POST">
			<form:hidden path="idUser" />
			<form:hidden path="enablet" />	
			<table>
				<tbody>
					<tr>
						<td><label>Логин:</label></td>
						<td id="login"><form:input path="login" /></td>
					</tr>
				
					<tr>
						<td><label>Пароль:</label></td>
						<td><form:input path="password" /></td>
					</tr>

					<tr>
						<td><label>Введите имя:</label></td>
						<td><form:input path="name" /></td>
					</tr>

					<tr>
						<td><label>Введите фамилию</label></td>
						<td><form:input path="surname" /></td>
					</tr>
					
					<tr>
						<td><label>Введите отчество</label></td>
						<td><form:input path="patronymic" /></td>
					</tr>
					
					<tr>
						<td><label>Введите номер телефона</label></td>
						<td><form:input path="telephone" /></td>
					</tr>
					
					<tr>
						<td><label>Введите адрес проживания</label></td>
						<td><form:input path="address" /></td>
					</tr>
					
					<tr>
						<td><label>Введите компанию</label></td>
						<td><form:input path="companyName" value = "Доброном" /></td>
					</tr>
					
					<tr>
						<td><label>Введите должность</label></td>
						<td><form:input path="department" /></td>
					</tr>
					
					
					<tr>
					<td><label>Права доступа (роли)</label></td>
					<td><select name="role" required="true">
 						<option>${role.authority}</option>
  						<option value="1">Администратор</option>
  						<option value="2">Топ менеджер</option>
  						<option value="3">Менеджер</option>
  						<option value="4">Магазин</option>
  						<option value="6">Склад</option>
 						</select></td>
					</tr>
			
					
					<tr>
						<td><label></label></td>
						<td><input type="submit" value="Save" class="save" /></td>
					</tr>
					
				</tbody>
			</table>			
		</form:form>




</body>
</html>