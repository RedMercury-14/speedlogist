package by.base.main.dto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class MarketDataFor330Responce {

	/** 
     * Идентификатор контрагента (bigint).
     */
    private Long contractorId;

    /** 
     * Краткое наименование контрагента (nvarchar(200).
     */
    private String contractorNameShort;

    /** 
     * Дата создания документа прихода (datetime).
     */
    private LocalDateTime datex;

    /** 
     * Дата выписки накладной (datetime).
     */
    private LocalDateTime date2;

    /** 
     * Дата разгрузки (datetime).
     */
    private LocalDateTime date3;

    /** 
     * Идентификатор документа заказа поставщику (bigint). ОН КОД ЗАКАЗА ИЗ МАРКЕТА
     */
    private Long orderBuyGroupId;

    /** 
     * Номер накладной (nvarchar(70)).
     */
    private String numberDoc;

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
     * Количество товара (float).
     */
    private Double quantity;

    /** 
     * Цена закупки товара без НДС (money).
     */
    private Double price2;

    /** 
     * Цена закупки товара с учетом скидок или надбавок поставщика с НДС (money).
     */
    private Double price;

    /** 
     * Конечная розничная цена товара с НДС (money).
     */
    private Double priceN;

    /** 
     * Сумма прихода в закупочных ценах без НДС (money).
     */
    private Double debitSum2;

    /** 
     * Сумма прихода в закупочных ценах с НДС (money).
     */
    private Double summ;

    /** 
     * Процент оптовой надбавки на товар (float).
     */
    private Double procB;

    /** 
     * Процент розничной надбавки на товар (float).
     */
    private Double procN;

    /** 
     * Дополнительная информация документа расценки импортного товара (nvarchar(300)).
     */
    private String info;

    /** 
     * Дополнительная информация по заказу (nvarchar(1000)).
     */
    private String infoOrder;

	/**
	 * @param contractorId
	 * @param contractorNameShort
	 * @param datex
	 * @param date2
	 * @param date3
	 * @param orderBuyGroupId
	 * @param numberDoc
	 * @param goodsId
	 * @param goodsName
	 * @param warehouseId
	 * @param quantity
	 * @param price2
	 * @param price
	 * @param priceN
	 * @param debitSum2
	 * @param summ
	 * @param procB
	 * @param procN
	 * @param info
	 * @param infoOrder
	 */
	public MarketDataFor330Responce(Long contractorId, String contractorNameShort, LocalDateTime datex,
			LocalDateTime date2, LocalDateTime date3, Long orderBuyGroupId, String numberDoc, Long goodsId,
			String goodsName, Long warehouseId, Double quantity, Double price2, Double price, Double priceN,
			Double debitSum2, Double summ, Double procB, Double procN, String info, String infoOrder) {
		super();
		this.contractorId = contractorId;
		this.contractorNameShort = contractorNameShort;
		this.datex = datex;
		this.date2 = date2;
		this.date3 = date3;
		this.orderBuyGroupId = orderBuyGroupId;
		this.numberDoc = numberDoc;
		this.goodsId = goodsId;
		this.goodsName = goodsName;
		this.warehouseId = warehouseId;
		this.quantity = quantity;
		this.price2 = price2;
		this.price = price;
		this.priceN = priceN;
		this.debitSum2 = debitSum2;
		this.summ = summ;
		this.procB = procB;
		this.procN = procN;
		this.info = info;
		this.infoOrder = infoOrder;
	}

	/**
	 * Конструктор который принимает json из маркета
	 * @param json
	 */
	public MarketDataFor330Responce(String json) {		
		super();
		JSONParser parser = new JSONParser();
		JSONObject json330Object = null;
		try {
			json330Object = (JSONObject) parser.parse(json);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.contractorId = (json330Object.get("ContractorId") != null ? Long.parseLong(json330Object.get("ContractorId").toString()) : null);
		this.contractorNameShort = (json330Object.get("ContractorNameShort") != null 
                ? json330Object.get("ContractorNameShort").toString() 
                : null);

		this.datex = (json330Object.get("Datex") != null
            	? LocalDateTime.parse(json330Object.get("Datex").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null);

		this.date2 = (json330Object.get("Date2") != null 
                ? LocalDateTime.parse(json330Object.get("Date2").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null);

		this.date3 = (json330Object.get("Date3") != null 
                ? LocalDateTime.parse(json330Object.get("Date3").toString(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                : null);

		this.orderBuyGroupId = (json330Object.get("OrderBuyGroupId") != null 
                ? Long.parseLong(json330Object.get("OrderBuyGroupId").toString()) 
                : null);

		this.numberDoc = (json330Object.get("NumberDoc") != null 
                ? json330Object.get("NumberDoc").toString() 
                : null);

		this.goodsId = (json330Object.get("GoodsId") != null 
                ? Long.parseLong(json330Object.get("GoodsId").toString()) 
                : null);

		this.goodsName = (json330Object.get("GoodsName") != null 
                ? json330Object.get("GoodsName").toString() 
                : null);

		this.warehouseId = (json330Object.get("WarehouseId") != null 
                ? Long.parseLong(json330Object.get("WarehouseId").toString()) 
                : null);

		this.quantity = (json330Object.get("Quantity") != null 
                ? Double.parseDouble(json330Object.get("Quantity").toString()) 
                : null);

		this.price2 = (json330Object.get("Price2") != null 
                ? Double.parseDouble(json330Object.get("Price2").toString()) 
                : null);

		this.price = (json330Object.get("Price") != null 
                ? Double.parseDouble(json330Object.get("Price").toString()) 
                : null);

		this.priceN = (json330Object.get("PriceN") != null 
                ? Double.parseDouble(json330Object.get("PriceN").toString()) 
                : null);

		this.debitSum2 = (json330Object.get("DebitSum2") != null 
                ? Double.parseDouble(json330Object.get("DebitSum2").toString()) 
                : null);

		this.summ = (json330Object.get("Summ") != null 
                ? Double.parseDouble(json330Object.get("Summ").toString()) 
                : null);

		this.procB = (json330Object.get("ProcB") != null 
                ? Double.parseDouble(json330Object.get("ProcB").toString()) 
                : null);

		this.procN = (json330Object.get("ProcN") != null 
                ? Double.parseDouble(json330Object.get("ProcN").toString()) 
                : null);

		this.info = (json330Object.get("info") != null 
                ? json330Object.get("info").toString() 
                : null);

		this.infoOrder = (json330Object.get("InfoOrder") != null 
                ? json330Object.get("InfoOrder").toString() 
                : null);
	}
	public MarketDataFor330Responce() {
		super();
	}

	public Long getContractorId() {
		return contractorId;
	}

	public void setContractorId(Long contractorId) {
		this.contractorId = contractorId;
	}

	public String getContractorNameShort() {
		return contractorNameShort;
	}

	public void setContractorNameShort(String contractorNameShort) {
		this.contractorNameShort = contractorNameShort;
	}

	public LocalDateTime getDatex() {
		return datex;
	}

	public void setDatex(LocalDateTime datex) {
		this.datex = datex;
	}

	public LocalDateTime getDate2() {
		return date2;
	}

	public void setDate2(LocalDateTime date2) {
		this.date2 = date2;
	}

	public LocalDateTime getDate3() {
		return date3;
	}

	public void setDate3(LocalDateTime date3) {
		this.date3 = date3;
	}

	/**
	 * номер из маркета
	 * @return
	 */
	public Long getOrderBuyGroupId() {
		return orderBuyGroupId;
	}

	/**
	 * номер из маркета
	 * @param orderBuyGroupId
	 */
	public void setOrderBuyGroupId(Long orderBuyGroupId) {
		this.orderBuyGroupId = orderBuyGroupId;
	}

	public String getNumberDoc() {
		return numberDoc;
	}

	public void setNumberDoc(String numberDoc) {
		this.numberDoc = numberDoc;
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

	public Double getQuantity() {
		return quantity;
	}

	public void setQuantity(Double quantity) {
		this.quantity = quantity;
	}

	public Double getPrice2() {
		return price2;
	}

	public void setPrice2(Double price2) {
		this.price2 = price2;
	}

	public Double getPrice() {
		return price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public Double getPriceN() {
		return priceN;
	}

	public void setPriceN(Double priceN) {
		this.priceN = priceN;
	}

	public Double getDebitSum2() {
		return debitSum2;
	}

	public void setDebitSum2(Double debitSum2) {
		this.debitSum2 = debitSum2;
	}

	public Double getSumm() {
		return summ;
	}

	public void setSumm(Double summ) {
		this.summ = summ;
	}

	public Double getProcB() {
		return procB;
	}

	public void setProcB(Double procB) {
		this.procB = procB;
	}

	public Double getProcN() {
		return procN;
	}

	public void setProcN(Double procN) {
		this.procN = procN;
	}

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getInfoOrder() {
		return infoOrder;
	}

	public void setInfoOrder(String infoOrder) {
		this.infoOrder = infoOrder;
	}

	@Override
	public int hashCode() {
		return Objects.hash(contractorId, contractorNameShort, date2, date3, datex, debitSum2, goodsId, goodsName, info,
				infoOrder, numberDoc, orderBuyGroupId, price, price2, priceN, procB, procN, quantity, summ,
				warehouseId);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		MarketDataFor330Responce other = (MarketDataFor330Responce) obj;
		return Objects.equals(contractorId, other.contractorId)
				&& Objects.equals(contractorNameShort, other.contractorNameShort) && Objects.equals(date2, other.date2)
				&& Objects.equals(date3, other.date3) && Objects.equals(datex, other.datex)
				&& Objects.equals(debitSum2, other.debitSum2) && Objects.equals(goodsId, other.goodsId)
				&& Objects.equals(goodsName, other.goodsName) && Objects.equals(info, other.info)
				&& Objects.equals(infoOrder, other.infoOrder) && Objects.equals(numberDoc, other.numberDoc)
				&& Objects.equals(orderBuyGroupId, other.orderBuyGroupId) && Objects.equals(price, other.price)
				&& Objects.equals(price2, other.price2) && Objects.equals(priceN, other.priceN)
				&& Objects.equals(procB, other.procB) && Objects.equals(procN, other.procN)
				&& Objects.equals(quantity, other.quantity) && Objects.equals(summ, other.summ)
				&& Objects.equals(warehouseId, other.warehouseId);
	}

	@Override
	public String toString() {
		return "MarketDataFor330Responce [contractorId=" + contractorId + ", contractorNameShort=" + contractorNameShort
				+ ", datex=" + datex + ", date2=" + date2 + ", date3=" + date3 + ", orderBuyGroupId=" + orderBuyGroupId
				+ ", numberDoc=" + numberDoc + ", goodsId=" + goodsId + ", goodsName=" + goodsName + ", warehouseId="
				+ warehouseId + ", quantity=" + quantity + ", price2=" + price2 + ", price=" + price + ", priceN="
				+ priceN + ", debitSum2=" + debitSum2 + ", summ=" + summ + ", procB=" + procB + ", procN=" + procN
				+ ", info=" + info + ", infoOrder=" + infoOrder + "]";
	}
	
	
    
}
