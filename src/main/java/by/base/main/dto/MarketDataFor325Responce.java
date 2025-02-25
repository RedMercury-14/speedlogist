package by.base.main.dto;

import java.time.LocalDateTime;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MarketDataFor325Responce {
	
	/** 
     * Код товара (bigint).
     */
    private Long goodsId;
    
    /** 
     * Наименование товара (nvarchar(40)).
     */
    private String goodsName;
    
    /** 
     * Номер склада (bigint).
     */
    private Long warehouseId;
    
    /**
     * ERP-остатки на конец дня (ед.).
     */
    private Double restQua;
    
    /**
     * сумма расходов (ед.).
     */
    private Double sumQua;
    
    /**
     * сумма расходов с НДС (BYN).
     */
    private Double sumSum;
    
    /**
     * наименование группы складов.
     */
    private String warehouseGroupName;
    
    /**
     * наименование склада.
     */
    private String warehouseNameInfo;
    
    /**
     * ERP-остаток на складе (дн.).
     */
    private Double restDays;
    
    /**
     * зарезервировано (ед.).
     */
    private Long orderSaleQuantity;
    
    /**
     * балансовый остаток (ед.)
     */
    private Double restWithOrderSale;
    
    /**
     * ИД пользователя.
     */
    private Long uUserId;
    
    /**
     * имя пользователя
     */
    private String userNameFul;
    
    public MarketDataFor325Responce() {}
    
	/**
	 * 
	 */
	public MarketDataFor325Responce(String json) {
		super();
		JSONParser parser = new JSONParser();
		JSONObject json325Object = null;
		try {
			json325Object = (JSONObject) parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (json325Object != null) {
			System.out.println(json);
            this.goodsId = parseLong(json325Object.get("GoodsId"));
            this.goodsName = parseString(json325Object.get("GoodsName"));
            this.warehouseId = parseLong(json325Object.get("WarehouseId"));
            this.restQua = parseDouble(json325Object.get("RestQua"));
            this.sumQua = parseDouble(json325Object.get("SumQua"));
            this.sumSum = parseDouble(json325Object.get("SumSum"));
            this.warehouseGroupName = parseString(json325Object.get("WarehouseGroupName"));
            this.warehouseNameInfo = parseString(json325Object.get("WarehouseNameInfo"));
            this.restDays = parseDouble(json325Object.get("RestDays"));
            this.orderSaleQuantity = parseLong(json325Object.get("OrderSaleQuantity"));
            this.restWithOrderSale = parseDouble(json325Object.get("RestWithOrderSale"));
            this.uUserId = parseLong(json325Object.get("UUserId"));
            this.userNameFul = parseString(json325Object.get("UserNameFul"));
        }
	}
	
	private Long parseLong(Object value) {
		if (value != null) {
	        try {
	            Double d = Double.parseDouble(value.toString()); // Парсим как Double
	            return d.longValue(); // Приводим к Long
	        } catch (NumberFormatException e) {
	        	e.printStackTrace();
	            return null; // Если ошибка, возвращаем null
	        }
	    }
	    return null;
    }

    private Double parseDouble(Object value) {
        return value != null ? Double.parseDouble(value.toString()) : null;
    }

    private String parseString(Object value) {
        return value != null ? value.toString() : null;
    }

	public Long getGoodsId() {
		return goodsId;
	}

	public void setGoodsId(Long goodsId) {
		this.goodsId = goodsId;
	}

	public String getGoodsName() {
		return goodsName;
	}

	public void setGoodsName(String goodsName) {
		this.goodsName = goodsName;
	}

	public Long getWarehouseId() {
		return warehouseId;
	}

	public void setWarehouseId(Long warehouseId) {
		this.warehouseId = warehouseId;
	}

	public Double getRestQua() {
		return restQua;
	}

	public void setRestQua(Double restQua) {
		this.restQua = restQua;
	}

	public Double getSumQua() {
		return sumQua;
	}

	public void setSumQua(Double sumQua) {
		this.sumQua = sumQua;
	}

	public Double getSumSum() {
		return sumSum;
	}

	public void setSumSum(Double sumSum) {
		this.sumSum = sumSum;
	}

	public String getWarehouseGroupName() {
		return warehouseGroupName;
	}

	public void setWarehouseGroupName(String warehouseGroupName) {
		this.warehouseGroupName = warehouseGroupName;
	}

	public String getWarehouseNameInfo() {
		return warehouseNameInfo;
	}

	public void setWarehouseNameInfo(String warehouseNameInfo) {
		this.warehouseNameInfo = warehouseNameInfo;
	}

	public Double getRestDays() {
		return restDays;
	}

	public void setRestDays(Double restDays) {
		this.restDays = restDays;
	}

	public Long getOrderSaleQuantity() {
		return orderSaleQuantity;
	}

	public void setOrderSaleQuantity(Long wrderSaleQuantity) {
		this.orderSaleQuantity = wrderSaleQuantity;
	}

	/**
	 * балансовый остаток (ед.)
	 * @return
	 */
	public Double getRestWithOrderSale() {
		return restWithOrderSale;
	}

	public void setRestWithOrderSale(Double restWithOrderSale) {
		this.restWithOrderSale = restWithOrderSale;
	}

	public Long getuUserId() {
		return uUserId;
	}

	public void setuUserId(Long uUserId) {
		this.uUserId = uUserId;
	}

	public String getUserNameFul() {
		return userNameFul;
	}

	public void setUserNameFul(String userNameFul) {
		this.userNameFul = userNameFul;
	}

	@Override
	public int hashCode() {
		return Objects.hash(goodsId, goodsName, restDays, restQua, restWithOrderSale, sumQua, sumSum, uUserId,
				userNameFul, warehouseGroupName, warehouseId, warehouseNameInfo, orderSaleQuantity);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarketDataFor325Responce other = (MarketDataFor325Responce) obj;
		return Objects.equals(goodsId, other.goodsId) && Objects.equals(goodsName, other.goodsName)
				&& Objects.equals(restDays, other.restDays) && Objects.equals(restQua, other.restQua)
				&& Objects.equals(restWithOrderSale, other.restWithOrderSale) && Objects.equals(sumQua, other.sumQua)
				&& Objects.equals(sumSum, other.sumSum) && Objects.equals(uUserId, other.uUserId)
				&& Objects.equals(userNameFul, other.userNameFul)
				&& Objects.equals(warehouseGroupName, other.warehouseGroupName)
				&& Objects.equals(warehouseId, other.warehouseId)
				&& Objects.equals(warehouseNameInfo, other.warehouseNameInfo)
				&& Objects.equals(orderSaleQuantity, other.orderSaleQuantity);
	}

	@Override
	public String toString() {
		return "MarketDataFor325Responce [goodsId=" + goodsId + ", goodsName=" + goodsName + ", warehouseId="
				+ warehouseId + ", restQua=" + restQua + ", sumQua=" + sumQua + ", sumSum=" + sumSum
				+ ", warehouseGroupName=" + warehouseGroupName + ", warehouseNameInfo=" + warehouseNameInfo
				+ ", restDays=" + restDays + ", wrderSaleQuantity=" + orderSaleQuantity + ", restWithOrderSale="
				+ restWithOrderSale + ", uUserId=" + uUserId + ", userNameFul=" + userNameFul + "]";
	}
    
    
}
