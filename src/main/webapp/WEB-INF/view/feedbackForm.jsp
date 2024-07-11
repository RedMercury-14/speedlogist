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
<form:form action="feedback/form" method="post">
<input type="hidden" value="${driver.idUser}" name="driver" />
<input type="hidden" value="${idRouteHasShop}" name="idRouteHasShop" />
<h1>Жалоба на ${driver.surname}  ${driver.name}</h1>
<fieldset>
 <legend><strong>Предмет жалобы</strong></legend>
 <p><input type="radio" name="radio" value="Воровство" required> Воровство <br>
 <input type="radio" name="radio" value="Оскорбление/нецензурная лексика" required> Оскорбление/нецензурная лексика <br>
 <input type="radio" name="radio" value="Нарушение ПДД" required> Нарушение ПДД<br>
  <input type="radio" name="radio" value="Административное правонарушение" required> Административное правонарушение</p>
</fieldset><br>
<p><textarea name="message" rows="10" cols="100" placeholder="Суть произошедшего"></textarea></p>
<p><input type="submit" value="Отправить"></p>
</form:form>
</div>
</div>


</body>
</html>