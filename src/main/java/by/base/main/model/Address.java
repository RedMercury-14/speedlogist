package by.base.main.model;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonBackReference;

@Entity(name = "Address")
@Table(name = "address")
public class Address {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idaddress")
	private Integer idAddress;
	
	@Column(name = "body_address")
	private String bodyAddress;
	
	@Column(name = "date")
	private Date date;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "pall")
	private String pall;
	
	@Column(name = "weight")
	private String weight;
	
	@Column(name = "volume")
	private String volume;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "time_frame")
	private String timeFrame;
	
	@Column(name = "contact")
	private String contact;
	
	@Column(name = "cargo")
	private String cargo;
	
	@Column(name = "customs_address")
	private String customsAddress;
	
	@Column(name = "time")
	private Time time;
	
	@Column(name = "isCorrect")
	private Boolean isCorrect;
	
	@Column(name = "old_idadress")
	private Integer oldIdaddress;
	
	@Column(name = "tnvd")
	private String tnvd;
	
	@Column(name = "point_number")
	private Integer pointNumber;	
	
	@ManyToOne(fetch = FetchType.LAZY, 
			cascade = { CascadeType.ALL })
	@JoinColumn(name = "order_idorder") 
	@JsonBackReference
	private Order order;

	public Address() {
		super();
	}
	
	

	/**
	 * @param bodyAddress
	 * @param date
	 * @param type
	 * @param pall
	 * @param weight
	 * @param volume
	 * @param comment
	 * @param timeFrame
	 * @param contact
	 * @param cargo
	 */
	public Address(String bodyAddress, Date date, String type, String pall, String weight, String volume, String timeFrame, String contact, String cargo) {
		super();
		this.bodyAddress = bodyAddress;
		this.date = date;
		this.type = type;
		this.pall = pall;
		this.weight = weight;
		this.volume = volume;
		this.timeFrame = timeFrame;
		this.contact = contact;
		this.cargo = cargo;
	}



	public Integer getIdAddress() {
		return idAddress;
	}

	public void setIdAddress(Integer idAddress) {
		this.idAddress = idAddress;
	}

	public String getBodyAddress() {
		return bodyAddress;
	}

	public void setBodyAddress(String bodyAddress) {
		this.bodyAddress = bodyAddress;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getPall() {
		return pall;
	}

	public void setPall(String pall) {
		this.pall = pall;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getVolume() {
		return volume;
	}

	public void setVolume(String volume) {
		this.volume = volume;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	public String getTimeFrame() {
		return timeFrame;
	}

	public void setTimeFrame(String timeFrame) {
		this.timeFrame = timeFrame;
	}

	public String getContact() {
		return contact;
	}

	public void setContact(String contact) {
		this.contact = contact;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public Order getOrder() {
		return order;
	}

	public void setOrder(Order order) {
		this.order = order;
	}
	
	public String getCustomsAddress() {
		return customsAddress;
	}

	public void setCustomsAddress(String customsAddress) {
		this.customsAddress = customsAddress;
	}

	public Time getTime() {
		return time;
	}

	public void setTime(Time time) {
		this.time = time;
	}

	public Boolean getIsCorrect() {
		return isCorrect;
	}

	public void setIsCorrect(Boolean isCorrect) {
		this.isCorrect = isCorrect;
	}

	public Integer getOldIdaddress() {
		return oldIdaddress;
	}

	public void setOldIdaddress(Integer oldIdaddress) {
		this.oldIdaddress = oldIdaddress;
	}

	public String getTnvd() {
		return tnvd;
	}

	public void setTnvd(String tnvd) {
		this.tnvd = tnvd;
	}

	public Integer getPointNumber() {
		return pointNumber;
	}



	public void setPointNumber(Integer pointNumber) {
		this.pointNumber = pointNumber;
	}



	@Override
	public int hashCode() {
		return Objects.hash(bodyAddress, cargo, comment, contact, date, idAddress, order, pall, timeFrame, type, volume,
				weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		return Objects.equals(bodyAddress, other.bodyAddress) && Objects.equals(cargo, other.cargo)
				&& Objects.equals(comment, other.comment) && Objects.equals(contact, other.contact)
				&& Objects.equals(date, other.date) && Objects.equals(idAddress, other.idAddress)
				&& Objects.equals(order, other.order) && Objects.equals(pall, other.pall)
				&& Objects.equals(timeFrame, other.timeFrame) && Objects.equals(type, other.type)
				&& Objects.equals(volume, other.volume) && Objects.equals(weight, other.weight);
	}



	@Override
	public String toString() {
		return "Address [idAddress=" + idAddress + ", bodyAddress=" + bodyAddress + ", date=" + date + ", type=" + type
				+ ", pall=" + pall + ", weight=" + weight + ", volume=" + volume + ", comment=" + comment
				+ ", timeFrame=" + timeFrame + ", contact=" + contact + ", cargo=" + cargo + ", customsAddress="
				+ customsAddress + ", time=" + time + ", isCorrect=" + isCorrect + ", oldIdaddress=" + oldIdaddress
				+ ", tnvd=" + tnvd + "]";
	}


}
