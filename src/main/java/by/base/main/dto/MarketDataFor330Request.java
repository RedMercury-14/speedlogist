package by.base.main.dto;

public class MarketDataFor330Request {
	
	private String DateFrom; //дата начала выборки;
    private String DateTo; // дата завершения выборки;
    private Object[] WarehouseId; // номера складов (опциональный параметр);
    private Object[] GoodsId; // коды товаров (опциональный параметр);

    
    
    
    /**
	 * 
	 */
	public MarketDataFor330Request() {
		super();
	}

	/**
	 * @param dateFrom
	 * @param dateTo
	 * @param warehouseId
	 * @param whatBase
	 */
	public MarketDataFor330Request(String dateFrom, String dateTo, Object[] warehouseId, Object[] goodsId) {
		super();
		DateFrom = dateFrom;
		DateTo = dateTo;
		WarehouseId = warehouseId;
		GoodsId = goodsId;
	}

	// Getters and Setters
    public String getDateFrom() {
        return DateFrom;
    }

    public void setDateFrom(String dateFrom) {
        this.DateFrom = dateFrom;
    }

    public String getDateTo() {
        return DateTo;
    }

    public void setDateTo(String dateTo) {
        this.DateTo = dateTo;
    }

    public Object[] getWarehouseId() {
        return WarehouseId;
    }

    public void setWarehouseId(Object[] warehouseId) {
        this.WarehouseId = warehouseId;
    }


	public Object[] getGoodsId() {
		return GoodsId;
	}

	public void setGoodsId(Object[] goodsId) {
		GoodsId = goodsId;
	}

	@Override
	public String toString() {
		return "{\"DateFrom\":\"" + DateFrom + "\", \"DateTo\":\"" + DateTo + "\", \"WarehouseId\":\"" + WarehouseId
				+ "\", \"GoodsId\":\"" + GoodsId + "\"}";
	}
    
    
}
