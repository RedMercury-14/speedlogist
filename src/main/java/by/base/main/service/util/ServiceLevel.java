package by.base.main.service.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Date;
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

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
	public File checkingOrdersForORLNeeds(List<Order> orders, Date dateOrder, HttpServletRequest request) throws IOException {
		orders.sort(Comparator.comparing(Order::getMarketContractType)); // групируем номера контрактов
		int sizeOrders = orders.size();
		int sizeVoidOrder = 0;
		Map <Integer, Integer> orderProductsORL = orderProductService.getOrderProductMapHasDate(Date.valueOf(dateOrder.toLocalDate().minusDays(1))); // что заказали ОРЛ
		List<DataOrderHasNumContract> dataOrderHasNumContracts = new ArrayList<DataOrderHasNumContract>(); // лист с результатами сложений заказов относительно кода контракта

		//формируем список с объектом data для удобного формирования екселя по номерам контактор
		//он суммирует данные по одному коду контракта
		Map <Long, Double> orderProductsManagerFact = new HashMap<Long, Double>(); // что заказали закупки (суммируется по всем заказам)
		List<Integer> idOrders = new ArrayList<Integer>();
		
		String codeContract = null;
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
					dataOrderHasNumContracts.add(new DataOrderHasNumContract(orderProductsORL, orderProductsManagerFact, codeContract, idOrders, order.getCounterparty()));
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
			
		}		
//		dataOrderHasNumContracts.forEach(d-> System.out.println(d));
		//закончили формирование сводного списка
		String appPath = request.getServletContext().getRealPath("");
		String fileName = "reportORL-ZAQ.xlsx";		
		System.err.println(appPath + "resources/others/");
		return exportToExcel(dataOrderHasNumContracts, orderProductsORL, appPath + "resources/others/" + fileName);		
	}
	
	public File exportToExcel(List<DataOrderHasNumContract> dataOrderHasNumContracts,Map <Integer, Integer> orderProductsORL, String filePath) throws IOException {
		Map<Integer, Product> products = productService.getAllProductMap();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Отчёт относительно заказов");
//        Sheet sheet2 = workbook.createSheet("Отчёт относительно заказов ОРЛ");

        // Создание заголовков
        Row headerRow = sheet.createRow(0);
        headerRow.createCell(0).setCellValue("Поставщик");
        headerRow.createCell(1).setCellValue("Код контракта");
        headerRow.createCell(2).setCellValue("Код товара");
        headerRow.createCell(3).setCellValue("Товар");
        headerRow.createCell(4).setCellValue("Заказ ОРЛ");
        headerRow.createCell(5).setCellValue("Заказ менеджера");
        headerRow.createCell(6).setCellValue("id Заказов");
        
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
                row.createCell(3).setCellValue(products.get(productCode.intValue()) == null ? "Товар отсутствует в базе данных" : products.get(productCode.intValue()).getName()); // Товар оставляем null
				if(orlValue != null) {
					row.createCell(4).setCellValue(orlValue);
				}else {
					row.createCell(4).setCellValue("Отсутствует заказ от ОРЛ");
				}                
                row.createCell(5).setCellValue(productValue);
                
                String idOrdersStr = "";
                for (Integer id : contract.idOrders) {
                	idOrdersStr = idOrdersStr + id + ";";
				}
                row.createCell(6).setCellValue(idOrdersStr);
                    
            }
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


