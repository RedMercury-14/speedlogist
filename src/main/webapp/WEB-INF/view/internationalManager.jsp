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
<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
<meta name="viewport" content="width=device-width, initial-scale=1">
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<style>
[data-tooltip] {
    position: relative; /* Относительное позиционирование */ 
   }
   [data-tooltip]::after {
    content: attr(data-tooltip); /* Выводим текст */
    position: absolute; /* Абсолютное позиционирование */
    width: 200px; /* Ширина подсказки */
    left: 0; top: 0; /* Положение подсказки */
    background: #d3d3d3; /* Синий цвет фона */
    color: #000000; /* Цвет текста */
    padding: 0.5em; /* Поля вокруг текста */
    box-shadow: 2px 2px 5px rgba(0, 0, 0, 0.3); /* Параметры тени */
    pointer-events: none; /* Подсказка */
    opacity: 0; /* Подсказка невидима */
    transition: 1s; /* Время появления подсказки */
   } 
   [data-tooltip]:hover::after {
    opacity: 1; /* Показываем подсказку */
    top: 2em; /* Положение подсказки */
   }
/*    вверху подсказка */
/* MODAL */
body {font-family: Arial, Helvetica, sans-serif;}

/* Модальный (фон) */
.modal {
  display: none; /* Скрыто по умолчанию */
  position: fixed; /* Оставаться на месте */
  z-index: 1; /* Сидеть на вершине */
  padding-top: 100px; /* Расположение коробки */
  left: 0;
  top: 0;
  width: 100%; /* Полная ширина */
  height: 100%; /* Полная высота */
  overflow: auto; /* Включите прокрутку, если это необходимо */
  background-color: rgb(0,0,0); /* Цвет запасной вариант  */
  background-color: rgba(0,0,0,0.4); /*Черный с непрозрачностью */
}

/* Модальное содержание */
.modal-content {
  position: relative;
  background-color: #fefefe;
  margin: auto;
  padding: 0;
  border: 1px solid #888;
  width: 80%;
  box-shadow: 0 4px 8px 0 rgba(0,0,0,0.2),0 6px 20px 0 rgba(0,0,0,0.19);
  -webkit-animation-name: animatetop;
  -webkit-animation-duration: 0.4s;
  animation-name: animatetop;
  animation-duration: 0.4s
}

/* Добавить анимацию */
@-webkit-keyframes animatetop {
  from {top:-300px; opacity:0} 
  to {top:0; opacity:1}
}

@keyframes animatetop {
  from {top:-300px; opacity:0}
  to {top:0; opacity:1}
}

/* Кнопка закрытия */
.closer {
  color: white;
  float: right;
  font-size: 28px;
  font-weight: bold;
}

.closer:hover,
.closer:focus {
  color: #000;
  text-decoration: none;
  cursor: pointer;
}

.modal-header {
  padding: 2px 16px;
  background-color: #747474;
  color: white;
}

.modal-body {padding: 2px 16px;}

.modal-footer {
  padding: 2px 16px;
  background-color: #5cb85c;
  color: white;
}
/* end */
.text {
	margin: auto;
	text-align: center;
	font-size: 32px;
	line-height: 1.5;
	text-shadow: 0 0 15px rgba(255, 255, 255, .5), 0 0 10px
		rgba(255, 255, 255, .5);
}

.right-click-menu {
	margin: 0;
	padding: 0;
	position: fixed; 
	list-style: none;
	background: #ddddddcc;
	border: 2px solid #ffffff00;
	border-radius: 2px;
	display: none;
}

.right-click-menu.active {
	display: block;
}

.right-click-menu li {
	width: 100%;
	padding: 10px;
	box-sizing: border-box;
	cursor: pointer;
	font-size: 15px;
}

.right-click-menu li:hover {
	background: #ffffff73;
}
.none{
	display: none;
}
.status-none{
	display: none;
}
.type-tender-none{
	display: none;
}
.activRow{
		background: #494f5252;
	}
.finishRow{
		background: #c4ffe1db;
	}
.attentionRow{
		background: #dce37266;
}
.endRow{
		background: #9ee9ffdb;
}
.oncePersonRoute{
	background: #ca44e366;
}
.cancelRow {
	background: #e3444466;
}
/* .noneRow{
		background: none;
} */
/* Меню боковой панели */
.sidepanel {
  height: 290px; /* Укажите высоту */
  width: 0; /* 0 ширина-измените это с помощью JavaScript */
  position: absolute; 
  z-index: 1; /* Оставайтесь на вершине */
  top: 0;
  background-color: #3c3c3c;
  overflow-x: hidden; /* Отключить горизонтальную прокрутку */
  padding-top: 60px; /* Поместите содержимое 60px сверху */
  transition: 0.5s; /* 0.5 секунды эффект перехода для скольжения в боковой панели */
  right: 0;
}

/* Боковая панель ссылок */
.sidepanel input {
  padding: 8px 8px 8px 32px;
  font-size: 25px;
  color: #818181;
  display: block;
  transition: 0.3s;
}
.sidepanel div {
  padding: 8px 8px 8px 10px;
  color: #818181;
}

/* Положение и стиль кнопки закрытия (верхний правый угол) */
.sidepanel .closebtn {
  position: absolute;
  right: 25px;
  font-size: 36px;
  margin-left: 50px;
}

/* Стиль кнопка, которая используется для открытия боковой панели */
.openbtn {
  font-size: 15px;
  cursor: pointer;
  background-color: #3c3c3c;
  color: white;
  padding: 10px 15px;
  border: none;
}

.openbtn:hover {
  background-color: #444;
}
.hidden {
  display: none;
}

#sort th.col-13 {
	padding: 0 180px;
}

.no-scroll {
    overflow-y: hidden;
}
#overlay {
    position: fixed;
    width: 100%;
    height: 100%;
    top: 0;
    bottom: 0;
    left: 0;
    right: 0;
    background: -webkit-gradient(linear, left top, left bottom, from(rgba(14, 29, 51, 0.6)), to(rgba(14, 29, 51, 0.2)));
    background: linear-gradient(to bottom, rgba(14, 29, 51, 0.4), rgba(14, 29, 51, 0.2));
}
#overlay .spinner-border {
    position: absolute;
    top: 50%;
    width: 60px;
    height: 60px;
    left: calc(50% - 20px);
}
.spinner-border {
    display: inline-block;
    width: 2rem;
    height: 2rem;
    vertical-align: text-bottom;
    border: 7px solid currentColor;
    border-right-color: transparent;
    border-radius: 50%;
    -webkit-animation: spinner-border .75s linear infinite;
    animation: spinner-border .75s linear infinite;
}
.sr-only {
    position: absolute;
    width: 1px;
    height: 1px;
    padding: 0;
    margin: -1px;
    overflow: hidden;
    clip: rect(0,0,0,0);
    white-space: nowrap;
    border: 0;
}
@keyframes spinner-border {
    100% {
        -webkit-transform: rotate(360deg);
        transform: rotate(360deg);
    }
}
</style>
<title>Менеджер международных маршрутов</title>
<link rel="stylesheet" type="text/css"
	href="<c:url value="/resources/css/other.css"/>" />
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body id="body">
	<jsp:include page="header.jsp" />		
	<sec:authorize access="isAuthenticated()">  
        	<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
    	</sec:authorize>
		<input type="hidden" value="${roles}" id="role">
	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>
	<div class="container">
		<h3>Менеджер международных маршрутов</h3>
	</div>		
	<div class="container">
		<form:form method="get">		
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart"
				value="<c:out value="${dateNow}" />" />
	по<input type="date" name="dateFinish"
				value="<c:out value="${dateTomorrow}" />" />
			<br>
			<input type="submit" value="Отобразить">
			<input type="button" value="Экспорт в Excel" id="export">
			</p>
		</form:form>
		<div id="stopTender">
			<form action="./stoptender">
			<c:choose>
				<c:when test="${isBlockTender}">
				<div style="display: inline-block;">Внимание! Тендеры отключены!
					<input type="submit" value="Запустить тендер" name="start" style="background: green;"> </div>
				</c:when>
				<c:otherwise>
					<input type="submit" value="Отключить тендер" name="stop" style="background: red" onclick="if (!(confirm('Вы действительно хотите остановить тендеры для всех перевозчитков?'))) return false">
				</c:otherwise>
			</c:choose>
				
			</form>
		</div>
		
		<div id="totalEconomy" style="display: inline;"> </div>  
		<div id="economyResultBYN" style="display: inline; margin-left:150px;"> </div>
		<div id="economyTotlClosetBYN"> </div>	
		<input type="button" id="getEconomy" value="Показать экономию">			
	</div>	
	<div class="container-fluid">
	<div style="text-align: right;"><button class="openbtn" onclick="openNav()" >&#9776; Вид таблицы </button></div>
		<div class="table-responsive">			
 						<a href="<spring:url value="/main/logistics/international/add"/>">Создать новый маршрут
				</a> | <a href="<spring:url value="/main/logistics/international/addRoutePattern"/>">Создать шаблонный маршрут
				</a> | <a href="<spring:url value="/main/logistics/international/disposition"/>">Диспозиция</a>
			<table	class="table table-bordered border-primary  table-condensed table-hover table-responsive"	id="sort">
				<thead class="text-center">
					<tr class="sticky">
						<th class="col-0">
						<select id="statusWay">
							<option value="0">Показать всех</option>
							<option value="1">Импорт</option>
							<option value="2">Экспорт</option>					
							<option value="3">РБ</option>					
 						</select>
						</th>
						<th class="col-1">Название маршрута
						<input type="text" id="directionSearch" onkeyup="directionSearch()" placeholder="Поиск по названиям"></th>
						<th class="col-2">Дата загрузки</th>
						<th class="col-4">Время загрузки (планируемое)</th>
						<th class="col-3">Дата и время выгрузки</th>
						<th class="col-5">Выставляемая стоимость</th>
						<th class="col-6">Экономия</th>
						<th class="col-7">Перевозчик
						<input type="text" id="companyNameSearch" onkeyup="companyNameSearch()" placeholder="Поиск по перевозчику"></th>
						<th class="col-8">Номер машины
						<input type="text" id="numCarSearch" onkeyup="numCarSearch()" placeholder="Поиск по номеру авто"></th>
						<th class="col-9">Данные по водителю</th>
						<th class="col-10">Заказчик</th>
						<th class="col-11">Паллеты/Объем</th>
						<th class="col-12">Общий вес</th>	
						<th class="col-13">Комментарии</th>					
						<th class="col-14">Начальные стоимости перевозки
						<select id="typeTenderSort">
							<option value="0">Показать всех</option>
							<option value="1">Закрытый тендер</option>
							<option value="2">Открытый тендер</option>						
 						</select></th>
						<th class="col-15">Статус
						<select id="statusSort">
							<option value="0">Показать всех</option>
							<option value="1">Ожидание подтверждения</option>
							<option value="2">Маршрут на бирже</option>
							<option value="3">Тендер завершен</option>
							<option value="4">Маршрут завершен</option>	
							<option value="5">Контроль цены</option>						
 						</select>
						</th>

					</tr>
				</thead>
				
				<!-- loop over and print our customers -->
				<c:forEach var="route" items="${routes}">
				 	<c:url var="showOffer" value="/main/logistics/international/tenderOffer">	
					 	<c:param name="idRoute" value="${route.idRoute}" />					
					</c:url>
					
					<c:url var="showOfferForAdmin" value="/main/admin/international/tenderOffer">	
					 	<c:param name="idRoute" value="${route.idRoute}" />					
					</c:url>
					
					<form:form method="post">
						<input type="hidden" value="${route.idRoute}" name="id" />
						<sec:csrfInput />
						<tr id="${route.idRoute}">				
							<td id="idRoute" class="none">${route.idRoute}</td>
							<td class="col-0" id="way">${route.simpleWay}</td>
							<td id="routeDirection" class="col-1">${route.routeDirection}</td>
							<td class="col-2" style="white-space: nowrap;">${route.simpleDateStart}</td>
							<td id="timeLoadPreviously" class="col-4">${route.timeLoadPreviously}</td>
							<td class="col-3" style="white-space: nowrap;">${route.dateUnloadPreviouslyStock} ${route.timeUnloadPreviouslyStock}</td>
							<td id="finishCost" class="col-5">${route.finishPrice} ${route.startCurrency}</td>
							<td id="economy" class="col-6"></td>
							<td class="col-7">${route.user.companyName}</td>
							<td class="col-8"><p data-tooltip = "${route.truck.typeTrailer}"><a id="myBtn2">${route.truck.numTruck} / ${route.truck.numTrailer}</a></p></td>
							<td class="col-9"><a id="myBtn">${route.driver.surname} ${route.driver.name} ${route.driver.patronymic}</a></td>
							<td class="col-10">${route.customer}</td>
							<td class="col-11">${route.totalLoadPall} / <c:forEach var="RHS" items="${route.roteHasShop}" end="0">
							${RHS.volume}
						</c:forEach></td>
							<td class="col-12">${route.totalCargoWeight}</td>	
							<td class="col-13">${route.userComments}</td>	
							<c:choose>
							<c:when test="${route.startPrice != null}">
								<td id="cost" class="col-14">${route.startPrice} BYN</td>
							</c:when>
							<c:otherwise>
								<td id="cost" class="col-14">${route.optimalCost} BYN - оптимальная</td>
							</c:otherwise>
							</c:choose>
												
													
							<td id="status" class="col-15"><c:choose>
									<c:when test="${route.statusRoute == 1}">	
										Маршрут на бирже. <div class="resp"><a href="${showOffer}" id="showOfferByCArrier">Просмотреть предложения</a>
									<div class="coll"></div>
								</div>
									</c:when>
									<c:when test="${route.statusRoute == 4}">
										Тендер завершен. Перевозчик принят.
									</c:when>
									<c:when test="${route.statusRoute == 5}">
										Тендер отменен.
									</c:when>
									<c:when test="${route.statusRoute == 6}">
										Маршрут завершен.
									</c:when>
									<c:when test="${route.statusRoute == 8}">
										Контроль цены. <div class="resp"><a href="${showOfferForAdmin}" id="showOfferByCArrier">Просмотреть предложения</a></div>
									</c:when>
									<c:otherwise>
										Ожидание подтверждения.
									</c:otherwise>
								</c:choose></td>							
					</form:form>			
						<!-- The Modal -->
<div id="myModal" class="modal">
  <!-- Модальное содержание водителя -->
  <div class="modal-content">
    <div class="modal-header">
      <span class="closer">&times;</span>
      <h2>Данные по водителю</h2>
    </div>
    <div class="modal-body">
      <p>${route.driver.surname} ${route.driver.name} ${route.driver.patronymic}</p>
      <p>Паспортные данные: ${route.driver.numPass}</p>
      <p>Номер телефона: ${route.driver.telephone}</p>
    </div>
  </div>
</div>

<div id="mainModal" class="modal">
  <!-- Модальное содержание водителя -->
  <div class="modal-content">
    <div class="modal-header">
      <span class="closer">&times;</span>
      <h2>Основная информация</h2>
    </div>
    <div class="modal-body" id="copyModal">
      <p>${route.routeDirection}</p>
      
      <p>Перевозчик: ${route.user.companyName}</p>
      <p>Подвижной состав: ${route.truck.numTruck} / ${route.truck.numTrailer};  ${route.truck.brandTruck} / ${route.truck.brandTrailer}</p>
      <p>Принадлежность транспорта: ${route.truck.ownerTruck}</p>
      <p> </p>
      <p>Данные по водителю:</p>
      <p>${route.driver.surname} ${route.driver.name} ${route.driver.patronymic}</p>
      <p>Паспортные данные: ${route.driver.numPass}</p>
      <p>Номер телефона: ${route.driver.telephone}</p>
      <p>Дата подачи машины на загрузку: ${route.dateLoadActuallySimple}; время: ${route.timeLoadActually};</p>
      <p>Дата прибытия авто под выгрузку: ${route.dateUnloadActuallySimple}; время: ${route.timeUnloadActually};</p>
      <p>Стоимость перевозки: ${route.finishPrice} ${route.startCurrency}</p>
    </div>
  </div>
</div>
				</c:forEach>
			</table>
			<ul class="right-click-menu">		
				<li id="l1">Истоpия предложений</li>		
				<li id="l2">Отправить тендер</li>
				<li id="l3">Показать точки выгрузок</li>
				<li id="l4">Редактор маршрутов</li>
				<li id="l5">Завершить маршрут</li>
<!-- 				<li id="l6">Копировать маршрут</li> -->
				<li style="color: red; font-weight: bold;" id="l7">Отменить тендер</li>
			</ul>
		</div>
	</div>
<div id="mySidepanel" class="sidepanel">
<a href="javascript:void(0)" class="closebtn" onclick="closeNav()">&times;</a>
  	<div id="controls" style="color: #9d9d9d;">
  		Направление  <input id="col-0" type="checkbox" data-column-class="col-0" checked style="display: inline;">
  		<br>
  		Название маршрута  <input id="col-1" type="checkbox" data-column-class="col-1" checked style="display: inline;">
  		<br>
  		Дата загрузки  <input id="col-2"  type="checkbox" data-column-class="col-2" checked style="display: inline;">
  		<br>
  		Фактическая дата загрузки  <input id="col-3"  type="checkbox" data-column-class="col-3" checked style="display: inline;">
  		<br>
  		Время загрузки  <input id="col-4"  type="checkbox" data-column-class="col-4" checked style="display: inline;">
  		<br>
  		Выставляемая стоимость  <input id="col-5"  type="checkbox" data-column-class="col-5" checked style="display: inline;">
  		<br>
  		Экономия  <input id="col-6"  type="checkbox" data-column-class="col-6" checked style="display: inline;">
  		<br>
  		Перевозчик  <input id="col-7"  type="checkbox" data-column-class="col-7" checked style="display: inline;">
  		<br>
  		Номер машины  <input id="col-8"  type="checkbox" data-column-class="col-8" checked style="display: inline;">
  		<br>
  		Данные по водителю  <input id="col-9"  type="checkbox" data-column-class="col-9" checked style="display: inline;">
  		<br>
  		Заказчик  <input id="col-10"  type="checkbox" data-column-class="col-10" checked style="display: inline;">
  		<br>
  		Паллеты/объем  <input id="col-11"  type="checkbox" data-column-class="col-11" checked style="display: inline;">
  		<br>
  		Общий вес  <input id="col-12"  type="checkbox" data-column-class="col-12" checked style="display: inline;">
  		<br>
  		Комментарии  <input id="col-13"  type="checkbox" data-column-class="col-13" checked style="display: inline;">
  		<br>
  		Стоимости перевозки  <input id="col-14"  type="checkbox" data-column-class="col-14" checked style="display: inline;">
  		<br>
  		Статус  <input id="col-15"  type="checkbox" data-column-class="col-15" checked style="display: inline;">
	</div>
</div>
<script	src="${pageContext.request.contextPath}/resources/js/internationalManager.js" type="module"></script>
<script type="text/javascript">
// var table = document.getElementById("sort")
// table.style.background.color = "#765050";
/* Установите ширину боковой панели на 250 пикселей (показать его) */
function openNav() {
  document.getElementById("mySidepanel").style.width = "250px";
}

/* Установите ширину боковой панели в 0 (скройте ее) */
function closeNav() {
  document.getElementById("mySidepanel").style.width = "0";
}


function directionSearch() {	
	  var input, filter, table, tr, td, i, txtValue;
	  input = document.getElementById("directionSearch");
	  filter = input.value.toUpperCase();
	  table = document.getElementById("sort");
	  tr = table.getElementsByTagName("tr");
	  for (i = 0; i < tr.length; i++) {
	    td = tr[i].getElementsByTagName("td")[2];
	    if (td) {
	      txtValue = td.textContent || td.innerText;
	      if (txtValue.toUpperCase().indexOf(filter) > -1) {
	        tr[i].style.display = "";
	      } else {
	        tr[i].style.display = "none";
	      }
	    }       
	  }
	}
function companyNameSearch() {	
	  var input, filter, table, tr, td, i, txtValue;
	  input = document.getElementById("companyNameSearch");
	  filter = input.value.toUpperCase();
	  table = document.getElementById("sort");
	  tr = table.getElementsByTagName("tr");
	  for (i = 0; i < tr.length; i++) {
	    td = tr[i].getElementsByTagName("td")[8];
	    if (td) {
	      txtValue = td.textContent || td.innerText;
	      if (txtValue.toUpperCase().indexOf(filter) > -1) {
	        tr[i].style.display = "";
	      } else {
	        tr[i].style.display = "none";
	      }
	    }       
	  }
	}

function numCarSearch() {	
	  var input, filter, table, tr, td, i, txtValue;
	  input = document.getElementById("numCarSearch");
	  filter = input.value.toUpperCase();
	  table = document.getElementById("sort");
	  tr = table.getElementsByTagName("tr");
	  for (i = 0; i < tr.length; i++) {
	    td = tr[i].getElementsByTagName("td")[9];
	    if (td) {
	      txtValue = td.textContent || td.innerText;
	      if (txtValue.toUpperCase().indexOf(filter) > -1) {
	        tr[i].style.display = "";
	      } else {
	        tr[i].style.display = "none";
	      }
	    }       
	  }
	}
</script>
</body>
</html>