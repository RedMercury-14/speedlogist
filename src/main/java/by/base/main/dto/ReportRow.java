package by.base.main.dto;

import java.sql.Date;

public class ReportRow {
	
	private Long idReportRow;
	
	private String counterpartyName;
	
	private Date dateStart;
	
	private Date dateFinish;
	
	/**
	 * dateStart - dateFinish
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
	
	

}
