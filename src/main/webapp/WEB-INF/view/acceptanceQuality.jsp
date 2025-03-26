<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>

<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
<%--    <meta name="${_csrf.parameterName}" content="${_csrf.token}" />--%>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Отдел качества</title>
    <script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
<%--    <script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>--%>
    <link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/analytics.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/acceptanceQualityMainCardsModal.css">
</head>
<body>

<jsp:include page="headerNEW.jsp" />

<div class="fluid-container my-container">
    <div class="px-5 d-flex align-items-center justify-content-between">
        <h1 class="mb-2">Отдел качества</h1>

        <!-- Фильтрация -->
        <div class="d-flex align-items-center">
            <label for="status" class="mr-2">Статус:</label>
            <select id="status" class="form-control">
                <option value="new">Новый</option>
                <option value="inProcess">В процессе</option>
                <option value="closed">Закрыт</option>
            </select>

            <label for="startDate" class="ml-4 mr-2">Дата начала:</label>
            <input type="date" id="startDate" class="form-control">

            <label for="endDate" class="ml-4 mr-2">Дата окончания:</label>
            <input type="date" id="endDate" class="form-control">

            <button id="loadDataButton" class="btn btn-primary ml-4">Загрузить данные</button>
        </div>
    </div>

    <div id="acceptanceQualityMainCardsModal" class="modal">
        <div class="modal-content">
            <span class="close">&times;</span> <!-- Кнопка закрытия -->
            <div class="modal-body">
            </div>
        </div>
    </div>

    <div id="grid" class="ag-theme-balham" style="height: 500px; width: 100%"></div>

    <!-- Модальное окно ошибки -->
    <div id="errorModal" class="modal">
        <div class="modal-content">
            <span id="errorMessage"></span>
            <button onclick="closeErrorModal()" class="btn btn-secondary">ОК</button>
        </div>
    </div>

    <!-- Сообщение Snackbar -->
    <div id="snackbar" class="snackbar">Сообщение</div>
</div>

<script src="${pageContext.request.contextPath}/resources/js/mainPage/nav-fixed-top.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/FullCalendar/index.global.min.js"></script>
<script src="${pageContext.request.contextPath}/resources/js/AG-Grid/ag-grid-enterprise.min.js"></script>
<%--<script type="module" src="${pageContext.request.contextPath}/resources/js/modal.js"></script>--%>
<script type="module" src="${pageContext.request.contextPath}/resources/js/acceptanceQuality.js"></script>

</body>
</html>
