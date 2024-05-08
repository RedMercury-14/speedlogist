package by.base.main.service.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;
import org.apache.poi.hssf.usermodel.HSSFPrintSetup;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.BorderExtent;
import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.VerticalAlignment;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.PropertyTemplate;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.hibernate.internal.build.AllowSysOut;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.ibm.icu.text.RuleBasedNumberFormat;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.ElementListener;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.Phrase;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfStructTreeController.returnType;
import com.itextpdf.text.pdf.PdfWriter;

import by.base.main.dao.RouteDAO;
import by.base.main.dao.RouteHasShopDAO;
import by.base.main.dao.ShopDAO;
import by.base.main.model.Act;
import by.base.main.model.MapResponse;
import by.base.main.model.Message;
import by.base.main.model.Order;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;
import by.base.main.service.ActService;
import by.base.main.service.MessageService;
import by.base.main.service.OrderService;
import by.base.main.service.ServiceException;

/**
 * 
 * @author Dima Hrushevski Класс реализующий парсинг и анпарсинг файлов excel а
 *         также отвечает за формирование актов выполненных работ
 *
 */
@Service
public class POIExcel {

	@Autowired
	private ShopDAO shopDAO;

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private RouteHasShopDAO routeHasShopDAO;

	@Autowired
	private ActService actService;

	@Autowired
	private MessageService messageService;
	
	@Autowired
	private OrderService orderService;

	private ArrayList<Shop> shops;
	private ArrayList<RouteHasShop> arrayRouteHasShop;

	public static String classLog;

	public File getFileByMultipart(MultipartFile multipart) throws ServiceException {
		File convFile = new File(multipart.getOriginalFilename());
		try {
			multipart.transferTo(convFile);
		} catch (IllegalStateException | IOException e) {
			System.out.println(e.toString());
			// throw new ServiceException("getFileByMultipart");
		}

		return convFile;
	}

	public File getFileByMultipartTarget(MultipartFile multipart, HttpServletRequest request, String fileName)
			throws ServiceException {
		String appPath = request.getServletContext().getRealPath("");
		File convFile = new File(appPath + "resources/others/" + fileName);
//		System.out.println(convFile.getAbsolutePath());
		try {
			multipart.transferTo(convFile);
		} catch (IllegalStateException | IOException e) {
			System.out.println(e.toString());
			// throw new ServiceException("getFileByMultipart");
		}

		return convFile;
	}

	public void addDBShops(File file) throws ServiceException {
		XSSFWorkbook book;
		ArrayList<Shop> arrayShops = new ArrayList<Shop>();
		try {
			book = new XSSFWorkbook(file);
			XSSFSheet sheet = book.getSheetAt(0);
			Iterator<Row> ri = sheet.rowIterator();
			while (ri.hasNext()) {
				XSSFRow row = (XSSFRow) ri.next();
				Iterator<Cell> ci = row.cellIterator();
				int i = 0;
				String[] cellMass = new String[4];
				while (ci.hasNext()) {
					XSSFCell cell = (XSSFCell) ci.next();
					if (cell.toString().isEmpty()) {
						continue;
					}
					cellMass[i] = cell.toString();
					i++;
					if (i == 4) {
						int numShop = Integer
								.parseInt(cellMass[0].trim().substring(0, cellMass[0].trim().length() - 2));
						String address = cellMass[1];
						Shop shop = new Shop(numShop, address);
						arrayShops.add(shop);
						i = 0;
						break;
					}
				}
			}
			arrayShops.stream().forEach(s -> shopDAO.saveShop(s));
			System.out.println("Finish");
		} catch (InvalidFormatException | IOException e) {
			throw new ServiceException("addDBShops_POI");
		}
	}

	/**
	 * Отдаёт мапу с магазинами из полученную из excel
	 * 
	 * @param file
	 * @return
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public Map<Long, List<Shop[]>> getShopAsPointExcel(File file) throws InvalidFormatException, IOException {
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		int numWay = 0;
		Map<Long, List<Shop[]>> result = new HashMap<Long, List<Shop[]>>();
		Shop startPoint = null;
		try {
			startPoint = shopDAO.getShopByNum(Integer.parseInt(sheet.getSheetName()));
		} catch (NumberFormatException e) {
//			System.err.println("Неправильное название листа в excel файле!");
//			System.err.println("Ожидается 1200, 1700, 1250 и др.");
			return null;
		}
		XSSFSheet additionallySheet = wb.getSheet("666");
		if (additionallySheet != null) {
			result.putAll(processShopAsPointExcel(sheet));
			result.putAll(processShopAsPointExcel(additionallySheet));
			;
		} else {
			result.putAll(processShopAsPointExcel(sheet));
		}

		XSSFSheet moovingySheet = wb.getSheet("Перемещение");
		XSSFSheet SPSheet = wb.getSheet("СП");
		if (moovingySheet != null) {
			result.putAll(processShopAsPointExcel(moovingySheet));
		}
		if (SPSheet != null) {
			result.putAll(processShopAsPointExcel(SPSheet));
		}
		return result;
	}

	private Map<Long, List<Shop[]>> processShopAsPointExcel(XSSFSheet sheet) {
		Map<Long, List<Shop[]>> result = new HashMap<Long, List<Shop[]>>();
		List<Shop> shops = new ArrayList<Shop>();
		Long personIdHasExcel = null;
		for (int i = 1; i <= sheet.getLastRowNum() + 1; i++) {
			if (sheet.getRow(i) == null || sheet.getRow(i).getCell(4) == null || i == sheet.getLastRowNum() + 1
					|| sheet.getRow(i).getCell(4).toString().isEmpty()) {
//				System.out.println("null");
				if (shops.size() != 1) {
					List<Shop[]> target = new ArrayList<Shop[]>();
					for (int j = 0; j < shops.size() - 1; j++) {
						target.add(new Shop[] { shops.get(j), shops.get(j + 1) });
					}
					result.put(personIdHasExcel, target);
					personIdHasExcel = null;
					shops = new ArrayList<Shop>();
				} else {
					int num = sheet.getRow(i - 1).getRowNum() + 1;
					shops = new ArrayList<Shop>();
				}
			} else if (sheet.getRow(i).getCell(0).toString().isEmpty()) {
				List<Shop[]> target = new ArrayList<Shop[]>();
				for (int j = 0; j < shops.size() - 1; j++) {
					target.add(new Shop[] { shops.get(j), shops.get(j + 1) });
				}
				result.put(personIdHasExcel, target);
				shops = new ArrayList<Shop>();
				personIdHasExcel = null;
			} else {
//				System.out.println(sheet.getRow(i).getCell(0).toString() + " " + sheet.getRow(i).getRowNum());
				Shop shop = null;
				// определяем id маршрута и записываем его
				if (sheet.getRow(i).getCell(3) != null) {
					sheet.getRow(i).getCell(3).setCellType(CellType.STRING);
					if (sheet.getRow(i).getCell(3).toString().length() > 5) { // впадлу проверять строки, пусть отсекает
																				// длину меньше 5
						personIdHasExcel = Long.parseLong(sheet.getRow(i).getCell(3).toString().split("\\.")[0]);
					}
				}
				try {
					Integer numShopParse;
					String numShopStr = sheet.getRow(i).getCell(4).toString().toLowerCase();
					if (numShopStr.contains("сборка")) {
						numShopParse = Integer.parseInt(numShopStr.split("сборка")[0].split("\\.")[0].trim());
					} else {
						numShopParse = Integer.parseInt(sheet.getRow(i).getCell(4).toString().split("\\.")[0]);
					}
					shop = shopDAO.getShopByNum(numShopParse);
					shops.add(shop);
					if (sheet.getRow(i).getRowNum() == sheet.getLastRowNum()) {
						// создаём последний маршрут, т.к. следующего поля уже нет
						List<Shop[]> target = new ArrayList<Shop[]>();
						for (int j = 0; j < shops.size() - 1; j++) {
							target.add(new Shop[] { shops.get(j), shops.get(j + 1) });
						}
					}
				} catch (NumberFormatException e) {
					int num = sheet.getRow(i).getRowNum() + 1;
				}
			}
		}
		return result;
	}

	public Map<Long, List<Double[]>> readExcelForWays(File file)
			throws ServiceException, InvalidFormatException, IOException {
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		Shop startPoint = null;
		try {
			startPoint = shopDAO.getShopByNum(Integer.parseInt(sheet.getSheetName()));
		} catch (NumberFormatException e) {
//			System.err.println("Неправильное название листа в excel файле!");
//			System.err.println("Ожидается 1200, 1700, 1250 и др.");
			classLog = classLog
					+ "\nPOIExcel -- Неправильное название листа в excel файле! Ожидается 1200, 1700, 1250 и др.";
			return null;
		}
		Map<Long, List<Double[]>> result1 = processWays(sheet);
		if (result1 == null) {
			return null;
		}

		// отдельный обработчик для сл. листа
		XSSFSheet additionallySheet = wb.getSheet("666");
//		result.forEach((k,v) -> {
//			System.out.println(k + "  --  " + v.size());
//		});
		Map<Long, List<Double[]>> result = new HashMap<Long, List<Double[]>>();
		if (additionallySheet != null) {
			System.out.println("Лист 666 найден");
			Map<Long, List<Double[]>> result2 = processWays(additionallySheet);
			if (result2 == null) {
				return null;
			}
			result.putAll(result1);
			result.putAll(result2);
		} else {
			classLog = classLog + "\nPOIExcel -- Лист \"666\" не найден!";
			System.err.println("Лист 666 не найден");
			result = result1;
		}
		XSSFSheet moovingySheet = wb.getSheet("Перемещение");
		if (moovingySheet != null) {
			System.out.println("Лист Перемещение найден");
			Map<Long, List<Double[]>> result3 = processWays(moovingySheet);
			if (result3 == null) {
				return null;
			}
			result.putAll(result3);
		} else {
			classLog = classLog + "\nPOIExcel -- Лист \"Перемещение\" не найден!";
//			System.err.println("Лист Перемещение не найден");
		}

		XSSFSheet SPSheet = wb.getSheet("СП");
		if (SPSheet != null) {
			Map<Long, List<Double[]>> result4 = processWays(SPSheet);
			result.putAll(result4);
			if (result4 == null) {
				return null;
			}
			System.out.println("Лист СП найден");
		} else {
			classLog = classLog + "\nPOIExcel -- Лист \"СП\" не найден!";
//			System.err.println("Лист СП не найден");
		}

		return result;
	}

	private Map<Long, List<Double[]>> processWays(XSSFSheet sheet) {
		List<Double[]> coordinates = new ArrayList<Double[]>();
		Map<Long, List<Double[]>> result = new HashMap<Long, List<Double[]>>();
		Long personIdHasExcel = null;
		for (int i = 1; i <= sheet.getLastRowNum() + 1; i++) { // начинаем с единицы! первую строку вообще не читаем
			if (sheet.getRow(i) == null || sheet.getRow(i).getCell(4) == null || i == sheet.getLastRowNum() + 1
					|| sheet.getRow(i).getCell(4).toString().isEmpty()) {
				if (coordinates.size() != 1 && personIdHasExcel != null) {
					result.put(personIdHasExcel, coordinates);
//					System.out.println(personIdHasExcel + " put key " + i);
					coordinates = new ArrayList<Double[]>();
					personIdHasExcel = null;
				} else {
					int num = sheet.getRow(i - 1).getRowNum() + 1;
//					System.err.println("Маршрут не создан! Строка - " + num);
					classLog = classLog + "\nPOIExcel -- Маршрут не создан! Строка - " + num;
					coordinates = new ArrayList<Double[]>();
				}
			} else if (sheet.getRow(i).getCell(0).toString().isEmpty()) {
				result.put(personIdHasExcel, coordinates);
				coordinates = new ArrayList<Double[]>();
				personIdHasExcel = null;
			} else {
//				System.out.println(sheet.getRow(i).getCell(0).toString() + " " + sheet.getRow(i).getRowNum());
				// определяем id маршрута и записываем его
				if (sheet.getRow(i).getCell(3) != null) {
					sheet.getRow(i).getCell(3).setCellType(CellType.STRING);
					if (sheet.getRow(i).getCell(3).toString().length() > 5) { // впадлу проверять строки, пусть отсекает
																				// длину меньше 5
						personIdHasExcel = Long.parseLong(sheet.getRow(i).getCell(3).toString().split("\\.")[0]);
					}
				}
//				System.out.println(sheet.getRow(i).getCell(3));
				Shop shop = null;
				try {
					Integer numShopParse;
					String numShopStr = sheet.getRow(i).getCell(4).toString().toLowerCase();
					if (numShopStr.contains("сборка")) {
						numShopParse = Integer.parseInt(numShopStr.split("сборка")[0].split("\\.")[0].trim());
					} else {
						numShopParse = Integer.parseInt(sheet.getRow(i).getCell(4).toString().split("\\.")[0]);
					}
					shop = shopDAO.getShopByNum(numShopParse);

					if (shop == null) {
						int num = sheet.getRow(i - 1).getRowNum() + 1;
						System.err.println("Магазин " + numShopParse + " не найден!");
						classLog = classLog + "\nPOIExcel -- Магазин " + numShopParse + " не найден! Строка " + num;
						return null;
					}
					Double[] shopPoint = new Double[] { Double.parseDouble(shop.getLat()),
							Double.parseDouble(shop.getLng()) };
					coordinates.add(shopPoint);
					if (sheet.getRow(i).getRowNum() == sheet.getLastRowNum()) {
						// создаём последний маршрут, т.к. следующего поля уже нет
						result.put(personIdHasExcel, coordinates);
						personIdHasExcel = null;
					}
				} catch (NumberFormatException e) {
					int num = sheet.getRow(i).getRowNum() + 1;
//					System.err.println("Неправильный формат данных в строке " + num);
					classLog = classLog + "\nPOIExcel -- Неправильный формат данных в строке " + num;
				}
			}
		}
		return result;
	}

	private final String NUMSHOP = "Склад";
	private final String SHOPADDERSS = "Наименование";
	private final String LAT = "Широта";
	private final String LNG = "Долгота";

	/**
	 * Метод загрузки новых магазов из файла маркета
	 * 
	 * @param file
	 * @throws ServiceException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public void loadShopFromMarket(File file) throws ServiceException, InvalidFormatException, IOException {
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);

		Integer numShop = null;
		Integer shopAddress = null;
		Integer lat = null;
		Integer lng = null;

		// читаем шапку
		XSSFRow rowHeader = sheet.getRow(2);
		Iterator<Cell> ciH = rowHeader.cellIterator();
		while (ciH.hasNext()) {
			XSSFCell cellH = (XSSFCell) ciH.next();
//			System.out.println(cell.toString() + " ---- " + cell.getColumnIndex());
			switch (cellH.toString()) {
			case NUMSHOP:
				numShop = cellH.getColumnIndex();
				break;
			case SHOPADDERSS:
				shopAddress = cellH.getColumnIndex();
				break;
			case LAT:
				lat = cellH.getColumnIndex();
				break;
			case LNG:
				lng = cellH.getColumnIndex();
				break;
			}
		}
		if (numShop != null && shopAddress != null && lat != null && lng != null) {
			System.out.println("Параметры шапки найдены");
		}

		for (int i = 3; i < sheet.getLastRowNum() + 1; i++) {
			XSSFRow rowI = sheet.getRow(i);
			XSSFCell cellNumShop = rowI.getCell(numShop);
			XSSFCell cellShopAddress = rowI.getCell(shopAddress);
			XSSFCell cellLat = rowI.getCell(lat);
			XSSFCell cellLng = rowI.getCell(lng);

			Integer numShopTarget = cellNumShop != null
					? Integer.parseInt(cellNumShop.toString().trim().split("\\.")[0])
					: null;
			String shopAddressTarget = cellShopAddress != null ? cellShopAddress.toString() : null;
			String shopLat = cellLat != null ? cellLat.toString() : null;
			String shopLng = cellLng != null ? cellLng.toString() : null;
//			System.out.println(numShopTarget + " " + shopLat + " - " + shopLng + " " +shopAddressTarget );

			if (shopDAO.getShopByNum(numShopTarget) == null) {
				Shop newShop = new Shop(numShopTarget, shopAddressTarget, shopLat, shopLng);
				shopDAO.saveShop(newShop);
				System.out.println("Записан магазин: " + numShopTarget);
			}
		}
	}
	
	private Integer codeСounterparty487 = 0;
	private Integer nameСounterparty487 = 1;
	private Integer codeOrder487 = 2;
	private Integer date487 = 3;
	private Integer numStock487 = 7;
	private Integer codeProduct487 = 11;
	private Integer nameProduct487 = 12;
	private Integer barcodeProduct487 = 13;
	private Integer countProduct487 = 15;
	private Integer timeCreateOrder487 = 29;
	private Integer statusOrderMarcet487 = 30;
	private Integer countInPack487 = 48;
	private Integer countInPall487 = 49;
	private Integer info487 = 50;
	
	
	/**
	 * Метод проверки полей 487 отчёта. 
	 * Так же метод записывает номера полей для следующего метода
	 * 
	 * @param file
	 * @return
	 * @throws ServiceException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	@Deprecated
	public String testHeaderOrderHasExcel(File file) throws ServiceException, InvalidFormatException, IOException {
		XSSFWorkbook wb = new XSSFWorkbook(file);
		XSSFSheet sheet = wb.getSheetAt(0);
		Map <Integer, String> controlHeader = new HashMap<Integer, String>();
		controlHeader.put(0, "Код контрагента");
		controlHeader.put(1, "Наименование контрагента");
		controlHeader.put(2, "Код заказа поставщику");
		controlHeader.put(3, "Дата");
		controlHeader.put(7, "Номер склада");
		controlHeader.put(11, "Код товара");
		controlHeader.put(12, "Наименование товара");
		controlHeader.put(13, "Штрих-код товара");
		controlHeader.put(15, "Кол-во заказанного товара");
		controlHeader.put(29, "Время создания заказа");
		controlHeader.put(30, "Статус заказа");
		controlHeader.put(48, "Кол-во в упаковке (ед.)");
		controlHeader.put(49, "Кол-во на паллете (ед.)");
		controlHeader.put(50, "Инфо расценки");
		
		// читаем шапку
		XSSFRow rowHeader = sheet.getRow(2);
		Iterator<Cell> ciH = rowHeader.cellIterator();
		for (Map.Entry<Integer, String> entry : controlHeader.entrySet()) {
			
			int iRow = entry.getKey();
			String nameRow = entry.getValue();
			String nameRowTest = rowHeader.getCell(iRow).toString();
//			System.out.println(nameRowTest);
			if(!nameRowTest.equals(nameRow)) {
				return "Обнаружено несоответствие колонок! В колонке " + iRow + " ожидается название " + nameRow+", а фактическое название: " + nameRowTest;
			}
		}
		return null;		
	}	
	
	/**
	 * Основной метод смчитки 487 отчёта
     * парсит только 50 статусы
     * записывает все ексели в папку 487
     * дополнительно записывает комментарии. Если коммент начинается на слово Создал - то не записывается
	 * @param file
	 * @param request
	 * @return
	 * @throws ServiceException
	 * @throws InvalidFormatException
	 * @throws IOException
	 */
	public String loadOrderHasExcelV2(File file, HttpServletRequest request) throws ServiceException, InvalidFormatException, IOException {
        String message = "СЧИТКА 50 и 51 СТАТУСОВ + столбец Информация  \n";
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = wb.getSheetAt(0);

        Integer numOrderMarket = null;
        Order order = null;
        int numRow50Status = 0;
        int createOrders = 0;
        int sku = 0;

        message = message + " Всего строк: " + (sheet.getLastRowNum() + 1) + " строк \n";
        boolean flag = false;

        for (int i = 3; i < sheet.getLastRowNum() + 1; i++) {
            XSSFRow rowI = sheet.getRow(i);
            XSSFRow rowBack = null;
            if(i != 3) {
            	rowBack = sheet.getRow(i-1);
            }
            XSSFRow rowNext = null;
            if(sheet.getLastRowNum() + 1 != i) {
                rowNext = sheet.getRow(i+1);
            }
            XSSFCell cellCodeСounterparty487 = rowI.getCell(codeСounterparty487);
            XSSFCell cellNameСounterparty487 = rowI.getCell(nameСounterparty487);
            XSSFCell cellCodeOrder487 = rowI.getCell(codeOrder487);
            XSSFCell cellCodeOrder487NEXT = rowNext == null ? null : rowNext.getCell(codeOrder487);
            XSSFCell cellCodeOrder487BACK = rowBack == null ? null : rowBack.getCell(codeOrder487);
            XSSFCell cellDate487 = rowI.getCell(date487);
            XSSFCell cellNumStock487 = rowI.getCell(numStock487);
            XSSFCell cellCodeProduct487 = rowI.getCell(codeProduct487);
            XSSFCell cellNameProduct487 = rowI.getCell(nameProduct487);
            XSSFCell cellBarcodeProduct487 = rowI.getCell(barcodeProduct487);
            XSSFCell cellCountProduct487 = rowI.getCell(countProduct487);
            XSSFCell cellTimeCreateOrder487 = rowI.getCell(timeCreateOrder487);
            XSSFCell cellStatusOrderMarcet487 = rowI.getCell(statusOrderMarcet487);
            XSSFCell cellCountInPack487 = rowI.getCell(countInPack487);
            XSSFCell cellCountInPall487 = rowI.getCell(countInPall487);
            XSSFCell cellInfo487 = rowI.getCell(info487);



            //устанавливаем типы ячеек индивидуально! Все стринг
            cellCodeСounterparty487.setCellType(CellType.STRING);
            cellNameСounterparty487.setCellType(CellType.STRING);
            cellCodeOrder487.setCellType(CellType.STRING);
            if(cellCodeOrder487NEXT != null) {
                cellCodeOrder487NEXT.setCellType(CellType.STRING);
            }
            cellNumStock487.setCellType(CellType.STRING);
            cellCodeProduct487.setCellType(CellType.STRING);
            cellNameProduct487.setCellType(CellType.STRING);
            cellBarcodeProduct487.setCellType(CellType.STRING);
            cellCountProduct487.setCellType(CellType.STRING);
//			cellTimeCreateOrder487.setCellType(CellType.STRING);
            cellStatusOrderMarcet487.setCellType(CellType.STRING);
            cellCountInPack487.setCellType(CellType.STRING);
            cellCountInPall487.setCellType(CellType.STRING);
            cellInfo487.setCellType(CellType.STRING);

          
            //Смотрим статус: если не 50 и не 51 - то пропускаем
            if(!cellStatusOrderMarcet487.toString().trim().equals("50") && !cellStatusOrderMarcet487.toString().trim().equals("51")) {
                continue;
            }
            numRow50Status++;

            if(numOrderMarket == null || numOrderMarket == Integer.parseInt(cellCodeOrder487.toString().trim())) {
                numOrderMarket = Integer.parseInt(cellCodeOrder487.toString().trim());
                if(order == null) {
                    order = new Order();
                    order.setMarketNumber(numOrderMarket.toString());                   
                    order.setCounterparty(cellNameСounterparty487.toString().trim());
                    Date dateDelivery;
                    try {
                        //отличное решение по датам
                        //https://qna.habr.com/q/1056046
                        dateDelivery = new Date(cellDate487.getDateCellValue().getTime());
                    } catch (Exception e) {
//						e.printStackTrace();
                        System.err.println("Ошибка парсинга даты! Неправильнный тип даты в строке " + i+1);
                        return "Ошибка парсинга даты! Неправильнный тип даты в строке " + i+1;
                    }
                    order.setDateDelivery(dateDelivery);
                    order.setNumStockDelivery(cellNumStock487.toString().trim());
                    order.setCargo(cellNameProduct487.toString().trim() + ", ");
                    //записываем в поле информация
                    if(!cellInfo487.toString().isEmpty()) {
                    	if(!cellInfo487.toString().split(" ")[0].equals("Создано")) {
                    		order.setMarketInfo(cellInfo487.toString());
                    	}
                    }
                    Date dateCreateInMarket = new Date(cellTimeCreateOrder487.getDateCellValue().getTime());
                    order.setDateCreateMarket(dateCreateInMarket);
                    order.setChangeStatus("Заказ создан в маркете: " + dateCreateInMarket);
                    if(Double.parseDouble(cellCountProduct487.toString().trim()) == 0.0) {
                        order = null;
                        numOrderMarket= null;
                        sku = 0;   
                        flag = false;
                        continue;
                        
                    }
                    Double pall = Math.ceil(Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim()));
                    String pallStr = pall+"";

//                    System.out.println(cellCountProduct487.toString());
//                    System.out.println(cellCountInPall487.toString());

// Вычисляем pallNew
                    Double pallNew = Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim());
//                    System.out.println(pallNew);
// Получаем целую и дробную части из pallNew
                    int integerPart = (int) Math.floor(pallNew);
                    double fractionalPart = pallNew - integerPart;
                    String pallMono = Integer.toString(integerPart);
                    String pallMix;
                    if (fractionalPart > 0) {
                        // Если есть дробная часть, паллет микс = 1
                        pallMix = "1";
                    } else {
                        // Иначе записываем дробную часть
                        pallMix = "0";
                    }
//                    System.out.println("pallMono: " + pallMono);
//                    System.out.println("pallMix: " + pallMix);

                    order.setPall(pallStr.split("\\.")[0]);
                    order.setMonoPall(Integer.parseInt(pallMono));
                    order.setMixPall(Integer.parseInt(pallMix));
                    sku++;
                    order.setSku(sku);
                }else {
                    if(Double.parseDouble(cellCountProduct487.toString().trim()) == 0.0 && order!= null) {
                    	if(i == sheet.getLastRowNum() && order != null) { //принудительная загрузка если последняя строка во всём по потребности равна 0
//            				System.err.println("Сохраняем заказ в базе");
                            //перед сохранение просчитываем время на выгрузку, пока костыльно, т.е. 6 мин на паллету!
                            Integer minute = Integer.parseInt(order.getPall()) * 6;
                            
                            order.setStatus(5);

                            Integer pallMono = Integer.valueOf(order.getMonoPall());
                            Integer pallMix = Integer.valueOf(order.getMixPall());
                            Integer skuTotal = Integer.valueOf(order.getSku());
                            
//                            Расчет времени выгрузки авто в минутах.
//                            =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//                            Разъяснение:
//                            10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//                            МОНО -количество моно паллет в заказе
//                            MIX - количество микс паллет в заказе
//                            SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
                            Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
                            System.out.println(order.getMarketNumber() + " <---MARKET");
                            try {
                            	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
            				} catch (Exception e) {
            					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
            				}
                            

//                            System.err.println(order);
                            
                            
                            message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
                            createOrders++;
                            order = null;
                            numOrderMarket= null;
                            sku = 0;
                    		break;
                    	}
                    	if(numOrderMarket != Integer.parseInt(cellCodeOrder487NEXT.toString().trim())) {//принудительная загрузка если послендняя строка в заказа равна 0, а дальше есть новый заказ
//                    		System.err.println("Сохраняем заказ в базе");
                            //перед сохранение просчитываем время на выгрузку, пока костыльно, т.е. 6 мин на паллету!
                            Integer minute = Integer.parseInt(order.getPall()) * 6;
                            
                            order.setStatus(5);

                            Integer pallMono = Integer.valueOf(order.getMonoPall());
                            Integer pallMix = Integer.valueOf(order.getMixPall());
                            Integer skuTotal = Integer.valueOf(order.getSku());
                            
//                            Расчет времени выгрузки авто в минутах.
//                            =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//                            Разъяснение:
//                            10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//                            МОНО -количество моно паллет в заказе
//                            MIX - количество микс паллет в заказе
//                            SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
                            Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
                            System.out.println(order.getMarketNumber() + " <---MARKET");
                            try {
                            	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
            				} catch (Exception e) {
            					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
            				}
                            
                            message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
                            createOrders++;
                            order = null;
                            numOrderMarket= null;
                            sku = 0;
                            continue;
                    	}
                        continue;
                    }
                    double pall = Math.ceil(Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim()));

//                    System.out.println(cellCountProduct487.toString());
//                    System.out.println(cellCountInPall487.toString());

                    // Вычисляем pallNew
                    Double pallNew = Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim());
//                    System.out.println(pallNew);

                    // Получаем целую и дробную части из pallNew
                    int integerPart = (int) Math.floor(pallNew);
                    double fractionalPart = pallNew - integerPart;

                    String pallMono = Integer.toString(integerPart);
                    String pallMix;

                    if (fractionalPart > 0) {
                        // Если есть дробная часть, паллет микс = 1
                        pallMix = "1";
                    } else {
                        // Иначе записываем дробную часть
                        pallMix = "0";
                    }

//                    System.out.println("pallMono: " + pallMono);
//                    System.out.println("pallMix: " + pallMix);

                    Integer intPall = (int) pall;
                    Integer oldIntPall = Integer.parseInt(order.getPall());
                    Integer totalPall = intPall + oldIntPall;
                 

                    Integer intPallMono = Integer.valueOf(pallMono);
                    Integer oldIntPallMono = order.getMonoPall();
                    Integer totalPallMono = intPallMono + oldIntPallMono;
                   

                    Integer intPallMix = Integer.valueOf(pallMix);
//                    System.out.println("oldPallMix - " + order.getMixPall());
                    Integer oldIntPallMix = Integer.valueOf(order.getMixPall());
                    Integer totalPallMix = intPallMix + oldIntPallMix;
                 

                    String totalPallStr = totalPall.toString();
                    String totalPallMonoStr = totalPallMono.toString();
                    String totalPallMixStr = totalPallMix.toString();

                    order.setPall(totalPallStr);
                    order.setMonoPall(Integer.parseInt(totalPallMonoStr));
                    order.setMixPall(Integer.parseInt(totalPallMixStr));
                    
                    sku++;
                    order.setSku(sku);
                }
            }
                        
            if(cellCodeOrder487NEXT == null || numOrderMarket != Integer.parseInt(cellCodeOrder487NEXT.toString().trim())) {
//				System.err.println("Сохраняем заказ в базе");
                
                order.setStatus(5);

                Integer pallMono = Integer.valueOf(order.getMonoPall());
                Integer pallMix = Integer.valueOf(order.getMixPall());
                Integer skuTotal = Integer.valueOf(order.getSku());
                
//                Расчет времени выгрузки авто в минутах.
//                =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//                Разъяснение:
//                10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//                МОНО -количество моно паллет в заказе
//                MIX - количество микс паллет в заказе
//                SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
                Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
                System.out.println(order.getMarketNumber() + " <---MARKET");
                try {
                	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
				} catch (Exception e) {
					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
				}
                

//                System.err.println(order);
                
                
              
                
                message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
                createOrders++;
                order = null;
                numOrderMarket = null;
                sku = 0; 
                continue;
            }
            
        }
        message = message + "\n Строк с 50 статусом: " + (numRow50Status) + "\n ";
        message = message + "Всего считано маршрутов: " + (createOrders) + "\n ";

        String appPath = request.getServletContext().getRealPath("");
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String timeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));
        String fileName = dateNow + " " + timeNow + ".xlsx";
        //если файла нет - создаём его 
        File fileTest= new File(appPath + "resources/others/487/");
        if (!fileTest.exists()) {
            fileTest.mkdir();
        }
        
        File fileLocal = new File(appPath + "resources/others/487/" + fileName);
        System.out.println(appPath + "resources/others/487/" + fileName);
//		  		book.write(fos);
//		  		fos.flush();
        wb.write(new FileOutputStream(fileLocal));
        wb.close();
        message = message + "Считка завершена без ошибок";
        return message;
    }
	
	
	/**
     * Основной метод смчитки 487 отчёта
     * парсит только 50 статусы
     * записывает все ексели в папку 487
     * УСТАРЕЛ
     * @param file
     * @return
     * @throws ServiceException
     * @throws InvalidFormatException
     * @throws IOException
     */
	@Deprecated
    public String loadOrderHasExcel(File file, HttpServletRequest request) throws ServiceException, InvalidFormatException, IOException {
        String message = "СЧИТКА 50 и 51 СТАТУСОВ \n";
        XSSFWorkbook wb = new XSSFWorkbook(new FileInputStream(file));
        XSSFSheet sheet = wb.getSheetAt(0);

        Integer numOrderMarket = null;
        Order order = null;
        int numRow50Status = 0;
        int createOrders = 0;
        int sku = 0;

        message = message + " Всего строк: " + (sheet.getLastRowNum() + 1) + " строк \n";
        boolean flag = false;

        for (int i = 3; i < sheet.getLastRowNum() + 1; i++) {
            XSSFRow rowI = sheet.getRow(i);
            XSSFRow rowBack = null;
            if(i != 3) {
            	rowBack = sheet.getRow(i-1);
            }
            XSSFRow rowNext = null;
            if(sheet.getLastRowNum() + 1 != i) {
                rowNext = sheet.getRow(i+1);
            }
            XSSFCell cellCodeСounterparty487 = rowI.getCell(codeСounterparty487);
            XSSFCell cellNameСounterparty487 = rowI.getCell(nameСounterparty487);
            XSSFCell cellCodeOrder487 = rowI.getCell(codeOrder487);
            XSSFCell cellCodeOrder487NEXT = rowNext == null ? null : rowNext.getCell(codeOrder487);
            XSSFCell cellCodeOrder487BACK = rowBack == null ? null : rowBack.getCell(codeOrder487);
            XSSFCell cellDate487 = rowI.getCell(date487);
            XSSFCell cellNumStock487 = rowI.getCell(numStock487);
            XSSFCell cellCodeProduct487 = rowI.getCell(codeProduct487);
            XSSFCell cellNameProduct487 = rowI.getCell(nameProduct487);
            XSSFCell cellBarcodeProduct487 = rowI.getCell(barcodeProduct487);
            XSSFCell cellCountProduct487 = rowI.getCell(countProduct487);
            XSSFCell cellTimeCreateOrder487 = rowI.getCell(timeCreateOrder487);
            XSSFCell cellStatusOrderMarcet487 = rowI.getCell(statusOrderMarcet487);
            XSSFCell cellCountInPack487 = rowI.getCell(countInPack487);
            XSSFCell cellCountInPall487 = rowI.getCell(countInPall487);



            //устанавливаем типы ячеек индивидуально! Все стринг
            cellCodeСounterparty487.setCellType(CellType.STRING);
            cellNameСounterparty487.setCellType(CellType.STRING);
            cellCodeOrder487.setCellType(CellType.STRING);
            if(cellCodeOrder487NEXT != null) {
                cellCodeOrder487NEXT.setCellType(CellType.STRING);
            }
            cellNumStock487.setCellType(CellType.STRING);
            cellCodeProduct487.setCellType(CellType.STRING);
            cellNameProduct487.setCellType(CellType.STRING);
            cellBarcodeProduct487.setCellType(CellType.STRING);
            cellCountProduct487.setCellType(CellType.STRING);
//			cellTimeCreateOrder487.setCellType(CellType.STRING);
            cellStatusOrderMarcet487.setCellType(CellType.STRING);
            cellCountInPack487.setCellType(CellType.STRING);
            cellCountInPall487.setCellType(CellType.STRING);

          
            //Смотрим статус: если не 50 и не 51 - то пропускаем
            if(!cellStatusOrderMarcet487.toString().trim().equals("50") && !cellStatusOrderMarcet487.toString().trim().equals("51")) {
                continue;
            }
            numRow50Status++;

            if(numOrderMarket == null || numOrderMarket == Integer.parseInt(cellCodeOrder487.toString().trim())) {
                numOrderMarket = Integer.parseInt(cellCodeOrder487.toString().trim());
                if(order == null) {
                    order = new Order();
                    order.setMarketNumber(numOrderMarket.toString());                   
                    order.setCounterparty(cellNameСounterparty487.toString().trim());
                    Date dateDelivery;
                    try {
                        //отличное решение по датам
                        //https://qna.habr.com/q/1056046
                        dateDelivery = new Date(cellDate487.getDateCellValue().getTime());
                    } catch (Exception e) {
//						e.printStackTrace();
                        System.err.println("Ошибка парсинга даты! Неправильнный тип даты в строке " + i+1);
                        return "Ошибка парсинга даты! Неправильнный тип даты в строке " + i+1;
                    }
                    order.setDateDelivery(dateDelivery);
                    order.setNumStockDelivery(cellNumStock487.toString().trim());
                    order.setCargo(cellNameProduct487.toString().trim() + ", ");
                    Date dateCreateInMarket = new Date(cellTimeCreateOrder487.getDateCellValue().getTime());
                    order.setDateCreateMarket(dateCreateInMarket);
                    order.setChangeStatus("Заказ создан в маркете: " + dateCreateInMarket);
                    if(Double.parseDouble(cellCountProduct487.toString().trim()) == 0.0) {
                        order = null;
                        numOrderMarket= null;
                        sku = 0;   
                        flag = false;
                        continue;
                        
                    }
                    Double pall = Math.ceil(Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim()));
                    String pallStr = pall+"";

//                    System.out.println(cellCountProduct487.toString());
//                    System.out.println(cellCountInPall487.toString());

// Вычисляем pallNew
                    Double pallNew = Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim());
//                    System.out.println(pallNew);
// Получаем целую и дробную части из pallNew
                    int integerPart = (int) Math.floor(pallNew);
                    double fractionalPart = pallNew - integerPart;
                    String pallMono = Integer.toString(integerPart);
                    String pallMix;
                    if (fractionalPart > 0) {
                        // Если есть дробная часть, паллет микс = 1
                        pallMix = "1";
                    } else {
                        // Иначе записываем дробную часть
                        pallMix = "0";
                    }
//                    System.out.println("pallMono: " + pallMono);
//                    System.out.println("pallMix: " + pallMix);

                    order.setPall(pallStr.split("\\.")[0]);
                    order.setMonoPall(Integer.parseInt(pallMono));
                    order.setMixPall(Integer.parseInt(pallMix));
                    sku++;
                    order.setSku(sku);
                }else {
                    if(Double.parseDouble(cellCountProduct487.toString().trim()) == 0.0 && order!= null) {
                    	if(i == sheet.getLastRowNum() && order != null) { //принудительная загрузка если последняя строка во всём по потребности равна 0
//            				System.err.println("Сохраняем заказ в базе");
                            //перед сохранение просчитываем время на выгрузку, пока костыльно, т.е. 6 мин на паллету!
                            Integer minute = Integer.parseInt(order.getPall()) * 6;
                            
                            order.setStatus(5);

                            Integer pallMono = Integer.valueOf(order.getMonoPall());
                            Integer pallMix = Integer.valueOf(order.getMixPall());
                            Integer skuTotal = Integer.valueOf(order.getSku());
                            
//                            Расчет времени выгрузки авто в минутах.
//                            =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//                            Разъяснение:
//                            10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//                            МОНО -количество моно паллет в заказе
//                            MIX - количество микс паллет в заказе
//                            SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
                            Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
                            System.out.println(order.getMarketNumber() + " <---MARKET");
                            try {
                            	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
            				} catch (Exception e) {
            					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
            				}
                            

//                            System.err.println(order);
                            
                            
                            message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
                            createOrders++;
                            order = null;
                            numOrderMarket= null;
                            sku = 0;
                    		break;
                    	}
                    	if(numOrderMarket != Integer.parseInt(cellCodeOrder487NEXT.toString().trim())) {//принудительная загрузка если послендняя строка в заказа равна 0, а дальше есть новый заказ
//                    		System.err.println("Сохраняем заказ в базе");
                            //перед сохранение просчитываем время на выгрузку, пока костыльно, т.е. 6 мин на паллету!
                            Integer minute = Integer.parseInt(order.getPall()) * 6;
                            
                            order.setStatus(5);

                            Integer pallMono = Integer.valueOf(order.getMonoPall());
                            Integer pallMix = Integer.valueOf(order.getMixPall());
                            Integer skuTotal = Integer.valueOf(order.getSku());
                            
//                            Расчет времени выгрузки авто в минутах.
//                            =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//                            Разъяснение:
//                            10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//                            МОНО -количество моно паллет в заказе
//                            MIX - количество микс паллет в заказе
//                            SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
                            Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
                            System.out.println(order.getMarketNumber() + " <---MARKET");
                            try {
                            	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
            				} catch (Exception e) {
            					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
            				}
                            
                            message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
                            createOrders++;
                            order = null;
                            numOrderMarket= null;
                            sku = 0;
                            continue;
                    	}
                        continue;
                    }
                    double pall = Math.ceil(Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim()));

//                    System.out.println(cellCountProduct487.toString());
//                    System.out.println(cellCountInPall487.toString());

                    // Вычисляем pallNew
                    Double pallNew = Double.parseDouble(cellCountProduct487.toString().trim()) / Double.parseDouble(cellCountInPall487.toString().trim());
//                    System.out.println(pallNew);

                    // Получаем целую и дробную части из pallNew
                    int integerPart = (int) Math.floor(pallNew);
                    double fractionalPart = pallNew - integerPart;

                    String pallMono = Integer.toString(integerPart);
                    String pallMix;

                    if (fractionalPart > 0) {
                        // Если есть дробная часть, паллет микс = 1
                        pallMix = "1";
                    } else {
                        // Иначе записываем дробную часть
                        pallMix = "0";
                    }

//                    System.out.println("pallMono: " + pallMono);
//                    System.out.println("pallMix: " + pallMix);

                    Integer intPall = (int) pall;
                    Integer oldIntPall = Integer.parseInt(order.getPall());
                    Integer totalPall = intPall + oldIntPall;
                 

                    Integer intPallMono = Integer.valueOf(pallMono);
                    Integer oldIntPallMono = order.getMonoPall();
                    Integer totalPallMono = intPallMono + oldIntPallMono;
                   

                    Integer intPallMix = Integer.valueOf(pallMix);
//                    System.out.println("oldPallMix - " + order.getMixPall());
                    Integer oldIntPallMix = Integer.valueOf(order.getMixPall());
                    Integer totalPallMix = intPallMix + oldIntPallMix;
                 

                    String totalPallStr = totalPall.toString();
                    String totalPallMonoStr = totalPallMono.toString();
                    String totalPallMixStr = totalPallMix.toString();

                    order.setPall(totalPallStr);
                    order.setMonoPall(Integer.parseInt(totalPallMonoStr));
                    order.setMixPall(Integer.parseInt(totalPallMixStr));
                    
                    sku++;
                    order.setSku(sku);
                }
            }
                        
            if(cellCodeOrder487NEXT == null || numOrderMarket != Integer.parseInt(cellCodeOrder487NEXT.toString().trim())) {
//				System.err.println("Сохраняем заказ в базе");
                
                order.setStatus(5);

                Integer pallMono = Integer.valueOf(order.getMonoPall());
                Integer pallMix = Integer.valueOf(order.getMixPall());
                Integer skuTotal = Integer.valueOf(order.getSku());
                
//                Расчет времени выгрузки авто в минутах.
//                =10мин+ МОНО*2мин.+MIX*3мин.+((SKU-1мин)*3мин)
//                Разъяснение:
//                10 мин.= не зависимо от объема поставки есть действия специалиста требующие временных затрат.
//                МОНО -количество моно паллет в заказе
//                MIX - количество микс паллет в заказе
//                SKU-1 – каждое SKU более одной требует дополнительных временных затрат.
                Integer minutesUnload = 10 + pallMono * 2 + pallMix * 3 + ((skuTotal - 1) * 3);
                System.out.println(order.getMarketNumber() + " <---MARKET");
                try {
                	order.setTimeUnload(Time.valueOf(LocalTime.ofSecondOfDay(minutesUnload*60)));
				} catch (Exception e) {
					 return "Ошибка расчёта времени выгрузки! В номере из маркета " + order.getMarketNumber() + " рассчитано " + minutesUnload + " минут! \nРАСЧЁТ ЗАВЕРШЕН С ОШИБКОЙ!";
				}
                

//                System.err.println(order);
                
                
              
                
                message = message + " \n " + orderService.saveOrderFromExcel(order); // здесь происходит пролверка и запись заявки
                createOrders++;
                order = null;
                numOrderMarket = null;
                sku = 0; 
                continue;
            }
            
        }
        message = message + "\n Строк с 50 статусом: " + (numRow50Status) + "\n ";
        message = message + "Всего считано маршрутов: " + (createOrders) + "\n ";

        String appPath = request.getServletContext().getRealPath("");
        String dateNow = LocalDate.now().format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String timeNow = LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm"));
        String fileName = dateNow + " " + timeNow + ".xlsx";
        //если файла нет - создаём его 
        File fileTest= new File(appPath + "resources/others/487/");
        if (!fileTest.exists()) {
            fileTest.mkdir();
        }
        
        File fileLocal = new File(appPath + "resources/others/487/" + fileName);
        System.out.println(appPath + "resources/others/487/" + fileName);
//		  		book.write(fos);
//		  		fos.flush();
        wb.write(new FileOutputStream(fileLocal));
        wb.close();
        message = message + "Считка завершена без ошибок";
        return message;
    }

	public void dataBaseFromExcel(File file, Date dateStart) throws ServiceException {
		int numPoint = 0;
		XSSFWorkbook book;
		try {
			book = new XSSFWorkbook(file);
			XSSFSheet sheet = book.getSheetAt(0);
			int rowStart = Math.min(0, sheet.getFirstRowNum());
			int rowEnd = Math.max(10000, sheet.getLastRowNum());
			boolean flag = false;
			boolean flag2 = false;
			Set<RouteHasShop> qRouteHasShops = new HashSet<RouteHasShop>();
			String routeDirection = "";
			Iterator<Row> ri = sheet.rowIterator();
			for (int rw = rowStart; rw < rowEnd; rw++) {
				numPoint++;
				XSSFRow row = sheet.getRow(rw);
				int i = 0;
				String[] cellMass = new String[4];

				if (row == null) {
					if (!flag) {
						if (!qRouteHasShops.isEmpty()) {
							Route route = creatureEmptyRoute(dateStart);
							Double sumPall = 0.0;
							Double sumWeight = 0.0;
							for (RouteHasShop routeHasShop : qRouteHasShops) {
								sumPall = sumPall + Double.parseDouble(routeHasShop.getPall());
								sumWeight = sumWeight + Double.parseDouble(routeHasShop.getWeight());
							}
							route.setStatusRoute("0");
							route.setStatusStock("0");
							route.setTotalLoadPall(sumPall.toString());
							route.setTotalCargoWeight(sumWeight.toString());
							qRouteHasShops.stream().forEach(s -> s.setRoute(route));
							RouteHasShop[] RHSArray = {};
							RHSArray = qRouteHasShops.toArray(new RouteHasShop[qRouteHasShops.size()]);
							RouteHasShop routeHasShopForDirection = RHSArray[RHSArray.length - 2];
							route.setRouteDirection(routeDirection(routeHasShopForDirection.getShop().getAddress())
									+ " [" + route.getIdRoute() + "]");
							qRouteHasShops.stream().forEach(s -> routeHasShopDAO.saveOrUpdateRouteHasShop(s));
							qRouteHasShops.clear();
						}
						numPoint = 0;
						System.out.println("+++++++++++++++++++++ создание маршрута +++++++++++++++++++++++++++++");
						flag = true;
						continue;
					} else {
						numPoint = 0;
						continue;
					}

				} else {
					flag = false;
					Iterator<Cell> ci = ri.next().cellIterator();
					while (ci.hasNext()) {

						XSSFCell cell = (XSSFCell) ci.next();
						if (cell.toString().trim().isEmpty()) {
							if (!flag2) {
								if (!qRouteHasShops.isEmpty()) {
									Route route = creatureEmptyRoute(dateStart);
									Double sumPall = 0.0;
									Double sumWeight = 0.0;
									for (RouteHasShop routeHasShop : qRouteHasShops) {
										sumPall = sumPall + Double.parseDouble(routeHasShop.getPall());
										sumWeight = sumWeight + Double.parseDouble(routeHasShop.getWeight());
									}
									route.setStatusRoute("0");
									route.setStatusStock("0");
									route.setTotalLoadPall(sumPall.toString());
									route.setTotalCargoWeight(sumWeight.toString());
									qRouteHasShops.stream().forEach(s -> s.setRoute(route));
									RouteHasShop[] RHSArray = {};
									RHSArray = qRouteHasShops.toArray(new RouteHasShop[qRouteHasShops.size()]);
									RouteHasShop routeHasShopForDirection = RHSArray[RHSArray.length - 2];
									route.setRouteDirection(
											routeDirection(routeHasShopForDirection.getShop().getAddress()) + " ["
													+ route.getIdRoute() + "]");
									qRouteHasShops.stream().forEach(s -> routeHasShopDAO.saveOrUpdateRouteHasShop(s));
									qRouteHasShops.clear();
								}
								numPoint = 0;
								System.out.println(
										"------------------------- создание маршрута +++++++++++++++++++++++++++++");

								flag2 = true;
								flag = true;
								break;
							} else {
								numPoint = 0;
								flag2 = true;
								flag = true;
								break;
							}
						} else {
							cellMass[i] = cell.toString();
							i++;
							if (i == 4) {
								int numShop = Integer.parseInt(cellMass[0].substring(0, cellMass[0].length() - 2));
								Shop shop = shopDAO.getShopByNum(numShop);
								RouteHasShop routeHasShop = new RouteHasShop(numPoint, cellMass[2],
										Math.ceil(Double.parseDouble(cellMass[3])) + "", shop);
								qRouteHasShops.add(routeHasShop);
								routeDirection = routeDirection(shop.getAddress());
								System.out.println("================================");
								i = 0;
								flag = false;
								flag2 = false;
								break;
							}
						}

					}
				}

			}
		} catch (InvalidFormatException | IOException e) {
			throw new ServiceException("dataBaseFromExcel_POI");
		}
	}

	public Route creatureEmptyRoute(Date dateStart) {
		Route route = new Route();
		route.setSanitization(false);
		route.setDateLoadPreviously(dateStart.toLocalDate());
		routeDAO.saveOrUpdateRoute(route);
		return routeDAO.getLastRoute();
	}

	private static String routeDirection(String adress) {
		String[] step1 = adress.split(" ", 2);
		String[] step2 = step1[1].split(",", 2);
		String target = step2[0];
		return target;
	}

	private List<String> readDistances(XSSFSheet sheet) {
		List<String> distances = new ArrayList<String>();
		int rowStart = Math.min(0, sheet.getFirstRowNum());
		int rowEnd = Math.max(0, sheet.getLastRowNum());

		for (int rw = rowStart + 1; rw <= rowEnd; rw++) {
			XSSFRow row = sheet.getRow(rw);
			if (row == null) {
				continue;
			}
			short minCol = row.getFirstCellNum();
			short maxCol = row.getLastCellNum();

			for (short col = (short) (minCol + 1); col <= maxCol; col++) {
				XSSFCell cell = row.getCell(col);
				if (cell == null) {
					continue;
				}

				DataFormatter formatter = new DataFormatter();
				String text = formatter.formatCellValue(cell);

				distances.add(text);
			}
		}
		return distances;
	}

	private List<String> readColumn(XSSFSheet sheet) {
		List<String> columns = new ArrayList<String>();
		int rowStart = Math.min(0, sheet.getFirstRowNum());
		int rowEnd = Math.max(0, sheet.getLastRowNum());
		for (int rw = rowStart + 1; rw <= rowEnd; rw++) {
			XSSFRow row = sheet.getRow(rw);
			if (row == null) {
				continue;
			}
			int col = 0;
			XSSFCell cell = row.getCell(col);
			if (cell == null) {
				continue;
			}
			DataFormatter formatter = new DataFormatter();
			String text = formatter.formatCellValue(cell);
			columns.add(text);
		}
		return columns;
	}

	private List<String> readRow(XSSFSheet sheet) { // считывает первый ряд
		List<String> rows = new ArrayList<String>();
		int rw = 0;
		XSSFRow row = sheet.getRow(rw);
		if (row == null) {
		}
		short minCol = row.getFirstCellNum();
		short maxCol = row.getLastCellNum();
		for (short col = (short) (minCol + 1); col < maxCol; col++) {
			XSSFCell cell = row.getCell(col);
			if (cell == null) {
				continue;
			}
			DataFormatter formatter = new DataFormatter();
			String text = formatter.formatCellValue(cell);
			rows.add(text);
		}
		// rows.add("\n");
		return rows;
	}

	public void getDistancesToMap(Map<String, String> distance, HttpServletRequest request)
			throws InvalidFormatException, IOException {
		String appPath = request.getServletContext().getRealPath("");
		File file = new File(appPath + "resources/others/matrix.xlsx"); // поправить путь к файлу!
		XSSFWorkbook book = new XSSFWorkbook(file);
		XSSFSheet sheet = book.getSheetAt(0);
		List<String> col = readColumn(sheet);
		List<String> row = readRow(sheet);
		List<String> distances = readDistances(sheet);
		for (String string : row) {
			if (string.equals("") || string.length() == 0 || string.isEmpty()) {
				break;
			}
			for (String string2 : col) {
				if (string2.equals("") || string2.length() == 0 || string2.isEmpty()) {
					break;
				}
				String str = distances.stream().findFirst().get();
				distance.put(string + "-" + string2, str);
				distances.remove(str);
			}
		}
	}

	/**
	 * Основной метод создания excel после обработки в маршрутизаторе развозника.
	 * <br>
	 * Работает после основного метода подсчёта расстояний.
	 * 
	 * @param mapResult
	 * @param excel
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public void getRouteExcelForLogist(Map<Long, List<MapResponse>> mapResult, File excel, HttpServletRequest request)
			throws FileNotFoundException, IOException {
		XSSFWorkbook excelBook = new XSSFWorkbook(new FileInputStream(excel));
		XSSFSheet excelSheet = excelBook.getSheetAt(0); // тут тупо первый лист ,предполагается что он 1700 или 1200 или
														// 1250 и т.д.

		List<CustomRowHasExcel> result = custimRowHasExcelProcess(excelSheet, mapResult);
		XSSFSheet additionallySheet = excelBook.getSheet("666");
		if (additionallySheet != null) {
			result.addAll(custimRowHasExcelProcess(additionallySheet, mapResult));
		}
		XSSFSheet moovingySheet = excelBook.getSheet("Перемещение");
		if (moovingySheet != null) {
			result.addAll(custimRowHasExcelProcess(moovingySheet, mapResult));
		}

		XSSFSheet SPSheet = excelBook.getSheet("СП");
		if (SPSheet != null) {
			result.addAll(custimRowHasExcelProcess(SPSheet, mapResult));
		}

//		for (CustomRowHasExcel customRowHasExcel : result) {
//			customRowHasExcel.printString();
//			System.out.println();
//		};
		createRazvoz(result, request);
	}

	/**
	 * Метод отвечает за формирование акта
	 * 
	 * @param routes
	 * @param request
	 * @param isNDS
	 * @param dateContract
	 * @param numContractTarget
	 * @param sheffName
	 * @param city
	 * @param requisitesCarrier
	 * @param dateOfAct
	 * @throws DocumentException 
	 */

	public void getActOfRoute(List<Route> routes, HttpServletRequest request, boolean isNDS, String dateContract,
			String numContractTarget, String sheffName, String city, String requisitesCarrier, String dateOfAct) throws DocumentException {

		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
		DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

		String password = "abcd";
		byte[] pwdBytes = null;
		try {
			pwdBytes = Hex.decodeHex(password.toCharArray());
		} catch (DecoderException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet sheet = (XSSFSheet) book.createSheet("Акт");
//		// запрет ручного редактирования документа
		sheet.lockDeleteColumns(true);
		sheet.lockDeleteRows(true);
		sheet.lockFormatCells(true);
		sheet.lockFormatColumns(true);
		sheet.lockFormatRows(true);
		sheet.lockInsertColumns(true);
		sheet.lockInsertRows(true);
		sheet.getCTWorksheet().getSheetProtection().setPassword(pwdBytes);
		sheet.enableLocking();

		Font font = book.createFont();
		font.setFontName("Arial");
		font.setBold(true);
		font.setFontHeightInPoints((short) 13);

		Font fontForOthers = book.createFont();
		fontForOthers.setFontName("Arial");
		fontForOthers.setBold(true);
		fontForOthers.setFontHeightInPoints((short) 12);

		Font fontForText = book.createFont();
		fontForText.setFontName("Arial");
		fontForText.setFontHeightInPoints((short) 12);

		CellStyle styleForHead = book.createCellStyle(); // стиль для шапки
		styleForHead.setFont(font);
		styleForHead.setAlignment(HorizontalAlignment.CENTER);
		styleForHead.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle styleForOthers = book.createCellStyle(); // стиль для доп информации
		styleForOthers.setFont(fontForOthers);
		styleForOthers.setAlignment(HorizontalAlignment.CENTER);
		styleForOthers.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthers.setWrapText(true);

		CellStyle styleForOthersNotCenter = book.createCellStyle(); // стиль для доп информации но без центровки
		styleForOthersNotCenter.setFont(fontForOthers);
		styleForOthersNotCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthersNotCenter.setWrapText(true);

		CellStyle styleForText = book.createCellStyle(); // стиль для обычного текста
		styleForText.setFont(fontForText);
		styleForText.setWrapText(true);

		CellStyle styleForTextRequisites = book.createCellStyle(); // стиль для текста с реквизитами
		styleForTextRequisites.setFont(fontForText);
		styleForTextRequisites.setVerticalAlignment(VerticalAlignment.DISTRIBUTED);
		styleForTextRequisites.setWrapText(true);

		CellStyle styleForAttantionText = book.createCellStyle(); // стлиль для жирного текста без выравнивания
		styleForAttantionText.setFont(fontForOthers);

		Row rowANum = sheet.createRow(0);
		Cell aNum = rowANum.createCell(0);
		String numAct;
		if (routes.size() == 1) {
			aNum.setCellValue("Акт № " + routes.get(0).getIdRoute());
			numAct = routes.get(0).getIdRoute().toString().trim();
		} else {
			aNum.setCellValue("Акт № T" + routes.get(0).getIdRoute());
			numAct = "T" + routes.get(0).getIdRoute();
		}
		aNum.setCellStyle(styleForHead);
		sheet.addMergedRegion(new CellRangeAddress(0, 0, 0, 29));

		Row rowANum2 = sheet.createRow(1);
		Cell aNum2 = rowANum2.createCell(0);
		aNum2.setCellValue("сдачи-приемки выполненных работ на оказание транспортных услуг");
		aNum2.setCellStyle(styleForHead);
		sheet.addMergedRegion(new CellRangeAddress(1, 1, 0, 29));

		Row rowDataAndPlace = sheet.createRow(2);
		Cell cellDataAndPlace1 = rowDataAndPlace.createCell(0);
		cellDataAndPlace1.setCellValue(dateOfAct);
		cellDataAndPlace1.setCellStyle(styleForOthers);
		Cell cellDataAndPlace2 = rowDataAndPlace.createCell(24);
		cellDataAndPlace2.setCellValue("г. " + city);
		cellDataAndPlace2.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 0, 3));
		sheet.addMergedRegion(new CellRangeAddress(2, 2, 24, 29));

		Row rowFirstText = sheet.createRow(3);
		Cell cellFirstText = rowFirstText.createCell(0);
		if (routes.get(0).getUser() != null) {
			cellFirstText.setCellValue("Мы, нижеподписавшиеся: представитель Перевозчика "
					+ routes.get(0).getUser().getCompanyName() + " , в лице директора " + sheffName
					+ " действующего на основании Устава одной стороны, и представитель Заказчика  ЗАО «Доброном» в лице заместителя генерального директора по логистике Якубова Евгения Владимировича, действующего на основании доверенности № 3 от 31.12.2022 года, с другой стороны, составили настоящий акт о том, что услуги, оказанные на основании договора перевозки №"
					+ numContractTarget + " от " + dateContract
					+ " выполнены в полном объеме и стороны претензий друг к другу не имеют.");
		} else {
			cellFirstText.setCellValue("!!!!");
		}
		cellFirstText.setCellStyle(styleForText);
		sheet.addMergedRegion(new CellRangeAddress(3, 5, 0, 29));

		Row rowHeaderTable = sheet.createRow(7);
		Cell dateHeader = rowHeaderTable.createCell(0);
		dateHeader.setCellValue("Дата загрузки");
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 0, 1));
		dateHeader.setCellStyle(styleForOthers);
		Cell dateHeaderUnload = rowHeaderTable.createCell(2);
		dateHeaderUnload.setCellValue("Дата выгрузки");
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 2, 3));
		dateHeaderUnload.setCellStyle(styleForOthers);
		Cell numRouteHeader = rowHeaderTable.createCell(4);
		numRouteHeader.setCellValue("№ рейса");
		numRouteHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 4, 5));
		Cell routeDirectionHeader = rowHeaderTable.createCell(6);
		routeDirectionHeader.setCellValue("Маршрут");
		routeDirectionHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 6, 13));//
		Cell numTruckHeader = rowHeaderTable.createCell(14);
		numTruckHeader.setCellValue("№ ТС");
		numTruckHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 14, 15));
		Cell numCargoListHeader = rowHeaderTable.createCell(16);
		numCargoListHeader.setCellValue("№ Путевого листа");
		numCargoListHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 16, 17));
		Cell numCMRHeader = rowHeaderTable.createCell(18);
		numCMRHeader.setCellValue("№ ТТН/CMR");
		numCMRHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 18, 21));
		Cell massCargoHeader = rowHeaderTable.createCell(22);
		massCargoHeader.setCellValue("Объем Груза (тонн)");
		massCargoHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 22, 22));
		Cell costHeader = rowHeaderTable.createCell(23);
		costHeader.setCellValue("Сумма без НДС");
		costHeader.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(7, 10, 23, 24));
		if (isNDS) {
			Cell ndsHeader = rowHeaderTable.createCell(25);
			ndsHeader.setCellValue("Сумма НДС");
			ndsHeader.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(7, 10, 25, 26));
			Cell costWayHeader = rowHeaderTable.createCell(27);
			costWayHeader.setCellValue("Платные дороги");
			costWayHeader.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(7, 10, 27, 27));
			Cell costAndNDSHeader = rowHeaderTable.createCell(28);
			costAndNDSHeader.setCellValue("Итого");
			costAndNDSHeader.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(7, 10, 28, 29));
		} else {
			Cell costWayHeader = rowHeaderTable.createCell(25);
			costWayHeader.setCellValue("Платные дороги");
			costWayHeader.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(7, 10, 25, 27));
			Cell costAndNDSHeader = rowHeaderTable.createCell(28);
			costAndNDSHeader.setCellValue("Итого");
			costAndNDSHeader.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(7, 10, 28, 29));
		}

		int i = 11;
		double cost = 0.0;
		double nds = 0.0;
		double way = 0.0;
		double costAndNdsValue = 0.0;
		String currency = routes.get(0).getStartCurrency();
		for (Route route : routes) {	
			Row rowTable = sheet.createRow(i);
			//определяем величину смещения
			//максимальный размер по абсолютному значения = 80 символов; Свыше - добавляем еще одну строку!
//			if(route.getCmr().length()>81) {
//				int numrow = (int) route.getCmr().length()/27;
//				numrow = numrow +1-3;
//				//0610931; 0610931; 0610931; 0610931; 0610931; 0610931; 0610931; 0610931; 0610931; 0610931;
//				int inPoints = 16 + numrow*16;
//				rowTable.setHeightInPoints(inPoints); // остановился тут
//			}
			
			Cell date = rowTable.createCell(0);
			date.setCellValue(route.getDateLoadPreviously().format(formatter));
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 0, 1));
			date.setCellStyle(styleForOthers);
			Cell dateUnload = rowTable.createCell(2);
//		    dateUnload.setCellValue(LocalDate.parse(route.getDateUnload()).format(formatter));
			dateUnload.setCellValue(route.getDateUnload());
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 2, 3));
			dateUnload.setCellStyle(styleForOthers);
			Cell numRoute = rowTable.createCell(4);
			numRoute.setCellValue(route.getIdRoute());
			numRoute.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 4, 5));
			Cell routeDirection = rowTable.createCell(6);
			routeDirection.setCellValue(route.getRouteDirection());
			routeDirection.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 6, 13));
			Cell numTruck = rowTable.createCell(14);
			if (route.getNumTruckAndTrailer() != null) {
				numTruck.setCellValue(route.getNumTruckAndTrailer());
			} else {
				numTruck.setCellValue(route.getTruck().getNumTruck() + "/" + route.getTruck().getNumTrailer());
			}
			numTruck.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 14, 15));
			Cell numCargoList = rowTable.createCell(16);
			numCargoList.setCellValue(route.getNumWayList());
			numCargoList.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 16, 17));
			Cell numCMR = rowTable.createCell(18);
			numCMR.setCellValue(route.getCmr()); // тут собака зарыта
			numCMR.setCellStyle(styleForOthers); // тут собака зарыта			
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 18, 21));
			Cell massCargo = rowTable.createCell(22);
			massCargo.setCellValue(route.getTotalCargoWeight());
			massCargo.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 22, 22));
			Cell costCell = rowTable.createCell(23);
			cost = cost + route.getFinishPrice();
			costCell.setCellValue(route.getFinishPrice());
			costCell.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 23, 24));
			Cell ndsCell = rowTable.createCell(25);
			if (isNDS) {
				double totalNDS = route.getFinishPrice() * 20.0 / 100.0;
				nds = nds + totalNDS;
				way = way + Double.parseDouble(route.getCostWay());
				ndsCell.setCellValue(totalNDS);
				ndsCell.setCellStyle(styleForOthers);
				sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 25, 26));
				Cell costWayVal = rowTable.createCell(27);
				costWayVal.setCellValue(roundВouble(Double.parseDouble(route.getCostWay()), 2));
				costWayVal.setCellStyle(styleForOthers);
				sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 27, 27));
				Cell costAndNDS = rowTable.createCell(28);
				costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
						+ Double.parseDouble(route.getCostWay());
				costAndNDS.setCellValue(
						route.getFinishPrice() + totalNDS + roundВouble(Double.parseDouble(route.getCostWay()), 2));
				costAndNDS.setCellStyle(styleForOthers);
				sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 28, 29));
				i = i + 3;
			} else {
				double totalNDS = 0.0;
				way = way + Double.parseDouble(route.getCostWay());
				nds = nds + totalNDS;
				Cell costWayVal = rowTable.createCell(25);
				costWayVal.setCellValue(roundВouble(Double.parseDouble(route.getCostWay()), 2));
				costWayVal.setCellStyle(styleForOthers);
				sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 25, 27));
				Cell costAndNDS = rowTable.createCell(28);
				costAndNdsValue = costAndNdsValue + route.getFinishPrice() + totalNDS
						+ Double.parseDouble(route.getCostWay());
				costAndNDS.setCellValue(route.getFinishPrice() + roundВouble(totalNDS, 2)
						+ roundВouble(Double.parseDouble(route.getCostWay()), 2));
				costAndNDS.setCellStyle(styleForOthers);
				sheet.addMergedRegion(new CellRangeAddress(i, i + 2, 28, 29));
				i = i + 3;
			}
		}

		Row total = sheet.createRow(i);
		Cell text = total.createCell(0);
		text.setCellValue("Итого:");
		sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 0, 22));
		text.setCellStyle(styleForOthersNotCenter);
		Cell costTotal = total.createCell(23);
		costTotal.setCellValue(roundВouble(cost, 2));
		costTotal.setCellStyle(styleForOthers);
		sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 23, 24));
		if (isNDS) {
			Cell ndsTotal = total.createCell(25);
			ndsTotal.setCellValue(roundВouble(nds, 2));
			ndsTotal.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 25, 26));
			Cell costWayTotal = total.createCell(27);
			costWayTotal.setCellValue(roundВouble(way, 2));
			costWayTotal.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 27, 27));
			Cell costAndNDSTotal = total.createCell(28);
			costAndNDSTotal.setCellValue(roundВouble(costAndNdsValue, 2));
			costAndNDSTotal.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 28, 29));
		} else {
			Cell costWayTotal = total.createCell(25);
			costWayTotal.setCellValue(roundВouble(way, 2));
			costWayTotal.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 25, 27));
			Cell costAndNDSTotal = total.createCell(28);
			costAndNDSTotal.setCellValue(roundВouble(costAndNdsValue, 2));
			costAndNDSTotal.setCellStyle(styleForOthers);
			sheet.addMergedRegion(new CellRangeAddress(i, i + 1, 28, 29));
		}

		PropertyTemplate propertyTemplate = new PropertyTemplate(); // таблица заполненная полностью
		propertyTemplate.drawBorders(new CellRangeAddress(7, i + 1, 0, 29), BorderStyle.MEDIUM, BorderExtent.ALL);
		propertyTemplate.applyBorders(sheet);
		double allCost = 0.0;
		if (isNDS) {
			Row fineshNDS = sheet.createRow(i + 3);
			Cell fineshNDSCell = fineshNDS.createCell(0);
//		    RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"),
//		            RuleBasedNumberFormat.SPELLOUT);		    
//		    fineshNDSCell.setCellValue("В том числе НДС: "+nf.format(roundВouble(nds, 2)) +" "+ routes.get(0).getStartCurrency());
			fineshNDSCell.setCellValue(
					"В том числе НДС: " + new FwMoney(roundВouble(nds, 2), routes.get(0).getStartCurrency()).num2str());
			sheet.addMergedRegion(new CellRangeAddress(i + 3, i + 3, 0, 22));
			fineshNDSCell.setCellStyle(styleForAttantionText);

			Row fineshCost = sheet.createRow(i + 4);
			Cell fineshCostCell = fineshCost.createCell(0);
			allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
//		    fineshCostCell.setCellValue("Всего оказано услуг на сумму с НДС: "+nf.format(allCost) +" "+ routes.get(0).getStartCurrency());
			fineshCostCell.setCellValue("Всего оказано услуг на сумму с НДС: "
					+ new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str());
			sheet.addMergedRegion(new CellRangeAddress(i + 4, i + 4, 0, 22));
			fineshCostCell.setCellStyle(styleForAttantionText);
		} else {
//		    RuleBasedNumberFormat nf = new RuleBasedNumberFormat(Locale.forLanguageTag("ru"),
//		            RuleBasedNumberFormat.SPELLOUT);

			Row fineshCost = sheet.createRow(i + 4);
			Cell fineshCostCell = fineshCost.createCell(0);
			allCost = roundВouble(cost, 2) + roundВouble(nds, 2) + roundВouble(way, 2);
//		    fineshCostCell.setCellValue("Всего оказано услуг на сумму без НДС: "+nf.format(allCost) +" "+ routes.get(0).getStartCurrency());
			fineshCostCell.setCellValue("Всего оказано услуг на сумму без НДС: "
					+ new FwMoney(roundВouble(allCost, 2), routes.get(0).getStartCurrency()).num2str());
			sheet.addMergedRegion(new CellRangeAddress(i + 4, i + 4, 0, 22));
			fineshCostCell.setCellStyle(styleForAttantionText);
		}

		Row rowFooter = sheet.createRow(i + 6);
		Cell cellFooterFrom = rowFooter.createCell(0);
		cellFooterFrom.setCellValue("Перевозчик:");
		cellFooterFrom.setCellStyle(styleForText);
		sheet.addMergedRegion(new CellRangeAddress(i + 6, i + 6, 0, 1));
		Cell cellFooterTo = rowFooter.createCell(24);
		cellFooterTo.setCellValue("Заказчик:");
		cellFooterTo.setCellStyle(styleForText);
		sheet.addMergedRegion(new CellRangeAddress(i + 6, i + 6, 24, 25));

		Row rowFooterText = sheet.createRow(i + 7);
		Cell cellFooterTextFrom = rowFooterText.createCell(0);
		cellFooterTextFrom.setCellValue(requisitesCarrier);
		cellFooterTextFrom.setCellStyle(styleForTextRequisites);
		sheet.addMergedRegion(new CellRangeAddress(i + 7, i + 20, 0, 5));
		Cell cellFooterTextTo = rowFooterText.createCell(24);
		cellFooterTextTo.setCellValue("ЗАО Доброном: Республика Беларусь,\r\n"
				+ "220112, г. Минск, ул. Янки Лучины, 5\r\n" + "УНП 191178504, ОКПО 378869615000\r\n"
				+ "р/с BY61ALFA30122365100050270000 ( BYN)                                                                                                                                     открытый  в Закрытое акционерное общество «Альфа-банк» \r\n"
				+ "Юридический адрес: Ул. Сурганова, 43-47                                                                                                                                           220013 Минск, Республика Беларусь\r\n"
				+ "УНП 101541947\r\n" + "SWIFT – ALFABY2X\r\n" + "р/с  BY24ALFA30122365100010270000 (USD)\r\n"
				+ "р/с  BY09ALFA30122365100020270000(EUR)\r\n" + "р/с BY91 ALFA 3012 2365 1000 3027 0000 (RUB.)");
		cellFooterTextTo.setCellStyle(styleForTextRequisites);
		sheet.addMergedRegion(new CellRangeAddress(i + 7, i + 20, 24, 29));

		Row rowFooterStamp = sheet.createRow(i + 22);
		Cell cellFooterStampFrom = rowFooterStamp.createCell(2);
		cellFooterStampFrom.setCellValue(sheffName);
		cellFooterStampFrom.setCellStyle(styleForText);
		sheet.addMergedRegion(new CellRangeAddress(i + 22, i + 22, 2, 7));
		Cell cellFooterStampTo = rowFooterStamp.createCell(24);
		cellFooterStampTo.setCellValue("_______________/ Е.В. Якубов/");
		cellFooterStampTo.setCellStyle(styleForText);
		sheet.addMergedRegion(new CellRangeAddress(i + 22, i + 22, 24, 29));
		
		
		// вписать всё в один лист при распечатке
		sheet.setFitToPage(true);
		PrintSetup ps = sheet.getPrintSetup();
		ps.setFitWidth((short) 1);
		ps.setFitHeight((short) 0);

		// альбомная ориентация
		sheet.getPrintSetup().setLandscape(true);
		// sheet.getPrintSetup().setPaperSize(HSSFPrintSetup.A4_PAPERSIZE);

		book.lockStructure();
		Act act = new Act();
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
		try {
			String appPath = request.getServletContext().getRealPath("");
			String fileName = routes.get(0).getUser().getCompanyName() + ".xlsx";
//			File file = new File(appPath + "resources/others/act.xlsx");
			File file = new File(appPath + "resources/others/" + fileName);
//	  		FileOutputStream fos = new FileOutputStream(file);
//	  		book.write(fos);
//	  		fos.flush();
			book.write(new FileOutputStream(file));
			book.close();
//			excel2pdf(appPath, fullName); // формирование пдф (в разр)
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	public File getReportFromInternationalManager(Set<Route> routes, HttpServletRequest request, Date start, Date finish) {
		List<Message> messages = messageService.getListMessageByPeriod(start, finish);
		List<Route> list = routes.stream().collect(Collectors.toList());
		// работа
		System.out.println("Старт импорта");
		System.out.println("Импорт " + list.size() + " маршрутов");
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet mySheet = (XSSFSheet) book.createSheet("Биржа");

		// создаём шрифты
		Font font = book.createFont();
		font.setFontName("Calibri");
		font.setBold(true);
		font.setFontHeightInPoints((short) 13);

		Font fontForOthers = book.createFont();
		fontForOthers.setFontName("Calibri");
		fontForOthers.setBold(true);
		fontForOthers.setFontHeightInPoints((short) 11);

		Font fontForText = book.createFont();
		fontForText.setFontName("Calibri");
		fontForText.setFontHeightInPoints((short) 11);

		// создаём стили
		CellStyle styleForHead = book.createCellStyle(); // стиль для шапки
		styleForHead.setFont(font);
		styleForHead.setAlignment(HorizontalAlignment.CENTER);
		styleForHead.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle styleForOthers = book.createCellStyle(); // стиль для доп информации
		styleForOthers.setFont(fontForOthers);
		styleForOthers.setAlignment(HorizontalAlignment.CENTER);
		styleForOthers.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthers.setWrapText(true);

		CellStyle styleForOthersNotCenter = book.createCellStyle(); // стиль для доп информации но без центровки
		styleForOthersNotCenter.setFont(fontForOthers);
		styleForOthersNotCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthersNotCenter.setWrapText(true);

		CellStyle styleForText = book.createCellStyle(); // стиль для обычного текста
		styleForText.setFont(fontForText);
		styleForText.setAlignment(HorizontalAlignment.CENTER);
		styleForText.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForText.setWrapText(true);

		CellStyle styleForTextRequisites = book.createCellStyle(); // стиль для текста с реквизитами
		styleForTextRequisites.setFont(fontForText);
		styleForTextRequisites.setVerticalAlignment(VerticalAlignment.DISTRIBUTED);
		styleForTextRequisites.setWrapText(true);

		CellStyle styleForAttantionText = book.createCellStyle(); // стлиль для жирного текста без выравнивания
		styleForAttantionText.setFont(fontForOthers);

		Row headerRow = mySheet.createRow(0); // шапка таблицы
		Cell сell0 = headerRow.createCell(0);
		сell0.setCellStyle(styleForHead);
		сell0.setCellValue("Направление");
		Cell сell01 = headerRow.createCell(1);
		сell01.setCellStyle(styleForHead);
		сell01.setCellValue("ID маршрута");
		Cell сell1 = headerRow.createCell(2);
		сell1.setCellStyle(styleForHead);
		сell1.setCellValue("Название маршрута");
		Cell сell2 = headerRow.createCell(3);
		сell2.setCellStyle(styleForHead);
		сell2.setCellValue("Дата загрузки");
		Cell сell3 = headerRow.createCell(4);
		сell3.setCellStyle(styleForHead);
		сell3.setCellValue("Время загрузки (планируемое)");
		Cell сell4 = headerRow.createCell(5);
		сell4.setCellStyle(styleForHead);
		сell4.setCellValue("Дата и время выгрузки");
		Cell сell5 = headerRow.createCell(6);
		сell5.setCellStyle(styleForHead);
		сell5.setCellValue("Выставляемая стоимость");
		Cell сell6 = headerRow.createCell(7);
		сell6.setCellStyle(styleForHead);
		сell6.setCellValue("Экономия");
		Cell сell7 = headerRow.createCell(8);
		сell7.setCellStyle(styleForHead);
		сell7.setCellValue("Перевозчик");
		Cell сell8 = headerRow.createCell(9);
		сell8.setCellStyle(styleForHead);
		сell8.setCellValue("Номер машины");
		Cell сell9 = headerRow.createCell(10);
		сell9.setCellStyle(styleForHead);
		сell9.setCellValue("Данные по водителю");
		Cell сell10 = headerRow.createCell(11);
		сell10.setCellStyle(styleForHead);
		сell10.setCellValue("Заказчик");
		Cell сell11 = headerRow.createCell(12);
		сell11.setCellStyle(styleForHead);
		сell11.setCellValue("Паллеты");
		Cell сell12 = headerRow.createCell(13);
		сell12.setCellStyle(styleForHead);
		сell12.setCellValue("Общий вес");
		Cell сell13 = headerRow.createCell(14);
		сell13.setCellStyle(styleForHead);
		сell13.setCellValue("Комментарии");
		Cell сell14 = headerRow.createCell(15);
		сell14.setCellStyle(styleForHead);
		сell14.setCellValue("Начальные стоимости перевозки");
		Cell сell15 = headerRow.createCell(16);
		сell15.setCellStyle(styleForHead);
		сell15.setCellValue("Статус");
		Cell сell16 = headerRow.createCell(17);
		сell16.setCellStyle(styleForHead);
		сell16.setCellValue("Колличество предложений");

		// второй лист
		XSSFSheet offers = (XSSFSheet) book.createSheet("История предложений");
		Row headerRowOffers = offers.createRow(0); // шапка таблицы
		Cell сellOffers0 = headerRowOffers.createCell(0);
		сellOffers0.setCellStyle(styleForHead);
		сellOffers0.setCellValue("ID маршрута");
		Cell сellOffers1 = headerRowOffers.createCell(1);
		сellOffers1.setCellStyle(styleForHead);
		сellOffers1.setCellValue("Перевозчик");
		Cell сellOffers2 = headerRowOffers.createCell(2);
		сellOffers2.setCellStyle(styleForHead);
		сellOffers2.setCellValue("Предложение");
		int k = 1;
		for (int i = 0; i < list.size(); i++) {
			Row rowI = mySheet.createRow(i + 1);
			Route route = list.get(i);
			System.out.println("Обработка маршрута " + route.getIdRoute());
			Cell сellI0 = rowI.createCell(0);
			сellI0.setCellStyle(styleForText);
			сellI0.setCellValue(route.getWay());
			Cell сellI01 = rowI.createCell(1);
			сellI01.setCellStyle(styleForHead);
			сellI01.setCellValue(route.getIdRoute());
			Cell сellI1 = rowI.createCell(2);
			сellI1.setCellStyle(styleForText);
			сellI1.setCellValue(route.getRouteDirection());
			Cell сellI2 = rowI.createCell(3);
			сellI2.setCellStyle(styleForText);
			сellI2.setCellValue(route.getSimpleDateStart());
			Cell сellI3 = rowI.createCell(4);
			сellI3.setCellStyle(styleForText);
			сellI3.setCellValue(route.getTimeLoadPreviously().toString());
			Cell сellI4 = rowI.createCell(5);
			сellI4.setCellStyle(styleForText);
			сellI4.setCellValue(
					route.getDateLoadPreviously().toString() + " " + route.getTimeLoadPreviously().toString());
			Cell сellI5 = rowI.createCell(6);
			сellI5.setCellStyle(styleForText);
			сellI5.setCellValue(route.getFinishPrice() + " " + route.getStartCurrency());
			Cell сellI6 = rowI.createCell(7);
			сellI6.setCellStyle(styleForText);
			if (route.getFinishPrice() != null) {
				сellI6.setCellValue(route.getStartPrice() != null ? route.getStartPrice() - route.getFinishPrice()
						: Integer.parseInt(route.getOptimalCost()) - route.getFinishPrice());
			} else {
				сellI6.setCellValue("-");
			}
			Cell сellI7 = rowI.createCell(8);
			сellI7.setCellStyle(styleForText);
			сellI7.setCellValue(route.getUser() != null ? route.getUser().getCompanyName() : null);
			Cell сellI8 = rowI.createCell(9);
			сellI8.setCellStyle(styleForText);
			сellI8.setCellValue(
					route.getTruck() != null ? route.getTruck().getNumTruck() + " / " + route.getTruck().getNumTrailer()
							: null);
			Cell сellI9 = rowI.createCell(10);
			сellI9.setCellStyle(styleForText);
			сellI9.setCellValue(
					route.getDriver() != null
							? route.getDriver().getSurname() + " " + route.getDriver().getName() + " "
									+ route.getDriver().getPatronymic()
							: null);
			Cell сellI10 = rowI.createCell(11);
			сellI10.setCellStyle(styleForText);
			сellI10.setCellValue(route.getCustomer() != null ? route.getCustomer() : null);
			Cell сellI11 = rowI.createCell(12);
			сellI11.setCellStyle(styleForText);
			сellI11.setCellValue(route.getTotalLoadPall());
			Cell сellI12 = rowI.createCell(13);
			сellI12.setCellStyle(styleForText);
			сellI12.setCellValue(route.getTotalCargoWeight());
			Cell сellI13 = rowI.createCell(14);
			сellI13.setCellStyle(styleForText);
			сellI13.setCellValue(route.getUserComments() != null ? route.getUserComments() : null);
			Cell сellI14 = rowI.createCell(15);
			сellI14.setCellStyle(styleForText);
			сellI14.setCellValue(route.getStartPrice() != null ? route.getStartPrice().toString() + " BYN"
					: route.getOptimalCost().toString() + " BYN");
			Cell сellI15 = rowI.createCell(16);
			сellI15.setCellStyle(styleForText);
			сellI15.setCellValue(route.getStatusRoute());
			List<Message> messagesList = new ArrayList<Message>();
			messages.stream().filter(m-> m.getIdRoute() != null && Integer.parseInt(m.getIdRoute()) == route.getIdRoute())
				.filter(m-> m.getToUser() == null && m.getCurrency() != null).forEach(m-> messagesList.add(m));
//			messageService.getListMessageByIdRoute(route.getIdRoute().toString()).stream()
//				.filter(m-> m.getToUser() == null && m.getCurrency() != null).forEach(m-> messagesList.add(m));
			Cell сellI16 = rowI.createCell(17);
			сellI16.setCellStyle(styleForText);
			сellI16.setCellValue(messagesList.size());

			for (int j = 0; j < messagesList.size(); j++) {
				Message message = messagesList.get(j);
				Cell сellI17 = rowI.createCell(18 + j);
				сellI17.setCellStyle(styleForText);
				сellI17.setCellValue(
						message.getCompanyName() + " - " + message.getText() + " " + message.getCurrency());

				Row rowIOffers = offers.createRow(k);
				Cell сellI0Offers = rowIOffers.createCell(0);// для второй страницы
				сellI0Offers.setCellStyle(styleForHead);
				сellI0Offers.setCellValue(route.getIdRoute());
				Cell сellI1Offers = rowIOffers.createCell(1);// для второй страницы
				сellI1Offers.setCellStyle(styleForText);
				сellI1Offers.setCellValue(message.getCompanyName());
				Cell сellI2Offers = rowIOffers.createCell(2);// для второй страницы
				сellI2Offers.setCellStyle(styleForText);
				сellI2Offers.setCellValue(message.getText() + " " + message.getCurrency());
				k++;
			}

		}
		File file = null;
		try {
			String appPath = request.getServletContext().getRealPath("");
			String fileName = "internationalManager.xlsx";
			file = new File(appPath + "resources/others/" + fileName);
			System.out.println(appPath + "resources/others/" + fileName);
//	  		FileOutputStream fos = new FileOutputStream(file);
//	  		book.write(fos);
//	  		fos.flush();
			book.write(new FileOutputStream(file));
			book.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return file;
	}

	// округляем числа до 2-х знаков после запятой
	private static double roundВouble(double value, int places) {
		double scale = Math.pow(10, places);
		return Math.round(value * scale) / scale;
	}

	private List<CustomRowHasExcel> custimRowHasExcelProcess(XSSFSheet excelSheet,
			Map<Long, List<MapResponse>> mapResult) {
		Long idWay = null;
		List<String> shopList = new ArrayList<String>();
		List<Double> distanceList = new ArrayList<Double>();
		List<Double> weightList = new ArrayList<Double>();
		List<CustomRowHasExcel> customRowHasExcels = new ArrayList<CustomRowHasExcel>();
		for (int i = 1; i <= excelSheet.getLastRowNum(); i++) {// не читаем первую строку
			XSSFRow row = excelSheet.getRow(i);
			String numShop = null;// определяем магаз! пустые ячейки определяет как null
			Double weigth = null;
			if (row.getCell(4) == null || row.getCell(4).toString().isEmpty()) {// опредиляем где закончился маршрут по
																				// столбцу с мазагинами
				customRowHasExcels.add(new CustomRowHasExcel(idWay, shopList, distanceList, weightList));
				shopList = new ArrayList<String>();
				distanceList = new ArrayList<Double>();
				weightList = new ArrayList<Double>();
				continue;
			} else if (i == excelSheet.getLastRowNum()) {// обработка последней строки как последней строки, что
															// очевидно!
				if (row.getCell(4) != null) {
					row.getCell(4).setCellType(CellType.STRING);
					numShop = row.getCell(4).toString();
					shopList.add(numShop);

				}
				if (row.getCell(7) != null && !row.getCell(7).toString().isEmpty()
						|| row.getCell(7) != null && row.getCell(7).toString().isEmpty() && row.getCell(4) != null) {
					if (row.getCell(7) != null && row.getCell(7).toString().isEmpty() && row.getCell(4) != null) {
						weigth = 0.0;
						weightList.add(weigth);
					} else {
						row.getCell(7).setCellType(CellType.STRING);
						weigth = Double.parseDouble(row.getCell(7).toString());
						weightList.add(weigth);
					}
				}
				customRowHasExcels.add(new CustomRowHasExcel(idWay, shopList, distanceList, weightList));
				shopList = new ArrayList<String>();
				distanceList = new ArrayList<Double>();
				weightList = new ArrayList<Double>();
				continue;
			} // конец обработки последнего маршрута
			if (row.getCell(3) != null && !row.getCell(3).toString().isEmpty()) {
				row.getCell(3).setCellType(CellType.STRING);
				idWay = Long.parseLong(row.getCell(3).toString());
			}

			if (row.getCell(4) != null) {
				row.getCell(4).setCellType(CellType.STRING);
				numShop = row.getCell(4).toString();
				shopList.add(numShop);

			}

			if (row.getCell(7) != null && !row.getCell(7).toString().isEmpty()
					|| row.getCell(7) != null && row.getCell(7).toString().isEmpty() && row.getCell(4) != null) {
				if (row.getCell(7) != null && row.getCell(7).toString().isEmpty() && row.getCell(4) != null) {
					weigth = 0.0;
					weightList.add(weigth);
				} else {
					row.getCell(7).setCellType(CellType.STRING);
					weigth = Double.parseDouble(row.getCell(7).toString());
					weightList.add(weigth);
				}
			}
		}
		// тут проходимся по полученному CustomRowHasExcel и записываем в нее расстояния
		List<CustomRowHasExcel> result = new ArrayList<CustomRowHasExcel>();
		for (CustomRowHasExcel customRowHasExcel : customRowHasExcels) {
			List<MapResponse> mapResponses = mapResult.get(customRowHasExcel.getId());
			if (mapResponses == null) {
				customRowHasExcel.setDistance(null);
				result.add(customRowHasExcel);
				continue;
			}
			List<Double> distance = new ArrayList<Double>();
			for (MapResponse mapResponse : mapResponses) {
				distance.add(roundВouble(mapResponse.getDistance() / 1000, 1));
			}
			customRowHasExcel.setDistance(distance);
			result.add(customRowHasExcel);
		}
		;
		return result;

	}

	
	
	/**
	 * Создание горизонтального развоза для Оли
	 * @param result
	 * @param request
	 */
	private void createRazvoz(List<CustomRowHasExcel> result, HttpServletRequest request) {
		// тут формируем сам ексель.
		// позже засунуть всё в отдельный метод
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet mySheet = (XSSFSheet) book.createSheet("Акт");

		// создаём шрифты
		Font font = book.createFont();
		font.setFontName("Calibri");
		font.setBold(true);
		font.setFontHeightInPoints((short) 13);

		Font fontForOthers = book.createFont();
		fontForOthers.setFontName("Calibri");
		fontForOthers.setBold(true);
		fontForOthers.setFontHeightInPoints((short) 11);

		Font fontForText = book.createFont();
		fontForText.setFontName("Calibri");
		fontForText.setFontHeightInPoints((short) 11);

		// создаём стили
		CellStyle styleForHead = book.createCellStyle(); // стиль для шапки
		styleForHead.setFont(font);
		styleForHead.setAlignment(HorizontalAlignment.CENTER);
		styleForHead.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle styleForOthers = book.createCellStyle(); // стиль для доп информации
		styleForOthers.setFont(fontForOthers);
		styleForOthers.setAlignment(HorizontalAlignment.CENTER);
		styleForOthers.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthers.setWrapText(true);

		CellStyle styleForOthersNotCenter = book.createCellStyle(); // стиль для доп информации но без центровки
		styleForOthersNotCenter.setFont(fontForOthers);
		styleForOthersNotCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthersNotCenter.setWrapText(true);

		CellStyle styleForText = book.createCellStyle(); // стиль для обычного текста
		styleForText.setFont(fontForText);
		styleForText.setAlignment(HorizontalAlignment.CENTER);
		styleForText.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForText.setWrapText(true);

		CellStyle styleForTextRequisites = book.createCellStyle(); // стиль для текста с реквизитами
		styleForTextRequisites.setFont(fontForText);
		styleForTextRequisites.setVerticalAlignment(VerticalAlignment.DISTRIBUTED);
		styleForTextRequisites.setWrapText(true);

		CellStyle styleForAttantionText = book.createCellStyle(); // стлиль для жирного текста без выравнивания
		styleForAttantionText.setFont(fontForOthers);

		Row headerRow = mySheet.createRow(0); // шапка таблицы
		Cell idHeaderCell = headerRow.createCell(0);
		idHeaderCell.setCellStyle(styleForHead);
		idHeaderCell.setCellValue("ID маршрута");
		// шапки для номеров магазинов, расстояний и весов будут формироваться в конце

		// определяем максимальную длинну для объединения названий
		// тупо самое большое колличество магазов
		int maxNumPoints = 0;
		for (CustomRowHasExcel customRowHasExcel : result) {
			if (maxNumPoints < customRowHasExcel.getShop().size()) {
				maxNumPoints = customRowHasExcel.getShop().size();
			}
		}

		// заполняем строки
		int group2 = 0;
		int group3 = 0;
		for (int i = 0; i < result.size(); i++) {
			CustomRowHasExcel customRowHasExcelI = result.get(i);
			Row rowI = mySheet.createRow(i + 1);
			Cell сellI = rowI.createCell(0);
			сellI.setCellStyle(styleForText);
			сellI.setCellValue(customRowHasExcelI.getId());// заполнена колонка id

			for (int j = 0; j < customRowHasExcelI.getShop().size(); j++) {// заполняем магазины
				String numShop = customRowHasExcelI.getShop().get(j);
				int k = j + 1;
				Cell сellK = rowI.createCell(k);
				сellK.setCellStyle(styleForText);
				сellK.setCellValue(numShop);
			}

			group2 = 1 + maxNumPoints + 1;// откуда будет начинаться вторая часть с расстояниями
			Double distanceSumm = 0.0;
			for (int j = 0; j < customRowHasExcelI.getDistance().size(); j++) {// заполняем расстояния
				Double distance = customRowHasExcelI.getDistance().get(j);
				distanceSumm = distanceSumm + distance;
				int k = group2 + j;
				Cell сellK = rowI.createCell(k);
				сellK.setCellStyle(styleForText);
				сellK.setCellValue(roundВouble(distance, 1));
			}

			Cell сellSumm = rowI.createCell(group2 + maxNumPoints - 1);
			сellSumm.setCellStyle(styleForHead);
			сellSumm.setCellValue(roundВouble(distanceSumm, 0));

			group3 = 1 + maxNumPoints + 1 + maxNumPoints + 1;// откуда будет начинаться третья часть с весами

			for (int j = 0; j < customRowHasExcelI.getWeight().size(); j++) {// заполняем расстояния
				Double weigth = customRowHasExcelI.getWeight().get(j);
				int k = group3 + j;
				Cell сellK = rowI.createCell(k);
				сellK.setCellStyle(styleForText);
				сellK.setCellValue(roundВouble(weigth, 1));
			}
		}

		mySheet.addMergedRegion(new CellRangeAddress(0, 0, 1, group2 - 2));
		mySheet.addMergedRegion(new CellRangeAddress(0, 0, group2, group3 - 3));
		mySheet.addMergedRegion(new CellRangeAddress(0, 0, group3, group3 + maxNumPoints));

		Cell shopHeaderCell = headerRow.createCell(1);
		shopHeaderCell.setCellStyle(styleForHead);
		shopHeaderCell.setCellValue("Магазины");

		Cell wayHeaderCell = headerRow.createCell(group2);
		wayHeaderCell.setCellStyle(styleForHead);
		wayHeaderCell.setCellValue("Расстояния");

		Cell сellSumm = headerRow.createCell(group2 + maxNumPoints - 1);
		сellSumm.setCellStyle(styleForHead);
		сellSumm.setCellValue("Итог");

		Cell weigthHeaderCell = headerRow.createCell(group3);
		weigthHeaderCell.setCellStyle(styleForHead);
		weigthHeaderCell.setCellValue("Тоннаж");

		try {
			String appPath = request.getServletContext().getRealPath("");
			String fileName = "razvoz.xlsx";
			File file = new File(appPath + "resources/others/" + fileName);
//					System.out.println(appPath + "resources/others/" + fileName);
//			  		book.write(fos);
//			  		fos.flush();
			book.write(new FileOutputStream(file));
			book.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void createRazvozForJa(Map<Long, MapResponse> mapResult, HttpServletRequest request) {
		// тут формируем сам ексель.
		// позже засунуть всё в отдельный метод
		XSSFWorkbook book = new XSSFWorkbook();
		XSSFSheet mySheet = (XSSFSheet) book.createSheet("1700");

		// создаём шрифты
		Font font = book.createFont();
		font.setFontName("Calibri");
		font.setBold(true);
		font.setFontHeightInPoints((short) 13);

		Font fontForOthers = book.createFont();
		fontForOthers.setFontName("Calibri");
		fontForOthers.setBold(true);
		fontForOthers.setFontHeightInPoints((short) 11);

		Font fontForText = book.createFont();
		fontForText.setFontName("Calibri");
		fontForText.setFontHeightInPoints((short) 11);

		// создаём стили
		CellStyle styleForHead = book.createCellStyle(); // стиль для шапки
		styleForHead.setFont(font);
		styleForHead.setAlignment(HorizontalAlignment.CENTER);
		styleForHead.setVerticalAlignment(VerticalAlignment.CENTER);

		CellStyle styleForOthers = book.createCellStyle(); // стиль для доп информации
		styleForOthers.setFont(fontForOthers);
		styleForOthers.setAlignment(HorizontalAlignment.CENTER);
		styleForOthers.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthers.setWrapText(true);

		CellStyle styleForOthersNotCenter = book.createCellStyle(); // стиль для доп информации но без центровки
		styleForOthersNotCenter.setFont(fontForOthers);
		styleForOthersNotCenter.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForOthersNotCenter.setWrapText(true);

		CellStyle styleForText = book.createCellStyle(); // стиль для обычного текста
		styleForText.setFont(fontForText);
		styleForText.setAlignment(HorizontalAlignment.CENTER);
		styleForText.setVerticalAlignment(VerticalAlignment.CENTER);
		styleForText.setWrapText(true);

		CellStyle styleForTextRequisites = book.createCellStyle(); // стиль для текста с реквизитами
		styleForTextRequisites.setFont(fontForText);
		styleForTextRequisites.setVerticalAlignment(VerticalAlignment.DISTRIBUTED);
		styleForTextRequisites.setWrapText(true);

		CellStyle styleForAttantionText = book.createCellStyle(); // стлиль для жирного текста без выравнивания
		styleForAttantionText.setFont(fontForOthers);

		Row headerRow = mySheet.createRow(0); // шапка таблицы
		Cell idHeaderCell = headerRow.createCell(0);		
		idHeaderCell.setCellStyle(styleForHead);
		idHeaderCell.setCellValue("");
		// шапки для номеров магазинов, расстояний и весов будут формироваться в конце

		Row valueRow = mySheet.createRow(1); 
		int i = 1;
		for (Map.Entry<Long, MapResponse> entry: mapResult.entrySet()) {
			Cell cellI = valueRow.createCell(i);
			cellI.setCellValue(entry.getValue().getDistance());
			Cell cellIHead = headerRow.createCell(i);
			cellIHead.setCellValue(entry.getKey());
			i++;
		}	
		
		try {
			String appPath = request.getServletContext().getRealPath("");
			String fileName = "1700.xlsx";
			File file = new File(appPath + "resources/others/" + fileName);
//					System.out.println(appPath + "resources/others/" + fileName);
//			  		book.write(fos);
//			  		fos.flush();
			book.write(new FileOutputStream(file));
			book.close();
			System.err.println(appPath + "resources/others/");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}

class CustomRowHasExcel {
	private Long id;
	private List<String> shop;
	private List<Double> distance;
	private List<Double> weight;

	/**
	 * @param id
	 * @param shop
	 * @param distance
	 * @param weight
	 */
	public CustomRowHasExcel(Long id, List<String> shop, List<Double> distance, List<Double> weight) {
		super();
		this.id = id;
		this.shop = shop;
		this.distance = distance;
		this.weight = weight;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public List<String> getShop() {
		return shop;
	}

	public void setShop(List<String> shop) {
		this.shop = shop;
	}

	public List<Double> getWeight() {
		return weight;
	}

	public void setWeight(List<Double> weight) {
		this.weight = weight;
	}

	public List<Double> getDistance() {
		return distance;
	}

	public void setDistance(List<Double> distance) {
		this.distance = distance;
	}

	public void printString() {
		System.out.print(id + " ");
		System.out.print("| ");
		shop.forEach(s -> System.out.print(s + " "));
		System.out.print("| ");
		if (distance == null) {
			System.err.print("null");
		} else {
			distance.forEach(d -> System.err.print(d + " "));
		}
		System.out.print("| ");
		weight.forEach(w -> System.out.print(w + " "));
	}

}