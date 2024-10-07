package by.base.main.model;

import java.io.Serializable;
import java.sql.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
@Entity
@Table(name = "message")
public class Message implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 5615966950661786948L;

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
	
	@Column(name = "url")
	private String url;
	
	@Column(name = "nds")
	private String nds;
	
	@Column(name = "fullName")
	private String fullName;
	
	@Column(name = "ynp")
	private String ynp;
	
	@Column(name = "date")
	private Date date;
	
	@Transient
	private String action;
	
	@Transient
	private String payload;
	
	@Transient
	private String idOrder;
	
	@Transient
	private String numMarket;
	
	@Transient
	private String WSPath;
	

	public Message() {
		super();
	}
	
	/**
	 * @param fromUser
	 * @param text
	 * @param status
	 * @param payload
	 * @param idOrder
	 */
	public Message(String fromUser, String text, String status, String payload, String idOrder) {
		super();
		this.fromUser = fromUser;
		this.text = text;
		this.status = status;
		this.payload = payload;
		this.idOrder = idOrder;
	}
	
	public Message(String WSPath, String fromUser, String text, String status, String payload, String idOrder, String action) {
		super();
		this.fromUser = fromUser;
		this.text = text;
		this.status = status;
		this.payload = payload;
		this.idOrder = idOrder;
		this.action = action;
		this.WSPath = WSPath;
	}
	
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
	
	public String getIdOrder() {
		return idOrder;
	}
	public void setIdOrder(String idOrder) {
		this.idOrder = idOrder;
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
	
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
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
	
	public String getUrl() {
		return url;
	}
	public void setUrl(String url) {
		this.url = url;
	}
	
	public String getNds() {
		return nds;
	}
	public void setNds(String nds) {
		this.nds = nds;
	}
	
	public String getFullName() {
		return fullName;
	}
	public void setFullName(String fullName) {
		this.fullName = fullName;
	}
	
	public String getYnp() {
		return ynp;
	}
	public void setYnp(String ynp) {
		this.ynp = ynp;
	}
	
	public String getNumMarket() {
		return numMarket;
	}

	public void setNumMarket(String numMarket) {
		this.numMarket = numMarket;
	}

	public Date getDate() {
		return date;
	}
	public void setDate(Date date) {
		this.date = date;
	}
	
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	/**
	 * @return the wSPath
	 */
	public String getWSPath() {
		return WSPath;
	}

	/**
	 * @param wSPath the wSPath to set
	 */
	public void setWSPath(String wSPath) {
		WSPath = wSPath;
	}

	@Override
	public int hashCode() {
		return Objects.hash(comment, companyName, currency, datetime, fromUser, fullName, idMessage, idRoute, nds,
				status, text, toUser, url, ynp);
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
				&& Objects.equals(currency, other.currency) && Objects.equals(datetime, other.datetime)
				&& Objects.equals(fromUser, other.fromUser) && Objects.equals(fullName, other.fullName)
				&& Objects.equals(idMessage, other.idMessage) && Objects.equals(idRoute, other.idRoute)
				&& Objects.equals(nds, other.nds) && Objects.equals(status, other.status)
				&& Objects.equals(text, other.text) && Objects.equals(toUser, other.toUser)
				&& Objects.equals(url, other.url) && Objects.equals(ynp, other.ynp);
	}
	@Override
	public String toString() {
		return "Message [idMessage=" + idMessage + ", fromUser=" + fromUser + ", toUser=" + toUser + ", text=" + text
				+ ", idRoute=" + idRoute + ", status=" + status + ", companyName=" + companyName + ", comment="
				+ comment + ", datetime=" + datetime + ", currency=" + currency + ", url=" + url + "]";
	}
	
	
	
	
	
	
}
