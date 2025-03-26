<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html lang="ru">
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}">
	<meta name="viewport" content="width=device-width, initial-scale=1.0">
	<title>Отдел качества</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-custom-caption.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-thumbnails.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/photoSwipe/photoswipe-dynamic-caption-plugin.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/variables.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/acceptanceQuality.css">
</head>
<body>

	<jsp:include page="headerNEW.jsp" />

	<div id="overlay" class="none">
		<div class="spinner-border text-primary" role="status">
			<span class="sr-only">Загрузка...</span>
		</div>
	</div>

	<div class="container-fluid my-container px-0 position-relative">
		<div class="title-container">
			<strong><h3>Подтверждение качества товаров</h3></strong>
		</div>
		<div class="search-form-container">
			<form class="" action="" id="orderSearchForm">
				<span class="font-weight-bold text-muted mb-0">Отобразить данные</span>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">с</label>
					<input class="form-control" type="date" name="date_from" id="date_from" required>
				</div>
				<div class="input-row-container">
					<label class="text-muted font-weight-bold">по</label>
					<input class="form-control" type="date" name="date_to" id="date_to" required>
				</div>
				<button class="btn btn-outline-secondary font-weight-bold" type="submit">Отобразить</button>
			</form>
		</div>

		<div id="myGrid" class="ag-theme-balham"></div>

		<div id="snackbar"></div>
	</div>

	<!-- Модальное окно для отображения текста -->
	<div class="modal fade" id="displayMessageModal" data-backdrop="static" data-keyboard="false" tabindex="-1" aria-labelledby="displayMessageModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-lg">
			<div class="modal-content">
				<div class="modal-header justify-content-center">
					<h5 class="modal-title" id="displayMessageModalLabel">Сообщение</h5>
				</div>
				<div class="modal-body">
					<div id="messageContainer"></div>
				</div>
				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно -->
	<div class="modal fade" id="qualityCardModal" tabindex="-1" role="dialog" aria-labelledby="qualityCardModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-primary text-white">
					<h5 class="modal-title" id="qualityCardModalLabel">Карта качества продукции</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<div class="modal-body">
					<div class="card mb-3">
						<div class="card-header bg-light">Основная информация</div>
						<div class="card-body">
							<p><strong>Наименование продукта:</strong> <span id="productName"></span></p>
							<div class="row">
								<div class="col-md-4">
									<p><strong>Дата:</strong> <span id="dateCard"></span></p>
									<p><strong>Поставщик:</strong> <span id="firmNameAccept"></span></p>
								</div>
								<div class="col-md-4">
									<p><strong>Номер документа:</strong> <span id="ttn"></span></p>
									<p><strong>Номер машины:</strong> <span id="carNumber"></span></p>
								</div>
								<div class="col-md-4">
									<p><strong>Масса поставки:</strong> <span id="cargoWeightCard"></span> кг</p>
									<p><strong>Выборка:</strong> <span id="sampleSize"></span> кг</p>
								</div>
							</div>
							<div class="row">
								<div class="col-md-12 d-flex justify-content-center" id="showImagesBtnContainer"></div>
							</div>
						</div>
					</div>

					<div class="card mb-3">
						<div class="card-header bg-light">Внутренние дефекты</div>
						<div class="card-body">
							<div class="defect-list mb-2">
								<table class="table table-sm mb-0">
									<thead>
										<tr>
											<th class="text-muted font-weight-bold">Вес</th>
											<th class="text-muted font-weight-bold">Процент</th>
											<th class="text-muted font-weight-bold">Описание</th>
										</tr>
									</thead>
									<tbody id="internalDefectsList"></tbody>
								</table>
							</div>
							<div class="row">
								<div class="col-md-6">
									<p class="font-weight-bold">Общий вес внутренних дефектов: <span id="totalInternalDefectWeight"></span> кг</p>
								</div>
								<div class="col-md-6">
									<p class="font-weight-bold">Общий процент внутренних дефектов: <span id="totalInternalDefectPercentage"></span>%</p>
								</div>
							</div>
						</div>
					</div>

					<div class="card mb-3">
						<div class="card-header bg-light">Брак</div>
						<div class="card-body">
							<div class="defect-list mb-2">
								<table class="table table-sm mb-0">
									<thead>
										<tr>
											<th class="text-muted font-weight-bold">Вес</th>
											<th class="text-muted font-weight-bold">Процент</th>
											<th class="text-muted font-weight-bold">Процент с ПК</th>
											<th class="text-muted font-weight-bold">Описание</th>
										</tr>
									</thead>
									<tbody id="totalDefectsList"></tbody>
								</table>
							</div>
							<div class="row">
								<div class="col-md-4">
									<p class="font-weight-bold">Общий вес дефектов: <span id="totalDefectWeight"></span> кг</p>
								</div>
								<div class="col-md-4">
									<p class="font-weight-bold">Общий процент дефектов: <span id="totalDefectPercentage"></span>%</p>
								</div>
								<div class="col-md-4">
									<p class="font-weight-bold">Общий процент дефектов с ПК: <span id="totalDefectPercentageWithPC"></span>%</p>
								</div>
							</div>
						</div>
					</div>

					<div class="card mb-3">
						<div class="card-header bg-light">Легкая некондиция</div>
						<div class="card-body">
							<div class="defect-list mb-2">
								<table class="table table-sm mb-0">
									<thead>
										<tr>
											<th class="text-muted font-weight-bold">Вес</th>
											<th class="text-muted font-weight-bold">Процент</th>
											<th class="text-muted font-weight-bold">Описание</th>
										</tr>
									</thead>
									<tbody id="lightDefectsList"></tbody>
								</table>
							</div>
							<div class="row">
								<div class="col-md-6">
									<p class="font-weight-bold">Общий вес легкой некондиции: <span id="totalLightDefectWeight"></span> кг</p>
								</div>
								<div class="col-md-6">
									<p class="font-weight-bold">Общий процент легкой некондиции: <span id="totalLightDefectPercentage"></span>%</p>
								</div>
							</div>
						</div>
					</div>

					<div class="card mb-3">
						<div class="card-header bg-light">Дополнительная информация</div>
						<div class="card-body">
							<div class="row">
								<div class="col-md-4">
									<p><strong>Класс:</strong> <span id="classType"></span></p>
									<p><strong>Количество брендов:</strong> <span id="numberOfBrands"></span></p>
									<p><strong>Качество упаковки:</strong> <span id="qualityOfProductPackaging"></span></p>
									<p><strong>Термограмма:</strong> <span id="thermogram"></span></p>
									<p><strong>Температура в кузове:</strong> <span id="bodyTemp"></span>°C</p>
									<p><strong>Температура внутри плода:</strong> <span id="fruitTemp"></span>°C</p>
								</div>
								<div class="col-md-8">
									<p><strong>Оценка внешнего вида:</strong> <span id="appearanceEvaluation"></span></p>
									<p><strong>Дефекты внешнего вида:</strong> <span id="appearanceDefects"></span></p>
									<p><strong>Степень зрелости:</strong> <span id="maturityLevel"></span></p>
									<p><strong>Вкусовые качества:</strong> <span id="tasteQuality"></span></p>
									<p><strong>Калибр:</strong> <span id="caliber"></span></p>
									<p><strong>Стикер: описание несоответствия:</strong> <span id="stickerDescription"></span></p>
								</div>
							</div>
							<div class="row mt-2">
								<div class="col-md-12">
									<p><strong>Примечание:</strong> <span id="cardInfo"></span></p>
								</div>
							</div>
						</div>
					</div>

				<div class="modal-footer">
					<button type="button" id="successProduct" class="btn btn-success">Подтвердить приемку товара</button>
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>


	<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/acceptanceQuality.js"></script>
</body>
</html>
