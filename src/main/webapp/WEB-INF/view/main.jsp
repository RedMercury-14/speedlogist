<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html lang="en-US" class="no-js">
    <style type="text/css">
        body {
            background-color: #0e377b  !important;
        }
        .sb-thumb {
            border: 4px solid #795548;
        }
    </style>
	<head>
		<meta charset="UTF-8">
        <meta name="${_csrf.parameterName}" content="${_csrf.token}" />
        <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
		<meta http-equiv="X-UA-Compatible" content="IE=edge">
		<title>SpeedLogist</title>
        <link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
        <!-- MAIN CSS STYLE SHEET -->
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/custom.css">
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/slick.min.css"> 
        <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/mainPage/owl.carousel.min.css">
        <link rel='stylesheet' href="${pageContext.request.contextPath}/resources/css/mainPage/flickity.min.css">
	</head>
<body>
    <sec:authorize access="authenticated" var="authenticated" />
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
    
    <jsp:include page="headerNEW.jsp" />

    <!--SLIDER START-->
    <div class="home-slider dot-hide">
        <!-- partial:index.partial.html -->
        <div class="hero-slider" data-carousel>
          <div class="carousel-cell" style="background-image:url(${pageContext.request.contextPath}/resources/img/mainPage/images/home-slider-7.jpg);">
            <div class="overlay"></div>
            <div class="container slider-caption">
              <h5 class="subtitle">Доставка точно в сроки</h5>
              <h2 class="title">Биржа международных маршрутов<br>на склады компании Доброном</h2>
              <c:choose>
                  <c:when test="${!authenticated}">
                    <a href="./main/registration"><button class="cargoy-btn-white">Зарегистрироваться</button></a>
                  </c:when>
              </c:choose>
            </div>
          </div>
        <div class="carousel-cell" style="background-image:url(${pageContext.request.contextPath}/resources/img/mainPage/images/home-slider-8.jpg);">
            <div class="overlay"></div>
            <div class="container slider-caption">
              <h5 class="subtitle">Доставка по Республике Беларусь</h5>
              <h2 class="title">Развоз товаров по <br> магазинам сети</h2>
              <c:choose>
                  <c:when test="${!authenticated}">
                    <a href="./main/registration"><button class="cargoy-btn-white">Зарегистрироваться</button></a>
                  </c:when>
              </c:choose>
            </div>
          </div>
        <div class="carousel-cell" style="background-image:url(${pageContext.request.contextPath}/resources/img/mainPage/images/home-slider-9.jpg);">
            <div class="overlay"></div>
            <div class="container slider-caption">
              <h5 class="subtitle">Срочные грузы</h5>
              <h2 class="title">Доставка продуктов <br>собственного производства</h2>
              <button class="cargoy-btn-white">Узнать больше</button>
            </div>
          </div>
        </div>
        <!-- partial -->
    </div>
    <!--SLIDER END-->
    
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
    
    <!-- CONTENT START -->
    <section>
        <!-- WIDE SECTION COUNTER START -->
        <div class="counter-wrapper">
           <div class="container">
                <div class="counter-layer">
                    <div class="row">
                      <div class="col-sm-6 col-lg-3"  data-aos="fade-left">
                        <div class="counter-box">
                            <div class="counter" data-count="933">0</div>
                            <p>Поставщиков РБ</p>
                        </div>    
                      </div>
                      <div class="col-sm-6 col-lg-3 mt-counter-t">
                        <div class="counter-box">
                            <div class="counter" data-count="690">0</div>
                            <p>Работников складов</p>
                        </div>    
                      </div>
                      <div class="col-sm-6 col-lg-3 mt-counter-d">
                        <div class="counter-box">
                            <div class="counter" data-count="1450">0</div>
                            <p>Паллет в сутки</p>
                        </div>    
                      </div>
                      <div class="col-sm-6 col-lg-3 mt-counter-d">
                        <div class="counter-box">
                            <div class="counter" data-count="1050">0</div>
                            <p>Магазинов</p>
                        </div>    
                      </div>
                    </div>
                </div>
           </div>
        </div>
        <!-- WIDE SECTION COUNTER END -->
    </section> 
    <!-- CONTENT END -->

    <!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>
    
    <jsp:include page="footer.jsp" />
    
    <!--SCROLL TOP START-->
    <a href="#0" class="cd-top">Top</a>
    <!--SCROLL TOP START-->

    <!-- JAVASCRIPTS -->
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/plugins.js'></script>
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/main.js'></script>
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/flickity.pkgd.min.js'></script>
    <script src="${pageContext.request.contextPath}/resources/js/mainPage/slider.js"></script>
    <script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
    <script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
    <!-- JAVASCRIPTS END -->
    
    </body>
    
</html>