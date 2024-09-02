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
	<title>Загрузить отчет</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/orderSupportTimeControlView.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div class="container my-container">
		<form id="reportForm" action="">
			<div class="form-group mb-0">
				<label class="col-form-label text-muted font-weight-bold">Загрузите файл Excel</label>
				<input type="file" class="form-control btn-outline-secondary"
						name="excel" id="excel"
						accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
						required
				>
			</div>
			<br>
			<div class="formButton-container">
				<button class="btn btn-primary" data-type="487" type="submit">Загрузить 487 отчет</button>
				<!-- <button class="btn btn-primary" data-type="need" type="submit">Загрузить отчет с инф-й о потребности</button> -->
				<button class="btn btn-primary" data-type="promotions" type="submit" disabled>Загрузить отчет с акциями</button>
			</div>
		</form>

		<br>
		<br>
		<div>
			<textarea class="form-control" id="stackTrace" cols="30" rows="20"></textarea>
		</div>

		<div id="snackbar"></div>
	</div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/orderSupportTimeControlView.js" type="module"></script>
</body>
</html>