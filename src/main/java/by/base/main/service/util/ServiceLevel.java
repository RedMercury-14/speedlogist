package by.base.main.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import by.base.main.model.Order;
import by.base.main.model.OrderLine;
import by.base.main.model.OrderProduct;
import by.base.main.model.Product;
import by.base.main.service.OrderProductService;
import by.base.main.service.ProductService;

/**
 * Класс отвечает за обработку информации 
 * <br>и предоставление отчётов ServiceLevel
 * <br>
 * <br>Отвечает за формирование самих отчётов
 * @author Dima Hrushevski
 *
 */
@Service
public class ServiceLevel {
	
	@Autowired
	private OrderProductService orderProductService;
	
	@Autowired
	private ProductService productService;
	
	/*
	 * 1. + Сначала разрабатываем метод который по дате определяет какие контракты должны быть заказаны в этот день (список Schedule) + 
	 * 2. + Разрабатываем метод, который принимает список кодов контрактов и по ним отдаёт заказы, в указаный период от текущей даты на 7 недель вперед
	 * 3. + Суммируем заказы по каждому коду контракта
	 * 4. формируем отчёт в excel и отправляем на почту 
	 */
	public File checkingOrdersForORLNeeds(List<Order> orders, Date dateOrder, String appPath) throws IOException {
		orders.sort(Comparator.comparing(Order::getMarketContractType)); // групируем номера контрактов
		int sizeOrders = orders.size();
		int sizeVoidOrder = 0;
		System.out.println(Date.valueOf(dateOrder.toLocalDate().minusDays(1)));
		Map <Integer, Integer> orderProductsORL = orderProductService.getOrderProductMapHasDate(Date.valueOf(dateOrder.toLocalDate().minusDays(1))); // что заказали ОРЛ
		System.out.println(orderProductsORL);
		List<DataOrderHasNumContract> dataOrderHasNumContracts = new ArrayList<DataOrderHasNumContract>(); // лист с результатами сложений заказов относительно кода контракта

		//формируем список с объектом data для удобного формирования екселя по номерам контактор
		//он суммирует данные по одному коду контракта
		Map <Long, Double> orderProductsManagerFact = new HashMap<Long, Double>(); // что заказали закупки (суммируется по всем заказам)
		List<Integer> idOrders = new ArrayList<Integer>();
		
		String codeContract = null;
		Order orderBefore = null;
		for (Order order : orders) {	
			if(codeContract != null && codeContract.equals(order.getMarketContractType())) {
				for (Entry<Long, Double> entry: order.getOrderLinesMap().entrySet()) {
					Double factQuantityOld = orderProductsManagerFact.get(entry.getKey());
					if(factQuantityOld != null) {
						orderProductsManagerFact.put(entry.getKey(), factQuantityOld+entry.getValue());
					}else {
						orderProductsManagerFact.put(entry.getKey(), entry.getValue());
					}
				}
				idOrders.add(order.getIdOrder());
			}else {
				if(!orderProductsManagerFact.isEmpty()) {
					dataOrderHasNumContracts.add(new DataOrderHasNumContract(orderProductsORL, orderProductsManagerFact, codeContract, idOrders, orderBefore.getCounterparty()));
					orderProductsManagerFact = new HashMap<Long, Double>();
					idOrders = new ArrayList<Integer>();
					
				}
				codeContract = order.getMarketContractType();
				orderProductsManagerFact.putAll(order.getOrderLinesMap());		
				idOrders.add(order.getIdOrder());
			}
			sizeVoidOrder++;
			if(sizeOrders == sizeVoidOrder){ // определяем последний элемент и записываем
				dataOrderHasNumContracts.add(new DataOrderHasNumContract(orderProductsORL, orderProductsManagerFact, codeContract, idOrders, order.getCounterparty()));
				orderProductsManagerFact = new HashMap<Long, Double>();
				idOrders = new ArrayList<Integer>();
			}
			orderBefore = order;
		}		
//		dataOrderHasNumContracts.forEach(d-> System.out.println(d));
		//закончили формирование сводного списка
		String dateReport = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
		String fileName = "SLevel.xlsx";		
		System.err.println(appPath + "resources/others/");
		
		
		
		return exportToExcel(dataOrderHasNumContracts, orderProductsORL, appPath + "resources/others/" + fileName);		
	}
	
	/**
	 * 
	 * @param orders
	 * @param dateOrder
	 * @param appPath
	 * @return
	 * @throws IOException
	 */
	public File orderBalanceHasDates(List<Order> orders, Date dateStart, Date dateFinish, String filePath) throws IOException {
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Отчёт по перемещению");

	    // Создание заголовков
	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("idOrder");
	    headerRow.createCell(1).setCellValue("Код маркета");
	    headerRow.createCell(2).setCellValue("Контрагент");
	    headerRow.createCell(3).setCellValue("Дата в слотах");
	    headerRow.createCell(4).setCellValue("Склад/рампа");
	    headerRow.createCell(5).setCellValue("Менеджер");
	    headerRow.createCell(6).setCellValue("Информация по остаткам");
	    headerRow.createCell(7).setCellValue("Паллет в заказе");
	    

	    // Применение фильтров
	    sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 6));
	    
	    //фиксируем верхнюю строку при скроле
	    sheet.createFreezePane(0, 1);

	    int rowNum = 1;

	    // Заполнение данными 1 страницы
	    for (Order order : orders) {

	    	Row row = sheet.createRow(rowNum++);
	    	row.createCell(0).setCellValue(order.getIdOrder());
            row.createCell(1).setCellValue(order.getMarketNumber());
            row.createCell(2).setCellValue(order.getCounterparty());
            row.createCell(3).setCellValue(order.getTimeUnload() != null ? order.getTimeUnload()+"" : "Oтсутствует в слотах");
            row.createCell(4).setCellValue(order.getIdRamp() != null ? order.getIdRamp()+"" : "Oтсутствует в слотах");
            row.createCell(5).setCellValue(order.getLoginManager());
            row.createCell(6).setCellValue(order.getSlotInfo());
            row.createCell(7).setCellValue(Integer.parseInt(order.getPall()));
            
            
	    }

	    // Установка автоширины для всех столбцов
	    for (int i = 0; i < 8; i++) {
	        sheet.autoSizeColumn(i);
	    }

	    // Запись в файл
	    File excelFile = new File(filePath);
	    try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
	        workbook.write(fileOut);
	    }

	    workbook.close();
	    return excelFile; // Возвращаем созданный файл
	}
	
	public File exportToExcel(List<DataOrderHasNumContract> dataOrderHasNumContracts, Map<Integer, Integer> orderProductsORL, String filePath) throws IOException {
	    Map<Integer, Product> products = productService.getAllProductMap();
	    Workbook workbook = new XSSFWorkbook();
	    Sheet sheet = workbook.createSheet("Отчёт относительно заказов");

	    // Создание заголовков
	    Row headerRow = sheet.createRow(0);
	    headerRow.createCell(0).setCellValue("Поставщик");
	    headerRow.createCell(1).setCellValue("Код контракта");
	    headerRow.createCell(2).setCellValue("Код товара");
	    headerRow.createCell(3).setCellValue("Товар");
	    headerRow.createCell(4).setCellValue("Заказ ОРЛ");
	    headerRow.createCell(5).setCellValue("Заказ менеджера");
	    headerRow.createCell(6).setCellValue("id Заказов");
	    

	    // Применение фильтров
	    sheet.setAutoFilter(new CellRangeAddress(0, 0, 0, 6));
	    
	    //фиксируем верхнюю строку при скроле
	    sheet.createFreezePane(0, 1);

	    int rowNum = 1;

	    // Заполнение данными 1 страницы
	    for (DataOrderHasNumContract contract : dataOrderHasNumContracts) {
	        String counterparty = contract.counterparty;
	        String numContract = contract.numContract;

	        // Обработка orderProductsManagerFact
	        for (Map.Entry<Long, Double> managerEntry : contract.orderProductsManagerFact.entrySet()) {
	            Long productCode = managerEntry.getKey();
	            Double productValue = managerEntry.getValue();

	            // Обработка orderProductsORL
	            Integer orlValue = orderProductsORL.get(productCode.intValue());

	            // Создание строки
	            Row row = sheet.createRow(rowNum++);
	            row.createCell(0).setCellValue(counterparty);
	            row.createCell(1).setCellValue(numContract);
	            row.createCell(2).setCellValue(productCode);
	            row.createCell(3).setCellValue(products.get(productCode.intValue()) == null ? "Товар отсутствует в базе данных" : products.get(productCode.intValue()).getName());
	            
	            if (orlValue != null) {
	                row.createCell(4).setCellValue(orlValue);
	            } else {
	                row.createCell(4).setCellValue("Отсутствует заказ от ОРЛ");
	            }

	            row.createCell(5).setCellValue(productValue);

	            String idOrdersStr = String.join(";", contract.idOrders.stream().map(String::valueOf).toArray(String[]::new));
	            row.createCell(6).setCellValue(idOrdersStr);

	            // Подсветка строки, если orlValue < productValue
	            if (orlValue != null && orlValue < productValue) {
	                CellStyle redStyle = workbook.createCellStyle();
	                Font redFont = workbook.createFont();
	                redFont.setColor(IndexedColors.RED.getIndex());
	                redStyle.setFont(redFont);
	                
	                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
	                    Cell cell = row.getCell(i);
	                    cell.setCellStyle(redStyle);
	                }
	            }else if (orlValue != null && orlValue > productValue) {
	            	CellStyle redStyle = workbook.createCellStyle();
	                Font redFont = workbook.createFont();
	                redFont.setColor(IndexedColors.BLUE.getIndex());
	                redStyle.setFont(redFont);
	                
	                for (int i = 0; i < row.getPhysicalNumberOfCells(); i++) {
	                    Cell cell = row.getCell(i);
	                    cell.setCellStyle(redStyle);
	                }
	            }
	        }
	    }

	    // Установка автоширины для всех столбцов
	    for (int i = 0; i < 7; i++) {
	        sheet.autoSizeColumn(i);
	    }

	    // Запись в файл
	    File excelFile = new File(filePath);
	    try (FileOutputStream fileOut = new FileOutputStream(excelFile)) {
	        workbook.write(fileOut);
	    }

	    workbook.close();
	    return excelFile; // Возвращаем созданный файл
	}
	
	

	
	class DataOrderHasNumContract {
		Map <Integer, Integer> orderProductsORL;
		Map <Long, Double> orderProductsManagerFact;
		String numContract;
		List<Integer> idOrders;
		String counterparty;
		
		
		
		public DataOrderHasNumContract(Map<Integer, Integer> orderProductsORL,
				Map<Long, Double> orderProductsManagerFact, String numContract, List<Integer> idOrders,
				String counterparty) {
			super();
			this.orderProductsORL = orderProductsORL;
			this.orderProductsManagerFact = orderProductsManagerFact;
			this.numContract = numContract;
			this.idOrders = idOrders;
			this.counterparty = counterparty;
		}


		@Override
		public int hashCode() {
			return Objects.hash(idOrders, numContract, orderProductsManagerFact, orderProductsORL);
		}


		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			DataOrderHasNumContract other = (DataOrderHasNumContract) obj;
			return Objects.equals(idOrders, other.idOrders) && Objects.equals(numContract, other.numContract)
					&& Objects.equals(orderProductsManagerFact, other.orderProductsManagerFact)
					&& Objects.equals(orderProductsORL, other.orderProductsORL);
		}


		@Override
		public String toString() {
			return "DataOrderHasNumContract [OrderProductsManagerFact="
					+ orderProductsManagerFact + ", numContract=" + numContract + ", idOrders=" + idOrders
					+ ", counterparty=" + counterparty + "]";
		}


		
			
	}
}


