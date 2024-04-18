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
<style type="text/css">
.none{
	display: none;
}
</style>
<title>Документооборот</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">

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
 <h3 class="container"><a href = "<spring:url value="/main/logistics/documentflow/documentlist"/>">Список актов выполненных работ</a></h3>
  <form:form class="container" method="post">
  	<input type="text" name="numAct" required>
  	<input type="submit" value="Проверить акт">
  	<h2 style="color: red;"><c:out value="${errorMessage}"/></h2>
  </form:form>
  
</body>
</html>