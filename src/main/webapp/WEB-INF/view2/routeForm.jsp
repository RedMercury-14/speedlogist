<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
<style type="text/css">
.none{
display: none;
}
</style>
<title>Маршрут</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">
<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">
<script src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
<script type="text/javascript" src="https://www.gstatic.com/charts/loader.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
<div class="container-fluid">
<c:choose>
	<c:when test="${route.timeLoadPreviously != null && edit == null}">
		<label><h3>Просмотр маршрута ${route.routeDirection}</h3></label>
<div class="table">
			
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort" >
			<thead class="text-center">
				<tr>
					<th>Направление</th>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Объем</th>
					<th>Вес</th>
					<th>Груз</th>
					<th>Тип транспорта</th>
					<th>Комментарии</th>
					<c:choose>
							<c:when test="${route.finishPrice == null && route.startPrice != null}">
								<th>Начальная стоимость перевозки</th>
								<th>Шаг понижения цены</th>
							</c:when>
							<c:when test="${route.finishPrice != null && route.startPrice != null}">
								<th>Начальная стоимость перевозки</th>
								<th>Текущая стоимость перевозки</th>
								<th>Шаг понижения цены</th>
							</c:when>
							<c:otherwise>
								<th>Текущая стоимость перевозки</th>
								<th>Оптимальная стоимость перевозки</th>								
							</c:otherwise>
						</c:choose>	
					
					
					<th>Перевозчик</th>
					<th>Номер авто</th>
					<th>ФИО водителя</th>
					
				</tr>
			</thead>
				<form:form modelAttribute="route" method="post">
				<input type="hidden" value="${route.idRoute}" id="idRoute">
					<tr>
						<td>${route.simpleWay}</td>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td><c:forEach var="RHS" items="${route.roteHasShop}" end="0">
							${RHS.volume}
						</c:forEach>
						</td>
						<td>${route.totalCargoWeight}</td>
						<c:forEach var="RHS" items="${route.roteHasShop}" end="0">
							<td>${RHS.cargo}</td>
						</c:forEach>
						<td>${route.typeTrailer}</td>
						<td>${route.userComments}</td>
						<c:choose>
							<c:when test="${route.finishPrice == null && route.startPrice != null}">
								<td>${route.startPrice} BYN</td>
								<td>${route.stepCost} BYN</td>	
							</c:when>
							<c:when test="${route.finishPrice != null && route.startPrice != null}">
								<td>${route.startPrice} BYN</td>
								<td>${route.finishPrice} BYN</td>
								<td>${route.stepCost} BYN</td>
							</c:when>
							<c:otherwise>
								<td>${route.finishPrice} BYN</td>
								<td>${route.optimalCost} BYN</td>
							</c:otherwise>
						</c:choose>	
										
						<td>${route.user.companyName}</td>
						<td>${route.truck.numTruck} / ${route.truck.numTrailer}</td>
						<td>${route.driver.surname} ${route.driver.name} ${route.driver.patronymic}</td>				
            </form:form>   			
			</table><br>	
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
							<form:form modelAttribute="route" method="post">
							<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.position}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
			<br><br>
			<div id="curve_chart" style="width: 1300px; height: 500px" class="container-fluit" ></div>
			<br>
			<h4>Стоимость прошлых перевозок</h4>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Название маршрута</th>
					<th>Стоимость</th>			
				</tr>
			</thead>
					<c:forEach var="point" items="${listCosts}">
					<tr>
					<td>${point.routeDirection}</td> 
					<td>${point.finishPrice}  ${point.startCurrency}</td> 
					</tr>
					</c:forEach>  			
			</table>
						
			</div>
			<div class="container"><input type="button" onclick="history.back();" value="Назад"/></div>
	</c:when>
	<c:when test="${route.timeLoadPreviously != null && edit == true}">
				<label><h3>Редактор маршрута</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort" >			
			<thead class="text-center">
				<tr>
					<th>Направление</th>
					<th>Дата и время загрузки</th>
					<th>Дата и время разгрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Объем</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Тип транспорта</th>
					<th>Заказчик</th>
					<th>Комментарии</th>
					<c:choose>
 							<c:when test="${route.startPrice != null}">
 								<th>Начальные стоимости перевозки</th>
 								<th>Шаг понижения цены</th>
 							</c:when>
 							<c:otherwise>
 								<th>Оптимальная стоимость перевозки</th>
 							</c:otherwise>
 					</c:choose>
 					
				</tr>
			</thead>
				<form:form modelAttribute="route" method="post">
				<input type="hidden" value="${route.idRoute}" id="idRoute">
				<form:input path="statusStock" value = "${route.statusStock}" type = "hidden" />
				<form:input path="statusRoute" value = "${route.statusRoute}" type = "hidden" />
				<form:input path="finishPrice" value = "${route.finishPrice}" type = "hidden" />
				<form:input path="time" value = "${route.time}" type = "hidden" />
				<form:input path="startPrice" value = "${route.startPrice}" type = "hidden" />
				<form:input path="comments" value = "${route.comments}" type = "hidden" />
					<tr>
						<td><p><form:select path="way" required="true">
 						<option>${route.way}</option>
  						<option>Импортный</option>
  						<option>Экспортный</option>
  						<option>РБ</option>
 						</form:select></p></td>
						<td>
							<input type = "date" name="date" value="${route.dateLoadPreviously}" required="true" />
							<input type = "time" name="timeOfLoad" value="${route.timeLoadPreviously}" required="true" readonly="true"/>
						</td>
						<td>
							<input type = "date" name="dateUnloadPreviouslyStock" value="${route.dateUnloadPreviouslyStock}" />
							<input type = "time" name="timeUnloadPreviouslyStock" value="${route.timeUnloadPreviouslyStock}" />
						</td>
						<td><form:input path="temperature" value="${route.temperature}" size="2" readonly="true"/></td>
						<td><form:input path="totalLoadPall" value="${route.totalLoadPall}" size="2" readonly="true"/></td>
						<td><input type="text" value="${volume}" name="volume" readonly="true"/></td>
						<td><form:input path="totalCargoWeight" value="${route.totalCargoWeight}" size="2" readonly="true"/></td>
						<td><form:input path="routeDirection" value="${route.routeDirection}" required="true" id="routeDirection" readonly="true"/>
						<br><div id="message"></div>
						</td>
						<td><p><form:select path="typeTrailer" required="true">
 						<option>${route.typeTrailer}</option>
  						<option>Открытый</option>
  						<option>Тент</option>
 						<option>Изотермический</option>
  						<option>Мебельный фургон</option>
 						<option>Рефрижератор</option>
 						</form:select></p></td>
 						<td><form:input path="customer" value="${route.customer}" required="true" id="customer" readonly="true"/>
 						<td><form:textarea path="userComments"/></td>
 						<c:choose>
 							<c:when test="${route.startPrice != null}">
 								<td><form:input path="startPrice" value="${route.startPrice}" size="2"/></td>
 								<td><form:input type="number" min = "1" path="stepCost" value="${route.stepCost}" size="2"/></td>
 							</c:when>
 							<c:otherwise>
 								<td><form:input path="optimalCost" type="number" value="${route.optimalCost}" size="3" required="true"/> BYN</td>
 							</c:otherwise>
 						</c:choose>
													
							<input type="submit" value="Редактировать маршрут" name="edit"/> | 	
							<input type="submit" value="Удалить маршрут" name="delite" onclick="if (!(confirm('Вы действительно хотите удалить маршрут?'))) return false"/> | 
							<input type="button" value="Отметить прибытие авто" name="На_выгрузке"/> |
							<input type="submit" value="Отменить проставленное время и авто" name="offCar"/>
										
            </form:form>   			
			</table><br>	
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
							<form:form modelAttribute="route" method="post">
							<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.position}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
			<br><br>
			<h4>Стоимость прошлых перевозок</h4>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Название маршрута</th>
					<th>Стоимость</th>			
				</tr>
			</thead>
					<c:forEach var="point" items="${listCosts}">
					<tr>
					<td>${point.routeDirection}</td> 
					<td>${point.finishPrice}  ${point.startCurrency}</td> 
					</tr>
					</c:forEach>  			
			</table>
			<div class="container"><input type="button" onclick="history.back();" value="Назад"/></div>
	</c:when>
	<c:when test="${route.roteHasShop == null}">
		<h3>Создание маршрута по шаблону</h3>
		<br>
		<select id="pattern">
		<option></option>
					<c:forEach var="patternRoute" items="${patternRoutes}">						
						<option value="<c:out value="${patternRoute.idRoute}"/>"><c:out
							value="${patternRoute.routeDirection}" /></option>
						</c:forEach>
 						</select>
 				<div id="message"></div>
 				<div id="contentRoute"></div>
 				
	</c:when>
	<c:otherwise>
	<c:choose>
	<c:when test="${flag == null}">
	<label><h3>Создание маршрута</h3></label></c:when>
	<c:otherwise><h3>Создание шаблона маршрута</h3>
	<p>При создании шаблона укажите дату и время! Эти данные будут задаваться заново при копировании маршрута</p>
	</c:otherwise>
	</c:choose>
	<div class="container-fluid">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">
			<thead class="text-center">
				<tr>
					<th>Направление</th>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Объем</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Тип транспорта</th>
					<th>Заказчик</th>
					<c:choose>
						<c:when test="${flag == null}">
							<th>Колличество маршрутов</th>
						</c:when>
					</c:choose> 
					<th>Комментарии</th>
					<c:choose>
						<c:when test="${flag == null}">
						<th>Оптимальная стоимость перевозки</th>
						</c:when>
						<c:otherwise>
						<th>Начальные стоимости перевозки</th>
						<th>Шаг понижения цены</th>
						<th>Оптимальная стоимость перевозки</th>
						</c:otherwise>
					</c:choose>
					
					
				</tr>
			</thead>
				<form:form modelAttribute="route" method="post">
					<tr>
						<td class="none"><input type = "hidden" name="idRouteCopy" value="${idRouteCopy}"/></td>
						<td><p><form:select path="way" required="true">
 						<option>${route.way}</option>
  						<option>Импортный</option>
  						<option>Экспортный</option>
  						<option>РБ</option>
 						</form:select></p></td>
						<td><input type = "date" name="date" value="${route.dateLoadPreviously}" required="true" /></td>
						<td><input type = "time" name="timeOfLoad" value="${route.timeLoadPreviously}" required="true" /></td>
						<td><form:input path="temperature" value="${route.temperature}" size="2"/></td>
						<td><form:input path="totalLoadPall" value="${pall}" size="2"/></td>
						<td><input type="text" value="${volume}" name="volume"/></td>
						<td><form:input path="totalCargoWeight" value="${weight}" size="2"/></td>
						
						<c:choose>
						<c:when test="${flag == null}"><td>${route.routeDirection}</td></c:when>
							<c:otherwise><td><form:input path="routeDirection"/></td></c:otherwise>
						</c:choose>
						<td><p><form:select path="typeTrailer" required="true">
 						<option></option>
  						<option>Открытый</option>
  						<option>Тент</option>
 						<option>Изотермический</option>
  						<option>Мебельный фургон</option>
 						<option>Рефрижератор</option>
 						</form:select></p></td>
 						<td><form:input path="customer" value="${route.customer}" required="true" id="customer"/>
 						<c:choose>
							<c:when test="${flag == null}">
								<td><input type="number" name="count" value="1" width="5" min="1"> </td>			
							</c:when>
						</c:choose> 						
 						<td><form:textarea path="userComments"/></td>
 						<c:choose>
							<c:when test="${flag == null}">
							<td class="none"><form:input path="startPrice" value="${route.startPrice}" size="2" /></td>
							<td><form:input path="optimalCost"  type="number" value="${route.optimalCost}" size="3" required="true" /> BYN</td>
							</c:when>
							<c:otherwise>
								<td><form:input path="startPrice" value="${route.startPrice}" size="2" /> BYN</td>
								<td><form:input type="number" min = "1" path="stepCost" value="${route.stepCost}" size="2" /></td>
								<td><form:input path="optimalCost"  type="number" value="${route.optimalCost}" size="3" /> BYN</td>							
							</c:otherwise>
						</c:choose>			
							<input type="submit" value="Создать маршрут">				
            </form:form>   			
			</table>
			</div>
			<br>			
			<label><h3>Данные по точкам</h3></label>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Номер точки</th>
					<th>Вес</th>
					<th>Паллеты</th>
					<th>Адрес</th>				
				</tr>
			</thead>
							<form:form modelAttribute="route" method="post">
							<c:forEach var="point" items="${route.roteHasShop}">
					<tr>
					<td>${point.position}</td> 
					<td>${point.weight}</td> 
					<td>${point.pall}</td>
					<td>${point.address}</td>
					</tr>
					</c:forEach>				
            </form:form>   			
			</table>
			<br><br>
			<h4>Стоимость прошлых перевозок</h4>
			<table  class="table table-bordered border-primary table-hover table-condensed">
			<thead class="text-center">
				<tr>
					<th>Название маршрута</th>
					<th>Стоимость</th>			
				</tr>
			</thead>
					<c:forEach var="point" items="${listCosts}">
					<tr>
					<td>${point.routeDirection}</td> 
					<td>${point.finishPrice}  ${point.startCurrency}</td> 
					</tr>
					</c:forEach>  			
			</table>
	</c:otherwise>
</c:choose>
			</div>
			
		<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/routeForm.js" type="module"></script>
</body>
</html>