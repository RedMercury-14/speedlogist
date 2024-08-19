<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>      
    <%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
	<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
	<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
	<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<!DOCTYPE html>
<html lang="ru">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Ошибка 500 - Внутренняя ошибка сервера</title>
    <style>
        body {
            margin: 0;
            padding: 0;
            font-family: Arial, sans-serif;
            background: linear-gradient(135deg, #3a6186, #89253e);
            color: #fff;
            display: flex;
            justify-content: center;
            align-items: center;
            height: 100vh;
            text-align: center;
            position: relative;
        }
        .header {
            position: absolute;
            top: 20px;
            left: 20px;
            right: 20px;
            display: flex;
            justify-content: space-between;
            font-size: 1.5em;
            font-weight: bold;
        }
        .container {
            max-width: 600px;
            padding: 20px;
            background: rgba(0, 0, 0, 0.6);
            border-radius: 10px;
            box-shadow: 0 4px 15px rgba(0, 0, 0, 0.3);
        }
        h1 {
            font-size: 3em;
            margin: 0;
        }
        p {
            font-size: 1.2em;
            margin: 20px 0;
        }
        .server-message {
            margin: 20px 0;
            padding: 20px;
            background-color: rgba(255, 255, 255, 0.1);
            border-left: 5px solid #ffd700;
            font-size: 1.1em;
            color: #f0f0f0;
            font-weight: bold;
            border-radius: 5px;
            box-shadow: inset 0 0 10px rgba(0, 0, 0, 0.2);
        }
        a {
            color: #ffd700;
            text-decoration: underline;
        }
        a:hover {
            text-decoration: none;
        }
    </style>
</head>
<body>
    <div class="header">
        <div>SpeedLogist</div>
        <div>Отделу разработок</div>
    </div>
    <div class="container">
        <h1>500</h1>
        <p>Внутренняя ошибка сервера</p>
        <p>Что-то пошло не так на нашей стороне. Пожалуйста, сообщите в отдел разработок о ошибке.</p>
        <div class="server-message">
            ${message}
        </div>
        <p><a href="/speedlogist/">На главную</a></p>
    </div>
</body>
</html>


