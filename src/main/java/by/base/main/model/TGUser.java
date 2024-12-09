package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

/**
 * User который используется для телеграмм ботов
 * @author DIma Hrusgevski
 */
@Entity
@Table(name = "tg_user")
public class TGUser implements Serializable{
	
	private static final long serialVersionUID = -2097537784597181301L;

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_tg_user")
	private Integer idTGUser;
	
	@Column(name = "company_name")
	private String companyName;
	
	@Column(name = "chat_id")
	private Long chatId;
	
	@Column(name = "telephone")
	private String telephone;
	
	@Column(name = "status")
	private Integer status;
	
	@Column(name = "command")
	private String command;
	
	@Column(name = "date_order_truck_optimization")
	private Date dateOrderTruckOptimization;
	
	@Transient
	private Map<String, TGTruck> trucksForBot = null;
	
	@Column(name = "validity_truck")
	private String validityTruck;
	
	public TGUser() {
		// TODO Auto-generated constructor stub
	}

	public Integer getIdTGUser() {
		return idTGUser;
	}

	public void setIdTGUser(Integer idTGUser) {
		this.idTGUser = idTGUser;
	}

	public Long getChatId() {
		return chatId;
	}

	public void setChatId(Long chatId) {
		this.chatId = chatId;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public Integer getStatus() {
		return status;
	}

	public void setStatus(Integer status) {
		this.status = status;
	}

	public Date getDateOrderTruckOptimization() {
		return dateOrderTruckOptimization;
	}

	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}

	public void setDateOrderTruckOptimization(Date dateOrderTruckOptimization) {
		this.dateOrderTruckOptimization = dateOrderTruckOptimization;
	}
	
	public void setTrucksForBot(Map<String, TGTruck> trucksForBot) {
		this.trucksForBot = trucksForBot;
	}
	
	public Map<String, TGTruck> getTrucksForBot() {
		return trucksForBot;
	}
	
	public TGTruck getTrucksForBot(String numTruck) {
		return trucksForBot.get(numTruck);
	}
	
	public void putTrucksForBot(String numTruck, TGTruck truck) {
		if(this.trucksForBot == null) {
			this.trucksForBot = new HashMap<String, TGTruck>();
			this.trucksForBot.put(numTruck, truck);
		}else {
			this.trucksForBot.put(numTruck, truck);
		}
	}
	
	public void removeTrucksForBot(String numTruck) {
		if(this.trucksForBot != null) {
			this.trucksForBot.remove(numTruck);
		}
	}

	public String getValidityTruck() {
		return validityTruck;
	}

	public void setValidityTruck(String validityTruck) {
		this.validityTruck = validityTruck;
	}

	public String getCompanyName() {
		return companyName;
	}

	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}

	@Override
	public String toString() {
		return "TGUser [idTGUser=" + idTGUser + ", companyName=" + companyName + ", chatId=" + chatId + ", telephone="
				+ telephone + ", status=" + status + ", command=" + command + ", dateOrderTruckOptimization="
				+ dateOrderTruckOptimization + ", trucksForBot=" + trucksForBot + ", validityTruck=" + validityTruck
				+ "]";
	}
	
	
	
}
