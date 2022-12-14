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
<title>Insert title here</title>
<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">
<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
<body>
<jsp:include page="header.jsp"/>
<input type="hidden" value="<sec:authentication property="principal.username" />" id="login">
<div class="container">
<c:choose>
	<c:when test="${route.timeLoadPreviously != null && edit == null}">
		<label><h3>Просмотр маршрута</h3></label>
<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">
			<thead class="text-center">
				<tr>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Тип транспорта</th>
					<th>Начальные стоимости перевозки</th>
					
				</tr>
			</thead>
				<form:form modelAttribute="route" method="post">
					<tr>
						<td>${route.dateLoadPreviously}</td>
						<td>${route.timeLoadPreviously}</td>
						<td>${route.temperature}</td>
						<td>${route.totalLoadPall}</td>
						<td>${route.totalCargoWeight}</td>
						<td>${route.routeDirection}</td>
						<td>${route.typeTrailer}</td>
						<td>${route.startPrice}</td>				
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
			</div>
			<div class="container"><input type="button" onclick="history.back();" value="Назад"/></div>
	</c:when>
	<c:when test="${route.timeLoadPreviously != null && edit == true}">
				<label><h3>Редактор маршрута</h3></label>
<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">			
			<thead class="text-center">
				<tr>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Тип транспорта</th>
					<th>Начальные стоимости перевозки</th>
					
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
						<td><input type = "date" name="date" value="${route.dateLoadPreviously}" required="true" /></td>
						<td><input type = "time" name="timeOfLoad" value="${route.timeLoadPreviously}" required="true"/></td>
						<td><form:input path="temperature" value="${route.temperature}" size="2"/></td>
						<td><form:input path="totalLoadPall" value="${route.totalLoadPall}" size="2"/></td>
						<td><form:input path="totalCargoWeight" value="${route.totalCargoWeight}" size="2"/></td>
						<td><form:input path="routeDirection" value="${route.routeDirection}" required="true" id="routeDirection"/>
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
						<td><form:input path="startPrice" value="${route.startPrice}" size="2"/></td>						
							<input type="submit" value="Редактировать маршрут" name="edit"/> | 	
							<input type="submit" value="Удалить маршрут" name="delite" onclick="if (!(confirm('Вы действительно хотите удалить маршрут?'))) return false"/> | 
							<input type="button" value="Отметить прибытие авто" name="На_выгрузке"/>
										
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
			</div>
			<div class="container"><input type="button" onclick="history.back();" value="Назад"/></div>
	</c:when>
	<c:otherwise>
	<label><h3>Создание маршрута</h3></label>
<div class="table-responsive">
			<table  class="table table-bordered border-primary table-hover table-condensed" id = "sort">
			<thead class="text-center">
				<tr>
					<th>Дата загрузки</th>
					<th>Время загрузки</th>
					<th>Температура</th>
					<th>Колличество паллет</th>
					<th>Вес</th>
					<th>Название маршрута</th>
					<th>Тип транспорта</th>
					<th class="none">Начальные стоимости перевозки</th>
					
				</tr>
			</thead>
				<form:form modelAttribute="route" method="post">
					<tr>
						<td><input type = "date" name="date" value="${route.dateLoadPreviously}" required="true" /></td>
						<td><input type = "time" name="timeOfLoad" value="${route.timeLoadPreviously}" required="true"/></td>
						<td><form:input path="temperature" value="${route.temperature}" size="2"/></td>
						<td><form:input path="totalLoadPall" value="${pall}" size="2"/></td>
						<td><form:input path="totalCargoWeight" value="${weight}" size="2"/></td>
						<td>${route.routeDirection}</td>
						<td><p><form:select path="typeTrailer" required="true">
 						<option></option>
  						<option>Открытый</option>
  						<option>Тент</option>
 						<option>Изотермический</option>
  						<option>Мебельный фургон</option>
 						<option>Рефрижератор</option>
 						</form:select></p></td>
						<td class="none"><form:input path="startPrice" value="${route.startPrice}" size="2" /></td>
							<input type="submit" value="Создать маршрут">				
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
			</div>
	</c:otherwise>
</c:choose>
			</div>
			
			<script charset="utf-8" src="${pageContext.request.contextPath}/resources/js/routeForm.js" type="module"></script>
</body>
</html>