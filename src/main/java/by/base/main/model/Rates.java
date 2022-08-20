package by.base.main.model;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
@Entity
@Table(name = "rates")
public class Rates implements Serializable{

	/**
	 * @author Dima Hrushevski
	 */
	private static final long serialVersionUID = 5144946183571933794L;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "idrates")
	private int idRates;
	
	@Column(name = "caste")
	private String caste;
	
	@Column(name = "weight")
	private String weight;
	
	@Column(name = "pall")
	private String pall;
	
	@Column(name = "type")
	private String type;
	
	@Column(name = "before_400")
	private String before400;
	
	@Column(name = "after_400")
	private String after400;
	
	@Column(name = "hour")
	private String hour;

	public int getIdRates() {
		return idRates;
	}

	public void setIdRates(int idRates) {
		this.idRates = idRates;
	}

	public String getCaste() {
		return caste;
	}

	public void setCaste(String caste) {
		this.caste = caste;
	}

	public String getWeight() {
		return weight;
	}

	public void setWeight(String weight) {
		this.weight = weight;
	}

	public String getPall() {
		return pall;
	}

	public void setPall(String pall) {
		this.pall = pall;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getBefore400() {
		return before400;
	}

	public void setBefore400(String before400) {
		this.before400 = before400;
	}

	public String getAfter400() {
		return after400;
	}

	public void setAfter400(String after400) {
		this.after400 = after400;
	}

	public String getHour() {
		return hour;
	}

	public void setHour(String hour) {
		this.hour = hour;
	}

	@Override
	public int hashCode() {
		return Objects.hash(idRates);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Rates other = (Rates) obj;
		return idRates == other.idRates;
	}

	@Override
	public String toString() {
		return "Rates [idRates=" + idRates + ", caste=" + caste + ", weight=" + weight + ", pall=" + pall + ", type="
				+ type + ", before400=" + before400 + ", after400=" + after400 + ", hour=" + hour + "]";
	}

	
}
