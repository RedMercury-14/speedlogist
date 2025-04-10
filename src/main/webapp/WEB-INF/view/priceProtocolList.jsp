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
	<title>Протокол согласования цены</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<script async src="${pageContext.request.contextPath}/resources/js/getInitData.js" type="module"></script>
	<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/bootstrap5overlay.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/priceProtocolList.css">
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
			<strong><h3>Протокол согласования цены</h3></strong>
		</div>
		<div class="toolbar">
			<button type="button" class="btn tools-btn font-weight-bold text-muted" data-toggle="modal" data-target="#createPriceProtocolModal">
				+
			</button>
			<!-- <div class="search-form-container">
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
			</div> -->
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

	<!-- Модальное окно  -->
	<!-- <div class="modal fade" id="createPriceProtocolModal" tabindex="-1" role="dialog" aria-labelledby="createPriceProtocolModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="createPriceProtocolModalLabel">Добавить данные</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form class="" id="createPriceProtocolForm">
					<div class="modal-body">
					
						<h6 class="text-primary mb-2">Основная информация о товаре</h6>
						<div class="form-row border-bottom mb-4">
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="barcode">Штрих-код товара</label>
								<input type="text" class="form-control" name="barcode" id="barcode" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="productCode">Код товара</label>
								<input type="text" class="form-control" name="productCode" id="productCode" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="tnvCode">Код ТНВЭД</label>
								<input type="text" class="form-control" name="tnvCode" id="tnvCode" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="name">Наименование товара</label>
								<input type="text" class="form-control" name="name" id="name" required>
							</div>
						</div>
					
						<h6 class="text-primary mb-2">Цены и расчёты</h6>
						<div class="table-responsive mb-4">
							<table class="table table-bordered small mb-0">
								<thead class="thead-light text-center">
									<tr>
										<th class="p-2">Прейскурантная цена производителя без НДС (BYN)</th>
										<th class="p-2">Себестоимость импортера без надбавки и без НДС (гр.11 ТТН)</th>
										<th class="p-2">Надбавка импортера, % (гр.11 ТТН)</th>
										<th class="p-2">Скидка с отпускной цены, %</th>
										<th class="p-2">Оптовая скидка, %</th>
										<th class="p-2">Отпускная цена без НДС (гр. 4 ТТН), BYN</th>
										<th class="p-2">Оптовая надбавка, %</th>
										<th class="p-2">Ставка НДС</th>
										<th class="p-2">Цена с НДС, BYN</th>
									</tr>
								</thead>
								<tbody>
									<tr>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="priceProducer" id="priceProducer" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="costImporter" id="costImporter" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="markupImporterPercent" id="markupImporterPercent" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="discountPercent" id="discountPercent" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="wholesaleDiscountPercent" id="wholesaleDiscountPercent" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="priceWithoutVat" id="priceWithoutVat" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="wholesaleMarkupPercent" id="wholesaleMarkupPercent" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="vatRate" id="vatRate" placeholder="0.00" required></td>
										<td class="p-1"><input type="number" step="0.01" class="form-control form-control-sm" name="priceWithVat" id="priceWithVat" placeholder="0.00" required></td>
									</tr>
								</tbody>
							</table>
						</div>

						<h6 class="text-primary mb-2">Производство и упаковка</h6>
						<div class="form-row border-bottom mb-4">
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="countryOrigin">Страна происхождения</label>
								<input type="text" class="form-control" name="countryOrigin" id="countryOrigin" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="manufacturer">Производитель</label>
								<input type="text" class="form-control" name="manufacturer" id="manufacturer" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="unitPerPack">Штук/кг в упаковке</label>
								<input type="text" class="form-control" name="unitPerPack" id="unitPerPack" required>
							</div>
						</div>
					
						<h6 class="text-primary mb-2">Срок годности и цена</h6>
						<div class="form-row border-bottom mb-4">
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="shelfLifeDays">Срок годности в днях</label>
								<input type="number" step="1" class="form-control" name="shelfLifeDays" id="shelfLifeDays" placeholder="Целое число" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="currentPrice">Текущая отпускная цена, BYN</label>
								<input type="number" step="0.01" class="form-control" name="currentPrice" id="currentPrice" placeholder="0.00" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="priceChangePercent">% изменения отпускной цены</label>
								<input type="number" step="0.01" class="form-control" name="priceChangePercent" id="priceChangePercent" placeholder="0.00" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="lastPriceChangeDate">Дата последнего изменения цены</label>
								<input type="date" class="form-control" name="lastPriceChangeDate" id="lastPriceChangeDate" required>
							</div>
						</div>
					
						<h6 class="text-primary mb-2">Срок действия и договор</h6>
						<div class="form-row mb-4">
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="dateValidFrom">Дата начала действия</label>
								<input type="date" class="form-control" name="dateValidFrom" id="dateValidFrom" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="dateValidTo">Дата окончания действия</label>
								<input type="date" class="form-control" name="dateValidTo" id="dateValidTo" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="contractNumber">Номер договора</label>
								<input type="text" class="form-control" name="contractNumber" id="contractNumber" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="contractDate">Дата договора</label>
								<input type="date" class="form-control" name="contractDate" id="contractDate" required>
							</div>
						</div>
					</div>

					<div class="modal-footer">
						<button type="submit" class="btn bg-color">Сохранить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div> -->


	<!-- Модальное окно  -->
	<div class="modal fade" id="createPriceProtocolModal" tabindex="-1" role="dialog" aria-labelledby="createPriceProtocolModalLabel" aria-hidden="true">
		<div class="modal-dialog modal-xl" role="document">
			<div class="modal-content p-0">
				<div class="modal-header bg-color text-white">
					<h5 class="modal-title" id="createPriceProtocolModalLabel">Добавить данные</h5>
					<button type="button" class="close text-white" data-dismiss="modal" aria-label="Close">
						<span aria-hidden="true">&times;</span>
					</button>
				</div>
				<form id="createPriceProtocolForm" class="mb-4">
					<div class="modal-body">

						<!-- Общие поля, которые заполняются один раз -->
						<h5 class="text-primary">Срок действия и договор</h5>
						<div class="form-row border-bottom mb-4">
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="dateValidFrom">Дата начала действия</label>
								<input type="date" class="form-control" name="dateValidFrom" id="dateValidFrom" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="dateValidTo">Дата окончания действия</label>
								<input type="date" class="form-control" name="dateValidTo" id="dateValidTo" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="contractNumber">Номер договора</label>
								<input type="text" class="form-control" name="contractNumber" id="contractNumber" placeholder="Номер договора" required>
							</div>
							<div class="form-group col-md-3 align-content-end">
								<label class="mb-1" for="contractDate">Дата договора</label>
								<input type="date" class="form-control" name="contractDate" id="contractDate" required>
							</div>
						</div>
					
						<!-- Сюда будут добавляться карточки товаров -->
						<div id="productItemsContainer"></div>
					
						<div class="text-right mb-3">
							<button type="button" class="btn btn-outline-primary btn-sm" id="addFormItem">+ Добавить</button>
						</div>
					</div>
				
				
					<div class="modal-footer">
						<button type="submit" class="btn bg-color">Сохранить</button>
						<button type="button" class="btn btn-secondary" data-dismiss="modal">Закрыть</button>
					</div>
				</form>
			</div>
		</div>
	</div>

	<!-- Шаблон карточки товара -->
	<template id="productItemTemplate">
		<div class="product-item border rounded p-3 mb-4 position-relative bg-light">
			<button type="button" style="right: 16px;" class="btn-close btn btn-outline-danger position-absolute" aria-label="Удалить">
				<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" fill="currentColor" class="bi bi-trash" viewBox="0 0 16 16">
					<path d="M5.5 5.5A.5.5 0 0 1 6 6v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm2.5 0a.5.5 0 0 1 .5.5v6a.5.5 0 0 1-1 0V6a.5.5 0 0 1 .5-.5zm3 .5a.5.5 0 0 0-1 0v6a.5.5 0 0 0 1 0V6z"/>
					<path fill-rule="evenodd" d="M14.5 3a1 1 0 0 1-1 1H13v9a2 2 0 0 1-2 2H5a2 2 0 0 1-2-2V4h-.5a1 1 0 0 1-1-1V2a1 1 0 0 1 1-1H6a1 1 0 0 1 1-1h2a1 1 0 0 1 1 1h3.5a1 1 0 0 1 1 1v1zM4.118 4 4 4.059V13a1 1 0 0 0 1 1h6a1 1 0 0 0 1-1V4.059L11.882 4H4.118zM2.5 3V2h11v1h-11z"/>
				</svg>
			</button>

			<h6 class="text-primary">Товар</h6>
			<div class="form-row">
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Штрих-код</label>
					<input type="text" class="form-control" data-name="barcode" placeholder="Штрих-код" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Код товара</label>
					<input type="text" class="form-control" data-name="productCode" placeholder="Код товара" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Код ТНВЭД</label>
					<input type="text" class="form-control" data-name="tnvCode" placeholder="Код ТНВЭД" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Наименование</label>
					<input type="text" class="form-control" data-name="name" placeholder="Наименование" required>
				</div>
			</div>

			<h6 class="text-primary mt-3">Цены и расчёты</h6>
			<div class="table-responsive mb-3">
				<table class="table table-bordered table-sm">
					<thead class="thead-light text-center small">
						<tr>
							<th class="p-2">Прейскурантная цена производителя без НДС (BYN)</th>
							<th class="p-2">Себестоимость импортера без надбавки и без НДС (гр.11 ТТН)</th>
							<th class="p-2">Надбавка импортера, % (гр.11 ТТН)</th>
							<th class="p-2">Скидка с отпускной цены, %</th>
							<th class="p-2">Оптовая скидка, %</th>
							<th class="p-2">Отпускная цена без НДС (гр. 4 ТТН), BYN</th>
							<th class="p-2">Оптовая надбавка, %</th>
							<th class="p-2">Ставка НДС</th>
							<th class="p-2">Цена с НДС, BYN</th>
						</tr>
					</thead>
					<tbody>
						<tr>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="priceProducer" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="costImporter" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="markupImporterPercent" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="discountPercent" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="wholesaleDiscountPercent" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="priceWithoutVat" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="wholesaleMarkupPercent" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="vatRate" placeholder="0.00" required></td>
							<td><input type="number" step="0.01" class="form-control form-control-sm" data-name="priceWithVat" placeholder="0.00" required></td>
						</tr>
					</tbody>
				</table>
			</div>

			<h6 class="text-primary">Производство и срок</h6>
			<div class="form-row">
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Страна происхождения</label>
					<input type="text" class="form-control" data-name="countryOrigin" placeholder="Страна происхождения" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Производитель</label>
					<input type="text" class="form-control" data-name="manufacturer" placeholder="Производитель" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Шт/кг в упаковке</label>
					<input type="text" class="form-control" data-name="unitPerPack" placeholder="Шт/кг в упаковке" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Срок годности (дни)</label>
					<input type="number" step="1" class="form-control" data-name="shelfLifeDays" placeholder="Целое число" required>
				</div>
			</div>

			<div class="form-row">
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Текущая цена, BYN</label>
					<input type="number" step="0.01" class="form-control" data-name="currentPrice" placeholder="0.00" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">% изменения цены</label>
					<input type="number" step="0.01" class="form-control" data-name="priceChangePercent" placeholder="0.00" required>
				</div>
				<div class="form-group col-md-3 align-content-end">
					<label class="mb-1">Дата последнего изменения</label>
					<input type="date" class="form-control" data-name="lastPriceChangeDate" required>
				</div>
			</div>
		</div>
	</template>

	<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
	<script type="module" src="${pageContext.request.contextPath}/resources/js/priceProtocolList.js"></script>\
	<!-- <script>
		let productIndex = 0;

		function addProductItem() {
			const container = document.getElementById('productItemsContainer');
			const template = document.getElementById('productItemTemplate');
			const clone = template.content.cloneNode(true);

			clone.querySelectorAll('[data-name]').forEach((el) => {
				const field = el.getAttribute('data-name');
				el.setAttribute('name', `${field}_${productIndex}`);
			});

			container.appendChild(clone);
			productIndex++;
		}

		function removeProductItem(button) {
			button.closest('.product-item').remove();
		}
	</script> -->
</body>
</html>
