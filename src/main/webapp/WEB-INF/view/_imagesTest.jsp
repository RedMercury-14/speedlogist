<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<title>Demo</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/_imagesTest.css">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-custom-caption.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-thumbnails.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-dynamic-caption-plugin.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />

	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>Demo</h3></strong>
		</div>
		<div id="myGrid" class="ag-theme-balham"></div>
		<div class="gallery-container" id="gallery"></div>
		<div id="snackbar"></div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src='${pageContext.request.contextPath}/resources/js/_imagesTest.js' type="module"></script>
</body>
</html>