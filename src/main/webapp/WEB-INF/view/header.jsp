<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<style>
body {
	font-family: Arial, Helvetica, sans-serif;
}
.none{
display: none;
}
.notification {
	color: white;
	text-decoration: none;
	padding: 15px 26px;
	position: right;
	display: inline-block;
	border-radius: 2px;
}
.notificationMobile{
	color: white;
	text-decoration: none;
	padding: 15px 26px;
	position: right;
	display: inline-block;
	border-radius: 2px;
}


.notification:hover {
	background: red;
}

.notification .badge {
	position: absolute;
	top: 27px;
	right: -15px;
	padding: 5px 10px;
	border-radius: 50%;
	background-color: red;
	color: white;
}
.sidenav {
  height: 100%;
  width: 0;
  position: fixed;
  z-index: 1;
  top: 0;
  left: 0;
  background-color: #111;
  overflow-x: hidden;
  transition: 0.5s;
  padding-top: 60px;
  text-align:center;
}

.sidenav a {
  padding: 8px 8px 8px 32px;
  text-decoration: none;
  font-size: 50px;
  color: #818181;
  display: block;
  transition: 0.3s;
}
.sidenav div {
  padding: 8px 8px 8px 32px;
  font-size: 50px;
}


.sidenav a:hover{
  color: #f1f1f1;
}

.sidenav .closebtn {
  position: absolute;
  top: 0;
  right: 25px;
  font-size: 36px;
  margin-left: 50px;
}
.badgeMobile{
	display: inline-block;
}
.badgeMobileTitle {
display: inline-block;
}
@media screen and (max-height: 450px) {
  .sidenav {padding-top: 15px;}
  .sidenav a {font-size: 18px;}
}
</style>
<body class="none">
<input type="hidden" value="${sessionCheck}" id="sessionCheck">
<nav class="navbar navbar-inverse navbar-static-top" id="pc">
	<div class="container-fluid">
		<div class="navbar-header">
			<a href="<spring:url value="/main"/>" class="navbar-brand">SpeedLogist</a>
		</div>
		<sec:authorize access="isAuthenticated()">  
        	<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
    	</sec:authorize>
		<input type="hidden" value="${roles}" id="role">
		
		<ul class="nav navbar-nav">
		<c:choose>
			<c:when test="${roles == '[ROLE_ADMIN]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
				<li><a href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
				<li><a href="<spring:url value="/main/slots" />">Слоты</a></li>
				<!-- <li><a href="<spring:url value="/main/shop" />">Магазин</a></li> -->
				<li><a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a></li>
				<li><a href="<spring:url value="/main/order-support/orders" />">Сопровождение заказовк</a></li>
				<!-- <li><a href="<spring:url value="/main/carrier" />">Перевозки</a></li> -->
				<li><a href="<spring:url value="/main/admin" />">Администрация</a></li>
			</c:when>	
			<c:when test="${roles == '[ROLE_SHOW]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
				<li><a href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
				<li><a href="<spring:url value="/main/slots" />">Слоты</a></li>
				<!-- <li><a href="<spring:url value="/main/shop" />">Магазин</a></li> -->
				<li><a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a></li>
				<li><a href="<spring:url value="/main/order-support/orders" />">Сопровождение заказовк</a></li>
				<!-- <li><a href="<spring:url value="/main/carrier" />">Перевозки</a></li> -->
			</c:when>	
			<c:when test="${roles == '[ROLE_MANAGER]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
				<li><a href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
				<li><a href="<spring:url value="/main/slots" />">Слоты</a></li>
			</c:when>	
			<c:when test="${roles == '[ROLE_TOPMANAGER]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
				<li><a href="<spring:url value="/main/depot" />">Маршрутизатор</a></li>
				<li><a href="<spring:url value="/main/slots" />">Слоты</a></li>
			</c:when>
			<c:when test="${roles == '[ROLE_SHOP]' || roles == '[ROLE_SHOPMANAGER]'}">				
				<li><a href="<spring:url value="/main/shop" />">Магазин</a></li>
			</c:when>	
			<c:when test="${roles == '[ROLE_STOCK]'}">				
				
			</c:when>
			<c:when test="${roles == '[ROLE_ORDERSUPPORT]'}">
				<li><a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a></li>
				<li><a href="<spring:url value="/main/order-support/orders" />">Сопровождение заказовк</a></li>
				<li><a href="<spring:url value="/main/slots" />">Слоты</a></li>
			</c:when>
			<c:when test="${roles == '[ROLE_STOCKSUPPORT]'}">
				<li><a href="<spring:url value="/main/stock-support/orders" />">Менеджер заявок</a></li>
			</c:when>
			<c:when test="${roles == '[ROLE_CARRIER]'}">				
				<!-- <li><a href="<spring:url value="/main/map" />">Маршрутизатор</a></li> -->
				<li><a href="<spring:url value="/main/carrier" />">Мой кабинет</a></li>
				<li><a href="<spring:url value="/main/carrier/exchange" />">О бирже</a></li>
			</c:when>
			<c:when test="${roles == '[ROLE_PROCUREMENT]'}">
				<li><a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a></li>
				<li><a href="<spring:url value="/main/slots" />">Слоты</a></li>
			</c:when>
			<c:otherwise>
			<li><a href="<spring:url value="/main/registration" />">Регистрация</a></li>
					<li><a href="<spring:url value="/main/signin"/>">Вход в
							систему</a></li>
			</c:otherwise>
		</c:choose>
			
<sec:authorize access="authenticated" var="authenticated" />
			<c:choose>
				<c:when test="${authenticated}">
					<li>
						<p class="navbar-text">
							Welcome
							<sec:authentication property="name" />
							<input type="hidden" value="<sec:authentication property="name" />" id="login">
							<a id="logout" href="#"> Выход</a>
						</p>
						<form id="logout-form" action="<c:url value="/logout"/>"
							method="POST">
							<sec:csrfInput />
						</form>

					</li>
					<li><a href="<spring:url value="/main/userpage"/>">Кабинет
							пользователя</a> 
					</li>
					<li>
					<a href="<spring:url value="/main/message" />" class="notification"> <span>Входящие</span>
							<span class="badge"></span>
					</a>
					</li>
				</c:when>
			</c:choose>
		</ul>
	</div>
</nav>
<span style="font-size:80px;cursor:pointer" onclick="openNav()" id="openMenu">&#9776; Меню <div class="badgeMobileTitle"></div></span>
<nav id="mySidenav" class="sidenav">

<a href="<spring:url value="/main"/>" >SpeedLogist</a>
  <c:choose>
			<c:when test="${roles == '[ROLE_ADMIN]'}">
				<a href="<spring:url value="/main/logistics" />">Отдел логистики</a>
				<a href="<spring:url value="/main/depot" />">Маршрутизатор</a>
				<a href="<spring:url value="/main/slots" />">Слоты</a>
				<a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a>
				<a href="<spring:url value="/main/order-support/orders" />">Сопровождение заказовк</a>
				<!-- <a href="<spring:url value="/main/carrier" />">Перевозки</a> -->
				<a href="<spring:url value="/main/admin" />">Администрация</a>
			</c:when>	
			<c:when test="${roles == '[ROLE_MANAGER]'}">
				<a href="<spring:url value="/main/logistics" />">Отдел логистики</a>
				<a href="<spring:url value="/main/depot" />">Маршрутизатор</a>
				<a href="<spring:url value="/main/slots" />">Слоты</a>
			</c:when>	
			<c:when test="${roles == '[ROLE_TOPMANAGER]'}">
				<a href="<spring:url value="/main/logistics" />">Отдел логистики</a>
				<a href="<spring:url value="/main/depot" />">Маршрутизатор</a>
				<a href="<spring:url value="/main/slots" />">Слоты</a>
			</c:when>
			<c:when test="${roles == '[ROLE_SHOP]' || roles == '[ROLE_SHOPMANAGER]'}">				
				<a href="<spring:url value="/main/shop" />">Магазин</a>
			</c:when>	
			<c:when test="${roles == '[ROLE_STOCK]'}">				
				
			</c:when>
			<c:when test="${roles == '[ROLE_ORDERSUPPORT]'}">
				<a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a>
				<a href="<spring:url value="/main/order-support/orders" />">Сопровождение заказовк</a>
				<a href="<spring:url value="/main/slots" />">Слоты</a>
			</c:when>
			<c:when test="${roles == '[ROLE_STOCKSUPPORT]'}">
				<a href="<spring:url value="/main/stock-support/orders" />">Сопровождение заказовк</a>
			</c:when>
			<c:when test="${roles == '[ROLE_CARRIER]'}">				
				<!-- <a href="<spring:url value="/main/map" />">Маршрутизатор</a> -->
				<a href="<spring:url value="/main/carrier" />">Мой кабинет</a>
				<a href="<spring:url value="/main/carrier/exchange" />">О бирже</a>
			</c:when>
			<c:when test="${roles == '[ROLE_PROCUREMENT]'}">
				<a href="<spring:url value="/main/procurement" />">Заявки на перевозки</a>
				<a href="<spring:url value="/main/slots" />">Слоты</a>
			</c:when>
			<c:otherwise>
			<li><a href="<spring:url value="/main/registration" />">Регистрация</a>
					<a href="<spring:url value="/main/signin"/>">Вход в
							систему</a></li>
			</c:otherwise>
		</c:choose>
		<sec:authorize access="authenticated" var="authenticated" />
			<c:choose>
				<c:when test="${authenticated}">
				<a href="<spring:url value="/main/message" />" class="notificationMobile"> <span id="symbolMessage">Входящие<div class="badgeMobile"></div></span></a>
				<a href="<spring:url value="/main/userpage"/>">Кабинет
							пользователя</a> 
				<br><br><br><br>
							
							<div>Welcome <sec:authentication property="name" /></div>
							<input type="hidden" value="<sec:authentication property="name" />" id="login">
							<a id="logoutMob" href="#"> Выход</a>
						
						<form id="logout-form" action="<c:url value="/logout"/>"
							method="POST">
							<sec:csrfInput />
						</form>

					
					
					
					
				</c:when>
			</c:choose>
			<a href="javascript:void(0)" class="closebtn" onclick="closeNav()" style="font-size: 100px;">&times;</a>
</nav>
</body>
<script>
function openNav() {
  document.getElementById("mySidenav").style.width = "100%";
}

function closeNav() {
  document.getElementById("mySidenav").style.width = "0";
}
</script>
<script src="<spring:url value="/resources/js/header.js"/>" type="module"></script>