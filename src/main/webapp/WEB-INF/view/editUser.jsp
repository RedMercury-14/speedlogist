<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<style>
		.navbar {
			background-color: #0e377b !important;
		}
		.my-container {
			margin-top: 83px;
			font-family: var(--font-family-sans-serif);
		}
		.input-row-container {
			display: flex;
			align-items: center;
			gap: 10px;
		}
	</style>
	<meta charset="UTF-8">
	<title>Редактирование профиля</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<sec:authorize access="isAuthenticated()">
		<strong>
			<sec:authentication property="principal.authorities" var="roles" />
		</strong>
	</sec:authorize>
	<div class="container my-container">
		<div class="card">
			<form:form modelAttribute="user" id="editUserForm" method="POST">
				<div class="card-header d-flex justify-content-between">
					<h3 class="mb-0">Редактирование профиля</h3>
				</div>
				<div class="card-body">
					<input type="hidden" value="${user.numContract}" id="numContractFromServer">
					<form:hidden path="login" id="login" />
					<div class="form-group row">
						<span class="col-sm-3 col-form-label text-muted font-weight-bold">Новый пароль</span>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="password" />
							<div class="font-weight-light text-muted">Если оставить поле пустым, то пароль изменен не будет</div>
						</div>
					</div>
					<div class="form-group row">
						<span class="col-sm-3 col-form-label text-muted font-weight-bold">Имя</span>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="name"value="${user.name}" required />
						</div>
					</div>
					<div class="form-group row">
						<span class="col-sm-3 col-form-label text-muted font-weight-bold">Фамилия</span>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="surname" value="${user.surname}" required />
						</div>
					</div>
					<div class="form-group row">
						<span class="col-sm-3 col-form-label text-muted font-weight-bold">Отчество</span>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="patronymic" value="${user.patronymic}" required />
						</div>
					</div>
					<div class="form-group row">
						<span class="col-sm-3 col-form-label text-muted font-weight-bold">E-Mail</span>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="eMail" value="${user.eMail}" required />
						</div>
					</div>
					<div class="form-group row">
						<span class="col-sm-3 col-form-label text-muted font-weight-bold">Номер телефона</span>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="telephone" value="${user.telephone}" required />
						</div>
					</div>
					<c:choose>
						<c:when test="${roles == '[ROLE_CARRIER]'}">
							<div class="form-group row">
								<span class="col-sm-3 col-form-label text-muted font-weight-bold">ФИО директора (полностью)</span>
								<div class="col-sm-9">
									<input type="text" class="form-control" name="director" value="${user.director}" required />
								</div>
							</div>
							<div class="form-group row">
								<span class="col-sm-3 col-form-label text-muted font-weight-bold">Номер договора</span>
								<div class="col-5 col-sm-4">
									<input class="form-control" name="numContract" required />
								</div>
								<span class="col-sm-0 col-form-label ">от</span>
								<div class="col-6 col-sm-4">
									<input class="form-control" name="numContract_date" type="date" required />
									<input class="form-control" name="dateContract" type="hidden" />
								</div>
							</div>
							<div class="form-group row">
								<span class="col-sm-3 col-form-label text-muted font-weight-bold">УНП</span>
								<div class="col-sm-9">
									<c:choose>
										<c:when test="${user.numYNP != null}">
											<input type="number" class="form-control" name="numYNP" value="${user.numYNP}" readonly required />
										</c:when>
										<c:otherwise>
											<input type="number" class="form-control" name="numYNP" required />
										</c:otherwise>
									</c:choose>
								</div>
							</div>
							<div class="form-group row">
								<span class="col-sm-3 col-form-label text-muted font-weight-bold">Банковские реквизиты</span>
								<div class="col-sm-9">
									<textarea class="form-control" name="requisites" id="requisites" rows="3">${user.requisites}</textarea>
								</div>
							</div>
						</c:when>
					</c:choose>
				</div>
				<div class="card-footer d-flex justify-content-end">
					<button class="btn btn-lg btn-primary mr-2" type="submit">Сохранить</button>
					<button class="btn btn-lg btn-secondary" type="button" onclick="history.back();">Отмена</button>
				</div>
			</form:form>
		</div>
	</div>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/editUser.js"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/tenderNotifications.js" type="module"></script>
</body>
</html>