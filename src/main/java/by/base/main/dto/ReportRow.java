package by.base.main.dto;

import java.sql.Date;
import java.time.LocalDateTime;
import java.util.Objects;

public class ReportRow {
	
	private Long idReportRow;
	
	/**
	 * Наименование поставщика
	 */
	private String counterpartyName;
	
	private Date dateStart;
	
	private Date dateFinish;
	
	/**
	 * Дата прихода на склад
	 */
	private LocalDateTime dateUnload;
	
	/**
	 * Номер заказа из маркета
	 */	
	private String marketNumber;
	
	/**
	 * Период 
	 * Поставки заказа 
	 */
	private String periodOrderDelivery;
	
	/**
	 * Группа товаров
	 */
	private String productGroup;
	
	/**
	 * Наименование товара
	 */
	private String productName;
	
	/**
	 * Код товара
	 */	
	private Long productCode;
	
	/**
	 * Заказано единиц ОРЛ
	 */
	private Integer orderedUnitsORL;
	
	/**
	 * Заказано единиц менеджером
	 */
	private Integer orderedUnitsManager;
	
	/**
	 * Принято единиц
	 */
	private Integer acceptedUnits;
	
	/**
	 * процент выполнения заказа
	 */
	private Double precentOrderFulfillment;
	
	/**
	 * расхождение (колш-во)
	 */
	private Integer discrepancyQuantity;
	
	/**
	 * заказано руб
	 */
	private Double orderedRUB;
	
	/**
	 * принято руб
	 */
	private Double acceptedRUB;
	
	/**
	 * % выполнения заказа руб без НДС
	 */
	private Double precentOrderCompletionNotNDS;
	
	/**
	 * расхождение (БЕЗ НДС)
	 */
	private Double discrepancyNotNDS;
	
	/**
	 * Комментарий при подготовке отчёта
	 */
	private String comment;
	
	/**
	 * склад прихода
	 */
	private String stock;
	
	/**
	 * дата расчёта ORL
	 */
	private Date dateOrderORL;
	
	
	/**
	 * Дата прихода на склад
	 */
	public LocalDateTime getDateUnload() {
		return dateUnload;
	}
	/**
	 * Дата прихода на склад
	 */
	public void setDateUnload(LocalDateTime dateUnload) {
		this.dateUnload = dateUnload;
	}

	public String getStock() {
		return stock;
	}

	public void setStock(String stock) {
		this.stock = stock;
	}

	public Long getIdReportRow() {
		return idReportRow;
	}

	public void setIdReportRow(Long idReportRow) {
		this.idReportRow = idReportRow;
	}

	public String getCounterpartyName() {
		return counterpartyName;
	}

	public String getMarketNumber() {
		return marketNumber;
	}

	public void setMarketNumber(String marketNumber) {
		this.marketNumber = marketNumber;
	}

	public void setCounterpartyName(String counterpartyName) {
		this.counterpartyName = counterpartyName;
	}

	public Date getDateStart() {
		return dateStart;
	}

	public void setDateStart(Date dateStart) {
		this.dateStart = dateStart;
	}

	public Date getDateFinish() {
		return dateFinish;
	}

	public void setDateFinish(Date dateFinish) {
		this.dateFinish = dateFinish;
	}

	public String getPeriodOrderDelivery() {
		return periodOrderDelivery;
	}

	public void setPeriodOrderDelivery(String periodOrderDelivery) {
		this.periodOrderDelivery = periodOrderDelivery;
	}

	public String getProductGroup() {
		return productGroup;
	}

	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}

	public String getProductName() {
		return productName;
	}

	public void setProductName(String productName) {
		this.productName = productName;
	}

	public Long getProductCode() {
		return productCode;
	}

	public void setProductCode(Long productCode) {
		this.productCode = productCode;
	}

	public Integer getOrderedUnitsORL() {
		return orderedUnitsORL;
	}

	public void setOrderedUnitsORL(Integer orderedUnitsORL) {
		this.orderedUnitsORL = orderedUnitsORL;
	}

	public Integer getOrderedUnitsManager() {
		return orderedUnitsManager;
	}

	public void setOrderedUnitsManager(Integer orderedUnitsManager) {
		this.orderedUnitsManager = orderedUnitsManager;
	}

	public Integer getAcceptedUnits() {
		return acceptedUnits;
	}

	public void setAcceptedUnits(Integer acceptedUnits) {
		this.acceptedUnits = acceptedUnits;
	}

	public Double getPrecentOrderFulfillment() {
		return precentOrderFulfillment;
	}

	public void setPrecentOrderFulfillment(Double precentOrderFulfillment) {
		this.precentOrderFulfillment = precentOrderFulfillment;
	}

	public Integer getDiscrepancyQuantity() {
		return discrepancyQuantity;
	}

	public void setDiscrepancyQuantity(Integer discrepancyQuantity) {
		this.discrepancyQuantity = discrepancyQuantity;
	}

	public Double getOrderedRUB() {
		return orderedRUB;
	}

	public void setOrderedRUB(Double orderedRUB) {
		this.orderedRUB = orderedRUB;
	}

	public Double getAcceptedRUB() {
		return acceptedRUB;
	}

	public void setAcceptedRUB(Double acceptedRUB) {
		this.acceptedRUB = acceptedRUB;
	}

	public Double getPrecentOrderCompletionNotNDS() {
		return precentOrderCompletionNotNDS;
	}

	public void setPrecentOrderCompletionNotNDS(Double precentOrderCompletionNotNDS) {
		this.precentOrderCompletionNotNDS = precentOrderCompletionNotNDS;
	}

	public Double getDiscrepancyNotNDS() {
		return discrepancyNotNDS;
	}

	public void setDiscrepancyNotNDS(Double discrepancyNotNDS) {
		this.discrepancyNotNDS = discrepancyNotNDS;
	}

	public String getComment() {
		return comment;
	}
	
	

	public Date getDateOrderORL() {
		return dateOrderORL;
	}
	public void setDateOrderORL(Date dateOrderORL) {
		this.dateOrderORL = dateOrderORL;
	}
	/**
	 * Можно без перехода строки, всё есть в методе
	 * @param comment
	 */
	public void setComment(String comment) {
		if(this.comment != null) {
			this.comment = this.comment + comment + "; \n";
		}else {
			this.comment = comment + "; \n";
		}
		
	}

	@Override
	public int hashCode() {
		return Objects.hash(acceptedRUB, acceptedUnits, counterpartyName, dateFinish, dateStart, discrepancyNotNDS,
				discrepancyQuantity, idReportRow, orderedRUB, orderedUnitsManager, orderedUnitsORL, periodOrderDelivery,
				precentOrderCompletionNotNDS, precentOrderFulfillment, productCode, productGroup, productName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		ReportRow other = (ReportRow) obj;
		return Objects.equals(acceptedRUB, other.acceptedRUB) && Objects.equals(acceptedUnits, other.acceptedUnits)
				&& Objects.equals(counterpartyName, other.counterpartyName)
				&& Objects.equals(dateFinish, other.dateFinish) && Objects.equals(dateStart, other.dateStart)
				&& Objects.equals(discrepancyNotNDS, other.discrepancyNotNDS)
				&& Objects.equals(discrepancyQuantity, other.discrepancyQuantity)
				&& Objects.equals(idReportRow, other.idReportRow) && Objects.equals(orderedRUB, other.orderedRUB)
				&& Objects.equals(orderedUnitsManager, other.orderedUnitsManager)
				&& Objects.equals(orderedUnitsORL, other.orderedUnitsORL)
				&& Objects.equals(periodOrderDelivery, other.periodOrderDelivery)
				&& Objects.equals(precentOrderCompletionNotNDS, other.precentOrderCompletionNotNDS)
				&& Objects.equals(precentOrderFulfillment, other.precentOrderFulfillment)
				&& Objects.equals(productCode, other.productCode) && Objects.equals(productGroup, other.productGroup)
				&& Objects.equals(productName, other.productName);
	}

	@Override
	public String toString() {
		return "ReportRow [idReportRow=" + idReportRow + ", counterpartyName=" + counterpartyName + ", dateStart="
				+ dateStart + ", dateFinish=" + dateFinish + ", periodOrderDelivery=" + periodOrderDelivery
				+ ", productGroup=" + productGroup + ", productName=" + productName + ", productCode=" + productCode
				+ ", orderedUnitsORL=" + orderedUnitsORL + ", orderedUnitsManager=" + orderedUnitsManager
				+ ", acceptedUnits=" + acceptedUnits + ", precentOrderFulfillment=" + precentOrderFulfillment
				+ ", discrepancyQuantity=" + discrepancyQuantity + ", orderedRUB=" + orderedRUB + ", acceptedRUB="
				+ acceptedRUB + ", precentOrderCompletionNotNDS=" + precentOrderCompletionNotNDS
				+ ", discrepancyNotNDS=" + discrepancyNotNDS + "]";
	}
	
}
