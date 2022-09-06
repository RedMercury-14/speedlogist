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

.notification {
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
</style>

<nav class="navbar navbar-inverse navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<a href="<spring:url value="/main"/>" class="navbar-brand">Speed Logist</a>
		</div>
		<sec:authorize access="isAuthenticated()">  
        	<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
    	</sec:authorize>
		<input type="hidden" value="${roles}" id="role">
		
		
		<ul class="nav navbar-nav">
		
		
			
		<c:choose>
			<c:when test="${roles == '[ROLE_ADMIN]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
				<li><a href="<spring:url value="/main/depot" />">Склад</a></li>
				<li><a href="<spring:url value="/main/shop" />">Магазин</a></li>
				<li><a href="<spring:url value="/main/carrier" />">Перевозки</a></li>
				<li><a href="<spring:url value="/main/admin" />">Администрация</a></li>
			</c:when>	
			<c:when test="${roles == '[ROLE_MANAGER]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
			</c:when>	
			<c:when test="${roles == '[ROLE_TOPMANAGER]'}">
				<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
			</c:when>
			<c:when test="${roles == '[ROLE_SHOP]' || roles == '[ROLE_SHOPMANAGER]'}">				
				<li><a href="<spring:url value="/main/shop" />">Магазин</a></li>
			</c:when>	
			<c:when test="${roles == '[ROLE_STOCK]'}">				
				<li><a href="<spring:url value="/main/depot" />">Склад</a></li>
			</c:when>
			<c:when test="${roles == '[ROLE_CARRIER]'}">				
				<li><a href="<spring:url value="/main/carrier" />">Перевозки</a></li>
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
							<a id="logout" href="#"> Выход</a>
						</p>
						<form id="logout-form" action="<c:url value="/logout"/>"
							method="POST">
							<sec:csrfInput />
						</form>

					</li>
					<li><a href="<spring:url value="/home/userpage"/>">Кабинет
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
<script src="<spring:url value="/resources/js/header.js"/>" type="module"></script>