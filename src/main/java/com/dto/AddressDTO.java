package com.dto;

import java.sql.Date;
import java.sql.Time;
import java.util.Objects;

public class AddressDTO{

	private Integer idAddress;
	
	private String bodyAddress;
	
	private Date date;
	
	private String type;
	
	private String pall;
	
	private String weight;
	
	private String volume;
	
	private String comment;
	
	private String timeFrame;
	
	private String contact;
	
	private String cargo;
	
	private String customsAddress;
	
	private Time time;
	
	private Boolean isCorrect;
	
	private Integer oldIdaddress;
	
	private String tnvd;
	
	private Integer pointNumber;
	
	public AddressDTO(Integer idAddress, String bodyAddress, Object date, String type, String pall, String weight,
			String volume, String comment, String timeFrame, String contact, String cargo, String customsAddress,
			Object time, Object isCorrect, Integer oldIdaddress, String tnvd, Integer pointNumber) {
		super();
		this.idAddress = idAddress;
		this.bodyAddress = bodyAddress;
		this.date = (Date) date;
		this.type = type;
		this.pall = pall;
		this.weight = weight;
		this.volume = volume;
		this.comment = comment;
		this.timeFrame = timeFrame;
		this.contact = contact;
		this.cargo = cargo;
		this.customsAddress = customsAddress;
		this.time = (Time) time;
		this.isCorrect = (Boolean) isCorrect;
		this.oldIdaddress = oldIdaddress;
		this.tnvd = tnvd;
		this.pointNumber = pointNumber;
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
		return Objects.hash(bodyAddress, cargo, comment, contact, customsAddress, date, idAddress, isCorrect,
				oldIdaddress, pall, pointNumber, time, timeFrame, tnvd, type, volume, weight);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AddressDTO other = (AddressDTO) obj;
		return Objects.equals(bodyAddress, other.bodyAddress) && Objects.equals(cargo, other.cargo)
				&& Objects.equals(comment, other.comment) && Objects.equals(contact, other.contact)
				&& Objects.equals(customsAddress, other.customsAddress) && Objects.equals(date, other.date)
				&& Objects.equals(idAddress, other.idAddress) && Objects.equals(isCorrect, other.isCorrect)
				&& Objects.equals(oldIdaddress, other.oldIdaddress) && Objects.equals(pall, other.pall)
				&& Objects.equals(pointNumber, other.pointNumber) && Objects.equals(time, other.time)
				&& Objects.equals(timeFrame, other.timeFrame) && Objects.equals(tnvd, other.tnvd)
				&& Objects.equals(type, other.type) && Objects.equals(volume, other.volume)
				&& Objects.equals(weight, other.weight);
	}

	@Override
	public String toString() {
		return "AddressDTO [idAddress=" + idAddress + ", bodyAddress=" + bodyAddress + ", date=" + date + ", type="
				+ type + ", pall=" + pall + ", weight=" + weight + ", volume=" + volume + ", comment=" + comment
				+ ", timeFrame=" + timeFrame + ", contact=" + contact + ", cargo=" + cargo + ", customsAddress="
				+ customsAddress + ", time=" + time + ", isCorrect=" + isCorrect + ", oldIdaddress=" + oldIdaddress
				+ ", tnvd=" + tnvd + ", pointNumber=" + pointNumber + "]";
	}	
	
	
}
