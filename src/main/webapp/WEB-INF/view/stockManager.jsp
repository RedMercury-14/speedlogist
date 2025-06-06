<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>Insert title here</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/jquery.min.js"
	type="text/javascript"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="${pageContext.request.contextPath}/resources/js/bootstrap3/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>

<div class="container">
    <form:form method="post" action="./stockmanager" >
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart" value="<c:out value="${dateNow}" />"/>
	по<input type="date" name="dateFinish" value="<c:out value="${dateTomorrow}" />"/>
	<br>
   <p><input type="submit" value="Отобразить" name="view"></p>   
  </form:form>
</div>
<div class="container-fluid">
<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" >
			<thead class="text-center">
				<tr>
					<th><p class="text-center">Номер маршрута</p></th>
					<th>Номер машины</th>
					<th>Перевозчик</th>
					<th>Номер склада</th>
					<th>Дата загрузки</th>
					<th>Время загрузки (планируемое)</th>
					<th>Название маршрута</th>
					<th>Время прибытия на загрузку</th>
					<th>Фактическое время прибытия на загрузку</th>
					<th>Начало загрузки</th>
					<th>Окончание загрузки</th>
					<th>Время выдачи ТТН</th>
					<th>ФИО Грузчика</th>
					<th>Рампа</th>
					<th>Линии</th>
					<th>Санобработка</th>
					<th>Температура</th>
					<th>Общее колличество паллет</th>
					<th>Паллетовместимость авто</th>
					<th>Общий вес</th>
					
					
				</tr>
			</thead>
				<!-- loop over and print our customers -->
				
							
				<c:forEach var="route" items="${routes}">
					 <form:form method="post" action="./stockUpdate">
					 <input type="hidden" value="${route.idRoute}" name="id" />	
					<tr>
						<td>${route.idRoute}</td>
						<td>${route.truck.numTruck}</td>
						<td>${route.user.companyName}</td>
						<td>${route.numStock}</td>
						<td nowrap="nowrap">${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.routeDirection}</td>
						<td><input type = "time" name="timeLoadPreviouslyStock" value="${route.timeLoadPreviouslyStock}"/></td>
						<td><input type = "datetime-local" name="actualTimeArrival" value="${route.actualTimeArrival}"/></td>						
						<td>
						<c:choose>
							<c:when test="${route.startLoad == null}">			
							<input type="submit" value="Начало" name="startLoad" />
							</c:when>
								<c:otherwise>
							${route.startLoad}
							</c:otherwise>
						</c:choose>	
						</td>
						<td>
						<c:choose>
						<c:when test="${route.finishLoad == null}">			
							<input type="submit" value="Окончание" name="finishLoad" />
							</c:when>
								<c:otherwise>
							${route.finishLoad}
							</c:otherwise>
						</c:choose>	
						</td>
						<td>
						<c:choose>
						<c:when test="${route.deliveryDocuments == null}">			
							<input type="submit" value="Выдача" name="deliveryDocuments" />
							</c:when>
								<c:otherwise>
							${route.deliveryDocuments}
							</c:otherwise>
						</c:choose>	
						</td>
						<td>${route.nameLoader}</td>
						<td><input name="rump" value="${route.ramp}" size="2"/></td>
						<td><input name="lines" value="${route.lines}" size="2"/></td>
						<td>${route.isSanitization}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>Паллетовместимость авто</td>
						<td>${route.totalCargoWeight}</td>
					</tr>
					<tr>	
					<td>		
							<tr><th>Номер точки</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.order}</td>                
				</c:forEach></tr>
							<tr><th>Вес</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.weight}</td>                
				</c:forEach></tr>
							<tr><th>Паллеты</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.pall}</td>                
				</c:forEach></tr>
							
							<tr><th>Адрес</th><c:forEach var="point" items="${route.roteHasShop}">																
				<td>${point.shop.address}</td>   
				            
				</c:forEach></tr>
								<tr><th><p><input type="submit" value="Обновить" name="update"></p>  </th></tr>
                  </td>
                  </tr>	   	 
            </form:form>        
			</c:forEach>
			
			</table>
			</div>			
		</div>
	</div>

</body>
</html>