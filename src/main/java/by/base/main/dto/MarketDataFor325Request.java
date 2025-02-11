package by.base.main.dto;

import java.util.Arrays;
import java.util.Objects;

public class MarketDataFor325Request {
	
	private String DateFrom; //дата начала выборки;
    private String DateTo; // дата завершения выборки;
    private Object[] WarehouseId; // номера складов (опциональный параметр);
    private Object[] GoodsId; // коды товаров (опциональный параметр);
    private Object[] WhatBase; // •	WhatBase, array - вид расхода (опциональный параметр, по умолчанию заданы расходы 11 и 21);
    
    
    
	/**
	 * @param dateFrom
	 * @param dateTo
	 * @param warehouseId
	 * @param goodsId
	 * @param whatBase
	 */
	public MarketDataFor325Request(String dateFrom, String dateTo, Object[] warehouseId, Object[] goodsId,
			Object[] whatBase) {
		super();
		DateFrom = dateFrom;
		DateTo = dateTo;
		WarehouseId = warehouseId;
		GoodsId = goodsId;
		WhatBase = whatBase;
	}
	public String getDateFrom() {
		return DateFrom;
	}
	public void setDateFrom(String dateFrom) {
		DateFrom = dateFrom;
	}
	public String getDateTo() {
		return DateTo;
	}
	public void setDateTo(String dateTo) {
		DateTo = dateTo;
	}
	public Object[] getWarehouseId() {
		return WarehouseId;
	}
	public void setWarehouseId(Object[] warehouseId) {
		WarehouseId = warehouseId;
	}
	public Object[] getGoodsId() {
		return GoodsId;
	}
	public void setGoodsId(Object[] goodsId) {
		GoodsId = goodsId;
	}
	public Object[] getWhatBase() {
		return WhatBase;
	}
	public void setWhatBase(Object[] whatBase) {
		WhatBase = whatBase;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + Arrays.deepHashCode(GoodsId);
		result = prime * result + Arrays.deepHashCode(WarehouseId);
		result = prime * result + Arrays.deepHashCode(WhatBase);
		result = prime * result + Objects.hash(DateFrom, DateTo);
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarketDataFor325Request other = (MarketDataFor325Request) obj;
		return Objects.equals(DateFrom, other.DateFrom) && Objects.equals(DateTo, other.DateTo)
				&& Arrays.deepEquals(GoodsId, other.GoodsId) && Arrays.deepEquals(WarehouseId, other.WarehouseId)
				&& Arrays.deepEquals(WhatBase, other.WhatBase);
	}
	@Override
	public String toString() {
		return "MarketDataFor325Request [DateFrom=" + DateFrom + ", DateTo=" + DateTo + ", WarehouseId="
				+ Arrays.toString(WarehouseId) + ", GoodsId=" + Arrays.toString(GoodsId) + ", WhatBase="
				+ Arrays.toString(WhatBase) + "]";
	}
    
    

}
