package by.base.main.dto;

public class MarketDataFor398Request {
	
	private String DateFrom; //дата начала выборки;
    private String DateTo; //дата завершения выборки;
    private Object[] WarehouseId; //номера складов (опциональный параметр);
    private Object[] WhatBase; //вид расхода (опциональный параметр);

    
    
    
    /**
	 * 
	 */
	public MarketDataFor398Request() {
		super();
	}

	/**
	 * @param dateFrom
	 * @param dateTo
	 * @param warehouseId
	 * @param whatBase
	 */
	public MarketDataFor398Request(String dateFrom, String dateTo, Object[] warehouseId, Object[] whatBase) {
		super();
		DateFrom = dateFrom;
		DateTo = dateTo;
		WarehouseId = warehouseId;
		WhatBase = whatBase;
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

    public Object[] getWhatBase() {
        return WhatBase;
    }

    public void setWhatBase(Object[] whatBase) {
        this.WhatBase = whatBase;
    }

	@Override
	public String toString() {
		return "{\"DateFrom\":\"" + DateFrom + "\", \"DateTo\":\"" + DateTo + "\", \"WarehouseId\":\"" + WarehouseId
				+ "\", \"WhatBase\":\"" + WhatBase + "\"}";
	}
    
    
}
