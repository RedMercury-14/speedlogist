package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.Objects;

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

	private static final long serialVersionUID = 1759554404124740447L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tg_truck")
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
	private Long chatIdUserTruck;
	
	@Column(name = "name_list")
	private String nameList;
	
	@Column(name = "id_list")
	private Integer idList;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "company_name")
	private String companyName;
	
	/**
	 * @return the companyName
	 */
	public String getCompanyName() {
		return companyName;
	}

	/**
	 * @param companyName the companyName to set
	 */
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	/**
	 * @return the status
	 */
	public Integer getStatus() {
		return status;
	}

	/**
	 * @param status the status to set
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * @return the nameList
	 */
	public String getNameList() {
		return nameList;
	}

	/**
	 * @param nameList the nameList to set
	 */
	public void setNameList(String nameList) {
		this.nameList = nameList;
	}

	/**
	 * @return the idList
	 */
	public Integer getIdList() {
		return idList;
	}

	/**
	 * @param idList the idList to set
	 */
	public void setIdList(Integer idList) {
		this.idList = idList;
	}

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
	public Long getChatIdUserTruck() {
		return chatIdUserTruck;
	}

	/**
	 * @param chatIdUserTruck the chatIdUserTruck to set
	 */
	public void setChatIdUserTruck(Long chatIdUserTruck) {
		this.chatIdUserTruck = chatIdUserTruck;
	}

    // Метод клонирования
    public TGTruck cloneWithNewId(Integer newIdTGTruck) {
        TGTruck clonedTruck = new TGTruck();
        
        clonedTruck.idTGTruck = newIdTGTruck;
        clonedTruck.numTruck = this.numTruck;
        clonedTruck.modelTruck = this.modelTruck;
        clonedTruck.pall = this.pall;
        clonedTruck.typeTrailer = this.typeTrailer;
        clonedTruck.dateRequisition = this.dateRequisition;
        clonedTruck.cargoCapacity = this.cargoCapacity;
        clonedTruck.chatIdUserTruck = this.chatIdUserTruck;
        clonedTruck.nameList = this.nameList;
        clonedTruck.idList = this.idList;
        clonedTruck.status = this.status;
        clonedTruck.companyName = this.companyName;

        return clonedTruck;
    }
	
	@Override
	public int hashCode() {
		return Objects.hash(cargoCapacity, chatIdUserTruck, dateRequisition, idList, idTGTruck, modelTruck, nameList,
				numTruck, pall, status, typeTrailer);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TGTruck other = (TGTruck) obj;
		return Objects.equals(cargoCapacity, other.cargoCapacity)
				&& Objects.equals(chatIdUserTruck, other.chatIdUserTruck)
				&& Objects.equals(dateRequisition, other.dateRequisition) && Objects.equals(idList, other.idList)
				&& Objects.equals(idTGTruck, other.idTGTruck) && Objects.equals(modelTruck, other.modelTruck)
				&& Objects.equals(nameList, other.nameList) && Objects.equals(numTruck, other.numTruck)
				&& Objects.equals(pall, other.pall) && Objects.equals(status, other.status)
				&& Objects.equals(typeTrailer, other.typeTrailer);
	}

	
	public String toJSON() {
		return "{\"idTGTruck\":\"" + idTGTruck + "\", \"numTruck\":\"" + numTruck + "\", \"modelTruck\":\"" + modelTruck
				+ "\", \"pall\":\"" + pall + "\", \"typeTrailer\":\"" + typeTrailer + "\", \"dateRequisition\":\""
				+ dateRequisition + "\", \"cargoCapacity\":\"" + cargoCapacity + "\", \"chatIdUserTruck\":\""
				+ chatIdUserTruck + "\", \"nameList\":\"" + nameList + "\", \"idList\":\"" + idList
				+ "\", \"status\":\"" + status + "\", \"companyName\":\"" + companyName + "\"}";
	}

	@Override
	public String toString() {
		return "TGTruck [idTGTruck=" + idTGTruck + ", numTruck=" + numTruck + ", modelTruck=" + modelTruck + ", pall="
				+ pall + ", typeTrailer=" + typeTrailer + ", dateRequisition=" + dateRequisition + ", cargoCapacity="
				+ cargoCapacity + ", chatIdUserTruck=" + chatIdUserTruck + "]";
	}
	
	
	
}
