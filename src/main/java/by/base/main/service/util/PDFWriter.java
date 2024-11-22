package by.base.main.service.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Row;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.BarcodeQRCode;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.GrayColor;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfGState;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;

import by.base.main.model.Act;
import by.base.main.model.Address;
import by.base.main.model.Order;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.User;
import by.base.main.service.ActService;

/**
 * Класс для работы с pdf
 */
@Service
public class PDFWriter {
	
	@Autowired
	private ActService actService;
	
	public int getProposal(HttpServletRequest request, Route route) throws FileNotFoundException, DocumentException {
	    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
	    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
	    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");
	    String path = request.getServletContext().getRealPath("");
	    com.itextpdf.text.Font fontMainHeader = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 14);
	    com.itextpdf.text.Font fontMainText = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans.ttf", "cp1251", BaseFont.EMBEDDED, 10);
	    com.itextpdf.text.Font fontForRequisitesBolt = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 8);
	    com.itextpdf.text.Font fontMainTextBold = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 10);

	    Document document = new Document();
	    String fileName = "proposal";
	    PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path + "resources/others/" + fileName + ".pdf"));
	    document.open();

//	    System.err.println(path+"resources/others/");  //это важно! Не удалять
	    
	    // Заголовок
	    Paragraph p1 = new Paragraph("Заявка на перевозку ЗАО Доброном №" + route.getIdRoute() + " от " + route.getDateLoadPreviously().format(dateFormatter), fontMainHeader);
	    p1.setSpacingBefore(-30f); // Отступ сверху
	    p1.setSpacingAfter(20f);   // Отступ снизу
	    p1.setAlignment(Element.ALIGN_CENTER);
	    document.add(p1);

	    // Создание таблицы
	    PdfPTable table = new PdfPTable(2);
	    table.setWidthPercentage(100); // Ширина таблицы на 100% страницы
	    table.setSpacingBefore(20f); // Отступ сверху таблицы

	    table.addCell(new Paragraph("Перевозчик", fontMainTextBold));
	    table.addCell(new Paragraph(route.getUser() != null ? route.getUser().getCompanyName() : "", fontMainText));
	    
	    table.addCell(new Paragraph("Выставляемая стоимость :", fontMainTextBold));
	    table.addCell(new Paragraph(route.getFinishPrice() != null ? route.getFinishPrice() + " " + route.getStartCurrency() : "", fontMainText));
	    

	    int numPoint = 1;
	    Order order = route.getOrders().stream().findFirst().get();
	    List<Address> addresses = new ArrayList<Address>(order.getAddresses());
	    if(addresses.get(0).getType().equalsIgnoreCase("выгрузка")) {
	    	Collections.reverse(addresses);
	    }
	    
	    for (Address point : addresses) {
	        // Внутренние строки для точки
	        addRowToTable(table, "Точка: " + numPoint, point.getType(), fontMainTextBold, fontMainText,true,false,true);

	        if (point.getType().equalsIgnoreCase("загрузка")) {
	            addRowToTable(table, "Дата:", point.getDate().toLocalDate().format(dateFormatter) + " " + point.getTime().toLocalTime().format(timeFormatter), fontMainTextBold, fontMainText, false, false, true);
	            addRowToTable(table, "Наименование контрагента:", order.getCounterparty(), fontMainTextBold, fontMainText, false, false, true);
	        } else {
	            addRowToTable(table, "Дата:", order.getTimeDelivery() != null 
	            		? order.getTimeDelivery().toLocalDateTime().format(dateTimeFormatter) 
	            		: "Уточнить у специалиста по логистике", fontMainTextBold, fontMainText, false, false, true);
	        }

	        
	        addRowToTable(table, "Контактное лицо контрагента:", order.getContact(), fontMainTextBold, fontMainText, false, false, true);
	        addRowToTable(table, "Адрес склада:", point.getBodyAddress(), fontMainTextBold, fontMainText, false, false, true);
	        
	        if(order != null && order.getWay().toLowerCase().equals("импорт") || order != null && order.getWay().toLowerCase().equals("экспорт")) {
	        	addRowToTable(table, "Адрес таможенного пункта: ", point.getCustomsAddress(), fontMainTextBold, fontMainText, false, false, true);
	        }

	        addRowToTable(table, "Время работы склада:", point.getTimeFrame(), fontMainTextBold, fontMainText, false, false, true);
	        addRowToTable(table, "Контактное лицо на складе:", point.getContact(), fontMainTextBold, fontMainText, false, false, true);
	        
	        if(point.getType().equalsIgnoreCase("загрузка")) {
	        	String cargoInfo = point.getCargo() + "; ";
		        if (point.getPall() != null) cargoInfo += point.getPall() + " палл; ";
		        if (point.getWeight() != null) cargoInfo += point.getWeight() + " кг; ";
		        if (point.getVolume() != null) cargoInfo += point.getVolume() + " м.куб;";
		        addRowToTable(table, "Информация о грузе:", cargoInfo, fontMainTextBold, fontMainText, false, false, true);
		        addRowToTable(table, "Тип загрузки:", order.getTypeLoad(), fontMainTextBold, fontMainText, false, false, true);
		        addRowToTable(table, "Способ загрузки:", order.getMethodLoad(), fontMainTextBold, fontMainText, false, false, true);
		        addRowToTable(table, "Тип кузова:", order.getTypeTruck(), fontMainTextBold, fontMainText, false, false, true);
		        addRowToTable(table, "Штабелирование:", order.getStacking() ? "Да" : "Нет", fontMainTextBold, fontMainText, false, false, true);
		        addRowToTable(table, "Температура:", order.getTemperature(), fontMainTextBold, fontMainText, false, false, true); 
		        if(order.getControl() != null) addRowToTable(table, "Сверка УКЗ: ", order.getControl() ? "Да, сверять УКЗ" : "Нет, не сверять УКЗ", fontMainTextBold, fontMainText, false, false, true);
	        }
	        
	        
	        if(point.getType().equalsIgnoreCase("выгрузка")) {
	        	if(order.getIncoterms() != null) addRowToTable(table, "Условия поставки :", order.getIncoterms(), fontMainTextBold, fontMainText, false, false, true);
	        	addRowToTable(table, "Номер машины/ прицепа:", route.getTruck() != null ? route.getTruck().getNumTruck() + " / " + route.getTruck().getNumTrailer() : null, fontMainTextBold, fontMainText, false, true, true);
	        }

	        numPoint++;
	    }

	    // Добавляем таблицу в документ
	    document.add(table);
	    
	    Paragraph paragraph = new Paragraph("Перевозчик:", fontMainTextBold);
	    paragraph.setAlignment(Element.ALIGN_RIGHT); // Устанавливаем выравнивание по правой стороне
	    paragraph.setSpacingBefore(20f); // Отступ перед параграфом
        document.add(paragraph); // Добавляем параграф в документ
	    
        
	    Paragraph paragraph123 = new Paragraph(route.getUser() != null ? route.getUser().getCompanyName() : "", fontMainTextBold);
	    paragraph123.setAlignment(Element.ALIGN_RIGHT); // Устанавливаем выравнивание по правой стороне
//	    paragraph123.setSpacingBefore(20f); // Отступ перед параграфом
        document.add(paragraph123); // Добавляем параграф в документ

	    // Закрываем документ
	    document.close();

	    return 0;
	}
	

//	// Вспомогательный метод для добавления строки
//	private void addRowToTable(PdfPTable table, String label, String value, com.itextpdf.text.Font labelFont, com.itextpdf.text.Font valueFont) {
//	    table.addCell(new PdfPCell(new Paragraph(label, labelFont)));
//	    table.addCell(new PdfPCell(new Paragraph(value, valueFont)));
//	}
	private void addRowToTable(PdfPTable table, String label, String value, 
            com.itextpdf.text.Font labelFont, com.itextpdf.text.Font valueFont, 
            boolean thickTopBorder, boolean thickBottomBorder, boolean thickSideBorders) {
			// Создаем ячейки для label и value
			PdfPCell labelCell = new PdfPCell(new Paragraph(label, labelFont));
			PdfPCell valueCell = new PdfPCell(new Paragraph(value, valueFont));
			
			labelCell.setPadding(3f);
			valueCell.setPadding(3f);
			
			// Устанавливаем толщину верхней границы, если требуется
			if (thickTopBorder) {
			labelCell.setBorderWidthTop(2f);
			valueCell.setBorderWidthTop(2f);
			}
			
			// Устанавливаем толщину нижней границы, если требуется
			if (thickBottomBorder) {
			labelCell.setBorderWidthBottom(2f);
			valueCell.setBorderWidthBottom(2f);
			}
			
			// Устанавливаем толщину боковых границ, если требуется
			if (thickSideBorders) {
			labelCell.setBorderWidthLeft(2f);
			valueCell.setBorderWidthRight(2f);
			}
			
			// Добавляем ячейки в таблицу
			table.addCell(labelCell);
			table.addCell(valueCell);
			}


	
	
	/**
	 *	Метод отвечает за формирование акта в формате PDF
	 * <br>Возвращает колличество листов, занимаемым документом.
	 * <br>Логика такая: про первичном формировании акта, метод проверяет сколько листов занимает акт.
	 * <br>Если занимает один лист - акт выплёвывается.
	 * <br>Если занимает больше - метод перестраивается одностительно колличества листов в акте. 
	 * <br>Важно то, что колличество листов передаётся в метод, т.е. оно берется за основу этого метода.
	 * <br><b>Важно: первый раз в свойства метода вписывается 0 (нуль)<b>
	 * @param routes
	 * @param request
	 * @param isNDS
	 * @param dateContract
	 * @param numContractTarget
	 * @param sheffName
	 * @param city
	 * @param requisitesCarrier
	 * @param dateOfAct
	 * @param numPage - принимаем начальное, или возвращенное колличество страниц
	 * @return колличество страниц.
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public int getActOfRoute(List<Route> routes, HttpServletRequest request, boolean isNDS, String dateContract,
			String numContractTarget, String sheffName, String city, String requisitesCarrier, String dateOfAct, int numPage) throws DocumentException, FileNotFoundException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		//загружаем шрифты
		String path = request.getServletContext().getRealPath("");
		com.itextpdf.text.Font fontMainHeader = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 8);
		com.itextpdf.text.Font fontMainText = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans.ttf", "cp1251", BaseFont.EMBEDDED, 6);
		com.itextpdf.text.Font fontForRequisitesBolt = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 5);
		com.itextpdf.text.Font fontMainTextBold = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 6);
//		com.itextpdf.text.Font fontWatermark = new Font(Font.FontFamily.HELVETICA, 34, Font.BOLD, new GrayColor(0.5f));
		System.err.println(path+"resources/others/");
		
		if(numPage == 0) { // самый первый прогон документа.
			Document document = new Document();
	        document.setPageSize(PageSize.A4.rotate()); // поворачиваем на альбомную ориентацию		         
	        
	        String fileName = routes.get(0).getUser().getCompanyName();
	        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path+"resources/others/"+fileName+".pdf"));		        
	        document.open();
	        
	        //формируем текс шапки
	        //Первая строка "Акт №5111551"
	        Paragraph p1 = null;
	        String numAct;
	        if (routes.size() == 1) {
	        	p1 = new Paragraph("Акт № "+routes.get(0).getIdRoute(),fontMainHeader);
				numAct = routes.get(0).getIdRoute().toString().trim();
			} else {
				p1 = new Paragraph("Акт № T"+routes.get(0).getIdRoute(),fontMainHeader);
				numAct = "T" + routes.get(0).getIdRoute();
			}
	        p1.setSpacingBefore(-30f);
	        p1.setAlignment(Element.ALIGN_CENTER);
	        document.add(p1);
	        
	        Paragraph p2 = new Paragraph("сдачи-приемки выполненных работ на оказание транспортных услуг",fontMainHeader);
	        p2.setAlignment(Element.ALIGN_CENTER);
	        document.add(p2);
	        
	        //третья строка шапки с городом и датой
	        Paragraph p3 = new Paragraph(dateOfAct+", г. "+city, fontMainHeader);
	        p3.setAlignment(Element.ALIGN_LEFT);
	        p3.setSpacingAfter(10f);
	        document.add(p3);
	        
	        Paragraph p4 = new Paragraph("       Мы, нижеподписавшиеся: представитель Перевозчика "
	        		+ routes.get(0).getUser().getCompanyName()
	        		+ ", в лице директора " + sheffName
	        		+ " действующего на основании Устава одной стороны, и представитель Заказчика  ЗАО «Доброном» в лице заместителя генерального директора по логистике Якубова Евгения Владимировича, действующего на основании доверенности № 8 от 20.12.2023 года, с другой стороны, составили настоящий акт о том, что услуги, оказанные на основании договора перевозки №"
	        		+ numContractTarget + " от " + dateContract
	        		+ " выполнены в полном объеме и стороны претензий друг к другу не имеют", fontMainText);
	        p4.setSpacingAfter(10f);
	        document.add(p4);
	        
	        //создаём таблицу, заполняем шапку таблицы       
	        
	        float [] pointColumnWidthsNDS = {180F, 180f, 150F, 600F, 190F, 180F, 450F, 140F, 175F, 175F, 180F, 180F}; //рa-ры колонок c НДС
	        float [] pointColumnWidths = {180F, 180f, 150F, 600F, 190F, 180F, 450F, 140F, 175F, 180F, 180F}; //рa-ры колонок БЕЗ НДС
	        PdfPTable table = null;
	        if(isNDS) {
	        	table = new PdfPTable(pointColumnWidthsNDS);
	        }else {
	        	table = new PdfPTable(pointColumnWidths);
	        }
	         
	        table.setTotalWidth(780f); // общий размер таблицы
	        table.setLockedWidth(true); // запрет изменения общего ра-ра табл.
	        table.addCell(new PdfPCell(new Phrase("Дата загрузки", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("Дата выгрузки", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("№ Рейса", fontMainTextBold)));
//	        table.addCell(new PdfPCell(new Phrase("Маршрут", fontMainText)));
	        PdfPCell routeCellHeader = new PdfPCell(new Phrase("Маршрут", fontMainTextBold));
	        routeCellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
	        table.addCell(routeCellHeader);
	        table.addCell(new PdfPCell(new Phrase("№ ТС", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("№ Путевого листа", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("№ ТТН/CMR", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("Объем Груза (тонн)", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("Сумма", fontMainTextBold)));
	        if(isNDS) {
	        	table.addCell(new PdfPCell(new Phrase("Сумма НДС", fontMainTextBold)));
	        }        
	        table.addCell(new PdfPCell(new Phrase("Платные дороги", fontMainTextBold)));
	        table.addCell(new PdfPCell(new Phrase("Итого", fontMainTextBold)));
	        
	        double cost = 0.0;
			double nds = 0.0;
			double way = 0.0;
			double costAndNdsValue = 0.0;
			String currency = routes.get(0).getStartCurrency();
			for (Route route : routes) {
				table.addCell(new PdfPCell(new Phrase(route.getDateLoadPreviously().format(formatter), fontMainText)));
		        table.addCell(new PdfPCell(new Phrase(route.getDateUnload(), fontMainText)));
		        table.addCell(new PdfPCell(new Phrase(route.getIdRoute().toString(), fontMainText)));
		        table.addCell(new PdfPCell(new Phrase(route.getRouteDirection(), fontMainText)));
		        if(route.getNumTruckAndTrailer() != null) {
		        	table.addCell(new PdfPCell(new Phrase(route.getNumTruckAndTrailer(), fontMainText)));
		        }else {
		        	table.addCell(new PdfPCell(new Phrase(route.getTruck().getNumTruck() + "/" + route.getTruck().getNumTrailer(), fontMainText)));
		        }
		        table.addCell(new PdfPCell(new Phrase(route.getNumWayList(), fontMainText)));
		        table.addCell(new PdfPCell(new Phrase(route.getCmr(), fontMainText)));
		        table.addCell(new PdfPCell(new Phrase(route.getCargoWeightForAct(), fontMainText)));
		        cost = cost + route.getFinishPrice();
		        table.addCell(new PdfPCell(new Phrase(route.getFinishPrice().toString(), fontMainText))); // финальная цена
		        if(isNDS) {
		        	double totalNDS = route.getFinishPrice() * 20.0 / 100.0;
					nds = nds + totalNDS;
					way = way + Double.parseDouble(route.getCostWay());
					table.addCell(new PdfPCell(new Phrase(totalNDS+"", fontMainText))); // сумма с НДС 
					table.addCell(new PdfPCell(new Phrase(roundВouble(Double.parseDouble(route.getCostWay()), 2)+"", fontMainText)));
					costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
							+ Double.parseDouble(route.getCostWay());
					Double costAndNDS = route.getFinishPrice() + totalNDS + roundВouble(Double.parseDouble(route.getCostWay()), 2);
			        table.addCell(new PdfPCell(new Phrase(costAndNDS.toString(), fontMainText)));
		        }else {
		        	double totalNDS = 0.0;
					way = way + Double.parseDouble(route.getCostWay());
					nds = nds + totalNDS; 
		        	table.addCell(new PdfPCell(new Phrase(roundВouble(Double.parseDouble(route.getCostWay()), 2)+"", fontMainText)));
		        	costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
							+ Double.parseDouble(route.getCostWay());
		        	Double costAndNDS = route.getFinishPrice() + roundВouble(totalNDS, 2)+ roundВouble(Double.parseDouble(route.getCostWay()), 2);
			        table.addCell(new PdfPCell(new Phrase(costAndNDS.toString(), fontMainText)));
		        }
			}
			document.add(table);
			
			//тут идёт проверка, а не занимает ли акт, с реквизитами больше одного листа
	        if(pdfWriter.getPageNumber() > 1) {
	        	return pdfWriter.getPageNumber();
	        }
			
	        //создаём сводную строку "Услуги экспедитора"
	        
	        
			//создём сводную строку
			float [] pointColumnWidthsBottomNDS = {2070F, 175F, 175F, 180F, 180F};
			float [] pointColumnWidthsBottom = {2070F, 175F, 180F, 180F};
			PdfPTable tableBottom = null;
			if(isNDS) {
				tableBottom = new PdfPTable(pointColumnWidthsBottomNDS);
			}else {
				tableBottom = new PdfPTable(pointColumnWidthsBottom);
			}
	        tableBottom.setTotalWidth(780f);
	        tableBottom.setLockedWidth(true);
	        tableBottom.addCell(new PdfPCell(new Phrase("Итого:", fontMainTextBold)));
	        tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(cost, 2)+"", fontMainTextBold)));
	        if(isNDS) {
	        	tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(nds, 2)+"", fontMainTextBold)));
	            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(way, 2)+"", fontMainTextBold)));
	            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(costAndNdsValue, 2)+"", fontMainTextBold)));
	        }else {
	            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(way, 2)+"", fontMainTextBold)));
	            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(costAndNdsValue, 2)+"", fontMainTextBold)));
	        }        
	        document.add(tableBottom);
	        
	        //создаём тескт под таблицей
	        double allCost = 0.0;
	        if(isNDS) {
	        	Paragraph p8 = new Paragraph("В том числе НДС: " + new FwMoney(roundВouble(nds, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
	            p8.setSpacingBefore(10f);
	            document.add(p8);
	            allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
	            Paragraph p9 = new Paragraph("Всего оказано услуг на сумму с НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
	            document.add(p9);
	        }else {
	        	allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
	        	Paragraph p8 = new Paragraph("Всего оказано услуг на сумму без НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
	            p8.setSpacingBefore(10f);
	            document.add(p8);
	            allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
	        }
	        
	      //вставляем QR код справа, где пока что номер акта и зашифрованная итоговая цена
	        int minValue = 1;
			int maxValue = 10000000;
			int randomValue = minValue + (int) (Math.random() * (maxValue - minValue + 1));
	        String QRBody = numAct+randomValue;
	        BarcodeQRCode qrcode = new BarcodeQRCode(QRBody, 1, 1, null);
	        Image qrcodeImage = qrcode.getImage();
	        qrcodeImage.setAbsolutePosition(750, 515);
	        qrcodeImage.setSpacingAfter(10f);
	        qrcodeImage.scalePercent(100);	
	        document.add(qrcodeImage);
	        
	        // вставляем футер
	        addFooterHasAct(document, fontForRequisitesBolt, requisitesCarrier, sheffName, routes.get(0));
	        
	        //тут идёт проверка, а не занимает ли акт, с реквизитами больше одного листа
	        if(pdfWriter.getPageNumber() > 1) {
	        	return pdfWriter.getPageNumber();
	        }		       
	        
	        document.addTitle("Акт выполненных работ");
	        document.addKeywords("Java, PDF, iText");
	        document.addCreator("SpeedLogist");
	        document.addAuthor("5596487735");
	        
	        System.out.println(pdfWriter.getPageNumber() + " - getPageNumber()");
	        System.out.println("======================");
	        document.close();    

	        Act act = new Act();
			act.setNumAct(numAct);
			act.setSecretCode(QRBody);
			String idRoutes = "";
			for (Route route : routes) {
				idRoutes = idRoutes + route.getIdRoute().toString().trim() + ";";
			}
			act.setIdRoutes(idRoutes);
			act.setFinalCost(cost + way);
			act.setNds(nds);
			act.setStatus("1");
			act.setCurrency(currency);
			act.setDate(LocalDate.now());
			act.setTime(LocalDateTime.now().format(formatter2).toString());
			actService.saveOrUpdateAct(act);
	        return 1;
		}
		
		if(numPage > 1) { // второй прогон, где нужно разбить документ на ДВА АКТА!
			int trueNumPage = numPage;
			
			//шапка, текст и прочая хуйня
			Document document = new Document();
	        document.setPageSize(PageSize.A4.rotate()); // поворачиваем на альбомную ориентацию		         
	        
	        String fileName = routes.get(0).getUser().getCompanyName();
	        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path+"resources/others/"+fileName+".pdf"));		        
	        document.open();
	        String numAct = null;
	        for (int i = 1; i <= trueNumPage; i++) {	        	
				if(i==1) {//создём первую страницу
					//формируем текс шапки
			        //Первая строка "Акт №5111551"
			        Paragraph p1 = null;
			        
			        if (routes.size() == 1) {
			        	p1 = new Paragraph("Акт № "+routes.get(0).getIdRoute(),fontMainHeader);
						numAct = routes.get(0).getIdRoute().toString().trim();
					} else {
						p1 = new Paragraph("Акт № T"+routes.get(0).getIdRoute(),fontMainHeader);
						numAct = "T" + routes.get(0).getIdRoute();
					}
			        p1.setSpacingBefore(-30f);
			        p1.setAlignment(Element.ALIGN_CENTER);
			        document.add(p1);
			        
			        Paragraph p2 = new Paragraph("сдачи-приемки выполненных работ на оказание транспортных услуг",fontMainHeader);
			        p2.setAlignment(Element.ALIGN_CENTER);
			        document.add(p2);
			        
			        //третья строка шапки с городом и датой
			        Paragraph p3 = new Paragraph(dateOfAct+", г. "+city, fontMainHeader);
			        p3.setAlignment(Element.ALIGN_LEFT);
			        p3.setSpacingAfter(10f);
			        document.add(p3);
			        
			        Paragraph p4 = new Paragraph("       Мы, нижеподписавшиеся: представитель Перевозчика "
			        		+ routes.get(0).getUser().getCompanyName()
			        		+ ", в лице директора " + sheffName
			        		+ " действующего на основании Устава одной стороны, и представитель Заказчика  ЗАО «Доброном» в лице заместителя генерального директора по логистике Якубова Евгения Владимировича, действующего на основании доверенности № 8 от 20.12.2023 года, с другой стороны, составили настоящий акт о том, что услуги, оказанные на основании договора перевозки №"
			        		+ numContractTarget + " от " + dateContract
			        		+ " выполнены в полном объеме и стороны претензий друг к другу не имеют", fontMainText);
			        p4.setSpacingAfter(10f);
			        document.add(p4);
				}
				
				//создаём таблицу, заполняем шапку таблицы       
				double cost = 0.0;
				double nds = 0.0;
				double way = 0.0;
				double costAndNdsValue = 0.0;
		        float [] pointColumnWidthsNDS = {180F, 180f, 150F, 600F, 190F, 180F, 450F, 140F, 175F, 175F, 180F, 180F}; //рa-ры колонок c НДС
		        float [] pointColumnWidths = {180F, 180f, 150F, 600F, 190F, 180F, 450F, 140F, 175F, 180F, 180F}; //рa-ры колонок БЕЗ НДС
		        PdfPTable table = null;
		        if(isNDS) {
		        	table = new PdfPTable(pointColumnWidthsNDS);
		        }else {
		        	table = new PdfPTable(pointColumnWidths);
		        }
		         
		        table.setTotalWidth(780f); // общий размер таблицы
		        table.setLockedWidth(true); // запрет изменения общего ра-ра табл.
		        table.addCell(new PdfPCell(new Phrase("Дата загрузки", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("Дата выгрузки", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("№ Рейса", fontMainTextBold)));
//		        table.addCell(new PdfPCell(new Phrase("Маршрут", fontMainText)));
		        PdfPCell routeCellHeader = new PdfPCell(new Phrase("Маршрут", fontMainTextBold));
		        routeCellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
		        table.addCell(routeCellHeader);
		        table.addCell(new PdfPCell(new Phrase("№ ТС", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("№ Путевого листа", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("№ ТТН/CMR", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("Объем Груза (тонн)", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("Сумма c НДС", fontMainTextBold)));
		        if(isNDS) {
		        	table.addCell(new PdfPCell(new Phrase("Сумма НДС", fontMainTextBold)));
		        }        
		        table.addCell(new PdfPCell(new Phrase("Платные дороги", fontMainTextBold)));
		        table.addCell(new PdfPCell(new Phrase("Итого", fontMainTextBold)));
		        
		        
				String currency = routes.get(0).getStartCurrency();
				int firstTable = routes.size()/trueNumPage; // по сути строки таблицы
				if(i==1) {
					//определяем и заполняем первую таблицу на основной страинце					
					for (int j = 0; j < firstTable; j++) {
						Route route = routes.get(j);
						table.addCell(new PdfPCell(new Phrase(route.getDateLoadPreviously().format(formatter), fontMainText)));
				        table.addCell(new PdfPCell(new Phrase(route.getDateUnload(), fontMainText)));
				        table.addCell(new PdfPCell(new Phrase(route.getIdRoute().toString(), fontMainText)));
				        table.addCell(new PdfPCell(new Phrase(route.getRouteDirection(), fontMainText)));
				        if(route.getNumTruckAndTrailer() != null) {
				        	table.addCell(new PdfPCell(new Phrase(route.getNumTruckAndTrailer(), fontMainText)));
				        }else {
				        	table.addCell(new PdfPCell(new Phrase(route.getTruck().getNumTruck() + "/" + route.getTruck().getNumTrailer(), fontMainText)));
				        }
				        table.addCell(new PdfPCell(new Phrase(route.getNumWayList(), fontMainText)));
				        table.addCell(new PdfPCell(new Phrase(route.getCmr(), fontMainText)));
				        table.addCell(new PdfPCell(new Phrase(route.getCargoWeightForAct(), fontMainText)));
				        cost = cost + route.getFinishPrice();
				        table.addCell(new PdfPCell(new Phrase(route.getFinishPrice().toString(), fontMainText))); // финальная цена
				        if(isNDS) {
				        	double totalNDS = route.getFinishPrice() * 20.0 / 100.0;
							nds = nds + totalNDS;
							way = way + Double.parseDouble(route.getCostWay());
							table.addCell(new PdfPCell(new Phrase(totalNDS+"", fontMainText))); // сумма с НДС 
							table.addCell(new PdfPCell(new Phrase(roundВouble(Double.parseDouble(route.getCostWay()), 2)+"", fontMainText)));
							costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
									+ Double.parseDouble(route.getCostWay());
							Double costAndNDS = route.getFinishPrice() + totalNDS + roundВouble(Double.parseDouble(route.getCostWay()), 2);
					        table.addCell(new PdfPCell(new Phrase(costAndNDS.toString(), fontMainText)));
				        }else {
				        	double totalNDS = 0.0;
							way = way + Double.parseDouble(route.getCostWay());
							nds = nds + totalNDS; 
				        	table.addCell(new PdfPCell(new Phrase(roundВouble(Double.parseDouble(route.getCostWay()), 2)+"", fontMainText)));
				        	costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
									+ Double.parseDouble(route.getCostWay());
				        	Double costAndNDS = route.getFinishPrice() + roundВouble(totalNDS, 2)+ roundВouble(Double.parseDouble(route.getCostWay()), 2);
					        table.addCell(new PdfPCell(new Phrase(costAndNDS.toString(), fontMainText)));
				        }				        
					}
					document.add(table);
					
					//создём сводную строку
					float [] pointColumnWidthsBottomNDS = {2070F, 175F, 175F, 180F, 180F};
					float [] pointColumnWidthsBottom = {2070F, 175F, 180F, 180F};
					PdfPTable tableBottom = null;
					if(isNDS) {
						tableBottom = new PdfPTable(pointColumnWidthsBottomNDS);
					}else {
						tableBottom = new PdfPTable(pointColumnWidthsBottom);
					}
			        tableBottom.setTotalWidth(780f);
			        tableBottom.setLockedWidth(true);
			        tableBottom.addCell(new PdfPCell(new Phrase("Итого:", fontMainTextBold)));
			        tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(cost, 2)+"", fontMainTextBold)));
			        if(isNDS) {
			        	tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(nds, 2)+"", fontMainTextBold)));
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(way, 2)+"", fontMainTextBold)));
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(costAndNdsValue, 2)+"", fontMainTextBold)));
			        }else {
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(way, 2)+"", fontMainTextBold)));
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(costAndNdsValue, 2)+"", fontMainTextBold)));
			        }        
			        document.add(tableBottom);
			        
			        //создаём тескт под таблицей
			        double allCost = 0.0;
			        if(isNDS) {
			        	Paragraph p8 = new Paragraph("В том числе НДС: " + new FwMoney(roundВouble(nds, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
			            p8.setSpacingBefore(10f);
			            document.add(p8);
			            allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
			            Paragraph p9 = new Paragraph("Всего оказано услуг на сумму с НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
			            document.add(p9);
			        }else {
			        	allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
			        	Paragraph p8 = new Paragraph("Всего оказано услуг на сумму без НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
			            p8.setSpacingBefore(10f);
			            document.add(p8);
			            allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
			        }
			        
			      //вставляем QR код справа, где пока что номер акта и зашифрованная итоговая цена
			        int minValue = 1;
					int maxValue = 10000000;
					int randomValue = minValue + (int) (Math.random() * (maxValue - minValue + 1));
			        String QRBody = numAct+randomValue;
			        BarcodeQRCode qrcode = new BarcodeQRCode(QRBody, 1, 1, null);
			        Image qrcodeImage = qrcode.getImage();
			        qrcodeImage.setAbsolutePosition(750, 515);
			        qrcodeImage.setSpacingAfter(10f);
			        qrcodeImage.scalePercent(100);	
			        document.add(qrcodeImage);
			        
			        //вставляем футер
					addFooterHasAct(document, fontForRequisitesBolt, requisitesCarrier, sheffName, routes.get(0));
					
					//сохраняем акт
					Act act = new Act();
					act.setSecretCode(QRBody);
					act.setNumAct(numAct);
					String idRoutes = "";
					for (Route route : routes) {
						idRoutes = idRoutes + route.getIdRoute().toString().trim() + ";";
					}
					act.setIdRoutes(idRoutes);
					act.setFinalCost(cost + way);
					act.setNds(nds);
					act.setStatus("1");
					act.setCurrency(currency);
					act.setDate(LocalDate.now());
					act.setTime(LocalDateTime.now().format(formatter2).toString());
					actService.saveOrUpdateAct(act);
					
					//обнуляем общие значения
					cost = 0.0;
					nds = 0.0;
					way = 0.0;
					costAndNdsValue = 0.0;
					
					//первая страница готова
					document.newPage();
				}//закончили формировать первую страницу
				else if(i==trueNumPage) {//создаём последнюю стрианицу
					
					//формируем текс шапки
			        //Первая строка "Акт №5111551"
			        Paragraph p1 = null;
			        
			        if (routes.size() == 1) {
			        	p1 = new Paragraph("Акт № "+routes.get(firstTable).getIdRoute(),fontMainHeader);
						numAct = routes.get(firstTable).getIdRoute().toString().trim();
					} else {
						p1 = new Paragraph("Акт № T"+routes.get(firstTable).getIdRoute(),fontMainHeader);
						numAct = "T" + routes.get(firstTable).getIdRoute();
					}
			        p1.setSpacingBefore(-30f);
			        p1.setAlignment(Element.ALIGN_CENTER);
			        document.add(p1);
			        
			        Paragraph p2 = new Paragraph("сдачи-приемки выполненных работ на оказание транспортных услуг",fontMainHeader);
			        p2.setAlignment(Element.ALIGN_CENTER);
			        document.add(p2);
			        
			        //третья строка шапки с городом и датой
			        Paragraph p3 = new Paragraph(dateOfAct+", г. "+city, fontMainHeader);
			        p3.setAlignment(Element.ALIGN_LEFT);
			        p3.setSpacingAfter(10f);
			        document.add(p3);
			        
			        Paragraph p4 = new Paragraph("       Мы, нижеподписавшиеся: представитель Перевозчика "
			        		+ routes.get(0).getUser().getCompanyName()
			        		+ ", в лице директора " + sheffName
			        		+ " действующего на основании Устава одной стороны, и представитель Заказчика  ЗАО «Доброном» в лице заместителя генерального директора по логистике Якубова Евгения Владимировича, действующего на основании доверенности № 8 от 20.12.2023 года, с другой стороны, составили настоящий акт о том, что услуги, оказанные на основании договора перевозки №"
			        		+ numContractTarget + " от " + dateContract
			        		+ " выполнены в полном объеме и стороны претензий друг к другу не имеют", fontMainText);
			        p4.setSpacingAfter(10f);
			        document.add(p4);
					//определяем и заполняем вторую таблицу на второй странице
					int secondTable = routes.size()-firstTable; // по сути строки таблицы
					//создаём таблицу, заполняем шапку таблицы       
					
			        PdfPTable tableSecond = null;
			        if(isNDS) {
			        	tableSecond = new PdfPTable(pointColumnWidthsNDS);
			        }else {
			        	tableSecond = new PdfPTable(pointColumnWidths);
			        }
			         
			        tableSecond.setTotalWidth(780f); // общий размер таблицы
			        tableSecond.setLockedWidth(true); // запрет изменения общего ра-ра табл.
			        tableSecond.addCell(new PdfPCell(new Phrase("Дата загрузки", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("Дата выгрузки", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("№ Рейса", fontMainTextBold)));
//			        tableSecond.addCell(new PdfPCell(new Phrase("Маршрут", fontMainText)));
			        PdfPCell routeCellHeaderSecond = new PdfPCell(new Phrase("Маршрут", fontMainTextBold));
			        routeCellHeaderSecond.setHorizontalAlignment(Element.ALIGN_CENTER);
			        tableSecond.addCell(routeCellHeaderSecond);
			        tableSecond.addCell(new PdfPCell(new Phrase("№ ТС", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("№ Путевого листа", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("№ ТТН/CMR", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("Объем Груза (тонн)", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("Сумма c НДС", fontMainTextBold)));
			        if(isNDS) {
			        	tableSecond.addCell(new PdfPCell(new Phrase("Сумма НДС", fontMainTextBold)));
			        }        
			        tableSecond.addCell(new PdfPCell(new Phrase("Платные дороги", fontMainTextBold)));
			        tableSecond.addCell(new PdfPCell(new Phrase("Итого", fontMainTextBold)));
					
			      //определяем и заполняем первую таблицу на основной страинце					
					for (int j = firstTable; j < routes.size(); j++) {
						Route route = routes.get(j);
						tableSecond.addCell(new PdfPCell(new Phrase(route.getDateLoadPreviously().format(formatter), fontMainText)));
						tableSecond.addCell(new PdfPCell(new Phrase(route.getDateUnload(), fontMainText)));
						tableSecond.addCell(new PdfPCell(new Phrase(route.getIdRoute().toString(), fontMainText)));
						tableSecond.addCell(new PdfPCell(new Phrase(route.getRouteDirection(), fontMainText)));
				        if(route.getNumTruckAndTrailer() != null) {
				        	tableSecond.addCell(new PdfPCell(new Phrase(route.getNumTruckAndTrailer(), fontMainText)));
				        }else {
				        	tableSecond.addCell(new PdfPCell(new Phrase(route.getTruck().getNumTruck() + "/" + route.getTruck().getNumTrailer(), fontMainText)));
				        }
				        tableSecond.addCell(new PdfPCell(new Phrase(route.getNumWayList(), fontMainText)));
				        tableSecond.addCell(new PdfPCell(new Phrase(route.getCmr(), fontMainText)));
				        tableSecond.addCell(new PdfPCell(new Phrase(route.getCargoWeightForAct(), fontMainText)));
				        cost = cost + route.getFinishPrice();
				        tableSecond.addCell(new PdfPCell(new Phrase(route.getFinishPrice().toString(), fontMainText))); // финальная цена
				        if(isNDS) {
				        	double totalNDS = route.getFinishPrice() * 20.0 / 100.0;
							nds = nds + totalNDS;
							way = way + Double.parseDouble(route.getCostWay());
							tableSecond.addCell(new PdfPCell(new Phrase(totalNDS+"", fontMainText))); // сумма с НДС 
							tableSecond.addCell(new PdfPCell(new Phrase(roundВouble(Double.parseDouble(route.getCostWay()), 2)+"", fontMainText)));
							costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
									+ Double.parseDouble(route.getCostWay());
							Double costAndNDS = route.getFinishPrice() + totalNDS + roundВouble(Double.parseDouble(route.getCostWay()), 2);
							tableSecond.addCell(new PdfPCell(new Phrase(costAndNDS.toString(), fontMainText)));
				        }else {
				        	double totalNDS = 0.0;
							way = way + Double.parseDouble(route.getCostWay());
							nds = nds + totalNDS; 
							tableSecond.addCell(new PdfPCell(new Phrase(roundВouble(Double.parseDouble(route.getCostWay()), 2)+"", fontMainText)));
				        	costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
									+ Double.parseDouble(route.getCostWay());
				        	Double costAndNDS = route.getFinishPrice() + roundВouble(totalNDS, 2)+ roundВouble(Double.parseDouble(route.getCostWay()), 2);
				        	tableSecond.addCell(new PdfPCell(new Phrase(costAndNDS.toString(), fontMainText)));
				        }				        
					}
					document.add(tableSecond);
					
					//тут идёт проверка, а не занимает ли акт, с реквизитами больше одного листа
			        if(pdfWriter.getPageNumber() > 2) {
			        	return pdfWriter.getPageNumber();
			        }
					
					//создём сводную строку
					float [] pointColumnWidthsBottomNDS = {2070F, 175F, 175F, 180F, 180F};
					float [] pointColumnWidthsBottom = {2070F, 175F, 180F, 180F};
					PdfPTable tableBottom = null;
					if(isNDS) {
						tableBottom = new PdfPTable(pointColumnWidthsBottomNDS);
					}else {
						tableBottom = new PdfPTable(pointColumnWidthsBottom);
					}
			        tableBottom.setTotalWidth(780f);
			        tableBottom.setLockedWidth(true);
			        tableBottom.addCell(new PdfPCell(new Phrase("Итого:", fontMainTextBold)));
			        tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(cost, 2)+"", fontMainTextBold)));
			        if(isNDS) {
			        	tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(nds, 2)+"", fontMainTextBold)));
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(way, 2)+"", fontMainTextBold)));
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(costAndNdsValue, 2)+"", fontMainTextBold)));
			        }else {
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(way, 2)+"", fontMainTextBold)));
			            tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(costAndNdsValue, 2)+"", fontMainTextBold)));
			        }        
			        document.add(tableBottom);
			        
			        //создаём тескт под таблицей
			        double allCost = 0.0;
			        if(isNDS) {
			        	Paragraph p8 = new Paragraph("В том числе НДС: " + new FwMoney(roundВouble(nds, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
			            p8.setSpacingBefore(10f);
			            document.add(p8);
			            allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
			            Paragraph p9 = new Paragraph("Всего оказано услуг на сумму с НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
			            document.add(p9);
			        }else {
			        	allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
			        	Paragraph p8 = new Paragraph("Всего оказано услуг на сумму без НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
			            p8.setSpacingBefore(10f);
			            document.add(p8);
			            allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
			        }
			        
			      //вставляем QR код справа, где пока что номер акта и зашифрованная итоговая цена
			        int minValue = 1;
					int maxValue = 10000000;
					int randomValue = minValue + (int) (Math.random() * (maxValue - minValue + 1));
			        String QRBody = numAct+randomValue;
			        BarcodeQRCode qrcode = new BarcodeQRCode(QRBody, 1, 1, null);
			        Image qrcodeImage = qrcode.getImage();
			        qrcodeImage.setAbsolutePosition(750, 515);
			        qrcodeImage.setSpacingAfter(10f);
			        qrcodeImage.scalePercent(100);	
			        document.add(qrcodeImage);
			        		        
			      	// вставляем футер
			        addFooterHasAct(document, fontForRequisitesBolt, requisitesCarrier, sheffName, routes.get(0));
			        
			        //тут идёт проверка, а не занимает ли акт, с реквизитами больше одного листа
			        if(pdfWriter.getPageNumber() > 2) {
			        	return pdfWriter.getPageNumber();
			        }		       
			        
			        document.addTitle("Акт выполненных работ");
			        document.addKeywords("Java, PDF, iText");
			        document.addCreator("SpeedLogist");
			        document.addAuthor("5596487735");
			        
			        System.out.println(pdfWriter.getPageNumber() + " - getPageNumber()");
			        System.out.println("======================");
			        document.close();
			        Act act = new Act();
			        act.setSecretCode(QRBody);
					act.setNumAct(numAct);
					String idRoutes = "";
					for (Route route : routes) {
						idRoutes = idRoutes + route.getIdRoute().toString().trim() + ";";
					}
					act.setIdRoutes(idRoutes);
					act.setFinalCost(cost + way);
					act.setNds(nds);
					act.setStatus("1");
					act.setCurrency(currency);
					act.setDate(LocalDate.now());
					act.setTime(LocalDateTime.now().format(formatter2).toString());
					actService.saveOrUpdateAct(act);
				}
				
			}			
			document.close();
			
		}
		return 1;
	}
	
	/**
	 *	Метод отвечает за формирование акта <b>для экспедиций импорт</b> в формате PDF
	 * <br>Возвращает колличество листов, занимаемым документом.
	 * <br>Логика такая: про первичном формировании акта, метод проверяет сколько листов занимает акт.
	 * <br>Если занимает один лист - акт выплёвывается.
	 * <br>Если занимает больше - метод перестраивается одностительно колличества листов в акте. 
	 * <br>Важно то, что колличество листов передаётся в метод, т.е. оно берется за основу этого метода.
	 * <br><b>Важно: первый раз в свойства метода вписывается 0 (нуль)<b>
	 * @param routes
	 * @param request
	 * @param isNDS
	 * @param dateContract
	 * @param numContractTarget
	 * @param sheffName
	 * @param city
	 * @param requisitesCarrier
	 * @param dateOfAct
	 * @param numPage
	 * @return
	 * @throws DocumentException
	 * @throws FileNotFoundException
	 */
	public int getActOfRouteExpedition(List<Route> routes, HttpServletRequest request, boolean isNDS, String dateContract,
			String numContractTarget, String sheffName, String city, String requisitesCarrier, String dateOfAct, int numPage) throws DocumentException, FileNotFoundException {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");
		//загружаем шрифты
		String path = request.getServletContext().getRealPath("");
		com.itextpdf.text.Font fontMainHeader = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 8);
		com.itextpdf.text.Font fontMainText = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans.ttf", "cp1251", BaseFont.EMBEDDED, 6);
		com.itextpdf.text.Font fontForRequisitesBolt = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 5);
		com.itextpdf.text.Font fontMainTextBold = FontFactory.getFont(path + "resources/others/fonts/DejaVuSans-Bold.ttf", "cp1251", BaseFont.EMBEDDED, 6);
//		com.itextpdf.text.Font fontWatermark = new Font(Font.FontFamily.HELVETICA, 34, Font.BOLD, new GrayColor(0.5f));
		System.err.println(path+"resources/others/");
		
		Document document = new Document();
        document.setPageSize(PageSize.A4.rotate()); // поворачиваем на альбомную ориентацию		
        
        
        String fileName = routes.get(0).getUser().getCompanyName();
        PdfWriter pdfWriter = PdfWriter.getInstance(document, new FileOutputStream(path+"resources/others/"+fileName+".pdf"));		        
        document.open(); 
        
        //формируем текс шапки
        //Первая строка "Акт №5111551"
        Paragraph p1 = null;
        String numAct;
        if (routes.size() == 1) {
        	p1 = new Paragraph("Акт № "+routes.get(0).getIdRoute(),fontMainHeader);
			numAct = routes.get(0).getIdRoute().toString().trim();
		} else {
			p1 = new Paragraph("Акт № T"+routes.get(0).getIdRoute(),fontMainHeader);
			numAct = "T" + routes.get(0).getIdRoute();
		}
        p1.setSpacingBefore(-30f);
        p1.setAlignment(Element.ALIGN_CENTER);
        document.add(p1);
        
        Paragraph p2 = new Paragraph("сдачи-приемки выполненных работ на оказание транспортных услуг",fontMainHeader);
        p2.setAlignment(Element.ALIGN_CENTER);
        document.add(p2);
        
        //третья строка шапки с городом и датой
        Paragraph p3 = new Paragraph(dateOfAct+", г. "+city, fontMainHeader);
        p3.setAlignment(Element.ALIGN_LEFT);
        p3.setSpacingAfter(10f);
        document.add(p3);
        
        Paragraph p4 = new Paragraph("       Мы, нижеподписавшиеся: представитель Перевозчика "
        		+ routes.get(0).getUser().getCompanyName()
        		+ ", в лице директора " + sheffName
        		+ " действующего на основании Устава одной стороны, и представитель Заказчика  ЗАО «Доброном» в лице заместителя генерального директора по логистике Якубова Евгения Владимировича, действующего на основании доверенности № 8 от 20.12.2023 года, с другой стороны, составили настоящий акт о том, что услуги, оказанные на основании договора перевозки №"
        		+ numContractTarget + " от " + dateContract
        		+ " выполнены в полном объеме и стороны претензий друг к другу не имеют", fontMainText);
        p4.setSpacingAfter(10f);
        document.add(p4);
        
        //создаём таблицу, заполняем шапку таблицы       
        
        
        float [] pointColumnWidths = {180F, 180f, 150F, 600F, 190F, 180F, 450F, 140F, 175F, 180F, 180F}; //рa-ры колонок БЕЗ НДС
        PdfPTable table = null;
        table = new PdfPTable(pointColumnWidths);
         
        table.setTotalWidth(780f); // общий размер таблицы
        table.setLockedWidth(true); // запрет изменения общего ра-ра табл.
        table.addCell(new PdfPCell(new Phrase("Дата загрузки", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("Дата выгрузки", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("№ Рейса", fontMainTextBold)));
//        table.addCell(new PdfPCell(new Phrase("Маршрут", fontMainText)));
        PdfPCell routeCellHeader = new PdfPCell(new Phrase("Маршрут", fontMainTextBold));
        routeCellHeader.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(routeCellHeader);
        table.addCell(new PdfPCell(new Phrase("№ ТС", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("№ Путевого листа", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("№ ТТН/CMR", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("Объем Груза (тонн)", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("Сумма", fontMainTextBold)));        
        table.addCell(new PdfPCell(new Phrase("Ставка НДС, 0%", fontMainTextBold)));
        table.addCell(new PdfPCell(new Phrase("Сумма без НДС", fontMainTextBold)));
        
        double cost = 0.0;
		double nds = 0.0;
		double way = 0.0;
		double expeditionCost = 0.0;
		double costAndNdsValue = 0.0;
		String currency = routes.get(0).getStartCurrency();
		for (Route route : routes) {
			table.addCell(new PdfPCell(new Phrase(route.getDateLoadPreviously().format(formatter), fontMainText)));
	        table.addCell(new PdfPCell(new Phrase(route.getDateUnload(), fontMainText)));
	        table.addCell(new PdfPCell(new Phrase(route.getIdRoute().toString(), fontMainText)));
	        table.addCell(new PdfPCell(new Phrase(route.getRouteDirection(), fontMainText)));
	        if(route.getNumTruckAndTrailer() != null) {
	        	table.addCell(new PdfPCell(new Phrase(route.getNumTruckAndTrailer(), fontMainText)));
	        }else {
	        	table.addCell(new PdfPCell(new Phrase(route.getTruck().getNumTruck() + "/" + route.getTruck().getNumTrailer(), fontMainText)));
	        }
	        table.addCell(new PdfPCell(new Phrase(route.getNumWayList(), fontMainText)));
	        table.addCell(new PdfPCell(new Phrase(route.getCmr(), fontMainText)));
	        table.addCell(new PdfPCell(new Phrase(route.getCargoWeightForAct(), fontMainText)));
	        cost = cost + route.getFinishPrice();
	        expeditionCost = expeditionCost + Double.parseDouble(route.getExpeditionCost().toString());
	        Integer price = route.getFinishPrice() - route.getExpeditionCost();
	        table.addCell(new PdfPCell(new Phrase(price.toString(), fontMainText))); // финальная цена
	        double totalNDS = 0.0;
			way = way + Double.parseDouble(route.getCostWay());
			nds = nds + totalNDS; 
        	table.addCell(new PdfPCell(new Phrase("0.0%", fontMainText)));
        	costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
					+ Double.parseDouble(route.getCostWay());
        	Double costAndNDS = route.getFinishPrice() + roundВouble(totalNDS, 2)+ roundВouble(Double.parseDouble(route.getCostWay()), 2);
	        table.addCell(new PdfPCell(new Phrase(price.toString(), fontMainText)));
		}
		document.add(table);
		
		//тут идёт проверка, а не занимает ли акт, с реквизитами больше одного листа
        if(pdfWriter.getPageNumber() > 1) {
        	return pdfWriter.getPageNumber();
        }
		
		float [] pointColumnWidthsBottom = {2070F, 175F, 180F, 180F};
		
		//создаём сводную строку "Услуги экспедитора"
		PdfPTable tableExpedition = null;
		tableExpedition = new PdfPTable(pointColumnWidthsBottom);
		tableExpedition.setTotalWidth(780f);
		tableExpedition.setLockedWidth(true);
		tableExpedition.addCell(new PdfPCell(new Phrase("Услуги экспедитора:", fontMainTextBold)));
		tableExpedition.addCell(new PdfPCell(new Phrase(roundВouble(expeditionCost, 2)+"", fontMainTextBold))); // остановился тут
       
		tableExpedition.addCell(new PdfPCell(new Phrase(roundВouble(0.0, 2)+"%", fontMainTextBold)));
    	tableExpedition.addCell(new PdfPCell(new Phrase(roundВouble(expeditionCost, 2)+"", fontMainTextBold)));
    	
        document.add(tableExpedition);
        
		//создём сводную строку
		PdfPTable tableBottom = null;
		tableBottom = new PdfPTable(pointColumnWidthsBottom);
        tableBottom.setTotalWidth(780f);
        tableBottom.setLockedWidth(true);
        tableBottom.addCell(new PdfPCell(new Phrase("Итого:", fontMainTextBold)));
        tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(cost, 2)+"", fontMainTextBold)));
        tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(0.0, 2)+"%", fontMainTextBold)));
        tableBottom.addCell(new PdfPCell(new Phrase(roundВouble(cost, 2)+"", fontMainTextBold)));       
        document.add(tableBottom);
        
        //создаём тескт под таблицей
        double allCost = 0.0;
        allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
    	Paragraph p8 = new Paragraph("Всего оказано услуг на сумму без НДС: " + new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str(), fontMainText);
        p8.setSpacingBefore(10f);
        document.add(p8);
        allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
        
        Paragraph infoText = new Paragraph("Настоящий акт составлен в двух экземплярах,  служит основанием для проведения расчетов Заказчика и Перевозчика и является одновременно протоколом согласования стоимости транспортных услуг.", fontMainText);
        p8.setSpacingBefore(10f);
        document.add(infoText);
        
      //вставляем QR код справа, где пока что номер акта и зашифрованная итоговая цена
        int minValue = 1;
		int maxValue = 10000000;
		int randomValue = minValue + (int) (Math.random() * (maxValue - minValue + 1));
        String QRBody = numAct+randomValue;
        BarcodeQRCode qrcode = new BarcodeQRCode(QRBody, 1, 1, null);
        Image qrcodeImage = qrcode.getImage();
        qrcodeImage.setAbsolutePosition(750, 515);
        qrcodeImage.setSpacingAfter(10f);
        qrcodeImage.scalePercent(100);	
        document.add(qrcodeImage);
        
        // вставляем футер
        addFooterHasAct(document, fontForRequisitesBolt, requisitesCarrier, sheffName, routes.get(0));
        
        //тут идёт проверка, а не занимает ли акт, с реквизитами больше одного листа
        if(pdfWriter.getPageNumber() > 1) {
        	return pdfWriter.getPageNumber();
        }		       
        
        document.addTitle("Акт выполненных работ");
        document.addKeywords("Java, PDF, iText");
        document.addCreator("SpeedLogist");
        document.addAuthor("Expedition");
              
        
        System.out.println(pdfWriter.getPageNumber() + " - getPageNumber()");
        System.out.println("======================");
        document.close();    
        
        	        
        Act act = new Act();
		act.setNumAct(numAct);
		act.setSecretCode(QRBody);
		String idRoutes = "";
		for (Route route : routes) {
			idRoutes = idRoutes + route.getIdRoute().toString().trim() + ";";
		}
		act.setIdRoutes(idRoutes);
		act.setFinalCost(cost + way);
		act.setNds(nds);
		act.setStatus("1");
		act.setCurrency(currency);
		act.setDate(LocalDate.now());
		act.setTime(LocalDateTime.now().format(formatter2).toString());
		actService.saveOrUpdateAct(act);
        return 1;
	}
	

	
	/**
	 * метод вставляет реквизиты в акт
	 * @param document
	 * @param fontMainText
	 * @param requisitesCarrier
	 * @param sheffName
	 * @throws DocumentException
	 */
	private void addFooterHasAct(Document document, com.itextpdf.text.Font fontForRequisites, String requisitesCarrier, String sheffName, Route route) throws DocumentException {
		//тут вставляются реквизиты свои
		Paragraph p11;
		// временно сделал так, что если выгрузка позже июня 2024 - ставит старый адрес
		if(Integer.parseInt(route.getDateUnload().split("\\.")[1]) >=7 && Integer.parseInt(route.getDateUnload().split("\\.")[2]) >= 2024) {
			p11 = new Paragraph("Заказчик:\nЗАО Доброном: Республика Беларусь,\r\n"
					+ "220073, г.Минск, пер.Загородный 1-й, 20-23; " + "УНП 191178504, ОКПО 378869615000\r\n"
					+ "р/с BY61ALFA30122365100050270000 ( BYN)\r\nоткрытый  в Закрытое акционерное общество «Альфа-банк» \r\n"
					+ "Юридический адрес: Ул. Сурганова, 43-47; 220013 Минск, Республика Беларусь\r\n"
					+ "УНП 101541947; " + "SWIFT – ALFABY2X\r\n" + "р/с  BY24ALFA30122365100010270000 (USD)\r\n"
					+ "р/с  BY09ALFA30122365100020270000(EUR)\r\n" + "р/с BY91 ALFA 3012 2365 1000 3027 0000 (RUB.)\r\n\n"
					+ "_______________/Е.В. Якубов", fontForRequisites);
	        
		}else {
			p11 = new Paragraph("Заказчик:\nЗАО Доброном: Республика Беларусь,\r\n"
					+ "220112, г. Минск, ул. Янки Лучины, 5; " + "УНП 191178504, ОКПО 378869615000\r\n"
					+ "р/с BY61ALFA30122365100050270000 ( BYN)\r\nоткрытый  в Закрытое акционерное общество «Альфа-банк» \r\n"
					+ "Юридический адрес: Ул. Сурганова, 43-47; 220013 Минск, Республика Беларусь\r\n"
					+ "УНП 101541947; " + "SWIFT – ALFABY2X\r\n" + "р/с  BY24ALFA30122365100010270000 (USD)\r\n"
					+ "р/с  BY09ALFA30122365100020270000(EUR)\r\n" + "р/с BY91 ALFA 3012 2365 1000 3027 0000 (RUB.)\r\n\n"
					+ "_______________/Е.В. Якубов", fontForRequisites);
		}
		
        		
        p11.setSpacingBefore(20f); // высота от прошлого текста
        p11.setAlignment(Element.ALIGN_RIGHT);
        document.add(p11);
        
        /**
         * Реквизиты перевоза
         */
        Paragraph p10 = new Paragraph("Перевозчик\n"+requisitesCarrier, fontForRequisites);			        
        p10.setSpacingBefore(-100f);
        p10.setIndentationRight(550f); // ширина относительно правого края		
        document.add(p10);
        
        Paragraph p12 = new Paragraph("\n_______________/"+sheffName, fontForRequisites);
        p12.setIndentationRight(550f); // ширина относительно правого края
        p12.setAlignment(Element.ALIGN_RIGHT);
        document.add(p12);
	}
	
	// округляем числа до 2-х знаков после запятой
		private static double roundВouble(double value, int places) {
			double scale = Math.pow(10, places);
			return Math.round(value * scale) / scale;
		}
}