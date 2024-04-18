package by.base.main.model;

import java.io.Serializable;
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
