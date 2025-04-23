package by.base.main.model;

import java.sql.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author Dima Hrushevski
 */

@Entity
@Table(name = "price_protocol")
public class PriceProtocol {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idprice_protocol")
    private int idPriceProtocol;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "product_code")
    private String productCode;

    @Column(name = "tnv_code")
    private String tnvCode;

    @Column(name = "name")
    private String name;

    @Column(name = "price_producer")
    private Double priceProducer;

    @Column(name = "cost_importer")
    private Double costImporter;

    @Column(name = "markup_importer_percent")
    private Double markupImporterPercent;

    @Column(name = "discount_percent")
    private Double discountPercent;

    @Column(name = "wholesale_discount_percent")
    private Double wholesaleDiscountPercent;

    @Column(name = "price_without_vat")
    private Double priceWithoutVat;

    @Column(name = "wholesale_markup_percent")
    private Double wholesaleMarkupPercent;

    @Column(name = "vat_rate")
    private Double vatRate;

    @Column(name = "price_with_vat")
    private Double priceWithVat;

    @Column(name = "country_origin")
    private String countryOrigin;

    @Column(name = "manufacturer")
    private String manufacturer;

    @Column(name = "unit_per_pack")
    private String unitPerPack;

    @Column(name = "shelf_life_days")
    private Integer shelfLifeDays;

    @Column(name = "current_price")
    private Double currentPrice;

    @Column(name = "price_change_percent")
    private Double priceChangePercent;

    @Column(name = "last_price_change_date")
    private Date lastPriceChangeDate;

    @Column(name = "date_valid_from")
    private Date dateValidFrom;

    @Column(name = "date_valid_to")
    private Date dateValidTo;

    @Column(name = "contract_number")
    private String contractNumber;
    
    @Column(name = "supplier")
    private String supplier;

    @Column(name = "contract_date")
    private Date contractDate;
    
    @Column(name = "date_arrival")
    private Date dateArrival;

    
	public Date getDateArrival() {
		return dateArrival;
	}

	public void setDateArrival(Date dateArrival) {
		this.dateArrival = dateArrival;
	}

	public String getSupplier() {
		return supplier;
	}

	public void setSupplier(String supplier) {
		this.supplier = supplier;
	}

	public int getIdPriceProtocol() {
		return idPriceProtocol;
	}

	public void setIdPriceProtocol(int idPriceProtocol) {
		this.idPriceProtocol = idPriceProtocol;
	}

	public String getBarcode() {
		return barcode;
	}

	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}

	public String getProductCode() {
		return productCode;
	}

	public void setProductCode(String productCode) {
		this.productCode = productCode;
	}

	public String getTnvCode() {
		return tnvCode;
	}

	public void setTnvCode(String tnvCode) {
		this.tnvCode = tnvCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getPriceProducer() {
		return priceProducer;
	}

	public void setPriceProducer(Double priceProducer) {
		this.priceProducer = priceProducer;
	}

	public Double getCostImporter() {
		return costImporter;
	}

	public void setCostImporter(Double costImporter) {
		this.costImporter = costImporter;
	}

	public Double getMarkupImporterPercent() {
		return markupImporterPercent;
	}

	public void setMarkupImporterPercent(Double markupImporterPercent) {
		this.markupImporterPercent = markupImporterPercent;
	}

	public Double getDiscountPercent() {
		return discountPercent;
	}

	public void setDiscountPercent(Double discountPercent) {
		this.discountPercent = discountPercent;
	}

	public Double getWholesaleDiscountPercent() {
		return wholesaleDiscountPercent;
	}

	public void setWholesaleDiscountPercent(Double wholesaleDiscountPercent) {
		this.wholesaleDiscountPercent = wholesaleDiscountPercent;
	}

	public Double getPriceWithoutVat() {
		return priceWithoutVat;
	}

	public void setPriceWithoutVat(Double priceWithoutVat) {
		this.priceWithoutVat = priceWithoutVat;
	}

	public Double getWholesaleMarkupPercent() {
		return wholesaleMarkupPercent;
	}

	public void setWholesaleMarkupPercent(Double wholesaleMarkupPercent) {
		this.wholesaleMarkupPercent = wholesaleMarkupPercent;
	}

	public Double getVatRate() {
		return vatRate;
	}

	public void setVatRate(Double vatRate) {
		this.vatRate = vatRate;
	}

	public Double getPriceWithVat() {
		return priceWithVat;
	}

	public void setPriceWithVat(Double priceWithVat) {
		this.priceWithVat = priceWithVat;
	}

	public String getCountryOrigin() {
		return countryOrigin;
	}

	public void setCountryOrigin(String countryOrigin) {
		this.countryOrigin = countryOrigin;
	}

	public String getManufacturer() {
		return manufacturer;
	}

	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}

	public String getUnitPerPack() {
		return unitPerPack;
	}

	public void setUnitPerPack(String unitPerPack) {
		this.unitPerPack = unitPerPack;
	}

	public Integer getShelfLifeDays() {
		return shelfLifeDays;
	}

	public void setShelfLifeDays(Integer shelfLifeDays) {
		this.shelfLifeDays = shelfLifeDays;
	}

	public Double getCurrentPrice() {
		return currentPrice;
	}

	public void setCurrentPrice(Double currentPrice) {
		this.currentPrice = currentPrice;
	}

	public Double getPriceChangePercent() {
		return priceChangePercent;
	}

	public void setPriceChangePercent(Double priceChangePercent) {
		this.priceChangePercent = priceChangePercent;
	}

	public Date getLastPriceChangeDate() {
		return lastPriceChangeDate;
	}

	public void setLastPriceChangeDate(Date lastPriceChangeDate) {
		this.lastPriceChangeDate = lastPriceChangeDate;
	}

	public Date getDateValidFrom() {
		return dateValidFrom;
	}

	public void setDateValidFrom(Date dateValidFrom) {
		this.dateValidFrom = dateValidFrom;
	}

	public Date getDateValidTo() {
		return dateValidTo;
	}

	public void setDateValidTo(Date dateValidTo) {
		this.dateValidTo = dateValidTo;
	}

	public String getContractNumber() {
		return contractNumber;
	}

	public void setContractNumber(String contractNumber) {
		this.contractNumber = contractNumber;
	}

	public Date getContractDate() {
		return contractDate;
	}

	public void setContractDate(Date contractDate) {
		this.contractDate = contractDate;
	}

	@Override
	public int hashCode() {
		return Objects.hash(barcode, contractDate, contractNumber, costImporter, countryOrigin, currentPrice,
				discountPercent, idPriceProtocol, lastPriceChangeDate, manufacturer, markupImporterPercent, name,
				priceChangePercent, priceProducer, priceWithVat, priceWithoutVat, productCode, shelfLifeDays, tnvCode,
				unitPerPack, dateValidFrom, dateValidTo, vatRate, wholesaleDiscountPercent, wholesaleMarkupPercent);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PriceProtocol other = (PriceProtocol) obj;
		return Objects.equals(barcode, other.barcode) && Objects.equals(contractDate, other.contractDate)
				&& Objects.equals(contractNumber, other.contractNumber)
				&& Objects.equals(costImporter, other.costImporter)
				&& Objects.equals(countryOrigin, other.countryOrigin)
				&& Objects.equals(currentPrice, other.currentPrice)
				&& Objects.equals(discountPercent, other.discountPercent) && idPriceProtocol == other.idPriceProtocol
				&& Objects.equals(lastPriceChangeDate, other.lastPriceChangeDate)
				&& Objects.equals(manufacturer, other.manufacturer)
				&& Objects.equals(markupImporterPercent, other.markupImporterPercent)
				&& Objects.equals(name, other.name) && Objects.equals(priceChangePercent, other.priceChangePercent)
				&& Objects.equals(priceProducer, other.priceProducer)
				&& Objects.equals(priceWithVat, other.priceWithVat)
				&& Objects.equals(priceWithoutVat, other.priceWithoutVat)
				&& Objects.equals(productCode, other.productCode) && Objects.equals(shelfLifeDays, other.shelfLifeDays)
				&& Objects.equals(tnvCode, other.tnvCode) && Objects.equals(unitPerPack, other.unitPerPack)
				&& Objects.equals(dateValidFrom, other.dateValidFrom) && Objects.equals(dateValidTo, other.dateValidTo)
				&& Objects.equals(vatRate, other.vatRate)
				&& Objects.equals(wholesaleDiscountPercent, other.wholesaleDiscountPercent)
				&& Objects.equals(wholesaleMarkupPercent, other.wholesaleMarkupPercent);
	}

	@Override
	public String toString() {
		return "PriceProtocol [idPriceProtocol=" + idPriceProtocol + ", barcode=" + barcode + ", productCode="
				+ productCode + ", tnvCode=" + tnvCode + ", name=" + name + ", priceProducer=" + priceProducer
				+ ", costImporter=" + costImporter + ", markupImporterPercent=" + markupImporterPercent
				+ ", discountPercent=" + discountPercent + ", wholesaleDiscountPercent=" + wholesaleDiscountPercent
				+ ", priceWithoutVat=" + priceWithoutVat + ", wholesaleMarkupPercent=" + wholesaleMarkupPercent
				+ ", vatRate=" + vatRate + ", priceWithVat=" + priceWithVat + ", countryOrigin=" + countryOrigin
				+ ", manufacturer=" + manufacturer + ", unitPerPack=" + unitPerPack + ", shelfLifeDays=" + shelfLifeDays
				+ ", currentPrice=" + currentPrice + ", priceChangePercent=" + priceChangePercent
				+ ", lastPriceChangeDate=" + lastPriceChangeDate + ", dateValidFrom=" + dateValidFrom + ", dateValidTo="
				+ dateValidTo + ", contractNumber=" + contractNumber + ", supplier=" + supplier + ", contractDate="
				+ contractDate + ", dateArrival=" + dateArrival + "]";
	}

	

	
    
}
