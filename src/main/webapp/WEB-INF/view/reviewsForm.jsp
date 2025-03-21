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
	<title>Форма обратной связи</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/reviewsForm.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container my-container">
		<br>
		<br>
		<div class="card shadow-sm">
			<div class="card-body">
				<div id="form-container">
					<h2 class="mb-4 text-center">Форма обратной связи</h2>
					<form id="feedbackForm">
						<div class="form-group">
							<label for="sender">От кого:</label>
							<input type="text" class="form-control" id="sender" name="sender" placeholder="ФИО или название организации">
						</div>
						<div class="form-group">
							<label for="topic">Тема:</label>
							<select class="form-control" id="topic" name="topic" required>
								<option selected disabled value="">Выберите тему сообщения</option>
								<option value="Поддержка">Поддержка</option>
								<option value="Предложение">Предложение</option>
								<option value="Жалоба">Жалоба</option>
								<option value="Другое">Другое</option>
								<option value="Выбрать отдел">Выбрать отдел (для сотрудников)</option>
							</select>
						</div>
						<div id="department-group" class="form-group d-none">
							<label for="department">Отдел:</label>
							<select class="form-control" id="department" name="department">
								<option selected disabled value="">Выберите отдел</option>
								<option value="Отдел сопровождения и управления заказами.">Отдел сопровождения и управления заказами.</option>
								<option value="Отдел обратной логистики и качества">Отдел обратной логистики и качества</option>
								<option value="Отдел контроля качества продукции">Отдел контроля качества продукции</option>
								<option value="Отдел транспортной логистики">Отдел транспортной логистики</option>
								<option value="Отдел таможенного декларирования">Отдел таможенного декларирования</option>
								<option value="Отдел регионального складирования">Отдел регионального складирования</option>
								<option value="Отдел международных перевозок">Отдел международных перевозок</option>
								<option value="Отдел распределительной логистики">Отдел распределительной логистики</option>
								<option value="Отдел фасовки">Отдел фасовки</option>
								<option value="Отдел сертификации">Отдел сертификации</option>
								<option value="Отдел учета и выписки">Отдел учета и выписки</option>
								<option value="Отдел разработок и оптимизации">Отдел разработок и оптимизации</option>
								<option value="Отдел бизнес-аналитики">Отдел бизнес-аналитики</option>
								<option value="Отдел продаж логистических услуг">Отдел продаж логистических услуг</option>
								<option value="Отдел сопровождения управления складирования">Отдел сопровождения управления складирования</option>
								<option value="Отдел Производственного обучения и адаптации персонала">Отдел Производственного обучения и адаптации персонала</option>
								<option value="Отдел экспедиции управления складирования">Отдел экспедиции управления складирования</option>
								<option value="Склад 1800 Прилесье Распределительный центр №5">Склад 1800 Прилесье Распределительный центр №5</option>
								<option value="Склад 1250 Распределительный центр №3">Склад 1250 Распределительный центр №3</option>
								<option value="Склад 1700 Прилесье Распределительный центр №1">Склад 1700 Прилесье Распределительный центр №1</option>
								<option value="Склад 1200 Таборы Распределительный центр №2">Склад 1200 Таборы Распределительный центр №2</option>
								<option value="Склад 1100 Таборы Распределительный центр №4">Склад 1100 Таборы Распределительный центр №4</option>
								<option value="Эксплуатационный участок">Эксплуатационный участок</option>
								<option value="Отдел по претензионной работе">Отдел по претензионной работе</option>
								<option value="Административно-хозяйственный отдел">Административно-хозяйственный отдел</option>
								<option value="Участок по реализации и учету вторичного сырья">Участок по реализации и учету вторичного сырья</option>
							</select>
						</div>
						<div class="form-group">
							<label for="reviewBody">Сообщение:</label>
							<textarea class="form-control" id="reviewBody" name="reviewBody" rows="4" required></textarea>
						</div>
						<div class="form-group callback-group">
							<div class="form-check">
								<input type="checkbox" class="form-check-input" id="needReply" name="needReply">
								<label class="form-check-label ml-2" for="needReply">Нужна обратная связь</label>
							</div>
							<div id="email-group" class="d-none">
								<input type="email" class="form-control" id="email" name="email" placeholder="Ваш email">
							</div>
						</div>
						<button type="submit" class="btn btn-primary mt-3 btn-block">Отправить</button>
					</form>
				</div>
				<div id="success-message-container" class="d-none">
					<div class="text-center p-4">
						<div class="alert alert-success" role="alert" style="font-size: 1.2rem;">
							<strong>Сообщение успешно отправлено!</strong><br>
							Благодарим за обратную связь. Сейчас вы попадете на главную страницу...
						</div>
						<div class="spinner-border text-success mt-3" role="status">
							<span class="sr-only">Загрузка...</span>
						</div>
					</div>
				</div>
			</div>
		</div>

		<div id="snackbar"></div>

	</div>

	<jsp:include page="footer.jsp" />

	<script src="${pageContext.request.contextPath}/resources/js/reviewsForm.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>