<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>       
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>

<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
	<title>Информационная страница</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/custom.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/slick.min.css"> 
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/welcome.css"> 
</head>
<body>

	<!-- PRELOADER START -->
    <div id="loader-wrapper">
        <div class="loader">
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
           <div class="ball"></div>
        </div> 
    </div>
    <!-- PRELOADER END -->

	<jsp:include page="headerNEW.jsp"/>

	<div class="my-content-container">

		<div class="bg-container">
			<input type="hidden" value="${user.numContract}" id="numContractFromServer">
			<div class="overlay"></div>
			<div class="container info-container">
				<h2 class="title">Информация для перевозчика</h2>
				<h5 class="subtitle">Для работы с биржей необходимо предоставить договор по адресу:</h5>
				<p class=" ">ЗАО «ДОБРОНОМ» 220073, г. Минск, пер. Загородный 1-й, 20-23</p>
				<br>
				<h5 class="subtitle">
					<a href="main/carrier/exchange">
						<button class="btn btn-lg  link-btn">С чего начать?</button>
					</a>
				</h5>
			</div>
		</div>
		
		<!-- FRONT BOXES START -->
		<div class="container services-carousel slider">
			<div class="slide">
				<div class="cargoy-sb">
					<figure class="sb-thumb green"><a href="#"><img src="${pageContext.request.contextPath}/resources/img/mainPage/images/tabor.jpg" alt=""></a></figure>
					<div class="sb-caption">
						<figure class="icon-caption"><img src="${pageContext.request.contextPath}/resources/img/mainPage/master/box.svg" alt=""></figure>
						<h4>Таборы</h4>
					</div>
				</div>
			</div>
			<div class="slide">
			   <div class="cargoy-sb">
					<figure class="sb-thumb green"><a href="#"><img src="${pageContext.request.contextPath}/resources/img/mainPage/images/priles.jpg" alt=""></a></figure>
					<div class="sb-caption">
						<figure class="icon-caption"><img src="${pageContext.request.contextPath}/resources/img/mainPage/master/box.svg" alt=""></figure>
						<h4>Прилесье</h4>
					</div>
				</div>
			</div>
			<div class="slide">
			   <div class="cargoy-sb">
					<figure class="sb-thumb green"><a href="#"><img src="${pageContext.request.contextPath}/resources/img/mainPage/images/kylik.jpg" alt=""></a></figure>
					<div class="sb-caption">
						<figure class="icon-caption"><img src="${pageContext.request.contextPath}/resources/img/mainPage/master/box.svg" alt=""></figure>
						<h4>Кулики</h4>
					</div>
				</div>
			</div>
			<div class="slide">
			   <div class="cargoy-sb">
					<figure class="sb-thumb green"><a href="#"><img src="${pageContext.request.contextPath}/resources/img/mainPage/images/sovremenny_sklad.jpg" alt=""></a></figure>
					<div class="sb-caption">
						<figure class="icon-caption"><img src="${pageContext.request.contextPath}/resources/img/mainPage/master/box.svg" alt=""></figure>
						<h4>Наши склады</h4>
					</div>
				</div>
			</div>
		</div>
		<!-- FRONT BOXES END -->
	
		<jsp:include page="footer.jsp" />
	</div>

	<!-- JAVASCRIPTS -->
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/plugins.js'></script>
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/main.js'></script>
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>

