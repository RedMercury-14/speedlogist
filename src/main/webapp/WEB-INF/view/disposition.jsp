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
<style type="text/css">
table {
	border: 3px solid grey;
	width: 100%;
	text-align: center;
	table-layout: fixed;
	border-collapse: separate;
	empty-cells: hide;
}
/* границы ячеек первого ряда таблицы */
th {
	border: 2px solid grey;
	text-align: center;
}
/* границы ячеек тела таблицы */
td {
	border: 1px solid grey;
}

tr:hover td {
	background: #e8edff;
}

.active {
	background: #c4ffe1db;
}

.none {
	display: none;
}
</style>
<title>Диспозиция</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
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
<body>
	<jsp:include page="header.jsp" />
	<div class="container">
		<label><h3>Диспозиция</h3></label>
	</div>
	<div class="container-fluid">
		<table id="table">
			<thead class="text-center">
				<tr>
					<th>Маршрут</th>
					<th>Подача машины</th>
					<th>На месте зазгрузки</th>
					<th>Начали загружать</th>
					<th>Загружена</th>
					<th>На таможне отправления</th>
					<th>Затаможена</th>
					<th>В пути</th>
					<th>Проходит границу</th>
					<th>На таможне назначения</th>
					<th>Растаможена</th>
					<th>На выгрузке</th>
				</tr>
			</thead>
		</table>
	</div>
	<div class="container-fluid"><input type="button" onclick="history.back();" value="Назад"/></div>
	<script
		src="${pageContext.request.contextPath}/resources/js/disposition.js" type="module"></script>
</body>
</html>