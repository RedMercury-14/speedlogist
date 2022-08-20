package by.base.main.util;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MainTest3 {
	/*
	 * рабочий парсер. главное: 4 строки (номер магазина\адрес магазина\паллеты\вес)! и один лист!
	 * */
	public static void main(String[] args) throws InvalidFormatException, IOException {
		File file = new File("C://test//после маршрутизатора//Развоз 04.07.xlsx");
		System.out.println(file.toString());

		int numPoint = 0;
		XSSFWorkbook book = new XSSFWorkbook(file);
		XSSFSheet sheet = book.getSheetAt(0);
		int rowStart = Math.min(0, sheet.getFirstRowNum());
		int rowEnd = Math.max(10000, sheet.getLastRowNum());
		boolean flag = false;
		boolean flag2 = false;

		Iterator<Row> ri = sheet.rowIterator();
		for (int rw = rowStart; rw < rowEnd; rw++) {
			numPoint++;
			XSSFRow row = sheet.getRow(rw);
			int i = 0;
			String[] cellMass = new String[4];
			if (row == null) {
				if (!flag) {
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
							System.out.println(Integer.parseInt(cellMass[0].substring(0, cellMass[0].length() - 2)));
							System.out.println(cellMass[1]);
							System.out.println(cellMass[2]);
							System.out.println(cellMass[3]);
							System.out.println("точка №" + numPoint);
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
	}
}