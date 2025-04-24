<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Заявка</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/orderHistory.css">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
	<div class="container my-container">
		<div class="card">
			<div class="card-header d-flex justify-content-between">
				<h3 class="mb-0">
					<c:choose>
						<c:when test="${order.link != null}">
							Заявка № ${order.idOrder}
							<span class="text-danger"> (заказ объединен по связи ${order.link})</span>
						</c:when>
						<c:otherwise>
							Заявка № ${order.idOrder}
						</c:otherwise>
					</c:choose>
				</h3>
			</div>
			<div class="card-body">
				<div class="order-container">
					<div class="order-section left">

						<c:choose>
							<c:when test="${order.counterparty != null && order.counterparty != ''}">
								<div class="text-container">
									<span class="text-muted font-weight-bold">Наименование контрагента: </span>
									<span>${order.counterparty}</span>
								</div>
							</c:when>
						</c:choose>

						<c:choose>
							<c:when test="${order.contact != null && order.contact != ''}">
								<div class="text-container">
									<span class="text-muted font-weight-bold">Контактное лицо контрагента: </span>
									<span>${order.contact}</span>
								</div>
							</c:when>
						</c:choose>
						
						<!-- <div class="text-container">
							<span class="text-muted font-weight-bold">Получатель: </span>
							<span>order.recipient</span>
						</div> -->

						<div class="text-container">
							<span class="text-muted font-weight-bold">Сверка УКЗ: </span>
							<span>
								<c:choose>
									<c:when test="${order.control == true}">
										Да, сверять УКЗ
									</c:when>
									<c:otherwise>
										Нет, не сверять УКЗ
									</c:otherwise>
								</c:choose>
							</span>
						</div>

						<!-- <div class="text-container">
							<span class="text-muted font-weight-bold">Необходим TIR: </span>
							<span>
									Да, необходим TIR для оформления
									Нет
							</span>
						</div> -->

						<div class="text-container">
							<span class="text-muted font-weight-bold">Тип маршрута: </span>
								<c:choose>
									<c:when test="${order.isInternalMovement == 'true'}">
										<span>Внутреннее перемещение</span>
									</c:when>
									<c:otherwise>
										<span>${order.way}</span>
									</c:otherwise>
								</c:choose>
							</span>
						</div>

						<c:choose>
							<c:when test="${order.marketNumber != null && order.marketNumber != ''}">
								<div class="text-container">
									<span class="text-muted font-weight-bold">Номер заказа из Маркета: </span>
									<span>${order.marketNumber}</span>
								</div>
							</c:when>
						</c:choose>

						<c:choose>
							<c:when test="${order.loadNumber != null && order.loadNumber != ''}">
								<div class="text-container">
									<span class="text-muted font-weight-bold">Погрузочный номер: </span>
									<span>${order.loadNumber}</span>
								</div>
							</c:when>
						</c:choose>

						<c:choose>
							<c:when test="${order.marketInfo != null && order.marketInfo != ''}">
								<div class="text-container comment-container">
									<span class="text-muted font-weight-bold">Информация из Маркета:</span>
									<span>${order.marketInfo}</span>
								</div>
							</c:when>
						</c:choose>

						<!-- <div class="text-container comment-container">
							<span class="text-muted font-weight-bold">Информация о маршруте: </span>
							<span></span>
						</div> -->

						<c:choose>
							<c:when test="${order.comment != null && order.comment != ''}">
								<div class="text-container comment-container">
									<span class="text-muted font-weight-bold">
										<c:choose>
											<c:when test="${order.way == 'АХО'}">
												Дополнительная информация:
											</c:when>
											<c:otherwise>
												Комментарии:
											</c:otherwise>
										</c:choose>
									</span>
									<span>${order.comment}</span>
								</div>
							</c:when>
						</c:choose>

					</div>

					<div class="order-section right">

						<div class="text-container">
							<span class="text-muted font-weight-bold">Тип загрузки: </span>
							<span>${order.typeLoad}</span>
						</div>

						<div class="text-container">
							<span class="text-muted font-weight-bold">Способ загрузки: </span>
							<span>${order.methodLoad}</span>
						</div>

						<div class="text-container">
							<span class="text-muted font-weight-bold">Тип кузова: </span>
							<span>${order.typeTruck}</span>
						</div>

						<c:choose>
							<c:when test="${order.incoterms != null && order.incoterms != ''}">
								<div class="text-container">
									<span class="text-muted font-weight-bold">
										<a class="my-link" href="/speedlogist/api/procurement/downdoad/incoterms" download>
											Условия поставки:
										</a>
									</span>
									<span>${order.incoterms}</span>
								</div>

								<!-- <div class="text-container">
									<span class="text-muted font-weight-bold">Место поставки: </span>
									<span></span>
								</div> -->

							</c:when>
						</c:choose>

						<div class="text-container">
							<span class="text-muted font-weight-bold">Груз: </span>
							<span>${order.cargo}</span>
						</div>

						<!-- <div class="text-container"></div>
							<span class="text-muted font-weight-bold">Грузоподъемность, т:</span>
							<span>order.truckLoadCapacity</span>
						</div>

						<div class="text-container"></div>
							<span class="text-muted font-weight-bold">Объем кузова, м.куб.:</span>
							<span>order.truckVolume</span>
						</div> -->

						<div class="text-container">
							<span class="text-muted font-weight-bold">Штабелирование: </span>
							<c:choose>
								<c:when test="${order.stacking == true}">
									Да
								</c:when>
								<c:otherwise>
									Нет
								</c:otherwise>
							</c:choose>
						</div>

						<c:choose>
							<c:when test="${order.temperature != null && order.temperature != ''}">
								<div class="text-container">
									<span class="text-muted font-weight-bold">Температура:</span>
									<span>${order.temperature}</span>
								</div>
							</c:when>
						</c:choose>

						<!-- <div class="text-container"></div>
							<span class="text-muted font-weight-bold">Фитосанитарный груз:</span>
							<span>Груз подлежит фитосанитарному контролю</span>
						</div>

						<div class="text-container"></div>
							<span class="text-muted font-weight-bold">Ветеринарный груз:</span>
							<span>Груз подлежит ветеринарному контролю</span>
						</div>

						<div class="text-container"></div>
							<span class="text-muted font-weight-bold">Опасный груз:</span>
							<span>order.dangerous</span>
						</div> -->

					</div>
				</div>

				<h4 class="mt-3 mb-1 text-center">Точки маршрута:</h4>

				<div class="point-container">
					<c:forEach var="point" items="${order.addressesToView}" varStatus="loop">
						<div class="card">
							<div class="card-header">
								<c:choose>
									<c:when test="${point.pointNumber != null && point.pointNumber != ''}">
										<h5 class="d-flex align-items-center mb-0">Точка ${point.pointNumber}: ${point.type} </h5>
									</c:when>
									<c:otherwise>
										<h5 class="d-flex align-items-center mb-0">Точка ${loop.index + 1}: ${point.type} </h5>
									</c:otherwise>
								</c:choose>
							</div>
							<div class="card-body">
								<div class="text-container text-muted">
									<span class="col-form-label font-weight-bold">Дата: </span>
									<c:choose>
										<c:when test="${point.date != null}">
											<span>${point.date}</span>
											<c:choose>
												<c:when test="${point.time != null}">
													<span>&#8226;</span>
													<span>${point.time}</span>
												</c:when>
											</c:choose>
										</c:when>
										<c:otherwise>
											Не назначена
										</c:otherwise>
									</c:choose>
								</div>

								<div class="text-container text-muted">
									<span class="col-form-label font-weight-bold">Информация о грузе: </span>
									<span>${point.cargo}</span>

									<c:choose>
										<c:when test="${point.pall != null}">
											<span>&#8226;</span>
											<span>${point.pall}</span>
											<span> палл.</span>
										</c:when>
									</c:choose>

									<c:choose>
										<c:when test="${point.weight != null}">
											<span>&#8226;</span>
											<span>${point.weight}</span>
											<span> кг</span>
										</c:when>
									</c:choose>

									<c:choose>
										<c:when test="${point.volume != null}">
											<span>&#8226;</span>
											<span>${point.volume}</span>
											<span> м.куб.</span>
										</c:when>
									</c:choose>

								</div>

								<c:choose>
									<c:when test="${point.tnvd != null && point.tnvd != ''}">
										<div class="text-container text-muted">
											<span class="col-form-label font-weight-bold">Коды ТН ВЭД: </span>
											<span>${point.tnvd}</span>
										</div>
									</c:when>
								</c:choose>

								<div class="text-container text-muted">
									<span class="col-form-label font-weight-bold">Адрес склада: </span>
									<span>${point.bodyAddress}</span>
								</div>

								<div class="row-container">

									<c:choose>
										<c:when test="${point.timeFrame != null && point.timeFrame != ''}">
											<div class="text-container text-muted">
												<span class="col-form-label font-weight-bold">Время работы склада: </span>
												<span>${point.timeFrame}</span>
											</div>
										</c:when>
									</c:choose>

									<c:choose>
										<c:when test="${point.contact != null && point.contact != ''}">
											<div class="text-container text-muted">
												<span class="col-form-label font-weight-bold">Контактное лицо на складе: </span>
												<span>${point.contact}</span>
											</div>
										</c:when>
									</c:choose>

								</div>

								<c:choose>
									<c:when test="${point.customsAddress != null && point.customsAddress != ''}">
										<div class="text-container text-muted">
											<span class="col-form-label font-weight-bold">Адрес таможенного пункта: </span>
											<span>${point.customsAddress}</span>
										</div>
									</c:when>
								</c:choose>

							</div>
						</div>
					</c:forEach>
				</div>
			</div>

			<div class="accordion" id="accordion">
				<div class="card">
					<div class="card-header" id="headingOne">
						<h2 class="mb-0 accordion-title">
							<div>Старые точки маршрута</div>
							<button class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapse" aria-expanded="true" aria-controls="collapse">
								Показать/скрыть
							</button>
						</h2>
					</div>
					<div id="collapse" class="collapse" aria-labelledby="headingOne" data-parent="#accordion">
						<div class="card-body">
							<div class="point-container">
								<c:forEach var="point" items="${order.addresses}" varStatus="loop">
									<c:choose>
										<c:when test="${point.isCorrect == false}">
											<div class="card">
												<div class="card-header">
													<h5 class="d-flex align-items-center mb-0">${point.type} </h5>
												</div>
												<div class="card-body">
													<div class="text-container text-muted">
														<span class="col-form-label font-weight-bold">Дата: </span>
														<c:choose>
															<c:when test="${point.date != null}">
																<span>${point.date}</span>
																<c:choose>
																	<c:when test="${point.time != null}">
																		<span>&#8226;</span>
																		<span>${point.time}</span>
																	</c:when>
																</c:choose>
															</c:when>
															<c:otherwise>
																Не назначена
															</c:otherwise>
														</c:choose>
													</div>
													<div class="text-container text-muted">
														<span class="col-form-label font-weight-bold">Информация о грузе: </span>
														<span>${point.cargo}</span>
														<c:choose>
															<c:when test="${point.pall != null}">
																<span>&#8226;</span>
																<span>${point.pall}</span>
																<span> палл.</span>
															</c:when>
														</c:choose>
														<c:choose>
															<c:when test="${point.weight != null}">
																<span>&#8226;</span>
																<span>${point.weight}</span>
																<span> кг</span>
															</c:when>
														</c:choose> 
														<c:choose>
															<c:when test="${point.volume != null}">
																<span>&#8226;</span>
																<span>${point.volume}</span>
																<span> м.куб.</span>
															</c:when>
														</c:choose>
													</div>
													<c:choose>
														<c:when test="${point.type == 'Загрузка'}">
															<div class="text-container text-muted">
																<span class="col-form-label font-weight-bold">Коды ТН ВЭД: </span>
																<span>${point.tnvd}</span>
															</div>
														</c:when>
													</c:choose>
													<div class="text-container text-muted">
														<span class="col-form-label font-weight-bold">Адрес склада: </span>
														<span>${point.bodyAddress}</span>
													</div>
													<div class="row-container">
														<c:choose>
															<c:when test="${point.timeFrame != null && point.timeFrame != ''}">
																<div class="text-container text-muted">
																	<span class="col-form-label font-weight-bold">Время работы склада: </span>
																	<span>${point.timeFrame}</span>
																</div>
															</c:when>
														</c:choose>
														<c:choose>
															<c:when test="${point.contact != null && point.contact != ''}">
																<div class="text-container text-muted">
																	<span class="col-form-label font-weight-bold">Контактное лицо на складе: </span>
																	<span>${point.contact}</span>
																</div>
															</c:when>
														</c:choose>
													</div>
													<c:choose>
														<c:when test="${point.customsAddress != null && point.customsAddress != ''}">
															<div class="text-container text-muted">
																<span class="col-form-label font-weight-bold">Адрес таможенного пункта: </span>
																<span>${point.customsAddress}</span>
															</div>
														</c:when>
													</c:choose>
												</div>
											</div>
										</c:when>
									</c:choose>
								</c:forEach>
							</div>
						</div>
					</div>
				</div>
			</div>
			<div class="accordion" id="accordionTwo">
				<div class="card">
					<div class="card-header" id="headingTwo">
						<h2 class="mb-0 accordion-title">
							<div>Маршруты</div>
							<button class="accordion-btn" type="button" data-toggle="collapse" data-target="#collapseTwo" aria-expanded="true" aria-controls="collapse">
								Показать/скрыть
							</button>
						</h2>
					</div>
					<div id="collapseTwo" class="collapse" aria-labelledby="headingTwo" data-parent="#accordionTwo">
						<div class="card-body">
							<div class="routes-container">
								
							</div>
						</div>
					</div>
				</div>
			</div>
		</div>
	</div>
	<jsp:include page="footer.jsp" />
	<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/orderHistory.js" type="module"></script>
	<script src='${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js'></script>
</body>
</html>