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
	<title>Кабинет поставщика</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/supplier.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel='stylesheet' href="${pageContext.request.contextPath}/resources/css/font-awesome/css/all.min.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container my-container">

		<h2 class="py-3 mb-4 text-center text-white">Добро пожаловать в кабинет поставщика [Название поставщика]</h2>

		<div class="row">

			<!-- Карточка "Регистрация работника" -->
			<div class="col-md-4 mb-4">
				<div class="card nav-card shadow-sm" data-toggle="modal" data-target="#newSupplierModal">
					<div class="card-body text-center">
						<div class="card-icon mb-3"><i class="fas fa-user-plus"></i></div>
						<div class="card-title">Регистрация работника</div>
						<p class="card-text">Создание нового пользователя в системе.</p>
					</div>
				</div>
			</div>

			<!-- Карточка 1 -->
			<div class="col-md-4 mb-4">
				<a href="orders.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-box"></i></div>
							<div class="card-title">Заказы</div>
							<p class="card-text">Просмотр и управление заказами от клиентов.</p>
						</div>
					</div>
				</a>
			</div>

			<!-- Карточка 2 -->
			<div class="col-md-4 mb-4">
				<a href="products.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-tags"></i></div>
							<div class="card-title">Товары</div>
							<p class="card-text">Управление ассортиментом товаров и их доступностью.</p>
						</div>
					</div>
				</a>
			</div>

			<!-- Карточка 3 -->
			<div class="col-md-4 mb-4">
				<a href="analytics.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-chart-line"></i></div>
							<div class="card-title">Аналитика</div>
							<p class="card-text">Статистика продаж и популярность товаров.</p>
						</div>
					</div>
				</a>
			</div>

			<!-- Карточка 2 -->
			<div class="col-md-4 mb-4">
				<a href="products.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-tags"></i></div>
							<div class="card-title">Товары</div>
							<p class="card-text">Управление ассортиментом товаров и их доступностью.</p>
						</div>
					</div>
				</a>
			</div>

			<!-- Карточка 3 -->
			<div class="col-md-4 mb-4">
				<a href="analytics.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-chart-line"></i></div>
							<div class="card-title">Аналитика</div>
							<p class="card-text">Статистика продаж и популярность товаров.</p>
						</div>
					</div>
				</a>
			</div>

			<!-- Карточка 2 -->
			<div class="col-md-4 mb-4">
				<a href="products.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-tags"></i></div>
							<div class="card-title">Товары</div>
							<p class="card-text">Управление ассортиментом товаров и их доступностью.</p>
						</div>
					</div>
				</a>
			</div>

			<!-- Карточка 3 -->
			<div class="col-md-4 mb-4">
				<a href="analytics.html" class="text-decoration-none text-dark">
					<div class="card nav-card shadow-sm">
						<div class="card-body text-center">
							<div class="card-icon mb-3"><i class="fas fa-chart-line"></i></div>
							<div class="card-title">Аналитика</div>
							<p class="card-text">Статистика продаж и популярность товаров.</p>
						</div>
					</div>
				</a>
			</div>

		</div> <!-- row -->

		<div id="snackbar"></div>
	</div> <!-- my-container -->


	<!-- Модальное окно регистрации нового пользователя о поставщика -->
	<div class="modal fade" id="newSupplierModal" tabindex="-1" aria-labelledby="newSupplierModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<form id="newSupplierForm">
					<div class="modal-header align-items-center bg-color">
						<h5 class="modal-title" id="newSupplierModalLabel">Регистрация нового работника</h5>
						<button type="button" class="close text-white" data-dismiss="modal" aria-label="Закрыть">
							<span aria-hidden="true">&times;</span>
						</button>
					</div>
					<div class="modal-body">

						<div class="mb-3"><span class="text-danger"> *</span> - поля обязательные для заполнения</div>

						<div class="form-row">

							<div class="form-group col-lg-4 col-md-12">
								<label class="text-muted font-weight-bold mb-2" for="login">Логин<span class="text-danger"> *</span></label>
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
								<label class="text-muted font-weight-bold mb-2" for="phone">Телефон<span class="text-danger"> *</span></label>
								<input type="text" class="form-control" name="phone" placeholder="+375XXYYYYYYY" required>
							</div>
	
							<div class="form-group col-lg-4 col-md-6">
								<label class="text-muted font-weight-bold mb-2" for="email">Адрес эл. почты<span class="text-danger"> *</span></label>
								<input type="email" class="form-control" name="email" placeholder="example@gmail.com" required>
							</div>

							<div class="form-group col-md-4">
								<label class="text-muted font-weight-bold mb-2" for="name">Имя<span class="text-danger"> *</span></label>
								<input type="text" class="form-control" name="name" placeholder="Иванов" required>
							</div>

							<div class="form-group col-md-4">
								<label class="text-muted font-weight-bold mb-2" for="surname">Фамилия<span class="text-danger"> *</span></label>
								<input type="text" class="form-control" name="surname" placeholder="Иван" required>
							</div>

							<div class="form-group col-md-4">
								<label class="text-muted font-weight-bold mb-2" for="patronymic">Отчество</label>
								<input type="text" class="form-control" name="patronymic" placeholder="Иванович" required>
							</div>

							<div class="form-group col-md-12">
								<label class="text-muted font-weight-bold mb-2" for="address">Адрес<span class="text-danger"> *</span></label>
								<input type="text" class="form-control" name="address" placeholder="Юридический адрес компании" required>
							</div>

							<div class="form-group col-lg-6 col-md-12">
								<label class="text-muted font-weight-bold mb-2" for="propertySize">Форма собственности<span class="text-danger"> *</span></label>
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

							<div class="form-group col-lg-3 col-md-6">
								<label class="text-muted font-weight-bold mb-2" for="numYNP">УНП:<span class="text-danger"> *</span></label>
								<input type="text" class="form-control" name="numYNP" placeholder="Номер УНП" required>
							</div>

							<div class="form-group col-lg-3 col-md-6">
								<label class="text-muted font-weight-bold mb-2" for="city">Код контрагента:<span class="text-danger"> *</span></label>
								<input type="number" class="form-control" name="counterpartyCode" step="1" min="0" placeholder="Только целое число" required>
							</div>

							<div class="form-group col-md-12">
								<label class="text-muted font-weight-bold mb-2" for="companyName">Наименование компании<span class="text-danger"> *</span></label>
								<input type="text" class="form-control" name="companyName" placeholder="Наименование компании без указания формы собственности" required>
							</div>

							<div class="form-group col-md-12">
								<label class="text-muted font-weight-bold mb-2" for="requisites">Реквизиты:<span class="text-danger"> *</span></label>
								<textarea class="form-control" rows="3" name="requisites" placeholder="Банковские реквизиты компании" required></textarea>
							</div>

						</div>
					</div>
					<div class="modal-footer">
						<button type="submit" class="btn btn-primary col-lg-4 col-md-6 col-sm-8">Сохранить</button>
						<button type="button" class="btn btn-secondary col-lg-2 col-md-3 col-sm-3" data-dismiss="modal">Отмена</button>
					</div>
				</form>
			</div>
		</div>
	</div>
	
	<jsp:include page="footer.jsp" />

	<script src="${pageContext.request.contextPath}/resources/js/supplier.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>