<%@ page language="java" contentType="text/html; charset=UTF-8"
	pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@ taglib uri="http://www.springframework.org/security/tags"
	prefix="sec"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<meta name="viewport" content="width=device-width, initial-scale=1">
	<style type="text/css">
		.none {
			display: none;
		}
		.activRow{
				background: #00ff0852;
			}
		.badRow{
				background: #ff000091;
		}
		.navbar {
			background-color: #0e377b !important;
		}
		.my-container {
			margin-top: 100px;
		}
		footer.to-bottom {
			margin-top: 51px;
			position: fixed;
		}
	</style>
	<title>Предложения по маршруту</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
</head>
<body>
	<jsp:include page="headerNEW.jsp" />
	<input type="hidden" value="${idRoute}" id="idRoute">
	<input type="hidden" value='${routeDirection}' id="routeDirection">
	<input type="hidden" value="${isAdmin}">
	<input type="hidden" value="${loginUser}" id="loginUser">
	
	<input type="hidden"
		value="<sec:authentication property="principal.authorities" />"
		id="role">

	<div class="container my-container">
		<h2>Предложения</h2>
		<table
			class="table table-bordered table-hover table-condensed"
			id="sort">
			<c:choose>
				<c:when test="${isAdmin}">
						<tr>
							<center><button id="proof">Подтвердить перевозчика</button></center> 
							<br>
						</tr>
				</c:when>				
			</c:choose>
			

		</table>
		<button class="btn btn-outline-secondary" onclick="goBack()">Назад</button>
		<script>
			function goBack() {
				window.history.back();
			}
		</script>
	</div>

	<jsp:include page="footer.jsp" />
	<script type="module" src="${pageContext.request.contextPath}/resources/js/tenderOffer.js"></script>
	<script	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>
	
</body>
</html>