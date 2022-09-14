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
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class = "container">
<div id="content" class="form-group"></div>
<div class="form-group">
<div id = "message"></div>
			<input id="button" type="button" value="Добавить точку выгрузки"> <input id="button2" type="button" value="Добавить точку загрузки"> <input id="next" type="button" value="Создать маршрут">
			<select name="staticRoute">
 						<option></option>
  						<option id="312">Минский КХП</option>
  						<option id="312">Кристалл Минск</option>
 						<option id="312">Кристалл Барановичи</option>
  						<option id="312">МЗВВ</option>
 						<option id="312">МЗИВ</option>
 						<option id="312">МЗИВ Промышленная</option>
 						<option id="312">Криница</option>
 						<option id="312">Криница Добрада</option>
 						<option id="344">Криница Прошленная</option>
 						<option id="412">Берталсервис Смолевичи</option>
 						<option id="412">Берталсервис Черницы</option>
 						<option id="217">Пакхаус</option>
 						<option id="412">Борисовский КХП</option>
 						<option id="412">Винторг Рованичи</option>
 						<option id="312">БелВинГрупп</option>
 						<option id="312">Энерго-Оил</option>
 						<option id="312">Джапан</option>
 						<option id="217">Банан</option>
 						<option id="312">Энерджи Групп</option>
 						</select>
			</div>
</div>
<script	src="${pageContext.request.contextPath}/resources/js/internationalForm.js" type="module"></script>
</body>
</html>