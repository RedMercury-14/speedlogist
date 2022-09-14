package by.base.main.model;

import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "message")
public class Message {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idMessage")
	private Integer idMessage;
	
	@Column(name = "fromUser")
	private String fromUser;
	
	@Column(name = "toUser")
	private String toUser;
	
	@Column(name = "text")
	private String text;
	
	@Column(name = "idRoute")
	private String idRoute;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "companyName")
	private String companyName;
	
	@Column(name = "comment")
	private String comment;
	
	@Column(name = "datetime")
	private String datetime;
	
	@Column(name = "currency")
	private String currency;
	
	
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getIdRoute() {
		return idRoute;
	}
	public void setIdRoute(String idRoute) {
		this.idRoute = idRoute;
	}
	
	public String getCompanyName() {
		return companyName;
	}
	public void setCompanyName(String companyName) {
		this.companyName = companyName;
	}
	
	public String getComment() {
		return comment;
	}
	public void setComment(String comment) {
		this.comment = comment;
	}
	
	public Integer getIdMessage() {
		return idMessage;
	}
	public void setIdMessage(Integer idMessage) {
		this.idMessage = idMessage;
	}
	
	public String getDatetime() {
		return datetime;
	}
	public void setDatetime(String datetime) {
		this.datetime = datetime;
	}	
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	@Override
	public int hashCode() {
		return Objects.hash(comment, companyName, datetime, fromUser, idMessage, idRoute, status, text, toUser);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Message other = (Message) obj;
		return Objects.equals(comment, other.comment) && Objects.equals(companyName, other.companyName)
				&& Objects.equals(datetime, other.datetime) && Objects.equals(fromUser, other.fromUser)
				&& Objects.equals(idMessage, other.idMessage) && Objects.equals(idRoute, other.idRoute)
				&& Objects.equals(status, other.status) && Objects.equals(text, other.text)
				&& Objects.equals(toUser, other.toUser);
	}
	@Override
	public String toString() {
		return "Message [idMessage=" + idMessage + ", fromUser=" + fromUser + ", toUser=" + toUser + ", text=" + text
				+ ", idRoute=" + idRoute + ", status=" + status + ", companyName=" + companyName + ", comment="
				+ comment + ", datetime=" + datetime + ", currency=" + currency + "]";
	}
	
	
	
	
	
}
