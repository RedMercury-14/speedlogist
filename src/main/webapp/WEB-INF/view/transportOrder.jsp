<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/security/tags" prefix="sec" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<!DOCTYPE html>
<html>
<head>
	<meta charset="UTF-8">
	<meta name="${_csrf.parameterName}" content="${_csrf.token}" />
	<title>Транспортная заявка</title>
	<link rel="icon" href="${pageContext.request.contextPath}/resources/img/favicon.ico">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/transportOrder.css">
	<link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/snackbar.css">
</head>

<body>
	<jsp:include page="headerNEW.jsp" />

	<div class="container my-container">

		<div class="printBtn-container no-print">
			<button id="printBtn" class="btn btn-secondary" type="button">Печать</button>
		</div>
		<!-- Колонтитул -->
		<div class="header-info">
			<!-- Контейнер для изображения -->
			<!-- <img src="${pageContext.request.contextPath}/resources/img/logo.png" alt="Логотип компании"> -->
			
			<!-- Текстовая информация -->
			<div class="header-text">
				<p>
					Закрытое акционерное общество «Доброном»<br>
					<!-- 220073 г. Минск пер. Загородный 1-й 20-23<br>
					УНП 191178504 ОКПО 378869615000<br>
					Тел./факс (8-017) 362 98 76, e–mail: dobronom@tut.by<br>
					р/с BY61ALFA30122365100050270000 в ЗАО «Альфа-банк»<br>
					МФО ALFABY2X г. Минск ул. Сурганова 43-47 -->
				</p>
			</div>
		</div>

		<div class="document-header">
			<p>Дата заявки: <strong>13.08.2024</strong></p>
			<p>Заявка № 18313 в адрес "Новак Деливери"</p>
		</div>

		<table class="table table-bordered">
			<tbody>
				<tr>
					<td>1</td>
					<td>Адрес загрузки</td>
					<td>
						1. TR Турция; 42250 Konya Organize Sanayi Bölgesi 101. Cd. No:15 KM<br>
						2. TR Турция; 42250 Konya Organize Sanayi Bölgesi 101. Cd. No:15 KM
					</td>
				</tr>
				<tr>
					<td>2</td>
					<td>Дата и время загрузки</td>
					<td>
						1. 03.06.2024  14:00 по местному времени<br>
						2. 03.06.2024  14:00 по местному времени
					</td>
				</tr>
				<tr>
					<td>3</td>
					<td>Отправитель</td>
					<td>LIMITED LIABILITY COMPANY</td>
				</tr>
				<tr>
					<td>4</td>
					<td>Контактное лицо на месте загрузки</td>
					<td>
						1. Çikolata Fabrikası тел. +48505289707, +905412771261<br>
						2. Çikolata Fabrikası тел. +48505289707, +905412771261
					</td>
				</tr>
				<tr>
					<td>5</td>
					<td>Режим работы места загрузки</td>
					<td>
						1. Будние дни: c 17:00 по 20:00, суббота: с 09:00 по 21:00, воскресенье: с 10:00 по 17:00<br>
						2. Будние дни: c 17:00 по 20:00, суббота: с 09:00 по 21:00, воскресенье: с 10:00 по 17:00
					</td>
				</tr>
				<tr>
					<td>6</td>
					<td>Место таможенного оформления (таможня отправления)</td>
					<td>
						1. TR Турция; 42250 Konya Organize Sanayi Bölgesi 101. Cd. No:15 KM<br>
						2. TR Турция; 42250 Konya Organize Sanayi Bölgesi 101. Cd. No:15 KM
					</td>
				</tr>
				<tr>
					<td>7</td>
					<td>Адрес выгрузки</td>
					<td>
						1. BY Беларусь; 223035 Минский р-н. д. Сеница, ул. Промышленная 9Б (Колодищанский лесозавод)<br>
						2. BY Беларусь; 223035 Минский р-н. д. Сеница, ул. Промышленная 9Б (Колодищанский лесозавод)
					</td>
				</tr>
				<tr>
					<td>8</td>
					<td>Дата и время выгрузки</td>
					<td>
						1. 10.06.2024, 14:00 по местному времени<br>
						2. 10.06.2024, 14:00 по местному времени
					</td>
				</tr>
				<tr>
					<td>9</td>
					<td>Получатель</td>
					<td>ООО «Новак Деливери»</td>
				</tr>
				<tr>
					<td>10</td>
					<td>Контактное лицо на месте выгрузки</td>
					<td>
						1. Иван Иванович Иванов тел. +375 (29) 111-11-11<br>
						2. Иван Иванович Иванов тел. +375 (29) 111-11-11
					</td>
				</tr>
				<tr>
					<td>11</td>
					<td>Режим работы места выгрузки</td>
					<td>
						1. Будние дни: c 17:00 по 20:00, суббота: с 09:00 по 21:00, воскресенье: с 10:00 по 17:00<br>
						2. Будние дни: c 17:00 по 20:00, суббота: с 09:00 по 21:00, воскресенье: с 10:00 по 17:00
					</td>
				</tr>
				<tr>
					<td>12</td>
					<td>Место таможенного оформления (таможня назначения)</td>
					<td>
						1. TR Турция; 42250 Konya Organize Sanayi Bölgesi 101. Cd. No:15 KM<br>
						2. TR Турция; 42250 Konya Organize Sanayi Bölgesi 101. Cd. No:15 KM
					</td>
				</tr>
				<tr>
					<td>13</td>
					<td>Маршрут (строгие погран. переходы, порты, иное)</td>
					<td></td>
				</tr>
				<tr class="subrow">
					<td>14</td>
					<td>Описание груза:</td>
					<td></td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- наименование</td>
					<td>Товары народного потребления</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- вес брутто, кг</td>
					<td>20 000</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- объем, м³</td>
					<td>40</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- способ загрузки</td>
					<td>На паллетах</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- тип загрузки</td>
					<td>Задняя</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- количество грузовых мест</td>
					<td>33</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- температурный режим</td>
					<td>20</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- опасный груз</td>
					<td>UN2040, класс 2, I группа упаковки I</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- штабелирование грузовых мест</td>
					<td>Не разрешено</td>
				</tr>
				<tr>
					<td>15</td>
					<td>Количество транспортных средств</td>
					<td>1</td>
				</tr>
				<tr class="subrow">
					<td>16</td>
					<td>Требуемый тип подвижного состава</td>
					<td></td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- грузоподъёмность, тонн</td>
					<td>20</td>
				</tr>
				<tr class="subrow">
					<td></td>
					<td>- объем, м³</td>
					<td>90</td>
				</tr>
				<tr>
					<td>17</td>
					<td>Способ загрузки</td>
					<td>Задняя</td>
				</tr>
				<tr>
					<td>18</td>
					<td>Дополнительные условия перевозки</td>
					<td class="termsOfCarriage">
						Необходима сверка Унифицированного контрольного знака, не разрешается покидать место погрузки без разрешения представителя заказчика перевозки.<br>
						Необходим TIR для оформления.<br>
						Груз подлежит ветеринарному контролю.<br>
						Груз подлежит фитосанитарному контролю.<br>
						Условия поставки Инкотермс: FAS – Free Alongside Ship.<br>
						Место поставки: LINYI,SHANDONG, CHINA. <br>

						контактный телефон охраны (КПП): моб — +7 903 380 44 91, город — +7 8452 75 99 57, доб. 205
						контактный телефон бухгалтера (Воскресенцева Ирина) на выписке первички: +7 8452 75 99 57, доб. 212
						GPS координаты для навигатора: широта: N 51°41.868' долгота: E 46°45.503'
						Режим работы склада: пн-пт 8:00 — 17:00, сб 8:00 — 15:30, вс — выходной
					</td>
				</tr>
				<tr>
					<td>19</td>
					<td>Стоимость услуг, включая вознаграждение экспедитора (исключая дополнительные расходы)</td>
					<td>5500 EUR</td>
				</tr>
				<tr>
					<td>20</td>
					<td>Условия оплаты</td>
					<td>Согласно договора</td>
				</tr>
				<tr>
					<td>21</td>
					<td>Логист, контактный телефон </td>
					<td>Насковец Андрей, +375-00-000-00-00</td>
				</tr>
			</tbody>
		</table>

		<div class="footer-text">
			<p>
				За несвоевременную подачу авто Исполнитель несёт ответственность согласно действующего договора.<br>
				Обязательно наличие CMR - страхования.<br>
				Штраф за срыв  загрузки - 10 % от суммы  фрахта с виновной стороны. <br>
				В случае полной или частичной утраты или повреждения груза полную материальную ответственность по возмещению ущерба несет Исполнитель.
			</p>
			
			<p>
				Настоящим подтверждаем принятие заявки на указанных Вами условиях, а также сообщаем Вам:<br>
				Автомобиль : ( марка, номер)<br>
				Полуприцеп / прицеп : ( марка, номер)<br>
				Данные водителя ( паспорт, ФИО )
			</p>
		</div>

		<div class="signature">
			<p>Подпись ___________________</p>
			<p>Подпись ___________________</p>
		</div>

		<div id="snackbar"></div>
	</div>

	<jsp:include page="footer.jsp" />

	<script src='${pageContext.request.contextPath}/resources/mainPage/js/nav-fixed-top.js'></script>
	<script src="${pageContext.request.contextPath}/resources/js/transportOrder.js" type="module"></script>
</body>
</html>