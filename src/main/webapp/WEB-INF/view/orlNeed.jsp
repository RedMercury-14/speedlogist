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
	<title>Потребности</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/orlNeed.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0">

		<sec:authorize access="isAuthenticated()">
			<sec:authentication property="principal.authorities" var="roles" />
		</sec:authorize>

		<div class="title-container">
			<strong><h3>Потребности</h3></strong>
		</div>
		<div class="toolbar">
			
			<div class="btn-group">
				<button type="button" class="btn tools tools-btn px-0 font-weight-bold text-muted" id="datePrev">
					<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
						<path d="M560-240 320-480l240-240 56 56-184 184 184 184-56 56Z"/>
					</svg>
				</button>

				<input class="btn tools-btn font-weight-bold" type="date" name="filterDate" id="filterDate">

				<button type="button" class="btn tools tools-btn px-0 font-weight-bold text-muted" id="dateNext">
					<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#5f6368">
						<path d="M504-480 320-664l56-56 240 240-240 240-56-56 184-184Z"/>
					</svg>
				</button>
			</div>

			<c:choose>
				<c:when test="${roles == '[ROLE_ADMIN]' || roles == '[ROLE_ORDERSUPPORT]'}">
					<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#sendExcelModal">
						Загрузить отчет с инф-й о потребности
					</button>
				</c:when>
			</c:choose>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- модальное окно загрузки таблицы Эксель -->
	<div class="modal fade" id="sendExcelModal" tabindex="-1" aria-labelledby="sendExcelModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header">
					<h1 class="modal-title fs-5 m-0" id="sendExcelModalLabel">Загрузить потребности</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="sendExcelForm" action="">
					<div class="modal-body">
						<div class="inputs-container">
							<div class="form-group">
								<label for="date" class="col-form-label text-muted font-weight-bold">Укажите дату отчета*</label>
								<span></span>
								<input class="form-control w-50" type="date" name="date" id="date">
								<small id="date" class="form-text text-muted">
									* Если дата не указана, то отчет загрузится с датой сегодняшнего дня
								</small>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Загрузите файл Excel</label>
								<input type="file" class="form-control btn-outline-secondary" name="excel"
									id="excel"
									accept=".csv, application/vnd.openxmlformats-officedocument.spreadsheetml.sheet, application/vnd.ms-excel"
									required>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Загрузить</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/orlNeed.js" type="module"></script>
</body>
</html>