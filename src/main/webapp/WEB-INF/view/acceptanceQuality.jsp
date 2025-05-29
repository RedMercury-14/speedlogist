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
	<script src="${pageContext.request.contextPath}/resources/js/jszip/jszip.min.js"></script>
	<script src="${pageContext.request.contextPath}/resources/js/FileSaver/FileSaver.min.js"></script>
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

	<!-- Модальное окно отображения карточки качества товара-->
	<div class="modal fade" id="qualityCardInfoModal" tabindex="-1" role="dialog" aria-labelledby="qualityCardInfoModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="qualityCardInfoModalLabel">Карта качества продукции</h5>
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
								<div class="col-lg-4">
									<p><strong>Дата:</strong> <span id="dateCard"></span></p>
									<p><strong>Поставщик:</strong> <span id="firmNameAccept"></span></p>
								</div>
								<div class="col-lg-4">
									<p><strong>Номер документа:</strong> <span id="ttn"></span></p>
									<p><strong>Номер машины:</strong> <span id="carNumber"></span></p>
								</div>
								<div class="col-lg-4">
									<p><strong>Масса поставки:</strong> <span id="cargoWeightCard"></span> кг</p>
									<p><strong>Выборка:</strong> <span id="sampleSize"></span> <span class="sampleSizeUnit"></span></p>
								</div>
							</div>
							<div class="row">
								<div class="col-lg-12 d-flex justify-content-center" id="showImagesBtnContainer"></div>
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
											<th class="text-muted font-weight-bold text-nowrap">Кол-во</th>
											<th class="text-muted font-weight-bold text-nowrap">Выборка для ВД</th>
											<th class="text-muted font-weight-bold">Процент</th>
											<th class="text-muted font-weight-bold">Описание</th>
										</tr>
									</thead>
									<tbody id="internalDefectsList"></tbody>
								</table>
							</div>
							<div class="row">
								<div class="col-lg-6">
									<p class="font-weight-bold">Всего: <span id="totalInternalDefectWeight"></span> шт</p>
								</div>
								<div class="col-lg-6">
									<p class="font-weight-bold">Общий процент: <span id="totalInternalDefectPercentage"></span>%</p>
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
											<th class="text-muted font-weight-bold text-nowrap">Кол-во</th>
											<th class="text-muted font-weight-bold">Процент</th>
											<th class="text-muted font-weight-bold">Процент с ПК</th>
											<th class="text-muted font-weight-bold">Описание</th>
										</tr>
									</thead>
									<tbody id="totalDefectsList"></tbody>
								</table>
							</div>
							<div class="row">
								<div class="col-lg-2">
									<p class="font-weight-bold">Всего: <span id="totalDefectWeight"></span> <span class="sampleSizeUnit"></span></p>
								</div>
								<div class="col-lg-5">
									<p class="font-weight-bold">Общий процент: <span id="totalDefectPercentage"></span>%</p>
								</div>
								<div class="col-lg-5">
									<p class="font-weight-bold">Общий процент с ПК: <span id="totalDefectPercentageWithPC"></span>%</p>
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
											<th class="text-muted font-weight-bold text-nowrap">Кол-во</th>
											<th class="text-muted font-weight-bold">Процент</th>
											<th class="text-muted font-weight-bold">Описание</th>
										</tr>
									</thead>
									<tbody id="lightDefectsList"></tbody>
								</table>
							</div>
							<div class="row">
								<div class="col-lg-6">
									<p class="font-weight-bold">Всего: <span id="totalLightDefectWeight"></span> <span class="sampleSizeUnit"></span></p>
								</div>
								<div class="col-lg-6">
									<p class="font-weight-bold">Общий процент: <span id="totalLightDefectPercentage"></span>%</p>
								</div>
							</div>
						</div>
					</div>

					<div class="card mb-3">
						<div class="card-header bg-light">Дополнительная информация</div>
						<div class="card-body">
							<div class="row">
								<div class="col-lg-4">
									<p><strong>Класс:</strong> <span id="classType"></span></p>
									<p><strong>Количество брендов:</strong> <span id="numberOfBrands"></span></p>
									<p><strong>Качество упаковки:</strong> <span id="qualityOfProductPackaging"></span></p>
									<p><strong>Термограмма:</strong> <span id="thermogram"></span></p>
									<!-- <p><strong>Температура в кузове:</strong> <span id="bodyTemp"></span>°C</p> -->
									<p><strong>Температура внутри плода:</strong> <span id="fruitTemp"></span>°C</p>
								</div>
								<div class="col-lg-8">
									<!-- <p><strong>Оценка внешнего вида:</strong> <span id="appearanceEvaluation"></span></p> -->
									<!-- <p><strong>Дефекты внешнего вида:</strong> <span id="appearanceDefects"></span></p> -->
									<p><strong>Степень зрелости:</strong> <span id="maturityLevel"></span></p>
									<p><strong>Вкусовые качества:</strong> <span id="tasteQuality"></span></p>
									<p><strong>Калибр:</strong> <span id="caliber"></span></p>
									<!-- <p><strong>Стикер: описание несоответствия:</strong> <span id="stickerDescription"></span></p> -->
								</div>
							</div>
							<div class="row mt-2">
								<div class="col-lg-12">
									<p><strong>Примечание:</strong> <span id="cardInfo"></span></p>
								</div>
							</div>
						</div>
					</div>

					<div class="card mb-3 border-primary">
						<div class="card-header bg-primary text-white">Подтверждение товара</div>

						<h3 id="cardStatusText" class="p-3 d-none mb-0"></h3>

						<form class="" id="approveCardForm2">
							<div class="card-body">
								<input type="hidden" name="idAcceptanceFoodQuality">
								<input type="hidden" name="idAcceptanceQualityFoodCard">
		
								<div class="row">
									<div class="col-lg-6">
										<div class="form-group">
											<label class="text-muted font-weight-bold mb-1" for="status2">Действие</label>
											<select name="status" id="status2" class="form-control" required>
												<option value="" selected hidden disabled>Выберите действие</option>
												<option value="150">Принимаем</option>
												<option value="152">Принимаем с переборкой</option>
												<option value="154">Принимаем с процентом брака от коммерции</option>
												<option value="156">Принимаем под реализацию</option>
												<option value="140">Не принимаем</option>
											</select>
										</div>
									</div>
		
									<div class="col-lg-4 managerPercentInput2 d-none">
										<div class="form-group">
											<label class="text-muted font-weight-bold mb-1" for="managerPercent_type2">Дефект</label>
											<select name="managerPercent_type" id="managerPercent_type2" class="form-control">
												<option value="" selected hidden disabled>Выберите дефект</option>
												<option value="ВД">Внутренний дефект</option>
												<option value="Брак">Брак</option>
												<option value="ЛН">Легкая некондиция</option>
											</select>
										</div>
									</div>
		
									<div class="col-lg-2 managerPercentInput2 d-none">
										<div class="form-group">
											<label class="text-muted font-weight-bold mb-1" for="managerPercent_value2">Процент</label>
											<div class="input-group">
												<input type="number" name="managerPercent_value" id="managerPercent_value2" min="0" max="100" class="form-control" aria-label="Процент брака">
												<div class="input-group-append">
													<span class="input-group-text">%</span>
												</div>
											</div>
										</div>
									</div>
								</div>
		
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="commen2">Комментарий</label>
									<textarea class="form-control" name="comment" id="comment2" rows="3"></textarea>
								</div>
			
							</div>
							<div class="card-footer d-flex justify-content-center">
								<button type="submit" class="btn btn-success">Подтвердить действие</button>
							</div>
						</form>
					</div>
				</div>

				<div class="modal-footer">
					<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
				</div>
			</div>
		</div>
	</div>

	<!-- Модальное окно указания статуса карточки товара -->
	<div class="modal fade" id="approveCardModal" tabindex="-1" role="dialog" aria-labelledby="approveCardModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="approveCardModalLabel">Подтверждение качества</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form class="" id="approveCardForm">
					<div class="modal-body">
						<input type="hidden" name="idAcceptanceFoodQuality">
						<input type="hidden" name="idAcceptanceQualityFoodCard">

						<div class="row">
							<div class="col-lg-6">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="status">Действие</label>
									<select name="status" id="status" class="form-control" required>
										<option value="" selected hidden disabled>Выберите действие</option>
										<option value="150">Принимаем</option>
										<option value="152">Принимаем с переборкой</option>
										<option value="154">Принимаем с процентом брака от коммерции</option>
										<option value="156">Принимаем под реализацию</option>
										<option value="158">Требуется дополнительная выборка (своими силами)</option>
										<option value="160">Принимаем с дополнительной выборкой (силами поставщика)</option>
										<option value="140">Не принимаем</option>
									</select>
								</div>
							</div>

							<div class="col-lg-4 managerPercentInput d-none">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="managerPercent_type">Дефект</label>
									<select name="managerPercent_type" id="managerPercent_type" class="form-control">
										<option value="" selected hidden disabled>Выберите дефект</option>
										<option value="ВД">Внутренний дефект</option>
										<option value="Брак">Брак</option>
										<option value="ЛН">Легкая некондиция</option>
									</select>
								</div>
							</div>

							<div class="col-lg-2 managerPercentInput d-none">
								<div class="form-group">
									<label class="text-muted font-weight-bold mb-1" for="managerPercent_value">Процент</label>
									<div class="input-group">
										<input type="number" name="managerPercent_value" id="managerPercent_value" min="0" max="100" step="0.1" class="form-control" aria-label="Процент брака">
										<div class="input-group-append">
											<span class="input-group-text">%</span>
										</div>
									</div>
								</div>
							</div>
						</div>

						<div class="form-group">
							<label class="text-muted font-weight-bold mb-1" for="comment">Комментарий</label>
							<textarea class="form-control" name="comment" id="comment" rows="3"></textarea>
						</div>
					</div>

					<div class="modal-footer">
						<button type="submit" class="btn btn-success">Подтвердить действие</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div>


	<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/acceptanceQuality.js"></script>
</body>
</html>
