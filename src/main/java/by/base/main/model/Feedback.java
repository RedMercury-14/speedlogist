package by.base.main.model;

import java.io.Serializable;
import java.time.LocalDate;
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
@Entity
@Table(name = "feedback")
public class Feedback implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = 8916705052216562837L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idfeedback")
	private int idFeedback;
	
	@Column(name = "message")
	private String message;
	
	@Column(name = "status")
	private String status;
	
	@Column(name = "date")
	private LocalDate date;
	
	@Column(name = "idRouteHasShop")
	private Integer idRouteHasShop;
		
	@ManyToOne(fetch = FetchType.EAGER, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "user_iduser")
	private User user; //driver
	
	@ManyToOne(fetch = FetchType.EAGER, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "shop_numshop")
	private Shop shop;
	
	@ManyToOne(fetch = FetchType.EAGER, 
			cascade = { CascadeType.PERSIST, 
						CascadeType.MERGE, 
						CascadeType.DETACH,
						CascadeType.REFRESH })
	@JoinColumn(name = "`from`")
	private User from;

	public int getIdFeedback() {
		return idFeedback;
	}

	public void setIdFeedback(int idFeedback) {
		this.idFeedback = idFeedback;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public LocalDate getDate() {
		return date;
	}

	public void setDate(LocalDate date) {
		this.date = date;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Shop getShop() {
		return shop;
	}

	public void setShop(Shop shop) {
		this.shop = shop;
	}
		
	public User getFrom() {
		return from;
	}

	public void setFrom(User from) {
		this.from = from;
	}
	
	public Integer getIdRouteHasShop() {
		return idRouteHasShop;
	}

	public void setIdRouteHasShop(Integer idRouteHasShop) {
		this.idRouteHasShop = idRouteHasShop;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idFeedback);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Feedback other = (Feedback) obj;
		return idFeedback == other.idFeedback;
	}

	@Override
	public String toString() {
		return "Feedback [idFeedback=" + idFeedback + ", message=" + message + ", status=" + status + ", date=" + date
				+ ", idRouteHasShop=" + idRouteHasShop + "]";
	}

	
	

}
