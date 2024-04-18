<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<style type="text/css">
		.navbar {
			background-color: #0e377b !important;
		}
	</style>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Insert title here</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
    <!-- MAIN CSS STYLE SHEET -->
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<div class="container" style="margin-top: 80px;">
		<div class="row">
			<h1>Редaктирование автомобиля</h1>
		</div>
		<form:form id="form" modelAttribute="truck" enctype="multipart/form-data" method="POST" action="./save?${_csrf.parameterName}=${_csrf.token}">
			<input type="hidden" value="${truck.idTruck}" name="id" />
			<div class="form-group">
				<label>Введите госномер авто:</label>
				<form:input path="numTruck" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите марку авто:</label>
				<form:input path="brandTruck" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите модель авто:</label>
				<form:input path="modelTruck" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите госномер прицепа (если имеется):</label>
				<form:input path="numTrailer" class="form-control" />
			</div>
			<div class="form-group">
				<label>Введите марку прицепа:</label>
				<form:input path="brandTrailer" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Выберите тип прицепа:</label>
				<select name="typeTrailer" class="form-group" required>
					<option></option>
					<option>Открытый</option>
					<option>Тент</option>
					<option>Изотермический</option>
					<option>Мебельный фургон</option>
					<option>Рефрижератор</option>
				</select>
			</div>
			<div class="form-group">
				<label>Выберите грузоподъёмность:</label>
				<select name="cargoCapacity" class="form-group" required>
					<option></option>
					<option value="1000">1 тонна</option>
					<option value="3000">3 тонны</option>
					<option value="5000">5 тонн</option>
					<option value="7000">7 тонн</option>
					<option value="10000">10 тонн</option>
					<option value="12000">12 тонн</option>
					<option value="15000">15 тонн</option>
					<option value="17000">17 тонн</option>
					<option value="20000">20 тонн</option>
				</select>
			</div>
			<div class="form-group">
				<label>Введите паллетовместимость (е-палл):</label>
				<form:input path="pallCapacity" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<label>Введите принадлежность транспорта (реквизиты владельца транспорта):</label>
				<form:input path="ownerTruck" class="form-control" required="true" />
			</div>
			<div class="form-group">
				<sec:csrfInput />
				<label for="make">Прикрепить фото техпаспорта авто</label>
				<p><input type="file" name="file">
			</div>
			<div class="form-group">
				<c:out value="${errorMessage}" />
			</div>
			<br>
			<input type="submit" value="Сохранить" class="save" />
		</form:form>
	</div>
	<script>
		const token = $("meta[name='_csrf']").attr("content");
		const form = $('#form')
		const formData = new FormData(form)

		form.on('submit', function(e) {
			e.preventDefault()

			$.ajax({
				type: "POST",
				url: "./save",
				headers: { "X-CSRF-TOKEN": token },
				data: formData,
				enctype: 'multipart/form-data',
				success: function(html) {
					console.log('success')
				},
				error: function(err){
					console.log(err)
				}
			})
		})
	</script>
	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
</body>
</html>