<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Менеджер заявок</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/procurementControl.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
	<div class="container-fluid my-container px-0">
		<div class="title-container">
			<strong><h3>Менеджер заявок</h3></strong>
		</div>
		<div class="accordion">
			<div class="search-form-container">
				<button class="accordion-btn collapsed" data-toggle="collapse" href="#orderSearchForm" role="button" aria-expanded="true" aria-controls="orderSearchForm">
					Поиск заявок
				</button>
				<form class="collapse" action="" id="orderSearchForm">
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">С</label>
						<input class="form-control" type="date" name="date_from" id="date_from" required>
					</div>
					<div class="input-row-container">
						<label class="text-muted font-weight-bold">по</label>
						<input class="form-control" type="date" name="date_to" id="date_to" required>
					</div>
					<input class="form-control" type="text" name="searchName" id="searchName" placeholder="Наименование контрагента...">
					<button class="btn btn-outline-secondary" type="submit">Отобразить</button>
				</form>
			</div>
		</div>
		<div id="myGrid" class="ag-theme-alpine"></div>
		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно отмены заявки -->
	<div class="modal fade" id="cancelOrderModal" tabindex="-1" aria-labelledby="cancelOrderModalLabel" aria-hidden="true">
		<div class="modal-dialog">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="middleUnloadPointModalLabel">
						Отмена заявки №
						<span id="canceledOrderId"></span>
					</h5>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form action="" id="cancelOrderForm">
					<div class="modal-body">
						<input type="text" class="form-control" name="idOrder" id="idOrder" hidden>
						<div class="form-group">
							Дата выгрузки в заявке:	<span class="font-weight-bold" id="canceledOrderDate"></span>
						</div>
						<div class="input-row-container">
							<span class="text-muted font-weight-bold">Новая дата выгрузки:</span>
							<input type="date" class="form-control" name="newUnloadDate" id="newUnloadDate" required>
						</div>
						<small class="form-text text-muted">
							Данная дата будет отражена в уведомлении менеджеру, создавшему данную заявку
						</small>
					</div>
					<div class="modal-footer">
						<button class="btn btn-outline-danger" type="submit">Отменить заявку</button>
						<button class="btn btn-secondary" type="button" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<jsp:include page="footer.jsp" />
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/orderSupportControlLogist.js" type="module"></script>
</body>
</html>