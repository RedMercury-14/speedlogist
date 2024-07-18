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
#map { 
width: 900px; 
height: 580px
 }
</style>
<title>Insert title here</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css"> -->
<!-- <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css"> -->
<link rel="stylesheet" href="https://unpkg.com/leaflet@1.9.2/dist/leaflet.css"/>
<link rel="stylesheet" href="https://unpkg.com/leaflet-routing-machine@latest/dist/leaflet-routing-machine.css" />
<script	src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"	type="text/javascript"></script>
<!-- <script	src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script> -->
<script src="https://unpkg.com/leaflet@1.9.2/dist/leaflet.js"></script>
<script src="https://unpkg.com/leaflet-routing-machine@latest/dist/leaflet-routing-machine.js"></script>
</head>
<body>
<%-- <jsp:include page="header.jsp"/> --%>
 <div id="map"></div>
</body>
<script	src="${pageContext.request.contextPath}/resources/js/map.js" type="module"></script>
</html>