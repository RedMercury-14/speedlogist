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
	<title>Настройки профиля</title>
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
			<form:form modelAttribute="user" method="POST">
				<div class="card-header d-flex justify-content-between">
					<h3 class="mb-0">Редатирование профиля</h3>
				</div>
				<div class="card-body">
					<input type="hidden" value="${user.numContract}" id="numContractFromServer">
					<form:hidden path="login" id="login" />
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Новый пароль</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="password" id="password" />
							<div class="font-weight-light text-muted">Если оставить поле пустым, то пароль изменен не будет</div>
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Имя</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="name" id="name" value="${user.name}" required />
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Фамилия</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="surname" id="surname" value="${user.surname}" required />
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Отчество</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="patronymic" id="patronymic" value="${user.patronymic}" required />
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">E-Mail</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="eMail" id="eMail" value="${user.eMail}" required />
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Номер телефона</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="telephone" id="telephone" value="${user.telephone}" required />
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">ФИО директора (полностью)</label>
						<div class="col-sm-9">
							<input type="text" class="form-control" name="director" id="director" value="${user.director}" required />
						</div>
					</div>
					<c:choose>
						<c:when test="${roles == '[ROLE_ADMIN]'}">
							<div class="border-top border-bottom pt-2 mb-2">
								<div class="form-group row">
									<label class="col-sm-3 col-form-label">Введите компанию</label>
									<div class="col-sm-9">
										<form:input class="form-control" path="companyName" value="Доброном" />
									</div>
								</div>
								<div class="form-group row">
									<label class="col-sm-3 col-form-label">Введите должность</label>
									<div class="col-sm-9">
										<form:input class="form-control" path="companyName" value="Доброном" />
									</div>
								</div>
								<div class="form-group row">
									<label class="col-sm-3 col-form-label">Права доступа (роли)</label>
									<div class="col-sm-9">
										<select class="form-control"  name="role" required="true">
											<option>${role.authority}</option>
											<option value="1">Администратор</option>
											<option value="2">Топ менеджер</option>
											<option value="3">Менеджер</option>
											<option value="4">Магазин</option>
											<option value="6">Склад(Пока что тест карты)</option>
											<option value="99">Отдел закупок</option>
											<option value="10">Отдел сопровождения закупок</option>
											<option value="11">Слот наблюдатель</option>
											<option value="12">Аналитик</option>
											<option value="13">Заказ транспорта для вн. пер-й</option>
											<option value="14">Специалист ОРЛ</option>
  											<option value="15">Специалист отдела транспортной логистики (занимается развозом)</option>
										</select>
									</div>
								</div>
							</div>
						</c:when>
						<c:otherwise>
							<form:input path="companyName" value="Доброном" type="hidden" />
							<form:input path="department" type="hidden" />
						</c:otherwise>
					</c:choose>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Номер договора</label>
						<div class="col-sm-4">
							<input class="form-control" name="numContract" required />
						</div>
						<label class="col-sm-0 col-form-label">от</label>
						<div class="col-sm-4">
							<input class="form-control" name="dateContract" type="text" required />
						</div>
					</div>
					<div class="form-group row">
						<label class="col-sm-3 col-form-label">Банковские реквизиты</label>
						<div class="col-sm-9">
							<textarea class="form-control" name="requisites" id="requisites" rows="3">${user.requisites}</textarea>
						</div>
					</div>
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
	<script src="${pageContext.request.contextPath}/resources/js/myMessage.js" type="module"></script>
</body>
</html>