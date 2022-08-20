<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<nav class="navbar navbar-inverse navbar-static-top">
	<div class="container">
		<div class="navbar-header">
			<a href="<spring:url value="/main"/>" class="navbar-brand">Speed Logist</a>
		</div>
		<ul class="nav navbar-nav">
			
			<li><a href="<spring:url value="/main/logistics" />">Отдел логистики</a></li>
			<li><a href="<spring:url value="/main/depot" />">Склад</a></li>
			<li><a href="<spring:url value="/main/shop" />">Магазин</a></li>
			<li><a href="<spring:url value="/main/carrier" />">Перевозки</a></li>
			<li><a href="<spring:url value="/main/admin" />">Администрация</a></li>
			
			
			<sec:authorize access="authenticated" var="authenticated" />

			<c:choose>
				<c:when test="${authenticated}">
					<li>
						<p class="navbar-text">
							Welcome 
							<sec:authentication property="name" />
							<a id="logout" href="#"> Выход</a> 
						</p>
						<form id="logout-form" action="<c:url value="/logout"/>" method="POST">
							<sec:csrfInput/>
						</form>
						
					</li>					
					<li>					
						<a href="<spring:url value="/home/userpage"/>">Кабинет пользователя</a>
					</li>
				</c:when>
				<c:otherwise>
					<li><a href="<spring:url value="/main/registration" />">Регистрация</a></li>
					<li><a href="<spring:url value="/main/signin"/>">Вход в систему</a></li>
				</c:otherwise>
			</c:choose>
		</ul>
	</div>
</nav>
<script src="<spring:url value="/resources/js/global.js"/>"></script>