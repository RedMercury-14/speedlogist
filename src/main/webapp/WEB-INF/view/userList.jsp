<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
</head>
<body>
<div id="wrapper">
	<c:out value="${param.message}" />
		<div id="header">
			<h2>Список зарегистрированных юзеров</h2>
		</div>
	</div>

	<div id="container">

		<div id="content">

			<!-- put new button: Add Customer -->

			<input type="button" value="Add Customer"
				onclick="window.location.href='showFormForAdd'; return false;"
				class="add-button" />

			<!--  add our html table here -->
${authenticated}
			<table>
				<tr>
					<th>ID</th>
					<th>Login</th>
					<th>Password</th>
					<th>Name</th>
					<th>SurName</th>
					<th>Telephone</th>
					<th>Address</th>
					<th>Company</th>
					<th>Departament</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="tempUser" items="${userlist}">

					<!-- construct an "update" link with customer id -->
					<c:url var="updateLink" value="/main/admin/userlist/showFormForUpdate">
						<c:param name="id" value="${tempUser.idUser}" />
					</c:url>

					<!-- construct an "delete" link with customer id -->
					<c:url var="deleteLink" value="/customer/delete">
						<c:param name="customerId" value="${tempUser.login}" />
					</c:url>

					<tr>	
						<td>${tempUser.idUser}</td>					
						<td>${tempUser.login}</td>
						<td>${tempUser.password}</td>
						<td>${tempUser.name}</td>
						<td>${tempUser.surname}</td>
						<td>${tempUser.telephone}</td>
						<td>${tempUser.address}</td>
						<td>${tempUser.companyName}</td>
						<td>${tempUser.department}</td>
						<td>
							<!-- display the update link --> 
							<a href="${updateLink}">Update</a>
							| <a href="${deleteLink}"
							onclick="if (!(confirm('Are you sure you want to delete this customer?'))) return false">Delete</a>
						</td>

					</tr>

				</c:forEach>

			</table>

		</div>

	</div>

</body>
</html>