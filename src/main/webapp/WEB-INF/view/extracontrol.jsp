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
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/style.css"/>" />
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
<body>
<jsp:include page="header.jsp"/>
<form:form enctype="multipart/form-data" method="post" action="./extracontrol?${_csrf.parameterName}=${_csrf.token}" >
  <sec:csrfInput />
  <label for="make">Загрузить постоянные цены в БД</label>
   <p><input type="file" name="file">
   <br>
   <input type="submit" value="Отправить"></p>
  </form:form>
  <br>
  <form:form enctype="multipart/form-data" method="post" action="./extracontrol2?${_csrf.parameterName}=${_csrf.token}" >
  <sec:csrfInput />
  <label for="make">Загрузить розничные цены в БД</label>
   <p><input type="file" name="file">
   <br>
   <input type="submit" value="Отправить"></p>
  </form:form>
  <br>
    <form:form enctype="multipart/form-data" method="post" action="./extracontrol3?${_csrf.parameterName}=${_csrf.token}" >
  <sec:csrfInput />
  <label for="make">Тест загрузки</label>
   <p><input type="file" name="file">
   <br>
   <input type="submit" value="Отправить"></p>
  </form:form>
</body>
</html>