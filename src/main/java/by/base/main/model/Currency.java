package by.base.main.model;

import java.io.Serializable;
import java.util.Objects;

public class Currency implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3684978437245332168L;
	
	private Integer Cur_ID;
	private String Date;
	private String Cur_Abbreviation;
	private Integer Cur_Scale;
	private String Cur_Name;
	private Double Cur_OfficialRate;
	public Integer getCur_ID() {
		return Cur_ID;
	}
	public void setCur_ID(Integer cur_ID) {
		Cur_ID = cur_ID;
	}
	public String getDate() {
		return Date;
	}
	public void setDate(String date) {
		Date = date;
	}
	public String getCur_Abbreviation() {
		return Cur_Abbreviation;
	}
	public void setCur_Abbreviation(String cur_Abbreviation) {
		Cur_Abbreviation = cur_Abbreviation;
	}
	public Integer getCur_Scale() {
		return Cur_Scale;
	}
	public void setCur_Scale(Integer cur_Scale) {
		Cur_Scale = cur_Scale;
	}
	public String getCur_Name() {
		return Cur_Name;
	}
	public void setCur_Name(String cur_Name) {
		Cur_Name = cur_Name;
	}
	public Double getCur_OfficialRate() {
		return Cur_OfficialRate;
	}
	public void setCur_OfficialRate(Double cur_OfficialRate) {
		Cur_OfficialRate = cur_OfficialRate;
	}
	@Override
	public int hashCode() {
		return Objects.hash(Cur_Abbreviation, Cur_ID, Cur_Name, Cur_OfficialRate, Cur_Scale, Date);
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Currency other = (Currency) obj;
		return Objects.equals(Cur_Abbreviation, other.Cur_Abbreviation) && Objects.equals(Cur_ID, other.Cur_ID)
				&& Objects.equals(Cur_Name, other.Cur_Name) && Objects.equals(Cur_OfficialRate, other.Cur_OfficialRate)
				&& Objects.equals(Cur_Scale, other.Cur_Scale) && Objects.equals(Date, other.Date);
	}
	@Override
	public String toString() {
		return "Currency [Cur_ID=" + Cur_ID + ", Date=" + Date + ", Cur_Abbreviation=" + Cur_Abbreviation
				+ ", Cur_Scale=" + Cur_Scale + ", Cur_Name=" + Cur_Name + ", Cur_OfficialRate=" + Cur_OfficialRate
				+ "]";
	}
	
	
}
