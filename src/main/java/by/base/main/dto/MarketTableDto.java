package by.base.main.dto;

import java.util.Arrays;

/**
 * ообщение об ошибке из Маркета 
 */
public class MarketTableDto {
	
	private Object[] Table;
	public Object[] getTable() {
		return Table;
	}

	public void setTable(Object[] table) {
		this.Table = table;
	}
	
	@Override
	public String toString() {
		return "MarketTableDto [Table=" + Arrays.toString(Table) + "]";
	}
	
}
