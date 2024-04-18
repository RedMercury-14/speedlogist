<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
	<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<style type="text/css">
.none{
	display: none;
}
</style>
<title>Архив актов выполненных работ</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">

<!-- reference our style sheet -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/style.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script
	src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"
	type="text/javascript"></script>
<!-- Latest compiled and minified JavaScript -->
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
</head>
</head>
<body>
<jsp:include page="header.jsp"/>
<div class="container">
		<form:form method="get">		
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart"
				value="<c:out value="${dateNow}" />" />
	по<input type="date" name="dateFinish"
				value="<c:out value="${dateTomorrow}" />" />
			<br>
			<input type="submit" value="Отобразить">
			</p>
		</form:form>	
	</div>	
	<div class="container table-responsive">
			<table	class="table table-bordered border-primary  table-condensed table-hover table-responsive"	id="sort">
				<thead class="text-center">
					<tr class="sticky">
						<th class="col-1">Id записи</th>
						<th class="col-2">Номер акта</th>
						<th class="col-3">Дата создания акта</th>
						<th class="col-4">Номера рейсев</th>
						<th class="col-5">Стоимость после тендера</th>
						<th class="col-6">НДС</th>
						<th class="col-7">Стоимость с НДС</th>
						<th class="col-8">Комментарий</th>
						<th class="col-9">Статус</th>
						<th></th>
					</tr>
				</thead>
				
				<!-- loop over and print our customers -->
				<c:forEach var="act" items="${acts}">
					<form:form method="post">
						<sec:csrfInput />
						<tr>
						<input type="hidden" value="${act.status}" id="status">
						<input type="hidden" value="${act.idAct}" name="idAct">
						<input type="date" name="dateStart"	value="<c:out value="${dateNow}" />" style="display: none;"/>
						<input type="date" name="dateFinish" value="<c:out value="${dateTomorrow}" />" style="display: none;"/>
							<td class="col-1">${act.idAct}</td>
							<td class="col-2">${act.numAct}</td>
							<td class="col-3">${act.time}</td>
							<td class="col-4">${act.idRoutes}</td>
							<td class="col-5" width="100">${act.finalCost} ${act.currency}</td>
							<td class="col-6">${act.nds}</td>
							<td class="col-7">${act.finalCost + act.nds} ${act.currency}</td>
										
							<c:choose>
								<c:when test="${act.comment == null}">
									<td class="col-8"><input type="text" value="${act.comment}" name="comment" required="true"></td>
								</c:when>
								<c:otherwise>
									<td class="col-8">${act.comment}</td>
								</c:otherwise>
							</c:choose>				
							<c:choose>
								<c:when test="${act.status == '1'}">
									<td class="col-9"> В обработке </td>
									<td width="200">							
								<input type="submit" value="Подписать" name="getAct" style="display: inline-block;">
								<input type="submit" value="Отменить" name="cancelAct" style="display: inline-block;">
							</td>	
								</c:when>
								<c:when test="${act.status != '1' && act.cancel == null}">
									<td class="col-9">Подписан  ${act.status}  </td>
								</c:when>
								<c:when test="${act.cancel != null}">
									<td class="col-9">Акт отменён ${act.cancel}</td>
								</c:when>
							</c:choose>						
						</tr>							
					</form:form>	
				</c:forEach>
			</table>
		</div>

</body>
</html>