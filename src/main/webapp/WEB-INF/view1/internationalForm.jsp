<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<style>
.raz { 
  -moz-appearance: textfield;
}
.raz::-webkit-inner-spin-button { 
  display: none;
}
* {
  box-sizing: border-box;
}

body {
  font: 16px Arial;  
}

/* контейнер должен быть расположен относительно друг друга: */
.autocomplete {
  position: relative;
  display: inline-block;
}

input {
  border: 1px solid transparent;
  background-color: #f1f1f1;
  padding: 10px;
  font-size: 16px;
}

input[type=text] {
  background-color: #f1f1f1;
  width: 100%;
}

input[type=submit] {
  background-color: DodgerBlue;
  color: #fff;
  cursor: pointer;
}

.autocomplete-items {
  position: absolute;
  border: 1px solid #d4d4d4;
  border-bottom: none;
  border-top: none;
  z-index: 99;
  /* расположите элементы автозаполнения на той же ширине, что и контейнер: */
  top: 100%;
  left: 0;
  right: 0;
}

.autocomplete-items div {
  padding: 10px;
  cursor: pointer;
  background-color: #fff; 
  border-bottom: 1px solid #d4d4d4; 
}

/* при наведении курсора на элемент: */
.autocomplete-items div:hover {
  background-color: #e9e9e9; 
}

/* при навигации по элементам используйте клавиши со стрелками: */
.autocomplete-active {
  background-color: DodgerBlue !important; 
  color: #ffffff; 
}
</style>
<title>Insert title here</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">
<script src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<input type="hidden" value="${flag}" id="flag">
<div class = "container">
<div id="content" class="form-group"></div>
<div class="form-group">
<div id = "message"></div>
			<input id="button" type="button" value="Добавить точку выгрузки"> <input id="button2" type="button" value="Добавить точку загрузки"> <input id="next" type="button" value="Создать маршрут">
			
			</div>
</div>
<script	src="${pageContext.request.contextPath}/resources/js/internationalForm.js" type="module"></script>
</body>
</html>