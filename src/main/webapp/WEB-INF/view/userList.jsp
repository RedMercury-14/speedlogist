<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
    
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style type="text/css">
.right {
	float: right;
}
</style>
<title>Insert title here</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/other.css"/>" />
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"></script>
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
</head>
<body>
	<jsp:include page="header.jsp" />
<div id="wrapper">
	<c:out value="${param.message}" />
		<div id="header">
			<h2>Список сотрудников</h2>
		</div>
	</div>

	<div class="container-fluid">
	<div class="right"><input type="button" onclick="history.back();" value="Назад"/></div>
		<div class="table-responsive">
			<input type="button" value="Создать аккаунт" onclick="window.location.href='userlist/add'; return false;" class="add-button" />

			<table class="table table-bordered border-primary table-hover table-condensed">
				<tr>
					<th>Логин</th>
					<th>Фамилия</th>
					<th>Имя</th>
					<th>Отчество</th>
					<th>Телефон</th>
					<th>Адрес</th>
					<th>Компания</th>
					<th>Должность</th>
				</tr>

				<!-- loop over and print our customers -->
				<c:forEach var="tempUser" items="${userlist}">

					<!-- construct an "update" link with customer id -->
					<c:url var="updateLink" value="/main/admin/userlist/showFormForUpdate">
						<c:param name="id" value="${tempUser.idUser}" />
					</c:url>

					<!-- construct an "delete" link with customer id -->
					<c:url var="deleteLink" value="/main/admin/userlist/delete">
						<c:param name="idUser" value="${tempUser.idUser}" />
					</c:url>

					<tr>				
						<td>${tempUser.login}</td>
						<td>${tempUser.surname}</td>
						<td>${tempUser.name}</td>
						<td>${tempUser.patronymic}</td>
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