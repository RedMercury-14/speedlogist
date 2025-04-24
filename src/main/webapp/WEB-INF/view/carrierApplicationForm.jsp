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
	<title>Предложение о сотрудничестве</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/carrierApplicationForm.css">
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
					<h2 class="mb-4 text-center">Заявка для сотрудничества</h2>

					<form id="carrierDataForm">

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2">Рынок грузоперевозок:</label><br>
							<div class="form-check form-check-inline mb-2">
								<input class="form-check-input" type="radio" name="market" id="market1" value="Рынок РБ" required>
								<label class="form-check-label" for="market1">Рынок РБ</label>
							</div>
							<div class="form-check form-check-inline mb-2">
								<input class="form-check-input" type="radio" name="market" id="market2" value="Международный рынок" required>
								<label class="form-check-label" for="market2">Международный рынок</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="market" id="market3" value="РБ + Международный рынки" required>
								<label class="form-check-label" for="market3">РБ + Международный рынки</label>
							</div>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="ownership">Форма собственности:</label>
							<select class="form-control" id="ownership" name="ownership" required>
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

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="organization">Название организации:</label>
							<input type="text" class="form-control" id="organization" name="organization" required>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="vehicleCount">Кол-во предлагаемых авто:</label>
							<input type="number" class="form-control" id="vehicleCount" name="vehicleCount" min="1" step="1" placeholder="Целое число" required>
						</div>

						<div class="form-group required-group">
							<label class="text-muted font-weight-bold mb-2">Грузоподъемность (т):</label><br>
							<div id="capCheckboxes" class="checkbox-container d-flex flex-wrap"></div>
							<div class="invalid-feedback">
								Пожалуйста, выберите хотя бы один вариант.
							</div>
						</div>

						<div class="form-group required-group">
							<label class="text-muted font-weight-bold mb-2">Паллетовместимость:</label><br>
							<div id="pallCheckboxes" class="checkbox-container d-flex flex-wrap"></div>
							<div class="invalid-feedback">
								Пожалуйста, выберите хотя бы один вариант.
							</div>
						</div>

						<div class="form-group required-group">
							<label class="text-muted font-weight-bold mb-2">Тип кузова:</label><br>
							<div class="checkbox-container d-flex flex-wrap">
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="type1" name="bodyType_1" value="реф">
									<label class="form-check-label" for="type1">Реф</label>
								</div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="type2" name="bodyType_2" value="изотерма">
									<label class="form-check-label" for="type2">Изотерма</label>
								</div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="type3" name="bodyType_3" value="мебельный">
									<label class="form-check-label" for="type3">Мебельный фургон</label>
								</div>
								<div class="form-check form-check-inline">
									<input class="form-check-input" type="checkbox" id="type4" name="bodyType_4" value="тент">
									<label class="form-check-label" for="type4">Тент</label>
								</div>
							</div>
							<div class="invalid-feedback">
								Пожалуйста, выберите хотя бы один вариант.
							</div>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2">Наличие гидроборта:</label><br>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="tail" id="tailYes" value="Да" required>
								<label class="form-check-label" for="tailYes">Да</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="tail" id="tailNo" value="Нет" required>
								<label class="form-check-label" for="tailNo">Нет</label>
							</div>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2">Наличие навигации:</label><br>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="navigation" id="navigationYes" value="Да" required>
								<label class="form-check-label" for="navigationYes">Да</label>
							</div>
							<div class="form-check form-check-inline">
								<input class="form-check-input" type="radio" name="navigation" id="navigationNo" value="Нет" required>
								<label class="form-check-label" for="navigationNo">Нет</label>
							</div>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="city">Город, в котором расположен транспорт:</label>
							<input type="text" class="form-control" id="city" name="city" required>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="phone">Телефон для связи:</label>
							<input type="tel" class="form-control" id="phone" name="phone" required>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="phone">ФИО:</label>
							<input type="text" class="form-control" id="fio" name="fio" required>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-2" for="email">Адрес эл. почты:</label>
							<input type="email" class="form-control" id="email" name="email" required>
						</div>

						<div class="d-flex justify-content-center">
							<button type="submit" class="btn btn-block btn-lg btn-success">Хочу сотрудничать!</button>
						</div>
					</form>
				</div>
				<div id="success-message-container" class="d-none text-center">
					<div class="alert alert-success" role="alert" style="font-size: 1.2rem;">
						Ваша заявка успешно отправлено!<br>
						Благодарим за проявленный интерес. Наш менеджер свяжется с вами в ближайшее время.
					</div>
					<!-- <div class="spinner-border text-success mt-3" role="status">
						<span class="sr-only">Загрузка...</span>
					</div> -->
				</div>
			</div>
		</div>

		<div id="snackbar"></div>

	</div>

	<jsp:include page="footer.jsp" />

	<script src="${pageContext.request.contextPath}/resources/js/carrierApplicationForm.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>