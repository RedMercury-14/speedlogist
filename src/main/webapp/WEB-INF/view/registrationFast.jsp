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
	<title>Быстрая регистрация</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/registrationFast.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<sec:authorize access="isAuthenticated()">  
		<strong><sec:authentication property="principal.authorities" var="roles"/></strong>
		<sec:authentication property="name" var="login"/>
	</sec:authorize>

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container my-container">
		
		<div class="card">
			<form id="newCarrierForm">
				<div class="card-header d-flex justify-content-center">
					<h3 class="mb-0" id="newCarrierModalLabel">Регистрация нового перевозчика</h3>
				</div>
				<div class="card-body">

					<div class="mb-3"><span class="text-danger"> *</span> - поля обязательные для заполнения</div>

					<div class="form-row">

						<div class="form-group col-lg-4 col-md-12">
							<span class="text-muted font-weight-bold mb-2">Логин<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="login" placeholder="Логин для входа в систему" required>
							<small class="form-text text-muted">
								Разрешены: буквы (a-z, A-Z), цифры (0-9), символы (@#$%^&*()_+!~-=[]{}|;:',.?\/)
							</small>
							<div class="invalid-feedback">
								Логин должен содержать только разрешённые символы!
							</div>
							<small class="" id="messageLogin"></small>
						</div>

						<div class="form-group col-lg-4 col-md-6">
							<span class="text-muted font-weight-bold mb-2">Телефон<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="phone" placeholder="+375XXYYYYYYY" required>
						</div>

						<div class="form-group col-lg-4 col-md-6">
							<span class="text-muted font-weight-bold mb-2">Адрес эл. почты<span class="text-danger"> *</span></span>
							<input type="email" class="form-control" name="email" placeholder="example@gmail.com" required>
						</div>

						<div class="form-group col-md-4">
							<span class="text-muted font-weight-bold mb-2">Фамилия<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="surname" placeholder="Иванов" required>
						</div>

						<div class="form-group col-md-4">
							<span class="text-muted font-weight-bold mb-2">Имя<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="name" placeholder="Иван" required>
						</div>

						<div class="form-group col-md-4">
							<span class="text-muted font-weight-bold mb-2">Отчество</span>
							<input type="text" class="form-control" name="patronymic" placeholder="Иванович">
						</div>

						<div class="form-group col-lg-4 col-md-12">
							<span class="text-muted font-weight-bold mb-2">Страна регистрации<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="countryOfRegistration" placeholder="Страна, регистрации юр. лица" required>
						</div>

						<div class="form-group col-lg-5 col-md-7 col-sm-7">
							<span class="text-muted font-weight-bold mb-2">Номер договора<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="numcontract_num" placeholder="Номер договора с Доброном" required>
						</div>

						<div class="form-group col-lg-3 col-md-5 col-sm-5">
							<span class="text-muted font-weight-bold mb-2">от<span class="text-danger"> *</span></span>
							<input type="date" class="form-control" name="numcontract_date" required>
						</div>

						<div class="form-group col-lg-4 col-md-12">
							<span class="text-muted font-weight-bold mb-2">Форма собственности<span class="text-danger"> *</span></span>
							<select class="form-control" name="propertySize" required>
								<option value="" selected disabled>Укажите форму собственности</option>
								<option value="ОАО">ОАО</option>
								<option value="ООО">ООО</option>
								<option value="ИП">ИП</option>
								<option value="ЧУП">ЧУП</option>
								<option value="УП">УП</option>
								<option value="ОДО">ОДО</option>
								<option value="ЗАО">ЗАО</option>
								<option value="Крестьянское (фермерское) хозяйство">Крестьянское (фермерское) хозяйство</option>
								<option value="ЧП">ЧП</option>
								<option value="ЧПУП">ЧПУП</option>
								<option value="ЧТУП">ЧТУП</option>
								<option value="ЧАУП">ЧАУП</option>
								<option value="УПТЧП">УПТЧП</option>
								<option value="СП">СП</option>
								<option value="РУП">РУП</option>
								<option value="УПП">УПП</option>
								<option value="ЧТТУП">ЧТТУП</option>
								<option value="УЧТП">УЧТП</option>
								<option value="ТУП">ТУП</option>
								<option value="СООО">СООО</option>
								<option value="ЧТЭУП">ЧТЭУП</option>
								<option value="ЧУТП">ЧУТП</option>
								<option value="ИУП">ИУП</option>
								<option value="АТУП">АТУП</option>
								<option value="ПАО">ПАО</option>
								<option value="АО">АО</option>
								<option value="ТОО">ТОО</option>
							</select>
						</div>

						<div class="form-group col-lg-8 col-md-12">
							<span class="text-muted font-weight-bold mb-2">Наименование компании<span class="text-danger"> *</span></span>
							<input type="text" class="form-control" name="companyName" placeholder="Наименование компании без указания формы собственности" required>
						</div>

					</div>
				</div>
				<div class="card-footer d-flex justify-content-center">
					<button type="submit" class="btn btn-lg btn-success">Зарегистрировать</button>
				</div>
			</form>
		</div>
		

		<div id="snackbar"></div>
	</div> <!-- my-container -->
	
	<jsp:include page="footer.jsp" />

	<script src="${pageContext.request.contextPath}/resources/js/registrationFast.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>