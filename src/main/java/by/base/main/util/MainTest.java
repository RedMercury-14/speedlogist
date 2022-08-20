package by.base.main.util;
import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


public class MainTest {

	public static void main(String[] args) throws Throwable {
		//File file = new File("C://test//обновленный 23.06.2022.xlsm");
		File file = new File("C://test//обновленный 23.06.2022.xlsx");		
		System.out.println(file.toString());
		LocalTime start = LocalTime.now();
		
		XSSFWorkbook book = new XSSFWorkbook(file);
		XSSFSheet sheet = book.getSheetAt(0);
		List<String> col = readColumn(sheet);
		List<String> row = readRow(sheet);
		List<String> distances = readDistances(sheet);
		Set<String> keys = new HashSet<String>();
		Map<String, String> points = new HashMap<String, String>(); 
		for (String string : row) {
			for (String string2 : col) {
				String str = distances.stream().findFirst().get();
				points.put(string + "-"+ string2, str);				
				distances.remove(str);
			}
		}
		
		
		
//		test.stream().forEach(s-> System.out.println(s));
//		System.out.println(test.size());
//		System.out.println(col.size());
//		System.out.println(row.size());
//		System.out.println(keys.size());
		/*
		// Определение граничных строк обработки
	    int rowStart = Math.min(0, sheet.getFirstRowNum());
	    int rowEnd   = Math.max(0, sheet.getLastRowNum ());

	    for (int rw = rowStart+1; rw < rowEnd; rw++) {
	        XSSFRow row = sheet.getRow(rw);
	        if (row == null) {
	            // System.out.println(
	            //      "row '" + rw + "' is not created");
	            continue;
	        }
	        short minCol = row.getFirstCellNum();
	        short maxCol = row.getLastCellNum();

	        //for(short col = minCol; col < maxCol; col++) {
	        int col = 0;
	            XSSFCell cell = row.getCell(col);
	            if (cell == null) {
	                // System.out.println(
	                //   "cell '" + col + "' is not created");
	                continue;
	            }
	            
	            DataFormatter formatter = new DataFormatter();
	            String text = formatter.formatCellValue(cell);
	            
	            System.out.println(text);
				
	                        
	        //}
	    }       */
		LocalTime finish = LocalTime.now();
	    System.out.println(start + "   " + finish);
	    System.out.println(points.get("784-297")); //109,5
	    System.out.println(points.get("418-646")); //177,7
	    System.out.println(points.get("2243-2322")); //122,3
	}
	
	
	private static List<String> readRow(XSSFSheet sheet) { // считывает первый ряд
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
	
	private static List<String> readColumn(XSSFSheet sheet) {
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
	private static List<String> readDistances(XSSFSheet sheet){
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
}



