<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<!DOCTYPE html>
<html lang="ru">
<head>
	<meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1, maximum-scale=1">
	<meta http-equiv="X-UA-Compatible" content="IE=edge">
    <style type="text/css">
		.to-bottom {
			position: relative;
			bottom: 0;
			width: 100vw;
			height: 51px;
			z-index: 2;
			padding: 0;
		}
    </style>
</head>
<body>
    <!-- FOOTER START -->
    <footer class="to-bottom">
        <div class="bottom-footer">
            <div class="container">
                <div class="row">
                    <div class="col-md-6">
                        <div class="copyright"><p>Доброном © 2024</p></div>
                    </div>
                    <div class="col-md-6">
                        <ul class="footer-nav">
                            <li><a href="#">О нас</a></li>
                            <li class="center-nav-item"><a href="#">Политика приватности</a></li>
                            <li><a href="#">Контакты</a></li>
                        </ul>
                    </div>
                </div>
            </div>
        </div>
    </footer>
    <!-- FOOTER END -->
</body>