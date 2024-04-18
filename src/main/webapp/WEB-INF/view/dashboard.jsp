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
* {
  box-sizing: border-box;
}

/* Создать три колонки одинаковой ширины */
.columns {
  float: left;
  width: 33.3%;
  padding: 8px;
}

/* Стиль списка */
.price {
  list-style-type: none;
  border: 1px solid #eee;
  margin: 0;
  padding: 0;
  -webkit-transition: 0.3s;
  transition: 0.3s;
}

/* Добавить тени при наведении курсора */
.price:hover {
  box-shadow: 0 8px 12px 0 rgba(0,0,0,0.2)
}

/* Заголовок ценообразования */
.price .header {
  background-color: #111;
  color: white;
  font-size: 25px;
}

/* Элемент списка */
.price li {
  border-bottom: 1px solid #eee;
  padding: 20px;
  text-align: center;
}

/* Элемент серого списка */
.price .grey {
  background-color: #eee;
  font-size: 20px;
}

/* Кнопки "Записаться" */
.button {
  background-color: #4CAF50;
  border: none;
  color: white;
  padding: 10px 25px;
  text-align: center;
  text-decoration: none;
  font-size: 18px;
}

/* Измените ширину трех столбцов на 100%
(складывать горизонтально на небольших экранах) */
@media only screen and (max-width: 600px) {
  .columns {
    width: 100%;
  }
}
</style>
<title>Сводные данные</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"	type="text/javascript"></script>
<script	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
		<form:form method="get">		
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart"
				value="<c:out value="${dateNow}" />" />
	по<input type="date" name="dateFinish"
				value="<c:out value="${dateTomorrow}" />" />
			<br>
			<input type="submit" value="Отобразить">
			</p>
		</form:form>	
	</div>
<div class="columns">
  <ul class="price">
    <li class="header">Регистрация</li>
    <li class="grey">Общее колличество перевозчиков: ${carrierTotal}</li>
    <li>Международные перевозчики: ${carrierInternational}</li>
    <li>Внутренние перевозчики: ${carrierTotal-carrierInternational-carrierInternationalNew-carrierRegionalNew}</li>
    <li>Международные заявки ${carrierInternationalNew}</li>
    <li>Внутренние заявки ${carrierRegionalNew}</li>
    <li class="grey">Активность: ${activity} %</li>
  </ul>
</div>


<div class="columns">
  <ul class="price">
    <li class="header" style="background-color:#4CAF50">Международные перевозки</li>
    <li class="grey"><p>Общее колличество маршрутов: ${routeCollInternational}</p>
    <p>на сумму: ${costTotalInternational} BYN</p></li>
    <li>Средняя экономия: ${economyInternational/routeCollInternational} BYN</li>
    <li>Средняя стоимость рейса: ${costTotalInternational/routeTotal} BYN</li>
    <li>Колличество рейсов без экономии: ${routeNotOptimalCost}</li>
    <li class="grey">Процент экономии к предыдущему месяцу</li>
  </ul>
</div>

<div class="columns">
  <ul class="price">
    <li class="header">Внутренние перевозки</li>
    <li class="grey">на сумму:</li>
    <li>Средняя экономия:</li>
    <li>Средняя стоимость рейса:</li>
    <li>Колличество рейсов без экономии:</li>
    <li class="grey">Процент экономии к предыдущему месяцу</li>
  </ul>
</div>
<div class="columns">
  <ul class="price">
    <li class="header">Документооборот</li>
    <li class="grey">Колличество активных рейсов</li>
    <li>Создано актов</li>
    <li>Отправлено актов</li>
    <li>Ср. колличество обработанных актов:</li>
    <li class="grey">Отставания: дней</li>
  </ul>
</div>
</body>
</html>