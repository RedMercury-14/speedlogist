package by.base.main.model;

import java.io.Serializable;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;

@Entity
@Table(name = "act_of_completed_works")
public class Act implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = -9044169476767565627L;
	
	
	
	public Act() {
	}

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idact")
	private Integer idAct;
	
	@Column(name = "idRoutes")
	private String idRoutes;
	
	@Column(name = "finalCost")
	private Double finalCost;
	
	@Column(name = "NDS")
	private Double nds;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "time")
	private String time;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "cancel")
	private String cancel;
	
	@Column(name = "numAct")
	private String numAct;
	
	@Column(name = "currency")
	private String currency;
	
	@Column(name = "date")
	private LocalDate date;

	@Column(name = "secret_code")
	private String secretCode;
	
	@Column(name = "column_date_load", columnDefinition = "TEXT", nullable = true)
    private String columnDateLoad; // Дата загрузки (разделитель ^)

    @Column(name = "column_date_unload", columnDefinition = "TEXT", nullable = true)
    private String columnDateUnload; // Дата выгрузки (разделитель ^)

    @Column(name = "column_name_route", columnDefinition = "TEXT", nullable = true)
    private String columnNameRoute; // Название маршрута (разделитель ^)

    @Column(name = "column_num_truck", columnDefinition = "TEXT", nullable = true)
    private String columnNumTruck; // Номер транспортного средства (разделитель ^)

    @Column(name = "column_num_route_list", columnDefinition = "TEXT", nullable = true)
    private String columnNumRouteList; // Номер путевого листа (разделитель ^)

    @Column(name = "column_num_document", columnDefinition = "TEXT", nullable = true)
    private String columnNumDocument; // Номера ЦМР/ТТН (разделитель ^)

    @Column(name = "column_veigth_cargo", columnDefinition = "TEXT", nullable = true)
    private String columnVeigthCargo; // Вес груза в тоннах (разделитель ^)

    @Column(name = "column_summ_cost", columnDefinition = "TEXT", nullable = true)
    private String columnSummCost; // Сумма (разделитель ^)

    @Column(name = "column_nds_summ", columnDefinition = "TEXT", nullable = true)
    private String columnNdsSumm; // Сумма НДС (разделитель ^)

    @Column(name = "column_toll_roads", columnDefinition = "TEXT", nullable = true)
    private String columnTollRoads; // Платные дороги (разделитель ^)

    @Column(name = "column_total", columnDefinition = "TEXT", nullable = true)
    private String columnTotal; // Итого (разделитель ^)

    @Column(name = "documents_arrived", nullable = true)
    private Timestamp documentsArrived; // Время, когда документы пришли (если null - документы не пришли)

    @Column(name = "user_documents_arrived", length = 255, nullable = true)
    private String userDocumentsArrived; // Пользователь, записавший факт прихода документов
    
    @Column(name = "carrier", columnDefinition = "TEXT", nullable = true)
    private String carrier; // Перевозчик (разделитель ^)

    @Column(name = "carrier_head", columnDefinition = "TEXT", nullable = true)
    private String carrierHead; // Директор перевозчика (разделитель ^)

    @Column(name = "carrier_driver", columnDefinition = "TEXT", nullable = true)
    private String carrierDriver; // Водитель (разделитель ^)

    @Column(name = "order_has_slot", columnDefinition = "TEXT", nullable = true)
    private String order; // Номера маршрутов (разделитель ^)

    @Column(name = "logist", columnDefinition = "TEXT", nullable = true)
    private String logist; // Логисты (разделитель ^)
    
    @Column(name = "total_cost")
    private Double totalCost;
    
    @Column(name = "total_way")
    private Double totalWay;
    
    @Column(name = "total_nds")
    private Double totalNds;
    
    @Column(name = "column_expedition_сost", columnDefinition = "TEXT", nullable = true)
    private String columnExpeditionCost; // кспедиторские услуги (разделитель ^)
    
    @Column(name = "total_expedition_сost")
    private Double totalExpeditionCost;
	
	public Integer getIdAct() {
		return idAct;
	}

	public void setIdAct(Integer idAct) {
		this.idAct = idAct;
	}

	public String getIdRoutes() {
		return idRoutes;
	}

	public void setIdRoutes(String idRoutes) {
		this.idRoutes = idRoutes;
	}

	public Double getFinalCost() {
		return finalCost;
	}

	public void setFinalCost(Double finalCost) {
		this.finalCost = finalCost;
	}

	public Double getNds() {
		return nds;
	}

	public void setNds(Double nds) {
		this.nds = nds;
	}

	public String getComment() {
		return comment;
	}
	
	public String getSecretCode() {
		return secretCode;
	}

	public void setSecretCode(String secretCode) {
		this.secretCode = secretCode;
	}	

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getCancel() {
		return cancel;
	}

	public void setCancel(String cancel) {
		this.cancel = cancel;
	}

	public String getNumAct() {
		return numAct;
	}

	public void setNumAct(String numAct) {
		this.numAct = numAct;
	}	

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public String getColumnDateLoad() {
		return columnDateLoad;
	}

	public void setColumnDateLoad(String columnDateLoad) {
		this.columnDateLoad = columnDateLoad.isEmpty() ? null : columnDateLoad;
	}

	public String getColumnDateUnload() {
		return columnDateUnload;
	}

	public void setColumnDateUnload(String columnDateUnload) {
		this.columnDateUnload = columnDateUnload.isEmpty() ? null : columnDateUnload;
	}

	public String getColumnNameRoute() {
		return columnNameRoute;
	}

	public void setColumnNameRoute(String columnNameRoute) {
		this.columnNameRoute = columnNameRoute.isEmpty() ? null : columnNameRoute;
	}

	public String getColumnNumTruck() {
		return columnNumTruck;
	}

	public void setColumnNumTruck(String columnNumTruck) {
		this.columnNumTruck = columnNumTruck.isEmpty() ? null : columnNumTruck;
	}

	public String getColumnNumRouteList() {
		return columnNumRouteList;
	}

	public void setColumnNumRouteList(String columnNumRouteList) {
		this.columnNumRouteList = columnNumRouteList.isEmpty() ? null : columnNumRouteList;
	}

	public String getColumnNumDocument() {
		return columnNumDocument;
	}

	public void setColumnNumDocument(String columnNumDocument) {
		this.columnNumDocument = columnNumDocument.isEmpty() ? null : columnNumDocument;
	}

	public String getColumnVeigthCargo() {
		return columnVeigthCargo;
	}

	public void setColumnVeigthCargo(String columnVeigthCargo) {
		this.columnVeigthCargo = columnVeigthCargo.isEmpty() ? null : columnVeigthCargo;
	}

	public String getColumnSummCost() {
		return columnSummCost;
	}

	public void setColumnSummCost(String columnSummCost) {
		this.columnSummCost = columnSummCost.isEmpty() ? null : columnSummCost;
	}

	public String getColumnNdsSumm() {
		return columnNdsSumm;
	}

	public void setColumnNdsSumm(String columnNdsSumm) {
		this.columnNdsSumm = columnNdsSumm.isEmpty() ? null : columnNdsSumm;
	}

	public String getColumnTollRoads() {
		return columnTollRoads;
	}

	public void setColumnTollRoads(String columnTollRoads) {
		this.columnTollRoads = columnTollRoads.isEmpty() ? null : columnTollRoads;
	}

	public String getColumnTotal() {
		return columnTotal;
	}

	public void setColumnTotal(String columnTotal) {
		this.columnTotal = columnTotal.isEmpty() ? null : columnTotal;
	}

	public Timestamp getDocumentsArrived() {
		return documentsArrived;
	}

	public void setDocumentsArrived(Timestamp documentsArrived) {
		this.documentsArrived = documentsArrived;
	}

	public String getUserDocumentsArrived() {
		return userDocumentsArrived;
	}

	public void setUserDocumentsArrived(String userDocumentsArrived) {
		this.userDocumentsArrived = userDocumentsArrived;
	}

	public String getCarrier() {
		return carrier;
	}

	public void setCarrier(String carrier) {
		this.carrier = carrier;
	}

	public String getCarrierHead() {
		return carrierHead;
	}

	public void setCarrierHead(String carrierHead) {
		this.carrierHead = carrierHead;
	}

	public String getCarrierDriver() {
		return carrierDriver;
	}

	public void setCarrierDriver(String carrierDriver) {
		this.carrierDriver = carrierDriver;
	}

	public String getOrder() {
		return order;
	}

	public void setOrder(String order) {
		this.order = order;
	}

	public String getLogist() {
		return logist;
	}

	public void setLogist(String logist) {
		this.logist = logist;
	}

	public Double getTotalCost() {
		return totalCost;
	}

	public void setTotalCost(Double totalCost) {
		this.totalCost = totalCost;
	}

	public Double getTotalWay() {
		return totalWay;
	}

	public void setTotalWay(Double totalWey) {
		this.totalWay = totalWey;
	}

	public Double getTotalNds() {
		return totalNds;
	}

	public void setTotalNds(Double totalNds) {
		this.totalNds = totalNds;
	}

	public String getColumnExpeditionCost() {
		return columnExpeditionCost;
	}

	public void setColumnExpeditionCost(String columnExpeditionCost) {
		this.columnExpeditionCost = columnExpeditionCost.isEmpty() ? null : columnExpeditionCost;
	}

	public Double getTotalExpeditionCost() {
		return totalExpeditionCost;
	}

	public void setTotalExpeditionCost(Double totalExpeditionCost) {
		this.totalExpeditionCost = totalExpeditionCost;
	}

	@Override
	public int hashCode() {
		return Objects.hash(cancel, comment, finalCost, idAct, idRoutes, nds, numAct, status, time);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Act other = (Act) obj;
		return Objects.equals(cancel, other.cancel) && Objects.equals(comment, other.comment)
				&& Objects.equals(finalCost, other.finalCost) && Objects.equals(idAct, other.idAct)
				&& Objects.equals(idRoutes, other.idRoutes) && Objects.equals(nds, other.nds)
				&& Objects.equals(numAct, other.numAct) && Objects.equals(status, other.status)
				&& Objects.equals(time, other.time);
	}

	@Override
	public String toString() {
		return "Act [idAct=" + idAct + ", idRoutes=" + idRoutes + ", finalCost=" + finalCost + ", nds=" + nds
				+ ", comment=" + comment + ", time=" + time + ", status=" + status + ", cancel=" + cancel + ", numAct="
				+ numAct + "]";
	}
	

}
