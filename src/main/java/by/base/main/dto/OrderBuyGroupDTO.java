package by.base.main.dto;

import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

public class OrderBuyGroupDTO {
	
	private Long OrderBuyGroupId; // номер из маркета
	private Integer Checkx; // статус
	private Timestamp Datex; // Время создания заказа
	private Timestamp DeliveryDate; // Дата поставки
	private Timestamp ShipmentDateLast; // Дата крайний поставки
	private Long ContractType; // Тип контракта
	private Long ContractGroupId; // Код контракта
	private String ContractNumber; // Номер контракта
	private Integer WarehouseId; // Склад назначения
	private Long ContractorId; // Код поставщика
	private String ContractorNameShort; // Краткое наименование поставщика
	private Double OrderSumFirst; // Начальная сумма заказа без НДС
	private Double OrderSumFinal; // Конечная сумма заказанного с НДС
	private String Info; // комментарий / информация
	private List<OrderBuyDTO> OrderBuy;
	
	public Timestamp getShipmentDateLast() {
		return ShipmentDateLast;
	}
	public void setShipmentDateLast(String shipmentDateLast) {
		if(shipmentDateLast != null) {
			ShipmentDateLast = Timestamp.valueOf(LocalDateTime.parse(shipmentDateLast));
		}else {
			ShipmentDateLast = null;
		}
	}
	public Long getOrderBuyGroupId() {
		return OrderBuyGroupId;
	}
	public void setOrderBuyGroupId(Long orderBuyGroupId) {
		OrderBuyGroupId = orderBuyGroupId;
	}
	public Integer getCheckx() {
		return Checkx;
	}
	public void setCheckx(Integer checkx) {
		Checkx = checkx;
	}
	public Timestamp getDatex() {
		return Datex;
	}
	public void setDatex(String datex) {		
		if(datex != null) {
			Datex = Timestamp.valueOf(LocalDateTime.parse(datex));
		}else {
			Datex = null;
		}
		
	}
	public Timestamp getDeliveryDate() {
		return DeliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		if(deliveryDate != null) {
			DeliveryDate = Timestamp.valueOf(LocalDateTime.parse(deliveryDate));			
		}else {
			DeliveryDate = null;
		}
	}
	public Long getContractType() {
		return ContractType;
	}
	public void setContractType(Long contractType) {
		ContractType = contractType;
	}
	public Long getContractGroupId() {
		return ContractGroupId;
	}
	public void setContractGroupId(Long contractGroupId) {
		ContractGroupId = contractGroupId;
	}
	public String getContractNumber() {
		return ContractNumber;
	}
	public void setContractNumber(String contractNumber) {
		ContractNumber = contractNumber;
	}
	public Integer getWarehouseId() {
		return WarehouseId;
	}
	public void setWarehouseId(Integer warehouseId) {
		WarehouseId = warehouseId;
	}
	public Long getContractorId() {
		return ContractorId;
	}
	public void setContractorId(Long contractorId) {
		ContractorId = contractorId;
	}
	public String getContractorNameShort() {
		return ContractorNameShort;
	}
	public void setContractorNameShort(String contractorNameShort) {
		ContractorNameShort = contractorNameShort;
	}
	public Double getOrderSumFirst() {
		return OrderSumFirst;
	}
	public void setOrderSumFirst(Double orderSumFirst) {
		OrderSumFirst = orderSumFirst;
	}
	public Double getOrderSumFinal() {
		return OrderSumFinal;
	}
	public void setOrderSumFinal(Double orderSumFinal) {
		OrderSumFinal = orderSumFinal;
	}
	public String getInfo() {
		return Info;
	}
	public void setInfo(String info) {
		Info = info;
	}
	
	public List<OrderBuyDTO> getOrderBuy() {
		return OrderBuy;
	}
	public void setOrderBuy(List<OrderBuyDTO> orderBuy) {
		OrderBuy = orderBuy;
	}
	@Override
	public String toString() {
		return "OrderBuyGroupDTO [OrderBuyGroupId=" + OrderBuyGroupId + ", Checkx=" + Checkx + ", Datex=" + Datex
				+ ", DeliveryDate=" + DeliveryDate + ", ShipmentDateLast=" + ShipmentDateLast + ", ContractType="
				+ ContractType + ", ContractGroupId=" + ContractGroupId + ", ContractNumber=" + ContractNumber
				+ ", WarehouseId=" + WarehouseId + ", ContractorId=" + ContractorId + ", ContractorNameShort="
				+ ContractorNameShort + ", OrderSumFirst=" + OrderSumFirst + ", OrderSumFinal=" + OrderSumFinal
				+ ", Info=" + Info + ", OrderBuy=" + OrderBuy + "]";
	}


}
