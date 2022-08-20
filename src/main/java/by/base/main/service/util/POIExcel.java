package by.base.main.service.util;

import java.io.File;
import java.io.IOException;
import java.sql.Date;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import by.base.main.dao.RouteDAO;
import by.base.main.dao.RouteHasShopDAO;
import by.base.main.dao.ShopDAO;
import by.base.main.model.Route;
import by.base.main.model.RouteHasShop;
import by.base.main.model.Shop;
import by.base.main.service.ServiceException;

@Service
public class POIExcel {

	@Autowired
	private ShopDAO shopDAO;

	@Autowired
	private RouteDAO routeDAO;

	@Autowired
	private RouteHasShopDAO routeHasShopDAO;
	
	
	private ArrayList<Shop> shops;
	private ArrayList<RouteHasShop> arrayRouteHasShop;

	public File getFileByMultipart(MultipartFile multipart) throws ServiceException {
		File convFile = new File(multipart.getOriginalFilename());
		try {
			multipart.transferTo(convFile);
		} catch (IllegalStateException | IOException e) {
			throw new ServiceException("getFileByMultipart");
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
						if(!qRouteHasShops.isEmpty()) {
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
							route.setRouteDirection(routeDirection);
							qRouteHasShops.stream().forEach(s-> s.setRoute(route));
							qRouteHasShops.stream().forEach(s-> routeHasShopDAO.saveOrUpdateRouteHasShop(s));
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
								if(!qRouteHasShops.isEmpty()) {
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
									route.setRouteDirection(routeDirection);
									qRouteHasShops.stream().forEach(s-> s.setRoute(route));
									qRouteHasShops.stream().forEach(s-> routeHasShopDAO.saveOrUpdateRouteHasShop(s));
									qRouteHasShops.clear();
								}
								numPoint = 0;
								System.out.println("------------------------- создание маршрута +++++++++++++++++++++++++++++");
								
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
								RouteHasShop routeHasShop = new RouteHasShop(numPoint, cellMass[2], Math.ceil(Double.parseDouble(cellMass[3]))+"", shop);								
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
	
	private List<String> readDistances(XSSFSheet sheet){
		List<String> distances = new ArrayList<String>();
		int rowStart = Math.min(0, sheet.getFirstRowNum());
	    int rowEnd   = Math.max(0, sheet.getLastRowNum ());

	    for (int rw = rowStart+1; rw <= rowEnd; rw++) {
	        XSSFRow row = sheet.getRow(rw);
	        if (row == null) {
	            continue;
	        }
	        short minCol = row.getFirstCellNum();
	        short maxCol = row.getLastCellNum();

	        for(short col = (short) (minCol+1); col <= maxCol; col++) {
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
	
	private  List<String> readColumn(XSSFSheet sheet) {
		List<String> columns = new ArrayList<String>();
		int rowStart = Math.min(0, sheet.getFirstRowNum());
	    int rowEnd   = Math.max(0, sheet.getLastRowNum ());
		for (int rw = rowStart+1; rw <= rowEnd; rw++) {
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
	        for(short col = (short) (minCol+1); col < maxCol; col++) {
	            XSSFCell cell = row.getCell(col);
	            if (cell == null) {
	                continue;
	            }
	            DataFormatter formatter = new DataFormatter();
	            String text = formatter.formatCellValue(cell);
	            rows.add(text);
	        }
	        //rows.add("\n");
			return rows;
	}
	
	public void getDistancesToMap(Map <String, String> distance) throws InvalidFormatException, IOException {
		File file = new File("C://test//обновленный 23.06.2022.xlsx");		//поправить путь к файлу!
		XSSFWorkbook book = new XSSFWorkbook(file);
		XSSFSheet sheet = book.getSheetAt(0);
		List<String> col = readColumn(sheet);
		List<String> row = readRow(sheet);
		List<String> distances = readDistances(sheet);
		for (String string : row) {
			for (String string2 : col) {
				String str = distances.stream().findFirst().get();
				distance.put(string + "-"+ string2, str);				
				distances.remove(str);
			}
		}
	}

}
