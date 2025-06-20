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
	<title>Текущие маршруты</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/transportation.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
</head>
<body class="no-scroll">
	<jsp:include page="headerNEW.jsp" />
	<div id="overlay" class="">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>
	<div class="my-container container-fluid p-0">
		<div class="title-container">
			<strong><h3>Текущие маршруты</h3></strong>
		</div>
		<div class="toolbar">
			<button type="button" class="btn tools-btn" data-toggle="modal" data-target="#truckModal">
				+ Добавить авто
			</button>
			<button type="button" class="btn tools-btn" data-toggle="modal" data-target="#driverModal">
				+ Добавить водителя
			</button>
		</div>
		<div id="myGrid" class="ag-theme-balham"></div>
		<div id="snackbar"></div>
	</div>

	<!-- контейнер для отображения полученных сообщений -->
	<div id="toasts" class="position-fixed bottom-0 right-0 p-3" style="z-index: 100; right: 0; bottom: 0;"></div>

	<!-- Модальное окно для добавления машины -->
	<div class="modal fade" id="truckModal" tabindex="-1" aria-labelledby="truckModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl">
			<div class="modal-content">
				<div class="modal-header bg-color">
					<h1 class="modal-title fs-5 mt-0" id="truckModalLabel">Создать новую машину</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="createTruckForm" name="createTruckForm" action="" class="needs-validation" novalidate>
					<div class="form-container">
						<div class="modal-body">
							<h3>Машина</h3>
							<div class="form-group">
								<input type="text" class="form-control" name="brandTruck" id="brandTruck" placeholder="Марка автомобиля" required>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" name="modelTruck" id="modelTruck" placeholder="Модель автомобиля" required>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" name="numTruck" id="numTruck" placeholder="Государственный номер" required>
								<div class="error-message" id="messageNumTruck"></div>
							</div>
							<div class="form-group">
								<input type="text" class="form-control" name="ownerTruck" id="ownerTruck" placeholder="Принадлежность ТС" required>
								<div class="remark text-muted">Пример: ООО "КаргоГрузПроектТранс"</div>
							</div>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numTrailer" id="numTrailer" placeholder="Номер прицепа">
								<input type="text" class="form-control" name="brandTrailer" id="brandTrailer" placeholder="Марка прицепа">
							</div>
							<div class="form-group">
								<select id="number_axes" name="number_axes" class="form-control" required>
									<option value="" disabled selected>Выберите количество осей</option>
									<option>2 оси</option>
									<option>3 оси</option>
									<option>4 и более осей</option>
								</select>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold" for="technical_certificate">Технический паспорт</label>
								<div class="form-group row-container">
									<input type="text" class="form-control" name="technical_certificate_1" id="technical_certificate_1" placeholder="Серия" required>
									<input type="number" class="form-control" name="technical_certificate_2" id="technical_certificate_2" placeholder="Номер" required>
								</div>
								<div class="form-group row-container">
									<input type="text" class="form-control" name="technical_certificate_3" id="technical_certificate_3" placeholder="Кем выдан" required>
									<span class="date-label">от</span>
									<input type="date" class="form-control" name="technical_certificate_4" id="technical_certificate_4" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Фото технического паспорта</label>
								<input type="file" class="form-control btn btn-outline-secondary" name="technical_certificate_file" id="technical_certificate_file" accept=".png, .jpg, .jpeg" required>
							</div>
							<div id="truckImageContainer"></div>
						</div>
						<div class="separationLine"></div>
						<div class="modal-body">
							<h3>Кузов</h3>
							<div class="form-group">
								<select id="typeTrailer" name="typeTrailer" class="form-control" required>
									<option value="" disabled selected>Выберите тип кузова</option>
									<option>Открытый</option>
									<option>Тент</option>
									<option>Изотермический</option>
									<option>Мебельный фургон</option>
									<option>Рефрижератор</option>
									<option>Бус</option>
									<!-- <option>Манипулятор</option> -->
								</select>
							</div>
							<div class="form-group">
								<select id="hitch_type" name="hitch_type" class="form-control" required>
									<option value="" disabled selected>Выберите тип сцепки</option>
									<option>Грузовик</option>
									<option>Полуприцеп</option>
									<option>Сцепка</option>
								</select>
							</div>
							<div class="form-group">
								<select id="type_of_load" name="type_of_load" class="form-control" required>
									<option value="" disabled selected>Выберите тип загрузки</option>
									<option>Задняя</option>
									<option>Боковая</option>
									<option>Задняя+боковая</option>
									<option>Полная растентовка</option>
								</select>
							</div>
							<div class="form-group position-relative">
								<input type="number" class="form-control" id="cargoCapacity" name="cargoCapacity" placeholder="Грузоподъёмность, т" min="1" max="22" step="0.01" required>
								<div class="invalid-feedback">
									Максимальное значение: 22
								</div>
							</div>
							<div class="form-group position-relative">
								<input type="number" class="form-control" id="volume_trailer" name="volume_trailer" placeholder="Объем, м. куб" min="1" max="120" step="1" required>
								<div class="invalid-feedback">
									Максимальное значение: 120, только целые числа
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold" for="dimensions_1">Внутренние габариты кузова (Д/Ш/В), м</label>
								<div class="form-group row-container dimensions-container">
									<input type="number" class="form-control" id="dimensions_1" name="dimensions_1" placeholder="Длина, м" min="0" max="20" step="0.01" required>
									<input type="number" class="form-control" id="dimensions_2" name="dimensions_2" placeholder="Ширина, м" min="0" max="20" step="0.01" required>
									<input type="number" class="form-control" id="dimensions_3" name="dimensions_3" placeholder="Высота, м" min="0" max="20" step="0.01" required>
								</div>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold" for="pallCapacity">
									Паллетовместимость кузова
								</label>
								<input type="number" class="form-control" id="pallCapacity" name="pallCapacity" placeholder="Количество европаллет 1200*800" min="0" step="1" required>
							</div>
							<div class="form-group">
								<label class="col-form-label text-muted font-weight-bold">Доп. информация</label>
								<div class="form-group info-container">
									<div class="form-check">
										<label class="form-check-label" for="check_1">Гидроборт</label>
										<input class="form-check-input" type="checkbox" value="Гидроборт" name="check_1" id="check_1">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_2">GPS-навигация</label>
										<input class="form-check-input" type="checkbox" value="GPS-навигация" name="check_2" id="check_2">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_3">Ремни</label>
										<input class="form-check-input" type="checkbox" value="Ремни" name="check_3" id="check_3">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_4">Стойки</label>
										<input class="form-check-input" type="checkbox" value="Стойки" name="check_4" id="check_4">
									</div>
									<div class="form-check">
										<label class="form-check-label" for="check_5">Пневмоподушки</label>
										<input class="form-check-input" type="checkbox" value="Пневмоподушки" name="check_5" id="check_5">
									</div>
								</div>
							</div>
						</div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить машину</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Модальное окно для добавления водителя -->
	<div class="modal fade" id="driverModal" tabindex="-1" aria-labelledby="driverModalLabel" aria-hidden="true">
		<div class="modal-dialog ">
			<div class="modal-content">
				<div class="modal-header bg-color">
					<h1 class="modal-title fs-5" id="driverModalLabel">Добавить водителя</h1>
					<button type="button" class="close" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="createDriverForm" action="" class="needs-validation" novalidate>
					<div class="modal-body">
						<div class="form-group">
							<input type="text" class="form-control" name="surname" id="surname" placeholder="Фамилия" required>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="name" id="name" placeholder="Имя" required>
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="patronymic" id="patronymic" placeholder="Отчество">
						</div>
						<div class="form-group">
							<input type="text" class="form-control" name="tel" id="tel" placeholder="Телефон" required>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold" for="numpass">Паспортные данные</label>
							<div class="form-group row-container numpass-container">
								<input type="text" class="form-control" name="numpass_1" id="numpass_1" placeholder="Серия" required>
								<input type="number" class="form-control" name="numpass_2" id="numpass_2" placeholder="Номер" required>
							</div>
							<div class="form-group row-container numpass-container">
								<input type="text" class="form-control" name="numpass_3" id="numpass_3" placeholder="Кем выдан" required>
								<span class="date-label">от</span>
								<input type="date" class="form-control" name="numpass_4" id="numpass_4" required>
							</div>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold" for="numdrivercard">Водительское удостоверение</label>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numdrivercard_1" id="numdrivercard_1" placeholder="Серия" required>
								<input type="number" class="form-control" name="numdrivercard_2" id="numdrivercard_2" placeholder="Номер" required>
							</div>
							<div class="form-group row-container">
								<input type="text" class="form-control" name="numdrivercard_3" id="numdrivercard_3" placeholder="Кем выдан" required>
								<span class="date-label">от</span>
								<input type="date" class="form-control" name="numdrivercard_4" id="numdrivercard_4" required>
							</div>
						</div>
						<div class="form-group">
							<label class="col-form-label text-muted font-weight-bold">Фото водительского удостоверения</label>
							<input type="file" class="form-control btn btn-outline-secondary" name="drivercard_file" id="drivercard_file" accept=".png, .jpg, .jpeg" required>
						</div>
						<div id="driverImageContainer"></div>
					</div>
					<div class="modal-footer">
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Отменить</button>
						<button type="submit" class="btn btn-primary">Добавить водителя</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/js/transportation.js' type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/tenderNotifications.js" type="module"></script>
</body>
</html>