package by.base.main.dto;

public class MarketDataFor398Responce {
	private String CreditGroupId;
	private String IdFrom;
	private String WhatBase;
	private String Datex;
    private String Quantity; 
    private String WarehouseIdFrom; 
    private String WarehouseId; 
    private String GoodsId; 
    private String Skidka;
	public String getCreditGroupId() {
		return CreditGroupId;
	}
	public void setCreditGroupId(String creditGroupId) {
		CreditGroupId = creditGroupId;
	}
	public String getIdFrom() {
		return IdFrom;
	}
	public void setIdFrom(String idFrom) {
		IdFrom = idFrom;
	}
	public String getWhatBase() {
		return WhatBase;
	}
	public void setWhatBase(String whatBase) {
		WhatBase = whatBase;
	}
	public String getDatex() {
		return Datex;
	}
	public void setDatex(String datex) {
		Datex = datex;
	}
	public String getQuantity() {
		return Quantity;
	}
	public void setQuantity(String quantity) {
		Quantity = quantity;
	}
	public String getWarehouseIdFrom() {
		return WarehouseIdFrom;
	}
	public void setWarehouseIdFrom(String warehouseIdFrom) {
		WarehouseIdFrom = warehouseIdFrom;
	}
	public String getWarehouseId() {
		return WarehouseId;
	}
	public void setWarehouseId(String warehouseId) {
		WarehouseId = warehouseId;
	}
	public String getGoodsId() {
		return GoodsId;
	}
	public void setGoodsId(String goodsId) {
		GoodsId = goodsId;
	}
	public String getSkidka() {
		return Skidka;
	}
	public void setSkidka(String skidka) {
		Skidka = skidka;
	}
	@Override
	public String toString() {
		return "MarketDataFor398Request [CreditGroupId=" + CreditGroupId + ", IdFrom=" + IdFrom + ", WhatBase="
				+ WhatBase + ", Datex=" + Datex + ", Quantity=" + Quantity + ", WarehouseIdFrom=" + WarehouseIdFrom
				+ ", WarehouseId=" + WarehouseId + ", GoodsId=" + GoodsId + ", Skidka=" + Skidka + "]";
	}     
    
}
