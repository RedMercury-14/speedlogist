<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
	<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<style type="text/css">
	.chat{
		display: none;
	}
	.messsages{
		background-color: #65baf1;
		width: 500px;
		padding: 20px;		
	}
	.messsages .message{
		background-color: #fff;
		border-radius: 10px;
		margin-bottom: 10px;
		overflow: hidden;
	}
	.messsages .message .fromUser{
		background-color: #396;
		line-height: 30px;
		text-align: center;
		color: white;
	}
	.messsages .message .text{
		padding: 10px;
		
	}
	textarea.text {
		width: 500px;
		padding: 10px;
		resize: none;
		border: none;
		box-shadow: 2px 2px 5px 0 inset;
		
	}	
</style>
<meta charset="UTF-8">
<title>Чат</title>
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
	type="text/javascript"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/vue@2/dist/vue.js"></script>
<script src="https://cdn.jsdelivr.net/npm/vue-resource@1.5.3"></script>
</head>
<body>
<h2>Типо чат</h2>
<!--  <div id="app"></div>-->
<div class="container">
	<div class="start">
		<input type="text" class="userName" placeholder="введите имя...">
		<button id="start" class="btn">Start</button>
	</div>
	<br>
	<div class="chat">
		<div class="messages">
			
		</div>
		<textarea class="text"></textarea>
	</div>
</div>

<script type="text/javascript" src="${pageContext.request.contextPath}/resources/js/chatTest.js"></script>
</body>
</html>