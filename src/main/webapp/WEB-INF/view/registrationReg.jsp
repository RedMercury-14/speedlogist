<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>

<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Регистрация</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/4.0.3/css/font-awesome.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/registration.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />
	<!-- MultiStep Form -->
	<div class="container" id="formInt">
		<div class="row justify-content-center mt-0">
			<div class="col-11 col-sm-9 col-md-7 col-lg-7 text-center p-0 mt-3 mb-2">
				<div class="card px-0 pt-4 pb-0 mt-3 mb-3">
					<h2><strong>Регистрация перевозчика</strong></h2>
					<p id="form-info">Заполните все поля для перехода к следующему шагу</p>
					<div class="row">
						<div class="col-md-12 mx-0">
							<form:form enctype="multipart/form-data" modelAttribute="user" method="POST" id="regform"
										action="${pageContext.request.contextPath}/main/registration/form?${_csrf.parameterName}=${_csrf.token}">
								<form:input type="hidden" value="${user.check}" path="check" />
								<!-- progressbar -->
								<ul id="progressbar" class="short">
									<li class="active" id="account"><strong>Аккаунт</strong></li>
									<li id="company"><strong>Компания</strong></li>
									<li id="confirm"><strong>Регистрация!</strong></li>
								</ul>
								<!-- fieldsets -->
								<fieldset>
									<div class="form-card">
										<h2 class="fs-title">Информация об аккаунте</h2>
										<div class="form-group">
										</div>
										<input name="login" placeholder="Логин (по нему будет происходить вход в систему)" class="form-control" id="userLogin" required />
										<div class="error-message" id="messageLogin"></div>
					
										<div class="pass-container">
											<div class="form-group">
												<input name="password" placeholder="Пароль" class="form-control" id="password" required />
											</div>
											<div class="form-group">
												<input name="confirmPassword" placeholder="Повторите пароль" class="form-control" id="confirmPassword" required />
											</div>
										</div>
										<div class="error-message" id="message"></div>
										<input name="name" id="name" placeholder="ФИО" class="form-control" required />
										<input name="tel" id="tel" placeholder="Телефон: +375YYXXXXXXX" class="form-control" required />
										<input name="mail" id="mail" type="email" placeholder="Электронная почта (E-mail)" class="form-control" required />
										<div class="form-group">
											<h6>Фото соглашения о соблюдении порядка обработки персональных данных</h6>
											<div class="error-message">Важно: загружайте файлы только формата .png, .jpg или .jpeg</div>
											<input name="agreePersonalData" id="agreePersonalData" type="file" accept=".png, .jpg, .jpeg, " class="form-control" required/>
										</div>
									</div>
									<input type="button" name="next" id="step1Btn" class="next action-button" value="Вперед" />
								</fieldset>
								<fieldset>
									<div class="form-card">
										<h2 class="fs-title">Сведения о компании</h2>
										<div class="form-group">
											<h6>Прикрепите договор (одним файлом)</h6>
											<input name="contract" id="contract" type="file" class="form-control" required/>
										</div>
										<select name="propertySize" id="propertySize" class="form-control" required>
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
										<div class="companyName-container">
											<input type="text" id="propertySizeTooltip" class="form-control" style="width: 80px; background-color: #fff;" readonly>
											<div class="popover fade bs-popover-top" role="tooltip" id="companyNamePopover" style="position: absolute; will-change: transform; top: -80px; left: 100px;" x-placement="top">
												<div class="arrow" style="left: 124px;"></div>
												<div class="popover-body">Указывайте ТОЛЬКО наименование компании без указания формы собственности</div>
											</div>
											<input name="companyName" id="companyName" placeholder="Наименование компании" class="form-control" required />
										</div>
										<input name="countryOfRegistration" id="countryOfRegistration" placeholder="Страна регистрации" class="form-control" required />
										<input name="director" id="director" placeholder="ФИО руководителя организации (полностью)" class="form-control" required />
										<input name="numYNP" id="numYNP" id="numYNP" type="number" placeholder="Номер УНП" class="form-control" required />
										<div class="error-message" id="messageYNP"></div>
										<div class="form-group">
											<textarea name="affiliatedCompanies" id="affiliatedCompanies" placeholder="Наличие дочерних компаний (наименование, страна регистрации)" class="form-control"></textarea>
										</div>
										<div class="form-group">
											<textarea name="requisites" id="requisites" placeholder="Банковские реквизиты фирмы" class="form-control" required></textarea>
											<details>
												<summary>Пример реквизитов</summary>
												<div>
													"ЗАО Доброном: Республика Беларусь,220112, г. Минск, ул. Янки Лучины, 5р/с
													BY61ALFA30122365100050270000 ( BYN)<br>
													открытый в Закрытое акционерное общество «Альфа-банк» Юридический адрес: Ул.
													Сурганова, 43-47 <br>
													220013 Минск, Республика БеларусьУНП 101541947Closed Joint-Stock Company
													«Alfa-Bank»SWIFT – ALFABY2X р/с BY24ALFA30122365100010270000 (USD)<br>р/с
													BY09ALFA30122365100020270000(EUR)<br>р/с BY91 ALFA 3012 2365 1000 3027 0000 (RUB.)"
												</div>
											</details>
										</div>
	
										<h6>Свидетельство о регистрации</h6>
										<div class="registrationCertificate-container">
											<div class="form-group">
												<input name="registrationCertificate_ser" id="registrationCertificate_ser" placeholder="Серия" class="form-control" />
												<input name="registrationCertificate_num" id="registrationCertificate_num" type="number" placeholder="Номер" class="form-control" required />
											</div>
											<div class="form-group">
												<label for="registrationCertificate_date">от </label>
												<input name="registrationCertificate_date" id="registrationCertificate_date" type="date" placeholder="Дата" class="form-control" required />
											</div>
										</div>
										<!-- <div class="form-group">
											<input name="registrationCertificate_file" id="registrationCertificate_file" type="file" class="form-control" />
										</div> -->
									</div>
									<input type="button" name="previous" class="previous action-button-previous" value="Назад"/>
									<input type="submit" name="make_payment" class="next action-button" id="step2Btn" value="Подтвердить"/>
								</fieldset>
								<fieldset>
									<div class="form-card">
										<h2 id="finishTitle" class="none fs-title text-center">Поздравляем!</h2>
										<h2 id="errorTitle" class="none fs-title text-center">Ошибка!</h2>
										<br><br>
										<div class="row justify-content-center">
											<div class="col-3">
												<div id="spinner" class="spinner-border text-secondary" role="status">
													<span class="visually-hidden"></span>
												</div>
												<img id="successImage" class="none" src="${pageContext.request.contextPath}/resources/img/mainPage/master/ok--v2.png" class="fit-image">
												<img id="errorImage" class="none" src="${pageContext.request.contextPath}/resources/img/mainPage/master/error.png" class="fit-image">
											</div>
										</div>
										<br><br>
										<div id="finishInfo" class="none row justify-content-center">
											<div class="col-7 text-center">
												<h5>Вы успешно прошли регистрацию!</h5>
												<strong>
													<p>Через несколько секунд вы попадете на страницу входа</p>
												</strong>
											</div>
										</div>
										<div id="errorInfo" class="none row justify-content-center">
											<div class="col-7 text-center">
												<h5>Регистрация не пройдена!</h5>
												<strong>
													<p>Попробуйте пройти регистрацию снова</p>
												</strong>
											</div>
										</div>
									</div>
								</fieldset>
							</form:form>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>

	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/registrationReg.js" type="module"></script>
</body>
</html>