package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * объект Машины для телеграмм ботов
 */
@Entity
@Table(name = "tg_truck")
public class TGTruck implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1759554404124740447L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tg_user")
	private Integer idTGTruck;
	
	@Column(name = "num_truck")
	private String numTruck;
	
	@Column(name = "model_truck")
	private String modelTruck;
	
	@Column(name = "pall")
	private Integer pall;
	
	@Column(name = "type_trailer")
	private String typeTrailer;
	
	@Column(name = "date_requisition")
	private Date dateRequisition;
	
	@Column(name = "cargo_capacity")
	private String cargoCapacity;
	
	@Column(name = "chat_id_user_truck")
	private Integer chatIdUserTruck;

	public Integer getIdTGTruck() {
		return idTGTruck;
	}

	public void setIdTGTruck(Integer idTGTruck) {
		this.idTGTruck = idTGTruck;
	}

	public String getNumTruck() {
		return numTruck;
	}

	public void setNumTruck(String numTruck) {
		this.numTruck = numTruck;
	}

	public String getModelTruck() {
		return modelTruck;
	}

	public void setModelTruck(String modelTruck) {
		this.modelTruck = modelTruck;
	}

	public Integer getPall() {
		return pall;
	}

	public void setPall(Integer pall) {
		this.pall = pall;
	}

	public String getTypeTrailer() {
		return typeTrailer;
	}

	public void setTypeTrailer(String typeTrailer) {
		this.typeTrailer = typeTrailer;
	}
	
	/**
	 * отдаёт дату на которую заявлялась машина SQLDate
	 * @return
	 */
	public Date getDateRequisition() {
		return dateRequisition;
	}
	
	/**
	 * отдаёт дату на которую заявлялась машина LocalDate
	 * @return
	 */
	public LocalDate getDateRequisitionLocalDate() {
		return dateRequisition != null ? dateRequisition.toLocalDate() : null;
	}

	/**
	 * Записывает дату на которую заявлялась машина
	 * @param dateRequisition
	 */
	public void setDateRequisition(Date dateRequisition) {
		this.dateRequisition = dateRequisition;
	}
	
	/**
	 * Записывает дату на которую заявлялась машина
	 * @param dateRequisition
	 */
	public void setDateRequisition(LocalDate dateRequisition) {
		this.dateRequisition = Date.valueOf(dateRequisition);
	}
	
	public String getTruckForBot (){
		return numTruck + " ("+typeTrailer+") " + cargoCapacity+"/"+pall + " (вес/паллеты)";
	}

	/**
	 * @return Заявленная грузоподъемность авто
	 */
	public String getCargoCapacity() {
		return cargoCapacity;
	}

	/**
	 * @param задаёт заявленную грузоподъемность авто
	 */
	public void setCargoCapacity(String cargoCapacity) {
		this.cargoCapacity = cargoCapacity;
	}



	/**
	 * @return the chatIdUserTruck
	 */
	public Integer getChatIdUserTruck() {
		return chatIdUserTruck;
	}

	/**
	 * @param chatIdUserTruck the chatIdUserTruck to set
	 */
	public void setChatIdUserTruck(Integer chatIdUserTruck) {
		this.chatIdUserTruck = chatIdUserTruck;
	}

	@Override
	public String toString() {
		return "TGTruck [idTGTruck=" + idTGTruck + ", numTruck=" + numTruck + ", modelTruck=" + modelTruck + ", pall="
				+ pall + ", typeTrailer=" + typeTrailer + ", dateRequisition=" + dateRequisition + ", cargoCapacity="
				+ cargoCapacity + ", chatIdUserTruck=" + chatIdUserTruck + "]";
	}
	
}
