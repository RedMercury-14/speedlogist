<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<meta name="${_csrf.parameterName}" content="${_csrf.token}"/>
<meta name="viewport" content="width=device-width, initial-scale=1">
         <style>
             .text {
                 margin: auto;
                 text-align: center;
                 font-size: 32px;
                 line-height: 1.5;
                 text-shadow: 0 0 15px rgba(255,255,255,.5), 0 0 10px rgba(255,255,255,.5);
             }
             .right-click-menu {
                 margin: 0;
                 padding: 0;
                 position: fixed;
                 list-style: none;
                 background: #ddddddcc;
                 border: 2px solid #ffffff00;
                 border-radius: 2px;
                 display: none;
             }
             .right-click-menu.active {
                 display: block;
             }
             .right-click-menu li {
                 width: 100%;
                 padding: 10px;
                 box-sizing: border-box;
                 cursor: pointer;
                 font-size: 15px;
             }
             .right-click-menu li:hover {
                 background: #ffffff73;
             }
         </style>
<title>Дневной отчёт</title>
<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
<!-- reference our style sheet -->

<link rel="stylesheet" type="text/css" href="<c:url value="/resources/css/other.css"/>"/>
<!-- Latest compiled and minified CSS -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap.min.css">

<!-- Optional theme -->
<link rel="stylesheet"
	href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/css/bootstrap-theme.min.css">

<!-- Latest Jquery -->
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.6.0/jquery.min.js"></script>

<!-- Latest compiled and minified JavaScript -->

</head>
<input id="users" type="button" value="Показать всех юзеров">
<body>

<jsp:include page="header.jsp"/>
<div class="container" id = "cahngeDate">
			<label>Выберите дни:</label>				
			C <input type="date" name="dateStart" id = "dateStart"/>
	по<input type="date" name="dateFinish" id = "dateFinish"/>
	<br> 
</div>
<div class = "content"></div>


		<ul class="right-click-menu">
            <li id="l1">Отправить на склад</li>
            <li id="l2">Отправить тендер</li>
            <li id="l3">Показать точки выгрузок</li>
            <li id="l4">Редактор маршрутов</li>
        </ul>
<script	src="${pageContext.request.contextPath}/resources/js/test.js" type="module"></script>
<script
	src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.5/js/bootstrap.min.js"></script>

<script src="${pageContext.request.contextPath}/resources/tablesort/src/tablesort.js"></script>
<script src="${pageContext.request.contextPath}/resources/tablesort/test/tape.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.dotsep.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.date.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.number.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.monthname.js"></script>
<script	src="${pageContext.request.contextPath}/resources/tablesort/src/sorts/tablesort.filesize.js"></script>

<script>
table = document.getElementById('sort');
console.log(table);
new Tablesort(table);
</script>
		


</body>
</html>