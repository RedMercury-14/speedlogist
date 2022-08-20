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
					<c:url var="updateRoles" value="/main/showFormForUpdate/roles">
						<c:param name="id" value="${idUser}" />
					</c:url>
					<tr>
						<td><label>логин:</label></td>
						<td><form:input path="login" /></td>
					</tr>
				
					<tr>
						<td><label>пароль:</label></td>
						<td><form:input path="password" /></td>
					</tr>

					<tr>
						<td><label>Введите имя:</label></td>
						<td><form:input path="name" /></td>
					</tr>

					<tr>
						<td><label>Введите surname</label></td>
						<td><form:input path="surname" /></td>
					</tr>
					
					<tr>
						<td><label>Введите telephone</label></td>
						<td><form:input path="telephone" /></td>
					</tr>
					
					<tr>
						<td><label>Введите address</label></td>
						<td><form:input path="address" /></td>
					</tr>
					
					<tr>
						<td><label>Введите company</label></td>
						<td><form:input path="companyName" /></td>
					</tr>
					
					<tr>
						<td><label>Введите depart</label></td>
						<td><form:input path="department" /></td>
					</tr>
					
					
					<tr>
					<td><label>ROLE</label></td>
					<form:form modelAttribute="role">
						<form:hidden path="idRole" />
						<form:hidden path="authority" />
						<td>${role.authority}</td>
					</form:form>
					</tr>
			
					
					<tr>
						<td><label></label></td>
						<td><input type="submit" value="Save" class="save" /></td>
					</tr>
					<tr>
						<td><label></label></td>
						<td><a href="${updateRoles}">UpdateRoles</a></td>
					</tr>
					
				</tbody>
			</table>			
		</form:form>




</body>
</html>